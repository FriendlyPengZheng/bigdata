package topics.active;
import util.*;
import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.mapred.join.*;
import org.apache.hadoop.mapreduce.Counter;

/**
 * @brief  compute distribution for :: gameid,mimi tad region login_count onlinetime 
 *          all distr vary with game(-1 is special)
 *        
 */
public class ActiveDimDistrMap extends MapReduceBase
    implements Mapper<Text, Text, Text, LongWritable>
{
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);
    private DistrImpl daysDistr = null;
    private DistrImpl timeDistr = null;
    private int dayInterval = 30;
    String onlinePday = null;
    public void configure(JobConf job) {
        String onlinedaydistr = job.get("online.day.distr", 
                "0,1,2,3,4,5,6,7,8,9,10,15,20");
        daysDistr = new DistrImpl(onlinedaydistr);

        String onlinetimedistr = job.get("online.avgtime.distr", 
                "0,600,3600,7200");
        timeDistr = new DistrImpl(onlinetimedistr);

        // process day foramt ::= 20121011
        onlinePday = job.get("online.process.day", "");
        try {
            // cut day part only
            dayInterval = Integer.parseInt(onlinePday.substring(6));
        } catch (Exception ex) {
            System.err.println("fetch error ** online.process.day ** : " + onlinePday);
            ex.printStackTrace();
        }
    }

    // gameid,mimi tad region login_count onlinetime(sum)
    //      =>
    // game,tad,region,freqLowPoint,onlinelenLowPoint uniqPlayers
    public void map(Text key, Text value,
            OutputCollector<Text,LongWritable> output, Reporter reporter) throws IOException {
        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");
        String dvalue = value.toString();
        String[] values = dvalue.split("\t");
        if (keys.length != 2 || values.length < 4) {
            System.err.printf("error DimDistr format: key = %s, value = %s\n", 
                    gamemimi, dvalue);
            return;
        }

        int vSz = values.length;
        try { 
            String gameid = keys[0];
            
            String tad = !values[vSz - 4].trim().equals("") ? values[vSz - 4].trim() : "unknown" ;
            String regioncode = values[vSz - 3];

            int onlineDays = Integer.parseInt(values[vSz - 2]);
            int dayLowPoint = daysDistr.getLowInclude(onlineDays);

            int avgLength = Integer.parseInt(values[vSz - 1]) / dayInterval;
            int lengthLowPoint = timeDistr.getLowInclude(avgLength);

            String[] tads = new String[] {tad, "all"};
            String[] regions = new String[] {regioncode, "-1"};
            int[] days = new int[] {dayLowPoint, -1};
            int[] lengths = new int[] { lengthLowPoint, -1};
            for (String t : tads) {
                for (String r : regions) {
                    for (int d : days) {
                        for (int l : lengths) {
                            realKey.set(String.format("%s,%s,%s,%d,%d", 
                                        gameid, t, r, d , l));
                            output.collect(realKey, one);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("error distr values : " + dvalue);
            ex.printStackTrace();
        } 
    }

    //public void close() {}
}

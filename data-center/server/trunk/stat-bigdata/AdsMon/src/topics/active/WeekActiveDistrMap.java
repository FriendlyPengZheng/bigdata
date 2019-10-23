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
 * @brief  compute distribution for :: gameid,mimi onlinetime 
 *          all distr vary with game(-1 is special)
 *        
 */
public class WeekActiveDistrMap extends MapReduceBase
    implements Mapper<Text, Text, Text, LongWritable>
{
    private Text realKey = new Text();
    private LongWritable one = new LongWritable(1);
    private DistrImpl timeDistr = null;
    public void configure(JobConf job) {

        String onlinetimedistr = job.get("online.avgtime.distr", 
                "0,600,3600,7200");
        timeDistr = new DistrImpl(onlinetimedistr);

    }

    // gameid,mimi tad region login_count onlinetime(sum)
    //      =>
    // game, onlinelenLowPoint uniqPlayers
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
            

            // 计算总时长，不是平均时长
            int total_length = Integer.parseInt(values[vSz - 1]);
            int lengthLowPoint = timeDistr.getLowInclude(total_length);

            int[] lengths = new int[] { lengthLowPoint, -1};
            for (int l : lengths) {
                realKey.set(String.format("%s,%d", gameid, l));
                output.collect(realKey, one);
            }
        } catch (Exception ex) {
            System.err.println("error distr values : " + dvalue);
            ex.printStackTrace();
        } 
    }

    //public void close() {}
}

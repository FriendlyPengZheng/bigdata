package topics.boss;
import io.*;
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
 * @brief  map process vip distribution 
 */
public class VipDistrMap extends MapReduceBase
        implements Mapper<Text, Text, Text, PairSumWritable>
{
    private Text realKey = new Text();
    private DistrImpl distrCost = null;
    private DistrImpl distrFreq = null;
    private PairSumWritable distrOut = new PairSumWritable();
    public void configure(JobConf job)
    {
        String freqdistrpoints = job.get("paid.freq.distr",
                "0,1,2,3,4,5,6,7,8,9,10");
        distrFreq = new DistrImpl(freqdistrpoints);

        String costdistrpoints = job.get("paid.cost.distr",
                "0,500,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000");
        distrCost = new DistrImpl(costdistrpoints);
    }

    //*  game(-1),viptype(-1),viplen(-1),mimi vip_num vip_times
    //*    =>
    //*  game,viptype,viplen,timesLowPoint,costLowPoint uniq cost
    //  viptype 1: purenew, 2: histnew, 3:renew
    //  viplen  units in months
    public void map(Text key, Text value,
            OutputCollector<Text, PairSumWritable> output, Reporter reportor) throws IOException
    {
        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");
        String line = value.toString();
        String[] values = line.split("\t");
        if (keys.length != 4 || values.length < 2) {
            System.err.printf("error consume distr format: %s %s\n", 
                    gamemimi, line);
            return;
        }

        try { 
            int cost = Integer.parseInt(values[0]);
            int times = Integer.parseInt(values[1]);

            // uniq players
            distrOut.key1 = 1;
            // vip cost sum
            distrOut.key2 = cost;

            // vip cost frequency not needed more
            //int freqDistrPoint = distrFreq.getLowInclude(times);
            //int[] freqs = new int[]{-1, freqDistrPoint};

            int costDistrPoint = distrCost.getLowInclude(cost);
            int[] costs = new int[]{-1, costDistrPoint};
            for (int c : costs) {
                realKey.set(String.format("%s,%s,%s,%s", keys[0], keys[1], keys[2], c));
                output.collect(realKey, distrOut);
            }

            //String[] viptypes = new String[] { "-1", keys[1]};
            //String[] viplens = new String[] { "-1", keys[2]};
            //for (String t : viptypes) {
                //for (String l: viplens) {
                    //for (int c : costs) {
                        //realKey.set(String.format("%s,%s,%s,%s", keys[0], t, l, c));
                        //output.collect(realKey, distrOut);
                    //}
                //}
            //}
        } catch (Exception ex) {
            System.err.printf("meet error, consume distr line: %s %s\n", 
                    gamemimi, line);
            ex.printStackTrace();
        } 
    }

    //public void close() {}
}

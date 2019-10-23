package topics.boss;
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
 * @brief  map process distribution as much as possible results
 */
public class ConsumeDistrMap extends MapReduceBase
        implements Mapper<Text, Text, Text, ConsumeDistrWritable>
{
    private Text realKey = new Text();
    private DistrImpl distrCost = null;
    private DistrImpl distrFreq = null;
    private ConsumeDistrWritable distrOut = new ConsumeDistrWritable();
    public void configure(JobConf job)
    {
        String freqdistrpoints = job.get("paid.freq.distr",
                "0,1,2,3,4,5,6,7,8,9,10");
        distrFreq =  new DistrImpl(freqdistrpoints);

        String costdistrpoints = job.get("paid.cost.distr",
                "0,500,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000");
        distrCost = new DistrImpl(costdistrpoints);
    }

    // input ::= gameid,mimi mb_num mb_times vip_num vip_times vipuser_type
    //      =>
    // game,costnumDistrP,costfreqDistrP costPlayers costSum mbvipnum
    public void map(Text key, Text value,
            OutputCollector<Text, ConsumeDistrWritable> output, Reporter reportor) throws IOException
    {
        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");
        String line = value.toString();
        String[] values = line.split("\t");

        if (keys.length != 2 || values.length < 5) {
            System.err.printf("error consume distr format: %s %s\n", 
                    gamemimi, line);
            return;
        }

        int vlen = values.length;
        try { 
            int mb_num = Integer.parseInt(values[vlen - 5]);
            int mb_times = Integer.parseInt(values[vlen - 4]);
            int vip_num = Integer.parseInt(values[vlen - 3]);
            int vip_times = Integer.parseInt(values[vlen - 2]);

            int all_num = mb_num + vip_num;
            int all_times = mb_times + vip_times;
            boolean mbvip = mb_num > 0 && vip_num >0 ? true : false;

            distrOut.allUniq = 1;
            distrOut.allCost = all_num;
            distrOut.allmbvip = mbvip ? 1 : 0;

            int costDistrPoint = distrCost.getLowInclude(all_num);
            int freqDistrPoint = distrFreq.getLowInclude(all_times);
            int[] costs = new int[]{-1, costDistrPoint};
            int[] freqs = new int[]{-1, freqDistrPoint};

            for(int c : costs) {
                for(int f: freqs) {
                    realKey.set(String.format("%s,%s,%s", keys[0], c, f));
                    output.collect(realKey, distrOut);
                }
            }
        } catch (Exception ex) {
            System.err.printf("meet error, consume distr line: %s %s\n", 
                    gamemimi, line);
            ex.printStackTrace();
        } 

    }

    //public void close() {}
}

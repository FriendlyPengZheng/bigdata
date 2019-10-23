package topics.boss;
import util.*;
import io.*;

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
public class WeekMBConsumeDistrMap extends MapReduceBase
        implements Mapper<Text, Text, Text, PairSumWritable>
{
    private Text realKey = new Text();
    private DistrImpl distrCost = null;
    private DistrImpl distrFreq = null;
    private PairSumWritable distrOut = new PairSumWritable();
    public void configure(JobConf job)
    {
        String freqdistrpoints = job.get("week.paid.freq.distr",
                "0,1,2,5");
        distrFreq =  new DistrImpl(freqdistrpoints);

        String costdistrpoints = job.get("week.paid.cost.distr",
                "0,20,40,60,80,100,500,1000");
        distrCost = new DistrImpl(costdistrpoints);
    }

    // input ::= gameid,mimi | | mb_num mb_times vip_num vip_times vipuser_type
    //      =>
    // out:: game,costNumP,costFreqP uniqPlayers costSum
    public void map(Text key, Text value,
            OutputCollector<Text, PairSumWritable> output, Reporter reportor) throws IOException
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

            // ignore not mb cost players
            if (mb_num <= 0 || mb_times <= 0) { return; }

            distrOut.key1 = 1;
            distrOut.key2 = mb_num;

            int costDistrPoint = distrCost.getLowInclude(mb_num);
            int freqDistrPoint = distrFreq.getLowInclude(mb_times);
            int[] costs = new int[]{-1, costDistrPoint};
            int[] freqs = new int[]{-1, freqDistrPoint};

            for (int c : costs) {
                for(int f: freqs) {
                    realKey.set(String.format("%s,%s,%s", keys[0], c, f));
                    output.collect(realKey, distrOut);
                }
            }
        } catch (Exception ex) {
            System.err.printf("meet error, distr mb line: %s %s\n", 
                    gamemimi, line);
            ex.printStackTrace();
        } 
    }

    //public void close() {}
}

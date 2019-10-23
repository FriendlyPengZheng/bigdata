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
public class WeekVipDistrMap extends MapReduceBase
        implements Mapper<Text, Text, Text, PairSumWritable>
{
    private Text realKey = new Text();
    private PairSumWritable distrOut = new PairSumWritable();
    public void configure(JobConf job)
    {
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
            // filter only for all game vip cost
            int viptype = Integer.parseInt(keys[1]);
            int viplen = Integer.parseInt(keys[2]);
            if (viptype != -1 || viplen != -1) {
                return;
            }

            int cost = Integer.parseInt(values[0]);
            // uniq players
            distrOut.key1 = 1;
            // vip cost sum
            distrOut.key2 = cost;

            realKey.set(keys[0]);
            output.collect(realKey, distrOut);

        } catch (Exception ex) {
            System.err.printf("meet error, consume distr line: %s %s\n", 
                    gamemimi, line);
            ex.printStackTrace();
        } 
    }

    //public void close() {}
}

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

// map for compute multiple cost statistical indicator
// min Quartile(Q1 Q2 Q3) mean median average max
public class StatGameCostMap extends MapReduceBase
        implements Mapper<Text, Text, GameTypeCostKey, LongWritable>
{
    LongWritable one = new LongWritable(1);
    LongWritable num = new LongWritable(0);
    private GameTypeCostKey gtck = new GameTypeCostKey();
    // gameid,mimi mb_cost mb_times vip_cost vip_times vip_type
    //          => 
    // game,bosstype,cost 1
    public void map(Text key, Text value,
            OutputCollector<GameTypeCostKey, LongWritable> output, Reporter reportor) throws IOException
    {
        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");
        String line = value.toString();
        String[] values = line.split("\t");
        int valuelen = values.length;
        if (keys.length != 2 || valuelen < 5) {
            System.err.printf("error input for statistical index: %s %s\n",
                    gamemimi, line);
            return;
        }

        try { 
            int gameid = Integer.parseInt(keys[0]);
            int mbcost = Integer.parseInt(values[valuelen - 5]);
            int vipcost = Integer.parseInt(values[valuelen - 3]);

            int allcost = mbcost + vipcost;
            if (allcost <= 0) {
                return;
            }
            // for all(1) bosstype
            gtck.gameid.set(gameid);
            gtck.bosstype.set(1);
            gtck.setCost(allcost);
            output.collect(gtck, one);

            // for mb(2) bosstype
            if (mbcost > 0) {
                gtck.gameid.set(gameid);
                gtck.bosstype.set(2);
                gtck.setCost(mbcost);
                output.collect(gtck, one);
            }
            // for vip(3) bosstype
            if (vipcost > 0) {
                gtck.gameid.set(gameid);
                gtck.bosstype.set(3);
                gtck.setCost(vipcost);
                output.collect(gtck, one);
            }
        } catch (Exception ex) {
            System.err.printf("meet error, for statistical index: %s %s\n",
                    gamemimi, line);
            ex.printStackTrace();
        }
    }
}

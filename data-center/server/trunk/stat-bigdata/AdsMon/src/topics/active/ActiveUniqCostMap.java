package topics.active;
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
 * @brief  compute cost unique players and cost mibi sum from join data
 *        
 */
public class ActiveUniqCostMap extends MapReduceBase
    implements Mapper<Text, Text, Text, PairSumWritable>
{
    private PairSumWritable ps = new PairSumWritable();
    private Text realKey = new Text();

    // game,mimi | | mb_num mb_times vip_num vip_times vip_type
    //      =>
    //  game uniqplayers costsum
    public void map(Text key, Text value,
            OutputCollector<Text,PairSumWritable> output, Reporter reporter) throws IOException {
        String gamemimi = key.toString();
        String[] keys = gamemimi.split(",");
        String line = value.toString();
        String[] values = line.split("\t");
        if (keys.length != 2 || values.length < 6) {
            System.err.println("UniqCost error input format: " + line);
            return;
        }

        int vlen = values.length;
        try { 
            int mb_cost = Integer.parseInt(values[vlen - 5]);
            int vip_cost = Integer.parseInt(values[vlen - 3]);

            ps.reset();
            ps.key1 = 1; // for unique players
            ps.key2 = mb_cost + vip_cost; // for cost sum

            realKey.set(keys[0]);

            output.collect(realKey, ps);
        } catch (Exception ex) {
            System.err.println("UniqCost meet error: " + line);
            ex.printStackTrace();
            return;
        }
    }
}

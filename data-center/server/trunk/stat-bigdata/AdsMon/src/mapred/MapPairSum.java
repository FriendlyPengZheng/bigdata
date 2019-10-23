package mapred;
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
 * @brief  generate :: < Text, PairSumWritable >
 */
public class MapPairSum extends MapReduceBase 
    implements Mapper<Text, Text, Text, PairSumWritable>
{
    private PairSumWritable ps = new PairSumWritable();

    public void map(Text key, Text value,
            OutputCollector<Text, PairSumWritable> output, Reporter reportor) throws IOException
    {
        String line = value.toString();
        String[] items = line.split("\t");
        if (items.length < 2) {
            System.err.println("error format for PairSum: " + line);
            return ;
        }

        try { 
            long k1 = Long.parseLong(items[0]);
            long k2 = Long.parseLong(items[1]);
            ps.key1 = k1;
            ps.key2 = k2;
            output.collect(key, ps);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("meet error: " + line);
        }
    }

}

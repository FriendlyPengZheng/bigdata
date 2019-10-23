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

public class GetMBPlayersMap extends MapReduceBase
        implements Mapper<Text, Text, Text, NullWritable>
{
    // gameid,mimi mb_num mb_times vip_num vip_times vip_type
    //      => (filter out mb_num <= 0, and output key only)
    // gameid,mimi
    public void map(Text key, Text value,
            OutputCollector<Text, NullWritable> output, Reporter reportor) throws IOException
    {
        String line = value.toString();
        String[] values = line.split("\t");

        if (values.length < 5) {
            System.err.printf("error consume distr value format: %s\n", line);
            return;
        }

        int vlen = values.length;
        try { 
            int mb_num = Integer.parseInt(values[vlen - 5]);
            if (mb_num <= 0) { return; }

            output.collect(key, NullWritable.get());
        } catch (Exception ex) {
            System.out.println("meet error when parsing mb_num: " + line);
            ex.printStackTrace();
        }
    }
}

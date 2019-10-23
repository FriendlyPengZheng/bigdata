package test.loginMerge;
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

public class MergeLoginReduce extends MapReduceBase
    implements Reducer<Text, Text, Text, NullWritable>
{
    private Text realKey = new Text();

    //key::= time:ip:mimi:game:idc
    //value::= tad
    public void reduce(Text key, Iterator<Text> values,
            OutputCollector<Text,NullWritable> output, Reporter reporter) throws IOException {
        String outTad = null;
        while(values.hasNext()) {
            Text v = values.next();
            String value = v.toString();
            if (value.length() != 0) { outTad = value; break;}
        }
        if (outTad == null) { outTad = "unknown"; }

        String[] items = key.toString().split(":", -1);
        // time mimi tad gameid idc ip
        realKey.set(String.format("%s\t%s\t%s\t%s\t%s\t%s", 
                    items[0], items[2], outTad, items[3], items[4], items[1]));
        output.collect(realKey, NullWritable.get());
    }
}


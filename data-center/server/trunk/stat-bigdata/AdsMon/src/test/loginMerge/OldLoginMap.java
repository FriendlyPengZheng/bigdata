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

// login format::= timestamp mimi tad gameid idc ip
// merge old login and new login log records
//  fetch tad from old
//  fetch time, ip etc. from new
public class OldLoginMap extends MapReduceBase
    implements Mapper<LongWritable, Text, Text, Text>
{
    private Text realKey = new Text();
    private Text realValue = new Text();

    public void map(LongWritable key, Text value,
            OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String line = value.toString();
        String[] items = line.split("\t", -1);
        if(items.length != 6) {
            System.err.println("error fmt:" + line);
            return;
        }
        // time:ip:mimi:game:idc
        realKey.set(String.format("%s:%s:%s:%s:%s", items[0], items[5], items[1], items[3], items[4]));
        // tad
        realValue.set(items[2]);
        output.collect(realKey, realValue);
    }
}

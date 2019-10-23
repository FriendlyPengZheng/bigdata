package com.taomee.bigdata.task.device;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class ActiveDeviceCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        output.collect(key, values.next());
    }
}

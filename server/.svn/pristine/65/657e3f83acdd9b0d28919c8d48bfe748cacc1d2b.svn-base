package com.taomee.bigdata.task.newlog;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;

public class NewLoginCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        if(values.hasNext()) {
            output.collect(key, values.next());
        }
    }
}

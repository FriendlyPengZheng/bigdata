package com.taomee.bigdata.task.query;
  
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.*;
import java.util.Iterator;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class LastLoginReducer extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable>
{
    private LongWritable outputValue = new LongWritable();

    public void reduce(Text key, Iterator<LongWritable> values, OutputCollector<Text, LongWritable> output, Reporter reporter) throws IOException
    {
        Long ts = 0l;

        while(values.hasNext()) {
            Long t = values.next().get();
            ts = t > ts ? t : ts;
		}

        outputValue.set(ts);
        output.collect(key, outputValue);
    }

}

package com.taomee.bigdata.basic;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.lang.Long;
import java.lang.Double;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;
import com.taomee.bigdata.util.MD5Util;
import com.taomee.bigdata.util.LogAnalyser;
import java.util.HashMap;

public class SplitCustomCombiner extends MapReduceBase implements Reducer<Text, NullWritable, Text, NullWritable>
{
    private NullWritable outputValue = NullWritable.get();

    public void reduce(Text key, Iterator<NullWritable> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        output.collect(key, outputValue);
    }
}

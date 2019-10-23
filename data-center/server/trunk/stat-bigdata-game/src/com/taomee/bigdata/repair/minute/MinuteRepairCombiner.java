package com.taomee.bigdata.repair.minute;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;

import java.io.IOException;
import java.util.Iterator;

public class MinuteRepairCombiner extends MapReduceBase implements Reducer<Text, FloatWritable, Text, FloatWritable>
{
    private FloatWritable outputValue = new FloatWritable();

    public void reduce(Text key, Iterator<FloatWritable> values, OutputCollector<Text, FloatWritable> output, Reporter reporter) throws IOException {
        String[] items;
        Integer op = Integer.valueOf(key.toString().split("\t")[0]);
        Float value = 0f;
        switch(op) {
            case Operator.COUNT:
            case Operator.SUM:
                value = 0f;
                while(values.hasNext()) {
                    value += values.next().get();
                }
                break;
            case Operator.MAX:
                value = Float.MIN_VALUE;
                while(values.hasNext()) {
                    Float v = values.next().get();
                    value = v > value ? v : value;
                }
                break;
            case Operator.SET:
                //TODO
                break;
            default:
                break;
        }
        outputValue.set(value);
        output.collect(key, outputValue);
    }

}

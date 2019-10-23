package com.taomee.bigdata.datamining.seerV1;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;

public class PayallMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        String game = items[0];
        String uid = items[4];
        String sstid = items[5];
        if(sstid.compareTo("_acpay_") != 0) return;
        double payall = Double.valueOf(items[10])/100;
        if(payall < 500)    return;
        outputKey.set(String.format("%s,%s",
                    game, uid));
        outputValue.set(String.format("%d\t%.2f",
                    Type.ALLPAY, payall));
        output.collect(outputKey, outputValue);
    }

}

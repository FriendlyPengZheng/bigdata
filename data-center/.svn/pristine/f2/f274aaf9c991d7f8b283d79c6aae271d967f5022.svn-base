package com.taomee.bigdata.task.pay;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.*;

public class MSRemainSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>
{
    private Text outputKey = new Text();
    private DoubleWritable outputValue = new DoubleWritable();

    //输入 game,platform,zone,zerver,uid,sstid,付费额
    //输出 key=game,platform,zone,server,sstid value=付费额
    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        if(items == null || items.length != 7) {
            return;
        }
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[5]));
        outputValue.set(Double.valueOf(items[6]));
        output.collect(outputKey, outputValue);
    }

}

package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * 某时间（周、月）范围用户：月新增、留存、回流、月付费
 * @author cheney
 * @date 2013-11-21
 */
public class RangeUserMapper extends MRBase implements Mapper<LongWritable, Text, Text, FloatWritable> {

    private FloatWritable outputValue = new FloatWritable(-1);
    private Text outputKey = new Text();
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, FloatWritable> output, Reporter reporter)
			throws IOException {
		this.reporter = reporter;
		
		//value: gid zid sid pid acid
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3], items[4]));
		output.collect(outputKey, outputValue);
	}

}

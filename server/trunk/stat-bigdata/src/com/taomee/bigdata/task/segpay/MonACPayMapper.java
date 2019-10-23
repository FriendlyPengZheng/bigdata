package com.taomee.bigdata.task.segpay;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

/**
 * 从月付费中间结果获取gid zid sid pid acid，_amt_
 * @author cheney
 * @date 2013-11-21
 */
public class MonACPayMapper extends MRBase implements Mapper<LongWritable, Text, Text, FloatWritable>{

	//gid zid sid pid acid
	private Text outputKey = new Text();
	//_amt_
	private FloatWritable outputValue = new FloatWritable();
	
	protected Integer assist = null;
	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, FloatWritable> output, Reporter reporter)
			throws IOException {
		
		this.reporter = reporter;
		String[] items = value.toString().split("\t");
		
		//付费sstid有多种，_acpay_, _vipmonth_, _buyitem_
		//task74-87付费用_acpay_
		if("_acpay_".equalsIgnoreCase(items[5])){
			outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", items[0], items[1], items[2], items[3], items[4]));
			if(assist != null)
				outputValue.set(assist);
			else outputValue.set(Float.parseFloat(items[6]));
            output.collect(outputKey, outputValue);
		}
		
	}

}

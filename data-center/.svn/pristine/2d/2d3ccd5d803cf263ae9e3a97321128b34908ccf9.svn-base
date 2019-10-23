package com.taomee.bigdata.task.vip3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;


/**
 * 截止昨天所有累计vip的数量
 * @author looper
 * @date 2016年12月28日
 */
public class YesterdayVipAll extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{
	/**
	 * 数据格式:
	 * 		 25      -1      -1      -1      100336758--1    1480232538
			 25      -1      -1      -1      102512356--1    1479211158
			 25      -1      -1      -1      106993382--1    1480141670
	 */
	private Text outputKey = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private Text outputValue = new Text(); 

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		// TODO Auto-generated method stub
		this.reporter = reporter;
		String items[] = value.toString().split("\t");
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",items[0], items[1], items[2], items[3], items[4]));
		outputValue.set(String.format("%s\t%s", items[5],1));//设置累计的vip的标识为1
		output.collect(outputKey, outputValue);
		
	}

}

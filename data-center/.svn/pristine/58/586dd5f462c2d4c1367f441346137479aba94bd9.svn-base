package com.taomee.bigdata.task.channel;

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

public class NewUserChannelkeepMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, Text> {

	/**
	 * _zid_=1 _sid_=30106 _pid_=-1 657 -1 -1 -1 1000643-1000643 ad 0/1
	 */
	private Text outputKey = new Text();
	private ReturnCode r = ReturnCode.get();
	private ReturnCodeMgr rOutput;
	private Reporter reporter;
	protected Text outputValue = new Text();

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
		String props[] = value.toString().split("\t");
		if (props.length != 7) {
			return;
		}

		String gid = props[0];
		String zid = props[1];
		String sid = props[2];
		String pid = props[3];
		String ad = props[5];
		String flag = props[6];
		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s", gid, zid, sid, pid,
				ad));
		outputValue.set(flag);
		output.collect(outputKey, outputValue);

	}

}

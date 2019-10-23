package com.taomee.bigdata.task.activeback_value;

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
import com.taomee.bigdata.util.LogAnalyser;

public class ActiveBackSourceMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>{

	private Text outputKey = new Text();
    private Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;

        String[] item = value.toString().split("\t");

        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    item[0],item[1], item[2], item[3], item[4]));
        outputValue.set(String.format("2\t%s", new Text("0")));
        output.collect(outputKey, outputValue);
    }

}

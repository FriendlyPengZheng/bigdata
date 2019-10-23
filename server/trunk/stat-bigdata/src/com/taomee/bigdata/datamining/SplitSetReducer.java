package com.taomee.bigdata.datamining;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.IOException;
import java.util.Iterator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import com.taomee.bigdata.lib.*;

public class SplitSetReducer extends MapReduceBase implements Reducer<IntWritable, Text, Text, NullWritable>
{
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private int splitNum;

    public void configure(JobConf job) {
        splitNum = Integer.valueOf(job.get("split"));
        for(int i=0; i<splitNum; i++) {
            try {
                MultipleOutputs.addNamedOutput(
                        job, String.valueOf(i),
                        Class.forName("org.apache.hadoop.mapred.TextOutputFormat").asSubclass(OutputFormat.class),
                        Class.forName("org.apache.hadoop.io.IntWritable").asSubclass(WritableComparable.class),
                        Class.forName("org.apache.hadoop.io.Text").asSubclass(Writable.class));
            } catch (java.lang.ClassNotFoundException e) {
                ReturnCode.get().setCode("E_CONF_CLASS_NOT_FOUND", e.getMessage());
            } catch (java.lang.IllegalArgumentException e) { }
        }
        rOutput = new ReturnCodeMgr(job);
        mos = rOutput.getMos();
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(IntWritable key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        while(values.hasNext()) {
            mos.getCollector(key.toString(), reporter).collect(values.next(), NullWritable.get());
        }
    }
}

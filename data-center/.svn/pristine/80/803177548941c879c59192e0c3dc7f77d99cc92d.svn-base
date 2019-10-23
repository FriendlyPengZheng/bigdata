package com.taomee.recycle;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.Iterator;
import java.util.HashSet;

public class RecycleReducer extends MapReduceBase implements Reducer<Text, Text, Text, NullWritable>
{
    private Text outputKey = new Text();
    private NullWritable outputValue = NullWritable.get();
    private HashSet<String> output = new HashSet<String>();
    private MultipleOutputs mos = null;
    private String mosName = null;

    public void configure(JobConf job) {
        if(job.get("mos") != null && job.get("mos").compareTo("part") != 0) {
            mosName = job.get("mos");
        }
        mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        boolean isRecycle = false;
        this.output.clear();
        //key / value
        while(values.hasNext()) {
            Text t = values.next();
            if(t.toString().compareTo("recycle") == 0) {
                isRecycle = true;
            } else {
                this.output.add(t.toString());
            }
        }
        if(!isRecycle) {
            Iterator<String> it = this.output.iterator();
            while(it.hasNext()) {
                outputKey.set(it.next());
                if(mosName == null) {
                    output.collect(outputKey, outputValue);
                } else {
                    mos.getCollector(mosName, reporter).collect(outputKey, outputValue);
                }
            }
        } else {
            Iterator<String> it = this.output.iterator();
            while(it.hasNext()) {
                outputKey.set(it.next());
                mos.getCollector("recycle", reporter).collect(outputKey, outputValue);
            }
        }
    }

}

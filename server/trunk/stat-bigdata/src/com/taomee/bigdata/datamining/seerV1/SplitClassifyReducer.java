package com.taomee.bigdata.datamining.seerV1;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;
import java.util.Iterator;
import com.taomee.bigdata.lib.*;

import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.Classifier;

public class SplitClassifyReducer extends MapReduceBase implements Reducer<IntWritable, Text, Text, NullWritable>
{
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private MultipleOutputs mos = null;
    private int splitNum;
    private Instances header;
    private String c;
    private String g;
    private JobConf conf;
    private String cOutput;

    public void configure(JobConf job) {
        conf = job;
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

        try {
            header = Util.readHeaderFromHDFS(job, job.get("header"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //c=0.5:0.1:20.5 g=0:0.01:0.2
        c = job.get("c");
        g = job.get("g");

        cOutput = job.get("classifier.output.path");
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }

    public void reduce(IntWritable key, Iterator<Text> values, OutputCollector<Text, NullWritable> output, Reporter reporter) throws IOException {
        this.reporter = reporter;
        Instances instances = new Instances(header);
        int numAttributes = instances.numAttributes();
        while(values.hasNext()) {
            String value = values.next().toString();
            mos.getCollector(key.toString(), reporter).collect(value, NullWritable.get());

            Instance i = Util.getInstance(value.split(",")[2].trim(), instances);
            instances.add(i);
        }

        System.out.println("instances " + key.get());

        GradientSearch gs = new GradientSearch();
        try {
            Classifier classifier = gs.run(new String[]{c, g}, instances);
            Util.writeClassifierToHDFS(conf, classifier, cOutput + "/" + key.toString() + ".classifier");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

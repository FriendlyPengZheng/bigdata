package com.taomee.bigdata.datamining.seerV2;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.lib.*;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;
import weka.classifiers.Classifier;
import java.io.IOException;
import java.util.Iterator;

public class ClassifyDatasetReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>
{
    private Text outputKey = new Text();
    private Text outputValue = new Text();
    private Instances header;
    private Classifier[] c;
    private Attribute classAttribute;
    private double maxCnt;
    private MultipleOutputs mos = null;
    private int classCnt = 3;

    public void configure(JobConf job) {
        try {
            header = Util.readHeaderFromHDFS(job, job.get("header"));
            classAttribute = header.classAttribute();
            c = Util.readClassifierFromHDFS(job, job.get("classifier.output.path") + "/*.classifier");
            maxCnt = (c.length+1.0)/classCnt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mos = new MultipleOutputs(job);
    }

    public void close() throws IOException {
        mos.close();
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        Instance i = Util.getInstanceWithoutClass(key.toString(), header);
        double type[] = new double[classCnt];
        String result = "null";

        //for(int j=0; j<c.length; j++) {
        //    try {
        //        int t = (int)c[j].classifyInstance(i);
        //        type[t]++;
        //        //if(type[t] >= maxCnt) {
        //        //    result = classAttribute.value(t);
        //        //    break;
        //        //}
        //    } catch (java.lang.Exception e) {
        //        throw new RuntimeException(e);
        //    }
        //}

        for(int j=0; j<c.length; j++) {
            type = new double[classCnt];
            try {
                double r[] = c[j].distributionForInstance(i);
                for(int k=0; k<classCnt; k++)   type[k] += r[k];
            } catch (java.lang.Exception e) {
                throw new RuntimeException(e);
            }
        }

        double max = Double.MIN_VALUE;
        int index = -1;
        for(int k=0; k<classCnt; k++) {
            if(type[k] > max) {
                max = type[k];
                index = k;
            }
        }
        result = classAttribute.value(index);

        outputValue.set(result);
        while(values.hasNext()) {
            String value = values.next().toString();
            outputKey.set(value + "," + key.toString());
            mos.getCollector(result, reporter).collect(outputKey, outputValue);
            //output.collect(outputKey, outputValue);
        }
    }
}

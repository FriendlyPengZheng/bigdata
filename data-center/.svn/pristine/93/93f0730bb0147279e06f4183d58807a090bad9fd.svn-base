package com.taomee.bigdata.datamining.seerV1;

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
    private int maxCnt;

    public void configure(JobConf job) {
        try {
            header = Util.readHeaderFromHDFS(job, job.get("header"));
            classAttribute = header.classAttribute();
            c = Util.readClassifierFromHDFS(job, job.get("classifier.output.path") + "/*.classifier");
            maxCnt = (c.length+1)/3;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        Instance i = Util.getInstance(key.toString(), header);
        double type[] = new double[3];
        String result = "null";

        for(int j=0; j<c.length; j++) {
            try {
                int t = (int)c[j].classifyInstance(i);
                type[t]++;
                if(type[t] >= maxCnt) {
                    result = classAttribute.value(t);
                    break;
                }
            } catch (java.lang.Exception e) {
                throw new RuntimeException(e);
            }
        }

        outputValue.set(result);
        while(values.hasNext()) {
            String value = values.next().toString();
            outputKey.set(value + "," + key.toString());
            output.collect(outputKey, outputValue);
        }
    }
}

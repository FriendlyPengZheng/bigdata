package com.taomee.bigdata.task.combat;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import com.taomee.bigdata.lib.Distr;
import java.io.IOException;
/**
 * 输入格式：
 * 169     -1      -1      -1      331025610--1    0.0
 * 输出格式：
 * @author looper
 * @date 2016年7月5日
 */
public class CombatSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>
{
    private Text outputKey = new Text();
    private IntWritable outputValue = new IntWritable(1);
    private Integer distr[] = null;

    public void configure(JobConf job) {
        String distr = job.get("distr");
        if(distr == null) { throw new RuntimeException("item distr not configured"); }
        String distrs[] = distr.split(",");
        this.distr = new Integer[distrs.length];
        for(int i=0; i<distrs.length; i++) {
            this.distr[i] = Integer.valueOf(distrs[i]);
        }
    }

    public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
        String items[] = value.toString().split("\t");
        outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                    items[0], items[1], items[2], items[3],  Distr.getDistrName(distr, Distr.getRangeIndex(distr, Double.valueOf(items[5])*100), 1)));
        output.collect(outputKey, outputValue);
    }
}

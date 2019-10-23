package com.taomee.bigdata.task.roll;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SourceTransToRollMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    protected Text outputValue = new Text();
    private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;
    private Reporter reporter;
    private LogAnalyser logAnalyser = new LogAnalyser();

    public void configure(JobConf job) {
        rOutput = new ReturnCodeMgr(job);
    }

    public void close() throws IOException {
        rOutput.close(reporter);
    }


    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null) {
					outputKey.set(String.format("%s\t%s\t%s\t%s",
								game, zone, platform, uid));
					outputValue.set(String.format("0\t%s\t%s", server, value.toString()));
					output.collect(outputKey, outputValue);

					//don't change the source data
					if(server.compareTo("-1") != 0){
						outputKey.set(String.format("%s", value.toString()));
						outputValue.set(String.format("2"));
						output.collect(outputKey, outputValue);
					}
				}

        }
    }

}

package com.taomee.bigdata.task.paycoins;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;

import com.taomee.bigdata.lib.*;
import com.taomee.bigdata.util.LogAnalyser;
import java.io.*;

public class SourceCoinsSumMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, DoubleWritable>
{
    private Text outputKey = new Text();
	private DoubleWritable outputValue = new DoubleWritable();
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

    //key=game,platform,zone,server,uid value=golds
    public void map(LongWritable key, Text value, OutputCollector<Text, DoubleWritable> output, Reporter reporter) throws IOException
    {
        this.reporter = reporter;
        if(logAnalyser.analysis(value.toString()) == ReturnCode.G_OK &&
            (logAnalyser.getValue("_sstid_").compareTo("_coinsbuyitem_") == 0)) {
            String game = logAnalyser.getValue(logAnalyser.GAME);
            String platform = logAnalyser.getValue(logAnalyser.PLATFORM);
            String zone = logAnalyser.getValue(logAnalyser.ZONE);
            String server = logAnalyser.getValue(logAnalyser.SERVER);
            String uid = logAnalyser.getAPid();
			String golds = logAnalyser.getValue("_golds_");
            if(game != null &&
                platform != null &&
                zone != null &&
                server != null &&
                uid != null &&
				golds != null) {
				//rexue need to be multiplied by 100
				Double gold_value = Double.valueOf(golds);
				if(game.compareTo("16") == 0){
					gold_value *= 100;;
				}  
                outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
                            game, zone, server, platform, uid));
				outputValue.set(gold_value);
                output.collect(outputKey, outputValue);
            }
        }
    }

}

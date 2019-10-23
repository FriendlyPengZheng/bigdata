package com.taomee.bigdata.task.roll;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.*;
import com.taomee.bigdata.lib.Operator;
import com.taomee.bigdata.lib.ReturnCode;
import com.taomee.bigdata.lib.ReturnCodeMgr;

import java.io.*;
import java.util.HashSet;

public class TransToRollMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
{
    private Text outputKey = new Text();
    protected Text outputValue = new Text();
	private ReturnCode r = ReturnCode.get();
    private ReturnCodeMgr rOutput;

    private HashSet<String> rollGame = new HashSet<String>();
    public void configure(JobConf job) {   
		String gametable = job.get("gameRoll");
        if(gametable == null) {
            throw new RuntimeException("gameRoll not configured");
        }   
        String[] gameinfo = gametable.split(",");
        for(int i = 0; i < gameinfo.length; i++) {
			rollGame.add(gameinfo[i]);
        }   
		rOutput = new ReturnCodeMgr(job);
    }   

	//key=gid pid zid uid  value=0 sid value_left
    public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String items[] = value.toString().split("\t");
        if(items == null || items.length < 5) {
            r.setCode("E_TRANS_TO_ROLL_MAPPER", "items split length < 5");
            return ;
        }      
		//special handling for roll games
		String game = items[0];
		if(rollGame != null && rollGame.contains(game)){
			if(items.length >= 6){
				String value_left = new String();
				for(int i=5; i<items.length; i++){
					value_left += items[i];
					value_left = i == items.length - 1 ? value_left : value_left + "\t";
				}
				outputValue.set(String.format("0\t%s\t%s", items[2], value_left));
			}else{
				outputValue.set(String.format("0\t%s", items[2]));
			}
			outputKey.set(String.format("%s\t%s\t%s\t%s",
					items[0], items[1], items[3], items[4]));
			output.collect(outputKey, outputValue);
		}
		//don't change the source data
		outputKey.set(String.format("%s", value.toString()));
		outputValue.set(String.format("2"));
		output.collect(outputKey, outputValue);
		
    }

}

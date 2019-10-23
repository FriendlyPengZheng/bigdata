package com.taomee.bigdata.tms;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class ColumnMapper extends MapReduceBase implements Mapper<LongWritable,Text,Text,Text>{
	private String keyFormatStr;
	private String splitter;
	private String valueFormatStr;
	protected Integer flagNum;
	private Text outputKey = new Text();
	private Text outputValue = new Text();
	private Boolean onlyFullGame = false;
	private StringBuilder sb = new StringBuilder();
	
	@Override
	public void configure(JobConf job) {
		keyFormatStr = job.get("keyFormat"+(flagNum==null?"":flagNum));
		valueFormatStr = job.get("valueFormat"+(flagNum==null?"":flagNum));
		splitter = job.get("splitter"+(flagNum==null?"":flagNum));
		if(splitter == null)splitter="\t";
//		if(keyFormatStr != null){
//			keyColumnIndex = getColumnIndexArray(keyFormatStr);
//		}
//		if(valueFormatStr != null){
//			valueColumnIndex = getColumnIndexArray(valueFormatStr);
//		}
		if(job.get("onlyFullGame") != null && job.get("onlyFullGame").equals("true")){
			onlyFullGame = true;
		}
	}

	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> output, Reporter reporter)throws IOException {
		String items[] = value.toString().split(splitter);
		
		if(onlyFullGame){
			if(!items[1].equals("-1") || !items[2].equals("-1") || !items[3].equals("-1")){
				return;
			}
		}
//		outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",items[0], items[1], items[2], items[3],items[4]));
//		if(columnNum == null){
//			outputValue.set(flagNum==null?"":String.format("%d",flagNum));
//			output.collect(outputKey,outputValue);
//		}else{
//			int valueOfColumnNum = Integer.valueOf(columnNum);
//			outputValue.set(flagNum==null?items[valueOfColumnNum-1]:String.format("%d\t%s",flagNum,items[valueOfColumnNum-1]));
//			output.collect(outputKey,outputValue);
//		}
		outputKey.set(getFormattedStr(keyFormatStr,items));
		outputValue.set("");
		String valueStr = getFormattedStr(valueFormatStr,items);
		if(valueStr == null || valueStr.equals("")){
			if(flagNum != null){
				outputValue.set(flagNum+"");
			}
		}else{
			if(flagNum == null){
				outputValue.set(valueStr);
			}else{
				outputValue.set(flagNum+"\t"+valueStr);
			}
		}
		output.collect(outputKey,outputValue);
	}

	private int[] getColumnIndexArray(String formatStr){
		if(formatStr == null)return null;
		String[] columnNums = formatStr.split("#");
		int[] result = new int[columnNums.length-1];
		for(int i = 0 ;i<=result.length-1;i++){
			result[i] = Integer.valueOf(columnNums[i+1]);
		}
		return result;
	}
	
	private String getFormattedStr(String formatStr,String[] items){
		int[] columnIndex = getColumnIndexArray(formatStr);
		sb.setLength(0);
		if(columnIndex != null && columnIndex.length != 0){
			sb.append(items[columnIndex[0]-1]);
			for(int i=1;i<=columnIndex.length-1;i++){
				sb.append("\t"+items[columnIndex[i]-1]);
			}
		}
		return sb.toString();
	}
}

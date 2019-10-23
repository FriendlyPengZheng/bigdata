package myDemo_3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class DemoMapper3 extends Mapper<LongWritable, Text, Text, Text>{

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		/*
		 * 获取mapper当前操作分片的文件名
		 */
		FileSplit fileSplit = (FileSplit)context.getInputSplit();
		String fileName = fileSplit.getPath().getName();
		
		String[] line = value.toString().split(" ");
		String miNum = "";
		String tad_time = "";
		miNum = line[0];
		if(fileName.contains("mi_time")){//时间戳
			tad_time = "D" + line[1];
		}
		if(fileName.contains("result1")){//tad
			tad_time = "T" + line[1];
		}
		context.write(new Text(miNum), new Text(tad_time));
	}

	
}

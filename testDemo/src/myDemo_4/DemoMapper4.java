package myDemo_4;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class DemoMapper4 extends Mapper<LongWritable, Text, Text, Text>{

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		/*
		 * 获取mapper当前操作分片的文件名
		 */
		FileSplit fileSplit = (FileSplit)context.getInputSplit();
		String fileName = fileSplit.getPath().getName();
		
		String[] line = value.toString().split(" ");
		String tad = null;
		
		if(fileName.equals("file1.txt")){//4-7第三方注册数据（米米号 tad）
			tad = "abc" + line[1];
		}
		if(fileName.equals("file2.txt")){//五月角色数据（gameId,米米号	tad）
			tad = "ABC" + line[1];
		}
		String miNum = line[0];
		
		context.write( new Text(miNum),new Text(tad));

	}

}

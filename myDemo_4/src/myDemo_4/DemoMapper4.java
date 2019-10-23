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
		String[] line = null;
		String miNum = null;
		String tad = null;
		String gameId = null;
		
		if(fileName.contains("mi_tad_4_7")){//4-7第三方注册数据（米米号 tad）
			line = value.toString().split(" ");
			miNum = line[0];
			tad = "4-7" + line[1];
		}
		if(fileName.contains("part-0000")){//五月角色数据（gameId,米米号	tad）
			line = value.toString().split("\t");
			gameId = line[0].split(",")[0];
			if(gameId.equals("2")){
				miNum = line[0].split(",")[1];
				tad = "5-5" + line[1];
			}else{
				miNum = null;
			}
		}
		
		if(!"".equals(miNum) && miNum != null){
			context.write( new Text(miNum),new Text(tad));
		}
	}

}

package myDemo_2;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DemoMapper2 extends Mapper<LongWritable, Text, Text, Text>{

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		
		String[] line = value.toString().split(" ");
		String miNum = "";
		String tad_time = "";
		miNum = line[0];
		if(line[1].toString().substring(0, 2).equals("15")){//时间戳
			tad_time = "D" + line[1];
		}else{//tad
			tad_time = "T" + line[1];
		}
		context.write(new Text(miNum), new Text(tad_time));
	}

	
}

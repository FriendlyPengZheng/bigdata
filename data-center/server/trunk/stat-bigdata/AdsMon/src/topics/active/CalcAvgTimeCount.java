package topics.active;

import org.apache.hadoop.mapred.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapred.lib.*;

import java.io.*;
import java.util.*;


//计算活跃用户 回流用户 新增用户 以及留存用户的平均登录次数和平均在线时长
//平均登录次数=总登录次数/该类用户数量
//平均登录时长=总登录时长/该类用户数量
public class CalcAvgTimeCount extends Configured implements Tool
{
	static class GTMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text>
	{
	
		private Text outkey = new Text();
		private Text outvalue = new Text();
	
		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
		{
			String[] lines = value.toString().split("\t");
			int line_len = lines.length;
			String[] game = lines[0].split(",");
			outkey.set(String.format("%s", game[0]));
			outvalue.set(String.format("%s\t%s", lines[line_len - 2].trim(),lines[line_len - 1].trim()));
			output.collect(outkey, outvalue);
		}
	}

	static class GTReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> 
	{
		private Text output_key = new Text();
		private Text output_value = new Text();
		
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
		{
			long total_time = 0;
			long total_login = 0;
			int count = 0;
			while(values.hasNext())
			{
				String[] data_value = values.next().toString().split("\t");
				total_login += Long.valueOf(data_value[0]);
				total_time += Long.valueOf(data_value[1]);
				
				count++;
			}

			output_value.set(String.format("%s\t%s", (total_time*100)/count, (total_login*100)/count));

			
			output.collect(key, output_value);
					
		}
	}

	public int run(String[] args) throws Exception
	{
		JobConf conf = new JobConf(this.getConf(), this.getClass());
		conf.setJobName("calcAvgTimeAndCount");
		FileOutputFormat.setOutputPath(conf, new Path(args[args.length - 1]));
		for(int i = 0; i < args.length - 1; i++)
		{
			FileInputFormat.addInputPath(conf, new Path(args[i]));
		}

		conf.setMapperClass(GTMapper.class);
		conf.setMapOutputKeyClass(Text.class);
		conf.setMapOutputValueClass(Text.class);

		conf.setReducerClass(GTReducer.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		JobClient.runJob(conf);
		return 0;
	}

	public static void main(String[] args) throws Exception
	{
		int ret = ToolRunner.run(new CalcAvgTimeCount(), args);
		System.exit(ret);
	}
}

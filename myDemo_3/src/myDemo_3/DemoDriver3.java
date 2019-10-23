package myDemo_3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class DemoDriver3 {

public static void main(String[] args) throws Exception {
		
		//创建一个job = map + reduce
				Configuration conf = new Configuration();
				
				//设置分隔符为“ ”（空格）
				conf.set("mapred.textoutputformat.ignoreseparator","true");
				conf.set("mapred.textoutputformat.separator"," ");
				
				//创建一个Job
				//Job job = Job.getInstance(conf);
				Job job = new Job(conf); 
				//指定任务的入口
				job.setJarByClass(DemoDriver3.class);
				
				//指定job的mapper
				job.setMapperClass(DemoMapper3.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);
				
				//指定job的reducer
				job.setReducerClass(DemoReducer3.class);
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
				
				//指定任务的输入和输出
				FileInputFormat.setInputPaths(job, new Path(args[0]));
				FileOutputFormat.setOutputPath(job, new Path(args[1]));		
				
				//提交任务
				job.waitForCompletion(true);
	}
}

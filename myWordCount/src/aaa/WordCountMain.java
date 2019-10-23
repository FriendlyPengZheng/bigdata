package aaa;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class WordCountMain extends Configured implements Tool {
	
    public static void main(String[] args) throws Exception {
        int status = ToolRunner.run(new WordCountMain(), args);
        System.exit(status);
    }

    public int run(String[] args)
		throws IOException, InterruptedException, ClassNotFoundException {
		//创建一个job = map + reduce
		Configuration conf = new Configuration();
		
		//创建一个Job
		//Job job = Job.getInstance(conf);
		Job job = new Job(conf); 
		//指定任务的入口
		job.setJarByClass(WordCountMain.class);
		
		//指定job的mapper
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		
		//指定job的reducer
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		//指定任务的输入和输出
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));		
		
		//提交任务
		return job.waitForCompletion(true) ? 0:1;
	}
}

/*
public class WordCountMain {

	public static void main(String[] args) throws Exception {
		//创建一个job = map + reduce
		Configuration conf = new Configuration();
		
		//创建一个Job
		//Job job = Job.getInstance(conf);
		Job job = new Job(conf); 
		//指定任务的入口
		job.setJarByClass(WordCountMain.class);
		
		//指定job的mapper
		job.setMapperClass(WordCountMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		
		//指定job的reducer
		job.setReducerClass(WordCountReducer1.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		//指定任务的输入和输出
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));		
		
		//提交任务
		job.waitForCompletion(true);
	}
}
*/
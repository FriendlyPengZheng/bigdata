package com.taomee.tms.driver;

import org.apache.hadoop.io.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.transData.OldLogToNewMapper;
import com.taomee.tms.transData.OldLogToNewReducer;

/*
 * 本地文件系统 local模式
 */
public class JobDriver {

	private static final Logger LOG = LoggerFactory.getLogger(JobDriver.class);

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.framework.name", "local");
		
		Job job = new Job(conf, "JobDriver");
		job.setJarByClass(JobDriver.class); // 设置运行jar中的class名称

		job.setMapperClass(OldLogToNewMapper.class);// 设置mapreduce中的mapper reducer
		job.setReducerClass(OldLogToNewReducer.class);

		job.setOutputKeyClass(Text.class); // 设置输出结果键值对类型
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));// 设置mapreduce输入输出文件路径
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}

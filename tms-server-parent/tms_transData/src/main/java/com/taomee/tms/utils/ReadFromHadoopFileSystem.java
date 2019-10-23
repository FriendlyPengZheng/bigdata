package com.taomee.tms.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class ReadFromHadoopFileSystem {
	public static String[] ReadFromHdfs(String file) throws IOException {
		
		System.out.println("file: " + file);
		Configuration conf = new Configuration();
		FSDataInputStream in = null;
		// FileSystem是用户操作HDFS的核心类，它获得URI对应的HDFS文件系统
		try {
			FileSystem fs = FileSystem.get(URI.create(file), conf);
			Path path = new Path(file);
			in = fs.open(path);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			IOUtils.copyBytes(in, buffer, 4096, true);
			System.out.println("buffer" + buffer);

			buffer.flush();
			
			//System.out.println("buffer_flush" + buffer);
			String[] strs = buffer.toString().split("\n");
		
			buffer.close();
			return strs;
		} finally {
			//IOUtils.closeStream(in);
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	}
}

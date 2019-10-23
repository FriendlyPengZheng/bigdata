package com.taomee.tms.custom.splitlog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 老统计自定义日志打散的第二种方式，打散步骤MR第一步,(相关日志去重)。
 * @author looper
 * @date 2017年8月24日 上午10:24:10
 * @project tms_hadoop CustomLogDuplicateMapper
 */
public class CustomOldLogDuplicateMapper extends Mapper<Object, Text, Text, Text>{
	
	private Text outputKey = new Text();
	
	private Text outputvalue = new Text("1");

	private Logger LOG = LoggerFactory
			.getLogger(CustomOldLogDuplicateMapper.class);
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
	}

	@Override
	protected void map(Object key, Text value,
			Context context)
			throws IOException, InterruptedException {
		String oldLog = value.toString();
		if (value.toString() != null || value.equals("")) {
			
			/**
			 * 解析日志到map当中,根据 \t = 两种符号去去重
			 */
			Map<String, String> maps = new HashMap<>();
			maps.clear();
			String[] oldLog_array = oldLog.split("\t");
			for (String split_log : oldLog_array) {
				String strKv = split_log;
				String[] kv = strKv.split("=", -1);
				if (kv.length < 2) {
					System.out.println("kv键值对有误:"+strKv+", "+oldLog);
					continue;
				}
				maps.put(kv[0], kv[1]);
			}
			String stid = maps.get("_stid_");
			String sstid = maps.get("_sstid_");
			String gid = maps.get("_gid_");
			
			if( (stid !=null || !stid.equals("")) 
					&& (sstid != null || !sstid.equals("")) 
					&& (gid !=null || !gid.equals("")))
			{
				String op = maps.get("_op_");
				
				outputKey.set(stid+"\t"+sstid+"\t"+gid+"\t"+op);
				context.write(outputKey, outputvalue);
			}
			else
			{
				LOG.error("log miss stid-val,or sstid-val,or gid-val:" + oldLog);
			}
		}
	}
	
	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
	}

	
	

}

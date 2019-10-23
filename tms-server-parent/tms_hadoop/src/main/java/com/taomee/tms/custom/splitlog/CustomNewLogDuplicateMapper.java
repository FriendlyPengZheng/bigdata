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
public class CustomNewLogDuplicateMapper extends Mapper<Object, Text, Text, NullWritable>{
	
	private Text outputKey = new Text();
	private NullWritable outputvalue = NullWritable.get();
	private Logger LOG = LoggerFactory.getLogger(CustomNewLogDuplicateMapper.class);
	private Map<String, String> maps = new HashMap<>();
	
	@Override
	protected void setup(Context context)throws IOException, InterruptedException {
	}

	@Override
	protected void map(Object key, Text value,Context context)throws IOException, InterruptedException {
		String newLog = value.toString();
		if (value.toString() != null || value.equals("")) {
			
			//解析日志到map当中,根据 \t = 两种符号去去重
			maps.clear();
			for (String split_log : newLog.split("\t")) {
				String[] kv = split_log.split("=", -1);
				if (kv.length < 2) {
					System.err.println("kv键值对有误:"+split_log+", "+newLog);
					continue;
				}
				maps.put(kv[0], kv[1]);
			}
			String logid = maps.get("_logid_");
			String gid = maps.get("_gid_");
			
			if((logid !=null && !logid.equals(""))){
				outputKey.set(String.format("%s\t%s",logid,gid));
				context.write(outputKey, outputvalue);
			}else{
				LOG.warn("newlog miss logid:" + newLog);
			}
		}
	}
	
	@Override
	protected void cleanup(Context context)throws IOException, InterruptedException {
	}

	
	

}

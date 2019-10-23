package com.taomee.tms.transData;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.taomee.tms.transData.OldBasicLogRefNewLog;
import com.taomee.tms.utils.DataTransUtil;
import com.taomee.tms.utils.ReadFromHadoopFileSystem;
import com.taomee.tms.utils.ReadTxtFile;

/**
 * 
 * 1) 加载stid+sstid -> logid 的映射   serverId->gameId的映射 
 * 2) 对本地每一条日志进行logid的转换操作 
 * 3) 对于转化的日志再进行上传
 */

public class OldLogToNewMapper extends Mapper<Object, Text, Text, Text> {

	
	private OldBasicLogRefNewLog oldBasicLogRefNewLog = new OldBasicLogRefNewLog();
	private Text outputvalue = new Text();
	private Text outputKey = new Text();

	/**
	 * 问题：1.从本地读取映射信息文件，多个map同时对文件进行操作，会导致文件锁住，获取文件失败
	 * 		 2.映射文件上传到hdfs，从hdfs上读取映射文件   
	 * 		 3.映射信息也可保存在数据库中，用dubbo服务对表操作
	 * 		   三种方式在eclipse下均可以使用，1主要是因为在eclipse同一个jvm不会出现1出现的文件锁住的问题
	 */
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		//hdfs读取gpzs->serverId映射信息
		/*String[] gpzsToserverInfos = ReadFromHadoopFileSystem.ReadFromHdfs("/bigdata/conf/serverIdToGameId");
		for(String gpzs :  gpzsToserverInfos) {
			System.out.println("gpzs:" + gpzs);
		}
		oldBasicLogRefNewLog.initServerInfoMaps(gpzsToserverInfos);
		
		System.out.println("init stidToLogId  begin...");
		String[]sstidTologInfos = ReadFromHadoopFileSystem.ReadFromHdfs("/bigdata/conf/Stid_logInfo");
		for(String server :  sstidTologInfos) {
			System.out.println("server:" + server);
		}
		oldBasicLogRefNewLog.initLogInfoMaps(sstidTologInfos);*/
		
		//本地读取映射信息
		//System.out.println("init serverToGameId begin...");
		//List<String> gpzsToserverInfos = new ReadTxtFile().readFile("/home/hadoop/tmsInputCache/serverIdToGameId");
		//List<String> gpzsToserverInfos = new ReadTxtFile().readFile("D://hadoop//test//serverIdToGameId");
		/*for(String gpzs :  gpzsToserverInfos) {
			System.out.println("gpzs:" + gpzs);
		}*/
		//oldBasicLogRefNewLog.initServerInfoMaps(gpzsToserverInfos);
		
		System.out.println("init stidToLogId  begin...");
		List<String> sstidTologInfos = new ReadTxtFile().readFile("/home/hadoop/tmsInputCache/Stid_logInfo");
		//List<String> sstidTologInfos = new ReadTxtFile().readFile("D://hadoop//test//aa.txt");
		/*for(String server :  sstidTologInfos) {
			System.out.println("server:" + server);
		}*/
		oldBasicLogRefNewLog.initLogInfoMaps(sstidTologInfos);	
	}
	
	@Override
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		LinkedHashMap<String, String> maps = new LinkedHashMap<>();
		String oldLog = value.toString();
		if (value.toString() != null || value.equals("")) {
			maps =  oldBasicLogRefNewLog.oldBasicLogToNewLog(oldLog);
		
			if ((!(null == maps)) && value.toString().length() > 0) {
				if (maps.containsKey("_gid_") && maps.containsKey("_ts_")) {
					String gid = maps.get("_gid_");
					String ts = maps.get("_ts_");
			
					String tsDay = new DataTransUtil().timeStamp2Date(ts , "yyyyMMdd_HH");

					String map_key = tsDay + "\t" + gid;

					StringBuilder builder = new StringBuilder();
					for (Map.Entry<String, String> entry : maps.entrySet()) {

						builder.append(entry.getKey() + "=" + entry.getValue());
						builder.append("\t");
					}
					String map_value = builder.toString();

					outputKey.set(map_key);
					outputvalue.set(map_value);
					context.write(outputKey, outputvalue);
				} 
			} else {
				//没有映射关系或者错误的日志
				System.err.println("illegal log:" + oldLog);
				return;
			}
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
	}

}

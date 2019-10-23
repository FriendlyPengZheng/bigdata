package com.taomee.tms.custom.splitlog;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimeLogAnalyser;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimeMaterialLogItem;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimePlainLogItem;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.StidSStidRefLog;
import com.taomee.tms.utils.DateTransfer;

public class CustomTransSplitMapper extends Mapper<LongWritable,Text,Text,Text>{
	private Logger LOG = LoggerFactory.getLogger(CustomTransSplitMapper.class);
	private OldCustomLogRefNewLog oldCustomLogRefNewLog = new OldCustomLogRefNewLog();
	private NonRealtimeLogAnalyser nonRealtimeLogAnalyser = new NonRealtimeLogAnalyser();
	private Text outputValue = new Text();
	private Text outputKey = new Text();
	private LogMgrService logMgrService;
	private Map<String,String> maps = new LinkedHashMap<String,String>();
	private MultipleOutputs mos;
	private StringBuilder strOutputValue = new StringBuilder();
	
	
	@Override
	protected void setup(Context context)throws IOException, InterruptedException {
		LOG.info("CustomTransSplitMapper setup() method starting...");
		
		ApplicationConfig application = new ApplicationConfig();
		application.setName("custom-split-trans");
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress("10.1.1.35:2181");
		ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>();
		reference.setApplication(application);
		reference.setRegistry(registry);
		reference.setInterface(LogMgrService.class);
		logMgrService = reference.get();

		List<StidSStidRefLog> sStidGidRefLogIds = logMgrService.getStidSStidRefLogFromRedis("sssgidop2logid_*");
		LOG.info("get stidsstidGidRefLogIds list from logMgrService,list size:{}" ,sStidGidRefLogIds.size());
		oldCustomLogRefNewLog.init2MapsInfo(sStidGidRefLogIds,logMgrService);
		List<SchemaInfo> schemaInfosFromRedis = logMgrService.getSchemaInfosFromRedis("sch_*");
		LOG.info("get SchemaInfo list from logMgrService,list size:{}" ,schemaInfosFromRedis.size());
		nonRealtimeLogAnalyser.Init(schemaInfosFromRedis,logMgrService.getAllServerInfos());
		
		mos = new MultipleOutputs(context);
	}

	@Override
	protected void map(LongWritable key, Text value,Context context)throws IOException, InterruptedException {
		maps.clear();
		if (value.toString() != null && !value.toString().equals("")) {
			String newLog = oldCustomLogRefNewLog.oldCustomLogToNewCustomLog(value.toString());//这一步主要是处理映射信息，且都在内存当中执行映射转换
			
			if (newLog.length() > 0) { //newLog包括: 1) 0长度的字符串 ; 2) 特殊日志; 3) 自定义转化后的日志
				for (String logSplit : newLog.split("\\s+")) {
					String[] kv = logSplit.split("=", -1);
					if (kv.length < 2) {
						System.err.println("kv键值对有误");
						continue;
					}
					maps.put(kv[0], kv[1]);
				}
			}else{
				String oldLog = value.toString();
				for (String logSplit : oldLog.split("\\s+")) {
					String[] kv = logSplit.split("=", -1);
					if (kv.length < 2) continue;
					maps.put(kv[0], kv[1]);
				}
				outputKey.set(oldLog);
				outputValue.set("");
//				mos.write("errorOldLogG"+maps.get("_gid_"), key, outputValue);
				mos.write(outputKey, outputValue, "errorOldLogG"+maps.get("_gid_"));
				return;
			}
			
			if(nonRealtimeLogAnalyser.SetLog(newLog) && !nonRealtimeLogAnalyser.IsDealingSpecailLog()) {
				List<NonRealtimePlainLogItem> plainItems = nonRealtimeLogAnalyser.GetAllNonRealtimePlainLogItems();
				if (plainItems != null) {
					for (NonRealtimePlainLogItem item: plainItems) {
						if (item.getServerId().equals("")|| item.getGameId().equals("")|| item.getServerId().equals("")|| item.getStrOp().equals("")) LOG.error("log miss field...");
						outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",item.getGameId(), item.getSchemaId(),item.getServerId(), item.getCascadeValue(),item.getStrOp()));
						
						strOutputValue.setLength(0);
						for (String s : item.getOpValues()) {
							if (!s.equals("")) {
								strOutputValue.append(s + "|");
							}
						}
						
						if (strOutputValue.length() != 0) {
							outputValue.set(strOutputValue.substring(0,strOutputValue.lastIndexOf("|")));// 级联字段是list?
						} else {
							outputValue.set(strOutputValue.toString());
						}
						context.write(outputKey, outputValue);
					}
				}
				
				List<NonRealtimeMaterialLogItem> materialItems = nonRealtimeLogAnalyser.GetAllNonRealtimeMaterailLogItems();
				if (materialItems != null) {
					for (NonRealtimeMaterialLogItem item: materialItems) {
						if (item.getServerId().equals("")|| item.getGameId().equals("")|| item.getServerId().equals("")|| item.getStrOp().equals(""))LOG.error("log miss field...");
						outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",item.getGameId(), item.getMaterialId(),item.getServerId(), item.getCascadeValue(),item.getStrOp()));
						
						strOutputValue.setLength(0);
						for (String s : item.getOpValues()) {
							if (!s.equals("")) {
								strOutputValue.append(s + "|");
							}
						}
						
						if (strOutputValue.length() != 0) {
							outputValue.set(strOutputValue.substring(0,
									strOutputValue.lastIndexOf("|")));// 级联字段是list?
						} else {
							outputValue.set(strOutputValue.toString());
						}
						context.write(outputKey, outputValue);
					}
				}
			}else{
				outputKey.set(newLog);
				outputValue.set("");
//				mos.write("errorNewLogG"+maps.get("_gid_"), key, outputValue);
				mos.write(outputKey, outputValue,"errorNewLogG"+maps.get("_gid_"));
				return;
			}
 		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		mos.close();
	}
}

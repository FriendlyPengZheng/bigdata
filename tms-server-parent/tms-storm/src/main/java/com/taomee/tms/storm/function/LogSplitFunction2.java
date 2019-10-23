package com.taomee.tms.storm.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.core.loganalyser.RealtimeLogAnalyser2;
import com.taomee.tms.mgr.core.loganalyser.RealtimePlainLogItem;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class LogSplitFunction2 extends BaseFunction {
	private static final long serialVersionUID = 5097236152288190818L;
	private static final Logger LOG = LoggerFactory.getLogger(LogSplitFunction2.class);
	private RealtimeLogAnalyser2 realtimelogAnalyser;
	private Integer partitionIndex;
//	private Configer config;
	protected LogMgrService logMgrService;
	private Properties properties;
	
	public LogSplitFunction2(){
		if(this.properties == null){
			this.properties = new TmsProperties(System.getProperty("user.home")+"/storm/tms-storm.properties");
		}
	}
	
	public LogSplitFunction2(Properties properties){
		this.properties = properties;
	}

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		this.partitionIndex = context.getPartitionIndex();
		LOG.info("partition {} LogSplitFunction2 prepare() method starting...",this.partitionIndex);
		
    	ApplicationConfig application = new ApplicationConfig();
    	application.setName(this.getClass().toString().split(" ")[1]);
    	
    	RegistryConfig registry = new RegistryConfig();
    	registry.setProtocol(properties.getProperty("dubboRegistryProtocol"));
    	registry.setAddress(properties.getProperty("dubboRegistryAdress"));
    	
    	ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
    	reference.setApplication(application);
    	reference.setRegistry(registry);
    	reference.setInterface(LogMgrService.class);
    	
    	this.logMgrService = reference.get();
    	
    	realtimelogAnalyser = new RealtimeLogAnalyser2(logMgrService);
    	
    	List<SchemaInfo> initialSchemaInfos = getInitialSchemaInfos();
    	List<ServerInfo> initialServerInfos = getInitialServerInfos();

		try {
			LOG.info("partition {} get {} schemainfos by logMgrService from redis,loading into memory...",this.partitionIndex,initialSchemaInfos.size());
			boolean ret = false;
			if(this.properties.get("running-mode") != null && this.properties.get("running-mode").equals("local")){
				ret = realtimelogAnalyser.Init(new ArrayList<SchemaInfo>(),initialServerInfos);
			}else{
				ret = realtimelogAnalyser.Init(initialSchemaInfos,initialServerInfos);
			}
			LOG.info("partition {} loaded {} schemainfos from redis to memory.",this.partitionIndex,initialSchemaInfos.size());

			if (!ret) {
				LOG.error("partition {} realtimelogAnalyser Init failed,return.",this.partitionIndex);
				return;
			}
		} catch (Exception e) {
			LOG.error("partition {} catch Exception during realtimelogAnalyser Init() method." + e.getMessage(),this.partitionIndex);
			e.printStackTrace();
		}
		
		LOG.info("partition {} LogSplitFunction2 finish prepare() method.",this.partitionIndex);
		
	}

 	protected List<ServerInfo> getInitialServerInfos() {
		return this.logMgrService.getAllServerInfos();
	}

	protected List<SchemaInfo> getInitialSchemaInfos() {
		return null;
	}

	/**
 	 * output <timestamp, cmd_return_status, return_code, account_id>
 	 **/
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String value = tuple.getString(0);
		
		if (!(value==null || value.equals("")) && realtimelogAnalyser.SetLog(value)) {//value不能为null或者"",这层主要是过滤掉上一级function的logid信息以及一些特殊信息.
			
			if (realtimelogAnalyser.IsDealingSpecailLog()) {
				if (!realtimelogAnalyser.UpdateConfig()) {
					LOG.error("partition {} LogSplitFunction execute, UpdateConfig failed",this.partitionIndex);
					return;
				}
			} else {
				List<RealtimePlainLogItem> logItems = realtimelogAnalyser.GetAllRealTimeLogItems();
				if (logItems != null) {
					for (RealtimePlainLogItem item : logItems) {
						List<Object> values = new ArrayList<Object>();
						values.add(item.getStrOp());
						values.add(item.getSchemaId());
						values.add(item.getServerId());
						values.add(item.getCascadeValue());
						values.add(item.getOpValues());
						values.add(item.getDateTime());
						collector.emit(values);
						LOG.debug(
								"partition {} emitting [{},{},{},{},{},{}]",//serverID,schemaID,op,op value,cascade value,date time 
								this.partitionIndex,
								item.getServerId(),
								item.getSchemaId(),
								item.getStrOp(),
								item.getOpValues(),
								item.getCascadeValue(),
								item.getDateTime()
								);
					}
				}
			}
		}else{
			if((value==null || value.equals(""))){
				LOG.debug("Error:input log is null!");
			}else{
				LOG.debug("Error:fail to process log \"{}\"",value);
			}
		}
	}
	
////	private RealtimeLogAnalyser GetTestLogAnalyser() {
////		RealtimeLogAnalyser realtimeLogAnalyser = new RealtimeLogAnalyser();
////		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
////		SchemaInfo si1 = new SchemaInfo();
////		si1.setLogId(100);
////		si1.setSchemaId(1);
////		si1.setOp("material(_acid_,_amt_)");
////		si1.setCascadeFields("_pid_|_zid_|_sid_");
////		schemaInfos.add(si1);
////		// count
////		SchemaInfo si2 = new SchemaInfo();
////		si2.setLogId(100);
////		si2.setSchemaId(2);
////		si2.setOp("count()");
////		si2.setCascadeFields("_pid_|_zid_|_sid_");
////		schemaInfos.add(si2);
////		// distinct_count
////		SchemaInfo si3 = new SchemaInfo();
////		si3.setLogId(100);
////		si3.setSchemaId(3);
////		si3.setOp("distinct_count(_acid_)");
////		si3.setCascadeFields("");
////		schemaInfos.add(si3);
////		// sum
////		SchemaInfo si4 = new SchemaInfo();
////		si4.setLogId(100);
////		si4.setSchemaId(4);
////		si4.setOp("sum(_amt_)");
////		si4.setCascadeFields("");
////		schemaInfos.add(si4);
////		// max
////		SchemaInfo si5 = new SchemaInfo();
////		si5.setLogId(100);
////		si5.setSchemaId(5);
////		si5.setOp("max(_amt_)");
////		si5.setCascadeFields("");
////		schemaInfos.add(si5);
////		// min
////		SchemaInfo si6 = new SchemaInfo();
////		si6.setLogId(100);
////		si6.setSchemaId(6);
////		si6.setOp("min(_amt_)");
////		si6.setCascadeFields("");
////		schemaInfos.add(si6);
////		// assign
////		SchemaInfo si7 = new SchemaInfo();
////		si7.setLogId(100);
////		si7.setSchemaId(7);
////		si7.setOp("assign(_amt_)");
////		si7.setCascadeFields("");
////		schemaInfos.add(si7);
////		
////		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
////		ServerInfo s1 = new ServerInfo();
////		s1.setServerId(1);
////		s1.setParentId(0);
////		s1.setGameId(16);
////		serverInfos.add(s1);
////		
////		ServerInfo s2 = new ServerInfo();
////		s2.setServerId(2);
////		s2.setParentId(1);
////		s2.setGameId(16);
////		serverInfos.add(s2);
////		
////		ServerInfo s3 = new ServerInfo();
////		s3.setServerId(3);
////		s3.setParentId(1);
////		s3.setGameId(16);
////		serverInfos.add(s3);
////		
////		ServerInfo s4 = new ServerInfo();
////		s4.setServerId(4);
////		s4.setParentId(2);
////		s4.setGameId(16);
////		serverInfos.add(s4);
////		
////		ServerInfo s5 = new ServerInfo();
////		s5.setServerId(5);
////		s5.setParentId(2);
////		s5.setGameId(16);
////		serverInfos.add(s5);
////		
////		ServerInfo s6 = new ServerInfo();
////		s6.setServerId(6);
////		s6.setParentId(3);
////		s6.setGameId(16);
////		serverInfos.add(s6);
////		
////		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
////		
////		return realtimeLogAnalyser;
////	}
//	
//	public static void main(String[] args) {
//		RealtimeLogAnalyser realtimeLogAnalyser = new RealtimeLogAnalyser();
//		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
//		
////		 * 1	material()
////		 * 2	count()
////		 * 3	distinct_count(key)
////		 * 4	sum(key)
////		 * 5	max(key)
////		 * 6	min(key)
////		 * 7	assign(key)
//		// material
//		String strLog = "_hip_=192.168.111.129   _logid_=100	_svrid_=6       _gid_=25        _zid_=3        _sid_=4	_amt_=10        _pid_=2        _ts_=1483801210 _acid_=583895372";
//		
//		SchemaInfo si1 = new SchemaInfo();
//		si1.setLogId(100);
//		si1.setSchemaId(1);
//		si1.setOp("material(_acid_,_amt_)");
//		si1.setCascadeFields("_pid_|_zid_|_sid_");
//		schemaInfos.add(si1);
//		// count
//		SchemaInfo si2 = new SchemaInfo();
//		si2.setLogId(100);
//		si2.setSchemaId(2);
//		si2.setOp("count()");
//		si2.setCascadeFields("_pid_|_zid_|_sid_");
//		schemaInfos.add(si2);
//		// distinct_count
//		SchemaInfo si3 = new SchemaInfo();
//		si3.setLogId(100);
//		si3.setSchemaId(3);
//		si3.setOp("distinct_count(_acid_)");
//		si3.setCascadeFields("");
//		schemaInfos.add(si3);
//		// sum
//		SchemaInfo si4 = new SchemaInfo();
//		si4.setLogId(100);
//		si4.setSchemaId(4);
//		si4.setOp("sum(_amt_)");
//		si4.setCascadeFields("");
//		schemaInfos.add(si4);
//		// max
//		SchemaInfo si5 = new SchemaInfo();
//		si5.setLogId(100);
//		si5.setSchemaId(5);
//		si5.setOp("max(_amt_)");
//		si5.setCascadeFields("");
//		schemaInfos.add(si5);
//		// min
//		SchemaInfo si6 = new SchemaInfo();
//		si6.setLogId(100);
//		si6.setSchemaId(6);
//		si6.setOp("min(_amt_)");
//		si6.setCascadeFields("");
//		schemaInfos.add(si6);
//		// assign
//		SchemaInfo si7 = new SchemaInfo();
//		si7.setLogId(100);
//		si7.setSchemaId(7);
//		si7.setOp("assign(_amt_)");
//		si7.setCascadeFields("");
//		schemaInfos.add(si7);
//		
//		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
//		ServerInfo s1 = new ServerInfo();
//		s1.setServerId(1);
//		s1.setParentId(0);
//		s1.setGameId(16);
//		serverInfos.add(s1);
//		
//		ServerInfo s2 = new ServerInfo();
//		s2.setServerId(2);
//		s2.setParentId(1);
//		serverInfos.add(s2);
//		
//		ServerInfo s3 = new ServerInfo();
//		s3.setServerId(3);
//		s3.setParentId(1);
//		serverInfos.add(s3);
//		
//		ServerInfo s4 = new ServerInfo();
//		s4.setServerId(4);
//		s4.setParentId(2);
//		serverInfos.add(s4);
//		
//		ServerInfo s5 = new ServerInfo();
//		s5.setServerId(5);
//		s5.setParentId(2);
//		serverInfos.add(s5);
//		
//		ServerInfo s6 = new ServerInfo();
//		s6.setServerId(6);
//		s6.setParentId(3);
//		serverInfos.add(s6);
//		
//		if (realtimeLogAnalyser.Init(schemaInfos, serverInfos)) {
//			System.out.println("-----------------------------------");
//			if (realtimeLogAnalyser.SetLog(strLog)) {
//				List<RealtimePlainLogItem> items = realtimeLogAnalyser.GetAllRealTimeLogItems();
//				if (items != null) {
//					for (RealtimePlainLogItem item: items) {
//						System.out.print("\n" + item.toString());
//					}
//				}
//			}
//		}
//	}

}

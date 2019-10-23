package com.taomee.tms.custom.splitlog;

import java.io.IOException;


import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimeLogAnalyser;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimeMaterialLogItem;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimePlainLogItem;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.api.LogMgrService;

/**
 * 
 * @author looper
 * @date 2017年1月16日 下午3:21:42
 * @project tms_hadoop BeginBasicSplitMapper
 */
public class BeginCustomSplitMapper extends Mapper<Object, Text, Text, Text> {

	/**
	 * 该方法在task只会被调用一次，可以用来初始化一些资源，比如从数据库里面读取一些配置信息
	 * 在这个方法当中，我们用来从tms数据库里面读取T_S_M_S四张表的信息
	 */
//	private List<LSMInfo> lsmInfos = new ArrayList<LSMInfo>();
	private List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();// 保存schema表的信息

	private List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();// 保存Server的信息

	private Text outputvalue = new Text();
	private Text outputKey = new Text();

	private Logger LOG = LoggerFactory.getLogger(BeginCustomSplitMapper.class);
	private NonRealtimeLogAnalyser nonRealtimeLogAnalyser = new NonRealtimeLogAnalyser();

	@Override
	protected void setup(Context context) throws IOException,InterruptedException {

		ApplicationConfig application = new ApplicationConfig();
    	application.setName("tms_hadoop_break_up_custom");
		// 连接注册中心配置
    	RegistryConfig registry = new RegistryConfig();
    	registry.setProtocol("zookeeper");
    	registry.setAddress("10.1.1.35:2181");
    	 
    	// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接    	 
    	// 引用远程服务
    	ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>();
    	reference.setApplication(application);
    	reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
    	reference.setInterface(LogMgrService.class);
    	 
    	// 和本地bean一样使用xxxService
    	LogMgrService logMgrService = reference.get();
    	
		// 获取SchemaInfo
		schemaInfos = logMgrService.getSchemaInfosByLogType(Integer.valueOf(1));
		/*for(SchemaInfo schemaInfo:schemaInfos)
		{
			System.out.println("schemaInfo:"+schemaInfo);
		}*/

		// 获取ServerInfo信息
		serverInfos = logMgrService.getAllServerInfos(Integer.valueOf(0));
		/*for(ServerInfo serverInfo:serverInfos)
		{
			System.out.println("serverInfo:"+serverInfo);
		}*/
		//非实时类日志类初始化失败
		//nonRealtimeLogAnalyser.Init(schemaInfos, serverInfos);
		if(!nonRealtimeLogAnalyser.Init(schemaInfos, serverInfos));
		{
			LOG.error("NonRealtimeLogAnalyser init Failed!");
			return;
		}

	}

	/**
	 * 执行日志Mapper的打散逻辑
	 */
	@Override
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		/**
		 * 需要解析的日志格式
		 * 格式1:LogItemBean [strOp=sum, schemaId=4, serverId=1, cascadeValue=, opValues=[10], gameId=16]
		 * 格式2:LogItemBean [strOp=material, schemaId=1, serverId=6, cascadeValue=, opValues=[583895372, 10], gameId=16, materialId=130]
		 */
		if(nonRealtimeLogAnalyser.SetLog(value.toString()) && !nonRealtimeLogAnalyser.IsDealingSpecailLog())
		{
			List<NonRealtimePlainLogItem> plainItems = nonRealtimeLogAnalyser.GetAllNonRealtimePlainLogItems();
			if (plainItems != null) {
				for (NonRealtimePlainLogItem item: plainItems) {
					if (item.getServerId().equals("")
							|| item.getGameId().equals("")
							|| item.getServerId().equals("")
							|| item.getStrOp().equals("")) {
						LOG.error("log miss field...");
					}
					outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
							item.getGameId(), item.getSchemaId(),
							item.getServerId(), item.getCascadeValue(),
							item.getStrOp()));
					
					StringBuilder strOutputValue = new StringBuilder();
					for (String s : item.getOpValues()) {
						if (!s.equals("")) {
							strOutputValue.append(s + "|");
						}
					}

					if (strOutputValue.length() != 0) {
						outputvalue.set(strOutputValue.substring(0,
								strOutputValue.lastIndexOf("|")));// 级联字段是list?
					} else {
						outputvalue.set(strOutputValue.toString());
					}
					context.write(outputKey, outputvalue);
					
				}
			}
			
			List<NonRealtimeMaterialLogItem> materialItems = nonRealtimeLogAnalyser.GetAllNonRealtimeMaterailLogItems();
			if (materialItems != null) {
				for (NonRealtimeMaterialLogItem item: materialItems) {
					if (item.getServerId().equals("")
							|| item.getGameId().equals("")
							|| item.getServerId().equals("")
							|| item.getStrOp().equals("")) {
						LOG.error("log miss field...");
					}
					outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
							item.getGameId(), item.getMaterialId(),
							item.getServerId(), item.getCascadeValue(),
							item.getStrOp()));
					
					StringBuilder strOutputValue = new StringBuilder();
					for (String s : item.getOpValues()) {
						if (!s.equals("")) {
							strOutputValue.append(s + "|");
						}
					}

					if (strOutputValue.length() != 0) {
						outputvalue.set(strOutputValue.substring(0,
								strOutputValue.lastIndexOf("|")));// 级联字段是list?
					} else {
						outputvalue.set(strOutputValue.toString());
					}
					context.write(outputKey, outputvalue);
				}
			}
		}
		else
		{
			return;
		}		
	}
	
	/**
	 * 在一次task任务的最后调用该方法，比如处理一些资源收回等一些任务
	 */
	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		super.cleanup(context);
	}
}

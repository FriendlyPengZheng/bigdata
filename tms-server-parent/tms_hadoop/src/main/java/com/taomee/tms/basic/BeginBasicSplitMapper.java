package com.taomee.tms.basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.taomee.tms.mgr.core.loganalyser.NonRealtimeLogAnalyser;

import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.api.LogMgrService;

/**
 * 日志打散Mapper
 * 
 * @author looper
 * @date 2016年10月26日
 */
public class BeginBasicSplitMapper extends Mapper<Object, Text, Text, Text> {

	/**
	 * 该方法在task只会被调用一次，可以用来初始化一些资源，比如从数据库里面读取一些配置信息
	 * 在这个方法当中，我们用来从tms数据库里面读取T_S_M_S四张表的信息
	 */
	/* private List<LSMInfo> lsmInfos = new ArrayList<LSMInfo>(); */
	private List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();// 保存schema表的信息

	private List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();// 保存Server的信息

	private Text outputvalue = new Text();
	private Text outputKey = new Text();

	private Logger LOG = LoggerFactory.getLogger(BeginBasicSplitMapper.class);

	private HashMap<Integer, Integer> schemaIdReflectMaterialId = new HashMap<Integer, Integer>();// 保存schemaId到materiaId的映射

	private NonRealtimeLogAnalyser nonRealtimeLogAnalyser = new NonRealtimeLogAnalyser();

	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {

		// 调用dubbo服务
		/*ClassPathXmlApplicationContext context1 = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		context1.start();
		LogMgrService logMgrService = (LogMgrService) context1
				.getBean("LogMgrService"); // 获取bean
*/
		LogMgrService logMgrService = null;
		// 获取SchemaInfo
		schemaInfos = logMgrService.getSchemaInfosByLogType(Integer.valueOf(0));

		// 获取ServerInfo信息
		serverInfos = logMgrService.getAllServerInfos(Integer.valueOf(0));

		schemaIdReflectMaterialId.clear();// 清空HashMap表信息

		for (SchemaInfo s : schemaInfos)// 迭代schemaInfo的信息
		{
			if (s.getMaterialId() > 0) {
				boolean flag = schemaIdReflectMaterialId.containsKey(s
						.getSchemaId());
				if (flag == true) {
					break;
				} else {
					schemaIdReflectMaterialId.put(s.getSchemaId(),
							s.getMaterialId());
				}
			}
		}

		nonRealtimeLogAnalyser.Init(schemaInfos, serverInfos);

	}

	/**
	 * 执行日志Mapper的打散逻辑
	 */
	@Override
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {

		Boolean flag;
		flag = nonRealtimeLogAnalyser.SetLog(value.toString());

		if (flag) {
			/*List<NonRealtimeLogItem> items = nonRealtimeLogAnalyser
					.GetAllNonRealTimeLogItems();
			for (NonRealtimeLogItem item : items) {
				if (schemaIdReflectMaterialId.containsKey(Integer.valueOf(item
						.getSchemaId()))) {

					item.setSchemaId(schemaIdReflectMaterialId.get(
							Integer.valueOf(item.getSchemaId())).toString());// 如果schemaId在对应HashMap当中，将对应的value替换之前的schemaId，避免对应里面添加字段

				}
				// if(item.get)
				// 日志格式
				// [strOp=material, schemaId=1, serverId=1, cascadeValue=,
				// opValues=[583895372, 10], gameId=16]

				if (item.getServerId().equals("")
						|| item.getGameId().equals("")
						|| item.getServerId().equals("")
						|| item.getStrOp().equals("")) {
					LOG.error("log miss filed");
				}
				// 设置Map的Key
				outputKey.set(String.format("%s\t%s\t%s\t%s\t%s",
						item.getGameId(), item.getSchemaId(),
						item.getServerId(), item.getCascadeValue(),
						item.getStrOp()));

				// 因为日志类返回的Opvalue是list，所以暂且对其解析成Opvalue1|Opvalue2...
				//////////////////////////////////////////////////////////////////
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
				/////////////////////////////////////////////////////////////////
				
				// LOG.error("Map key:"+outputKey.toString()+"\t"+outputKey.toString().split("\t").length);
				context.write(outputKey, outputvalue);
			}*/
		} else {
			return;
		}
	}

	/**
	 * 在一次task任务的最后调用该方法，比如处理一些资源收回等一些任务
	 */
	@Override
	protected void cleanup(Context context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		// dbTemplate.closeConn(dbTemplate.getConn());
		super.cleanup(context);
	}
}

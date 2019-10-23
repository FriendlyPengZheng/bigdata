package com.taomee.tms.test.taskBeginSetup;

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
	private List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
	private Map<Integer, List<String>> gid_serverInfos = new HashMap<Integer, List<String>>();
	//private DbTemplate dbTemplate = null;
	// 映射logId到Schema的映射
	private Map<String, List<SchemaInfo>> logIdReferSchemaInfos = new HashMap<String, List<SchemaInfo>>();
	// 输出value值
	private Text outputvalue = new Text();
	private Logger LOG=LoggerFactory.getLogger(BeginBasicSplitMapper.class);

	//private int a;
	/* private Map<String, String> log_kv_records=new HashMap<String, String>(); */
	@Override
	protected void setup(Context context) throws IOException,
			InterruptedException {
		
		//调用dubbo服务
		/*ClassPathXmlApplicationContext context1 = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		context1.start();
		LogMgrService logMgrService = (LogMgrService) context1.getBean("LogMgrService"); // 获取bean
*/		
		
		//a = 10;
		// TODO Auto-generated method stub
		/* super.setup(context); */
		//ApplicationContext applicationContext = new ClassPathXmlApplicationContext("db.xml");
		//dbTemplate = (DbTemplate) applicationContext.getBean("dbtemplate");
		try {
			/*
			 * lsmInfos = dbTemplate .getBeanList(
			 * "select t_schema_info.schema_id, t_schema_info.log_id, t_schema_info.op, t_schema_info.cascade_fields, t_schema_info.material_id, t_schema_info.add_time  from t_schema_info inner join t_log_info on t_schema_info.log_id = t_log_info.log_id  where t_log_info.type = 0"
			 * , LSMInfo.class, null);
			 */
			/*schemaInfos = dbTemplate
					.getBeanList(
							"select t_schema_info.schema_id, t_schema_info.log_id, t_schema_info.op, t_schema_info.cascade_fields, t_schema_info.material_id, t_schema_info.add_time  from t_schema_info inner join t_log_info on t_schema_info.log_id = t_log_info.log_id  where t_log_info.type = 0",
							SchemaInfo.class, null);*/
			//schemaInfos = logMgrService.getSchemaInfosByLogType(Integer.valueOf(0));
			
			//edit
			////////////////////
			System.out.println("SchemeInfo query...");
			for(SchemaInfo info:schemaInfos)
			{
				System.out.print(info+"-->");
			}
			System.out.println();
			/////////////////
			logIdReferSchemaInfos.clear();
			for (SchemaInfo schemaInfo : schemaInfos) {
				if (!(logIdReferSchemaInfos.containsKey(schemaInfo.getLogId()
						.toString()))) {
					List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
					schemaInfos.add(schemaInfo);
					logIdReferSchemaInfos.put(
							schemaInfo.getLogId().toString(), schemaInfos);
				} else {
					List<SchemaInfo> schemaInfoExists = logIdReferSchemaInfos
							.get(schemaInfo.getLogId().toString());
					schemaInfoExists.add(schemaInfo);
				}
			}

			List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
			/*
			 * 读取t_ser_info
			 */
			gid_serverInfos.clear();
			//serverInfos = logMgrService.getAllServerInfos(Integer.valueOf(0));
			
			//edit
			///////////////////////
			System.out.println("serverInfo query...");
			for(ServerInfo info:serverInfos)
			{
				System.out.println(info+"......");
				
			}
			System.out.println();
			///////////////////
			/*serverInfos = dbTemplate
					.getBeanList(
							"select server_id,game_id,parent_id,is_leaf from t_server_info where status=0 order by game_id asc;",
							ServerInfo.class, null);*/

			gid_serverInfos.clear();
			// 把所有从数据库里面查询的信息存储在Map里面，但是该Map的信息是无序的
			for (ServerInfo s : serverInfos) {
				if (!gid_serverInfos.containsKey(s.getGameId())) {
					List<String> list = new ArrayList<String>();
					String sid_pid = s.getServerId() + "_" + s.getParentId();
					list.add(sid_pid);
					gid_serverInfos.put(s.getGameId(), list);
				} else {
					List<String> sid_pids = gid_serverInfos.get(s.getGameId());
					sid_pids.add(s.getServerId() + "_" + s.getParentId());
				}
			}
			// 让Map里面的数据value有序
			for (Entry<Integer, List<String>> entry : gid_serverInfos
					.entrySet()) {
				Map<Integer, Integer> map_tmp = new HashMap<Integer, Integer>();
				Set<Integer> sets = new LinkedHashSet<Integer>();
				Integer root = 0;
				map_tmp.clear();
				for (String s : entry.getValue()) {
					String value[] = s.split("_");
					// if()
					map_tmp.put(Integer.parseInt(value[0]),
							Integer.parseInt(value[1]));
					if (Integer.parseInt(value[1]) == 0) {
						root = Integer.parseInt(value[0]);
						sets.add(root);
					}
				}
				/* for(map_tmp) */
				while (sets.size() != map_tmp.size()) {
					for (Entry<Integer, Integer> entry2 : map_tmp.entrySet()) {
						if (entry2.getValue() == root) {
							root = entry2.getKey();
							sets.add(root);
						}
					}
				}
				entry.getValue().clear();
				String value3 = new String();
				for (Integer s2 : sets) {
					value3 = value3 + s2 + "|";
				}
				value3 = value3.substring(0, value3.lastIndexOf("|"));
				entry.getValue().add(value3);

				/*
				 * System.out.println("key:" + entry.getKey() + "------>"
				 * +"value:" + entry.getValue());
				 */
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 执行日志Mapper的打散逻辑
	 */
	@Override
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		///////////////////////////////////////////////
		if(logIdReferSchemaInfos.size() == 0){
			System.out.println("未获取到schema_id");
		}else {
			System.out.println("SchemeInfo query...");
			for(SchemaInfo info:schemaInfos) {
				System.out.print(info+"-->");
			}
			System.out.println();
		}
		///////////////////////////////////////////////
		
		// 1.save 信息到kv map当中
		Map<String, String> log_kv_records = new HashMap<String, String>();
		String key_values[] = value.toString().split("\t");
		for (String s : key_values) {
			String key_value[] = s.split("=");
			log_kv_records.put(key_value[0], key_value[1]);
		}

		// 判断logid信息是否存在SchemeInfo的kv map当中，其中SchemeInfo的kv map保存的是t_log_info
		// 和t_scheme_info表的关联信息

		if (logIdReferSchemaInfos.containsKey(log_kv_records.get("_logid_"))) {

			// 获取logid对应的schema_info集合信息
			List<SchemaInfo> schemaInfos = logIdReferSchemaInfos
					.get(log_kv_records.get("_logid_"));
			String cascade_field_value = new String();
			// op为表t_schema_info的op字段信息,op_value表示op对应括号里key的value值，splitServInfo保存打散的servInfo

			Text reduceKeyOp = new Text();
			String op = new String();
			/*
			 * String splitServInfo = new String(); String splitCascadeFieldInfo
			 * = new String();
			 */
			// String cascade_fields[]
			for (SchemaInfo lsmInfo : schemaInfos) {

				cascade_field_value = new String();
				// op为表t_schema_info的op字段信息。
				op = lsmInfo.getOp();
				// op_value = new String();
				// value2 = new String();

				// /////////////////////////////////////////////////////////////////////////////
				// 获取级联字段的信息，没有就是空字符串，有就解析
				/*
				 * System.out.println("数据库读取的级联配置信息:"+lsmInfo.getCascade_fields()
				 * );
				 */
				// 去除以""结尾的标志
				if (lsmInfo.getCascadeFields() == ""
						|| lsmInfo.getCascadeFields().length() == 0
						|| lsmInfo.getCascadeFields().equals("")) {

				} else {
					String cascade_fields[] = lsmInfo.getCascadeFields()
							.split("\\|");
					for (String cascade_field : cascade_fields) {
						if (log_kv_records.get(cascade_field) != null) {
							cascade_field_value = cascade_field_value
									+ log_kv_records.get(cascade_field) + "|";
						} else {
							// break;
						}
					}
					/*
					 * System.out.println("cascade_field_value：" +
					 * cascade_field_value);
					 */
					//避免虽然配置文件当中配置了级联字段，但是日志当中没有对应的日志字段
					if(cascade_field_value.length()!=0 || cascade_field_value.equals("") || cascade_field_value=="")
					{
						LOG.error(lsmInfo.getCascadeFields()+"字段存在,但是日志没有落对应日志");
					}
					else
					{
					cascade_field_value = cascade_field_value.substring(0,
							cascade_field_value.lastIndexOf("|"));
					}
					//System.out.println("级联字段:" + cascade_field_value);
				}
				// ////////////////////////////////////////////////////////////////////////////////

				// ////////////////////////////////////////////////////////////////////////////////
				// 获取op，以及op后面key对应日志当中的value

				String op_value = new String();
				String value2 = new String();

				if (op.contains("(") && op.contains(")")) {
					value2 = op.substring(op.lastIndexOf("(") + 1,
							op.lastIndexOf(")"));
					op = op.substring(0, op.lastIndexOf("("));
					String op_keys[] = value2.split(",");
					for (String s_op_key : op_keys) {
						s_op_key = "_" + s_op_key + "_";
						String log_op_key_to_value = log_kv_records
								.get(s_op_key);
						op_value = op_value + log_op_key_to_value + ",";
					}
					op_value = op_value.substring(0, op_value.lastIndexOf(","));

				} else {

				}
				// /////////////////////////////////////////////////////////////////////////////////

				if (gid_serverInfos.containsKey(Integer.parseInt(log_kv_records
						.get("_gid_")))) {

					List<String> servInfo = gid_serverInfos.get(Integer
							.parseInt(log_kv_records.get("_gid_")));
					String splitServInfo = new String();

					reduceKeyOp = new Text(op);
					for (String serv : servInfo) {

						String servs[] = serv.split("\\|");
						for (String isBeSplitServ : servs) {
							// save被打散的服务器信息
							splitServInfo = splitServInfo + isBeSplitServ + "|";
							if (cascade_field_value.length() != 0) {
								// for()
								String str[] = cascade_field_value.split("\\|");
								String splitCascadeFieldInfo = new String();
								for (String s : str) {
									// Text reduceKeyOp = new Text(op);
									splitCascadeFieldInfo = splitCascadeFieldInfo
											+ s + "|";
									// schema_id,serv打散信息，级联字段信息，操作key的字段，material_id的值
									/*
									 * outputvalue = new Text(String.format(
									 * "%d\t%s\t%s\t%s\t%d", lsmInfo
									 * .getSchema_id(),
									 * splitServInfo.substring(0, splitServInfo
									 * .lastIndexOf("|")),
									 * splitCascadeFieldInfo.substring(0,
									 * splitCascadeFieldInfo .lastIndexOf("|")),
									 * op_value, lsmInfo.getMaterial_id()));
									 */
									outputvalue
											.set(String
													.format("%d\t%s\t%s\t%s\t%d\t%s",
															lsmInfo.getSchemaId(),
															splitServInfo
																	.substring(
																			0,
																			splitServInfo
																					.lastIndexOf("|")),
															splitCascadeFieldInfo
																	.substring(
																			0,
																			splitCascadeFieldInfo
																					.lastIndexOf("|")),
															op_value,
															lsmInfo.getMaterialId(),
															log_kv_records.get("_gid_")));
									context.write(reduceKeyOp, outputvalue);
								}
							} else {
								/*
								 * outputvalue = new Text(String.format(
								 * "%d\t%s\t%s\t%s\t%d", lsmInfo
								 * .getSchema_id(), splitServInfo .substring(0,
								 * splitServInfo .lastIndexOf("|")),
								 * cascade_field_value, op_value, lsmInfo
								 * .getMaterial_id()));
								 */
								outputvalue.set(String.format(
										"%d\t%s\t%s\t%s\t%d\t%s", 
										lsmInfo.getSchemaId(), 
										splitServInfo.substring(0, splitServInfo.lastIndexOf("|")),
										cascade_field_value, 
										op_value, 
										lsmInfo.getMaterialId(),
										log_kv_records.get("_gid_")));
								context.write(reduceKeyOp, outputvalue);
							}

						}
					}
				}
				/*
				 * } else { // break; }
				 */

			}
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

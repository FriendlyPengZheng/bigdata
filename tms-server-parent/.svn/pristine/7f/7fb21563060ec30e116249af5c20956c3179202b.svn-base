package com.taomee.tms.custom.method2.splitlog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.taomee.tms.custom.splitlog.OldCustomLogRefNewLog;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.StidSStidRefLog;
import com.taomee.tms.utils.DateTransfer;

/**
 * 老统计自定义日志打散的第二种方式，打散步骤MR第三步。
 * 1、读取截止当天之前的所以合法状态的自定义日志的映射信息,
 * 2、将老统计的自定义的日志转换成现在统计的日志格式
 * @author looper
 * @date 2017年8月24日 下午3:50:54
 * @project tms_hadoop CustomLogTransNewLogMapper
 */
public class CustomLogTransNewLogMapper extends Mapper<Object, Text, Text, Text>{
	private Logger LOG = LoggerFactory
			.getLogger(CustomLogTransNewLogMapper.class);
	private LogMgrService logMgrService;
	private OldCustomLogRefNewLog oldCustomLogRefNewLog = new OldCustomLogRefNewLog();
	private Text outputvalue = new Text();
	private Text outputKey = new Text();
	
	@Override
	protected void setup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.setup(context);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午3:57:08
		 * @body_statement super.setup(context);
		 */
		LOG.info("Custom Split Method2 Trans...");
		ApplicationConfig application = new ApplicationConfig();
		application.setName("custom-split-merhod2-Trans");
		// 连接注册中心配置
		RegistryConfig registry = new RegistryConfig();
		registry.setProtocol("zookeeper");
		registry.setAddress("10.1.1.35:2181");

		// 注意：ReferenceConfig为重对象，内部封装了与注册中心的连接，以及与服务提供方的连接
		// 引用远程服务
		ReferenceConfig<LogMgrService> reference = new ReferenceConfig<LogMgrService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
		reference.setApplication(application);
		reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
		reference.setInterface(LogMgrService.class);

		// 和本地bean一样使用xxxService
		logMgrService = reference.get();
		// logMgrService.insertUpdateDataResultInfo();

		if (logMgrService != null) {
			// LOG.info("init ReadRedisDayData2HBaseFunction prepare method end...");
			List<StidSStidRefLog> sStidGidRefLogIds = logMgrService
					.getStidSStidRefLogBystatus(0);
			LOG.info("stidsstidGidRefLogIds size:" + sStidGidRefLogIds.size());
			oldCustomLogRefNewLog.init2MapsInfo(sStidGidRefLogIds,
					logMgrService);
			//oldCustomLogRefNewLog.printstidSStidRefLogMapsInfo();
			LOG.info("get AllStidSStidRefLogId info...");
		}

	}

	@Override
	protected void map(Object key, Text value,
			Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.map(key, value, context);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午3:57:08
		 * @body_statement super.map(key, value, context);
		 */
		String oldLog = value.toString();
		if (value.toString() != null || value.equals("")) {
			/**
			 * 这一步主要是处理映射信息，这一步都是在内存当中执行的，和之前不一样的是，这一步不需要再和后台DB做交互
			 */
			oldLog = oldCustomLogRefNewLog.oldCustomLogToNewCustomLog(oldLog);
			/**
			 * oldLog包括: 1) 0长度的字符串 ; 2) 特殊日志; 3) 自定义转化后的日志
			 */
			if (value.toString().length() > 0) {
				Map<String, String> maps = new HashMap<>();
				maps.clear();
				String[] oldLog_array = oldLog.split("\\s+");
				for (String split_log : oldLog_array) {
					String strKv = split_log;
					String[] kv = strKv.split("=", -1);
					if (kv.length < 2) {
						System.out.println("kv键值对有误");
						continue;
					}
					maps.put(kv[0], kv[1]);
				}
				/**
				 * MR =>
				 * key : gid + 小时
				 * value : 除去 gid的所有剩下字段。
				 * 这么设计原因，是在后面的reducer当中，直接输出key中的gid(key中的其一字段) + 日志剩下的所剩字段。
				 */
				if(maps.containsKey("_gid_") && maps.containsKey("_ts_"))
				{
					/**
					 * 日志当中的gid和ts字段,供后面分游戏和分文件夹分文件使用
					 */
					String gid = maps.get("_gid_");
					String ts = maps.get("_ts_");
					/**
					 * 组成的key
					 */
					maps.remove("_gid_");
					/**
					 * 时间戳住转换成对于的小时
					 */
					String ts_hour = new DateTransfer().getHourString(Long.parseLong(ts));
					
					/**
					 * 获取mapper的key与value
					 */
					String map_key = ts_hour +"\t"+ gid;
					
					StringBuilder builder = new StringBuilder();
					for (Map.Entry<String, String> entry : maps.entrySet()) {

						builder.append(entry.getKey() + "=" + entry.getValue());
						builder.append("\t");
					}
					String map_value = builder.toString();
					
					/**
					 * 发送kv键值对 发送到reducer
					 */
					outputKey.set(map_key);
					outputvalue.set(map_value);
					context.write(outputKey, outputvalue);
				}
				else
				{
					//过滤掉特殊日志
					LOG.warn("special log :" +oldLog+" filter...");
					return;
				}
			}
			else
			{
				//顾虑掉特殊日志
				LOG.error("illegal log:" +oldLog);
				return;
			}
		}
	}

	@Override
	protected void cleanup(Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// super.cleanup(context);
		/**
		 * @author looper
		 * @date 2017年8月24日 下午3:57:08
		 * @body_statement super.cleanup(context);
		 */
	}
	

}

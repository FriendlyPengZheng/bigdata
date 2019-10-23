package com.taomee.tms.storm.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.bigdata.lib.TmsProperties;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.CustomQueryParams;
import com.taomee.tms.mgr.entity.StidSStidRefLog;

/**
 * Author looper. Company TaoMee.Inc, ShangHai. Date 2017/7/5.
 * 1.从dubbo获取StidSstidGidRef的映射信息 
 * 2.根据上面的映射信息在内存当中初始化Map的映射信息
 * 3.转换每条日志
 */
public class OldCustomLogRefNewLog implements Serializable {
	private static Logger LOG = LoggerFactory.getLogger(OldCustomLogRefNewLog.class);
	private static final long serialVersionUID = 1L;
	private Map<String, String> S2LMap = new HashMap<>();//保存stid+sstid+op+gid到logid的映射消息,map存储的容量1073741824
	private Map<String,Long> SSOGExpire = new HashMap<String,Long>();//保存存在于内存中的stid+sstid+op+gid的redis过期时间，用于定期刷新这些映射的过期时间
	private String noDealWithMessage = new String();//对于logid>0的日志，已经在同一个并行的topology作业当中处理过。
	private LogMgrService logMgrService;//如果不希望该属性被序列化,添加transient关键字修饰
	private TmsProperties properties = new TmsProperties(System.getProperty("user.home")+"/storm/tms-storm.properties");

	private StringBuilder sb = new StringBuilder();
	
	/**
	 * 初始化2Map信息
	 */
	public void init2MapsInfo(List<StidSStidRefLog> stidRefLogsLists,LogMgrService logMgrService) {
		S2LMap.clear();

		for (StidSStidRefLog stidSStidRefLog : stidRefLogsLists) {
			S2LMap.put(
					stidSStidRefLog.getStid() + "|"
							+ stidSStidRefLog.getSstid() + "|"
							+ stidSStidRefLog.getGameId() + "|"
							+ stidSStidRefLog.getOp(),
					String.valueOf(stidSStidRefLog.getLogId()));
			LOG.info("sssopgid {},{} put into StidSStidRefLog map,map size:{}",stidSStidRefLog.getStid() + "|"+ stidSStidRefLog.getSstid() + "|"+ stidSStidRefLog.getGameId() + "|" +stidSStidRefLog.getOp(),String.valueOf(stidSStidRefLog.getLogId()),S2LMap.size());
		}

		this.logMgrService = logMgrService;

	}

	@SuppressWarnings("finally")
	public String old2new(String oldLog) {
		Map<String, String> kvMap = getKvMap(oldLog);
		
		if (kvMap.containsKey("_logid_")) {
//			if (Integer.valueOf(kvMap.get("_logid_")) > 0) {
//				return noDealWithMessage;
//			}
			return oldLog;
		}else if (kvMap.containsKey("_stid_") && kvMap.containsKey("_sstid_") && kvMap.containsKey("_gid_")) {
			if(kvMap.get("_gid_").equals("3") || kvMap.get("_gid_").equals("4") || kvMap.get("_gid_").equals("1") || kvMap.get("_gid_").equals("2")){//TODO 临时修改，屏蔽gid=3和gid=4的错误数据
				return noDealWithMessage;
			}
			String stid_sstid_gid_op = kvMap.get("_stid_") + "|"+ kvMap.get("_sstid_") + "|" + kvMap.get("_gid_") + "|" + kvMap.get("_op_");
			if(S2LMap.containsKey(stid_sstid_gid_op)){//已存在与内存中则检测是否需要刷新redis过期时间
				if(SSOGExpire.containsKey(stid_sstid_gid_op)){
					if(System.currentTimeMillis()-SSOGExpire.get(stid_sstid_gid_op)>properties.getProperty("inmemory.key.redis.expire.refresh.period.milliseconds",43200000)){
						SSOGExpire.put(stid_sstid_gid_op, System.currentTimeMillis());
						logMgrService.getCustomLogInfo(kvMap.get("_stid_"), kvMap.get("_sstid_"),Integer.valueOf(kvMap.get("_gid_")),kvMap.get("_op_"), true, properties.getProperty("key.redis.expire.seconds",345607));
						LOG.info("{}(logid {}) already in valid sssopgid set for over {} minutes,update redis expire for {} minutes.",stid_sstid_gid_op,S2LMap.get(stid_sstid_gid_op),properties.getProperty("inmemory.key.redis.expire.refresh.period.milliseconds",43200000)/60000,properties.getProperty("key.redis.expire.seconds",345608)/60);
					}else{
						LOG.debug("{}(logid {}) already in valid sssopgid set less than {} minutes,not to update redis expire seconds",stid_sstid_gid_op,S2LMap.get(stid_sstid_gid_op),properties.getProperty("inmemory.key.redis.expire.refresh.period.milliseconds",43200000)/60000);
					}
				}else{
					SSOGExpire.put(stid_sstid_gid_op, System.currentTimeMillis());
					logMgrService.getCustomLogInfo(kvMap.get("_stid_"), kvMap.get("_sstid_"),Integer.valueOf(kvMap.get("_gid_")),kvMap.get("_op_"), true, properties.getProperty("key.redis.expire.seconds",345609));
					LOG.info("{}(logid {})put into valid sssopgid set,updating redis expire for {} minutes.",stid_sstid_gid_op,S2LMap.get(stid_sstid_gid_op),properties.getProperty("key.redis.expire.seconds",345600)/60);
				}
				kvMap.remove("_stid_");
				kvMap.remove("_sstid_");
//				kvMap.remove("_op_");
				kvMap.put("_logid_", S2LMap.get(stid_sstid_gid_op));
				LOG.debug("log \"{}\" from memory get sss2logid map <{},{}>",oldLog,stid_sstid_gid_op,S2LMap.get(stid_sstid_gid_op));
			}else{//不存在于内存中则向dubbo发送请求获取/生成映射
				int index = 0;
				while (true) {
					LOG.info("{} not found in sss2logid map,getting from logMgrService with try {}...",stid_sstid_gid_op,index);
					
//					if(kvMap.get("_op_") == null){//为了区分新老格式统计日志，对于老日志，转换后保留OP，新统计格式应本就不包含op
//						kvMap.put("_op_","null");
//					}
					CustomQueryParams customQueryParams = new CustomQueryParams(kvMap.get("_stid_"),kvMap.get("_sstid_"),Integer.valueOf(kvMap.get("_gid_")),kvMap.get("_op_")==null?"null":kvMap.get("_op_"));
					
					Integer logid = -1;// 后台返回的gid不可能 为负数的

					try {
						logid = logMgrService.getCustomLogInfo(customQueryParams,true,properties.getProperty("key.redis.expire.seconds",345600));//先去redis查找，如果没有则去db进行查找，还没有则插入记录并写redis再返回
						if (logid > 0) {
							LOG.info("{} get logID {} by logMgrService",stid_sstid_gid_op,logid);
							kvMap.put("_logid_", String.valueOf(logid));
							kvMap.remove("_stid_");
							kvMap.remove("_sstid_");
//							kvMap.remove("_op_");
							S2LMap.put(stid_sstid_gid_op,String.valueOf(logid));
							LOG.info("{},{} put into stidSStidRefLogMaps,map size:{}",stid_sstid_gid_op,logid,S2LMap.size());
							SSOGExpire.put(stid_sstid_gid_op, System.currentTimeMillis());
							LOG.info("{}(logid {}) put  into valid sssopgid set,set redis expire for {} minutes.",stid_sstid_gid_op,logid,properties.getProperty("key.redis.expire.seconds",345600)/60);
							break;
						}else{
							LOG.warn("{} get logid {} by logMgrService",stid_sstid_gid_op,logid);
						}
					} catch (Exception e) {
						e.printStackTrace();
						
						index++;
						if(index >= properties.getProperty("customparam.insertion.max.retries",6))
						{
							LOG.warn("{} fail to get logid by logMgrService.Max retries {} exceeded.",customQueryParams.toString(),properties.getProperty("customparam.insertion.max.retries",6));//丢掉该实时的数据,后面可考虑在这边加策略
							return noDealWithMessage;
						}
						continue;
					}
					index++;
					if(index >= properties.getProperty("customparam.insertion.max.retries",6))
					{
						LOG.warn("{} fail to get logid by logMgrService.Max retries {} exceeded.",customQueryParams.toString(),properties.getProperty("customparam.insertion.max.retries",6));//丢掉该实时的数据,后面可考虑在这边加策略
						return noDealWithMessage;
					}
				}

			}

		}

		return genNewLog(kvMap);
	}

	private String genNewLog(Map<String, String> kvMap) {
		sb.setLength(0);
		int i = 1;
		for (Map.Entry<String, String> entry : kvMap.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			if (i < kvMap.size()) {
				sb.append("\t");
			}
			i++;
		}
		return sb.toString();
	}

	private Map<String, String> getKvMap(String logStr) {
		Map<String, String> rtMaps = new LinkedHashMap<>();
		for (String split_log : logStr.split("\\s+")) {
			String[] kv = split_log.split("=", -1);
			if (kv.length < 2) {
				LOG.debug("Warn:wrong kv pair {} found in log \"{}\".",split_log,logStr);
				continue;
			}
			rtMaps.put(kv[0], kv[1]);
		}
		
		return rtMaps;
	}

	/**
	 * 第二种自定义日志打散方式，去掉之前的找不到日志需要往后台发送插入，现在没找到的数据(这种情况应该不存在的,
	 * 因为之前几步MR已经拿到了截止当天为止全量的合法自定义映射数据)直接丢掉。
	 * 
	 * @param oldLog
	 * @return
	 */
	public String oldCustomLogToNewCustomLog(String oldLog) {
		Map<String, String> maps = new HashMap<>();
		maps.clear();
		for (String split_log : oldLog.split("\t")) {
			String strKv = split_log;
			String[] kv = strKv.split("=", -1);
			if (kv.length < 2) {
				System.out.println("kv键值对有误:" + strKv + ", " + oldLog);
				continue;
			}
			maps.put(kv[0], kv[1]);
		}

		if (maps.containsKey("_logid_")) {
			if (Integer.valueOf(maps.get("_logid_")) > 0) {
				return noDealWithMessage;
			}
			return oldLog;
		}

		else if (maps.containsKey("_stid_") && maps.containsKey("_sstid_")&& maps.containsKey("_gid_")) {

			//查找映射表当中是否存在stid+sstid+gid+op,如果存在需要替换成新的logid,同时把stid、sstid以及op字段去掉。
			String stid_sstid_gid_op = maps.get("_stid_") + "|"+ maps.get("_sstid_") + "|" + maps.get("_gid_") +"|"+maps.get("_op_");

			if (S2LMap.containsKey(stid_sstid_gid_op)) {
				//如果存在，去掉原先日志当中的stid、sstid、op,减少现在日志的数量,因为现在很多自定义日志都是中文的，很占空间
				maps.remove("_stid_");
				maps.remove("_sstid_");
				maps.remove("_op_");
				maps.put("_logid_", S2LMap.get(stid_sstid_gid_op));

			} else {
				LOG.error("Error:log not contain field:stid/sstid/gid :\"{}\"",oldLog);
				return noDealWithMessage;
			}

		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : maps.entrySet()) {

			builder.append(entry.getKey() + "=" + entry.getValue());
			builder.append("\t");
		}
		return builder.toString();
	}

	/**
	 * 对于新的自定义数据映射，需要往dubbo后台插入这条新的日志映射信息
	 * 
	 * @param oldLog
	 * @return
	 */
	public void insertNewLog(CustomQueryParams customQueryParams) {
		/**
		 * 之前一步的reducer已经判断了最后的日志分割后的数组长度为4
		 */
		/*
		 * String[] customLogParams = newCustomLog.split("\t"); String stid =
		 * customLogParams[0]; String sstid = customLogParams[1]; String gid =
		 * customLogParams[2]; String op = customLogParams[3];
		 */

		/**
		 * 拼接成后台的自定义查询参数
		 */
		/*
		 * CustomQueryParams customQueryParams = new CustomQueryParams();
		 * customQueryParams.setStid(stid); customQueryParams.setSstid(sstid);
		 * customQueryParams.setGameId(Integer.valueOf(gid));
		 * customQueryParams.setOp(op);
		 */

		Integer newLogid = -1;
		/**
		 * 后期如果不希望无限插入,可以配置插入index最大尝试次数,避免DB一致被在被插入数据
		 */
		int index = 1;

		while (true) {
			try {

				newLogid = logMgrService.insertCustomLogInfo(customQueryParams);

				LOG.info(customQueryParams.getStid() + ","
						+ customQueryParams.getSstid() + ","
						+ customQueryParams.getGameId() + ":" + "第" + index
						+ "次尝试DB插入...");
				if (newLogid > 0) {
					break;
				} else {
					// 不会出现else这一分支计算的
					index++;
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				index++;
				continue;
			}
		}
	}

	/**
	 * 判断stid、sstid、gid 是否存在
	 * @param customQueryParams
	 * @return
	 */
	public boolean isExistStidSstidGid(CustomQueryParams customQueryParams) {
		String stid_sstid_gid_op = customQueryParams.getStid() + "|"
				+ customQueryParams.getSstid() + "|"
				+ customQueryParams.getGameId() +"|"
				+ customQueryParams.getOp();
		boolean flag = S2LMap.containsKey(stid_sstid_gid_op);
		if (flag) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 打印内存的映射关系
	 */
	public void printstidSStidRefLogMapsInfo() {
		for (Map.Entry<String, String> entry : S2LMap.entrySet()) {
			/*
			 * System.out.println("key:" + entry.getKey() + ",value:" +
			 * entry.getValue());
			 */
			LOG.info("key:" + entry.getKey() + ",value:" + entry.getValue());
		}
	}

}

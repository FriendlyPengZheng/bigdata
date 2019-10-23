package com.taomee.tms.custom.splitlog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.CustomQueryParams;
import com.taomee.tms.mgr.entity.StidSStidRefLog;

/**
 * Author looper. Company TaoMee.Inc, ShangHai. Date 2017/7/5.
 * 1.模拟从dubbo服务那边获取StidSstidGidRef的映射信息. 2.根据上面的映射信息在内存当中初始化Map的映射信息.
 * 3.转换每条日志的信息.
 */
public class OldCustomLogRefNewLog implements Serializable {

	/**
	 * 
	 */
	private static Logger LOG = LoggerFactory
			.getLogger(OldCustomLogRefNewLog.class);

	private static final long serialVersionUID = 1L;

	/**
	 * 保存stidsstid到logid的映射消息,map存储的容量1073741824
	 */
	private Map<String, String> stidSStidRefLogMaps = new HashMap<>();

	/**
	 * 对于logid>0的日志，已经在同一个并行的topology作业当中处理过。
	 */
	private String noDealWithMessage = new String();

	/**
	 * 如果不希望该属性被序列化,添加transient关键字修饰
	 */
	private LogMgrService logMgrService;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * 构造映射信息,这部分最后信息需要调用dubbo那边服务，获取整个自定义日志到logid的映射信息.
		 */
		/*List<StidSStidRefLog> stidRefLogsLists = new ArrayList<>();
		//StidSStidRefLog stidSStidRefLog = new StidSStidRefLog("主线剧情", "完成步骤1",
				632, 10);
		StidSStidRefLog stidSStidRefLog2 = new StidSStidRefLog("主线剧情", "完成步骤2",
				632, 11);
		stidRefLogsLists.add(stidSStidRefLog);
		stidRefLogsLists.add(stidSStidRefLog2);*/

	}

	/**
	 * 初始化2Map信息
	 * 
	 * @param stidRefLogsLists
	 * @param gpzsGidRefServerLists
	 */
	public void init2MapsInfo(List<StidSStidRefLog> stidRefLogsLists,LogMgrService logMgrService) {
		this.logMgrService = logMgrService;
		stidSStidRefLogMaps.clear();
		
		for (StidSStidRefLog stidSStidRefLog : stidRefLogsLists) {
			stidSStidRefLogMaps.put(
					stidSStidRefLog.getStid() + "|"
							+ stidSStidRefLog.getSstid() + "|"
							+ stidSStidRefLog.getGameId() + "|" +stidSStidRefLog.getOp(),
					String.valueOf(stidSStidRefLog.getLogId()));
		}
	}

	/**
	 * 第一种自定义打散方式解析日志方式
	 * 废弃 ( 20170829日废弃该方法 )
	 * @param oldLog
	 * @return
	 */
	@SuppressWarnings("finally")
	public String old2new(String oldLog) {
		Map<String, String> maps = new HashMap<>();
		maps.clear();
		String[] oldLog_array = oldLog.split("\t");
		for (String split_log : oldLog_array) {
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
				// 避免这边创建大量的String对象,造成jvm堆需要频繁创建对象以及垃圾回收这些对象，造成资源浪费
				// return new String();
				return noDealWithMessage;
			}
			return oldLog;
		}

		else if (maps.containsKey("_stid_") && maps.containsKey("_sstid_")
				&& maps.containsKey("_gid_")) {

			String stid_sstid_gid = maps.get("_stid_") + "|"
					+ maps.get("_sstid_") + "|" + maps.get("_gid_");

			// LOG.info("log service object1:" +logMgrService);
			/**
			 * 1.映射存在Map当中，直接按照映射关系替换就可以了。if
			 * 2.如果映射不存在Map当中，说明这条是个新的自定义日志，需要通过调用dubbo的服务获取到对应这条日志的logid信息 else
			 */
			if (stidSStidRefLogMaps.containsKey(stid_sstid_gid)) {
				maps.remove("_stid_");
				maps.remove("_sstid_");
				maps.put("_logid_", stidSStidRefLogMaps.get(stid_sstid_gid));
				
			} else {
				int index = 0;
				while (true) {

					String op = maps.get("_op_");
					
					CustomQueryParams customQueryParams = new CustomQueryParams();
					customQueryParams.setStid(maps.get("_stid_"));
					customQueryParams.setSstid(maps.get("_sstid_"));
					customQueryParams.setGameId(Integer.valueOf(maps
							.get("_gid_")));
					customQueryParams.setOp(maps.get("_op_"));

					Integer logid = -1;// 后台返回的gid不可能 为负数的

					// LOG.info("log service object:" + logMgrService);
					LOG.info("msg:" + maps.get("_stid_") + ","
							+ maps.get("_sstid_") + "," + maps.get("_gid_")
							+ "," + maps.get("_op_") + ",index = " + index);
					try {

						logid = logMgrService
								.insertCustomLogInfo(customQueryParams);// 这步有可能后台会抛异常，需要在上一步给logid初始化。否则可能这步会出现nullPoint
						if (logid > 0) {
							maps.put("_logid_", String.valueOf(logid));
							maps.remove("_stid_");
							maps.remove("_sstid_");
							maps.remove("_op_");
							stidSStidRefLogMaps.put(stid_sstid_gid,
									String.valueOf(logid));
							break;
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LOG.info("insert CustomQueryParams info error:"
								+ customQueryParams.getStid() + ","
								+ customQueryParams.getSstid() + ","
								+ customQueryParams.getGameId() + ","
								+ customQueryParams.getOp());
						/*
						 * try { //控制线程睡眠10毫秒 //Thread.sleep(10l); index++; }
						 * catch (InterruptedException e2) { // TODO
						 * Auto-generated catch block e2.printStackTrace(); }
						 */
						index++;
						continue;
					} finally {
						/**
						 * 插入失败了,还得执行下面语句
						 */
						/*
						 * while(true) {
						 * 
						 * }
						 */
						// return noDealWithMessage;

					}

				}

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
	 * 第二种自定义日志打散方式，去掉之前的找不到日志需要往后台发送插入，现在没找到的数据(这种情况应该不存在的,
	 * 因为之前几步MR已经拿到了截止当天为止全量的合法自定义映射数据)直接丢掉。
	 * 
	 * 对旧格式日志，转换为新格式日志（去掉stid/sstid/op，加上logid）
	 * 对于新格式日志（已含有logid），直接返回该日志
	 * @param oldLog
	 * @return
	 */
	public String oldCustomLogToNewCustomLog(String oldLog) {
//		Map<String, String> maps = new HashMap<>();
		Map<String, String> maps = new LinkedHashMap<>();
		maps.clear();
		String[] oldLog_array = oldLog.split("\t");
		for (String split_log : oldLog_array) {
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

				return oldLog;
			}
			return oldLog;
		}

		else if (maps.containsKey("_stid_") && maps.containsKey("_sstid_")
				&& maps.containsKey("_gid_")) {

			/**
			 * 查找映射表当中是否存在stid+sstid+gid+op,如果存在需要替换成新的logid,同时把stid、sstid以及op字段去掉。
			 */
			String stid_sstid_gid_op = maps.get("_stid_") + "|"
					+ maps.get("_sstid_") + "|" + maps.get("_gid_") +"|"+maps.get("_op_");

			if (stidSStidRefLogMaps.containsKey(stid_sstid_gid_op)) {
				/**
				 * 如果存在，去掉原先日志当中的stid、sstid、op,减少现在日志的数量,因为现在很多自定义日志都是中文的，很占空间
				 */
				maps.remove("_stid_");
				maps.remove("_sstid_");
				maps.remove("_op_");
				maps.put("_logid_", stidSStidRefLogMaps.get(stid_sstid_gid_op));

			} else {
				LOG.error("error custom log =>" + oldLog);
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
	public Integer insertNewLog(CustomQueryParams customQueryParams) {
		Integer newLogid = -1;
		int index = 1;
		while (index <= 6) {
			try {
				newLogid = logMgrService.getCustomLogInfo(customQueryParams,true, 86400 * 4);
				if (newLogid == null || newLogid <= 0) {
					index++;
					continue;
				} else {
					logMgrService.getSchemaInfosByLogIdForStorm(newLogid,true,86400 * 4);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				index++;
				continue;
			}
		}
		return newLogid;
	}

	/**
	 * 判断stid、sstid、gid 是否存在
	 * 
	 * @param customQueryParams
	 * @return
	 */
	public boolean isExistStidSstidGid(CustomQueryParams customQueryParams) {
		return stidSStidRefLogMaps.containsKey(
				customQueryParams.getStid() + "|"
				+ customQueryParams.getSstid() + "|"
				+ customQueryParams.getGameId() +"|"
				+ customQueryParams.getOp());
	}

	/**
	 * 打印内存的映射关系
	 */
	public void printstidSStidRefLogMapsInfo() {
		for (Map.Entry<String, String> entry : stidSStidRefLogMaps.entrySet()) {
			/*
			 * System.out.println("key:" + entry.getKey() + ",value:" +
			 * entry.getValue());
			 */
			LOG.info("key:" + entry.getKey() + ",value:" + entry.getValue());
		}
	}

}

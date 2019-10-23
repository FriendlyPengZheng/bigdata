package com.taomee.tms.transData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.在内存中加载本地的映射信息，并初始化Map的映射信息.
 * 2.每条日志进行转换.
 */

public class OldBasicLogRefNewLog implements Serializable {
	private static final long serialVersionUID = -4670799715600770600L;
	/**
	 * 保存serverId到gameId的映射信息
	 */
	private Map<String, String> serverIdToGameIdMaps = new HashMap<>();
	
	/**
	 * 保存stid+sstid到logId的映射信息
	 */
	private Map<String, String> sstidToGameIdMaps = new HashMap<>();
	
	/**
	 * 初始化serverIdToGameIdMap信息
	 * 
	 * @param serverToGameIdLists
	 */
	public void initServerInfoMaps(List<String> serverToGameIdLists) {
		serverIdToGameIdMaps.clear();
		//文本格式：gameId,platform_id,zone_id,server_id,newserverId
		for (String serverIdToGameId : serverToGameIdLists) {
			String[] str = serverIdToGameId.split(",");
			serverIdToGameIdMaps.put(str[0] + "|" + str[1] + "|" + str[2] + "|" + str[3], str[4]);
		}
		
		//System.out.println("msg size end:" + serverIdToGameIdMaps.size());
		//printMapInfos(serverIdToGameIdMaps);
	}
	
	
	/**
	 * 打印内存的映射关系
	 */
	public void printMapInfos(Map<String, String> maps) {
		System.out.println("打印MapInfos=======================");
		for (Map.Entry<String, String> entry : maps.entrySet()) {
			System.out.println("key:" + entry.getKey() + ",value:" + entry.getValue());
		}
	}
	
	/**
	 * 初始化sstidToGameIdMaps信息
	 * 
	 * @param sstidTologInfos
	 */
	public void initLogInfoMaps(List<String> sstidTologInfos) {
		sstidToGameIdMaps.clear();
		//文本格式：stid,sstid,logId
		for (String sstidToLogId : sstidTologInfos) {
			String[] str = sstidToLogId.split(",");
			sstidToGameIdMaps.put(str[0] + "|" + str[1], str[2]);
		}
		
		//System.out.println("msg size end:" + sstidToGameIdMaps.size());
		//printMapInfos(sstidToGameIdMaps);
	}
	
	/*旧日志格式：_hip_=192.168.122.120   _stid_=_loginacct_      _sstid_=_loginacct_     _gid_=25        _zid_=-1        _sid_=-1     
			 *    _pid_=-1 _ts_=1507651210 _acid_=143005045 _actype_=mimi   _acctid_=143005045      _cip_=2021659662        _op_=item:_actype_
			 */
	/*
	 * 转换后日志格式：_hip_=192.168.122.120 _logid_=32 _svrid_=10 _gid_=25 _zid_=-1
	 * _sid_=-1 _pid_=-1 _ts_=1507564858 _acid_=130971631 _actype_=mimi
	 * _acctid_=130971631 _cip_=3013027550 _op_=item:_actype_
	 */
			 
	/**
	 * 旧日志格式转化成新的格式
	 * @param oldLog
	 * @return
	 */
	public LinkedHashMap<String, String> oldBasicLogToNewLog(String oldLog) {
		LinkedHashMap<String, String> maps = new LinkedHashMap<>();
		maps.clear();
		String[] oldLog_array = oldLog.split("\t");
		for (String split_log : oldLog_array) {
			String strKv = split_log;
			String[] kv = strKv.split("=", -1);
	
			maps.put(kv[0], kv[1]);
		}
		
		LinkedHashMap<String, String> mapTmps = new LinkedHashMap<>();
		mapTmps.clear();
		mapTmps.put("_hip_", maps.get("_hip_"));
		
		if (maps.containsKey("_stid_") && maps.containsKey("_sstid_")
				&& maps.containsKey("_gid_")) {
			/**
			 * 查找映射表当中是否存在stid+sstid,如果存在需要替换成新的logid
			 */
			String stid_sstid= maps.get("_stid_") + "|" + maps.get("_sstid_");
			//System.out.println("stid_sstid: " + stid_sstid);
			String logId = sstidToGameIdMaps.get(stid_sstid);;
			//printMapInfos(sstidToGameIdMaps);
			/*if(sstidToGameIdMaps.containsKey(stid_sstid)) {
				System.out.println("stid_sstid key: " + stid_sstid);
				logId = sstidToGameIdMaps.get(stid_sstid);
			}*/
			
			if(null == logId || logId.equals("")) {
				return null;
			}
			maps.remove("_stid_");
			maps.remove("_sstid_");
			maps.put("_logid_", logId);
			mapTmps.put("_logid_", logId);
			
		} else {
			//跳过旧日志中在本地没有映射关系的日志
			System.err.println("stid+sstid error basic log =>" + oldLog);
		}
		
		//  _gid_=25        _zid_=-1        _sid_=-1        _pid_=-1 
		/*if (maps.containsKey("_gid_") && maps.containsKey("_pid_")
				&& maps.containsKey("_zid_") && maps.containsKey("_sid_")) {
		
			  查找映射表当中是否存在gpzsId,如果存在需要替换成新的serverId
			 
			//String gpzsId= maps.get("_gid_") + "|" + maps.get("_pid_") + "|" + maps.get("_zid_") + "|" + maps.get("_sid_");
			//String serverId = serverIdToGameIdMaps.get(gpzsId);
			
			//if(null == serverId || serverId.equals("")) {
			//	return null;
			//}
			//maps.put("_svrid_", serverId);
			//mapTmps.put("_svrid_", serverId);
		} else {
			System.err.println("gpzs error basic log =>" + oldLog);
		}*/

		mapTmps.putAll(maps);
		return mapTmps;
	}
	
}
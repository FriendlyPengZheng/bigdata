package com.taomee.tms.mgr.core.gpzsidanalyser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.core.LogMgrServiceFactory;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;

public class GPZSIdAnalyzer implements Serializable {
	private static final long serialVersionUID = -1930134650620011345L;
	private static final Logger LOG = LoggerFactory.getLogger(GPZSIdAnalyzer.class);
	private static LogMgrService logMgrService;
	
	private static HashMap<String,Integer> gpzs2ServerIDMap = new HashMap<String,Integer>();
	private static HashMap<String,List<Integer>> gpzs2AncestorsServerID = new HashMap<String,List<Integer>>();
	
	static{
		logMgrService = LogMgrServiceFactory.getInstance();
		init();
	}
	
	/**
	 * 载入redis中gpzs组合到serverID的映射
	 */
	private static boolean init() {
		List<ServerGPZSInfo> gpzs2ServerInfos = logMgrService.getServerGpzsInfosFromRedis("gpzs2serverid_*");
		LOG.info("get {} gpzs2ServerInfos from redis by logMgrService",gpzs2ServerInfos.size());
		loadGpzs2ServerInfosIntoMemory(gpzs2ServerInfos);
		LOG.info("{} gpzs2ServerInfos loaded into gpzs2ServerIDMap",gpzs2ServerIDMap.size());
		return true;
	}
	
	private static void loadGpzs2ServerInfosIntoMemory(List<ServerGPZSInfo> gpzs2ServerInfos){
		for(ServerGPZSInfo sinfo:gpzs2ServerInfos){
			Integer gid = sinfo.getGameId();
			Integer pid = sinfo.getPlatformId();
			Integer zid = sinfo.getZoneId();
			Integer sid = sinfo.getsId();
			Integer serverID = sinfo.getServerId();
			if(gid != null && pid != null && zid != null && sid != null && serverID != null){
				String key = String.format("%s,%s,%s,%s", gid,pid,zid,sid);
				if (!gpzs2ServerIDMap.containsKey(key)){
					gpzs2ServerIDMap.put(key, serverID);
					LOG.debug("put key,value:<{},{}> into gpzs2ServerIDMap",key,serverID);
				}
			}
		}
		
	}
	
	/*
	 * 根据gpzs，返回包括其本身在内的所有祖先serverID
	 */
	public List<Integer> getAncestorsServerIDList(int gid,int pid,int zid,int sid){
		List<Integer> ancestorsServerID = gpzs2AncestorsServerID.get(String.format("%s,%s,%s,%s", gid,pid,zid,sid));
		if(ancestorsServerID != null){
			return ancestorsServerID;
		}else{
			return getAncestorsServerID(gid,pid,zid,sid);
		}
	}
	
	public List<Integer> getAncestorsServerIDList(String strGid,String strPid,String strZid,String strSid){
		try{
			return this.getAncestorsServerIDList(Integer.valueOf(strGid),Integer.valueOf(strPid),Integer.valueOf(strZid),Integer.valueOf(strSid));
		}catch(NumberFormatException e){
			e.printStackTrace();
			LOG.error("gpzs {}|{}|{}|{} contains non-number value!",strGid,strPid,strZid,strSid);
			return null;
		}
	}
	
	/*
	 * 根据gpzs，返回包括其本身在内的所有祖先serverID，并存入本地Map中以供快速获取
	 */
	private List<Integer> getAncestorsServerID(int gid,int pid,int zid,int sid){
		int[][] ancestorsGPZS = getAllSplittedGPZS(gid,pid,zid,sid);
		if(ancestorsGPZS != null){
			List<Integer> ancestorsServerID = new LinkedList<Integer>();
			try{
				for(int[] gpzs:ancestorsGPZS){
					Integer serverID = this.getServerID(gpzs[0],gpzs[1],gpzs[2],gpzs[3]);
					if(serverID != null){
						if(!ancestorsServerID.contains(serverID)){
							ancestorsServerID.add(serverID);
						}
					}else{
						return null;
					}
				}
				gpzs2AncestorsServerID.put(String.format("%s,%s,%s,%s", gid,pid,zid,sid),ancestorsServerID);
				return ancestorsServerID;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}else{
			LOG.error("gpzs {}|{}|{}|{} cannot get splitted gpzs!",gid,pid,zid,sid);
			return null;
		}
	}
	
	/*
	 * 返回打散后的gpzs组合，包含原句子
	 */
	public static int[][] getAllSplittedGPZS(int gid,int pid,int zid,int sid){
		if(pid == -1){
			if(zid == -1 || sid == -1){
				if(zid == -1 && sid == -1){
					return new int[][]{{gid,pid,zid,sid}};
				}else if(zid == -1){
					return new int[][]{{gid,pid,zid,sid},{gid,pid,zid,-1}};
				}else{
					return new int[][]{{gid,pid,zid,sid},{gid,pid,-1,sid}};
				}
			}else{
				return new int[][]{{gid,pid,zid,sid},{gid,pid,zid,-1},{gid,pid,-1,-1}};
			}
		}else{
			if(zid == -1 || sid == -1){
				if(zid == -1 && sid == -1){
					return new int[][]{{gid,pid,zid,sid},{gid,-1,zid,sid}};
				}else if (zid == -1){
					return new int[][]{{gid,pid,zid,sid},{gid,-1,zid,sid},{gid,pid,zid,-1},{gid,-1,-1,-1}};
				}else{
					return new int[][]{{gid,pid,zid,sid},{gid,-1,zid,sid},{gid,pid,-1,sid},{gid,-1,-1,-1}};
				}
			}else{
				return new int[][]{{gid,pid,zid,sid},{gid,-1,zid,sid},{gid,pid,zid,-1},{gid,pid,-1,-1},{gid,-1,-1,-1}};
			}
		}
	}
	
	/*
	 * 根据gpzs返回ServerID，按照本地-联机-插入的顺序执行
	 */
	private Integer getServerID(int gid,int pid,int zid,int sid){
		Integer serverID = gpzs2ServerIDMap.get(String.format("%s,%s,%s,%s",gid,pid,zid,sid));
		if(serverID == null){
			LOG.warn("gpzs {}|{}|{}|{} get null serverID from memory.",gid,pid,zid,sid);
		}
		if(serverID != null){
			LOG.debug("gpzs {}|{}|{}|{} get serverID {} from memory.",gid,pid,zid,sid,serverID);
			return serverID;
		}else{
			serverID = logMgrService.getServerIDByGPZSForStorm(gid,pid,zid,sid,86400*3+60*60*3);
			if(serverID != null && serverID > 0){
				LOG.info("gpzs {}|{}|{}|{} get serverID {} from logMgrService.",gid,pid,zid,sid,serverID);
				if(!gpzs2ServerIDMap.containsKey(serverID)){
					gpzs2ServerIDMap.put(String.format("%s,%s,%s,%s", gid,pid,zid,sid),serverID);
				}
			}else{
				LOG.error("gpzs {}|{}|{}|{} get null serverID from logMgrService.",gid,pid,zid,sid,serverID);
			}
		}
		return serverID;
	}
	
	/*
	 * 根据pid返回平台名
	 */
	public static String getPName(int pid){
		if(pid == -1){
			return "全平台";
		}else{
			return pid+"平台";
		}
	}
	
	/*
	 * 根据zid和sid返回区服名
	 */
	public static String getZSName(int zid,int sid){
		if(zid == -1 && sid == -1){
			return "全区全服";
		}else if(zid == -1){
			return sid+"服";
		}else if(sid == -1){
			return zid+"区";
		}else{
			return zid+"区"+sid+"服";
		}
	}
	
	public static void main(String args[]){
//		GPZSIdAnalyzer analyzer = new GPZSIdAnalyzer();
//		showAncestorsServerID(analyzer.getAncestorsServerIDList(632, 5, 1, -1));
//		showAncestorsServerID(analyzer.getAncestorsServerIDList(664, -1, 3, -1));
//		showAncestorsServerID(analyzer.getAncestorsServerIDList(664, -1, 2, -1));
//		showAncestorsServerID(analyzer.getAncestorsServerIDList(664, -1, 7, -1));
//		showAncestorsServerID(analyzer.getAncestorsServerIDList(2, 1, 2, 3));
	}
	
	private static void showAncestorsServerID(List<Integer> ancestors){
		for(Integer ancestor:ancestors){
			System.out.print(ancestor+"\t");
		}
		System.out.println();
	}
	
}


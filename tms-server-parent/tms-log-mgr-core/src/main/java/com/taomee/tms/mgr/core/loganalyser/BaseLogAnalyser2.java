package com.taomee.tms.mgr.core.loganalyser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.core.gpzsidanalyser.GPZSIdAnalyzer;
import com.taomee.tms.mgr.core.serveridanalyser.ServerIdAnalyzer;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public abstract class BaseLogAnalyser2 implements Serializable {
	private static final long serialVersionUID = 5399707255057828105L;
	private static final Logger LOG = LoggerFactory.getLogger(BaseLogAnalyser2.class);
	
	protected Map<String, String> attrMap = new HashMap<String, String>();
	protected ServerIdAnalyzer serverIdAnalyzer = new ServerIdAnalyzer();
	protected GPZSIdAnalyzer gpzsIdAnalyzer = new GPZSIdAnalyzer();
	protected List<Integer> serverAncestors;
	protected boolean dealingSpecailLog;
	protected String strLog;
	protected String strLogId;
	protected String strgid;
	protected String strpid;
	protected String strzid;
	protected String strsid;
	
//	/**
//	 * 
//	 * @return 提供对外的服信息分析器,使得不同包外的类可以访问
//	 */
//	public ServerIdAnalyzer getServerIdAnalyzer() {
//		return serverIdAnalyzer;
//	}
//
//	public void setServerIdAnalyzer(ServerIdAnalyzer serverIdAnalyzer) {
//		this.serverIdAnalyzer = serverIdAnalyzer;
//	}

	abstract protected boolean SetSchemaInfos(List<SchemaInfo> schemaInfos);
	
	public boolean Init(List<SchemaInfo> schemaInfos, List<ServerInfo> serverInfos) {
		// 基类只处理serverId
//		if (!serverIdAnalyzer.Init(serverInfos)) {
//			LOG.error("BaseLogAnalyser Init, ServerIdAnalyser SetServerInfos failed");
//			return false;
//		}
		
		if (!SetSchemaInfos(schemaInfos)) {
			LOG.error("BaseLogAnalyser Init, setSchemaInfo failed");
			return false;
		}

//		if(!gpzsIdAnalyzer.init(serverInfos)){
//			LOG.error("BaseLogAnalyser Init, ServerIdAnalyser SetServerInfos failed");
//			return false;
//		}
		
		return true;
	}
	
	protected boolean IsSpecailLog(String strLogId) {
//		if (strLogId.equals("-1") ||//添加schema
//			strLogId.equals("-2") ||//修改schema（暂时无效）
//			strLogId.equals("-3") ||//删除schema
//			strLogId.equals("-4") ||//添加serverId
//			strLogId.equals("-5") ||//修改serverId（暂时无效）
//			strLogId.equals("-6")) {//删除serverId
//			return true;
//		}
		if (strLogId.startsWith("-")) {
			return true;
		}
		return false;
	}
	
	public boolean IsDealingSpecailLog() {
		return dealingSpecailLog;
	}

	/**
	 *  预处理日志，解析得到日志各字段值
	 */
	public boolean SetLog(String strLog) {
		this.strLog = strLog;
		this.dealingSpecailLog = false;
		
		setAttributeMap(this.strLog);
		
		if(!setStrLogId(this.attrMap))return false;
		if (IsSpecailLog(this.strLogId)) {
			LOG.info("processing special log \"{}\"" , this.strLog);
			dealingSpecailLog = true;
			return true;
		}
		
		if(!setGPZS(attrMap))return false;
		
		if(!setServerAncestors(this.strgid,this.strpid,this.strzid,this.strsid))return false;
		
		return true;
	}

	private boolean setServerAncestors(String strgid, String strpid,String strzid, String strsid) {
//		this.serverAncestors = gpzsIdAnalyzer.getAncestorsServerIDList(strgid,strpid,strzid,strsid);
		if (this.serverAncestors == null || this.serverAncestors.size() == 0) {
			LOG.error("gpzs {}|{}|{}|{} get no serverID ansestors" , strgid,strzid,strsid,strpid,this.strLog);
			return false;
		}else{
			LOG.debug("get serverID ancestors:{} for gpzs:{},{},{},{}",serverAncestors,strgid,strpid,strzid,strsid);
		}
		return true;
	}

	private void setAttributeMap(String strLog) {
		this.attrMap.clear();
		String[] slices = strLog.split("\\s+");
		for (String slice: slices) {
			String strKv = slice;
			String[] kv = strKv.split("=", -1);
			if (kv.length < 2) {
				LOG.debug("Warning:invalid line key value {} for log:{}",strKv,strLog);
				continue;
			}
			this.attrMap.put(kv[0], kv[1]);
		}
	}

	private boolean setStrLogId(Map<String, String> attrMap) {
		this.strLogId = attrMap.get("_logid_");
		if (this.strLogId == null || this.strLogId.trim().isEmpty()) {
			LOG.debug("Error:logid \"{}\" not found in log:\"{}\"",this.strLog);
			return false;
		}
		return true;
	}

	private boolean setGPZS(Map<String, String> attrMap) {
		this.strgid = attrMap.get("_gid_");
		this.strpid = attrMap.get("_pid_");
		this.strzid = attrMap.get("_zid_");
		this.strsid = attrMap.get("_sid_");
		if(this.strgid == null || this.strpid == null || this.strzid == null || this.strsid == null){
			LOG.debug("Error:gid/pid/zid/sid not found in log:\"{}\"" ,this.strLog);
			return false;
		}
		return true;
	}

}

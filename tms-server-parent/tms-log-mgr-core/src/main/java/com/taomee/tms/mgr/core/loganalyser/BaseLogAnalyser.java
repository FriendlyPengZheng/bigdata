package com.taomee.tms.mgr.core.loganalyser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.core.gpzsidanalyser.GPZSIdAnalyzer;
import com.taomee.tms.mgr.core.serveridanalyser.ServerIdAnalyzer;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public abstract class BaseLogAnalyser implements Serializable {
	private static final long serialVersionUID = 5399707255057828105L;
	private static final Logger LOG = LoggerFactory.getLogger(BaseLogAnalyser.class);

	//////////////////////全局静态参数///////////////////////////////////
	// serverId分析器
	protected ServerIdAnalyzer serverIdAnalyzer = new ServerIdAnalyzer();
	protected GPZSIdAnalyzer gpzsIdAnalyzer = new GPZSIdAnalyzer();
	
	/**
	 * 
	 * @return 提供对外的服信息分析器,使得不同包外的类可以访问
	 */
//	public ServerIdAnalyzer getServerIdAnalyzer() {
//		return serverIdAnalyzer;
//	}

	/*public void setServerIdAnalyzer(ServerIdAnalyzer serverIdAnalyzer) {
		this.serverIdAnalyzer = serverIdAnalyzer;
	}*/

	//////////////////////每次通过日志打散时使用的参数/////////////////////
	protected Map<String, String> attrMap = new HashMap<String, String>();
	protected String strLog = null;
	protected String strServerId = null;
	protected String strgid;
	protected String strpid;
	protected String strzid;
	protected String strsid;
	protected String strLogId = null;	
	protected List<Integer> serverAncestors = null;
	protected final SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected boolean dealingSpecailLog = false;
	
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
		// -1	添加schema
		// -2	修改schema（暂时无效）
		// -3	删除schema
		// -4	添加serverId
		// -5	修改serverId（暂时无效）
		// -6	删除serverId
//		if (strLogId.equals("-1") ||
//			strLogId.equals("-2") ||
//			strLogId.equals("-3") ||
//			strLogId.equals("-4") ||
//			strLogId.equals("-5") ||
//			strLogId.equals("-6")) {
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

	// 预处理
	public boolean SetLog(String strLog) {
		// 保存日志
		this.strLog = strLog;
		dealingSpecailLog = false;
		
		// 获取日志的参数列表
		attrMap.clear();		
		String[] slices = strLog.split("\\s+");
		for (String slice: slices) {
			String strKv = slice;
			String[] kv = strKv.split("=", -1);
			if (kv.length < 2) {
				LOG.error("BaseLogAnalyser setLog, invalid line key value " + strKv);
				continue;
			}
			attrMap.put(kv[0], kv[1]);
		}
		
		// 获取LogId
		strLogId = attrMap.get("_logid_");
		if (strLogId == null || strLogId.trim().isEmpty()) {
			// 日志中的serverId(即_logid_)不合法
			LOG.error("BaseLogAnalyser SetLog, empty logid, log is " + strLog);
			return false;
		}
		
		if (IsSpecailLog(strLogId)) {
			LOG.info("BaseLogAnalyser SetLog, met special log " + strLog);
			dealingSpecailLog = true;
			return true;
		}
		
//		// 获取serverId
//		strServerId = attrMap.get("_svrid_");
//		if (strServerId == null || strServerId.trim().isEmpty()) {
//			// 日志中的serverId(即_svrid_)不合法
//			LOG.error("LogAnalyser SetLog, no _svrid_ found, log is " + strLog);
//			return false;
//		}
		
		//获取gid,pid,zid,sid
		strgid = attrMap.get("_gid_");
		strpid = attrMap.get("_pid_");
		strzid = attrMap.get("_zid_");
		strsid = attrMap.get("_sid_");
		if(strgid == null || strpid == null || strzid == null || strsid == null){
			LOG.error("LogAnalyser SetLog, the whole gpzs ids are not found, log is " + strLog);
			return false;
		}
		
		// 根据gpzsID获取所有的祖先（也包括自己）
		serverAncestors = gpzsIdAnalyzer.getAncestorsServerIDList(strgid,strpid,strzid,strsid);
		if (serverAncestors == null || serverAncestors.size() == 0) {
			LOG.error("BaseLogAnalyser SetLog, get no ansestors from serverId " + strServerId + ", log is " + strLog);
			return false;
		}
		
		return true;
	}

	public static void main(String[] args) {
		final SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// int 最大值2147483648
			System.out.println("start test");
			long iTimestamp = 0;
			String str = null;
			str = "";
			str = "0";
			str = "1484210861000";
			iTimestamp = Long.parseLong(str);
			System.out.println("iTimestamp is " + iTimestamp);
			System.out.println(dateFormat.format(iTimestamp));
			System.out.println("end test");
		} catch (NullPointerException ex) {
			System.out.println("LogAnalyser SetLog, NullPointerException catched, field _ts_ not in log");
		} catch (NumberFormatException ex) {
			System.out.println("LogAnalyser SetLog, NullPointerException catched, field _ts_ invalid in log");
		} catch (Exception ex) {
			System.out.println("LogAnalyser SetLog, NullPointerException catched, unknown exception");
		}
		
		String tmp = "_setcascadefields_=";
		tmp = "1==3";
		String[] kv = tmp.split("=", -1);
//		String[] kv = StringUtils.split(tmp, "=");
		System.out.println("size of kv is " + kv.length);
		
		String tmp2 = "-2";
		tmp2 = "3";
		if (tmp2.startsWith("-")) {
			System.out.println("特殊日志");
		} else {
			System.out.println("非特殊日志");
		}
	}
}

































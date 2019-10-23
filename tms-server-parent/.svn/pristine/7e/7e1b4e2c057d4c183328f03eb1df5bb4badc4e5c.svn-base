package com.taomee.tms.mgr.core.loganalyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.core.opanalyser.BaseOpAnalyser;
import com.taomee.tms.mgr.core.opanalyser.OpAnalyserFactory;
import com.taomee.tms.mgr.core.schemaanalyser.BaseSchemaAnalyser;
import com.taomee.tms.mgr.core.schemaanalyser.MaterialSchemaAnalyser;
import com.taomee.tms.mgr.core.schemaanalyser.PlainSchemaAnalyser;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

// 实时计算使用
public class RealtimeLogAnalyser extends BaseLogAnalyser {
	private static final long serialVersionUID = 6368458023937197983L;
	private static final Logger LOG = LoggerFactory.getLogger(RealtimeLogAnalyser.class);
	
	protected String dateTime = null;
	protected Map<String, List<PlainSchemaAnalyser>> logId2PlainSchemaInfos = new HashMap<String, List<PlainSchemaAnalyser>>();
	
	public Map getlogId2PlainSchemaInfos()
	{
		return this.logId2PlainSchemaInfos;
	}
	
	// overwrite 表示遇到schemaId一样的，是覆盖还是报错
	// 初始化时直接报错
	// 动态配置时（在storm中)覆盖
	
	// 新增 TODO 待测试
	private boolean AddServerId(ServerInfo serverInfo) {
		return serverIdAnalyzer.AddServerInfo(serverInfo);
	}
	
	private boolean DelServerId(ServerInfo serverInfo) {
		return serverIdAnalyzer.DelServerInfo(serverInfo);
	}
	
	/**
	 * 将schemaInfo信息加载到对应logID映射的List中
	 * @param schemaInfo
	 * @param isIniting
	 * @return 
	 */
	// 新增 TODO 待测试
	private boolean AddSchemaInfo(SchemaInfo schemaInfo, boolean isIniting) {
		if (schemaInfo.getLogId() == null || schemaInfo.getLogId().intValue() <= 0) {
			LOG.error("RealtimeLogAnalyser SetSchemaInfo, null logId or logId <= 0");
			return false;
		}
		
		String strLogId = schemaInfo.getLogId().toString();
		
		BaseOpAnalyser opAnalyser = OpAnalyserFactory.createOpAnalyser(schemaInfo.getOp());
		if (opAnalyser == null) {
			LOG.error("schemaInfo:"+schemaInfo.getSchemaId()+",logid:"+schemaInfo.getLogId());
			LOG.error("RealtimeLogAnalyser AddSchemaInfo, OpAnalyserFactory create null OpAnalyser");
			return false;
		}
		
		// 过滤掉不需要实时计算的
		if (!opAnalyser.IsRealtime()) {
			if (isIniting) {
				// 初始化时可能会传入非实时的op，过滤掉即可
				LOG.info("RealtimeLogAnalyser AddSchemaInfo, opAnalyser IsRealTime false");
				return true;
			}
			
			LOG.error("RealtimeLogAnalyser AddSchemaInfo, opAnalyser IsRealTime false");
			return false;
		}
		
		PlainSchemaAnalyser newSchemaAnalyser = new PlainSchemaAnalyser();
		
		if (!newSchemaAnalyser.Init(schemaInfo, opAnalyser)) {
			LOG.error("RealtimeLogAnalyser AddSchemaInfo, PlainSchemaAnalyser Init failed, logId is " + strLogId);
			return false;
		}
		
		if (logId2PlainSchemaInfos.get(strLogId) == null) {
			logId2PlainSchemaInfos.put(strLogId, new ArrayList<PlainSchemaAnalyser>());
		}
		
		// 处理schemaId重复的情况
		for (PlainSchemaAnalyser schemaAnalyser: logId2PlainSchemaInfos.get(strLogId)) {
			if (schemaAnalyser.GetStrSchemaId().equals(newSchemaAnalyser.GetStrSchemaId())) {
				if (isIniting) {
					LOG.error("RealtimeLogAnalyser AddSchemaInfo, duplicate schemaId " + schemaAnalyser.GetStrSchemaId() + ", logId " + strLogId);
					return false;
				} else {
					// 注意此处不会覆盖
					LOG.info("RealtimeLogAnalyser AddSchemaInfo, duplicate schemaId " + schemaAnalyser.GetStrSchemaId() + ", logId " + strLogId);
					return true;
				}
			}
		}
		
		// 没有重复的
		logId2PlainSchemaInfos.get(strLogId).add(newSchemaAnalyser);
		return true;
	}
	
	// TODO 待测试
	private boolean ModSchemaInfo(SchemaInfo schemaInfo) {
		if (schemaInfo.getLogId() == null || schemaInfo.getLogId().intValue() <= 0) {
			LOG.error("RealtimeLogAnalyser ModSchemaInfo, null logId or logId <= 0");
			return false;
		}
		
		String strLogId = schemaInfo.getLogId().toString();
		
		if (logId2PlainSchemaInfos.get(strLogId) == null) {
			LOG.error("RealtimeLogAnalyser ModSchemaInfo, logId " + strLogId + " has no schemaInfo in cache");
			return false;
		}
		
		BaseOpAnalyser opAnalyser = OpAnalyserFactory.createOpAnalyser(schemaInfo.getOp());
		if (opAnalyser == null) {
			LOG.error("RealtimeLogAnalyser ModSchemaInfo, OpAnalyserFactory create null OpAnalyser");
			return false;
		}
		
		// 过滤掉不需要实时计算的
		if (!opAnalyser.IsRealtime()) {
			LOG.error("RealtimeLogAnalyser ModSchemaInfo, opAnalyser IsRealTime false");
			return false;
		}
		
		PlainSchemaAnalyser newSchemaAnalyser = new PlainSchemaAnalyser();
		
		if (!newSchemaAnalyser.Init(schemaInfo, opAnalyser)) {
			LOG.error("RealtimeLogAnalyser ModSchemaInfo, PlainSchemaAnalyser Init failed, logId is " + strLogId);
			return false;
		}
		
		boolean found = false;
		
		for (int i = 0; i < logId2PlainSchemaInfos.get(strLogId).size(); ++i) {
			if (logId2PlainSchemaInfos.get(strLogId).get(i).GetStrSchemaId().equals(newSchemaAnalyser.GetStrSchemaId())) {
				logId2PlainSchemaInfos.get(strLogId).set(i, newSchemaAnalyser);
				found = true;
			}
		}
		
		if (!found) {
			LOG.error("RealtimeLogAnalyser ModSchemaInfo, schemaId " + newSchemaAnalyser.GetStrSchemaId() + " not found in cache, logId " + strLogId + "");
			return false;
		}		
		
		return true;
	}
	
	// TODO 待测试
	private boolean DelSchemaInfo(SchemaInfo schemaInfo) {
		if (schemaInfo.getLogId() == null || schemaInfo.getLogId().intValue() <= 0) {
			LOG.error("RealtimeLogAnalyser DelSchemaInfo, null logId or logId <= 0");
			return false;
		}
		
		if (schemaInfo.getSchemaId() == null || schemaInfo.getSchemaId().intValue() <= 0) {
			LOG.error("RealtimeLogAnalyser DelSchemaInfo, null schemaId or schemaId <= 0");
			return false;
		}
		
		String strLogId = schemaInfo.getLogId().toString();
		String strSchemaId = schemaInfo.getSchemaId().toString();
		
		if (logId2PlainSchemaInfos.get(strLogId) == null) {
			LOG.error("RealtimeLogAnalyser DelSchemaInfo, logId " + strLogId + " has no schemaInfo in cache");
			return false;
		}
		
		// 不安全，会报错
//		boolean found = false;
//		for(PlainSchemaAnalyser schemaAnalyser: logId2PlainSchemaInfos.get(strLogId)){
//		    if (schemaAnalyser.GetStrSchemaId().equals(strSchemaId)) {
//		    	logId2PlainSchemaInfos.get(strLogId).remove(schemaAnalyser);
//		    	found = true;
//		    }
//		}
		
		boolean found = false;
		Iterator<PlainSchemaAnalyser> it = logId2PlainSchemaInfos.get(strLogId).iterator();
		while(it.hasNext()){
			PlainSchemaAnalyser cur = it.next();
		    if(cur.GetStrSchemaId().equals(strSchemaId)){
		        it.remove();
		        found = true;
		    }
		}		
		
		if (logId2PlainSchemaInfos.get(strLogId).size() == 0) {
			// 若相应log对应schema均已删除，则删除此log
			logId2PlainSchemaInfos.remove(strLogId);
		}
		
		if (!found) {
			LOG.error("RealtimeLogAnalyser DelSchemaInfo, logId " + strLogId + " has no schemaInfo with schemaId " + strSchemaId + " in cache");
			return false;
		}
		
		return true;
	}
	
	public boolean SetSchemaInfos(List<SchemaInfo> schemaInfos) {
		if (schemaInfos == null || schemaInfos.size() == 0) {
			LOG.error("RealtimeLogAnalyser setSchemaInfos, null or empty schemaInfos");
			return false;
		}
				
		for (SchemaInfo schemaInfo : schemaInfos) {
			// 保证初始化后同一个logId下没有重复的schemaId
			if (!AddSchemaInfo(schemaInfo, true)) {
				LOG.error("RealtimeLogAnalyser SetSchemaInfos, SetSchemaInfo failed");
				return false;
			}
		}
				
		return true;
	}
	
	// protected Map<String, List<PlainSchemaAnalyser>> logId2PlainSchemaInfos = new HashMap<String, List<PlainSchemaAnalyser>>();
	// 用于测试
	private String GetPrintMemoryConfigs() {
		StringBuffer buf = new StringBuffer();
		buf.append("start PrintMemoryConfigs\n");
		
		for (Map.Entry<String, List<PlainSchemaAnalyser>> entry : logId2PlainSchemaInfos.entrySet()) {
			buf.append("logId: ");
			buf.append(entry.getKey());
			buf.append("\n");
			List<PlainSchemaAnalyser> analysers = entry.getValue();
			for (PlainSchemaAnalyser analyser: analysers) {
//				buf.append("schemaId: ");
//				buf.append(analyser.GetStrSchemaId());
				buf.append(analyser.toString());
				buf.append("\n");
			}
		}
		
		return buf.toString(); 
	}
	
	public boolean UpdateConfig() {
		if (!IsDealingSpecailLog()) {
			LOG.error("RealtimeLogAnalyser UpdateConfig, not in dealingSpecialLog state, perhaps not SetLog with config log before UpdateConfigs");
			return false;
		}
		
		switch (strLogId) {
			case "-1":{
				// 添加
				SchemaInfo schemaInfo = new SchemaInfo();
				try {
					schemaInfo.setLogId(Integer.parseInt(attrMap.get("_setlogid_")));
					schemaInfo.setSchemaId(Integer.parseInt(attrMap.get("_setschemaid_")));
				} catch (NullPointerException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, _setlogid_ or _setschemaid_ null");
					return false;
				} catch (NumberFormatException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, invalid _setlogid_ or _setschemaid_");
					return false;
				} catch (Exception ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, unknown exeption occured, perhaps _setlogid_ or _setschemaid_ invalid");
					return false;
				}
				schemaInfo.setOp((attrMap.get("_setop_")));
				schemaInfo.setCascadeFields((attrMap.get("_setcascadefields_")));
				
				// 由于发送这种特殊日志到kafka无法保证幂等，因此可能会重复发送，isIniting设置为false
				if (!AddSchemaInfo(schemaInfo, false)) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, AddSchemaInfo failed");
					return false;
				}
				break;
			}
			case "-2":{
				// 修改
				SchemaInfo schemaInfo = new SchemaInfo();
				try {
					schemaInfo.setLogId(Integer.parseInt(attrMap.get("_setlogid_")));
					schemaInfo.setSchemaId(Integer.parseInt(attrMap.get("_setschemaid_")));
				} catch (NullPointerException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, _setlogid_ or _setschemaid_ null");
					return false;
				} catch (NumberFormatException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, invalid _setlogid_ or _setschemaid_");
					return false;
				} catch (Exception ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, unknown exeption occured, perhaps _setlogid_ or _setschemaid_ invalid");
					return false;
				}
				schemaInfo.setOp((attrMap.get("_setop_")));
				schemaInfo.setCascadeFields((attrMap.get("_setcascadefields_")));
				
				// 由于发送这种特殊日志到kafka无法保证幂等，因此可能会重复发送，重复设置没有影响
				if (!ModSchemaInfo(schemaInfo)) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, ModSchemaInfo failed");
					return false;
				}
				break;
			}
			case "-3":{
				// 删除
				SchemaInfo schemaInfo = new SchemaInfo();
				try {
					schemaInfo.setLogId(Integer.parseInt(attrMap.get("_setlogid_")));
					schemaInfo.setSchemaId(Integer.parseInt(attrMap.get("_setschemaid_")));
				} catch (NullPointerException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, _setlogid_ or _setschemaid_ null");
					return false;
				} catch (NumberFormatException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, invalid _setlogid_ or _setschemaid_");
					return false;
				} catch (Exception ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, unknown exeption occured, perhaps _setlogid_ or _setschemaid_ invalid");
					return false;
				}
				if (!DelSchemaInfo(schemaInfo)) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, DelSchemaInfo failed");
					return false;
				}
				break;
			}
			case "-4":{
				// 添加
				ServerInfo serverInfo = new ServerInfo();
				try {
					serverInfo.setServerId(Integer.parseInt(attrMap.get("_setserverid_")));
					serverInfo.setParentId(Integer.parseInt(attrMap.get("_setparentid_")));
					serverInfo.setGameId(Integer.parseInt(attrMap.get("_setgameid_")));
				} catch (NullPointerException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, _setserverid_ or _setparentid_ or _setgameid_ null");
					return false;
				} catch (NumberFormatException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, invalid _setserverid_ or _setparentid_ or _setgameid_");
					return false;
				} catch (Exception ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, unknown exeption occured, perhaps _setserverid_ or _setparentid_ or _setgameid_ invalid");
					return false;
				}
				
				// 由于发送这种特殊日志到kafka无法保证幂等，因此可能会重复发送
				if (!AddServerId(serverInfo)) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, AddServerId failed");
					return false;
				}
				break;
			}
			case "-5":{
				// 修改
				break;
			}
			case "-6":{
				// 删除
				ServerInfo serverInfo = new ServerInfo();
				try {
					serverInfo.setServerId(Integer.parseInt(attrMap.get("_setserverid_")));
				} catch (NullPointerException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, _setserverid_ null");
					return false;
				} catch (NumberFormatException ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, invalid _setserverid_");
					return false;
				} catch (Exception ex) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, unknown exeption occured, perhaps _setserverid_ invalid");
					return false;
				}
				
				// 由于发送这种特殊日志到kafka无法保证幂等，因此可能会重复发送
				if (!DelServerId(serverInfo)) {
					LOG.error("RealtimeLogAnalyser UpdateConfig, DelServerId failed");
					return false;
				}
				break;
			}
			
			default:
				// should not be here
				LOG.error("RealtimeLogAnalyser UpdateConfig, unknown logId " + strLogId);
				break;
		}
		
		return true;
	}
		
	public boolean SetLog(String strLog) {
		if (!super.SetLog(strLog)) {
			LOG.error("RealtimeLogAnalyser SetLog, super SetLog failed");
			return false;
		}
		
		if (IsDealingSpecailLog()) {
			LOG.info("RealtimeLogAnalyser SetLog, dealing special log");
			return true;
		}
		
		if (!logId2PlainSchemaInfos.keySet().contains(this.strLogId)) {
			LOG.error("RealtimeLogAnalyser SetLog, logId " + this.strLogId + " not in cache");
			return false;
		}
		
		// 获取strTimestamp，只有实时使用
		try {
			long timestamp = Long.parseLong((attrMap.get("_ts_") + "000"));
			dateTime = dateFormat.format(timestamp);
		} catch (NullPointerException ex) {
			LOG.error("RealtimeLogAnalyser SetLog, NullPointerException catched, field _ts_ not in log [" + strLog + "]");
			return false;
		} catch (NumberFormatException ex) {
			LOG.error("RealtimeLogAnalyser SetLog, NullPointerException catched, field _ts_ invalid in log [" + strLog + "]");
			return false;
		} catch (Exception ex) {
			LOG.error("RealtimeLogAnalyser SetLog, unknown exception [" + ex.getMessage() + "]catched, field _ts_ invalid in log [" + strLog + "]");
			return false;
		}
		
		return true;
	}
	
	public String GetDateTime() {
		return dateTime;
	}
	
	// 必须在SetLog之后调用
	public List<RealtimePlainLogItem> GetAllRealTimeLogItems() {
		List<RealtimePlainLogItem> items = null;
		
		if (logId2PlainSchemaInfos.get(this.strLogId) != null && logId2PlainSchemaInfos.get(this.strLogId).size() != 0) {
			items = new ArrayList<RealtimePlainLogItem>();
			
			for (PlainSchemaAnalyser schemaAnalyser: logId2PlainSchemaInfos.get(this.strLogId)) {
				List<String> opValues = schemaAnalyser.GetOpValues(attrMap);
				// 失败返回null
				if (opValues == null) {
					// 一般来说是log中的相应字段填写有问题，需要通知维护人员
					//LOG.error("RealtimeLogAnalyser GetAllRealTimeLogItems get opValues failed, schemaId is " + schemaAnalyser.GetStrSchemaId() + ", log is " + this.strLog);
					//LOG.error("get opValues fialed,schemaId is "+schemaAnalyser.GetStrSchemaId());
					continue;
				}
				
				List<String> cascadeValues = schemaAnalyser.GetAllCascadeValues(attrMap);
				if(cascadeValues !=null){
				for (String cascadeValue : cascadeValues) {
					for (Integer serverId : serverAncestors) {
						// ServerIdAnalyzer内部机制保证GetGameIdByServerId(serverId)一定不为null且>0
						// SchemaAnalyser内部机制保证GetStrSchemaId()一定不为null且>0
						RealtimePlainLogItem item = new RealtimePlainLogItem(schemaAnalyser.GetOp(), schemaAnalyser.GetStrSchemaId(), serverId.toString(), cascadeValue, opValues, GetDateTime());
						items.add(item);
					}
				}
			  }
				else
				{
					LOG.error("parse log cascadeValues is null");
				}
			}
		}
		
		return items;
	}
	
	
	
	public static void main(String[] args) {
		RealtimeLogAnalyser realtimeLogAnalyser = new RealtimeLogAnalyser();
		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
		
//		 * 1	material()
//		 * 2	count()
//		 * 3	distinct_count(key)
//		 * 4	sum(key)
//		 * 5	max(key)
//		 * 6	min(key)
//		 * 7	assign(key)
		// material
		String strLog = "_hip_=192.168.111.129   _logid_=100	_svrid_=6       _gid_=25        _zid_=3        _sid_=4	_amt_=10        _pid_=2        _ts_=1483801210 _acid_=583895372";
		
		// material
//		SchemaInfo si1 = new SchemaInfo();
//		si1.setLogId(100);
//		si1.setSchemaId(1);
//		si1.setOp("material(_acid_,_amt_)");
//		si1.setCascadeFields("_pid_|_zid_|_sid_");
//		si1.setMaterialId(130);
//		schemaInfos.add(si1);
		// count
		SchemaInfo si2 = new SchemaInfo();
		si2.setLogId(100);
		si2.setSchemaId(2);
		si2.setOp("count()");
		si2.setCascadeFields("");
		schemaInfos.add(si2);
		// distinct_count
//		SchemaInfo si3 = new SchemaInfo();
//		si3.setLogId(100);
//		si3.setSchemaId(3);
//		si3.setOp("distinct_count(_acid_)");
//		si3.setCascadeFields("");
//		schemaInfos.add(si3);
		// sum
		SchemaInfo si4 = new SchemaInfo();
		si4.setLogId(100);
		si4.setSchemaId(4);
		si4.setOp("sum(_amt_)");
//		si4.setCascadeFields("");
		si4.setCascadeFields("_pid_|_zid_|_sid_");
		schemaInfos.add(si4);
		// max
		SchemaInfo si5 = new SchemaInfo();
		si5.setLogId(100);
		si5.setSchemaId(5);
		si5.setOp("max(_amt_)");
		si5.setCascadeFields("");
		schemaInfos.add(si5);
		// min
		SchemaInfo si6 = new SchemaInfo();
		si6.setLogId(100);
		si6.setSchemaId(6);
		si6.setOp("min(_amt_)");
		si6.setCascadeFields("");
		schemaInfos.add(si6);
		// assign
		SchemaInfo si7 = new SchemaInfo();
		si7.setLogId(100);
		si7.setSchemaId(7);
		si7.setOp("assign(_amt_)");
		si7.setCascadeFields("");
		schemaInfos.add(si7);
		
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		ServerInfo s1 = new ServerInfo();
		s1.setServerId(1);
		s1.setParentId(0);
		s1.setGameId(2);
		serverInfos.add(s1);
		
		ServerInfo s2 = new ServerInfo();
		s2.setServerId(2);
		s2.setParentId(1);
		s2.setGameId(2);
		serverInfos.add(s2);
		
		ServerInfo s3 = new ServerInfo();
		s3.setServerId(3);
		s3.setParentId(1);
		s3.setGameId(2);
		serverInfos.add(s3);
		
		ServerInfo s4 = new ServerInfo();
		s4.setServerId(4);
		s4.setParentId(2);
		s4.setGameId(2);
		serverInfos.add(s4);
		
		ServerInfo s5 = new ServerInfo();
		s5.setServerId(5);
		s5.setParentId(2);
		s5.setGameId(2);
		serverInfos.add(s5);
		
		ServerInfo s6 = new ServerInfo();
		s6.setServerId(6);
		s6.setParentId(3);
		s6.setGameId(2);
		serverInfos.add(s6);
		
		ServerInfo s7 = new ServerInfo();
		s7.setServerId(7);
		s7.setParentId(0);
		s7.setGameId(3);
		serverInfos.add(s7);
		
		if (realtimeLogAnalyser.Init(schemaInfos, serverInfos)) {
			if (realtimeLogAnalyser.SetLog(strLog) && !realtimeLogAnalyser.IsDealingSpecailLog()) {
				List<RealtimePlainLogItem> plainItems = realtimeLogAnalyser.GetAllRealTimeLogItems();
				if (plainItems != null) {
					for (RealtimePlainLogItem item: plainItems) {
						System.out.print("\n" + item.toString());
					}
				}
			}
		}
		
		String log = "";
		
		StringBuffer buf = new StringBuffer();
		
		System.out.println("---------------开始测试update schema----------------\n");
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		
		//
		System.out.println("增加，无log，无schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-1  _setlogid_=200  _setschemaid_=201  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");
		
		//
		System.out.println("增加，有log，无schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-1  _setlogid_=100  _setschemaid_=201  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");
		
		//
		System.out.println("增加，有log，有schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-1  _setlogid_=100  _setschemaid_=2  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();		
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");

		//
		System.out.println("修改，无log，无schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-2  _setlogid_=200  _setschemaid_=201  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");
		
		//
		System.out.println("修改，有log，无schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-2  _setlogid_=100  _setschemaid_=201  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");
		
		//
		System.out.println("修改，有log，有schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-2  _setlogid_=100  _setschemaid_=2  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");

		//
		System.out.println("删除，无log，无schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-3  _setlogid_=200  _setschemaid_=201  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");
		
		//
		System.out.println("删除，有log，无schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-3  _setlogid_=100  _setschemaid_=201  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		System.out.println("-------------------\n");
		
		//
		System.out.println("删除，有log，有schema");
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		log = "_logid_=-3  _setlogid_=100  _setschemaid_=2  _setop_=max(_acid_)  _setcascadefields_=";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.GetPrintMemoryConfigs());
		
		
		System.out.println("---------------开始测试update serverinfo----------------\n");
		//避免初始化多次，造成相同的serverID在构造时报错。
		//错误信息:com.taomee.tms.mgr.core.serveridanalyser.ServerIdAnalyzer.Init(67) | ServerIdAnalyizer Init, duplicate serverId, serverId is 1
		realtimeLogAnalyser = new RealtimeLogAnalyser();
		realtimeLogAnalyser.Init(schemaInfos, serverInfos);
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		
		System.out.println("增加，serverid已存在");
		log = "_logid_=-4  _setserverid_=7  _setparentid_=0  _setgameid_=3";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("增加，根节点，gameid已存在");
		log = "_logid_=-4  _setserverid_=8  _setparentid_=0  _setgameid_=3";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("增加，根节点，gameid不重复");
		log = "_logid_=-4  _setserverid_=8  _setparentid_=0  _setgameid_=4";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("增加，叶节点，parent不存在");
		log = "_logid_=-4  _setserverid_=9  _setparentid_=10  _setgameid_=4";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("增加，叶节点，parent存在，gameid不一致");
		log = "_logid_=-4  _setserverid_=9  _setparentid_=7  _setgameid_=4";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("增加，叶节点，parent存在，gameid一致");
		log = "_logid_=-4  _setserverid_=9  _setparentid_=7  _setgameid_=3";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("删除，serverId不存在");
		log = "_logid_=-6  _setserverid_=10";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("删除，serverId存在但不是叶节点");
		log = "_logid_=-6  _setserverid_=1";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
		
		System.out.println("删除，serverId存，也是叶节点");
		log = "_logid_=-6  _setserverid_=9";
		realtimeLogAnalyser.SetLog(log);
		realtimeLogAnalyser.UpdateConfig();
		System.out.println(realtimeLogAnalyser.serverIdAnalyzer.GetPrintAllMemoryInfos());
		System.out.println("-------------------\n");
	}
}
































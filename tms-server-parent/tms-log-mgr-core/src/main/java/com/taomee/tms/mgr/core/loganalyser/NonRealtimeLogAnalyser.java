package com.taomee.tms.mgr.core.loganalyser;

import java.util.ArrayList;
import java.util.HashMap;
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


// 离线计算使用
public class NonRealtimeLogAnalyser extends BaseLogAnalyser {
	private static final long serialVersionUID = 5960660091971133469L;
	private static final Logger LOG = LoggerFactory.getLogger(NonRealtimeLogAnalyser.class);
	
	// <logid, SchemaAnalyser列表>
	protected Map<String, List<PlainSchemaAnalyser>> logId2PlainSchemaInfos = new HashMap<String, List<PlainSchemaAnalyser>>();
	protected Map<String, List<MaterialSchemaAnalyser>> logId2MaterialSchemaInfos = new HashMap<String, List<MaterialSchemaAnalyser>>();
	
	public boolean SetSchemaInfos(List<SchemaInfo> schemaInfos) {
		if (schemaInfos == null || schemaInfos.size() == 0) {
			LOG.error("NonRealtimeLogAnalyser setSchemaInfos, null or empty schemaInfos");
			return false;
		}
				
		for (SchemaInfo schemaInfo : schemaInfos) {
			// 先构造OpAnalyser
			BaseOpAnalyser opAnalyser = OpAnalyserFactory.createOpAnalyser(schemaInfo.getOp());
			if (opAnalyser == null) {
				LOG.error("NonRealtimeLogAnalyser setSchemaInfos, OpAnalyserFactory create null OpAnalyser");
				return false;
			}
			
			// 过滤掉不需要离线计算的
			if (!opAnalyser.IsNonRealtime()) {
				continue;
			}
			
			if (schemaInfo.getLogId() == null || schemaInfo.getLogId().intValue() <= 0) {
				LOG.error("NonRealtimeLogAnalyser Init, empty logId or logId < 0");
				return false;
			}
						
			String strLogId = schemaInfo.getLogId().toString();
			
			switch(opAnalyser.GetOp()) {
				case "material": {
					MaterialSchemaAnalyser schemaAnalyser = new MaterialSchemaAnalyser();
					
					if (!schemaAnalyser.Init(schemaInfo, opAnalyser)) {
						LOG.error("NonRealtimeLogAnalyser setSchemaInfos, MaterialSchemaAnalyser Init failed, logId is " + strLogId);
						return false;
					}
					
					if (logId2MaterialSchemaInfos.get(strLogId) == null) {
						logId2MaterialSchemaInfos.put(strLogId,	new ArrayList<MaterialSchemaAnalyser>());
					}
					
					logId2MaterialSchemaInfos.get(strLogId).add(schemaAnalyser);
					LOG.info("adding {} to memory",schemaAnalyser);
					break;
				}
				case "count":
				case "distinct_count":
				case "sum":
				case "max":
				case "min":
				case "assign": {
					PlainSchemaAnalyser schemaAnalyser = new PlainSchemaAnalyser();
					
					if (!schemaAnalyser.Init(schemaInfo, opAnalyser)) {
						LOG.error("NonRealtimeLogAnalyser setSchemaInfos, PlainSchemaAnalyser Init failed, logId is " + strLogId);
						return false;
					}
					
					if (logId2PlainSchemaInfos.get(strLogId) == null) {
						logId2PlainSchemaInfos.put(strLogId, new ArrayList<PlainSchemaAnalyser>());
					}
					
					logId2PlainSchemaInfos.get(strLogId).add(schemaAnalyser);
					LOG.info("adding {} to memory",schemaAnalyser);
					break;
				}
				default:
					LOG.error("NonRealtimeLogAnalyser createSchemaAnalyser, invalid op " + opAnalyser.GetOp());
					return false;
			}
		}
				
		return true;
	}
	
	public boolean SetLog(String strLog) {
		if (!super.SetLog(strLog)) {
			LOG.error("NonRealtimeLogAnalyser SetLog, super SetLog failed");
			return false;
		}
		
		if (!logId2PlainSchemaInfos.keySet().contains(this.strLogId) && !logId2MaterialSchemaInfos.keySet().contains(this.strLogId)) {
			LOG.error("NonRealtimeLogAnalyser SetLog, logId " + this.strLogId + " not in cache");
			return false;
		}
		
		return true;
	}

	// 必须在SetLog之后调用
	public List<NonRealtimePlainLogItem> GetAllNonRealtimePlainLogItems() {
		List<NonRealtimePlainLogItem> items = null;
		
		if (logId2PlainSchemaInfos.get(this.strLogId) != null && logId2PlainSchemaInfos.get(this.strLogId).size() != 0) {
			items = new ArrayList<NonRealtimePlainLogItem>();
			
			for (PlainSchemaAnalyser schemaAnalyser: logId2PlainSchemaInfos.get(this.strLogId)) {
				List<String> opValues = schemaAnalyser.GetOpValues(attrMap);
				// 失败返回null
				if (opValues == null) {
					// 一般来说是log中的相应字段填写有问题，需要通知维护人员
					//LOG.error("NonRealtimeLogAnalyser GetAllNonRealtimePlainLogItems get opValues failed, schemaId is " + schemaAnalyser.GetStrSchemaId() + ", log is " + this.strLog);
					//LOG.warn("this log("+this.strLog+") miss schemaid "+schemaAnalyser.GetStrSchemaId()+", no effect this log other schemas Split");
					//LOG.warn("this log miss schemaid "+schemaAnalyser.GetStrSchemaId()+", no effect this log other schemas Split");
					continue;
				}
				
				List<String> cascadeValues = schemaAnalyser.GetAllCascadeValues(attrMap);
				/**
				 * 这边添加级联字段是否为null判断，避免后台插入配表的时候插错，导致迭代数据的时候出错。
				 */
				if(cascadeValues !=null)
				{
					for (String cascadeValue : cascadeValues) {
						for (Integer serverId : serverAncestors) {
						// ServerIdAnalyzer内部机制保证GetGameIdByServerId(serverId)一定不为null且>0
						// SchemaAnalyser内部机制保证GetStrSchemaId()一定不为null且>0
						NonRealtimePlainLogItem item = new NonRealtimePlainLogItem(schemaAnalyser.GetOp(), schemaAnalyser.GetStrSchemaId(), serverId.toString(), cascadeValue, opValues, (String)attrMap.get("_gid_"));
						items.add(item);
						}
					}
				}
				else{
					LOG.debug("Error:fail to get cascade values with log:\"{}\",",this.strLog);
				}
			}
		}
		
		return items;
	}

	public List<NonRealtimeMaterialLogItem> GetAllNonRealtimeMaterailLogItems() {
		List<NonRealtimeMaterialLogItem> items = null;
		
		if (logId2MaterialSchemaInfos.get(this.strLogId) != null && logId2MaterialSchemaInfos.get(this.strLogId).size() != 0) {
			items = new ArrayList<NonRealtimeMaterialLogItem>();
			
			for (MaterialSchemaAnalyser schemaAnalyser: logId2MaterialSchemaInfos.get(this.strLogId)) {
				List<String> opValues = schemaAnalyser.GetOpValues(attrMap);
				// 失败返回null
				if (opValues == null) {
					// 一般来说是log中的相应字段填写有问题，需要通知维护人员
					LOG.error("NonRealtimeLogAnalyser GetAllNonRealTimeLogItems get opValues failed, schemaId is " + schemaAnalyser.GetStrSchemaId());
					continue;
				}
				
				List<String> cascadeValues = schemaAnalyser.GetAllCascadeValues(attrMap);
				for (String cascadeValue : cascadeValues) {
					for (Integer serverId : serverAncestors) {
						// ServerIdAnalyzer内部机制保证GetGameIdByServerId(serverId)一定不为null且>0
						NonRealtimeMaterialLogItem bean = new NonRealtimeMaterialLogItem(schemaAnalyser.GetOp(), schemaAnalyser.GetStrSchemaId(), 
								serverId.toString(), cascadeValue, opValues, (String)attrMap.get("_gid_"), schemaAnalyser.GetMaterialId());
						items.add(bean);
					}
				}
			}
		}
		
		return items;
	}
		
	public static void main(String[] args) {
		NonRealtimeLogAnalyser nonRealtimeLogAnalyser = new NonRealtimeLogAnalyser();
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
		
		/*SchemaInfo si1 = new SchemaInfo();
		si1.setLogId(100);
		si1.setSchemaId(1);
		si1.setOp("material(_acid_,_amt_)");
		si1.setCascadeFields("_pid_|_zid_|_sid_");
		si1.setMaterialId(130);
		schemaInfos.add(si1);*/
		// count
		SchemaInfo si2 = new SchemaInfo();
		si2.setLogId(100);
		si2.setSchemaId(2);
		si2.setOp("count()");
		si2.setCascadeFields("");
		schemaInfos.add(si2);
		// distinct_count
		SchemaInfo si3 = new SchemaInfo();
		si3.setLogId(100);
		si3.setSchemaId(3);
		si3.setOp("distinct_count(_acid_)");
		si3.setCascadeFields("");
		schemaInfos.add(si3);
		// sum
		/*SchemaInfo si4 = new SchemaInfo();
		si4.setLogId(100);
		si4.setSchemaId(4);
		si4.setOp("sum(_amt_)");
//		si4.setCascadeFields("");
		si4.setCascadeFields("_pid_|_zid_|_sid_");
		schemaInfos.add(si4);*/
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
		
		/**
		 * 测试非法schema操作
		 */
		SchemaInfo si8 = new SchemaInfo();
		si8.setLogId(100);
		si8.setSchemaId(8);
		si8.setOp("sum(_bb_)");
		si8.setCascadeFields("");
		schemaInfos.add(si8);
		
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		ServerInfo s1 = new ServerInfo();
		s1.setServerId(1);
		s1.setParentId(0);
		s1.setGameId(16);
		serverInfos.add(s1);
		
		ServerInfo s2 = new ServerInfo();
		s2.setServerId(2);
		s2.setParentId(1);
		s2.setGameId(16);
		serverInfos.add(s2);
		
		ServerInfo s3 = new ServerInfo();
		s3.setServerId(3);
		s3.setParentId(1);
		s3.setGameId(16);
		serverInfos.add(s3);
		
		ServerInfo s4 = new ServerInfo();
		s4.setServerId(4);
		s4.setParentId(2);
		s4.setGameId(16);
		serverInfos.add(s4);
		
		ServerInfo s5 = new ServerInfo();
		s5.setServerId(5);
		s5.setParentId(2);
		s5.setGameId(16);
		serverInfos.add(s5);
		
		ServerInfo s6 = new ServerInfo();
		s6.setServerId(6);
		s6.setParentId(3);
		s6.setGameId(16);
		serverInfos.add(s6);
		
		if (nonRealtimeLogAnalyser.Init(schemaInfos, serverInfos)) {
			if (nonRealtimeLogAnalyser.SetLog(strLog) && !nonRealtimeLogAnalyser.IsDealingSpecailLog()) {
				List<NonRealtimePlainLogItem> plainItems = nonRealtimeLogAnalyser.GetAllNonRealtimePlainLogItems();
				if (plainItems != null) {
					for (NonRealtimePlainLogItem item: plainItems) {
						System.out.print("\n" + item.toString());
					}
				}
				
				List<NonRealtimeMaterialLogItem> materialItems = nonRealtimeLogAnalyser.GetAllNonRealtimeMaterailLogItems();
				if (materialItems != null) {
					for (NonRealtimeMaterialLogItem item: materialItems) {
						System.out.print("\n" + item.toString());
					}
				}
			}
		}
	}
}























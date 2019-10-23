package com.taomee.bigdata.test;

import java.util.ArrayList;
import java.util.List;

import com.taomee.tms.mgr.core.loganalyser.RealtimeLogAnalyser;
import com.taomee.tms.mgr.core.loganalyser.RealtimePlainLogItem;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class TestRealtimeLogAnalyser {
	
	public static void main(String[] args) {
		RealtimeLogAnalyser realtimeLogAnalyser = new RealtimeLogAnalyser();
		
		String strLog = "_hip_=192.168.111.129   _logid_=100	_svrid_=1       _gid_=2        _zid_=3        _sid_=4	_amt_=10        _pid_=2        _ts_=1483801210 _acid_=583895372";
		List<SchemaInfo> schemaInfos = new ArrayList<>();
		// count
		SchemaInfo si2 = new SchemaInfo();
		si2.setLogId(100);
		si2.setSchemaId(2);
		si2.setOp("count()");
		si2.setCascadeFields("");
		schemaInfos.add(si2);
		
		// sum
		SchemaInfo si4 = new SchemaInfo();
		si4.setLogId(100);
		si4.setSchemaId(4);
		si4.setOp("sum(_amt_)");
		si4.setCascadeFields("");
		schemaInfos.add(si4);
		
		SchemaInfo si5 = new SchemaInfo();
		si5.setLogId(100);
		si5.setSchemaId(5);
		si5.setOp("sum(_bb_)");
		si5.setCascadeFields("");
		schemaInfos.add(si5);
		
		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		ServerInfo s1 = new ServerInfo();
		s1.setServerId(1);
		s1.setParentId(0);
		s1.setGameId(2);
		serverInfos.add(s1);
		
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
	}

}

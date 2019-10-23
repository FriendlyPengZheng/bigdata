package com.taomee.bigdata.test;

import java.util.ArrayList;
import java.util.List;

import com.taomee.tms.mgr.core.loganalyser.NonRealtimeLogAnalyser;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimeMaterialLogItem;
import com.taomee.tms.mgr.core.loganalyser.NonRealtimePlainLogItem;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

public class TestDasan {

	public static void main(String[] args) {

		NonRealtimeLogAnalyser nonRealtimeLogAnalyser = new NonRealtimeLogAnalyser();
		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
		SchemaInfo si1 = new SchemaInfo();
		si1.setLogId(9);
		si1.setSchemaId(88);
		si1.setOp("max(_olcnt_)");
		//si1.setCascadeFields("_gid_|_zone_");
		si1.setCascadeFields("");
		schemaInfos.add(si1);
		
		SchemaInfo si2 = new SchemaInfo();
		si2.setLogId(9);
		si2.setSchemaId(89);
		si2.setOp("min(_olcnt_)");
		//si1.setCascadeFields("_gid_|_zone_");
		si2.setCascadeFields("");
		schemaInfos.add(si2);
		
		SchemaInfo si3 = new SchemaInfo();
		si3.setLogId(9);
		si3.setSchemaId(90);
		si3.setOp("sum(_amt_)");
		//si1.setCascadeFields("_gid_|_zone_");
		si3.setCascadeFields("");
		schemaInfos.add(si3);

		/*
		 * SchemaInfo si2 = new SchemaInfo(); si2.setLogId(9);
		 * si2.setSchemaId(89); si2.setOp("max(_olcnt_)");
		 * si2.setCascadeFields("_zone_"); schemaInfos.add(si2);
		 */

		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
		ServerInfo s1 = new ServerInfo();
		s1.setServerId(1);
		s1.setParentId(0);
		s1.setGameId(2);
		serverInfos.add(s1);

		ServerInfo s2 = new ServerInfo();
		s2.setServerId(2);
		s2.setParentId(0);
		s2.setGameId(16);
		serverInfos.add(s2);
		String strLog = new String(
				"_hip_=192.168.111.129\t_logid_=9\t_svrid_=1\t_gid_=2\t_acid_=583895372\t_olcnt_=300\t_zone_=7k7k\n_hip_=192.168.111.129\t_logid_=9\t_svrid_=1\t_gid_=1\t_acid_=583895372\t_amt_=1500");

		if (nonRealtimeLogAnalyser.Init(schemaInfos, serverInfos)) {
			{
				String[] s = strLog.split("\n");
				for (String s21 : s) {
					{
						if (nonRealtimeLogAnalyser.SetLog(s21)
								&& !nonRealtimeLogAnalyser
										.IsDealingSpecailLog()) {
							List<NonRealtimePlainLogItem> plainItems = nonRealtimeLogAnalyser
									.GetAllNonRealtimePlainLogItems();
							if (plainItems != null) {
								for (NonRealtimePlainLogItem item : plainItems) {
									System.out.print("\n" + item.toString());
								}
							}

							List<NonRealtimeMaterialLogItem> materialItems = nonRealtimeLogAnalyser
									.GetAllNonRealtimeMaterailLogItems();
							if (materialItems != null) {
								for (NonRealtimeMaterialLogItem item : materialItems) {
									System.out.print("\n" + item.toString());
								}
							}
						}
					}
				}
			}
		}
	}
}

//package test.taomee.bigdata.lib;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.taomee.tms.mgr.core.loganalyser.NonRealtimeLogAnalyser;
//import com.taomee.tms.mgr.core.loganalyser.NonRealtimeLogItem;
//import com.taomee.tms.mgr.entity.SchemaInfo;
//import com.taomee.tms.mgr.entity.ServerInfo;
//
//public class TestNonRealtimeLogAnalyzer {
//	
//	public static void main (String args[]){
//		NonRealtimeLogAnalyser nonRealtimeLogAnalyser = new NonRealtimeLogAnalyser();
//		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
//		
////		 * 1	material()
////		 * 2	count()
////		 * 3	distinct_count(key)
////		 * 4	sum(key)
////		 * 5	max(key)
////		 * 6	min(key)
////		 * 7	assign(key)
//		// material
//		String strLog = "_hip_=192.168.111.129   _logid_=100	_svrid_=6       _gid_=25        _zid_=3        _sid_=4	_amt_=10        _pid_=2        _ts_=1483801210 _acid_=583895372";
//		
//		SchemaInfo si1 = new SchemaInfo();
//		si1.setLogId(100);
//		si1.setSchemaId(1);
//		si1.setOp("material(_acid_,_amt_)");
//		si1.setCascadeFields("_pid_|_zid_|_sid_");
//		schemaInfos.add(si1);
//		// count
//		SchemaInfo si2 = new SchemaInfo();
//		si2.setLogId(100);
//		si2.setSchemaId(2);
//		si2.setOp("count()");
//		si2.setCascadeFields("");
//		schemaInfos.add(si2);
//		// distinct_count
//		SchemaInfo si3 = new SchemaInfo();
//		si3.setLogId(100);
//		si3.setSchemaId(3);
//		si3.setOp("distinct_count(_acid_)");
//		si3.setCascadeFields("");
//		schemaInfos.add(si3);
//		// sum
//		SchemaInfo si4 = new SchemaInfo();
//		si4.setLogId(100);
//		si4.setSchemaId(4);
//		si4.setOp("sum(_amt_)");
//		si4.setCascadeFields("");
//		schemaInfos.add(si4);
//		// max
//		SchemaInfo si5 = new SchemaInfo();
//		si5.setLogId(100);
//		si5.setSchemaId(5);
//		si5.setOp("max(_amt_)");
//		si5.setCascadeFields("");
//		schemaInfos.add(si5);
//		// min
//		SchemaInfo si6 = new SchemaInfo();
//		si6.setLogId(100);
//		si6.setSchemaId(6);
//		si6.setOp("min(_amt_)");
//		si6.setCascadeFields("");
//		schemaInfos.add(si6);
//		// assign
//		SchemaInfo si7 = new SchemaInfo();
//		si7.setLogId(100);
//		si7.setSchemaId(7);
//		si7.setOp("assign(_amt_)");
//		si7.setCascadeFields("");
//		schemaInfos.add(si7);
//		
//		List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
//		ServerInfo s1 = new ServerInfo();
//		s1.setServerId(1);
//		s1.setParentId(0);
//		s1.setGameId(16);
//		serverInfos.add(s1);
//		
//		ServerInfo s2 = new ServerInfo();
//		s2.setServerId(2);
//		s2.setParentId(1);
//		serverInfos.add(s2);
//		
//		ServerInfo s3 = new ServerInfo();
//		s3.setServerId(3);
//		s3.setParentId(1);
//		serverInfos.add(s3);
//		
//		ServerInfo s4 = new ServerInfo();
//		s4.setServerId(4);
//		s4.setParentId(2);
//		serverInfos.add(s4);
//		
//		ServerInfo s5 = new ServerInfo();
//		s5.setServerId(5);
//		s5.setParentId(2);
//		serverInfos.add(s5);
//		
//		ServerInfo s6 = new ServerInfo();
//		s6.setServerId(6);
//		s6.setParentId(3);
//		serverInfos.add(s6);
//		
//		if (nonRealtimeLogAnalyser.Init(schemaInfos, serverInfos)) {
//			System.out.println("-----------------------------------");
//			if (nonRealtimeLogAnalyser.SetLog(strLog)) {
//				List<NonRealtimeLogItem> items = nonRealtimeLogAnalyser.GetAllNonRealTimeLogItems();
//				if (items != null) {
//					for (NonRealtimeLogItem item: items) {
//						System.out.print("\n" + item.toString());
//					}
//				}
//			}
//		}
//	}
//
//}

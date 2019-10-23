//package test.taomee.bigdata.lib;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.taomee.tms.mgr.api.LogMgrService;
//import com.taomee.tms.mgr.entity.SchemaInfo;
//import com.taomee.tms.mgr.entity.ServerInfo;
//import com.taomee.tms.mgr.entity.TaskInfo;
//
//
////输出台测试用
//public class Test2 {
//	public static void main(String[] args) {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
//				new String[] { "applicationContext.xml" });
//		context.start();
//		LogMgrService logMgrService = (LogMgrService) context.getBean("LogMgrService"); // 获取bean
//		// service
//		// invocation
//		// proxy		
//		try {
////			List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
////			schemaInfos = logMgrService.getSchemaInfosByLogType(Integer.valueOf(0));
////			
////			System.out.println("SchemeInfo query...");
////			for(SchemaInfo info:schemaInfos)
////			{
////				System.out.print(info+"-->");
////			}
////			System.out.println();
////			
////			///////////////////////
////			List<ServerInfo> serverInfos = new ArrayList<ServerInfo>();
////			serverInfos = logMgrService.getAllServerInfos(Integer.valueOf(0));
////			
////			System.out.println("serverInfo query...");
////			for(ServerInfo info:serverInfos)
////			{
////				System.out.println(info+"......");
////				
////			}
////			System.out.println();
//			
//			TaskInfo taskInfo = logMgrService.getTaskInfo(10);
//			System.out.println(taskInfo.getOp());
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}

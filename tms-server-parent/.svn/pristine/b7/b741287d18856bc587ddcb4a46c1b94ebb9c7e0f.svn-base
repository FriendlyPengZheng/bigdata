package com.taomee.tms.test;
import org.junit.Test;
import com.taomee.bigdata.lib.SetExpressionAnalyzer;
import com.taomee.tms.client.simulatedubboserver.HdfsLoadPathConditions;
import com.taomee.tms.common.util.LoadConditionUtils;

/**
 * 测试
 * @author looper
 * @date 2017年5月18日 下午2:35:36
 * @project tms-hdfsFetchData TestStringBuider
 */
public class TestStringBuider {
	
	public static void main(String[] args) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("/user/");
		System.out.println(builder.length());
		TestStringBuider tsb = new TestStringBuider();
		tsb.test1();
		tsb.test2();
	}
	
	/**
	 * 测试默认情况
	 */
	@Test
	public void test1()
	{
		LoadConditionUtils loadConditionUtils = new LoadConditionUtils();
		HdfsLoadPathConditions conditions = new HdfsLoadPathConditions();
		conditions.setTableName("t_156_artifact");
		conditions.setFileDate("20170518");
		String path = loadConditionUtils.loadPath(conditions);
		System.out.println(path);
	}
	
	/**
	 * 填写hdfs path入库拼凑条件
	 */
	@Test
	public void test2()
	{
		LoadConditionUtils loadConditionUtils = new LoadConditionUtils();
		HdfsLoadPathConditions conditions = new HdfsLoadPathConditions();
		conditions.setHiveHomeBase("/hive/");
		conditions.setDbName("taomee");
		conditions.setTableName("t_156_artifact");
		conditions.setGameId("25");
		conditions.setFileDate("20170518");
		String path = loadConditionUtils.loadPath(conditions);
		System.out.println(path);
	}
	
	@Test
	public void test3()
	{
		String zeroOffset = LoadConditionUtils.periodReflectHdfsFileDate(3);
		try {
			//Date date = SetExpressionAnalyzer.getDateByOffset(zeroOffset, "20170518");
			String date = SetExpressionAnalyzer.getDateByOffset(zeroOffset, "20170519");
			
			System.out.println(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(zeroOffset);
	}

}

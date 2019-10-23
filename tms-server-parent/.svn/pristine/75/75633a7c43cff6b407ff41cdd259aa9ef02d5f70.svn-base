package test.taomee.bigdata.lib;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.AfterClass;
import org.junit.Test;

import com.taomee.bigdata.lib.SetExpressionAnalyzer;

public class TestSetExpressionAnalyzer {
	private Calendar cal = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	{
		cal.setFirstDayOfWeek(Calendar.MONDAY);
	}
	
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//	}
//
	@Test
	public void testGetInputPathsBySetExpression() throws ParseException {
		//测试material
		assertArrayEquals(new String[]{"/bigdata/output/day/20161210/basic/1_material*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("m_1[d1]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/day/20161211/basic/1_material*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("m_1[d0]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/day/20161211/basic/19_material*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("m_19[d0]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/day/20161210/basic/19_material*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("m_19[d1,d1]", "20161211", "*"));
		assertArrayEquals(new String[] {
				"/bigdata/output/day/20161210/basic/19_material*",
				"/bigdata/output/day/20161211/basic/19_material*",
				},
				SetExpressionAnalyzer.getInputPathsBySetExpression("m_19[d1,d0]", "20161211", "*"));
		assertArrayEquals(new String[] {
				"/bigdata/output/day/20170109/18_artifact/part*",
				"/bigdata/output/day/20170110/18_artifact/part*",
				"/bigdata/output/day/20170111/18_artifact/part*",
				"/bigdata/output/day/20170112/18_artifact/part*",
				"/bigdata/output/day/20170113/18_artifact/part*",
				"/bigdata/output/day/20170114/18_artifact/part*",
				"/bigdata/output/day/20170115/18_artifact/part*" },
				SetExpressionAnalyzer.getInputPathsBySetExpression("a_18[W0]",
						"20170109", "*"));

		// 测试artifact
		assertArrayEquals(new String[]{"/bigdata/output/sum/20161210/1_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_1[d1]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/sum/20161211/1_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_1[d0]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/day/20161211/18_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_18[d0]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/sum/20161210/19_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_19[d1,d1]", "20161211", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/sum/20170102/1_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_1[w0]", "20170105", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/week/20170102/20_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_20[w0]", "20170105", "*"));
		assertArrayEquals(new String[]{"/bigdata/output/week/20170105/20_artifact/part*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("a_20[d0]", "20170105", "*"));
		assertArrayEquals(new String[] {
				"/bigdata/output/sum/20161210/19_artifact/part*",
				"/bigdata/output/sum/20161211/19_artifact/part*",
		},
		SetExpressionAnalyzer.getInputPathsBySetExpression("a_19[d1,d0]", "20161211", "*"));
		assertArrayEquals(new String[] {"/bigdata/output/day/20170113/basic/19_material*",},SetExpressionAnalyzer.getInputPathsBySetExpression("m_19[v0]", "20170113", "*"));
		assertArrayEquals(new String[] {"/bigdata/output/day/20170113/basic/19_materialG2-*",},SetExpressionAnalyzer.getInputPathsBySetExpression("m_19[d0]", "20170113", "2"));
		assertArrayEquals(new String[] {"/bigdata/output/day/20170113/basic/19_materialG632-*","/bigdata/output/day/20170113/basic/19_materialG25-*"},SetExpressionAnalyzer.getInputPathsBySetExpression("m_19[d0]", "20170113", "632,25"));
		
		//测试linux local
		assertArrayEquals(new String[]{"file:///home/hadoop/mrtest/20161210/input0/*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_0[d1]", "20161211", "*"));
		assertArrayEquals(new String[]{"file:///home/hadoop/mrtest/20161210/input0/*","file:///home/hadoop/mrtest/20161211/input0/*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_0[d1,d0]", "20161211", "*"));
		
		//测试windows local
		assertArrayEquals(new String[]{"file:///d:/mrtest/20161210/input0/*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("winlocal_0[d1]", "20161211", "*"));
		
		//测试hdfs
		assertArrayEquals(new String[]{"/home/hadoop/mrtest/20161210/input0/*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("hdfs_0[d1]", "20161211", "*"));
	}
//	
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void testGetInputPathsBySetExpressionForException() throws ParseException {
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[d1,2]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[1,d]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[d1,]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[d1,0]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[,d1]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[,0d]", "20161211", "*"));
//		assertArrayEquals(new String[]{"/home/hadoop/mrtest/20161210/input0/*"}, SetExpressionAnalyzer.getInputPathsBySetExpression("hdfs_0[1d]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[d1,d]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[d1,2]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_1[2d,d1]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("linlocal_0[0d]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("d_1[d1]", "20161211", "*"));
//		assertArrayEquals(new String[]{""}, SetExpressionAnalyzer.getInputPathsBySetExpression("d1[d1]", "20161211", "*"));
//		
//	}
//
//	@Test
//	public void testGetAllDate() {
//		fail("Not yet implemented");
//	}
//	
//	@Test
//	public void testGetDateByOffset() throws ParseException{
//		assertEquals("20161215",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161216", cal, sdf)));
//		assertEquals("20161215",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161216", cal, sdf)));
//		assertEquals("20161216",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161217", cal, sdf)));
//		assertEquals("20161215",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161216", cal, sdf)));
//		assertEquals("20161214",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161215", cal, sdf)));
//		assertEquals("20161216",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161217", cal, sdf)));
//		assertEquals("20161214",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20161215", cal, sdf)));
//		assertEquals("20160229",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20160301", cal, sdf)));
//		assertEquals("20151231",sdf.format(SetExpressionAnalyzer.getDateByOffset("d1", "20160101", cal, sdf)));
//		assertEquals("20161212",sdf.format(SetExpressionAnalyzer.getDateByOffset("w0", "20161218", cal, sdf)));
//		assertEquals("20161205",sdf.format(SetExpressionAnalyzer.getDateByOffset("w1", "20161218", cal, sdf)));
//		assertEquals("20161128",sdf.format(SetExpressionAnalyzer.getDateByOffset("w2", "20161218", cal, sdf)));
//		assertEquals("20161121",sdf.format(SetExpressionAnalyzer.getDateByOffset("w3", "20161218", cal, sdf)));
//		assertEquals("20161114",sdf.format(SetExpressionAnalyzer.getDateByOffset("w4", "20161218", cal, sdf)));
//		assertEquals("20161107",sdf.format(SetExpressionAnalyzer.getDateByOffset("w5", "20161218", cal, sdf)));
//		assertEquals("20161031",sdf.format(SetExpressionAnalyzer.getDateByOffset("w6", "20161218", cal, sdf)));
//		assertEquals("20161201",sdf.format(SetExpressionAnalyzer.getDateByOffset("m0", "20161218", cal, sdf)));
//		assertEquals("20161101",sdf.format(SetExpressionAnalyzer.getDateByOffset("m1", "20161218", cal, sdf)));
//		assertEquals("20161001",sdf.format(SetExpressionAnalyzer.getDateByOffset("m2", "20161218", cal, sdf)));
//		assertEquals("20160901",sdf.format(SetExpressionAnalyzer.getDateByOffset("m3", "20161218", cal, sdf)));
//		assertEquals("20160801",sdf.format(SetExpressionAnalyzer.getDateByOffset("m4", "20161218", cal, sdf)));
//		assertEquals("20160701",sdf.format(SetExpressionAnalyzer.getDateByOffset("m5", "20161218", cal, sdf)));
//		assertEquals("20160601",sdf.format(SetExpressionAnalyzer.getDateByOffset("m6", "20161218", cal, sdf)));
//		assertEquals("20160501",sdf.format(SetExpressionAnalyzer.getDateByOffset("m7", "20161218", cal, sdf)));
//		assertEquals("20160401",sdf.format(SetExpressionAnalyzer.getDateByOffset("m8", "20161218", cal, sdf)));
//		assertEquals("20160301",sdf.format(SetExpressionAnalyzer.getDateByOffset("m9", "20161218", cal, sdf)));
//		assertEquals("20160201",sdf.format(SetExpressionAnalyzer.getDateByOffset("m10", "20161218", cal, sdf)));
//		assertEquals("20160101",sdf.format(SetExpressionAnalyzer.getDateByOffset("m11", "20161218", cal, sdf)));
//		assertEquals("20151201",sdf.format(SetExpressionAnalyzer.getDateByOffset("m12", "20161218", cal, sdf)));
//		assertEquals("20151101",sdf.format(SetExpressionAnalyzer.getDateByOffset("m13", "20161218", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170113", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170114", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170115", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170116", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170117", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170118", cal, sdf)));
//		assertEquals("20170113",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170119", cal, sdf)));
//		assertEquals("20170120",sdf.format(SetExpressionAnalyzer.getDateByOffset("v0", "20170120", cal, sdf)));
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void testGetDateByOffsetForException() throws ParseException{
//		assertEquals("20161219",sdf.format(SetExpressionAnalyzer.getDateByOffset("d-1", "20161218", cal, sdf)));
//	}
//	
//	@Test
//	public void testGetAllDate() throws ParseException{
//		assertArrayEquals(new String[]{"20161201"}, SetExpressionAnalyzer.getAllDate("d0", "20161201"));
//		assertArrayEquals(new String[]{"20161201"}, SetExpressionAnalyzer.getAllDate("d0", "20161201"));
//	}
}

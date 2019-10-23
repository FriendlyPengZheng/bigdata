package test.taomee.bigdata.lib;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.AfterClass;
import org.junit.Test;

import com.taomee.bigdata.lib.Utils;

public class TestUtils {

	@Test
	public void testGetOutputDirPathByTaskID() throws ParseException {
		assertEquals("/bigdata/output/sum/20170102/1_artifact",Utils.getOutputDirPathByTaskID("1", "20170105"));
		assertEquals("/bigdata/output/sum/20170101/2_artifact",Utils.getOutputDirPathByTaskID("2", "20170105"));
		assertEquals("/bigdata/output/sum/20170105/7_artifact",Utils.getOutputDirPathByTaskID("7", "20170105"));
		assertEquals("/bigdata/output/day/20170105/10_artifact",Utils.getOutputDirPathByTaskID("10", "20170105"));
		assertEquals("/bigdata/output/week/20170102/20_artifact",Utils.getOutputDirPathByTaskID("20", "20170105"));
		assertEquals("/bigdata/output/month/20170101/22_artifact",Utils.getOutputDirPathByTaskID("22", "20170105"));
		assertEquals("/bigdata/output/sum/20170101/23_artifact",Utils.getOutputDirPathByTaskID("23", "20170105"));
		assertEquals("/bigdata/output/sum/20170102/24_artifact",Utils.getOutputDirPathByTaskID("24", "20170105"));
		
	}


	@Test
	public void testGetAllPreTask(){
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(1));
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(2));
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(7));
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(10));
		assertArrayEquals(new int[]{0,10}, Utils.getAllPreTask(11));
		assertArrayEquals(new int[]{0,12}, Utils.getAllPreTask(13));
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(16));
		assertArrayEquals(new int[]{0,16}, Utils.getAllPreTask(17));
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(18));
		assertArrayEquals(new int[]{0,18}, Utils.getAllPreTask(19));
		assertArrayEquals(new int[]{0,18}, Utils.getAllPreTask(20));
		assertArrayEquals(new int[]{0,18,20}, Utils.getAllPreTask(21));
		assertArrayEquals(new int[]{0,18,22}, Utils.getAllPreTask(23));
		assertArrayEquals(new int[]{0,56,57}, Utils.getAllPreTask(58));
		assertArrayEquals(new int[]{0}, Utils.getAllPreTask(69));
		assertArrayEquals(new int[]{0,69}, Utils.getAllPreTask(70));
		assertArrayEquals(new int[]{0,71}, Utils.getAllPreTask(72));
		assertArrayEquals(new int[]{0,43}, Utils.getAllPreTask(73));
		assertArrayEquals(new int[]{0,43,73}, Utils.getAllPreTask(74));
		assertArrayEquals(new int[]{0,56}, Utils.getAllPreTask(75));
		assertArrayEquals(new int[]{0,18,140}, Utils.getAllPreTask(141));
		assertArrayEquals(new int[]{87,0,18,20}, Utils.getAllPreTask(142));
		assertArrayEquals(new int[]{87,0,18,20,142}, Utils.getAllPreTask(143));
		assertArrayEquals(new int[]{88,0,18,22}, Utils.getAllPreTask(144));
		assertArrayEquals(new int[]{88,0,18,22,144}, Utils.getAllPreTask(145));
		
	}
}

package com.taomee.tms.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.taomee.bigdata.lib.SetExpressionAnalyzer;
/**
 * 
 * @author looper
 * @date 2017年5月17日 下午3:29:39
 * @project tms-hdfsFetchData TestExpress
 */
public class TestExpress {
	
	@Test
	public void test1()
	{
		SetExpressionAnalyzer exp = new SetExpressionAnalyzer();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			
			//Date date = exp.getDateByOffset("d7", "20170518");
			
			//System.out.println(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

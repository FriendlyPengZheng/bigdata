package com.taomee.tms.test2;

import java.text.SimpleDateFormat;
import java.util.Date;




public class TestDateUtils {
	
	public static void main(String[] args) {
		
		System.out.println(System.currentTimeMillis());
		Date date = new Date(1503298800);
		/*int abc = date.getDay();
		System.out.println(abc);*/
		/*int hour = date.getHours();
		System.out.println(hour);*/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s = sdf.format(date);
		System.out.println(s);
	}

}

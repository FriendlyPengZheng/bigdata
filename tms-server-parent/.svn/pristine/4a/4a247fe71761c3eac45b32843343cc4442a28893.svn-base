package com.taomee.tms.storm.realtime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRedisStoreFunction {
	public static void main(String[] args){
		Pattern pattern =  Pattern.compile("([0-9\\-]+) ([\\d]{2}):([\\d]{2})(.*)");
		Matcher matcher = pattern.matcher("2017-01-22_00:01:01_14_10_");
		
		System.out.println(matcher.group(1)+matcher.group(4));
	}
}

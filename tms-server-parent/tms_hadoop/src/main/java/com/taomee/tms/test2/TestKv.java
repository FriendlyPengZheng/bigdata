package com.taomee.tms.test2;

public class TestKv {
	
	public static void main(String[] args) {
		String log = "12\t13\t14";
		String[] kv = log.split("\t");
		for(String s : kv)
		{
			System.out.println(s);
		}
	}

}

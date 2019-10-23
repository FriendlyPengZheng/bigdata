package com.taomee.tms.utils;

public class TestWhile {
	
	public static void main(String[] args) {
		
		int index = 0;
		while(true)
		{
			if(index >10)
			{
				System.out.println("程序退出while循环:"+index+",步长");
				break;
			}
			else
			{
				index++;
				System.out.println("继续while循环:"+index+",步长");
				continue;
			}
		}
	}

}

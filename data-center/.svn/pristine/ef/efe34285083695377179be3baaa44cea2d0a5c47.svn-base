package com.taomee.bigdata.util;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

public class GetGameinfo
{
	//Set the single instance model
	private static GetGameinfo instance = null;
	private GetGameinfo(){}
	public static GetGameinfo getInstance()
	{
	  if(instance == null)
	  {   
		  instance = new GetGameinfo();
	  }   
	  return instance;
	}

	static public String gametable = null;
	//static public HashSet<String> gameid = new HashSet<String>();
	static public HashSet<Integer> gameid = new HashSet<Integer>();
	public void config(JobConf job) 
	{
	   	gametable = job.get("GameInfo");
		String[] gameinfo = gametable.split(",");
		for(int i = 0; i < gameinfo.length; i++) 
		{
			try {
		   		//Gameid is converted to int,prevent gameid lik 0123			
				gameid.add(Integer.valueOf(gameinfo[i]));
			} catch (java.lang.NumberFormatException e) { }
		}   
   	}
	public String getValue(String key) 
	{
		return gameid.contains(Integer.valueOf(key)) ? ("G" + key) : "Gother";
	}
}

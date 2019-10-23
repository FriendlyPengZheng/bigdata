package com.taomee.tms.storm.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import storm.trident.operation.*;
//import storm.trident.tuple.TridentTuple;

public class LoginLogoutSplitFunction extends BaseFunction {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(LoginLogoutSplitFunction.class);
	
	public LoginLogoutSplitFunction()
	{}
	
	// output <timestamp, cmd_return_status, return_code, account_id>
	public void execute(TridentTuple tuple, TridentCollector collector)
	{
		LOG.error("LoginLogoutSplitFunction recv: " + tuple.getString(0));
		String log_str = null;
		String timestamp = null;
		String cmd_id = null;
		String return_code = null;
		String account_id = null;
		
		if(tuple.size() > 0)
		{
			log_str = tuple.getString(0);
			
			String[] first_split = log_str.split(" ");
			if(first_split.length >= 8)
			{
			    timestamp = first_split[0];

			    String[] second_split = first_split[4].split(":");
			    if(second_split.length >= 6)
			    {
			    	cmd_id = second_split[3];
			    	account_id = second_split[4];
			    	return_code = second_split[5].split(",")[0];
			    	
			    	List<Object> val = new ArrayList<Object>();
			    	val.add(timestamp);
			    	// 时间（精确到分钟），命令号，返回值的组合，便于后面分组统计
			    	String min_str = TimeStamp2Date(timestamp, "yyyy-MM-dd_HH:mm");
			    	String ret_status = "OK";
			    	if(!return_code.equals("0"))
			    		ret_status = "ERROR";
			    	val.add(min_str + "|" + cmd_id + "|" + ret_status);
			    	val.add(return_code);
			    	val.add(account_id);
			    	
			    	collector.emit(val);
			    }
			}
		}
		else
			LOG.error("tuple size: " + tuple.size() + " <= 0.");
	}
	
	public String TimeStamp2Date(String timestampString, String formats){    
		  Long ts = Long.parseLong(timestampString)*1000;
		  SimpleDateFormat sd = new SimpleDateFormat(formats);
		  sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		  String date = sd.format(new java.util.Date(ts));    
		  return date;    
		}  
}

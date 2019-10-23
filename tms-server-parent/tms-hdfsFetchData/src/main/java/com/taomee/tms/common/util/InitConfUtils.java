package com.taomee.tms.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cheney
 * @date 2013-11-15
 */
public class InitConfUtils {
	
	static PropertiesHandle handle = new PropertiesHandle("/com/taomee/tms/common/util/init.properties");
	
	
	private InitConfUtils(){
		//禁止实例化
	}
	private static Map<String, PropertiesHandle> rt_handle = new ConcurrentHashMap<String, PropertiesHandle>();
	
	public static String getParamValue(String paramName)
	{
		String value = handle.getValue(paramName);
		//System.out.println(handle);
		//System.out.println();
		return value;
	}
	
	public static String getParamValue(String paramName,String defaultValue)
	{
		String value = handle.getValue(paramName);
		return "".equals(value)?defaultValue:value;
	}
	
	public static String getParamValueFromPath(String paramName, String name){
		PropertiesHandle handle = rt_handle.get(name);
		if(handle == null) handle = registe(name);
		return handle.getValue(paramName);
	}
	
	public synchronized static PropertiesHandle registe(String name){
		PropertiesHandle handle = rt_handle.get(name);
		if(handle == null){
			handle = new PropertiesHandle("/" + name);
			rt_handle.put(name, handle);
		}
		return handle;
	}
	
}

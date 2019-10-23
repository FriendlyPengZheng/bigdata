package com.taomee.tms.client;

import java.util.Random;

import com.taomee.tms.common.util.InitConfUtils;

/**
 * @author cheney
 * @date 2013-11-15
 */
public class PropertiesConf {

	public static String[] SOCKET_SERVER;
	public static int[] SOCKTE_PORT;
	
	static {
		String s = InitConfUtils.getParamValue("server.socket.host");
		String p = InitConfUtils.getParamValue("server.socket.port");
		SOCKET_SERVER = s.split(",");
		String[] t = p.split(",");
		SOCKTE_PORT = new int[t.length];
		for(int i = 0; i < t.length; i++){
			SOCKTE_PORT[i] = Integer.parseInt(t[i]);
		}
	}
	
	public static int get(){
		Random r = new Random();
		return r.nextInt(SOCKET_SERVER.length);
	}
		
}

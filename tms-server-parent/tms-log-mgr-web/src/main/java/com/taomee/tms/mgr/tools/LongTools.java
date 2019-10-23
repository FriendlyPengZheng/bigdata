package com.taomee.tms.mgr.tools;

public class LongTools {
	public static Long safeStringToLong(String param) {
		if(param == null || param.isEmpty()) {
			return 0L;
		}
		
		return Long.parseLong(param);
	}
}

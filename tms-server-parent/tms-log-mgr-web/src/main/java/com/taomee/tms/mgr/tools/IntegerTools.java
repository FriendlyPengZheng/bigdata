
package com.taomee.tms.mgr.tools;

public class IntegerTools{
	public static int safeStringToInt(String param)
	{
		int ret = 0;
		try {
			ret = Integer.parseInt(param);
		} catch (NumberFormatException e) {
			ret = 0;
		}
		return ret;
	}
}
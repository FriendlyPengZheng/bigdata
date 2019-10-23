package com.taomee.tms.mgr.tools;

public class DoubleTools {
	public static Double safeStringToDouble(String param)
	{
		Double ret = 0.0;
		try {
			ret = Double.parseDouble(param);
		} catch (NumberFormatException e) {
			ret = 0.0;
		}
		return ret;
	}
}

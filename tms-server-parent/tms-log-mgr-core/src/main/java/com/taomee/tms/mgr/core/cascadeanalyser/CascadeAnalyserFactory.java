package com.taomee.tms.mgr.core.cascadeanalyser;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CascadeAnalyserFactory implements Serializable {
	private static final long serialVersionUID = -3390651898025642019L;
	private static final Logger LOG = LoggerFactory.getLogger(CascadeAnalyserFactory.class);

	public static BaseCascadeAnalyser createCascadeAnalyser(String strCascade) {
		/* 
		 * 暂时只处理以下类型：
		 * str_ip_to_province(_ip_)
		 * round(_amt_)
		 */
		if (strCascade == null) {
			LOG.error("param cascade is null!");
			return null;
		}
		
		// 只会包含字母数字下划线，等同于"[A-Za-z0-9_]"
		// 可以匹配"key1"或者"round(_amt_)"
		Pattern pattern = Pattern.compile("(\\w+)\\((\\w+)\\)|(\\w+)");
		Matcher matcher = pattern.matcher(strCascade);
		
		if (!matcher.find() || matcher.groupCount() != 3) {
			LOG.error("CascadeAnalyserFactory createCascadeAnalyser, invalid strCascade " + strCascade);
			return null;
		}
		
		String function = null;
		String keyField = null;
		if (matcher.group(1) != null && matcher.group(2) != null) {
			function = matcher.group(1);
			keyField = matcher.group(2);
		} else if (matcher.group(3) != null) {
			keyField = matcher.group(3);
			return new PlainCascadeAnalyser(keyField);
		} else {
			// 不可能到这里
			LOG.error("CascadeAnalyserFactory createCascadeAnalyser, invalid strCascade " + strCascade);
			return null;
		}
		
		switch (function) {
			case "str_ip_to_province":
				return new StrIpToProvinceCascadeAnalyser(keyField);
			
			case "round":
				return new RoundCascadeAnalyser(keyField);
			
			default:
				LOG.error("CascadeAnalyserFactory createCascadeAnalyser, invalid function " + function);
				return null;
		}
	}	
	
	public static void main(String[] args) {
//		Pattern pattern = Pattern.compile("(\\w+)\\((\\w*)\\)|(\\w+)");
		Pattern pattern = Pattern.compile("(\\w+)\\((\\w+)\\)|(\\w+)");
//		Matcher matcher = pattern.matcher("distinct_count(key1)");
//		Matcher matcher = pattern.matcher("count()");
		Matcher matcher = pattern.matcher("key1");
		
		while (matcher.find()) {
			System.out.println("groupCount " + matcher.groupCount());
			for (int i = 0; i <= matcher.groupCount(); ++i) {
				System.out.println("--" + matcher.group(i) + "--");
			}
		}
	}
}

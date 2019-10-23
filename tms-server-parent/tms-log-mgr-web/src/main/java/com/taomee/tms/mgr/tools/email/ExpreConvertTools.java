package com.taomee.tms.mgr.tools.email;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.dubbo.config.annotation.Reference;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.ResultInfo;
import com.taomee.tms.mgr.tools.ExprProcessTools;

public class ExpreConvertTools {
	
	public static Double getValueByExpre(List<Double> dataValueList, String expre) {
		String caculateExpre = ExpreConvertTools.replaceExprs(dataValueList, expre);
		
		Double result = ExprProcessTools.convertToSuffixExpression(caculateExpre);
		BigDecimal b = new BigDecimal(result);
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	private static String replaceExprs(List<Double> data, String expre) {
		/*Map<Integer, Double> data = new HashMap<Integer, Double>();
		data.put(0, 10.0);
		data.put(1, 21.0);*/
		
		String regEx = "\\{\\d\\}";
		Pattern pattern = Pattern.compile(regEx);
		String patternsString = expre;
		Matcher matcher = pattern.matcher(expre);
		boolean result = matcher.find();
		
		StringBuffer sb = new StringBuffer();
		int index = 0;
		while(result) {
			index = Integer.parseInt(patternsString.substring(matcher.start()+1, matcher.end()-1));
			java.text.NumberFormat nf = java.text.NumberFormat.getInstance(); 
			nf.setGroupingUsed(false);
			matcher.appendReplacement(sb, nf.format(data.get(index)));
			result = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

}

package com.taomee.bigdata.tms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewSetExpressionAnalyzer {
	private int flagNum = 0;
	private String setExpressionInProcess; 
	private String convertedExpressionInProcess;
	private LinkedHashMap<String, Integer> setMap = new LinkedHashMap<String,Integer>();
	
	public NewSetExpressionAnalyzer(){
		
	}
	
	/**
	 * 对传入的类似"m_2[d3]∩m_3[d7]"的集合计算表达式进行解析，解析后可以通过get方法返回该表达式的相关要素
	 * @param setExpression 集合计算表达式
	 */
	public NewSetExpressionAnalyzer analysis(String setExpression){
		analysisAndConvert(setExpression);
		return this;
	}
	
	/**
	 * 对传入的类似"m_2[d3]∩m_3[d7]"的集合计算表达式进行解析，并返回转换后供MR集合运算使用的类似"inSet0&&inSet1"的真值表达式
	 * @param setExpression 集合计算表达式
	 */
	public String analysisAndConvert(String setExpression){
		if(setExpressionInProcess != null){
			this.reset();
		}
		this.setExpressionInProcess = setExpression;
		convertedExpressionInProcess = convertExpression(this.setExpressionInProcess);
		return convertedExpressionInProcess;
	}
	
	/**
	 * 得到转换后供MR集合运算使用的类似"inSet0&&inSet1"的真值表达式，执行此方法前必须先执行analysis或analysisAndConvert对表达式进行解析
	 */
	public String getConvertedExpression(){
		if(this.setExpressionInProcess == null){
			throw new RuntimeException("no valid Set Expression input!");
		}else{
			return this.convertedExpressionInProcess.toString();
		}
	}
	
	/**
	 * 返回该集合运算表达式中不同集合的个数，例如
	 * "A_2[d3]-B_1[d1]∩A_2[d4]"将返回3，而
	 * "A_2[d3]-B_1[d1]∩A_2[d3]"将返回2
	 */
	public int getNumOfSet(){
		if(this.setExpressionInProcess == null){
			throw new RuntimeException("no valid Set Expression input!");
		}else{
			return this.setMap.size();
		}
	}
	
	/**
	 * 返回该集合运算表达式中各个集合及对应的整数flag（以0为起始，从左至右递增）的map
	 */
	public Map<String, Integer> getSetMap(){
		if(this.setExpressionInProcess == null){
			throw new RuntimeException("no valid Set Expression input!");
		}else{
			return this.setMap;
		}
	}
	
	private void reset(){
		this.flagNum=0;
		this.setExpressionInProcess = null;
		this.setMap.clear();
	}
	
	/**
	 * 对集合运算表达式进行解析，解析过程中对相关域进行赋值
	 */
	private String convertExpression(String str) {
		int j,k = 0;
		StringBuilder convertedExp = new StringBuilder("");
		for(int i=0;i<=str.length()-1;){
			char c = str.charAt(i);
			if( c== '('	||c == ')'){
				convertedExp.append(c);
				i++;
			}else if(c == '∩' || c == '∪'){
				convertedExp.append(c=='∩'?"&&":"||");
				i++;
			}else if(c != '-'){
				j=i;
				k=str.indexOf(']',j);
				String set = str.substring(j, k+1);
				if(!this.setMap.containsKey(set)){
					setMap.put(set, flagNum);
					flagNum++;
				}
				convertedExp.append("inSet"+setMap.get(set));
				i=k;
				i++;
			}else{
				convertedExp.append("&&(!");
				k=findEndIndexOfSubExpression(str, i+1);
				convertedExp.append(convertExpression(str.substring(i+1, k+1)));
				convertedExp.append(")");
				i=k;
				i++;
			}
		}
		return convertedExp.toString();
	}
	
	private int findEndIndexOfBracket(String str,int startIndex){
		int i = startIndex;
		int count = 1;
		while(count != 0){
			char c = str.charAt(++i);
			if(c == '('){
				count++;
			}
			if(c == ')'){
				count--;
			}
		}
		return i;
	}
	
	private int findEndIndexOfSubExpression(String str,int startIndex){
		int i = startIndex;
		while(i<=str.length()-1){
			char c = str.charAt(i);
			if(c == '('){
				i = findEndIndexOfBracket(str, i)+1;
			}else if(c == '-'){
				break;
			}else{
				i++;
			}
		}
		return i-1;
	}
	
	/**
	 * 由类似m_id[]的集合表达式返回所有路径字符串
	 * @param exp 集合表达式
	 * @param date 当日日期
	 * @throws ParseException
	 */
	public static String[] getInputPathsBySetExpression(String exp, String date)
			throws ParseException {
		String type = exp.charAt(0) == 'a' ? "artifact" : "material";
		int ID = Integer.valueOf(exp.substring(exp.indexOf("_") + 1,exp.indexOf("[")));
		String[] dates = getAllDate(
				exp.substring(exp.indexOf('[')+1, exp.indexOf(']')), date);
		String[] paths = new String[dates.length];
		for (int i = 0; i <= dates.length - 1; i++) {
			paths[i] = "/bigdata/output/day/"+dates[i]+ "/basic/" + ID
					+ "_"+type+"*";
		}
		return paths;
	}
	
	/**
	 * 由集合表达式"[]"内的偏移量表达式offsetExp和给定日期date返回所有日期字符串
	 * @param offsetExp 偏移量表达式
	 * @param 偏移量相对应的日期
	 */
	private static String[] getAllDate(String offsetExp, String date)
			throws ParseException {
		LinkedList<String> dates = new LinkedList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		cal.setTime(sdf.parse(date));

		String[] periods = offsetExp.split("\\|");
		for (String period : periods) {
			String startOffset,endOffset;
			String[] startEnd = period.split(",");
			if(startEnd.length == 1){
				char type = startEnd[0].charAt(0);
				if(type == 'M'||type == 'W'){
					cal.setTime(sdf.parse(date));
					int num = Integer.valueOf(startEnd[0].substring(1));
					Date firstDay = cal.getTime();
					Date lastDay = cal.getTime();
					if(type == 'M'){
						cal.add(Calendar.MONTH, -1*num);
						cal.set(Calendar.DAY_OF_MONTH,1);
						firstDay = cal.getTime();
						cal.add(Calendar.MONTH,1);
						cal.add(Calendar.DATE,-1);
						lastDay = cal.getTime();
					}
					if(type == 'W'){
						cal.add(Calendar.DATE,-7*num);
						cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
						firstDay = cal.getTime();
						cal.add(Calendar.DATE,6);
						lastDay = cal.getTime();
					}
					cal.setTime(firstDay);
					for(Date d = firstDay;d.before(lastDay)||d.equals(lastDay);){
						dates.add(sdf.format(d));
						cal.add(Calendar.DATE,1);
						d = cal.getTime();
					}
					continue;
				}else{
					startOffset = endOffset = startEnd[0];
				}
			}else{
				startOffset = startEnd[0];
				endOffset = startEnd[1];
			}
			Date startDate = getDateByOffset(startOffset, date, cal, sdf);
			Date endDate = getDateByOffset(endOffset, date, cal, sdf);
			cal.setTime(startDate);
			for (Date d = startDate; d.before(endDate) || d.equals(endDate);) {
				dates.add(sdf.format(d));
				cal.add(Calendar.DATE, +1);
				d = cal.getTime();
			}
		}
		return dates.toArray(new String[0]);
	}

	/**
	 * 返回指定日期date相对offset偏移量的日期字符串
	 * @param offset 偏移量
	 * @param date 偏移量相对应的日期
	 * @throws ParseException
	 */
	private static Date getDateByOffset(String offset, String date,
			Calendar cal, SimpleDateFormat sdf) throws ParseException {
		char type = offset.charAt(0);
		cal.setTime(sdf.parse(date));
		if (type == 'd') {
			int num = Integer.valueOf(offset.substring(1));
			cal.add(Calendar.DATE, -1 * num);
		}else if (type == 'm') {
			int num = Integer.valueOf(offset.substring(1));
			cal.add(Calendar.MONTH, -1 * num);
			cal.set(Calendar.DATE, 1);
		}else if (type == 'w') {
			int num = Integer.valueOf(offset.substring(1));
			cal.add(Calendar.DATE, -7 * num);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}else{
			return sdf.parse(offset);
		}
		return cal.getTime();
	}
	
	public static void main(String[] args){
		NewSetExpressionAnalyzer analyzer = new NewSetExpressionAnalyzer();
		System.out.println(analyzer.analysis("[0]∩[1]-[2]").getSetMap());
		System.out.println();
	}
}

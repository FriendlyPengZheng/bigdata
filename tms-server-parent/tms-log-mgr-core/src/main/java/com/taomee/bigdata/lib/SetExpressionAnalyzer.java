package com.taomee.bigdata.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.taomee.tms.mgr.entity.ArtifactInfo;


public class SetExpressionAnalyzer {
	private String setExpressionInProcess; 
	private String convertedExpressionInProcess;
	private LinkedHashMap<String, Integer> setMap = new LinkedHashMap<String,Integer>();
	private int flagNum = 0;
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	
	public SetExpressionAnalyzer(){
		
	}
	
	public SetExpressionAnalyzer(String setExpression){
		analysisAndConvert(setExpression);
	}
	
	/**
	 * 对传入的类似"m_2[d3]∩m_3[d7]"的集合计算表达式进行解析，解析后可以通过get方法返回该表达式的相关要素
	 * @param setExpression 集合计算表达式
	 */
	public SetExpressionAnalyzer analysis(String setExpression){
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
		convertedExpressionInProcess = convertExpression(setExpressionInProcess);
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
	 * 以set形式返回此集合交并差表达式中的所有集合
	 */
	public Set<String> getAllExp(){
		if(this.setExpressionInProcess == null){
			throw new RuntimeException("no valid Set Expression input!");
		}else{
			return this.setMap.keySet();
		}
	}
	
	/**
	 * 返回该集合交并差表达式中的一个集合exp对应的flagNum
	 */
	public int getExpFlag(String exp){
		if(this.setExpressionInProcess == null){
			throw new RuntimeException("no valid Set Expression input!");
		}else{
			return this.setMap.get(exp);
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
	 * @param gameID 游戏ID,*表示所有游戏,
	 * @throws ParseException
	 */
/*	public static String[] getInputPathsBySetExpression(String exps, String date ,String gameID)
			throws ParseException {
		List<String> paths = new LinkedList<String>();
		String[] expStrs = exps.split("、");
		for(String exp:expStrs){
			if(exp.startsWith("path")){
				paths.add(exp.substring(5, exp.length()-1));
			}else{
				if(exp == null||exp.length()==0){
					throw new IllegalArgumentException("set expression can not be null!");
				}
				if(date == null||date.length()==0){
					throw new IllegalArgumentException("date can not be null!");
				}
				if(!exp.contains("_")){
					throw new IllegalArgumentException("wrong set expression!");
				}
				
				String type = exp.split("_")[0];
				
				int ID = Integer.valueOf(exp.substring(exp.indexOf("_") + 1,exp.indexOf("[")));
				
				String[] dates = getAllDate(
						exp.substring(exp.indexOf('[')+1, exp.indexOf(']')), date);
				
				String[] games = gameID.split(",");
				String[] gameStrs = new String[games.length];
				if(gameID.equals("*")||gameID.equals("")){
					gameStrs[0] = "*";
				}else{
					for(int i =0;i<=games.length-1;i++){
						gameStrs[i] = "G"+Integer.valueOf(games[i])+"-*";
					}
				}
				
				String[] pathStrs = new String[dates.length*gameStrs.length];
				
				int i = 0;
				for (String datestr:dates) {
					for(String gameStr:gameStrs){
						if(type.equals("m")){
							pathStrs[i++] = "/bigdata/output/day/"+datestr+ "/basic/" + ID + "_material"+gameStr;
						}else if(type.equals("a")){
							ArtifactInfo artifact = Utils.getArtifactInfoByArtifactID(ID);
							int timeType = artifact.getPeriod();
							int isSum = artifact.getResult();
							pathStrs[i++] = Utils.spellArtifactOutputPathBy(ID, timeType, isSum, datestr)+"/part"+gameStr;
						}else{
							throw new IllegalArgumentException("wrong set expression!");
						}
					}
				}
				paths.addAll(Arrays.asList(pathStrs));
			}
		}
		return paths.toArray(new String[]{});
	}
	*/
	/**
	 * 由集合表达式"[]"内的偏移量表达式offsetExp和给定日期date返回所有日期字符串
	 * @param offsetExp 偏移量表达式
	 * @param 偏移量相对应的日期
	 */
	public static String[] getAllDate(String offsetExp, String date)
			throws ParseException {
		if(offsetExp == null||offsetExp.length() ==0){
			throw new IllegalArgumentException("wrong offset expression!");
		}
		if(date == null||date.length()==0){
			throw new IllegalArgumentException("date can not be null!");
		}
		LinkedList<String> dates = new LinkedList<String>();
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		cal.setTime(sdf.parse(date));
		
		String[] periods = offsetExp.split("\\|");
		for (String period : periods) {
			if(period.equals("")){
				throw new IllegalArgumentException("wrong offset expression!");
			}
			String startOffset,endOffset;
			String[] startEnd = period.split(",");
			if(startEnd.length>2){
				throw new IllegalArgumentException("wrong offset expression!");
			}else if(startEnd.length == 2){
				if(startEnd[0].equals("")){
					throw new IllegalArgumentException("wrong offset expression!");
				}
				char type = startEnd[0].charAt(0);
				if(type != 'm' && type != 'w' && type != 'd'){
					throw new IllegalArgumentException("wrong offset expression!");
				}
				startOffset = startEnd[0];
				endOffset = startEnd[1];
			}else if(startEnd.length == 1){
				if(period.endsWith(",")){
					throw new IllegalArgumentException("wrong offset expression!");
				}
				char type = startEnd[0].charAt(0);
				if(type == 'M'||type == 'W'||type == 'V'){
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
					}else if(type == 'W'){
						cal.add(Calendar.DATE,-7*num);
						cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
						firstDay = cal.getTime();
						cal.add(Calendar.DATE,6);
						lastDay = cal.getTime();
					}else if(type == 'V'){
						int week = cal.get(Calendar.DAY_OF_WEEK)-1;
						if(week>=6 && week <=7 || week==1){
							cal.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
							cal.add(Calendar.DATE, -7*num);
							firstDay=cal.getTime();
							cal.add(Calendar.DATE,6);
							lastDay=cal.getTime();
						}else if(week>=2 && week<=5){
							cal.add(Calendar.DATE,-7);
							cal.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
							cal.add(Calendar.DATE, -7*num);
							firstDay=cal.getTime();
							cal.add(Calendar.DATE,6);
							lastDay=cal.getTime();
						}
					}
					cal.setTime(firstDay);
					for(Date d = firstDay;d.before(lastDay)||d.equals(lastDay);){
						dates.add(sdf.format(d));
						cal.add(Calendar.DATE,1);
						d = cal.getTime();
					}
					continue;
				}else if (type == 'm'||type == 'w'||type == 'd'||type == 'v'){
					startOffset = endOffset = startEnd[0];
				}else{
					throw new IllegalArgumentException("wrong offset expression!");
				}
			}else{
				throw new IllegalArgumentException("wrong offset expression!");
			}
			Date startDate = getDateByOffset(startOffset, date, cal, sdf);
			Date endDate = getDateByOffset(endOffset, date, cal, sdf);
			if(startDate.after(endDate)){
				throw new IllegalArgumentException("wrong offset expression!");
			}
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
	 * 由指定日期date和日偏移量offset返回单个相应日期
	 * @param offset 偏移量
	 * @param date 偏移量相对应的日期
	 * @throws ParseException
	 */
	public static Date getDateByOffset(String offset, String date,
			Calendar cal, SimpleDateFormat sdf) throws ParseException {
		if(offset==null||offset.length()==0){
			throw new IllegalArgumentException("wrong offset expression!");
		}
		char type = offset.charAt(0);
		cal.setTime(sdf.parse(date));
		if (type == 'd') {
			if(offset.charAt(1) == '-'){
				throw new IllegalArgumentException("offset must not contain \'-\' character");
			}
			int num = Integer.valueOf(offset.substring(1));
			cal.add(Calendar.DATE, -1 * num);
		}else if (type == 'm') {
			if(offset.charAt(1) == '-'){
				throw new IllegalArgumentException("offset must not contain \'-\' character");
			}
			int num = Integer.valueOf(offset.substring(1));
			cal.add(Calendar.MONTH, -1 * num);
			cal.set(Calendar.DATE, 1);
		}else if (type == 'w') {
			if(offset.charAt(1) == '-'){
				throw new IllegalArgumentException("offset must not contain \'-\' character");
			}
			int num = Integer.valueOf(offset.substring(1));
			cal.add(Calendar.DATE, -7 * num);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}else if (type == 'v') {
			if(offset.charAt(1) == '-'){
				throw new IllegalArgumentException("offset must not contain \'-\' character");
			}
			int week = cal.get(Calendar.DAY_OF_WEEK);
			int num = Integer.valueOf(offset.substring(1));
			if(week>=6 && week <=7 || week==1){
				cal.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
				cal.add(Calendar.DATE, -7*num);
			}else if(week>=2 && week<=5){
				cal.add(Calendar.DATE,-7);
				cal.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
				cal.add(Calendar.DATE, -7*num);
			}
		}else{
			return sdf.parse(offset);
		}
		return cal.getTime();
	}
	
	public static String getDateByOffset(String offset,String date) throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		return sdf1.format(getDateByOffset(offset,date,cal,sdf1));
	}
	
	public static void main(String[] args){
		SetExpressionAnalyzer analyzer = new SetExpressionAnalyzer();
		System.out.println();
	}
}

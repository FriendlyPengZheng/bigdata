package com.taomee.tms.mgr.core.opanalyser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpAnalyserFactory implements Serializable {
	private static final long serialVersionUID = 2118419945316797292L;
	private static final Logger LOG = LoggerFactory.getLogger(OpAnalyserFactory.class);

	public static BaseOpAnalyser createOpAnalyser(String strOp) {
		/* 提取出op和key，
		 * 如distinct_count(key1)提取出op="distinct_count"，key="key1"
		 * 或count()提取出op="count"，key=""
		 * 暂时只处理以下类型：
		 * 1	material()
		 * 2	count()
		 * 3	distinct_count(key)
		 * 4	sum(key)
		 * 5	max(key)
		 * 6	min(key)
		 * 7	assign(key)
		 */
		if (strOp == null) {
			LOG.error("OpAnalyserFactory createOpAnalyser, parm op null");
			return null;
		}
		
		// 只会包含字母数字下划线，"\w"等同于"[A-Za-z0-9_]"，不能有空格
		// 暂时支持一个函数的表达式，如material(field1,field2)
		// 不支持多个函数，如groupbycount(key1,key2).distr()
		
		//之前的这种方式不能匹配中文字符,改成现在这种方式，是匹配所以字符。
		//Pattern pattern = Pattern.compile("(\\w+)\\(([A-Za-z0-9_,]*)\\)");
		Pattern pattern = Pattern.compile("(\\w+)\\(([\\s\\S]*)\\)");
		Matcher matcher = pattern.matcher(strOp);
		
		if (!matcher.find() || matcher.groupCount() != 2) {
			LOG.error("OpAnalyserFactory createOpAnalyser, invalid strOp [" + strOp + "]");
			return null;
		}
		
		// 由于正则表达式已做过匹配，op一定不为空（且不包含空字符）
		String op = matcher.group(1);
		
		List<String> keyFields = new ArrayList<String>();
		// 空字符串表示类似count()这种无参函数
		// 非空字符串中参数用","分隔
		if (!matcher.group(2).equals("")) {
			String[] strKeyFields = matcher.group(2).split(",");
			for (String strKeyField: strKeyFields) {
				if (strKeyField.length() == 0) {
					LOG.error("OpAnalyserFactory createOpAnalyser, invalid keys [" + matcher.group(2) +"]");
					return null;
				}
				keyFields.add(strKeyField);
			}
		}
		
		BaseOpAnalyser opAnalyser = null;
		switch (op) {
			case "material":
				opAnalyser = new MaterialOpAnalyser();
				break;
			case "count":
				opAnalyser = new CountOpAnalyser();
				break;
			case "distinct_count":
				opAnalyser = new DistinctCountOpAnalyser();
				break;
			case "sum":
				opAnalyser = new SumOpAnalyser();
				break;
			case "max":
				opAnalyser = new MaxOpAnalyser();
				break;
			case "min":
				opAnalyser = new MinOpAnalyser();
				break;
			case "assign":
				opAnalyser = new AssignOpAnalyser();
				break;
			default:
				LOG.error("OpAnalyserFactory createOpAnalyser, invalid op " + op);
				return null;
		}
		
		if (!opAnalyser.Init(keyFields)) {
			LOG.error("OpAnalyserFactory createOpAnalyser, opAnalyser Init failed");
			return null;
		}
		return opAnalyser;
	}	
	
	public static void main(String[] args) {
//		Pattern pattern = Pattern.compile("(\\w+)\\((\\w*)\\)");
		//Pattern pattern = Pattern.compile("(\\w+)\\(([A-Za-z0-9_,]*)\\)");
		Pattern pattern = Pattern.compile("(\\w+)\\(([\\s\\S]*)\\)");
//		Matcher matcher = pattern.matcher("distinct_count(key1)");
		Matcher matcher = pattern.matcher("sum(miniQ卫衣)");
		
		while (matcher.find()) {
			System.out.println("groupCount " + matcher.groupCount());
			for (int i = 0; i <= matcher.groupCount(); ++i) {
				System.out.println("--" + matcher.group(i) + "--");
			}
		}
		
//		Matcher m=p.matcher(“aaa2223bb”); 
//		m.find(); //匹配aaa2223 
//		m.groupCount(); //返回2,因为有2组 
//		m.start(1); //返回0 返回第一组匹配到的子字符串在字符串中的索引号 
//		m.start(2); //返回3 
//		m.end(1); //返回3 返回第一组匹配到的子字符串的最后一个字符在字符串中的索引位置. 
//		m.end(2); //返回7 
//		m.group(1); //返回aaa,返回第一组匹配到的子字符串 
//		m.group(2); //返回2223,返回第二组匹配到的子字符串
	}
}

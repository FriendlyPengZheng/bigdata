package com.taomee.tms.mgr.tools.DataCaculator;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.ctl.common.DataController;
import com.taomee.tms.mgr.entity.ResultInfo;
import com.taomee.tms.mgr.tools.ExprProcessTools;

/**
 * 
 * @author window
 * @brief 计算结果用于前端页面展示，最后对外只提供两个接口，获取数据和时间序列
 * 
 */

public class WeekCaculateDataTools extends AbstactCaculateDataTools{

	// private ArrayList<String> m_keyList;
	// private ArrayList<Map<String, Double>> m_resultList;
	// private List<Double> m_valueList;
	private String m_expr;
	private String m_exprName;// 显示项名称
	private Integer m_index;// 此expr的偏移
	private String m_fromDateString;
	private String m_toDateString;
	private HttpServletRequest m_request;
	// private LogMgrService m_logMgrService;
	private Integer m_precision; // 精度
	private String  m_unit; // 单位
	private LogMgrService m_logMgrService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DataController.class);
	
	/**
	 * @param expr
	 * @param expr的偏移
	 * @param 開始時間
	 * @param 結束時間
	 * @param request, httpSeveletRequest
	 * @param 調用db服務
	 */
	public WeekCaculateDataTools(String  expr, 
							    Integer index, // 此expr在exprList中的偏移
							    String fromDataString,
							    String toDateString,
							    HttpServletRequest request,
							    LogMgrService logMgrService
								) {
		
		m_fromDateString = fromDataString;
		m_toDateString = toDateString;
		m_expr = expr;
		// m_expr = "{0}";
		// System.out.println("expr: " + expr + "idx: " + index);
		m_index = index;
		m_request = request;
		// TODO 用@reference解决这个问题
		m_exprName = m_request.getParameter("expres[" + m_index + "][data_name]");
		// m_logMgrService = logMgrService;
		m_unit = m_request.getParameter("expres[" + m_index + "][unit]");
		String precisionString = m_request.getParameter("expres[" + m_index + "][precision]");
		// 需要控制判断，避免precision传过来为空
		m_precision = (precisionString == null || precisionString.isEmpty()) ? 0 : Integer.parseInt(precisionString);
		m_logMgrService = logMgrService;
	}
	
	
	/**
	 * @brief  求得前端展示所需要的数据
	 * @param  start time
	 * @param  to time
	 * @return list
	 */
	// TODO 公共方法
	@Override
	public List<String> getValueList(String fromDateString, String toStringDateString) {
		// assert fromDay < toDay
		List<String> keyList = genKeyList(fromDateString, toStringDateString);
		System.out.println("keyList: " + keyList);
		List<String> valueList = new ArrayList<String>(); 
 		List<Double> valueListTmp = getCaculatedResults(keyList);
		// 对valueList进行format
		// 遍历valueList
		for(Double valueTmp : valueListTmp) {
			String value = null;
			if (valueTmp != null) {
				value = valueTmp.toString();
				value = value.concat(m_unit);
			} else {
				value = null;
			}
			// System.out.println("value: " + value);
			valueList.add(value);
		}
		return valueList;
	}
	
	/**
	 * @brief 获取同比数
	 * @return list
	 */
	// TODO 公共方法
	@Override
	public List<String> getQoqValueList() {
		List<String> qoqValueList = new ArrayList<String>();
		List<Double> preValueList = null;
		List<Double> curValueList = null;
		// 获取时间序列
		List<String> keyList = genKeyList(m_fromDateString, m_toDateString);
		List<String> qoqKeyList = genQoqKeyList(keyList);
		// 通过keyList获取valueList
		// 环比数据
		preValueList = getCaculatedResults(qoqKeyList);	
		// 当前数据
		curValueList = getCaculatedResults(keyList);
		// 环比计算
		// 将valuelist 作为数据成员
		DecimalFormat df = new DecimalFormat("##.##%");
		Double qoqDateTmp = null;
		List<String> curKeyList = keyList;
		for (int i = 0; i < curKeyList.size(); ++i) {
			if (curValueList.get(i) != null && preValueList.get(i) != null) {
				qoqDateTmp = (curValueList.get(i) - preValueList.get(i))/preValueList.get(i);
				String qoqDate = df.format(qoqDateTmp);
				qoqValueList.add(qoqDate);
			} else {
				qoqValueList.add(null);
			}
		}
		return qoqValueList; 
	}
	/**
	 * @brief  获取环比数据
	 * @return list
	 */
	// TODO 公共方法
	@Override
	public List<String> getYoyValueList() {
		List<String> yoyValueList = new ArrayList<String>();
		List<Double> preValueList = null;
		List<Double> curValueList = null;
		// 获取时间序列
		List<String> keyList = genKeyList(m_fromDateString, m_toDateString);
		List<String> yoyKeyList = genYoyKeyList(keyList);
		// 通过keyList获取valueList
		// ArrayList<Map<String, Double>> curResultList = preDataList(keyList);
		// 获取同比数据
		// ArrayList<Map<String, Double>> yoyResultList = preDataList(yoyKeyList);
		// 同比数据
		preValueList = getCaculatedResults(yoyKeyList);
		// 当前数据
		curValueList = getCaculatedResults(keyList);
		DecimalFormat df = new DecimalFormat("##.##%");
		Double yoyDateTmp = null;
		List<String> curKeyList = keyList;
		for (int i = 0; i < curKeyList.size(); ++i) {
			if (curValueList.get(i) != null && preValueList.get(i) != null) {
				yoyDateTmp = (curValueList.get(i) - preValueList.get(i))/curValueList.get(i);
				// format
				String yoyDate = df.format(yoyDateTmp);
				yoyValueList.add(yoyDate);
			} else {
				yoyValueList.add(null);
			}
		}
		return yoyValueList;
	}
	

	/**
	 * @brief 返回keyList接口,此接口可以对外
	 * @return list
	 */
	// 此方法需要各自实现
	@Override
	public List<String> getKeyList() {
		// 对keyList进行格式化
		List<String> keyList = genKeyList(m_fromDateString, m_toDateString);
		//TODO 遍历keyList 对Key进行forman
		for (int i = 0; i < keyList.size(); ++i) {
			String tmpKey = CaculateDataTools.offsetDateString(keyList.get(i), 6);
				keyList.set(i, keyList.get(i).concat("~").concat(tmpKey));
		}
		
		return keyList;
	}
	
	/**
	 * @brief 获取expr的数据名称
	 */
	public String getExprName() {
		return m_exprName;
	}
	
	/**
	 * @brief 解析表达式，依据表达式准备resultList，同比环比也会从里面获取数据，相关result会进行追加,即完善result
	 * @brief 获取数据需要有一定的余量
	 */	
	@Override
	protected ArrayList<Map<String, Double>> preDataList(List<String> keyList) {
		// 遍历index, 取出数据
		// 时间序列数据fff
		ArrayList<Map<String, Double>> resultList = new ArrayList<Map<String,Double>>();
		ArrayList<Integer> indexList;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if ((indexList = parseExpr()) == null) {
			// return;
			return null;
		}
		for (int i = 0; i < indexList.size(); ++i) {
			Map<String, Double> tmpMap = new HashMap<String, Double>();
			List<ResultInfo> resultInfos = null;
			// 获取数据,这里间接和数据库交互，拉取数据
			// 这些都是dataInfo级别的数据
			// System.out.println("index.size" + indexList.size());
			try {
				// 获取dataId
				String dataIdString = m_request.getParameter("data_info[" + indexList.get(i) + "][data_id]");
				dataIdString = (dataIdString == null) ? "-1" : dataIdString;
				Integer dataId = Integer.parseInt(dataIdString);
	
				// 获取时间维度
				String periodString = m_request.getParameter("data_info[" + indexList.get(i) + "][period]");
				periodString = (periodString == null) ? "0" : periodString;
				// 此period为请求级别的参数，需要特别关注
				// 前端处理的period - 1 = 后台处理的period
				Integer period = Integer.parseInt(periodString) - 1;
				// 获取factor, 考虑factor 没有设置的情况
				String factorString = m_request.getParameter("data_info[" + indexList.get(i) + "][factor]");
				factorString = (factorString == null) ? "1" : factorString;
				Double factor = Double.parseDouble(factorString);
	
				// 周测试，暂时先写死
				// resultInfos = m_logMgrService.getValueInfo(dataId , serverId, keyList.get(0), keyList.get(keyList.size() - 1), 1);
				// 时间区间需要重置，周数据处理有些差异
				String fromDateString = keyList.get(0);
				String toDateString = keyList.get(keyList.size() - 1);
				// 测试周数据
				String serverIdString = m_request.getParameter("data_info[" + indexList.get(i) + "][server_id]");
				Integer serverId = null;
				if (serverIdString == null || serverIdString.isEmpty()) {
					String gameIdString = m_request.getParameter("game_id");
					String platformIdString = m_request.getParameter("platform_id");
					String zondServerId = m_request.getParameter("ZS_id");
					
					if(gameIdString == null || gameIdString.isEmpty()) {
						logger.error("Get serverId failed");
						return null;
					}
					
					Integer gameId = Integer.parseInt(gameIdString);
					Integer platformId = (platformIdString == null || platformIdString.isEmpty())? -1 : Integer.parseInt(platformIdString);
					zondServerId = (zondServerId == null || zondServerId.isEmpty())? "-1_-1" : zondServerId;
					// 进行结果相关的计算，需要调用接口获取serverId
					serverId = getServerId(gameId, platformId, zondServerId, m_logMgrService);
				} else {
					serverId = Integer.parseInt(serverIdString);
				}
	
				if (serverId == -1 || dataId == -1) {
					logger.error("param error");
					return null;
				}
				resultInfos = m_logMgrService.getValueInfo(dataId, serverId, fromDateString, toDateString, period);
				if (resultInfos == null || resultInfos.size() == 0) {
					return null;
				}
				// 得到时间序列的map
				for(int j = 0; j < keyList.size(); ++j) {
					for(int k = 0; k < resultInfos.size(); ++k) { 
						if (keyList.get(j).equals(sdf.format(resultInfos.get(k).getTime() * 1000))) {
							try {
								tmpMap.put(keyList.get(j), resultInfos.get(k).getValue() * factor);
							} catch (NullPointerException e) {
								e.printStackTrace();
								return null;
							}
						}
					}
				}
				
				// test print map
				// should focus
				resultList.add(i, tmpMap);
				
			} catch (NullPointerException e) {
				e.printStackTrace();	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				logger.error("param error" + e.getMessage());
			}
		}
		// System.out.println("resultlist :" + resultList);
		return resultList;
		
		
	}
	
	/**
	 * @brief  获取前端需要的数据，对内接口,同比环比也会调用调用此接口
	 * @param  时间序列
	 * @return list，经过表达式产生的数据
	 */
	@Override
	protected List<Double> getCaculatedResults(List<String> keyList) {
		// 计算resultList,再计算
		List<Double> valueListNull = new ArrayList<Double>();
		for (int i = 0; i < keyList.size(); ++i) {
			valueListNull.add(null);
		}
		List<Double> results = new ArrayList<Double>();
		
		ArrayList<Map<String, Double>> resultList = preDataList(keyList);
		if (resultList == null) {
			return valueListNull; 
		}
		System.out.println("resultList: " + resultList);
		// TODO null值校验
		List<Integer> indexList = parseExpr();
		// assert m_result.size 需要 和 index.size 相等
	
		if (indexList.size() == resultList.size() ) {
			for (int i = 0; i < keyList.size(); ++i) {
				// 遍历表达式中的index
				Map<Integer, Double> tmpData = new HashMap<Integer, Double>();
				Double value = 0.0;
				try {
					for (int j = 0; j < indexList.size(); ++j) {
						// 找值，处理没有值的情况
						if (resultList.get(j).containsKey(keyList.get(i))) {
							Double tmpValue = 0.0;
							// 过滤null
							if (((tmpValue = (resultList.get(j).get(keyList.get(i)))) == null)) {
								// 不参与计算
								value = null;
								continue;
							}
							// System.out.println("tmpValue :" + tmpValue);
							tmpData.put(indexList.get(j), tmpValue);
						} else {
							// 处理没有值的情况
							value = null;
						}
					}
					if (!tmpData.isEmpty() && tmpData.size() == indexList.size()) { 
						String exprCaculator = replaceExprs(tmpData);
						value = ExprProcessTools.convertToSuffixExpression(exprCaculator);
					}
					results.add(value);
					
				} catch (IllegalArgumentException e) {
					// 除0异常,
					e.printStackTrace();
				}
			}
			return formatValueList(results);
		}
	
	    // return null;
		// 返回空的List可以避免更多错误，后续可整合
		
		return valueListNull;
		
	}
	/**
	 * @brief 对结果list进行format， 对照参数presion进行处理
	 * 
	 */
	@Override
	protected List<Double> formatValueList(List<Double> valueList) {
		// 取有效数字
		// DecimalFormat df = new DecimalFormat("#.##");
		
		List<Double> tmpList = new ArrayList<Double>();
		for (Double value: valueList) {
			Double tmpValue = null;
			if (value != null) {
				BigDecimal bd = new BigDecimal(value);
				bd = bd.setScale(m_precision, RoundingMode.HALF_UP);
				String tmpValueString = bd.toString();
				// 转double
				tmpValue = Double.parseDouble(tmpValueString);
				// tmpList.add(tmpValue);
			} 
			tmpList.add(tmpValue);
		}
		return tmpList;
	}
	
	/**
	 * @brief  解析表达式，准备数据，Index列表为datalist的下标索引，表示一个表达式所需要的数据集合
	 * @return List
	 */
	// 可公用
	@Override
	protected ArrayList<Integer> parseExpr() {
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		try {
			String regEx = "\\{\\d\\}";
			Pattern pattern = Pattern.compile(regEx);
			// 匹配字符
			// TODO 这里只是避免null值，后续进行更严格的判断
			
			String patternsString = m_expr;
			Matcher matcher = pattern.matcher(m_expr);
			boolean result = matcher.find();
			int index = 0;
			while(result) {
				index = Integer.parseInt(patternsString.substring(matcher.start()+1, matcher.end()-1));
				indexList.add(index);
				result = matcher.find();	
			}
			return indexList;
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} 
		
	}
	
	/**
	 * @brief 将表达式的占位符用真实值替代，只是个替换操作
	 * @param map, key为表达式中的缩影，value为值，进行替换
	 */
	// 可共用 
	@Override
	protected String replaceExprs(Map<Integer, Double> data) {
		// System.out.println("CaculateDataTools.replaceExprs()");
		// 生成正则表达式
		// 进行编译
		// 去掉{}，依据{**}内的数字来判断
		try {
			String regEx = "\\{\\d\\}";
			Pattern pattern = Pattern.compile(regEx);
			// 匹配字符
			// TODO 这里只是避免null值，后续进行更严格的判断
//			if (m_exprs.size() == 0 || m_exprs.get(0) == null) {
//				System.out.println("regEx is empty");;
//				return;
//			}
			String patternsString = m_expr;
			Matcher matcher = pattern.matcher(m_expr);
			boolean result = matcher.find();
			
			StringBuffer sb = new StringBuffer();
			int index = 0;
			// 寻找替换值
			// 依据{}里的index0
			// 表达式支持10个操作数
			while(result) {
				index = Integer.parseInt(patternsString.substring(matcher.start()+1, matcher.end()-1));
				java.text.NumberFormat nf = java.text.NumberFormat.getInstance(); 
				nf.setGroupingUsed(false);
				matcher.appendReplacement(sb, nf.format(data.get(index)));
				result = matcher.find();
			}
			matcher.appendTail(sb);
			// sb相关的参数记录
			return sb.toString();
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} 
	}
	/**
	 * @brief 生成时间序列
	 * @param 开始时间
	 * @param 结束时间
	 */
	// 各自实现
	@Override
	protected List<String> genKeyList(String fromDateString, String toDateString) {
		
		ArrayList<String> keyList = new ArrayList<String>();
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Set<String> keySet = new LinkedHashSet<String>();
		Date fromDate, toDate = null;
		try {
			df.applyPattern("yyyy-MM-dd");
			fromDate = df.parse(fromDateString);
			toDate = df.parse(toDateString);
			long tmpStamp = fromDate.getTime();
			// 获取天数
			for (; tmpStamp <= toDate.getTime();) {
				// String tmpDayString = sdf.format(tmp);
				// System.out.println("tmpDayString :" + tmpDayString);
				// keyList.add(tmpDayString);
				String mondayString = CaculateDataTools.getMondayString(tmpStamp);
				keySet.add(mondayString);
				tmpStamp += 24*3600*1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			// return null;
		}
		keyList.addAll(keySet);
		return keyList;
 	}
	
	/**
	 * @brief 获取环比序列, 此方法可以重载，针对不同的时间维度做处理
	 * @param 时间维度
	 */
	// 各自实现
	@Override
	protected List<String> genQoqKeyList(List<String> keyList) {
		ArrayList<String> qoqKeyList = new ArrayList<String>();
		// 遍历KeyList，取得时间序列数据
		for (String key : keyList) {
			// Long keyLong = null;
			Long keyLong = parseDateToLong("yyyy-MM-dd", key);
			Long qoqKeyLong = null;
			String qoqKeyString = null;
			if (keyLong != null) {
				// 计算
				qoqKeyLong = keyLong - 604800*1000L;
				// 转化为string 格式
				qoqKeyString = ParseDateToString("yyyy-MM-dd", qoqKeyLong);
			} else {
				qoqKeyString = null;
			}
			qoqKeyList.add(qoqKeyString);
		}
		// System.out.println("qoqKeyList: " + qoqKeyList);
		return qoqKeyList;
	}
	
	/**
	 * @brief 获取同比序列, 此方法可以重载，针对不同的时间维度做处理
	 * @param 时间维度
	 */
	// 各自实现
	@Override
	protected List<String> genYoyKeyList(List<String> keyList) {
		ArrayList<String> yoyKeyList = new ArrayList<String>();
		// 遍历KeyList，取得时间序列数据
		for (String key : keyList) {
			// Long keyLong = null;
			Long keyLong = parseDateToLong("yyyy-MM-dd", key);
			Long yoyKeyLong = null;
			String yoyKeyString = null;
			if (keyLong != null) {
				// 计算
				// java 的 整形常亮默认是int，如果超过int的范围则需要转成long,不然会有溢出的问题
				yoyKeyLong = keyLong - 2419200*1000L;
				// 转化为string 格式
				yoyKeyString = ParseDateToString("yyyy-MM-dd", yoyKeyLong);
			} else {
				yoyKeyString = null;
			}
			yoyKeyList.add(yoyKeyString);
		}
		// System.out.println("yoyKeyList: " + yoyKeyList);
		return yoyKeyList;
	}
	
	
	
	
	


	/**
	 * @brief 获取对比起止时间
	 * @param 开始时间
	 * @param 结束时间
	 * @param 对比开始时间
	 * @throws ParseException 
	 */
	@Override
	protected String getContrastToDateString(String contrastFromDateString, 
										   String fromDateString, 
										   String toDateString) {
		
		// 根据偏移，计算totime
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance();
		String conStringToDateString = null;
		df.applyPattern("yyyy-MM-dd");
		Date fromDate = null;
		Date toDate = null;
		Date contrastFromDate = null;
		try {
			fromDate = df.parse(fromDateString);
			toDate = df.parse(toDateString);
			contrastFromDate = df.parse(contrastFromDateString);
			Long contrastToDateTime = toDate.getTime() - fromDate.getTime() + contrastFromDate.getTime();
			// 转化为时间
			df.applyPattern("yyyy-MM-dd");
			conStringToDateString = df.format(contrastToDateTime);
			// System.out.println("conStringToDateString: " + conStringToDateString);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return conStringToDateString;
		
	}
	/**
	 * @brief 解析字符串类型的时间序列方法
	 * @param format
	 * @param dateString 
	 */
	@Override
	protected Long parseDateToLong(String dateFormat, String dateString) {
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance();
		df.applyPattern(dateFormat);
		Date date = null;
		Long timeStamLong = null;
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return timeStamLong;
		}
		Long timeStampLong = date.getTime();
		return timeStampLong;
	}
	
	/**
	 * @brief 解析Long类型的时间序列为字符串
	 * @param format
	 * @param timeStamp
	 */
	@Override
	protected String ParseDateToString(String dateFormat, Long dateLong) {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		String dateString = df.format(dateLong);
		return dateString;
	}
	
	
	
}
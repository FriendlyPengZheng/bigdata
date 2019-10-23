package com.taomee.tms.mgr.tools.DataCaculator;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.ctl.common.DataController;
import com.taomee.tms.mgr.entity.RealTimeDataInfo;
import com.taomee.tms.mgr.tools.ExprProcessTools;

/**
 * 
 * @author window
 * @brief 计算结果用于前端页面展示，最后对外只提供两个接口，获取数据和时间序列
 * 
 */

public class MinCaculateDataTools extends AbstactCaculateDataTools{


	private String 					m_expr;
	private String 					m_exprName;// 显示项名称
	private Integer 				m_index;// 此expr的偏移
	private String 					m_fromDateString;
	private String 					m_toDateString;
	private HttpServletRequest 		m_request;
	// private LogMgrService m_logMgrService;
	private Integer 				m_precision; // 精度
	private String  				m_unit; // 单位
	// private String  m_dataName; // 数据项名称

	public  LogMgrService 			m_logMgrService;

	
	private static final Logger logger = LoggerFactory
			.getLogger(DataController.class);
	
	/**
	 * @param expr
	 * @param expr的偏移
	 * @param start time
	 * @param end time
	 * @param request, httpSeveletRequest
	 * @param dao接口
	 */
	public MinCaculateDataTools(String  expr, 
							    Integer index, // 此expr在exprList中的偏移
							    String fromDataString,
							    String toDateString,
							    HttpServletRequest request,
							    LogMgrService logMgrService
								) {
	
		
		m_fromDateString = fromDataString;
		m_toDateString = toDateString;
		m_expr = expr;// 表达式
		m_index = index;// 对于多个表达式的情况
		m_request = request;// 请求包结构
		// TODO 用@reference解决这个问题
		m_exprName = m_request.getParameter("data_info[" + m_index + "][data_name]");// 可能为空
		m_logMgrService = logMgrService; // dubbo服务
		m_unit = m_request.getParameter("data_info[" + m_index + "][unit]"); // 单位
		String precisionString = m_request.getParameter("data_info[" + m_index + "][precision]"); // 精确位数
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
		// System.out.println("keyList: " + keyList);
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
	 * @brief 返回keyList接口,此接口可以对外
	 * @return list
	 */
	// 此方法需要各自实现
	@Override
	public List<String> getKeyList() {
		// 对keyList进行格式化
		List<String> keyList = genKeyList(m_fromDateString, m_toDateString);

		System.out.println("keylist:" + keyList);
		return keyList;
	}

	
	
	/**
	 * @brief 获取expr的数据名称
	 */
	// TODO 返回数据标签，总在线。。
	public String getExprName() {
		// return m_exprName;
		return m_exprName; // 可能为空
	}
	
	
	// 此函数可以进行封装
	/**
	 * @brief 解析表达式，依据表达式准备resultList，同比环比也会从里面获取数据，相关result会进行追加,即完善result
	 * @brief 获取数据需要有一定的余量
	 */	
	@Override
	protected ArrayList<Map<String, Double>> preDataList(List<String> keyList) {
		// 遍历index, 取出数据
		// 时间序列数据
	   
		ArrayList<Map<String, Double>> resultList = new ArrayList<Map<String,Double>>();
		ArrayList<Integer> indexList;
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if ((indexList = parseExpr()) == null || indexList.isEmpty()) {
			// return;
			logger.error("parseExpr_error, wrong expr recved");
			return null;
		}
		
		String gameIdString = m_request.getParameter("game_id");
		Integer gameId = (gameIdString == null || gameIdString.isEmpty())? -1 : Integer.parseInt(gameIdString);
		
		String platformIdString = m_request.getParameter("platform_id");
		Integer platformId = (platformIdString == null || platformIdString.isEmpty())? -1 : Integer.parseInt(platformIdString);
		
		/*String zoneIdString = m_request.getParameter("zone_id");
		Integer zoneId = (zoneIdString == null || zoneIdString.isEmpty())? -1 : Integer.parseInt(zoneIdString);
		
		String serverIdString = m_request.getParameter("server_id");
		Integer serverId = (serverIdString == null || serverIdString.isEmpty())? -1 : Integer.parseInt(serverIdString);*/
		
		// serverId 异常会 返回 -1
		String zondServerId = m_request.getParameter("ZS_id");
		zondServerId = (zondServerId == null || zondServerId.isEmpty())? "-1_-1" : zondServerId;
		
		Integer serverId = getServerId(gameId, platformId, zondServerId, m_logMgrService);
		if (serverId == null) {
			logger.error("serverId get is null" 
							+ "game_id" + gameId
							+ "platformId" + platformId
							+ "zondServerId" + zondServerId
							+ "serverId" + serverId);
			return null;	
		}
		
		// indexList 表达式里面有多少个操作数
		// 每个操作数代表一种dataid
		for (int i = 0; i < indexList.size(); ++i) {
			Map<String, Double> tmpMap = new TreeMap<String, Double>();
			List<RealTimeDataInfo> resultInfos = null;
			
			
			try {
				// System.out.println("dataInfo");
				String dataIdString = m_request.getParameter("data_info[" + indexList.get(i) + "][data_id]");
				dataIdString = (dataIdString == null || dataIdString.isEmpty()) ? "-1" : dataIdString;
				Integer dataId = Integer.parseInt(dataIdString);		
				if (dataId == -1) {
					// 任然为-1， 退出
					logger.error("dataId is invalied");
					return null;
				}
				
				String factorString = m_request.getParameter("data_info[" +indexList.get(i) + "][factor]");
				factorString = (factorString == null || factorString.isEmpty()) ? "1" : factorString;
				Double factor = Double.parseDouble(factorString);
				// resultInfos = m_logMgrService.getValueInfo(dataId, 10, keyList.get(0), keyList.get(keyList.size() - 1), 0);
				
				String fromDateString = keyList.get(0).substring(0, 10);
				// String toDateString = keyList.get(keyList.size() - 1).substring(0, 10);

				// 拿到data_name
				if (serverId == -1 || dataId == -1) {
					logger.error("param error");
					return null;
				}
				resultInfos = m_logMgrService.getDateValueMin(dataId, gameId, serverId, fromDateString);
				
				System.out.println("resultInfo from db: " + resultInfos);
				
				
				// resultInfos = m_logMgrService.getValueInfo(102, 10, "2017-02-04", "2017-02-09", period);
				
				if (resultInfos == null || resultInfos.size() == 0) {
					logger.error("get null from db");
					return null;
				}
				
				Map<Integer, Double> valueMap = resultInfos.get(0).getValues();
			
				// for(RealTimeDataInfo resultInfo : resultInfos) {
				for(int j = 0; j < keyList.size(); ++j) {
					// for(int k = 0; k < resultInfos.size(); ++k) { 
					try {
						if (valueMap.get(j) == null) {
							tmpMap.put(keyList.get(j), null);
						}else 
							tmpMap.put(keyList.get(j), valueMap.get(j) * factor);
					} catch (NullPointerException e) {
						e.printStackTrace();
						return null;
					}
				}
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
	 * @param  数据池
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
		// System.out.println("resultList: " + resultList);
		// TODO null值校验
		List<Integer> indexList = parseExpr();
		// assert m_result.size 需要 和 index.size 相等
	
		if (indexList.size() == resultList.size()) {
			System.out.println("indexList" + indexList);
			System.out.println("resultList" + resultList);
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
						results.add(value);
					} else results.add(value);
					
				} catch (IllegalArgumentException e) {
					// 除0异常,
					e.printStackTrace();
				}
			}
			m_caculateDataList = results;
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
		
		// assert fromDateString == toDateString
		// 日期选择器传的时间参数一般是不会有问题的
		// 转化为时间戳
		
		List<String> keyList = new ArrayList<String>();
		SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfMin = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date fromDate = null;
		try {
			fromDate  = sdfDay.parse(fromDateString);
			Long fromTimeStamp = fromDate.getTime();
			for (int i = 0; i < 24 * 60; i++) {
				String timeMinString = sdfMin.format(fromTimeStamp);
				keyList.add(timeMinString);
				fromTimeStamp += 60*1000;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		// System.out.println("keyList: " + keyList);
		return keyList;
 	}
	
	/**
	 * @brief 依据valuelist 进行求和
	 * @return 
	 */
	
	
	
	
//	/**
//	 * @brief 获取环比序列, 此方法可以重载，针对不同的时间维度做处理
//	 * @param 时间维度
//	 */
//	// 各自实现
//	@Override
//	protected List<String> genQoqKeyList(List<String> keyList) {
//		ArrayList<String> qoqKeyList = new ArrayList<String>();
//		// 遍历KeyList，取得时间序列数据
//		for (String key : keyList) {
//			// Long keyLong = null;
//			Long keyLong = parseDateToLong("yyyy-MM-dd HH:mm", key);
//			Long qoqKeyLong = null;
//			String qoqKeyString = null;
//			if (keyLong != null) {
//				// 计算
//				qoqKeyLong = keyLong - 60*1000L;
//				// 转化为string 格式
//				qoqKeyString = ParseDateToString("yyyy-MM-dd HH:mm", qoqKeyLong);
//			} else {
//				qoqKeyString = null;
//			}
//			qoqKeyList.add(qoqKeyString);
//		}
//		System.out.println("qoqKeyList: " + qoqKeyList);
//		return qoqKeyList;
//	}
//	
//	/**
//	 * @brief 获取同比序列, 此方法可以重载，针对不同的时间维度做处理
//	 * @param 时间维度
//	 */
//	// 各自实现
//	@Override
//	protected List<String> genYoyKeyList(List<String> keyList) {
//		ArrayList<String> yoyKeyList = new ArrayList<String>();
//		// 遍历KeyList，取得时间序列数据
//		for (String key : keyList) {
//			// Long keyLong = null;
//			Long keyLong = parseDateToLong("yyyy-MM-dd HH:mm", key);
//			Long yoyKeyLong = null;
//			String yoyKeyString = null;
//			if (keyLong != null) {
//				// 计算
//				// java 的 整形常亮默认是int，如果超过int的范围则需要转成long,不然会有溢出的问题
//				yoyKeyLong = keyLong - 86400*1000L;
//				// 转化为string 格式
//				yoyKeyString = ParseDateToString("yyyy-MM-dd HH:mm", yoyKeyLong);
//			} else {
//				yoyKeyString = null;
//			}
//			yoyKeyList.add(yoyKeyString);
//		}
//		System.out.println("yoyKeyList: " + yoyKeyList);
//		return yoyKeyList;
//	}
//	


	// TODO 改为统一方法
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
	
	// TODO 改为统一方法
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
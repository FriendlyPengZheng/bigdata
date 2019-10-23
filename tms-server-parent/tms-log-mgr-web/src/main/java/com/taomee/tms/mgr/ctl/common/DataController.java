package com.taomee.tms.mgr.ctl.common;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.module.CheckAuthority;
import com.taomee.tms.mgr.module.GameService;
import com.taomee.tms.mgr.tools.IntegerTools;
import com.taomee.tms.mgr.tools.DataCaculator.AbstactCaculateDataTools;
import com.taomee.tms.mgr.tools.DataCaculator.CaculateConfigration;
import com.taomee.tms.mgr.tools.DataCaculator.CaculateDataTools;
import com.taomee.tms.mgr.tools.DataCaculator.DistriCaculateTool;
import com.taomee.tms.mgr.tools.DataCaculator.OnlineDataCaculateDataTools;




@Controller
@RequestMapping("/common/data")

public class DataController {
	private static final Logger logger = LoggerFactory
			.getLogger(DataController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	private GameService gameService;
	
	@SuppressWarnings("unused")
	private Boolean checkAuthority(HttpServletRequest request){
		Integer gameId = 0;
		
		String serverIdStr = request.getParameter("server_id");
		if(serverIdStr == null || serverIdStr.isEmpty()) {
			String gameIdStr = request.getParameter("game_id");
			if(gameIdStr == null || gameIdStr.isEmpty()) {
				return false;
			} else {
				gameId = IntegerTools.safeStringToInt(gameIdStr);
			}
		} else {
			Integer serverId = IntegerTools.safeStringToInt(serverIdStr);
			if(serverId.equals(0)) {
				return false;
			}
			
			ServerInfo info = logMgrService.getServerInfo(serverId);
			if(info == null) {
				return false;
			}
			gameId = info.getGameId();
			
			if(gameId == null) {
				return false;
			}
		}
		
		List<Integer> gameIds = gameService.getGameIdsByViewAuth();
		if(gameIds == null) {
			//session过期
			return false;
		}
		
		if(!gameIds.contains(gameId)) {
			return false;
		}
		
		return true;
	}
	
	@RequestMapping(value="/getDistribution")
	public void getDistribution(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		if(!checkAuthority(request)) {
			printWriter.write("{\"result\":-1,\"data\":" + null + "}");
			printWriter.flush();
			return;
		}
			
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		DistriCaculateTool tool = new DistriCaculateTool(logMgrService, request);
		JSONArray dataInfoArray = tool.getDataInfo();
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(dataInfoArray) + "}");
		//printWriter.write("{\"result\":0,\"data\":[{\"key\":[\"[181,366)\",\"1\",\"[91,181)\",\"[31,91)\",\"[15,31)\",\"[8,15)\",\"[2,4)\",\"[4,8)\",\">=366\"],\"data\":[{\"name\":\"\u6d3b\u8dc3\u7528\u6237\u6570\",\"data\":[\"208187\",\"100934\",\"89316\",\"88504\",\"50847\",\"30927\",\"26788\",\"21929\",\"324\"],\"percentage\":[\"33.7%\",\"16.34%\",\"14.46%\",\"14.33%\",\"8.23%\",\"5.01%\",\"4.34%\",\"3.55%\",\"0.05%\"]}]}]}");
		printWriter.flush();
	}
	
	@RequestMapping(value="/getGPZSDistribution")
	public void getGPZSDistribution(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		if(!checkAuthority(request)) {
			printWriter.write("{\"result\":-1,\"data\":" + null + "}");
			printWriter.flush();
			return;
		}
		
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		DistriCaculateTool tool = new DistriCaculateTool(logMgrService, request);
		JSONArray dataInfoArray = tool.getDataInfo();
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(dataInfoArray) + "}");
		printWriter.flush();
		
	}

	@RequestMapping(value="/getTimeSeries")
	public void getTimeSeries(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		/*if(!checkAuthority(request)) {
			printWriter.write("{\"result\":-1,\"data\":" + null + "}");
			printWriter.flush();
			return;
		}*/
		
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		List<String> exprsList = new ArrayList<String>();
		String expr = null, exprTmp = null, dataIdTmp = null, relatedIdTmp = null;
		Integer qoq       =	     0;
		Integer yoy 	  =      0;
		Integer contrast  =      0;
		// TODO 对时间维度的处理
		Integer period    =      0; // 只需要考慮到日，周 ，月的情況
	
		Integer offset    =      0;
		Integer average   =      0;
		Integer sum       =      0;

		// expr_data_id区别处理
		// String by_data_expr = null;
	
		int i = 0;
		int isExpr = 0;
		try {
			if (request.getParameter("by_data_expr") == null) {
				isExpr = 0;
				while(true) {
					
					if ((exprTmp = request.getParameter("expres[" + i + "][expre]")) != null ) {
						expr = exprTmp;
						exprsList.add(expr);
						i++;
					} else 
						break;
				}
			} else if (request.getParameter("data_info[0][data_id]") != null && request.getParameter("by_data_expr") != null){
				isExpr = 1;
				while(true) {
					 if ((dataIdTmp = request.getParameter("data_info[" + i + "][data_id]")) != null) {
						 exprTmp = request.getParameter("data_info[" + i + "][data_expr]");
						 expr = (exprTmp == null || exprTmp.isEmpty()) ? "{" + i + "}" : exprTmp;
						 exprsList.add(expr);
						 i++;
					 } else 
						 break;
				}
			} else {
				isExpr = 1;
				while(true) {
					if ((relatedIdTmp = request.getParameter("data_info[" + i + "][r_id]")) != null && request.getParameter("by_data_expr") != null) {
						exprTmp = "{" + i + "}";
						expr = exprTmp;
						exprsList.add(expr);
						i++;
					} else 
						break;
				}
			}
			
			if(exprsList.size() == 0) {
				printWriter.write("{\"result\":-1,\"data\":" + null + "}");
				printWriter.flush();
				return;
			}
			
			
			// TODO contrast和 pop qoq的约束关系需要做
			qoq = (request.getParameter("qoq") == null) ? 0 : Integer.parseInt(request.getParameter("qoq"));
			yoy = (request.getParameter("yoy") == null) ? 0 : Integer.parseInt(request.getParameter("yoy"));
			period = (request.getParameter("period") == null) ? 0 : Integer.parseInt(request.getParameter("period"));
			contrast = (request.getParameter("contrast") == null) ? 0 : Integer.parseInt(request.getParameter("contrast"));
			offset = (request.getParameter("offset") == null)? 0 : Integer.parseInt(request.getParameter("offset"));
			
			// 针对特殊的expr类型 period为7
			// TODO 这个字段后面改成type
			if (isExpr == 1) {
				period = 7;
			}
			
			if (contrast == 1) {
				qoq = 0; yoy = 0;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			// TODO 直接回复错误包
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// TODO 直接回复错误包
		}
		
		String fromDateString = (request.getParameter("from[0]") == null)?  request.getParameter("from") : request.getParameter("from[0]");
		String toDateString = (request.getParameter("to[0]") == null)?  request.getParameter("to") : request.getParameter("to[0]"); 
		
		// server_id, server_id需要计算，通过calulator来计算
		List<Map<String, Map<String, List<String>>>> resultInfoList = null;
		resultInfoList = formatResultInfo(contrast, qoq, yoy, exprsList, request, period, offset,fromDateString,toDateString);
		JSONArray dataInfoArray = new JSONArray();
		packJson(dataInfoArray, resultInfoList, period, contrast, qoq, yoy, 0, 0);
		
		if(contrast.equals(0) && request.getParameter("from[1]") != null) {
			for(int i2 = 1; ;i2++) {
				if(request.getParameter("from["+i2+"]") == null || request.getParameter("to["+i2+"]") == null )
					break;
			
				String from= request.getParameter("from["+i2+"]");
				String to = request.getParameter("to["+i2+"]");
				resultInfoList = formatResultInfo(contrast, qoq, yoy, exprsList, request, period, offset,from,to);
				packJson(dataInfoArray, resultInfoList, period, contrast, qoq, yoy, 0, 0);
			}
		}
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(dataInfoArray) + "}");
		printWriter.flush();
		
		// 
	}	
	/**
	 * @brief 实时，另外的处理逻辑，相对比较简单
	 * @brief 实时数据是从redis里取值，不用考虑时间序列是否比较齐全
	 * @param request
	 * @param printWriter
	 * @param response
	 */
	
	@RequestMapping(value="/getRealTimeSeries")
	public void getRealTimeSeries(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		if(!checkAuthority(request)) {
			printWriter.write("{\"result\":-1,\"data\":" + null + "}");
			printWriter.flush();
			return;
		}
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		// 小时数据
		// 解析前端请求，取data_id， 全平台的data_id相关的数据
		// 依据game_id取到相对应的所有server_id，再依据
		// 这里需要一个总在线需要计算
		// 这里的expr没有作用
		// 解析data_id， 和server_id
		Integer period     =    0;
		// Integer sum        =    0;
		// TODO 参数控制
		Integer checkAll   =  	0;
		// 填Null
		// Integer fillNull   =  	0;
		Integer index      =    0;
		// 同比，环比
		// Integer qoq        =    0;
		// Integer yoy        =    0;
		// 分钟数据的expr是没有意义的
		// String  expr       =    "";
		Integer average    =    0;
		// sum 是默认会加的
		Integer sum        =    0;
		// Integer contrast   =    0;
		
		// period-1 前端的period和后端
		// 此period对应取哪些数据
		
		
		
		String periodString = request.getParameter("period");
		period = (periodString == null || periodString.isEmpty() ? -1 : Integer.parseInt(periodString));
		
		String checkAllString = request.getParameter("check_all");
		checkAll = (checkAllString == null || checkAllString.isEmpty() ? -1 : Integer.parseInt(checkAllString));
	
		
		sum = (request.getParameter("sum") == null) ? 0 : Integer.parseInt(request.getParameter("sum"));
		average = (request.getParameter("average") == null) ? 0 : Integer.parseInt(request.getParameter("average"));
		// String fillNullString = request.getParameter("fill_null");
		// fillNull = (fillNullString == null || fillNullString.isEmpty() ? 0 : Integer.parseInt(fillNullString));
		
	
		List<String> exprList = new ArrayList<String>();
		String expr = null, exprTmp = null;
		// String dataInfo = null, dataInfoTmp = null;
		int i = 0;
		try {
			while(true) {
				if ((exprTmp = request.getParameter("expres[" + i + "][expre]")) != null ) {
					// exprNameString = request.getParameter("exprs[" + i + "][data_name]");
					// System.out.println("expr: " + exprTmp);
					expr = exprTmp;
					exprList.add(expr);
					i++;
				} else 
					break;
			}
		
		} catch (NullPointerException e) {
			e.printStackTrace();
			// TODO 直接回复错误包
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// TODO 直接回复错误包
		}
		System.out.println("exprsList" + exprList);
		List<String> dateList = new ArrayList<String>();
		String dateString = null, dateStringTmp = null;
		int j = 0;
		try {
			while(true) {
				if ((dateStringTmp = request.getParameter("from[" + j + "]")) != null ) {
					// exprNameString = request.getParameter("exprs[" + i + "][data_name]");
					dateString = dateStringTmp;
					dateList.add(dateString);
					j++;
				} else 
					break;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		List<Map<String, Map<String, List<String>>>> resultInfoLists = new ArrayList<Map<String,Map<String,List<String>>>>();
		
		// 获取check_all ，如果有check_all字段，就是在线数据, 没有check_all 就是其他实时数据
		
		/**
		 * @brief 计算实时在线人数数据
		 */
		
		if (checkAll != -1) {
			// 无check_all 字段
			// 直接构造
			// 遍历dataList
			OnlineDataCaculateDataTools onlineDataCaculateDataTools = 
												new OnlineDataCaculateDataTools(request,
																				logMgrService);
			for(String dateTime : dateList) {
				// 获取dataId
				// 获取serverId
				// 获取gameId
				// 解析返回包
				Map<String, Map<String, List<String>>> valueMap = onlineDataCaculateDataTools.getValueMap(dateTime);
				if (valueMap == null || valueMap.size() == 0) {
					logger.error("onlineDataCaculateDataTools get data error");
					return;
				}
				resultInfoLists.add(valueMap);
			}
			
			// System.out.println("resultInfoLists:" + resultInfoLists);
		    // 在线数据，无需计算
			JSONArray dataInfoArray = new JSONArray();
			packJson(dataInfoArray,resultInfoLists, period, 0, 0, 0, 0, 0);
			// System.out.println("datajson:" + JSON.toJSONString(dataInfoArray));
			printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(dataInfoArray) + "}");
			printWriter.flush();
			return;
		}
		
		
		/**
		 * @brief 计算实时常规统计项（登录登出，新增等）
		 */
		
		for(String date : dateList) {
			String fromDateString = date;
			String toDateString = date;
			
			// List<String> sumList = new ArrayList<>();
			// List<String> averageList = new ArrayList<>();
			
			// System.out.println("fromdate: " + fromDateString);
			// System.out.println("todate: " + toDateString);
			Map<String, Map<String, List<String>>> resultMap = new LinkedHashMap<String, Map<String,List<String>>>();
			for(index = 0; index < exprList.size(); ++ index) {
				AbstactCaculateDataTools caculator = 
						CaculateConfigration.getCaculateDataTools(exprList.get(index), 
																  index, 
																  fromDateString, // 实时数据的开始时间和结束时间是同一天
																  toDateString, 
																  request, 
																  logMgrService, 
																  period); 
				
				// 对caculator做适当处理
				// System.out.println("exprList" + exprList.get(index));
				Map<String, List<String>> valueMap = new LinkedHashMap<String, List<String>>();
				// Map<String, Map<String, List<String>>> resultMap = new HashMap<String, Map<String,List<String>>>();
				// 这两个函数 返回值需要改，list<<>>
				
				List<String> keyList = caculator.getKeyList();
				List<String> valueList = caculator.getValueList(fromDateString, toDateString);
				
				
				valueMap.put("key", keyList);
				valueMap.put("value", valueList);
					
				List<String> sumList = new ArrayList<>();
				List<String> averageList = new ArrayList<>();
				
				// average
				if (average == 1) {
					// TODO
					averageList.add(CaculateDataTools.getAverage(valueList));
					
				}
				
				// sum
				if (sum == 1) {
					sumList.add(CaculateDataTools.getSum(valueList));
				}
				
			
				// valueMap.put("sum", valueList.)
				// name 相关 需要去接口取
				// 获取一个list
				
				
				ArrayList<String> timeRange = new ArrayList<String>();
				timeRange.add(fromDateString);
				timeRange.add(toDateString);
				valueMap.put("range", timeRange);
			
			
				// 没有name 的用在线人数替换
				String name = null;
				if (caculator.getExprName() == null) {
					// TODO 后续此项需要修改
					name = "[" + fromDateString + "]" + "没有数据项名称";
				} else {
					name = "[" + fromDateString + "]" + caculator.getExprName();
				}
				valueMap.put("sum", sumList);
				valueMap.put("average", averageList);
				resultMap.put(name, valueMap);
				
			}
			resultInfoLists.add(resultMap);
		}

		System.out.println("resultInfoLists: " + resultInfoLists);
		// 无同比环比
		JSONArray dataInfoArray = new JSONArray();
		packJson(dataInfoArray,resultInfoLists, period, 0, 0, 0, average, sum);
		System.out.println("datajson:" + JSON.toJSONString(dataInfoArray));
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(dataInfoArray) + "}");
		printWriter.flush();
		
	}
	
	/**
	 * @brief 打包json格式，返回
	 * @param resultInfoList
	 * @param 时间维度
	 * @param 是否对比
	 * @param 环比
	 * @param 同比
	 * @param 是否需要求平均
	 * @param 是否需要求和
	 */
	private void packJson(JSONArray dataInfoArray ,
								List<Map<String, Map<String, List<String>>>> resultInfoList, 
								Integer period, 
								Integer contrast,
								Integer qoq,
								Integer yoy,
								Integer average,
								Integer sum) {
		//JSONArray dataInfoArray = new JSONArray();
		Long pointStart = 0L;
		// +1month的兼容
		String pointInerval = "";
		// TODO 封装为公共方法，提供给小时，分钟，日周月调用
		for (Map<String, Map<String, List<String>>> resultMap : resultInfoList) {
			// JSONObject dataInfoObject = new JSONObject();
			JSONObject dataInfoObject = new JSONObject();
			
			// 对key的处理，对key的处理需要提出来进行处理
			if (resultMap != null && resultMap.size() != 0) {
				Set<String> set = resultMap.keySet();
				Iterator<String> iterator = set.iterator();
				JSONArray outerdataInfoArray = new JSONArray();
				
				JSONArray caldataInfoArray = new JSONArray();
				JSONArray caldataNameArray = new JSONArray();
				JSONObject caldataInfoObject = new JSONObject();
				
				while (iterator.hasNext()) {
					String key  = iterator.next().toString();
					dataInfoObject.put("key", resultMap.get(key).get("key"));	
					JSONArray interDataArray = new JSONArray();
					interDataArray.addAll(resultMap.get(key).get("value"));
					JSONObject interdataObject = new JSONObject();
					// name需要一定的拓展功能
					String name = null;
					if (contrast == 1) {
							name = "[" + resultMap.get(key).get("range").get(0) + "~"
									   + resultMap.get(key).get("range").get(1) + "]" + key; 
					} else {
						name = key;
					}
					interdataObject.put("name", name);
					interdataObject.put("data", interDataArray);
					interdataObject.put("contrast_rate", resultMap.get(key).get("contrast_rate"));
					// dataObject.put("data", interdataObject);
					if (qoq == 1 && yoy == 1) {
						interdataObject.put("qoq", resultMap.get(key).get("qoq"));
						interdataObject.put("yoy", resultMap.get(key).get("yoy"));
					}
					if (sum == 1) {
						caldataInfoArray.add(resultMap.get(key).get("sum").get(0));
						caldataNameArray.add(resultMap.get(key));
					}
					outerdataInfoArray.add(interdataObject);
					pointStart = CaculateDataTools.convertDateStringTots(resultMap.get(key).get("range").get(0));
					//pointStart = 0L;
					pointInerval = CaculateDataTools.getPointInterval(period);
				}
				if (sum == 1) {
					caldataInfoObject.put("key", caldataNameArray);
					caldataInfoObject.put("data", caldataInfoArray);
				} 
				if (average == 1) {
					caldataInfoObject.put("key", caldataNameArray);
					caldataInfoObject.put("data", caldataInfoArray);
				}
				dataInfoObject.put("data", outerdataInfoArray);
					// System.out.println("pointStart: " + pointStart);
				dataInfoObject.put("pointStart", pointStart);
		
				dataInfoObject.put("pointInterval", pointInerval);
				if (sum == 1) {
					dataInfoObject.put("sum", caldataInfoObject);
					
				}
				if (average == 1) {
					dataInfoObject.put("average", caldataInfoObject);
					
				}
				
			}
			dataInfoArray.add(dataInfoObject);
		}
		return;
	}
	
	/**
	 * @brief  format数据接口, 如果涉及到constrast list中会有两个元素,取數據接口
	 * @param  constrast, 
	 * @param  exprlist
	 * @param  qoq, yoy
	 * @param  period // 根据时间区间实例化不同的对象
	 * @return list<map<string, list>>
	 */
	private List<Map<String, Map<String, List<String>>>> formatResultInfo(int contrast, 
																			   int qoq, 
															                   int yoy,
															                   List<String> exprsList,
															                   HttpServletRequest request,
															                   int period,
															                   int offset,
															                   String fromDateString,
															                   String toDateString) {
		List<Map<String, Map<String, List<String>>>> resultInfoList = new ArrayList<Map<String,Map<String,List<String>>>>();
		List<AbstactCaculateDataTools> caculateList = new ArrayList<AbstactCaculateDataTools>(); 
		//String fromDateString = (request.getParameter("from[0]") == null)?  request.getParameter("from") : request.getParameter("from[0]");
		fromDateString = CaculateDataTools.formatDateString(fromDateString);
		
		//String toDateString = (request.getParameter("to[0]") == null)?  request.getParameter("to") : request.getParameter("to[0]");
		toDateString = CaculateDataTools.formatDateString(toDateString);
		
		// 对偏移做出处理, 对于次日留存等等的数据，或者次周留存的数据，需要在前面若干天去取值
		if (offset != 0) {
			fromDateString = CaculateDataTools.offsetDateString(fromDateString, offset);
			toDateString = CaculateDataTools.offsetDateString(toDateString, offset);
		}
		
		// 获取时间区间
		// 遍历exprsList来获取值
		// 获取开始时间好结束时间
		// 获取对比开始时间
		// TODO 依据period实例化不同的对象
		for (int index = 0; index < exprsList.size(); ++index) {
			AbstactCaculateDataTools caculator = 
				       CaculateConfigration.getCaculateDataTools(exprsList.get(index), 
																			   index, 
																			   fromDateString,
																			   toDateString,
																			   request,
																			   logMgrService,
																			   period);
		    caculateList.add(caculator);   
		}
		Map<String, Map<String, List<String>>> resultMap = new LinkedHashMap<String, Map<String,List<String>>>();
		// System.out.println("caculator" + caculateList.size());
		int i = 0;
		for (AbstactCaculateDataTools caculator : caculateList) {
			
			Map<String, List<String>> valueMap = new LinkedHashMap<String, List<String>>();
			valueMap.put("key", caculator.getKeyList());
			valueMap.put("value", caculator.getValueList(fromDateString, toDateString));
			
			if (qoq == 1 && yoy == 1) {
				// 同比环比数据
				valueMap.put("qoq", caculator.getQoqValueList());
				valueMap.put("yoy", caculator.getYoyValueList());
			}
			ArrayList<String> timeRange = new ArrayList<String>();
			timeRange.add(fromDateString);
			timeRange.add(toDateString);
			valueMap.put("range", timeRange);
			String name = null;
			// TODO 这里的name是否有意义？
			if (caculator.getExprName() == null)
				name = "没有计算名称" + String.valueOf(i);
			else 
				name = caculator.getExprName();
			resultMap.put(name, valueMap);
			i++;
		}
		resultInfoList.add(resultMap);
		// caculateList.clear();
		if (contrast == 1) {
			List<String> contrastDateList = new ArrayList<String>();
			for(int n = 1;;n++) {
				String contrastFromDateString = request.getParameter("from["+n+"]");
				if(contrastFromDateString == null || contrastFromDateString.isEmpty()) {
					break;
				}
				contrastDateList.add(contrastFromDateString);
			}
			for (AbstactCaculateDataTools caculator : caculateList) {
				for(String date:contrastDateList){
					Map<String, Map<String, List<String>>> resultMap_contrast = new HashMap<String, Map<String,List<String>>>();
					Map<String, List<String>> valueMap = new HashMap<String, List<String>>();
					List<String> keyList = caculator.getContrastKeyList(date);
					valueMap.put("key", keyList);
					valueMap.put("value", caculator.getContrastLists().get("contrast"));
					if (qoq == 1 && yoy == 1) {
						// 同比环比数据
						// 如果有对比，同比环比数据是不会展示的，之前的统计是这样的逻辑
						valueMap.put("qoq", caculator.getQoqValueList());
						valueMap.put("yoy", caculator.getYoyValueList());
					} 
					ArrayList<String> timeRange = new ArrayList<String>();
					// range
					timeRange.add(keyList.get(0));
					timeRange.add(keyList.get(keyList.size() - 1));
					valueMap.put("range", timeRange);
					valueMap.put("contrast_rate", caculator.getContrastLists().get("contrastRate"));
					resultMap_contrast.put(caculator.getExprName(), valueMap);
					resultInfoList.add(resultMap_contrast);
				}
			}
		}
		return resultInfoList;
	}
}









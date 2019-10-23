package com.taomee.tms.mgr.ctl.gameanalysis;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.taomee.tms.mgr.entity.ResultInfo;
import com.taomee.tms.mgr.module.GameService;
import com.taomee.tms.mgr.tools.DataCaculator.CaculateDataTools;



@Controller
@RequestMapping("/gameanalysis/{type}/overview")
public class OverviewController extends CommonAnalysisController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	private GameService gameService;
    
	//@Reference
	//private List<ResultInfo> ResultInfos;
	
	
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		// logger.info("ComponentQueryForm is " + JSON.toJSONString(form));
		// 一些参数的设置
		// JSONObject commonParamObject = new JSONObject();
		// 获取当前的日期
		// from_time 当前日期
		// to_time　往前推３０天
		// TODO 写一个tool方法，处理这些参数
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, -30);
//	
//		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
//		long now = System.currentTimeMillis();
//		
//		String toDate = dfs.format(now);
//		String fromDate = dfs.format(calendar.getTime());
//		
//		request.setAttribute("from", fromDate);
//		request.setAttribute("to", toDate);
//		
		// 对比开始时间, -60, 在原先的基础上再往前30天
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -30);
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		String constractFromDate = dfs.format(calendar.getTime());
		request.setAttribute("constract_from", constractFromDate);
		
//		String gameString = request.getParameter("game_id");
//		if(gameString == null || gameString.isEmpty()) {
//			request.setAttribute("game_id", -1);
//		} else {
//			request.setAttribute("game_id", gameString);
//		}
		request.setAttribute("game_type", "web");
		request.setAttribute("gpzs_id", -1);
		assignCommon(request, httpSession, logMgrService, gameService);
		
		return "gameanalysis/overview";
	}
			
	// TODO getModules后期可以整合
	// TODO 写表单整合，后面需要获取数据
	// 页面发送http请求调用此方法,获取数据，再用json返回
	// 求和的时候加上也需要进行nul值判断
	@RequestMapping(value = "/viewdata")
	public void viewData(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		System.out.println("viewdata");
		// 新增用户数， 新增角色数，收入
		int[] dataIdList = {10270, 8, 9};
		
		List<ResultInfo> resultInfos = null;
	
		String fromDateString = request.getParameter("from[0]");
		String toDateString = request.getParameter("to[0]");
		Integer contrast = Integer.parseInt(request.getParameter("contrast"));
		String contrastFromDateString = request.getParameter("from[1]");
		// 此處的時間維度默認是0
		// 不一样的只是serverId
		// System.out.println("fromdate" + fromDateString + "toDateString" + toDateString);
		
		String gameIdString = request.getParameter("game_id");
		Integer gameId = (gameIdString == null || gameIdString.isEmpty())? 0 : Integer.parseInt(gameIdString);
		System.out.println("gameId" + gameId);
		Integer serverId = logMgrService.getServerInfoByTopgameId(gameId).getServerId();
		
		resultInfos = logMgrService.getValueInfo(dataIdList[0], serverId, fromDateString, toDateString, 0);
		// System.out.println("resultInfos" + resultInfos);
		// System.out.println("serverId" + serverId);
		// 遍歷list，加和
		Double newUserSum = 0.0;
		for (int i = 0; i < resultInfos.size(); ++i) {
			newUserSum += resultInfos.get(i).getValue();
		}
		resultInfos = logMgrService.getValueInfo(dataIdList[1], serverId, fromDateString, toDateString, 0);
		
		// 遍歷list，加和
		Double newRoleSum = 0.0;
		for (int i = 0; i < resultInfos.size(); ++i) {
			newRoleSum += resultInfos.get(i).getValue();
		}
		resultInfos = logMgrService.getValueInfo(dataIdList[2], serverId, fromDateString, toDateString, 0);
		
		// 遍歷list，加和
		Double newInComeSum = 0.0;
		for (int i = 0; i < resultInfos.size(); ++i) {
			newInComeSum += resultInfos.get(i).getValue();
		}
		newInComeSum = newInComeSum / 100;
		List<ArrayList<String>> sumList = getSum(dataIdList, fromDateString, toDateString, contrastFromDateString, contrast, serverId);
		System.out.println("sumList" + sumList);
		// 構造json字符串
		JSONArray resultInfoArray = new JSONArray();
		JSONObject resultObject = new JSONObject();
		resultObject.put("name", "新增用户数");
		JSONArray interresultInfoArray = new JSONArray();
		interresultInfoArray.add(newUserSum);
		if (contrast == 1) {
			interresultInfoArray.add(sumList.get(0).get(1));
			interresultInfoArray.add(sumList.get(0).get(2));
		}
		resultObject.put("data", interresultInfoArray);
		resultInfoArray.add(resultObject);
		JSONObject resultObject1 = new JSONObject();
		resultObject1.put("name", "新增角色数");
		JSONArray interresultInfoArray1 = new JSONArray();
		interresultInfoArray1.add(newRoleSum);
		if (contrast == 1) {
			interresultInfoArray1.add(sumList.get(1).get(1));
			interresultInfoArray1.add(sumList.get(1).get(2));
		}
		resultObject1.put("data", interresultInfoArray1);
		resultInfoArray.add(resultObject1);
		
		JSONObject resultObject2 = new JSONObject();
		resultObject2.put("name", "收入(元)");
		JSONArray interresultInfoArray2 = new JSONArray();
		interresultInfoArray2.add(newInComeSum);
		if (contrast == 1) {
			interresultInfoArray2.add(sumList.get(2).get(1));
			interresultInfoArray2.add(sumList.get(2).get(2));
		}
		resultObject2.put("data", interresultInfoArray2);
		resultInfoArray.add(resultObject2);
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(resultInfoArray) + "}");
		printWriter.flush();
		// TODO 直接拷贝
		//"/json/overview/gameanalysis-overview-keymetrics.json";
	}
	
	@RequestMapping(value = "/isTaskComplete")
	public void isTaskComplete(HttpServletRequest request, HttpServletResponse response) {
		// TODO
		// 修改返回包
	}
    
	/**
	 * @brief  與後台交互，獲取數據,進行統一處理
	 * @param  data_id的數組，此data_id依據是固定的幾個data_id表示固定的幾個意思
	 * @param  String fromDate
	 * @param  String toDate
	 * @param  String contrastFromDateString
	 * @return Map
	 */
	 List<ArrayList<String>> getSum(int[] dataIdList,
												  String fromDateString, 
												  String toDateString,
												  String contrastFromDateString,
												  Integer contrast,
												  Integer serverId
												  ) {
		
		List<ArrayList<String>> sumList = new ArrayList<ArrayList<String>>();
		// map中獲取數據
		List<ResultInfo> resultInfos = null;
		for (int i = 0; i < dataIdList.length; ++i) {
			// TODO 空值判斷
			resultInfos = logMgrService.getValueInfo(dataIdList[i], serverId, fromDateString, toDateString, 0);
			// 求和
			// Map<String, String> tmpMap = new HashMap<String, String>();
			ArrayList<String> tmpList = new ArrayList<String>();
			// tmpMap.put("name", "新增用戶數");
			Double newSum = 0.0;
			for (ResultInfo result : resultInfos) {
				// 求和
				newSum += result.getValue();
			}
			newSum = (i == 2) ? newSum/100 : newSum;
			// 保留两位小数
			String newSumString = CaculateDataTools.formatValueList(newSum, 0);
			tmpList.add(newSumString);
			sumList.add(tmpList);
		}
		// 對比值
		if (contrast == 1) {
			// 計算
			String contrastToDateString = 
					CaculateDataTools.getContrastToDateString(contrastFromDateString, fromDateString, toDateString);
			List<ResultInfo> contrastResultInfos = null;
			for (int i = 0; i < dataIdList.length; ++i) {
				// TODO 空值判斷
				contrastResultInfos = logMgrService.getValueInfo(dataIdList[i], serverId, contrastFromDateString, contrastToDateString, 0);
				if (contrastResultInfos.isEmpty() || contrastResultInfos == null) {
					// 为空
				}
				Double newSum = 0.0;
				for (ResultInfo result : contrastResultInfos) {
					newSum += result.getValue();
				}
				newSum = (i == 2) ? newSum/100 : newSum;
				String newSumString = CaculateDataTools.formatValueList(newSum, 0);
				sumList.get(i).add(newSumString);
				// 求对比
				Double curValue = Double.parseDouble(sumList.get(i).get(0));
				Double rate = (curValue - newSum)/newSum * 100;
				String rateString = CaculateDataTools.formatValueList(rate, 0);
				sumList.get(i).add(rateString);
			}
		}
		return sumList;
	}
}
























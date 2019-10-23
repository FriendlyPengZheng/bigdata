package com.taomee.tms.mgr.tools.DataCaculator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.ctl.common.DataController;
import com.taomee.tms.mgr.entity.RealTimeDataInfo;


/**
 * @author window
 * @brief  计算在线数据的类
 */
public class OnlineDataCaculateDataTools extends AbstactCaculateDataTools{
	
	
	@Reference
	private LogMgrService m_logMgrService;
	
	private HttpServletRequest m_request;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DataController.class);
	
	/**
	 * brief 构造参数
	 */
	public OnlineDataCaculateDataTools(HttpServletRequest request,
			                           LogMgrService logMgrService) {
		m_request = request;
		m_logMgrService = logMgrService;
	}
	
	
	/**
	 * @brief 依据日期取数据
	 * @param fromDateTime
	 * @return map<string, RealTimeDataInfo>
	 */
	@Autowired
	public Map<String, Map<String, List<String>>>getValueMap(String dateTime){
		// 获取数据
		// 获取其他参数(dataId, ServerId, gameId...)
		// 调用接口
		Map<String, Map<String, List<String>>> resultMap = new HashMap<String, Map<String,List<String>>>();	
		try  {
				
			// dataId, gameId, serverId
			String dataIdString = m_request.getParameter("data_info[0][data_id]");
			int dataId = (dataIdString == null || dataIdString.isEmpty()) ? -1 : Integer.parseInt(dataIdString);
			
			
			String gameIdString = m_request.getParameter("game_id");
			Integer gameId = (gameIdString == null || gameIdString.isEmpty())? -1 : Integer.parseInt(gameIdString);
			
			String platformIdString = m_request.getParameter("platform_id");
			Integer platformId = (platformIdString == null || platformIdString.isEmpty())? -1 : Integer.parseInt(platformIdString);
			
			String zondServerId = m_request.getParameter("ZS_id");
			zondServerId = (zondServerId == null || zondServerId.isEmpty())? "-1_-1" : zondServerId;
			
			/*String zoneIdString = m_request.getParameter("zone_id");
			Integer zoneId = (zoneIdString == null || zoneIdString.isEmpty())? -1 : Integer.parseInt(zoneIdString);
			
			String serverIdString = m_request.getParameter("server_id");
			Integer serverId = (serverIdString == null || serverIdString.isEmpty())? -1 : Integer.parseInt(serverIdString);*/
			// 进行结果相关的计算，需要调用接口获取serverId
			// TODO serverId null值判断
			
			Integer serverId = getServerId(gameId, platformId, zondServerId, m_logMgrService);
		
			if (serverId == -1 || dataId == -1 || gameId == -1) {
				logger.error("param error");
				return null;
			}
	
			List<RealTimeDataInfo> resultInfos = null;
			resultInfos = m_logMgrService.getDateValueMin(dataId, gameId, serverId, dateTime);
		
			System.out.println("resultInfos min:" + resultInfos);
			
			// 在线人数相关的数据
			// keylist & valuelist
			List<String> keyList = genKeyList(dateTime, dateTime);
			for(RealTimeDataInfo realTimeDataInfo : resultInfos) {
				Map<String, List<String>> valueMap = new LinkedHashMap<String, List<String>>();
				List<String> valueList = new ArrayList<String>();
				Map<Integer, Double> tmpMap = realTimeDataInfo.getValues();
				for(int i = 0; i < keyList.size(); ++i) {
					// for(int k = 0; k < resultInfos.size(); ++k) { 
					try {
						if (tmpMap.get(i) == null) {
							valueList.add(null);
						}else 
							valueList.add(tmpMap.get(i).toString());
					} catch (NullPointerException e) {
						e.printStackTrace();
						return null;
					}
				}
				valueMap.put("value", valueList);
				valueMap.put("key", keyList);
				ArrayList<String> timeRange = new ArrayList<String>();
				// 接口兼容 fromDateTime == toDateTime
				timeRange.add(dateTime);
				timeRange.add(dateTime);
				valueMap.put("range", timeRange);
				String dataName = "[" + dateTime + "]" + realTimeDataInfo.getDataName();
				resultMap.put(dataName, valueMap);
			}
	
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			logger.error("param error" + e.getMessage());
		}
		return resultMap;
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
	
	
	
}

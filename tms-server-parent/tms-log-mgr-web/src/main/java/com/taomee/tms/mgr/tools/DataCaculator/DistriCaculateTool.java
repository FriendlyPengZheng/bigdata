package com.taomee.tms.mgr.tools.DataCaculator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.entity.DistrDataInfo;

public class DistriCaculateTool {

	public  LogMgrService m_logMgrService;
	private HttpServletRequest m_request;
	
	private String fromDateString;
	private String toDateString;
	private Integer artifact_id;
	private Integer schema_id;
	private Integer gameId;
	private Integer platformId;
	private Integer zoneId;
	private Integer serverId;
	private Integer type;
	private String style;
	private Integer timeDimension;
	private Double factor;
	
	public DistriCaculateTool(LogMgrService logMgrService, HttpServletRequest request) {
		m_request = request;
		m_logMgrService = logMgrService;
		
		fromDateString = m_request.getParameter("from[0]");
		toDateString = m_request.getParameter("to[0]");
		
		String gameIdString = m_request.getParameter("game_id");
		gameId = (gameIdString == null || gameIdString.isEmpty())? -1 : Integer.parseInt(gameIdString);
		
		String platformIdString = m_request.getParameter("platform_id");
		platformId = (platformIdString == null || platformIdString.isEmpty())? -1 : Integer.parseInt(platformIdString);
		
		/*String zoneIdString = m_request.getParameter("zone_id");
		zoneId = (zoneIdString == null || zoneIdString.isEmpty())? -1 : Integer.parseInt(zoneIdString);
		
		String serverIdString = m_request.getParameter("server_id");
		serverId = (serverIdString == null || serverIdString.isEmpty())? -1 : Integer.parseInt(serverIdString);*/
		
		String zoneServerString = m_request.getParameter("ZS_id");
		String zoneServerId = (zoneServerString == null || zoneServerString.isEmpty())? "-1_-1" : zoneServerString;
		String[] tmp = zoneServerId.split("_");
		String zoneString = tmp[0];
		String serverString = tmp[1];
		if(zoneString == null || zoneString.isEmpty()) {
			zoneId = -1;
		} else {
			zoneId = Integer.valueOf(zoneString);
		}
		
		if(serverString == null || serverString.isEmpty()) {
			serverId = -1;
		} else {
			serverId = Integer.valueOf(serverString);
		}
		
		
		String artifactString = m_request.getParameter("data_info[0][artifact_id]");
		artifact_id = (artifactString == null || artifactString.isEmpty())? -1 : Integer.parseInt(artifactString);
		
		String schemaString = m_request.getParameter("data_info[0][schema_id]");
		schema_id = (schemaString == null || schemaString.isEmpty())? -1 : Integer.parseInt(schemaString);
		
		String timeDimensionString = m_request.getParameter("data_info[0][period]");
		timeDimension = (timeDimensionString == null || timeDimensionString.isEmpty())? 0 : (Integer.parseInt(timeDimensionString) - 1);
		
		String factorString = m_request.getParameter("data_info[0][factor]");
		factor = (factorString == null || factorString.isEmpty())? 1 : Double.parseDouble(factorString);
		
		type = -1;
		if(artifact_id != -1 && schema_id == -1 ) {
			type = 1;
		}
		if(artifact_id == -1 && schema_id != -1) {
			type = 0;
		}
		
		String style1 = m_request.getParameter("data_info[0][dir_type]");
		style = (style1 == null || style1.isEmpty()) ? "common" : style1;
	}
	
	public Integer getServerId(Integer gameId, Integer platformId, Integer zoneId, Integer serverId, LogMgrService logMgrService) {
		Integer globalServerId = -1;
		ServerGPZSInfo serverInfo = null;
		// 不设置区服，平台ID的
		if (platformId == -1 || platformId == null &&
			zoneId     == -1 || zoneId == null &&
			serverId   == -1 || serverId == null) {
			
			// 获取顶层server_id
			// System.out.println("----------------------------------m_logMgrService: " + this.m_logMgrService);
		
			serverInfo = logMgrService.getServerInfoByTopgameId(gameId);
			System.out.println("gameid:" + gameId + " serverInfo:" + serverInfo);
			globalServerId = serverInfo.getServerId();
			return globalServerId;
		} 
		// 其他情况
		if (serverId != -1) {
			globalServerId = serverId;
		} else if (zoneId != -1) {
			globalServerId = zoneId;
		} else if (platformId != -1){
			globalServerId = platformId;
		} else {
			// error
			System.out.println("can not find the serverId");
		}
		return globalServerId;
	}
	
	public JSONArray getDataInfo() {
		String start_time = (fromDateString == null || fromDateString.isEmpty()) ? "" : fromDateString ;
		String end_time = (toDateString == null || toDateString.isEmpty()) ? "" : toDateString;
		
		List<DistrDataInfo> distrDataList = null;
		System.out.println("style:" + style) ;
		if(style.equals("common")){
			distrDataList = getCommonDirInfos(start_time, end_time);
		} else {
			distrDataList = getGpzsDirInfos(start_time, end_time);
		}
		 
		if(distrDataList == null) {
			return new JSONArray();
		}
		
		//List<DistriCompare> compareList = sortDataInfo(distrDataList);
		//System.out.println("compareList size:" + compareList.size());
		Comparator<DistrDataInfo> comp = Collections.reverseOrder();
		Collections.sort(distrDataList, comp);
		
		JSONArray dataValueArray = new JSONArray();
		List<Double> dataValueList = new ArrayList<Double>();
		//JSONArray dataIdArray = new JSONArray();
		JSONArray dataNameArray = new JSONArray();
		Double sum = 0.0;
		
		for(DistrDataInfo dataInfo : distrDataList) {
			Double value = dataInfo.getValue();
			dataValueArray.add(value);
			dataValueList.add(value);
			sum += value;
			
			String name = dataInfo.getDataName();
			String[] nameArray = name.split(":");
			if(nameArray.length <= 1) {
				dataNameArray.add(name);
				continue;
			}
			name = nameArray[1];
			dataNameArray.add(name);
		}
		
		JSONArray dataPercentArray = toPercent(dataValueList, sum);
		String name = (m_request.getParameter("data_info[0][distr_name]") == null)? "" : m_request.getParameter("data_info[0][distr_name]");
		System.out.println("dataValueArray:" + dataValueArray.toString());
		System.out.println("dataNameArray:" + dataNameArray.toString());
		System.out.println("dataPercentArray:" + dataPercentArray.toString());
		
		
		return combination(name, dataValueArray, dataNameArray, dataPercentArray);
	}
	
	private List<DistrDataInfo> getCommonDirInfos(String start_time,String end_time) {
		Integer global_id = getServerId(gameId, platformId, zoneId, serverId, m_logMgrService);
		if(global_id == -1 || start_time.isEmpty() || end_time.isEmpty()) {
			System.out.println("parama error!!!");
			return null;
		}
		Integer relatedId = null;
		if (type ==1){
			relatedId = artifact_id;
		} else if (type == 0) {
			relatedId = schema_id;
		} else {
			return null;
		}
		
		if (relatedId == null) {
			return null;
		}
		System.out.println("relatedId:" + relatedId + " global_id:" + global_id + " start_time:" + start_time + " end_time:" + end_time + 
				" type:" + type + " timeDimension:" + timeDimension);
		List<DistrDataInfo> distrDataList = m_logMgrService.getDistrDataInfos(relatedId, global_id, start_time, end_time, type, timeDimension);
		
		return distrDataList;
	}
	
	private List<DistrDataInfo> getGpzsDirInfos(String start_time,String end_time) {
		Integer relatedId = null;
		if (type ==1){
			relatedId = artifact_id;
		} else if (type == 0) {
			relatedId = schema_id;
		} else {
			return null;
		}
		
		Integer id = null;
		switch(style) {
			case "common":
				break;
			case "platform":
				id = platformId;
				break;
			case "zone":
				id = zoneId;
				break;
			case "server":
				id = serverId;
				break;
			default:
				break;
		}
		
		if(id == null || id == -1) {
			return null;
		}
		
		System.out.println("relatedId:" + relatedId + " id:" + id + " type:" + type);
		List<DistrDataInfo> distrDataList = m_logMgrService.getGPZSDistrInfos(relatedId, id, start_time, end_time, type); 
	
		return distrDataList;
	}
	
	private JSONArray toPercent(List<Double> list, Double sum) {
		JSONArray dataPercentArray = new JSONArray();
		
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		
		for(Double value: list) {
			Double percent = value/sum;
			dataPercentArray.add(nt.format(percent));
		}
		
		return dataPercentArray;
	}
	
	private JSONArray combination(String name, JSONArray dataValueArray, JSONArray dataNameArray, JSONArray dataPercentArray) {
		JSONObject level1 = new JSONObject();
		JSONArray dataDoubleArray = new JSONArray();
		for(int i = 0; i < dataValueArray.size(); i++) {
			Double data = dataValueArray.getDouble(i) * factor;
			dataDoubleArray.add(data);
		}
		
		level1.put("data", dataDoubleArray);
		level1.put("name", name);
		level1.put("percentage", dataPercentArray);
		
		JSONArray level2 = new JSONArray();
		level2.add(level1);
		
		JSONObject level3 = new JSONObject();
		level3.put("data", level2);
		System.out.println("key:"+dataNameArray);
		level3.put("key", dataNameArray);
		
		JSONArray level4 = new JSONArray();
		level4.add(level3);
		
		return level4;
		
	}
}

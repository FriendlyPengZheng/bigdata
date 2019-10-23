package com.taomee.tms.mgr.tools.email;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.ResultInfo;
import com.taomee.tms.mgr.tools.DateTools;

public class DataResultTools {
	protected LogMgrService logMgrService;
	
	private DateTools dateTool;
	
	public DataResultTools(LogMgrService logMgrService) {
		this.logMgrService = logMgrService;
		this.dateTool = new DateTools();
	}
	
	public List<DataResultInfo> getDataResult(List<Integer> dataIdList, Integer serverId, String startTime, String endTime, Integer timeDimension, String expre) {
		//获取数据库中的数据
		List<List<ResultInfo>> dataListList = new ArrayList<List<ResultInfo>>();
		for(Integer dataId: dataIdList) {
			List<ResultInfo> dataInfos = logMgrService.getValueInfo(dataId, serverId, startTime, endTime, timeDimension);
			if(dataInfos == null || dataInfos.isEmpty()) {
				System.out.println("DataResultTools.getDataResult:  dataInfos is empty!");
				return null;
			}
			dataListList.add(dataInfos);
		}
		
		if(dataListList.isEmpty()) {
			System.out.println("dataListList is empty!");
			return null;
		}
		
		Integer basicSize = dataListList.get(0).size();
		for(List<ResultInfo> list:dataListList) {
			if(!basicSize.equals(list.size())) {
				System.out.println("Data result list size error!");
				return null;
			}
		}
		//将数据库中数据并转换
		List<List<ResultInfo>> convertListList = new ArrayList<List<ResultInfo>>();
		for(int i = 0; i < dataListList.get(0).size(); i++) {
			List<ResultInfo> convertList = new ArrayList<ResultInfo>();
			ResultInfo basicData = dataListList.get(0).get(i);
			for(int j = 0; j < dataIdList.size(); j++) {
				ResultInfo tmp = dataListList.get(j).get(i);
				//检查数据日期是否一致
				if(tmp.getTime() != basicData.getTime()) {
					System.out.println("Date is error: tmpTime["+basicData.getTime()+"] basicTime["+tmp.getTime()+"]");
					return null;
				}
				convertList.add(tmp);
			}
			
			convertListList.add(convertList);
		}
		
		//计算结果
		List<DataResultInfo> resultList = new ArrayList<DataResultInfo>();
		for(List<ResultInfo> convertList:convertListList) {
			List<Double> valueList = new ArrayList<Double>();
			for(ResultInfo dataInfo: convertList) {
				valueList.add(dataInfo.getValue());
			}
			DataResultInfo resultInfo = new DataResultInfo();
			resultInfo.setDate(dateTool.timeStamptoStirng(convertList.get(0).getTime(), "MM-dd"));
			resultInfo.setDataValue(ExpreConvertTools.getValueByExpre(valueList, expre));
			
			resultList.add(resultInfo);
			
		}
		
		return resultList;
	}
	
	public DataResultInfo getSingleDataResult(List<Integer> dataIdList, Integer serverId, String time, Integer timeDimension, String expre) {
		List<DataResultInfo> resultList = getDataResult(dataIdList, serverId, time, time, timeDimension, expre);
		if(resultList == null || resultList.size() != 1) {
			System.out.println("Get resultList failed, time:" + time);
			return null;
		}
		
		return resultList.get(0);
	}
	
	public List<DataResultInfo> getDataList(List<Integer> dataIdList, Integer serverId, List<String> timeList, Integer timeDimension, String expre) {
		List<DataResultInfo> result = new ArrayList<DataResultInfo>();
		if(timeList == null || timeList.isEmpty()) {
			return null;
		}
		
		for(String time:timeList) {
			DataResultInfo tmp = getSingleDataResult(dataIdList, serverId, time, timeDimension, expre);
			if(tmp == null) {
				result.add(new DataResultInfo());
				continue;
			}
			result.add(tmp);
		}
		return result;
	}
	
	
	public List<DataResultInfo> getContrastDataList(List<Integer> dataIdList, Integer serverId, List<String> timeList, Integer timeDimension, String expre) {
		List<DataResultInfo> result = new ArrayList<DataResultInfo>();
		if(timeList == null || timeList.isEmpty()) {
			return null;
		}
		
		DataResultInfo basicData = getSingleDataResult(dataIdList, serverId, timeList.get(0), timeDimension, expre);
		if(basicData == null){
			return null;
		}
		result.add(basicData);
		List<String> contrastList = timeList.subList(1, timeList.size());
		for(String time:contrastList) {
			DataResultInfo tmp = getSingleDataResult(dataIdList, serverId, time, timeDimension, expre);
			if(tmp == null) {
				result.add(new DataResultInfo());
		  		continue;
			}
			tmp.setContrastRate(getContrast(basicData.getDataValue(), tmp.getDataValue()));
			result.add(tmp);
		}
		return result;
	}

	public String getContrast(Double data1, Double data2) {
		if(data1 == null || data1.equals(0D)) {
			return "0.00%";
		}
		
		Double result = (data2-data1)/data2;
		
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return nt.format(Math.abs(result));
	}
	
	public Double getContrastDouble(Double data1, Double data2) {
		return (data2-data1)/data2;
	}
}

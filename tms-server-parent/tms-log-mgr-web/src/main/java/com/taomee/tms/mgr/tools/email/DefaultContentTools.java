package com.taomee.tms.mgr.tools.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.GenericPair;
import com.taomee.tms.mgr.tools.IntegerTools;

public abstract class DefaultContentTools {
	protected DateTools dateTool = new DateTools();
	
	public abstract String getMessageBody();
	public abstract Map<String, String> getMessagePic();
	public abstract List<GenericPair<String,List<String>>> getExcelData();
	
	protected List<Integer> setDataIdList(String dataString) {
		List<Integer> dataIdList = new ArrayList<Integer>();
		List<String> dataStringList= Arrays.asList(dataString.split(","));
		for(String dataIdString: dataStringList) {
			Integer dataId = IntegerTools.safeStringToInt(dataIdString);
			if(dataId.equals(0)) {
				//dataIdList.clear();
				return null;
			}
			dataIdList.add(dataId);
		}
		return dataIdList;
	}
	
	protected String getExpre(String info) {
		String[] expreSplit = info.split("\\|");
		
		if(expreSplit.length < 2) {
			System.out.println("Expre form error, expre:" + info);
			return null;
		}
		
		return expreSplit[1];
	}
	
	protected List<Integer> getDataList(String info) {
		String[] expreSplit = info.split("\\|");
		
		if(expreSplit.length < 2) {
			System.out.println("Expre form error, expre:" + info);
			return null;
		}
		
		return setDataIdList(expreSplit[0]);
	}
	
	protected List<String> getTimeList(String type, Integer offer) {
		List<String> result = new ArrayList<String>();
		if(type.equals("DAY")) {
			result.add(dateTool.getDayDate(offer));
			result.add(dateTool.getDayDate(-1 + offer));
			result.add(dateTool.getDayDate(-7 + offer));
			result.add(dateTool.getDayDate(-28 + offer));
		} else if(type.equals("MONTH")) {
			result.add(dateTool.getMonthDate(-1 + (offer/30)));
			result.add(dateTool.getMonthDate(-2 + (offer/30)));
			result.add(dateTool.getMonthDate(-13 + (offer/30)));
		}
		return result;
	}
	
	protected List<String> getYearTimeList(String type, Integer offer) {
		List<String> result = new ArrayList<String>();
		if(type.endsWith("MONTH")) {
			for(int i = 1; i <= 13; i++) {
				result.add(dateTool.getMonthDate(-i+ (offer/30)));
				//result.add(dateTool.getMonthDate(-i-2));
			}
		} else {
			return null;
		}
		
		return result;
	}
	
	protected String getContrastString(DataResultInfo data1, DataResultInfo data2, String percent) {
		if(data1 == null || data2 == null || percent == null) {
			return "";
		}
		if(percent.contains("%")) {
			percent = percent.replace("%", "");
		}
		percent = percent.replace(",", "");
		Double tmp = Double.valueOf(percent);
		
		String colour = "";
		if(tmp > 5D) {
			if(data1.getDataValue() > data2.getDataValue()) {
				colour = "#009933";
			} else {
				//colour = "#009933";
				colour = "#ff0000";
			}
		} else {
			colour = "#000000";
		}
		String result = "<span style=\"color:"+colour+";\">"+contrast(data1, data2)+percent+"%</span>";
		return result;
	}
	
	protected String contrast(DataResultInfo data1, DataResultInfo data2) {
		if(data1.getDataValue() > data2.getDataValue()) {
			return "下降";
		} else if (data1.getDataValue() < data2.getDataValue()) {
			return "上升";
		} else {
			return "持平";
		}
	}
}

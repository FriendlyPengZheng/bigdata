package com.taomee.tms.mgr.tools.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.GenericPair;

public class TableContentTools extends DefaultContentTools{	
	private DateTools dateTool;
	private DataResultTools getDataTool;
	private List<EmailDataInfo> emailDataList;
	private Boolean messageFlag;
	private String body;
	private Integer mOffer = -30;
	private String title;
	
	public TableContentTools(List<EmailDataInfo> emailDataList, DataResultTools getDataTool, String title) {
		this.body = "";
		this.emailDataList = emailDataList;
		this.messageFlag = false;
		
		this.dateTool = new DateTools();
		this.getDataTool= getDataTool;
		this.title = title;
	}

	@Override
	public String getMessageBody() {
		setMessage();
		return body;
	}

	@Override
	public Map<String, String> getMessagePic() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void setMessage() {
		if(messageFlag) {
			return;
		}
		
		this.body = "";
		
		Boolean dayFlag = false;
		Boolean monthFlag = false;
		List<List<DataResultInfo>> dataResult = new ArrayList<List<DataResultInfo>>();
		List<String> nameList = new ArrayList<String>();
		for(EmailDataInfo emailInfo:emailDataList) {
			List<String> timeList = getTimeList(emailInfo.getdataDateType(),mOffer);
			String expre = emailInfo.getdataExpr();
			if(expre == null || expre.isEmpty()) {
				continue;
			}
			
			List<DataResultInfo> tmp = null;
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!emailInfo:" + JSON.toJSONString(emailInfo));
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!timeList:" + JSON.toJSONString(timeList));
			if(emailInfo.getdataDateType().equals("DAY")) {
				dayFlag = true;
				tmp = getDataTool.getContrastDataList(getDataList(expre), emailInfo.getServerId(), timeList, 0, getExpre(expre));
			} else if(emailInfo.getdataDateType().equals("MONTH")) {
				monthFlag = true;
				tmp = getDataTool.getContrastDataList(getDataList(expre), emailInfo.getServerId(), timeList, 2, getExpre(expre));
			} else {
				continue;
			}
			
			if(tmp == null || tmp.isEmpty()){
				continue;
			}
			dataResult.add(tmp);
			nameList.add(emailInfo.getdataName());
		}
		
		if(dataResult.isEmpty()) {
			return;
		}
		
		if(dayFlag && !monthFlag) {
			body = setDayTable(dataResult, nameList, title);
		} else if(!dayFlag && monthFlag) {
			body = setMonthTable(dataResult, nameList, title, dateTool.getMonthDate());
		}
		
		messageFlag = true;
	}
	

	public String setDayTable(List<List<DataResultInfo>> dataResult, List<String> nameList, String title) {
		if(dataResult == null || dataResult.isEmpty() ) {
			return "";
		}
		
		if(dataResult.size() != nameList.size()) {
			return "";
		}
		
		String result = "";
		
		result += "<table width=\"1000\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"table\">";
		if(title != null && !title.isEmpty()) {
			result += "<tr> <th valign=\"top\" align=\"center\" colspan=\"5\">"+ title +"</th></tr>";
		}
		result += "<tr style=\"background-color:#11a2bb;\">";
		if(title != null && !title.isEmpty()) {
			result += "<th valign=\"top\">项目名称</th>";
		} else {
			result += "<th valign=\"top\">指标</th>";
		}
		result += "<th valign=\"top\">当天</th>";
		result += "<th valign=\"top\">较前一天</th>";
		result += "<th valign=\"top\">教上周同期</th>";
		result += "<th valign=\"top\">教上月同期</th>";
		result += "</tr>";
		
		Integer i = 0;
		for(List<DataResultInfo> dataList:dataResult) {
			//System.out.println("dataList:" + JSON.toJSONString(dataList));
			String backCol = "";
			if(new Integer(i%2).equals(1)) {
				backCol = "#E8F8F7";
			} else {
				backCol = "#FFFFFF";
			}
			
			if(dataList == null || dataList.size() < 4) {
				continue;
			}
			
			String name = dataList.get(0).getDataName();
			Double basicData = dataList.get(0).getDataValue();
			result += "<tr>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+nameList.get(i)+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+basicData+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+dataList.get(1).getDataValue()+"("+getContrastString(dataList.get(1), dataList.get(0), dataList.get(1).getContrastRate())+")</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+dataList.get(2).getDataValue()+"("+getContrastString(dataList.get(2), dataList.get(0), dataList.get(2).getContrastRate())+")</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+dataList.get(3).getDataValue()+"("+getContrastString(dataList.get(3), dataList.get(0), dataList.get(3).getContrastRate())+")</td>";
			result += "</tr>";
			i++;
		}
		result += "</table>";
		result += "<br />";
		
		//System.out.println("Table!!!!!:" + result);
		return result;
	}
	
	
	public String setMonthTable(List<List<DataResultInfo>> dataResult, List<String> nameList, String title, String time) {
		String result = "";
		result += "<table width=\"1000\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"table\">";
		if(title != null && !title.isEmpty()) {
			result += "<tr style=\"background-color:#11a2bb;\"><th valign= \"top\" align= \"center\" colspan= \"6\">"+title+"</th></tr>";
		}
		
		result += "<tr style=\"background-color:#11a2bb;\">";
		if(time != null && !time.isEmpty()) {
			result += "<th valign=\"top\">"+time+"</th>";
		}
		result += "<th valign=\"top\">本月</th>";
		result += "<th valign=\"top\">上月</th>";
		result += "<th valign=\"top\">环比变化率</th>";
		result += "<th valign=\"top\">去年同期</th>";
		result += "<th valign=\"top\">同比变化率</th>";
		result += "</tr>";
		
		Integer i = 0;
		for(List<DataResultInfo> dataList:dataResult) {
			String backCol = "";
			if(new Integer(i%2).equals(1)) {
				backCol = "#E8F8F7";
			} else {
				backCol = "#FFFFFF";
			}
			
			if(dataList == null || dataList.size() != 3) {
				continue;
			}
			System.out.println("dataList:" + JSON.toJSONString(dataList));
			Double basicData = dataList.get(0).getDataValue();
			result += "<tr>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+nameList.get(i)+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+basicData+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+dataList.get(1).getDataValue()+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+getContrastString(dataList.get(1), dataList.get(0), dataList.get(1).getContrastRate())+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+dataList.get(2).getDataValue()+"</td>";
			result += "<td valign=\"top\" style=\"background-color:"+backCol+";\">&nbsp;"+getContrastString(dataList.get(2), dataList.get(0), dataList.get(2).getContrastRate())+"</td>";
			result += "</tr>";
			i++;
		}
		
		result += "</table>";
		result += "<br />";
		return result;
	}

	@Override
	public List<GenericPair<String, List<String>>> getExcelData() {
		// TODO Auto-generated method stub
		return null;
	}

}

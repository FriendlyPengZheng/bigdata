package com.taomee.tms.mgr.tools.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.GenericPair;
import com.taomee.tms.mgr.tools.IntegerTools;
import com.taomee.tms.mgr.tools.chart.LineChartTools;

public class MixContentTools extends DefaultContentTools{
	
	private DateTools dateTool;
	private DataResultTools getDataTool;
	
	private String body;
	private Map<String, String> picMap;	
	private Boolean messageFlag;
	
	private EmailDataInfo emailDataInfo;
	private List<EmailDataInfo> emailDataList;
	private String expre;
	private List<Integer> dataIdList;
	
	private Integer mOffer = -30;
		
	public MixContentTools(List<EmailDataInfo> emailDataList, DataResultTools getDataTool) {
		this.body = "";
		this.picMap = new HashMap<String, String>();
		this.messageFlag = false;
		//this.emailDataInfo = emailDataList.get(0);
		this.emailDataList = emailDataList;
		this.expre = new String();
		this.dataIdList = new ArrayList<Integer>();
		
		this.dateTool = new DateTools();
		this.getDataTool= getDataTool;
	}
	
	@Override
	public String getMessageBody() {
		setMessage();
		//System.out.println("body:" + body);
		return body;
	}
	
	@Override
	public Map<String, String> getMessagePic() {
		setMessage();
		//System.out.println("picMap:" + JSON.toJSONString(picMap)); 
		return picMap;
	}
	
	private void setMessage() {
		//System.out.println("setMessage!!!!");
		if(messageFlag) {
			System.out.println("MessageFlag is already true.");
			return;
		}
		
		if(emailDataList.size() != 1) {
			System.out.println("EmailDataList size is not one.");
			return;
		}
		
		this.body = "";
		this.picMap.clear();
		
		this.emailDataInfo = this.emailDataList.get(0);
		
		//获取数据列
		if(!setCalculatorInfo()) {
			System.out.println("Set calculator info failed.");
			return;
		}
		
		if(emailDataInfo.getinGraph() == 1) {
			setGraph();
		}
		
		if(emailDataInfo.getinTable() == 1) {
			setTable();
		}
		
		messageFlag = true;
	}
	
	private Boolean setGraph() {
		
		List<DataResultInfo> dataResultList;
		if(emailDataInfo.getdataDateType().equals("MINUTE")) {
			//TODO
			dataResultList = new ArrayList<DataResultInfo>();
		} else {
			String startTime = null;
			String endTime = null;
			Integer timeDimension = null;
			if(emailDataInfo.getdataDateType().equals("DAY")){
				startTime = dateTool.getDayDate(-29 + mOffer);
				endTime = dateTool.getDayDate(mOffer);
				timeDimension = 0;
			} else {
				//TODO
				return false;
			}
			
			System.out.println("dataIdList:" + JSON.toJSONString(dataIdList) + " startTime:" + startTime + " endTime:" + endTime + " timeDimension:" + timeDimension + " expre:" + expre);
			dataResultList = getDataTool.getDataResult(dataIdList, emailDataInfo.getServerId(), startTime, endTime, timeDimension, expre);
			System.out.println("dataResultList:" + JSON.toJSONString(dataResultList));
			if(dataResultList == null || dataResultList.isEmpty()) {
				System.out.println("Get dataResultList failed.");
				return false;
			}
		}
		
		//将数据列化成图片并保存
		LineChartTools chartTool = new LineChartTools();
		Double lower = Double.MAX_VALUE;
		Double upper = Double.MIN_VALUE;
		Map<String, List<GenericPair<String, Number>>> lineMap = new HashMap<String, List<GenericPair<String, Number>>>();
		List<GenericPair<String, Number>> lineDatas = new ArrayList<GenericPair<String, Number>>();
		for(DataResultInfo tmp:dataResultList) {
			GenericPair<String, Number> pair = new GenericPair<String, Number>();
			Double value = tmp.getDataValue();
			lower = (value < lower) ? value:lower;
			upper = (value > upper) ? value:upper;
			pair.setFirst(tmp.getDate());
			pair.setSecond(value);
			lineDatas.add(pair);
		}
		lineMap.put(emailDataInfo.getdataName(), lineDatas);
		
		//获取图片名称,格式emailDataId_pic
		String picName = emailDataInfo.getemailDataId()+"_pic";
		String picShow = "<img src=\"cid:"+picName+"\">";
		//设置文件内容
		body = picShow + body;
		
		JFreeChart chart = chartTool.createChart(chartTool.getDataset(lineMap), "", "", emailDataInfo.getdataName());
		String picFile = "C:\\Users\\maggie\\Pictures\\test\\" + picName+".jpeg";
		
		System.out.println("lower:" + lower + " upper:" + upper);
		chart = chartTool.setRang(chart, lower*0.9, upper*1.1);
		//chartTool.showChart(chart);
		chartTool.saveJPEG(chart, picFile, 1000, 400);
		
		//将数据列和图片地址保存到picMap中
		picMap.put(picName, picFile);
		
		return true;
	}
	
	private void setTable() {
		//获取四天比较数据
		if(!emailDataInfo.getdataDateType().equals("DAY")) {
			return;
		}
		
		String todayTime = dateTool.getDayDate(mOffer); 
		String yesterdayTime = dateTool.getDayDate(-1 + mOffer);
		String lastWeekTime = dateTool.getDayDate(-7 + mOffer);
		String lastMonthTime = dateTool.getDayDate(-28 + mOffer);
		
		DataResultInfo todayInfo = getDataTool.getSingleDataResult(dataIdList, emailDataInfo.getServerId(), todayTime, 0, expre);
		DataResultInfo yesterdayInfo = getDataTool.getSingleDataResult(dataIdList, emailDataInfo.getServerId(), yesterdayTime, 0, expre);
		DataResultInfo lastWeekInfo = getDataTool.getSingleDataResult(dataIdList, emailDataInfo.getServerId(), lastWeekTime, 0, expre);
		DataResultInfo lastMonthInfo = getDataTool.getSingleDataResult(dataIdList, emailDataInfo.getServerId(), lastMonthTime, 0, expre);
		
		//将数据放入body中
		String unit = emailDataInfo.getUnit();
		if(todayInfo != null) {
			body = body + "<p> 【<span class=\"fb\">"+emailDataInfo.getdataName()+"</span>】<span class=\"fb\">" + todayInfo.getDataValue()+unit+"</span>；";
		} else {
			return;
		}
		
		if(yesterdayInfo != null) {
			String contrastRate = getDataTool.getContrast(todayInfo.getDataValue(), yesterdayInfo.getDataValue());
			String contrast = contrast(todayInfo, yesterdayInfo);
			body = body + "较前一天（<span class=\"fb\">"+yesterdayInfo.getDataValue() + unit +"</span>）<span class=\"fb\">"+contrast + contrastRate +"</span>；";
		}
		
		if(lastWeekInfo != null) {
			String contrastRate = getDataTool.getContrast(todayInfo.getDataValue(), lastWeekInfo.getDataValue());
			String contrast = contrast(todayInfo, lastWeekInfo);
			body = body + "较前一天（<span class=\"fb\">"+lastWeekInfo.getDataValue() + unit +"</span>）<span class=\"fb\">"+contrast + contrastRate +"</span>；";
		}
		
		if(lastMonthInfo != null) {
			String contrastRate = getDataTool.getContrast(todayInfo.getDataValue(), lastMonthInfo.getDataValue());
			String contrast = contrast(todayInfo, lastMonthInfo);
			body = body + "较前一天（<span class=\"fb\">"+lastMonthInfo.getDataValue() + unit +"</span>）<span class=\"fb\">"+contrast + contrastRate +"</span>；</p>";
		}
		
	}
	
	private Boolean setCalculatorInfo() {
		String expre = emailDataInfo.getdataExpr();
		this.expre = getExpre(expre);
		if(expre == null || expre.isEmpty()) {
			return false;
		}
		
		dataIdList = getDataList(expre);
		if(dataIdList == null || dataIdList.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public List<com.taomee.tms.mgr.tools.GenericPair<String, List<String>>> getExcelData() {
		// TODO Auto-generated method stub
		return null;
	}

}

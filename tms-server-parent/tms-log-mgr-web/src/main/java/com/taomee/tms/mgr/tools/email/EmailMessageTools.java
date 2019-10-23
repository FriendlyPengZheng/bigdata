package com.taomee.tms.mgr.tools.email;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jxl.write.WriteException;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.EmailConfigInfo;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.entity.EmailTemplateContentInfo;
import com.taomee.tms.mgr.entity.ResultInfo;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.DoubleTools;
import com.taomee.tms.mgr.tools.GenericPair;
import com.taomee.tms.mgr.tools.excle.CellInfo;
import com.taomee.tms.mgr.tools.excle.ExcelConvertTools;
import com.taomee.tms.mgr.tools.excle.ExcelTreeNode;
import com.taomee.tms.mgr.tools.excle.Point;

//获取所有message信息的
public class EmailMessageTools {
	private LogMgrService logMgrService;
	
	private EmailConfigInfo configInfo;
	private String frequencyType;
	
	private List<String> messageBodyList;
	private Map<String, String> messagePicture;
	private List<GenericPair<String, List<String>>> messageExcel;
	private File messageAttachment;
	
	public EmailMessageTools(EmailConfigInfo configInfo, String frequencyType, LogMgrService logMgrService) {
		this.configInfo = configInfo;
		this.frequencyType = frequencyType;
		this.logMgrService = logMgrService;
		
		this.messageBodyList = new ArrayList<String>();
		this.messagePicture = new HashMap<String,String>();
		this.messageExcel = new LinkedList<GenericPair<String, List<String>>>();
		this.messageAttachment = null;
	}
	
	public Map<String, String> getEmailPicture() {
		//setInfo();
		System.out.println("messagePicture:" + JSON.toJSONString(messagePicture));
		return messagePicture;
	}
	
	public String getEmailBody() {
		//设置message
		//setInfo();
		
		String messageBody = new String();
		for(String info:messageBodyList) {
			messageBody += info;
		}
		
		System.out.println("messageBody:" + messageBody);
		return messageBody;
	}
	
	public File getMessageFile() {
		return this.messageAttachment;
	}
	
	public boolean action() {
		List<EmailTemplateContentInfo> contentList = getContentInfos();
		if(contentList.isEmpty()) {
			return false;
		}
		
		for(EmailTemplateContentInfo contentInfo:contentList) {
			List<EmailDataInfo> emailDataList = getDataInfos(contentInfo.getEmailTemplateContentId());
			if(emailDataList == null || emailDataList.isEmpty()) {
				continue;
			}
			
			DefaultContentTools tool;
			switch(contentInfo.getContentType()) {
			case "TABLE" :
				tool = new TableContentTools(emailDataList, new DataResultTools(logMgrService), contentInfo.getContentTitle());
				messageBodyList.add(tool.getMessageBody());
				break;
			case "MULTI_GAME_TABLE":
				//System.out.println("MULTI_GAME_TABLE emailDataList:" + JSON.toJSONString(emailDataList));
				tool = new TableContentTools(emailDataList, new DataResultTools(logMgrService), contentInfo.getContentTitle());
				messageBodyList.add(tool.getMessageBody());
				break;
			case "EXCEL":
				//System.out.println("EXCEL emailDataList:" + JSON.toJSONString(emailDataList));
				tool = new ExcelContentTools(emailDataList, new DataResultTools(logMgrService), contentInfo.getContentTitle());
				messageExcel.addAll(tool.getExcelData());
				break;
			default:
				//TODO
				//System.out.println("emailDataList:" + JSON.toJSONString(emailDataList));
				tool = new MixContentTools(emailDataList, new DataResultTools(logMgrService));
				messageBodyList.add(tool.getMessageBody());
				messagePicture.putAll(tool.getMessagePic());
				break;
			}
		}
		
		//生成excel文件
		//System.out.println("EXCEL list:" + JSON.toJSONString(this.messageExcel));
		if(messageExcel.isEmpty()) {
			System.out.println("messageExcel is empty!");
			return true;
		}
		
		ExcelConvertTools excelConvertTool = new ExcelConvertTools();
		for(GenericPair<String, List<String>> excelInfo:this.messageExcel) {
			System.out.println("first:" + excelInfo.getFirst() + " second:" + JSON.toJSONString(excelInfo.getSecond()));
			excelConvertTool.addInfo(excelInfo.getFirst(), excelInfo.getSecond());
		}
		//System.out.println(JSON.toJSON(excelConvertTool.getRoot()));
		try {
			excelConvertTool.writeToExcle("data_excel.xls", configInfo.getSubject());
			File file = new File("data_excel.xls");
			if(file.exists()) {
				this.messageAttachment = file;
			}
			
		} catch (WriteException |IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	private List<EmailTemplateContentInfo> getContentInfos() {
		//System.out.println("frequencyType:" + frequencyType);
		Integer emailTemplateId = logMgrService.getEmailTemplateId(frequencyType);
		List<EmailTemplateContentInfo> contentList = logMgrService.getEmailTemplateContentInfo(emailTemplateId);
		return contentList;
	}
	
	private List<EmailDataInfo> getDataInfos(Integer contentId) {
		return logMgrService.getEmailInfoByContentId(configInfo.getemailId(), contentId);
	}
}

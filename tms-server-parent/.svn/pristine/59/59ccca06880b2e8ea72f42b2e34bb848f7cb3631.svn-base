package com.taomee.tms.mgr.tools.weixin;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.entity.EmailTemplateContentInfo;
import com.taomee.tms.mgr.tools.email.DataResultTools;
import com.taomee.tms.mgr.tools.email.DefaultContentTools;
//import com.taomee.tms.mgr.tools.email.ExcelContentTools;
//import com.taomee.tms.mgr.tools.email.MixContentTools;
import com.taomee.tms.mgr.tools.email.TableContentTools;

public class WXMessageTools {
	private LogMgrService logMgrService;
	
	private Integer emailId;
	private String frequencyType;
	private Integer serverId;
	private String result;
	
	public WXMessageTools(Integer emailId, String frequencyType, LogMgrService logMgrService) {
		this.emailId = emailId;
		this.frequencyType = frequencyType;
		this.logMgrService = logMgrService;
		this.result = "";
	}
	
	public String getMessage() {
		List<EmailTemplateContentInfo> contentList = getContentInfos();
		if(contentList.isEmpty()) {
			return null;
		}
		
		for(EmailTemplateContentInfo contentInfo:contentList) {
			List<EmailDataInfo> emailDataList = getDataInfos(contentInfo.getEmailTemplateContentId());
			if(emailDataList == null || emailDataList.isEmpty()) {
				continue;
			}
			
			DefaultContentTools tool;
			switch(contentInfo.getContentType()) {
			case "TABLE" :
				tool = new TableContentTools(emailDataList,new DataResultTools(logMgrService), contentInfo.getContentTitle());
				result += tool.getMessageBody();
				break;
			case "MULTI_GAME_TABLE":
				tool = new TableContentTools(emailDataList, new DataResultTools(logMgrService), contentInfo.getContentTitle());
				result += tool.getMessageBody();
				break;
			default:
				//TODO
			}
		}
		
		System.out.println("####result:"+ JSON.toJSONString(result));
		return result + getNotes();
	}
	
	private List<EmailTemplateContentInfo> getContentInfos() {
		Integer emailTemplateId = logMgrService.getEmailTemplateId(frequencyType);
		List<EmailTemplateContentInfo> contentList = logMgrService.getEmailTemplateContentInfo(emailTemplateId);
		return contentList;
	}
	
	private List<EmailDataInfo> getDataInfos(Integer contentId) {
		return logMgrService.getEmailInfoByContentId(emailId, contentId);
	}
	
	private String getNotes() {
		return "<p style='line-height:10px'>&nbsp;</p>"
				+"<p style=\'line-height:12px;color:#808080;font-size:12px'>注：<br>上周同期往前推7天，上月同期往前推28天；<br>上升高于5%，红色标示，下降低于-5%，绿色标示；<br>所有付费相关数据均不包含短信续费。</p>"
				+"<p style='line-height:6px'>&nbsp;</p>"
				+"<p style='line-height:12px;color:#808080;font-size:12px'>数据定义：<br>1、新增用户数：首次登录游戏的米米号数；<br>2、Arppu：平均每付费用户收入；<br>3、付费率：付费用户占活跃用户的比例。</p>";
	}

}

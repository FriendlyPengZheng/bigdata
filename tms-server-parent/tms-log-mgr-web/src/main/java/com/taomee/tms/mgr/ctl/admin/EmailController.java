package com.taomee.tms.mgr.ctl.admin;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.ctl.common.GameController;
import com.taomee.tms.mgr.entity.EmailConfigInfo;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.entity.EmailTemplateContentInfo;
import com.taomee.tms.mgr.entity.EmailTemplateDataInfo;
import com.taomee.tms.mgr.entity.GameInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.tools.BooleanTools;
import com.taomee.tms.mgr.tools.FileOperateTools;
import com.taomee.tms.mgr.tools.IntegerTools;
import com.taomee.tms.mgr.tools.LongTools;
import com.taomee.tms.mgr.tools.DataCaculator.AbstactCaculateDataTools;
import com.taomee.tms.mgr.tools.email.EmailMessageTools;
import com.taomee.tms.mgr.tools.email.EmailTools;
import com.taomee.tms.mgr.tools.weixin.WXMessageTools;
import com.taomee.tms.mgr.tools.weixin.WeiXinTools;

@Controller
@RequestMapping("/admin/email")

public class EmailController {
	private static final Logger logger = LoggerFactory
			.getLogger(GameController.class);
	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	/*@SuppressWarnings("unused")
	private static String MINUTE = "MINUTE";
	@SuppressWarnings("unused")
	private static String DAY = "DAY";
	@SuppressWarnings("unused")
	private static String MONTH = "MONTH";*/
	
	//显示数据
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		List<EmailConfigInfo> emailConfigList = logMgrService.getEmailConfigAll();
		
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		String receviers = "";
		String testReceiver = "";
		Long lastSendTimeStamp = System.currentTimeMillis();
		String lastSendUser = "";
		if(emailId == 0) {
			emailId = emailConfigList.get(0).getemailId();
		}
		
		for(Iterator<EmailConfigInfo> it = emailConfigList.iterator();it.hasNext();){ 
			EmailConfigInfo email = it.next();
			email.setUrl("http://maggie222.taomee.net/tms-log-mgr-web/admin/email/index/01?email_id=" + email.getemailId());
			//System.out.println("id:" + email.getemailId() + " subject:" + email.getSubject() + "current");
			if(email.getemailId().equals(emailId)) {
				email.setCurrent(1);
				receviers = email.getReceviers();
				testReceiver = email.getTestReceiver();
				if(email.getLastSendTime() < lastSendTimeStamp) {
					lastSendTimeStamp = email.getLastSendTime();
					lastSendUser = email.getLastSendUser();
				}
			} else {
				email.setCurrent(0);
			}
		}
		
		SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		String lastSendTime = format.format(lastSendTimeStamp);
		
		//获取info数据
		List<EmailDataInfo> emailDataList = logMgrService.getEmailInfoByEmailId(emailId);
	
		//获取manageUrl
		String manageUrl = "http://maggie222.taomee.net/tms-log-mgr-web/admin/email/manage/01?email_id=" + emailId;
		
		//获取频率
		EmailConfigInfo configInfo = logMgrService.getEmailConfigByEmailId(emailId);
		
		request.setAttribute("emails", emailConfigList);
		request.setAttribute("receviers", receviers);
		request.setAttribute("test_receiver", testReceiver);
		request.setAttribute("last_send_time", lastSendTime);
		request.setAttribute("last_send_user", lastSendUser);
		request.setAttribute("datalist", emailDataList);
		request.setAttribute("manage_url", manageUrl);
		request.setAttribute("email_id", emailId);
		request.setAttribute("frequency_type", configInfo.getFrequencyType());
		return "kernel/daydata";
	}
	
	//显示管理页面
	@RequestMapping(value = "/manage/{id}")
	public String manage(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		List<EmailConfigInfo> emailConfigList = logMgrService.getEmailConfigAll();
		request.setAttribute("emaillist", emailConfigList);
		
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		if(emailId == 0) {
			emailId = emailConfigList.get(0).getemailId();
		}
		
		String bakUrl = "http://maggie222.taomee.net/tms-log-mgr-web/admin/email/index/01?email_id=" + emailId;
		request.setAttribute("bak_url", bakUrl);
		return "kernel/manage";
	}
	
	//保存数据
	@RequestMapping(value = "/apply")
	public void apply(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		Integer platformId = IntegerTools.safeStringToInt(request.getParameter("platform_id"));
		String zondServerId = request.getParameter("gpzs_id");
		String[] tmp = zondServerId.split("_");
		Integer zoneId = IntegerTools.safeStringToInt(tmp[0]);
		Integer sId = IntegerTools.safeStringToInt(tmp[1]);
		
		System.out.println("gameId:" + gameId+" platformId:" + platformId + " zoneServer:" + zondServerId);
		if(gameId == 0 || platformId == 0 || zoneId == 0 || sId == 0) {
			printWriter.write("{\"result\":-1,\"data\":"  +"获取区服号失败"+ "}");
			logger.error("get serverId error");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		//如果是汇总的情况
		List<Integer> serverIdList = new ArrayList<Integer>();
		Boolean flag = false;
		if(gameId == -2) {
			String dependenciesStr = request.getParameter("dependencies");
			if(dependenciesStr == null || dependenciesStr.isEmpty()) {
				logger.error("get serverId error");
				printWriter.flush();
				printWriter.close();
				return;
			}
			
			JSONObject dependencies = JSON.parseObject(dependenciesStr);
			JSONArray emailIdList = dependencies.getJSONArray("email");
			for(int i = 0; i < emailIdList.size();i++) {
				EmailConfigInfo tmpInfo = logMgrService.getEmailConfigByEmailId(emailIdList.getInteger(i));
				if(tmpInfo == null) {
					System.out.println("EmailConfigInfo is empty!");
					continue;
				}
				Integer tmpServerId = tmpInfo.getServerId();
				if(tmpServerId <= 0) {
					System.out.println("tmpServerId is error:"+tmpServerId+"!");
					continue;
				}
				System.out.println("tmpServerId:"+tmpServerId);
				serverIdList.add(tmpServerId);
			}
			flag = true;
		} else {
			//如果不是总汇的情况
			Integer serverId = logMgrService.getServerIDByGPZS( gameId,  platformId , zoneId , sId);
			serverIdList.add(serverId);
		}
		
		System.out.print("serverIdList:");
		for(Integer id:serverIdList) {
			System.out.print(id + ",");
		}
		System.out.println("");
		
		String frequencyType = request.getParameter("frequency_type");
		if(frequencyType == null || frequencyType.isEmpty()) {
			printWriter.write("{\"result\":-1,\"data\":"  +"frequencyType error"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		EmailConfigInfo info = new EmailConfigInfo();
		try{
			info.setSubject(request.getParameter("subject"));
			if(flag) {
				info.setServerId(-2);
			} else {
				info.setServerId(serverIdList.get(0));
			}
			info.setGameId(gameId);
			info.setReceviers(request.getParameter("receviers"));
			info.setCc(request.getParameter("cc"));
			info.setTestReceiver(request.getParameter("test_receiver"));
			info.setWeixinMediaId(request.getParameter("weixin_media_id"));
			info.setWeixinRecev(request.getParameter("weixin_recev"));
			info.setFrequencyType(frequencyType);
			info.setDependencies(request.getParameter("dependencies"));
			info.setRemarks(request.getParameter("remarks"));
		}catch (Exception e) {
			printWriter.write("{\"result\":-1,\"data\":"  +e.getMessage()+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		
		//插入EmailConfig
		Integer emailId = 0;
		JSONObject result = new JSONObject();
		try{
			//插入数据到t_web_config
			emailId = logMgrService.insertEmailConfigInfo(info);
			result.put("email_id", emailId);
			//获取模版id
			Integer templateId;
			if(flag) {
				templateId = logMgrService.getEmailTemplateId("SUMMARY_" + frequencyType);
			} else {
				templateId = logMgrService.getEmailTemplateId(frequencyType);
			}
			//获取模版信息
			List<EmailDataInfo> dataList = new ArrayList<EmailDataInfo>();
			List<EmailTemplateContentInfo> templateContentInfos = logMgrService.getEmailTemplateContentInfo(templateId);
			for(EmailTemplateContentInfo contentInfo:templateContentInfos) {
				List<EmailTemplateDataInfo> templateDataInfos = logMgrService.getEmailTemplateData(contentInfo.getEmailTemplateContentId());
				for(EmailTemplateDataInfo templateDataInfo:templateDataInfos) {
					for(Integer serverId:serverIdList) {
						System.out.println("serverId:" + serverId);
						EmailDataInfo dataInfo = new EmailDataInfo();
						dataInfo.setEmailId(emailId);
						dataInfo.setEmailContentId(contentInfo.getEmailTemplateContentId());
						dataInfo.setContentTitle(contentInfo.getContentTitle());
						dataInfo.setdataDateType(templateDataInfo.getDataDateType());
						dataInfo.setdataExpr(templateDataInfo.getDataExpr());
						if(flag) {
							ServerInfo serverInfo = logMgrService.getServerInfo(serverId);
							if(serverInfo == null) {
								System.out.println("serverInfo is null");
								continue;
							}
							GameInfo gameInfo = logMgrService.getGameInfoById(serverInfo.getGameId());
							if(gameInfo == null) {
								System.out.println("GameInfo is null");
								continue;
							}
							dataInfo.setdataName(gameInfo.getGameName());
						}else{
							dataInfo.setdataName(templateDataInfo.getDataName());
						}
						dataInfo.setServerId(serverId);
						dataInfo.setOffset(templateDataInfo.getOffset());
						dataInfo.setUnit(templateDataInfo.getUnit());
						dataInfo.setinTable(templateDataInfo.getInTable());
						dataInfo.setinGraph(templateDataInfo.getInGraph());
						dataInfo.setThreshold("");
						dataList.add(dataInfo);
					}
				}
			}
			//插入数据
			for(EmailDataInfo dataInfo:dataList) {
				logMgrService.insertEmailDataInfo(dataInfo);
			}
		}catch (Exception e) {
			/*printWriter.write("{\"result\":-1,\"data\":"  +e.getMessage()+ "}");
			printWriter.flush();
			printWriter.close();*/
			System.out.println(e.getMessage());
			return;
		}
		
		printWriter.write("{\"result\":0,\"data\":"  +JSON.toJSONString(result)+ "}");
		printWriter.flush();
		printWriter.close();
		return;
	}
	
	//删除EmailConfig
	@RequestMapping(value = "/delete")
	public void delete(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		if(emailId == 0){
			printWriter.write("{\"result\":-1,\"data\":"  +"删除失败"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		Integer ret = logMgrService.deleteEmailConfigByEmailId(emailId);
		if(ret == 0) {
			printWriter.write("{\"result\":0}");
			printWriter.flush();
			printWriter.close();
		} else {
			printWriter.write("{\"result\":-1,\"data\":"  +"删除失败"+ "}");
			printWriter.flush();
			printWriter.close();
		}
		return;
	}
	
	//修改EmailConfig
	@RequestMapping(value = "/updateEmailConfig")
	public void updateEmailConfig(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		if(emailId == 0){
			printWriter.write("{\"result\":-1,\"data\":"  +"get message failed"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		EmailConfigInfo info = new EmailConfigInfo();
		try{
			info.setemailId(emailId);
			info.setReceviers(request.getParameter("receviers"));
			info.setTestReceiver(request.getParameter("test_receiver"));
			info.setCc(request.getParameter("cc"));
			info.setSubject(request.getParameter("subject"));
			info.setRemarks(request.getParameter("remarks"));
			info.setDependencies(request.getParameter("dependencies"));
			info.setWeixinMediaId(request.getParameter("weixin_media_id"));
			info.setWeixinRecev(request.getParameter("weixin_recev"));
			
		}catch (Exception e) {
			printWriter.write("{\"result\":-1,\"data\":"  +e.getMessage()+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		//System.out.println("configInfo:"+ JSON.toJSONString(info));
		//System.out.println("weixin_media_id:"+ info.getWeixinMediaId() + " wexin_recev:" + info.getWeixinRecev());
		
		Integer ret = logMgrService.updateEmailConfigInfo(info);
		System.out.println("update ret:" + ret);
		if(ret > 0) {
			printWriter.write("{\"result\":0}");
			printWriter.flush();
			printWriter.close();
		} else {
			printWriter.write("{\"result\":-1,\"data\":"  +"update failed"+ "}");
			printWriter.flush();
			printWriter.close();
		}
		return;
	}
	
	@RequestMapping(value = "/getEmailConfig")
	public void getEmailConfig(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		if(emailId == 0){
			printWriter.write("{\"result\":-1,\"data\":"  +"get message failed"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		EmailConfigInfo info = logMgrService.getEmailConfigByEmailId(emailId);
		JSONObject result = new JSONObject();
		result.put("email_id", info.getemailId());
		result.put("subject", info.getSubject());
		result.put("receviers", info.getReceviers());
		result.put("test_receiver", info.getTestReceiver());
		result.put("cc", info.getCc());
		result.put("remarks", info.getRemarks());
		result.put("dependencies", info.getDependencies());
		result.put("weixin_recev", info.getWeixinRecev());
		result.put("weixin_media_id", info.getWeixinMediaId());
		
		printWriter.write("{\"result\":0,\"data\":"  +JSON.toJSONString(result)+ "}");
		printWriter.flush();
		printWriter.close();
		return;
	}
	
	@RequestMapping(value = "/setStatus")
	public void setStatus(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		Integer status = IntegerTools.safeStringToInt(request.getParameter("status"));
		if(emailId == 0) {
			printWriter.write("{\"result\":-1,\"data\":"  +"get emailId failed"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		Integer ret = logMgrService.seEmailConfigtStatus(emailId, status);
		printWriter.write("{\"result\":0,\"data\":"  +ret+ "}");
		printWriter.flush();
		printWriter.close();
		return;
	}
	
	@RequestMapping(value = "/send")
	public void send(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		//获取email配置信息
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		EmailConfigInfo configInfo= logMgrService.getEmailConfigByEmailId(emailId);
		
		String receviers;
		if(BooleanTools.safeStringToBoolean(request.getParameter("is_test"))) {
			receviers = configInfo.getTestReceiver();
		} else {
			receviers = configInfo.getReceviers();
		}
		
		if(receviers == null) {
			printWriter.write("{\"result\":-1,\"data\":"  +"recevies is empty"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		List<String> recevierList = Arrays.asList(receviers.split(";")); 
		
		String ccs = configInfo.getCc();
		List<String> ccList;
		if(ccs == null || ccs.isEmpty()) {
			ccList = new ArrayList<String>();
		} else {
			ccList = Arrays.asList(ccs.split(";")); 
		}
		
		//获取message
		System.out.println("configInfo:" + JSON.toJSONString(configInfo));
		String frenquncy = configInfo.getFrequencyType();
		if(configInfo.getServerId() < 0) {
			frenquncy = "SUMMARY_" + frenquncy;
		}
		EmailMessageTools messageTool = new EmailMessageTools(configInfo, frenquncy, logMgrService);
		if(!messageTool.action()) {
			System.out.println("messageTool action failed.");
			return;
		}
		
		
		//发送邮件
		EmailTools emailTool = new EmailTools();
		//emailTool.createEmail(recevierList,ccList);
		emailTool.setToList(recevierList);
		emailTool.setCcList(ccList);
		emailTool.setSubject("这个是测试");
		String messageBody = messageTool.getEmailBody();
		Map<String, String> messagePic = messageTool.getEmailPicture();
		File attachment = messageTool.getMessageFile();
		if(messageBody != null){
			emailTool.setMessageBody(messageBody);
			if(messagePic != null) {
				emailTool.setMessagePicMap(messagePic);
			}
		}
		if(attachment != null) {
			emailTool.setAttachment(attachment);
		}
		emailTool.sendEmail();
		
	}
	
	@RequestMapping(value = "/sendWeixin")
	public void sendWeixin(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		Integer emailId = IntegerTools.safeStringToInt(request.getParameter("email_id"));
		EmailConfigInfo configInfo= logMgrService.getEmailConfigByEmailId(emailId);
		
		String receviers = configInfo.getWeixinRecev();
		if (receviers == null || receviers.isEmpty()){
			System.out.println("sendWeixin():recevies is empty");
			return;
		}
		
		String[] receviersList = receviers.split("-");
		if (receviersList.length == 1) {
			receviers = receviersList[0];
		} else {
			if(BooleanTools.safeStringToBoolean(request.getParameter("is_test"))) {
				receviers = receviersList[1];
			} else {
				receviers = receviersList[0];
			}
		}
		
		String frenquncy = configInfo.getFrequencyType();
		if(configInfo.getServerId() < 0) {
			frenquncy = "SUMMARY_" + frenquncy;
		}
		WXMessageTools tool = new WXMessageTools(emailId, frenquncy, logMgrService);
		String message = tool.getMessage();
		System.out.println("####message:" + message);
		
		
		WeiXinTools weixin = new WeiXinTools();
		String token = weixin.getToken();
		if (token == null) {
			System.out.println("sendWeixin():get token failed");
			return;
		}
		
		//String path = System.getProperty("user.dir") + "\\weixin\\"+emailId;
		String path = "";
		try {
			path = EmailController.class.getClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		path += "weixin_new/"+emailId;
		List<File> files = FileOperateTools.getFiles(path);
		if (files == null || files.size() == 0) {
			System.out.println("sendWeixin():dir["+path+"] not exit");
			return;
		}
		
		File file = files.get(0);
		System.out.println(file.getAbsolutePath());
		String weixinMediaInfo = configInfo.getWeixinMediaId();
		JSONObject wexinMediaObj = null;
		if(weixinMediaInfo == null || weixinMediaInfo.isEmpty()) {
			wexinMediaObj = new JSONObject();
		} else {
			wexinMediaObj = JSON.parseObject(weixinMediaInfo);
		}
		
		long nowTimeStamp = System.currentTimeMillis()/1000;
		long creatTimeStamp = LongTools.safeStringToLong(wexinMediaObj.getString("createtime"));
		if(wexinMediaObj.isEmpty() || nowTimeStamp-creatTimeStamp > (60*60*24*3)) {
			wexinMediaObj.clear();
			JSONObject obj = weixin.upload(token, "image", file);
			String errcode = obj.getString("errcode");
			if (!errcode.equals("0")) {
				System.out.println("sendWeixin():upload faile, errcode:"+errcode+" errmsg:" + obj.getString("errmsg"));
				return;
			}
			
			wexinMediaObj.put("id", obj.getString("media_id"));
			wexinMediaObj.put("createtime", obj.getString("created_at"));
			configInfo.setWeixinMediaId(wexinMediaObj.toJSONString());
			logMgrService.updateEmailConfigInfo(configInfo);
		}
		
		System.out.println("######id:" + wexinMediaObj.getString("id"));
		System.out.println("######message:" + message.toString());
		//return;
		try {
			System.out.println(weixin.sendWeixin(configInfo.getSubject() ,message, wexinMediaObj.getString("id"), receviers));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

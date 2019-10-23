package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
// import java.util.function.ToDoubleBiFunction;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.Spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.Metadata;
import com.taomee.tms.mgr.form.MetadataForm;

@Controller
@RequestMapping("/common/navi/")
public class NaviController {
	private static final Logger logger = LoggerFactory
			.getLogger(NaviController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	@RequestMapping(value = "/getModules")
//	public void delete(PrintWriter printWriter) {		
//		int result = 0;
//		printWriter.write("{\"result\":" + result + "}");
//		printWriter.flush();
//		printWriter.close();
//	}
//	
	// 从数据库里面取出数据
	// 进行展示
	public void getModules(HttpServletResponse response, PrintWriter printWriter) {
		int result = 0;
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		//先从session中获取
		//Map modelMap =  model.asMap();
		
		//Enumeration<String> e = session.getAttributeNames();
		String jsonString = "[{\"name\":\"游戏分析\",\"key\":\"gameanalysis\",\"children\":[{\"name\":\"游戏概览\",\"key\":\"overview\",\"children\":[{\"name\":\"关键数据\",\"key\":\"keymetrics\"},{\"name\":\"数据看板\",\"key\":\"board\"}]},{\"name\":\"游戏玩家\",\"key\":\"players\",\"children\":[{\"name\":\"新增玩家\",\"key\":\"new\"},{\"name\":\"活跃玩家\",\"key\":\"activers\"},{\"name\":\"玩家留存\",\"key\":\"keepers\"},{\"name\":\"玩家流失\",\"key\":\"losers\"},{\"name\":\"同时在线\",\"key\":\"onliners\"},{\"name\":\"游戏习惯\",\"key\":\"custom\"},{\"name\":\"设备相关\",\"key\":\"facility\"}]},{\"name\":\"实时数据\",\"key\":\"realtime\"},{\"name\":\"收入分析\",\"key\":\"income\",\"children\":[{\"name\":\"收入数据\",\"key\":\"income\"},{\"name\":\"付费渗透\",\"key\":\"penetration\"},{\"name\":\"付费转化\",\"key\":\"conversion\"},{\"name\":\"新玩家价值\",\"key\":\"newersvalue\"},{\"name\":\"付费习惯\",\"key\":\"habit\"}]},{\"name\":\"鲸鱼用户\",\"key\":\"whale\"},{\"name\":\"经济系统\",\"key\":\"economy\",\"children\":[{\"name\":\"游戏币产出与消耗\",\"key\":\"gamecoin\"},{\"name\":\"道具销售\",\"key\":\"mbsales\"},{\"name\":\"道具管理\",\"key\":\"mbmanage\"},{\"name\":\"道具类别管理\",\"key\":\"mbcategory\"}]},{\"name\":\"等级分析\",\"key\":\"level\"},{\"name\":\"任务分析\",\"key\":\"mission\",\"children\":[{\"name\":\"任务数据\",\"key\":\"data\"},{\"name\":\"任务管理\",\"key\":\"manage\"}]},{\"name\":\"渠道区服分析\",\"key\":\"gpzsanalysis\",\"children\":[{\"name\":\"渠道区服数据\",\"key\":\"data\"},{\"name\":\"渠道区服管理\",\"key\":\"manage\"}]},{\"name\":\"实时监控\",\"key\":\"monitor\",\"children\":[{\"name\":\"StatServer\",\"key\":\"statserver\"},{\"name\":\"DB Server\",\"key\":\"dbserver\"},{\"name\":\"Config Server\",\"key\":\"configserver\"},{\"name\":\"Stat Client\",\"key\":\"statclient\"},{\"name\":\"Data Node\",\"key\":\"datanode\"},{\"name\":\"JobTracker\",\"key\":\"jobtracker\"}]},{\"name\":\"后台服务状态\",\"key\":\"backend\",\"children\":[{\"name\":\"Stat-Client\",\"key\":\"statclient\"},{\"name\":\"Stat-Server\",\"key\":\"statserver\"},{\"name\":\"Db-Server\",\"key\":\"dbserver\"},{\"name\":\"Config-Server\",\"key\":\"configserver\"},{\"name\":\"Stat-Redis\",\"key\":\"statredis\"},{\"name\":\"Stat-Namenode\",\"key\":\"statnamenode\"},{\"name\":\"Stat-Jobtracker\",\"key\":\"statjobtracker\"},{\"name\":\"Stat-Datanode\",\"key\":\"statdatanode\"},{\"name\":\"Stat-Tasktracker\",\"key\":\"stattasktracker\"}]}]},{\"name\":\"充值系统\",\"key\":\"charge\",\"children\":[{\"name\":\"米币充值与消耗\",\"key\":\"mb\",\"children\":[{\"name\":\"米币充值\",\"key\":\"mbcharge\",\"children\":[{\"name\":\"充值数据\",\"key\":\"mbchargedata\"},{\"name\":\"充值习惯\",\"key\":\"mbchargebehavior\"}]},{\"name\":\"米币消耗\",\"key\":\"mbconsume\",\"children\":[{\"name\":\"消耗数据\",\"key\":\"mbconsumedata\"},{\"name\":\"各游戏消耗对比\",\"key\":\"mbconsumecmp\"}]}]},{\"name\":\"游戏包月\",\"key\":\"vip\",\"children\":[{\"name\":\"包月数据\",\"key\":\"vipdata\"},{\"name\":\"包月习惯\",\"key\":\"vipbehavior\"},{\"name\":\"退订与销户\",\"key\":\"cancelvip\"}]},{\"name\":\"总充值与消耗\",\"key\":\"total\",\"children\":[{\"name\":\"收入统计\",\"key\":\"revenuestat\"},{\"name\":\"充值数据\",\"key\":\"chargedata\"},{\"name\":\"消耗数据\",\"key\":\"consumedata\"}]}]},{\"name\":\"帐号系统\",\"key\":\"account\",\"children\":[{\"name\":\"总体帐号数据\",\"key\":\"overall\"},{\"name\":\"分游戏帐号数据\",\"key\":\"partial\"}]}]";
		
		// TODO
		// 从数据库中取数据并删选

	
 		result = 0;
		printWriter.write("{\"result\":" + result + "," + "\"data\":" + jsonString + "}");
     	printWriter.flush();
        printWriter.close();
		
		
	
	}
}

























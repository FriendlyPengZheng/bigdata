package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.lf5.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.PlatformInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.entity.ZSInfo;
import com.taomee.tms.mgr.form.ZoneServerForm;



@Controller
@RequestMapping("/common/gpzs")
public class GpzsController {
	private static final Logger logger = LoggerFactory
			.getLogger(GpzsController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;

	// 获取所有的评论
	@RequestMapping(value = "/index")
	public String index(Model model) {
		return "gameanalysis/realtime";
	}

	@RequestMapping(value = "/getZoneServer")
	public void getZoneServer(ZoneServerForm form, HttpServletResponse response,PrintWriter printWriter) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		Integer platformId = form.getPlatformId();
		System.out.print("platform id: " +platformId+ "\n");
		Integer gameId = form.getGameId();
		System.out.print("game id: " +gameId+ "\n");

		
		List<ZSInfo> list = logMgrService.getZSInfosByGPId(gameId, platformId);
		
		//转化一下适应多种格式
	/*	JSONArray zsInfos = new JSONArray();
		for(ZSInfo info:list) {
			JSONObject zsInfo = new JSONObject();
			zsInfo.put("zoneServerId", info.getZoneServerId());
			zsInfo.put("zoneServerName", info.getZoneServerName() );
			zsInfo.put("gpzs_id", info.getZoneServerId());
			zsInfo.put("gpzs_name", info.getZoneServerName());
			zsInfos.add(zsInfo);
		}*/
		
		//printWriter.write("{\"result\":0,\"data\":[{\"gpzs_id\":\"1_1\",\"game_id\":\"2\",\"gpzs_name\":\"一区一服\",\"status\":\"0\",\"add_time\":\"2014-01-26 20:28:10\"}]}");
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(list) + "}");
		printWriter.flush();

	
	}
	
	@RequestMapping(value = "/getPlatform")
	public void getPlatform(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		//获取gameid
		String gameIdString = request.getParameter("game_id");
		if(gameIdString == null || gameIdString.isEmpty()) {
			//TODO报错
			printWriter.write("{\"result\":-1,\"data\":"  +"游戏不存在"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}		
		Integer gameId = Integer.valueOf(gameIdString);
		
		//通过gameid获取platformid列表
		JSONArray platformInfos = new JSONArray();
		//添加全平台
		JSONObject tmp = new JSONObject();
		tmp.put("game_id", gameId);
		tmp.put("gpzs_name", "全平台");
		tmp.put("platform_id", "-1");
		platformInfos.add(tmp);
				
		
		List<PlatformInfo> list = logMgrService.getPlatFormInfosByGameId(gameId);
		for(PlatformInfo info:list) {
			JSONObject platformInfo = new JSONObject();
			platformInfo.put("game_id", gameId);
			platformInfo.put("gpzs_name", info.getPlatformName());
			platformInfo.put("platform_id", info.getPlatformId());
			//platformInfo.put("server_id", "-1");
			//platformInfo.put("server_id", "-1");
			platformInfos.add(platformInfo);
			//System.out.println("name:"+ info.getPlatformName());
		}
		 //System.out.println("platformInfos size:" + platformInfos.size());
		
		printWriter.write("{\"result\":0,\"data\":"  +JSON.toJSONString(platformInfos)+ "}");
		printWriter.flush();
		printWriter.close();
	}

	@RequestMapping(value = "/getZoneServer2")
	public void getZoneServer2(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		//获取gameid和platformid
		String gameIdString = request.getParameter("game_id");
		String platformIdString = request.getParameter("platform_id");
		if(gameIdString == null || platformIdString == null || gameIdString.isEmpty() || platformIdString.isEmpty())
		{
			//TODO报错
			printWriter.write("{\"result\":-1,\"data\":"  +"不存在"+ "}");
			printWriter.flush();
			printWriter.close();
			return;
		}

		Integer gameId = Integer.valueOf(gameIdString);
		Integer platformId = Integer.valueOf(platformIdString);
		List<ZSInfo> list = logMgrService.getZSInfosByGPId(gameId, platformId);
		
		JSONArray zsInfos = new JSONArray();
		JSONObject tmp = new JSONObject();
		tmp.put("gpzs_id", "-1_-1");
		tmp.put("gpzs_name", "全区全服");
		zsInfos.add(tmp);
		
		for(ZSInfo info:list) {
			JSONObject zsInfo = new JSONObject();
			zsInfo.put("gpzs_id", info.getZoneServerId());
			zsInfo.put("gpzs_name", info.getZoneServerName());
			zsInfos.add(zsInfo);
		}
		
		//printWriter.write("{\"result\":0,\"data\":[{\"gpzs_id\":\"1_1\",\"game_id\":\"2\",\"gpzs_name\":\"一区一服\",\"status\":\"0\",\"add_time\":\"2014-01-26 20:28:10\"}]}");
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(zsInfos) + "}");
		printWriter.flush();

	
	}
	
	
}

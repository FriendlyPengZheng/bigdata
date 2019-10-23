package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.DisplayPageModule;
import com.taomee.tms.mgr.entity.GameInfo;
import com.taomee.tms.mgr.entity.Page;
import com.taomee.tms.mgr.form.DisplayPageModuleForm;
import com.taomee.tms.mgr.module.GameService;
import com.taomee.tms.mgr.module.NavigatorService;
import com.taomee.tms.mgr.module.UserService;
import com.taomee.tms.mgr.tools.Message;

@Controller
@RequestMapping("/common/page")
public class PageController {
	
	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	private NavigatorService navigatorService;
	@Autowired
	private GameService gameService;
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/index/{id}")
	public String index(Model model) throws UnsupportedEncodingException {
		Map<String, Object> navigatorAll = navigatorService.getNavigatorForManage(); 
		if(navigatorAll == null) {
			return "conf/page-list";
		}
		List<Page> pages = new ArrayList<Page>();
		if(navigatorAll.containsKey("pages")){
			//Map<String, Map<String, Object>> pages = (Map<String, Map<String, Object>>) navigatorAll.get("pages");
			Map<String, Object> pageInfo = (Map<String, Object>) navigatorAll.get("pages");
	
			Iterator<Map.Entry<String, Object>> it = pageInfo.entrySet().iterator();  
			while(it.hasNext()){
				Map.Entry<String, Object> entry = it.next();
				Object values = entry.getValue();
				
				String jsonString = JSON.toJSONString(values);
				Page page = JSON.parseObject(jsonString, Page.class);
				pages.add(page);
				
			}
		}
		model.addAttribute("pages", pages);
		
		
		return "conf/page-list";
	}
	
	@RequestMapping(value = "/displayPageModule")
	public String displayPageModule(Model model, DisplayPageModuleForm form, PrintWriter printWriter, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		
		//url中传递的中文信息需要解码
		String moduleName = URLDecoder.decode(request.getQueryString(), "utf-8");
		moduleName = (moduleName.split("&")[1]).split("=")[1];

		DisplayPageModule displayPageModule = new DisplayPageModule();
		displayPageModule.setKey(form.getKey());
		displayPageModule.setGameId(form.getGameId());
		displayPageModule.setModuleName(moduleName);
		displayPageModule.setPageUrl(form.getPageUrl());
		model.addAttribute("displayPageModule", displayPageModule);
		
		return "conf/page-module";
	}
	
	@RequestMapping(value = "/getGameList")
	@ResponseBody
	public Message getGameList() {
		List<GameInfo> games = logMgrService.getGameInfos();
		//List<Integer> gameIds = gameService.getGameIdsByViewAuth();
		Set<Integer> gameIds = (Set<Integer>) gameService.getGameIdsByViewAuth();
		List<GameInfo> gameInfos = new ArrayList<GameInfo>();
		for(GameInfo gameInfo: games) {
			if(gameIds.contains(gameInfo.getGameId())){
				gameInfos.add(gameInfo);
			}
		}

		GameInfo basicModule = new GameInfo();
		basicModule.setGameId(0);
		basicModule.setGameName("基础模版");
		gameInfos.add(basicModule);
		
		Message rest = new Message().setSuccessMessage().setData(gameInfos);
		return rest;
	}

//	@RequestMapping(value = "/save")
//	public void save(MetadataForm form, PrintWriter printWriter) {
//		System.out.println("MetadataController save :"
//				+ JSON.toJSONString(form));
//		Metadata m1 = new Metadata();
//		m1.setMetadataId(1);
//		m1.setMetadataName("元数据1");
//		m1.setDataId(1);
//		m1.setPeriod(1);
//		m1.setFactor(1);
//		m1.setPrecision(2);
//		m1.setUnit("%");
//		m1.setComment("评论1");
//
//		printWriter.write("{\"result\":0,\"data\":\"user/home\"}");
//		printWriter.flush();
//		printWriter.close();
//	}

}

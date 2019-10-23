package com.taomee.tms.mgr.ctl.gameanalysis;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.module.GameService;

@Controller
@RequestMapping("/gameanalysis/{type}/economy")
public class EconomyController extends CommonAnalysisController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference
	@Autowired*/
	@Resource
	private LogMgrService logMgrService;
	@Autowired
	private GameService gameService;
    
	//@Reference
	//private List<ResultInfo> ResultInfos;
	
	
	@RequestMapping(value = "/index/{id}")
	public String index(HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		assignCommon(request, httpSession,logMgrService,gameService);
		return "gameanalysis/period-economy";
	}
	
	@RequestMapping(value = "/item/{id}")
	public String item(HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		assignCommon(request, httpSession,logMgrService,gameService);
		return "gameanalysis/mbmanage";
	}
	
	@RequestMapping(value = "/category/{id}")
	public String category(HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		assignCommon(request, httpSession,logMgrService,gameService);
		return "gameanalysis/category";
	}
	
	@RequestMapping(value = "/month/{id}")
	public String month(HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		long now = System.currentTimeMillis();
		String fromDate = dfs.format(now);
		request.setAttribute("fromDate", fromDate);
		
		SimpleDateFormat dfs2 = new SimpleDateFormat("yyyy-MM");
		String fromMonth = dfs2.format(now);
		request.setAttribute("fromMonth", fromMonth);
		
		assignCommon(request, httpSession, logMgrService, gameService);
		
		return "gameanalysis/month_gp";
	}
}

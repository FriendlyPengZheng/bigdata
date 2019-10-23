package com.taomee.tms.mgr.ctl.gameanalysis;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.taomee.tms.mgr.entity.PlatformInfo;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.module.GameService;

@Controller
@RequestMapping("/gameanalysis/{type}/analysis")
public class AnalysisController extends CommonAnalysisController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	private GameService gameService;
    
	//@Reference
	//private List<ResultInfo> ResultInfos;
	
	
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
				
//		//logger.info("ComponentQueryForm is " + JSON.toJSONString(form));
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, -30);
//		
//		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
//		long now = System.currentTimeMillis();
//		
//		String toDate = dfs.format(now);
//		String fromDate = dfs.format(calendar.getTime());
//		
//		request.setAttribute("from", fromDate);
//		request.setAttribute("to", toDate);
//		
//		
//		Integer gameId = 0;
//		String gameString = request.getParameter("game_id");
//		if(gameString == null || gameString.isEmpty()) {
//			return "gameanalysis/period-gpzs";
//		} else {
//			gameId = Integer.parseInt(gameString);
//		}
//		/*ServerInfo tmp = logMgrService.getServerInfoByTopgameId(gameId);
//		if(tmp == null) {
//			return "gameanalysis/period-gpzs";
//		}
//		request.setAttribute("platform_id", tmp.getServerId());
//		request.setAttribute("zone_id", -1);
//		request.setAttribute("server_id", -1);*/
//		request.setAttribute("platform_id", -1);
//		request.setAttribute("ZS_id", "-1_-1");
//		
//		// 初始game_id
//		/*List<ServerInfo> list = logMgrService.getServerInfoByparentId(tmp.getServerId());//先写死写了小花仙
//		if(list == null || list.size()==0) {
//			System.out.print("Get ERROR\n");
//		}*/
//		
//		//List<PlatformInfo> list = logMgrService.getPlatFromInfo(gameId);
//		/*List<PlatformInfo> list = new ArrayList<PlatformInfo>();
//		PlatformInfo tmpPlatform1 = new PlatformInfo();
//		tmpPlatform1.setPlatformId(1);
//		tmpPlatform1.setPlatformName("1平台");
//		list.add(tmpPlatform1);
//		PlatformInfo tmpPlatform2 = new PlatformInfo();
//		tmpPlatform2.setPlatformId(2);
//		tmpPlatform2.setPlatformName("2平台");
//		list.add(tmpPlatform2);*/
//		
//		List<PlatformInfo> list = logMgrService.getPlatFormInfosByGameId(gameId);
//		/*Map<Integer, String> list = new HashMap<Integer, String>();
//		list.put(1, "1平台");
//		list.put(2, "2平台");*/
//		
//		
//		request.setAttribute("platform", list);
		assignCommon(request, httpSession,logMgrService,gameService);
		return "gameanalysis/period-gpzs";
	}
	
	@RequestMapping(value = "/month/{id}")
	public String month(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		long now = System.currentTimeMillis();
		String fromDate = dfs.format(now);
		request.setAttribute("fromDate", fromDate);
		
		SimpleDateFormat dfs2 = new SimpleDateFormat("yyyy-MM");
		String fromMonth = dfs2.format(now);
		request.setAttribute("fromMonth", fromMonth);
		
//		Integer gameId = 0;
//		String gameString = request.getParameter("game_id");
//		if(gameString == null || gameString.isEmpty()) {
//			return "gameanalysis/month_gp";
//		} else {
//			gameId = Integer.parseInt(gameString);
//		}
//
//		ServerGPZSInfo tmp = logMgrService.getServerInfoByTopgameId(gameId);
//		if(tmp == null) {
//			return "gameanalysis/month_gp";
//		}
//		request.setAttribute("game_id", gameId);
//		request.setAttribute("platform_id", tmp.getServerId());
//		
//		// 初始game_id
//		List<ServerInfo> list = logMgrService.getServerInfoByparentId(20);//先写死写了小花仙
//		if(list == null || list.size()==0) {
//			System.out.print("Get ERROR\n");
//		}
//		request.setAttribute("platform", list);
		assignCommon(request, httpSession, logMgrService, gameService);
		
		return "gameanalysis/month_gp";
	}
}

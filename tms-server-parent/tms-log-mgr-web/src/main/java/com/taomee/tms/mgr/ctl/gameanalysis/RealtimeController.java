package com.taomee.tms.mgr.ctl.gameanalysis;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.module.GameService;

@Controller
@RequestMapping("/gameanalysis/{type}/realtime")
public class RealtimeController extends CommonAnalysisController{
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	private GameService gameService;
	
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
				
//		//logger.info("ComponentQueryForm is " + JSON.toJSONString(form));
//		Calendar calendar = Calendar.getInstance();
//		//calendar.add(Calendar.DATE - 30);
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
//		
//		//request.setAttribute("game_id", 10000);
//		Integer gameId = 0;
//		String gameString = request.getParameter("game_id");
//		if(gameString == null || gameString.isEmpty()) {
//			return "gameanalysis/realtime";
//		} else {
//			gameId = Integer.parseInt(gameString);
//		}
//		
//		/*ServerInfo tmp = logMgrService.getServerInfoByTopgameId(gameId);
//		if(tmp == null) {
//			return "gameanalysis/realtime";
//		}
//		//System.out.print("Server id: " +tmp.toString()+ "\n");
//		request.setAttribute("platform_id", tmp.getServerId());
//		request.setAttribute("zone_id", -1);
//		request.setAttribute("server_id", -1);
//		
//		// 初始game_id
//		List<ServerInfo> list = logMgrService.getServerInfoByparentId(tmp.getServerId());//先写死写了小花仙
//		if(list == null || list.size()==0) {
//			System.out.print("Get ERROR\n");
//		}*/
//		
//		request.setAttribute("platform_id", -1);
//		request.setAttribute("ZS_id", "-1_-1");
//		
//		List<PlatformInfo> list = logMgrService.getPlatFormInfosByGameId(gameId);
//		request.setAttribute("platform", list);
		assignCommon(request, httpSession, logMgrService, gameService);
		
		return "gameanalysis/realtime";
	}
	
}

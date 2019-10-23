/**
 * 
 */
package com.taomee.tms.mgr.ctl.gameanalysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sun.org.glassfish.gmbal.InheritedAttribute;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.GameInfo;
import com.taomee.tms.mgr.entity.PlatformInfo;
import com.taomee.tms.mgr.module.GameService;

/**
 * @author sevin
 *
 */
@Repository
public class CommonAnalysisController {
//	@Reference
//	private LogMgrService logMgrService;
//	@Reference
//	private GameService gameService;

	protected void assignCommon(HttpServletRequest request,HttpSession httpSession, LogMgrService logMgrService, GameService gameService) {
		Calendar from = Calendar.getInstance();
		from.add(Calendar.DATE, -31);
		
		Calendar to = Calendar.getInstance();
		to.add(Calendar.DATE, -1);
		
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
		
		String toDate = dfs.format(to.getTime());
		String fromDate = dfs.format(from.getTime());
		
		request.setAttribute("from", fromDate);
		request.setAttribute("to", toDate);
		
		Integer gameId = 0;
		//String gameString = (String) httpSession.getAttribute("game_id") ;
		String gameString = request.getParameter("game_id");
		if(gameString == null || gameString.isEmpty()) {
			gameId = gameService.getGameIdsByViewAuth().get(0);
		} else {
			gameId = Integer.parseInt(gameString);
		}
		request.setAttribute("game_id", gameId);

		/*ServerInfo tmp = logMgrService.getServerInfoByTopgameId(gameId);
		if(tmp == null) {
			return "gameanalysis/board";
		}*/
		//System.out.print("Server id: " +tmp.toString()+ "\n");
		/*request.setAttribute("platform_id", tmp.getServerId());
		request.setAttribute("zone_id", -1);
		request.setAttribute("server_id", -1);*/
		request.setAttribute("platform_id", -1);
		request.setAttribute("ZS_id", "-1_-1");
		
		// 初始game_id 
		/*List<ServerInfo> list = logMgrService.getServerInfoByparentId(20);//先写死写了小花仙
		if(list == null || list.size()==0) {
			System.out.print("Get ERROR\n");
		}*/
		List<PlatformInfo> list = logMgrService.getPlatFormInfosByGameId(gameId);
		request.setAttribute("platform", list);
		assignIgnore(request, gameId, gameService);
	}
	
	protected void assignIgnore(HttpServletRequest request, Integer gameId, GameService gameService) {
		ArrayList<Integer> funcMask = new ArrayList<Integer>();
		funcMask.add(5);//游戏分析
		Map<Integer, GameInfo> gameInfos = gameService.getIdGroupedGameByAuth(funcMask);
		if (gameId != null) {
			String[] ignore = gameInfos.get(gameId).getIgnoreId().split("_");
			request.setAttribute("ignore", ignore);
		}
	}
}

package com.taomee.tms.mgr.ctl.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.form.GameForm;
import com.taomee.tms.mgr.module.GameService;
import com.taomee.tms.mgr.tools.Message;
import com.taomee.tms.mgr.entity.GameInfo;

@Controller
@RequestMapping("/common/game")

//游戏管理页
public class GameController {
	private static final Logger logger = LoggerFactory
			.getLogger(GameController.class);

	@Autowired
	private GameService gameService;
	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	@RequestMapping(value = "/index/{id}")
	public String index(Model model) {
		List<GameInfo> games = new ArrayList<GameInfo>();
		games = logMgrService.getGameInfos();
		model.addAttribute("games", games);
		return "conf/game";
	}
	
	//保存游戏配置
	@RequestMapping(value = "/save")
	@ResponseBody
	public Message save(@ModelAttribute GameForm form) {
		GameInfo game = new GameInfo();
		game.setGameName(form.getGame_name());
		game.setGameType(form.getGame_type());
		game.setAuthId(form.getAuth_id());
		game.setMangeAuthId(form.getManage_auth_id());
		game.setOnlineAuthId(form.getOnline_auth_id());
		game.setFuncSlot(form.getFunc_slot());
		game.setStatus(form.getStatus());
		game.setIgnoreId(form.getIgnore());
		game.setGameEmail(form.getGame_email());
		
		if(form.getGame_id() == null || form.getGame_id().equals("")){
			game.setGameId(1);
		} else {
			game.setGameId(Integer.parseInt(form.getGame_id()));
		}
		//Integer gameId = game.getGameId();
		GameInfo gameInfo= logMgrService.getGameInfoById(game.getGameId());
		if(gameInfo == null){
			try{
				logMgrService.insertGameInfo(game);
			}catch(Exception e) {
				System.out.println("my_test_mysql_error:" + e.getMessage());
				logger.error("MySQL error" + e.getMessage());
				Message rest = new Message().setMessage(1, e.getClass().getName());
				return rest;
			}
		}else{
			try {
				logMgrService.updateGameInfo(game);
			} catch(Exception e){
				logger.error("MySQL error" + e.getMessage());
				Message rest = new Message().setMessage(1, e.getClass().getName());
				return rest;
			}
		}
		
		Message rest = new Message().setSuccessMessage().setData("tastId:1");
		return rest;
	}
	
	//设置状态
	@RequestMapping(value = "/updateStatus")
	@ResponseBody
	public Message delete(GameForm form) {
		System.out.println("GameController delete :"
				+ JSON.toJSONString(form));
		
		int result = 0;
		String strGameId = form.getGame_id();
		GameInfo game = new GameInfo();
		game.setStatus(form.getStatus());
		if(strGameId == null || Integer.parseInt(strGameId) == 0){
			result = 1;
		} else {
			int gameId = Integer.parseInt(strGameId);
			game.setGameId(gameId);
			logMgrService.updateStatus(game);
			result = 0;
		}
		
		Message rest = new Message().setMessage(result, "");
		return rest;
	}
	
	@RequestMapping(value = "/getGameList")
	@ResponseBody
	public Message getGameList(HttpServletRequest request, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		Map<Integer, GameInfo> aIdGrouped = (Map<Integer, GameInfo>) gameService.getGameByAuth().get("2");
		
		JSONArray gameInfos = new JSONArray();
		for(GameInfo info:aIdGrouped.values()) {
			JSONObject gameInfo = new JSONObject();
			//gameInfo.put("auth_id", "-1");
			//gameInfo.put("func_slot", "32");
			gameInfo.put("game_id", info.getGameId());
			gameInfo.put("game_name", info.getGameName());
			//gameInfo.put("game_type", "clientgame");
			//gameInfo.put("gpzs_id", "9597");
			//gameInfo.put("ignore", "");
			//gameInfo.put("manage_auth_id", "-1");
			//gameInfo.put("status", "1");
			gameInfos.add(gameInfo);
		}
		
		Message rest = new Message().setSuccessMessage().setData(gameInfos);
		return rest;
	}
	@RequestMapping(value = "/getGameType")
	@ResponseBody
	public Message getGameType() {
		ArrayList<String> gameTypes = gameService.getGameType();
		
		Message rest = new Message().setSuccessMessage().setData(gameTypes);
		return rest;
	}
	@RequestMapping(value = "/getStatus")
	@ResponseBody
	public Message getStatus() {
		String[] gameStatus = gameService.getStatus();
		
		Message rest = new Message().setSuccessMessage().setData(gameStatus);
		return rest;
	}
	@RequestMapping(value = "/getFuncMask")
	@ResponseBody
	public Message getFuncMask() {
		String[] funcMask = gameService.getFuncMask();
		
		Message rest = new Message().setSuccessMessage().setData(funcMask);
		return rest;
	}
}

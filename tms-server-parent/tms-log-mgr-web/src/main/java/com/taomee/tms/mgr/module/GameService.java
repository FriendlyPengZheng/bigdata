package com.taomee.tms.mgr.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.GameInfo;

@Service
public class GameService {
	private String web = "webgame"; //页游
	private String client = "clientgame"; //端游
	private String mobile = "mobilegame"; //手游
	private String test = "test"; //测试
	private String site = "site"; //网站
	
	private Integer unused = 0; //未使用
	private Integer using = 1; //使用中
	private Integer deprecate = 2; //已废弃
	
	private int onLineMask = 0; //在线统计
	private int foreignMask = 1; //对外
	private int signTransMask = 2; //新用户注册转化
	private int accountMask = 3; //账户系统
	private int chargeMask = 4; //充值系统
	private int gameAnalysisMask = 5; //游戏分析
	private int muyouOnlyMask = 6; //合作公司-木游only
	private int selfSearchMask = 7; //自定义查询
	@Autowired
	private UserService userService;
	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	/*
	 * get game list against authority.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ConcurrentHashMap<Integer, GameInfo>> getTypeGroupedGameByAuth(ArrayList<Integer> funcMask) {
		String[] aAuth= userService.getAuthority();
		if (Arrays.binarySearch(aAuth, "-3") < 0) { //does not have auth for internal games
			funcMask.add(this.foreignMask);
		}
		//TODO 添加用户特殊功能码
		//TODO 先从session中获取
		ConcurrentHashMap<String, ConcurrentHashMap<Integer, GameInfo>> aTypeGrouped = (ConcurrentHashMap<String, ConcurrentHashMap<Integer, GameInfo>>) this.getGameByAuth().get("1");
		if (funcMask.isEmpty()) {
			return aTypeGrouped;
		}
		for(String gameType : aTypeGrouped.keySet()){
			for(Integer gameId : aTypeGrouped.get(gameType).keySet()){
				for(int mask : funcMask){
					if((aTypeGrouped.get(gameType).get(gameId).getFuncSlot() & (1 << mask)) == 0){
						aTypeGrouped.get(gameType).remove(gameId);
						if (aTypeGrouped.get(gameType).isEmpty()) {
							aTypeGrouped.remove(gameType);
						}
						break;
					}
				}
			}
		}
		return aTypeGrouped;
	}
	/*
	 * Get authorized games' key-value list as game_id->game_info
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, GameInfo> getIdGroupedGameByAuth(ArrayList<Integer> funcMask) {
		String[] aAuth = userService.getAuthority();
		if (Arrays.binarySearch(aAuth, "-3") < 0) { //does not have auth for internal games
			funcMask.add(this.foreignMask);
		}
		//TODO 添加用户特殊功能码
		//TODO 先从session中获取
		ConcurrentHashMap<Integer, GameInfo> aIdGrouped = (ConcurrentHashMap<Integer, GameInfo>) this.getGameByAuth().get("2");
		/*System.out.println("aIdGrouped1:" +aIdGrouped);
		System.out.println("funcMask:" +funcMask);*/
		if (funcMask.isEmpty()) {
			return aIdGrouped;
		}
		for(int gameId : aIdGrouped.keySet()){
			for(int mask : funcMask){
				if ((aIdGrouped.get(gameId).getFuncSlot() & (1 << mask)) == 0) {
					aIdGrouped.remove(gameId);
					break;
				}
			}
		}
		/*System.out.println("aIdGrouped2:" +aIdGrouped);*/
		return aIdGrouped;
	}
	/*
	 * Get games from database against authority.
	 */
	public Map<String, Object> getGameByAuth() {
		String[] aAuth = userService.getAuthority();
		List<GameInfo> aGameList = logMgrService.getGameInfos();
		Map<String, ConcurrentHashMap<Integer, GameInfo>> aTypeGrouped = new ConcurrentHashMap<String, ConcurrentHashMap<Integer,GameInfo>>();
		Map<Integer, GameInfo> aIdGrouped = new ConcurrentHashMap<Integer,GameInfo>();
		for (int i = 0; i < aGameList.size(); i++) {
			GameInfo game = aGameList.get(i);
			if (Arrays.binarySearch(aAuth, game.getAuthId()) >= 0) {
				if (aTypeGrouped.containsKey(game.getGameType())) {
					aTypeGrouped.get(game.getGameType()).put(game.getGameId(),game);
				} else {
					ConcurrentHashMap<Integer, GameInfo> gameByType = new ConcurrentHashMap<Integer,GameInfo>();
					gameByType.put(game.getGameId(), game);
					aTypeGrouped.put(game.getGameType(), gameByType);
				}
				aIdGrouped.put(game.getGameId(), game);
			}
		}
		//TODO 添加session
		Map<String, Object> gameInfos = new HashMap<String,Object>();
		gameInfos.put("1", aTypeGrouped);
		gameInfos.put("2", aIdGrouped);
		return gameInfos;
	}
	
/*	
	public Set<Integer> getGameIdsByViewAuth() {
		String[] aAuth = userService.getAuthority();
		if(aAuth==null || aAuth.length == 0) {
			return null;
		}
		List<GameInfo> aGameList = logMgrService.getGameInfos();
		Set<Integer> gameIds = new HashSet<Integer>();
		for (int i = 0; i < aGameList.size(); i++) {
			GameInfo game = aGameList.get(i);
			if (Arrays.binarySearch(aAuth, game.getAuthId()) >= 0) {
				gameIds.add(game.getGameId());
			}
		}
		return gameIds;
	}*/
	
	
	public List<Integer> getGameIdsByViewAuth() {
		String[] aAuth = userService.getAuthority();
		if(aAuth==null || aAuth.length == 0) {
			return null;
		}
		List<GameInfo> aGameList = logMgrService.getGameInfos();
		List<Integer> gameIds = new ArrayList<Integer>();
		for (int i = 0; i < aGameList.size(); i++) {
			GameInfo game = aGameList.get(i);
			if (Arrays.binarySearch(aAuth, game.getAuthId()) >= 0) {
				gameIds.add(game.getGameId());
			}
		}
		return gameIds;
	}
	
	public Set<Integer> getGameIdsByManageAuth() {
		String[] aAuth = userService.getAuthority();
		List<GameInfo> aGameList = logMgrService.getGameInfos();
		Set<Integer> gameIds = new HashSet<Integer>();
		for (int i = 0; i < aGameList.size(); i++) {
			GameInfo game = aGameList.get(i);
			if (Arrays.binarySearch(aAuth, game.getMangeAuthId()) >= 0) {
				gameIds.add(game.getGameId());
			}
		}
		return gameIds;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ArrayList<String> getGameType() {
		ArrayList<String> gameTypes = new ArrayList<String>();
		gameTypes.add(this.web);
		gameTypes.add(this.client);
		gameTypes.add(this.mobile);
		gameTypes.add(this.test);
		gameTypes.add(this.site);
		return gameTypes;
	}
	
	public String[] getStatus() {
		String[] gameStatus = new String[3];
		gameStatus[this.unused] = "未使用";
		gameStatus[this.using] = "使用中";
		gameStatus[this.deprecate] = "已废弃";
		return gameStatus;
	}
	
	public String[] getFuncMask(){
		String[] gameFuncMasks = new String[8];
		gameFuncMasks[this.onLineMask] = "在线统计";
		gameFuncMasks[this.foreignMask] = "对外";
		gameFuncMasks[this.signTransMask] = "新用户注册转化";
		gameFuncMasks[this.accountMask] = "账户系统";
		gameFuncMasks[this.chargeMask] = "充值系统";
		gameFuncMasks[this.gameAnalysisMask] = "游戏分析";
		gameFuncMasks[this.muyouOnlyMask] = "合作公司-木游only";
		gameFuncMasks[this.selfSearchMask] = "自定义查询";
		return gameFuncMasks;
	}
}

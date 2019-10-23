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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.GameTaskInfo;
import com.taomee.tms.mgr.entity.MissionInfo;
import com.taomee.tms.mgr.entity.PlatformInfo;
import com.taomee.tms.mgr.module.GameService;
import com.taomee.tms.mgr.tools.DateTools;
import com.taomee.tms.mgr.tools.IntegerTools;

@Controller
@RequestMapping("/gameanalysis/{type}/mission")
public class MissionController extends CommonAnalysisController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	@Autowired
	private GameService gameService;
    
	//@Reference
	//private List<ResultInfo> ResultInfos;
	
	/**
     * 游戏任务管理
     */
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
				
		//logger.info("ComponentQueryForm is " + JSON.toJSONString(form));
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
//			return "gameanalysis/listtable";
//		} else {
//			gameId = Integer.parseInt(gameString);
//		}
//		
//		request.setAttribute("platform_id", -1);
//		request.setAttribute("ZS_id", "-1_-1");
//		
//		System.out.println("!!!!gameId:" + gameId);
//		List<PlatformInfo> list = logMgrService.getPlatFormInfosByGameId(gameId);
//		System.out.println("!!!!platform:" + JSON.toJSONString(list));
//		
//		request.setAttribute("platform", list);
		assignCommon(request, httpSession, logMgrService, gameService);
		return "gameanalysis/listtable";
	}
	

    /**
     * 游戏任务管理
     */
	@RequestMapping(value = "/gametask/{id}")
	public String gametask(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		return "gameanalysis/gametaskmanage";
	}
	
	/**
     * 拉取任务
     */
	@RequestMapping(value = "/getMissionList")
	public void getMissionList(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		List<MissionInfo> listInfo = getMissionList(request);
		//System.out.println("getMissionList res:" + JSON.toJSONString(listInfo));
		JSONArray res = new JSONArray();
		for(MissionInfo info:listInfo) {
			JSONObject tmp = new JSONObject();
			tmp.put("gametaskName", info.getGametaskName());
			tmp.put("sstid", info.getSstid());
			tmp.put("abrtUcount", info.getAbrtUcount());
			tmp.put("doneUcount", info.getDoneUcount());
			tmp.put("getUcount", info.getGetUcount());
			tmp.put("rate", info.getDonepercent());
			res.add(tmp);
		}
		printWriter.write("{\"result\":0,\"data\":" + res.toJSONString() + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/getMissionDetail")
	public void getMissionDetail(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式//
		
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		Integer platformId = IntegerTools.safeStringToInt(request.getParameter("platform_id"));
		String zondServerId = request.getParameter("ZS_id");
		if(gameId.equals(0) || platformId.equals(0)){
			return;
		}
		
		Integer zoneId;
		Integer sId;
		if(zondServerId == null || zondServerId.isEmpty() ){
			zoneId = -1;
			sId = -1;
		} else {
			String[] tmp = zondServerId.split("_");
			zoneId = IntegerTools.safeStringToInt(tmp[0]);
			sId = IntegerTools.safeStringToInt(tmp[1]);
			if(zoneId.equals(0) || sId.equals(0)) {
				return;
			}
		}	
		Integer serverId = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		if(serverId == null || serverId.equals(0)) {
			return;
		}
		
		Long from = DateTools.date2TimeStamp(request.getParameter("from[0]"), "yyyy-MM-dd");
		Long to = DateTools.date2TimeStamp(request.getParameter("to[0]"), "yyyy-MM-dd");
		if(from.equals(0L) || to.equals(0L)) {
			return;
		}
		
		List<Long> timeStampList = getDateList(from,to);
		List<MissionInfo> infoList = new ArrayList<MissionInfo>();
		for(Long timeStamp : timeStampList) {
			List<MissionInfo> infos = logMgrService.getMissionDetialInfo(serverId, timeStamp.intValue(), timeStamp.intValue()+86400, "test");
			System.out.println("["+timeStamp+":"+timeStamp+"]:" + JSON.toJSONString(infos));
			if(infos == null || infos.size() == 0) {
				MissionInfo tmp = new MissionInfo();
				//tmp.setGetUcount(10);
				//tmp.setDoneUcount(5);
				infoList.add(tmp);
			} else {
				infoList.add(infos.get(0));
				//System.out.println("!!!!!!!!!!:infos.get(0)" + JSON.toJSONString(infos.get(0)));
			}
		}
		System.out.println("infoList:" + JSON.toJSONString(infoList));
		if(infoList.isEmpty()) {
			return;
		}
		
		Map<String,List<String>> infoMap = new HashMap<String,List<String>>();
		infoMap.put("getUcount", new ArrayList<String>());
		infoMap.put("doneUcount", new ArrayList<String>());
		infoMap.put("abrtUcount", new ArrayList<String>());
		infoMap.put("acceptPercent", new ArrayList<String>());
		infoMap.put("donepercent", new ArrayList<String>());
		
		for(MissionInfo info :infoList) {
			infoMap.get("getUcount").add(info.getGetUcount().toString());
			infoMap.get("doneUcount").add(info.getDoneUcount().toString());
			infoMap.get("abrtUcount").add(info.getAbrtUcount().toString());
			infoMap.get("acceptPercent").add(info.getAcceptPercent());
			infoMap.get("donepercent").add(info.getDonepercent());
		}
		
		Integer rate = IntegerTools.safeStringToInt(request.getParameter("rate"));
		JSONArray jsonInfo = convertMissionDetail(rate, infoMap);
		if(jsonInfo.isEmpty()) {
			return;
		}
		
		JSONObject obj = new JSONObject();
		obj.put("data", jsonInfo);
		obj.put("key", converDate(timeStampList));
		JSONArray res = new JSONArray();
		res.add(obj);
		System.out.println("getMissionDetail"+ res.toJSONString());
		
		//List<MissionInfo> infos = logMgrService.getMissionDetialInfo(serverId, 1532843081, 1532843083, "test");
		//System.out.println("getMissionDetail:"+JSON.toJSON(infos));
		
		//String res2 = "{\"result\":0,\"data\":[{\"key\":[\"2018-07-09\",\"2018-07-10\",\"2018-07-11\",\"2018-07-12\",\"2018-07-13\",\"2018-07-14\",\"2018-07-15\",\"2018-07-16\",\"2018-07-17\",\"2018-07-18\",\"2018-07-19\",\"2018-07-20\",\"2018-07-21\",\"2018-07-22\",\"2018-07-23\",\"2018-07-24\",\"2018-07-25\",\"2018-07-26\",\"2018-07-27\",\"2018-07-28\",\"2018-07-29\",\"2018-07-30\",\"2018-07-31\",\"2018-08-01\",\"2018-08-02\",\"2018-08-03\",\"2018-08-04\",\"2018-08-05\",\"2018-08-06\",\"2018-08-07\",\"2018-08-08\"],\"data\":[{\"name\":\"\u63a5\u53d6\u4eba\u6570\",\"data\":[\"4\",\"1\",\"2\",\"2\",\"2\",\"3\",\"3\",\"2\",\"3\",\"8\",\"2\",\"2\",\"1\",0,\"5\",\"2\",\"1\",\"1\",\"4\",\"1\",\"1\",\"3\",\"1\",\"3\",\"3\",\"1\",\"2\",\"4\",\"2\",\"2\",0]},{\"name\":\"\u5b8c\u6210\u4eba\u6570\",\"data\":[\"4\",\"1\",\"1\",\"1\",\"0\",\"1\",\"1\",\"2\",\"3\",\"8\",\"2\",\"2\",\"1\",0,\"5\",\"2\",\"1\",\"1\",\"3\",\"1\",\"1\",\"3\",\"1\",\"3\",\"2\",\"0\",\"2\",\"3\",\"2\",\"2\",0]},{\"name\":\"\u653e\u5f03\u4eba\u6570\",\"data\":[\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",0,\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"1\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",\"0\",0]}]}]}";
		//printWriter.write(res2);
		printWriter.write("{\"result\":0,\"data\":" + res.toJSONString() + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	private JSONArray converDate(List<Long> timeStampList) {
		JSONArray res = new JSONArray(); 
		for(Long time: timeStampList) {
			//System.out.println("timestamp:" + time + " time:" + DateTools.timeStamp2Date(time, "yyyy-MM-dd"));
			res.add(DateTools.timeStamp2Date(time, "yyyy-MM-dd"));
		}
		return res;
	}
	
	private JSONArray convertMissionDetail(Integer rate, Map<String,List<String>> infoMap) {
		JSONArray res = new JSONArray();
		if(rate.equals(0)) {
			JSONObject getUcount = new JSONObject();
			getUcount.put("name", "接取人数");
			getUcount.put("data", JSON.toJSON(infoMap.get("getUcount")));
			JSONObject doneUcount = new JSONObject();
			doneUcount.put("name", "完成人数");
			doneUcount.put("data", JSON.toJSON(infoMap.get("doneUcount")));
			JSONObject abrtUcount = new JSONObject();
			abrtUcount.put("name", "放弃人数");
			abrtUcount.put("data", JSON.toJSON(infoMap.get("abrtUcount")));
			res.add(getUcount);
			res.add(doneUcount);
			res.add(abrtUcount);
		}else if (rate.equals(1)) {
			JSONObject acceptPercent = new JSONObject();
			acceptPercent.put("name", "接取率");
			acceptPercent.put("data", JSON.toJSON(infoMap.get("acceptPercent")));
			JSONObject donepercent = new JSONObject();
			donepercent.put("name", "完成率");
			donepercent.put("data", JSON.toJSON(infoMap.get("donepercent")));
			res.add(acceptPercent);
			res.add(donepercent);
		}
		return res;
	}
	
	/*private JSONArray listToJson(List<String> list) {
		JSONArray res = new JSONArray();
		for(String str :list) {
			res.add(str);
		}
		return res;
	}*/
	
	/**
     * 拉取任务
     */
	@RequestMapping(value = "/getNewMissionTop")
	public void getNewMissionTop(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		//String res = "{\"result\":0,\"data\":[{\"key\":[\"1\",\"9\",\"8\",\"7\",\"6\",\"5\",\"4\",\"3\",\"2\",\"19\"],\"data\":[{\"name\":\"\u65b0\u624b\u4efb\u52a1\u8f6c\u5316\u7387\",\"index\":[1,2,3,4,5,6,7,8,9,10],\"data\":[\"361008\",\"296840\",\"301905\",\"308747\",\"326641\",\"340628\",\"343928\",\"350480\",\"353220\",\"1\"],\"percentage\":[\"100%\",\"82.23%\",\"83.63%\",\"85.52%\",\"90.48%\",\"94.35%\",\"95.27%\",\"97.08%\",\"97.84%\",\"0%\"],\"specialper\":[\"0%\",\"82.23%\",\"101.71%\",\"102.27%\",\"105.8%\",\"104.28%\",\"100.97%\",\"101.91%\",\"100.78%\",\"0%\"]}]}]}";
		//printWriter.write(res);
		
		List<MissionInfo> listInfo = getMissionList(request);
		
		//对数据重组
		JSONArray index = new JSONArray();
		JSONArray data = new JSONArray();
		JSONArray percentage = new JSONArray();
		JSONArray specialper = new JSONArray();
		JSONArray key = new JSONArray();
		
		Integer i = 0;
		Integer first = 0;
		for(MissionInfo info: listInfo) {
			key.add(info.getGametaskName());
			index.add(i+1);
			
			data.add(info.getDoneUcount());
			if(i.equals(0)){
				first = info.getDoneUcount();
				specialper.add("0%");
			} else {
				if(i - 1 < 0) {
					specialper.add("0%");
				} else {
					System.out.println("info.getDoneUcount():" + info.getDoneUcount());
					System.out.println("listInfo.get(i-1).getDoneUcount():" + listInfo.get(i-1).getDoneUcount());
					double res = (double)Math.round(10000*(double)info.getDoneUcount()/(double)listInfo.get(i-1).getDoneUcount())/100;
					specialper.add(String.valueOf(res) + "%");
					System.out.println("specialper:" + res);
				}
			}
			
			System.out.println("first:" + first);
			if(first.equals(0)) {
				percentage.add("0%");
			} else {
				double res = (double)Math.round(10000*(double)info.getDoneUcount()/first)/100;
				percentage.add(String.valueOf(res) + "%");
				System.out.println("percentage:" + res); 
			}	
			i++;
		}
		
		JSONObject tmp = new JSONObject();
		tmp.put("name", "新手任务转化率");
		tmp.put("index", index);
		tmp.put("data",data);
		tmp.put("percentage", percentage);
		tmp.put("specialper", specialper);
		JSONArray tmp2 = new JSONArray();
		tmp2.add(tmp);
		JSONObject tmp3 = new JSONObject();
		tmp3.put("data", tmp2);
		tmp3.put("key", key);
		JSONArray tmp4 = new JSONArray();
		tmp4.add(tmp3);
		
		System.out.println("getNewMissionTop res:" + tmp4.toJSONString());
		printWriter.write("{\"result\":0,\"data\":" + tmp4.toJSONString() + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	/**
     * 拉取任务
     */
	@RequestMapping(value = "/getGametaskList")
	public void getGametaskList(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		//String res = "{\"result\":0,\"data\":[{\"id\":\"0\",\"name\":\"0\",\"hide\":\"0\"},{\"id\":\"1\",\"name\":\"1\",\"hide\":\"0\"},{\"id\":\"10\",\"name\":\"10\",\"hide\":\"0\"},{\"id\":\"11\",\"name\":\"11\",\"hide\":\"0\"},{\"id\":\"12\",\"name\":\"12\",\"hide\":\"0\"},{\"id\":\"13\",\"name\":\"13\",\"hide\":\"0\"},{\"id\":\"14\",\"name\":\"14\",\"hide\":\"0\"},{\"id\":\"15\",\"name\":\"15\",\"hide\":\"0\"},{\"id\":\"16\",\"name\":\"16\",\"hide\":\"0\"},{\"id\":\"17\",\"name\":\"17\",\"hide\":\"0\"},{\"id\":\"18\",\"name\":\"18\",\"hide\":\"0\"},{\"id\":\"19\",\"name\":\"19\",\"hide\":\"0\"},{\"id\":\"2\",\"name\":\"2\",\"hide\":\"0\"},{\"id\":\"20\",\"name\":\"20\",\"hide\":\"0\"},{\"id\":\"21\",\"name\":\"21\",\"hide\":\"0\"},{\"id\":\"22\",\"name\":\"22\",\"hide\":\"0\"},{\"id\":\"23\",\"name\":\"23\",\"hide\":\"0\"},{\"id\":\"24\",\"name\":\"24\",\"hide\":\"0\"},{\"id\":\"25\",\"name\":\"25\",\"hide\":\"0\"},{\"id\":\"26\",\"name\":\"26\",\"hide\":\"0\"},{\"id\":\"27\",\"name\":\"27\",\"hide\":\"0\"},{\"id\":\"28\",\"name\":\"28\",\"hide\":\"0\"},{\"id\":\"29\",\"name\":\"29\",\"hide\":\"0\"},{\"id\":\"3\",\"name\":\"3\",\"hide\":\"0\"},{\"id\":\"30\",\"name\":\"30\",\"hide\":\"0\"},{\"id\":\"4\",\"name\":\"4\",\"hide\":\"0\"},{\"id\":\"5\",\"name\":\"5\",\"hide\":\"0\"},{\"id\":\"6\",\"name\":\"6\",\"hide\":\"0\"},{\"id\":\"7\",\"name\":\"7\",\"hide\":\"0\"},{\"id\":\"8\",\"name\":\"8\",\"hide\":\"0\"},{\"id\":\"9\",\"name\":\"9\",\"hide\":\"0\"},{\"id\":\"NaN\",\"name\":\"NaN\",\"hide\":\"0\"}]}";
		//printWriter.write(res);
		
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		String type = request.getParameter("type");
		if(gameId.equals(0)|| type == null || type.isEmpty()) {
			printWriter.write("{\"result\":0,\"data\":{}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		List<GameTaskInfo> gameTaskInfo = logMgrService.getGameTaskInfoList(gameId, type);
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(gameTaskInfo) + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/setName")
	public void setName(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		if(type == null || id == null || name == null || gameId.equals(0)) {
			printWriter.write("{\"result\":-1,\"data\":{}}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		GameTaskInfo info = new GameTaskInfo();
		info.setGameId(gameId);
		info.setId(id);
		info.setName(name);
		info.setType(type);
		
		logMgrService.updateGameTaskName(info);
		
		printWriter.write("{\"result\":0,\"data\":{}}");
		printWriter.flush();
		printWriter.close();
		return;
	}
	
	@RequestMapping(value = "/setHide")
	public void setHide(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String hideString = request.getParameter("hide");
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		if(type == null || id == null || hideString == null || gameId.equals(0)) {
			printWriter.write("{\"result\":-1,\"data\":{}}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
		GameTaskInfo info = new GameTaskInfo();
		info.setGameId(gameId);
		info.setId(id);
		info.setHide(IntegerTools.safeStringToInt(hideString));
		info.setType(type);
		
		logMgrService.updateGameTaskHide(info);
		
		printWriter.write("{\"result\":0,\"data\":{}}");
		printWriter.flush();
		printWriter.close();
		return;
	}
	
	
	@RequestMapping(value = "/setHideAll")
	public void setHideAll(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String type = request.getParameter("type");
		String hideString = request.getParameter("hide");
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		if(type == null || hideString == null || gameId.equals(0)) {
			printWriter.write("{\"result\":-1,\"data\":{}}");
			printWriter.flush();
			printWriter.close();
			return;
		}
		
	
		
		logMgrService.updateGameTaskHideAll(gameId, type, IntegerTools.safeStringToInt(hideString));
		printWriter.write("{\"result\":0,\"data\":{}}");
		printWriter.flush();
		printWriter.close();
		return;
	}
	
	
	
	private List<MissionInfo> getMissionList(HttpServletRequest request) {
		Integer gameId = IntegerTools.safeStringToInt(request.getParameter("game_id"));
		Integer platformId = IntegerTools.safeStringToInt(request.getParameter("platform_id"));
		String zondServerId = request.getParameter("ZS_id");
		if(gameId.equals(0) || platformId.equals(0)){
			return null;
		}
		
		Integer zoneId;
		Integer sId;
		if(zondServerId == null || zondServerId.isEmpty() ){
			zoneId = -1;
			sId = -1;
		} else {
			String[] tmp = zondServerId.split("_");
			zoneId = IntegerTools.safeStringToInt(tmp[0]);
			sId = IntegerTools.safeStringToInt(tmp[1]);
			if(zoneId.equals(0) || sId.equals(0)) {
				return null;
			}
		}	
		Integer serverId = logMgrService.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		if(serverId == null || serverId.equals(0)) {
			return null;
		}
		
		System.out.println("getMissionList serverID:" +serverId );
		Long from = DateTools.date2TimeStamp(request.getParameter("from[0]"), "yyyy-MM-dd");
		Long to = DateTools.date2TimeStamp(request.getParameter("to[0]"), "yyyy-MM-dd");
		String type = request.getParameter("mission_type");
		if(type == null || type.isEmpty()){
			type = "main";
		}
		if(from.equals(0l) || to.equals(0l)) {
			return null;
		}
		
		System.out.println("serverId:" + serverId + " from:"+ from.intValue() + " to:" + to.intValue() + " type:" + type);
		return logMgrService.getMissionList(serverId, from.intValue(), to.intValue(), type);
	}
	
	
	private List<Long> getDateList(Long from,Long to) {
		List<Long> res = new ArrayList<Long>();
		for(Long time = from; time <= to; time += 86400) {
			res.add(time);
		}
		return res;
	}
}

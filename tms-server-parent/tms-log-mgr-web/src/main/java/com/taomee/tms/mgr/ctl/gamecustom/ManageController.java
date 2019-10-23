package com.taomee.tms.mgr.ctl.gamecustom;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.taomee.tms.mgr.ctl.gameanalysis.OverviewController;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

@Controller
@RequestMapping("/gamecustom/manage")
public class ManageController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
    
	//@Reference
	//private List<ResultInfo> ResultInfos;
	private int CATEGORY_BASIC = 1;
	private int CATEGORY_DISTR = 2;
	private int CATEGORY_ITEM = 3;
	
	
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		Integer gameId = 0;
		String gameString = request.getParameter("game_id");
		if(gameString == null || gameString.isEmpty()) {
			return "gamecustom/manage";
		} else {
			gameId = Integer.parseInt(gameString);
		}
		request.setAttribute("game_id", gameId);
		return "gamecustom/manage";
	}

	@RequestMapping(value = "/getStatItemByNodeId")
	public void getStatItemByNodeId(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		JSONArray result = new JSONArray();
		
		String gameString = request.getParameter("game_id");
		String nodeString = request.getParameter("node_id");
		if(gameString == null || nodeString == null 
				|| gameString.isEmpty() || nodeString.isEmpty()) {
			printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
			printWriter.flush();
			return;
		}
		
		Integer gameId = Integer.parseInt(gameString);
		Integer nodeId = Integer.parseInt(nodeString);
		if(nodeId < 0) {
			nodeId = 0;
		}

		System.out.println("node_id:" + nodeId + " gameID:" + gameId);
		List<SchemaInfo> schemaList = logMgrService.getSchemaInfoContentList(nodeId, gameId);
		List<SchemaInfo> sechmaIteamList = logMgrService.getSchemaInfoContentListItem(nodeId, gameId);
		System.out.println("schemaList:" +schemaList);
		System.out.println("sechmaIteamList:" +sechmaIteamList);
		
		for(SchemaInfo dataInfo:schemaList) {
			result.add(sechmaToJson(dataInfo, false, "report"));
		}
		
		for(SchemaInfo dataInfo:sechmaIteamList) {
			result.add(sechmaToJson(dataInfo, true, "report"));
		}
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "/setStatItemName")
	public void setStatItemName(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String idString = request.getParameter("id");
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		
		if(idString == null || name == null || type == null) {
			printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
			printWriter.flush();
			return;
		}
		
		SchemaInfo schemaInfo = new SchemaInfo();
		schemaInfo.setSchemaId(Integer.parseInt(idString));
		schemaInfo.setSchemaName(name);
		logMgrService.updateItemName(schemaInfo);
		
		printWriter.write("{\"result\":0,\"data\":" + "null" + "}");
		printWriter.flush();	
	}
	
	
	private JSONObject sechmaToJson(SchemaInfo dataInfo, Boolean isMulti, String type) {
		JSONObject result = new JSONObject();
		result.put("is_multi", (isMulti ? 1 : 0));
		result.put("is_setted", 0);
		result.put("op_fields", dataInfo.getCascadeFields());
		result.put("op_type", dataInfo.getOp());
		result.put("r_id", dataInfo.getSchemaId());
		result.put("r_name", dataInfo.getSchemaName());
		result.put("r_type", (isMulti ? CATEGORY_ITEM : CATEGORY_BASIC));
		result.put("report_id", dataInfo.getSchemaId());
		result.put("report_name", dataInfo.getSchemaName());
		result.put("type", type);
		return result;
	}
}

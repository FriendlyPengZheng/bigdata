package com.taomee.tms.mgr.ctl.gamecustom;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.taomee.tms.mgr.entity.DataInfo;
import com.taomee.tms.mgr.entity.DataInfoQueryParams;
import com.taomee.tms.mgr.entity.SchemaInfo;

@Controller
@RequestMapping("/gamecustom/content")
public class ContentController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	@RequestMapping(value = "/getContentList")
	public void getContentList(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		//设置日期
		String fromDateString = request.getParameter("from");
		String toDateString = request.getParameter("to");
		String nodeIdString = request.getParameter("node_id");
		String gameIdString = request.getParameter("game_id");
		JSONArray dateList = new JSONArray();
		if(fromDateString == null || toDateString == null
				|| fromDateString.isEmpty() || toDateString.isEmpty()) {
			dateList.clear();
		} else {
			dateList = genKeyList(fromDateString,toDateString);
		}
		
		Integer nodeId = Integer.parseInt(nodeIdString);
		Integer gameId = Integer.parseInt(gameIdString);
		//查询contentList
		List<SchemaInfo> schemaList = logMgrService.getSchemaInfoContentList(nodeId, gameId);
		List<SchemaInfo> sechmaIteamList = logMgrService.getSchemaInfoContentListItem(nodeId, gameId);
		System.out.println("schemaList:" +schemaList);
		System.out.println("sechmaIteamList:" +sechmaIteamList);
		
		JSONArray schemaArray = SchemaListToArray(schemaList, "report", false);
		JSONArray sechmaIteamArray = SchemaListToArray(sechmaIteamList, "report", true);
		
		JSONArray dataArray = new JSONArray();
		dataArray.add(schemaArray);
		dataArray.add(sechmaIteamArray);
		
		JSONObject result = new JSONObject();
		result.put("data", dataArray);
		result.put("date", dateList);
		
		//printWriter.write("{\"result\":0,\"data\":{\"date\":[\"2017-05-23\",\"2017-05-24\",\"2017-05-25\",\"2017-05-26\",\"2017-05-27\",\"2017-05-28\",\"2017-05-29\",\"2017-05-30\",\"2017-05-31\",\"2017-06-01\",\"2017-06-02\",\"2017-06-03\",\"2017-06-04\",\"2017-06-05\",\"2017-06-06\",\"2017-06-07\",\"2017-06-08\",\"2017-06-09\",\"2017-06-10\",\"2017-06-11\",\"2017-06-12\",\"2017-06-13\",\"2017-06-14\",\"2017-06-15\",\"2017-06-16\",\"2017-06-17\",\"2017-06-18\",\"2017-06-19\",\"2017-06-20\",\"2017-06-21\",\"2017-06-22\"],\"data\":[[{\"r_id\":\"1287060\",\"ifselfhelp\":0,\"r_name\":\"\u70b9\u51fb\u5408\u6210\u4eba\u6b21\",\"type\":\"report\",\"display_order\":\"0\",\"add_time\":\"2017-06-19 15:07:32\",\"r_length\":-1,\"iffavor\":1,\"inselfhelp\":0,\"infavor\":0},{\"r_id\":\"1287274\",\"ifselfhelp\":1,\"r_name\":\"\u70b9\u51fb\u5408\u6210\u4eba\u6570\",\"type\":\"report\",\"display_order\":\"0\",\"add_time\":\"2017-06-20 07:38:36\",\"r_length\":-1,\"iffavor\":1,\"inselfhelp\":0,\"infavor\":0}],[]]}}");
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "/getDataList")
	public void getDataList(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		String idString = request.getParameter("r_id");
		String typeString = request.getParameter("type");
		if(idString == null || typeString == null || (!typeString.equals("report"))) {
			return;
		}
		
		Integer relateId = Integer.parseInt(idString);
		//Integer relateId =14;
		Integer type = 0;
		
		DataInfoQueryParams param = new DataInfoQueryParams();
		param.setRelateId(relateId);
		param.setType(type);
		//System.out.println("relateId:" + idString + " type:" + type);
		List<DataInfo> dataList = logMgrService.getDataInfosByParams(param);
		//System.out.println("getDataList  dataList:" + dataList);
		
		SchemaInfo schemaInfo = logMgrService.getSchemaInfoByschemaId(relateId);
		//System.out.println("schemaInfo:" + schemaInfo);
				
		JSONArray result = dataInfoToArray(dataList, ((schemaInfo.getOp().equals("ucount"))?1:0), relateId);
		//System.out.println("result:" + result);
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		printWriter.flush();
	}
	
	@RequestMapping(value = "/getTimePoints")
	public void getTimePoints(PrintWriter printWriter, HttpServletResponse response, HttpServletRequest request) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		String fromDateString = request.getParameter("from");
		String toDateString = request.getParameter("to");
		
		if(fromDateString == null || toDateString == null
				|| fromDateString.isEmpty() || toDateString.isEmpty()) {
			printWriter.write("{\"result\":0,\"data\":[]}");
		} else {
			JSONArray result = genKeyList(fromDateString,toDateString);
			printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(result) + "}");
		}
		printWriter.flush();
	
	}
	
	
	private JSONArray genKeyList(String fromDateString, String toDateString) {
		JSONArray keyList = new JSONArray();
		SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate, toDate = null;
		long now=System.currentTimeMillis(); 
		try {
			df.applyPattern("yyyy-MM-dd");
			fromDate = df.parse(fromDateString);
			toDate = df.parse(toDateString);
			long tmp = fromDate.getTime();
			// 获取天数
			for (; tmp <= toDate.getTime() && tmp <= now;) {
				String tmpDayString = sdf.format(tmp);
				// System.out.println("tmpDayString :" + tmpDayString);
				keyList.add(tmpDayString);
				tmp += 24*3600*1000;
			}
			
			if(keyList.size() == 0) {
				keyList.add(sdf.format(now));
			}
			return keyList;
		} catch (ParseException e) {
			e.printStackTrace();
			return new JSONArray();
		}
	}
	
	private JSONArray SchemaListToArray(List<SchemaInfo> schemaList, String type, Boolean isMulti){
		JSONArray result = new JSONArray();
		for(SchemaInfo dataInfo : schemaList) {
			JSONObject dataObj = new JSONObject();
			dataObj.put("add_time", dataInfo.getAddTime());
			dataObj.put("display_order", dataInfo.getDisplayOrder());
			dataObj.put("iffavor", 1);
			dataObj.put("ifselfhelp", ((dataInfo.getOp().equals("ucount"))?1:0));
			dataObj.put("infavor", 0);
			dataObj.put("inselfhelp", 0);
			dataObj.put("r_id", dataInfo.getSchemaId());
			if(isMulti) {
				dataObj.put("r_length", getDataNumber(dataInfo.getSchemaId()));
			} else {
				dataObj.put("r_length", -1);
			}
			dataObj.put("r_name", dataInfo.getSchemaName());
			dataObj.put("type", type);
			result.add(dataObj);
		}
		return result;
	}
	
	private Integer getDataNumber(Integer r_id) {
		DataInfoQueryParams param = new DataInfoQueryParams();
		param.setRelateId(r_id);
		param.setType(0);
		List<DataInfo> tmp = logMgrService.getDataInfosByParams(param);
		if(tmp == null) {
			return 0;
		}
		return tmp.size();
	}
	
	private JSONArray dataInfoToArray(List<DataInfo> list, Integer ifselfhelp, Integer relatId) {
		JSONArray result = new JSONArray();
		for(DataInfo info : list) {
			JSONObject dataObj = new JSONObject();
			dataObj.put("add_time", info.getAddTime());
			dataObj.put("data_id", info.getDataId().toString());
			dataObj.put("data_name", info.getDataName());
			dataObj.put("id", info.getDataId().toString());
			dataObj.put("iffavor", 1);
			dataObj.put("ifselfhelp", ifselfhelp);
			dataObj.put("infavor", 0);
			dataObj.put("inselfhelp", 0);
			dataObj.put("name", info.getDataName());
			dataObj.put("r_id", relatId.toString());
			dataObj.put("range", "");
			dataObj.put("range_name", "");
			dataObj.put("sthash", "0");
			result.add(dataObj);
		}
		
		return result;
	}

}

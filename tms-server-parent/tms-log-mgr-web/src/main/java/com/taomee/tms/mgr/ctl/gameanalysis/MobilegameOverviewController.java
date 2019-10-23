/*package com.taomee.tms.mgr.ctl.gameanalysis;

import java.io.ObjectOutputStream.PutField;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.event.HyperlinkListener;

import org.apache.taglibs.standard.tag.common.sql.ResultImpl;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.Component;
import com.taomee.tms.mgr.entity.ResultInfo;


@Controller
@RequestMapping("/gameanalysis/mobilegame/overview")
public class MobilegameOverviewController {
	private static final Logger logger = LoggerFactory
			.getLogger(OverviewController.class);

	@Reference
	private LogMgrService logMgrService;
    
	//@Reference
	//private List<ResultInfo> ResultInfos;
	
	
	@RequestMapping(value = "/index/{id}")
	public String index(PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		//logger.info("ComponentQueryForm is " + JSON.toJSONString(form));

		return "gameanalysis/overview";
	}
	
	// TODO getModules后期可以整合
	// TODO 写表单整合，后面需要获取数据
	// 页面发送http请求调用此方法,获取数据，再用json返回
	@RequestMapping(value = "/viewdata")
	public void viewData(HttpServletRequest request, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		//System.out.println("viewdata");
		//　可以得到from_time 和 to_time
		
		//System.out.println("from_time" + request.getParameter("gpzs_id"));
		//System.out.println("to_time" + request.getAttribute("to[0]"));
		
		// 查找dataname先关的数据
		// 获取serverid
		// 再次去查询
		// select * from dataid
		
		JSONObject dataObject = new JSONObject();
		
		
		printWriter.write("{\"result\":0,\"data\":[{\"name\":\"新增用户数\",\"data\":[\"0\"]},{\"name\":\"新增角色数\",\"data\":[\"0\"]},{\"name\":\"收入（元）\",\"data\":[\"0\"]}]}");
		printWriter.flush();
		// TODO 直接拷贝
		//"/json/overview/gameanalysis-overview-keymetrics.json";
	}
	
	@RequestMapping(value = "/isTaskComplete")
	public void isTaskComplete(HttpServletRequest request, HttpServletResponse response) {
		// TODO
		// 修改返回包
	}
    


}*/
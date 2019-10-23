package com.taomee.tms.mgr.ctl.common;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.Component;
import com.taomee.tms.mgr.form.ComponentQueryForm;
import com.taomee.tms.mgr.form.ComponetSaveForm;
import com.taomee.tms.mgr.form.Outer;
import com.taomee.tms.mgr.tools.ModuleBuilder;
//import com.taomee.tms.mgr.tools.ComponentTools;
import com.taomee.tms.mgr.tools.WrapConverTools;
import com.taomee.tms.mgr.tools.TabsConvertTools;
import com.taomee.tms.mgr.tools.TabConvertTools;
import com.taomee.tms.mgr.tools.DataConvertTools;
import com.taomee.tms.mgr.tools.ListtableConvertTools;
import com.taomee.tms.mgr.tools.Message;

@Controller
@RequestMapping("/common/component")
public class ComponentController {
	private static final Logger logger = LoggerFactory
			.getLogger(ComponentController.class);

	/*@Reference*/
	@Autowired
	private LogMgrService logMgrService;
	
	
	@RequestMapping(value = "/getComponents")
	public void getComponents(ComponentQueryForm form, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		logger.info("ComponentQueryForm is " + JSON.toJSONString(form));
//		int result = 0;
		
		String moduleKey = form.getModuleKey();
		Integer parentId = Integer.valueOf(form.getParentId());
		Integer gameId = form.getGameId();
		//String moduleKey = "gameanalysis-overview-keymetrics";
		//Integer parentId = 725;
		
		// TODO validator
		System.out.println("-----------moduleKey:" + form.getModuleKey() + "------------\n");
//		System.out.println("--------------parentId:" + form.getParentId() + "-----------\n");
		
		List<Component> components = logMgrService.getComponents(moduleKey, parentId, gameId);
		
		///////////////////////////////////////////
		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < components.size(); i++){
			Component component = components.get(i);
			JSONObject jsonObject;
			String properties = component.getProperties();
			System.out.print("===properties:" +properties+"\n");
			if(properties.length() == 0){
				jsonObject = new JSONObject();
			} else {
				jsonObject = JSON.parseObject(properties);
			}
			
			jsonObject.put("component_id", component.getComponentId());
			jsonObject.put("component_type",component.getComponentType() );
			jsonObject.put("parent_id", component.getParentId());
			jsonObject.put("module_key",component.getModuleKey() );
			jsonObject.put("display_order", component.getDisplayOrder());
			jsonObject.put("hidden", component.getHidden());
			jsonObject.put("ignore_id", component.getIgnoreId());
			jsonObject.put("component_title", component.getComponentTitle());
			jsonObject.put("component_desc", component.getComponentDesc());
			//System.out.println("jsonObject:"+jsonObject+"\n");
			
			jsonArray.add(jsonObject);
		//	System.out.println("-------------------------------------\n");
		}
		///////////////////////////////////////////
		
//		printWriter.write("{\"result\":" + result + ",\"data\":" + JSON.toJSONString(components) + "}");
		printWriter.write("{\"result\":0,\"data\":" + jsonArray + "}");
//		printWriter.write("{\"result\":0}");
		
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/addComponents")
	public void addComponents(ComponetSaveForm form, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		System.out.println("addComponents :" + form.getJsonString());
		
		String JsonString = form.getJsonString();
		Integer result = 0;
		JSONObject back_info = new JSONObject();
		//Component componet = creatComponent(JsonString);
		do{
			Component componet = creatComponent(JsonString);
			if(componet == null){
				result = -1;
				break;
			}
			componet.setGameId(form.getGameId());
			//System.out.println("=============GameId :"+form.getGameId()+"\n");
			//Integer result = 0;
			//JSONObject back_info = new JSONObject();
			back_info.put("component_id", 0);
			
			Integer component_id = logMgrService.insertComponentInfo(componet);
			back_info.put("component_id", component_id);
			System.out.println("component_id :"+component_id+"\n");
		}while(false);
		
		printWriter.write("{\"result\":" + result + ",\"data\":" + back_info + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	public Component creatComponent(String jsonString) {
		// TODO Auto-generated method stub
		JSONObject back_info = new JSONObject();
		back_info.put("component_id", 0);
		//ComponentTools tools = new ComponentTools();
		WrapConverTools wrapTools = new WrapConverTools();
		TabsConvertTools tabsTools = new TabsConvertTools();
		TabConvertTools tabTools = new TabConvertTools();
		DataConvertTools dataTools = new DataConvertTools();
		ListtableConvertTools littableTools = new ListtableConvertTools();
		Component componet;
		do{
			//获取ajax发来的jsonString
			if(jsonString.length() == 0) {
				return null;
			} 
			JSONObject jsonObject = JSON.parseObject(jsonString);
			
			//jsonString转换成Component类型
			switch(jsonObject.getString("component_type"))
			{
			case "data":
				componet = dataTools.toData(jsonObject);
				break;
			case "tabs":
				componet = tabsTools.toTabs(jsonObject);
				break;
			case "tab":
				componet = tabTools.toTab(jsonObject);
				break;
			case "listtable":
				componet = littableTools.toListtable(jsonObject);
				break;
			case "wrap":
				componet = wrapTools.toWrap(jsonObject);
				break;
			default:
				return null;
				//printWriter.write("{\"result\":" + result + ",\"data\":" + back_info + "}");
				//printWriter.flush();
				//printWriter.close();
			//	return;
			}
		}while(false);
		return componet;
	}
		
	@RequestMapping(value = "/updateComponent")
	public void updateComponent(ComponetSaveForm form, PrintWriter printWriter, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		System.out.println("updateComponent :" + form.getJsonString() + "\n");
		
		String jsonString = form.getJsonString();
		Integer result = 0;
		JSONObject back_info = new JSONObject();
		//Component componet = creatComponent(JsonString);
		do{
			System.out.println("JsonString :"+jsonString+"\n");
			Component componet = creatComponent(jsonString);
			JSONObject jsonObject = JSON.parseObject(jsonString);
			if(componet == null){
				System.out.println("componet null\n");
				result = -1;
				break;
			}
			
			Integer id = jsonObject.getInteger("component_id");
			componet.setComponentId(id);
			componet.setGameId(jsonObject.getInteger("game_id"));
			//Integer id = componet.getParentId();
			System.out.println("id :"+id+"\n");
			//result = -1;
			//break;
			
			List<Component> components = logMgrService.getComponentInfoByComponentId(id);
			System.out.println("size :"+components.size()+"\n");
			System.out.println("Component :"+JSON.toJSONString(components)+"\n");
			if(components.size() == 0){
				result = -1;
				break;
			}
			//Integer result = 0;
			//JSONObject back_info = new JSONObject();
			//back_info.put("component_id", 0);
			System.out.println("friendly="+componet.toString());
			logMgrService.updateComponentInfo(componet);
			back_info.put("component_id", id);
		}while(false);
		
		//printWriter.write("{\"result\":0}");
		printWriter.write("{\"result\":" + result + ",\"data\":" + back_info + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/getComponentById")
	public void getComponentById(ComponentQueryForm form, PrintWriter printWriter, HttpServletResponse response){
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		System.out.println("getComponentById :" + JSON.toJSONString(form));
		System.out.println("Id :" + form.getComponentId());
		
		Integer id = form.getComponentId();
		List<Component> components = logMgrService.getComponentInfoByComponentId(id);
		System.out.println("size :"+components.size()+"\n");
		System.out.println("Component :"+JSON.toJSONString(components)+"\n");
		
		///////////////////////////////////////////
		//JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;
		if(components.size() != 0){
			Component component = components.get(0);
			String properties = component.getProperties();
			jsonObject = JSON.parseObject(properties);
			
			jsonObject.put("component_id", component.getComponentId());
			jsonObject.put("component_type",component.getComponentType() );
			jsonObject.put("parent_id", component.getParentId());
			jsonObject.put("module_key",component.getModuleKey() );
			jsonObject.put("display_order", component.getDisplayOrder());
			jsonObject.put("hidden", component.getHidden());
			jsonObject.put("ignore_id", component.getIgnoreId());
			jsonObject.put("component_title", component.getComponentTitle());
			jsonObject.put("component_desc", component.getComponentDesc());
			System.out.println("jsonObject:"+jsonObject+"\n");
			//jsonArray.add(jsonObject);
		}else {
			jsonObject = new JSONObject();
		}
		
		//printWriter.write("{\"result\":0}");
		//printWriter.write("{\"result\":" + 0 + ",\"data\":" + 20 + "}");
		printWriter.write("{\"result\":0,\"data\":" + jsonObject + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/build")
	@ResponseBody
	public Message build(ComponentQueryForm form, HttpServletResponse response){
		response.setCharacterEncoding("UTF-8"); //设置编码格式
		response.setContentType("text/html");   //设置数据格式
		
		System.out.println("build :" + JSON.toJSONString(form));
		String moduleKey = form.getModuleKey();
		Integer gameId = form.getGameId();
		String buildPath = "json";
		ModuleBuilder builder = new ModuleBuilder(moduleKey, buildPath, gameId,logMgrService);
		
		String buildresult = builder.build();
		Message rest = new Message().setSuccessMessage().setData(buildresult);
		return rest;
	}
	
	// TODO 传参的方式需要改变下，这里只需要用到component_id, 后续传参考虑用表单实现
	@RequestMapping(value = "/delete")
	public void delete(HttpServletRequest request, PrintWriter printWriter) {
		
		// 擦数只有一个component_id
		int result = 1;
			
		Integer component_id = Integer.parseInt(request.getParameter("component_id"));
			
					
		try {
			logMgrService.deleteComponentInfos(component_id);
			result = 0;
		}catch(Exception e){
			logger.error("mysql err" + e.getMessage());
			
		}
			
			// TODO 返回值需要修改
		printWriter.write("{\"result\":" + result + "}");
		printWriter.flush();
		printWriter.close();
	}
	
	@RequestMapping(value = "/getIgnoredComponents")
	public void getIgnoredComponents(PrintWriter printWriter) {
		List<Component> iComponents = logMgrService.getAllComponentsByIgnored();
		
		printWriter.write("{\"result\":0,\"data\":" + JSON.toJSONString(iComponents) + "}");
		printWriter.flush();
		printWriter.close();
	}
	
}
























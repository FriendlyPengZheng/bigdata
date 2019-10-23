package com.taomee.tms.mgr.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import org.springframework.stereotype.Controller;








import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.entity.Component;
import com.taomee.tms.mgr.tools.ComponentTools;

public class ModuleBuilder {
	private String buildPath;
	private String moduleKey;
	private Integer gameId;
	private LogMgrService logMgrService;
	private JSONArray basicDatas = new JSONArray();
	private JSONObject wrapData;
	
	public ModuleBuilder(String key, String path, Integer id, LogMgrService service){
		buildPath = path;
		moduleKey = key;
		gameId = id;
		logMgrService = service;
	}
	
	public String build()
	{
		String sepa = java.io.File.separator;
		String[] keyParts = moduleKey.split("-"); 
		buildPath = System.getProperty("tm.web.root") + sepa + "res" + sepa + "json" + sepa + keyParts[0];
		
		
		//File f = new File(System.getProperty("tm.web.root")); 
		//System.out.println("---------------------------------"+f+"\n");
		
		//String path = f + "\\..\\..\\..\\..\\..\\..\\..\\..\\tms-server-parent\\tms-log-mgr-web\\src\\main\\webapp\\res\\json\\gameanalysis\\";
		//String path = f + "\\..\\..\\..\\..\\..\\..\\..\\..\\";
		
		//JSONArray file = new JSONArray();
		//System.out.print("module_key :" + moduleKey +", buildPath :" + buildPath + "\n");
		JSONArray file = buildComponents(moduleKey, 0, gameId); 
		//System.out.print(file + "\n");
		//List<Component> components = logMgrService.getComponents(moduleKey, 0);
		
		
		//FileOutputStream  writename = new FileOutputStream (path + moduleKey + ".json");
		
		//System.out.println("-------------charset:"+Charset.defaultCharset()+"\n");
		String fileName = new String(); 
		if(gameId == 0) {
			fileName = buildPath + sepa + moduleKey + ".json";
		} else {
			fileName = buildPath + sepa + moduleKey + "-" + gameId.toString() + ".json";
		}
		
		System.out.println("==========fileName: " + fileName +"\n");
		File writename = new File(fileName);
		ComponentTools tools = new ComponentTools();
		try {
			//FileOutputStream  writename = new FileOutputStream (path + moduleKey + ".json");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			String fileStr = tools.utf8ToUnicode(file.toString());
			out.write(fileStr);
	        out.flush(); // 把缓存区内容压入文件  
	        out.close(); // 最后记得关闭文件
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 创建新文件
		
		return fileName;//这里要返回路径
	}

	private JSONArray buildComponents(String moduleKey2, int parent_id, int game_id) {
		// TODO Auto-generated method stub
		//1.获取Component的list
		//for(){
		//1. properties转换成JSONObject
		//2. JSONObject build
		//3. children = builderComponents(modulekey2, parnet_id)
		//}
		
		List<Component> components = logMgrService.getComponents(moduleKey2, parent_id, game_id);
		JSONArray jsonComponents = new JSONArray();
		for(int i = 0; i < components.size(); i++){
			Component component = components.get(i);
			JSONObject jsonComponet = buildComponent(component);
			//if(jsonComponet.get("id").equals("928")){
				//System.out.println("^^^^^^^jsonComponet:" +jsonComponet.toString());
			//}
			if(!jsonComponet.getString("type").equals("data")){
				JSONArray children = buildComponents(moduleKey2, component.getComponentId(), game_id);
				jsonComponet.put("child", children);
			}
			
			//if type = wrap
			if(jsonComponet.getString("type").equals("wrap")){
				clearBasicData();
			}
			jsonComponents.add(jsonComponet);
		}
		
		return jsonComponents;
	}

	private JSONObject buildComponent(Component component) {
		// TODO Auto-generated method stub
		//WrapConverTools wrapTools = new WrapConverTools();
		//TabsConvertTools tabsTools = new TabsConvertTools();
		//TabConvertTools tabTools = new TabConvertTools();
		//DataConvertTools dataTools = new DataConvertTools();
		
		JSONObject jsonComponent = toJsonObject(component);
		switch(jsonComponent.getString("type"))
		{
		case "wrap":
			WrapConverTools wrapTools = new WrapConverTools();
			wrapTools.build(jsonComponent, this);
			//System.out.print("jsonComponent :" + jsonComponent + "\n");
			break;
		case "tabs":
			break;
		case "tab":
			TabConvertTools tabTools = new TabConvertTools();
			tabTools.build(jsonComponent, this);
			//System.out.print("jsonComponent :" + jsonComponent + "\n");
			break;
		case "data":
			DataConvertTools dataTools = new DataConvertTools();
			dataTools.build(jsonComponent, this);
			//System.out.print("jsonComponent :" + jsonComponent + "\n");
			break;
		case "listtable":
			ListtableConvertTools littableTools = new ListtableConvertTools();
			littableTools.build(jsonComponent, this);
			//System.out.print("jsonComponent :" + jsonComponent + "\n");
		default:
			break;
		}
		
		//System.out.print("jsonComponent :" + jsonComponent + "\n");
		return jsonComponent;
		
	}

	private JSONObject toJsonObject(Component component) {
		// TODO Auto-generated method stu
		//ComponentTools tools = new ComponentTools();
		String properties = component.getProperties();
		//properties = tools.utf8ToUnicode(properties);
		JSONObject obj;
		if(properties.isEmpty()){
			obj = new JSONObject();
		} else {
			obj = JSON.parseObject(properties);
		}
		
		obj.put("id", component.getComponentId());
		obj.put("type", component.getComponentType());
		obj.put("ignore_id", component.getIgnoreId());
		return obj;
	}

	public JSONObject endBasicData() {
		if(this.basicDatas.size() == 0) {
			return null;
		}
		return this.basicDatas.getJSONObject(this.basicDatas.size() - 1);
	}

	public void pushBasicData(JSONObject basicData) {
		//System.out.print("size:" +this.basicDatas.size()+ " basicData:" +basicData.toString()+"\n");
		this.basicDatas.add(basicData);
	}
	
	public JSONObject popBasicData(){
		if(this.basicDatas.size() == 0) {
			return null;
		}
		
		int i = this.basicDatas.size() - 1;
		JSONObject obj = this.basicDatas.getJSONObject(i);
		this.basicDatas.remove(i);
		return obj;
	}

	public void clearBasicData(){
		this.basicDatas.clear();
	}
	public ModuleBuilder addExprsWrapData(JSONArray data) {
		// TODO Auto-generated method stub
		this.wrapData.put("key", "data_info");
		return addWrapData(data);
	}

	private ModuleBuilder addWrapData(JSONArray data) {
		// TODO Auto-generated method stub
		JSONArray array = new JSONArray();
		if(!this.wrapData.containsKey("period")){
			this.wrapData.put("common", "");
			this.wrapData.put("group", array);
			this.wrapData.put("period", array);
			this.wrapData.put("data", "");
		}
		JSONObject aGroup = new JSONObject();
		int iNextStartIdx = this.wrapData.getJSONArray("data").size();
		for(int i = 0; i < data.size(); i++) {
			JSONObject info = data.getJSONObject(i);
			info.put("common", info.getString("common").trim());
			info.put("data", info.getString("data").trim());
			//JSONArray infoDatas = info.getJSONArray("data");
			//JSONArray newArray = new JSONArray();
			//for(int j = 0; j < infoDatas.size(); j++){
			//	String infoData = infoDatas.getString(j);
			//	newArray.add(infoData.trim());
			//}
			//info.put("data", newArray);
			if(!info.getString("common").isEmpty()){
				URL url = null;
				try{
					url = new URL(info.getString("common"));
				}catch(IOException e){
					e.printStackTrace();
				}
				String queryString = url.getQuery();
				queryString = handleUrl(queryString);
				this.wrapData.put("common", url.getPath() + "?" +queryString);
			}
			
			if(!info.getString("data").isEmpty()){
				String dataStr = info.getString("data");
				String[] list = dataStr.split(",");
				for(String str : list){
					JSONArray dataArray = new JSONArray();
					JSONArray periodArray = new JSONArray();
					JSONObject period = new JSONObject();
					dataArray.add(str);
					period.put("period", i+1);
					periodArray.add(period);
					this.wrapData.put("period", periodArray);
					if(!aGroup.containsKey("start")){
						aGroup.put("start", iNextStartIdx);
					}
				}
			}
		}
		JSONArray group = this.wrapData.getJSONArray("group");
		group.add(aGroup);
		this.wrapData.put("group", group);
		return this;
	}

	private String handleUrl(String queryString) {
		// TODO Auto-generated method stub
		String[] queryParam = queryString.split("&");
		List<String> list = new ArrayList<String>();
		for(String str : queryParam){
			String tmp = str.substring(0,6);
			if(tmp.equals("group_")) {
				list.add(tmp);
			} else {
				list.add(str);
			}
		}
		
		String result = new String();
		for(int i = 0; i < list.size(); i++){
			result = result + list.get(i) + "&";
		}
		
		if(result.length() != 0){
			result = result.substring(0, result.length() - 1);
		}
		
		return result;
	}

}

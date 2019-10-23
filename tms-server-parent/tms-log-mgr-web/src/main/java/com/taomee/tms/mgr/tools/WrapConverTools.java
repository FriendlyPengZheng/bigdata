package com.taomee.tms.mgr.tools;

import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.entity.Component;

public class WrapConverTools extends ComponentTools{
	private void initParameters(JSONObject parameters){
		JSONArray jsonArray = new JSONArray();
		parameters.put("title","");
		parameters.put("ignore",0);
		parameters.put("ignoreId","");
		parameters.put("headEnabled",0);
		parameters.put("headTime",jsonArray);
		parameters.put("condition",jsonArray);
		parameters.put("bottomEnabled",0);
		parameters.put("width",50);
		parameters.put("remove","");
		parameters.put("nameListUrl","");
		parameters.put("renameUrl","");
		parameters.put("edit","");
		parameters.put("download","");
		parameters.put("favor","");
		parameters.put("comment","");
		parameters.put("heatmap","");
		parameters.put("attr_key",jsonArray);
		parameters.put("attr_value",jsonArray);
		parameters.put("isSelControl",0);
		parameters.put("selControl",jsonArray);
		parameters.put("process_type","expres");
		parameters.put("process_list",jsonArray);
		parameters.put("data_info",jsonArray);	
	}
	
	private void buildSelControlData(JSONObject config){
		JSONArray data = new JSONArray();
		if(!config.containsKey("control_id")) {
			config.put("data", data);
			return;
		}
		if(config.getJSONArray("control_id").size() != 0)
		{
			//Set<String> keys = config.keySet();
			JSONArray controlId = config.getJSONArray("control_id");
			JSONArray controlName = config.getJSONArray("control_name");
			for(int i = 0; i < controlId.size(); i++){
				JSONObject dataInfo = new JSONObject();
				dataInfo.put("id", controlId.get(i));
				//name需要国际化
				dataInfo.put("name", controlName.get(i));
				data.add(dataInfo);
			}

		}
		config.put("data", data);
	}
	
	private void buildSelControl(JSONObject src){
		if((src.containsKey("isSelControl") == false) 
				|| (src.containsKey("selControl") == false)
				|| src.getInteger("isSelControl") == 0
				|| src.getJSONObject("selControl").isEmpty()){
			src.put("isSelControl", false);
			src.remove("selControl");
			return;
		}

		src.put("isSelControl", true);
		JSONObject control = src.getJSONObject("selControl");
		control.put("isMatch", control.getBoolean("isMatch"));
		if(src.containsKey("isMultiple")){
			control.put("isMultiple", control.get("isMultiple"));
		}else {
			control.put("isMultiple", false);
		}
		
		JSONArray selConfig = control.getJSONArray("selConfig");
		//JSONArray configs = new JSONArray();
		for(int i = 0; i < selConfig.size(); i++){
			JSONObject config = selConfig.getJSONObject(i);
			//国际化
			//config.put("titlePre",config.getJSONArray("titlePre"));
			//config.put("titleSuf",config.getJSONArray("titleSuf"));
			buildSelControlData(config);
			JSONObject url = new JSONObject();
			url.put("page", config.get("urlPage"));
			url.put("extend", config.get("urlExtend"));
			config.put("url", url);
			config.put("isAjax", config.getBoolean("isAjax"));
			config.remove("control_id");
			config.remove("control_name");
			config.remove("urlPage");
			config.remove("urlExtend");
			
			//configs.add(config);
		}
		control.put("config", selConfig);
		control.remove("selConfig");
		src.put("selControl", control);
	}
	
	
	private void buildHeadTime(JSONObject src){
		JSONObject conf = new JSONObject();
		//后面加上国际化
		conf.put("1", "日");
		conf.put("2", "周");
		conf.put("3", "月");
		conf.put("4", "分钟");
		conf.put("5", "小时");
		conf.put("6", "版本周");
		
		if(src.containsKey("headTime")){
			if(src.getJSONArray("headTime").size() != 0){
				JSONArray tmp = new JSONArray();
				JSONArray headTime = src.getJSONArray("headTime");
				for(int i = 0; i < headTime.size(); i ++) {
					String num = headTime.getString(i);
					if(conf.containsKey(num)){
						JSONObject info = new JSONObject();
						info.put("title", conf.getString(num));
						info.put("dataId", num);
						tmp.add(info);
					}
				}
				src.put("headTime", tmp);
			}else {
				//System.out.print("no headTime------------\n");
				src.remove("headTime");
			}
		}
	}
	
	JSONObject build(JSONObject obj, ModuleBuilder builder) {
		//title 国际化翻译
		//TODO
		buildDataInfo(obj);
		JSONObject basicData = new JSONObject();
		basicData.put("process_type", obj.get("process_type"));
		basicData.put("process_list",obj.get("process_list"));
		basicData.put("data_info",obj.get("data_info"));
		builder.pushBasicData(basicData);
		//------------------可能改回来----------------------------
		//builder.pushBasicData(basicData);
		obj.remove("process_type");
		obj.remove("process_list");
		obj.remove("data_info");
		
		String renameUrl = obj.getString("renameUrl");
		String nameListUrl = obj.getString("nameListUrl");
		if(renameUrl.length() != 0 && nameListUrl.length() != 0){
			JSONObject rename = new JSONObject();
			rename.put("renameUrl", renameUrl);
			rename.put("nameListUrl", nameListUrl);
			obj.put("rename", rename);
			obj.remove("renameUrl");
			obj.remove("nameListUrl");
		}
		
		double width = obj.getDoubleValue("width");
		obj.put("width", width/100);
		obj.put("ignore", obj.getBoolean("ignore"));
		obj.put("headEnabled", obj.getBoolean("headEnabled"));
		obj.put("bottomEnabled", obj.getBoolean("bottomEnabled"));
		
		buildAttr(obj);
		buildSelControl(obj);
		buildHeadTime(obj);
		return obj;
	}
	
	private void buildDataInfo(JSONObject obj) {
		// TODO Auto-generated method stub
		JSONArray dataInfoArray = obj.getJSONArray("data_info");
		if(dataInfoArray.isEmpty()){
			return;
		}
		
		JSONArray tmpArray = new JSONArray();
		for(int i = 0; i < dataInfoArray.size(); i++){
			JSONObject tmpObject = new JSONObject();
			String dataString = dataInfoArray.getString(i);
			String[] dataList = dataString.split("&");
			if(dataList.length == 0){
				continue;
			}
			for(String str : dataList){
				//System.out.print("data_info: "+str+"\n");
				String[] info = str.split("=");
				String key = info[0];
				String value = new String();
				if(info.length > 1){
					value = info[1];
				}
				tmpObject.put(key, value);
			}
			tmpArray.add(tmpObject);
		}
		
		obj.put("data_info", tmpArray);
		//System.out.print("data_info: "+obj.getJSONArray("data_info").toString()+"\n");
	}

	private JSONObject convert(JSONObject src){
		JSONObject parameters = new JSONObject();
		initParameters(parameters);
		convertArrayfromStr("data_info", src);
		putParamters(parameters, src);
		
		return parameters;
	}
	
	public Component toWrap(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(WRAP_TYPE);
		putComponent(res, jsonObject);
		
		JSONObject parameters = convert(jsonObject);
		//res.setProperties(getUnicode(parameters.toString()));
		res.setProperties(utf8ToUnicode(parameters.toString()));
		//res.setProperties(GBK2Unicode(parameters.toString()));
		
		return res;
	}
}

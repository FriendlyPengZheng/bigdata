package com.taomee.tms.mgr.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.entity.Component;

public class ListtableConvertTools extends ComponentTools{
	public Component toListtable(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(LISTTABLE_TYPE);
		putComponent(res, jsonObject);
		
		JSONObject parameters = convert(jsonObject);
		res.setProperties(utf8ToUnicode(parameters.toString()));
		
		return res;
	}

	private JSONObject convert(JSONObject src) {
		// TODO Auto-generated method stub
		JSONObject parameters = new JSONObject();
		initParameters(parameters);
		putParamters(parameters, src);
		
		//JSONObject url = new JSONObject();
		//url.put("page", src.get("urlPage"));
		//url.put("extend", src.get("urlExtend"));
		//parameters.put("url", url);
		//todo
		
		
		return parameters;
	}

	private void initParameters(JSONObject parameters) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		parameters.put("urlExtend","");
		parameters.put("urlPage","");
		parameters.put("renameUrl","");
		parameters.put("isAjax",1);
		parameters.put("thead_type",jsonArray);
		parameters.put("thead_title",jsonArray);
		parameters.put("appendColumns",jsonArray);
		parameters.put("urlPagination","");
		parameters.put("enablePagination",1);
	}

	public void build(JSONObject obj, ModuleBuilder builder) {
		System.out.println("obj-----listtable---------" + obj);
		// TODO Auto-generated method stub
		JSONObject url = new JSONObject();
		url.put("page", obj.get("urlPage"));
		url.put("extend", obj.get("urlExtend"));
		url.put("paginationUrl", obj.get("urlPagination"));
		obj.put("url", url);
		//---------------add wrapdata		
		//TODO
		obj.remove("urlPage");
		obj.remove("urlPagination");
		obj.remove("urlExtend");
		JSONArray theadType;
		JSONArray theads = new JSONArray();
		if(!obj.getJSONArray("thead_type").isEmpty()){
			theadType = obj.getJSONArray("thead_type");
			for(int i = 0; i < theadType.size(); i++){
				JSONObject thead = new JSONObject();
				thead.put("type", theadType.get(i));
				thead.put("title", (obj.getJSONArray("thead_title")).get(i));
				theads.add(thead);
			}	
		}
		obj.put("thead", theads);
		obj.remove("thead_type");
		obj.remove("thead_title");
		obj.put("isAjax", obj.getBoolean("isAjax"));
		buildAppendColumns(obj);
	}

	private void buildAppendColumns(JSONObject obj) {
		// TODO Auto-generated method stub
		JSONArray appendColumns = new JSONArray();
		JSONArray array = obj.getJSONArray("appendColumns");
		for(int i = 0; i < array.size(); i++) {
			JSONObject column = array.getJSONObject(i);
			if(column.getBooleanValue("isFn")){
				appendColumns.add(column.get("fn"));
				continue;
			}
			JSONObject info = new JSONObject();
			info.put("type", column.get("type"));
			info.put("key", column.get("key"));
			if(column.getBooleanValue("isID")){
				info.put("isID", column.getInteger("isID"));
			} else {
				info.put("isID", 0);
			}
			appendColumns.add(info);
		}
		obj.put("appendColumns", appendColumns);
	}
}

package com.taomee.tms.mgr.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.entity.Component;

public class TabConvertTools extends ComponentTools{
	public Component toTab(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(TAB_TYPE);
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
		
		return parameters;
	}

	private void initParameters(JSONObject parameters) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		parameters.put("title","");
		parameters.put("tabsSkin","");
		parameters.put("ignore",0);
		parameters.put("ignoreId","");
		parameters.put("attr_key",jsonArray);
		parameters.put("attr_value",jsonArray);
	}

	public void build(JSONObject obj, ModuleBuilder moduleBuilder) {
		// TODO Auto-generated method stub
		//title国际化
		if(obj.getInteger("ignore") == 0){
			obj.put("ignore", false);
		} 
		
		buildAttr(obj);	
	}
}

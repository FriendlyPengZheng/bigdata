package com.taomee.tms.mgr.tools;

import com.alibaba.fastjson.JSONObject;
import com.taomee.tms.mgr.entity.Component;

public class TabsConvertTools extends ComponentTools{
	public Component toTabs(JSONObject jsonObject){
		Component res = new Component();
		res.setComponentType(TABS_TYPE);
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
		parameters.put("tabsSkin","");
	}
}

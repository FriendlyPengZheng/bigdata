package com.taomee.tms.mgr.core.cascadeanalyser;

import java.util.Map;

public abstract class BaseCascadeAnalyser {
	protected String cascadeKeyField;
	
	public BaseCascadeAnalyser(String field) {
		cascadeKeyField = field;
	}
	
	public abstract String GetCascadeValue(Map<String, String> attrMap);
	
	public String getCascadeKeyField(){
		return this.cascadeKeyField;
	}
}






















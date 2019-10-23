package com.taomee.tms.mgr.core.cascadeanalyser;

import java.io.Serializable;
import java.util.Map;

public class PlainCascadeAnalyser extends BaseCascadeAnalyser implements Serializable {
	private static final long serialVersionUID = -7420781833746468766L;

	public PlainCascadeAnalyser(String field) {
		super(field);
	}

	@Override
	public String GetCascadeValue(Map<String, String> attrMap) {
		return attrMap.get(cascadeKeyField);
	}
	
	public String toString(){
		return this.cascadeKeyField;
	}
	
}

package com.taomee.tms.mgr.core.opanalyser;

import java.io.Serializable;

public class DistinctCountOpAnalyser extends BaseOpAnalyser implements Serializable{
	private static final long serialVersionUID = -769347035405020786L;
	
	@Override
	public boolean IsRealtime() {
		return false;
	}

	@Override
	public boolean IsNonRealtime() {
		return true;
	}
	
	@Override
	public String GetOp() { 
		return "distinct_count";
	}

}

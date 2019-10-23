package com.taomee.tms.mgr.core.opanalyser;

import java.io.Serializable;

public class SumOpAnalyser extends BaseOpAnalyser implements Serializable{
	private static final long serialVersionUID = -3529793233618551237L;
	
	@Override
	public boolean IsRealtime() {
		return true;
	}

	@Override
	public boolean IsNonRealtime() {
		return true;
	}
	
	@Override
	public String GetOp() { 
		return "sum";
	}
}

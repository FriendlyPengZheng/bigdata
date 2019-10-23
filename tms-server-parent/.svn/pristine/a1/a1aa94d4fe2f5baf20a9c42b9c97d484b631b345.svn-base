package com.taomee.tms.mgr.core.opanalyser;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaterialOpAnalyser extends BaseOpAnalyser implements Serializable{
	private static final long serialVersionUID = -4400420100674713926L;
	private static final Logger LOG = LoggerFactory.getLogger(MaterialOpAnalyser.class);

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
		return "material";
	}
	
	// 默认只处理一元函数，其他在各自子类中实现
	public boolean Init(List<String> fields) {
		this.opKeyFields = fields;

		if (opKeyFields == null || opKeyFields.size() < 1) {
			LOG.error("MaterialOpAnalyser Init, invalid opKeyFields null or size < 1");
			return false;
		}
		
		for (String opKeyField: opKeyFields) {
			if (opKeyField == null || opKeyField.isEmpty()) {
				LOG.error("MaterialOpAnalyser Init, invalid opKeyField null or empty");
				return false;
			}
		}

		return true;
	}
}



































package com.taomee.tms.mgr.core.schemaanalyser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.core.opanalyser.BaseOpAnalyser;
import com.taomee.tms.mgr.entity.SchemaInfo;

public class MaterialSchemaAnalyser extends BaseSchemaAnalyser {
	private static final long serialVersionUID = -8925383055622971299L;
	private static final Logger LOG = LoggerFactory.getLogger(MaterialSchemaAnalyser.class);
	
	String strMaterialId = null;
	
	public boolean Init(SchemaInfo schemaInfo, BaseOpAnalyser opAnalyser) {
		if (!super.Init(schemaInfo, opAnalyser)) {
			LOG.error("MaterialSchemaAnalyser Init, super Init failed");
			return false;
		}
		
        if (schemaInfo.getMaterialId() == null || schemaInfo.getMaterialId().intValue() <= 0) {
        	LOG.error("MaterialSchemaAnalyser Init, invalid materialId, schemaId is " + schemaInfo.getSchemaId());
        	return false;
        }
        
        strMaterialId = schemaInfo.getMaterialId().toString();
        
        return true;
	}
	
	public String GetMaterialId() {
		return strMaterialId;
	}

	public static void main(String[] args) {

	}
}























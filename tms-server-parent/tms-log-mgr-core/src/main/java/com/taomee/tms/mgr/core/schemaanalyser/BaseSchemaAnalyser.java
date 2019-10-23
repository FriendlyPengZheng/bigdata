package com.taomee.tms.mgr.core.schemaanalyser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taomee.tms.mgr.core.cascadeanalyser.BaseCascadeAnalyser;
import com.taomee.tms.mgr.core.cascadeanalyser.CascadeAnalyserFactory;
import com.taomee.tms.mgr.core.opanalyser.BaseOpAnalyser;
import com.taomee.tms.mgr.entity.SchemaInfo;

public class BaseSchemaAnalyser implements Serializable {
	private static final long serialVersionUID = 8170645701279444416L;
	private static final Logger LOG = LoggerFactory.getLogger(BaseSchemaAnalyser.class);
	
	List<BaseCascadeAnalyser> cascadeAnalysers = new ArrayList<BaseCascadeAnalyser>();
	BaseOpAnalyser opAnalyser = null;
	String strSchemaId = null;

	public String toString() {
//		StringBuffer buf = new StringBuffer();
//		buf.append("schemaId=" + strSchemaId);
//		buf.append(" opAnalyser={" + opAnalyser.toString() + "}");
//		return buf.toString();
		return "schemaId=" + strSchemaId+" opAnalyser={" + opAnalyser.toString() +"}"+ " cascadeAnalysers={cascadeFields="+cascadeAnalysers+"}" ;
	}

	public boolean Init(SchemaInfo schemaInfo, BaseOpAnalyser opAnalyser) {
		if (schemaInfo.getSchemaId() == null || schemaInfo.getSchemaId().intValue() <= 0) {
			LOG.error("BaseSchemaAnalyser Init, empty schemaId or schemaId < 0");
			return false;
		}
		
		strSchemaId = schemaInfo.getSchemaId().toString();
		
		if (schemaInfo.getCascadeFields() == null) {
			LOG.error("BaseSchemaAnalyser Init, empty cascadeFields");
			return false;
		}
		
		// 不能有空占位符
		if (schemaInfo.getCascadeFields().split("\\s", -1).length != 1) {
			LOG.error("BaseSchemaAnalyser Init, cascadeFields [" + schemaInfo.getCascadeFields() + "] contains empty charactor");
			return false;
		}
		
		// 级联字段现在以"|"分隔
		if (!schemaInfo.getCascadeFields().equals("")) {
			String[] slices = schemaInfo.getCascadeFields().split("\\|", -1);
			for (String slice : slices) {
				String cascadeExpression = slice;
				if (cascadeExpression.isEmpty()) {
					LOG.error("BaseSchemaAnalyser Init, invalid cascadeFields [" + schemaInfo.getCascadeFields() + "] contain empty field");
					return false;
				}
				
				BaseCascadeAnalyser cascadeAnalyser = CascadeAnalyserFactory.createCascadeAnalyser(cascadeExpression);
				if (cascadeAnalyser == null) {
					LOG.error("BaseSchemaAnalyser Init, createCascadeAnalyser failed, cascadeExpression is [" + cascadeExpression + "]");
					return false;
				}
				
				cascadeAnalysers.add(cascadeAnalyser);
			}	
		}
	
		
		this.opAnalyser = opAnalyser;
		if (this.opAnalyser == null) {
			LOG.error("BaseSchemaAnalyser Init, param opAnalyser null");
			return false;
		}
		
		return true;
	}

	
	public String GetOp() {
		return opAnalyser.GetOp();
	}
	
	public String GetStrSchemaId() {
		return strSchemaId;
	}
	
	public List<String> GetOpValues(Map<String, String> attrMap) {
		return opAnalyser.GetOpValues(attrMap);
	}
	
	public List<String> GetAllCascadeValues(Map<String, String> attrMap) {
		List<String> values = new ArrayList<String>();
		// 无论如何要插入""表示all
		values.add("");
		StringBuilder fullValue = new StringBuilder("");
		
		for (BaseCascadeAnalyser cascadeAnalyser: cascadeAnalysers) {
			String value = cascadeAnalyser.GetCascadeValue(attrMap);
			if (value == null || value.length() == 0) {
//				LOG.debug("Error:fail to get cascade value with field:{}",cascadeAnalyser.getCascadeKeyField());
				return values;
			}
			
			if (!fullValue.toString().isEmpty()) {
				fullValue.append("|");
			}
			fullValue.append(value);
			
			values.add(fullValue.toString());
		}
			
		return values;
	}

	public static void main(String[] args) {
//		Integer i = null;
//		System.out.println("null Integer toString is [" + i.toString() + "]");
		
		String tmp = "emtpy|";
		String[] slices = tmp.split("\\|", -1);
		for (String slice : slices) {
			if (slice.isEmpty()) {
				System.out.println("empty");
			}
			System.out.println("slice is --" + slice + "--");
		}
		
		String tmp2 = "af";
		System.out.println(tmp2.split("\\s", -1).length);
	}
}























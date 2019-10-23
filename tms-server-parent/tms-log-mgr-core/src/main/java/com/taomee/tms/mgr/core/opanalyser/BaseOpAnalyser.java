package com.taomee.tms.mgr.core.opanalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseOpAnalyser {
	private static final Logger LOG = LoggerFactory.getLogger(BaseOpAnalyser.class);
	
	protected List<String> opKeyFields = null;
	
	public abstract boolean IsRealtime();
	
	public abstract boolean IsNonRealtime();
	
	public abstract String GetOp();
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("op=");
		buf.append(GetOp());
		buf.append(", opKeyFields=");
		if (opKeyFields == null) {
			buf.append("null");
		} else {
			buf.append(opKeyFields.toString());
		}
		return buf.toString();
	}
	
	// 默认只处理一元函数，其他在各自子类中实现
	public boolean Init(List<String> fields) {
		this.opKeyFields = fields;
		
		if (opKeyFields == null || opKeyFields.size() != 1 || opKeyFields.get(0).length() == 0) {
			LOG.error("OpAnalyser Init, invalid opKeys");
			return false;
		}
		return true;
	}
	
	// 保证返回的List中没有emtpy的String
	// 默认处理是返回map中opKey对应的值，返回的List中只有一个元素
	public List<String> GetOpValues(Map<String, String> attrMap) {
		List<String> values = new ArrayList<String>();
		
		for (String opKeyField: opKeyFields) {
			String value = attrMap.get(opKeyField);
			if (value == null) {
				LOG.debug("Error: fail to get value for key {}",opKeyField);
				//这个warn和下面的warn一样，暂时注释掉，否则打印的日志文件太大了
				//LOG.warn("OpAnalyser GetOpValues, opKey " + opKeyField + " get null from log");
				return null;
				//continue;
			}
			
			value = value.trim();
			if (value.length() == 0) {
				//LOG.error("OpAnalyser GetOpValues, opKey " + opKeyField + " get empty from log");
				//LOG.warn("OpAnalyser GetOpValues, opKey " + opKeyField + " get null from log");
				return null;
				//continue;
			}
			
			values.add(value);
		}		
		
		return values;
	}
}






















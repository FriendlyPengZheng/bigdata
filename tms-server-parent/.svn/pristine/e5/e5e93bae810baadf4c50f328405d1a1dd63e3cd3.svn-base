package com.taomee.tms.mgr.core.cascadeanalyser;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 四舍五入
public class RoundCascadeAnalyser extends BaseCascadeAnalyser implements Serializable{
	private static final long serialVersionUID = 1069062670152814621L;
	private static final Logger LOG = LoggerFactory.getLogger(RoundCascadeAnalyser.class);

	public RoundCascadeAnalyser(String field) {
		super(field);
	}

	@Override
	public String GetCascadeValue(Map<String, String> attrMap) {
		double value;
		try {
			value = Float.parseFloat(attrMap.get(cascadeKeyField));
		} catch (NullPointerException ex) {
			LOG.error("RoundCascadeAnalyser GetCascadeValue, NullPointerException catched, field " + this.cascadeKeyField + " not in log");
			return null;
		} catch (NumberFormatException ex) {
			LOG.error("RoundCascadeAnalyser GetCascadeValue, NumberFormatException catched, in log field " + this.cascadeKeyField + " value is [" + attrMap.get(cascadeKeyField) + "]");
			return null;
		} catch (Exception ex) {
			LOG.error("RoundCascadeAnalyser GetCascadeValue, unknown exception [" + ex.getMessage() + "] catched, in log field " + this.cascadeKeyField + " value is [" + attrMap.get(cascadeKeyField) + "]");
			return null;
		}
		return Long.toString(Math.round(value));
	}
	
	public static void main(String[] args) {
		try {
//			String field = null;
//			String field = "abc";
			String field = "";
			float value = Float.parseFloat(field);
			System.out.println("value is " + Float.toString(value));
		} catch (NullPointerException ex) {
			System.out.println("NullPointerException catched");
		} catch (NumberFormatException ex) {
			System.out.println("NumberFormatException catched");
		}
		
	}

}

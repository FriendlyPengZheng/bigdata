package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class EmailTemplateDataInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8931014581083169382L;
	
	private Integer emailTemplateDataId;
	private Integer emailTemplateContentId;
	private String dataDateType;
	private String dataExpr;
	private String dataName;
	private Integer offset;
	private String unit;
	private Integer inTable;
	private Integer inGraph;
	
	public Integer getEmailTemplateDataId() {
		return emailTemplateDataId;
	}
	
	public void setEmailTemplateDataId(Integer emailTemplateDataId) {
		this.emailTemplateDataId = emailTemplateDataId;
	}

	public Integer getEmailTemplateContentId() {
		return emailTemplateContentId;
	}

	public void setEmailTemplateContentId(Integer emailTemplateContentId) {
		this.emailTemplateContentId = emailTemplateContentId;
	}

	public String getDataDateType() {
		return dataDateType;
	}

	public void setDataDateType(String dataDateType) {
		this.dataDateType = dataDateType;
	}

	public String getDataExpr() {
		return dataExpr;
	}

	public void setDataExpr(String dataExpr) {
		this.dataExpr = dataExpr;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getInTable() {
		return inTable;
	}

	public void setInTable(Integer inTable) {
		this.inTable = inTable;
	}

	public Integer getInGraph() {
		return inGraph;
	}

	public void setInGraph(Integer inGraph) {
		this.inGraph = inGraph;
	}
	

}

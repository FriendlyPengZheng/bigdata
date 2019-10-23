package com.taomee.tms.mgr.core.loganalyser;

import java.io.Serializable;
import java.util.List;

public class RealtimePlainLogItem implements Serializable {
	private static final long serialVersionUID = -104175224318837177L;
	
	private String strOp;
	private String schemaId;
	private String serverId;
	private String cascadeValue;
	private List<String> opValues;
	private String dateTime;
	
	public RealtimePlainLogItem(String strOp, String schemaId, String serverId, String cascadeValue, List<String> opValues, String dateTime) {
		this.strOp = strOp;
		this.schemaId = schemaId;
		this.serverId = serverId;
		this.cascadeValue = cascadeValue;
		this.opValues = opValues;
		this.dateTime = dateTime;
	}
	
	public String toString() {
		return "LogItemBean [strOp=" + strOp + ", schemaId=" + schemaId
				+ ", serverId=" + serverId + ", cascadeValue=" + cascadeValue
				+ ", opValues=" + opValues.toString() + " dateTime=" + dateTime + "]";
	}

	public String getStrOp() {
		return strOp;
	}

	public void setStrOp(String strOp) {
		this.strOp = strOp;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getCascadeValue() {
		return cascadeValue;
	}

	public void setCascadeValue(String cascadeValue) {
		this.cascadeValue = cascadeValue;
	}

	public List<String> getOpValues() {
		return opValues;
	}

	public void setOpValues(List<String> opValues) {
		this.opValues = opValues;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

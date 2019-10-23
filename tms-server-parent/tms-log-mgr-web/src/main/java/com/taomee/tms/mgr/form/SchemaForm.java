package com.taomee.tms.mgr.form;

import java.sql.Timestamp;

public class SchemaForm {
	private String schemaId;
	private Integer logId;
	private String op;
	private String cascadeFields;
	private Integer materialId;
	private String materialName;
	private Integer status;

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	public Integer getLogId() {
		return logId;
	}

	public void setLogId(Integer logId) {
		this.logId = logId;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getCascadeFields() {
		return cascadeFields;
	}

	public void setCascadeFields(String cascadeFields) {
		this.cascadeFields = cascadeFields;
	}

	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}

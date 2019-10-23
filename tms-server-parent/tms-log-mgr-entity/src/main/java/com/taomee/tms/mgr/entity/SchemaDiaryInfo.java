package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class SchemaDiaryInfo implements Serializable {
	@Override
	public String toString() {
		return "SchemaDiaryInfo [diaryId=" + diaryId + ", curd=" + curd
				+ ", schemaId=" + schemaId + ", logId=" + logId + ", op=" + op
				+ ", cascadeFields=" + cascadeFields + ", materialId="
				+ materialId + ", status=" + status + ", addTime=" + addTime
				+ "]";
	}

	private static final long serialVersionUID = -4200973544329127144L;


	private Integer diaryId;
	private Integer curd;     //crud  增1    改2    删3
	private Integer schemaId;
	private Integer logId;
	private String op;
	private String cascadeFields;
	private Integer materialId;
	private Integer status;
	private Timestamp addTime;

	public Integer getDiaryId() {
		return diaryId;
	}
	
	public void setDiaryId(Integer diaryId) {
		this.diaryId = diaryId;
	}

	public Integer getCurd() {
		return curd;
	}

	public void setCurd(Integer curd) {
		this.curd = curd;
	}

	public Integer getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Integer schemaId) {
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}
}

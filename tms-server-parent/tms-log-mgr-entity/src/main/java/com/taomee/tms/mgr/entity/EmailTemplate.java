package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class EmailTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3032314761934164722L;

	private Integer emailTemplateId;
	private String templateType;
	
	public Integer getEmailTemplateId() {
		return emailTemplateId;
	}
	
	public void setEmailTemplateId(Integer emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
}

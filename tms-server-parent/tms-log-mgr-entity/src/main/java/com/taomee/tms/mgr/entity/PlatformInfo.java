package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class PlatformInfo implements Serializable {
	@Override
	public String toString() {
		return "PlatformInfo [platformId=" + platformId + ", platformName="
				+ platformName + "]";
	}

	private static final long serialVersionUID = -1632824675007614450L;
	
	private Integer platformId;
	private String platformName;
	
	public Integer getPlatformId() {
		return platformId;
	}
	
	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
}

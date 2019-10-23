package com.taomee.tms.mgr.entity;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable {

	@Override
	public String toString() {
		return "Page [url=" + url + ", name=" + name + ", key=" + key
				+ ", title=" + title + "]";
	}

	private static final long serialVersionUID = 3895106722142673104L;

	private String url;
	private List<String> name;
	private String key;
	private String title;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getName() {
		return name;
	}

	public void setName(List<String> name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

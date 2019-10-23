package com.taomee.tms.mgr.tools.excle;

import java.lang.reflect.Array;
import java.util.HashSet;
//import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ExcelTreeNode {
	private String nodeName = null;
	private Integer level = null;
	private List<ExcelTreeNode> childs = new LinkedList<ExcelTreeNode>();
	private List<String> extreInfo = new LinkedList<String>();
	private Set<String> childsName = new HashSet<String>();
	
	public ExcelTreeNode(String nodeName, Integer level) {
		this.nodeName = nodeName;
		this.level = level;
	}
	
	public void addChild(ExcelTreeNode node) {
		if(node == null ||node.getNodeName() == null || childsName.contains(node.getNodeName())) {
			return;
		}
		childs.add(node);
		childsName.add(node.nodeName);
	}
	
	public ExcelTreeNode getChild(String nodeName) {
		for(ExcelTreeNode child:childs) {
			if(child.getNodeName().equals(nodeName)) {
				return child;
			}
		}
		return null;
	}
	
	public void addExtreInfo(String value) {
		extreInfo.add(value);
	}
	
	public void addExtreInfos(List<String> values) {
		extreInfo.addAll(values);
	}
	
	public List<ExcelTreeNode> getChildren() {
		return this.childs;
	}
	
	public List<String> getExtreInfo() {
		return this.extreInfo;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
}

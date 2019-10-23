package com.taomee.tms.mgr.entity;

import java.io.Serializable;

public class CommentByComponentId implements Serializable {
	
	@Override
	public String toString() {
		return "CommentByComponentId [commentId=" + commentId + ", keyword="
				+ keyword + ", comment=" + comment + ", displayOrder="
				+ displayOrder + "]";
	}
	private static final long serialVersionUID = 5006885992185901910L;
	private Integer commentId;
	private String keyword;
	private String comment;
	private Integer displayOrder;
	
	public Integer getCommentId() {
		return commentId;
	}
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	

}

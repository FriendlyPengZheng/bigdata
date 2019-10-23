package com.taomee.tms.mgr.dao;

import java.util.List;

import com.taomee.tms.mgr.entity.CommentByComponentId;

public interface CommentByComponentDao {
	List<CommentByComponentId> getCommentsByComponentId(Integer componentId);
}

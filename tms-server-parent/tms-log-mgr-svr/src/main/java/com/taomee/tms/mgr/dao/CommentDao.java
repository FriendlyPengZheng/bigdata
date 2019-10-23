package com.taomee.tms.mgr.dao;

import java.util.List;

import com.taomee.tms.mgr.entity.Comment;
import com.taomee.tms.mgr.entity.Metadata;

public interface CommentDao {
	Integer insertCommentInfo(Comment CommentInfo);
	void updateCommentInfo(Comment commentInfo); //其中commendId为0表示新增，否则为update
	List<Comment> getAllCommentInfos(); 
	
	void deleteCommentInfo(Comment CommentInfo);
	
	List<Comment> getCommentByKeyword(String keyword);
	List<Comment> getCommentByKeywordFuzzy(String keyword);
	Comment getCommentInfoBycommentId(Integer commentId);
}

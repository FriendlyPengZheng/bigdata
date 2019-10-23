package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.GameTaskInfo;

public interface GameTaskInfoDao {
	List<GameTaskInfo> getList(@Param("gameId")Integer gameId, @Param("type")String type);
	void setName(GameTaskInfo info);
	void setHide(GameTaskInfo info);
	void setHideAll(@Param("gameId")Integer gameId, @Param("type")String type, @Param("hide")Integer hide);
	
}

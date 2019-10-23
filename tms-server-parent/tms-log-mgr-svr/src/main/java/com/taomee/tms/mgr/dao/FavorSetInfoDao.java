package com.taomee.tms.mgr.dao;


import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.FavorSetInfo;

public interface FavorSetInfoDao {
	Integer insertSetInfo(FavorSetInfo favorSetInfo);	
	Integer getSetInfoByGameIdComponentId(@Param("gameId")Integer gameId, 
				@Param("componentId")Integer componentId);
	void updateSetInfo(FavorSetInfo favorSetInfo);
	//FavorSetInfo getFavorSetInfoBysetId(Integer setId);
}

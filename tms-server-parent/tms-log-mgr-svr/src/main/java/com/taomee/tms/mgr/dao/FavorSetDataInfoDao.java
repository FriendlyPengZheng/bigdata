package com.taomee.tms.mgr.dao;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.FavorSetDataInfo;

public interface FavorSetDataInfoDao {
	void insertFavorSetDataInfo(FavorSetDataInfo favorSetDataInfo);
	FavorSetDataInfo getFavorSetDataInfoByPrimaryKey(@Param("dataId")Integer dataId, @Param("id")Integer id, 
				@Param("dataExpr")String dataExpr, @Param("gameId")Integer gameId);
}

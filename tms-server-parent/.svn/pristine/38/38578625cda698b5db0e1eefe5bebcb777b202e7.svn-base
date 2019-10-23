package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.CollectInfo;
import com.taomee.tms.mgr.entity.SharedCollectInfo;

public interface CollectInfoDao {
	List<CollectInfo> getListByFavorIdByShared(Integer favord);
	
	List<CollectInfo> getListByFavorIdBySelf(@Param("favorId")Integer favorId, @Param("userId")Integer userId); //bCountMetadata = false
	List<CollectInfo> getListByFavorIdByCountMetadataSelf(@Param("favorId")Integer favorId, @Param("userId")Integer userId); //bCountMetadata = true

	CollectInfo getCollectById(Integer collectId);
	Integer deleteCollectByCollectId(Integer collectId);
	Integer insertCollectInfo(CollectInfo collectInfo);
	void updateCollectInfoBycollectId(@Param("collectId")Integer collectId, @Param("favorId")Integer favorId);
	
	void updateCollectNameByCollectId(@Param("collectId")Integer collectId, @Param("collectName")String collectName);
	
	
}

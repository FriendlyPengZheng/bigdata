package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.SharedCollectInfo;

public interface SharedCollectInfoDao {
	List<SharedCollectInfo> findByCollectId(Integer collectId);
	void deleteShareInfoByCollectId(Integer collectId);
	void deleteShareInfoByFavorId(Integer favorId);
	SharedCollectInfo getShareInfoByPrimaryId(@Param("favorId")Integer favorId, @Param("collectId")Integer collectId);
	Integer deleteShareInfoByPrimaryId(@Param("favorId")Integer favorId, @Param("collectId")Integer collectId);
	Integer insertShareCollectInfo(SharedCollectInfo sInfo);
	void deleteShareInfoByCollectUserId(@Param("userId")Integer userId, @Param("collectId")Integer collectId);
	void updateShareCollectInfoBycollectId(@Param("collectId")Integer collectId, @Param("srcFavorId")Integer srcFavorId, @Param("descFavorId")Integer descFavorId);
	List<SharedCollectInfo> getShareInfoByFavorId(@Param("favorId")Integer favorId, @Param("userId")Integer userId);
	
}

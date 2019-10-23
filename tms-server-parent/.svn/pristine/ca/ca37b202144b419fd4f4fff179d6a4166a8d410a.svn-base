package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.CollectMetadataInfo;

public interface CollectMetadataDao {
	List<CollectMetadataInfo> getMetadataInfosByCollectId(Integer collectId);
	Integer insertCollectMetadataInfo(CollectMetadataInfo collectMetadataInfo);
	CollectMetadataInfo getCollectMetadataInfoByPrimaryKey(@Param("dataId")Integer dataId, @Param("gpzsId")Integer gpzsId,
				@Param("collectId")Integer collectId, @Param("dataExpr")String dataExpr);

	Integer deleteMetadataByCollectId(Integer collectId);
	void updateCollectMetadataOrderByPrimaryKey(@Param("dataId")Integer dataId, @Param("gpzsId")Integer gpzsId,
			@Param("collectId")Integer collectId, @Param("dataExpr")String dataExpr, @Param("displayOrder")Integer displayOrder);
}

package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.ArtifactInfo;

public interface ArtifactInfoDao {
	Integer insertArtifactInfo(ArtifactInfo artifactInfo);
	ArtifactInfo getArtifactInfoBytaskId(Integer taskId);
	ArtifactInfo getArtifactInfoByartifact(Integer artifactId);
	void deleteArtifactInfo(Integer taskId);
	void updateArtifactInfo(ArtifactInfo artifactInfo);
	ArtifactInfo getStatItemByArtifactId(Integer artifactId);
	
	List<ArtifactInfo> getArtifactInfoContentList(Integer nodeId, Integer gameId);
	void updateStatItemByNodeId(@Param("nodeId")Integer oldNodeId, @Param("newNodeId")Integer newNodeId);
}

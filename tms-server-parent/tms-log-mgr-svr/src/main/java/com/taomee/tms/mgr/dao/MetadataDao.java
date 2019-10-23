package com.taomee.tms.mgr.dao;

import java.util.List;

import com.taomee.tms.mgr.entity.Metadata;

public interface MetadataDao {
	Integer insertMetadataInfo(Metadata metadataInfo);
	void updateMetadataInfo(Metadata metadataInfo);
	List<Metadata> getAllMetadataInfos();
	void deleteMetadataInfo(Metadata metadataInfo);
	List<Metadata> getMetadataInfoBycommentId(Integer commentId);
	
	/*List<Metadata> getListByCollectId(Integer collectId);*/
}

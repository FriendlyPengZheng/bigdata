package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.ServerNameInfo;


public interface ServerNameInfoDao {
	void insertServerNameInfo(Integer serverId, Integer gameId, String pName, String zsName);
	List<ServerNameInfo> getServerNameInfosByGameId(Integer gameId);
	void updateServerPNameByServerId(@Param("serverId")Integer serverId, @Param("pname")String pname);
	void updateServerZSNameByServerId(@Param("serverId")Integer serverId, @Param("zsname")String zsname);
	void updateServerNameStatusByServerId(@Param("serverId")Integer serverId, @Param("hide")Integer hide);
}

package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.PlatformInfo;
import com.taomee.tms.mgr.entity.ServerAllPlatform;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;
import com.taomee.tms.mgr.entity.ZSIdInfos;

public interface ServerGPZSInfoDao {
	List<PlatformInfo> getPlatFormInfosByGameId(Integer gameId);
	List<ZSIdInfos> getZSInfosByGPId(Integer gameId, Integer platformId);
	Integer getServerIDByGPZS(Integer gameId, Integer platformId,Integer zoneId, Integer sId);
	void insertServerGPZSInfo(Integer gameId, Integer platformId, Integer zoneId, Integer sId);
	List<ServerGPZSInfo> getAllServerGpzsInfos();
	List<ServerGPZSInfo> getAllServerGpzsInfosByStatus(Integer status);
	ServerGPZSInfo getServerInfoByTopgameId(Integer gameId);
	void updateServerGPZSStatusByServerId(@Param("serverId")Integer serverId, @Param("hide")Integer hide);
	List<ServerAllPlatform> getAllPlatByGameId(Integer gameId);
}

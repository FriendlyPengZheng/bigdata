package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.MissionInfo;

public interface MissionInfoDao {
	List<MissionInfo> getMissionList(@Param("serverId")Integer serverId,@Param("start")Integer start, @Param("end")Integer end,@Param("type")String type);
	List<MissionInfo> getMissionDetial(@Param("serverId")Integer serverId,@Param("start")Integer start, @Param("end")Integer end,@Param("sstid")String sstid);
}

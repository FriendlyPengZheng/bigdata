package com.taomee.tms.mgr.dao;

import java.util.List;

import com.taomee.tms.mgr.entity.ResultInfo;

public interface ResultInfoDao {
	void insertUpdataResultInfo(ResultInfo resultInfo);
	List<ResultInfo> getDayValueInfo(Integer dataId, Integer serverId, long starttime, long endtime);
	List<ResultInfo> getMinuteValueInfo(Integer dataId, Integer serverId, long starttime, long endtime);

}

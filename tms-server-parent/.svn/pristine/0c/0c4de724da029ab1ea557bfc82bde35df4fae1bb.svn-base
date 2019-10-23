package com.taomee.tms.mgr.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.StidSStidRefLog;

public interface StidSStidRefLogDao {
	void insertStidSStidRefLog(StidSStidRefLog stidSStidRefLog);
	void updateStidSStidRefLog(StidSStidRefLog stidSStidRefLog);
	StidSStidRefLog getLogIdBySStidGameId(String stid, String sstid, Integer gameId, String op);
	List<StidSStidRefLog> getStidSStidRefBylogId(Integer logId);
	List<StidSStidRefLog> getStidSStidRefLogBystatus(Integer status);
}

package com.taomee.tms.mgr.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.LogInfo;

/*注意：使用到事务时方法名前缀是有规定的，参考spring-mybatis.xml的transactionAdvice
 * */

public interface LogInfoDao {
	Integer insertLogInfo(LogInfo loginfo); //throws DataAccessException;
	void updateLogInfoByLogName(LogInfo loginfo);
	Integer updateLogInfoByStatus(LogInfo loginfo);
	void deleteLogInfo(Integer logId);
	LogInfo getLogInfoByLogId(Integer logId);
	List<LogInfo> getLogInfos();
	List<LogInfo> getLogInfosByStatus(Integer status);
	LogInfo getLogInfoBylogName(String logName);
}

package com.taomee.tms.mgr.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.TaskInfo;

/*注意：使用到事务时方法名前缀是有规定的，参考spring-mybatis.xml的transactionAdvice
 * */

public interface TaskInfoDao {
	Integer insertTaskInfo(TaskInfo taskInfo);
	void updateTaskInfo(TaskInfo taskInfo) ;
	void deleteTaskInfo(Integer taskId) ;
	TaskInfo getTaskInfo(Integer taskId);
	List<TaskInfo> getTaskInfos();
}

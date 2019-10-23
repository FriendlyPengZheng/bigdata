package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.EmailConfigInfo;

public interface EmailConfigDao {
	//返回数据库所有信息
	List<EmailConfigInfo> queryAll();
	//获取info
	EmailConfigInfo queryByEmailId(Integer emailId);
	//插入数据
	Integer insert(EmailConfigInfo info);
	//删除数据
	Integer deleteByEmailId(Integer emailId);
	//更新数据
	Integer update(EmailConfigInfo info);
	//设置status
	Integer setStatus(@Param("emailId")Integer emailId,@Param("status")Integer status);
}

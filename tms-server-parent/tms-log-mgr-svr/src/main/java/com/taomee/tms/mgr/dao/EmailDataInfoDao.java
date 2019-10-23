package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taomee.tms.mgr.entity.DataInfo;
import com.taomee.tms.mgr.entity.EmailDataInfo;

public interface EmailDataInfoDao {
	List<EmailDataInfo> queryByEmailId(Integer emailId);
	Integer deleteByEmailDataId(Integer emailDataId);
	Integer insert(EmailDataInfo info);
	Integer setRemoveByEmailId(Integer emailId);
	Integer setRemoveByEmailDataId(Integer emailDataId);
	Integer setDataName(@Param("emailDataId")Integer emailDataId, @Param("dataName")String dataName);
	List<EmailDataInfo> getbyContentId(@Param("emailId")Integer emailId, @Param("emailContentId")Integer emailContentId);
}
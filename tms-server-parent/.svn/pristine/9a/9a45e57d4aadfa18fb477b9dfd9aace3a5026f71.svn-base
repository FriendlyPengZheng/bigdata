package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.DataInfo;
import com.taomee.tms.mgr.entity.DataInfoQueryParams;
import com.taomee.tms.mgr.entity.DataInfoUniqueKeyParams;

/*注意：使用到事务时方法名前缀是有规定的，参考spring-mybatis.xml的transactionAdvice
 * */

public interface DataInfoDao {
	Integer insertDataInfo(DataInfo dataInfo);
	DataInfo getDataInfo(Integer dataId);
	List<DataInfo> getDataInfosByParams(DataInfoQueryParams params);
	void insertUpdataInfo(DataInfo dataInfo);
	DataInfo findDataInfo(Integer schemaId, Integer type, String dataName);
	List<DataInfo> getChildDataInfoByparentId(Integer parentId);

	void deleteDataInfo(@Param("relateId") Integer relateId,
			@Param("type") Integer type, @Param("dataName") String dataName);
	DataInfo getDataInfoByUniqueKey(DataInfoUniqueKeyParams params);
}

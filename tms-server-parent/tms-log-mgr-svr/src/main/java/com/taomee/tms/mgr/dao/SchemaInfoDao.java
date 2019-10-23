package com.taomee.tms.mgr.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.SchemaInfo;
import org.springframework.web.bind.annotation.RequestParam;

/*注意：使用到事务时方法名前缀是有规定的，参考spring-mybatis.xml的transactionAdvice
 * */

public interface SchemaInfoDao {
	Integer insertSchemaInfo(SchemaInfo schemaInfo);
    List<SchemaInfo> getSchemaInfosByLogType(Integer type);
	void updateSchemaInfoByStatus(SchemaInfo schemaInfo);
	void updateSchemaInfoBySchemaName(SchemaInfo schemaInfo);
	void updateSchemaFlagBySchemaId(Integer schemaId);  //更改schema_id为0
	void deleteSchemaInfo(Integer schemaId);
	SchemaInfo getSchemaInfoByschemaId(Integer schemaId);
	List<SchemaInfo> getSchemaInfos();
	List<SchemaInfo> getSchemaInfosByStatus(Integer status);
	List<SchemaInfo> getSchemaInfosByLogId(Integer logId);
	void insertSchemaInfoByschemaId(SchemaInfo schemaInfo);
	
	List<SchemaInfo> getSchemaInfoContentList(Integer nodeId, Integer gameId);
	void updateStatItemByNodeId(@Param("nodeId")Integer oldNodeId,
								@Param("newNodeId")Integer newNodeId);
	void updateSchemaNodeInfo(SchemaInfo schemaInfo); // 修改node_id
	List<SchemaInfo> getTrashInfos(Integer gameId);
	
	//List<SchemaInfo> getSchemaInfosByschemaStatus(Integer status);
	int getSchemaInfoByParams(SchemaInfo schemaInfo);

}

package com.taomee.tms.mgr.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.SchemaDiaryInfo;

/*注意：使用到事务时方法名前缀是有规定的，参考spring-mybatis.xml的transactionAdvice
 * */

public interface SchemaDiaryInfoDao {
    Integer insertSchemaDiaryInfo(SchemaDiaryInfo schemaDiaryInfo);
    List<SchemaDiaryInfo> getSchemaDiaryInfoBydiaryId(Integer diaryId);
    SchemaDiaryInfo getSchemaDiaryInfoBylastId();
}

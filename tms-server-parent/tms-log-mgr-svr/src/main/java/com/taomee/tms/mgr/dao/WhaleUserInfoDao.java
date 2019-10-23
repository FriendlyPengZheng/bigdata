package com.taomee.tms.mgr.dao;

import com.taomee.tms.mgr.entity.WhaleUserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WhaleUserInfoDao {
    List<WhaleUserInfo> getListWhaleUserInfos(@Param("gameId")Integer gameId,
                                              @Param("platFormId") Integer platFormId,
                                              @Param("accountId") String accountId);

}

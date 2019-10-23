package com.taomee.tms.mgr.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.taomee.tms.mgr.entity.ServerDiaryInfo;
import com.taomee.tms.mgr.entity.ServerInfo;

/*注意：使用到事务时方法名前缀是有规定的，参考spring-mybatis.xml的transactionAdvice
 * */

public interface ServerInfoDao {
    Integer insertServerInfo(ServerInfo serverInfo);
    Integer updateServerInfo(ServerInfo serverInfo);
    Integer updateServerInfoByStatus(ServerInfo serverInfo);
	Integer deleteServerInfo(Integer serverId);
    ServerInfo getServerInfo(Integer serverId);
    List<ServerInfo> getAllServerInfosByStatus(Integer status);
    List<ServerInfo> getAllServerInfos(); 
    List<ServerInfo> getServerInfoByparentId(Integer parentId);
    List<ServerInfo> getServerInfoByGameId(Integer gameId);
    ServerInfo getServerInfoByTopgameId(Integer gameId);
}

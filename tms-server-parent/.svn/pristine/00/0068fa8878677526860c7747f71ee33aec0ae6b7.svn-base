package com.taomee.tms.mgr.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.taomee.tms.mgr.entity.FavorInfo;

public interface FavorInfoDao {
	Integer insertFavorInfo(FavorInfo favorInfo);
	FavorInfo getFavorByfavorId(Integer favorId);
	//FavorInfo getFavorBycollectId(Integer collectId);
	List<FavorInfo> getListByUserId(@Param("userId")Integer userId, @Param("favorType")Integer favorType, 
				@Param("favorName")String favorName);
	
	
	Integer deleteFavorByfavorId(Integer favorId);
	Integer updateDefaultFavorByfavorId(@Param("favorId")Integer favorId, @Param("isDefault")Integer isDefault);
	Integer updateFavorByFavorNameLayOut(@Param("favorId") Integer favorId, @Param("favorName") String favorname, 
				@Param("layout") Integer layout);
	
	Integer updateFavorDefault(@Param("favorId")Integer favorId, @Param("userId")Integer userId, @Param("isDefault")Integer isDefault);
}

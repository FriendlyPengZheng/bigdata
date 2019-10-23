package com.taomee.tms.mgr.dao;

import java.util.List;

import com.taomee.tms.mgr.entity.WhaleUserMonthInfo;
import org.apache.ibatis.annotations.Param;


public interface WhaleUserMonthDao {
	List<WhaleUserMonthInfo> getMultiPlatFormWhaleInfos(@Param("gameId") Integer gameId,
														@Param("time") Integer time,
														@Param("topNum") Integer topNum);

	List<WhaleUserMonthInfo> getPlatFormWhaleInfosByPlatFormId(@Param("gameId") Integer gameId,
																@Param("time") Integer time,
																@Param("platFormId") Integer platFormId,
																@Param("topNum") Integer topNum);


}

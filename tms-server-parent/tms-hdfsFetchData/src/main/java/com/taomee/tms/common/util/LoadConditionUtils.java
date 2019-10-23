package com.taomee.tms.common.util;

import java.util.HashMap;

import com.taomee.tms.client.simulatedubboserver.HdfsLoadPathConditions;

/**
 * 
 * @author looper
 * @date 2017年5月18日 上午11:27:10
 * @project tms-hdfsFetchData SetExpressionAnalyzerEx
 * 
 *          入库文件格式/user/hive/warehouse/tms.db/t_artifact_info的hive_table_name/
 *          gameid=25/计算日期+period/000000_0
 */
public class LoadConditionUtils {
	
	private static HashMap<Integer, String> peroidReflectZeroOffset = new HashMap<>() ;
	
	static
	{
		peroidReflectZeroOffset.put(0, "d0"); //天
		peroidReflectZeroOffset.put(1, "w0"); //周
		peroidReflectZeroOffset.put(2, "m0"); //月
		peroidReflectZeroOffset.put(3, "v0"); //版本周
	}

	/**
	 * 根据hdfs入库条件拼接入库的hdfspath
	 * @param gameId
	 * @return
	 */
	public String loadPath(HdfsLoadPathConditions conditions) {
		StringBuilder builder = new StringBuilder();

		String hiveHomeBase = (conditions.getHiveHomeBase() == null) ? "/user/hive/warehouse/"
				: conditions.getHiveHomeBase();
		
		String dbName = (conditions.getDbName() == null) ? "tms.db/"
				: conditions.getDbName() + ".db/";
		
		String gameId = (conditions.getGameId() == null) ? "gameid=*/"
				: "gameid=" + conditions.getGameId() + "/";
		
		return builder.append(hiveHomeBase).append(dbName)
				.append(conditions.getTableName() + "/").append(gameId)
				.append("date=" + conditions.getFileDate()).append("/*")
				.toString();
	}
	
	/**
	 * period映射offset，默认这些offset的Num都为0
	 * @param period
	 * @return
	 * 0---日
 	   1---周
	   2---月
	   3---版本周
	 */
	public static String periodReflectHdfsFileDate(Integer period)
	{
		if(period <0 || period >3)
		{
			throw new RuntimeException("period 参数数值非法,数字大小范围0~3!");
		}
		else
		{
			return peroidReflectZeroOffset.get(period);
		}
	}

}

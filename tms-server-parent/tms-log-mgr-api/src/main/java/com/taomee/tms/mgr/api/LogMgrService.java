package com.taomee.tms.mgr.api;

import java.util.List;

import com.taomee.tms.mgr.entity.*;

public interface LogMgrService {
	/**
	 * 调用hbase接口，从hbase中拉取所选时间段数据
	 * @param dataId
	 * @param serverId
	 * @param startTime
	 * @param endTime
	 * @param timeDimension 0:日 周 1 月 2   版本周3  分钟4  小时5
	 * @return
	 */
	List<ResultInfo> getValueInfo(Integer dataId, Integer serverId,
			String startTime, String endTime, Integer timeDimension);

	/**
	 * 拉取实时分钟数据(此接口主要用于拉取实时在线分钟数据)
	 * @param dataId
	 * @param gameId
	 * @param serverId
	 * @param dataTime
	 * @return
	 */
	List<RealTimeDataInfo> getDateValueMin(Integer dataId, Integer gameId, Integer serverId, String dataTime);

	/**
	 * 拉取实时小时数据(此接口主要用于拉取实时在线小时数据)
	 * @param dataId
	 * @param gameId
	 * @param serverId
	 * @param dataTime
	 * @return
	 */
	List<RealTimeDataInfo> getDateValueHour(Integer dataId, Integer gameId, Integer serverId, String dataTime);

	/**
	 * 分布计算拉取dataInfo信息以及对应的value
	 * @param relateId
	 * @param serverId
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @param timeDimension 0:日  周 1  月 2  版本周 3  分钟4 小时5
	 * @return
	 */
	List<DistrDataInfo> getDistrDataInfos(Integer relateId, Integer serverId, String startTime,
										  String endTime, Integer type, Integer timeDimension);

	/**
	 * 平台区服分布拉取数据
	 * @param relateId
	 * @param serverId 传的是上一级的serverId
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 */
	List<DistrDataInfo> getGPZSDistrInfos(Integer relateId, Integer serverId,
										  String startTime, String endTime, Integer type);

	/**
	 * t_log_info表的增删改查
	 */
	Integer insertLogInfo(LogInfo loginfo);
	Integer updateLogInfo(LogInfo loginfo); //修改logName
	Integer updateLogInfoByStatus(LogInfo loginfo); //修改状态【在使用 弃用】
	LogInfo getLogInfoByLogId(Integer logId);
	List<LogInfo> getLogInfos();     //可用和不可用进行获取
	List<LogInfo> getLogInfos(Integer status);

	/**
	 * t_schema_info表的增删改查
	 */
	Integer insertSchemaInfo(SchemaInfo schemaInfo);
	Integer updateSchemaInfo(SchemaInfo schemaInfo);  //仅修改material_name
	Integer updateSchemaInfoByStatus(SchemaInfo schemaInfo); //修改状态【是否可用 0：可用  1：不可用】
	SchemaInfo getSchemaInfoByschemaId(Integer schemaId);
	List<SchemaInfo> getSchemaInfos();  
	List<SchemaInfo> getSchemaInfosFromRedis();
	List<SchemaInfo> getSchemaInfosFromRedis(String pattern);
	List<SchemaInfo> getSchemaInfos(Integer status);
	List<SchemaInfo> getSchemaInfosByLogId(Integer logId);
	List<SchemaInfo> getSchemaInfosByLogIdForStorm(Integer logId,Boolean writeToRedis,Integer redisExpireTime);
	List<SchemaInfo> getRefreshedSchemaInfosByLogIdForStorm(Integer logId,Boolean writeToRedis,Integer redisExpireTime);
	// 供hadoop打散脚本使用
	List<SchemaInfo> getSchemaInfosByLogType(Integer type);

	/**
	 * t_data_info表的增删改查
	 */
	void deleteDataInfo(DeleteDataInfoParams deleteDataInfoParams);
	Integer insertDataInfo(DataInfo dataInfo);
	DataInfo getDataInfo(Integer dataId);
	DataInfo getDataInfoByUniqueKey(DataInfoUniqueKeyParams params);
	List<DataInfo> getDataInfosByParams(DataInfoQueryParams params);
	// 通过schema_id和级联字段增加一个dataInfo
	Integer insertUpdateDataInfoByschemaId(Integer schemaId, String cascadeFields);

	/**
	 * 插入t_data_info以及结果数据入库hbase
	 */
	void insertUpdateDataResultInfo(DataToObject datatoObject);

	// t_server_info
	/**
	 * t_server_info表的增删改查 [Deprecated]
	 * 代替该接口涉及的表：t_server_gpzs_info和t_serverName_info
	 */
	Integer insertServerInfo(ServerInfo serverInfo);
	Integer updateServerInfo(ServerInfo serverInfo);
	Integer updateServerInfoByStatus(ServerInfo serverInfo);
	ServerInfo getServerInfo(Integer serverId);
	List<ServerInfo> getAllServerInfos(Integer status);  //status 0为正常显示的游戏
	List<ServerInfo> getAllServerInfos();
	List<ServerInfo> getServerInfoByparentId(Integer parentId);
	ServerGPZSInfo getServerInfoByTopgameId(Integer gameId); //获取全区全服信息
	//ServerDiaryInfo
	List<ServerDiaryInfo> getServerDiaryInfoBydiaryId(Integer diaryId);

	/**
	 * 加工运算使用
	 * t_task_info的增删改查
	 */
	Integer insertTaskInfo(TaskInfo taskInfo);
	Integer updateTaskInfo(TaskInfo taskInfo);
	Integer deleteTaskInfo(Integer taskId);
	TaskInfo getTaskInfo(Integer taskId);
	List<TaskInfo> getTaskInfos();

	/**
	 *
	 * t_artifact_info的增删改查
	 */
	void insertArtifactInfo(ArtifactInfo artifactInfo);
	ArtifactInfo getArtifactInfoBytaskId(Integer taskId);
	ArtifactInfo getArtifactInfoByartifact(Integer artifactId);
	//入库程序特定的taskId获取对应的ArtifactInfo信息
	ArtifactInfo getArtifactInfoToLoadBytaskId(Integer taskId);
    ArtifactInfo getArtifactInfoByHiveTableName(String tableName);

	/**
	 * 注释管理功能接口
	 * t_web_comment的增删改查
	 */
	Integer insertCommentInfo(Comment commentInfo);
	void updateCommentInfo(Comment commentInfo); // 其中commendId为0表示新增，否则为update
	List<Comment> getAllCommentInfos();
	void deleteCommentInfo(Comment CommentInfo);
	List<Comment> getCommentByKeyword(String keyword); // 精确匹配keyword
	List<Comment> getCommentByKeywordFuzzy(String keyword); // 模糊匹配keyword
	Comment getCommentInfoBycommentId(Integer commentId);

	/**
	 * 元数据管理功能接口
	 * t_web_metadata的增删改查
	 */
	Integer insertMetadataInfo(Metadata metadataInfo);
	void updateMetadataInfo(Metadata metadataInfo);
	List<Metadata> getAllMetadataInfos();
	void deleteMetadataInfo(Metadata metadataInfo);
	List<Metadata> getMetadataInfoBycommentId(Integer commentId);

	/**
	 * t_web_component的增删改查
	 */
	List<Component> getComponentInfoByComponentId(Integer componentId);
	List<Component> getComponents(String moduleKey, Integer parentId, Integer gameId);
	Integer insertComponentInfo(Component componentInfo);
	void updateComponentInfo(Component componentInfo);
	void deleteComponentInfos(Integer componentId);
	/**
	 * 查找t_web_componet中所有ignored不等于0的记录
	 */
	List<Component> getAllComponentsByIgnored();

	/**
	 * 通过gameid获取平台信息
	 * game_id -> PlatformInfo(platform_id, platform_name)
	 */
	List<PlatformInfo> getPlatFormInfosByGameId(Integer gameId);
	
	/**
	 * 通过game_id和pid获取区服信息
	 * game_id+platform_id -> zsInfo(zoneServerId, zoneServerName)
	 * zoneServerId用_分隔，例如1区全服(1_-1)
	 */
	List<ZSInfo> getZSInfosByGPId(Integer gameId, Integer platformId);

	/**
	 * 通过gid+pid+zid+sid获取唯一对应的serverId
	 */
	Integer getServerIDByGPZS(Integer gameId, Integer platformId ,Integer zoneId ,Integer sId);

	/**
	 *
	 */
	Integer getServerIDByGPZSForStorm(Integer gameId, Integer platformId ,
									  Integer zoneId ,Integer sId,Integer expireSeconds);
	
	/**
	 * t_server_gpzs_info和t_serverName_info表的插入
	 */
	Integer insertServerInfosByGPZS(Integer gameId, Integer platformId, Integer zoneId,
									Integer sId, String pName, String zsName);
	
	/**
	 * 获取t_server_gpzs_info表中所有记录
	 */
	List<ServerGPZSInfo> getAllServerGpzsInfos();

	/**
	 *
	 */
	List<ServerGPZSInfo> getServerGpzsInfosFromRedis(String pattern);
	
	/**
	 * 获取【使用中/废弃状态】的gpzs_info信息
	 * status: 0-使用中 1-废弃
	 */
	List<ServerGPZSInfo> getAllServerGpzsInfos(Integer status);

	/**
	 * 模板管理页面
	 * t_web_navi
	 */
	List<Navi> getNaviInfosByLevel();

	
	//获取所有使用中的游戏信息(gameId和gameName)
	List<GameInfoPage> getGameInfoPage();
	
	// t_web_component_comment 关联表
	List<CommentByComponentId> getCommentsByComponentId(Integer componentId);
	void saveComponentComments(Integer componentId, Integer commentId);

	// SchemaDiaryInfo日志信息表变更记录：实时更新redis的schemaInfo信息
	List<SchemaDiaryInfo> getSchemaDiaryInfoBydiaryId(Integer diaryId);
	//获取SchemaDiaryInfo日志信息表的最大的日志id
		
	//通过父的data_id获取子dataInfo信息(级联显示)
	List<DataInfo> getChildDataInfoByparentId(Integer dataId);

	/**
	 * 游戏管理页面
	 * t_game_info的增删改查
	 */
	List<GameInfo> getGameInfos();
	GameInfo getGameInfoById(Integer gameId);
	Integer insertGameInfo(GameInfo gameInfo);
	Integer updateGameInfo(GameInfo gameInfo);
	Integer updateStatus(GameInfo game);

	/*********************游戏数据自定义相关接口************************************************************/
	//从数据库中获取树结构(只包含两级)
	List<TreeInfo> getTreeInfos(Integer gameId);  
	List<TreeInfo> getTreeInfosByGameIdParentId(Integer gameId, Integer parentId);
	//重命名【设置node和item名称】 
	void updateNodeName(TreeInfo treeInfo);  //必须参数：gameId nodeName nodeId
	void updateItemName(SchemaInfo schemaInfo); //必须参数：schemaId schemaName
	//新增节点
	TreeInfo addTreeNode(TreeInfo treeInfo);
	//删除节点
	void delTreeNode(List<Integer> nodeId, Integer gameId);
	//合并非叶子节点
	TreeInfo mergeTreeNode(Integer nodeId, Integer gameId);  //传进来的参数是node_id是左树的顶层的node_id即可合并它的子节点
	//移动节点
	void moveTreeNode(Integer gameId, Integer nodeId, Integer parentId, Integer afterId);
	//绑定统计项到树节点
	void moveStatItem(Integer gameId, List<Integer> itemSchemaIdList, Integer moveToNodeId);
	//搜索节点
	List<TreeInfo> searchNode(Integer gameId, String nodeName);
	//根据node_id获取内容列表，只获取显示的项(status=0)
	List<SchemaInfo> getSchemaInfoContentList(Integer nodeId, Integer gameId);
	List<SchemaInfo> getSchemaInfoContentListItem(Integer nodeId, Integer gameId);   //注意区分游戏拉取数据
	List<ArtifactInfo> getArtifactInfoContentList(Integer nodeId, Integer gameId);
	//回收站t_schema_info表中加一个字段标记flag判断 是否加入了回收站   flag=0正常 flag=1加入回收站中的item
	//List<SchemaInfo> getTrashInfos(Integer nodeId, Integer gameId);   //回收站的node_id=-1 
	
	Integer insertCustomLogInfo(CustomQueryParams customQueryParams);
	Integer getCustomLogInfo(CustomQueryParams customQueryParams,Boolean writeToRedis,Integer redisExpireTime);
	Integer getCustomLogInfo(String stid,String sstid,Integer gameID,String op,Boolean writeToRedis, Integer redisExpireSeconds);
	List<StidSStidRefLogDiary> getStidSStidRefLogDiaryBydiaryId(Integer diaryId);
	List<StidSStidRefLog> getStidSStidRefLogBystatus(Integer status);
	List<StidSStidRefLog> getStidSStidRefLogFromRedis(String pattern);
	/*********************游戏数据自定义相关接口************************************************************/

	/*********************核心数据[邮件]相关接口************************************************************/
	//t_web_email_config
	List<EmailConfigInfo> getEmailConfigAll();
	EmailConfigInfo getEmailConfigByEmailId(Integer emailId);
	Integer insertEmailConfigInfo(EmailConfigInfo info);
	Integer deleteEmailConfigByEmailId(Integer emailId);
	Integer updateEmailConfigInfo(EmailConfigInfo info);
	Integer seEmailConfigtStatus(Integer emailId, Integer status);
	//t_web_email_data
	List<EmailDataInfo> getEmailInfoByEmailId(Integer emailId);
	List<EmailDataInfo> getEmailInfoByContentId(Integer emailId, Integer emailContentId);
	Integer insertEmailDataInfo(EmailDataInfo info);
	
	//t_web_email_template
	Integer getEmailTemplateId(String templateType);
	
	//t_web_email_template_content
	List<EmailTemplateContentInfo> getEmailTemplateContentInfo(Integer emailTemplateId);
	EmailTemplateContentInfo getEmailTemlateContentInfoById(Integer emailTemplateContentId);
	
	//t_web_email_template_data
	List<EmailTemplateDataInfo>  getEmailTemplateData(Integer emailTemplateContentId);
	
	//v_gametask_data
	List<MissionInfo> getMissionList(Integer serverId,Integer start, Integer end,String type);
	List<MissionInfo> getMissionDetialInfo(Integer serverId,Integer start, Integer end,String sstid);
	
	//t_gametask_info
	List<GameTaskInfo> getGameTaskInfoList(Integer gameId, String type);
	void updateGameTaskName(GameTaskInfo info);
	void updateGameTaskHide(GameTaskInfo info);
	void updateGameTaskHideAll(Integer gameId, String type, Integer hide);
	/*********************核心数据[邮件]相关接口************************************************************/
	
	/*********************我的收藏相关接口************************************************************/
	List<CollectInfo> getListByFavorId(Integer favorId, Integer userId, boolean bCountMetadata);
	List<CollectInfo> getListByFavorId(Integer favorId);
	List<SharedCollectInfo> findByCollectId(Integer collectId);
	CollectInfo getCollectById(Integer collectId);

	//通过collectId在t_web_collect_metadata表中拉出所需的数据
	List<CollectMetadataInfo> getMetadataInfosByCollectId(Integer collectId);
	
	//根据user_id在t_web_favor中查找对应的记录  favor_type 1/2 favorType=null则可以拉出该用户下的所有收藏
	List<FavorInfo> getListByUserId(Integer userId, Integer favorType, String favorName);
	//增加默认我的收藏
	Integer addDefaultFavor(FavorInfo favorInfo);
	Integer insertSetInfo(FavorSetInfo favorSetInfo);	
	void insertFavorSetDataInfo(FavorSetDataInfo favorSetDataInfo);
	Integer insertFavorInfo(FavorInfo favorInfo);
	boolean updateFavorInfoByFavorId(FavorInfo favorInfo);
	//todo以下两个接口需要在收藏环节同一个事务中 
	Integer insertCollectInfo(CollectInfo collectInfo);
	//通过set_id+data_id/set_id+data_erpr返回对应的记录
	FavorSetDataInfo getFavorSetDataInfoByPrimaryKey(Integer dataId, Integer id,
													 String dataExpr, Integer gameId);

	Integer insertCollectMetadataInfo(CollectMetadataInfo collectMetadataInfo);
	//PRIMARY KEY (`data_id`,`gpzs_id`,`collect_id`,`data_expr`)
	CollectMetadataInfo getCollectMetadataInfoByPrimaryKey(Integer dataId, Integer gpzsId,
														   Integer collectId, String dataExpr);

	Integer updateFavor(Integer favorId, String favorName, Integer layOut);
	Integer deleteFavor(Integer favorId, Integer userId);
	Integer updateFavorDefault(Integer favorId, Integer userId, Integer isDefault);
	Integer updateDefaultFavorByfavorId(Integer favorId);
	
	Integer deleteCollectByCollectId(Integer collectId);
	Integer deleteCollectMetadataInfo(Integer collectId);
	void deleteShareInfoByCollectUserId(Integer collectId, Integer userId);
	
	FavorInfo getFavorByfavorId(Integer favorId);
	SharedCollectInfo getShareInfoByPrimaryId(Integer favorId, Integer collectId);
	Integer deleteShareInfoByPrimaryId(Integer favorId, Integer collectId);
	Integer insertShareCollectInfo(SharedCollectInfo sInfo);
	void updateCollectInfoBycollectId(Integer collectId, Integer favorId);
	void updateShareCollectInfoBycollectId(Integer collectId, Integer srcFavorId, Integer descFavorId);
	
	List<SharedCollectInfo> getShareInfoByFavorId(Integer favorId, Integer userId);
	void updateCollectNameByCollectId(Integer collectId, String collectName);
	void updateCollectMetadataOrderByPrimaryKey(Integer dataId, Integer gpzsId, Integer collectId,
												String dataExpr, Integer displayOrder);
	/*********************我的收藏相关接口************************************************************/

	/*********************平台区服/渠道区服分析相关接口************************************************************/
	List<ServerNameInfo> getServerNameInfosByGameId(Integer gameId);
	void updateServerPNameByServerId(Integer serverId, String pname);
	void updateServerZSNameByServerId(Integer serverId, String zsname);
	void updateServerNameStatusByServerId(Integer serverId, Integer hide);
	List<ServerAllPlatform> getAllPlatByGameId(Integer gameId);
	/*********************平台区服/渠道区服分析相关接口************************************************************/

	/*********************鲸鱼用户相关接口************************************************************/
	/**
	 * 鲸鱼用户拉取的数据涉及的视图：v_whale_user_month_new  v_whale_user_new
 	 * v_whale_user_new：由t_whale_user和v_gpzs_pname(t_server_gpzs_info和t_serverName_info创建的视图)创建
	 * v_whale_user_month_new：由t_whale_user_month和v_whale_user_new创建的视图
	 */
	List<WhaleUserInfo> getListWhaleUserInfos(Integer gameId, Integer platFormId, String accountId);
	List<WhaleUserMonthInfo> getListWhaleUserMonthInfos(Integer gameId, Integer time, Integer platFomrId, Integer topNum);
	/*********************鲸鱼用户相关接口************************************************************/


	//friendly
		//v_item_sale_data
		List<EconomyInfo> getItemSaleTop10(String sstid,Integer vip,String timeFrom,String timeTo,Integer server_id);
		List<EconomyInfo> getItemSaleList(String sstid,Integer vip,String timeFrom,String timeTo,Integer server_id,Integer start,Integer end);
		List<EconomyInfo> getItemSaleDetail(String sstid,Integer vip,String timeFrom,String timeTo,Integer server_id,String item_id);
		Integer getItemSaleListTotal(String sstid,Integer vip,String timeFrom,String timeTo,Integer server_id);
		//void dropTemTableByName(String tableName);
		//void createTemporaryTable(Integer parent_id);
		List<CategoryInfo> getCategorySaleList(Integer server_id,String sstid,String timeFrom,String timeTo,Integer start,Integer end,String tableName,Integer parent_id);
		Integer getCategorySaleListTotal(Integer server_id,String sstid,String timeFrom,String timeTo,String tableName,Integer parent_id);
		List<CategoryInfo> getCategorySaleDetail(Integer category_id,String sstid,Integer server_id,String timeFrom,String timeTo);
		List<ItemInfo> getItemListIfGameIdIs2(String sstid ,Integer game_id,Integer is_leaf,Integer start,Integer end);
		List<ItemInfo> getItemList(String sstid ,Integer game_id,Integer is_leaf,Integer start,Integer end);
		Integer getItemListTotalIfGameIdIs2(String sstid ,Integer game_id,Integer is_leaf);
		Integer getItemListTotal(String sstid ,Integer game_id,Integer is_leaf);
		Integer setCategoryName(String sstid ,Integer game_id, String item_id,String item_name,Integer hide);
		Integer setHide(String sstid ,Integer game_id, String item_id,String item_name,Integer hide);
		List<ItemCategoryInfo> getItemCategory(String item_id,String sstid ,Integer game_id,Integer is_leaf);
		List<ItemCategoryInfo> getCategoryList(Integer parent_id,String sstid ,Integer game_id);
		Integer setItemCategory(Integer category_id,String sstid,Integer game_id,String item_id,Integer ref_count);
		void deleteItemCategory(String item_id,String sstid,Integer game_id);
		void updateItemCategory(Integer category_id,String category_name); 
		void insertItemCategory(String category_name,String sstid,Integer game_id,Integer parent_id,Integer is_leaf);
		void delCategory(Integer category_id, String sstid, Integer game_id);
		void moveCategory(Integer parent_id,Integer category_id, String sstid, Integer game_id);
		//friendly




	/****************************修复内外网数据接口****************************************************/
	Integer fixData(Integer logId, Integer gameId, String stid, String sstid, String op) ;

	/**************************************************************************************/
}

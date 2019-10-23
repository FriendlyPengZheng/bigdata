package com.taomee.tms.mgr.svc;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.taomee.bigdata.hbase.bean.dao.HbaseDao;
import com.taomee.bigdata.hbase.bean.dao.impl.ResultDaoImpl;
import com.taomee.tms.mgr.api.LogMgrService;
import com.taomee.tms.mgr.dao.ArtifactInfoDao;
import com.taomee.tms.mgr.dao.CollectInfoDao;
import com.taomee.tms.mgr.dao.CollectMetadataDao;
import com.taomee.tms.mgr.dao.CommentByComponentDao;
import com.taomee.tms.mgr.dao.CommentDao;
import com.taomee.tms.mgr.dao.ComponentCommentDao;
import com.taomee.tms.mgr.dao.ComponentDao;
import com.taomee.tms.mgr.dao.DataInfoDao;
import com.taomee.tms.mgr.dao.EconomyInfoDao;
import com.taomee.tms.mgr.dao.EmailConfigDao;
import com.taomee.tms.mgr.dao.EmailDataInfoDao;
import com.taomee.tms.mgr.dao.EmailTemplateContentDao;
import com.taomee.tms.mgr.dao.EmailTemplateDao;
import com.taomee.tms.mgr.dao.EmailTemplateDataDao;
import com.taomee.tms.mgr.dao.FavorInfoDao;
import com.taomee.tms.mgr.dao.FavorSetDataInfoDao;
import com.taomee.tms.mgr.dao.FavorSetInfoDao;
import com.taomee.tms.mgr.dao.GameInfoDao;
import com.taomee.tms.mgr.dao.GameInfoPageDao;
import com.taomee.tms.mgr.dao.GameTaskInfoDao;
import com.taomee.tms.mgr.dao.LogInfoDao;
import com.taomee.tms.mgr.dao.MaterialInfoDao;
import com.taomee.tms.mgr.dao.MetadataDao;
import com.taomee.tms.mgr.dao.MissionInfoDao;
import com.taomee.tms.mgr.dao.NaviDao;
import com.taomee.tms.mgr.dao.PageDao;
import com.taomee.tms.mgr.dao.ResultInfoDao;
import com.taomee.tms.mgr.dao.SchemaDiaryInfoDao;
import com.taomee.tms.mgr.dao.SchemaInfoDao;
import com.taomee.tms.mgr.dao.ServerDiaryInfoDao;
import com.taomee.tms.mgr.dao.ServerGPZSInfoDao;
import com.taomee.tms.mgr.dao.ServerInfoDao;
import com.taomee.tms.mgr.dao.ServerNameInfoDao;
import com.taomee.tms.mgr.dao.SharedCollectInfoDao;
import com.taomee.tms.mgr.dao.StidSStidRefLogDao;
import com.taomee.tms.mgr.dao.StidSStidRefLogDiaryDao;
import com.taomee.tms.mgr.dao.TaskInfoDao;
import com.taomee.tms.mgr.dao.TreeInfoDao;
import com.taomee.tms.mgr.dao.WhaleUserInfoDao;
import com.taomee.tms.mgr.dao.WhaleUserMonthDao;
import com.taomee.tms.mgr.entity.ArtifactInfo;
import com.taomee.tms.mgr.entity.CategoryInfo;
import com.taomee.tms.mgr.entity.CollectInfo;
import com.taomee.tms.mgr.entity.CollectMetadataInfo;
import com.taomee.tms.mgr.entity.Comment;
import com.taomee.tms.mgr.entity.CommentByComponentId;
import com.taomee.tms.mgr.entity.Component;
import com.taomee.tms.mgr.entity.CustomQueryParams;
import com.taomee.tms.mgr.entity.DataInfo;
import com.taomee.tms.mgr.entity.DataInfoQueryParams;
import com.taomee.tms.mgr.entity.DataInfoUniqueKeyParams;
import com.taomee.tms.mgr.entity.DataToObject;
import com.taomee.tms.mgr.entity.DeleteDataInfoParams;
import com.taomee.tms.mgr.entity.DistrDataInfo;
import com.taomee.tms.mgr.entity.EconomyInfo;
import com.taomee.tms.mgr.entity.EmailConfigInfo;
import com.taomee.tms.mgr.entity.EmailDataInfo;
import com.taomee.tms.mgr.entity.EmailTemplateContentInfo;
import com.taomee.tms.mgr.entity.EmailTemplateDataInfo;
import com.taomee.tms.mgr.entity.FavorInfo;
import com.taomee.tms.mgr.entity.FavorSetDataInfo;
import com.taomee.tms.mgr.entity.FavorSetInfo;
import com.taomee.tms.mgr.entity.GameInfo;
import com.taomee.tms.mgr.entity.GameInfoPage;
import com.taomee.tms.mgr.entity.GameTaskInfo;
import com.taomee.tms.mgr.entity.ItemCategoryInfo;
import com.taomee.tms.mgr.entity.ItemInfo;
import com.taomee.tms.mgr.entity.LogInfo;
import com.taomee.tms.mgr.entity.MaterialInfo;
import com.taomee.tms.mgr.entity.Metadata;
import com.taomee.tms.mgr.entity.MissionInfo;
import com.taomee.tms.mgr.entity.Navi;
import com.taomee.tms.mgr.entity.Page;
import com.taomee.tms.mgr.entity.PlatformInfo;
import com.taomee.tms.mgr.entity.RealTimeDataInfo;
import com.taomee.tms.mgr.entity.ResultInfo;
import com.taomee.tms.mgr.entity.SchemaDiaryInfo;
import com.taomee.tms.mgr.entity.SchemaInfo;
import com.taomee.tms.mgr.entity.ServerAllPlatform;
import com.taomee.tms.mgr.entity.ServerDiaryInfo;
import com.taomee.tms.mgr.entity.ServerGPZSInfo;
import com.taomee.tms.mgr.entity.ServerInfo;
import com.taomee.tms.mgr.entity.ServerNameInfo;
import com.taomee.tms.mgr.entity.SharedCollectInfo;
import com.taomee.tms.mgr.entity.StidSStidRefLog;
import com.taomee.tms.mgr.entity.StidSStidRefLogDiary;
import com.taomee.tms.mgr.entity.TaskInfo;
import com.taomee.tms.mgr.entity.TreeInfo;
import com.taomee.tms.mgr.entity.WhaleUserInfo;
import com.taomee.tms.mgr.entity.WhaleUserMonthInfo;
import com.taomee.tms.mgr.entity.ZSIdInfos;
import com.taomee.tms.mgr.entity.ZSInfo;
import com.taomee.tms.mgr.util.RedisUtil;


public class LogMgrServiceImpl implements LogMgrService {
	@Autowired
	private LogInfoDao logInfoDao;
	@Autowired
	private SchemaInfoDao schemaInfoDao;
	@Autowired
	private DataInfoDao dataInfoDao;
	@Autowired
	private ServerInfoDao serverInfoDao;
	@Autowired
	private TaskInfoDao taskInfoDao;
	@Autowired
	private ArtifactInfoDao artifactInfoDao;
	@Autowired
	private CommentDao commentDao;
	@Autowired
	private MetadataDao metadataDao;
	@Autowired
	private ComponentDao componentDao;
	@Autowired
	private NaviDao naviDao;
	@Autowired
	private CommentByComponentDao commentBycomponentDao;
	@Autowired
	private ComponentCommentDao componentCommentDao;
	@Autowired
	private ResultInfoDao resultInfoDao;
	@Autowired
	private MaterialInfoDao materialInfoDao;
	@Autowired
	private SchemaDiaryInfoDao schemaDiaryInfoDao;
	@Autowired
	private ServerDiaryInfoDao serverDiaryInfoDao;
	@Autowired
	private GameInfoPageDao gameInfoPageDao;
	@Autowired
	private PageDao pageDao;
	@Autowired
	private GameInfoDao gameInfoDao;
	@Autowired
	private ServerGPZSInfoDao serverGPZSInfoDao;
	@Autowired
	private ServerNameInfoDao serverNameInfoDao;
	@Autowired
	private MissionInfoDao missionInfoDao;
	@Autowired
	private HbaseDao<ResultInfo> hbaseDao;
	@Autowired
	private TreeInfoDao treeInfoDao;
	@Autowired
	private StidSStidRefLogDao stidSStidRefLogDao;
	@Autowired
	private StidSStidRefLogDiaryDao stidSStidRefLogDiaryDao;
	@Autowired
	private SharedCollectInfoDao sharedCollectInfoDao;
	@Autowired
	private EmailConfigDao emailConfigDao;
	@Autowired
	private EmailDataInfoDao emailDataInfoDao;
	@Autowired
	private EmailTemplateDao emailTemplateDao;
	@Autowired
	private EmailTemplateContentDao emailTemplateContentDao;
	@Autowired
	private EmailTemplateDataDao emailTemplateDataDao;
	@Autowired
	private GameTaskInfoDao gameTaskInfoDao;
	@Autowired
	private CollectInfoDao collectInfoDao;
	@Autowired
	private FavorInfoDao favorInfoDao;
	@Autowired
	private FavorSetInfoDao favorSetInfoDao;
	@Autowired
	private FavorSetDataInfoDao favorSetDataInfoDao;
	@Autowired
	private CollectMetadataDao collectMetadataDao;
	@Autowired
	private EconomyInfoDao economyInfoDao;
	@Autowired
	private WhaleUserInfoDao whaleUserInfoDao;
	@Autowired
	private WhaleUserMonthDao whaleUserMonthDao;
	
	
	private	Logger LOG = LogManager .getLogger(LogMgrServiceImpl.class);

	@Override
	public Integer insertLogInfo(LogInfo logInfo) {
		LOG.info("insert parameter: " + logInfo);
		if (logInfo.getLogName() == null || logInfo.getLogName().length() == 0) {
			throw new IllegalArgumentException("logName is null");
		}
		LOG.info("insert type: " + logInfo.getType());
		if (logInfo.getType() == null
				|| (logInfo.getType() != 0 && logInfo.getType() != 1)) {
			throw new IllegalArgumentException("type error");
		}

		if (logInfo.getStatus() == null) {
			logInfo.setStatus(0);
		}
		logInfoDao.insertLogInfo(logInfo);
		LOG.info("insert success logId: " + logInfo.getLogId());
		return logInfo.getLogId();
	}
	
	public void insertSStidRefLog(StidSStidRefLog stidSStidRefLog) {
		LOG.info("stidSStidRefLog params: " + stidSStidRefLog);

		if (stidSStidRefLog.getLogId() == null
				|| stidSStidRefLog.getLogId() == 0) {
			throw new IllegalArgumentException("logId can not null!");
		}
		if (logInfoDao.getLogInfoByLogId(stidSStidRefLog.getLogId()) == null) {
			throw new IllegalArgumentException("t_log_info中无该日志Id信息!");
		}
		if (stidSStidRefLog.getStatus() == null) {
			stidSStidRefLog.setStatus(0);
		}
		stidSStidRefLogDao.insertStidSStidRefLog(stidSStidRefLog);
		// insertSStidRefLogDiary(1, stidSStidRefLog);
	}

	public void insertSStidRefLogDiary(Integer curd,
			StidSStidRefLog stidSStidRefLog) {
		LOG.info("stidSStidRefLogDiary params: " + stidSStidRefLog);
		StidSStidRefLogDiary stidSStidRefLogDiary = new StidSStidRefLogDiary();
		stidSStidRefLogDiary.setCurd(curd); // 1:插入 2:修改状态
		stidSStidRefLogDiary.setStid(stidSStidRefLog.getStid());
		stidSStidRefLogDiary.setSstid(stidSStidRefLog.getSstid());
		stidSStidRefLogDiary.setLogId(stidSStidRefLog.getLogId());
		stidSStidRefLogDiary.setGameId(stidSStidRefLog.getGameId());
		stidSStidRefLogDiary.setStatus(stidSStidRefLog.getStatus());
		stidSStidRefLogDiaryDao
				.insertStidSStidRefLogDiary(stidSStidRefLogDiary);
	}

	// 构造一个schemaInfo对象
	public SchemaInfo createSchemaInfo(Integer logId, String schemaName,
			String op, String cascadeFields, Integer materialId,
			Integer nodeId, Integer status) {
		SchemaInfo scheInfo = new SchemaInfo();
		scheInfo.setLogId(logId);
		scheInfo.setSchemaName(schemaName);
		scheInfo.setOp(op);
		scheInfo.setCascadeFields(cascadeFields);
		scheInfo.setMaterialId(materialId);
		scheInfo.setNodeId(nodeId);
		scheInfo.setStatus(status);
		return scheInfo;
	}

	// 解析op操作 對distr先不做什麼操作

	////sign字段判断是不是需要进行count操作 0不需要 1：需要
	public void insertSchemaInfoByopAnalyser(
			CustomQueryParams customQueryParams, Integer logId, Integer nodeId, Integer sign) {
		String sstid = customQueryParams.getSstid();
		String opInfo = customQueryParams.getOp();

		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
		schemaInfos.clear();

		// op默认计算count和ucount操作
		if (sign == 1) {
			SchemaInfo scheCountInfo = createSchemaInfo(logId, sstid + "人次",
					"count()", "", 0, nodeId, 0);
			LOG.info("scheCountInfo params: " + scheCountInfo);
			schemaInfos.add(scheCountInfo);

			SchemaInfo scheUcountInfo = createSchemaInfo(logId, sstid + "人数",
					"distinct_count(_acid_)", "", 0, nodeId, 0);
			schemaInfos.add(scheUcountInfo);
			LOG.info("scheUcountInfo params: " + scheUcountInfo);
		}


		LOG.info("opInfo params: " + opInfo);
		// op有其他的操作
		if (opInfo != null && (!opInfo.equals(""))) {
			String[] opItem = opInfo.split("\\|"); // op操作可能不止一個
			LOG.info("opItem params: " + opItem.toString());

			for (int i = 0; i < opItem.length; i++) {
				String[] items = opItem[i].split(":");
				// _op_=sum:派对豆获得总数
				LOG.info("items[0]: " + items[0]);
				if (items[0].equals("sum")) {
					SchemaInfo schemaSumInfo = createSchemaInfo(logId, sstid
									+ items[1] + "求和", "sum" + "(" + items[1] + ")",
							"", 0, nodeId, 0);
					LOG.info("schemaSumInfo params: " + schemaSumInfo);
					schemaInfos.add(schemaSumInfo);
				}

				if (items[0].equals("ucount")) {
					SchemaInfo schemaUcountInfo = createSchemaInfo(logId, sstid
									+ items[1] + "人数", "distinct_count" + "(" + items[1] + ")",
							"", 0, nodeId, 0);
					LOG.info("schemaUcountInfo params: " + schemaUcountInfo);
					schemaInfos.add(schemaUcountInfo);
				}

				if (items[0].equals("max")) {
					SchemaInfo schemaMaxInfo = createSchemaInfo(logId, sstid
									+ items[1] + "最大值", "max" + "(" + items[1] + ")",
							"", 0, nodeId, 0);
					LOG.info("schemaMaxInfo params: " + schemaMaxInfo);
					schemaInfos.add(schemaMaxInfo);
				}

				// op=item:item _op_=item:task|set_distr:lv 分布暂时先不做处理 item是sum
				// max set等未处理
				if (sign == 1) {
					if (items[0].equals("item")) {
						SchemaInfo schemaItemCountInfo = createSchemaInfo(logId,
								sstid + items[1] + "item人次", "count()", items[1],
								0, nodeId, 0);
						schemaInfos.add(schemaItemCountInfo);

						// item的key可能是item
						SchemaInfo schemaItemUcountInfo = new SchemaInfo();
						if (items[1].equals("item")) {
							schemaItemUcountInfo = createSchemaInfo(logId, sstid
											+ items[1] + "item人数",
									"distinct_count(_acid_)", items[1], 0, nodeId,
									0);
						} else {
							schemaItemUcountInfo = createSchemaInfo(logId, sstid
									+ items[1] + "item人数", "distinct_count" + "("
									+ items[1] + ")", items[1], 0, nodeId, 0);
						}
						LOG.info("schemaItemUcountInfo params: "
								+ schemaItemUcountInfo);
						schemaInfos.add(schemaItemUcountInfo);
					}
				}
				//处理item的key有可能会发生变化的情况  _op_=item_sum:item,exp ucount max
				if (items[0].equals("item_sum")) {
					SchemaInfo schemaItemSumInfo = createSchemaInfo(logId, sstid
									+ items[1] + "item求和", "sum" + "(" + items[1].split(",")[1] + ")",
							items[1].split(",")[0], 0, nodeId, 0);
					LOG.info("schemaItemSumInfo params: " + schemaItemSumInfo);
					schemaInfos.add(schemaItemSumInfo);
				}

				if (items[0].equals("item_max")) {
					SchemaInfo schemaItemMaxInfo = createSchemaInfo(logId, sstid
									+ items[1] + "item最大值", "max" + "(" + items[1].split(",")[1] + ")",
							items[1].split(",")[0], 0, nodeId, 0);
					LOG.info("schemaItemMaxInfo params: " + schemaItemMaxInfo);
					schemaInfos.add(schemaItemMaxInfo);
				}
			}
		}
		LOG.info("schemaInfos params: " + schemaInfos);
		// 插入t_schema_info中同时插入t_schema_diary_info的映射表
		for (SchemaInfo aInfo : schemaInfos) {
			System.err.println("========================aInfo:" + aInfo);
			int count = schemaInfoDao.getSchemaInfoByParams(aInfo);
			System.err.println("++++++++++++++++++++++++++++++++++++++count" + count);
			if (count <= 0) {
				insertSchemaInfo(aInfo);
			}
		}
	}
	
	@Override
	public Integer insertCustomLogInfo(CustomQueryParams customQueryParams) {
		LOG.info("params: " + customQueryParams);
		Integer result = 0;
		if (customQueryParams == null || customQueryParams.equals("")) {
			throw new IllegalArgumentException("stidSStidRefLog can not null!");
		}
		String stid = customQueryParams.getStid();
		String sstid = customQueryParams.getSstid();
		Integer gameId = customQueryParams.getGameId();
		String  op = customQueryParams.getOp();
		
		if (stid == null || stid.equals("")) {
			throw new IllegalArgumentException("stid can not null!");
		}
		if (sstid == null || sstid.equals("")) {
			throw new IllegalArgumentException("sstid can not null!");
		}
		if (gameId == null || gameId == 0) {
			throw new IllegalArgumentException("gameId can not null!");
		}
		if (gameInfoDao.getGameInfoByGameId(gameId) == null) {
			throw new IllegalArgumentException("t_game_info中未加入该游戏，该游戏非法!");
		}

		StidSStidRefLog stidSStidRefLog = stidSStidRefLogDao.
								getLogIdBySStidGameId(stid, sstid, gameId, op); // stid+sstid+game+op查找是否已经有logInfo
		LOG.info("stidSStidRefLog: " + stidSStidRefLog);
		
		if (stidSStidRefLog == null || stidSStidRefLog.equals("")) {
			// 在t_Log_info中没有此logInfo信息
			String logName = gameId + "," + stid + "," + sstid;
			LogInfo logInfoByLogName = logInfoDao.getLogInfoBylogName(logName);
			LOG.info("logInfoByLogName: " + logInfoByLogName);
			
			if(logInfoByLogName != null && (!logInfoByLogName.equals(""))) {
				StidSStidRefLog sstidInfo = new StidSStidRefLog();
				sstidInfo.setLogId(logInfoByLogName.getLogId());
				sstidInfo.setStid(stid);
				sstidInfo.setSstid(sstid);
				sstidInfo.setGameId(gameId);
				sstidInfo.setStatus(0);
				//特殊处理op字段，为null或者"" 均为"null"
				if(op == null || op.equals(" ")) {
					op = "null";
				}
				sstidInfo.setOp(op);
				insertSStidRefLog(sstidInfo);
				LOG.info("sstidInfo: " + sstidInfo);

				//sstid作为node_name和game_id查找node_id
				//TreeInfo treeInfo = treeInfoDao.getTreeInfosBynameGameId(gameId, sstid);
				Integer nodeId = insertTreeInfoBycustom(stid, sstid, gameId, logInfoByLogName.getLogId());
				//Integer nodeId = treeInfo.getNodeId();
				
				insertSchemaInfoByopAnalyser(customQueryParams, logInfoByLogName.getLogId(), nodeId, 0);
				result = logInfoByLogName.getLogId();
			}else {
				LogInfo logInfo = new LogInfo();
				logInfo.setLogName(logName);
				logInfo.setType(1); // 自定义数据插入
				logInfo.setStatus(0);
				insertLogInfo(logInfo);
				Integer newlogId = logInfo.getLogId();
				
				StidSStidRefLog sstidInfo = new StidSStidRefLog();
				sstidInfo.setLogId(newlogId);
				sstidInfo.setStid(stid);
				sstidInfo.setSstid(sstid);
				sstidInfo.setGameId(gameId);
				sstidInfo.setStatus(0);
				sstidInfo.setOp(op);
				insertSStidRefLog(sstidInfo);
				LOG.info("sstidInfo: " + sstidInfo);

				// 插入t_web_tree
				Integer nodeId = insertTreeInfoBycustom(stid, sstid, gameId,
						newlogId);
				// 插入t_schema_info表和schemInfo对应的日志表 解析op字段
				insertSchemaInfoByopAnalyser(customQueryParams, newlogId, nodeId, 1);  //sign字段判断是不是需要进行count操作
				result = newlogId;
			}
		} else {
			result = stidSStidRefLog.getLogId();
		}
		LOG.info("result: " + result);
		return result;
	}
	
	@Override
	public Integer getCustomLogInfo(CustomQueryParams customQueryParams,Boolean writeToRedis, Integer redisExpireSeconds){
		if (customQueryParams == null)throw new IllegalArgumentException("Error:customQueryParams is null!");
		Integer logIDFromRedis = getCustom2LogInfoFromRedis(customQueryParams);
		if(logIDFromRedis != null && logIDFromRedis != -1){
			if(writeToRedis){
				updateCustomLogInfo2LogIDExpire(customQueryParams,redisExpireSeconds);
			}
			return logIDFromRedis;
		}else{
			Integer logIDFromDB = insertCustomLogInfo(customQueryParams);
			if(logIDFromDB > 0){
				if(writeToRedis){
					loadCustomQueryParam2LogidToRedis(customQueryParams,logIDFromDB,redisExpireSeconds);
				}
			}
			return logIDFromDB;
		}
	}
	
	@Override
	public Integer getCustomLogInfo(String stid,String sstid,Integer gameID,String op,Boolean writeToRedis, Integer redisExpireSeconds){
		if (stid == null || sstid == null || gameID == null)throw new IllegalArgumentException("Error:param stid/sstid/gameid could not be null!");
		Integer logIDFromRedis = getCustom2LogInfoFromRedis(stid,sstid,gameID,op);
		if(logIDFromRedis != null && logIDFromRedis != -1){
			if(writeToRedis){
				updateCustomLogInfo2LogIDExpire(stid,sstid,gameID,op,redisExpireSeconds);
			}
			return logIDFromRedis;
		}else{
			Integer logIDFromDB = insertCustomLogInfo(new CustomQueryParams(stid,sstid,gameID,op));
			if(logIDFromDB > 0){
				if(writeToRedis){
					loadCustomQueryParam2LogidToRedis(stid,sstid,gameID,op,logIDFromDB,redisExpireSeconds);
				}
			}
			return logIDFromDB;
		}
	}
	
	
	private void updateCustomLogInfo2LogIDExpire(CustomQueryParams customQueryParams, Integer expireSeconds) {
		if(customQueryParams == null)throw new IllegalArgumentException("param CustomQueryParams could not be null!");
		if(expireSeconds != null){
			RedisUtil redis = RedisUtil.getInstance();
			redis.jedisExpire(String.format("%s_%s|%s|%s|%s",
					"sssgidop2logid", customQueryParams.getStid(),
					customQueryParams.getSstid(), customQueryParams.getGameId(),
					customQueryParams.getOp()),expireSeconds);
		}
	}
	
	private void updateCustomLogInfo2LogIDExpire(String stid, String sstid ,Integer gameID,String op, Integer expireSeconds) {
		if(stid == null || sstid == null || gameID == null )throw new IllegalArgumentException("param stid/sstid/gameid could not be null!");
		if(expireSeconds != null){
			RedisUtil redis = RedisUtil.getInstance();
			redis.jedisExpire(String.format("%s_%s|%s|%s|%s","sssgidop2logid", stid,sstid, gameID,op),expireSeconds);
		}
	}

	private Integer getCustom2LogInfoFromRedis(CustomQueryParams customQueryParams) {
		if (customQueryParams == null)throw new IllegalArgumentException("Error:customQueryParams is null!");
		RedisUtil redis = RedisUtil.getInstance();
		String strLogIDFromRedis = redis.jedisGet(String.format("%s_%s|%s|%s|%s",
				"sssgidop2logid", customQueryParams.getStid(),
				customQueryParams.getSstid(), customQueryParams.getGameId(),
				customQueryParams.getOp()));
		if(strLogIDFromRedis == null || strLogIDFromRedis.equals("")){
			return -1;
		}else{
			return Integer.valueOf(strLogIDFromRedis);
		}
	}
	
	private Integer getCustom2LogInfoFromRedis(String stid,String sstid,Integer gameID,String op) {
		if(stid == null || sstid == null || gameID == null )throw new IllegalArgumentException("Error:param stid/sstid/gameid could not be null!");
		RedisUtil redis = RedisUtil.getInstance();
		String strLogIDFromRedis = redis.jedisGet(String.format("%s_%s|%s|%s|%s","sssgidop2logid", stid, sstid, gameID,op));
		if(strLogIDFromRedis == null || strLogIDFromRedis.equals("")){
			return -1;
		}else{
			return Integer.valueOf(strLogIDFromRedis);
		}
	}

	private void loadCustomQueryParam2LogidToRedis(CustomQueryParams customQueryParams, Integer logIDFromDB, Integer expireSeconds) {
		if(customQueryParams == null)throw new IllegalArgumentException("Error:CustomQueryParams is null!");
		if(logIDFromDB == null)throw new IllegalArgumentException("Error:logIDFromDB is null!");
		RedisUtil redis = RedisUtil.getInstance();
		redis.jedisSet(String.format("%s_%s|%s|%s|%s",
				"sssgidop2logid", customQueryParams.getStid(),
				customQueryParams.getSstid(), customQueryParams.getGameId(),
				customQueryParams.getOp()),logIDFromDB.toString());
		if(expireSeconds != null){
			redis.jedisExpire(String.format("%s_%s|%s|%s|%s",
					"sssgidop2logid", customQueryParams.getStid(),
					customQueryParams.getSstid(), customQueryParams.getGameId(),
					customQueryParams.getOp()),expireSeconds);
		}
	}
	
	private void loadCustomQueryParam2LogidToRedis(String stid,String sstid ,Integer gameID,String op, Integer logIDFromDB, Integer expireSeconds) {
		if(stid == null||sstid == null || gameID == null)throw new IllegalArgumentException("Error:param stid/sstid could not be null!");
		if(logIDFromDB == null)throw new IllegalArgumentException("Error:logIDFromDB is null!");
		RedisUtil redis = RedisUtil.getInstance();
		redis.jedisSet(String.format("%s_%s|%s|%s|%s","sssgidop2logid", stid,sstid,gameID,op),logIDFromDB.toString());
		if(expireSeconds != null){
			redis.jedisExpire(String.format("%s_%s|%s|%s|%s","sssgidop2logid",stid,sstid,gameID,op),expireSeconds);
		}
	}

	// 修改日志名
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public Integer updateLogInfo(LogInfo logInfo) {
		// TODO Auto-generated method stub
		LOG.info("updateName parameter: " + logInfo);
		if (logInfo == null || logInfo.equals("")) {
			throw new IllegalArgumentException("logInfo can not null!");
		}
		logInfoDao.updateLogInfoByLogName(logInfo);
		return 0;
	}

	// 修改日志状态的同时 需要修改与这个logID映射的stid等的记录也设置为废弃
	// 注：日志映射被废弃，日志修改表给我设置为删除

	@Override
	public Integer updateLogInfoByStatus(LogInfo logInfo) {
		// TODO Auto-generated method stub
		LOG.info("updateStatus parameter:" + logInfo);
		if (logInfo.getStatus() == null
				|| (logInfo.getStatus() != 0 && logInfo.getStatus() != 1)) {
			throw new IllegalArgumentException("Illegal parameter logId : "
					+ logInfo.getStatus());
		}
		// 查找logId对应的映射表中有无此映射

		logInfoDao.updateLogInfoByStatus(logInfo);
		StidSStidRefLog sstidInfo = new StidSStidRefLog();
		sstidInfo.setLogId(logInfo.getLogId());
		sstidInfo.setStatus(logInfo.getStatus());
		stidSStidRefLogDao.updateStidSStidRefLog(sstidInfo);

		LOG.info("sstidInfo insert success: " + sstidInfo);
		// insertSStidRefLogDiary(2, sstidInfo);
		return 0;
	}

	// 根据logId查找logInfo信息

	@Override
	public LogInfo getLogInfoByLogId(Integer logId) {
		LogInfo logInfo = null;

		if (logId == null || logId == 0) {
			throw new IllegalArgumentException("Illegal parameter logId: " + logId);
		}

		logInfo = logInfoDao.getLogInfoByLogId(logId);
		if (logInfo == null) {
			LOG.warn("Illegal Get logId");
		}
		return logInfo;
	}

	// 根据logId获取全部的logInfo信息

	@Override
	public List<LogInfo> getLogInfos() {
		return logInfoDao.getLogInfos();
	}

	// 根据状态获取logInfo的信息(status: 0-正常, 1-废弃)

	@Override
	public List<LogInfo> getLogInfos(Integer status) {
		// TODO Auto-generated method stub
		List<LogInfo> logInfo = null;
		if (status != 0 && status != 1) {
			throw new IllegalArgumentException("Illegal parameter status: "
					+ status);
		}

		logInfo = logInfoDao.getLogInfosByStatus(status);
		return logInfo;
	}

	public Integer insertMaterialInfo(MaterialInfo materialInfo) {
		// material_name在t_material_info中为唯一索引
		if (materialInfo.getMaterialName() == null
				|| materialInfo.getMaterialName().equals("")) {
			throw new IllegalArgumentException("materialName can not null!");
		}
		materialInfoDao.insertMaterialInfo(materialInfo);
		return materialInfo.getMaterialId();
	}

	public void insertSchemaDiaryInfo(SchemaInfo schemaInfo, int curd) {
		// schema_name log_id op cascade_fields materia_id node_id
		SchemaDiaryInfo schemaDiaryInfo = new SchemaDiaryInfo();
		schemaDiaryInfo.setCurd(curd);
		schemaDiaryInfo.setSchemaId(schemaInfo.getSchemaId());
		schemaDiaryInfo.setLogId(schemaInfo.getLogId());
		schemaDiaryInfo.setOp(schemaInfo.getOp());
		schemaDiaryInfo.setCascadeFields(schemaInfo.getCascadeFields());
		schemaDiaryInfo.setMaterialId(schemaInfo.getMaterialId());
		schemaDiaryInfo.setStatus(schemaInfo.getStatus());
		schemaDiaryInfoDao.insertSchemaDiaryInfo(schemaDiaryInfo);
	}

	// 插入字段：schema_name log_id op cascade_fields material_id node_id status flag

	@Override
	public Integer insertSchemaInfo(SchemaInfo schemaInfo) {
		LOG.info("insertScheam parameter:" + schemaInfo);
		LogInfo logInfo = logInfoDao.getLogInfoByLogId(schemaInfo.getLogId());
		if (null == logInfo || logInfo.getStatus() != 0) {
			throw new IllegalArgumentException("检查t_log_info中log_id不存在或状态不可用");
		}

		// TODO 还需检查op的合法性
		if (schemaInfo.getOp() == null) {
		}

		// 没有传递nodeId,直接用nodeId的默认值0
		if (schemaInfo.getNodeId() == null) {
			schemaInfo.setNodeId(0);
		}

		if (schemaInfo.getSchemaName() == null) {
			schemaInfo.setSchemaName("");
		}
		// TODO 检查cascade_fields的合法性
		if (schemaInfo.getCascadeFields() == null) {
			schemaInfo.setCascadeFields("");
		}

		MaterialInfo materialInfo = new MaterialInfo();
		if ((schemaInfo.getMaterialName() == null || schemaInfo
				.getMaterialName().length() == 0)
				&& (schemaInfo.getOp().contains("material") == false)) {
			schemaInfo.setMaterialId(0);
		} else if ((schemaInfo.getMaterialName() != null)
				&& (schemaInfo.getOp().contains("material"))) {
			materialInfo.setMaterialName(schemaInfo.getMaterialName());
			Integer materialId = insertMaterialInfo(materialInfo);
			schemaInfo.setMaterialId(materialId);
		} else {
			throw new IllegalArgumentException("materialName参数传递有问题");
		}

		if (schemaInfo.getStatus() == null) {
			schemaInfo.setStatus(0);
		}
		schemaInfoDao.insertSchemaInfo(schemaInfo);
		//insertSchemaDiaryInfo(schemaInfo, 1);// 插入curd=1
		return schemaInfo.getSchemaId();
	}

	// 仅更新material_name, shemaInfo信息不变更

	@Override
	public Integer updateSchemaInfo(SchemaInfo schemaInfo) {
		// TODO Auto-generated methosd stub
		LOG.info("updateMaterialName parameter:" + schemaInfo);
		Integer materialId = schemaInfo.getMaterialId();
		materialInfoDao.deleteMaterialInfo(materialId);

		if (materialId != 0) {
			MaterialInfo materialInfoNew = new MaterialInfo();
			materialInfoNew.setMaterialId(materialId);
			if (schemaInfo.getMaterialName() == null
					|| schemaInfo.getMaterialName().length() == 0) {
				throw new IllegalArgumentException("materialName不能为空");
			}
			materialInfoNew.setMaterialName(schemaInfo.getMaterialName());
			materialInfoDao.insertMaterialInfoBymaterialId(materialInfoNew);
		}
		// insertSchemaDiaryInfo(schemaInfo, 0);
		// //修改material_name对相对的日志表不插入新的记录
		return 0;
	}

	// 修改schemaInfo状态信息
	@Override
	public Integer updateSchemaInfoByStatus(SchemaInfo schemaInfo) {
		// TODO Auto-generated method stub
		if (schemaInfo.getStatus() != 0 && schemaInfo.getStatus() != 1) {
			throw new IllegalArgumentException("Illegal parameter :status "
					+ schemaInfo.getStatus());
		}
		schemaInfoDao.updateSchemaInfoByStatus(schemaInfo);
		insertSchemaDiaryInfo(schemaInfo, 2); // 更新状态
		return 0;
	}

	// 获取t_schema_info join t_material_info

	@Override
	public List<SchemaInfo> getSchemaInfos() {
		// TODO Auto-generated method stub
		return schemaInfoDao.getSchemaInfos();
	}

	// 根据schemaInfo状态获取schemaInfo信息
	@Override
	public List<SchemaInfo> getSchemaInfos(Integer status) {
		List<SchemaInfo> schemaInfos = null;
		if (status != 0 && status != 1) {
			throw new IllegalArgumentException("Illegal parameter: status"
					+ status);
		}
		schemaInfos = schemaInfoDao.getSchemaInfosByStatus(status);
		return schemaInfos;
	}

	@Override
	public SchemaInfo getSchemaInfoByschemaId(Integer schemaId) {
		SchemaInfo schemaInfo = null;

		if (schemaId == null || schemaId <= 0) {
			throw new IllegalArgumentException("Illegal parameter: schemaId"
					+ schemaId);
		}
		schemaInfo = schemaInfoDao.getSchemaInfoByschemaId(schemaId);
		return schemaInfo;
	}

	@Override
	public List<SchemaInfo> getSchemaInfosByLogId(Integer logId) {
		List<SchemaInfo> schemaInfos = null;
		LogInfo logInfo = getLogInfoByLogId(logId);
		if (logInfo == null || logInfo.equals("")) {
			throw new IllegalArgumentException("logId在logInfo中不存在！");
		}
		schemaInfos = schemaInfoDao.getSchemaInfosByLogId(logId);
		return schemaInfos;
	}
	
	@Override
	/**
	 * 返回给定logID映射的所有schemaInfo信息，并将映射写入redis
	 * 先从redis获取映射关系，获取不到再从db获取
	 */
	public List<SchemaInfo> getSchemaInfosByLogIdForStorm(Integer logId,Boolean writeToRedis, Integer redisExpireSeconds) {
//		if(!isLogIdAvailableInRedis(logId)){
//			LogInfo logInfoFromDB = getLogInfoByLogId(logId);
//			if (logInfoFromDB == null || logInfoFromDB.equals("")) {
//				throw new IllegalArgumentException("logId在logInfo中不存在！");
//			}else{
//				loadAvailableLogIdToRedis(logId);
//			}
//		}
		List<SchemaInfo> schemaInfosFromRedis = getSchemaInfosByLogIdFromRedis(logId);
		if(schemaInfosFromRedis != null){//映射存在于redis中
			if(writeToRedis){
				updateLogID2SchemaInfosExpireSeconds(logId,schemaInfosFromRedis,redisExpireSeconds);
			}
			return schemaInfosFromRedis;
		}else{
			List<SchemaInfo> schemaInfosFromDB = getSchemaInfosByLogId(logId);
			if(schemaInfosFromDB != null){
				if(writeToRedis){
					loadLogID2SchemaInfosToRedis(logId,schemaInfosFromDB,redisExpireSeconds);
				}
				return schemaInfosFromDB;
			}
		}
		return null;
	}
	
	@Override
	public List<SchemaInfo> getRefreshedSchemaInfosByLogIdForStorm(Integer logId,Boolean writeToRedis, Integer redisExpireSeconds) {
		List<SchemaInfo> schemaInfosFromDB = getSchemaInfosByLogId(logId);
		if(schemaInfosFromDB != null){
			if(writeToRedis){
				loadLogID2SchemaInfosToRedis(logId,schemaInfosFromDB,redisExpireSeconds);
			}
		}
		return schemaInfosFromDB;
	}
	
	private void updateLogID2SchemaInfosExpireSeconds(Integer logId, List<SchemaInfo> schemaInfos,Integer expireSeconds){
		if(logId == null)throw new IllegalArgumentException("param logId could not be null!");
		if(expireSeconds != null){
			RedisUtil redisUtil = RedisUtil.getInstance();
			if(schemaInfos != null){
				redisUtil.jedisExpire("l2schs_"+logId, expireSeconds);
				for(SchemaInfo s:schemaInfos){
					updateSchemaInfoExpireSeconds(s,expireSeconds);
				}
			}
		}
	}
	
	private void updateSchemaInfoExpireSeconds(SchemaInfo schemaInfo,Integer expireSeconds){
		updateSchemaInfoExpireSeconds(schemaInfo.getSchemaId(),expireSeconds);
	}
	
	private void updateSchemaInfoExpireSeconds(Integer schemaID,Integer expireSeconds){
		if(schemaID == null || expireSeconds == null){
			throw new IllegalArgumentException("param schemaID and expire seconds could not be null!");
		}else{
			RedisUtil redisUtil = RedisUtil.getInstance();
			redisUtil.jedisExpire("sch_"+schemaID,expireSeconds);
		}
	}
	
	private boolean isLogIdAvailableInRedis(Integer logId){
		if(logId == null)throw new IllegalArgumentException("logId could not be null!");
		RedisUtil redisUtil = RedisUtil.getInstance();
		return redisUtil.jedisSismember("avail_logids", logId.toString());
	}
	
	private void loadAvailableLogIdToRedis(Integer logId) {
		if(logId == null)throw new IllegalArgumentException("logId could not be null!");
		RedisUtil redisUtil = RedisUtil.getInstance();
		redisUtil.jedisSadd("avail_logids", logId.toString());
	}
	
	private List<SchemaInfo> getSchemaInfosByLogIdFromRedis(Integer logId){
		if(logId == null)throw new IllegalArgumentException("logId could not be null!");
		RedisUtil redisUtil = RedisUtil.getInstance();
		Set<String> schemaIDs= redisUtil.jedisSmembers("l2schs_"+logId);
		if(schemaIDs == null || schemaIDs.size() == 0){
			LOG.warn("Could not find logID2schemaID from redis,logID is "+logId);
			return null;
		}
		List<SchemaInfo> rt = new LinkedList<SchemaInfo>();
		try{
			for(String schemaID:schemaIDs){
				Map<String,String> schemaInfo = redisUtil.jedisHgetAll("sch_"+schemaID);
				if(schemaInfo.get("schemaid") == null){
					return null;
				}
				rt.add(genSchemaInfoFromJedisHset(schemaInfo));
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		if(rt.size() != schemaIDs.size()){
			return null;
		}else{
			return rt;
		}
	}
	
	private SchemaInfo genSchemaInfoFromJedisHset(Map<String,String> schemaInfo){
		SchemaInfo s = new SchemaInfo();
		for(String key:schemaInfo.keySet()){
			switch (key.toLowerCase()){
			case "schemaid":
				s.setSchemaId(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "logid":
				s.setLogId(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "op":
				s.setOp(schemaInfo.get(key));
				break;
			case "cascadefields":
				s.setCascadeFields(schemaInfo.get(key));
				break;
			case "materialid":
				s.setMaterialId(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "materialname":
				s.setMaterialName(schemaInfo.get(key));
				break;
			case "nodeid":
				s.setNodeId(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "status":
				s.setStatus(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "displayorder":
				s.setDisplayOrder(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "schemaname":
				s.setSchemaName(schemaInfo.get(key));
				break;
			case "flag":
				s.setFlag(Integer.valueOf(schemaInfo.get(key)));
				break;
			case "addtime":
				s.setAddTime(Timestamp.valueOf(schemaInfo.get(key)));
				break;
			default:
				break;
			}
		}
		return s;
	}

	private boolean loadLogID2SchemaInfosToRedis(Integer logId,List<SchemaInfo> schemaInfos,Integer expireSeconds) {
		if(loadLogID2SchemaIDsToRedis(logId,schemaInfos,expireSeconds) == true){
			loadSchemaInfosToRedis(schemaInfos,expireSeconds);
			return true;
		}else{
			return false;
		}
	}
	
	private boolean loadLogID2SchemaIDsToRedis(Integer logId,List<SchemaInfo> schemaInfos,Integer expireSeconds) {
		if(logId == null || schemaInfos == null)throw new IllegalArgumentException("Error: both logid and schemaInfos could not be null!");
		RedisUtil redisUtil = RedisUtil.getInstance();
		redisUtil.jedisDel("l2schs_"+logId.toString());
		try{
			for(SchemaInfo schemaInfo:schemaInfos){
				if(redisUtil.jedisSadd("l2schs_"+logId.toString(),schemaInfo.getSchemaId().toString()) == 0){
					redisUtil.jedisDel("l2schs_"+logId.toString());
					return false;
				}
				if(expireSeconds != null){
					redisUtil.jedisExpire("l2schs_"+logId.toString(), expireSeconds);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			redisUtil.jedisDel("l2schs_"+logId.toString());
			return false;
		}
		return true;
	}

	private void loadSchemaInfosToRedis(List<SchemaInfo> schemaInfos,Integer expireSeconds) {
		for(SchemaInfo s:schemaInfos){
			try{
				loadSchemaInfoToRedis(s,expireSeconds);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void loadSchemaInfoToRedis(SchemaInfo s,Integer expireSeconds) {
		if(s == null)throw new IllegalArgumentException("Error:schemainfo is null!");
		RedisUtil redisUtil = RedisUtil.getInstance();
		Map<String,String> schemaInfoMap = new HashMap<String,String>();
		schemaInfoMap.put("schemaid",s.getSchemaId().toString());
		schemaInfoMap.put("logid",s.getLogId().toString());
		schemaInfoMap.put("op",s.getOp());
		schemaInfoMap.put("cascadeFields",s.getCascadeFields());
		schemaInfoMap.put("materialid",s.getMaterialId().toString());
//		schemaInfo.put("materialname",s.getMaterialName());
//		schemaInfo.put("nodeid",s.getNodeId().toString());
		schemaInfoMap.put("status",s.getStatus().toString());
//		schemaInfo.put("displayorder",s.getDisplayOrder().toString());
		schemaInfoMap.put("schemaname",s.getSchemaName());
		schemaInfoMap.put("flag",s.getFlag().toString());
//		schemaInfo.put("addtime",s.getAddTime().toString());
		String ret = redisUtil.jedisHmset("sch_"+s.getSchemaId().toString(), schemaInfoMap);
		if(expireSeconds != null){
			redisUtil.jedisExpire("sch_"+s.getSchemaId().toString(),expireSeconds);
		}
	}
	
	@Override
	public List<SchemaInfo> getSchemaInfosByLogType(Integer type) {
		List<SchemaInfo> schemaInfos = null;
		if (type == null || (type != 1 && type != 0)) {
			// TODO 参数检查
			throw new IllegalArgumentException("Illegal parameter: type");
		}
		schemaInfos = schemaInfoDao.getSchemaInfosByLogType(type);
		return schemaInfos;
	}
	
	@Override
	public List<SchemaInfo> getSchemaInfosFromRedis() {
		return getSchemaInfosFromRedis("sch_*");
	}
	
	@Override
	public List<SchemaInfo> getSchemaInfosFromRedis(String pattern) {
		RedisUtil redisUtil = RedisUtil.getInstance();
		List<SchemaInfo> rt = new LinkedList<SchemaInfo>();
		Map<String,Map<String,String>> schemaInfos = redisUtil.jedisMhgetall(pattern);
		for(String schemaID:schemaInfos.keySet()){
			Map<String,String> schemaInfoHset = schemaInfos.get(schemaID);
			if(schemaInfoHset != null){
				rt.add(genSchemaInfoFromJedisHset(schemaInfoHset));
			}
		}
		return rt;
	}

	@Override
	public Integer insertDataInfo(DataInfo dataInfo) {
		if (dataInfo == null || dataInfo.equals("")) {
			throw new IllegalArgumentException("dataInfo can not null! ");
		}
		if (dataInfo.getRelateId() == null
				|| (dataInfo.getRelateId() != 1 && dataInfo.getRelateId() != 0)) {
			throw new IllegalArgumentException("Illegal parameter relateId： "
					+ dataInfo.getRelateId());
		}
		if (dataInfo.getType() == null
				|| (dataInfo.getType() != 0 && dataInfo.getType() != 1)) {
			throw new IllegalArgumentException("Illegal parameter type: "
					+ dataInfo.getType());
		}
		if (dataInfo.getIsLeaf() == null
				|| (dataInfo.getIsLeaf() != 1 && dataInfo.getIsLeaf() != 2)) {
			throw new IllegalArgumentException("Illegal parameter isLeaf: "
					+ dataInfo.getIsLeaf());
		}
		if (dataInfo.getParentId() == null || dataInfo.getParentId().equals("")
				|| dataInfo.getParentId() < 0) {
			throw new IllegalArgumentException("Illegal parameter parentId: "
					+ dataInfo.getParentId());
		}

		dataInfoDao.insertDataInfo(dataInfo);
		return dataInfo.getDataId();
	}

	@Override
	public DataInfo getDataInfo(Integer dataId) {
		DataInfo dataInfo = null;
		if (dataId == null || dataId < 0) {
			throw new IllegalArgumentException("Illegal parameter dataId: "
					+ dataId);
		}
		dataInfo = dataInfoDao.getDataInfo(dataId);
		return dataInfo;
	}

	@Override
	public List<DataInfo> getDataInfosByParams(DataInfoQueryParams params) {
		List<DataInfo> dataInfos = null;
		if (params.getType() != 0 && params.getType() != 1) {
			throw new IllegalArgumentException("Illegal parameter type: "
					+ params.getType());
		}
		dataInfos = dataInfoDao.getDataInfosByParams(params);
		return dataInfos;
	}

	public void insertServerDiaryInfo(ServerInfo serverInfo, int curd) {
		ServerDiaryInfo serverDiaryInfo = new ServerDiaryInfo();
		serverDiaryInfo.setCurd(curd);
		serverDiaryInfo.setServerId(serverInfo.getServerId());
		serverDiaryInfo.setGameId(serverInfo.getGameId());
		serverDiaryInfo.setParentId(serverInfo.getParentId());
		serverDiaryInfo.setIsLeaf(serverInfo.getIsLeaf());
		serverDiaryInfo.setServerName(serverInfo.getServerName());
		serverDiaryInfo.setStatus(serverInfo.getStatus());
		serverDiaryInfoDao.insertServerDiaryInfo(serverDiaryInfo);
	}

	@Override
	public Integer insertServerInfo(ServerInfo serverInfo) {
		LOG.info("insertServer parameter: " + serverInfo);

		if (serverInfo == null || serverInfo.equals("")) {
			throw new IllegalArgumentException("serverInfo can not null!");
		}
		if (serverInfo.getServerName() == null
				|| serverInfo.getServerName().equals("")) {
			throw new IllegalArgumentException("serverName can not null!");
		}

		GameInfo gameInfoByGameId = gameInfoDao.getGameInfoByGameId(serverInfo
				.getGameId());
		if (null == gameInfoByGameId || gameInfoByGameId.getStatus() != 1) {
			throw new IllegalArgumentException("gameId不存在或该游戏已经废弃");
		}

		if (serverInfo.getParentId() == 0) { // 顶级的游戏信息
			if (serverInfo.getIsLeaf() == 1) { // 插入父节点为叶子节点=1
				;
			} else {
				throw new IllegalArgumentException("插入的为父节点，但叶子节点的设置出错");
			}
		} else {
			if (serverInfo.getIsLeaf() == 1) {
				Integer parentId = serverInfo.getParentId();
				// 级联显示serverId
				// 插入子serverId,需要修改父serverId的is_leaf字段
				ServerInfo serverParentInfo = serverInfoDao
						.getServerInfo(serverInfo.getParentId());
				serverParentInfo.setIsLeaf(2); // isLeaf=2 不是叶子节点
				serverInfoDao.updateServerInfo(serverParentInfo);
				while (parentId != 0) {
					ServerInfo serverInfos = serverInfoDao
							.getServerInfo(parentId);
					if (serverInfos.getStatus() == 0) {
						parentId = serverInfos.getParentId();
					} else {
						throw new IllegalArgumentException("父节点中有不可用的节点");
					}
				}
			} else {
				throw new IllegalArgumentException("插入的为子节点，但叶子节点的设置出错");
			}
		}
		serverInfoDao.insertServerInfo(serverInfo);
		insertServerDiaryInfo(serverInfo, 1); // 插入日志信息表
		return serverInfo.getServerId();
	}

	// 修改server名 叶子节点信息

	@Override
	public Integer updateServerInfo(ServerInfo serverInfo) {
		// TODO Auto-generated method stub
		if (serverInfo.getServerName() == null
				|| serverInfo.getServerName().length() == 0) {
			throw new IllegalArgumentException("serverName为空");
		}
		serverInfoDao.updateServerInfo(serverInfo);
		// insertServerDiaryInfo(serverInfo, 0); // 更新server_name和is_leaf
		return 0;
	}

	@Override
	public Integer updateServerInfoByStatus(ServerInfo serverInfo) {
		// TODO Auto-generated method stub
		LOG.info("updateServerStatus parameter: " + serverInfo);
		Integer parentId = serverInfo.getParentId();
		while (parentId != 0) { // 设置叶子节点的状态
			ServerInfo serverInfos = serverInfoDao.getServerInfo(parentId);
			if (serverInfos.getStatus() == 0) {
				parentId = serverInfos.getParentId();
			} else {
				throw new IllegalArgumentException("父节点中有不可用的节点");
			}
		}
		if (parentId == 0) { // 设置最顶级父节点的状态
			List<ServerInfo> serverInfoByGameIdInfos = serverInfoDao
					.getServerInfoByGameId(serverInfo.getGameId());
			Iterator<ServerInfo> iter = serverInfoByGameIdInfos.iterator();
			while (iter.hasNext()) {
				if (iter.next().getStatus() == 0) {
					;
				} else {
					throw new IllegalArgumentException("子节点中有可用的节点");
				}
			}
		}
		serverInfoDao.updateServerInfoByStatus(serverInfo);
		insertServerDiaryInfo(serverInfo, 2); // 更新serverInfo的状态 0：正在使用 1：弃用
		return 0;
	}

	@Override
	public List<ServerInfo> getAllServerInfos() {
		// TODO Auto-generated method stub
		return serverInfoDao.getAllServerInfos();
	}

	@Override
	public ServerInfo getServerInfo(Integer serverId) {
		ServerInfo serverInfo = null;
		if (serverId == null || serverId <= 0) {
			throw new IllegalArgumentException("Illegal parameter serverId");
		}
		serverInfo = serverInfoDao.getServerInfo(serverId);
		return serverInfo;
	}

	@Override
	public List<ServerInfo> getAllServerInfos(Integer status) {
		List<ServerInfo> serverInfos = null;
		if (status == null || (status != 0 && status != 1)) {
			throw new IllegalArgumentException("Illegal parameter status ");
		}
		serverInfos = serverInfoDao.getAllServerInfosByStatus(status);
		return serverInfos;
	}

	@Override
	public Integer insertTaskInfo(TaskInfo taskInfo) {
		// TODO Auto-generated method stub
		if (taskInfo.getTaskName() == null
				|| taskInfo.getTaskName().length() == 0) {
			// TODO 参数检查
			throw new IllegalArgumentException("log_name为空");
		}

		taskInfoDao.insertTaskInfo(taskInfo);
		ArtifactInfo artifactInfo = new ArtifactInfo();
		artifactInfo.setTaskId(taskInfo.getTaskId());
		artifactInfo.setPeriod(taskInfo.getPeriod());
		artifactInfo.setResult(taskInfo.getResult());
		artifactInfoDao.insertArtifactInfo(artifactInfo);
		return taskInfo.getTaskId();
	}

	@Override
	public Integer updateTaskInfo(TaskInfo taskInfo) {
		// TODO Auto-generated method stub
		taskInfoDao.updateTaskInfo(taskInfo);
		ArtifactInfo artifactInfo = new ArtifactInfo();
		artifactInfo.setTaskId(taskInfo.getTaskId());
		artifactInfo.setPeriod(taskInfo.getPeriod());
		artifactInfo.setResult(taskInfo.getResult());
		artifactInfoDao.updateArtifactInfo(artifactInfo);
		return 0;
	}

	@Override
	public Integer deleteTaskInfo(Integer taskId) {
		// TODO Auto-generated method stub
		taskInfoDao.deleteTaskInfo(taskId);
		artifactInfoDao.deleteArtifactInfo(taskId);
		// 一个taskId对应一个artifactId
		return 0;
	}

	@Override
	public List<TaskInfo> getTaskInfos() {
		// TODO Auto-generated method stub
		return taskInfoDao.getTaskInfos();
	}

	@Override
	public TaskInfo getTaskInfo(Integer taskId) {
		// TODO Auto-generated method stub
		return taskInfoDao.getTaskInfo(taskId);
	}

	@Override
	public void insertArtifactInfo(ArtifactInfo artifactInfo) {
		// TODO Auto-generated method stub
		if (artifactInfo == null || artifactInfo.equals("")) {
			throw new IllegalArgumentException("artifactInfo為null");
		}
		artifactInfoDao.insertArtifactInfo(artifactInfo);
	}

	@Override
	public ArtifactInfo getArtifactInfoBytaskId(Integer taskId) {
		// TODO Auto-generated method stub
		return artifactInfoDao.getArtifactInfoBytaskId(taskId);
	}

	@Override
	public Integer insertCommentInfo(Comment commentInfo) {
		// TODO Auto-generated method stub
		commentDao.insertCommentInfo(commentInfo);
		// LOG.info("插入" + count + "条数据，刚刚插入记录的主键自增长值为：" +
		// commentInfo.getCommentId());
		return commentInfo.getCommentId();
	}

	// 其中commendId为0表示新增，否则为update

	@Override
	public void updateCommentInfo(Comment commentInfo) {
		// TODO Auto-generated method stub
		Integer commendId = commentInfo.getCommentId();
		if (commendId == Integer.valueOf(0)) {
			commentDao.insertCommentInfo(commentInfo);
		}
		commentDao.updateCommentInfo(commentInfo);
	}

	@Override
	public void deleteCommentInfo(Comment CommentInfo) {
		// TODO Auto-generated method stub
		Integer c_commentId = CommentInfo.getCommentId();
		List<Metadata> MetadataInfo = metadataDao
				.getMetadataInfoBycommentId(c_commentId);
		for (Metadata metadataInfo : MetadataInfo) {
			metadataInfo.setCommentId(Integer.valueOf(0));
			metadataDao.updateMetadataInfo(metadataInfo);
		}
		commentDao.deleteCommentInfo(CommentInfo);
	}

	@Override
	public List<Comment> getAllCommentInfos() {
		// TODO Auto-generated method stub
		return commentDao.getAllCommentInfos();
	}

	@Override
	public List<Comment> getCommentByKeyword(String keyword) {
		// TODO Auto-generated method stub
		return commentDao.getCommentByKeyword(keyword);
	}

	@Override
	public List<Comment> getCommentByKeywordFuzzy(String keyword) {
		// TODO Auto-generated method stub
		// keyword = "%"+keyword+"%";
		return commentDao.getCommentByKeywordFuzzy(keyword);
	}

	public Integer checkCommentId(Metadata metadataInfo) {
		Integer flag = 0;
		Integer metaCommentId = metadataInfo.getCommentId(); // ==0
		String metaComment = metadataInfo.getComment(); // !=null
		Comment commentInfo = commentDao
				.getCommentInfoBycommentId(metaCommentId);
		String comment = null;
		if (commentInfo != null) {
			comment = commentInfo.getComment();
		}
		if (Integer.valueOf(0) != metaCommentId) {
			if (null == comment) {
				metadataInfo.setCommentId(Integer.valueOf(0)); // 无注释
				metadataDao.updateMetadataInfo(metadataInfo);
				flag = 1;
				return flag;
			} else {
				commentInfo.setCommentId(metaCommentId);
				// commentInfo.setKeyword(metadata_name);
				commentInfo.setComment(metaComment);
				commentDao.updateCommentInfo(commentInfo);
				metadataDao.updateMetadataInfo(metadataInfo); // 直接插入
				flag = 2;
				return flag;
			}
		}
		if (null != comment) {
			flag = 3;
			return flag;
		}

		// 插入情况是m_comment==0 t_web_comment没有此记录
		Comment CommentInfo = new Comment();
		CommentInfo.setKeyword(metadataInfo.getMetadataName());
		CommentInfo.setComment(metadataInfo.getComment());
		commentDao.insertCommentInfo(CommentInfo);
		metadataInfo.setCommentId(CommentInfo.getCommentId());
		metadataDao.insertMetadataInfo(metadataInfo);
		flag = 4;
		return flag;
	}

	@Override
	public Integer insertMetadataInfo(Metadata metadataInfo) {
		// 调用检查参数 进行判断
		/*
		 * metadataDao.insertMetadataInfo(metadataInfo); Integer metadataId =
		 * metadataInfo.getMetadataId(); return metadataId;
		 */
		Integer flag = checkCommentId(metadataInfo);
		if (flag == 4) {
			Integer metadataId = metadataInfo.getMetadataId();
			// LOG.info(metadataId);
			return metadataId;
		}
		return 0;
	}

	@Override
	public void updateMetadataInfo(Metadata metadataInfo) {
		// TODO Auto-generated method stub
		// metadataDao.updateMetadataInfo(metadataInfo);
		checkCommentId(metadataInfo);
	}

	@Override
	public List<Metadata> getAllMetadataInfos() {
		// TODO Auto-generated method stub
		return metadataDao.getAllMetadataInfos();
	}

	@Override
	public void deleteMetadataInfo(Metadata metadataInfo) {
		// TODO Auto-generated method stub
		if (metadataInfo == null || metadataInfo.equals("")) {
			throw new IllegalArgumentException("delete metadataInfo為null");
		}

		if (metadataInfo.getMetadataId() != Integer.valueOf(0)) {
			metadataDao.deleteMetadataInfo(metadataInfo);
		}
	}

	@Override
	public Comment getCommentInfoBycommentId(Integer commentId) {
		// TODO Auto-generated method stub
		return commentDao.getCommentInfoBycommentId(commentId);
	}

	@Override
	public List<Metadata> getMetadataInfoBycommentId(Integer commentId) {
		// TODO Auto-generated method stub
		return metadataDao.getMetadataInfoBycommentId(commentId);
	}

	@Override
	public List<Component> getComponents(String moduleKey, Integer parentId,
			Integer gameId) {
		// TODO Auto-generated method stub
		return componentDao.getComponents(moduleKey, parentId, gameId);
	}

	@Override
	public Integer insertComponentInfo(Component componentInfo) {
		// TODO Auto-generated method stub
		if (componentInfo == null || componentInfo.equals("")) {
			throw new IllegalArgumentException("insert componentInfo為null");
		}
		componentDao.insertComponentInfo(componentInfo);
		return componentInfo.getComponentId();
	}

	@Override
	public void updateComponentInfo(Component componentInfo) {
		// TODO Auto-generated method stub
		if (componentInfo == null || componentInfo.equals("")) {
			throw new IllegalArgumentException("update metadataInfo為null");
		}
		componentDao.updateComponentInfo(componentInfo);
	}

	@Override
	public List<Navi> getNaviInfosByLevel() {
		// TODO Auto-generated method stub
		return naviDao.getNaviInfosByLevel();
	}

	@Override
	public List<CommentByComponentId> getCommentsByComponentId(
			Integer componentId) {
		// TODO Auto-generated method stub
		return commentBycomponentDao.getCommentsByComponentId(componentId);
	}

	@Override
	public List<Component> getComponentInfoByComponentId(Integer componentId) {
		// TODO Auto-generated method stub
		return componentDao.getComponentInfoByComponentId(componentId);
	}

	@Override
	public void saveComponentComments(Integer componentId, Integer commentId) {
		// TODO Auto-generated method stub
		componentCommentDao.saveComponentComments(componentId, commentId);
	}

	@Override
	public ArtifactInfo getArtifactInfoByartifact(Integer artifactId) {
		// TODO Auto-generated method stub
		return artifactInfoDao.getArtifactInfoByartifact(artifactId);
	}

	// 递归删除Components
	public void beforedeleteComponents(Integer componentId) {
		List<Component> componentInfo = componentDao
				.getComponentInfoByComponentId(componentId);
		for (Component ComponentInfo : componentInfo) {
			// Integer parentId = ComponentInfo.getParentId();
			List<Component> componentInfoByparentId = componentDao
					.getComponentInfoByParentId(componentId);
			for (Component ComponentInfoByparentId : componentInfoByparentId) {
				Integer ComponentId = ComponentInfoByparentId.getComponentId();
				if ((ComponentInfoByparentId.getParentId()) == null) {
					componentDao.deleteComponentInfoBycomponentId(ComponentId);
					LOG.info(ComponentId);
				} else {
					deleteComponentInfos(ComponentId);
				}
			}
		}
	}

	@Override
	public void deleteComponentInfos(Integer componentId) {
		// TODO Auto-generated method stub
		beforedeleteComponents(componentId);
		componentDao.deleteComponentInfoBycomponentId(componentId);
	}

	// period:timeDimension 0:日 1:周 月:2 版本周:3 分钟:4 小时:5
	@Override
	public void insertUpdateDataResultInfo(DataToObject datatoObject) {
		// TODO Auto-generated method stub
		LOG.info("datatoObject :" + datatoObject);
		DataInfo dataInfo = new DataInfo();
		Integer schemaId = datatoObject.getSchemaId();
		Integer artifactId = datatoObject.getArtifactId();
		Integer dataId = 0;
		Integer timeDimension = 0;
		String dataName = null;
		if (schemaId != null && schemaId != 0) {
			dataInfo.setRelateId(schemaId);
			dataInfo.setType(0);
		} else {
			dataInfo.setRelateId(artifactId);
			dataInfo.setType(1);
		}

		String cascade_fields[] = datatoObject.getCascadeField().split("\\|");
		LOG.info(cascade_fields[0] + " 级联长度：" + cascade_fields.length);

		for (int i = 0; i <= cascade_fields.length - 1; i++) {
			if (i == 0) {
				dataInfo.setParentId(0); // 最起始的data_id
				dataName = cascade_fields[i];
				LOG.info("仅有一层级联字段" + dataName);
			} else {
				dataInfo.setParentId(dataId);
				dataName = dataName + "|" + cascade_fields[i];
				LOG.info("多层级联" + dataName);
			}
			if (i == cascade_fields.length - 1) {
				dataInfo.setIsLeaf(1); // 叶子节点
			} else {
				// 不是叶子节点，需更新父节点为非叶子节点
				dataInfo.setIsLeaf(2); // 不是叶子节点
			}

			dataInfo.setDataName(dataName);
			// 赋值dataId
			dataInfoDao.insertUpdataInfo(dataInfo);

			// 做redis緩存 redis->mysql
			// 生存周期（to do）
			DataInfo FindDataId = new DataInfo();

			
			RedisUtil redisUtil = RedisUtil.getInstance();
			// key Data_relateId_type_dataName
			// RedisUtil redisUtil = new RedisUtil();
			String keyGet = "data" + "_" + dataInfo.getRelateId() + "_"
					+ dataInfo.getType() + "_" + dataName;
			String valueGet = redisUtil.jedisGet(keyGet);

			if (valueGet == null || valueGet.equals("")) {
				LOG.info("mysql get data");
				FindDataId = dataInfoDao.findDataInfo(dataInfo.getRelateId(),
						dataInfo.getType(), dataName);
				LOG.info("FindDataId: " + FindDataId);
				if (FindDataId == null || FindDataId.equals("")) {
					throw new IllegalArgumentException("find data_id is null");
				}
				dataId = FindDataId.getDataId();
				String kset = FindDataId.getDataId().toString();
				// redis set
				redisUtil.jedisSet(keyGet, kset);
			} else {
				dataId = Integer.valueOf(valueGet); // 直接從redis上取數據
				LOG.info("redis get data");
			}

			LOG.info("r_id->" + dataInfo.getRelateId() + " type->"
					+ dataInfo.getType() + " dataName" + dataName);

			LOG.info("插入结果表----> dataId:" + dataId);
			if (dataId == null || dataId == 0) {
				throw new IllegalArgumentException("dataId不能为null或0 :" + dataId);
			}

			Integer serverId = datatoObject.getServId();
			long time = datatoObject.getDate();
			Double value = datatoObject.getValue();

			// 结果数据插入hbase ResultBean | server_id | data_id | time | value |
			// tableCode:时间维度 0:日 周 1 月 2 版本周 3 分钟 4 小时 5
			ResultDaoImpl resultDaoImpl = new ResultDaoImpl();
			ResultInfo dataRow = new ResultInfo(serverId, dataId, time, value);
			LOG.info("ResultInfo:" + dataRow);
			
			if (schemaId == null) {
				ArtifactInfo artifactInfo = artifactInfoDao
						.getArtifactInfoByartifact(artifactId);
				
				timeDimension = artifactInfo.getPeriod(); // 加工项需要查看对应的时间维度
				resultDaoImpl.putData(dataRow, timeDimension);
			} else {
				resultDaoImpl.putData(dataRow, timeDimension);
			}
		}
	}

	public DataInfo findSchemaIdDataInfosBydataId(Integer dataId) {
		DataInfo dataInfo = new DataInfo();
		dataInfo = dataInfoDao.getDataInfo(dataId);
		if (dataInfo.getType() == 0) { // relate_id->schema_id
			return dataInfo;
		} else {
			return null;
		}
	}

	public Integer getSchemaDiaryInfoBylastId() {
		SchemaDiaryInfo schemaDiaryInfo = schemaDiaryInfoDao
				.getSchemaDiaryInfoBylastId();
		Integer dataId = schemaDiaryInfo.getDiaryId();
		if (dataId != 0) {
			return dataId;
		} else {
			return 0;
		}
	}

	@Override
	public List<SchemaDiaryInfo> getSchemaDiaryInfoBydiaryId(Integer diaryId) {
		// TODO Auto-generated method stub
		LOG.info("传进来的参数: " + "diaryId: " + diaryId);

		Integer diary_id = getSchemaDiaryInfoBylastId();
		LOG.info("数据库中的diary_id: " + "diary_id: " + diary_id);
		if (diaryId < diary_id) {
			return schemaDiaryInfoDao.getSchemaDiaryInfoBydiaryId(diaryId);
		} else if (diaryId > diary_id) {
			return null;
		} else {
			return null;
		}
	}

	// 通过schema_id和级联字段增加一个dataInfo

	@Override
	public Integer insertUpdateDataInfoByschemaId(Integer schemaId,
			String cascadeFields) {
		// TODO Auto-generated method stub
		LOG.info("传进来的参数: " + "schemaId: " + schemaId + "   cascadeFields: "
				+ cascadeFields);

		DataInfo dataInfo = new DataInfo();
		dataInfo.setRelateId(schemaId);
		dataInfo.setType(0);
		Integer dataId = 0;
		String dataName = null;

		String cascade_fields[] = cascadeFields.split("\\|");

		for (int i = 0; i <= cascade_fields.length - 1; i++) {
			if (i == 0) {
				dataInfo.setParentId(0);
				dataName = cascade_fields[i];
				LOG.info(dataName);
			} else {
				dataInfo.setParentId(dataId);
				dataName = dataName + "|" + cascade_fields[i];
			}
			if (i == cascade_fields.length - 1) {
				dataInfo.setIsLeaf(1); // 叶子节点
			} else {
				dataInfo.setIsLeaf(2);
			}

			dataInfo.setDataName(dataName);
			// 赋值dataId
			dataInfoDao.insertUpdataInfo(dataInfo);

			DataInfo FindDataId = dataInfoDao.findDataInfo(
					dataInfo.getRelateId(), dataInfo.getType(), dataName);

			LOG.info("schemaId-> " + schemaId + "   dataname-> " + dataName);
			if (FindDataId == null) {
				LOG.info("find data_id is null");
			} else {
				dataId = FindDataId.getDataId();
			}
		}
		if (dataId != 0) {
			return dataId;
		} else {
			return 0;
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////

	// redis上取分钟数据：拼接key:日期时间_schemaId_serverId_cascadeFields
	public RealTimeDataInfo getRedisDataMinInfo(DataInfo dataInfo,
			Integer serverId, String dataTime, Integer gameId) {
		TreeMap<Integer, Double> mapValue = new TreeMap<Integer, Double>();
		mapValue.clear();
		StringBuffer sb = new StringBuffer();
		sb.delete(0, sb.length());

		Integer schemaId = dataInfo.getRelateId();
		String cascadeFields = dataInfo.getDataName();

		sb.append(dataTime + "_");
		sb.append(schemaId + "_");
		sb.append(serverId + "_");
		sb.append(cascadeFields);
		LOG.info("sb: " + sb);

		String key = sb.toString();
		LOG.info("redis->key: " + key);

		Map<String, String> value = new TreeMap<String, String>();
		value.clear();
		try {
			RedisUtil redisUtil = RedisUtil.getInstance();
			value = redisUtil.jedishGetAll(sb.toString());
		} catch (NullPointerException e) {
			e.printStackTrace(); // redis未进行初始化
		}

		for (Map.Entry<String, String> entry : value.entrySet()) {
			if (entry.getValue() == null) {
				mapValue.put(Integer.valueOf(entry.getKey()), null);
			} else {
				mapValue.put(Integer.valueOf(entry.getKey()),
						Double.valueOf(entry.getValue()));
			}
		}

		String cascadeField[] = cascadeFields.split("\\|"); // 级联字段最后一个为dataName
		if (cascadeField[0].equals(gameId.toString())) {
			cascadeField[0] = "总在线";
		}
		RealTimeDataInfo realDataInfo = new RealTimeDataInfo(
				dataInfo.getDataId(), cascadeField[cascadeField.length - 1],
				mapValue);
		return realDataInfo;
	}

	// 拉取父节点下的所有子节点信息

	public List<DataInfo> getChildDataInfosByParentId(Integer parentId) {
		List<DataInfo> childInfos = new ArrayList<DataInfo>();
		childInfos.clear();

		List<DataInfo> childDataInfos = dataInfoDao
				.getChildDataInfoByparentId(parentId);
		if (childDataInfos == null || childDataInfos.size() == 0) {
			return null;
		}

		for (DataInfo childDataInfo : childDataInfos) {
			if (childDataInfo == null || childDataInfo.equals("")) {
				LOG.info("某个子节点没有");
			} else {
				// 子节点不为null
				childInfos.add(childDataInfo);
				getChildDataInfoByparentId(childDataInfo.getDataId());
			}
		}
		return childInfos;
	}

	// 小时数据 最顶级父节点的id=dataId //type=-1 (2)id=schemaId type=0
	@Override
	public List<RealTimeDataInfo> getDateValueMin(Integer dataId,
			Integer gameId, Integer serverId, String dataTime) {
		List<RealTimeDataInfo> realTimeDataInfos = new ArrayList<RealTimeDataInfo>();
		realTimeDataInfos.clear();

		if (dataId == null || dataId <= 0 || gameId == null || gameId <= 0
				|| serverId == null || serverId <= 0 || dataTime == null) {
			throw new IllegalArgumentException("分钟参数传递有问题: " + "dataId："
					+ dataId + " gameId: " + gameId + " serverId: " + serverId
					+ " dataTime: " + dataTime);
		}

		GameInfo gameInfo = gameInfoDao.getGameInfoByGameId(gameId);
		if (gameInfo == null || gameInfo.equals("")) {
			throw new IllegalArgumentException("传进的参数gameId不合法： " + gameId);
		}
		ServerInfo serverInfo = serverInfoDao.getServerInfo(serverId);
		if (serverInfo == null || serverInfo.equals("")) {
			throw new IllegalArgumentException("传进的参数serverId不合法： " + serverId);
		}

		TreeMap<Integer, Double> mapValue = new TreeMap<Integer, Double>();
		mapValue.clear();

		// 先拉取dataId父层级的数据
		DataInfo dataInfo = dataInfoDao.getDataInfo(dataId);
		if (dataInfo == null || dataInfo.equals("")) {
			throw new IllegalArgumentException("传进的dataId不合法： " + dataId);
		}

		RealTimeDataInfo realDataInfo = getRedisDataMinInfo(dataInfo, serverId,
				dataTime, gameId);
		realTimeDataInfos.add(realDataInfo);

		// 父节点下的所有子节点信息拉取
		List<DataInfo> childDataInfos = getChildDataInfosByParentId(dataId);
		if (childDataInfos == null || childDataInfos.size() == 0) {
			LOG.info("父dataId下无子节点");
		} else {
			LOG.info("父dataId下有子节点： " + childDataInfos);
			for (DataInfo childDataInfo : childDataInfos) {
				RealTimeDataInfo childrealDataInfo = getRedisDataMinInfo(
						childDataInfo, serverId, dataTime, gameId);
				realTimeDataInfos.add(childrealDataInfo);
			}
		}

		LOG.info("realTimeDataInfos" + realTimeDataInfos);
		return realTimeDataInfos;
	}

	// 小时数据：每分钟的数据累计相加的结果 注：一般情况下求sum、count可以直接相加，若是max、min则需要求一个小时的max、min
	@Override
	public List<RealTimeDataInfo> getDateValueHour(Integer dataId,
			Integer gameId, Integer serverId, String dataTime) {
		// TODO Auto-generated method stub
		if (dataId == null || dataId <= 0 || gameId == null || gameId <= 0
				|| serverId == null || serverId <= 0 || dataTime == null) {
			throw new IllegalArgumentException("小时参数传递有问题: " + "id：" + dataId
					+ " gameId: " + gameId + " serverId: " + serverId
					+ " dataTime: " + dataTime);
		}

		Double valueMap = 0.0;
		Double kv = 0.0;
		int count = 0;
		List<RealTimeDataInfo> realTimeDataInfos = new ArrayList<RealTimeDataInfo>();
		realTimeDataInfos.clear();
		// 取出一天中每分钟的数据
		List<RealTimeDataInfo> realTimeDataInfo = getDateValueMin(dataId,
				gameId, serverId, dataTime);
		if (realTimeDataInfo == null || realTimeDataInfo.size() == 0) {
			return null;
		}

		// 这个list中仅有一个realTimeDataInfo
		// LOG.info("getDateValueHour minMap from db: " + realTimeDataInfo);
		// [0,1439] 一天中每一分钟
		// 传给前端每个小时的数据【偏移量,value】 偏移60 按小时返回 TreeMap
		// key%60 为0映射到0这个位置
		for (RealTimeDataInfo realTimeInfo : realTimeDataInfo) {
			Integer realdataId = realTimeInfo.getDataId();
			String dataName = realTimeInfo.getDataName();
			TreeMap<Integer, Double> minMap = realTimeInfo.getValues();
			TreeMap<Integer, Double> mapValue = new TreeMap<Integer, Double>();
			mapValue.clear();

			Iterator iterator = minMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				Integer key = Integer.valueOf(entry.getKey().toString());
				// LOG.info("key: " + key);

				String value = null;
				if (entry.getValue() == null) {
					value = null;
				} else {
					value = entry.getValue().toString();
				}
				// LOG.info("value: " + value);

				if (value == null) {
					kv = null;
				} else {
					kv = Double.valueOf((entry.getValue().toString()));
				}

				if ((key + 1) % 60 == 0) {
					Integer keyHour = 0;
					keyHour = (key + 1) / 60 - 1;
					count++;
					mapValue.put(keyHour, valueMap);
				} else {
					count++;
					// LOG.info("count: " + count);
					if (kv == null && valueMap == null) {
						valueMap = null;
					} else if (kv == null && valueMap != null) {
						valueMap = valueMap;
					} else if (kv != null && valueMap != null) {
						valueMap += kv;
					} else if (kv != null && valueMap == null) {
						valueMap = kv;
					}
					// LOG.info("valueMap2:" + valueMap);
				}

				if (count >= 60) {
					valueMap = null;
					count = 0;
				}
			}

			RealTimeDataInfo realDataInfo = new RealTimeDataInfo(realdataId,
					dataName, mapValue);
			realTimeDataInfos.add(realDataInfo);
		}
		return realTimeDataInfos;
	}

	@Override
	public List<ResultInfo> getValueInfo(Integer dataId, Integer serverId,
			String startTime, String endTime, Integer timeDimension) {
		LOG.info("getValueInfo-> " + "dataId: " + dataId + " serverId: "
				+ serverId + " startTime:" + startTime + " endTime: " + endTime
				+ " timedimension :" + timeDimension);
		List<ResultInfo> resultInfos = null;
		long starttime = 0;
		long endtime = 0;

		// 检查参数是否正确
		if (dataId == null || dataId <= 0
				|| (dataInfoDao.getDataInfo(dataId) == null)) {
			throw new IllegalArgumentException("dataId不合法: " + dataId);
		}
		if (serverId == null || serverId <= 0
				/*|| (serverInfoDao.getServerInfo(serverId) == null)*/) {
			throw new IllegalArgumentException("serverId不合法： " + serverId);
		}
		if (timeDimension == null) {
			throw new IllegalArgumentException("时间维度不合法： " + timeDimension);
		}

		if (timeDimension == 0 || timeDimension == 1 || timeDimension == 2
				|| timeDimension == 3) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			try {
				Date startDate = simpleDateFormat.parse(startTime);
				Date endDate = simpleDateFormat.parse(endTime);

				starttime = startDate.getTime() / 1000;
				endtime = endDate.getTime() / 1000;

				LOG.info(starttime);
				LOG.info(endtime);
			} catch (ParseException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			}
			ResultDaoImpl resultDaoImpl = new ResultDaoImpl();
			ResultInfo startDataRow = new ResultInfo(serverId, dataId,
					starttime, 0d);
			ResultInfo endDataRowBean = new ResultInfo(serverId, dataId,
					endtime, 0d);
			resultInfos = resultDaoImpl.findListData(startDataRow,
					endDataRowBean, timeDimension);
			LOG.info("resultInfos: " + resultInfos);
		}
		return resultInfos;
	}

	@Override
	public List<DistrDataInfo> getGPZSDistrInfos(Integer relateId,
			Integer serverId, String startTime, String endTime, Integer type) {
		// TODO Auto-generated method stub
		LOG.info("getGPZSDistrInfos-> " + "relateId: " + relateId
				+ " serverId: " + serverId + " startTime:" + startTime
				+ " endTime: " + endTime + " type: " + type);
		if (relateId == null) {
			throw new IllegalArgumentException("relateId不能为null");
		}
		if (schemaInfoDao.getSchemaInfoByschemaId(relateId) == null) {
			throw new IllegalArgumentException("scheamId不合法: " + relateId);
		}
		if (artifactInfoDao.getArtifactInfoByartifact(relateId) == null) {
			throw new IllegalArgumentException("artifactId不合法: " + relateId);
		}
		if (serverId == null || serverId <= 0
				|| (serverInfoDao.getServerInfo(serverId) == null)) {
			throw new IllegalArgumentException("serverId不合法： " + serverId);
		}

		DataInfoQueryParams dataInfoQueryParams = new DataInfoQueryParams();
		dataInfoQueryParams.setType(type);
		dataInfoQueryParams.setRelateId(relateId);
		List<DataInfo> dataInfos = dataInfoDao
				.getDataInfosByParams(dataInfoQueryParams); // 根据schemaID或artifact_id+type找dataInfo
		if (dataInfos == null || dataInfos.size() == 0) {
			throw new IllegalArgumentException("dataInfos不合法: " + dataInfos);
		}

		// 注：大多数情况下此处的dataInfos中仅有一条记录，只有一个dataId
		List<DistrDataInfo> distrDataInfos = new ArrayList<DistrDataInfo>();
		distrDataInfos.clear();

		for (DataInfo dataInfoList : dataInfos) {
			Integer dataId = dataInfoList.getDataId();

			// 传进来的serverId将作为parentId拉取其所有的子serverId 存在多个serverId的情况
			List<ServerInfo> childServerInfos = serverInfoDao
					.getServerInfoByparentId(serverId);
			if (childServerInfos == null || childServerInfos.size() == 0) {
				return null;
			}

			for (ServerInfo childInfo : childServerInfos) {
				Integer childServerId = childInfo.getServerId();
				List<ResultInfo> resultInfos = getValueInfo(dataId,
						childServerId, startTime, endTime, 0);
				for (ResultInfo rf : resultInfos) {
					LOG.info("resultInfo:" + rf.getValue());
				}

				// 用serverId查找serverName作为页面的dataName
				ServerInfo getServerNameByserverInfo = serverInfoDao
						.getServerInfo(childServerId);
				DistrDataInfo distrDataInfo = new DistrDataInfo();
				distrDataInfo.setDataId(dataId);
				distrDataInfo.setDataName(getServerNameByserverInfo
						.getServerName());
				Double valueSum = 0.0;

				for (ResultInfo rfs : resultInfos) {
					LOG.info("rfs.getValue: " + rfs.getValue());
					valueSum += rfs.getValue();
				}
				distrDataInfo.setValue(valueSum);
				distrDataInfos.add(distrDataInfo);
			}
		}
		LOG.info("distrDataInfos: " + distrDataInfos);
		return distrDataInfos;
	}

	@Override
	public List<ServerDiaryInfo> getServerDiaryInfoBydiaryId(Integer diaryId) {
		// TODO Auto-generated method stub
		Integer diary_id = getServerDiaryInfoBylastId();
		if (diaryId < diary_id) {
			return serverDiaryInfoDao.getServerDiaryInfoBydiaryId(diaryId);
		} else if (diaryId > diary_id) {
			return null;
		} else {
			return null;
		}
	}

	public Integer getServerDiaryInfoBylastId() {
		// TODO Auto-generated method stub
		ServerDiaryInfo serverDiaryInfo = serverDiaryInfoDao
				.getServerDiaryInfoBylastId();
		Integer dataId = serverDiaryInfo.getDiaryId();
		if (dataId != 0) {
			return dataId;
		} else {
			return 0;
		}
	}

	@Override
	public List<DataInfo> getChildDataInfoByparentId(Integer parentId) {
		// TODO Auto-generated method stub
		// dataInfo.getDataId();
		return dataInfoDao.getChildDataInfoByparentId(parentId);
	}

	@Override
	public List<GameInfoPage> getGameInfoPage() {
		// TODO Auto-generated method stub
		return gameInfoPageDao.getGameInfoPage();
	}

	public void beforegetNaviInfo() {
		List<Page> pageInfo = pageDao.getNaviInfos();
	}

	@Override
	public List<GameInfo> getGameInfos() {
		// TODO Auto-generated method stub
		return gameInfoDao.getGameInfos();
	}
	
	@Override
	public GameInfo getGameInfoById(Integer gameId) {
		return gameInfoDao.getGameInfoByGameId(gameId);
	}

	@Override
	public Integer insertGameInfo(GameInfo gameInfo) {
		Integer gameId = -1;
		gameId = gameInfoDao.insertGameInfo(gameInfo);
		return gameId;
	}

	@Override
	public Integer updateGameInfo(GameInfo gameInfo) {
		// TODO Auto-generated method stub
		gameInfoDao.updateGameInfo(gameInfo);
		return 0;
	}

	@Override
	public Integer updateStatus(GameInfo gameInfo) {
		// TODO Auto-generated method stub
		gameInfoDao.updateStatus(gameInfo);
		return 0;
	}

	@Override
	public List<ServerInfo> getServerInfoByparentId(Integer parentId) {
		// TODO Auto-generated method stub
		return serverInfoDao.getServerInfoByparentId(parentId);
	}

	@Override
	public ServerGPZSInfo getServerInfoByTopgameId(Integer gameId) {
		// TODO Auto-generated method stub
		LOG.info("gameId " + gameId);
		ServerGPZSInfo serverInfo = serverGPZSInfoDao.getServerInfoByTopgameId(gameId);
		LOG.info("serverInfo:" + serverInfo);
		return serverInfo;
	}

	// 自定义接口

	@Override
	public List<TreeInfo> getTreeInfos(Integer gameId) {
		// TODO Auto-generated method stub
		return treeInfoDao.getTreeInfos(gameId);
	}

	public ArtifactInfo getArtifactInfoToLoadBytaskId(Integer taskId) {
		// TODO Auto-generated method stub
		TaskInfo taskInfo = taskInfoDao.getTaskInfo(taskId);
		return artifactInfoDao.getArtifactInfoByartifact(Integer
				.valueOf(taskInfo.getOp()));
	}

	@Override
	public List<DistrDataInfo> getDistrDataInfos(Integer relateId,
			Integer serverId, String startTime, String endTime, Integer type,
			Integer timeDimension) {
		LOG.info("分布拉数据接口参数" + "relateId: " + relateId + " serverId: "
				+ serverId + " startTime:" + startTime + " endtime: " + endTime
				+ " type: " + type + " timeDimension:" + timeDimension);

		DataInfoQueryParams dataInfoQueryParams = new DataInfoQueryParams();
		dataInfoQueryParams.setType(type);
		dataInfoQueryParams.setRelateId(relateId);
		List<DataInfo> dataInfos = dataInfoDao
				.getDataInfosByParams(dataInfoQueryParams);

		List<DistrDataInfo> distrDataInfos = new ArrayList<DistrDataInfo>();
		distrDataInfos.clear();

		for (DataInfo dataInfoList : dataInfos) {
			Integer dataId = dataInfoList.getDataId();

			List<ResultInfo> resultInfos = getValueInfo(dataId, serverId,
					startTime, endTime, timeDimension);
			if (resultInfos == null || resultInfos.equals("")) {
				LOG.info("数据库中没有数据 resultInfos" + resultInfos);
				return null;
			}

			LOG.info("数据库中拉出的数据resultInfo:" + resultInfos);

			DistrDataInfo distrDataInfo = new DistrDataInfo();
			distrDataInfo.setDataId(dataId);
			
			if(dataInfoList.getDataName().equals("")) {
				continue;
			}
			distrDataInfo.setDataName(dataInfoList.getDataName());
			
			Double valueSum = 0.0;

			for (ResultInfo rfs : resultInfos) {
				// LOG.info("rfs.getValue: " + rfs.getValue());
				valueSum += rfs.getValue();
			}
			distrDataInfo.setValue(valueSum);
			distrDataInfos.add(distrDataInfo);
		}
		return distrDataInfos;
	}

	@Override
	public List<TreeInfo> getTreeInfosByGameIdParentId(Integer gameId,
			Integer parentId) {
		// TODO Auto-generated method stub
		return treeInfoDao.getTreeInfosByGameIdParentId(gameId, parentId);
	}

	// 设置node名称 t_web_tree中的node_name

	@Override
	public void updateNodeName(TreeInfo treeInfo) {
		// TODO Auto-generated method stub
		LOG.info("updateNameParams: " + treeInfo);
		TreeInfo tree = ensureNodeExistsAndAuthed(treeInfo.getNodeId());
		LOG.info("tree:" + tree);
		if (tree != null) {
			treeInfoDao.updateNodeByParams(treeInfo);
		}
	}

	// 设置t_schema_info中的schema_name名称

	@Override
	public void updateItemName(SchemaInfo schemaInfo) {
		// TODO Auto-generated method stub
		LOG.info("updateNameParams: " + schemaInfo);

		schemaInfoDao.updateSchemaInfoBySchemaName(schemaInfo);

	}

	// 确保节点存在且拥有游戏权限

	public TreeInfo ensureNodeExistsAndAuthed(Integer nodeId) {
		// TODO Auto-generated method stub
		TreeInfo treeInfo = getNodeById(nodeId);
		return treeInfo;
	}

	// 获取节点信息

	public TreeInfo getNodeById(Integer nodeId) {
		// TODO Auto-generated method stub
		return treeInfoDao.getNodeById(nodeId); // is_basic=0
	}

	@Override
	public TreeInfo addTreeNode(TreeInfo treeInfo) {
		// TODO Auto-generated method stub
		LOG.info("addTreeNode:" + treeInfo);
		Integer parentId = treeInfo.getParentId();
		// 游戏权限在接收参数时已经检查
		// 第一级节点父节点ID为0
		treeInfo.setIsLeaf(2); // 默认未知类型 NODE_UNKNOWN=2 需改枚举
		if (parentId != 0) {
			TreeInfo aInfo = treeInfoDao.getNodeById(parentId);
			LOG.info("aInfo:" + aInfo);
			if (aInfo == null) {
				LOG.info("父节点不存在！");
				throw new IllegalArgumentException("父节点不存在！");
			}
			if (aInfo.getIsLeaf() == 1) { // NODE_LEAF=1
				LOG.info("不能在叶子节点下新增节点！");
				throw new IllegalArgumentException("不能在叶子节点下新增节点！");
			}
			if (aInfo.getParentId() != 0) {
				treeInfo.setIsLeaf(1); // 只能叶子，确保三层结构
			}
		}

		List<TreeInfo> aChildren = getNodeByPid(treeInfo); // parentId
		// ,node_name,
		// gameId
		if (aChildren != null && aChildren.size() != 0) {
			LOG.info("父节点下已经存在相同名称的节点！");
			throw new IllegalArgumentException("父节点下已经存在相同名称的节点！");
		}

		TreeInfo aFields = new TreeInfo();
		aFields.setNodeName(treeInfo.getNodeName());
		aFields.setGameId(treeInfo.getGameId());
		aFields.setParentId(treeInfo.getParentId());
		aFields.setIsBasic(0);
		aFields.setIsLeaf(treeInfo.getIsLeaf());

		LOG.info("aFields:" + aFields);
		if (treeInfo.getParentId() != 0) {
			TreeInfo tree = new TreeInfo();
			tree.setNodeId(treeInfo.getParentId());
			tree.setIsLeaf(0); // NODE_NONLEAF 非叶子节点
			tree.setGameId(treeInfo.getGameId());
			treeInfoDao.updateNode(tree); // parentId(nodeId) is_leaf=0
		}
		treeInfoDao.insertTreeInfo(aFields);
		return aFields;
	}

	// 通过nodeId作为父节点ID 查找子节点，子节点名称可选，父节点为0时需指定游戏
	// 查询该nodeId有多少个子节点
	/*
	 * @param integer nodeId
	 * 
	 * @param string NodeName
	 * 
	 * @param integer GameId
	 */

	public List<TreeInfo> findNodeChildByNodeIdPid(TreeInfo treeInfo) {
		if (treeInfo == null) {
			return null;
		}

		Integer nodeId = treeInfo.getNodeId();
		TreeInfo tree = new TreeInfo();
		tree.setIsBasic(0);
		tree.setParentId(nodeId);// 传进来的参数用node_id做父节点 查找节点下是否有其他节点

		if (nodeId == 0) {
			throw new IllegalArgumentException("nodeId为0，参数错误");
		}

		/*if (treeInfo.getNodeName() != null && !treeInfo.equals("")) {
			tree.setNodeName(treeInfo.getNodeName());
		}*/
		
		if (treeInfo.getGameId() != null || !treeInfo.equals("")) {
			tree.setGameId(treeInfo.getGameId());
		}
		LOG.info("tree" + tree.toString());
		return treeInfoDao.getNodeByPid(tree); // 传参数 nodeId[可选nodeName]
	}

	// 通过父节点ID查找子节点，子节点名称可选，父节点为0时需指定游戏
	/*
	 * @param integer ParentId
	 * 
	 * @param string NodeName
	 * 
	 * @param integer GameId
	 */
	// 和该节点同一个父节点的所有节点信息

	public List<TreeInfo> getNodeByPid(TreeInfo treeInfo) {
		if (treeInfo == null) {
			return null;
		}

		Integer parentId = treeInfo.getParentId();
		TreeInfo tree = new TreeInfo();
		tree.setIsBasic(0);
		tree.setParentId(parentId);

		// treeInfo.getNodeName() == null
		if (parentId == 0) {
			// 父节点为0时 gameId不能0
			if (treeInfo.getGameId() == 0) {
				LOG.info("父节点为0时需指定游戏");
				throw new IllegalArgumentException("父节点为0时需指定游戏");
			}
			tree.setGameId(treeInfo.getGameId());
		}

		if (treeInfo.getNodeName() != null && !treeInfo.equals("")) {
			tree.setNodeName(treeInfo.getNodeName());
		}
		LOG.info("tree" + tree.toString());
		return treeInfoDao.getNodeByPid(tree); // 传参数parentId
	}

	// 刪除节点

	@Override
	public void delTreeNode(List<Integer> nodeList, Integer gameId) {
		// TODO Auto-generated method stub
		for (Integer aNodeIds : nodeList) {
			LOG.info("aNodeIds:" + aNodeIds);
			TreeInfo aInfo = ensureNodeExistsAndAuthed(aNodeIds); // 判断节点是否被删除和有权限
			LOG.info("aInfo:" + aInfo.toString());
			// 检查是否有兄弟节点
			unknownParentNode(aInfo);
			List<Integer> aNodes = new ArrayList<>();
			// 非叶子，找到其子对应的统计项重置后删除
			if (aInfo.getIsLeaf() == 0) {
				List<TreeInfo> nodeInfos = findNodeChildByNodeIdPid(aInfo); // node_id
				for (TreeInfo nodeInfo : nodeInfos) {
					LOG.info("nodeInfo:" + nodeInfo);
					aNodes.add(nodeInfo.getNodeId());
				}
			}

			LOG.info("aNodesList :" + aNodes);
			if (aNodes == null || aNodes.size() == 0) {
				aNodes.add(aNodeIds);
			}

			LOG.info("aNodesList :" + aNodes);
			for (Integer node : aNodes) {
				LOG.info("删除node:" + node);
				schemaInfoDao.updateStatItemByNodeId(node, 0);
				// 通过节点ID更新统计项信息
				//artifactInfoDao.updateStatItemByNodeId(node, 0);
				deleteNode(node);
			}
		}
	}

	// 当父节点下没有其他子节点时，设置父节点为未知

	public void unknownParentNode(TreeInfo aInfo) {
		if (aInfo.getParentId() != 0) {
			Integer parentId = aInfo.getParentId();
			List<TreeInfo> sibling = getNodeByPid(aInfo); // parent_id
			boolean bHasSibling = false;
			for (TreeInfo aSibling : sibling) {
				if (aSibling.getNodeId() != aInfo.getNodeId()) {
					bHasSibling = true;
				}
			}
			if (!bHasSibling) {
				TreeInfo tree = new TreeInfo();
				tree.setNodeId(aInfo.getParentId());
				tree.setIsLeaf(2); // is_leaf=2 未知
				tree.setGameId(aInfo.getGameId());
				treeInfoDao.updateNode(tree);
			}
		}
	}

	public void deleteNode(Integer nodeId) {
		treeInfoDao.deleteNode(nodeId);
	}

	// 移动节点，父节点不能为叶子节点，树深度不能超过3层
	/*
	 * game_id:2 id:85184 parent_id:0 after_id:531247 game_id:2 id:85185 移动的节点
	 * parent_id:531247 移动的节点的父节点 after_id:531248 后一个 game_id:659 id:919297
	 * parent_id:91565 after_id:0 game_id:659 id:915655 parent_id:0 after_id:-1
	 */

	//moveNode gameId: 664nodeId:894873parentId:870903afterId:870904
	@Override
	public void moveTreeNode(Integer gameId, Integer nodeId, Integer parentId,
			Integer afterId) {
		LOG.info("moveNode：" + "gameId: " + gameId + " nodeId:" + nodeId
				+ " parentId:" + parentId + " afterId:" + afterId);

		// TODO Auto-generated method stub
		TreeInfo aInfo = ensureNodeExistsAndAuthed(nodeId);
		unknownParentNode(aInfo); // 当父节点下没有其他子节点时，设置父节点为未知
		if (parentId != 0) {
			TreeInfo aParentInfo = ensureNodeExistsAndAuthed(parentId);
			if (aParentInfo.getIsLeaf() == 1) {
				LOG.error("父节点是叶子节点，不能包含其他节点！");
				throw new IllegalArgumentException("父节点是叶子节点，不能包含其他节点！");
			}
			if (aParentInfo.getIsLeaf() == 2) {
				TreeInfo tree = new TreeInfo();
				tree.setNodeId(nodeId);
				tree.setIsLeaf(0);
				tree.setGameId(gameId);
				treeInfoDao.updateNode(tree); // node_id
												// id_leaf=NODE_NONLEAF
			}
			List<TreeInfo> aCheck = new ArrayList<TreeInfo>();
			if (aParentInfo.getParentId() != 0) {
				aCheck.add(aInfo);
			} else {
				aCheck = findNodeChildByNodeIdPid(aInfo); // node_id
			}
			for (TreeInfo check : aCheck) {
				if (!(check.getIsLeaf() != 0)) {
					LOG.error("树深度不能超过3层！");
					throw new IllegalArgumentException("树深度不能超过3层！");
				}
				if (check.getIsLeaf() == 2) {
					TreeInfo checkTree = new TreeInfo();
					checkTree.setNodeId(check.getNodeId());
					checkTree.setIsLeaf(1);
					checkTree.setGameId(check.getGameId());
					treeInfoDao.updateNode(checkTree); // nodeiD
														// is_leaf=NODE_LEAF
				}
			}
		}

		TreeInfo tree = new TreeInfo();
		tree.setNodeId(aInfo.getNodeId());
		tree.setParentId(aInfo.getParentId());
		tree.setGameId(aInfo.getGameId());
		treeInfoDao.updateNode(tree); // nodeId parentId

		aInfo.setParentId(parentId);
		adjustDisplayOrder(afterId, aInfo);
	}

	// 将aInfo排序到iAfterId的前面

	public void adjustDisplayOrder(Integer iAfterId, TreeInfo treeInfo) {
		TreeInfo aNodeInfo = new TreeInfo();
		aNodeInfo.setNodeId(treeInfo.getNodeId());
		aNodeInfo.setParentId(treeInfo.getParentId());
		aNodeInfo.setGameId(treeInfo.getGameId());

		if (iAfterId != 0) {
			TreeInfo aAfterInfo = ensureNodeExistsAndAuthed(iAfterId);

			LOG.info("aAfterInfo: " + aAfterInfo);
			LOG.info("aAfterInfo.parentId: " + aAfterInfo.getParentId());
			LOG.info("aNodeInfo.parentId: " + aNodeInfo.getParentId());
			LOG.info("aNodeInfo.nodeId: " + aNodeInfo.getNodeId());

			if (!aAfterInfo.getParentId().equals(aNodeInfo.getParentId())) {
				LOG.error("排序节点的父节点必须一致！");
				throw new IllegalArgumentException("排序节点的父节点必须一致！");
			}
		} else {
			iAfterId = 0;
		}

		List<TreeInfo> treeInfos = getNodeByPid(aNodeInfo); // 父找同级的节点：必须参数
															// parent_id
															// node_name game_id
		// 若有同級节点 修改displayOrder字段 需进行位置排序
		// 同级的节点中包含需要排序的这两个节点 需要判断
		if (treeInfo != null) {
			int order = 1;
			for (TreeInfo childInfo : treeInfos) {
				if (childInfo.getNodeId().intValue() == iAfterId.intValue()) {
					LOG.info("iAfterId：" + iAfterId);
					continue;
				}

				if (childInfo.getNodeId().intValue() == aNodeInfo.getNodeId()
						.intValue()) {
					LOG.info("aNodeInfo：" + aNodeInfo);
					TreeInfo tree = new TreeInfo(aNodeInfo.getNodeId(), order,
							treeInfo.getGameId());
					treeInfoDao.updateNode(tree);

					TreeInfo treeAfter = new TreeInfo(iAfterId, ++order,
							treeInfo.getGameId());
					LOG.info("treeAfter：" + treeAfter);
					treeInfoDao.updateNode(treeAfter);
					++order;
					continue;
				}

				LOG.info("order:" + order);
				TreeInfo tree = new TreeInfo(childInfo.getNodeId(), order,
						treeInfo.getGameId());
				treeInfoDao.updateNode(tree);
				LOG.info("sort tree: " + tree);
				++order;
			}
		}
	}

	// 合并节点，对非叶子节点有效

	@Override
	public TreeInfo mergeTreeNode(Integer nodeId, Integer gameId) {
		// TODO Auto-generated method stub
		TreeInfo aInfo = ensureNodeExistsAndAuthed(nodeId);
		LOG.info("mergeTreeNode aInfo: " + aInfo);
		if (aInfo.getIsLeaf() == 1) {
			LOG.warn("非叶子节点才能合并");
		}

		TreeInfo tree = new TreeInfo();
		tree.setNodeId(nodeId);
		tree.setIsLeaf(1); // 叶子节点
		tree.setGameId(gameId);
		treeInfoDao.updateNode(tree);

		merge(aInfo.getNodeId(), aInfo, new SchemaInfo(), new ArtifactInfo());
		return treeInfoDao.getNodeById(nodeId);
	}

	// 参数：合并到的节点 节点信息 用于操作统计项 用于操作加工统计项

	public void merge(Integer nodeId, TreeInfo nodeInfo, SchemaInfo schemaInfo,
			ArtifactInfo artifactInfo) {
		if (nodeInfo.getIsLeaf() != 0)
			return;
		List<TreeInfo> aNodes = findNodeChildByNodeIdPid(nodeInfo); // node_id
		LOG.info("aNodes1" + aNodes);
		if (schemaInfo == null) {
			schemaInfo = new SchemaInfo();
		}
		if (artifactInfo == null) {
			artifactInfo = new ArtifactInfo();
		}

		for (TreeInfo node : aNodes) {
			if (node.getIsLeaf() == 0) {
				merge(nodeId, node, schemaInfo, artifactInfo);
			} else if (node.getIsLeaf() == 1) {
				System.out.println("old node.getNodeId() :"+node.getNodeId());
				System.out.println("new nodeId :"+nodeId);
				schemaInfoDao.updateStatItemByNodeId(node.getNodeId(), nodeId);
				//artifactInfoDao.updateStatItemByNodeId(node.getNodeId(), nodeId);
			}
			deleteNode(node.getNodeId());
		}
	}

	// 绑定统计项到树节点
	/*
	 * 参数：game_id:659 id:1209635 report_id 移动item parent_id:915270 移动到那个树节点的下面
	 */

	@Override
	public void moveStatItem(Integer gameId, List<Integer> itemSchemaIdList,
			Integer moveToNodeId) {
		// TODO Auto-generated method stub
		// 需要移动的item
		LOG.info("传进来的参数： " + "game_id: " + gameId + " itemScheamId: "
				+ itemSchemaIdList + " modveToNodeId: " + moveToNodeId);
		for (Integer schemaId : itemSchemaIdList) {
			SchemaInfo aStatItem = schemaInfoDao
					.getSchemaInfoByschemaId(schemaId);
			// 移到回收站
			if (moveToNodeId == -1) {
				schemaInfoDao.updateSchemaFlagBySchemaId(schemaId);
			} else {
				TreeInfo aTreeInfo = treeInfoDao.getNodeById(moveToNodeId);

				LOG.info("aStatItem: " + aStatItem);
				LOG.info("aTreeInfo: " + aTreeInfo);

				if (aStatItem == null) {
					throw new IllegalArgumentException("统计项不存在！");
				}

				aStatItem.setNodeId(aTreeInfo.getNodeId());
				LOG.info("new aStatItem: " + aStatItem);

				if (aTreeInfo.getNodeId() < 0) {
					aTreeInfo.setNodeId(0);
				}

				boolean bUpdateNode = false;
				if (aStatItem.getNodeId() != 0) {
					TreeInfo aInfo = ensureNodeExistsAndAuthed(aStatItem
							.getNodeId());
					if (!(aInfo.getIsLeaf() != 0)) {
						throw new IllegalArgumentException("非叶子节点不能绑定统计项！");
					}
					if (aInfo.getIsLeaf() == 2) {
						bUpdateNode = true;
					}
				} 
				if (bUpdateNode) {
					TreeInfo tree = new TreeInfo();
					tree.setNodeId(aStatItem.getNodeId());
					tree.setIsLeaf(0);
					tree.setGameId(gameId);
					treeInfoDao.updateNode(tree);
				}
				
				//if(aStatItem.getFlag() == 1) {
				aStatItem.setFlag(0);
				LOG.info(" aStatItem: " + aStatItem);
				// 根据r_id更新统计项信息
				schemaInfoDao.updateSchemaNodeInfo(aStatItem);
			}
		}
	}

	// 搜索节点

	@Override
	public List<TreeInfo> searchNode(Integer gameId, String nodeName) {
		LOG.info("gameID:" + gameId + "nodeName:" + nodeName);
		if (nodeName == null) {
			LOG.error("nodeName is null");
			return null;
		}

		return treeInfoDao.searchNodeName(gameId, nodeName);
	}

	@Override
	public List<SchemaInfo> getSchemaInfoContentList(Integer nodeId,
			Integer gameId) {
		// TODO Auto-generated method stub
		List<SchemaInfo> schemaInfoList = new ArrayList<SchemaInfo>();
		if(nodeId == 0) {
			schemaInfoList = schemaInfoDao.getTrashInfos(gameId);
		} else {
			schemaInfoList = schemaInfoDao
					.getSchemaInfoContentList(nodeId, gameId);
		}
		
		List<SchemaInfo> schemaInfos = new ArrayList<>();
		for (SchemaInfo schemaInfo : schemaInfoList) {
			if (schemaInfo.getCascadeFields().equals("")) {
				schemaInfos.add(schemaInfo);
			}
		}
		return schemaInfos;
	}

	@Override
	public List<SchemaInfo> getSchemaInfoContentListItem(Integer nodeId,
			Integer gameId) {
		// TODO Auto-generated method stub
		List<SchemaInfo> schemaInfoList = new ArrayList<SchemaInfo>();
		if(nodeId == 0) {
			schemaInfoList = schemaInfoDao.getTrashInfos(gameId);
		} else {
			schemaInfoList = schemaInfoDao.getSchemaInfoContentList(nodeId, gameId);
		}
	
		List<SchemaInfo> schemaInfoItems = new ArrayList<>();
		for (SchemaInfo schemaInfo : schemaInfoList) {
			if (!schemaInfo.getCascadeFields().equals("")) {
				schemaInfoItems.add(schemaInfo);
			}
		}
		return schemaInfoItems;
	}

	@Override
	public List<ArtifactInfo> getArtifactInfoContentList(Integer nodeId,
			Integer gameId) {
		// TODO Auto-generated method stub
		return artifactInfoDao.getArtifactInfoContentList(nodeId, gameId);
	}

	// 通过schemaId修改t_schema_info中的node_id

	public void updateSchemaNodeInfo(SchemaInfo schemaInfo) {
		// 仅修改schemaId
		schemaInfo.setFlag(0);
		schemaInfoDao.updateSchemaNodeInfo(schemaInfo);
	}

	/*@Override
	public List<SchemaInfo> getTrashInfos(Integer nodeId, Integer gameId) {
		// TODO Auto-generated method stub
		if(nodeId == 0) {
			return schemaInfoDao.getTrashInfos(gameId);
		}
		return null;
	}*/

	public Integer insertTreeInfoBycustom(String stid, String sstid,
			Integer gameId, Integer logId) {
		// 4.插入t_web_tree 获取node_id
		// 【node_name【stid/sstid】 game_id parent_id is_leaf=1 是叶子节点 is_basic=0
		// hide=0 status=0 display_order=0】
		// 先检测stid是否存在
		Integer nodeId = 0;
		TreeInfo stidTreeInfo = treeInfoDao.getTreeInfosBynameGameId(gameId,
				stid, 0);
		TreeInfo sstidtree = new TreeInfo();
		if (stidTreeInfo == null || stidTreeInfo.equals("")) {
			// stid是新的，新建树节点
			TreeInfo stidtree = new TreeInfo();
			stidtree.setGameId(gameId);
			stidtree.setNodeName(stid);
			stidtree.setParentId(0);
			stidtree.setIsLeaf(0); // 非叶子节点
			stidtree.setIsBasic(0);
			treeInfoDao.insertTreeInfo(stidtree); // 这个接口需要调整
			nodeId = stidtree.getNodeId();
			if (nodeId == 0) {
				return -1;
			}

			sstidtree.setNodeName(sstid);
			sstidtree.setGameId(gameId);
			sstidtree.setParentId(nodeId);
			sstidtree.setIsLeaf(1); // 叶子节点
			sstidtree.setIsBasic(0);
			treeInfoDao.insertTreeInfo(sstidtree);
			if (sstidtree.getNodeId() == 0) {
				return -1;
			}
			// return sstidtree.getNodeId();
		} else {
			// stid不是新的 sstid是新的
			TreeInfo sstidTreeInfo = treeInfoDao.getTreeInfosBynameGameId(
					gameId, sstid, stidTreeInfo.getNodeId());
			if (sstidTreeInfo == null || sstidTreeInfo.equals("")) {
				sstidtree.setNodeName(sstid);
				sstidtree.setGameId(gameId);
				sstidtree.setParentId(stidTreeInfo.getNodeId());
				sstidtree.setIsLeaf(1); // 叶子节点
				sstidtree.setIsBasic(0);
				treeInfoDao.insertTreeInfo(sstidtree);
				if (sstidtree.getNodeId() == 0) {
					return -1;
				}
			}
		}
		return sstidtree.getNodeId();
	}

	// //////////////////////////////////////////////////////////////////////////
	/*
	 * @Override public List<CollectInfo> getCollectListByFavorId(Integer
	 * favorId, Integer userId) { // TODO Auto-generated method stub return
	 * collectInfoDao.getListByFavorIdBySelf(favorId, userId); }
	 */

	/*@Override
	public boolean changeDefault(Integer favorId) {
		// TODO Auto-generated method stub
		FavorInfo favorInfo = getFavorByfavorId(favorId);
		if (favorInfo == null) {
			LOG.info("該收藏不存在");
			return false;
		}
		favorInfoDao.updateFavorByUserId(favorInfo.getUserId(), 0);
		favorInfoDao.updateFavorByfavorId(favorId, 1);
		return true;
	}

	public FavorInfo getFavorByfavorId(Integer favorId) {
		return favorInfoDao.getFavorByfavorId(favorId);
	}

	@Override
	public boolean cancelDefault(Integer favorId) {
		// TODO Auto-generated method stub
		FavorInfo favorInfo = getFavorByfavorId(favorId);
		if (favorInfo == null) {
			LOG.info("該收藏不存在");
			return false;
		}
		favorInfoDao.updateFavorByfavorId(favorId, 0);
		return true;
	}
//////////////////////////////////////////////////////////////////////
	@Override
	public List<FavorInfo> getListByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return favorInfoDao.getListByUserId(userId);
	}
	
	@Override
	public FavorInfo getListByUserGameId(Integer userId, Integer gameId) {
		// TODO Auto-generated method stub
		return favorInfoDao.getListByUserGameId(userId,gameId);
	}

	//根據權限進行更改
	@Override
	public FavorInfo addDefaultFavor(Integer userId) {
		// TODO Auto-generated method stub
		FavorInfo favorInfo = new FavorInfo();
		favorInfo.setFavorName("我的收藏");
		favorInfo.setLayout(1);
		favorInfo.setIsDefault(0);
		favorInfo.setFavorType(2);
		favorInfo.setGameId(2); // use the first authorized game as default
								// 需查询用户权限下的第一个授权的游戏 暂时默认是赛尔号
		favorInfo.setUserId(userId);
		favorInfoDao.insertFavorInfo(favorInfo);
		return favorInfo;
	}
//////////////////////////////////////////////////////////////////////
	// 默认传递的参数 bCountMetadata=true //Count metadata or not

	@Override
	public CollectInfo checkCollectExists(Integer collectId) {
		// TODO Auto-generated method stub
		CollectInfo collectInfo = collectInfoDao.getCollectById(collectId);
		if (collectInfo == null) {
			LOG.info("小部件不存在");
		}
		return collectInfo;
	}

	@Override
	public List<SharedCollectInfo> findByCollectId(Integer collectId) {
		// TODO Auto-generated method stub
		return sharedCollectInfoDao.findByCollectId(collectId);
	}

	
	 * @Override public List<Metadata> getListByCollectId(Integer collectId) {
	 * // TODO Auto-generated method stub return
	 * metadataDao.getListByCollectId(collectId); }
	 

	@Override
	public boolean deleteCollect(Integer collectId) {
		// TODO Auto-generated method stub
		CollectInfo collectInfo = collectInfoDao.getCollectById(collectId);
		if (collectInfo == null) {
			LOG.info("collectId找不到對應的小部件");
			return false;
		}
		collectInfoDao.deleteCollectByCollectId(collectId);
		// deleteAllByAttributes
		return false;
	}

	@Override
	public boolean moveCollectInfo(Integer collectId, Integer favorId,
			Integer userId) {
		// TODO Auto-generated method stub
		CollectInfo collectInfo = collectInfoDao.getCollectById(collectId);
		if (collectInfo == null) {
			LOG.info("小部件不存在");
		}
		if (favorId == collectInfo.getCollectId()) {
			return true;
		}
		if (userId == collectInfo.getUserId()) {
			return true;
		}

		return false;
	}*/

	// //////////////////////////////////////////////////////////////////////////////////////////

	public Integer getStidSStidRefLogDiaryBylastId() {
		Integer diaryId = 0;
		StidSStidRefLogDiary stidSStidRefLogDiary = stidSStidRefLogDiaryDao
				.getStidSStidRefLogDiaryBylastId();
		if (stidSStidRefLogDiary == null || stidSStidRefLogDiary.equals("")) {
			throw new IllegalArgumentException(
					"stidSStidRefLogDiary can not null!");
		}
		diaryId = stidSStidRefLogDiary.getDiaryId();
		return diaryId;
	}

	@Override
	public List<StidSStidRefLogDiary> getStidSStidRefLogDiaryBydiaryId(
			Integer diaryId) {
		// TODO Auto-generated method stub
		List<StidSStidRefLogDiary> stidSStidRefLogDiary = null;

		Integer diary_id = getStidSStidRefLogDiaryBylastId();
		if (diaryId < diary_id) {
			stidSStidRefLogDiary = stidSStidRefLogDiaryDao
					.getStidSStidRefLogDiaryBydiaryId(diaryId);
		}
		return stidSStidRefLogDiary;
	}

	@Override
	public List<StidSStidRefLog> getStidSStidRefLogBystatus(Integer status) {
		
		LOG.info("getting StidSStidRefLogs list with status:" + status);
//		if (status != 0 && status != 1) {
//			throw new IllegalArgumentException("status 非法");
//		}
		List<StidSStidRefLog>  stidSStidRefLog = stidSStidRefLogDao.getStidSStidRefLogBystatus(status);
		LOG.info("finish getting StidSStidRefLog list "+ stidSStidRefLog +" with status:"+ status);
		return stidSStidRefLog;
		
	}
	
	@Override
	public List<StidSStidRefLog> getStidSStidRefLogFromRedis(String pattern) {
		List<StidSStidRefLog> rt = new LinkedList<StidSStidRefLog>();
		RedisUtil redis = RedisUtil.getInstance();
		Map<String,String> sss2logids = redis.jedisMget(pattern);
		for(String sssgidop:sss2logids.keySet()){
			String logid = sss2logids.get(sssgidop);
			if(logid != null && !logid.equals("")){
				StidSStidRefLog s = genStidSStidRefLog(sssgidop.substring(15),Integer.valueOf(logid));
				if(s != null){
					rt.add(s);
				}
			}
		}
		return rt;
	}

	private StidSStidRefLog genStidSStidRefLog(String sssgidopStr,Integer logID) {
		if(sssgidopStr == null || sssgidopStr.equals("")||logID == null)throw new IllegalArgumentException();
		String[] items = sssgidopStr.split("\\|");
		if(items != null && items.length>=4){
			StidSStidRefLog s = new StidSStidRefLog();
			s.setStid(items[0]);
			s.setSstid(items[1]);
			s.setGameId(Integer.valueOf(items[2]));
			String op = "";
			for(int i = 3;i<items.length;i++){
				op += items[i];
			}
			if(op.equals("") || op.equals("null"))op=null;
			s.setOp(op);
			s.setLogId(logID);
			return s; 
		}
		return null;
	}

	//Get collect list by favor_id.
	/*@Override
	public List<CollectShareInfo> getListByFavorId(Integer favorId, Integer userId, boolean countMetaNumber) {
		// TODO Auto-generated method stub
		return sharedCollectInfoDao.getListByFavorId(favorId, userId, countMetaNumber);
	}
	
	//Get collect list by favor_id.
	@Override
	public List<CollectShareInfo> getListByFavorId(Integer favorId) {
		
		// TODO Auto-generated method stub
		return sharedCollectInfoDao.getListByFavorIdByShared(favorId);
	}*/

	/////////////////////////////////////////////////////////////////////////////////////
	@Override
	public List<PlatformInfo> getPlatFormInfosByGameId(Integer gameId) {
		// TODO Auto-generated method stub
		List<PlatformInfo> platformInfos = serverGPZSInfoDao.getPlatFormInfosByGameId(gameId);
		if(platformInfos == null) {
			return null;
		}
		
		List<PlatformInfo> pInfoResult = new  ArrayList<PlatformInfo>(); 
		pInfoResult.clear();
		
		HashSet<Integer> pInfos = new HashSet<>();
        for (PlatformInfo pf : platformInfos) {
        	System.out.println("pInfos:" + pf);
        	//HashSet中的add方法会返回一个Boolean值，如果插入的值已经存在，则直接返回false
        	boolean add = pInfos.add(pf.getPlatformId());
           if(add){
        	   pInfoResult.add(pf);
           }
       }
        
        System.out.println("pInfos.size: "+pInfos.size());
        System.out.println("pInfoResult.size: "+pInfoResult.size());
		return pInfoResult;
	}

	@Override
	public List<ZSInfo> getZSInfosByGPId(Integer gameId, Integer platformId) {
		// TODO Auto-generated method stub
		List<ZSIdInfos> zsIdInfos = serverGPZSInfoDao.getZSInfosByGPId(gameId, platformId);
		if(zsIdInfos == null || zsIdInfos.equals("")) {
			return null;
		}
		
		List<ZSInfo> zsInfoReuslt = new ArrayList<ZSInfo>();
		zsInfoReuslt.clear();
		for(ZSIdInfos zs : zsIdInfos) {
			//zoneServerId用_分隔，例如1区全服(1_-1), 1区1服(1_1)
			ZSInfo zsInfo = new ZSInfo();
			String zoneServerId = (zs.getZoneId()+"_"+zs.getsId()).toString();
			zsInfo.setZoneServerId(zoneServerId);
			zsInfo.setZoneServerName(zs.getZoneServerName());
			zsInfoReuslt.add(zsInfo);
		}
		zsIdInfos.clear();
 		return zsInfoReuslt;
	}

	@Override
	public Integer getServerIDByGPZS(Integer gameId, Integer platformId,Integer zoneId, Integer sId) {
		return serverGPZSInfoDao.getServerIDByGPZS(gameId, platformId, zoneId, sId);
	}

	@Override
	public Integer getServerIDByGPZSForStorm(Integer gameId, Integer platformId,Integer zoneId, Integer sId,Integer expireSeconds){
		if(gameId == null || platformId == null || zoneId == null || sId == null)throw new IllegalArgumentException("Error:gameId/platformId/zoneId/sId could not be null!");
		Integer serverIDFromRedis = getServerIDByGPZSFromRedis(gameId,platformId,zoneId,sId);
		if(serverIDFromRedis != null){
			return serverIDFromRedis;
		}
		Integer serverIDFromDB = getServerIDByGPZS(gameId,platformId,zoneId,sId);
		if(serverIDFromDB != null){
			try{
				loadGPZS2ServerIDToRedis(gameId,platformId,zoneId,sId,serverIDFromDB,expireSeconds);
			}catch(Exception e){
				e.printStackTrace();
				return serverIDFromDB;
			}
			return serverIDFromDB;
		}
		Integer serverIDinserted = insertServerInfosByGPZS(gameId,platformId,zoneId,sId,getPName(platformId),getZSName(zoneId,sId));
		if(serverIDinserted != null){
			try{
				loadGPZS2ServerIDToRedis(gameId,platformId,zoneId,sId,serverIDinserted,expireSeconds);
			}catch(Exception e){
				e.printStackTrace();
				return serverIDinserted;
			}
		}
		return serverIDinserted;
	}
	
	private void loadGPZS2ServerIDToRedis(Integer gameId, Integer platformId,Integer zoneId, Integer sId, Integer serverID,Integer expireSeconds) {
		if(gameId == null || platformId == null ||zoneId == null || sId == null || serverID == null)throw new IllegalArgumentException("Error:gpzs and serverID could not be null!");
		RedisUtil redis = RedisUtil.getInstance();
		redis.jedisSet(String.format("%s_%s|%s|%s|%s","gpzs2serverid", gameId,platformId,zoneId,sId),serverID.toString());
		if(expireSeconds != null){
			redis.jedisExpire(String.format("%s_%s|%s|%s|%s","gpzs2serverid", gameId,platformId,zoneId,sId),expireSeconds);
		}
	}

	private String getPName(int pid){
		if(pid == -1){
			return "全平台";
		}else{
			return pid+"平台";
		}
	}
	
	private String getZSName(int zid,int sid){
		if(zid == -1 && sid == -1){
			return "全区全服";
		}else if(zid == -1){
			return sid+"服";
		}else if(sid == -1){
			return zid+"区";
		}else{
			return zid+"区"+sid+"服";
		}
	}
	
	private Integer getServerIDByGPZSFromRedis(Integer gameId,Integer platformId, Integer zoneId, Integer sId) {
		if(gameId == null || platformId == null ||  zoneId == null ||  sId == null)throw new IllegalArgumentException("Error:gpzs could not be null!");
		RedisUtil redisUtil = RedisUtil.getInstance();
		String strServerIDFromRedis = redisUtil.jedisGet(String.format("%s_%s|%s|%s|%s","gpzs2serverid",gameId,platformId,zoneId,sId)); 
		if(strServerIDFromRedis != null){
			return Integer.valueOf(strServerIDFromRedis);
		}else{
			return null;
		}
	}

	public void insertServerNameInfos(Integer serverId,Integer gameId, String pName, String zsName) {
		serverNameInfoDao.insertServerNameInfo(serverId, gameId, pName, zsName);
	}
	
	@Override
	public Integer insertServerInfosByGPZS(Integer gameId, Integer platformId,Integer zoneId, Integer sId, String pName, String zsName) {
		Integer serverId = serverGPZSInfoDao.getServerIDByGPZS(gameId, platformId, zoneId, sId);
		if((null == serverId) || serverId.equals("")) {
			serverGPZSInfoDao.insertServerGPZSInfo(gameId, platformId, zoneId, sId);
			serverId = serverGPZSInfoDao.getServerIDByGPZS(gameId, platformId, zoneId, sId);
			System.out.print("serverId: insertServerInfosByGPZS" + serverId);
			insertServerNameInfos(serverId, gameId, pName, zsName);
		}
		return serverId;
	}

	@Override
	public List<ServerGPZSInfo> getAllServerGpzsInfos() {
		return serverGPZSInfoDao.getAllServerGpzsInfos();
	}

	@Override
	public List<ServerGPZSInfo> getServerGpzsInfosFromRedis(String pattern){
		List<ServerGPZSInfo> rt = new LinkedList<ServerGPZSInfo>();
		RedisUtil redis = RedisUtil.getInstance();
		Map<String,String> gpzs2ServerIDs = redis.jedisMget(pattern);
		for(String gpzs:gpzs2ServerIDs.keySet()){
			String serverID = gpzs2ServerIDs.get(gpzs);
			if(serverID != null && !serverID.equals("")){
				ServerGPZSInfo s = genGpzs2ServerInfo(gpzs.substring(14),Integer.valueOf(serverID));
				if(s != null){
					rt.add(s);
				}
			}
		}
		return rt;
	}
	
	private ServerGPZSInfo genGpzs2ServerInfo(String gpzsStr, Integer serverID) {
		if(serverID == null)throw new IllegalArgumentException("serverID could not be null!");
		ServerGPZSInfo rt = new ServerGPZSInfo();
		String[] gpzs = gpzsStr.split("\\|");
		for(String str:gpzs){
			if(str == null){
				return null;
			}
		}
		rt.setGameId(Integer.valueOf(gpzs[0]));
		rt.setPlatformId(Integer.valueOf(gpzs[1]));
		rt.setZoneId(Integer.valueOf(gpzs[2]));
		rt.setsId(Integer.valueOf(gpzs[3]));
		rt.setServerId(serverID);
		return rt;
	}

	@Override
	public List<ServerGPZSInfo> getAllServerGpzsInfos(Integer status) {
		// TODO Auto-generated method stub
		return serverGPZSInfoDao.getAllServerGpzsInfosByStatus(status);
	}
/*
	@Override
	public FavorInfo getListByTop1UserId(Integer userId) {
		// TODO Auto-generated method stub
		return favorInfoDao.getListByTop1UserId(userId);
	}
*/
	////////////////////////////////////////////////////////////////////
	@Override
	public void deleteDataInfo(DeleteDataInfoParams deleteDataInfoParams) {
		// TODO Auto-generated method stub
		Integer relateId = deleteDataInfoParams.getRelateId();
		Integer type = deleteDataInfoParams.getType();
		String dataName = deleteDataInfoParams.getDataName();
		
		dataInfoDao.deleteDataInfo(relateId, type, dataName);
	}

	@Override
	public DataInfo getDataInfoByUniqueKey(DataInfoUniqueKeyParams params) {
		// TODO Auto-generated method stub
		return dataInfoDao.getDataInfoByUniqueKey(params);
	}

	@Override
	public List<EmailConfigInfo> getEmailConfigAll() {
		// TODO Auto-generated method stub
		return emailConfigDao.queryAll();
	}
	
	@Override
	public Integer insertEmailConfigInfo(EmailConfigInfo info) {
		LOG.info("insert parameter: " + info);
		emailConfigDao.insert(info);
		LOG.info("insert success logId: " + info.getemailId());
		return info.getemailId();
		
	}
	
	@Override
	public Integer deleteEmailConfigByEmailId(Integer emailId) {
		try{
			emailConfigDao.deleteByEmailId(emailId);
			emailDataInfoDao.setRemoveByEmailId(emailId);
		} catch(Exception e) {
			LOG.error("delete EmailInfo failed[emailId="+ emailId +"]: " + e.getMessage());
			return -1;
		}
		return 0;
	}
	
	@Override
	public Integer updateEmailConfigInfo(EmailConfigInfo info) {
		LOG.info("update parameter: " + info);
		Integer ret = -1;
		try{
			ret = emailConfigDao.update(info);
		} catch(Exception e) {
			LOG.error("update EmailConfigInfo failed: " + e.getMessage());
		}
		return ret;
	}
	
	@Override
	public EmailConfigInfo getEmailConfigByEmailId(Integer emailId) {
		return emailConfigDao.queryByEmailId(emailId);
	}
	
	@Override
	public Integer seEmailConfigtStatus(Integer emailId, Integer status) {
		return emailConfigDao.setStatus(emailId, status);
	}
	
	@Override
	public List<EmailDataInfo> getEmailInfoByEmailId(Integer emailId) {
		return emailDataInfoDao.queryByEmailId(emailId);
	}
	
	@Override
	public Integer insertEmailDataInfo(EmailDataInfo info) {
		return emailDataInfoDao.insert(info);
	}
	
	@Override
	public Integer getEmailTemplateId(String templateType) {
		return emailTemplateDao.getEmailTemplateId(templateType);
	}
	
	@Override
	public List<EmailTemplateDataInfo>  getEmailTemplateData(Integer emailTemplateContentId) {
		return emailTemplateDataDao.getByTemplateContentId(emailTemplateContentId);
	}
	
	@Override
	public List<EmailTemplateContentInfo> getEmailTemplateContentInfo(Integer emailTemplateId) {
		return emailTemplateContentDao.getByTemplateId(emailTemplateId);
	}
	
	@Override
	public EmailTemplateContentInfo getEmailTemlateContentInfoById(Integer emailTemplateContentId) {
		//System.out.println("!!!!!!!!!!!!!!emailTemplateContentId:" +emailTemplateContentId);
		return emailTemplateContentDao.getByTemplateContentId(emailTemplateContentId);
	}
	
	@Override
	public List<EmailDataInfo> getEmailInfoByContentId(Integer emailId, Integer emailContentId) {
		return emailDataInfoDao.getbyContentId(emailId, emailContentId);
	}
	
	@Override
	public List<MissionInfo> getMissionList(Integer serverId,Integer start, Integer end,String type) {
		return missionInfoDao.getMissionList(serverId, start, end, type);
	}
	
	@Override
	public List<GameTaskInfo> getGameTaskInfoList(Integer gameId, String type) {
		return gameTaskInfoDao.getList(gameId, type);
	}
	
	@Override 
	public void updateGameTaskName(GameTaskInfo info) {
		gameTaskInfoDao.setName(info);
	}
	
	@Override 
	public void updateGameTaskHide(GameTaskInfo info) {
		gameTaskInfoDao.setHide(info);
	}

	@Override 
	public void updateGameTaskHideAll(Integer gameId, String type, Integer hide) {
		gameTaskInfoDao.setHideAll(gameId, type, hide);
	}
	
	@Override
	public List<MissionInfo> getMissionDetialInfo(Integer serverId,Integer start, Integer end,String sstid) {
		return missionInfoDao.getMissionDetial(serverId, start, end, sstid);
	}

	@Override
	public List<Component> getAllComponentsByIgnored() {
		return componentDao.getAllComponentsByIgnored();
	}
	
	
	/**********************************我的收藏接口实现**********************************************************/
	
	@Override
	public List<CollectInfo> getListByFavorId(Integer favorId, Integer userId,
			boolean bCountMetadata) {
		if (bCountMetadata) {
			return collectInfoDao.getListByFavorIdByCountMetadataSelf(favorId,
					userId);
		}
		return collectInfoDao.getListByFavorIdBySelf(favorId, userId);
	}

	@Override
	public List<CollectInfo> getListByFavorId(Integer favorId) {
		return collectInfoDao.getListByFavorIdByShared(favorId);
	}

	@Override
	public List<SharedCollectInfo> findByCollectId(Integer collectId) {
		return sharedCollectInfoDao.findByCollectId(collectId);
	}

	@Override
	public List<CollectMetadataInfo> getMetadataInfosByCollectId(
			Integer collectId) {
		return collectMetadataDao.getMetadataInfosByCollectId(collectId);
	}

	@Override
	public List<FavorInfo> getListByUserId(Integer userId, Integer favorType, String favorName) {
		return favorInfoDao.getListByUserId(userId, favorType, favorName);
	}

	@Override
	public Integer addDefaultFavor(FavorInfo favorInfo) {
		favorInfoDao.insertFavorInfo(favorInfo);
		// 新添加的主键id并不是在执行添加操作时直接返回的 而是在执行添加操作之后将所添加的主键id字段设置为pojo对象的主键id属性
		return favorInfo.getFavorId();
	}

	@Override
	public CollectInfo getCollectById(Integer collectId) {
		return collectInfoDao.getCollectById(collectId);
	}

	@Override
	public Integer insertSetInfo(FavorSetInfo params) {
		Integer gameId = params.getGameId();
		Integer componentId = params.getComponentId();
		// 问题：https://blog.csdn.net/eleanoryss/article/details/82997899
		Integer setId = favorSetInfoDao.getSetInfoByGameIdComponentId(gameId,
				componentId);
		System.out.println("set_id+++++++++++++" + setId);
		if (setId == null || setId <= 0) {
			favorSetInfoDao.insertSetInfo(params);
			setId = params.getId();
			System.out.println("set_id+++++++++++++" + setId);
		} else {
			// game_id和componentId唯一键 set_id为主键
			params.setId(setId);
			favorSetInfoDao.updateSetInfo(params);
		}
		return setId;
	}

	@Override
	public void insertFavorSetDataInfo(FavorSetDataInfo params) {
		// PRIMARY KEY (`data_id`,`set_id`,`data_expr`)
		Integer dataId = params.getDataId();
		Integer id = params.getId();
		String dataExpr = params.getDataExpr();
		//FavorSetDataInfo favorSetDataIndo = favorSetDataInfoDao.getFavorSetDataInfoByPrimaryKey(dataId, id, dataExpr, params.getGameId());
		//if (favorSetDataIndo == null || favorSetDataIndo.equals("")) {
		favorSetDataInfoDao.insertFavorSetDataInfo(params);
		//}
		
	}

	@Override
	public Integer insertFavorInfo(FavorInfo favorInfo) {
		favorInfoDao.insertFavorInfo(favorInfo);
		return favorInfo.getFavorId();
	}

	@Override
	public boolean updateFavorInfoByFavorId(FavorInfo favorInfo) {
		Integer favorId = favorInfo.getFavorId();
		FavorInfo favor = favorInfoDao.getFavorByfavorId(favorId);
		if (favor == null || favor.equals("")) {
			return false;
		}
		String favorName = favorInfo.getFavorName();
		Integer layout = favorInfo.getLayout();
		favorInfoDao.updateFavorByFavorNameLayOut(favorId, favorName, layout);
		return true;
	}

	@Override
	public Integer insertCollectInfo(CollectInfo collectInfo) {
		collectInfoDao.insertCollectInfo(collectInfo);
		return collectInfo.getCollectId();
	}

	@Override
	public FavorSetDataInfo getFavorSetDataInfoByPrimaryKey(Integer dataId,
			Integer id, String dataExpr, Integer gameId) {
		FavorSetDataInfo favorInfoSetData = favorSetDataInfoDao.getFavorSetDataInfoByPrimaryKey(dataId, id, dataExpr, gameId);
		if (favorInfoSetData == null || favorInfoSetData.equals("") || favorInfoSetData.getId() == null || favorInfoSetData.getId().equals("")) {
			System.out.println("getFavorSetDataInfoByPrimaryKey null" + favorInfoSetData);
			return null;
		}
		System.out.println("getFavorSetDataInfoByPrimaryKey" + favorInfoSetData);
		return favorInfoSetData;
	}

	@Override
	public Integer insertCollectMetadataInfo(
			CollectMetadataInfo collectMetadataInfo) {
		 Integer column = collectMetadataDao.insertCollectMetadataInfo(collectMetadataInfo);
		 return column;
	}

	@Override
	public CollectMetadataInfo getCollectMetadataInfoByPrimaryKey(
			Integer dataId, Integer gpzsId, Integer collectId, String dataExpr) {
		return collectMetadataDao.getCollectMetadataInfoByPrimaryKey(dataId, gpzsId, collectId, dataExpr);
	}

	

	@Override
	public Integer updateFavor(Integer favorId, String favorName, Integer layOut) {
		FavorInfo favor = favorInfoDao.getFavorByfavorId(favorId);
		if (favor == null || favor.equals("")) {
			return -1;
		}
		favorInfoDao.updateFavorByFavorNameLayOut(favorId, favorName, layOut);
		return 0;
	}

	@Override
	public Integer deleteFavor(Integer favorId, Integer userId) {
		FavorInfo favor = favorInfoDao.getFavorByfavorId(favorId);
		if (favor == null || favor.equals("")) {
			return -1;
		}
		//t_web_shared_collect  t_web_collect t_web_collect_metadata_ada
		favorInfoDao.deleteFavorByfavorId(favorId);
		List<CollectInfo> collectInfos = getListByFavorId(favorId, userId, true);
		List<CollectInfo> sharedCollectInfos = getListByFavorId(favorId);
		System.out.println(collectInfos);
		System.out.println(sharedCollectInfos);
		boolean coInfos = collectInfos.addAll(sharedCollectInfos);
		System.out.println("collectInfos" + collectInfos);
		
		for (CollectInfo cInfo : collectInfos) {
			Integer collectId = cInfo.getCollectId();
			collectInfoDao.deleteCollectByCollectId(collectId);
			collectMetadataDao.deleteMetadataByCollectId(collectId);
			sharedCollectInfoDao.deleteShareInfoByCollectId(collectId);
		}
		
		sharedCollectInfoDao.deleteShareInfoByFavorId(favorId);
		return 0;
	}

	@Override
	public Integer updateFavorDefault(Integer favorId, Integer userId, Integer isDefault) {
		FavorInfo favor = favorInfoDao.getFavorByfavorId(favorId);
		if (favor == null || favor.equals("")) {
			return -1;
		}
		return favorInfoDao.updateFavorDefault(favorId, userId, isDefault);
		
	}

	@Override
	public Integer updateDefaultFavorByfavorId(Integer favorId) {
		FavorInfo favor = favorInfoDao.getFavorByfavorId(favorId);
		if (favor == null || favor.equals("")) {
			return -1;
		}
		Integer isDefault = 0;
		return favorInfoDao.updateDefaultFavorByfavorId(favorId, isDefault);
		
	}

	@Override
	public FavorInfo getFavorByfavorId(Integer favorId) {
		return favorInfoDao.getFavorByfavorId(favorId);
	}

	@Override
	public SharedCollectInfo getShareInfoByPrimaryId(Integer favorId,
			Integer collectId) {
		return sharedCollectInfoDao.getShareInfoByPrimaryId(favorId, collectId);
	}

	@Override
	public Integer deleteShareInfoByPrimaryId(Integer favorId, Integer collectId) {
		return sharedCollectInfoDao.deleteShareInfoByPrimaryId(favorId, collectId);
	}

	@Override
	public Integer insertShareCollectInfo(SharedCollectInfo sInfo) {
		 sharedCollectInfoDao.insertShareCollectInfo(sInfo);
		 return sInfo.getFavorId();
	}

	@Override
	public Integer deleteCollectByCollectId(Integer collectId) {
		return collectInfoDao.deleteCollectByCollectId(collectId);
	}

	@Override
	public void deleteShareInfoByCollectUserId(Integer collectId, Integer userId) {
		sharedCollectInfoDao.deleteShareInfoByCollectUserId(userId, collectId);
	}

	@Override
	public Integer deleteCollectMetadataInfo(Integer collectId) {
		return collectMetadataDao.deleteMetadataByCollectId(collectId);
	}

	@Override
	public void updateCollectInfoBycollectId(Integer collectId, Integer favorId) {
		collectInfoDao.updateCollectInfoBycollectId(collectId, favorId);
	}

	@Override
	public void updateShareCollectInfoBycollectId(Integer collectId,
			Integer srcFavorId, Integer descFavorId) {
		sharedCollectInfoDao.updateShareCollectInfoBycollectId(collectId, srcFavorId, descFavorId);
	}

	@Override
	public List<SharedCollectInfo> getShareInfoByFavorId(Integer favorId,
			Integer userId) {
		return sharedCollectInfoDao.getShareInfoByFavorId(favorId, userId);
	}

	@Override
	public void updateCollectNameByCollectId(Integer collectId,
			String collectName) {
		collectInfoDao.updateCollectNameByCollectId(collectId, collectName);
	}

	@Override
	public void updateCollectMetadataOrderByPrimaryKey(Integer dataId,
			Integer gpzsId, Integer collectId, String dataExpr,
			Integer displayOrder) {
		collectMetadataDao.updateCollectMetadataOrderByPrimaryKey(dataId, gpzsId, collectId, dataExpr, displayOrder);
	}

	@Override
	public List<ServerNameInfo> getServerNameInfosByGameId(Integer gameId) {
		return serverNameInfoDao.getServerNameInfosByGameId(gameId);
	}

	
	@Override
	public void updateServerPNameByServerId(Integer serverId, String pname) {
		serverNameInfoDao.updateServerPNameByServerId(serverId, pname);
	}

	@Override
	public void updateServerZSNameByServerId(Integer serverId, String zsname) {
		serverNameInfoDao.updateServerZSNameByServerId(serverId, zsname);
	}

	@Override
	public void updateServerNameStatusByServerId(Integer serverId, Integer hide) {
		serverNameInfoDao.updateServerNameStatusByServerId(serverId, hide);
		serverGPZSInfoDao.updateServerGPZSStatusByServerId(serverId, hide);
	}

	@Override
	public List<ServerAllPlatform> getAllPlatByGameId(Integer gameId) {
		
		return serverGPZSInfoDao.getAllPlatByGameId(gameId);
	}

	@Override
	public List<WhaleUserInfo> getListWhaleUserInfos(Integer gameId, Integer platFormId, String accountId) {
		return whaleUserInfoDao.getListWhaleUserInfos(gameId, platFormId, accountId);
	}

	@Override
	public List<WhaleUserMonthInfo> getListWhaleUserMonthInfos(Integer gameId, Integer time, Integer platFomrId, Integer topNum) {
		if (platFomrId == -1) {
		    if (serverGPZSInfoDao.getPlatFormInfosByGameId(gameId).size() > 0) {
		        //该游戏有多个平台
                return whaleUserMonthDao.getMultiPlatFormWhaleInfos(gameId, time, topNum);
            }
        }
        return whaleUserMonthDao.getPlatFormWhaleInfosByPlatFormId(gameId, time, platFomrId, topNum);
	}

	/********************************************************************************************/
	
	//friendly
	
	//道具销售top10
	@Override
	public List<EconomyInfo> getItemSaleTop10(String sstid,
			Integer vip, String timeFrom, String timeTo, Integer server_id) {
		return economyInfoDao.getSaleTop10(sstid, vip, timeFrom, timeTo, server_id);
	}

	@Override
	public List<EconomyInfo> getItemSaleList(String sstid, Integer vip,
			String timeFrom, String timeTo, Integer server_id,Integer start,Integer end) {
		
		return economyInfoDao.getItemSaleList(sstid, vip, timeFrom, timeTo, server_id,start,end);
	}

	@Override
	public List<EconomyInfo> getItemSaleDetail(String sstid, Integer vip,
			String timeFrom, String timeTo, Integer server_id,String item_id) {
		return economyInfoDao.getItemSaleDetail(sstid, vip, timeFrom, timeTo, server_id,item_id);
	}

	@Override
	public Integer getItemSaleListTotal(String sstid, Integer vip,
			String timeFrom, String timeTo, Integer server_id) {
		return economyInfoDao.getItemSaleListTotal(sstid, vip, timeFrom, timeTo, server_id);
	}

	/*@Override
	public void dropTemTableByName(String tableName) {
		economyInfoDao.dropTemTableByName(tableName);
	}

	@Override
	public void createTemporaryTable(Integer parent_id) {
		economyInfoDao.createTemporaryTable(parent_id);
	}*/
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public List<CategoryInfo> getCategorySaleList(Integer server_id, String sstid,
			String timeFrom, String timeTo,Integer start,Integer end,String tableName,Integer parent_id) {
		economyInfoDao.dropTemTableByName(tableName);
		economyInfoDao.createTemporaryTable(parent_id);
		return economyInfoDao.getCategorySaleList(server_id, sstid, timeFrom, timeTo,start,end);
	}

	@Override
	public Integer getCategorySaleListTotal(Integer server_id,
			String sstid, String timeFrom, String timeTo,String tableName,Integer parent_id) {
		economyInfoDao.dropTemTableByName(tableName);
		economyInfoDao.createTemporaryTable(parent_id);
		return economyInfoDao.getCategorySaleListTotal(sstid, timeFrom, timeTo, server_id);
	}

	@Override
	public List<CategoryInfo> getCategorySaleDetail(Integer category_id,String sstid,Integer server_id,String timeFrom,String timeTo) {
		return economyInfoDao.getCategorySaleDetail(category_id,sstid,server_id, timeFrom, timeTo);
	}


	/*@Override
	public List<SchemaInfo> getRefreshedSchemaInfosByLogIdForStorm(
			Integer logId, Boolean writeToRedis, Integer redisExpireTime) {
		// TODO Auto-generated method stub
		return null;
	}*/

	@Transactional(rollbackFor=Exception.class)
	public List<ItemInfo> getItemListIfGameIdIs2(String sstid, Integer game_id,
			Integer is_leaf,Integer start,Integer end) {
		
		economyInfoDao.createCat1();
		economyInfoDao.InitializeCat1(sstid, game_id, is_leaf);
		
		return economyInfoDao.getItemInfoListIfGameIdIs2(sstid, game_id,start,end);
	}
	@Transactional(rollbackFor=Exception.class)
	public Integer getItemListTotalIfGameIdIs2(String sstid, Integer game_id,
			Integer is_leaf) {
		
		economyInfoDao.createCat1();
		economyInfoDao.InitializeCat1(sstid, game_id, is_leaf);
		
		return economyInfoDao.getItemInfoListTotalIfGameIdIs2(sstid, game_id);
	}
	@Override
	public List<ItemInfo> getItemList(String sstid, Integer game_id,
			Integer is_leaf,Integer start,Integer end) {
		return economyInfoDao.getItemInfoList(sstid, game_id, is_leaf,start,end);
	}
	@Override
	public Integer getItemListTotal(String sstid, Integer game_id,
			Integer is_leaf) {
		return economyInfoDao.getItemInfoListTotal(sstid, game_id, is_leaf);
	}
	@Override
	public Integer setCategoryName(String sstid, Integer game_id,
			String item_id, String item_name, Integer hide) {
		return economyInfoDao.replaceItemInfo(sstid, game_id, item_id, item_name, hide);
	}

	@Override
	public Integer setHide(String sstid, Integer game_id, String item_id,
			String item_name, Integer hide) {
		return economyInfoDao.replaceItemInfo(sstid, game_id, item_id, item_name, hide);
	}

	@Override
	public List<ItemCategoryInfo> getItemCategory(String item_id, String sstid,
			Integer game_id, Integer is_leaf) {
		return economyInfoDao.getItemCategory(item_id, sstid, game_id, is_leaf);
	}

	@Override
	public List<ItemCategoryInfo> getCategoryList(Integer parent_id,
			String sstid, Integer game_id) {
		return economyInfoDao.getCategoryList(parent_id, sstid, game_id);
	}

	@Override
	public Integer setItemCategory(Integer category_id, String sstid,
			Integer game_id, String item_id, Integer ref_count) {
		return economyInfoDao.setItemCategory(category_id, sstid, game_id, item_id, ref_count);
	}

	@Override
	public void deleteItemCategory(String item_id, String sstid,
			Integer game_id) {
		economyInfoDao.deleteItemCategory(item_id, sstid, game_id);
	}

	@Override
	public void updateItemCategory(Integer category_id, String category_name) {
		economyInfoDao.updateItemCategory(category_id, category_name);
	}

	@Override
	public void insertItemCategory(String category_name,
			String sstid, Integer game_id, Integer parent_id, Integer is_leaf) {
		economyInfoDao.insertItemCategory( category_name, sstid, game_id, parent_id, is_leaf);
	}

	@Override
	public void delCategory(Integer category_id, String sstid, Integer game_id) {
		economyInfoDao.delCategory(category_id, sstid, game_id);
	}

	@Override
	public void moveCategory(Integer parent_id, Integer category_id,
			String sstid, Integer game_id) {
		// TODO Auto-generated method stub
		
	}

	//friendly


	/****************************修复数据****************************************/
	/**
	 * 修复t_stid_logId表中缺失的数据
	 * @param logId
	 * @param gameId
	 * @param stid
	 * @param sstid
	 * @param op
	 * @return  错误码
	 */
	private Integer fixStidLogInfo(Integer logId, Integer gameId, String stid, String sstid, String op) {
		System.err.println("===========fix stidLogInfo start============");
		StidSStidRefLog logIdBySStidGameId = null;
		try {
			logIdBySStidGameId = stidSStidRefLogDao.getLogIdBySStidGameId(stid, sstid, gameId, op);
		} catch (Exception e) {
			System.err.println("fixStidLogInfo: " + logId + "," + gameId + "," + stid + "," + sstid + "," + op);
			System.err.println(e.getMessage());
			e.printStackTrace();
			return -1;
		}

		if (logIdBySStidGameId == null || logIdBySStidGameId.equals("")) {
			StidSStidRefLog stidSStidRefLog = new StidSStidRefLog();
			stidSStidRefLog.setLogId(logId);
			stidSStidRefLog.setSstid(sstid);
			stidSStidRefLog.setStid(stid);
			stidSStidRefLog.setGameId(gameId);
			stidSStidRefLog.setOp(op);
			stidSStidRefLog.setStatus(0);
			stidSStidRefLogDao.insertStidSStidRefLog(stidSStidRefLog);
			return 1;
		}
		System.err.println("===========fix stidLogInfo stop============");
		return 0;
	}


	/**
	 * 修复t_web_tree表中的数据
	 * @param nodeName
	 * @param gameId
	 * @param parentId
	 * @return new nodeId
	 */
	private Integer fixTreeInfo(String nodeName, Integer gameId, Integer parentId) {
		System.err.println("===========fix TreeInfo start============");
		TreeInfo treeInfosBynameGameId = null;
		try {
			treeInfosBynameGameId = treeInfoDao.getTreeInfosBynameGameId(gameId, nodeName, parentId);
			System.err.println("treeInfosBynameGameId=="+ treeInfosBynameGameId);
		} catch (Exception e) {
			System.err.println("fixTreeInfo: " + nodeName + "," + gameId + "," + parentId);
			System.err.println(e.getMessage());
			e.printStackTrace();
			return -1;
		}

		if (treeInfosBynameGameId == null || treeInfosBynameGameId.equals("")) {
			TreeInfo treeInfo = new TreeInfo();
			treeInfo.setGameId(gameId);
			treeInfo.setNodeName(nodeName);
			treeInfo.setParentId(parentId);
			treeInfo.setIsBasic(0);
			if (parentId == 0) {
				treeInfo.setIsLeaf(0);
			} else {
				treeInfo.setIsLeaf(1);
			}
			treeInfoDao.insertTreeInfo(treeInfo);
			return treeInfo.getNodeId();
		}
		System.err.println("===========fix Treeinfo stop============");
		return treeInfosBynameGameId.getNodeId();
	}

	private Integer fixSchemaInfo(Integer gameId, String stid, String sstid, String op, Integer nodeId, Integer logId) {
		System.err.println("===========fix schemaInfo start============");
		CustomQueryParams customQueryParams = new CustomQueryParams();
		customQueryParams.setGameId(gameId);
		customQueryParams.setOp(op);
		customQueryParams.setSstid(sstid);
		customQueryParams.setStid(stid);
		System.err.println("customQueryParams: " + customQueryParams);

		try {
			insertSchemaInfoByopAnalyser(customQueryParams, logId, nodeId, 1);
		} catch (Exception e) {
			System.err.println(e.getMessage() + customQueryParams + "," + logId + "," + nodeId);
			e.printStackTrace();
			return -1;
		}
		System.err.println("===========fix schemaInfo stop============");
		return 0;
	}

	/**
	 * 修复数据涉及以下表：
	 * t_log_info  t_stid_logId t_web_tree t_schema_info
	 */
	@Override
	public Integer fixData(Integer logId, Integer gameId, String stid, String sstid, String op) {
		Integer logCode = fixStidLogInfo(logId, gameId, stid, sstid, op);
		if (logCode == -1) {
			System.err.println("第一步error：修复t_stid_logId出现问题" + logId + "," + gameId + "," + stid + "," + sstid + "," + op);
			return -1;
		}
		//修复父节点
		Integer parentNodeId = fixTreeInfo(stid, gameId, 0);
		if (parentNodeId == -1) {
			System.err.println("第二步error：修复t_web_tree的父节点出现问题" + stid + "," + gameId + "," + 0);
			return -1;
		}
		//修复子节点
		Integer childNodeId = fixTreeInfo(sstid, gameId, parentNodeId);
		if (childNodeId == -1) {
			System.err.println("第三步error：修复t_web_tree的父节点出现问题" + stid + "," + gameId + "," + 0 + parentNodeId);
			return -1;
		} else {
			/**
			 * 注：父节点的node_id没有对应的t_schema_info信息
			 * 出现异常的情况：
			 * 1.常规插入失败
			 * 2.schemaInfo记录存在 插入失败
			 */
			Integer schemaCode = fixSchemaInfo(gameId, stid, sstid, op, childNodeId, logId);
			if (schemaCode == -1) {
				System.err.println("第四步error:修复t_schema_info信息有问题" + childNodeId + "," + gameId + ","
						+ stid + "," + sstid + "," + op + "," + logId);
				return -1;
			}
		}
		return 0;
	}

	@Override
	public ArtifactInfo getArtifactInfoByHiveTableName(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

}
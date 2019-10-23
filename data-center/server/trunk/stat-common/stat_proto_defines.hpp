/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_PROTO_DEFINES_HPP
#define STAT_PROTO_DEFINES_HPP

#pragma pack(1)

// 所有协议均以此为开头
struct StatProtoHeader
{
    uint32_t len;      // 协议包长度
    uint32_t proto_id; // 协议号
    char     body[0];
};

// 以下为日志收集时协议定义
// 日志发送：stat-client -> stat-server -> db-server

#define PROTO_BASIC_STATLOG  (0x1000)
#define PROTO_CUSTOM_STATLOG (0x1001)
#define PROTO_PARSE_SERIAL   (0x2000)

#define PROTO_BASIC_STATLOG_OTHER_DAY (0x2000)
#define PROTO_CUSTOM_STATLOG_OTHER_DAY (0x2001)

#define PROTO_CALC_CUSTOM   (0x3000)

// 发送日志时，stat-client发给stat-server的请求包
struct StatLogLineHeader
{
    uint32_t len;      // 协议包长度
    uint32_t proto_id; // 协议号
    uint32_t game_id;  // 游戏id
    uint32_t timestamp;// 日志消息时间戳
    char     body[0];
};

// 发送日志时，stat-server返回给stat-client.
struct StatLogLineRet
{
    uint32_t len;       // 协议包长度 
    uint32_t proto_id;  // 协议号
    uint32_t game_id;   // 游戏id 
    uint32_t timestamp; // 日志消息时间戳 
    uint32_t ret;       //返回值，0表示成功
};

// 解析日志时，db_server给stat-server 的返回包。
struct StatLogItemRet
{
    uint32_t len;        // packet length
    uint32_t proto_id;   // protocol ID
    uint8_t ret;        //返回值，0表示成功
};

// 解析日志时，stat-server给db-server的请求包。
// 统计系统db-server说明及协议文档中定义
struct StatLogItemSerialHeader
{
    uint16_t pkg_len;   // 包长，包括包头的18字节
    uint32_t proto_id;  // 协议号
    uint32_t version;   // 版本号，从0开始
    uint32_t seq_no;    // 消息序列号。服务端原样返回，供客户端匹配返回包用
    uint32_t ret_val;   // 返回值。0为成功，其余为错误码
    char body[0];
};

struct StatLogItemOpSerial
{
    uint8_t op_type;      // op类型
    uint8_t op_field_len; // op_field长度，没有op_field则field_len为0
    char op_filed[0];      // op操作的字段，不包括最后的’\0’
};

#define PROTO_ID_ADD     0x1001
#define PROTO_ID_UPDATE  0x1002
// 注释的代码是C++非法语句，写在此是为了说明协议内容。
struct StatLogItemsSerialAdd     // 新增统计项协议
{
    int32_t pid;          // 平台id
    int32_t zid;          // 区id
    int32_t sid;          // 服id
    uint32_t gid;         //游戏id
    char var[0];
    //uint8_t stid_len;     // stid长度
    //char stid[stid_len];  // stid内容，不包括最后的’\0’
    //uint8_t sstid_len;    // sstid长度
    //char sstid[sstid_len];// sstid内容，不包括最后的’\0’
};

struct StatLogItemsSerialUpdate     // 更新统计项协议.
{
    uint8_t data_type;    // 数据类型：0-分钟，1-小时，2-天
    uint32_t timestamp;   // 时间戳
    double value;         // 值
    int32_t pid;          // 平台id
    int32_t zid;          // 区id
    int32_t sid;          // 服id
    uint32_t gid;         //游戏id
    char var[0];
    //uint8_t stid_len;     // stid长度
    //char stid[stid_len];  // stid内容，不包括最后的’\0’
    //uint8_t sstid_len;    // sstid长度
    //char sstid[sstid_len];// sstid内容，不包括最后的’\0’
    //uint8_t key1_len;
    //char key1[key1_len];
};

// 以下是处理服务模块升级时协议定义
// 检查更新协议号
#define STAT_PROTO_UPDATE (0xA100)
// 下载更新协议号
#define STAT_PROTO_DOWNLOAD (0xA101)

// 注释部分为变长字段
struct StatUpdateHeader
{
    uint32_t len;      // 协议包长度
    uint32_t proto_id; // 协议号
    char     body[0];
    // 以下是检查更新时协议定义
    //uint8_t  os_name_len;
    //char     os_name[os_name_len]; // 操作系统名称， 如debian, centos
    //uint8_t  os_version_len;
    //char     os_version[os_version_len]; // 操作系统版本，如6.0.2
    //uint8_t  module_len;
    //char     module[module_len]; // 服务模块名称：如stat-client
    //uint8_t  module_version_len;
    //char     module_version[module_version_len]; // 服务模块当前版本， 如0.0.1
    //以下是下载更新时时协议定义
    //uint32_t offset; // 下载文件的offset
    //uint16_t path_len;
    //char     path[path_len]; // 下载文件的container路径名
    //uint8_t  module_version_len;
    //char     module_version[module_version_len];
};

// 注释部分为变长字段
struct StatUpdateRet
{
    uint32_t len;
    uint32_t proto_id;
    uint32_t file_size;   // 当检查更新时为文件总大小，file_size=0表示无更新，当下载更新时，为当前传输的文件块大小
    char     body[0];
    //以下字段在检查更新时有效，下载更新时该部分为所传输的文件块
    //uint16_t path_len;
    //char     path[path_len]; // 下载文件的container路径名
    //uint8_t  module_version_len;
    //char     module_version[module_version_len];
};

// 以下是各模块向stat-center注册和心跳协议
#define STAT_PROTO_REGISTER           (0xA001)
#define STAT_PROTO_UNREGISTER         (0xA002)
#define STAT_PROTO_HB_HARDDISK        (0xA003)
#define STAT_PROTO_HB_NOR             (0xA004)
#define STAT_PROTO_HB_NAMENODE        (0xA005)
#define STAT_PROTO_HB_JOBTRACKER      (0xA006)
#define STAT_PROTO_HB_DATANODE        (0xA007)
#define STAT_PROTO_HB_TASKTRACKER     (0xA008)
#define STAT_PROTO_HB_REDIS           (0xA009)
#define STAT_PROTO_HB_PRINT           (0xA010)
#define STAT_PROTO_HB_FD              (0xA011)
#define STAT_PROTO_STAT_SET_HOLIDAY   (0xA012)
#define STAT_PROTO_HB_CUSTOM          (0xA013)

#define STAT_PROTO_NOTUTF8_DB         (0xA014)
#define STAT_PROTO_UPLOAD_INFO        (0xA015)
#define STAT_PROTO_INSERT_STAT_ERROR_CS   (0xA016)
#define STAT_PROTO_STAT_DATA_HOLIDAY  (0xA017)
#define STAT_PROTO_STAT_CALC          (0xA018)
#define STAT_PROTO_REG_ALARM          (0xA019)

#define STAT_PROTO_END                (0xA01a)

struct StatRegAlarm
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  moudule_type;
};

struct StatCalcError
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type;
    uint32_t ip;
    int8_t   status;
    uint8_t  body_len;             
    char     body[0];
};

// UPload上传错误信息
struct StatUploadInfo
{
	uint32_t len;         // 协议包长度
	uint32_t proto_id;    // 协议号 0xA014
	uint8_t  module_type; // 11 stat-upload
	uint32_t ip;          // ip地址
	uint32_t game_id;     // 游戏id
	uint32_t timestamp;   // 日志消息时间戳
	uint32_t basic_cnt;   // 基础项信息条数 
	uint32_t custom_cnt;  // 自定义项信息条数 
	uint32_t e_flag;      // 正常->0 错误->非0
};

struct StatModuleHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                          // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;   // network byte order
    uint16_t port; // network byte order
};

struct StatRegisterHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                          // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;   // network byte order
    uint16_t port; // network byte order
};

struct StatUnRegisterHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 5 stat-hadoop
    uint32_t ip;   // network byte order
};

struct StatHeartbeatHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                          // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;
    char     body[0];
};

// 含硬盘文件系统信息的心跳包
struct StatHeartbeatHdHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                          // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;
    uint64_t wp_size; // working path available size
    uint32_t if_count;// # of files under inbox
    uint64_t if_size; // inbox size
    uint32_t of_count;// # of files under oubox
    uint64_t of_size; // outbox size
    uint32_t sf_count;// # of files under sent 
    uint64_t sf_size; // sent size
};

struct StatNOTUTF8DbHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type;
    uint32_t ip;
    uint32_t num; // number of gameid whose log not utf8
    char     body[0];
};

struct InsertStatErrorCsHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type;
    uint32_t ip;
    uint8_t  error_type; // 1: redis 2: report_id 3: data_id 4: gpzs_id 5: task
};

struct StatAlarmRet
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  ret;
    char     body[0];
};

struct StatHeartbeatRet
{
    uint32_t len;
    uint32_t proto_id;
    int8_t   ret;
    char     body[0]; // 预留，返回stat-center给服务模块的消息，比如需要执行的命令等等，后续可扩展。
};

typedef StatHeartbeatRet StatRegistertRet;
typedef StatHeartbeatRet StatUnRegistertRet;

struct StatHeartbeatPrintHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type;
    uint8_t  print_type; // 0 txt, 1 html
    uint8_t  print_flag; // (0 1 0 1 0 1 0 1)2
                         //  | +-------------> IP flag     : 0 nothing, 1 print_param[] is an IP
                         //  +---------------> Alarm flag  : 0 nothing, 1 just get alarmed msg
    char     print_param[0];
};

struct StatForbidHeader
{
    uint32_t len;
    uint32_t proto_id;
    uint8_t  module_type;
    uint8_t  fbd_flag;   // (- - - - - - - -)
                         //  | +-------------> IP flag      : 0 all ip of module_type, 1 certain ip of module_type
                         //  +---------------> Forbid flag  : 0 start alarm, 1 forbid alarm
    uint32_t ip;
    uint32_t minutes;
};

struct StatSetHolidayHeader
{
    uint32_t len;
    uint32_t proto_id;  // set stat holiday or set data holiday
    int8_t   ret;
    uint32_t num;
    char     body[0];
    //uint8_t  op_flag;          // 1: add holiday  2: add weekday  
    //time_t   day;
};

struct StatSetHolidayRet
{
    uint32_t len;
    uint32_t proto_id;
    int8_t   ret;
};

struct statHeartbeatPrintRet
{
    uint32_t len;
    uint32_t proto_id;
    int8_t   ret;
    char     body[0];
};

struct JobTrackerHeartbeatInfoHeader
{
    int32_t len;
    int32_t proto_id;
    int8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                         // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;
    int32_t active_task_trackers;
    int32_t black_listed_task_trackers;
    int32_t running_map_tasks;
    int32_t max_map_tasks;
    int32_t running_reduce_tasks;
    int32_t max_reduce_tasks;
    int32_t failed;
    int32_t killed;
    int32_t prep;
    int32_t running;
    char    failed_job_id[0];  //最近执行失败的任务id，不为空需要报警
};

struct NameNodeHeartbeatInfoHeader
{
    int32_t len;
    int32_t proto_id;
    int8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                         // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;
    int32_t safe_mode;  //0-OFF, 1-ON
    int64_t configured_capacity; //最大磁盘空间
    int64_t present_capacity;    //可用磁盘空间
    int64_t dfs_remaining;       //剩余磁盘空间
    int64_t dfs_used;            //已用磁盘空间
    float   dfs_used_percent;    //已用磁盘空间占比(0.0-100.0)，大于一定比值需要报警
    int64_t under_replicated_blocks;    //需要拷贝的文件块数量
    int64_t missing_blocks;             //丢失的文件块数量，不为0需要报警
    int32_t total_datanodes;            //datanode数量
    int32_t live_nodes;                 //存活datanode数量
    int32_t dead_nodes;                 //挂掉的datanode数量，不为0需要报警
};

struct DatanodeHeartbeatInfoHeader
{
    int32_t len;
    int32_t proto_id;
    int8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                         // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;
    int64_t present_capacity;    //可用磁盘空间
    int64_t dfs_remaining;       //剩余磁盘空间
    int64_t dfs_used;            //已用磁盘空间
    float   dfs_used_percent;    //已用磁盘空间占比(0.0-100.0)，大于一定比值需要报警
    char    storage_info[0];     //挂载目录
};

struct TasktrackerHeartbeatInfoHeader
{
    int32_t len;
    int32_t proto_id;
    int8_t  module_type; // 0 stat-client, 1 stat-server, 2 db-server, 3 config-server, 4 stat-redis, 
                         // 5 stat-namenode, 6 stat-jobtracker, 7 stat-datanode, 8 stat-tasktracker
    uint32_t ip;
    int32_t maps_running;
    int32_t reduce_running;
    int32_t map_task_slots;
    int32_t reduce_task_slots;
    int32_t task_completed;
};

struct StatCalcCustomHeader 
{
    uint32_t len;
    uint32_t proto_id;
    uint32_t file_id;
};

// stat-server request stid from db-server by msgid
#define STAT_PROTO_STID_REQUEST    (0xB001)
struct StatRequestStidHeader 
{
    uint16_t len;
    uint32_t proto_id;
    uint32_t msgid;
    uint32_t game_id;
    uint8_t  ret;
};

struct StatResponseStidHeader 
{
    uint16_t len;
    uint32_t proto_id;
    uint8_t  ret;
    uint8_t  type;
    char     body[0]; 
};

struct db_request_str 
{
    uint8_t str_len;
    char body[0];
};

#pragma pack()

#endif

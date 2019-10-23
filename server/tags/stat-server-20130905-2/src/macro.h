#ifndef MACRO_H
#define MACRO_H

#define MAX_CLIENT_NUM        (1024)
#define MAX_PROXY_NUM         (1024)
#define MAX_BUFFER_LEN        (1024 * 40960)
#define MAX_MSG_LEN           40960  /**< 消息长度 */


#define STAT_REPORT_COMBINE  30 //新添加的组合类型 proto_id
/**
 * 最大工作进程个数
 */
#define MAX_WORK_PROC_NUM     500

/**
 * 配置文件列表
 */
const static char CONFIG_FILE_LIST[][PATH_MAX] = {
	"../conf/stat_server.ini"
};

/**
 * 配置文件个数
 */
const static int CONFIG_FILE_COUNT = sizeof(CONFIG_FILE_LIST) / sizeof(*CONFIG_FILE_LIST);

#endif


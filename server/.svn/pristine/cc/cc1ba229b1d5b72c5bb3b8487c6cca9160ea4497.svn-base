/** 
 * ========================================================================
 * @file main.cpp
 * @brief 
 * @author tonyliu, jeanyu
 * @version 1.0.0
 * @date 2012-12-17
 * Modify $Date: $
 * Modify $Author: $
 * Copyright: TaoMee, Inc. ShangHai CN. All rights reserved.
 * ========================================================================
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <signal.h>
//#include <sys/time.h>
#include <time.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <dirent.h>
#include <assert.h>
//fcgi函数包
#include <fcgi_stdio.h>

#include "log.h"
#include "config.h"
#include "msglog.h"
#include "url_code.h"
#include "misc_macro.h"
#include "misc_utils.h"



int g_misc_stop = 0;
std::string local_ip;

static void sigterm_handler(int sig)
{
    g_misc_stop = 1;
    DEBUG_LOG("receive signo: %d", sig);
}

static inline int rlimit_reset()
{
    // 上调打开文件数的限制
    struct rlimit rl = {0};
    if (getrlimit(RLIMIT_NOFILE, &rl) == -1)
    {
        printf("ERROR: getrlimit(RLIMIT_NOFILE).\n");
        return -1;
    }
    rl.rlim_cur = rl.rlim_max;
    if (setrlimit(RLIMIT_NOFILE, &rl) != 0 )
    {
        printf("ERROR: setrlimit(RLIMIT_NOFILE).\n");
        return -1;
    }

    // 允许产生CORE文件
    if (getrlimit(RLIMIT_CORE, &rl) != 0)
    {
        printf("ERROR: getrlimit(RLIMIT_CORE).\n");
        return -1;
    }

    rl.rlim_cur = rl.rlim_max;
    if (setrlimit(RLIMIT_CORE, &rl) != 0)
    {
        printf("ERROR: setrlimit(RLIMIT_CORE).\n");
        return -1;
    }

    return 0;
}

static void daemon_start()
{
    rlimit_reset();

    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_handler = sigterm_handler;
    sigaction(SIGINT, &sa, NULL);
    sigaction(SIGHUP, &sa, NULL);
    sigaction(SIGTERM, &sa, NULL);
    sigaction(SIGQUIT, &sa, NULL);
    signal(SIGPIPE,SIG_IGN);

    sigset_t sset;
    sigemptyset(&sset);
    sigaddset(&sset, SIGBUS);
    sigaddset(&sset, SIGILL);
    sigaddset(&sset, SIGFPE);
    sigaddset(&sset, SIGSEGV);
    sigaddset(&sset, SIGCHLD);
    sigaddset(&sset, SIGABRT);
    sigprocmask(SIG_UNBLOCK, &sset, &sset);
    //daemon(1, 1);
}

int check_single()
{
    int fd = -1;
    char buf[16] = {0};

    fd = open(config_get_strval("pid_file", "./pid"), O_RDWR|O_CREAT, 0644);
    if (fd < 0)
    {
        BOOT_LOG(-1, "check_single() failed");
    }

    struct flock fl;

    fl.l_type = F_WRLCK;
    fl.l_whence = SEEK_SET;
    fl.l_start = 0;
    fl.l_len = 0;
    fl.l_pid = getpid();

    if (fcntl(fd, F_SETLK, &fl) < 0)
    {
        if (errno == EACCES || errno == EAGAIN)
        {
            close(fd);
            BOOT_LOG(-1, "service is running");
            return -2;
        }
        BOOT_LOG(-1, "service is running");
    }

    if (ftruncate(fd, 0) != 0)
    {
        BOOT_LOG(-1, "check single failed");
    }

    snprintf(buf, sizeof(buf), "%d", (int)getpid());
    if (write(fd, buf, strlen(buf)) == -1)
    {
        BOOT_LOG(-1, "check single failed");
    }

    return 0;
}

//将老统计的几种可能的Key值放在p_url_key_group中
//具体老统计的几种数据情况参考misc.conf
int init_url_key_group(url_key_group_t *p_url_key_group)
{
    assert(NULL != p_url_key_group);
    memset(p_url_key_group, 0, sizeof(*p_url_key_group));

    MISC_CONFIG_GET_STRVAL(p_url_key_group->key_msg_id, "key_msg_id");

    int group_count = 0;
    MISC_CONFIG_GET_INTVAL(group_count, "key_group_count");
    if (group_count <= 0 && group_count > MISC_GROUP_MAX_SIZE)
    {
        ERROR_LOG("wrong conf[key_group_count]: %d not in [1,%d]", group_count, MISC_GROUP_MAX_SIZE);
        return -1;
    }
    p_url_key_group->group_count = group_count;

    int key_count = 0;
    char key_name[64] = {0};
    for (int i = 0; i < group_count; ++i)
    {
        snprintf(key_name, sizeof(key_name), "group%d_key_count", i);
        MISC_CONFIG_GET_INTVAL(key_count, key_name);
        if (key_count <= 0 && key_count > MISC_KEY_MAX_SIZE)
        {
            ERROR_LOG("wrong conf[%s]: %d not in [1,%d]", key_name, key_count, MISC_KEY_MAX_SIZE);
            return -1;
        }
        p_url_key_group->group_key[i].key_count = key_count;

        for (int j = 0; j < key_count; ++j)
        {
            snprintf(key_name, sizeof(key_name), "group%d_key%d_name", i, j);
            MISC_CONFIG_GET_STRVAL(p_url_key_group->group_key[i].key_names[j], key_name);
        }
    }

    //dump
    DEBUG_LOG("key_msg_id ---> %s", p_url_key_group->key_msg_id);
    for (int i = 0; i < p_url_key_group->group_count; i++)
    {
        DEBUG_LOG("[group%d]", i);
        for (int j = 0; j < p_url_key_group->group_key[i].key_count; j++)
        {
            snprintf(key_name, sizeof(key_name), "group%d_key%d_name", i, j);
            DEBUG_LOG("%s ---> %s", key_name, p_url_key_group->group_key[i].key_names[j]);
        }
    }

    return 0;
}
void init_tongji_data(tongji_data_t *p_tongji_data)
{
	sprintf(p_tongji_data->zid, "%s", "-1");
	sprintf(p_tongji_data->sid, "%s", "-1");
	sprintf(p_tongji_data->pid, "%s", "-1");
	sprintf(p_tongji_data->plid, "%s", "-1");
}
/** 
 * @brief 对ＵＲＬ字符串长度进行校验
 * 
 * @param str 要校验的字符串
 * @return >0 校验正确， 0校验错误
 * @input: gameid=1&stid=淘米&sstid=统计平台&uid=123456&item=sum&&stidlen=18&sstidlen=24&itemlen=3
 */
int url_verify(const char *query_string)
{
	assert(NULL != query_string);
	std::string gameid("gameid");
	std::string stid("stid");
	std::string sstid("sstid");
	std::string item("item");
	std::string stidlen("stidlen");
	std::string sstidlen("sstidlen");
	std::string itemlen("itemlen");
	int game_id = 0;
	int stid_len = 0;
	int sstid_len = 0;
	int item_len = 0;
	int stid_len_url = 0;
	int sstid_len_url = 0;
	int item_len_url = 0;
	int stid_flag = 0;
	int sstid_flag = 0;
	int item_flag = 0;

	key_value_map_t key_value_map;//map结构
	key_value_iter_t key_value_iter;//迭代结构

	//将名称和数据以key和value的形式放到key_value_map结构中,例如：key=gameid，value=16
    if (0 != map_query_string(query_string, &key_value_map))
	{   
		ERROR_LOG("map_query_string(%s) failed.", query_string);
		key_value_map.clear();
		return -1; 
	}
	if(key_value_map.size() > 2 && key_value_map.size() < 20)
	{
		for(key_value_iter = key_value_map.begin(); key_value_iter != key_value_map.end(); key_value_iter++)
		{
			if(gameid == key_value_iter->first)
			{
				game_id = atoi(key_value_iter->second.c_str());
			}
			if(stid == key_value_iter->first)
			{
				stid_len = key_value_iter->second.length();
			}
			if(sstid == key_value_iter->first)
			{
				sstid_len = key_value_iter->second.length();
			}
			if(item == key_value_iter->first)
			{
				item_len = key_value_iter->second.length();
			}
			if(stidlen == key_value_iter->first)
			{
				stid_flag = 1;
				stid_len_url = atoi(key_value_iter->second.c_str());
			}
			if(sstidlen == key_value_iter->first)
			{
				sstid_flag = 1;
				sstid_len_url = atoi(key_value_iter->second.c_str());
			}
			if(itemlen == key_value_iter->first)
			{
				item_flag = 1;
				item_len_url = atoi(key_value_iter->second.c_str());
			}
		}
	}	
	if(game_id == 0)
	{
		return game_id;
	}
	if(stid_flag == 0 && sstid_flag == 0 && item_flag == 0)
	{
		ERROR_LOG("[ERROR] this url without check length!");
		return game_id;
	}
	if(stid_flag == 1 && stid_len != stid_len_url)
	{
		ERROR_LOG("[ERROR] stid_len=%d stid_len_url=%d",stid_len,stid_len_url);
		return game_id;
	}
	if(sstid_flag == 1 && sstid_len != sstid_len_url)
	{
		ERROR_LOG("[ERROR] sstid_len=%d sstid_len_url=%d",sstid_len,sstid_len_url);
		return game_id;
	}
	if(item_flag == 1 && item_len != item_len_url)
	{
		ERROR_LOG("[ERROR] item_len=%d item_len_url=%d",item_len,item_len_url);
		return game_id;
	}
	return -1;
}
int parse_query_string(const char *query_string, url_key_group_t *p_url_key_group, msg_data_t *p_msg_info, tongji_data_t *p_tongji_data, int *p_is_tongji)
{
    assert(NULL != query_string && NULL != p_url_key_group && NULL != p_msg_info);

    key_value_map_t key_value_map;//map结构
    key_value_iter_t key_value_iter;//迭代结构

	//将名称和数据以key和value的形式放到key_value_map结构中,例如：key=gameid，value=16
    if (0 != map_query_string(query_string, &key_value_map))
    {
        ERROR_LOG("map_query_string(%s) failed.", query_string);
        key_value_map.clear();
        return -1;
    }
	
    *p_is_tongji = 0;
    key_value_iter = key_value_map.find(p_url_key_group->key_msg_id);
    if (key_value_iter == key_value_map.end())
    {
		//tongji start
	    //不是老统计的数据，判断是否为新统计的数据
	    std::string gameid("gameid");
	    std::string stid("stid");
	    std::string sstid("sstid");
	    std::string uid("uid");
	    std::string item("item");
        std::string zid("zid");
		std::string sid("sid");
		std::string pid("pid");
		std::string plid("plid");

	    if(key_value_map.size() > 2 && key_value_map.size() < 20)
	    {//目前至少3个 之多5个
		    for(key_value_iter = key_value_map.begin(); key_value_iter != key_value_map.end(); key_value_iter++)
		    {
			    if(gameid == key_value_iter->first)
			    {
				    *p_is_tongji += 1;
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN)
				    {
				    	sprintf(p_tongji_data->game_id, "%s", key_value_iter->second.c_str());
				    	continue;
				    }
				    else
				    {
    					   *p_is_tongji = 0;
					    WARN_LOG("gameid length not valid");
					    break;

				    }
			    }
			    if(stid == key_value_iter->first)
			    {
				    *p_is_tongji += 1;
					//字符串长度判断及utf8的判断
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
				    	sprintf(p_tongji_data->stid, "%s", key_value_iter->second.c_str());
				    	continue;
				    }
				    else
				    {
    					   *p_is_tongji = 0;
					    WARN_LOG(" stid length not valid or  not utf8");
					    break;
				    }
			    }
			    if(sstid == key_value_iter->first)
			    {
				    *p_is_tongji += 1;
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
					    sprintf(p_tongji_data->sstid, "%s", key_value_iter->second.c_str());
				    		continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("sstid length not valid or  not utf8");
					    break;
				    }
			    }
			    if(uid == key_value_iter->first)
			    {
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN)
				    {
				    	sprintf(p_tongji_data->uid, "%s", key_value_iter->second.c_str());
				    	continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("uid length not valid");
					    break;

				    }
			    }
			    if(item == key_value_iter->first)
			    {
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
					    sprintf(p_tongji_data->item, "%s",  key_value_iter->second.c_str());
				    	    continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("item length not valid or  not utf8");
					    break;
				    }
			    }
			    if(zid == key_value_iter->first)
			    {
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
					    sprintf(p_tongji_data->zid, "%s",  key_value_iter->second.c_str());
				    	    continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("zid length not valid or  not utf8");
					    break;
				    }
			    }
			    if(sid == key_value_iter->first)
			    {
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
					    sprintf(p_tongji_data->sid, "%s",  key_value_iter->second.c_str());
				    	    continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("sid length not valid or  not utf8");
					    break;
				    }
			    }
			    if(pid == key_value_iter->first)
			    {
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
					    sprintf(p_tongji_data->pid, "%s",  key_value_iter->second.c_str());
				    	    continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("pid length not valid or  not utf8");
					    break;
				    }
			    }
			    if(plid == key_value_iter->first)
			    {
				    if(key_value_iter->second.length() < MISC_STR_MAX_LEN && is_utf8(key_value_iter->second))
				    {
					    sprintf(p_tongji_data->plid, "%s",  key_value_iter->second.c_str());
				    	    continue;
				    }
				    else
				    {
    					    *p_is_tongji = 0;
					    WARN_LOG("plid length not valid or  not utf8");
					    break;
				    }
			    }
		    }

		    if(*p_is_tongji != 3)
		    {
				//新统计的数据，但是不满足参数要求，必须含有gameid stid sstid 并且为utf8编码
        			ERROR_LOG("query_string[%s]  para invalid", query_string);
        			key_value_map.clear();
        			return -1;
		    }
			else if(strlen(p_tongji_data->sstid) == 0)
		    {
				//新统计的数据，但是不满足参数要求或者sstid为空，必须含有gameid stid sstid 并且为utf8编码
        			//ERROR_LOG("query_string[%s]  sstid is null", query_string);
        			key_value_map.clear();
        			return -1;
		    }
		    else
		    {  //正常的数据
        			key_value_map.clear();
        			return 0;
		    }
	    }
	    else
	    {
        	ERROR_LOG("no key[%s] in query_string[%s] or other para invalid", p_url_key_group->key_msg_id, query_string);
        	key_value_map.clear();
        	return -1;
	    }
	//tongji end
    }
    uint32_t msg_id = 0;
    if (misc_is_numeric(key_value_iter->second.c_str()))
    {
        msg_id = misc_strtol(key_value_iter->second.c_str());
    }
    else
    {
        msg_id = config_get_msgid(key_value_iter->second.c_str(), 0);
    }
    if (msg_id == 0)
    {
        ERROR_LOG("wrong msg_id[%s] in query_string[%s]", key_value_iter->second.c_str(), query_string);
        key_value_map.clear();
        return -1;
    }
    p_msg_info->msg_id = msg_id;

    int map_key_count = key_value_map.size() - 1;//剔除type剩余的key的个数

    const char *p_key_name = NULL;
    int i, j;
    for (i = 0; i < p_url_key_group->group_count; i++)
    {
        if (p_url_key_group->group_key[i].key_count != map_key_count)
        {//query_string中的key的数量必须与group中的key_count保持一致
            continue;
        }
        for (j = 0; j < p_url_key_group->group_key[i].key_count; j++)
        {
            p_key_name = p_url_key_group->group_key[i].key_names[j];
            key_value_iter = key_value_map.find(p_key_name);
            if (key_value_iter == key_value_map.end())
            {
                break;
            }
            //p_msg_info->data[j] = strtol(key_value_iter->second.c_str(), NULL, 10);
            p_msg_info->data[j] = misc_strtol(key_value_iter->second.c_str());
        }
        if (j == p_url_key_group->group_key[i].key_count)//all match
        {
            p_msg_info->data_len = p_url_key_group->group_key[i].key_count * sizeof(uint32_t);
            break;
        }
    }

    key_value_map.clear();
    return i == p_url_key_group->group_count ? -1 : 0;
}

bool is_specail_msg_id(uint32_t msg_id, msg_id_vec_t specail_msg_vec)
{
    vector_for_each(specail_msg_vec, it)
    {
        if (*it == msg_id)
        {
            return true;
        }
    }

    return false;
}

int init_specail_msg_list(msg_id_vec_t &specail_msg_vec)
{
    specail_msg_vec.clear();

    int msg_count = 0;
    MISC_CONFIG_GET_INTVAL(msg_count, "specail_msg_count");
    if (msg_count <= 0)
    {
        //ERROR_LOG("ERROR: wrong conf specail_msg_count: %d", msg_count);
        //return -1;
        BOOT_LOG(-1, "wrong conf specail_msg_count: %d", msg_count);
    }
    char buff[1024] = {0};
    uint32_t msg_id = 0;
    const char *str_msg = NULL;
    for (int i = 0; i < msg_count; i++)
    {
        snprintf(buff, sizeof(buff), "specail_msg_id%d", i);
        MISC_CONFIG_GET_STRVAL(str_msg, buff);
        msg_id = misc_strtol(str_msg);
        if (is_specail_msg_id(msg_id, specail_msg_vec))
        {
            //ERROR_LOG("repeated conf %s[%u]", buff, msg_id);
            //return -1;
            BOOT_LOG(-1, "repeated conf %s[%u]", buff, msg_id);
        }
        DEBUG_LOG("%s: 0x%x", buff, msg_id);
        specail_msg_vec.push_back(msg_id);
    }

    return 0;
}

int main(int argc, char *argv[])
{

	/***********************************************
	 * 初始化部分
	 **********************************************/
    const char *stat_inbox_dir = NULL;/**<统计客户端inbox目录*/
    const char *stat_file_name = NULL;/**<统计日志文件*/
    const char *msg_id_file = NULL;/**<key=msgid,映射文件*/
    const char *tip_info = NULL;/**<返回给客户端的提示*/
    url_key_group_t url_key_group;
    msg_id_vec_t specail_msg_vec;

    if (argc != 2)
    {
        printf("Usage: %s config_file\n", argv[0]);
        exit(-1);
    }
    daemon_start();
    TIP_LOG(0, "misc_server %s", MISC_VERSION);

    load_config_file(argv[1]);
    //check_single();

    if (-1 == log_init(config_get_strval("log_dir", "./"), 
        (log_lvl_t)config_get_intval("log_level", 8),
        config_get_intval("log_size", 33554432),
        config_get_intval("log_maxfiles", 100), "main_"))
    {
        BOOT_LOG(-1, "log init");
    }

    MISC_CONFIG_GET_STRVAL(stat_inbox_dir, "stat_file_dir");
    MISC_CHECK_DIR(stat_inbox_dir);
    MISC_CONFIG_GET_STRVAL(stat_file_name, "stat_file_name");
    MISC_CONFIG_GET_STRVAL(msg_id_file, "msg_id_file");
    MISC_CONFIG_GET_STRVAL(tip_info, "client_tip_info");
    load_msgid_file(msg_id_file);
    

    if (0 != init_url_key_group(&url_key_group))
    {
        BOOT_LOG(-1, "url key group init");
    }
    if (0 != init_specail_msg_list(specail_msg_vec))
    {
        BOOT_LOG(-1, "specail message id list init");
    }

    pid_t pid = getpid();
    init_proc_title(argc, argv);
    set_proc_title("%s_%d", config_get_strval("proc_name", "stat_misc"), pid);


    local_ip = get_ip_addr("eth1", 1); 
    if(local_ip.length() == 0)
    {
	    local_ip = "0.0.0.0";
    }
    DEBUG_LOG("local_ip:%s", local_ip.c_str());

    char full_path[MISC_PATH_MAX_LEN] = {0};
    snprintf(full_path, sizeof(full_path), "%s/%s_%d", stat_inbox_dir, stat_file_name, pid);

    uint32_t now = 0;
    const char *query_string = NULL;//保存请求信息
    msg_data_t msg_info = {0};
	//tongji start
    tongji_data_t tongji_data; //存储新统计的数据
    int is_tongji = 0; //标识是否为新统计的数据
	//tongji end
	/***********************************************
	 * 应答循环部分
	 * while(FCGI_Accept() >= 0){应答循环体}
	 **********************************************/
    while (!g_misc_stop && FCGI_Accept() >= 0)
    {
        /* body of response loop */
        FCGI_printf("Content-type: text/javascript\r\n"
                "\r\n"
                "(function(){})();"
		);
		
		/********************************************************
		 * QUERY_STRING:传递的query信息，即url中问号后面的信息
		 * 例如:url:http://newmisc.taomee.com/misc.js?gameid=1&stid=xxx&sstid=xxx&uid=xxx&item=xxx
		 * 		query:gameid=1&stid=xxx&sstid=xxx&uid=xxx&item=xxx
		 ********************************************************/
        query_string = getenv("QUERY_STRING");
        if (NULL == query_string || strlen(query_string) > MISC_BUFF_MAX_LEN/3)
        {
			ERROR_LOG("query_string is null or query_string extend max length");
            continue;
        }
		/********************************************************
		 * url_verify:对query字符串进行校验
		 * 检测出错误则返回有错误信息的gameid
		 * 成功则返回 -1
		 ********************************************************/
		char decode_string[MISC_BUFF_MAX_LEN] = {0};
		int decode_len = 0;
		int verify_result = 0;
		if((verify_result = url_verify(query_string)) != -1)
		{
			ERROR_LOG("[ERROR] game = %d wrong URL = %s", verify_result,query_string);
			if(verify_result == 0 ||
			   verify_result == 1 || 
			   verify_result == 2 || 
			   verify_result == 5 || 
			   verify_result == 6 || 
			   verify_result == 10 || 
			   verify_result == 16)
			{
				continue;
			}
		}
		/********************************************************
		 * url_decode:对query字符串进行解码
		 * 结果放在decode_string中
		 * 针对新统计，解码后结果不变
		 * decode_string[]=gameid=1&stid=xxx&sstid=xxx&uid=xxx&item=xxx
		 ********************************************************/
        if((decode_len = url_decode(query_string, strlen(query_string), decode_string, MISC_BUFF_MAX_LEN)) == 0)	
		{
			ERROR_LOG("query_string %s decode faield.", query_string);
			continue;
		}
		//DEBUG_LOG("[DEBUG] decode_string = %s",decode_string);
	
	//for gongfu ,second decode 
	//因为功夫派多做了一次转码，故我们这里需要多解码一次
	//对正常的已经解码的，不影响，多做一次解码结果还是一样的
	char de_decode_string[MISC_BUFF_MAX_LEN*3+1] ={0};
	if(url_decode(decode_string, decode_len, de_decode_string, MISC_BUFF_MAX_LEN*3) == 0)
	{
		ERROR_LOG("dedecode[%s] failed.", decode_string);
		continue;
	}
	

        now = time(NULL);
        memset(&msg_info, 0, sizeof(msg_info));
		//初始化存储统计数据的内存
		memset(&tongji_data, 0, sizeof(tongji_data));
		init_tongji_data(&tongji_data);
		is_tongji = 0;//新统计标志
        if (0 != parse_query_string(de_decode_string, &url_key_group, &msg_info, &tongji_data, &is_tongji))
        {
           // WARN_LOG("%s&time=%u", decode_string, now);
            continue;
        }
	if(is_tongji == 3)
	{//新统计的数据
		tongji_log(&tongji_data, now);
	}
	else if(is_tongji == 0)
	{
        	if (is_specail_msg_id(msg_info.msg_id, specail_msg_vec))
        	{
            	msg_info.body_len = msg_info.data_len + sizeof(uint16_t);
            	msglog(full_path, msg_info.msg_id, now, &msg_info.body_len, msg_info.body_len);
        	}
        	else
        	{
            	msglog(full_path, msg_info.msg_id, now, &msg_info.data, msg_info.data_len);
        	}
    	}
	else
	{
		//do nothing 新统计的数据，但是参数不全,必须含有game_id, stid ,sstid
	}
    }
    DEBUG_LOG("misc_server stopped");

    return 0;
}

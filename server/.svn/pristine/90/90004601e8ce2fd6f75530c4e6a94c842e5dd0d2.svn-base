#include <cstdlib>
#include <stdexcept>
#include <string>
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include <set>
#include <ctime>

#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#include "db_server.hpp"
#include "log.h"
#include "tcp_operator.h"
#include "c_mysql_operator.h"
#include "redis_operator.h"
#include "util.h"
#include "../../stat-common/stat_config.hpp"
#include "../../stat-common/string_utils.hpp"

#include "stat_common.hpp"
#include "stat_proto_defines.hpp"

using std::set;

c_mysql_operator g_mysql;

uint32_t StatDbServer::getGPZSId(redisConnection* redis, TcpClient* config_server, const gpzs_t* gpzs, int fd)
{
    sprintf(hashkey, "gpzs:%d:%d:%d:%u", gpzs->platform_id, gpzs->zone_id, gpzs->server_id, gpzs->game_id);
	//首先从应用程序自己定义的hashtable中查找
    c_data data = this->hash_table.search(hashkey);
    if(data.not_null()) {
        return *(uint32_t*)data.get_value();
    }
    uint32_t ret;
	//再从redis中查找
    if((ret = getGPZSFromRedis(redis, gpzs)) != 0) {
        this->hash_table.insert(sizeof(ret), &ret, hashkey);
        return ret;
    } else {
		//最后从configserver中找
        if((ret = getGPZSFromConfig(config_server, gpzs, fd)) != 0) {
            this->hash_table.insert(sizeof(ret), &ret, hashkey);
            return ret;
        } else {
            return 0;
        }
    }
}

uint32_t StatDbServer::getDataId(redisConnection* redis, TcpClient* config_server, const void* buf, uint32_t len, const c_proto_pkg* pkg, int fd)
{
    sprintf(hashkey, "data:%u:%s:%s:%u:%s:%s", pkg->getGame(), pkg->getStid(), pkg->getSstid(), pkg->getOpType(), pkg->getField(), pkg->getKey());
    c_data data = this->hash_table.search(hashkey);
    if(data.not_null()) {
        return *(uint32_t*)data.get_value();
    }
    uint32_t ret;
    if((ret = getDataIdFromRedis(redis, buf, len)) != 0) {
        //DEBUG_LOG("redis return %s => %u", hashkey, ret);
        this->hash_table.insert(sizeof(ret), &ret, hashkey);
        return ret;
    } else {
        if((ret = getDataIdFromConfig(config_server, buf, len, fd)) != 0) {
            //DEBUG_LOG("config server return %s => %u", hashkey, ret);
            this->hash_table.insert(sizeof(ret), &ret, hashkey);
            return ret;
        } else {
            return 0;
        }
    }
}

uint32_t StatDbServer::getTaskId(redisConnection* redis, TcpClient* config_server, const void* buf, uint32_t len, int fd)
{
    uint32_t ret;
    if((ret = getDataIdFromRedis(redis, buf, len)) != 0) {
        return ret;
    } else {
        if((ret = getTaskIdFromConfig(config_server, buf, len, fd)) != 0) {
            return ret;
        } else {
            return 0;
        }
    }
}

uint32_t StatDbServer::getReportId(redisConnection* redis, TcpClient* config_server, const void* buf, uint32_t len, const c_proto_pkg* pkg, int fd)
{
    sprintf(hashkey, "report:%u:%s:%s:%u:%s", pkg->getGame(), pkg->getStid(), pkg->getSstid(), pkg->getOpType(), pkg->getField());
    c_data data = this->hash_table.search(hashkey);
    if(data.not_null()) {
        return *(uint32_t*)data.get_value();
    }
    uint32_t ret;
    if((ret = getReportIdFromRedis(redis, buf, len)) != 0) {
        this->hash_table.insert(sizeof(ret), &ret, hashkey);
        return ret;
    } else {
        if((ret = getDataIdFromConfig(config_server, buf, len, fd)) != 0) {
            this->hash_table.insert(sizeof(ret), &ret, hashkey);
            return ret;
        } else {
            return 0;
        }
    }
}

bool StatDbServer::getStHash(redisConnection* redis, const char* stid, uint32_t* sthash)
{
    sprintf(hashkey, "sthash:%s", stid);
    c_data data = this->hash_table.search(hashkey);
    if(data.not_null()) {
        *sthash = *(uint32_t*)data.get_value();
        return true;
    }
    if((*sthash = getHashFromRedis(redis, stid)) != 0) {
        this->hash_table.insert(sizeof(uint32_t), sthash, hashkey);
        return true;
    }
    *sthash = getHash(stid);
    this->hash_table.insert(sizeof(uint32_t), sthash, hashkey);
    insertHashToRedis(redis, stid, *sthash);
    return true;
}

uint32_t StatDbServer::doCmd(uint32_t platform_id, uint32_t zone_id, uint32_t server_id, int fd)
{
    uint32_t gpzs_id, data_id, sthash, r;
    pkg.setPlatform(platform_id);
    pkg.setZone(zone_id);
    pkg.setServer(server_id);
    static char str_sthash[256];
    //DEBUG_LOG("do cmd 0x%08x", pkg.getCmdID());
    switch(pkg.getCmdID()) {
		//添加统计项
        case CMD_INSERT_STAT:
			//获取gpzs_id
            if((gpzs_id = getGPZSId(&redis, &config_server, pkg.getGPZS(), fd)) == 0) {
                ERROR_LOG("get [%u.%d.%d.%d] id error", pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer());
                return 1;
            }
            if(pkg.getOpType() > SET && strlen(pkg.getKey()) == 0) {
				//获取report_id
                if((data_id = getReportId(&redis, &config_server, pkg.getDataInfo(), pkg.getDataInfoLen(), &pkg, fd)) == 0) { 
                    ERROR_LOG("get [%u.%s.%s.%u.%s] id error", pkg.getGame(), pkg.getStid(), pkg.getSstid(), pkg.getOpType(), pkg.getField());
                    return 1;
                }
            } else {
				//获取data_id
                if((data_id = getDataId(&redis, &config_server, pkg.getDataInfo(), pkg.getDataInfoLen(), &pkg, fd)) == 0) {
                    ERROR_LOG("get [%u.%s.%s.%u.%s] id error", pkg.getGame(), pkg.getStid(), pkg.getSstid(), pkg.getOpType(), pkg.getField());
                    return 1;
                }
            }
            return 0;
		//实时处理部分数据入库
        case CMD_ONLINE_UPDATE:
            //DEBUG_LOG("CMD_ONLINE_UPDATE [%u.%s.%s.%u.%s.%s]", pkg.getGame(), pkg.getStid(), pkg.getSstid(), pkg.getOpType(), pkg.getField(), pkg.getKey());
            if((gpzs_id = getGPZSId(&redis, &config_server, pkg.getGPZS(), fd)) == 0) {
                ERROR_LOG("get [%u.%d.%d.%d] id error", pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer());
                return 1;
            }
            if(strstr(pkg.getStid(), "tsk_") != 0 ) {
                //20150202添加，不处理任务的实时数据
                return 0;
            }
            if((data_id = getDataId(&redis, &config_server, pkg.getDataInfo(), pkg.getDataInfoLen(), &pkg, fd)) == 0) {
                ERROR_LOG("get [%u.%s.%s.%u.%s.%s] id error", pkg.getGame(), pkg.getStid(), pkg.getSstid(), pkg.getOpType(), pkg.getField(), pkg.getKey());
                //大部分情况是乱码问题  返回0
                return 0;
            }
            if(!getStHash(&redis, pkg.getStid(), &sthash)) {
                ERROR_LOG("get [%s] sthash error", pkg.getStid());
                return 1;
            }
            if((r = this->sqlcache.cacheOnlineData(gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), pkg.getOpType(), sthash)))
            {
                ERROR_LOG("cache online error %u gpzs=%u data=%u datatype=%u time=%u value=%f optype=%u",
                        r, gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), pkg.getOpType());
                return 1;
            }
            return 0;
		//离线处理部分数据入库
        case CMD_HADOOP_UPDATE:
            if((gpzs_id = getGPZSId(&redis, &config_server, pkg.getGPZS(), fd)) == 0) {
                ERROR_LOG("get [%u.%d.%d.%d] id error", pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer());
                return 1;
            }
            if((data_id = getDataId(&redis, &config_server, pkg.getDataInfo(), pkg.getDataInfoLen(), &pkg, fd)) == 0) {
                //大部分情况是乱码问题  返回0
                return 0;
            }
            //DEBUG_LOG("gpzs=%u data=%u datatype=%u time=%u value=%f optype=%u",
            //        gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), pkg.getOpType());
            if(!getStHash(&redis, pkg.getStid(), &sthash)) {
                ERROR_LOG("get [%s] sthash error", pkg.getStid());
                return 1;
            }
            //DEBUG_LOG("%s| sthash = %u", pkg.getStid(), sthash);
            if((r = g_mysql.updateHadoop(gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), pkg.getOpType(), sthash)) != 0) {
                ERROR_LOG("insert online error %u gpzs=%u data=%u datatype=%u time=%u value=%f optype=%u",
                        r, gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), pkg.getOpType());
                return 1;
            }
            return 0;
		//基础加工项数据入库
        case CMD_TASK_UPDATE:
            //DEBUG_LOG("get [%u.%u.%s.%u.%f]", pkg.getGame(), pkg.getTask(), pkg.getRange(), pkg.getTime(), pkg.getValue());
            if((gpzs_id = getGPZSId(&redis, &config_server, pkg.getGPZS(), fd)) == 0) {
                ERROR_LOG("get [%u.%d.%d.%d] id error", pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer());
                return 1;
            }
            if((data_id = getTaskId(&redis, &config_server, pkg.getTaskInfo(), pkg.getTaskInfoLen(), fd)) == 0) {
                ERROR_LOG("get [%u.%u.%s] id error", pkg.getGame(), pkg.getTask(), pkg.getRange());
                return 1;
            }
            snprintf(str_sthash, 256, "%u;%u", pkg.getGame(), pkg.getTask());
            if(!getStHash(&redis, str_sthash, &sthash)) {
                ERROR_LOG("get [%u;%u] sthash error", pkg.getGame(), pkg.getTask());
                return 1;
            }
            //DEBUG_LOG("%u;%u| sthash = %u", pkg.getGame(), pkg.getTask(), sthash);
            if((r = g_mysql.updateHadoop(gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), TASK, sthash)) != 0) {
                ERROR_LOG("insert online error %u gpzs=%u data=%u datatype=%u time=%u value=%f optype=%u",
                        r, gpzs_id, data_id, pkg.getDataType(), pkg.getTime(), pkg.getValue(), pkg.getOpType());
                return 1;
            }
            return 0;
        default:
            ERROR_LOG("unknow cmd id %u", pkg.getCmdID());
            return 1;
    }
}

int StatDbServer::init(){
    //redis
    if(this->redis.connect(config_get_strval("redis_ip", "127.0.0.1"), config_get_intval("redis_port", 6379))) {
        ERROR_LOG("can not connect to redis server [%s]", redis.getError());
        sleep(1);
        BOOT_LOG(-1, "can not connect to redis server [%s]", redis.getError());
    }
    //config_server
    strcpy(config_ip, config_get_strval("config_ip", "127.0.0.1"));
    strcpy(config_port, config_get_strval("config_port", "19903"));
    if(this->config_server.connect(config_ip, config_port) <= 0) {
        ERROR_LOG("can not connect to config server %s:%s", config_ip, config_port);
        config_connected = false;
    } else {
        DEBUG_LOG("connected to config server %s:%s",config_ip,config_port);
        config_connected = true;
    }
    this->config_server.set_timeout(10);
    //result mysql
    if(g_mysql.init(config_get_strval("db_name", "db_td_config"),
                config_get_strval("db_user", ""),
                config_get_strval("db_pwd", ""),
                config_get_strval("db_host", ""),
                config_get_intval("db_port", 3306))) {
        ERROR_LOG("can not connect to mysql");
        sleep(1);
        BOOT_LOG(-1, "can not connect to mysql");
    }
    //update DataBase once per 1000 lines or once per minute by default
	//sql缓存中最多存放的条数 缓存更新到数据库的间隔
    this->sqlcache.init(config_get_intval("sql_cache_size", 1000), config_get_intval("sql_update_interval", 60));

    if(m_traffic_log.init() != 0){
        return -1;
    }
    //config mysql
    if(db_config.init(config_get_strval("db_host", ""),
                config_get_strval("db_user", ""),
                config_get_strval("db_pwd", ""),
                config_get_strval("db_name", "db_td_config"),
                config_get_intval("db_port", 3306),
                CLIENT_INTERACTIVE)) {
        ERROR_LOG("can not connect to mysql");
        sleep(1);
        BOOT_LOG(-1, "can not connect to mysql");
    }
    this->init_msgid_cache();

    m_alarm_interval = StatCommon::stat_config_get("alarm_interval", 30);
    DEBUG_LOG("alarm interval: %d min", m_alarm_interval);

    StatCommon::stat_config_get("bind_ip", m_bind_ip);

    init_alarm_ip_port();
    int port = atoi(m_alarm_port.c_str());
    m_alarm_fd = net_connect_ser(m_alarm_ip.c_str(), port, 1000000);  

    return 0;
}

int StatDbServer::uninit()
{
    this->redis.close();
    this->config_server.close();
    this->sqlcache.uninit();
    g_mysql.uninit();

    net_close_ser(m_alarm_fd);
    m_alarm_fd = -1;

    return 0;
}

int StatDbServer::process_traffic_log(time_t now){
    static time_t last_time = 0;
    if(now - last_time >= 60){
        if(last_time != 0)
        {
            if(m_traffic_log.process_log_traffic("(条数)") == 0)
                m_traffic_log.clear_traffic_value();
        }
        last_time = now;
    }
    return 0;
}

void StatDbServer::alarm_notutf8()
{
    if (m_gameid_not_utf8.empty())
        return;

    uint32_t size = m_gameid_not_utf8.size();
    
    char buf_send[100] = {0};

    uint32_t pkg_len = sizeof(struct StatNOTUTF8DbHeader) + size * sizeof(uint32_t);               // pkg_len
    memcpy(buf_send, &pkg_len, sizeof(pkg_len)); 
    uint32_t offset = sizeof(pkg_len);

    uint32_t proto_id = STAT_PROTO_NOTUTF8_DB;
    memcpy(buf_send+offset, &proto_id, sizeof(proto_id)); 
    offset += sizeof(proto_id);                    // proto_id

    StatModuleType module_type = NOTUTF8_DB; 
    memcpy(buf_send+offset, &module_type, 1);
    offset += 1;                                  // module_type

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    inet_pton(AF_INET, m_bind_ip.c_str(), &addr.sin_addr);
    uint32_t ip = addr.sin_addr.s_addr;
    memcpy(buf_send+offset, &ip, sizeof(uint32_t));
    offset += sizeof(uint32_t);                    // ip

    memcpy(buf_send+offset, &size, sizeof(size));
    offset += sizeof(size);                        // size 

    set<uint32_t>::iterator iter = m_gameid_not_utf8.begin();
    while (iter != m_gameid_not_utf8.end())
    {
        uint32_t game_id_temp = *iter;
        
        memcpy(buf_send+offset, &game_id_temp, sizeof(game_id_temp));
        offset += sizeof(game_id_temp);          // gamie_id[size]

        iter++;
    }

    m_gameid_not_utf8.clear();

    int res = net_send_ser(m_alarm_fd,  buf_send, pkg_len);
    if (res == -1)
        ERROR_LOG("Send messy alarm failed");

    memset(buf_send, 0, 100);
}

void StatDbServer::timer_event()
{        
    time_t now;
    time(&now);

    if ((now-m_last_send_time) > 60 * m_alarm_interval)   // to send m_gameid_not_utf8 every 5 minutes
    {
        reconnect();
        alarm_notutf8();
        m_last_send_time = now;
    }

    this->sqlcache.flush_timer(now);
    process_traffic_log(now);
}

StatDbServer::~StatDbServer()
{
    this->uninit();
}

int StatDbServer::get_server_pkg_len(const char *buf, uint32_t len)
{
    if(len < sizeof(uint32_t))
    {
        return 0;
    }
    return *(uint32_t*)(buf);
}


char StatDbServer::getChar(uint8_t c)
{
    if(32 <= c && c <= 126) return c;
    return ' ';
}

void StatDbServer::print_pkg(const char *pbuf, uint32_t len)
{
    uint32_t lines = len%4==0?len/4:len/4+1;
    uint8_t *buf = (uint8_t*)malloc(lines*4);
    memset(buf, 0, lines*4);
    memcpy(buf, pbuf, len);
    printf("recv %d bytes\n", len);
    for(uint32_t i=0; i<lines; i++) {
        printf("%02X %02X %02X %02X  |  ", buf[i*4], buf[i*4+1], buf[i*4+2], buf[i*4+3]);
        printf("%03d %03d %03d %03d  |  ", buf[i*4], buf[i*4+1], buf[i*4+2], buf[i*4+3]);
        printf("%c %c %c %c  |\n",    getChar(buf[i*4]), getChar(buf[i*4+1]), getChar(buf[i*4+2]), getChar(buf[i*4+3]));
    }
    free(buf);
}

void StatDbServer::process_client_pkg(int fd, const char *buf, uint32_t len)
{
    header_t *item_pkgs = (header_t *)buf;

    ret_t ret;
    ret.len = sizeof(ret);
    ret.cmd_id = item_pkgs->cmd_id;
    ret.ret = 0;
    uint32_t recv_ret;

    DEBUG_LOG("recv cmd 0x%08x", item_pkgs->cmd_id);
	//基础加工项数据入库
    if(item_pkgs->cmd_id == CMD_MULTI_PROTO)
    {
        uint32_t count = 0;
        // buf中包含多个包，逐个处理。
        for(const char *item = item_pkgs->body; item < buf + len - 1;)
        {
            ++count;
            //print_pkg(buf, len);
			//调用c_proto_pkg类的recv_pkg方法接收数据包，解析包并检查数据的合法性
            recv_ret = pkg.recv_pkg(item);
            if(recv_ret == E_STID_NOT_UTF8 ||
                    recv_ret == E_SSTID_NOT_UTF8 ||
                    recv_ret == E_FIELD_NOT_UTF8) {

                m_gameid_not_utf8.insert(uint32_t(pkg.getGPZS()->game_id));
                
				//item向后移一个数据包的长度
                item += pkg.getPkgLen();

                continue;
            } else if(ret.ret != 0) {
                ret.ret |= recv_ret;
                break;
            }
            int32_t platform = pkg.getPlatform();
            int32_t zone = pkg.getZone();
            int32_t server = pkg.getServer();

            ret.ret |= doCmd(platform, zone, server, fd);
            if(ret.ret) break;
            if(pkg.getCmdID() == CMD_INSERT_STAT ||
                    pkg.getCmdID() == CMD_ONLINE_UPDATE) {
                //doCmd(platform, zone, server, fd);
                if(platform != -1) {
                    ret.ret |= doCmd(-1, zone, server, fd);
                    if(ret.ret) break;
                }
                if(zone != -1 || server != -1) {
                    ret.ret |= doCmd(-1, -1, -1, fd);
                    if(ret.ret) break;
                }
            }

            item += pkg.getPkgLen();
        }
        m_traffic_log.add_traffic_value(count);
    }
    else if(CMD_INSERT_STAT <= item_pkgs->cmd_id && item_pkgs->cmd_id <= CMD_TASK_UPDATE)
    {
        m_traffic_log.add_traffic_value(1);
        
        ret.ret = pkg.recv_pkg(buf);
        if(ret.ret == E_STID_NOT_UTF8 ||
                ret.ret == E_SSTID_NOT_UTF8 ||
                ret.ret == E_FIELD_NOT_UTF8) {

            m_gameid_not_utf8.insert(uint32_t(pkg.getGPZS()->game_id));

            ret.ret = 0;
            goto exit;
        } else if(ret.ret != 0) {
            goto exit;
        }
        int32_t platform = pkg.getPlatform();
        int32_t zone = pkg.getZone();
        int32_t server = pkg.getServer();

        ret.ret |= doCmd(platform, zone, server, fd);
        if(ret.ret) goto exit;
        if(pkg.getCmdID() == CMD_INSERT_STAT ||
                pkg.getCmdID() == CMD_ONLINE_UPDATE) {
            //实时计算需要做平台，区服的组合
            if(platform != -1) {
                ret.ret |= doCmd(-1, zone, server, fd);
                if(ret.ret) goto exit;
                if(zone != -1 && server != -1) {
                    ret.ret |= doCmd(-1, -1, -1, fd);
                    if(ret.ret) goto exit;
                }
            }
            if(zone != -1 || server != -1) {
                ret.ret |= doCmd(-1, -1, -1, fd);
            }
        }

        if(pkg.getCmdID() == CMD_HADOOP_UPDATE ||
                pkg.getCmdID() == CMD_TASK_UPDATE) {
            //ret.ret = 0;//临时全部放行
            net_send_cli(fd, &ret, ret.len);
            return;
        }
    } else if(item_pkgs->cmd_id == STAT_PROTO_STID) {
        stid_request_t *stid_request = (stid_request_t*)buf;
        if(stid_request->len == sizeof(stid_request_t)) {//包长正确
            c_data data;
            stid_response_t* stid_response = getStid(stid_request->game_id, stid_request->msg_id, data);
            if(stid_response != NULL) {
                //DEBUG_LOG("return %p", stid_response);
                //print(stid_response);
                net_send_cli(fd, stid_response, stid_response->len);
                return;
            } else {
                stid_response_t ret;
                ret.len = sizeof(ret);
                ret.proto_id = stid_request->proto_id;
                ret.ret = 0x1;
                ret.type = 0;
                net_send_cli(fd, &ret, ret.len);
                return;
            }
        } else {//包长不正确
            stid_response_t ret;
            ret.len = sizeof(ret);
            ret.proto_id = stid_request->proto_id;
            ret.ret = E_PACKAGE_LENGTH;
            ret.type = 0;
            net_send_cli(fd, &ret, ret.len);
            return;
        }
    }
exit:
    //ret.ret = 0;//临时全部放行
    net_send_cli(fd, &ret, ret.len);
}

stid_response_t* StatDbServer::getStid(uint32_t game_id, uint32_t msg_id, c_data& data) {
    static char key[64];
    snprintf(key, sizeof(key)-1, "%u:%u", game_id, msg_id);
    data = used_msgid.search(key);
    if(data.not_null()) {
        //返回从cache中找到的结果
        DEBUG_LOG("get from cache %s", key);
        stid_response_t* ret = (stid_response_t*)(data.get_value());
        //print(ret);
        //DEBUG_LOG("return %p", ret);
        //print(ret);
        return ret;
    } else {
        time_t now;
        time(&now);
        string str_key(key);
        map<string, time_t>::iterator it = unused_msgid.find(str_key);
        if(it != unused_msgid.end()) {
            //检查上次查询时间
            if(now - it->second <= 600) {
                DEBUG_LOG("key %s less than 600 seconds", key);
                return NULL;    //去数据库检查的间隔至少是600秒
            }
            unused_msgid.erase(str_key);
        }
        //从db获取stid，并更新检查时间
        static char sql[1024];
        sprintf(sql, "select concat(game_id,':',id), first, second, third, fourth, type from t_custom_msgid_info where game_id = %u and id = %u", game_id, msg_id);
        MYSQL_ROW row;
        if((db_config.do_sql(sql)) == 0 &&
                (row = db_config.get_next_row()) != NULL) {
            DEBUG_LOG("%s first time", key);
            //查找到stid
            static char package_buf[1024];
            stid_response_t* value = pack_cache(package_buf, row);
            used_msgid.insert(value->len, value, key);
            //更新load_time
            sprintf(sql, "update t_custom_msgid_info set load_time = if(unix_timestamp(load_time)=0,now(),load_time),status='used' where game_id = %u and id = %u", game_id, msg_id);
            if(db_config.do_sql(sql) != 0) {
                ERROR_LOG("update %s load_time error", key);
            }
            return value;
        } else {
            //没找到，更新检查时间
            DEBUG_LOG("not found %s", key);
            unused_msgid.insert(std::pair<string, time_t>(str_key, now));
            return NULL;
        }
    }
}

void StatDbServer::process_server_pkg(int fd, const char *buf, uint32_t len)
{
    // to handle the retrun from stat-center 
    StatAlarmRet* ret_pkgs = (StatAlarmRet*)(buf);
    if (ret_pkgs->proto_id == STAT_PROTO_NOTUTF8_DB)
    {
        return;
    }
}

void StatDbServer::client_connected(int fd, uint32_t ip)
{
}
void StatDbServer::client_disconnected(int fd)
{
}
void StatDbServer::server_disconnected(int fd)
{
    reconnect();
}

bool StatDbServer::is_real_alive()
{
    if(m_alarm_fd < 0)
        return false;

    char test[1];

    ssize_t ret = ::recv(m_alarm_fd, test, 1, MSG_PEEK | MSG_DONTWAIT);

    if(ret > 0)
        return true;
    else if(ret == 0)
    {   
        return false;
    }   
    else
    {   
        if(errno == EAGAIN || errno == EWOULDBLOCK || errno == EINTR)
            return true;
        else
        {
            return false;
        }
    }   
}

int StatDbServer::reconnect()
{
    if(is_real_alive() == false)
    {   
        DEBUG_LOG("disconnected to %s:%s, try to reconnect.", m_alarm_ip.c_str(), m_alarm_port.c_str());
        m_alarm_fd = -1;

        int port = atoi(m_alarm_port.c_str());
        m_alarm_fd = net_connect_ser(m_alarm_ip.c_str(), port, 1000000);  
    }   

    return 0;
}

void StatDbServer::init_msgid_cache()
{
    if(db_config.do_sql("select concat(game_id,':',id) from t_custom_msgid_info where status='unused'") == 0) {
        unused_msgid.clear();
        MYSQL_ROW row;
        while((row = db_config.get_next_row())) {
            unused_msgid.insert(std::pair<string, uint32_t>(string(row[0]), 0));
        }
    }

    if(db_config.do_sql("select concat(game_id,':',id), first, second, third, fourth, type from t_custom_msgid_info where status='used'") == 0) {
        MYSQL_ROW row;
        while((row = db_config.get_next_row())) {
            char* key = row[0];
            static char package_buf[1024];
            stid_response_t* value = pack_cache(package_buf, row);
            used_msgid.insert(value->len, value, key);

            //print(value);
        }

    }
}

stid_response_t* StatDbServer::pack_cache(void* buf, MYSQL_ROW row) {
    stid_response_t* value = (stid_response_t*)buf;
    value->proto_id = STAT_PROTO_STID;
    value->ret = 0;
    value->type = atoi(row[5]);

    char* body = value->body;
    uint16_t len = 0;
    for(int i=1; i<=4; i++) {
        str_t* str = (str_t*)body;
        char* s = row[i];
        if(s == NULL) {
            str->str_len = 0;
        } else {
            str->str_len = strlen(s);
            strncpy(str->str, s, str->str_len);
        }
        len += str->str_len;
        body += (sizeof(str->str_len) + str->str_len);
    }
    value->len = sizeof(stid_response_t) + len + sizeof(str_t) * 4;
    //DEBUG_LOG("in pack_cache");
    //print(value);
    return value;
}

void StatDbServer::print(stid_response_t* s) {
    DEBUG_LOG("pointer=%p", s);
    DEBUG_LOG("len = %u", s->len);
    DEBUG_LOG("proto = 0x%08x", s->proto_id);
    DEBUG_LOG("ret = %u", s->ret);
    DEBUG_LOG("type = %u", s->type);

    char* body = s->body;
    for(uint32_t i=0; i<4; i++) {
        static char buf[64];
        str_t *str = (str_t*)body;
        strncpy(buf, str->str, str->str_len);
        buf[str->str_len] = 0;
        DEBUG_LOG("%u:%s:", str->str_len, buf);
        body += (str->str_len + 1);
    }
}

void StatDbServer::init_alarm_ip_port()
{
    int proxy_count = StatCommon::stat_config_get("stat-center-count", 1); 

    for(int id = 0; id < proxy_count; ++id)
    {   
        ostringstream oss;
        oss.str("");

        oss << "stat-center" << "-host" << id; 
        string ip_key = oss.str();
        string ipp;
        StatCommon::stat_config_get(ip_key, ipp);
        if(ipp.empty())
        {
            ERROR_LOG("can not get %s from conf.", ip_key.c_str());
            continue;
        }

        vector<string> elems;
        elems.clear();

        StatCommon::split(ipp, ':', elems);
        if(elems.size() != 2)
        {
            ERROR_LOG("bad format of ip:port, %s", ipp.c_str());
            continue;
        }

        m_alarm_ip = elems[0];
        m_alarm_port = elems[1];
    }   
}

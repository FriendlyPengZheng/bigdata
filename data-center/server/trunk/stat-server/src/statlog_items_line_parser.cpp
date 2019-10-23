/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-server服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <cstring>
#include <vector>

#include "stat_common.hpp"
#include "string_utils.hpp"
#include "statlog_items_line_parser.hpp"
#include <stat_config.hpp>

#include <map>
#include <stdlib.h>

using std::vector;
using std::map;
using std::pair;
using std::string;

map<string, map<string, string> > StatLogItemsLineParser::g_msgid_stid;
map<string, time_t> StatLogItemsLineParser::g_invalid_msgid;

static const string static_basic_keys[] = 
{
    string("_zid_"),
    string("_sid_"),
    string("_pid_"),
    string("_gid_"),
    string("_stid_"),
    string("_sstid_"),
    string("_ts_"),
};

enum basic_key_type
{
    BKT_ZID = 0,
    BKT_SID,
    BKT_PID,
    BKT_GID,
    BKT_STID,
    BKT_SSTID,
    BKT_TS,
    BKT_END
};

StatLogItemsLineParser::StatLogItemsBasicKey StatLogItemsLineParser::s_statlog_basic_keys;

StatLogItemsLineParser::StatLogItemsLineParser()
{
    // m_dbserver_ip = "10.1.1.63";
    // m_dbserver_port = "19905";
    string dbserver_ip_port;
    vector<string> elems;
    StatCommon::stat_config_get("stat-dbserver-host0", dbserver_ip_port);

    StatCommon::split(dbserver_ip_port, ':', elems);
    m_dbserver_ip = elems[0];
    m_dbserver_port = elems[1];

    DEBUG_LOG("dbserver_ip: %s", m_dbserver_ip.c_str());
    DEBUG_LOG("dbserver_port: %s", m_dbserver_port.c_str());

    if(s_statlog_basic_keys.empty())
    {
        create_basic_keys();
    }

    m_query_fd = m_query_tc.connect(m_dbserver_ip, m_dbserver_port); 
    if(m_query_fd > 0)
        m_query_tc.set_timeout(20);
}

StatLogItemsLineParser::StatLogItemsLineParser(const string& statlog_line) : StatLogItemsParser(statlog_line)
{
    if(s_statlog_basic_keys.empty())
    {
        create_basic_keys();
    }
    m_query_fd = m_query_tc.connect(m_dbserver_ip, m_dbserver_port); 
    if(m_query_fd > 0)
        m_query_tc.set_timeout(20);
}

StatLogItemsLineParser::StatLogItemsLineParser(const char* statlog_line, size_t len) : StatLogItemsParser(statlog_line, len)
{
    if(s_statlog_basic_keys.empty())
    {
        create_basic_keys();
    }
    m_query_fd = m_query_tc.connect(m_dbserver_ip, m_dbserver_port); 
    if(m_query_fd > 0)
        m_query_tc.set_timeout(20);
}

StatLogItemsLineParser::~StatLogItemsLineParser()
{
}

void StatLogItemsLineParser::create_basic_keys()
{
    for(int i = BKT_ZID; i < BKT_END; ++i)
    {
        DEBUG_LOG("add statlog basic key: %s", static_basic_keys[i].c_str());
        s_statlog_basic_keys.insert(std::make_pair(string(static_basic_keys[i]), (int)i));
    }
}

void StatLogItemsLineParser::clear()
{
    m_items.clear();
    m_ops.clear();
}

int StatLogItemsLineParser::parse()
{
    if(get_items_string().empty())
        return -1;

    clear();

    vector<string> all_elems;

    StatCommon::split(get_items_string(), '\t', all_elems);
    if(all_elems.empty())
    {
        ERROR_LOG("bad format of stat log: %s", get_items_string().c_str());
        return -1;
    }

    // 解析key-value对，包括Op
    int basic_key_count = 0;
    vector<string>::iterator it = all_elems.begin();
    map<string, string> map_all_elems;    // 保存每行中所有的键值对

    bool stid_is_msgid = false;
    string msgid_str;
    for(; it != all_elems.end(); ++it)
    {
        vector<string> elems;
        StatCommon::split(*it, '=', elems);
        if(elems.size() < 2)
        {
            ERROR_LOG("bad size: %lu", elems.size());
            ERROR_LOG("bad format of stat log: %s", get_items_string().c_str());
            clear();
            return -1;
        }

        if(elems.size() > 2) {
            for(uint32_t i=2; i<elems.size(); i++) {
                elems[1] += ('=' + elems[i]);
            }
        }

        if(elems[0] == "_stid_" && elems[1] == "msgid_")  
        {
            stid_is_msgid = true;
        }
       
        if(elems[0] == "_sstid_")
        {
            msgid_str = elems[1];
        }

        map_all_elems.insert(pair<string, string>(elems[0], elems[1]));

        StatLogItemsBasicKey::iterator bit = s_statlog_basic_keys.find(elems[0]);
        if(bit != s_statlog_basic_keys.end())
            ++basic_key_count;
    }

    if(basic_key_count < BKT_END)
    {
        clear();
        ERROR_LOG("basic keys in %s error, should include _zid_ _sid_ _pid_ _gid_ _stid_ _sstid_ _ts_", get_items_string().c_str());
        return -1;
    }

    if(stid_is_msgid)     // 是msgid格式
    {

        if (map_all_elems.find("_sstid_") == map_all_elems.end())
            return -1;
        if (map_all_elems.find("_gid_") == map_all_elems.end())
            return -1;

        if(g_msgid_stid.find(msgid_str) != g_msgid_stid.end())     // 已经缓存有msgid和stid的对应关系
        {
            if (get_from_cache(msgid_str, map_all_elems) != 0)
                return -1;
        } 
        else
        {
            // 十分钟之内不会请求上次请求失败的msgid
            if (StatLogItemsLineParser::g_invalid_msgid.find(map_all_elems["_sstid_"]) != StatLogItemsLineParser::g_invalid_msgid.end())
            {
                if(time(0) - StatLogItemsLineParser::g_invalid_msgid[map_all_elems["_sstid_"]] >= 600)
                {
                    if (request_from_dbserver(map_all_elems) == -1)
                    {
                        //ERROR_LOG("again get %s from dbserver return error", map_all_elems["_sstid_"].c_str());
                        return -1;
                    }
                }
                else
                {
                    //StatLogItemsLineParser::g_invalid_msgid[map_all_elems["_sstid_"]] = time(0);
                    //ERROR_LOG("get %s from dbserver return error less than 10 minutes", map_all_elems["_sstid_"].c_str());
                    return -1;
                }
            }

            else if (request_from_dbserver(map_all_elems) == -1)
            {
                ERROR_LOG("first get %s from dbserver return error", map_all_elems["_sstid_"].c_str());
                return -1;
            }
        } 
    }
    else // 仍然使用stid字符串而不是msgid
    {
        it = all_elems.begin();
        for(; it != all_elems.end(); ++it)
        {
            vector<string> elems;
            StatCommon::split(*it, '=', elems);

            if(elems[0] != "_op_")
            {
                // StatLogItemsBasicKey::iterator bit = s_statlog_basic_keys.find(elems[0]);
                // if(bit != s_statlog_basic_keys.end())
                //     ++basic_key_count;

                m_items.insert(std::make_pair(elems[0], elems[1]));
            }
            else
            {
                if(parse_op(elems[1]) != 0)
                {
                    clear();
                    return -1;
                }
            }
        }
    }

    // 每一条日志都需要算人次。
    parse_op("count:1");
    m_items.insert(std::make_pair(string("1"), string("1")));

    return 0;
}

int StatLogItemsLineParser::parse_op(const string& op)
{
    vector<string> vec_ops;

    StatCommon::split(op, '|', vec_ops);
    if(vec_ops.empty())
    {
        ERROR_LOG("bad format of op string: %s", op.c_str());
        return -1;
    }

    vector<string> vec_op_key;
    for(vector<string>::iterator it = vec_ops.begin(); it != vec_ops.end(); ++it)
    {
        /*
        StatCommon::split(*it, ':', vec_op_key);
        if(vec_op_key.size() != 2)
        {
            ERROR_LOG("bad format of op: %s", op.c_str());
            return -1;
        }
        */

        StatLogItemsOpParser sli_op(*it);
        if(sli_op.parse() >= 0)
        {
            m_ops.push_back(sli_op);
        }
    }

    return 0;
}

// 成功时返回，生成的包大小，失败时返回-1。
int StatLogItemsLineParser::serialize(void* buf, size_t len)
{
    if(buf == NULL || len == 0 || m_items.empty() || m_ops.empty())
        return -1;

    int serial_size = 0;

    char* cur_ptr = (char*)buf;
    StatLogItemOpList::iterator it = m_ops.begin();
    for(; it != m_ops.end(); ++it)
    {
        int pkg_size = 0;
        uint32_t proto_id = (*it).get_proto_id();

        // 协议包头
        StatLogItemSerialHeader* pkg_header = (StatLogItemSerialHeader*)cur_ptr;
        char* pkg_body = pkg_header->body; 

        static uint32_t seq_no = 0; // 对每个协议包编号。
        pkg_header->proto_id = proto_id;
        pkg_header->seq_no = seq_no++;
        pkg_header->version = 0;
        pkg_header->ret_val = 0;

        if(proto_id == PROTO_ID_UPDATE)
        {
            // 先将各字段值取出来。
            const string& key1 = (*it).get_arg(StatLogItemsOpParser::SLI_OP_ARGS_KEY1);
            StatLogItemsMap::iterator kit = m_items.find(key1); // 找到key1, 并取其值。
            if(kit == m_items.end())
            {
                ERROR_LOG("cannot find key1 %s in %s", key1.c_str(), get_items_string().c_str());
                return -1;
            }
            string& key1_value = kit->second; 
            uint8_t key1_value_len = key1_value.size();
            bool key2_empty = true;;
            double key2_value;
            if((*it).get_args_count() > 1) // key2存在, 并取其值。
            {
                key2_empty = false;
                const string& key2 = (*it).get_arg(StatLogItemsOpParser::SLI_OP_ARGS_KEY2);
                kit = m_items.find(key2);
                if(kit == m_items.end())
                {
                    ERROR_LOG("cannot find key2 %s in %s", key2.c_str(), get_items_string().c_str());
                    return -1;
                }
                string& key2_value_str = kit->second;
                if(!isdigit(key2_value_str[0])) // key2_value肯定不为空，判断其是否是数字。
                {
                    ERROR_LOG("key2 value %s must be digit.", key2_value_str.c_str());
                    continue;
                }
                StatCommon::strtodigit(key2_value_str, key2_value);
            }
            double key1_value_double = 0.0;
            if(key2_empty)
            {
                if(key1_value_len == 0)
                {
                    ERROR_LOG("key1 value not exist.");
                    continue;
                }
                if(isdigit(key1_value[0]))
                    StatCommon::strtodigit(key1_value, key1_value_double);
                else
                    key1_value_double = 1.0;
            }
            string& stid = m_items[static_basic_keys[BKT_STID]];
            uint8_t stid_len = stid.size();
            string& sstid = m_items[static_basic_keys[BKT_SSTID]];
            uint8_t sstid_len = sstid.size();

            // 然后计算各字段大小之和。
            pkg_size += sizeof(StatLogItemSerialHeader); // 包大小包括包头大小
            pkg_size += (*it).get_serial_size(); // 先加上操作符的大小。
            pkg_size += sizeof(StatLogItemsSerialUpdate);
            pkg_size += (2 * sizeof(uint8_t)); // stid_len和sstid_len字段大小
            pkg_size += stid_len; // stid字段大小
            pkg_size += sstid_len; // sstid字段大小 
            pkg_size += sizeof(uint8_t); // key1_len字段大小
            pkg_size += key1_value_len; // key1值字段大小。

            if(len < size_t(serial_size + pkg_size)) // 空间不够。
            {
                ERROR_LOG("buffer not enough for packing proto data. current pkg size: %d, buffer size: %ld",
                        serial_size + pkg_size, len);
                return 0;
            }

            // 最后填充协议包中各个字段，基础项在日志中是一定存在的，所以在此不做检测。
            StatLogItemsSerialUpdate* pkg = (StatLogItemsSerialUpdate*)pkg_body;
            if(get_items_type() == StatLogItemsParser::SLI_CUSTOM)
                pkg->data_type = 2; // 自定义统计项都是天数据
            else
                pkg->data_type = 0; // 基础统计项都是分钟数据
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_TS]], pkg->timestamp);
            if((*it).get_op_type() == StatLogItemsOpParser::SLI_OP_COUNT)
            {
                pkg->value = 1.0;
                if(key1_value == "1")
                {
                    pkg_size -= key1_value_len;
                    key1_value_len = 0; // 此时协议中最后两个字段不需要。
                }
            }
            else if(key2_empty)
            {
                pkg->value = key1_value_double;
                pkg_size -= key1_value_len;
                key1_value_len = 0; // 协议中最后两个字段不需要。
            }
            else
                pkg->value = key2_value;
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_PID]], pkg->pid);
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_SID]], pkg->sid);
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_ZID]], pkg->zid);
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_GID]], pkg->gid);

            //ERROR_LOG("g=%d p=%d z=%d s=%d", pkg->gid, pkg->pid, pkg->zid, pkg->sid);

            cur_ptr = pkg->var;
            memcpy(cur_ptr, &stid_len, sizeof(uint8_t));
            cur_ptr += sizeof(uint8_t);
            memcpy(cur_ptr, stid.c_str(), stid_len);
            cur_ptr += stid_len;

            memcpy(cur_ptr, &sstid_len, sizeof(uint8_t));
            cur_ptr += sizeof(uint8_t);
            memcpy(cur_ptr, sstid.c_str(), sstid_len);
            cur_ptr += sstid_len;

            (*it).serialize(cur_ptr, len - (serial_size + pkg_size));
            cur_ptr += (*it).get_serial_size();

            memcpy(cur_ptr, &key1_value_len, sizeof(uint8_t));
            cur_ptr += sizeof(uint8_t);
            memcpy(cur_ptr, key1_value.c_str(), key1_value_len);
            cur_ptr += key1_value_len;
        }
        else if(proto_id == PROTO_ID_ADD)
        {
            string& stid = m_items[static_basic_keys[BKT_STID]];
            uint8_t stid_len = stid.size();
            string& sstid = m_items[static_basic_keys[BKT_SSTID]];
            uint8_t sstid_len = sstid.size();

            pkg_size += sizeof(StatLogItemSerialHeader); // 包大小包括包头大小
            pkg_size += sizeof(StatLogItemsSerialAdd);
            pkg_size += (2 * sizeof(uint8_t)); // stid_len和sstid_len字段大小
            pkg_size += stid_len; // stid字段大小
            pkg_size += sstid_len; // sstid字段大小 
            pkg_size += (*it).get_serial_size();

            if(len < size_t(serial_size + pkg_size)) // 空间不够。
            {
                ERROR_LOG("buffer not enough for packing proto data. current pkg size: %d, buffer size: %ld",
                        serial_size + pkg_size, len);
                return 0;
            }

            // 开始填充协议包中各个字段，基础项在日志中是一定存在的，所以在此不做检测。
            StatLogItemsSerialAdd* pkg = (StatLogItemsSerialAdd*)pkg_body;
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_PID]], pkg->pid);
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_SID]], pkg->sid);
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_ZID]], pkg->zid);
            StatCommon::strtodigit(m_items[static_basic_keys[BKT_GID]], pkg->gid);

            cur_ptr = pkg->var;
            memcpy(cur_ptr, &stid_len, sizeof(uint8_t));
            cur_ptr += sizeof(uint8_t);
            memcpy(cur_ptr, stid.c_str(), stid_len);
            cur_ptr += stid_len;

            memcpy(cur_ptr, &sstid_len, sizeof(uint8_t));
            cur_ptr += sizeof(uint8_t);
            memcpy(cur_ptr, sstid.c_str(), sstid_len);
            cur_ptr += sstid_len;

            (*it).serialize(cur_ptr, len - (serial_size + pkg_size));
            cur_ptr += (*it).get_serial_size();
        }

        pkg_header->pkg_len = pkg_size;

        serial_size += pkg_size;
    }

    return serial_size;
}

std::ostream& operator << (std::ostream& out, StatLogItemsLineParser& sli)
{
    out << sli.get_items_string() << "\n";

    StatLogItemsLineParser::StatLogItemOpList::iterator ops_it = sli.m_ops.begin();
    for(; ops_it != sli.m_ops.end(); ++ops_it)
    {
        StatLogItemsLineParser::StatLogItemsMap::const_iterator item_it;
        // 先输出基础项
        for(int i = BKT_ZID; i < BKT_END; ++i)
        {
            item_it = sli.m_items.find(static_basic_keys[i]);
            if(item_it != sli.m_items.end())
            {
                out << item_it->first << "=" << item_it->second << "\t";
            }
        }

        // 再输出key
        for(int i = 0; i < (int)(*ops_it).get_args_count(); ++i)
        {
            const string& key = (*ops_it).get_arg((StatLogItemsOpParser::StatLogItemOpArgIndex)i);
            item_it = sli.m_items.find(key);
            if(item_it != sli.m_items.end())
            {
                out << item_it->first << "=" << item_it->second << "\t";
            }
        }

        // 最后输出操作
        out << *ops_it << "\n";
    }

    return out;
}

int StatLogItemsLineParser::request_from_dbserver(map<string, string>& map_all_elems)
{
    // send to db

    StatRequestStidHeader searchstid_header;
    searchstid_header.len = sizeof(StatRequestStidHeader);
    searchstid_header.proto_id = STAT_PROTO_STID_REQUEST;
    searchstid_header.msgid = atoi(map_all_elems["_sstid_"].c_str());
    searchstid_header.game_id = atoi(map_all_elems["_gid_"].c_str());
    searchstid_header.ret = 0;

    // refer to StatDataMonitor::send_and_recv
    //TcpClient tc;
    if (m_query_fd < 0) {
        m_query_fd = m_query_tc.connect(m_dbserver_ip, m_dbserver_port); 
        if(m_query_fd < 0)
            return -1;
        m_query_tc.set_timeout(20);
    }

    int ret = m_query_tc.send(&searchstid_header, searchstid_header.len);
    if (ret < (int)searchstid_header.len)
        return -1;

    char buff[1024] = {0};
    ret = m_query_tc.recv(buff, sizeof(StatResponseStidHeader));
    if (ret < (int)sizeof(StatResponseStidHeader))
        return -1;


    StatResponseStidHeader * rep_header = (StatResponseStidHeader*)buff;
    char * rep_pkg = (char *)rep_header->body;
    if (rep_header->len > sizeof(buff))
    {
        ERROR_LOG("Alarmer's response is to large, > 1KB !!");
        return -1;
    }

    if (rep_header->ret != 0)  // 获取失败
    {
        time_t now;
        now = time(0);
        StatLogItemsLineParser::g_invalid_msgid.insert(pair<string, time_t>(map_all_elems["_sstid_"], now));
        return -1;
    }

    ret = m_query_tc.recv(rep_pkg, rep_header->len - sizeof(StatResponseStidHeader));
    if (ret < (int)(rep_header->len - sizeof(StatResponseStidHeader)))
        return -1;

    int count = 0;
    uint16_t len = sizeof(StatResponseStidHeader);
    struct db_request_str * str_temp = (struct db_request_str*)(buff+len);
    string first_elem(str_temp->body, 0, str_temp->str_len);
    len += str_temp->str_len;
    len++;
    count++;

    str_temp = (struct db_request_str*)(buff+len);
    string second_elem(str_temp->body, 0, str_temp->str_len);
    len += str_temp->str_len;
    len++;
    count++;

    string third_elem;
    if (len < rep_header->len)
    {
        str_temp = (struct db_request_str*)(buff+len);
        third_elem += string(str_temp->body, 0, str_temp->str_len);
        len += str_temp->str_len;
        len++;
        if (str_temp->str_len > 0)
            count++;
    }

    string fourth_elem;
    if (len < rep_header->len)
    {
        str_temp = (struct db_request_str*)(buff+len);
        fourth_elem += string(str_temp->body, 0, str_temp->str_len);
        len += str_temp->str_len;
        len++;
        if (str_temp->str_len > 0)
            count++;
    }

    map<string, string> cache_map;

    m_items.insert(pair<string, string>("_stid_", first_elem));
    m_items.insert(pair<string, string>("_sstid_", second_elem));

    cache_map.insert(pair<string, string>("_stid_", first_elem));
    cache_map.insert(pair<string, string>("_sstid_", second_elem));
    if (rep_header->type == 1 && count == 2)
    {
        cache_map.insert(pair<string, string>("type", "1"));
    }
    if (rep_header->type == 1 && count == 3)
    {
        m_items.insert(pair<string, string>("item", third_elem));

        cache_map.insert(pair<string, string>("type", "1"));
        cache_map.insert(pair<string, string>("item", third_elem));
        cache_map.insert(pair<string, string>("_op_", "item:item"));
    }
    if ((rep_header->type == 2 || rep_header->type == 3) && count == 3)
    {
        if (rep_header->type == 2)
        {
            cache_map.insert(pair<string, string>("_op_", "sum:"+third_elem));
            cache_map.insert(pair<string, string>("type", "2"));
        }
        else if (rep_header->type == 3)
        {
            cache_map.insert(pair<string, string>("_op_", "max:"+third_elem));
            cache_map.insert(pair<string, string>("type", "3"));
        }

        map<string, string>::iterator iter = map_all_elems.end();
        if ((iter = map_all_elems.find("_value_")) != map_all_elems.end())
        {
            m_items.insert(pair<string, string>(third_elem, map_all_elems["_value_"]));
            cache_map.insert(pair<string, string>(third_elem, map_all_elems["_value_"]));
        }
        else
        {
            m_items.insert(pair<string, string>(third_elem, "0"));
            cache_map.insert(pair<string, string>(third_elem, "0"));
        }
    }
    if ((rep_header->type == 3 || rep_header->type == 2) && count == 4)
    {
        m_items.insert(pair<string, string>("item", third_elem));
        // m_items.insert(pair<string, string>("_op_", "item_sum:item,"+fourth_elem));

        if (rep_header->type == 2)
        {
            cache_map.insert(pair<string, string>("type", "2"));
            cache_map.insert(pair<string, string>("_op_", "item_sum:item,"+fourth_elem));
        }
        else if (rep_header->type == 3)
        {
            cache_map.insert(pair<string, string>("type", "3"));
            cache_map.insert(pair<string, string>("_op_", "item_max:item,"+fourth_elem));
        }
        cache_map.insert(pair<string, string>("item", third_elem));

        // 实现 m_items.insert(pair<string, string>(fourth_elem, _value_));    
        map<string, string>::iterator iter = map_all_elems.end();
        if ((iter = map_all_elems.find("_value_")) == map_all_elems.end())
        {
            m_items.insert(pair<string, string>(fourth_elem, "0")); // FIXME
            cache_map.insert(pair<string, string>(fourth_elem, "0")); 
        }
        else
        {
            m_items.insert(pair<string, string>(fourth_elem, iter->second)); // FIXME
            cache_map.insert(pair<string, string>(fourth_elem, iter->second)); 
        }
    }

    map<string, string>::iterator iter = map_all_elems.begin();
    while (iter != map_all_elems.end())
    {
        if (iter->first == "_stid_" || iter->first == "_sstid_" || iter->first == "_value_")
        {
            iter++;
            continue;
        }
        m_items.insert(pair<string, string>(iter->first, iter->second));
        iter++;
    }

    // TODO : to handle op
    if (cache_map.find("_op_") != cache_map.end())
    {
        if(parse_op(cache_map["_op_"]) != 0)
        {
            clear();
            return -1;
        }
    }

    // insert msgid-stid in g_msgid_stid
    //map<string, string>::iterator it_test = cache_map.begin();
    //while (it_test != cache_map.end())
    //{
    //    DEBUG_LOG("%s    :    %s", it_test->first.c_str(), it_test->second.c_str());
    //    it_test++;
    //}
    g_msgid_stid.insert(pair<string, map<string, string> >(map_all_elems["_sstid_"], cache_map));

    if (g_invalid_msgid.find("_sstid_") != g_invalid_msgid.end())
        StatLogItemsLineParser::g_invalid_msgid.erase(map_all_elems["_sstid_"]);

    return 0;
}

int StatLogItemsLineParser::get_from_cache(const string& msgid_str, map<string, string>& map_all_elems)
{
    map<string, string> name_value;
    name_value = g_msgid_stid[msgid_str];
    //map<string, string>::iterator iter_test = name_value.begin();
    //while (iter_test != name_value.end())
    //{
    //    DEBUG_LOG("%s:    %s", iter_test->first.c_str(), iter_test->second.c_str());
    //    iter_test++;
    //}
    m_items.insert(pair<string, string>(static_basic_keys[BKT_PID], map_all_elems[static_basic_keys[BKT_PID]]));
    m_items.insert(pair<string, string>(static_basic_keys[BKT_SID], map_all_elems[static_basic_keys[BKT_SID]]));
    m_items.insert(pair<string, string>(static_basic_keys[BKT_ZID], map_all_elems[static_basic_keys[BKT_ZID]]));
    m_items.insert(pair<string, string>(static_basic_keys[BKT_GID], map_all_elems[static_basic_keys[BKT_GID]]));
    m_items.insert(pair<string, string>(static_basic_keys[BKT_TS] , map_all_elems[static_basic_keys[BKT_TS]]));
    map<string, string>::iterator iter = name_value.begin();
    if (name_value["type"] == "1" && name_value.size() == 3)
    {
        while (iter != name_value.end())
        {
            if (iter->first != "type")
            {
                m_items.insert(pair<string, string>(iter->first, iter->second));
            }
            iter++;
        }
    }
    else if (name_value["type"] == "1" && name_value.size() == 5) 
    {
        while (iter != name_value.end())
        {
            if (iter->first == "_op_")
            {
                if(parse_op(iter->second) != 0)
                {
                    clear();
                    return -1;
                }
                iter++;
                continue;
            }

            if (iter->first != "type")
            {
                m_items.insert(pair<string, string>(iter->first, iter->second));
            }
            iter++;
        }
    }
    else if ((name_value["type"] == "2" || name_value["type"] == "3") && name_value.size() == 5)
    {
        while (iter != name_value.end())
        {
            if (iter->first == "_op_")
            {
                if(parse_op(iter->second) != 0)
                {
                    clear();
                    return -1;
                }
                iter++;
            }
            else if (iter->first == "type")
            {
                iter++;
            }
            else 
            {
                if (iter->first != "_stid_" && iter->first != "_sstid_") 
                {
                    if(map_all_elems.find("_value_") == map_all_elems.end())
                    {
                        m_items.insert(pair<string, string>(iter->first, "0"));
                    }
                    else
                    {
                        m_items.insert(pair<string, string>(iter->first, map_all_elems["_value_"]));
                    }
                }
                else if (iter->first == "_stid_" || iter->first == "_sstid_")
                {
                        m_items.insert(pair<string, string>(iter->first, iter->second));
                }

                iter++;
            }
        }
    }
    else if ((name_value["type"] == "2" || name_value["type"] == "3") && name_value.size() == 6)
    {
        while (iter != name_value.end())
        {
            if (iter->first == "_op_")
            {
                if(parse_op(iter->second) != 0)
                {
                    clear();
                    return -1;
                }
                iter++;
            }
            else if (iter->first == "type")
                iter++;
            else
            {
                string value_str = name_value["_value_"];
                if (iter->first != "_stid_" && iter->first != "_sstid_" && iter->first != "item")
                {
                    if (map_all_elems.find("_value_") == map_all_elems.end())
                    {
                        m_items.insert(pair<string, string>(iter->first, "0")); 
                    }
                    else
                    {
                        m_items.insert(pair<string, string>(iter->first, map_all_elems["_value_"])); 
                    }
                    iter++;
                }
                //else if (iter->first == "item")
                //{
                //    m_items.insert(pair<string, string>(iter->first, iter->second)); 
                //    iter++;
                //}
                //else
                //    iter++;
                else
                {
                    m_items.insert(pair<string, string>(iter->first, iter->second));
                    iter++;
                }
            }
        }
    }
    return 0;
}

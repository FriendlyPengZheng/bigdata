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

using std::vector;

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
    if(s_statlog_basic_keys.empty())
    {
        create_basic_keys();
    }
}

StatLogItemsLineParser::StatLogItemsLineParser(const string& statlog_line) : StatLogItemsParser(statlog_line)
{
    if(s_statlog_basic_keys.empty())
    {
        create_basic_keys();
    }
}

StatLogItemsLineParser::StatLogItemsLineParser(const char* statlog_line, size_t len) : StatLogItemsParser(statlog_line, len)
{
    if(s_statlog_basic_keys.empty())
    {
        create_basic_keys();
    }
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
    for(; it != all_elems.end(); ++it)
    {
        vector<string> elems;
        StatCommon::split(*it, '=', elems);
        if(elems.size() != 2)
        {
            ERROR_LOG("bad format of stat log: %s", get_items_string().c_str());
            clear();
            return -1;
        }

        if(elems[0] != "_op_")
        {
            StatLogItemsBasicKey::iterator bit = s_statlog_basic_keys.find(elems[0]);
            if(bit != s_statlog_basic_keys.end())
                ++basic_key_count;

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

    if(basic_key_count < BKT_END)
    {
        clear();
        ERROR_LOG("basic keys in %s error, should include _zid_ _sid_ _pid_ _gid_ _stid_ _sstid_ _ts_", get_items_string().c_str());
        return -1;
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

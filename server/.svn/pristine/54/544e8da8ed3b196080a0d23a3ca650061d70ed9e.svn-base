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
#include <algorithm>

#include "stat_common.hpp"
#include "string_utils.hpp"
#include "statlog_items_op_parser.hpp"

StatLogItemsOpParser::OpStrTypeMap StatLogItemsOpParser::s_op_str_type_map;

StatLogItemsOpParser::StatLogItemsOpParser() : m_serial_size(0)
{
    if(s_op_str_type_map.empty())
    {
        create_op_str_type_map();
    }
}

StatLogItemsOpParser::StatLogItemsOpParser(const string& item_op) : StatLogItemsParser(item_op), m_serial_size(0)
{
    if(s_op_str_type_map.empty())
    {
        create_op_str_type_map();
    }
}

StatLogItemsOpParser::StatLogItemsOpParser(const char* item_op, size_t len) : StatLogItemsParser(item_op, len), m_serial_size(0)
{
    if(s_op_str_type_map.empty())
    {
        create_op_str_type_map();
    }
}

StatLogItemsOpParser::~StatLogItemsOpParser()
{
}

void StatLogItemsOpParser::create_op_str_type_map()
{
    const char* op_str[] =
    {
        "ucount",
        "count",
        "sum",
        "max",
        "set",
        "sum_distr",
        "max_distr",
        "set_distr",
        "ip_distr",
        NULL,
    };

    for(int i = SLI_OP_UCOUNT; i < SLI_OP_END; ++i)
    {
        DEBUG_LOG("add op string: %s", op_str[i]);
        s_op_str_type_map.insert(std::make_pair(string(op_str[i]), (StatLogItemOpType)i));
    }
}

int StatLogItemsOpParser::parse()
{
    if(get_items_string().empty())
        return -1;

    m_op_args.clear();
    m_serial_size = 0;

    vector<string> elems;
    StatCommon::split(get_items_string(), ':', elems);

    if(elems.empty() || elems.size() != 2)
    {
        ERROR_LOG("bad format of stat log op: %s", get_items_string().c_str());
        return -1;
    }

    // TODO: 这四种需要特殊处理。先写死，以后再优化
    if(elems[0] == "item_sum" || 
       elems[0] == "item_max" ||
       elems[0] == "item_set" || 
       elems[0] == "item") 
    {
        vector<string> vec_op_type;
        StatCommon::split(elems[0], '_', vec_op_type);
        if(vec_op_type.size() == 1) // item:key  -->  count:key
        {
            m_op_str = "count";
        }
        else // item_sum:key1:key2  -->  sum:key1,key2
        {
            m_op_str = vec_op_type[1];
        }
    }
    else
    {
        m_op_str = elems[0];
    }

    m_op_args_str = elems[1];

    elems.clear();
    StatCommon::split(m_op_args_str, ',', elems);
    if(elems.empty())
    {
        ERROR_LOG("bad format of stat log op: %s", get_items_string().c_str());
        return -1;
    }

    for(unsigned int i = 0; i < elems.size() && i < SLI_OP_ARGS_END; ++i)
    {
        m_op_args.push_back(elems[i]);
    }
    //m_op_args.shrink_to_fit();

    OpStrTypeMap::iterator it = s_op_str_type_map.find(m_op_str);
    if(it != s_op_str_type_map.end())
        m_op_type = it->second;

    m_serial_size = sizeof(StatLogItemOpSerial) + m_op_args_str.length();
    if(get_items_string() == "count:1")
        m_serial_size -= 1; // 特例：当为"count:1"时，不记录1的长度。

    return 0;
}

int StatLogItemsOpParser::serialize(void* buf, size_t len)
{
    if(buf == NULL || m_serial_size <= 0 || len < (size_t)m_serial_size)
        return -1;

    StatLogItemOpSerial *pkg = (StatLogItemOpSerial *) buf;

    pkg->op_type = m_op_type;
    if(m_op_type == SLI_OP_COUNT && m_op_args[0] == "1") // count:1时，不需要op_field字段。
        pkg->op_field_len = 0;
    else
        pkg->op_field_len = m_op_args_str.length();
    
    memcpy(pkg->op_filed, m_op_args_str.c_str(), pkg->op_field_len);

    return sizeof(StatLogItemOpSerial) + pkg->op_field_len;
}

std::ostream& operator << (std::ostream& out, StatLogItemsOpParser& op)
{
    out << "_op_=" << op.m_op_str << ":";

    vector<string>::iterator it = op.m_op_args.begin();
    for(int i = 0; it != op.m_op_args.end(); ++i, ++it)
    {
        if(i != 0)
            out << ",";

        out << *it;
    }

    return out;
}

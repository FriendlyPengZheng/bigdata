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

#ifndef STAT_LOG_ITEM_OP_HPP
#define STAT_LOG_ITEM_OP_HPP

#include <vector>
#include <string>
#include <unordered_map>
#include <stdexcept>

#include "statlog_items_parser.hpp"
#include <stat_proto_defines.hpp>

using std::vector;
using std::string;
using std::unordered_map;

class StatLogItemsOpParser : public StatLogItemsParser
{
public:
    typedef enum
    {
        SLI_OP_UCOUNT          = 0,
        SLI_OP_COUNT           = 1,
        SLI_OP_SUM             = 2,
        SLI_OP_MAX             = 3,
        SLI_OP_SET             = 4,
        SLI_OP_DISTR_SUM       = 5,
        SLI_OP_DISTR_MAX       = 6,
        SLI_OP_DISTR_SET       = 7,
        SLI_OP_IP_DISTR        = 8,
        SLI_OP_END
    }StatLogItemOpType;

    typedef enum 
    {
        SLI_OP_ARGS_KEY1 = 0,
        SLI_OP_ARGS_KEY2 = 1,
        SLI_OP_ARGS_END
    }StatLogItemOpArgIndex;

    StatLogItemsOpParser();
    StatLogItemsOpParser(const string& item_op);
    StatLogItemsOpParser(const char* item_op, size_t len);
    virtual ~StatLogItemsOpParser();

    virtual int parse();
    int parse(const string& items, StatLogItemsType type)
    {
        return StatLogItemsParser::parse(items, type);
    }
    int parse(const char* items, size_t len, StatLogItemsType type)
    {
        return StatLogItemsParser::parse(items, len, type);
    }
    virtual int serialize(void* buf, size_t len);

    StatLogItemOpType get_op_type() const
    {
        return m_op_type;
    }

    size_t get_args_count() const
    {
        return m_op_args.size();
    }

    const string& get_arg(StatLogItemOpArgIndex index) const
    {
        if(index >= SLI_OP_ARGS_KEY1 && index < SLI_OP_ARGS_END)
        {
            return m_op_args[index];
        }
        else
            throw std::out_of_range(string("parameter index out of range."));
    }
    const string& get_args_string()const
    {
        return m_op_args_str;
    }
    const int get_serial_size() const
    {
        return m_serial_size;
    }
    uint32_t get_proto_id() const // 返回该op所对应的协议号。
    {
        if(m_op_type >= SLI_OP_UCOUNT && m_op_type < SLI_OP_DISTR_SUM)
            return PROTO_ID_UPDATE;
        else if(m_op_type >= SLI_OP_DISTR_SUM && m_op_type < SLI_OP_END)
            return PROTO_ID_ADD;
        else 
            return 0;
    }

private:
    //items string = item_sum:key1,key2
    string m_op_str; // = item_sum
    string m_op_args_str; // = key1,key2
    vector<string> m_op_args; // m_op_args[0] = key1, m_op_args[1] = key2
    StatLogItemOpType m_op_type;

    int m_serial_size;

    // OP字符串与type的映射表。
    typedef unordered_map<string, StatLogItemOpType> OpStrTypeMap;

    static OpStrTypeMap s_op_str_type_map;
    static void create_op_str_type_map();

    friend std::ostream& operator << (std::ostream & out, StatLogItemsOpParser& op);
};

std::ostream& operator << (std::ostream & out, StatLogItemsOpParser& op);

#endif

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

#ifndef STAT_LOG_ITEMS_INTERFACE
#define STAT_LOG_ITEMS_INTERFACE

#include <string>

using std::string;

/**
 * 统计项日志解析抽象类。
 * 各种具体解析类均需继承该类
 */

class StatLogItemsParser
{
public:
    // 统计项类型
    typedef enum
    {
        SLI_BASIC, // 基础统计项
        SLI_CUSTOM // 自定义统计项
    }StatLogItemsType;

    StatLogItemsParser()
    {}
    StatLogItemsParser(const string& items) : m_items_str(items), m_items_type(SLI_BASIC)
    {
        //StatCommon::trim(m_items_str, "\n");
    }
    StatLogItemsParser(const char* items, size_t len)
    {
        m_items_str.assign(items, len);
        //StatCommon::trim(m_items_str, "\n");
    }
    virtual ~StatLogItemsParser()
    {}

    // 解析日志，默认的统计项类型是SLI_BASIC。
    // 可先调用set_items_type()改变统计项类型。
    virtual int parse() = 0;
    int parse(const string& items, StatLogItemsType type)
    {
        m_items_str = items;
        //StatCommon::trim(m_items_str, "\n");

        m_items_type = type;

        return this->parse();
    }
    int parse(const char* items, size_t len, StatLogItemsType type)
    {
        m_items_str.assign(items, len);
        //StatCommon::trim(m_items_str, "\n");

        m_items_type = type;

        return this->parse();
    }
    // 将解析好的日志序列化成相关协议.
    virtual int serialize(void* buf, size_t len) = 0;
    const string& get_items_string() const
    {
        return m_items_str;
    }
    StatLogItemsType get_items_type() const
    {
        return m_items_type;
    }
    void set_items_type(StatLogItemsType type)
    {
        if(type != SLI_BASIC && type != SLI_CUSTOM)
            return;
        m_items_type = type;
    }

private:
    string m_items_str;
    StatLogItemsType m_items_type;
};

#endif

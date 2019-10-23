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

#ifndef STAT_LOG_ITMES_HPP
#define STAT_LOG_ITMES_HPP

#include <iostream>
#include <string>
#include <list>
#include <map>
#include <unordered_map>
#include <tcp_client.hpp>

#include "statlog_items_parser.hpp"
#include "statlog_items_op_parser.hpp"

using std::string;
using std::list;
using std::map;
using std::unordered_map;

class StatLogItemsLineParser : public StatLogItemsParser
{
public:
    StatLogItemsLineParser();
    StatLogItemsLineParser(const string& statlog_line);
    StatLogItemsLineParser(const char* statlog_line, size_t len);
    virtual ~StatLogItemsLineParser();

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

private:
    int parse_op(const string& op);
    void clear();
    friend std::ostream& operator << (std::ostream& out, StatLogItemsLineParser& sli);

    int request_from_dbserver(map<string, string>& mm); 
    int get_from_cache(const string& msgid_str, map<string, string>& mm);
private:
    typedef unordered_map<string, int> StatLogItemsBasicKey;
    static StatLogItemsBasicKey s_statlog_basic_keys;
    static void create_basic_keys();

    typedef map<string, string> StatLogItemsMap;
    typedef list<StatLogItemsOpParser> StatLogItemOpList;

    StatLogItemsMap m_items; // 日志的基础部分

    StatLogItemOpList m_ops; // 操作符

    string m_dbserver_ip;
    string m_dbserver_port;
    static map<string, map<string, string> > g_msgid_stid;
    static map<string, time_t> g_invalid_msgid;

    TcpClient m_query_tc;   //connection for query msgid<==>stid from db-server
    int m_query_fd;
};

std::ostream& operator << (std::ostream& out, StatLogItemsLineParser& sli);

#endif

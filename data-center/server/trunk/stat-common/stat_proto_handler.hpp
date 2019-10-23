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

#ifndef PROTO_HANDLER_HPP
#define PROTO_HANDLER_HPP

#include <unordered_map>
#include <string>
#include <utility>

#include <stdint.h>

using std::unordered_map;
using std::string;

// 协议处理抽象基类，所有处理具体协议的类必须继承这个基类。
// 此基类同时负责管理所有子类对象，子类对象定义时，自动注册到基类里的子类管理器中。
class StatProtoHandler
{
public:
    StatProtoHandler(uint32_t proto_id, const char* proto_name);
    // 以proto_id（含）开头的连续proto_count个协议号，按1递增。
    // 调用该接口可以在一个类中支持多个协议号
    StatProtoHandler(uint32_t proto_id, const char* proto_name, unsigned proto_count);
    virtual ~StatProtoHandler()
    {}

	static int  process(int fd, const void* pkg);
    static void timer_event();
    static void timer_event(uint32_t proto_id);
	static void print_supported_proto();

    const string& get_proto_name() const
    {
        return m_proto_name;
    }
    uint32_t get_proto_id() const
    {
        return m_proto_id;
    }

private:
	// 子类实现此函数，对接收到的协议进行处理
	virtual int proc_proto(int fd, const void* pkg) = 0;		
	// 子类实现此函数，处理定时器事件
    virtual void proc_timer_event() = 0;

private:
	typedef unordered_map<uint32_t, StatProtoHandler*> ProtoHandlerMgr;

    // 主要操作是查找，故使用hash表，该hash表在程序初始化时创建并填充，退出时释放。
    static ProtoHandlerMgr s_handler_mgr;

    uint32_t m_proto_id;
	string m_proto_name;
};

#endif


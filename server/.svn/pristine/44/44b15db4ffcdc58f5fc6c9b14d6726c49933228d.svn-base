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

#include <sstream>
#include <stdexcept>

#include "stat_common.hpp"
#include "stat_proto_defines.hpp"
#include "stat_proto_handler.hpp"

using std::ostringstream;

StatProtoHandler::ProtoHandlerMgr StatProtoHandler::s_handler_mgr;

StatProtoHandler::StatProtoHandler(uint32_t proto_id, const char* proto_name) : m_proto_id(proto_id), m_proto_name(proto_name)
{
    std::pair<ProtoHandlerMgr::iterator, bool> r = s_handler_mgr.insert(std::make_pair(proto_id, this));
	if (r.second == false)
    {
		ostringstream oss;
		oss << "At Constructor StatProtoHandler: Duplicated ProtoID 0x" << std::hex << proto_id;
		throw std::logic_error(oss.str());
	}
}

StatProtoHandler::StatProtoHandler(uint32_t proto_id, const char* proto_name, unsigned proto_count) : m_proto_id(proto_id), m_proto_name(proto_name)
{
    if(proto_count == 0)
        ++proto_count; // 至少一个协议号

    for(unsigned i = 0; i < proto_count; ++i)
    {
        std::pair<ProtoHandlerMgr::iterator, bool> r = s_handler_mgr.insert(std::make_pair(proto_id + i, this));
        if (r.second == false)
        {
            ostringstream oss;
            oss << "At Constructor StatProtoHandler: Duplicated ProtoID 0x" << std::hex << proto_id + i;
            throw std::logic_error(oss.str());
        }
    }
}

int StatProtoHandler::process(int fd, const void* pkg)
{
	const StatProtoHeader* h = static_cast<const StatProtoHeader*>(pkg);

	ProtoHandlerMgr::iterator it = s_handler_mgr.find(h->proto_id);
	if (it != s_handler_mgr.end())
    {
		return it->second->proc_proto(fd, pkg);
	}

	ERROR_LOG("Unsupported proto: %X", h->proto_id);
	return -1;
}

void StatProtoHandler::timer_event()
{
	for(ProtoHandlerMgr::iterator it = s_handler_mgr.begin(); it != s_handler_mgr.end(); ++it)
    {
        it->second->proc_timer_event();
    }
}

void StatProtoHandler::timer_event(uint32_t proto_id)
{
	ProtoHandlerMgr::iterator it = s_handler_mgr.find(proto_id);
	if (it != s_handler_mgr.end())
    {
        it->second->proc_timer_event();
	}
    else
        ERROR_LOG("Unsupported proto: %X", proto_id);
}

void StatProtoHandler::print_supported_proto()
{
	DEBUG_LOG("===============================================");
	DEBUG_LOG("=                Supported Proto              =");

	for(ProtoHandlerMgr::iterator it = s_handler_mgr.begin(); it != s_handler_mgr.end(); ++it)
    {
		DEBUG_LOG("\t0x%X\t%s\t%p", it->first, it->second->m_proto_name.c_str(), it->second);
	}

	DEBUG_LOG("===============================================");
}


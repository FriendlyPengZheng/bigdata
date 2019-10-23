#include <sstream>
#include <stdexcept>

#include <iter_serv/log.h>

#include "./libant/inet/inet_utils.hpp"
#include "protohandler.hpp"

using namespace std;

uint8_t ProtoHandler::ms_rspbuf[ProtoHandler::max_packet_len];

ProtoHandler::ProtoHandler(uint32_t proto_id, const char* proto_name)
	: m_proto_name(proto_name)
{
	ProtoHdlrMgr& mgr = get_proto_hdlr_mgr();
	pair<ProtoHdlrMgr::iterator, bool> r = mgr.insert(std::make_pair(proto_id, this));
	if (r.second == false) {
		ostringstream oss;
		oss << "At Constructor ProtoHandler: Duplicated ProtoID 0x" << hex << proto_id;
		throw logic_error(oss.str());
	}
}

int ProtoHandler::get_proto_len(const void* pkg, int pkglen)
{
	if (static_cast<uint32_t>(pkglen) >= sizeof(ProtoHeader)) {
		const ProtoHeader* h = static_cast<const ProtoHeader*>(pkg);
		if ((h->len > sizeof(ProtoHeader)) && (h->len < max_packet_len)) {
			return h->len;
		}

		EMERG_LOG("invalid length: len=%u proto_id=0x%X", h->len, h->proto_id);
		return -1;
	}

	return 0;
}

int ProtoHandler::proc_proto(int peerfd, const void* pkg)
{
	pair<string, bool> ip = get_peer_ipaddr(peerfd);
	if (ip.second == false) {
		EMERG_LOG("get_peer_ipaddr: peerfd=%d errmsg=%s", peerfd, ip.first.c_str());
		// 如果处理该协议数据时，客户端刚好关掉了连接，可能出现得不到IP的情况，所有这里不要return -1。
		// TODO: 如果出现效率问题，这里可以优化成只需获取一次IP，然后记录到内存中。
	}

	const ProtoHeader* h = static_cast<const ProtoHeader*>(pkg);
	ProtoHdlrMgr& mgr = get_proto_hdlr_mgr();
	ProtoHdlrMgr::iterator it = mgr.find(h->proto_id);
	if (it != mgr.end()) {
		return it->second->proc(h, peerfd, ip.first);
	}

	EMERG_LOG("Unsupported proto: %X", h->proto_id);
	return -1;
}

void ProtoHandler::print_supported_proto()
{
	INFO_LOG("=======================");
	INFO_LOG("=   Supported Proto   =");
	INFO_LOG("=======================");

	ProtoHdlrMgr& mgr = get_proto_hdlr_mgr();
	for (ProtoHdlrMgr::iterator it = mgr.begin(); it != mgr.end(); ++it) {
		INFO_LOG("\t0x%X\t%s", it->first, it->second->m_proto_name.c_str());
	}
}


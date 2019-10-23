#ifndef WRITER_PROTOHANDLER_HPP_
#define WRITER_PROTOHANDLER_HPP_

#include <map>
#include <string>
#include <utility>

#include <stdint.h>

extern "C" {
#include <iter_serv/net_if.h>
}

#pragma pack(1)

/**
  * @brief header
  */
struct ProtoHeader {
	uint32_t len;      // packet length
	uint32_t proto_id; // protocol ID
	uint8_t  body[];   // protocol body
};

#pragma pack()

// 协议处理抽象基类，所有处理具体协议的类必须继承这个基类。
// 此基类同时负责管理所有子类对象，子类对象定义时，自动注册到基类里的子类管理器中。
class ProtoHandler {
public:
	ProtoHandler(uint32_t proto_id, const char* proto_name);
	virtual ~ProtoHandler()
		{ }

public:
	static int  get_proto_len(const void* pkg, int pkglen);
	static int  proc_proto(int peerfd, const void* pkg);
	static void print_supported_proto();
	static void proc_events()
		{ }
	static int send_packet(int peerfd, const void* data, uint32_t len)
		{ return net_send(peerfd, data, len); }

private:
	// 子类实现此函数，对接收到的协议进行处理
	virtual int proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip) = 0;		

private:
	typedef std::map<uint32_t, ProtoHandler*> ProtoHdlrMgr;

private:
	static ProtoHdlrMgr& get_proto_hdlr_mgr()
	{
		static ProtoHdlrMgr hdlr_mgr;

		return hdlr_mgr;
	}

private:
	static const uint32_t max_packet_len = 512 * 1024;

protected:
	static uint8_t ms_rspbuf[max_packet_len];

private:
	std::string m_proto_name;
};

#endif // WRITER_PROTOHANDLER_HPP_


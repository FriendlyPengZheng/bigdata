#ifndef WRITER_EVENTDATAHANDLER_HPP_
#define WRITER_EVENTDATAHANDLER_HPP_

#include <string>

#include <stdint.h>

#include "filewriter.hpp"
#include "protohandler.hpp"

// 处理接收到的统计数据的通用接口
class EventDataHandler : public ProtoHandler {
public:
	EventDataHandler(uint32_t proto_id, const char* proto_name)
			: ProtoHandler(proto_id, proto_name), m_fwriter("./data", "", 200)
		{ }

	// TODO: think about remove `this' from ms_hdlr_mgr at destruction
private:
	virtual int proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip);

private:
	FileWriter m_fwriter;
};

#endif // WRITER_EVENTDATAHANDLER_HPP_

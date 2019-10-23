#ifndef WRITER_XSEERHANDLER_HPP_
#define WRITER_XSEERHANDLER_HPP_

#include <string>

#include <stdint.h>

#include "filewriter.hpp"
#include "protohandler.hpp"

// 把战神联盟发过来的数据写入文件
class XseerHandler : public ProtoHandler {
public:
	XseerHandler(uint32_t proto_id, const char* proto_name)
			: ProtoHandler(proto_id, proto_name), m_fwriter("./data", "", 40)
		{ }

	// TODO: think about remove `this' from ms_hdlr_mgr at destruction
private:
	virtual int proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip);

private:
	FileWriter m_fwriter;
	int i;
};

#endif // WRITER_XSEERHANDLER_HPP_


#ifndef WRITER_UCOUNTHANDLER_HPP_
#define WRITER_UCOUNTHANDLER_HPP_

#include <string>

#include <stdint.h>

#include "filewriter.hpp"
#include "protohandler.hpp"

// 把ucount数据写到文件中
class UcountHandler : public ProtoHandler {
public:
	UcountHandler(uint32_t proto_id, const char* proto_name)
			: ProtoHandler(proto_id, proto_name), m_fwriter(".", "ucount")
		{ m_fwriter.set_sub_dir("old_stat_data"); }
	// TODO: think about remove `this' from ms_hdlr_mgr at destruction
private:
	virtual int proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip);

private:
	FileWriter m_fwriter;
};

#endif // WRITER_UCOUNTHANDLER_HPP_


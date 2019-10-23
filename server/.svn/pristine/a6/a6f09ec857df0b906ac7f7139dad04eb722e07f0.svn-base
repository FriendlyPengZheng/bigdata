#include <ctime>
#include <sys/time.h>

#include "uvaluehandler.hpp"

using namespace std;

namespace {
UvalueHandler uvalue(0x1004, "Uvalue");
}

#pragma pack(1)

struct UvalueData {
	uint32_t report_id;
	uint32_t timestamp;
	uint32_t key;
	uint32_t level;

	friend std::ostream& operator<<(std::ostream& out, const UvalueData& data);
};

inline std::ostream& operator<<(std::ostream& out, const UvalueData& data)
{ 
	out << data.report_id << '\t' << data.timestamp << '\t'
		<< data.key << '\t' << data.level << '\n';
    return out;
}

#pragma pack()

int UvalueHandler::proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip)
{
	const UvalueData* data = reinterpret_cast<const UvalueData*>(h->body);
	uint32_t bodylen = h->len - sizeof(ProtoHeader);
	// 确保收到数据的长度正确
	if (bodylen % sizeof(UvalueData)) {
		EMERG_LOG("invalid packet: proto_id=%X bodylen=%u peer_ip=%s",
					h->proto_id, bodylen, peer_ip.c_str());
		return -1;
	}
	
	int count = bodylen / sizeof(UvalueData);
	ProtoHeader* ph = reinterpret_cast<ProtoHeader*>(ms_rspbuf);
	ph->proto_id = h->proto_id;
	ph->len 	 = sizeof(ProtoHeader) + 4 * count;
	if (sizeof(ms_rspbuf) < ph->len) {
		EMERG_LOG("packet too big: reqlen=%u rsplen=%u", bodylen, ph->len);
		return -1;
	}
	
	uint32_t* res = reinterpret_cast<uint32_t*>(ph->body);
	for (int i = 0; i != count; ++i) {
		if (m_fwriter.write(data[i]) != -1) {
			res[i] = 0;
		} else {
			res[i] = -1;
		}
	}
	return send_packet(peerfd, ms_rspbuf, ph->len);
}


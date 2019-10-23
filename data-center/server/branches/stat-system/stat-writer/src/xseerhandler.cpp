#include <ctime>
#include <sstream>

#include <sys/time.h>

#include "xseerhandler.hpp"

using namespace std;

namespace {
XseerHandler xseer(0x1005, "Xseer");
}

#pragma pack(1)

struct XseerData {
	uint32_t timestamp;
	uint32_t event_type;
	int32_t  zone_id;
	int32_t  svr_id;
	uint32_t userid;
	uint32_t eventid;
	char     event_parm[];

	friend std::ostream& operator<<(std::ostream& out, const XseerData& data);
};

inline std::ostream& operator<<(std::ostream& out, const XseerData& data)
{ 
	out << data.timestamp << '\t' << data.zone_id << '\t' << data.svr_id << '\t'
		<< data.userid << '\t' << data.eventid << '\t' << data.event_parm << '\n';
    return out;
}

#pragma pack()

int XseerHandler::proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip)
{
	const uint32_t bufsz = 1024 * 16;
	static uint8_t buf[bufsz + 1];

	uint32_t bodylen = h->len - sizeof(ProtoHeader);
	if (bodylen > bufsz) {
		EMERG_LOG("packet too big: cmd=0x%X len=%u", h->proto_id, h->len);
		return -1;
	}
	memcpy(buf, h->body, bodylen);
	buf[bodylen] = '\0';

	const XseerData* data = reinterpret_cast<XseerData*>(buf);
	ProtoHeader* ph = reinterpret_cast<ProtoHeader*>(ms_rspbuf);
	ph->proto_id = h->proto_id;
	ph->len 	 = sizeof(ProtoHeader) + 4;
	if (sizeof(ms_rspbuf) < ph->len) {
		EMERG_LOG("packet too big: cmd=0x%X reqlen=%u rsplen=%u", h->proto_id, bodylen, ph->len);
		return -1;
	}

	// dirname & filename prefix is base on the 4th byte of `data->eventid`
	ostringstream oss;
	oss << hex << (data->eventid >> 24);
	m_fwriter.set_sub_dir(oss.str());
	oss << '-' << data->event_type;
	m_fwriter.set_filename_prefix(oss.str());
	uint32_t* res = reinterpret_cast<uint32_t*>(ph->body);
	if (m_fwriter.write(*data) != -1) {
		*res = 0;
	} else {
		*res = -1;
	}

	return send_packet(peerfd, ms_rspbuf, ph->len);
}


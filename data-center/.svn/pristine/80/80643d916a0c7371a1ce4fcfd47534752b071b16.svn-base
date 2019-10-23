#include <ctime>
#include <sys/time.h>

#include "eventdatahandler.hpp"

using namespace std;

namespace {
EventDataHandler eventdata(0x1006, "EventData");
}

#pragma pack(1)

struct EventData {
	uint32_t timestamp;
	uint32_t event_type;
	int32_t  zone_id;
	int32_t  svr_id;
	char     userid[128];
	uint32_t eventid;
	char     event_parm[];

	friend std::ostream& operator<<(std::ostream& out, const EventData& data);
};

inline std::ostream& operator<<(std::ostream& out, const EventData& data)
{ 
	out << data.timestamp << '\t' << data.zone_id << '\t' << data.svr_id << '\t'
		<< data.userid << '\t' << data.eventid << '\t' << data.event_parm << '\n';
    return out;
}

#pragma pack()

int EventDataHandler::proc(const ProtoHeader* h, int peerfd, const std::string& peer_ip)
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

	EventData* data = reinterpret_cast<EventData*>(buf);
	data->userid[sizeof(data->userid) - 1] = '\0';
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


#include <cerrno>
#include <cstring>

#include <arpa/inet.h>
#include <sys/socket.h>

#include "inet_utils.hpp"

using namespace std;

pair<string, bool> get_peer_ipaddr(int peerfd) throw()
{
	sockaddr_storage saddr;
	socklen_t slen = sizeof(saddr);
	if (getpeername(peerfd, reinterpret_cast<sockaddr*>(&saddr), &slen) == -1) {
		return make_pair(strerror(errno), false);
	}

	bool   ret = false;
	string ip;
	char   ipaddr[INET6_ADDRSTRLEN];
	switch (saddr.ss_family) {
	case AF_INET: {
		sockaddr_in* s = reinterpret_cast<sockaddr_in*>(&saddr);
		if (inet_ntop(AF_INET, reinterpret_cast<void*>(&s->sin_addr), ipaddr, sizeof(ipaddr))) {
			ret = true;
			ip  = ipaddr;
		} else {
			ip  = strerror(errno);
		}
		break;
	}
	case AF_INET6: {
		sockaddr_in6* s = reinterpret_cast<sockaddr_in6*>(&saddr);
		if (inet_ntop(AF_INET6, reinterpret_cast<void*>(&s->sin6_addr), ipaddr, sizeof(ipaddr))) {
			ret = true;
			ip	= ipaddr;
		} else {
			ip	= strerror(errno);
		}
		break;
	}
	default:
		ip = "Unsupported Address Family!";
		break;
	}
	
	return make_pair(ip, ret);
}


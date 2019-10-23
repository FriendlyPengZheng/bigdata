#ifndef LIBANT_INET_UTILS_HPP_
#define LIBANT_INET_UTILS_HPP_

#include <string>
#include <utility>

/**
 * @brief get ipaddr of the underlying peer sockfd
 * @param peerfd peer sockfd
 * @return true on success, false on failure. if true is returned, pair::first holds the ipaddr;
 *			if false is returned, pair::first holds the error message.
 */
std::pair<std::string, bool> get_peer_ipaddr(int peerfd) throw();

inline const std::string& get_local_ipaddr() throw()
{
	extern std::string g_local_ipaddr;
	return g_local_ipaddr;
}	

#endif // LIBANT_INET_UTILS_HPP_

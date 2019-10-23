// IterServer的so接口实现

#include <string>

extern "C" {
#include <iter_serv/log.h>
#include <iter_serv/config.h>
}

#include "protohandler.hpp"

using namespace std;

string g_local_ipaddr = config_get_strval("bind_ip");

extern "C" int init_service()
{
	DEBUG_LOG("WRITER (BuildTime: %s %s) INITING...", __TIME__, __DATE__);

	ProtoHandler::print_supported_proto();

	DEBUG_LOG("WRITER INITED SUCCESSFULLY!");
	BOOT_LOG(0, "StatWriter BuildTime: %s %s", __TIME__, __DATE__);
}

extern "C" int fini_service()
{
	DEBUG_LOG("WRITER FINALIZING...");

	DEBUG_LOG("WRITER FINALIZED SUCCESSFULLY!");
	return 0;
}

extern "C" int	get_pkg_len(int fd, const void* pkg, int pkglen)
{
	return ProtoHandler::get_proto_len(pkg, pkglen);
}

extern "C" int on_pkg_received(int sockfd, void* pkg, int pkglen)
{
	return ProtoHandler::proc_proto(sockfd, pkg);
}

// optional interface
/*extern "C" void proc_events()
{
	ProtoHandler::proc_events();
}*/

//----------------------------------------

/*!   *  Called each time on a new connection accepted. Optional interface.\n
  *   You can do something (ie. allocate memory) to the newly accepted sockfd here.\n
  *   You must return 0 on success, -1 otherwise.	  */
//int 	(*on_conn_accepted)(int sockfd);

/*!   *  Called each time on a connection closed. Optional interface.\n
  *   You can do something (ie. deallocate memory) to the closed sockfd here.\n   */
//void	(*on_conn_closed)(int sockfd);

/*!   *  Called to process multicast packages from the specified `mcast_ip` and `mcast_port`. \n
  *   Called once for each package. Optional interface.   */
//void	(*proc_mcast_pkg)(const void* data, int len);


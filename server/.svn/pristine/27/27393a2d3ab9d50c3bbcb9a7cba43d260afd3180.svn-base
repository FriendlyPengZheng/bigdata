#ifndef CLIENT_IP_FILTER_FACADE_H
#define CLIENT_IP_FILTER_FACADE_H

/**
 * use facade pattern, all filter check are done by calling do_filter()
 */

#include <netinet/in.h>

#include <list>
#include <string>

#include "c_client_ip_filter.h"

class c_client_ip_filter_facade
{
    //std::list<i_client_ip_filter *> m_filter_list;

    public:
        c_client_ip_filter_facade()
        {}
        ~c_client_ip_filter_facade()
        {}

        int do_filter(const std::string &ip);
        int do_filter(const sockaddr_in* addr);

        /*
        int add_filter(i_client_ip_filter *filter);
        int remove_filter(i_client_ip_filter *filter);
        int clear_filters();
        */
};

#endif // CLIENT_IP_FILTER_FACADE_H

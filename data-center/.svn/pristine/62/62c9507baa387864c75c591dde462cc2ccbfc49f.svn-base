#include <arpa/inet.h>

#include "c_client_ip_filter.h"
#include "c_client_ip_filter_facade.h"

using std::string;
using std::list;

int c_client_ip_filter_facade::do_filter(const string &ip)
{
    int ret;

    // check if it is internal ip
    c_client_internal_ip_filter internal_ip_filter(ip);

    ret = internal_ip_filter.check_rule();

    // check more rules here

    return ret;
}

// ipv4 only
int c_client_ip_filter_facade::do_filter(const sockaddr_in *addr)
{
    if(NULL == addr)
        return -1;

    char str_addr[16] = { 0 };
    if(NULL == inet_ntop(AF_INET, &addr->sin_addr, str_addr, sizeof(str_addr)))
    {
        return -1;
    }

    return do_filter(string(str_addr));
}

/*
int c_client_ip_filter_facade::add_filter(i_client_ip_filter *filter)
{
    if(NULL == filter)
        return -1;

    //check if it is alreadly exist
    list<i_client_ip_filter*>::iterator it;
    for(it = m_filter_list.begin(); it != m_filter_list.end(); ++it)
    {
        if(*it == filter)
            return 1;
    }

    m_filter_list.push_back(filter);

    return 0;
}

int c_client_ip_filter_facade::remove_filter(i_client_ip_filter *filter)
{
    m_filter_list.remove(filter);

    return 0;
}

int c_client_ip_filter_facade::clear_filters()
{
    m_filter_list.clear();

    return 0;
}
*/

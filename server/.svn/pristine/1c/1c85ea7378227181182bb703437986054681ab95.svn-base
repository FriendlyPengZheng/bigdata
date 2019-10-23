#include "c_client_ip_filter.h"

using std::string;

c_client_internal_ip_filter::c_client_internal_ip_filter(const string &ip) : m_str_ip(ip), 
    m_internal_ip_pattern("192.168")
{
}

c_client_internal_ip_filter::~c_client_internal_ip_filter()
{
}

int c_client_internal_ip_filter::check_rule()
{
    return m_str_ip.compare(0, m_internal_ip_pattern.size(), m_internal_ip_pattern);
}

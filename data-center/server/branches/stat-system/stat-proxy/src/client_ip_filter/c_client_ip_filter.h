#ifndef CLIENT_IP_FILTER_H
#define CLIENT_IP_FILTER_H

/**
 * use strategy pattern, all filters should inherit from i_client_ip_fileter
 * and implement check_rule().
 */

#include <string>

class i_client_ip_filter
{
    public:
        virtual int check_rule() = 0;
};

class c_client_internal_ip_filter : public i_client_ip_filter
{
    std::string m_str_ip;
    const std::string m_internal_ip_pattern;

    public:
        c_client_internal_ip_filter(const std::string &ip);
        virtual ~c_client_internal_ip_filter();

        virtual int check_rule();
};

#endif // CLIENT_IP_FILTER_H

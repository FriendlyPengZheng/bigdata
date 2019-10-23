
#ifndef ROUTE_IMPL_H_20091112
#define ROUTE_IMPL_H_20091112

#include <map>
#include <ext/hash_map>
#include "../i_route.h"

class c_route_impl : public i_route
{
public:
    c_route_impl();
    virtual ~c_route_impl();
    virtual int init();
    virtual int add_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key,int is_atomic);
    virtual int update_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key);
    virtual int set_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key);
    virtual int remove_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid);
    virtual int enum_rules(unsigned short channel_id, rule_item_t* p_recv_buffer,int buffer_len);
    virtual int get_rule(unsigned short channel_id, unsigned int msgid,unsigned int* p_server_key);
    virtual int get_rules_count(unsigned short channel_id);
    virtual int get_last_error();
    virtual int uninit();
    virtual int release();

protected:
    typedef __gnu_cxx::hash_map<unsigned int,unsigned int> rule_hash_map_t;
	typedef std::map<unsigned short, rule_hash_map_t> chnl_rule_map_t; 
    typedef rule_hash_map_t::iterator rule_hash_map_iterator_t;
    typedef chnl_rule_map_t::iterator chnl_rule_map_iterator_t;
    typedef rule_hash_map_t::const_iterator rule_hash_map_const_iterator_t;
    typedef chnl_rule_map_t::const_iterator chnl_rule_map_const_iterator_t;

protected:
    int m_inited; /**< module status */
    int m_last_error;
    chnl_rule_map_t m_chnl_rule_map;
};

#endif//ROUTE_IMPL_H_20091112

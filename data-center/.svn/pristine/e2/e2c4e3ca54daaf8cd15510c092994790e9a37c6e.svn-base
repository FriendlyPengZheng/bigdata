
#include <stdlib.h>
#include <new>
#include "route_impl.h"

int create_route_instance(i_route** pp_instance)
{
    if(NULL == pp_instance)
    {
        return -1;
    }

    c_route_impl* p_instance = new (std::nothrow) c_route_impl();
    if(NULL == p_instance)
    {
        return -1;
    }
    else
    {
        *pp_instance = dynamic_cast<i_route*>(p_instance);
        return 0;
    }
}

c_route_impl::c_route_impl()
{
    m_chnl_rule_map.clear();
    m_last_error = 0;
    m_inited = 0;
}

c_route_impl::~c_route_impl()
{
    uninit();/**< implicit call uninit() */
}

int c_route_impl::init()
{
    if(m_inited)
    {
        return 0;
    }

    m_chnl_rule_map.clear();
    m_last_error = 0;
    m_inited = 1;

    return 0;
}

/**
 *@brief add rules in batch mode
 *@param channel_id channel id
 *@param start_msgid start message id
 *@param end_msgid end message id
 *@param is_atomic whether the add operation is atomic
 *@return rules count added or -1 if error occur
 *@note if is_atomic is zero,the rule will be added as much as possible,else all rules will be added or nothing
 *@note will be added. however,no update operation will be taken.
 */
int c_route_impl::add_rule(unsigned short channel_id,unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key,int is_atomic)
{
    if(!m_inited)
    {
        return -1;
    }

    ///end_msgid should large or equal than start_msgid
    if(start_msgid <= 0 || end_msgid < start_msgid || server_key <= 0)
    {
        return -1;
    }

    ///check msg count,should not exceed 1024 * 128
    if(end_msgid - start_msgid > 1024 * 128)
    {
        return -1;
    }

	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		m_chnl_rule_map.insert(make_pair(channel_id,rule_hash_map_t()));
	}

    ///check total rule count,should not exceed 1024 * 128
    if(m_chnl_rule_map[channel_id].size() > 1024 * 128)
    {
        return -1;
    }

    if(is_atomic)
    {
        for(unsigned int index = start_msgid; index <= end_msgid; index++)
        {
            rule_hash_map_const_iterator_t iterator = m_chnl_rule_map[channel_id].find(index);
            if(iterator != m_chnl_rule_map[channel_id].end())
            {
                return -1;
            }
        }
    }//if(is_atomic)

    int rule_count = 0;
    for(unsigned int index = start_msgid; index <= end_msgid; index++)
    {
        rule_hash_map_const_iterator_t iterator = m_chnl_rule_map[channel_id].find(index);
        if(iterator == m_chnl_rule_map[channel_id].end())
        {
            ///insert into hash map
            m_chnl_rule_map[channel_id].insert(std::make_pair(index,server_key));
            rule_count++;
        }
    }

    return rule_count;
}

/**
 *@brief update rules in batch mode
 *@param channel_id channel id
 *@param start_msgid start message id
 *@param end_msgid end message id
 *@return rules count updated or -1 if error occur
 *@note no insert operation will be token
 */
int c_route_impl::update_rule(unsigned short channel_id,unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key)
{
    if(!m_inited)
    {
        return -1;
    }

    ///end_msgid should large or equal than start_msgid
    if(start_msgid <= 0 || end_msgid < start_msgid || server_key <= 0)
    {
        return -1;
    }

    int rule_count = 0;
	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		return rule_count;
	}

    for(unsigned int index = start_msgid; index <= end_msgid; index++)
    {
        rule_hash_map_iterator_t iterator = m_chnl_rule_map[channel_id].find(index);
        if(iterator != m_chnl_rule_map[channel_id].end())
        {
            ///update server key
            iterator->second = server_key;
            rule_count++;
        }
    }

    return rule_count;
}

/**
 *@brief set rules in batch mode
 *@param channel_id channel id
 *@param start_msgid start message id
 *@param end_msgid end message id
 *@return rules count added or updated ,or -1 if error occur
 *@note will be added. will be updated.
 */
int c_route_impl::set_rule(unsigned short channel_id,unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key)
{
    if(!m_inited)
    {
        return -1;
    }

    ///end_msgid should large or equal than start_msgid
    if(start_msgid <= 0 || end_msgid < start_msgid || server_key <= 0)
    {
        return -1;
    }

    int rule_count = 0;
	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		return rule_count;
	}

    for(unsigned int index = start_msgid; index <= end_msgid; index++)
    {
        rule_hash_map_iterator_t iterator = m_chnl_rule_map[channel_id].find(index);
        if(iterator == m_chnl_rule_map[channel_id].end())
        {
            ///insert into hash map
            m_chnl_rule_map[channel_id].insert(std::make_pair(index,server_key));
            rule_count++;
        }
        else
        {
            ///update server key
            iterator->second = server_key;
            rule_count++;
        }
    }

    return rule_count;
}

/**
 *@brief remove rules in batch mode
 *@param channel_id channel id
 *@param start_msgid start message id
 *@param end_msgid end message id
 *@return rules count removed or -1 if error occur
 */
int c_route_impl::remove_rule(unsigned short channel_id,unsigned int start_msgid,unsigned int end_msgid)
{
    if(!m_inited)
    {
        return -1;
    }

    ///end_msgid should large or equal than start_msgid
    if(start_msgid <= 0 || end_msgid < start_msgid)
    {
        return -1;
    }

    int rule_count = 0;
	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		return rule_count;
	}

    for(unsigned int index = start_msgid; index <= end_msgid; index++)
    {
        rule_hash_map_iterator_t iterator = m_chnl_rule_map[channel_id].find(index);
        if(iterator != m_chnl_rule_map[channel_id].end())
        {
            ///remove the item
            m_chnl_rule_map[channel_id].erase(iterator);
            rule_count++;
        }
    }

    return rule_count;
}

/**
 *@brief enumerate all rules
 *@param channel_id channel id
 *@param p_recv_buffer receive buffer for rules
 *@param buffer_len receive buffer length
 *@return rule count in receive buffer or -1 if error occur
 */
int c_route_impl::enum_rules(unsigned short channel_id,rule_item_t* p_recv_buffer,int buffer_len)
{
    if(!m_inited)
    {
        return -1;
    }

    if(NULL == p_recv_buffer || buffer_len < 1)
    {
        return -1;
    }

    int data_len =  0;
	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		return data_len;
	}

    rule_hash_map_const_iterator_t iterator = m_chnl_rule_map[channel_id].begin();
    for(; iterator != m_chnl_rule_map[channel_id].end(); ++iterator)
    {
        if(data_len >= buffer_len)
        {
            break;
        }

        (p_recv_buffer + data_len)->msgid = iterator->first;
        (p_recv_buffer + data_len)->server_key = iterator->second;
        data_len++;
    }

    return data_len;
}

/**
 *@brief find corresponding server_key according to msgid
 *@param channel_id channel id
 *@param msgid message id
 *@param p_server_key [out] receive buffer
 *@return 0 if corresponding item exists,-1 if not or error occur
 */
int c_route_impl::get_rule(unsigned short channel_id,unsigned int msgid,unsigned int* p_server_key)
{
    if(!m_inited)
    {
        return -1;
    }

	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		return -1;
	}

    rule_hash_map_const_iterator_t iterator = m_chnl_rule_map[channel_id].find(msgid);
    if(iterator != m_chnl_rule_map[channel_id].end())
    {
        *p_server_key = iterator->second;
        return 0;
    }
    else
    {
        return -1;
    }
}

int c_route_impl::get_rules_count(unsigned short channel_id)
{
    if(!m_inited)
    {
        return -1;
    }

	chnl_rule_map_const_iterator_t iter = m_chnl_rule_map.find(channel_id);
	if (iter == m_chnl_rule_map.end()) 
	{
		return 0;
	}

    return m_chnl_rule_map[channel_id].size();
}

int c_route_impl::get_last_error()
{
    return m_last_error;
}

int c_route_impl::uninit()
{
    if(!m_inited)
    {
        return -1;
    }

    m_chnl_rule_map.clear();
    m_last_error = 0;
    m_inited = 0;

    return 0;
}

int c_route_impl::release()
{
    delete this;
    return 0;
}

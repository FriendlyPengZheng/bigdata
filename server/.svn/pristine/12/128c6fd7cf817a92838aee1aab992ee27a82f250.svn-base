/**
 * @file c_timer.cpp
 * @brief  定时器实现
 * @author Hansel
 * @version 1.0.0
 * @date 2010-12-22
 */

#include <new>

#include "c_timer.h"

using namespace std;

int create_timer_instance(i_timer **pp_instance)
{
	if (pp_instance == NULL) {
		return -1; 
	}   
	c_timer *p_timer = new (nothrow)c_timer();
	if (p_timer == NULL) {
		return -1; 
	}   
	*pp_instance = dynamic_cast<i_timer *>(p_timer);

	return 0;
}

c_timer::c_timer(): m_inited(0)
{
}

int c_timer::init()
{
	if (1 == m_inited) {
		return -1;
	}
	INIT_LIST_HEAD(&m_timer_list.tlist);
	m_inited = 1;	
	return 0;
}

c_timer::timer_id_t c_timer::add(int interval, int (*fnc)(void *p_data), void *p_data)
{
	if (NULL == fnc || 1 != m_inited) {
		return -1;
	}
	timer_node_t *tmp;
	tmp = new (nothrow)timer_node_t;
	if (tmp == NULL) {
		return -1;
	}
	tmp->item.next_run = time(NULL) + interval;
	tmp->item.interval = interval;
	tmp->item.process = fnc;
	tmp->item.p_data = p_data;
	list_add(&(tmp->tlist), &(m_timer_list.tlist));
	return reinterpret_cast<timer_id_t>(tmp);
}

int c_timer::del(const timer_id_t &item_id)
{
	if (item_id == 0) {
		return -1;
	}
	timer_node_t *tmp;;
	tmp = reinterpret_cast<timer_node_t *>(item_id);
	list_del(&(tmp->tlist));
	delete tmp;
	return 0;
}

int c_timer::check()
{
	if (m_inited != 1) {
		return -1;
	}
	timer_node_t *pos;
	list_for_each_entry(pos, &m_timer_list.tlist, tlist) {
		if (time(NULL) >= (pos->item.next_run)) {
			/// update the next run time anyway.
			pos->item.next_run = time(NULL) + pos->item.interval;
			if ((pos->item.process(pos->item.p_data)) != 0) {
				return -1;
			}
		}
	}
	return 0;
}

int c_timer::uninit()
{
	if (m_inited != 1) {
		return -1;
	}
	struct list_head *pos;
	struct list_head *tmp_pos;
	timer_node_t *tmp;
	list_for_each_safe(pos, tmp_pos, &m_timer_list.tlist) {
		tmp = list_entry(pos, timer_node_t, tlist);
		list_del(&(tmp->tlist));
		delete tmp;
	}
	return 0;
}

int c_timer::release()
{
	delete this;
	return 0;
}

c_timer::~c_timer()
{
	if (m_inited == 1) {
		uninit();
	}
}

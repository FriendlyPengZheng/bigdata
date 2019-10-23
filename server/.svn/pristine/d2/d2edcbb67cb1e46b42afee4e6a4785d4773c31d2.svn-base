/**
 * @file c_timer.h
 * @brief  定时器实现接口定义
 * @author Hansel
 * @version 1.0.0
 * @date 2010-12-22
 */

#ifndef C_TIMER_H_2010_12_22
#define C_TIMER_H_2010_12_22

#include "list.h"
#include "i_timer.h"

typedef struct {
	time_t next_run;
	time_t interval;
	int (*process)(void *data);
	void *p_data;
}timer_item_t;

typedef struct {
	struct list_head tlist;
	timer_item_t item;
}timer_node_t;

class c_timer : public i_timer
{
	public:
		c_timer();
		~c_timer();
		virtual int init();
		virtual timer_id_t add(int interval, int (*fnc)(void *p_data), void *p_data);
		virtual int del(const timer_id_t &item);
		virtual int check();
		virtual int uninit();
		virtual int release();

	private:
		int m_inited;
		timer_node_t m_timer_list;
};

#endif//C_TIMER_H_2010_12_22

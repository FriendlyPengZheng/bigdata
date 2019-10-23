/**
 * @file i_timer.h
 * @brief  定时器接口定义
 * @author Hansel
 * @version 1.0.0
 * @date 2010-12-22
 */

#ifndef I_TIMER_H_2010_12_22
#define I_TIMER_H_2010_12_22

#include <time.h>

struct i_timer 
{
	/**
	 * @brief  定时器标识
	 */
	typedef long timer_id_t;

	/**
	 * @brief  初始化
	 *
	 * @returns  0成功 -1失败 
	 */
	virtual int init() = 0;

	/**
	 * @brief 添加一个定时器 
	 *
	 * @param interval 定时器处理时间间隔。
	 * @param fnc 定时器处理回调函数。
	 * @param p_data 定时器处理回调函数实参。
	 *
	 * @returns  成功返回添加的定时器的标识，失败返回-1。 
	 */
	virtual timer_id_t add(int interval, int (*fnc)(void *p_data), void *p_data) = 0;

	/**
	 * @brief  根据定时器的指针从链表删除一个定时器对象。
	 *
	 * @param item 指向需要删除对象的指针。
	 *
	 * @returns  0成功 -1失败 
	 */
	virtual int del(const timer_id_t &item) = 0;

	/**
	 * @brief  检查链表中的定时器对象,执行满足条件定时器的回调函数。
	 *
	 * @returns   0成功 -1失败
	 */
	virtual int check() = 0;

	/**
	 * @brief  反初始化
	 *
	 * @returns  0成功 -1失败 
	 */
	virtual int uninit() = 0;

	/**
	 * @brief 释放定时器实例 
	 *
	 * @returns  0成功 -1失败 
	 */
	virtual int release() = 0;
};

int create_timer_instance(i_timer **pp_instance);

#endif//I_TIMER_H_2010_12_22

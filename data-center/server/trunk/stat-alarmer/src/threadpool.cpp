/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2014, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  tomli<tomli@taomee.com>
 *   @date    2014-12-26
 * =====================================================================================
 */

#include "threadpool.hpp"  
#include "data_buffer.hpp"

#include <pthread.h>
#include <map>

using std::map;
using std::pair;

extern int g_stop;
map<pthread_t, int> ThreadPool::m_busy_idle;
pthread_mutex_t ThreadPool::m_stat_mutex = PTHREAD_MUTEX_INITIALIZER;  

void ThreadPool::init(int thread_num)  
{  
    m_thread_num = thread_num;
    create();
}

void* ThreadPool::thread_func(void* param)  
{  
    pthread_t tid = pthread_self();  
    while(!g_stop)   // FIXME
    {  
        TaskData task_data = DataBuffer::get();

        set_busy(tid);
        task_data.process();
        set_idle(tid);
    }  
    return (void*)0;  
}  

int ThreadPool::add_task(TaskData task_data)  
{  
    DataBuffer::set(task_data);  

    return 0;  
}  

int ThreadPool::create()  
{ 
    for(int i = 0; i < m_thread_num;i++)  
    {  
        pthread_t tid = 0;  
        pthread_create(&tid, NULL, thread_func, (void*)NULL);  
        set_idle(tid);  
    }  
    return 0;  
}  

void ThreadPool::uninit()
{
    // stop_all();
}

// 退出的时候先把ctask清空，然后等待忙碌的线程结束，等到所有线程都空闲就stopall
int ThreadPool::stop_all()   // 非线程安全
{  
    DataBuffer::clear();

    map<pthread_t, int>::iterator iter = m_busy_idle.begin();  
    while (iter != m_busy_idle.end()) 
    {
        // pthread_join(iter->first, NULL);
        pthread_cancel(iter->first);
        iter++;
    }
    m_busy_idle.clear();

    return 0;  
}  

void ThreadPool::set_idle(pthread_t tid)
{
    pthread_mutex_lock(&m_stat_mutex);

    map<pthread_t, int>::iterator iter = m_busy_idle.begin();
    while (iter != m_busy_idle.end())
    {
        if (iter->first == tid)
        {
            iter->second = 0;
            break;
        }

        iter++;
    }

    if (iter == m_busy_idle.end())
        m_busy_idle.insert(pair<pthread_t, int>(tid, 0));

    pthread_mutex_unlock(&m_stat_mutex);
}

void ThreadPool::set_busy(pthread_t tid)
{
    pthread_mutex_lock(&m_stat_mutex);

    map<pthread_t, int>::iterator iter = m_busy_idle.begin();
    while (iter != m_busy_idle.end())
    {
        if (iter->first == tid)
        {
            iter->second = 1;
            break;
        }

        iter++;
    }
    pthread_mutex_unlock(&m_stat_mutex);
}

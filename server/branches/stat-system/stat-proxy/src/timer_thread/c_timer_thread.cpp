/**
 * @file c_timer_thread.cpp
 * @brief thread work as a timer, run a specified function priodically.
 * @author Ian Guo<ianguo@taomee.com>
 * @date 2013-06-13
 */

#include <sys/time.h>
#include <sys/select.h>

#include "c_timer_thread.h"

void* c_timer_thread::timer_thread_func(void *param)
{
    if(NULL == param)
        return NULL;
    c_timer_thread *p = static_cast<c_timer_thread *>(param);
    if(NULL == p)
        return NULL;

    p->real_thread_func();

    return param;
}

c_timer_thread::c_timer_thread() : m_thread_id(0), m_sec(0), m_usec(100)
{
    init();
}

c_timer_thread::c_timer_thread(long sec, long usec) : m_thread_id(0), m_sec(sec), m_usec(usec)
{
    init();
}

c_timer_thread::~c_timer_thread()
{
}

// hide parent release()
int c_timer_thread::release()
{
    return 0;
}

int c_timer_thread::start()
{
    // already running
    if(m_thread_id > 0)
        return -1;

    if(pthread_create(&m_thread_id, NULL, c_timer_thread::timer_thread_func, this) != 0)
    {
        return -1;
    }

    return 0;
}

int c_timer_thread::stop()
{
    if(0 == m_thread_id)
    {
        return -1;
    }

    pthread_cancel(m_thread_id);
    pthread_join(m_thread_id, NULL);

    m_thread_id = 0;

    return 0;
}

void c_timer_thread::real_thread_func()
{
    while(1)
    {
        pthread_testcancel();

        struct timeval interval;
        interval.tv_sec = m_sec;
        interval.tv_usec = m_usec;

        select(0, NULL, NULL, NULL, &interval);
        check();
    }
}

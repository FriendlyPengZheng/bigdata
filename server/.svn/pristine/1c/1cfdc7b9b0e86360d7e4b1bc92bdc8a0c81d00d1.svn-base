/**
 * @file c_timer_thread.h
 * @brief thread work as a timer, run a specified function priodically.
 * @author Ian Guo<ianguo@taomee.com>
 * @date 2013-06-13
 */

#ifndef C_TIMER_THREAD_H
#define C_TIMER_THREAD_H

#include <timer/c_timer.h>
#include <pthread.h>

class c_timer_thread : public c_timer
{
    pthread_t m_thread_id;

    long m_sec;
    long m_usec;

public:
    static void* timer_thread_func(void *param);

    c_timer_thread();
    c_timer_thread(long sec, long usec);
    virtual ~c_timer_thread();

    int start();
    int stop();

    virtual int release();

private:
    void real_thread_func();
};


#endif //C_TIMER_THREAD_H

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

#ifndef THREADPOOL_HPP  
#define THREADPOOL_HPP  

#include <map>  
#include <string>  
#include <pthread.h>  

#include "task_data.hpp"

using std::map;  

/** 
 * 线程池 
 * **/  
class ThreadPool  
{  
    public:  
        ThreadPool()
        {}

        ~ThreadPool()
        {
            stop_all();
        }

        void init(int thread_num);
        void uninit();

        int create();          //创建所有的线程  
        int add_task(TaskData task_data);      //把任务添加到线程池中  
        int stop_all();  

    private:  
        static void* thread_func(void* param); //新线程的线程函数  

        static void set_idle(pthread_t tid);
        static void set_busy(pthread_t tid);

    private:  
        int m_thread_num;                            //线程池中启动的线程数             
        static map<pthread_t, int> m_busy_idle;     // 1 : busy ; 0 : idle
        static pthread_mutex_t m_stat_mutex;
};  
#endif  

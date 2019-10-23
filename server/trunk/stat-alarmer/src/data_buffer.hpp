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

#ifndef DATA_BUFFER_H
#define DATA_BUFFER_H

#include <list>
#include <pthread.h>

#include "task_data.hpp"

using std::list;

class DataBuffer
{
    public:
        static void init();
        static void set(TaskData data);
        static TaskData get();
        static void clear();

        static int backup();
        static int restore();

    private:
        static list<TaskData>   m_taskdata_list;
        static pthread_mutex_t  m_mutex;
        static pthread_cond_t   m_cond;
};
#endif


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

#ifndef CTASK_HPP
#define CTASK_HPP

#include <cstring>
#include <unistd.h>

#include <stat_common.hpp>
#include <stat_proto_handler.hpp>

#include "stat_alarmer_defines.hpp"

class TaskData
{
    private:
        int m_data_len;              
        char* m_data_ptr;           

    public:
        TaskData()
        {
            m_data_len = 0;
            m_data_ptr = NULL;
        }

        TaskData(int len, const char *data)
        {
            m_data_len = len;
            m_data_ptr = new char[len];
            if (m_data_ptr == NULL)
                ERROR_LOG("TaskData new data failed.");

            memcpy(m_data_ptr, data, len);
        }

        TaskData(const TaskData& task_data);

        TaskData& operator=(const TaskData& task_data);

        ~TaskData()
        {
            delete[] m_data_ptr;
            m_data_ptr = NULL;
        }

        bool backup(int fd);

        bool restore(int fd);

        // 在此处处理stat-center发来的告警数据包
        void process()
        {
            // 从task_data中取出数据，交给process函数执行
            StatProtoHandler::process(0, static_cast<const void*>(m_data_ptr));  
        }
};

#endif

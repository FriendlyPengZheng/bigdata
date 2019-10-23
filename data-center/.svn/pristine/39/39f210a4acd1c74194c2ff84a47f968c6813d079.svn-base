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

#include "task_data.hpp"

TaskData::TaskData(const TaskData& task_data)
{
    m_data_len = task_data.m_data_len;
    m_data_ptr = new char[m_data_len];
    if (m_data_ptr == NULL)
        ERROR_LOG("TaskData new data failed.");

    memcpy(m_data_ptr , task_data.m_data_ptr, m_data_len);
}

TaskData& TaskData::operator=(const TaskData& task_data)
{
    if (this == &task_data)
        return *this;

    delete[] m_data_ptr;
    m_data_ptr = NULL;

    m_data_len = task_data.m_data_len;
    m_data_ptr = new char[m_data_len];
    if (m_data_ptr == NULL)
        ERROR_LOG("TaskData new data failed.");

    memcpy(m_data_ptr, task_data.m_data_ptr, m_data_len);

    return *this;
}

bool TaskData::backup(int fd)
{
    if (::write(fd, &m_data_len, sizeof(m_data_len)) != sizeof(m_data_len))
        return false; 

    if (::write(fd, m_data_ptr, m_data_len) != m_data_len)
        return false; 

    return true;
}

bool TaskData::restore(int fd)
{
    if (::read(fd, &m_data_len, sizeof(m_data_len)) != sizeof(m_data_len))
        return false; 

    m_data_ptr = new char[m_data_len];
    if (m_data_ptr == NULL)
        ERROR_LOG("restore new data failed.");

    if (::read(fd, m_data_ptr, m_data_len) != m_data_len)
        return false; 

    return true;
}

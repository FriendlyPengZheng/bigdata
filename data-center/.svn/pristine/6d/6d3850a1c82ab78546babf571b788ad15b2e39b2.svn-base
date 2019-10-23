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

#include <fcntl.h>

#include "data_buffer.hpp"
#include "task_data.hpp"

extern int g_stop;

list<TaskData>   DataBuffer::m_taskdata_list;
pthread_mutex_t  DataBuffer::m_mutex;
pthread_cond_t   DataBuffer::m_cond;

void DataBuffer::init()
{
    pthread_mutex_lock(&m_mutex);
    m_taskdata_list.clear();
    pthread_mutex_unlock(&m_mutex);

    restore();
    pthread_mutex_init(&m_mutex,0);
    pthread_cond_init(&m_cond,0);
    DEBUG_LOG(" init  m_taskdata_list    size  :  %d", m_taskdata_list.size());
}

void DataBuffer::set(TaskData task_data)
{
    pthread_mutex_lock(&m_mutex);
    m_taskdata_list.push_back(task_data);    
    pthread_mutex_unlock(&m_mutex);
    pthread_cond_signal(&m_cond);
}

TaskData DataBuffer::get()
{
    pthread_mutex_lock(&m_mutex);
    while (m_taskdata_list.empty())
        pthread_cond_wait(&m_cond, &m_mutex);
    //while (m_taskdata_list.empty())
    //{
    //    if (!g_stop)
    //        pthread_cond_wait(&m_cond,&m_mutex);
    //    else
    //    {
    //        pthread_mutex_unlock(&m_mutex);
    //        return TaskData();
    //    }
    //}
    
    TaskData task_data;
    task_data = *(m_taskdata_list.begin());
    m_taskdata_list.pop_front();
    pthread_mutex_unlock(&m_mutex);
    return task_data;
}

void DataBuffer::clear()
{
    pthread_mutex_lock(&m_mutex); // FIXME

    backup();
    
    m_taskdata_list.clear();

    pthread_mutex_unlock(&m_mutex);
}

int DataBuffer::backup()
{
    if (m_taskdata_list.size() == 0)
        return 0;

    DEBUG_LOG("backup  alarm   number :%d-----------", m_taskdata_list.size());

    int fd = ::open(".backup_alarm", O_CREAT | O_TRUNC | O_RDWR | O_APPEND | O_NONBLOCK, S_IRWXU);

    if(fd < 0)
    {   
        DEBUG_LOG("open file %s failed", ".backup_alarm");
        return -1; 
    }   

    list<TaskData>::iterator iter = m_taskdata_list.begin();
    int alarm_count = 0;
    while (iter != m_taskdata_list.end())
    {   
        alarm_count++;
        iter++;
    }   

    if (::write(fd, &alarm_count, sizeof(alarm_count)) != sizeof(alarm_count))
    {   
        ::close(fd);
        return -1; 
    }   

    if (alarm_count != 0)
    {   
        iter = m_taskdata_list.begin();
        while (iter != m_taskdata_list.end())
        {   
            TaskData task_data = *iter;

            if(!(*iter).backup(fd))
            {
                ::close(fd);
                return -1;
            }
            
            iter++;
        }
    }

    ::close(fd);
    return 0;
}

int DataBuffer::restore()
{
    int fd = ::open(".backup_alarm", O_RDONLY);
    if (fd < 0)
    {   
        DEBUG_LOG("open file %s failed", ".backup_alarm");
        return -1; 
    }   

    DEBUG_LOG("restore alarm_info data from file %s", ".backup_alarm");

    int alarm_count = 0;    

    struct stat st;
    fstat(fd, &st);
    if (st.st_size == 0)
    {
        alarm_count = 0;
        ::close(fd);
        DEBUG_LOG("restore  alarm_count : %d", alarm_count);
        return 0;
    }

    if (::read(fd, &alarm_count, sizeof(alarm_count)) != sizeof(alarm_count))  
    {   
        ::close(fd);
        return -1; 
    }   
    DEBUG_LOG("restore  alarm_count : %d", alarm_count);

    for (int i=0; i<alarm_count; i++)
    {   
        TaskData task_data;
        if(!task_data.restore(fd))
        {   
            ::close(fd);
            return -1; 
        }   

        m_taskdata_list.push_back(task_data);
    }   

    DEBUG_LOG("init  alarm  number :%d-----------", m_taskdata_list.size());

    ::close(fd);

    return 0;
}

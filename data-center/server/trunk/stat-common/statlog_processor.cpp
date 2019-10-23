/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

#include <cstdio>
#include <cerrno>
#include <cstring>

#include "statlog_processor.hpp"

StatLogProcessor::StatLogProcessor() : m_continue_last(true), m_rename_failed(false), m_ignore_rename_err(false)
{
}

StatLogProcessor::~StatLogProcessor()
{
    m_statlog_files.clear();
}

int StatLogProcessor::setup_work_path(const string& inputpath, 
        const string& invalidpath, 
        const string& outputpath,
        const string& failedpath)
{
    bool ret = true;

    if(inputpath.empty() || invalidpath.empty() || outputpath.empty() || failedpath.empty())
        return -1;

    m_inputpath = inputpath;
    m_invalidpath = invalidpath;
    m_outputpath = outputpath;
    m_failedpath = failedpath;

    // 任何一步失败，则返回失败。
    ret = ret && StatCommon::makedir(m_inputpath);
    ret = ret && StatCommon::makedir(m_invalidpath);
    ret = ret && StatCommon::makedir(m_outputpath);
    ret = ret && StatCommon::makedir(m_failedpath);

    if(ret)
        m_flister.open(m_inputpath);

    return (ret ? 0 : -1);
}

inline int StatLogProcessor::rename_statlog(FnHolder::iterator& it, std::string& save_path, bool important)
{
    string outfile = save_path + "/" + (it->second).get_file_name();

    int ret = rename((it->first).c_str(), outfile.c_str());
    if(ret != 0)
    {
        ERROR_LOG("move: %s to %s failed, %d:%s", (it->first).c_str(), 
                outfile.c_str(), errno, strerror(errno));
        if(important && !m_ignore_rename_err && errno != ENOENT)
        {
            m_rename_failed = true;
            m_rename_failed_fname = (it->second).get_file_name();

            return -1;
        }
    }

    m_rename_failed = false;
    m_statlog_files.erase(it);

    return 0;
}

/** 
 * 该函数由定时器调用
 */
int StatLogProcessor::process()
{
	//处理文件容器，文件以map key的方式放入m_statlog_files中
    list_statlog();

    m_outbox_filecount = m_statlog_files.size();

    FnHolder::iterator it;
    string last_file;
    size_t offset;
    // 上次未处理完的，继续处理。
    if(m_continue_last)
    {
        switch(get_statlog_preserved(last_file, offset))
        {
            case SLP_OK:
                it = m_statlog_files.find(last_file);
                if(it != m_statlog_files.end())
                {
                    int p = process_statlog(it->first, it->second, offset);
                    if(p >= SLP_OK) // 处理成功，移动文件。
                    {
                        if(rename_statlog(it, m_outputpath) < 0)
                        {
                            return -1;
                        }
                    }

                    if(p != SLP_OK) // 处理失败或者不需要继续，比如流量到了最大值。
                        return p;
                }
                break;
            case SLP_OK_BREAK:
                m_continue_last = true;
                break;
            case SLP_ERR_BREAK:
                ERROR_LOG("process preserve statlog failed.");
                m_continue_last = true;
                return -1;
            default: // 下次不需要再处理preserved文件
                m_continue_last = false;
                break;
        }
    }

    // 在一些情况下，文件发送完成，但是无法移动，比如硬盘故障，可读但不可写。
    // 为了避免该文件再次加入到发送队列，重复发送，需一直重试移动该文件，
    // 如一直不成功，则不继续发送文件。
    // TODO：增加相关告警。
    if(m_rename_failed) // 有已发送完成的文件，但是无法移动。
    {
        string infile = m_inputpath + "/" + m_rename_failed_fname;
        string outfile = m_outputpath + "/" + m_rename_failed_fname; 

        if(rename(infile.c_str(), outfile.c_str()) != 0 && errno != ENOENT)
        {
            ERROR_LOG("move: %s to %s failed, %s\n\tPlease move it manuanly.", infile.c_str(), 
                    outfile.c_str(), strerror(errno));
            return -1;
        }

        m_rename_failed = false;
        m_rename_failed_fname.clear();
        m_statlog_files.erase(infile);
    }

    int ret = 0;
    for(it = m_statlog_files.begin(); it != m_statlog_files.end();)
    {
        ret = process_statlog(it->first, it->second, 0);

        if(ret == SLP_OK) // 处理成功，而且继续处理下一个文件。
        {
            if(rename_statlog(it, m_outputpath) < 0)
                break;

            it = m_statlog_files.begin();
        }
        else if(ret == SLP_OK_BREAK) // 处理成功，但本时间片内不继续处理下一个文件。
        {
            rename_statlog(it, m_outputpath);

            break;
        }
        else if(ret == SLP_ERR_BREAK)
        {
            break; // 处理失败，本时间片结束，下一个时间片继续。
        }
        else if(ret == SLP_ERROR) // 处理失败，将文件移动到failed目录，而且不继续处理下一文件。
        {
            rename_statlog(it, m_failedpath, false);
            break;
        }
        else // 代码不应该到这里。
        {
            ERROR_LOG("BUG: unsupported return value of process_statlog().");
            break;
        }
    }

    return ret;
}

void StatLogProcessor::list_statlog()
{
    // 只有当容器为空时才添加。
    //if(!m_statlog_files.empty())
    //    return;

	// 从头开始重新读取目录下所有文件
    m_flister.start();

    string fpath;
    string file_name;

    unsigned int count = 0;
	// 只返回普通文件（DT_REG），如果已经没有文件了，则返回空串
    while(m_flister.next(file_name))
    {
        fpath = m_inputpath + "/" + file_name;

        // 如果已经存在，不重复添加。
        FnHolder::iterator it = m_statlog_files.find(fpath);
        if(it != m_statlog_files.end())
            continue;

		//检测文件是否有读写权限
        if(check_permission(fpath))
        {
            StatLogFileMmap mmap_file(fpath, file_name);

            // 不是有效的日志文件, 移动到invalid目录。
			// parse_file_name()解析文件名
            if(mmap_file.parse_file_name() == false)
            {
                string finvalid = m_invalidpath + "/" + file_name;
                // FIXME: rename可能失败。
                if(rename(fpath.c_str(), finvalid.c_str()) != 0)
                {
                    ERROR_LOG("move: %s to %s failed, %d:%s", fpath.c_str(), 
                            finvalid.c_str(), errno, strerror(errno));
                }
                continue;
            }

            if(mmap_file.parse_file_stat() && sanity_check_file(mmap_file))
            {
                mmap_file.init();
                m_statlog_files.insert(std::make_pair(fpath, mmap_file));
                ++count;

                // 当容器达到最大值，暂时不再继续添加。
                if(count % sc_max_files == 0)
                {
                    DEBUG_LOG("sending queue is full, add more files next time.");
                    return;
                }
            }
        }
    }
}

inline bool StatLogProcessor::check_permission(const string& fpath)
{
    if(access(fpath.c_str(), R_OK | W_OK) == 0)
        return true;

    return false;
}

int StatLogProcessor::get_outbox_filecount()
{
    return m_outbox_filecount;
}

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

#ifndef STAT_LOG_PROCESSOR_HPP
#define STAT_LOG_PROCESSOR_HPP

#include <cstdint>
#include <string>
#include <map>

#include <stat_common.hpp>
#include <filelister.hpp>
#include <fs_utils.hpp>

#include "statlog_file_mmap.hpp"

using std::string;
using std::map;

/**
 * 日志文件处理父类，采用模板方法模式，由子类实现相关处理函数。
 * 父类从inputpath中找到文件，检查文件格式（子类实现），若文件格式不对
 * 则将文件移动到invalid目录中，若正确，则调用处理函数（子类实现）处理之。
 * 处理失败，移动到failed目录中，成功则移动到output目录中。
 * 该类非线程安全。
 */

class StatLogProcessor
{
public:
    enum
    {
        SLP_OK = 0,
        SLP_OK_BREAK = 1,
        SLP_ERR_BREAK = -1,
        SLP_ERROR = -2
    };
    // key: 文件绝对路径名，包括文件名；value: StatLogFileMmap 
	typedef std::map<std::string, StatLogFileMmap> FnHolder; // filename holder

    StatLogProcessor();
    virtual ~StatLogProcessor();

    int get_outbox_filecount();

private:
    // 列出所有符合条件的日志文件，并添加到容器中。
    void list_statlog();
    int rename_statlog(FnHolder::iterator& it, std::string& save_path, bool important = true);

    // 检查文件名，判断是否是statlogger写的文件，如果不是，文件将移动到invalid目录中。
	//virtual bool parse_filename(const std::string& fn, std::string& filetype, time_t& ts) const = 0; 
    // 检查文件是否在等待处理。
    virtual bool sanity_check_file(const StatLogFile& slf) const = 0;

    // 处理上次未处理完的文件。
    virtual int get_statlog_preserved(std::string& fname, size_t& offset) = 0;
    // 处理日志文件
    virtual int process_statlog(const std::string& fn, StatLogFile& mmap_file, size_t offset) = 0;

    bool check_permission(const string& fpath);

private:
    // 输入目录，若输入文件格式不对，则移动到invalid目录中。
	string m_inputpath;
    string m_invalidpath;

    // 输出目录，若处理失败，则移动到failed目录中。
    string m_outputpath;
    string m_failedpath;

	FileLister m_flister;

    // 容器的最大容量。
    static const unsigned int sc_max_files = 2000;
	FnHolder m_statlog_files;

    bool m_continue_last;

    bool m_rename_failed; // 用于记录是否有移动文件失败,增加该变量是为了减少低效的文件名的比较.
    string m_rename_failed_fname; // 记录移动失败的文件名。

    int m_outbox_filecount;

protected:
    // 处理函数入口，子类应在定时器中调用该函数。
    int process();

    int setup_work_path(const string& inputpath, const string& invalidpath, 
            const string& outputpath, const string& failedpath);

protected:
    bool m_ignore_rename_err; // 忽略移动文件产生的错误，可由子类决定是否开启，默认不忽略。
};

#endif

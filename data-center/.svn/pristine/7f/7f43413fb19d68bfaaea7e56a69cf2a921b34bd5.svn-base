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

#ifndef STAT_LOG_ARCHIVE_HPP
#define STAT_LOG_ARCHIVE_HPP

#include <string>
#include <map>
#include <set>
#include <ctime>

using std::string;

/**
 * 本类功能：
 * 1. 将符合条件的文件打成tar包。
 * 2. 将符合条件的文件删除。
 *
 * 该类处理的需求相对固定，所以将参数都定义为数据成员，
 * 以减少每次调用函数时新建临时变量个数。
 */
class StatLogArchive
{
public:
    StatLogArchive(const string& workpath, const string& rexp1, const string& rexp2, const string& prefix) : 
        m_workpath(workpath), m_rexp1(rexp1), m_rexp2(rexp2), m_archive_prefix(prefix)
    {}
    explicit StatLogArchive(const string& workpath):m_workpath(workpath){}
    virtual ~StatLogArchive()
    {}

    /**
     * it will change default m_workpath
     * make sure you really want to do that
     */
    void set_work_path(const string& workpath)
    {
        m_workpath = workpath;
    }
    // 生成tar包, 只是简单调用系统命令。
    int do_archive();
    // 将离现在time_span秒前修改过的文件删除，包括目录，子目录，文件。
    int rm_archive(unsigned time_span);

    void add_clear_path(const std::string& path);

private:
    int _rm_archive(unsigned time_span, const string& root_path);
    void sort_files_tomap();
    void get_file_date(const std::string& filename,std::string& result);
    void tar_map_files();
    std::string get_archive_name(tm& tm_time,const std::string& file_date);
    int open_and_write(const std::string& filename,const std::string& write_buf);

private:
    string m_workpath;
    string m_rexp1; // 搜索要处理文件的字串，该字串两端会加上_*。
    string m_rexp2; // 搜索要处理文件的字串，该字串两端会加上_*。
    string m_archive_prefix; // 生成文件的前缀，后面会加上时间。
    string m_curr_clear_path;//当前需要清理的文件夹

    std::map< std::string,std::set<std::string> > m_tar_files;
    std::set<std::string> m_clear_path_set;
};


#endif

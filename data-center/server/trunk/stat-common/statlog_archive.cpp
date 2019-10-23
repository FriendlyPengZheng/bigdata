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

#include <cerrno>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <sys/types.h>
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sstream>
#include <iomanip>
#include <fcntl.h>

#include "stat_common.hpp"
#include "fs_utils.hpp"
#include "statlog_archive.hpp"
#include "filelister.hpp"
#include "string_utils.hpp"

#define MAX_SORT_DAY 60

/**
 * @brief  get date string from filename which contain m_rexp1 or m_rexp2
 * eg: 1_basic_1392134400 the result is 1392134400
 */
void StatLogArchive::get_file_date(const std::string& filename,std::string& result){
    std::size_t locate;
    if((locate = filename.find(m_rexp1)) == std::string::npos){
        if((locate = filename.find(m_rexp2)) == std::string::npos){
            return;
        }else{
            locate += m_rexp2.size() + 1;
        }
    }else{
        locate += m_rexp1.size() + 1;
    }
    if(locate > filename.size()){
        return;
    }

    result.assign(filename.begin()+locate,filename.end());
}

void StatLogArchive::sort_files_tomap(){
    if(m_workpath.empty()){
        return;
    }
    FileLister file;
    file.open(m_workpath);
    file.start();

    std::string filename,date;
    tm *day_tm;
    char buf[16];

    while(file.next(filename)){
        date.clear();
        this->get_file_date(filename,date);
        if(date.empty()){
            continue;
        }
        time_t t = (time_t)(atol(date.c_str())+8*3600);
        day_tm = gmtime(&t);

        memset(buf,0,sizeof(buf));
        snprintf(buf, sizeof(buf)/sizeof(char), "%02d%02d%02d", day_tm->tm_year+1900, day_tm->tm_mon+1, day_tm->tm_mday);
        this->m_tar_files[buf].insert(filename);
    }

    //file.close();
}

std::string StatLogArchive::get_archive_name(tm& tm_time,const std::string& file_date){
    std::string archive_name = m_workpath + "/" + file_date;
    StatCommon::makedir(archive_name);

    archive_name += "/";
    char dir_name[7],log_date[9];
    snprintf(log_date, sizeof(log_date)/sizeof(char), "%4d%02d%02d", tm_time.tm_year+1900, tm_time.tm_mon+1, tm_time.tm_mday);
    snprintf(dir_name, sizeof(dir_name)/sizeof(char), "%02d%02d%02d", tm_time.tm_hour, tm_time.tm_min, tm_time.tm_sec);
    archive_name += m_archive_prefix + "_log"+ log_date + "_" + file_date +dir_name + ".tar.gz";

    return archive_name;
}

int StatLogArchive::open_and_write(const std::string& filename,const std::string& write_buf){
    int fd = open(filename.c_str(),O_CREAT|O_RDWR|O_TRUNC,S_IRWXU | S_IRWXG | S_IRWXO);
    if(fd == -1){
        return -1;
    }

    if(write(fd,write_buf.c_str(),write_buf.size()) == -1){
        close(fd);
        return -1;
    }

    close(fd);
    return 0;
}

void StatLogArchive::tar_map_files(){
    time_t now = time(0);
    struct tm tm_now = {0};
    localtime_r(&now, &tm_now);

    std::string day_files,tmp_file = m_workpath + "/.tmp";
    std::map< std::string, std::set<std::string> >::iterator it = this->m_tar_files.begin(),tmp_it;
    int count = 0;

    for(; it != this->m_tar_files.end();) {
        if(++count > MAX_SORT_DAY){
            break;
        }

        tmp_it = it;
        ++it;
        std::string archive_name = this->get_archive_name(tm_now,tmp_it->first);

        day_files.clear();//Initialization before use
        std::set<std::string>::iterator iter = tmp_it->second.begin();
        for(; iter != tmp_it->second.end(); ++iter) {
            day_files += *iter;
            day_files += '\n';
        }

        if(day_files.empty() || this->open_and_write(tmp_file,day_files) != 0){
            continue;
        }

        std::string cmd = "cd " + m_workpath + ";" +
            "/bin/tar czf " + archive_name + " --files-from=.tmp" + " --exclude=*.tar.gz --remove-files >/dev/null 2>&1";

        DEBUG_LOG("create archive: %s", cmd.c_str());
        system(cmd.c_str());

        this->m_tar_files.erase(tmp_it);

    }
    unlink(tmp_file.c_str());
}

int StatLogArchive::do_archive()
{
    if(m_workpath.empty() || m_archive_prefix.empty() ||
            m_rexp1.empty() || m_rexp2.empty())
        return -1;

    this->sort_files_tomap();
    this->tar_map_files();

    return 0;
}

int StatLogArchive::rm_archive(unsigned time_span)
{
    std::set<std::string>::iterator it = this->m_clear_path_set.begin();
    for(; it != this->m_clear_path_set.end(); ++it) {
        m_curr_clear_path = *it;
        _rm_archive(time_span,*it);
    }
    return 0;
}

void StatLogArchive::add_clear_path(const std::string& path){
    this->m_clear_path_set.insert(path);
}

int StatLogArchive::_rm_archive(unsigned time_span, const string& root_path)
{
    if(m_workpath.empty() || root_path.empty())
        return -1;

    DIR *workdir = opendir(root_path.c_str());
    if(workdir == NULL)
    {
        ERROR_LOG("open %s failed: %s", root_path.c_str(), strerror(errno));
        return -1;
    }

    dirent* entry = NULL;
    time_t now = time(0);
    while ((entry = readdir(workdir)) != NULL) 
    {
        string filename = root_path + "/" + entry->d_name;
        if (entry->d_type == DT_REG) 
        {
            struct stat file_stat;
            if(stat(filename.c_str(), &file_stat) == 0)
            {
                if(now - file_stat.st_mtime >= time_span)
                {
                    if(unlink(filename.c_str()) == 0)
                        DEBUG_LOG("removed file: %s", filename.c_str());
                }
            }
        }
        else if(entry->d_type == DT_DIR &&
                strncmp(".", entry->d_name, 1) != 0 &&
                strncmp("..", entry->d_name, 2) != 0)
        {
            _rm_archive(time_span, filename);
        }
    }

    closedir(workdir);

    if(root_path != m_curr_clear_path)
    {
        if(rmdir(root_path.c_str()) == 0)
            DEBUG_LOG("removed dir: %s", root_path.c_str());
    }

    return 0;
}

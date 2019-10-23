/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-client服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <cerrno>
#include <cstring>
#include <sstream>
#include <stdexcept>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

//#include <zlib.h>
#include <string_utils.hpp>

#include "stat_config.hpp"
#include "statlog_reader.hpp"

using std::string;
using std::map;
using std::vector;
using std::istringstream;

//--------------------------------------
// Public Methods
//--------------------------------------
StatLogReader::StatLogReader()
{
    m_ignore_rename_err = true;
}

StatLogReader::~StatLogReader()
{
    uninit();
}

/**
 * 工作根目录是唯一的。
 * 这样做的原因是：程序需要将inbox中文件移动到outbox,如果这两个目录
 * 在不同的文件系统，rename操作需要移动数据文件，影响性能。
 */
int StatLogReader::init()
{
    string work_path;
    string inbox_path;

	//获取work-path = /opt/taomee/stat/data
    StatCommon::stat_config_get("work-path", work_path);
    if(work_path.empty())
    {
        ERROR_LOG("work-path not found in conf.");
        return -1;
    }
	//获取inbox-path = /opt/taomee/stat/data/inbox
    StatCommon::stat_config_get("inbox-path", inbox_path);
    if(inbox_path.empty())
    {
        ERROR_LOG("inbox-path not found in conf.");
        return -1;
    }

    DEBUG_LOG("inbox-path: %s", inbox_path.c_str());
    DEBUG_LOG("work-path: %s", work_path.c_str());

    int ret = setup_work_path(inbox_path, work_path + "/invalid",
                           work_path + "/outbox", work_path + "/read-failed");

    string log_path = work_path + "/log";
    StatCommon::makedir(log_path); // 新建statlogger的log目录

    if(ret == 0)
    {
        if(chmod(inbox_path.c_str(), S_IRWXU | S_IRWXG | S_IRWXO) != 0)
            ERROR_LOG("chmod %s failed: %s", inbox_path.c_str(), strerror(errno));
        if(chmod(log_path.c_str(), S_IRWXU | S_IRWXG | S_IRWXO) != 0)
            ERROR_LOG("chmod %s failed: %s", log_path.c_str(), strerror(errno));
    }

    return ret;
}

int StatLogReader::uninit()
{
    return 0;
}

/*
int StatLogReader::get_client_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}
*/

int StatLogReader::get_server_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

void StatLogReader::timer_event()
{
    process();
}

void StatLogReader::process_client_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatLogReader::process_server_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatLogReader::client_connected(int fd, uint32_t ip)
{
}

void StatLogReader::client_disconnected(int fd)
{
}

void StatLogReader::server_disconnected(int fd)
{
}

//--------------------------------------
// Private Methods
//--------------------------------------
int StatLogReader::get_statlog_preserved(string& fname, size_t& offset)
{
    return SLP_ERROR;
}

int StatLogReader::process_statlog(const string& fn, StatLogFile& mmap_file, size_t offset)
{
    return SLP_OK;
}

//bool StatLogReader::parse_filename(const string& fn, string& filetype, time_t& ts) const
//{
	//vector<string> fn_parts;

    //StatCommon::split(fn, '_', fn_parts);
	//if (fn_parts.size() == 4)
    //{
        //// 暂时不用时间戳
		////istringstream iss(fn_parts[3]);
		////iss >> ts;
		//if (is_valid_appid(fn_parts[0])	&& is_valid_filetype(fn_parts[2]))
        //{
			//filetype = fn_parts[2];
			//return true;
		//}
	//}

	//return false;
//}

bool StatLogReader::sanity_check_file(const StatLogFile& slf) const
{
    time_t now = time(NULL);
    // 基础和自定义统计项每20s生成一个文件，据此可判断文件是否准备好
    // 另外加2s的缓冲时间。
    if((slf.get_file_type() == StatLogFile::SLFT_BASIC && (now - slf.get_file_mtime()) > 20 + 1) ||
        (slf.get_file_type() == StatLogFile::SLFT_CUSTOM && (now - slf.get_file_mtime()) > 20 + 1))
    {
        return true;
    }

    return false;
}

/*
bool StatLogReader::compress(const string& file)
{
    const int rdbuf_sz = 4096;
    char rdbuf[rdbuf_sz];

	int srcfd = open(file.c_str(), O_RDONLY);
	if (srcfd == -1)
    {
		EMERG_LOG("open: %s, %d:%s", file.c_str(), errno, strerror(errno));
		return false;
	}
	// 后缀先用.tmp，成功后再改成.gz
	string fn = file + ".tmp";
	int dstfd = open(fn.c_str(), O_RDWR | O_CREAT | O_TRUNC, S_IRWXU);
	if (dstfd == -1)
    {
		EMERG_LOG("open: %s, %d:%s", fn.c_str(), errno, strerror(errno));
		close(srcfd);
		return false;
	}

	gzFile gzf = gzdopen(dstfd, "wb");
	if (gzf == 0)
    {
		EMERG_LOG("gzopen failed!");
		close(srcfd);
		close(dstfd);
		return false;
	}

	// 读取源文件
	ssize_t n = read(srcfd, rdbuf, rdbuf_sz);
	while (n > 0)
    {
		// 写入压缩文件
		int bytes = gzwrite(gzf, rdbuf, n);
		if (bytes == n)
        {
			n = read(srcfd, rdbuf, rdbuf_sz);
			continue;
		}

		if (bytes == 0)
        { // gzwrite error
			int e;
			const char* es = gzerror(gzf, &e);
			EMERG_LOG("gzwrite failed: %s, %d:%s", fn.c_str(), e, es);
		}
        else 
        { // 写入压缩文件的长度比读取出来的长度短
			EMERG_LOG("gzwrite: bytes_read(%ld) > bytes_written(%d)", n, bytes);
		}
		n = -1;
		break;
	}

	bool ret = true;
	if (n == 0) 
    {
		int r1 = gzflush(gzf, Z_FINISH);
		int r2 = fdatasync(dstfd);
		gzclose(gzf); // 这里会把dstfd也关闭掉

		if ((r1 == Z_OK) && (r2 == 0))
        {
			string fn_new = file + ".gz";
			if (rename(fn.c_str(), fn_new.c_str()) != 0)
            {
				EMERG_LOG("rename failed: %d:%s", errno, strerror(errno));
				ret = false;
			}
		} 
        else
        {
			EMERG_LOG("gzflush: %d, fdatasync: %d:%s",
						r1, r2, (r2 != 0) ? strerror(errno) : "");
			ret = false;
		}
	} 
    else
    {
		gzclose(gzf); // 这里会把dstfd也关闭掉
		ret = false;
	}

	close(srcfd);
	return ret;
}
*/


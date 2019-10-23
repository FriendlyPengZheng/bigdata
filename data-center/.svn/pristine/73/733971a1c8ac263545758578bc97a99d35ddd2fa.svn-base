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

#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <signal.h>
#include <wait.h>
#include <cerrno>
#include <cstring>
#include <vector>
#include <iostream>
#include <fstream>

#include "os_utils.hpp"
#include "stat_common.hpp"
#include "stat_config.hpp"
#include "stat_proto_defines.hpp"
#include "stat_updater.hpp"

using std::ofstream;
using std::ifstream;

StatUpdater::StatUpdater(StatConnector& conn) : m_connector(conn), m_buf(NULL)
{
    m_buf = new (std::nothrow) char [s_buf_size];
}

StatUpdater::~StatUpdater()
{
    delete [] m_buf;
}

bool StatUpdater::check_buf()
{
    if(m_buf == NULL)
    {
        m_buf = new (std::nothrow) char [s_buf_size];

        if(m_buf == NULL)
            return false;
    }

    return true;
}

bool StatUpdater::check_update(string& remote_pkg_path, string& remote_pkg_version, uint32_t& remote_pkg_size)
{
    if(!check_buf())
    {
        return false;
    }

    // 构建请求包
    StatUpdateHeader* pkg = (StatUpdateHeader*) m_buf;
    pkg->len = sizeof(StatUpdateHeader);
    pkg->proto_id = STAT_PROTO_UPDATE;

    string os_name, os_version;
    if(StatCommon::get_os_info(os_name, os_version) != 0)
    {
        ERROR_LOG("get os information failed.");
        return false;
    }

    string module, module_version;
    StatCommon::stat_config_get("module-name", module);
    StatCommon::stat_config_get("version", module_version);
    if(module.empty() || module_version.empty())
    {
        ERROR_LOG("get module_name or version from config file failed.");
        return false;
    }

    std::vector<string> strs;
    strs.push_back(os_name);
    strs.push_back(os_version);
    strs.push_back(module);
    strs.push_back(module_version);
    for(std::vector<string>::iterator it = strs.begin(); it != strs.end(); ++it)
    {
        uint8_t *str_len = (uint8_t*)(m_buf + pkg->len);
        *str_len = (*it).length();
        pkg->len += sizeof(*str_len);
        memcpy(m_buf + pkg->len, (*it).c_str(), *str_len);
        pkg->len += *str_len;
    }

    TcpClient * tunnel = m_connector.get_available_connection();
    if(tunnel == NULL)
    {
        ERROR_LOG("no connection available.");
        return false;
    }

    if(send_recv_pkg(tunnel, m_buf) != 0)
        return false;

    // 解析返回包，获得container路径和版本。
    StatUpdateRet* ret_pkg = (StatUpdateRet*)m_buf;
    if(ret_pkg->file_size == 0) // 无更新
        return false;

    remote_pkg_size = ret_pkg->file_size;

    uint16_t *path_len = (uint16_t *)ret_pkg->body;
    remote_pkg_path.assign(ret_pkg->body + sizeof(uint16_t), *path_len);
    remote_pkg_version.assign(ret_pkg->body + sizeof(uint16_t) + *path_len + sizeof(uint8_t), 
            *(uint8_t*)(ret_pkg->body + sizeof(uint16_t) + *path_len));

    return true;
}

// helper function
int StatUpdater::send_recv_pkg(TcpClient* tunnel, const void *send_buf)
{
    if(tunnel == NULL || send_buf == NULL)
        return -1;

    const StatProtoHeader *header = static_cast<const StatProtoHeader*>(send_buf);

    if(tunnel->send(send_buf, header->len) <= 0)
        return -1;

    StatUpdateRet *ret_pkg = reinterpret_cast<StatUpdateRet *>(m_buf);
    // 返回包是变长的，先取长度，然后再取其内容。
    int n = tunnel->recv(m_buf, sizeof(ret_pkg->len));
    if( n <= 0)
        return -1;

    // 对于返回包大小由代码约定，不采用握手的方式。
    // 如收到超过buffer大小，直接丢弃。
    if(ret_pkg->len > s_buf_size)
    {
        ERROR_LOG("unsupported proto package size.");
        return -1;
    }
    n = tunnel->recv(m_buf + sizeof(ret_pkg->len), ret_pkg->len - sizeof(ret_pkg->len));
    if(n <= 0)
        return -1;

    return 0;
}

int StatUpdater::download_update(const string& remote_pkg_path, const string& remote_pkg_version, 
        const string& local_pkg_path, uint32_t remote_pkg_size)
{
    if(remote_pkg_size == 0 || local_pkg_path.empty())
        return -1;

    if(!check_buf())
    {
        return -1;
    }

    TcpClient * tunnel = m_connector.get_available_connection();
    if(tunnel == NULL)
    {
        ERROR_LOG("no connection available.");
        return -1;
    }

    ::unlink(local_pkg_path.c_str());

    int fd = ::open(local_pkg_path.c_str(), O_RDWR | O_CREAT | O_TRUNC | O_APPEND, S_IRWXU);
    if(fd == -1)
    {
        ERROR_LOG("open file %s failed: %s", local_pkg_path.c_str(), strerror(errno));
        return -1;
    }

    int ret = 0;
    uint32_t saved_size = 0;
    while(saved_size < remote_pkg_size)
    {
        // 构建请求包
        StatUpdateHeader* pkg = (StatUpdateHeader*) m_buf;
        pkg->len = sizeof(StatUpdateHeader);
        pkg->proto_id = STAT_PROTO_DOWNLOAD;

        uint32_t* offset = (uint32_t*)pkg->body;
        pkg->len += sizeof(uint32_t); // 先加上offset大小

        uint16_t *str_len = (uint16_t*)(m_buf + pkg->len);
        *str_len = remote_pkg_path.length();
        pkg->len += sizeof(*str_len);
        memcpy(m_buf + pkg->len, remote_pkg_path.c_str(), *str_len);
        pkg->len += *str_len;

        uint8_t *version_len = (uint8_t*)(m_buf + pkg->len);
        *version_len = remote_pkg_version.length();
        pkg->len += sizeof(*version_len);
        memcpy(m_buf + pkg->len, remote_pkg_version.c_str(), *version_len);
        pkg->len += *version_len;

        *offset = saved_size;

        if(send_recv_pkg(tunnel, m_buf) != 0)
        {
            ret = -1;
            ::unlink(local_pkg_path.c_str());
            break;
        }

        StatUpdateRet* ret_pkg = (StatUpdateRet*)m_buf;
        if(ret_pkg->file_size == 0)
        {
            ret = -1;
            ::unlink(local_pkg_path.c_str());
            break;
        }

        int n = ::write(fd, ret_pkg->body, ret_pkg->file_size);
        if(n <= 0)
        {
            ret = -1;
            ::unlink(local_pkg_path.c_str());
            break;
        }

        saved_size += n;
    }

    ::close(fd);

    return ret;
}

bool StatUpdater::check_install_blacklist(const string& file_path)
{
    if(file_path.empty())
        return false;

    ifstream bl_if;

    bl_if.open(STAT_INSTALL_BLACKLIST);
    if(!bl_if.is_open()) // 文件不存在或出错，认为不在黑名单中
        return false;

    int ret = false;
    string line;
    while(!bl_if.eof())
    {
        std::getline(bl_if, line);
        if(line == file_path)
        {
            ret = true;
            break;
        }

        line.clear();
    }

    bl_if.close();

    return ret;
}

int StatUpdater::add_install_blacklist(const string& file_path)
{
    if(file_path.empty())
        return -1;

    int fd = ::open(STAT_INSTALL_BLACKLIST, O_RDWR | O_CREAT | O_APPEND, S_IRUSR | S_IWUSR);
    if(fd < 0)
    {
        ERROR_LOG("open %s failed: %s.", STAT_INSTALL_BLACKLIST, strerror(errno));
        return -1;
    }

    string line = file_path + '\n';
    int saved = 0;
    int len = line.length();
    const char* str = line.c_str();
    int ret = 0;
    while(saved < len)
    {
        int n = ::write(fd, str + saved, len - saved);
        if(n < 0)
        {
            ERROR_LOG("write file %s failed: %s", file_path.c_str(), strerror(errno));

            struct stat file_stat;
            if(::fstat(fd, &file_stat) == 0)
            {
                uint32_t total_size = file_stat.st_size;
                if(::ftruncate(fd, total_size - saved) < 0)
                    ERROR_LOG("restore file %s failed: %s", file_path.c_str(), strerror(errno));

                ret = -2;
                break;
            }

            break;
        }

        saved += n;
    }

    ::close(fd);

    return ret;
}

int StatUpdater::install_update(const string& local_pkg_path)
{
    if(local_pkg_path.empty())
        return -1;

    string cmd = STAT_UPDATE_SCRIPT;

    /**
     * fork twice to avoid zombie process
     */
    int ret = 0;
    pid_t pid = fork();
    if(pid == 0)
    {
        // if child process exit before parent, it will be zombie.
        // so set SIGCHLD to avoid it.
        signal(SIGCHLD, SIG_IGN);
        pid = fork();
        if(pid == 0)
        {
            if(check_install_blacklist(local_pkg_path) == false)
            {
                vector<string> args;
                args.push_back(local_pkg_path);
                int status = StatCommon::run_cmd_sync_with_timeout(cmd, args, STAT_UPDATE_TIMEOUT);
                if(status == 0)
                {
                    DEBUG_LOG("install %s done, update succeeded!", local_pkg_path.c_str());
                }
                else if(status > 0)
                {
                    DEBUG_LOG("install %s failed, return: %d, add it to blacklist.", local_pkg_path.c_str(), status);
                    add_install_blacklist(local_pkg_path);
                }
                else
                    DEBUG_LOG("internal system error while trying to install: %s.", local_pkg_path.c_str());
            }
            else
                DEBUG_LOG("%s is in blacklist, ignore it.", local_pkg_path.c_str());

            _exit(10);
        }
        else
            _exit(10);
    }
    else if(pid > 0)
    {
        int status, werr;
        while((werr = waitpid(pid, &status, 0)) < 0 && errno == EINTR);
        if(werr < 0)
        {
            ERROR_LOG("waitpid %u failed.", pid);
            ret = -1;
        }
    }
    else
        ret = -1;

    return ret;
}

int  StatUpdater::do_update_proxy(int fd, const void *pkg)
{
    if(pkg == NULL)
        return -1;

    TcpClient * tunnel = m_connector.get_available_connection();
    if(tunnel == NULL)
    {
        ERROR_LOG("no connection available.");
        return -1;
    }

    if(send_recv_pkg(tunnel, pkg) != 0)
        return -1;

    return net_send_cli(fd, m_buf, ((StatProtoHeader*)m_buf)->len);
}

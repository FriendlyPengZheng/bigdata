/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <unistd.h>
#include <libgen.h>
#include <cerrno>
#include <cstring>
#include <string>

#include <filelister.hpp>
#include <fs_utils.hpp>
#include <stat_common.hpp>
#include <stat_config.hpp>
#include <os_utils.hpp>
#include <stat_updater.hpp>

#include "stat_pkg_update.hpp"

using std::string;

const char* const StatPkgUpdate::s_pkg_name_suffix = STAT_PKG_NAME_SUFFIX;
const char* const StatPkgUpdate::s_pkg_path_prefix = STAT_PKG_REPOS_PATH;

StatPkgUpdate::StatPkgUpdate(uint32_t proto_id, const char* proto_name) : StatProtoHandler(proto_id, proto_name), m_ret_buf(NULL)
{
    m_ret_buf = new (std::nothrow) char[s_ret_buf_size];
}

StatPkgUpdate::StatPkgUpdate(uint32_t proto_id, const char* proto_name, unsigned proto_count) : StatProtoHandler(proto_id, proto_name, proto_count), m_ret_buf(NULL)
{
    m_ret_buf = new (std::nothrow) char[s_ret_buf_size];
}

StatPkgUpdate::~StatPkgUpdate()
{
    delete [] m_ret_buf;

    stat_pkg_clear();
}

void StatPkgUpdate::stat_pkg_clear()
{
    for(StatPkgMap::iterator it = m_stat_pkgs.begin(); it != m_stat_pkgs.end(); ++it)
    {
        delete it->second;
        it->second = NULL;
    }

    m_stat_pkgs.clear();
}

// 解析检查更新时协议包
bool StatPkgUpdate::parse_pkg_update_info(const StatUpdateHeader* pkg, StatPkg& stat_pkg, string& parent_path)
{
    const char* p_field = pkg->body;
    char field_len = 0;

    string full_path;
    full_path.append(s_pkg_path_prefix);

    for(int i = 0; i < 4; ++i) // hack: 协议中前几段字符串: os_name, os_version, module, module_version
    {
        field_len = *(char*)p_field;
        p_field += sizeof(char);
        full_path.append(p_field, field_len);
        p_field += field_len;

        if(i == 2)
        {
            parent_path = full_path;
        }

        if(i >= 2)
            full_path += "-";
        else
            full_path += "/";
    }

    full_path.append(s_pkg_name_suffix);

    if(stat_pkg.set_pkg_path(full_path) != 0)
        return false;

    return true;
}

bool StatPkgUpdate::parse_pkg_download_info(const StatUpdateHeader* pkg, 
        uint32_t &file_offset, StatPkgVersion& pkg_version, string& parent_path)
{
    file_offset = *((uint32_t *)pkg->body);
    
    uint16_t path_len = *(uint16_t*)(pkg->body + sizeof(uint32_t));
    parent_path.assign(pkg->body + sizeof(uint32_t) + sizeof(uint16_t), path_len);

    uint8_t version_len = *(uint8_t*)(pkg->body + sizeof(uint32_t) + sizeof(uint16_t) + path_len);
    string version;
    version.assign(pkg->body + sizeof(uint32_t) + sizeof(uint16_t) + path_len + sizeof(uint8_t), version_len);

    if(pkg_version.set_version(version) != 0)
        return false;

    return true;
}

bool StatPkgUpdate::update_latest_pkg(const string& pkg_path, string& latest_pkg_path)
{
    string latest_link = pkg_path + "/latest-version";

    FileLister file_lister;
    file_lister.open(pkg_path);
    file_lister.start();

    string file_name;
    StatPkg latest_pkg;
    bool has_latest = false;
    string fpath;
    while(file_lister.next(file_name))
    {
        StatPkg stat_pkg;

        fpath = pkg_path + "/" + file_name;

        if(file_name.find(s_pkg_name_suffix) != string::npos && 
                stat_pkg.set_pkg_path(fpath) == 0 && 
                stat_pkg.get_pkg_version() > latest_pkg.get_pkg_version())
        {
            latest_pkg = stat_pkg;
            has_latest = true;
        }
    }

    // file_lister.close(); // 析构函数会自动调用close函数

    if(has_latest)
    {
        unlink(latest_link.c_str());
        symlink(latest_pkg.get_pkg_name().c_str(), latest_link.c_str());

        latest_pkg_path = latest_pkg.get_pkg_path();
        return true;
    }

    return false;
}

/**
 * @brief: 检查升级目录，如果该目录下有更新包，则创建软链接latest-version -> 更新包，
 * 并将该更新包信息放入缓存中。
 * @param: pkg_path，更新包路径。
 * @param: it指向缓存中位置的迭代器
 * @return: 有更新包，返回true，否则返回false.
 */
bool StatPkgUpdate::check_update_dir(const string& pkg_path, StatPkgMap::iterator &it)
{
    StatCommon::makedir(pkg_path);

    string latest_pkg_path;
    if(update_latest_pkg(pkg_path, latest_pkg_path))
    {
        if(m_stat_pkgs.size() > 1000) // 最大缓存数量，这个数字不会变化，在这里写死。
            stat_pkg_clear();

        StatPkg *stat_pkg = new (std::nothrow) StatPkg();
        if(stat_pkg == NULL || 
                stat_pkg->set_pkg_path(latest_pkg_path) != 0 ||
                stat_pkg->is_pkg_valid() == false)
        {
            delete stat_pkg;
            return false;
        }

        std::pair<StatPkgMap::iterator, bool> map_ret = m_stat_pkgs.insert(std::make_pair(pkg_path, stat_pkg));
        if(map_ret.second == false)
        {
            delete stat_pkg;
            return false;
        }

        it = map_ret.first;

        return true;
    }

    return false;
}

/**
 * @brief: 检查是否由更新
 * @param: cur_pkg, 当前使用的版本
 * @param: pkg_path, 更新包存放路径
 * @return: 有更新返回true, 否则返回false
 */
bool StatPkgUpdate::check_pkg_update(const StatPkg& cur_pkg, const string& pkg_path, StatPkgMap::iterator &ret_it)
{
    StatPkgMap::iterator it = m_stat_pkgs.find(pkg_path);
    if(it != m_stat_pkgs.end())
    {
        if(cur_pkg.get_pkg_version() < it->second->get_pkg_version())
        {
            ret_it = it;
            return true;
        }
    }
    else
    {
        if(check_update_dir(pkg_path, it)) 
        {
            if(cur_pkg.get_pkg_version() < it->second->get_pkg_version())
            {
                ret_it = it;
                return true;
            }
        }
    }

    return false;
}

bool StatPkgUpdate::check_pkg_download(const StatPkgVersion& pkg_version, const string& pkg_path, StatPkgMap::iterator& ret_it)
{
    StatPkgMap::iterator it = m_stat_pkgs.find(pkg_path);
    if(it != m_stat_pkgs.end())
    {
        if(pkg_version == it->second->get_pkg_version())
        {
            ret_it = it;
            return true;
        }
    }

    return false;
}

int StatPkgUpdate::self_update()
{
    string module, module_version;
    StatCommon::stat_config_get("module-name", module);
    StatCommon::stat_config_get("version", module_version);
    if(module.empty() || module_version.empty())
    {
        ERROR_LOG("get module-name or version from config file failed.");
        return -1;
    }

    string os, os_version;
    if(StatCommon::get_os_info(os, os_version) != 0)
    {
        ERROR_LOG("get os name or version failed.");
        return -1;
    }

    string full_path = s_pkg_path_prefix;
    full_path += (os + "/" + os_version + "/" + module);

    string pkg_path = full_path;
    full_path += "/";
    full_path += (module + "-" + module_version + "-" + s_pkg_name_suffix);

    StatPkg cur_pkg;
    if(cur_pkg.set_pkg_path(full_path) != 0)
        return -1;

    StatPkgMap::iterator it;
    if(check_pkg_update(cur_pkg, pkg_path, it))
    {
        if(StatUpdater::check_install_blacklist(it->second->get_pkg_path()))
        {
            DEBUG_LOG("%s is in blacklist, ignore it.", it->second->get_pkg_path().c_str());
            return 0;
        }

        DEBUG_LOG("update available, installing %s", it->second->get_pkg_path().c_str());
        StatUpdater::install_update(it->second->get_pkg_path());
        DEBUG_LOG("install completed, check log for more details.");
    }

    return 0;
}

void StatPkgUpdate::proc_timer_event()
{
    for(StatPkgMap::iterator it = m_stat_pkgs.begin(); it != m_stat_pkgs.end();)
    {
        string latest_pkg_path;
        if(update_latest_pkg(it->first, latest_pkg_path))
            it->second->set_pkg_path(latest_pkg_path);

        if(it->second->is_pkg_valid() == false)
        {
            delete it->second;
            it->second = NULL;

            m_stat_pkgs.erase(it++);
        }
        else
            ++it;
    }

    self_update();
}

int StatPkgUpdate::proc_proto(int fd, const void* pkg_buf)
{
    //if(pkg_buf == NULL)
    //    return -1;

    const StatUpdateHeader* header = static_cast<const StatUpdateHeader*>(pkg_buf);

    if(m_ret_buf == NULL)
    {
        m_ret_buf = new (std::nothrow) char[s_ret_buf_size];
        if(m_ret_buf == NULL)
            return -1;
    }

    string pkg_path;
    uint32_t file_offset;
    StatPkg cur_pkg;
    StatPkgVersion cur_pkg_version;
    StatPkgMap::iterator it;
    StatUpdateRet *p_ret_pkg = reinterpret_cast<StatUpdateRet *>(m_ret_buf);

    p_ret_pkg->len = sizeof(StatUpdateRet);
    p_ret_pkg->proto_id = header->proto_id;

    switch(header->proto_id)
    {
        // 检查更新
        case STAT_PROTO_UPDATE:
            if(parse_pkg_update_info(header, cur_pkg, pkg_path) == false || // 解析协议包时出错
                    check_pkg_update(cur_pkg, pkg_path, it) == false) // 无更新
            {
                p_ret_pkg->file_size = 0;
            }
            else // 有更新
            {
                p_ret_pkg->file_size = it->second->get_pkg_size();

                // 返回更新包container路径
                uint16_t *pkg_path_len = (uint16_t *)p_ret_pkg->body;
                *pkg_path_len = (it->first).length();
                p_ret_pkg->len += sizeof(*pkg_path_len);

                memcpy(m_ret_buf + p_ret_pkg->len, (it->first).c_str(), *pkg_path_len);
                p_ret_pkg->len += *pkg_path_len;

                // 返回更新包版本
                uint8_t *version_len = (uint8_t*)(p_ret_pkg->body + sizeof(*pkg_path_len) + *pkg_path_len);
                *version_len = it->second->get_pkg_version().get_version_string().length();
                p_ret_pkg->len += sizeof(*version_len);

                memcpy(m_ret_buf + p_ret_pkg->len, 
                        it->second->get_pkg_version().get_version_string().c_str(), 
                        *version_len);
                p_ret_pkg->len += *version_len;
            }
            break;
        // 下载更新
        case STAT_PROTO_DOWNLOAD:
            if(parse_pkg_download_info(header, file_offset, cur_pkg_version, pkg_path) && 
                    check_pkg_download(cur_pkg_version, pkg_path, it))
            {
                int r = it->second->get_pkg_content(p_ret_pkg->body, file_offset, 
                        s_ret_buf_size - sizeof(StatUpdateRet) - sizeof(uint32_t) - sizeof(uint16_t));
                if(r > 0)
                    p_ret_pkg->file_size = r;
                else
                    p_ret_pkg->file_size = 0;

                p_ret_pkg->len += p_ret_pkg->file_size;
            }
            else
            {
                p_ret_pkg->file_size = 0;
            }
            break;
        default:
            p_ret_pkg->file_size = 0;
            ERROR_LOG("unsupported update type.");
            break;
    }

    net_send_cli(fd, m_ret_buf, p_ret_pkg->len);

    return 0;
}

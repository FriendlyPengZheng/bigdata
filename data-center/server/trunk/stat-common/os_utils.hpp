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

#ifndef OS_UTILS_HPP
#define OS_UTILS_HPP

#include <string>
#include <vector>

using std::string;
using std::vector;

namespace StatCommon
{
    int get_os_info(string& os_name, string& os_version);

    /**
     * @brief: 以同步方式运行一个命令，这个导致调用进程挂起。如果在timeout时间内
     * 命令未结束，将会被强制kill。
     * @param cmd_full_path: 命令全路径名，包括命令名。
     * @param args: 传给命令的参数。
     * @param timeout: timeout必须>1.
     */
    int run_cmd_sync_with_timeout(const string& cmd_full_path, const vector<string>& args, unsigned timeout);
}

#endif

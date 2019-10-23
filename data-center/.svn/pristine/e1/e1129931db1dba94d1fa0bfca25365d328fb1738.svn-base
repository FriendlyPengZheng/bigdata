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

#ifndef LIBANT_FS_UTILS_HPP_
#define LIBANT_FS_UTILS_HPP_

#include <string>

namespace StatCommon
{
    // 自动创建path中不存在的子目录
    bool makedir(const std::string& path);

    // 获取目录下文件个数和大小总和，不包括子目录，最多扫描max_count个文件
    int get_dir_files_size(const std::string& path, uint32_t& file_count, uint64_t& total_size, uint32_t max_count = 20000);
    // 获取目录下剩余硬盘大小
    int get_dir_free_size(const std::string& path, uint64_t& free_size);

    // convert bytes to human-readable string
    // 1024 bytes -> 1 KB
    std::string convert_disk_size_unit(uint64_t bytes);
}

#endif // LIBANT_FS_UTILS_HPP_

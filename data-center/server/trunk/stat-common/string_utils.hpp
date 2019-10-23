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

#ifndef LIBANT_STRING_UTILS_HPP_
#define LIBANT_STRING_UTILS_HPP_

#include <sstream>
#include <string>
#include <vector>

using std::string;
using std::vector;
using std::istringstream;
using std::stringstream;

namespace StatCommon
{
    // 根据delim分解字符串
    void split(const std::string& s, char delim, std::vector<std::string>& elems);
    // 去除字符串s头尾的charlist字符
    void trim(std::string& s, const std::string& charlist);
    // 判断给定的字符串是否utf8编码
    bool is_utf8(const std::string& s);
    // 判断字符串是否全是数字
    bool is_all_digit(const std::string& s);
    // 字符串转换成数字类型，整型，浮点型等。
    template<typename T> void strtodigit(const string& str, T& out);
    // 数字类型转字符串
    template<typename T> void digittostr(const T& out, string & str);

    // 只做简单转换，调用时要确保str的值。
    template<typename T> void strtodigit(const string& str, T& out)
    {
        stringstream ss;

        ss.clear();

        ss << str;
        ss >> out;
    }

    template<typename T> void digittostr(const T& out, string & str)
    {
        stringstream ss;

        ss.clear();

        ss << out;
        ss >> str;

    }
}

#endif // LIBANT_STRING_UTILS_HPP_

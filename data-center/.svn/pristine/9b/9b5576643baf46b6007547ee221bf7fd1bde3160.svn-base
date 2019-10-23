/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  Lance<lance@taomee.com>
 *   @date    2014-04-01
 * =====================================================================================
 */

#ifndef BASE64_UTILS_HPP
#define BASE64_UTILS_HPP
#include <string>

using std::string;

namespace StatCommon
{
    /**
     * @brief 检查是否是64位编码的字符串
     * @return true or false
     */
    bool is_base64_encode(const string& str);

    /**
     * @brief Base64编码
     * @param str_in 带编码的输入字符串
     * @param str_out 编码后的Base64字符集的字符串 
     * @return 成功返回编码后的字符串长度，失败返回-1 
     * @attention 此函数相当于对字符串进行Base64加密
     */
    int encode_base64(const string& str_in, string& str_out);

    /**
     * @brief Base64解码
     * @param str_in Base64字符集字符串
     * @param str_out 解码后的输出字符串
     * @return 成功返回解码后的字符串长度，失败返回-1
     */
    int decode_base64(const string& str_in, string& str_out);    
}
#endif

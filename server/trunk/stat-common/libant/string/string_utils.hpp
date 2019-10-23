#ifndef LIBANT_STRING_UTILS_HPP_
#define LIBANT_STRING_UTILS_HPP_

#include <string>
#include <vector>

// 根据delim分解字符串
std::vector<std::string> split(const std::string& s, char delim);
// 去除字符串s头尾的charlist字符
void trim(std::string& s, const std::string& charlist);
// 判断给定的字符串是否utf8编码
bool is_utf8(const std::string& s);

#endif // LIBANT_STRING_UTILS_HPP_

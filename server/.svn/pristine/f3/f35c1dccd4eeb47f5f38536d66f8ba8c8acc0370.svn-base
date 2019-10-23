/**
 * @file other_functions.h
 * @brief 套接字相关函数的定义文件
 * @author richard <richard@taomee.com>
 * @date 2009-11-23
 */

#ifndef OTHER_FUNCTIONS_H_20091105
#define OTHER_FUNCTIONS_H_20091105

#include <sys/types.h>

/**
 * @brief 开启允许重用本地地址选项
 * @param sock_fd 套接字
 * @return 成功返回0，失败返回－1
 */
int setreuseaddr(int sock_fd);

/**
 * @brief 设置sock_fd非阻塞I/O标志
 * @param sock_fd 套接字
 * @return 成功返回0，失败返回－1
 */
int setnonblocking(int sock_fd);

/**
 * @brief 采用非阻塞模式从sock_fd中读取字节流
 * @param sock_fd 套接字
 * @param buf     接收数据的缓存
 * @param buf_len 接收数据的缓存的长度
 * @return 成功返回接收数据的长度，失败返回－1
 * @note 本函数只进行一次读操作，返回实际接收的数据的长度
 */
ssize_t e_read(int sock_fd, void *buf, size_t buf_len);

/**
 * @brief 采用非阻塞模式向sock_fd中写入字节流
 * @param sock_fd 套接字
 * @param buf     要写入的数据
 * @param buf_len 要写入的数据的长度
 * @return 成功返回已写入的数据的长度，失败返回－1
 * @note 本函数只进行一次写操作，返回实际写入的数据的长度
 */
ssize_t e_write(int sock_fd, const void* buf, size_t buf_len);

/**
 * @brief 采用阻塞模式从sock_fd中读取字节流
 * @param sock_fd 套接字
 * @param buf     接收数据的缓存
 * @param buf_len 接收数据的缓存的长度
 * @return 成功返回buf_len，失败返回－1
 * @note 只有当成功读取buf_len的长度的数据或发生错误时，本函数才会返回
 */
ssize_t b_read(int sock_fd, void *buf, size_t buf_len);

/**
 * @brief 采用阻塞模式向sock_fd中写入字节流，并确保所有的字节流都被写入
 * @param sock_fd 套接字
 * @param buf     要写入的数据
 * @param buf_len 要写入的数据的长度
 * @return 成功返回buf_len，失败返回－1
 * @note 只有当要写入的数据都被写入时本函数才会返回
 */
ssize_t b_write(int sock_fd, const void* buf, size_t buf_len);

/**
 * @brief 对一个字符串进行md5编码
 * @param string 要进行md5编码的字符串
 * @param buffer 存储编码后的字符串的缓存
 * @param buffer_len 缓存的长度
 * @return 成功返回0，失败返回-1
 */
int md5(const char *string, char *buf, int buf_len);

#endif //OTHER_FUNCTIONS_H_20091105

/**
 * =====================================================================================
 *       @file  tcp_operator.cpp
 *      @brief  
 *
 *     Created  2013-11-13 15:17:34
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "tcp_operator.h"
#include "tcp_client.hpp"
#include "log.h"

#define  CMD_INSERT_STAT    (0x1001)
#define  CMD_INSERT_GPZS    (0x1002)
#define  CMD_INSERT_TASK    (0x1003)

bool config_connected;
char config_ip[64];
char config_port[16];

bool connectToConfig(TcpClient* server, const char* ip, const char* port)
{
    if(server->connect(ip, port) <= 0) {
        ERROR_LOG("can not connect to config server %s:%s", ip, port);
        return false;
    } else {
        DEBUG_LOG("connect to config server %s:%s", ip, port);
        return true;
    }
}

uint32_t getGPZSFromConfig(TcpClient* server, const gpzs_t* gpzs, int fd)
{
    header_t head;
    int ret;
    head.pkg_len = sizeof(header_t) + sizeof(fd) + sizeof(gpzs_t);
    head.cmd_id  = CMD_INSERT_GPZS;
    head.version = 0;
    head.seq_no  = 0;
    head.return_value = 0;
    if(!config_connected) {
        config_connected = connectToConfig(server, config_ip, config_port);
    }
    if(!config_connected) {
        return 0;
    }

    struct iovec send_pkg[3];
    send_pkg[0].iov_base = &head;
    send_pkg[0].iov_len = sizeof(head);
    send_pkg[1].iov_base = &fd;
    send_pkg[1].iov_len = sizeof(fd);
    send_pkg[2].iov_base = (void*)gpzs;
    send_pkg[2].iov_len = sizeof(gpzs_t);
    if(server->writev(send_pkg, 3) <= 0)
    {
        server->close();
        server->reconnect();
        return 0;
    }

    uint32_t gpzs_id;
    int tmp_fd;
    char buf[1024];
    send_pkg[0].iov_base = buf;
    send_pkg[0].iov_len = sizeof(head);
    send_pkg[1].iov_base = &tmp_fd;
    send_pkg[1].iov_len = sizeof(tmp_fd);
    send_pkg[2].iov_base = &gpzs_id;
    send_pkg[2].iov_len = sizeof(gpzs_id);

    ret = server->readv(send_pkg, 3);
    if(ret != sizeof(head) + sizeof(tmp_fd) + sizeof(gpzs_id))
    {
        /**
         * 读出错或超时，直接关闭。
         * 如果出现读数据不完整，无法处理，直接关闭。
         */
        server->close();
        server->reconnect();
        ERROR_LOG("recv from config_server failed.");
        return 0;
    }

    if(((header_t*)buf)->return_value != 0) {
        ERROR_LOG("recv from config_server ret %u", ((header_t*)buf)->return_value);
        return 0;
    }
    return gpzs_id;
}

uint32_t getDataIdFromConfig(TcpClient* server, const void* data, uint32_t len, int fd)
{
    header_t head;
    int ret;
    head.pkg_len = sizeof(header_t) + sizeof(fd) + len;
    head.cmd_id  = CMD_INSERT_STAT;
    head.version = 0;
    head.seq_no  = 0;
    head.return_value = 0;
    if(!config_connected) {
        config_connected = connectToConfig(server, config_ip, config_port);
    }
    if(!config_connected) {
        return 0;
    }

    struct iovec send_pkg[3];
    send_pkg[0].iov_base = &head;
    send_pkg[0].iov_len = sizeof(head);
    send_pkg[1].iov_base = &fd;
    send_pkg[1].iov_len = sizeof(fd);
    send_pkg[2].iov_base = (void*)data;
    send_pkg[2].iov_len = len;
    if(server->writev(send_pkg, 3) <= 0)
    {
        server->close();
        server->reconnect();
        return 0;
    }

    uint32_t data_id;
    int tmp_fd;
    char buf[1024];
    send_pkg[0].iov_base = buf;
    send_pkg[0].iov_len = sizeof(head);
    send_pkg[1].iov_base = &tmp_fd;
    send_pkg[1].iov_len = sizeof(tmp_fd);
    send_pkg[2].iov_base = &data_id;
    send_pkg[2].iov_len = sizeof(data_id);

    ret = server->readv(send_pkg, 3);
    if(ret != sizeof(head) + sizeof(tmp_fd) + sizeof(data_id))
    {
        /**
         * 读出错或超时，直接关闭。
         * 如果出现读数据不完整，无法处理，直接关闭。
         */
        server->close();
        server->reconnect();
        ERROR_LOG("recv from config_server failed.");
        return 0;
    }

    if(((header_t*)buf)->return_value != 0) {
        ERROR_LOG("recv from config_server ret %u", ((header_t*)buf)->return_value);
        return 0;
    }
    return data_id;
}

uint32_t getTaskIdFromConfig(TcpClient* server, const void* data, uint32_t len, int fd)
{
    header_t head;
    int ret;
    head.pkg_len = sizeof(header_t) + sizeof(fd) + len;
    head.cmd_id  = CMD_INSERT_TASK;
    head.version = 0;
    head.seq_no  = 0;
    head.return_value = 0;
    if(!config_connected) {
        config_connected = connectToConfig(server, config_ip, config_port);
    }
    if(!config_connected) {
        return 0;
    }

    struct iovec send_pkg[3];
    send_pkg[0].iov_base = &head;
    send_pkg[0].iov_len = sizeof(head);
    send_pkg[1].iov_base = &fd;
    send_pkg[1].iov_len = sizeof(fd);
    send_pkg[2].iov_base = (void*)data;
    send_pkg[2].iov_len = len;
    if(server->writev(send_pkg, 3) <= 0)
    {
        server->close();
        server->reconnect();
        return 0;
    }

    uint32_t task_id;
    int tmp_fd;
    char buf[1024];
    send_pkg[0].iov_base = buf;
    send_pkg[0].iov_len = sizeof(head);
    send_pkg[1].iov_base = &tmp_fd;
    send_pkg[1].iov_len = sizeof(tmp_fd);
    send_pkg[2].iov_base = &task_id;
    send_pkg[2].iov_len = sizeof(task_id);

    ret = server->readv(send_pkg, 3);
    if(ret != sizeof(head) + sizeof(tmp_fd) + sizeof(task_id))
    {
        /**
         * 读出错或超时，直接关闭。
         * 如果出现读数据不完整，无法处理，直接关闭。
         */
        server->close();
        server->reconnect();
        ERROR_LOG("recv from config_server failed.");
        return 0;
    }

    if(((header_t*)buf)->return_value != 0) {
        ERROR_LOG("recv from config_server ret %u", ((header_t*)buf)->return_value);
        return 0;
    }
    return task_id;
}

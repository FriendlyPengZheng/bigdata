/**
 * =====================================================================================
 *       @file  tcp_operator.h
 *      @brief  
 *
 *     Created  2013-11-13 15:17:08
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  TCP_OPERATOR_H
#define  TCP_OPERATOR_H

#include "tcp_client.hpp"
#include "proto.h"

extern bool config_connected;
extern char config_ip[64];
extern char config_port[16];

bool connectToConfig(TcpClient* server, const char* ip, const char* port);
uint32_t getGPZSFromConfig(TcpClient* server, const gpzs_t* gpzs, int fd);
uint32_t getDataIdFromConfig(TcpClient* server, const void* data, uint32_t len, int fd);
uint32_t getTaskIdFromConfig(TcpClient* server, const void* data, uint32_t len, int fd);

#endif  /*TCP_OPERATOR_H*/

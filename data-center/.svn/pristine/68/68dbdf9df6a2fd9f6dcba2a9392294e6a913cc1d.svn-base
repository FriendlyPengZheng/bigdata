/**
 * =====================================================================================
 *       @file  redis.cpp
 *      @brief  
 *
 *     Created  2013-11-07 14:14:47
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "redis.h"
#include "stat_common.hpp"
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <unistd.h>

redisConnection::redisConnection() {
    connection = 0;
    strcpy(errstr, "not connected yet");
}

redisConnection::~redisConnection() {
    close();
}

redisConnection::redisConnection(const char *ip, int port) {
    connection = 0;
    connect(ip, port);
}

int redisConnection::connect(const char *ip, int port) {
    connection = redisConnect(ip, port);
    if(connection->err) {
        int ret = connection->err;
        strncpy(errstr, connection->errstr, sizeof(errstr) - 1);
        redisFree(connection);
        connection = 0;
        return ret;
    }
    strcpy(this->ip, ip);
    this->port = port;
    errstr[0] = 0;
    return 0;
}

redisReply* redisConnection::doCommand(const char* format, ...) {
    va_list ap;
    va_start(ap, format);
    redisReply *reply = (redisReply*)redisvCommand(connection, format, ap);
    va_end(ap);

    if(unlikely(reply == NULL))
    {
        ping();
        reply = (redisReply*)redisvCommand(connection, format, ap);
    }

    if(unlikely(reply && reply->type == REDIS_REPLY_ERROR))
    {
        strncpy(errstr, reply->str, sizeof(errstr));
        freeReplyObject(reply);
        return NULL;
    }

    return reply;
}

void redisConnection::close() {
    if(connection == NULL)  return;
    redisFree(connection);
    connection = NULL;
    clear();
    strcpy(errstr, "not connected yet");
}

/**
 *     @fn  `ping`
 *  @brief  `保持与redis服务器的连接`
 *
 *  @param  ``
 * @return  `服务器存活返回true，否则返回false`
 */
bool redisConnection::ping() {
    if(connection == NULL) {
        if(connect(ip, port)) {
            return false;
        }
    }
    redisReply* r = (redisReply*)redisCommand(connection, "PING");
    if(r == NULL) {
        redisFree(connection);
        connection = NULL;
        connect(ip, port);
        if(connection == NULL)  {
            strcpy(errstr, "lost connection");
            return false;
        }
        r = (redisReply*)redisCommand(connection, "PING");
    }
    if(r->type == REDIS_REPLY_ERROR) {
        freeReplyObject(r);
        return false;
    } else {
        freeReplyObject(r);
        return true;
    }
}

//int main(int args, char* argv[])
//{
    //redisConnection r("10.1.1.63", 6379);
    //if(r.getError()) {
        //printf("%s\n", r.getError());
    //} else {
        //printf("connect\n");
    //}
    //while(1) {
        //if(r.ping()) printf("ok\n");
        //else printf("%s\n", r.getError());
        //sleep(1);
    //}
    //return 0;
//}

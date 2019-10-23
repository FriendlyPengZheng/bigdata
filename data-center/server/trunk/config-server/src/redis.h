/**
 * =====================================================================================
 *       @file  redis.h
 *      @brief  
 *
 *     Created  2013-11-07 13:47:13
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  REDIS_H
#define  REDIS_H

#include <hiredis.h>
#include <string.h>

class redisConnection
{
    private:
        redisContext* connection;
        char errstr[64];
        char ip[16];
        int  port;

    public:
        redisConnection();
        ~redisConnection();
        redisConnection(const char *ip, int port);

        int connect(const char *ip, int port);
        void close();

        redisReply* doCommand(const char* format, ...);

        char* getError() {
            if(connection == NULL)  return errstr;
            if(connection->err && strlen(connection->errstr) != 0)
                return connection->errstr;
            if(strlen(errstr) != 0) return errstr;
            return 0;
        }
        bool ping();

    private:
        void clear() {
            connection = 0;
            errstr[0] = 0;
            ip[0] = 0;
            port  = 0;
        }

};

#endif  /*REDIS_H*/

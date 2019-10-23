/**
 * =====================================================================================
 *       @file  redis_operator.cpp
 *      @brief  
 *
 *     Created  2013-11-13 14:08:11
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "redis_operator.h"
#include "stat_common.hpp"
#include <stdlib.h>

uint32_t getGPZSFromRedis(redisConnection* redis, const gpzs_t* gpzs)
{
    if(unlikely(gpzs == NULL)) 
    {
        ERROR_LOG("gpzs pointer is NULL");
        return 0;
    }

    redisReply* reply = redis->doCommand("HGET gpzs %b", gpzs, sizeof(gpzs_t));
    if(unlikely(reply == NULL)) 
        return 0;

    uint32_t ret;
    switch(reply->type) {
        case REDIS_REPLY_STRING:
            ret = atoi(reply->str);
            break;
        case REDIS_REPLY_INTEGER:
            ret = reply->integer;
            break;
        case REDIS_REPLY_NIL://没有找到
            ret = 0;
            break;
        default :
            ERROR_LOG("unexcepted type[%d] str[%s] array[%zu]", reply->type, reply->str, reply->elements);
            ret = 0;
            break;
    }

    freeReplyObject(reply);
    return ret;
}

uint32_t getDataIdFromRedis(redisConnection* redis, const void* buf, uint32_t len)
{
    if(unlikely(buf == NULL))
    {
        ERROR_LOG("buf pointer is NULL");
        return 0;
    }

    redisReply* reply = redis->doCommand("HGET data_info %b", buf, len);
    if(unlikely(reply == NULL)) 
        return 0;

    uint32_t ret;
    switch(reply->type) {
        case REDIS_REPLY_STRING:
            ret = atoi(reply->str);
            break;
        case REDIS_REPLY_INTEGER:
            ret = reply->integer;
            break;
        case REDIS_REPLY_NIL://没有找到
            ret = 0;
            break;
        default :
            ERROR_LOG("unexcepted type[%d] str[%s] array[%zu]", reply->type, reply->str, reply->elements);
            ret = 0;
            break;
    }

    freeReplyObject(reply);
    return ret;
}

uint32_t getReportIdFromRedis(redisConnection* redis, const void* buf, uint32_t len)
{
    if(unlikely(buf == NULL))
    {
        ERROR_LOG("buf pointer is NULL");
        return 0;
    }
    redisReply* reply = redis->doCommand("HGET report_info %b", buf, len);
    if(unlikely(reply == NULL))
        return 0;

    uint32_t ret;
    switch(reply->type) {
        case REDIS_REPLY_STRING:
            ret = atoi(reply->str);
            break;
        case REDIS_REPLY_INTEGER:
            ret = reply->integer;
            break;
        case REDIS_REPLY_NIL://没有找到
            ret = 0;
            break;
        default :
            ERROR_LOG("unexcepted type[%d] str[%s] array[%zu]", reply->type, reply->str, reply->elements);
            ret = 0;
            break;
    }

    freeReplyObject(reply);
    return ret;
}

uint32_t getHashFromRedis(redisConnection* redis, const char* stid)
{
    redisReply* reply = redis->doCommand("GET %s", stid);
    if(reply == NULL || reply->type != REDIS_REPLY_STRING) {
        freeReplyObject(reply);
        return 0;
    }
    uint32_t ret = (uint32_t) atol(reply->str);
    freeReplyObject(reply);
    return ret;
}

uint32_t insertHashToRedis(redisConnection* redis, const char* stid, uint32_t hash)
{
    freeReplyObject(redis->doCommand("SET %s %u", stid, hash));
    return hash;
}

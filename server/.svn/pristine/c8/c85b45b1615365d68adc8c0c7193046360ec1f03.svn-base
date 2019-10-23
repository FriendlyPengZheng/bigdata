/**
 * =====================================================================================
 *       @file  redis_operator.h
 *      @brief  
 *
 *     Created  2013-11-13 13:18:07
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  REDIS_OPERATOR_H
#define  REDIS_OPERATOR_H

#include <stdint.h>
#include "redis.h"
#include "proto.h"

uint32_t getGPZSFromRedis(redisConnection* redis, const gpzs_t* gpzs);
uint32_t getDataIdFromRedis(redisConnection* redis, const void* buf, uint32_t len);
uint32_t getReportIdFromRedis(redisConnection* redis, const void* buf, uint32_t len);
uint32_t getHashFromRedis(redisConnection* redis, const char* stid);
uint32_t insertHashToRedis(redisConnection* redis, const char* stid, uint32_t hash);

#endif  /*REDIS_OPERATOR_H*/

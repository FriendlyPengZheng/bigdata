/**
 * =====================================================================================
 *       @file  util.cpp
 *      @brief  
 *
 *     Created  2013-11-14 09:50:37
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "util.h"
#include "proto.h"
#include <stdio.h>

uint32_t getHash(const char* p)
{
    uint32_t h = 0;
    while (*p) {
        h = h * 11 + (*p << 4) + (*p >> 4);
        p++;
    }
    return h;
}

uint32_t getTimeByType(uint32_t time, uint32_t type)
{
    uint32_t ret = 0;
    switch(type) {
        case MINUTE:
            ret = time - time % 60;
            break;
        case HOUR:
            ret = time - time % 3600;
            break;
        case DAY:
            ret = time - (time + 3600 * 8) % 86400;
            break;
        default:
            break;
    }
    return ret;
}

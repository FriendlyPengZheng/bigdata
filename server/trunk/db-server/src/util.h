/**
 * =====================================================================================
 *       @file  util.h
 *      @brief  
 *
 *     Created  2013-11-14 09:51:06
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  UTIL_H
#define  UTIL_H

#include <stdint.h>
#include "c_mysql_operator.h"

extern c_mysql_operator g_mysql;

uint32_t getHash(const char* p);
uint32_t getTimeByType(uint32_t time, uint32_t type);

#endif  /*UTIL_H*/

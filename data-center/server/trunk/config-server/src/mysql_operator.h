/**
 * =====================================================================================
 *       @file  mysql_operator.h
 *      @brief  
 *
 *     Created  2013-11-11 18:29:13
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  MYSQL_OPERATOR_H
#define  MYSQL_OPERATOR_H

#include "c_mysql_connect_auto_ptr.h"
#include <stdint.h>
#include <string>

using std::string;

uint32_t getGPZS(c_mysql_connect_auto_ptr *mysql, uint32_t g, int32_t p,int32_t z, int32_t s);
uint32_t insertGPZS(c_mysql_connect_auto_ptr* mysql, uint32_t g, int32_t p,int32_t z, int32_t s);
//uint32_t insertTree(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid, const char* sstid);
uint32_t insertReport(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field);
//uint32_t getStidNode(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid);
//uint32_t getSstidNode(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid, const char* sstid);
//uint32_t insertSstidNode(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid, const char* sstid, uint32_t parent);
uint32_t getNodeId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field);
uint32_t createNodeId(c_mysql_connect_auto_ptr* mysql, const char* node_name, uint32_t g, uint32_t parent, uint8_t leaf, uint8_t basic);
uint32_t getReportId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field);
uint32_t insertReportId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field, uint32_t node_id);
uint8_t getMulti(uint8_t op, const char* field);
const char* getReportName(uint8_t op, const char* stid, const char* sstid, const char* field);
uint32_t insertData(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field, const char* key);
const char* getRange(uint8_t op, const char* field, const char*key);
uint32_t getHash(const char* p);
const char* getOpName(uint8_t op);
const char* getGPZSName(int32_t p, int32_t z, int32_t s);
uint32_t getTask(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t, const char* range);
uint32_t insertTask(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t, const char* range);
uint32_t getCommonResultId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t);
uint32_t insertCommonResultId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t);
uint32_t getData(c_mysql_connect_auto_ptr* mysql, uint32_t r, const char* range);
#endif  /*MYSQL_OPERATOR_H*/

/**
 * =====================================================================================
 *       @file  c_proto_pkg.h
 *      @brief  
 *
 *     Created  2013-11-08 15:49:56
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  C_PROTO_PKG_H
#define  C_PROTO_PKG_H

#include "proto.h"

#define MAX_BUFFER  (64*1024)

class c_proto_pkg {
    private:
        char  buf[MAX_BUFFER];
        header_t *head;
        gpzs_t   *gpzs;
        char     stid[256];
        char     sstid[256];
        char     field[256];
        char     key[256];  //0x1003中是range
        uint32_t game_id;
        uint8_t  op_type;
        uint32_t task_id;

    public:
        uint32_t  recv_pkg(const void*); //接受数据包并检查包体的合法性
        uint16_t  getPkgLen()   { return head->pkg_len; }
        uint32_t  getCmdID()    { return head->cmd_id; }
        uint32_t  getVersion()  { return head->version; }
        uint32_t  getSeqNo()    { return head->seq_no; }
        uint32_t  getReturn()   { return head->return_value; }
        uint32_t  getFd()       { return head->fd; }
        int32_t   getPlatform() { return gpzs->platform_id; }
        int32_t   getZone()     { return gpzs->zone_id; }
        int32_t   getServer()   { return gpzs->server_id; }
        uint32_t  getGame()     { return game_id; }
        uint32_t  getTask()     { return task_id; }
        const char*  getStid()  { return (const char*)stid; }
        const char*  getSstid() { return (const char*)sstid; }
        const char*  getField() { return (const char*)field; }
        const char*  getKey()   { return (const char*)key; }
        const char*  getRange() { return (const char*)key; }
        uint8_t   getOpType()   { return op_type; }
        //
        const void* getGPZS()  { return (const void*)gpzs; }
        const void* getStat()  { return (const void*)(buf + sizeof(header_t)); }

};

#endif  /*C_PROTO_PKG_H*/

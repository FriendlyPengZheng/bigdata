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
        char     key[256];//0x1004中是range
        uint32_t task_id;
        uint8_t  op_type;
        value_t  *value;

    public:
        uint32_t  recv_pkg(const void*); //接受数据包并检查包体的合法性
        uint16_t  getPkgLen()   const { return head->pkg_len; }
        uint32_t  getCmdID()    const { return head->cmd_id; }
        uint32_t  getVersion()  const { return head->version; }
        uint32_t  getSeqNo()    const { return head->seq_no; }
        uint32_t  getReturn()   const { return head->return_value; }
        int32_t   getPlatform() const { return gpzs->platform_id; }
        int32_t   getZone()     const { return gpzs->zone_id; }
        int32_t   getServer()   const { return gpzs->server_id; }
        uint32_t  getGame()     const { return gpzs->game_id; }
        uint32_t  getTask()     const { return task_id; }
        const char*  getStid()  const { return (const char*)stid; }
        const char*  getSstid() const { return (const char*)sstid; }
        const char*  getField() const { return (const char*)field; }
        const char*  getKey()   const { return (const char*)key; }
        const char*  getRange() const { return (const char*)key; }
        uint8_t   getOpType()   const { return op_type; }
        uint8_t   getDataType() const { return value->data_type; }
        uint32_t  getTime()     const { return value->time; }
        double    getValue()    const { return value->value; }
        
        const gpzs_t* getGPZS() { return (const gpzs_t*)gpzs; }
        const void*   getDataInfo() { return (const void*)&gpzs->game_id; }
        uint32_t      getDataInfoLen() { return sizeof(gpzs->game_id) + sizeof(uint8_t)*5 + strlen(stid) + strlen(sstid) + strlen(field) + strlen(key); }
        const void*   getTaskInfo() { return (const void*)&gpzs->game_id; }
        uint32_t      getTaskInfoLen() { return sizeof(gpzs->game_id) + sizeof(task_id) + sizeof(uint8_t) + strlen(key); }

        void setPlatform(uint32_t p) { gpzs->platform_id = p; }
        void setZone    (uint32_t z) { gpzs->zone_id = z; }
        void setServer  (uint32_t s) { gpzs->server_id = s; }
};

#endif  /*C_PROTO_PKG_H*/

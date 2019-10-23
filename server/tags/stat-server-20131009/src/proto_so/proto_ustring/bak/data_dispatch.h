/**
 * =====================================================================================
 *       @file  data_dispatch.h
 *      @brief  
 *
 *  Detailed description starts here.
 *
 *   @internal
 *     Created  05/10/2010 05:03:09 PM 
 *    Revision  3.0.0
 *    Compiler  gcc/g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2010, TaoMee.Inc, ShangHai.
 *
 *     @author  henry (韩林), henry@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */
#ifndef H_DATA_DISPATCH_H_2010_05_10
#define H_DATA_DISPATCH_H_2010_05_10

#include <map>
#include "../../message.h"
#include "i_config.h"
#include "i_net_client.h"
#include "i_mysql_iface.h"
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#include "i_proto_so.h"

#define MAX_RECV_BUFFER_LEN (1024*4)

#define TOTAL_CYCLE_COUNT 60

class c_data_dispatch
{
public:
    c_data_dispatch();
    ~c_data_dispatch();
    int init(i_config *p_config);
    int uninit();
    int dispatch_to_ustring(const ss_message_header_t &ss_msg_hdr, void *p_data);
    int get_proto_id(uint32_t *p_proto_id, int *proto_count);
protected:
    struct response_msg_t {
        uint16_t msg_len; 
        uint16_t result;
    } __attribute__((__packed__));

    struct request_msg_t {
        uint16_t msg_len; 
        uint32_t msg_id; 
        uint32_t report_id;
        uint32_t timestamp;
        char ustring[36];
    } __attribute__((__packed__));

    int init_mysql_conn(i_mysql_iface *p_mysql);
    int send_uuid_to_cache(const request_msg_t *p_request);
private:
    i_config *m_p_config;
    i_net_client *m_p_net;  

    char m_recv_buffer[MAX_RECV_BUFFER_LEN];
    int m_recv_buffer_len;
    int m_inited;
};

#endif //H_DATA_DISPATCH_H_2010_05_10

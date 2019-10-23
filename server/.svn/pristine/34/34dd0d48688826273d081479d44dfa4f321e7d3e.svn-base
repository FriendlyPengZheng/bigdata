/**
 * =====================================================================================
 *       @file  uvalue.cpp
 *      @brief  use to send uvalue request
 *
 *  Detailed description starts here.
 *
 *   @internal
 *     Created  08/26/2010 04:31:50 PM 
 *    Revision  1.0.0.0
 *    Compiler  gcc/g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2010, TaoMee.Inc, ShangHai.
 *
 *     @author  imane (小曼), imane@taomee.com
 * This source code was wrote for TaoMee,Inc. ShangHai CN.
 * =====================================================================================
 */
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <string.h>

#include "log.h"
#include "i_net_client.h"
#include "newsfeed.h"

i_net_client *p_net = NULL;

int proto_init(i_timer *p_timer, i_config *p_config)
{
    if(p_config == NULL)
    {
        ERROR_LOG("p_config is NULL"); 
        return -1;
    }  

    /**< 初始化日志模块*/     
    char log_dir[PATH_MAX] = {'\0'};
    char log_prefix[NAME_MAX] = {'\0'}; 
    char log_lvl_str[16] = {'\0'}; 
    char log_size_str[16] = {'\0'};
    char log_count_str[16] = {'\0'};
    int log_lvl = 0;
    int log_size = 0;
    int log_count = 10;

    if(p_config->get_config("log", "log_dir", log_dir, sizeof(log_dir)) ||
                p_config->get_config("log", "log_prefix", log_prefix, sizeof(log_prefix)) ||
                p_config->get_config("log", "log_lvl", log_lvl_str, sizeof(log_lvl_str)) ||
                p_config->get_config("log", "log_size", log_size_str, sizeof(log_size_str)) ||
                p_config->get_config("log", "log_count", log_count_str, sizeof(log_count_str)))
    {
        ERROR_LOG("get log config failed.");
        return -1;
    }

    log_lvl = atoi(log_lvl_str);
    log_size = atoi(log_size_str);
    log_count = atoi(log_count_str);
    if(log_lvl <= 0 || log_size <= 0 || log_count <= 0)
    {
        ERROR_LOG("log_lvl log_size log_count convert failed.");
        return -1;
    }

    if (log_init(log_dir, (log_lvl_t)log_lvl, log_size, log_count, log_prefix) != 0)
    {
        ERROR_LOG("log_init error.");
        return -1;
    }
    enable_multi_thread();
    set_log_dest(log_dest_file);

    if (create_net_client_instance(&p_net) != 0) {
        ERROR_LOG("create net client instance.");
        return -1;
    }  
       
    char str_ip[100] = {0};
    if (p_config->get_config("dispatch_server", "dispatch_ip", str_ip, sizeof(str_ip) - 1) != 0) {
        ERROR_LOG("ERROR: p_config->get_config.dispatch_server");
        return -1; 
    }   
    char str_port[10]={0};    
    if (p_config->get_config("dispatch_server", "dispatch_port",str_port, sizeof(str_port) - 1) != 0) {
        ERROR_LOG("ERROR: p_config->get_config.dispatch_port");
        return -1; 
    }   

    int port = atoi(str_port);
    if (port < 0 || port > 65536) {
        ERROR_LOG("ERROR: port is invalid.");
        return -1; 
    }

    sockaddr_in client_addr;
    memset(&client_addr, 0, sizeof(client_addr));
    inet_pton(AF_INET, str_ip, &client_addr.sin_addr);

    int ip_num = client_addr.sin_addr.s_addr;
    //if (0 != p_net->init(ip_num, port, 1000))
    if (0 != p_net->init(ip_num, port, 5000))
    {
        ERROR_LOG("ERROR:init happens an error!\n");
        return -1;
    }

    return 0;
}


int get_proto_id(uint32_t *p_proto_id, int *proto_count)
{
    if(NULL == p_proto_id || NULL == proto_count)
    {
        ERROR_LOG("p_proto_id or proto_count is NULL.");
        return -1;
    }
    
    p_proto_id[0] = SHARED_DATA; 
    *proto_count = 1;

    return 0;
}

void hexdump(unsigned char *str, int len, char buffer[])       
{                                                              
    int i = 0;                                                 
    for(i = 0; i < len; i++)                                   
    {                                                          
        sprintf(&buffer[i*3],"%02x ",str[i]);                  
    }                                                          
}                                                              

int dispatch_to_dispatch(ss_message_header_t ss_msg_hdr, void *p_data)
{

    if (NULL == p_data)
    {
        ERROR_LOG("in dispatch_to_uvalue: p_data is NULL");
        return -1;
    }
 
    char send_buffer[MAX_DATA_BUFFER] = {0};
    unsigned int data_len = *(uint16_t *)p_data;

    if (data_len < sizeof(dispatch_request_msg_t))
    {
        char buffer[MAX_DATA_BUFFER * 3 + 1];                
        hexdump((unsigned char *)p_data, 13, buffer);        
        ERROR_LOG("data_len:%u,content{%s} reason:data_len < %lu",data_len,buffer,sizeof(dispatch_request_msg_t)); 
        return 0;
    }
    
    if (data_len > MAX_DATA_BUFFER)
    {
        char buffer[MAX_DATA_BUFFER * 3 + 1];                
        hexdump((unsigned char *)p_data, 13, buffer);        
        ERROR_LOG("data len is too big: %u,content{%s}",data_len,buffer);
        return -1;
    }

    memcpy(send_buffer, p_data, data_len);


    char feed_buffer[13 * 3 + 1];                
    hexdump((unsigned char *)p_data, 13, feed_buffer);        
    char ipstr[20] = "";
    struct in_addr addr;
    addr.s_addr = ss_msg_hdr.cli_addr;
    inet_ntop(AF_INET, (void *)&addr, ipstr, 20);
    //WARN_LOG("feed data [time: %d, addr %s]is [%s]",ss_msg_hdr.timestamp, ipstr, feed_buffer); 


    p_net->ping();
    if(p_net->send_data(send_buffer, data_len) != 0)
    {
        ERROR_LOG("send data failed");
        return -1;
    }

    //校巴的feed数据，转发，这里不在等待返回
    return 0;


    char recv_buffer[MAX_DATA_BUFFER]= {0};
    int recv_buffer_len =0;
    int recv_len = 0;
    int result = 0;
    int begin_time = time(NULL); 
    while(1)
    {
        result = p_net->do_io();
        recv_len = p_net->recv_data(recv_buffer + recv_buffer_len, sizeof(recv_buffer) - recv_buffer_len);
        if(recv_len > 0)
        {
            recv_buffer_len += recv_len;
        }
        else if(recv_len == 0)
        {
            int cur_time = time(NULL);
            if(cur_time - begin_time > TOTAL_CYCLE_COUNT)
            {
                ERROR_LOG("recv from cache server timeout");
                return -1;
            }
        }
        else
        {
            ERROR_LOG("error happened");
            return -1;
        }
        
        //*************************解包**********************************
        int msg_len = sizeof(dispatch_response_msg_t);

        if(recv_buffer_len >= msg_len)
        {
            dispatch_response_msg_t *p_recv_msg = (dispatch_response_msg_t*)recv_buffer;
            if(p_recv_msg->result != 0)
            {
                char buffer[MAX_DATA_BUFFER * 3 + 1];                
                hexdump((unsigned char *)p_data, 13, buffer);        
                ERROR_LOG("dispatch server return failed");
                return -1;
            }
            else
            {
                return 0;
            }
        }

        if(result != 0)
        {
            ERROR_LOG("do_io error");
            return -1;
        }
        //******************************************************************
    }

    return 0;
}


int proto_process(const ss_message_header_t &ss_msg_hdr, void *p_data)
{
    //DEBUG_LOG("proto_id:%d",ss_msg_hdr.proto_id);
    if(NULL == p_data)
    {
        ERROR_LOG("p_data is NULL.");
        return -1;
    }

    if (0 == *(uint16_t *)p_data) //去掉重发0的情况
    {
        return 0;
    }

    if(ss_msg_hdr.len - sizeof(ss_message_header_t) < sizeof(dispatch_request_msg_t))
    {
        ERROR_LOG("request data len is error [%lu < %lu]",ss_msg_hdr.len - sizeof(ss_message_header_t),sizeof(dispatch_request_msg_t));
        return 0;
    }

    int ret_value = 0;
    int proto_id = ss_msg_hdr.proto_id;
    if (proto_id == SHARED_DATA)
    {
        ret_value = dispatch_to_dispatch(ss_msg_hdr, p_data);            
        //return ret_value;
        return 0;
    }
    else
    {
        ERROR_LOG("proto_id[%d] not known for this so", proto_id);
        return -1;
    }
}

int proto_uninit()
{
    p_net->uninit();
    p_net->release();
    p_net = NULL;
    return 0;
}


int timer_process(void *p_data)
{
    return 0;
}

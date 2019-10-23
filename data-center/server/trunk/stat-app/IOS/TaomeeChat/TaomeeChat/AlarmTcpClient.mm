//
//  AlarmTcpClient.m
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <sys/socket.h>
#import <netinet/in.h>
#import <arpa/inet.h>
#import <unistd.h>
#import <errno.h>

#include "app_login_request.pb.h"
#include "app_pull_msg.pb.h"
#import "SettingTable.h"
#import "AlarmMsgTable.h"
#import "AlarmNotificationTable.h"

#import "AlarmTcpClient.h"

@implementation AlarmTcpClient : NSObject

static const char * alarmer_ip = "61.155.182.20";
static const int alarmer_port = 19400;
static const int time_out = 10;

struct proto_header {
    uint32_t len;
    uint32_t proto_id;
    char body[];
};

static int trytimes = 0;

+ (BOOL) login:(NSString *)name :(NSString *)pwd :(NSString *)mobile :(NSString*)token
{
    StatAppLoginProto::StatAppLoginRequest request;
    request.set_user_name(name.UTF8String);
    request.set_password(pwd.UTF8String);
    request.set_mobile(mobile.UTF8String);
    request.set_device_type("4");
    request.set_token([SettingTable getSettingValue:KEY_BD_TOKEN].UTF8String);
//    NSLog(@"name : %s, pwd : %s, mobile : %s, token: %s", request.user_name().c_str(), request.password().c_str(), request.mobile().c_str(), request.token().c_str());
    
    int fd = [self connectToAlarmer];
    if (fd < 0)
        return -1;
    BOOL ret = NO;
    do {
        int len = sizeof(struct proto_header) + request.ByteSize();
        char *msg = (char*)malloc(len);
        memset(msg, 0, len);
        proto_header *header = (struct proto_header*) msg;
        header->len = len;
        header->proto_id = 0xB011;
        request.SerializeToArray(header->body, request.ByteSize());

        ret = [self sendData:fd :msg :len];
        free(msg);
        if (!ret)
            break;
    
        ret = [self recvData:fd :(char*)&len :sizeof(uint32_t)];
        if (!ret)
            break;
    
        char * resMsg = (char*) malloc(len);
        memset(resMsg, 0, len);
        ret = [self recvData:fd :resMsg :len - sizeof(uint32_t)];
        if (!ret) {
            free(resMsg);
            break;
        }
    
        StatAppLoginProto::StatAppLoginResponse response;
        if (!response.ParseFromArray(resMsg + sizeof(uint32_t), len-sizeof(uint32_t)*2)) {
            free(resMsg);
            ret = NO;
            break;
        }
        ret = (response.ret() == 0);
        free(resMsg);
    }while (NO);
    [self disconect:fd];
    
    return ret;
}

+ (void) pullMsg
{
    trytimes = 0;
    [NSTimer scheduledTimerWithTimeInterval:0.01 target:self selector:@selector(doPullMsg) userInfo:nil repeats:NO];
}

+ (void) doPullMsg
{
    ++trytimes;
    if (trytimes >= 5)
        return;
    
    StatPullMsgProto::AppPullMsgRequest request;
    request.set_user_name([[SettingTable getSettingValue:KEY_NAME] UTF8String]);
    request.set_token([[SettingTable getSettingValue:KEY_BD_TOKEN] UTF8String]);

//    NSMutableArray *msgIdArray = [[AlarmNotificationTable Instance] getNotificationArray];
//    if (nil == msgIdArray || msgIdArray.count == 0) {
//    }
//    else
//        for (int i=0; (nil!=msgIdArray) && i<msgIdArray.count; ++i) {
//            request.add_msg_id([[msgIdArray objectAtIndex:i] UTF8String]);
//            NSLog(@"msgId : %@", [msgIdArray objectAtIndex:i]);
//        }
    int fd = [self connectToAlarmer];
    do {
        if (fd < 0)
            break;
    
        BOOL ret = NO;
    
        int len = sizeof(struct proto_header) + request.ByteSize();
        char *msg = (char*)malloc(len);
        memset(msg, 0, len);
        proto_header *header = (struct proto_header*) msg;
        header->len = len;
        header->proto_id = 0xB010;
        request.SerializeToArray(header->body, len);
        
        ret = [self sendData:fd :msg :len];
        free(msg);
        if (!ret)
            break;
        
        ret = [self recvData:fd: (char*)&len :sizeof(uint32_t)];
        if (!ret)
            break;
        
        char * resMsg = (char*) malloc(len);
        memset(resMsg, 0, len);
        ret = [self recvData:fd :resMsg :len - sizeof(uint32_t)];
        if (!ret) {
            free(resMsg);
            break;
        }
        
        StatPullMsgProto::AppPullMsgResponse response;
        if (!response.ParseFromArray(resMsg + sizeof(uint32_t), len-sizeof(uint32_t)*2)) {
            free(resMsg);
            ret = NO;
            break;
        }
        free(resMsg);
        
        if (response.ret() != 0)
            break;
        
        NSLog(@"received %d message.",response.msg_size());
        
//        NSMutableArray *msgArray = [[NSMutableArray alloc] init];
        for (int i=0; i<response.msg_size(); ++i) {
            AlarmMsgItem *item = [AlarmMsgItem buildItemWith:0
                                                            :response.msg(i).msg_id().c_str()
                                                            :response.msg(i).title().c_str()
                                                            :response.msg(i).content().c_str()
                                                            :0 :0 :0
                                                            :[self getTimeFromMsgid:response.msg(i).msg_id().c_str()]];
            [[AlarmMsgTable Instance] insertAlarmMsg:item];
//            [msgArray addObject:item];
        }
        [[NSNotificationCenter defaultCenter] postNotificationName:MSG_PULL_SUCCESS object:nil];
        [self disconect:fd];
        return;
        
    }while (NO);
    [self disconect:fd];
    [NSTimer scheduledTimerWithTimeInterval:10 target:self selector:@selector(doPullMsg) userInfo:nil repeats:NO];
    return;
}

+ (int) getTimeFromMsgid:(const char *)msgid
{
    NSString * msgId = [[NSString alloc] initWithUTF8String:msgid];
    return [[[msgId componentsSeparatedByString:@":"] objectAtIndex:0] intValue];
}

+ (int) connectToAlarmer
{
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    NSLog(@"socket fd : %d", fd);
    if (fd < 0)
        return -1;
    
    struct sockaddr_in serverAddr = {0};
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(alarmer_port);
    serverAddr.sin_addr.s_addr = inet_addr(alarmer_ip);
    
    int ret = connect(fd, (struct sockaddr*)&serverAddr, sizeof(struct sockaddr));
    if (ret != 0)
    {
        close(fd);
        return -1;
    }
    
    struct timeval tv = {0};
    tv.tv_sec = time_out;
    tv.tv_usec = 0;
    if (setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(struct timeval)) != 0 ||
        setsockopt(fd, SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(struct timeval)) != 0)
    {
        close(fd);
        return -1;
    }
    return fd;
}

+ (BOOL) sendData:(int)m_fd :(char*)buffer :(uint32_t)len
{
    int ret = 0;
    uint32_t sentLen = 0;
    while (sentLen < len) {
        ret = send(m_fd, buffer+sentLen, len-sentLen, 0);
        if (ret <= 0)
            return NO;
        sentLen += ret;
    }
    return YES;
}

+ (BOOL) recvData:(int)m_fd :(char*)buffer :(uint32_t)len
{
    int ret = 0;
    uint32_t recvLen = 0;
    while (recvLen < len) {
        ret = recv(m_fd, buffer+recvLen, len-recvLen, 0);
        if (ret <= 0) {
            return NO;
        }
        recvLen += ret;
    }
    return YES;
}

+ (void)disconect:(int)m_fd
{
    if (m_fd > 0)
        close(m_fd);
    m_fd = -1;
}

@end

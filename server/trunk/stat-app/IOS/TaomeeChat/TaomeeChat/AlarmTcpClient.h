//
//  AlarmTcpClient.h
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <Foundation/Foundation.h>

#define MSG_PULL_SUCCESS    @"pull_alarm_msg_success"
#define MSG_PULL_FAILED     @"pull_alarm_msg_failed"

@interface AlarmTcpClient : NSObject {
//    int m_fd;
}

+ (BOOL) login:(NSString *)name :(NSString *)pwd :(NSString*)mobile :(NSString*)token;
+ (void) pullMsg;

@end

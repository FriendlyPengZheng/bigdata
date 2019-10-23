//
//  SettingTable.h
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDatabaseHelper;

#define MAX_VALUE_LENGTH (0xFF)

#define KEY_NAME            (0x0001)
#define KEY_MOBILE          (0x0002)
#define KEY_BD_TOKEN        (0x0003)
#define KEY_ALARMER_IP      (0x0004)
#define KEY_ALARMER_PORT    (0x0005)
#define KEY_API_KEY         (0x0006)
#define KEY_SECRET_KEY      (0x0007)
#define KEY_MAX_MSG_COUNT   (0x0008)
#define KEY_MAX_MSG_TIME    (0x0009)
#define KEY_PULL_WAIT       (0x000A)
#define KEY_ONCE_SHOW_COUNT (0x000B)
#define KEY_REFRESH_COUNT   (0x000C)
#define KEY_BELL_FLAG       (0x000D)
#define KEY_APP_MODE        (0x000E)
#define KEY_LOGIN_STATE     (0x000F)

@interface SettingTable : NSObject

+ (BOOL) initialize:(LocalDatabaseHelper *)dbHelper;

+ (NSString *) getSettingValue:(int)key;

+ (BOOL) setKey:(int)key toValue:(NSString *)value;

@end

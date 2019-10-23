//
//  AlarmMsgTable.h
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <sqlite3.h>
#import <Foundation/Foundation.h>

#import "AlarmMsgItem.h"

@class LocalDatabaseHelper;

@interface AlarmMsgTable : NSObject {
@private LocalDatabaseHelper *m_dbHelper;
}

typedef enum ALARM_MSG_STATE {
    ALL,
    UNREAD,
    READ,
    MARKED,
    CHECKED
} AlarmMsgType;

typedef enum ACTION_ON_ALARM_MSG {
    MARK_AS_READ,
    MARK_AS_UNREAD,
    MARK_AS_CARE,
    MARK_AS_UNCARE,
    
    DELETE_CHECEKED,
} ActionOnAlarmMsg;

+ (BOOL) initialize:(LocalDatabaseHelper *)dbHelper;

- (int) getAlarmMsgCount:(AlarmMsgType)type;

- (NSMutableArray *) getAlarmMsg:(AlarmMsgType)type :(int)count;
- (NSMutableArray *) getMoreAlarmMsg:(AlarmMsgType)type :(BOOL)direction :(long)_id :(int)count;

- (BOOL) setMsg:(NSString *)msgId Checked:(BOOL)checked;
- (BOOL) setType:(AlarmMsgType)type Checked:(BOOL)checked;

- (BOOL) doActionOnCheckedRows:(ActionOnAlarmMsg)action;

- (BOOL) insertAlarmMsg:(AlarmMsgItem *)item;

+ (AlarmMsgTable*) Instance;

@end

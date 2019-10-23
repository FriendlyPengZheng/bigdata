//
//  AlarmMsgTable.m
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "LocalDatabaseHelper.h"
#import "AlarmMsgItem.h"
#import "AlarmNotificationTable.h"
#import "SettingTable.h"

#import "AlarmMsgTable.h"
#define TABLE_NAME      @"t_alarm_msg"

#define C_MSG_ID        @"msg_id"
#define C_MSG_TITLE     @"msg_title"
#define C_MSG_CONTENT   @"msg_content"
#define C_MSG_STATUS    @"msg_status"
#define C_MSG_MARK      @"msg_mark"
#define C_MSG_CHECKED   @"msg_checked"
#define C_MSG_TIME      @"msg_time"

@implementation AlarmMsgTable

static AlarmMsgTable * instance = nil;

+ (AlarmMsgTable *) Instance
{
    return instance;
}

+ (BOOL) initialize:(LocalDatabaseHelper *)dbHelper
{
    instance = [[AlarmMsgTable alloc] init];
    instance->m_dbHelper = dbHelper;
    NSString *sqlFormat = @"create table if not exists %@ (_id integer primary key autoincrement, %@ text, %@ text, %@ text, %@ integer, %@ integer, %@ integer, %@ integer)";
    NSString *sql = [[NSString alloc] initWithFormat:sqlFormat, TABLE_NAME, C_MSG_ID, C_MSG_TITLE, C_MSG_CONTENT, C_MSG_STATUS, C_MSG_MARK, C_MSG_CHECKED, C_MSG_TIME];
    BOOL ret = [dbHelper exec:sql];
    [sql release];
    return ret;
}

static int callback(void *param, int cloumnCount, char ** values, char ** name)
{
    AlarmMsgItem *item = [AlarmMsgItem buildItemWith:atol(values[0]) :values[1] :values[2] :values[3] :atoi(values[4]) :atoi(values[5]) :atoi(values[6]) :atoi(values[7])];
    if (nil != item)
        [(__bridge NSMutableArray*)param addObject:item];
    [item release];
    return 0;
}

- (NSString *) getCondition:(AlarmMsgType)type
{
    switch (type) {
        case ALL:
            return [[NSString alloc] initWithFormat:@"%@ >= 0", C_MSG_STATUS];
        case READ:
            return [[NSString alloc] initWithFormat:@"%@ > 0", C_MSG_STATUS];
        case UNREAD:
            return [[NSString alloc] initWithFormat:@"%@ = 0", C_MSG_STATUS];
        case MARKED:
            return [[NSString alloc] initWithFormat:@"%@ > 0", C_MSG_MARK];
        case CHECKED:
            return [[NSString alloc] initWithFormat:@"%@ > 0", C_MSG_CHECKED];
        default:
            return @"";
    }
}

- (int) getAlarmMsgCount:(AlarmMsgType)type
{
    return [m_dbHelper countInTable:TABLE_NAME Condition:[self getCondition:type]];
}


- (NSMutableArray *) quaryWithConditon:(NSString*)condition Order:(BOOL)order Limit:(int)limit
{
    NSString *whereStr = condition.length > 0 ? [[NSString alloc] initWithFormat:@"where %@", condition] : @"";
    NSString *orderStr = order ? @"desc" : @"asc";
    NSString *limitStr = limit > 0 ? [[NSString alloc] initWithFormat:@"limit %d", limit] : @"";
    
    NSString *sqlFormat = @"select * from %@ %@ order by %@ %@ %@;";
    NSString *sql = [[NSString alloc] initWithFormat:sqlFormat, TABLE_NAME, whereStr, C_MSG_TIME, orderStr, limitStr];
    
    NSMutableArray *msgArray = [[NSMutableArray alloc] init];
    if ([m_dbHelper exec:sql :(sqlite3_callback)callback : (void*)msgArray]) {
        [sql release];
        return msgArray;
    }
    else {
        [msgArray release];
        return nil;
    }
}

- (NSMutableArray *) getAlarmMsg:(AlarmMsgType)type :(int)limitCount
{
    return [self quaryWithConditon:[self getCondition:type] Order:(UNREAD != type) Limit:limitCount];
}

- (NSMutableArray *) getMoreAlarmMsg:(AlarmMsgType)type :(BOOL)direction :(long)rowId :(int)count
{
    NSString * condition = [[NSString alloc] initWithFormat:@"_id %c %ld and %@", direction?'>':'<', rowId, [self getCondition:type]];
    return [self quaryWithConditon:condition Order:!direction Limit:count];
}

- (BOOL) setMsg:(NSString *)msgId Checked:(BOOL)checked
{
    NSString *condition = (nil == msgId || msgId.length == 0) ? @"" : [[NSString alloc] initWithFormat:@"where %@ = '%@'", C_MSG_ID, msgId];
    NSString *sql = [[NSString alloc] initWithFormat:@"update %@ set %@ = %d %@;", TABLE_NAME, C_MSG_CHECKED, checked?1:0, condition];
//    NSLog(@"%@",sql);
    return [m_dbHelper exec:sql];
}

- (BOOL) setType:(AlarmMsgType)type Checked:(BOOL)checked
{
    NSString *sql = [[NSString alloc] initWithFormat:@"update %@ set %@ = %d where %@;", TABLE_NAME, C_MSG_CHECKED, checked?1:0, [self getCondition:type]];
//    NSLog(@"%@",sql);
    return [m_dbHelper exec:sql];
}

- (BOOL) doActionOnCheckedRows:(ActionOnAlarmMsg)action
{
    NSString *sql = nil;
    switch (action) {
        case MARK_AS_READ:
            sql = [[NSString alloc] initWithFormat:@"update %@ set %@ = 1, %@ = 0 where %@;", TABLE_NAME, C_MSG_STATUS, C_MSG_CHECKED, [self getCondition:CHECKED]];
            break;
        case MARK_AS_UNREAD:
            sql = [[NSString alloc] initWithFormat:@"update %@ set %@ = 0, %@ = 0 where %@;", TABLE_NAME, C_MSG_STATUS, C_MSG_CHECKED, [self getCondition:CHECKED]] ;
            break;
        case MARK_AS_CARE:
            sql = [[NSString alloc] initWithFormat:@"update %@ set %@ = 1, %@ = 0 where %@;", TABLE_NAME, C_MSG_MARK, C_MSG_CHECKED, [self getCondition:CHECKED]] ;
            break;
        case MARK_AS_UNCARE:
            sql = [[NSString alloc] initWithFormat:@"update %@ set %@ = 0, %@ = 0 where %@;", TABLE_NAME, C_MSG_MARK, C_MSG_CHECKED, [self getCondition:CHECKED]] ;
            break;
        case DELETE_CHECEKED:
            sql = [[NSString alloc] initWithFormat:@"delete from %@ where %@;", TABLE_NAME, [self getCondition:CHECKED]];
            break;
        default:
            return NO;
    }
//    NSLog(@"%@",sql);
    return [m_dbHelper exec:sql];
}

- (BOOL) insertAlarmMsg:(AlarmMsgItem *)item
{
    NSString *sql = [[NSString alloc] initWithFormat:@"insert into %@ values (null, '%@', '%@', '%@', %d, %d, %d, %d);",
                     TABLE_NAME, item.m_msgId, item.m_title, item.m_content, item.m_status, item.m_mark, item.m_checked, item.m_time];
//    NSLog(@"%@",sql);
    [[AlarmNotificationTable Instance] deleteNotification:item.m_msgId];
    return [m_dbHelper exec:sql];
}

@end

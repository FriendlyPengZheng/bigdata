//
//  AlarmNotificationTable.m
//  TaomeeChat
//
//  Created by violet on 14-5-15.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "LocalDatabaseHelper.h"

#import "AlarmNotificationTable.h"

@implementation AlarmNotificationTable

#define TABLE_NAME  @"t_alarm_notification"

#define C_MSG_ID    @"msgid"
#define C_STATE     @"state"

static AlarmNotificationTable * instance = nil;

+ (AlarmNotificationTable*) Instance
{
    return instance;
}

- (BOOL) initialize:(LocalDatabaseHelper *)dbHelper
{
    instance = self;
    m_dbHelper = dbHelper;
    
    NSString *sqlFormat = @"create table if not exists %@ (rowid integer primary key autoincrement, %@ text, %@ integer);";
    NSString *sql = [[NSString alloc] initWithFormat:sqlFormat, TABLE_NAME, C_MSG_ID, C_STATE];
    
    return [dbHelper exec:sql];
}

- (BOOL) insertNewAlarmNotification:(NSString *)msgId
{
    NSString *sql = [[NSString alloc] initWithFormat:@"insert into %@ values(null, %@, 0)",TABLE_NAME ,msgId];
    return [m_dbHelper exec:sql];
}

- (int) getNotificationCount
{
    return [m_dbHelper countInTable:TABLE_NAME Condition:nil];
}

static int callback(void *param, int cloumnCount, char ** values, char ** name)
{
    NSString * msgId = [[NSString alloc] initWithUTF8String:values[0]];
    [(__bridge NSMutableArray*)(param) addObject:msgId];
    return 0;
}

- (NSMutableArray*) getNotificationArray
{
    NSString * sql = [[NSString alloc] initWithFormat:@"select %@ from %@;", C_MSG_ID, TABLE_NAME];
    NSString * updateSql = [[NSString alloc] initWithFormat:@"update %@ set %@ = %@ + 1;", TABLE_NAME, C_STATE, C_STATE];
    NSMutableArray *array = [[NSMutableArray alloc] init];
    if ([m_dbHelper exec:updateSql] && [m_dbHelper exec:sql :(sqlite3_callback)callback :(void*)array])
        return array;
    else
        return nil;
}

- (int) deletePullFailedNotification
{
    NSString * sql = [[NSString alloc] initWithFormat:@"delete from %@ where %@ >= 1;", TABLE_NAME, C_STATE];
    if ([m_dbHelper exec:sql])
        return [m_dbHelper getChangeRowCount];
    else
        return -1;
}

- (BOOL) deleteNotification:(NSString*)msgId
{
    NSString * sql = [[NSString alloc] initWithFormat:@"delete from %@ where %@ = '%@';", TABLE_NAME, C_MSG_ID, msgId];
    return [m_dbHelper exec:sql];
}

@end

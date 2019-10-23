//
//  LocalDatabaseHelper.m
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "LocalDatabaseHelper.h"
#import "AlarmMsgTable.h"
#import "SettingTable.h"
#import "AlarmNotificationTable.h"

@implementation LocalDatabaseHelper

//@synthesize m_msgTable, m_notificationTable, m_settingTable;

#define DBNAME  @"taomee_chat.db"

static LocalDatabaseHelper * instance = nil;

+ (LocalDatabaseHelper*) Instance
{
    return instance;
}

+ (void) openLocalDataBase
{
    if (nil == instance)
        instance = [[LocalDatabaseHelper alloc] init];
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *dir = [paths objectAtIndex:0];
    NSString *path = [dir stringByAppendingPathComponent:DBNAME];
    
    int ret = sqlite3_open([path UTF8String], &instance->m_db);
    if (ret != SQLITE_OK)
    {
        NSLog(@"Sqlite open %@ failed : %d", path,ret);
        NSLog(@"Error : %s", sqlite3_errmsg(instance->m_db));
        return;
    }
    
    NSLog(@"Sqlite open success");
    if (![AlarmMsgTable initialize:instance])
        NSLog(@"AlamMsgTable Initialize failed!");
    
    if (![SettingTable initialize:instance])
        NSLog(@"SettingTable Initialize failed!");
    if ([[AlarmMsgTable Instance] getAlarmMsgCount:CHECKED] > 0)
        [[AlarmMsgTable Instance] setMsg:nil Checked:NO];
    
//    for (int i=0; i < 1000; i++) {
//        time_t t;
//        time(&t);
//        NSString * sql = [[NSString alloc] initWithFormat:@"insert into t_alarm_msg values (null, 'id:%d', '%d', 'contentxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 0, 0, 0, %ld);",i+1, i+1,t+i];
//        [instance exec:sql];
//    }
}

- (BOOL) exec:(NSString *)sql
{
    char * errmsg = NULL;
    NSLog(@"%@",sql);
    if (sqlite3_exec(m_db, [sql UTF8String], NULL, NULL, &errmsg) != SQLITE_OK || errmsg != NULL)
    {
        NSLog(@"Sqlite exec failed : %s", errmsg);
        sqlite3_free(errmsg);
        return NO;
    }
//    NSLog(@"change %d rows", [self getChangeRowCount]);
    return YES;
}

- (BOOL) exec:(NSString *)sql :(sqlite3_callback)callback : (void *) param
{
    NSLog(@"%@",sql);
    char * errmsg = NULL;
    if (sqlite3_exec(m_db, [sql UTF8String], callback, param, &errmsg)!= SQLITE_OK || errmsg != NULL)
    {
        NSLog(@"Sqlite exec failed : %s", errmsg);
        sqlite3_free(errmsg);
        return NO;
    }
    return YES;
}

static int counterCallback(int * count, int cloumnCount, char ** values, char ** name)
{
    *count = atoi(values[0]);
    return 0;
}

- (int) countInTable:(NSString *)table Condition:(NSString *)condition
{
    NSString * where = (condition == nil || condition.length == 0) ? @"" : [[NSString alloc] initWithFormat:@"where %@", condition];
    NSString *sql = [[NSString alloc] initWithFormat:@"select count(*) from %@ %@;", table, where];
    int count = 0;
    if ([self exec:sql :(sqlite3_callback)counterCallback: &count])
        return count;
    else
        return -1;
}

- (int) getChangeRowCount
{
    return sqlite3_changes(m_db);
}

- (void) onEnd
{
    sqlite3_close(m_db);
}

- (void) dealloc
{
    NSLog(@"local db helper dealloc");
    [self onEnd];
    [super dealloc];
}

@end

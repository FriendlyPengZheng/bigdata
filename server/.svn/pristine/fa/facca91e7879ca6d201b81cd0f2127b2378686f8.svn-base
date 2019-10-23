//
//  SettingTable.m
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "LocalDatabaseHelper.h"

#import "SettingTable.h"

#define TABLE_NAME  @"t_settings"
#define C_KEY       @"key"
#define C_VALUE     @"value"

static SettingTable * instance;

@implementation SettingTable
{
    LocalDatabaseHelper * m_dbHelper;
}

+ (BOOL) initialize:(LocalDatabaseHelper *)dbHelper
{
    instance = [[SettingTable alloc] init];
    instance->m_dbHelper = dbHelper;
    
    NSString *sqlFormat = @"create table if not exists %@ (_id integer primary key autoincrement, %@ integer, %@ text)";
    NSString *sql = [[NSString alloc] initWithFormat:sqlFormat, TABLE_NAME, C_KEY, C_VALUE];
    
    return [dbHelper exec:sql];
}

static int callback(void *param, int cloumnCount, char ** values, char ** name)
{
    NSLog(@"Settings Table Callback | count : %d", cloumnCount);
    memcpy(param, values[0], strlen(values[0]) > MAX_VALUE_LENGTH ? MAX_VALUE_LENGTH : strlen(values[0]));
    return 0;
}

+ (NSString *) getSettingValue:(int)key
{
    return [instance getSettingValue:key];
}

- (NSString *) getSettingValue:(int)key
{
    NSString *sqlFormat = @"select %@ from %@ where %@ = %d;";
    NSString *sql = [[NSString alloc] initWithFormat:sqlFormat, C_VALUE, TABLE_NAME, C_KEY, key];
    char value[MAX_VALUE_LENGTH+1] = {0};
    if ([m_dbHelper exec:sql :(sqlite3_callback)callback :value] && (strlen(value) > 0))
        return [[NSString alloc] initWithUTF8String:value] ;
    else
        return nil;
}

+ (BOOL) setKey:(int)key toValue:(NSString *)value
{
    return [instance setKey:key toValue:value];
}

- (BOOL) setKey:(int)key toValue:(NSString *)value
{
    NSString *updateFormat = @"update %@ set %@ = '%@' where %@ = %d;";
    NSString *update = [[NSString alloc] initWithFormat:updateFormat, TABLE_NAME, C_VALUE, value, C_KEY, key];
    if ([m_dbHelper exec:update] && [m_dbHelper getChangeRowCount] == 0) {
        NSString *insertFormat = @"insert into %@  values (null, %d, '%@');";
        NSString *insert = [[NSString alloc] initWithFormat:insertFormat, TABLE_NAME, key, value];
        return [m_dbHelper exec:insert];
    }

    return YES;
}

@end

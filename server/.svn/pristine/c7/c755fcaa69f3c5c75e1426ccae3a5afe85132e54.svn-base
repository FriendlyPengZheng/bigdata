//
//  LocalDatabaseHelper.h
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <sqlite3.h>
#import <Foundation/Foundation.h>

#import "AlarmMsgTable.h"
#import "AlarmNotificationTable.h"
#import "SettingTable.h"

typedef enum SQL_ACTOIN {
    SQL_CREATE_TABLE,
    SQL_DROP_TABLE,
    
    SQL_INSERT,
    SQL_UPDATE,
    SQL_DELETE,
    SQL_SELECT,
    
    SQL_COUNT
} SqlAction;

@interface LocalDatabaseHelper : NSObject {
@private sqlite3 *m_db;
//@private AlarmMsgTable *m_msgTable;
//@private AlarmNotificationTable *m_notificationTable;
//@private SettingTable *m_settingTable;
}

//@property (nonatomic, readonly) AlarmMsgTable * m_msgTable;
//@property (nonatomic, readonly) SettingTable * m_settingTable;
//@property (nonatomic, readonly) AlarmNotificationTable * m_notificationTable;

+ (LocalDatabaseHelper*) Instance;

+ (void) openLocalDataBase;
- (BOOL) exec:(NSString *)sql;
- (BOOL) exec:(NSString *)sql :(sqlite3_callback)callback :(void*)param;

- (int) countInTable:(NSString*)table Condition:(NSString*)condition;

- (int) getChangeRowCount;

- (void) onEnd;

@end

//
//  AlarmNotificationTable.h
//  TaomeeChat
//
//  Created by violet on 14-5-15.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalDatabaseHelper;

@interface AlarmNotificationTable : NSObject {
@private LocalDatabaseHelper *m_dbHelper;
}

- (BOOL) initialize:(LocalDatabaseHelper *)dbHelper;

- (BOOL) insertNewAlarmNotification:(NSString*)msgId;

- (int) getNotificationCount;
- (NSMutableArray*) getNotificationArray;
- (int) deletePullFailedNotification;
- (BOOL) deleteNotification:(NSString*)msgId;

+ (AlarmNotificationTable *) Instance;
@end

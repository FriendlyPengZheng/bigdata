//
//  AlarmMsgItem.h
//  TaomeeChat
//
//  Created by violet on 14-5-15.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AlarmMsgItem : NSObject
{
@private long m_rowId;
@private NSString *m_msgId;
@private NSString *m_title;
@private NSString *m_content;
@private int m_status;
@private int m_mark;
@private int m_checked;
@private int m_time;
}

@property (nonatomic, readonly) long m_rowId;
@property (nonatomic, readonly, retain) NSString *m_msgId;
@property (nonatomic, readonly, retain) NSString *m_title;
@property (nonatomic, readonly, retain) NSString *m_content;
@property (nonatomic) int m_status;
@property (nonatomic) int m_mark;
@property (nonatomic) int m_checked;
@property (nonatomic, readonly) int m_time;

+ (AlarmMsgItem*) buildItemWith:(long)rowId :(const char*)msgId :(const char*)title :(const char*)content :(int)status :(int)mark :(int)checked :(int)time;

@end

//
//  AlarmMsgItem.m
//  TaomeeChat
//
//  Created by violet on 14-5-15.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "AlarmMsgItem.h"

@implementation AlarmMsgItem

@synthesize m_rowId, m_msgId, m_title, m_content, m_status, m_mark, m_checked, m_time;

+ (id) buildItemWith:(long)rowId :(const char*)msgId :(const char*)title :(const char*)content :(int)status :(int)mark :(int)checked :(int)time
{
    if (NULL == msgId || NULL == title || NULL == content) {
        return nil;
    }
    
//    NSLog(@"%ld\t%s\t%s\t%s\t%d\t%d\t%d\t%d",rowId,msgId,title,content,status,mark,checked,time);
    
    AlarmMsgItem *item = [[AlarmMsgItem alloc] init];
    item->m_rowId = rowId;
    item->m_msgId = [[NSString alloc] initWithUTF8String:msgId];
    item->m_title = [[NSString alloc] initWithUTF8String:title];
    item->m_content = [[NSString alloc] initWithUTF8String:content];
//    NSLog(@"%@\t%@\t%@", item.m_msgId,item.m_title, item.m_content);
    item->m_status = status;
    item->m_mark = mark;
    item->m_checked = checked;
    item->m_time = time;
    return item;
}

@end

//
//  AlarmMsgUIControllerViewController.m
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#include <list>

#import "AlarmMsgTable.h"
#import "SettingTable.h"
#import "LoginViewController.h"
#import "AlarmTcpClient.h"
#import "Toast.h"

#import "AlarmMsgUIControllerViewController.h"

#define CELL_IDENTIFIER @"CellIdentifier"

@interface AlarmMsgUIControllerViewController () {
    IBOutlet UIButton *m_allBtn;
    IBOutlet UIButton *m_readBtn;
    IBOutlet UIButton *m_unreadBtn;
    IBOutlet UIButton *m_markedBtn;
    IBOutlet UIButton *m_menuBtn;
    
    IBOutlet UIView * m_msgDisplayView;
    IBOutlet UIView * m_typeView;
    IBOutlet UIView * m_ActionBarView;
    
    IBOutlet UIButton * m_deleteBtn;
    IBOutlet UIButton * m_markReadBtn;
    IBOutlet UIButton * m_markUnreadBtn;
    IBOutlet UIButton * m_markCareBtn;
    IBOutlet UIButton * m_dismissCareBtn;
    IBOutlet UISwitch * m_switchAll;
    
    IBOutlet UIButton * m_previousBtn;
    IBOutlet UIButton * m_nextBtn;
    
    ActionOnAlarmMsg m_action;
    
    IBOutlet UITableView *m_tableView;
//    NSMutableArray *m_dataArray;
    
    AlarmMsgTable * m_alarmMsgTable;
    AlarmMsgType m_showType;
    
    int m_maxShowCountOnce;
    int m_moreTryGetCount;
    int m_totalCount;
    int m_selectedCount;
    
    BOOL m_tryAfter;
    BOOL m_tryBefore;
//    BOOL m_isUpdateData;
    int m_counter;
    BOOL m_isAfter;
    
//    int m_getMoreFlag;
}

@end

@implementation AlarmMsgUIControllerViewController

@synthesize m_dataArray;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
//    if (m_tableView == nil) {
//        CGRect frame = m_msgDisplayView.frame;
//        frame.origin = CGPointMake(0, 0);
//        m_tableView = [[UITableView alloc] initWithFrame:frame];
//        [m_tableView setDelegate:self];
//        [m_tableView setDataSource:self];
//        [m_msgDisplayView addSubview:m_tableView];
//    }
    m_tableView.center = m_msgDisplayView.center;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onAlarmMsgPullSuccess) name:MSG_PULL_SUCCESS object:nil];
    
    if (0 != [m_alarmMsgTable getAlarmMsgCount:UNREAD])
        [self changeDisplayType:UNREAD];
    else if (0 != [m_alarmMsgTable getAlarmMsgCount:MARKED])
        [self changeDisplayType:MARKED];
    else if (0 != [m_alarmMsgTable getAlarmMsgCount:READ])
        [self changeDisplayType:READ];
    else {
        m_allBtn.enabled = NO;
        [self changeDisplayType:ALL];
    }

    [m_ActionBarView setAlpha:0];
    m_previousBtn.alpha = 0;
    m_nextBtn.alpha = 0;
}

- (void) onAlarmMsgPullSuccess
{
    NSLog(@"UI alarm pull success !");
    [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
    
    int count = [[AlarmMsgTable Instance] getAlarmMsgCount:UNREAD];
    if (count <= 0)
        return;
    
    NSString *msg = [[NSString alloc] initWithFormat:@"您有%d条未读消息！",count];
    [Toast ShowToastWithMessge:msg Least:2 OnView:m_msgDisplayView BackGroundColor:[UIColor whiteColor]];
    
    if (m_showType != UNREAD) {
        NSString * title = [[NSString alloc] initWithFormat:@"未读(%d)",count];
        [m_unreadBtn setTitle:title forState:UIControlStateNormal];
        [title release];
    }
    else {
        if (count < m_maxShowCountOnce)
            [self updateTableViewData];
//        m_tryAfter = YES;
    }
    [msg release];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction) onButtonClick:(id)sender
{
    if (sender == m_allBtn) {
        [self changeDisplayType:ALL];
    }
    else if (sender == m_readBtn) {
        [self changeDisplayType:READ];
    }
    else if (sender == m_unreadBtn) {
        [self changeDisplayType:UNREAD];
    }
    else if (sender == m_markedBtn) {
        [self changeDisplayType:MARKED];
    }
    else if (sender == m_menuBtn) {
        LoginViewController *login = [[LoginViewController alloc] init];
        [self addChildViewController:login];
        [self.view addSubview:login.view];
        login.view.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
        
//        [AlarmTcpClient pullMsg];
    } else if (sender == m_previousBtn) {
        m_isAfter = NO;
        m_previousBtn.alpha = 0;
        [self tryLoadMoreAlarmMsg];
    } else if (sender == m_nextBtn) {
        m_isAfter = YES;
        m_nextBtn.alpha = 0;
        [self tryLoadMoreAlarmMsg];
    }
    else if (sender == m_switchAll) {
        NSLog(@"All Checked Press");
        [m_alarmMsgTable setType:m_showType Checked:m_switchAll.on];
        m_selectedCount = [m_alarmMsgTable getAlarmMsgCount:CHECKED];
        if (m_selectedCount == 0)
            [self hideOperatorBar];
        [self updateTableViewData];
    }
    else if (m_selectedCount > 0) {
        if (sender == m_deleteBtn) {
            m_action = DELETE_CHECEKED;
            NSString * msg = [[NSString alloc] initWithFormat:@"选中的%d条消息将被删除", m_selectedCount];
            [self showAlertViewWithString:msg];
            [msg release];
        }
        else if (sender == m_markReadBtn) {
            m_action = MARK_AS_READ;
            NSString * msg = [[NSString alloc] initWithFormat:@"选中的%d条消息将被标记为已读", m_selectedCount];
            [self showAlertViewWithString:msg];
            [msg release];
        }
        else if (sender == m_markUnreadBtn) {
            m_action = MARK_AS_UNREAD;
            NSString * msg = [[NSString alloc] initWithFormat:@"选中的%d条消息将被标记为未读", m_selectedCount];
            [self showAlertViewWithString:msg];
            [msg release];
        }
        else if (sender == m_markCareBtn) {
            m_action = MARK_AS_CARE;
            [self alertView:nil clickedButtonAtIndex:0];
        }
        else if (sender == m_dismissCareBtn) {
            m_action = MARK_AS_UNCARE;
            [self alertView:nil clickedButtonAtIndex:0];
        }
//        [self hideOperatorBar];
    }
    
    return;
}

- (void) showAlertViewWithString:(NSString*)msg
{
    UIAlertView *alart = [[UIAlertView alloc] initWithTitle:@"请确认" message:msg delegate:self  cancelButtonTitle:@"确认" otherButtonTitles:@"取消", nil];
    [alart show];
    [alart release];
}

- (void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != 0)
        return;
    
    [m_alarmMsgTable doActionOnCheckedRows:m_action];
    [self hideOperatorBar];
    [self updateTableViewData];
}

- (void) initialize
{
    m_alarmMsgTable = [AlarmMsgTable Instance];
    
//    m_maxShowCountOnce = [[SettingTable getSettingValue:KEY_ONCE_SHOW_COUNT] intValue];
//    m_moreTryGetCount = [[SettingTable getSettingValue:KEY_REFRESH_COUNT] intValue];
    m_maxShowCountOnce = 30;
    
//    m_tableView = nil;
    m_dataArray = [[NSMutableArray alloc] init];
    m_tryAfter = NO;
    m_tryBefore = NO;
}

- (UILabel *) makeLabelInTableCellWithTag:(int) tag
{
    UILabel * label = [[UILabel alloc] initWithFrame:CGRectZero];
    label.tag = tag;
    label.lineBreakMode = NSLineBreakByWordWrapping;
//    label.highlightedTextColor = [UIColor redColor];
    label.numberOfLines = 0;
//    label.opaque = NO;
    label.backgroundColor = [UIColor clearColor];
    return label;
}

- (NSString *) getTimeString:(time_t)time
{
    NSDateFormatter * formatter = [[NSDateFormatter alloc] init];
    [formatter setDateStyle:NSDateFormatterMediumStyle];
    [formatter setTimeStyle:NSDateFormatterShortStyle];
    [formatter setDateFormat:@"yyyy-MM-dd HH:MM:ss"];
    
    NSString * str = [[NSString alloc] initWithString:[formatter stringFromDate:[NSDate dateWithTimeIntervalSince1970:time]]];
    return str;
}

- (BOOL) checkDataAscOrder
{
    if (m_dataArray.count > 1)
        return [[m_dataArray firstObject] m_rowId] <= [[m_dataArray lastObject] m_rowId];
    else
        return YES;
}

- (AlarmMsgItem *) getMsgItemAt:(int)index
{
    if ([self checkDataAscOrder]) {
        return [m_dataArray objectAtIndex:index];
    }
    else {
        return [m_dataArray objectAtIndex:m_dataArray.count-index-1];
    }
    return nil;
}

- (UITableViewCell *) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    NSLog(@"Draw Cell : %d",indexPath.row);
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CELL_IDENTIFIER];
    if (nil == cell) {
        cell = [[UITableViewCell alloc] initWithFrame:CGRectZero];
        [cell addSubview:[self makeLabelInTableCellWithTag:1]];
        [cell addSubview:[self makeLabelInTableCellWithTag:2]];
        [cell addSubview:[self makeLabelInTableCellWithTag:3]];
//        NSLog(@"0");
    }
//    NSLog(@"1");
    AlarmMsgItem * item = [self getMsgItemAt:indexPath.row];
    
    CGRect cellFrame = [cell frame];
    cellFrame.origin = CGPointMake(0, 0);
    
    UILabel *timeLabel = (UILabel*)[cell viewWithTag:1];
    timeLabel.text = [self getTimeString:item.m_time];
    CGRect frame = CGRectMake(5, 10, self.view.frame.size.width-10, 0);
    timeLabel.frame = frame;
    [timeLabel sizeToFit];
    
    UILabel *titleLabel = (UILabel*)[cell viewWithTag:2];
    titleLabel.text = item.m_title;
    frame.origin.y = timeLabel.frame.origin.y + timeLabel.frame.size.height + 5;
    titleLabel.frame = frame;
    [titleLabel sizeToFit];
    
    UILabel *contentLabel = (UILabel*)[cell viewWithTag:3];
    contentLabel.text = item.m_content;
    frame.origin.y = titleLabel.frame.origin.y + titleLabel.frame.size.height + 5;
    contentLabel.frame = frame;
    [contentLabel sizeToFit];
    
    cellFrame.size.height = contentLabel.frame.origin.y + contentLabel.frame.size.height + 5;
    [cell setFrame:cellFrame];
    [self setCell:cell StyleAtIndex:indexPath.row];

    return cell;
}

- (void) tryLoadMoreAlarmMsg
{
//    NSLog(@"Do TryLoadMoreAlarmMsg : %d", m_isAfter);
    if (m_dataArray.count == 0)
        return;
    
    NSArray * visibleRows = [m_tableView indexPathsForVisibleRows];
    int index = (m_isAfter) ? [[visibleRows lastObject] row] : [[visibleRows firstObject] row];
    NSMutableArray * array = [m_alarmMsgTable getMoreAlarmMsg:m_showType :(m_isAfter) :[self getMsgItemAt:index].m_rowId :m_maxShowCountOnce];
    m_isAfter ? (m_tryAfter = (array.count == m_maxShowCountOnce)) : (m_tryBefore = (array.count == m_maxShowCountOnce));
    
    if (array.count > 0) {
        self.m_dataArray = array;
        m_counter = 0;
        [m_tableView reloadData];
//        for (int i=0; i<m_dataArray.count; ++i) {
//            if ([[self getMsgItemAt:i] m_checked])
//                [m_tableView selectRowAtIndexPath:[NSIndexPath indexPathForItem:i inSection:0] animated:NO scrollPosition:UITableViewScrollPositionNone];
//        }
        if (m_isAfter) {
            m_tryBefore = YES;
            [m_tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
            
        }
        else {
            m_tryAfter = YES;
            [m_tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:m_dataArray.count-1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:NO];
        }
    }
    else
        [array release];
}

- (void) scrollViewDidScroll:(UIScrollView *)scrollView {
//    NSLog(@"Did Scroll");
    if (!m_tryAfter && !m_tryBefore)
        return;
    
    if (m_tryBefore && [[[m_tableView indexPathsForVisibleRows] firstObject] row] == 0) {
        m_previousBtn.alpha = 0.6;
        m_nextBtn.alpha = 0;
        return;
    }
    
    if (m_tryAfter && [[[m_tableView indexPathsForVisibleRows] lastObject] row] == m_dataArray.count-1) {
        m_previousBtn.alpha = 0;
        m_nextBtn.alpha = 0.6;
        return;
    }
    
    m_previousBtn.alpha = 0;
    m_nextBtn.alpha = 0;
}

- (void) refreshSelectStatus
{
    for (int i=0; i<m_dataArray.count; ++i) {
        //[m_tableView deselectRowAtIndexPath:[NSIndexPath indexPathForRow:i inSection:0] animated:NO];
        if ([[self getMsgItemAt:i] m_checked] > 0)
            [m_tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:i inSection:0] animated:NO scrollPosition:UITableViewScrollPositionBottom];
    }
}

//- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
//{
//    NSLog(@"touchBegan");
//}
//
//- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
//{
//     NSLog(@"touchMoved");
//}
//
//- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
//{
//     NSLog(@"touchEnded");
//}
//
//- (void) touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
//{
//     NSLog(@"touchCancelled");
//}

- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    
//    return [m_alarmMsgTable getAlarmMsgCount:m_showType];
    return m_dataArray.count;
//    return 20000;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self tableView:tableView cellForRowAtIndexPath:indexPath].frame.size.height;
}

- (void) showOperatorBar
{
//    CGPoint center = m_msgDisplayView.center;
//    center.y -= 30;
//    m_msgDisplayView.center = center;
    m_typeView.alpha = 0;
    m_ActionBarView.alpha = 1;
}

- (void) hideOperatorBar
{
//    if (m_selectedCount > 0)
//        return;
    m_typeView.alpha = 1;
    m_ActionBarView.alpha = 0;
//    CGPoint center = m_msgDisplayView.center;
//    center.y += 30;
//    m_msgDisplayView.center = center;
}

- (void) setCell:(UITableViewCell*)cell StyleAtIndex:(int)index
{
    if ([[self getMsgItemAt:index] m_status] == 0) {
        [(UILabel *)[cell viewWithTag:1] setTextColor:[UIColor redColor]];
        [(UILabel *)[cell viewWithTag:2] setTextColor:[UIColor redColor]];
        [(UILabel *)[cell viewWithTag:3] setTextColor:[UIColor redColor]];
    }
    else if ([[self getMsgItemAt:index] m_mark] > 0) {
        [(UILabel *)[cell viewWithTag:1] setTextColor:[UIColor blueColor]];
        [(UILabel *)[cell viewWithTag:2] setTextColor:[UIColor blueColor]];
        [(UILabel *)[cell viewWithTag:3] setTextColor:[UIColor blueColor]];
    }
    else {
        [(UILabel *)[cell viewWithTag:1] setTextColor:[UIColor blackColor]];
        [(UILabel *)[cell viewWithTag:2] setTextColor:[UIColor blackColor]];
        [(UILabel *)[cell viewWithTag:3] setTextColor:[UIColor blackColor]];
    }

    if ([[self getMsgItemAt:index] m_checked] > 0) {
        [(UILabel *)[cell viewWithTag:1] setBackgroundColor:[UIColor yellowColor]];
        [(UILabel *)[cell viewWithTag:2] setBackgroundColor:[UIColor yellowColor]];
        [(UILabel *)[cell viewWithTag:3] setBackgroundColor:[UIColor yellowColor]];
    }
    else {
        [(UILabel *)[cell viewWithTag:1] setBackgroundColor:[UIColor clearColor]];
        [(UILabel *)[cell viewWithTag:2] setBackgroundColor:[UIColor clearColor]];
        [(UILabel *)[cell viewWithTag:3] setBackgroundColor:[UIColor clearColor]];
    }
}

- (NSIndexPath *) tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"select : %d",indexPath.row);
    int checked = [[self getMsgItemAt:indexPath.row] m_checked];
    
    [[self getMsgItemAt:indexPath.row] setM_checked: checked > 0 ? 0 : 1];
    [m_alarmMsgTable setMsg:[[self getMsgItemAt:indexPath.row] m_msgId] Checked:checked > 0 ? 0 : 1];
    [self setCell:[tableView cellForRowAtIndexPath:indexPath] StyleAtIndex:indexPath.row];
    m_selectedCount += checked > 0 ? -1 : 1;
    
//    if (checked != 0) {
//        [m_tableView deselectRowAtIndexPath:indexPath animated:NO];
//        [[m_tableView cellForRowAtIndexPath:indexPath] setBackgroundColor:[UIColor clearColor]];
//    }
//    else
//        [[m_tableView cellForRowAtIndexPath:indexPath] setBackgroundColor:[UIColor yellowColor]];
    
    if (m_selectedCount == 0)
        [self hideOperatorBar];
    else if (1 == m_selectedCount && checked == 0)
        [self showOperatorBar];
    
    if (m_selectedCount == m_totalCount)
        m_switchAll.on = YES;
    else
        m_switchAll.on = NO;
    
//    return checked == 0 ? indexPath : 0;
    return 0;
}

- (NSIndexPath *) tableView:(UITableView *)tableView willDeselectRowAtIndexPath:(NSIndexPath *)indexPath
{
    return indexPath;
}

- (void) changeDisplayType:(AlarmMsgType)type
{
    if (type == m_showType)
        return;
    m_showType = type;
    [m_allBtn setEnabled:(ALL != type)];
    [m_readBtn setEnabled:(READ != type)];
    [m_unreadBtn setEnabled:(UNREAD != type)];
    int count = [m_alarmMsgTable getAlarmMsgCount:UNREAD];
    if (UNREAD != type && count != 0) {
        NSString * title = [[NSString alloc] initWithFormat:@"未读(%d)",count];
        [m_unreadBtn setTitle:title forState:UIControlStateNormal];
        [title release];
    }
    else
        [m_unreadBtn setTitle:@"未读" forState:UIControlStateNormal];
    [m_markedBtn setEnabled:(MARKED != type)];

    if (m_selectedCount != 0)
        [m_alarmMsgTable setMsg:nil Checked:NO];
    
    [self updateTableViewData];
    if (UNREAD == m_showType)
        m_tryAfter = (m_dataArray.count == m_maxShowCountOnce);
    else
        m_tryBefore = (m_dataArray.count == m_maxShowCountOnce);
    NSLog(@"before : %d, after : %d", m_tryBefore, m_tryAfter);
    if (m_dataArray.count > 0) {
        if (UNREAD == m_showType)
            [m_tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] atScrollPosition:UITableViewScrollPositionTop animated:NO];
        else {
            [m_tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:m_dataArray.count-1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:NO];
        }
    }
}

- (void) updateTableViewData
{
//    NSLog(@"Update Table View Data======================");
//    m_isUpdateData = YES;
    m_counter = 0;
    self.m_dataArray = [m_alarmMsgTable getAlarmMsg:m_showType :m_maxShowCountOnce];
    m_totalCount = [m_alarmMsgTable getAlarmMsgCount:m_showType];
    m_selectedCount = [m_alarmMsgTable getAlarmMsgCount:CHECKED];
//    m_getMoreFlag = 0;
//    NSLog(@"Array Count : %d, Total : %d, Selceted : %d", m_dataArray.count, m_totalCount, m_selectedCount);
//    CGRect frame = m_msgDisplayView.frame;
//    frame.origin = CGPointMake(0, 0);
//    m_tableView = [[UITableView alloc] initWithFrame:frame];
//    [m_tableView setDelegate:self];
//    [m_tableView setDataSource:self];
//    [m_msgDisplayView addSubview:m_tableView];
    [m_tableView reloadData];
//    NSLog(@"+++++++++");
}

@end

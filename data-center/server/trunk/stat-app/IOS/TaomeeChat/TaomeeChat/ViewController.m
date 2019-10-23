//
//  ViewController.m
//  TaomeeChat
//
//  Created by violet on 14-5-12.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "LoginViewController.h"
#import "LocalDatabaseHelper.h"
//#import "AlarmMsgOperatorBar.h"
#import "AlarmMsgUIControllerViewController.h"
#import "AlarmTcpClient.h"
#import "Toast.h"

#include "ViewController.h"

@interface ViewController () {
@private LocalDatabaseHelper *m_dbHelper;
    UIActivityIndicatorView * m_indicator;
}

@end

@implementation ViewController

- (BOOL) textFieldShouldReturn:(UITextField *) edit
{
    [edit resignFirstResponder];
    return YES;
}

- (void)viewDidLoad
{
    NSLog(@"MainView DidLoad");
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    CGRect frame = self.view.frame;
    NSLog(@"view frame (%f,%f,%f,%f)",frame.origin.x,frame.origin.y,frame.size.width,frame.size.height);
    UIImageView *imageV = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"first"]];
    [self.view addSubview:imageV];
    imageV.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
//    imageV.center = self.view.center;
    [imageV release];
    [self waitForToken];
}

- (void) waitForToken
{
    NSLog(@"wait For Token");
    NSString* token = [SettingTable getSettingValue:KEY_BD_TOKEN];
    if (token != nil && token.length > 0) {
        [m_indicator stopAnimating];
        [m_indicator release];
        m_indicator = nil;
        
        AlarmMsgUIControllerViewController *msgUiCtrl = [[AlarmMsgUIControllerViewController alloc] init];
        [msgUiCtrl initialize];
        [self addChildViewController:msgUiCtrl];
        [self.view addSubview:msgUiCtrl.view];

        msgUiCtrl.view.frame = CGRectMake(0, 20, self.view.frame.size.width, self.view.frame.size.height-20);
        if ([UIApplication sharedApplication].applicationIconBadgeNumber != 0)
            [AlarmTcpClient pullMsg];
        NSLog(@"badge : %d", [UIApplication sharedApplication].applicationIconBadgeNumber);
        [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
        NSLog(@"badge : %d", [UIApplication sharedApplication].applicationIconBadgeNumber);
        
        NSString * loginState = [SettingTable getSettingValue:KEY_LOGIN_STATE];
        if (nil == loginState || loginState.length == 0 || loginState.intValue == 0) {
            LoginViewController *m_loginCtrl = [[LoginViewController alloc] init];
            [self addChildViewController:m_loginCtrl];
            [self.view addSubview:m_loginCtrl.view];
            m_loginCtrl.view.frame = CGRectMake(0, 20, self.view.frame.size.width, self.view.frame.size.height-20);
        }
        return;
    }
    if (nil == m_indicator) {
        m_indicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        [self.view addSubview:m_indicator];
        [m_indicator startAnimating];
        [m_indicator setHidesWhenStopped:YES];
        CGPoint center = self.view.center;
        center.y -= 20;
        m_indicator.center = center;
    }
    [Toast ShowToastWithMessge:@"Waiting For Token!" Least:0.5 OnView:self.view BackGroundColor:[UIColor clearColor]];
    [NSTimer scheduledTimerWithTimeInterval:0.6 target:self selector:@selector(waitForToken) userInfo:Nil repeats:NO];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end

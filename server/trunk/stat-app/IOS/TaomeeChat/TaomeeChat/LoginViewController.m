//
//  LoginViewController.m
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "LoginViewController.h"

#import "AlarmTcpClient.h"
#import "AlarmMsgUIControllerViewController.h"
#import "SettingTable.h"

@interface LoginViewController ()
{
    UIActivityIndicatorView * loading;
    
    IBOutlet UITextField * m_nameEdit;
    IBOutlet UITextField * m_pwdEdit;
    IBOutlet UITextField * m_mobileEdit;
    
    IBOutlet UIButton * m_loginBtn;
}

@end

@implementation LoginViewController

@synthesize m_name, m_mobile, m_pwd;

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
//    [m_loginBtn setEnabled:NO];
    
    m_name = @"";
    m_nameEdit.text = m_name;
    
    m_pwd = @"";
    
    m_mobile = @"";
    m_mobileEdit.text = m_mobile;
}

- (void) didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void) dealloc
{
    if (nil != m_name)
        [m_name release];
    if (nil != m_mobile)
        [m_mobile release];
    if (nil != loading)
        [loading release];
    
    [super dealloc];
}

- (BOOL) textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

- (BOOL) textFieldShouldEndEditing:(UITextField *)textField
{
//    NSLog(@"Should End Editing");
    if (textField == m_nameEdit)
    {
        self.m_name = [m_nameEdit.text stringByReplacingOccurrencesOfString:@" " withString:@""];
        [m_nameEdit setText:m_name];
    }
    else if (textField == m_pwdEdit)
    {
        self.m_pwd = [m_pwdEdit.text stringByReplacingOccurrencesOfString:@" " withString:@"" ];
        [m_pwdEdit setText:m_pwd];
    }
    else if (textField == m_mobileEdit)
    {
        self.m_mobile = [m_mobileEdit.text stringByReplacingOccurrencesOfString:@" " withString:	@""];
        [m_mobileEdit setText:m_mobile];
    }
    BOOL flag = YES;
    if (m_name == nil || m_name.length == 0 || m_pwd == nil || m_pwd.length == 0 || m_mobile == nil || m_mobile.length == 0)
        flag = NO;
//    BOOL flag = m_name.length != 0 && m_pwd.length != 0 && m_mobile.length != 0;
    [m_loginBtn setEnabled:flag];
    return YES;
}

- (IBAction) onLoginBtnClick:sender {
    NSLog(@"Log In Click");
    NSLog(@"name : %@, pwd : %@, mobile : %@", m_name, m_pwd, m_mobile);
    
    NSString *msg = [[NSString alloc] initWithFormat:@"用户名：%@\n手机号码：%@", m_nameEdit.text, m_mobileEdit.text];
    
    UIAlertView *alart = [[UIAlertView alloc] initWithTitle:@"请确认您的登录信息" message:msg delegate:self  cancelButtonTitle:@"取消" otherButtonTitles:@"确认", nil];
    [alart show];
    [alart release];
    [msg release];
}

- (void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSLog(@"alert view button : %d", buttonIndex);
    
    if (1 == buttonIndex) {
        if (nil == loading)
            loading =  [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];

        [self.view addSubview:loading];
        [loading startAnimating];
        [loading setHidesWhenStopped:YES];
        loading.center = self.view.center;
        [self setItemsEnable:NO];
        [NSTimer scheduledTimerWithTimeInterval:0.05 target:self selector:@selector(login) userInfo:nil repeats:NO];
    }
}

- (void) setItemsEnable:(BOOL)enable
{
    [m_nameEdit setEnabled:enable];
    [m_pwdEdit setEnabled:enable];
    [m_mobileEdit setEnabled:enable];
    [m_loginBtn setEnabled:enable];
}

- (void) login
{
//    NSLog(@"Login as %@ use pwd '%@' with mobile %@", m_name, m_pwd, m_mobile);

    BOOL ret = [AlarmTcpClient login:m_name :m_pwd :m_mobile : nil];
    [loading stopAnimating];
    [self setItemsEnable:YES];
    
    NSLog(@"Login result : %d", ret);
    if (ret) {
        [SettingTable setKey:KEY_NAME toValue:m_name];
        [SettingTable setKey:KEY_MOBILE toValue:m_mobile];
        [SettingTable setKey:KEY_LOGIN_STATE toValue:@"1"];
        [self.view removeFromSuperview];
        [self removeFromParentViewController];
    }
    else {
        UIAlertView *alart = [[UIAlertView alloc] initWithTitle:@"登录失败" message:@"密码错误，请重新登录..." delegate:self cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
        m_pwdEdit.text = m_pwd = @"";
        [alart show];
        [alart release];
    }
}

- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    if ([[self parentViewController] isKindOfClass:[AlarmMsgUIControllerViewController class]] && m_nameEdit.enabled) {
        [self.view removeFromSuperview];
        [self removeFromParentViewController];
    }
}

@end

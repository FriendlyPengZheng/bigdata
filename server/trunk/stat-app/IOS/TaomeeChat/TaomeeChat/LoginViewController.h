//
//  LoginViewController.h
//  TaomeeChat
//
//  Created by violet on 14-5-13.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LoginViewController : UIViewController<UITextFieldDelegate, UIAlertViewDelegate> {
    
//    IBOutlet UITextField * m_nameEdit;
//    IBOutlet UITextField * m_pwdEdit;
//    IBOutlet UITextField * m_mobileEdit;
//    
//    NSString * m_name;
//    NSString * m_pwd;
//    NSString * m_mobile;
//    
//    IBOutlet UIButton * m_loginBtn;
}

@property (nonatomic, retain) NSString * m_name;
@property (nonatomic, retain) NSString * m_pwd;
@property (nonatomic, retain) NSString * m_mobile;

//- (void) initialize:(ViewController *)mainCtrl;

@end

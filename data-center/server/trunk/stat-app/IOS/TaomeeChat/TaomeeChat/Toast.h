//
//  Toast.h
//  ichat
//
//  Created by violet on 14-5-21.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Toast : NSObject

//+ (void) ShowToastWithMessge:(NSString*)msg Least:(float)time OnView:(UIView*)view;
+ (void) ShowToastWithMessge:(NSString*)msg Least:(float)time OnView:(UIView*)view BackGroundColor:(UIColor*)color;

@end

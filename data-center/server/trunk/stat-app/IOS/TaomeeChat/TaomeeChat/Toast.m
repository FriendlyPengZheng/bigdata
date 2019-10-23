//
//  Toast.m
//  ichat
//
//  Created by violet on 14-5-21.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "Toast.h"

@implementation Toast

//+ (void) ShowToastWithMessge:(NSString *)msg Least:(float)time OnView:(UIView *)view
//{
//    if (nil == msg || msg.length == 0)
//        return;
//    
//    if (time <= 0) time = 1;
//    
//    UILabel * label = [[UILabel alloc] initWithFrame:CGRectZero];
//    label.tag = 0xFE;
//    label.lineBreakMode = NSLineBreakByWordWrapping;
//    label.numberOfLines = 0;
//    label.backgroundColor = [UIColor whiteColor];
//    label.textColor = [UIColor blueColor];
//    label.text = msg;
//
//    label.frame = view.frame;
//    [label sizeToFit];
//    [view addSubview:label];
//    label.center = view.center;
//    [NSTimer scheduledTimerWithTimeInterval:time target:label selector:@selector(removeFromSuperview) userInfo:nil repeats:NO];
//}

+ (void) ShowToastWithMessge:(NSString*)msg Least:(float)time OnView:(UIView*)view BackGroundColor:(UIColor*)color;
{
    if (nil == msg || msg.length == 0)
        return;
    
    if (time <= 0) time = 1;
    
    UILabel * label = [[UILabel alloc] initWithFrame:CGRectZero];
    label.tag = 0xFE;
    label.lineBreakMode = NSLineBreakByWordWrapping;
    label.numberOfLines = 0;
    label.backgroundColor = color;
    label.textColor = [UIColor blueColor];
    label.text = msg;
    
    label.frame = view.frame;
    [label sizeToFit];
    [view addSubview:label];
    label.center = view.center;
    [NSTimer scheduledTimerWithTimeInterval:time target:label selector:@selector(removeFromSuperview) userInfo:nil repeats:NO];
}

@end

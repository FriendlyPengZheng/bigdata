//
//  AppDelegate.m
//  TaomeeChat
//
//  Created by violet on 14-5-12.
//  Copyright (c) 2014年 lance. All rights reserved.
//

#import "AppDelegate.h"
#import "LocalDatabaseHelper.h"
#import "SettingTable.h"
#import "AlarmTcpClient.h"

#import "BPush.h"

@implementation AppDelegate

static NSData * token;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // Override point for customization after application launch.
//    NSLog(@"AppDelegate did Finish Launch");
    
    [LocalDatabaseHelper openLocalDataBase];
    [BPush setupChannel:launchOptions];
    [BPush setDelegate:self];
    [application registerForRemoteNotificationTypes:UIRemoteNotificationTypeAlert
                                                    | UIRemoteNotificationTypeBadge
                                                    | UIRemoteNotificationTypeSound];
    return YES;
}

- (void) application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    NSLog(@"Got Token From APNs");
//    NSString * bd_token = [SettingTable getSettingValue:KEY_BD_TOKEN];
//    if (bd_token != nil && bd_token.length > 0)
//        return;
    
    token = deviceToken;
    [BPush registerDeviceToken:deviceToken];
    [BPush bindChannel];
}

- (void) onMethod:(NSString*)method response:(NSDictionary*)data {
//    NSLog(@"On method:%@", method);
    NSLog(@"data:%@", [data description]);
    NSDictionary* res = [[NSDictionary alloc] initWithDictionary:data];
    if ([BPushRequestMethod_Bind isEqualToString:method]) {
//        NSString *appid = [res valueForKey:BPushRequestAppIdKey];
        NSString *userid = [res valueForKey:BPushRequestUserIdKey];
//        NSString *channelid = [res valueForKey:BPushRequestChannelIdKey];
//        NSString *requestid = [res valueForKey:BPushRequestRequestIdKey];
        
        int returnCode = [[res valueForKey:BPushRequestErrorCodeKey] intValue];
        
        if (returnCode == BPushErrorCode_Success) {
            [SettingTable setKey:KEY_BD_TOKEN toValue:userid];
//            NSArray *tagArr = [[NSArray alloc] initWithObjects:@"all@ios", nil];
//            [BPush setTags:tagArr];
        }
        else {
            [BPush registerDeviceToken:token];
            [BPush bindChannel];
        }
    } else if ([BPushRequestMethod_Unbind isEqualToString:method]) {
        int returnCode = [[res valueForKey:BPushRequestErrorCodeKey] intValue];
        if (returnCode == BPushErrorCode_Success) {
        }
    }
    [res release];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    NSLog(@"Receive Remote Notification!");
    [AlarmTcpClient pullMsg];
}
- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    NSLog(@"Will ResignActive");
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    NSLog(@"Enter Background");
//    [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    NSLog(@"Enter Foreground");
    if ([UIApplication sharedApplication].applicationIconBadgeNumber != 0)
        [AlarmTcpClient pullMsg];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    NSLog(@"Become Active");
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end

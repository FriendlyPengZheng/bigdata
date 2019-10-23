/**
 * 
 */
package com.taomee.tms.mgr.tools;

/**
 * @author sevin
 *
 */
public class MessageCode {
	 // 请求成功
    public static int SUCCESS = 0 ;
    // 警告
    public static int WARN = 2001;

    // token
    public static int TOKEN_ERROR = 4001;//token 不合法
    public static int TOKEN_OVERDUE = 4002;//token 已过期

    // 登录失败
    public static int LOGIN_FAILED = 4003;

    // 没有权限
    public static int PERMISSION_DENIED = 4004;

    // 服务器处理失败(常用错误)
    public static int ERROR = 5000 ;

}

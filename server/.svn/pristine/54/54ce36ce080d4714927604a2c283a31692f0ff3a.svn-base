/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  Lance<lance@taomee.com>
 *   @date    2014-04-14
 * =====================================================================================
 */

#ifndef STAT_MSG_PUSHER_HPP
#define STAT_MSG_PUSHER_HPP

#include <string>
using std::string;

/**
 * @brief 云推送服务接口类，定义的基本的消息发送和设置接口
 */
class StatMsgPusher
{
public:
    /**
     * @brief 推送消息参数
     */
    typedef struct PushMsgRequest
    {
        string msg_key;         //重复的msg_key会被覆盖
        string msg_title;       //标题
        string msg_content;     //内容
        string device_type;     //3 android, 4 IOS
    } PushMsgRequest;

public:
    StatMsgPusher()
    {
    }

    virtual ~StatMsgPusher()
    {
    }
    
    /**
     * @brief 单人消息推送
     *
     * @param user_id [IN] 用户标示，用于区分不同的移动终端
     * @param request [IN] 推送消息参数
     *
     * @return 成功返回0，失败返回-1
     */
    virtual int push_msg(const string& user_id, const PushMsgRequest& request) = 0;

    /**
     * @brief 广播消息推送
     *
     * @param request [IN] 推送消息参数
     *
     * @return 成功返回0，失败返回-1
     */
    virtual int push_broadcast_msg(const PushMsgRequest& request) = 0;

    /**
     * @brief 分组消息推送
     *
     * @param tag [IN] 组标签
     * @param request [IN] 推送消息参数
     *
     * @return 成功返回0，失败返回-1
     */
    virtual int push_tag_msg(const string& tag, const PushMsgRequest& request) = 0;

    /**
     * @brief 添加分组标签，标签不存在时创建，存在时返回添加成功
     *
     * @param tag [IN] 分组标签标示，标签长度不能大于128Byte
     *
     * @return 成功返回0，失败返回-1
     */
    virtual int tag_add(const string& tag) = 0;

    /**
     * @brief 用户分组设置
     *
     * @param tag [IN] 组标签
     * @param user_id [IN] 用户标示
     *
     * @return 成功返回0，失败返回-1
     * @attention 当分组不存在时会创建
     */
    virtual int tag_bind_user(const string& tag, const string& user_id) = 0;

    /**
     * @brief 删除用户分组
     *
     * @param tag [IN] 待删除的组标签
     *
     * @return 成功返回0，失败返回-1
     */
    virtual int tag_del(const string& tag) = 0;

    /**
     * @brief 解除用户与分组的绑定
     *
     * @param tag [IN] 分组标示
     * @param user_id [IN] 用户标示
     *
     * @return 成功返回0，失败返回-1
     */
    virtual int tag_unbind_user(const string& tag, const string& user_id) = 0;
};
#endif

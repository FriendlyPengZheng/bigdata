/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  Lance<lance@taomee.com>
 *   @date    2014-04-01
 * =====================================================================================
 */

#ifndef STAT_MAIL_SENDER_HPP
#define STAT_MAIL_SENDER_HPP

#include <string>
#include <vector>

using std::string;
using std::vector;

/**
 * 基于SMTP协议的简单邮件发送
 */
class StatMailSender
{
public:
	typedef struct SmtpHeader{
		string h_username;
		string h_password;

		string h_sender;
		string h_host;
		uint16_t h_port;
		time_t h_timeout;
		SmtpHeader() : h_username(""), h_password(""), h_sender(""), h_host(""), h_port(25), h_timeout(300000) {}
	} SmtpHeader;

public:
	StatMailSender() : m_username(""), m_password(""), m_sender(""), m_host(""), m_port(25), m_timeout(300000)
	{
	}
	StatMailSender(const string& sender, const string& host, uint16_t port=25, time_t timeout=300000) 
		: m_sender(sender), m_host(host), m_port(port), m_timeout(timeout)
	{
	}

	~StatMailSender()
	{
	}

public:
	int init(const SmtpHeader& header);
    
    /**
     * @brief 使用SMTP协议发送邮件
     * @param send_to 收件人列表
     * @param send_cc 抄送人列表
     * @param send_bcc 密抄列表
     * @param subject 主题
     * @param content 内容
     * @return 0 超时 <0 出错 >0 成功
     */
	int send_mail(const vector<string>& send_to, const vector<string>& send_cc, const vector<string>& send_bcc, const string& subject, const string& content);

    /**
     * @brief 使用SMTP协议发送邮件
     * @param send_to 收件人列表
     * @param subject 主题
     * @param content 内容
     * @return 0 超时 <0 出错 >0 成功
     */
    int send_mail(const vector<string>& send_to, const string& subject, const string& content);

    /**
     * @brief 使用SMTP协议发送邮件
     * @param send_to 收件人列表
     * @param subject 主题
     * @param content 内容
     * @param send_cc 抄送人列表
     * @return 0 超时 <0 出错 >0 成功
     */
    int send_mail(const vector<string>& send_to, const vector<string>& send_cc, const string& subject, const string& content);

private:
	int send_and_check(int fd, char *buffer, unsigned int size, const char* check_str, const unsigned int check_len);
	int connect_host();

private:
	string m_username;
	string m_password;

	string m_sender;
	string m_host;
	uint16_t m_port;
	time_t m_timeout;
};

inline int StatMailSender::send_mail(const vector<string>& send_to, const string& subject, const string& content)
{
    return send_mail(send_to, vector<string>(), vector<string>(), subject, content);
}

inline int StatMailSender::send_mail(const vector<string>& send_to, const vector<string>& send_cc, const string& subject, const string& content)
{
    return send_mail(send_to, send_cc, vector<string>(), subject, content);
}

#endif

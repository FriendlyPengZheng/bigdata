/*
 * =====================================================================================
 *
 *       Filename:  url_code.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  2010年09月25日 16时23分52秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  henry (韩林), henry@taomee.com
 *        Company:  TaoMee.Inc, ShangHai
 *
 * =====================================================================================
 */
#include <stdio.h>
#include <string.h>
#include <iconv.h>
#include <errno.h>
extern "C"
{
    #include <libtaomee/log.h>
}
#include "url_code.h"

#define NON_NUM '0'

char char_to_num(char ch)
{
	if(ch >= '0' && ch <= '9')
	{
			return (char)(ch - '0');
	}
	else if(ch >= 'a' && ch <= 'f')
	{
			return (char)(ch - 'a' + 10);
	}
	else if(ch >= 'A' && ch <= 'F')
	{
			return (char)(ch - 'A' + 10);
	}
	return NON_NUM;
}

/** 
 * @brief 把字符串进行 URL编码
 * 
 * @param str	要编码的字符串
 * @param str_size字符串的长度
 * @param result结果缓冲区地址
 * @param result_size结果缓冲取大小（如果str所有字符都编码，该值为str_size*3）
 * 
 * @return >0 result中实际有效字符的长度， 0编码失败 
 */
int url_encode(const char *str, const int str_size, char *result, int result_size)
{
	if(str == NULL || result == NULL || str_size <=0 || result_size <= 0)
	{
			return 0;
	}

	int i = 0;
	int j = 0;//result index
	char ch;

	for(i = 0; (i < str_size) && (j < result_size); i++)
	{
			ch = str[i];
			if(ch >= 'A' && ch <= 'Z')
			{
					result[j++] = ch;
			}
			else if(ch >= 'a' && ch <= 'z')
			{
					result[j++] = ch;
			}
			else if(ch >= '0' && ch <= '9')
			{
					result[j++] = ch;
			}
			else if(ch == ' ')
			{
					result[j++] = '+';
			}
            else if(ch == '_')
            {
                result[j++] = ch;
            }
            else if(ch == '.')
            {
                result[j++] = ch;
            }
			else
			{
					if(j + 3 <= result_size)
					{
							sprintf(result + j, "%%%02X", (unsigned char)ch);
							j += 3;
					}
					else
					{
							return 0;
					}
			}


	}
	
	//result[j] ='\0';
	return j;
}


/** 
 * @brief 将字符串进行解码
 * 
 * @param str	要解码的字符串
 * @param str_size字符串的长度
 * @param result 结果缓冲区地址
 * @param result_size 结果缓冲区大小
 * 
 * @return >0 结果缓冲区中实际有效的长度 0解码失败
 */
int url_decode(const char *str, const int str_size, char *result, const int result_size)
{
		if(str == NULL || result == NULL || str_size <= 0 || result_size <= 0)
		{
				return 0;
		}

		char ch, ch1, ch2;
		int i;
		int j = 0;//result index
		
		for(i = 0; (i < str_size) && (j < result_size); i++)
		{
			ch = str[i];
			switch(ch)
			{
					case '+':
							result[j++] = ' ';
							break;
					case '%':
							if(i + 2 < str_size)
							{
									ch1 = char_to_num(str[i+1]);
									ch2 = char_to_num(str[i+2]);
									if(ch1 != NON_NUM && ch2 != NON_NUM)
									{
											result[j++] = (char)((ch1<<4) | ch2);
											i += 2;
											break;
									}
							}
					default:
							result[j++] = ch;
							break;

			}
		}

		//result[j] = '\0';
		return j;
}

int code_convert(char *from_charset, char *to_charset, char *inbuf, size_t inlen, char *outbuf, size_t outlen)
{
		if(inbuf == NULL || inlen == 0 || outbuf == NULL || outlen == 0)
		{
				//ERROR_LOG("inlen:%d,outlen:%d",  inlen, outlen);
				return -1;
		}
		iconv_t cd;
		char **pin = &inbuf;
		char **pout = &outbuf;
		memset(outbuf, 0, outlen);
		cd = iconv_open(to_charset, from_charset);
		if(cd <= 0)
		{   
            //ERROR_LOG("iconv open error: %s", strerror(errno));
			return -1;
		}

		if(iconv(cd, pin, &inlen, pout, &outlen) == (size_t)-1)
		{
			iconv_close(cd);
           // ERROR_LOG("iconv error: %s", strerror(errno));
			return -1;
		}

		iconv_close(cd);
		return 0;
}

//int main()
//{
// char url_baidu[1024] = "http%3A%2F%2Fwww.google.com.tw%2Fsearch%3Fsourceid%3Dnavclient%26aq%3D0h%26oq%3D%26hl%3Dzh-TW%26ie%3DUTF-8%26rlz%3D1T4ADSA_zh-TWTW332TW389%26q%3D%25e8%25b3%25bd%25e7%2588%25be%25e8%2599%259f";
////		char url_baidu[1024] = "%B9%A6%B7%F2%C5%C9%D4%C2%CD%C3%D7%D3%C1%B7%CA%B2%C3%B4%BC%BC%C4%DC";
////		char url_google[1024] = "%E8%8A%B1%E4%BB%99%E5%AD%90+%E6%91%A9%E5%B0%94";
//		char result[1024] = {0};
//		char baidu_result[1024] = {0};
//		if(url_decode(url_baidu, strlen(url_baidu), result, sizeof(result)) > 0)
//		{
//				printf("result=%s\n", result);
//				if(code_convert("gb2312", "utf8", result, strlen(result),  baidu_result, sizeof(baidu_result)) != 0)
//				{
//						printf("convert failed\n");
//				}
//				else
//				{
//					printf("url_decode:%s\n", baidu_result);
//				}
//		}
//
//		return 0;
//}

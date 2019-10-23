/*
 * =====================================================================================
 *
 *       Filename:  url_code.h
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  2010年09月25日 16时20分15秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  henry (韩林), henry@taomee.com
 *        Company:  TaoMee.Inc, ShangHai
 *
 * =====================================================================================
 */
#ifndef H_URL_CODE_2010_09_25_H
#define H_URL_CODE_2010_09_25_h

char char_to_num(char ch);
int url_encode(const char *str, const int str_size, char *result, const int result_size);
int url_decode(const char *str, const int str_size, char *result, const int result_size);
int code_convert(char *from_charset, char *to_charset, char *inbuf, size_t inlen, char *outbuf, size_t outlen);

#endif //H_URL_CODE_2010_09_25_h 

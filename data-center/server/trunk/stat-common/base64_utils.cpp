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

#include <stdlib.h>
#include <string.h>
#include "base64_utils.hpp"

namespace StatCommon {

bool is_base64_encode(const string& str_check)
{
    int check_len = str_check.size();
    if (check_len%4 != 0)
        return false;

    for (int i=0; i<3; ++i)
    {
        if (str_check[check_len - 1] == '=')
            check_len -= 1;
        else
            break;
    }

    const unsigned char dekey[] = {
        // '+' '/' '0'-'9'
        '+' ,0, 0, 0, '/', '0','1','2','3','4','5','6','7','8','9',0, 0, 0, 0, 0, 0, 0,
        // 'A'-'Z'
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
        0, 0, 0, 0, 0, 0,
        // 'a'-'z'
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
    };
    
    for(int i=0; i<check_len; ++i)
    {
        if (str_check[i] < '+' || str_check[i] > 'z')
            return false;

        if (dekey[(unsigned char)str_check[i] - '+'] == 0) 
            return false;
    }
    return true;
}

int encode_base64(const string& str_in, string& str_out)
{
    int in_len = str_in.length();

    int i = 0;
    const char *enkey = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    char *p_encoded = (char *)malloc(in_len*4/3 + 4);
    memset(p_encoded, 0, in_len*4/3 + 4);
    int nLoop = (in_len%3 == 0) ? in_len : in_len - 3;
    int n = 0;
    const unsigned char *buf_in = (const unsigned char *)str_in.c_str();
    for(i=0; i < nLoop; i+=3 )
    {
        p_encoded[n] = enkey[buf_in[i] >> 2];
        p_encoded[n+1] = enkey[((buf_in[i] & 0x03) <<4) | ((buf_in[i+1] & 0xF0) >> 4)];
        p_encoded[n+2] = enkey[((buf_in[i+1] & 0x0F) << 2) | ((buf_in[i+2] & 0xC0) >> 6)];
        p_encoded[n+3] = enkey[buf_in[i+2] & 0x3F];
        n += 4;
    }

    switch(in_len%3)
    {
    case 0:
        p_encoded[n] = '\0';
        break;

    case 1:
        p_encoded[n] = enkey[buf_in[i]>>2];
        p_encoded[n+1] = enkey[((buf_in[i]&3)<<4) | ((0&0xf0)>>4)];
        p_encoded[n+2] = '=';
        p_encoded[n+3] = '=';
        p_encoded[n+4] = '\0';
        n+=4;
        break;

    case 2:
        p_encoded[n] = enkey[buf_in[i]>>2];
        p_encoded[n+1] = enkey[((buf_in[i]&3)<<4) | ((buf_in[i+1]&0xf0)>>4)];
        p_encoded[n+2] = enkey[(( buf_in[i+1]&0xf)<<2 ) | ((0&0xc0)>>6)];
        p_encoded[n+3] = '=';
        p_encoded[n+4] = '\0';
        n+=4;
        break;
    }

    str_out.clear();
    str_out = string(p_encoded, n);
    free(p_encoded);
    return n;
}

int decode_base64(const string& str_in, string& str_out)
{
    int in_len = str_in.length();
    if (!is_base64_encode(str_in))
        return -1;
    const unsigned char* p_in = (const unsigned char*)str_in.c_str();

    const int dekey[] = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        62, // '+'
        -1, -1, -1,
        63, // '/'
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // '0'-'9'
        -1, -1, -1, -1, -1, -1, -1,
        0 , 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10, 11, 12,
        13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // 'A'-'Z'
        -1, -1, -1, -1, -1, -1,
        26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38,
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // 'a'-'z'
    };

    char* p_decoded = (char*)malloc(in_len*3/4+1);
    memset(p_decoded, 0, in_len*3/4+1);
    int nLoop = (p_in[in_len-1] == '=') ? (in_len - 4) : in_len;
    int i = 0, n = 0;
    for(i = 0; i < nLoop; i += 4 )
    {
        p_decoded[n] = (dekey[p_in[i]] << 2) | ((dekey[p_in[i+1]] & 0x30) >> 4);
        p_decoded[n+1] = ((dekey[p_in[i+1]] & 0xF) << 4) | ((dekey[p_in[i+2]] & 0x3C) >> 2);
        p_decoded[n+2] =  ((dekey[p_in[i+2]] & 0x3) << 6) | dekey[p_in[i+3]];

        n+=3;
    }

    if( p_in[in_len-1] == '=' && p_in[in_len-2] == '=' )
    {
        p_decoded[n] = (dekey[p_in[i]] << 2) | ((dekey[p_in[i+1]] & 0x30) >> 4);
        p_decoded[n+1] = '\0';
        n+=1;
    }

    if( p_in[in_len-1] == '=' && p_in[in_len-2] != '=' )
    {
        p_decoded[n] = (dekey[p_in[i]] << 2) | ((dekey[p_in[i+1]] & 0x30) >> 4);
        p_decoded[n+1] = ((dekey[p_in[i+1]] & 0xf) << 4) | ((dekey[p_in[i+2]] & 0x3c) >> 2);
        p_decoded[n+2] = '\0';
        n+=2;
    }

    if( p_in[in_len-1] != '=' && p_in[in_len-2] != '=' )
        p_decoded[n] = '\0';

    str_out.clear();
    str_out = string(p_decoded, n);
    free(p_decoded);
    return n;
}

}//namespace StatCommon end

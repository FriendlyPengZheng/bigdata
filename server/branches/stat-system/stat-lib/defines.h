/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 *@file defines.h
 *@author richard <richard@taomee.com>
 *@date 2010-11-01
 */

#ifndef DEFINES_H_2010_11_01
#define DEFINES_H_2010_11_01

#define OFFSET(s, m)		(int)&(((s*)NULL)->m)            /**< 结构s的成员m在结构存储中的偏移值 */
#define SIZEOF(s, m)		sizeof(((s*)NULL)->m)            /**< 结构s的成员m占用存储空间的大小 */

#endif //DEFINES_H_2010_11_01


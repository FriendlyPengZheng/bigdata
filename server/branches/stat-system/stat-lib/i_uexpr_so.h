/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file i_uexpr.h
 * @author richard <richard@taomee.com>
 * @date 2010-10-26
 */

#ifndef I_UEXPR_SO_H_2010_10_26
#define I_UEXPR_SO_H_2010_10_26

#include "i_config.h"

/**
 * @brief 表达式so接口
 */
extern "C" {

/**
 * @brief 表达式so初始化函数
 * @param p_config 配置接口的实例
 * @param p_so_param SO的参数
 * @param pp_user_data 接收SO自定义数据的指针
 * @return 成功返回0，失败返回-1 
 */
int uexpr_so_init(i_config *p_config, const char *p_so_param, void **pp_user_data);

/**
 * @brief 遍历唯一值的回调函数
 * @param key 唯一值的key
 * @param key_size 唯一值的key的大小
 * @param value 唯一值的value
 * @param value_size 唯一值的value的大小
 * @param 用户自定义的数据
 * @return 成功返回0，失败返回-1
 */
int traverse(const void *key, uint32_t key_size, const void *value, uint32_t value_size, 
			 void *p_user_data);

typedef struct uexpr_so_result {
	uint32_t result_len;
	uint32_t result_data[0];
} __attribute__((__packed__)) uexpr_so_result_t;

/**
 * @brief 获取SO运算的结果
 * @param p_user_data 用户自定义的数据
 * @return 成功返回0，失败返回-1 
 */
uexpr_so_result_t * get_result(void *p_user_data);

/**
 * @brief 反初始化
 * @param p_user_data 用户自定义数据
 * @return 成功返回0，失败返回-1
 */
int uexpr_so_uninit(void *p_user_data);

typedef int (*p_uexpr_so_init_t)(i_config *p_config, const char *p_so_param, void **pp_user_data);
typedef int (*p_traverse_t)(const void *key, uint32_t key_size, const void *value, uint32_t value_size, 
			                   void *p_user_data);
typedef uexpr_so_result_t * (*p_get_result_t)(void *p_user_data);
typedef int (*p_uexpr_so_uninit_t)(void *p_user_data);

}

#endif /* I_UEXPR_SO_H_2010_10_26 */

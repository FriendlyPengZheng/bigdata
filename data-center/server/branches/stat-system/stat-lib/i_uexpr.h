/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file i_uexpr.h
 * @author richard <richard@taomee.com>
 * @date 2010-10-26
 */

#ifndef I_UEXPR_H_2010_10_26
#define I_UEXPR_H_2010_10_26

#include "i_config.h"
#include "i_uexpr_so.h"

/**
 * @brief i_uexpr接口类
 */
struct i_uexpr
{
public:
	enum {
		TOKEN_LEN_MAX = 4096
	};
	
	/**
	 * @brief 初始化
	 * @return 成功返回0，失败返回－1
	 */
	virtual int init(i_config *p_config, uint32_t work_id) = 0;

	/**
	 * @brief 表达式求值
	 * @return 成功返回结果，失败返回NULL
	 */
	virtual uexpr_so_result_t * eval(const char *p_uexpr) = 0;

	/**
	 * @brief 表达式求值，以可读形式返回
	 * @return 成功返回结果，失败返回NULL
	 */
	virtual uexpr_so_result_t * eval_list(const char *p_uexpr, char *p_ret_buf) = 0;

	/**
	 * @brief 反初始化
	 * @return 成功返回0，失败返回－1
	 */
	virtual int uninit() = 0;

	/**
	 * @brief 释放自己
	 * @return 成功返回0，失败返回－1
	 */
	virtual int release() = 0;
};

/**
 * @brief 创建i_uexpr接口的实例
 */
int create_uexpr_instance(i_uexpr **pp_instance);

#endif /* I_UEXPR_H_2010_10_26 */


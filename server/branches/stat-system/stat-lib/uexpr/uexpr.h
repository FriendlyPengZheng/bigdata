/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file uexpr.h
 * @author richard <richard@taomee.com>
 * @date 2010-10-26
 */

#ifndef UEXPR_H_2010_10_26
#define UEXPR_H_2010_10_26

#include <string>
#include <vector>

#include "i_ucount.h"
#include "i_uvalue.h"
#include "i_uexpr.h"

class c_uexpr : public i_uexpr
{
public:
	c_uexpr();
	virtual ~c_uexpr();
	
	virtual int init(i_config *p_config, uint32_t work_idx);

	virtual uexpr_so_result_t * eval(const char *p_uexpr);

    virtual uexpr_so_result_t * eval_list(const char *p_uexpr, char *p_ret_buf);

	virtual int uninit();

	virtual int release();

protected:
	/**
	 * @enum 表达式符号类别
	 */
	typedef enum token_type {
		NONE,
		DELIMITER,
		UCOUNT_REPORT,
		UCOUNT_RESULT,
		UVALUE_REPORT,
		UVALUE_RESULT,
		SO_NAME,
		SO_PARAM
	} token_type_t;
	
	/**
	 * @brief 解析赋值运算符(=)
	 */
	int eval_equ(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type);
	
	/**
	 * @brief 解析集合运算符(&、|和-)
	 */
	int eval_set(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type);
	
	/**
	 * @brief 解析括号
	 */
	int eval_par(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type);
	
	/**
	 * @brief 解析[1, 0]和{1, 0}
	 */
	int atom(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type);
	
	/**
	 * @brief 从表达式中获取下一个token
	 */
	int get_token();
	
	/**
	 * @brief 把当前token放回表达式
	 */
	int put_token();

	i_ucount * create_and_init_tmp_ucount(char *p_ucount_path_name, int len);
	
	i_ucount * create_and_init_tmp_ucount(const char *p_ucount_path_name);

	i_uvalue * create_and_init_tmp_uvalue(char *p_uvalue_path_name, int len);
	
	i_uvalue * create_and_init_tmp_uvalue(const char *p_uvalue_path_name);

	int get_ufile_path_name(int r_id, int day_count, char *p_buffer, int buffer_size, token_type_t token_type);

	int rsync_ufile(int r_id, int day_count, token_type_t token_type);

	int set_ufile_from_list(i_ucount *p_ucount, const char *ulist_path_name);

	int get_list_from_ufile(i_ucount *p_ucount, const char *ulist_path_name);

	static int uvalue_equ(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_add(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_intadd(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_sub(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_mul(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_div(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_max(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_min(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_left(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);
	static int uvalue_right(const void *p_value_data1, uint32_t value_size1,
				          const void *p_value_data2, uint32_t value_size2,
						  void *p_result_value, uint32_t *p_result_value_size);

private:
	int m_inited;
	i_config *m_p_config;                                  /**< 配置实例 */
	const char *m_p_uexpr;                                 /**< 要解析的表达式 */
	char m_token[TOKEN_LEN_MAX];                           /**< 当前token */
	token_type_t m_token_type;                             /**< 当前token_type */
	int m_last_errno;
	//int m_bwlimit;                                         /**< 带宽限制, 单位:KBPS */
    int m_rsync;                                           /**< 是否同步文件 */

	char m_last_errstr[1024];                              /**< 错误信息 */
	char m_tmp_dir[PATH_MAX];
	char m_ucount_report_data_dir[PATH_MAX];
	char m_ucount_result_data_dir[PATH_MAX];
	char m_uvalue_report_data_dir[PATH_MAX];
	char m_uvalue_result_data_dir[PATH_MAX];
	char m_bak_ucount_report_data_dir[PATH_MAX];
	char m_bak_ucount_result_data_dir[PATH_MAX];
	char m_bak_uvalue_report_data_dir[PATH_MAX];
	char m_bak_uvalue_result_data_dir[PATH_MAX];
	char m_uexpr_so_dir[PATH_MAX];
	char m_uexpr_result[((uint16_t)-1) + sizeof(uint16_t)];
	std::vector<std::string> m_vec_rsync_ucount_addr;
	std::vector<std::string> m_vec_rsync_uvalue_addr;
	std::vector<std::string> m_vec_rsync_bak_ucount_addr;
	std::vector<std::string> m_vec_rsync_bak_uvalue_addr;
    uint32_t m_work_idx;
};

#endif /* UEXPR_H_2010_10_26 */


/* vim: set tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file uexpr.cpp
 * @author richard <richard@taomee.com>
 * @date 2010-10-26
 */

#include <algorithm>
#include <cctype>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <new>

#include <dlfcn.h>
#include <errno.h>
#include <unistd.h>
#include <fcntl.h>

#include "defines.h"
#include "uexpr.h"
#include "log.h"
#include "i_uexpr_so.h"

using namespace std;

int create_uexpr_instance(i_uexpr **pp_instance)
{
    if (pp_instance == NULL) {
        return -1;
    }

    c_uexpr *p_instance = new(nothrow)c_uexpr();
    if (p_instance == NULL) {
        return -1;
    } else {
        *pp_instance = dynamic_cast<i_uexpr *>(p_instance);
        return 0;
    }
}

c_uexpr::c_uexpr() : m_inited(0), m_p_config(NULL), m_p_uexpr(NULL), m_token_type(NONE),
	                 m_last_errno(0)
{
}

c_uexpr::~c_uexpr()
{
	if (m_inited) {
		uninit();
	}
}

int c_uexpr::init(i_config *p_config, uint32_t work_idx)
{
	if (m_inited) {
		ERROR_LOG("ERROR: m_inited: %d", m_inited);
		return -1;
	}

	if (p_config == NULL) {
		ERROR_LOG("ERROR: p_config == NULL");
		return -1;
	}

	m_p_config = p_config;
    m_work_idx = work_idx;

    //bandwidth limit
    //char bwlimit[32] = {0};
	//if (m_p_config->get_config("uexpr", "bandwidth", bwlimit, sizeof(bwlimit) - 1) != 0) {
	//	ERROR_LOG("ERROR: m_p_config->get_config(uexpr, bandwidth)");
	//	return -1;
	//}
    //m_bwlimit = atoi(bwlimit);
    //if (m_bwlimit <= 0) {
	//	ERROR_LOG("ERROR: wrong bandwidth[%s]", bwlimit);
	//	return -1;
    //}

    //rsync
    char rsync[32] = {0};
    if(m_p_config->get_config("uexpr", "rsync", rsync, sizeof(rsync) - 1) == 0) {
        if(strcmp(rsync, "yes") == 0) {
            m_rsync = 1;
        } else {
            m_rsync = 0;
        }
    } else {
        m_rsync = 1;
    }

	// uexpr tmp_dir
	memset(m_tmp_dir, 0, sizeof(m_tmp_dir));
	if (m_p_config->get_config("uexpr", "tmp_dir", m_tmp_dir, sizeof(m_tmp_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, tmp_dir)");
		return -1;
	}
	if (m_tmp_dir[strlen(m_tmp_dir) - 1] == '/') {         // 去掉最后的'/'
		m_tmp_dir[strlen(m_tmp_dir) - 1] = 0;
	}

    mkdir(m_tmp_dir, 0777);

    char tmp_str[128];
    sprintf(tmp_str, "/%u", m_work_idx);
    strcat(m_tmp_dir, tmp_str);
    mkdir(m_tmp_dir, 0777);

	// uexpr ucount_report_data_dir
	memset(m_ucount_report_data_dir, 0, sizeof(m_ucount_report_data_dir));
	if (m_p_config->get_config("uexpr", "ucount_report_data_dir", m_ucount_report_data_dir,
					sizeof(m_ucount_report_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, ucount_report_data_dir)");
		return -1;
	}
	if (m_ucount_report_data_dir[strlen(m_ucount_report_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_ucount_report_data_dir[strlen(m_ucount_report_data_dir) - 1] = 0;
	}

	// uexpr bak_ucount_report_data_dir
	memset(m_bak_ucount_report_data_dir, 0, sizeof(m_bak_ucount_report_data_dir));
	if (m_p_config->get_config("uexpr", "bak_ucount_report_data_dir", m_bak_ucount_report_data_dir,
					sizeof(m_bak_ucount_report_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, bak_ucount_report_data_dir)");
		return -1;
	}
	if (m_bak_ucount_report_data_dir[strlen(m_bak_ucount_report_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_bak_ucount_report_data_dir[strlen(m_bak_ucount_report_data_dir) - 1] = 0;
	}

	// uexpr ucount_result_data_dir
	memset(m_ucount_result_data_dir, 0, sizeof(m_ucount_result_data_dir));
	if (m_p_config->get_config("uexpr", "ucount_result_data_dir", m_ucount_result_data_dir,
					sizeof(m_ucount_result_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, ucount_result_data_dir)");
		return -1;
	}
	if (m_ucount_result_data_dir[strlen(m_ucount_result_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_ucount_result_data_dir[strlen(m_ucount_result_data_dir) - 1] = 0;
	}

	// uexpr bak_ucount_result_data_dir
	memset(m_bak_ucount_result_data_dir, 0, sizeof(m_bak_ucount_result_data_dir));
	if (m_p_config->get_config("uexpr", "bak_ucount_result_data_dir", m_bak_ucount_result_data_dir,
					sizeof(m_bak_ucount_result_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, bak_ucount_result_data_dir)");
		return -1;
	}
	if (m_bak_ucount_result_data_dir[strlen(m_bak_ucount_result_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_bak_ucount_result_data_dir[strlen(m_bak_ucount_result_data_dir) - 1] = 0;
	}

	// uexpr uvalue_report_data_dir
	memset(m_uvalue_report_data_dir, 0, sizeof(m_uvalue_report_data_dir));
	if (m_p_config->get_config("uexpr", "uvalue_report_data_dir", m_uvalue_report_data_dir,
					sizeof(m_uvalue_report_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, uvalue_report_data_dir)");
		return -1;
	}
	if (m_uvalue_report_data_dir[strlen(m_uvalue_report_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_uvalue_report_data_dir[strlen(m_uvalue_report_data_dir) - 1] = 0;
	}

	// uexpr bak_uvalue_report_data_dir
	memset(m_bak_uvalue_report_data_dir, 0, sizeof(m_bak_uvalue_report_data_dir));
	if (m_p_config->get_config("uexpr", "bak_uvalue_report_data_dir", m_bak_uvalue_report_data_dir,
					sizeof(m_bak_uvalue_report_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, bak_uvalue_report_data_dir)");
		return -1;
	}
	if (m_bak_uvalue_report_data_dir[strlen(m_bak_uvalue_report_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_bak_uvalue_report_data_dir[strlen(m_bak_uvalue_report_data_dir) - 1] = 0;
	}

	// uexpr uvalue_result_data_dir
	memset(m_uvalue_result_data_dir, 0, sizeof(m_uvalue_result_data_dir));
	if (m_p_config->get_config("uexpr", "uvalue_result_data_dir", m_uvalue_result_data_dir,
					sizeof(m_uvalue_result_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, uvalue_result_data_dir)");
		return -1;
	}
	if (m_uvalue_result_data_dir[strlen(m_uvalue_result_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_uvalue_result_data_dir[strlen(m_uvalue_result_data_dir) - 1] = 0;
	}

	// uexpr bak_uvalue_result_data_dir
	memset(m_bak_uvalue_result_data_dir, 0, sizeof(m_bak_uvalue_result_data_dir));
	if (m_p_config->get_config("uexpr", "bak_uvalue_result_data_dir", m_bak_uvalue_result_data_dir,
					sizeof(m_bak_uvalue_result_data_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, bak_uvalue_result_data_dir)");
		return -1;
	}
	if (m_bak_uvalue_result_data_dir[strlen(m_bak_uvalue_result_data_dir) - 1] == '/') {         // 去掉最后的'/'
		m_bak_uvalue_result_data_dir[strlen(m_bak_uvalue_result_data_dir) - 1] = 0;
	}

	// uexpr_so_dir
	memset(m_uexpr_so_dir, 0, sizeof(m_uexpr_so_dir));
	if (m_p_config->get_config("uexpr", "uexpr_so_dir", m_uexpr_so_dir,
					sizeof(m_uexpr_so_dir) - 1) != 0) {
		ERROR_LOG("ERROR: m_p_config->get_config(uexpr, uexpr_so_dir)");
		return -1;
	}
	if (m_uexpr_so_dir[strlen(m_uexpr_so_dir) - 1] == '/') {         // 去掉最后的'/'
		m_uexpr_so_dir[strlen(m_uexpr_so_dir) - 1] = 0;
	}

	int i = 0;
	char rsync_addr[1024] = {0};
	char ini_key_name[1024] = {0};

	// rsync_ucount_addr
	i = 0;
	do {
		memset(ini_key_name, 0, sizeof(ini_key_name));
		snprintf(ini_key_name, sizeof(ini_key_name) - 1, "rsync_ucount_addr_%d", i);

		memset(rsync_addr, 0, sizeof(rsync_addr));
		if (m_p_config->get_config("uexpr", ini_key_name,
					rsync_addr, sizeof(rsync_addr) - 1) != 0) {
			break;
		}
		if (rsync_addr[strlen(rsync_addr) - 1] == '/') {             // 去掉最后的'/'
			rsync_addr[strlen(rsync_addr) - 1] = 0;
		}
		m_vec_rsync_ucount_addr.push_back(rsync_addr);
		++i;
	} while(true);

	// rsync_bak_ucount_addr
	i = 0;
	do {
		memset(ini_key_name, 0, sizeof(ini_key_name));
		snprintf(ini_key_name, sizeof(ini_key_name) - 1, "rsync_bak_ucount_addr_%d", i);

		memset(rsync_addr, 0, sizeof(rsync_addr));
		if (m_p_config->get_config("uexpr", ini_key_name,
					rsync_addr, sizeof(rsync_addr) - 1) != 0) {
			break;
		}
		if (rsync_addr[strlen(rsync_addr) - 1] == '/') {             // 去掉最后的'/'
			rsync_addr[strlen(rsync_addr) - 1] = 0;
		}
		m_vec_rsync_bak_ucount_addr.push_back(rsync_addr);
		++i;
	} while(true);

	// rsync_uvalue_addr
	i = 0;
	do {
		memset(ini_key_name, 0, sizeof(ini_key_name));
		snprintf(ini_key_name, sizeof(ini_key_name) - 1, "rsync_uvalue_addr_%d", i);

		memset(rsync_addr, 0, sizeof(rsync_addr));
		if (m_p_config->get_config("uexpr", ini_key_name,
					rsync_addr, sizeof(rsync_addr) - 1) != 0) {
			break;
		}
		if (rsync_addr[strlen(rsync_addr) - 1] == '/') {             // 去掉最后的'/'
			rsync_addr[strlen(rsync_addr) - 1] = 0;
		}
		m_vec_rsync_uvalue_addr.push_back(rsync_addr);
		++i;
	} while(true);

	// rsync_uvalue_bak_addr
	i = 0;
	do {
		memset(ini_key_name, 0, sizeof(ini_key_name));
		snprintf(ini_key_name, sizeof(ini_key_name) - 1, "rsync_bak_uvalue_addr_%d", i);

		memset(rsync_addr, 0, sizeof(rsync_addr));
		if (m_p_config->get_config("uexpr", ini_key_name,
					rsync_addr, sizeof(rsync_addr) - 1) != 0) {
			break;
		}
		if (rsync_addr[strlen(rsync_addr) - 1] == '/') {             // 去掉最后的'/'
			rsync_addr[strlen(rsync_addr) - 1] = 0;
		}
		m_vec_rsync_bak_uvalue_addr.push_back(rsync_addr);
		++i;
	} while(true);

	if (m_vec_rsync_ucount_addr.empty()) {
		ERROR_LOG("ERROR: m_vec_rsync_ucount_addr.empty()");
		return -1;
	}
	if (m_vec_rsync_uvalue_addr.empty()) {
		ERROR_LOG("ERROR: m_vec_rsync_uvalue_addr.empty()");
		return -1;
	}
	if (m_vec_rsync_bak_ucount_addr.empty()) {
		ERROR_LOG("ERROR: m_vec_rsync_bak_ucount_addr.empty()");
		return -1;
	}
	if (m_vec_rsync_bak_uvalue_addr.empty()) {
		ERROR_LOG("ERROR: m_vec_rsync_bak_uvalue_addr.empty()");
		return -1;
	}

	m_inited = 1;

	return 0;
}

static uint32_t list_index;

static int print_ucount_list(uint32_t ucount, uint32_t isset, void *p_user_data)
{
	if (p_user_data == NULL) {
		return -1;
	}

    ((uint32_t *)p_user_data)[list_index++] = ucount;

	return 0;
}

uexpr_so_result_t * c_uexpr::eval_list(const char *p_uexpr, char *p_ret_buf)
{
    char *p_tmp = NULL;
    if((p_tmp = (char*)strchr(p_uexpr, '=')) != NULL) {
        p_tmp++;
    } else {
        p_tmp = (char *)p_uexpr;
    }

	DEBUG_LOG("p_uexpr: %s", p_tmp);

	i_ucount *p_ucount = NULL;
	i_uvalue *p_uvalue = NULL;
	token_type_t eval_token_type = NONE;

	char ucount_path_name[PATH_MAX] = {0};
	char uvalue_path_name[PATH_MAX] = {0};
	void *p_handle = NULL;

	p_ucount = create_and_init_tmp_ucount(ucount_path_name, sizeof(ucount_path_name) - 1);
	if (p_ucount == NULL) {
		ERROR_LOG("ERROR: create_and_init_tmp_ucount");
		goto ERROR;
	}
	p_uvalue = create_and_init_tmp_uvalue(uvalue_path_name, sizeof(uvalue_path_name) - 1);
	if (p_uvalue == NULL) {
		ERROR_LOG("ERROR: create_and_init_tmp_uvalue");
		goto ERROR;
	}

	m_p_uexpr = p_tmp;
	if (get_token() != 0) {
		ERROR_LOG("ERROR: get_token()");
		goto ERROR;
	}
	if (strlen(m_token) == 0) {
		ERROR_LOG("ERROR: strlen(%s) == 0", m_token);
		goto ERROR;
	}
	if (eval_equ(p_ucount, p_uvalue, &eval_token_type) != 0) {
		ERROR_LOG("ERROR: eval_equ(p_ucount, p_uvalue)");
		goto ERROR;
	}

	if (m_token_type == NONE) {                            // no so
		if (eval_token_type == UCOUNT_REPORT || eval_token_type == UCOUNT_RESULT) {
			int ucount_count = p_ucount->get();
			if (ucount_count == -1) {
				ERROR_LOG("ERROR: p_ucount->get()");
				goto ERROR;
			}

            p_ret_buf = (char*)malloc(sizeof(uint32_t) * (ucount_count + 1));

			((uexpr_so_result_t *)p_ret_buf)->result_len = SIZEOF(uexpr_so_result_t, result_len)  + (sizeof(uint32_t) * (ucount_count + 1));
			((uexpr_so_result_t *)p_ret_buf)->result_data[0] = ucount_count;

            list_index = 0;
            if (p_ucount->traverse(print_ucount_list, (void*)(&((uexpr_so_result_t *)p_ret_buf)->result_data[1]), 1) != 0) {
                ERROR_LOG("ERROR: traverse().");
            }
		}
		else {
			ERROR_LOG("ERROR: eval_token_type: %d is not ucount and no so", eval_token_type);
			goto ERROR;
		}
	} else if (m_token_type == SO_NAME) {
		//if (snprintf(so_path_name, sizeof(so_path_name) - 1, "%s/%s", m_uexpr_so_dir, m_token + 1) ==
		//			sizeof(so_path_name) - 1) {
		//	ERROR_LOG("ERROR: so_path_name: %s is truncated", so_path_name);
		//	goto ERROR;
		//}
		//if (get_token() != 0) {
		//	ERROR_LOG("ERROR: get_token() != 0");
		//	goto ERROR;
		//}
		//strncpy(so_param, m_token + 1, sizeof(so_param) - 1);

		//DEBUG_LOG("dlopen(%s, RTLD_LAZY)", so_path_name);
		//p_handle = dlopen(so_path_name, RTLD_LAZY);
		//if (p_handle == NULL) {
		//	ERROR_LOG("ERROR: dlopen(%s, RTLD_LAZY)", so_path_name);
		//	goto ERROR;
		//}
		//dlerror();                                         // clear any existing error

		//p_uexpr_so_init_t p_uexpr_so_init = NULL;
		//p_traverse_t p_traverse = NULL;
		//p_get_result_t p_get_result = NULL;
		//p_uexpr_so_uninit_t p_uexpr_so_uninit = NULL;

		//*(void **)(&p_uexpr_so_init) = dlsym(p_handle, "uexpr_so_init");
		//if ((p_error = dlerror()) != NULL) {
		//	ERROR_LOG("ERROR: dlsym(p_handle, uexpr_so_init)");
		//	goto ERROR;
		//}
		//*(void **)(&p_traverse) = dlsym(p_handle, "traverse");
		//if ((p_error = dlerror()) != NULL) {
		//	ERROR_LOG("ERROR: dlsym(p_handle, traverse)");
		//	goto ERROR;
		//}
		//*(void **)(&p_get_result) = dlsym(p_handle, "get_result");
		//if ((p_error = dlerror()) != NULL) {
		//	ERROR_LOG("ERROR: dlsym(p_handle, get_result)");
		//	goto ERROR;
		//}
		//*(void **)(&p_uexpr_so_uninit) = dlsym(p_handle, "uexpr_so_uninit");
		//if ((p_error = dlerror()) != NULL) {
		//	ERROR_LOG("ERROR: dlsym(p_handle, uexpr_so_uninit)");
		//	goto ERROR;
		//}

		//DEBUG_LOG("p_uexpr_so_init");
		//if ((*p_uexpr_so_init)(m_p_config, so_param, &p_user_data) != 0) {
		//	ERROR_LOG("ERROR: p_uexpr_so_init");
		//	goto ERROR;
		//}
		//DEBUG_LOG("traverse");
		//if (p_uvalue->traverse(p_traverse, p_user_data)) {
		//	ERROR_LOG("ERROR: p_uvalue->traverse");
		//	(*p_uexpr_so_uninit)(p_user_data);
		//	goto ERROR;
		//}
		//DEBUG_LOG("get_result");
		//uexpr_so_result_t *p_uexpr_so_result = (*p_get_result)(p_user_data);
		//if (p_uexpr_so_result == NULL) {
		//	ERROR_LOG("ERROR: p_get_result");
		//	(*p_uexpr_so_uninit)(p_user_data);
		//	goto ERROR;
		//}

		//memcpy(m_uexpr_result, p_uexpr_so_result, p_uexpr_so_result->result_len);

		//for (uint32_t i = 0; i != ((p_uexpr_so_result->result_len - SIZEOF(uexpr_so_result_t, result_len)) / SIZEOF(uexpr_so_result_t, result_data[0])); ++i) {
		//	DEBUG_LOG("value_%u: %u", i, p_uexpr_so_result->result_data[i]);
		//}

		//if ((*p_uexpr_so_uninit)(p_user_data) != 0) {
		//	ERROR_LOG("ERROR: p_get_result");
		//	goto ERROR;
		//}

		//dlclose(p_handle);
		//p_handle = NULL;
        
        ERROR_LOG("ERROR: uexpr not support yet");
        goto ERROR;
	} else {
		ERROR_LOG("ERROR: uexpr syntax: %s:%d", m_token, m_token_type);
		goto ERROR;
	}

	if (p_ucount != NULL) {
		p_ucount->uninit();
		p_ucount->release();
		p_ucount = NULL;
	}
	if (p_uvalue != NULL) {
		p_uvalue->uninit();
		p_uvalue->release();
		p_ucount = NULL;
	}

	unlink(ucount_path_name);
	unlink(uvalue_path_name);

	return (uexpr_so_result_t *)p_ret_buf;
ERROR:
	if (p_ucount != NULL) {
		p_ucount->uninit();
		p_ucount->release();
		p_ucount = NULL;
	}
	if (p_uvalue != NULL) {
		p_uvalue->uninit();
		p_uvalue->release();
		p_ucount = NULL;
	}

	unlink(ucount_path_name);
	unlink(uvalue_path_name);

	if (p_handle != NULL) {
		dlclose(p_handle);
		p_handle = NULL;
	}

	return NULL;
}

uexpr_so_result_t * c_uexpr::eval(const char *p_uexpr)
{
	DEBUG_LOG("p_uexpr: %s", p_uexpr);

	i_ucount *p_ucount = NULL;
	i_uvalue *p_uvalue = NULL;
	token_type_t eval_token_type = NONE;

	char ucount_path_name[PATH_MAX] = {0};
	char uvalue_path_name[PATH_MAX] = {0};
	char so_path_name[PATH_MAX] = {0};
	char so_param[TOKEN_LEN_MAX] = {0};
	void *p_handle = NULL;
	char *p_error = NULL;
	void *p_user_data = NULL;

	p_ucount = create_and_init_tmp_ucount(ucount_path_name, sizeof(ucount_path_name) - 1);
	if (p_ucount == NULL) {
		ERROR_LOG("ERROR: create_and_init_tmp_ucount");
		goto ERROR;
	}
	p_uvalue = create_and_init_tmp_uvalue(uvalue_path_name, sizeof(uvalue_path_name) - 1);
	if (p_uvalue == NULL) {
		ERROR_LOG("ERROR: create_and_init_tmp_uvalue");
		goto ERROR;
	}

	m_p_uexpr = p_uexpr;
	if (get_token() != 0) {
		ERROR_LOG("ERROR: get_token()");
		goto ERROR;
	}
	if (strlen(m_token) == 0) {
		ERROR_LOG("ERROR: strlen(%s) == 0", m_token);
		goto ERROR;
	}
	if (eval_equ(p_ucount, p_uvalue, &eval_token_type) != 0) {
		ERROR_LOG("ERROR: eval_equ(p_ucount, p_uvalue)");
		goto ERROR;
	}

	if (m_token_type == NONE) {                            // no so
		if (eval_token_type == UCOUNT_REPORT || eval_token_type == UCOUNT_RESULT) {
			int ucount_count = p_ucount->get();
			if (ucount_count == -1) {
				ERROR_LOG("ERROR: p_ucount->get()");
				goto ERROR;
			}

			((uexpr_so_result_t *)m_uexpr_result)->result_len = SIZEOF(uexpr_so_result_t, result_len)  + SIZEOF(uexpr_so_result_t, result_data[0]);
			((uexpr_so_result_t *)m_uexpr_result)->result_data[0] = ucount_count;
		}
		else {
			ERROR_LOG("ERROR: eval_token_type: %d is not ucount and no so", eval_token_type);
			goto ERROR;
		}
	} else if (m_token_type == SO_NAME) {
		if (snprintf(so_path_name, sizeof(so_path_name) - 1, "%s/%s", m_uexpr_so_dir, m_token + 1) ==
					sizeof(so_path_name) - 1) {
			ERROR_LOG("ERROR: so_path_name: %s is truncated", so_path_name);
			goto ERROR;
		}
		if (get_token() != 0) {
			ERROR_LOG("ERROR: get_token() != 0");
			goto ERROR;
		}
		strncpy(so_param, m_token + 1, sizeof(so_param) - 1);

		DEBUG_LOG("dlopen(%s, RTLD_LAZY)", so_path_name);
		p_handle = dlopen(so_path_name, RTLD_LAZY);
		if (p_handle == NULL) {
			ERROR_LOG("ERROR: dlopen(%s, RTLD_LAZY)", so_path_name);
			goto ERROR;
		}
		dlerror();                                         // clear any existing error

		p_uexpr_so_init_t p_uexpr_so_init = NULL;
		p_traverse_t p_traverse = NULL;
		p_get_result_t p_get_result = NULL;
		p_uexpr_so_uninit_t p_uexpr_so_uninit = NULL;

		*(void **)(&p_uexpr_so_init) = dlsym(p_handle, "uexpr_so_init");
		if ((p_error = dlerror()) != NULL) {
			ERROR_LOG("ERROR: dlsym(p_handle, uexpr_so_init)");
			goto ERROR;
		}
		*(void **)(&p_traverse) = dlsym(p_handle, "traverse");
		if ((p_error = dlerror()) != NULL) {
			ERROR_LOG("ERROR: dlsym(p_handle, traverse)");
			goto ERROR;
		}
		*(void **)(&p_get_result) = dlsym(p_handle, "get_result");
		if ((p_error = dlerror()) != NULL) {
			ERROR_LOG("ERROR: dlsym(p_handle, get_result)");
			goto ERROR;
		}
		*(void **)(&p_uexpr_so_uninit) = dlsym(p_handle, "uexpr_so_uninit");
		if ((p_error = dlerror()) != NULL) {
			ERROR_LOG("ERROR: dlsym(p_handle, uexpr_so_uninit)");
			goto ERROR;
		}

		//DEBUG_LOG("p_uexpr_so_init");
		if ((*p_uexpr_so_init)(m_p_config, so_param, &p_user_data) != 0) {
			ERROR_LOG("ERROR: p_uexpr_so_init");
			goto ERROR;
		}
		//DEBUG_LOG("traverse");
		if (p_uvalue->traverse(p_traverse, p_user_data)) {
			ERROR_LOG("ERROR: p_uvalue->traverse");
			(*p_uexpr_so_uninit)(p_user_data);
			goto ERROR;
		}
		//DEBUG_LOG("get_result");
		uexpr_so_result_t *p_uexpr_so_result = (*p_get_result)(p_user_data);
		if (p_uexpr_so_result == NULL) {
			ERROR_LOG("ERROR: p_get_result");
			(*p_uexpr_so_uninit)(p_user_data);
			goto ERROR;
		}

		memcpy(m_uexpr_result, p_uexpr_so_result, p_uexpr_so_result->result_len);

		//for (uint32_t i = 0; i != ((p_uexpr_so_result->result_len - SIZEOF(uexpr_so_result_t, result_len)) / SIZEOF(uexpr_so_result_t, result_data[0])); ++i) {
		//	DEBUG_LOG("value_%u: %u", i, p_uexpr_so_result->result_data[i]);
		//}

		if ((*p_uexpr_so_uninit)(p_user_data) != 0) {
			ERROR_LOG("ERROR: p_get_result");
			goto ERROR;
		}

		dlclose(p_handle);
		p_handle = NULL;
	} else {
		ERROR_LOG("ERROR: uexpr syntax: %s:%d", m_token, m_token_type);
		goto ERROR;
	}

	if (p_ucount != NULL) {
		p_ucount->uninit();
		p_ucount->release();
		p_ucount = NULL;
	}
	if (p_uvalue != NULL) {
		p_uvalue->uninit();
		p_uvalue->release();
		p_ucount = NULL;
	}

	unlink(ucount_path_name);
	unlink(uvalue_path_name);

	return (uexpr_so_result_t *)m_uexpr_result;
ERROR:
	if (p_ucount != NULL) {
		p_ucount->uninit();
		p_ucount->release();
		p_ucount = NULL;
	}
	if (p_uvalue != NULL) {
		p_uvalue->uninit();
		p_uvalue->release();
		p_ucount = NULL;
	}

	unlink(ucount_path_name);
	unlink(uvalue_path_name);

	if (p_handle != NULL) {
		dlclose(p_handle);
		p_handle = NULL;
	}

	return NULL;
}

int c_uexpr::eval_equ(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type)
{
	if (m_token_type == UCOUNT_RESULT || m_token_type == UVALUE_RESULT) {
		char token[TOKEN_LEN_MAX] = {0};
		token_type_t token_type = m_token_type;
		token_type_t eval_token_type = NONE;
		strncpy(token, m_token, sizeof(token) - 1);

		if (get_token() != 0) {
			ERROR_LOG("ERROR: get_token() != 0");
			return -1;
		}
		if (strncmp(m_token, "=", strlen("=")) == 0) {
			if (get_token() != 0) {
				ERROR_LOG("ERROR: get_token() != 0");
				return -1;
			}

			if (eval_equ(p_ucount, p_uvalue, &eval_token_type) != 0) {       // 支持连续赋值
				ERROR_LOG("ERROR: eval_equ");
				return -1;
			}

			//DEBUG_LOG("eval_token_type: %u", eval_token_type);

			int rv = 0;

			if (strchr(token, ',') == NULL) {
				char ulist_path_name[PATH_MAX] = {0};

				if (token_type == UCOUNT_RESULT) {
					rv = sscanf(token, "{%[^}]", ulist_path_name);
					*p_eval_token_type = UCOUNT_RESULT;
				} else {
					ERROR_LOG("ERROR: left operand must be UCOUNT_RESULT type !");
					return -1;
				}

				if (rv != 1) {
					ERROR_LOG("ERROR: rv != 1: %s", token);
					return -1;
				}

				if (get_list_from_ufile(p_ucount, ulist_path_name) != 0) {
					ERROR_LOG("ERROR: get_list_from_ufile(%p, %s)", p_ucount, ulist_path_name);
					return -1;
				}

			} else {
				uint32_t result_id = 0;
				uint32_t day_count = 0;
				if (token_type == UCOUNT_RESULT) {
					rv = sscanf(token, "{%u,%d}", &result_id, &day_count);
					*p_eval_token_type = UCOUNT_RESULT;
				} else {                                       // token_type == UVALUE_RESULT
					rv = sscanf(token, "{{%u,%d}}", &result_id, &day_count);
					*p_eval_token_type = UVALUE_RESULT;
				}

				if (rv != 2) {
					ERROR_LOG("ERROR: rv != 2: %s", token);
					return -1;
				}

				char ufile_path_name[PATH_MAX] = {0};
				if (get_ufile_path_name(result_id, day_count, ufile_path_name, sizeof(ufile_path_name) - 1, token_type) != 0) {
					ERROR_LOG("ERROR: get_ufile_path_name result_id: %d day_count: %d", result_id, day_count);
					return -1;
				}

				if (token_type == UCOUNT_RESULT) {
					i_ucount *p_ucount_result = NULL;
					p_ucount_result = create_and_init_tmp_ucount(ufile_path_name);
					if (p_ucount_result == NULL) {
						ERROR_LOG("ERROR: create_and_init_tmp_ucount(%s)", ufile_path_name);
						return -1;
					}

					if (eval_token_type == UCOUNT_REPORT || eval_token_type == UCOUNT_RESULT) {
						p_ucount_result->merge(p_ucount, i_ucount::UNION);
					} else {                                   // eval_token_type == UVALUE
						p_ucount_result->merge(p_uvalue, i_ucount::UNION);
						p_ucount->merge(p_uvalue, i_ucount::UNION);
					}

					if (p_ucount_result->uninit() != 0) {
						ERROR_LOG("ERROR: p_ucount_result->uninit() != 0");
						p_ucount_result->release();
						return -1;
					}
					if (p_ucount_result->release() != 0) {
						ERROR_LOG("ERROR: p_ucount_result->release() != 0");
						return -1;
					}
				} else {                                       // token_type == UVALUE_RESULT
					i_uvalue *p_uvalue_result = NULL;
					p_uvalue_result = create_and_init_tmp_uvalue(ufile_path_name);
					if (p_uvalue_result == NULL) {
						ERROR_LOG("ERROR: create_and_init_tmp_uvalue(%s)", ufile_path_name);
						return -1;
					}

					if (eval_token_type == UVALUE_REPORT || eval_token_type == UVALUE_RESULT) {
						p_uvalue->flush();
						p_uvalue_result->merge(p_uvalue, i_uvalue::UNION, uvalue_equ);
					} else {                                   // eval_token_type == UCOUNT
						p_uvalue_result->merge(p_ucount, i_uvalue::UNION);
						p_uvalue_result->flush();
						p_uvalue->merge(p_uvalue_result, i_uvalue::UNION, uvalue_equ);
					}

					if (p_uvalue_result->uninit() != 0) {
						ERROR_LOG("ERROR: p_uvalue_result->uninit() != 0");
						p_uvalue_result->release();
						return -1;
					}
					if (p_uvalue_result->release() != 0) {
						ERROR_LOG("ERROR: p_uvalue_result->release() != 0");
						return -1;
					}
				}
			}
		} else {
			if (put_token() != 0) {
				ERROR_LOG("ERROR: put_token");
				return -1;
			}
			memset(m_token, 0, sizeof(m_token));
			strncpy(m_token, token, sizeof(m_token) - 1);
			m_token_type = token_type;
			if (eval_set(p_ucount, p_uvalue, p_eval_token_type) != 0) {
				ERROR_LOG("ERROR: eval_set");
				return -1;
			}
		}
	} else {
		if (eval_set(p_ucount, p_uvalue, p_eval_token_type) != 0) {
			ERROR_LOG("ERROR: eval_set");
			return -1;
		}
	}

	return 0;
}

int c_uexpr::eval_set(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type)
{
	token_type_t eval_token_type = NONE;
	if (eval_par(p_ucount, p_uvalue, &eval_token_type) != 0) {
		ERROR_LOG("ERROR: eval_par.");
		return -1;
	}

	*p_eval_token_type = eval_token_type;

	char token[TOKEN_LEN_MAX] = {0};
	token_type_t token_type = NONE;
	strcpy(token, m_token);
	token_type = m_token_type;

	while (strncmp(token, "&", strlen("&")) == 0 ||
		   strncmp(token, "|", strlen("|")) == 0 ||
		   strncmp(token, "-", strlen("-")) == 0) {
		if (get_token() != 0) {
			ERROR_LOG("ERROR: get_token");
			return -1;
		}

		char ucount_path_name[PATH_MAX] = {0};
		char uvalue_path_name[PATH_MAX] = {0};

		i_ucount *p_tmp_ucount = create_and_init_tmp_ucount(ucount_path_name, sizeof(ucount_path_name) - 1);
		if (p_tmp_ucount == NULL) {
			ERROR_LOG("ERROR: create_and_init_tmp_ucount");
			unlink(ucount_path_name);
			unlink(uvalue_path_name);
			return -1;
		}
		i_uvalue *p_tmp_uvalue = create_and_init_tmp_uvalue(uvalue_path_name, sizeof(uvalue_path_name) - 1);
		if (p_tmp_uvalue == NULL) {
			ERROR_LOG("ERROR: create_and_init_tmp_uvalue");
			unlink(ucount_path_name);
			unlink(uvalue_path_name);
			return -1;
		}

		token_type_t tmp_eval_token_type;
		if (eval_par(p_tmp_ucount, p_tmp_uvalue, &tmp_eval_token_type) != 0) {
			ERROR_LOG("ERROR: eval_equ");
			p_tmp_ucount->uninit();
			p_tmp_uvalue->uninit();
			p_tmp_ucount->release();
			p_tmp_uvalue->release();
			unlink(ucount_path_name);
			unlink(uvalue_path_name);
			return -1;
		}

		if (strcmp(token, "&") == 0) {
			if (eval_token_type == UCOUNT_REPORT || eval_token_type == UCOUNT_RESULT) {
				if (tmp_eval_token_type == UCOUNT_REPORT || tmp_eval_token_type == UCOUNT_RESULT) {
					if (p_ucount->merge(p_tmp_ucount, i_ucount::INTERSECT) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(p_tmp_ucount, i_ucount::INTERSECT)");
						p_tmp_ucount->uninit();
						p_tmp_uvalue->uninit();
						p_tmp_ucount->release();
						p_tmp_uvalue->release();
						unlink(ucount_path_name);
						unlink(uvalue_path_name);
						return -1;
					}
				} else if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT){
					if (p_ucount->merge(p_tmp_uvalue, i_ucount::INTERSECT) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(p_tmp_uvalue, i_ucount::INTERSECT)");
						p_tmp_ucount->uninit();
						p_tmp_uvalue->uninit();
						p_tmp_ucount->release();
						p_tmp_uvalue->release();
						unlink(ucount_path_name);
						unlink(uvalue_path_name);
						return -1;
					}
				} else {
					ERROR_LOG("ERROR: it should never come here");
					p_tmp_ucount->uninit();
					p_tmp_uvalue->uninit();
					p_tmp_ucount->release();
					p_tmp_uvalue->release();
					unlink(ucount_path_name);
					unlink(uvalue_path_name);
					return -1;
				}
			} else {
				ERROR_LOG("ERROR: uexpr syntax: %s:%d", m_token, m_token_type);
				p_tmp_ucount->uninit();
				p_tmp_uvalue->uninit();
				p_tmp_ucount->release();
				p_tmp_uvalue->release();
				unlink(ucount_path_name);
				unlink(uvalue_path_name);
				return -1;
			}
		} else if (strcmp(token, "|") == 0) {
			if (eval_token_type == UCOUNT_REPORT || eval_token_type == UCOUNT_RESULT) {
				if (tmp_eval_token_type == UCOUNT_REPORT || tmp_eval_token_type == UCOUNT_RESULT) {
					if (p_ucount->merge(p_tmp_ucount, i_ucount::UNION) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(p_tmp_ucount, i_ucount::UNION)");
						p_tmp_ucount->uninit();
						p_tmp_uvalue->uninit();
						p_tmp_ucount->release();
						p_tmp_uvalue->release();
						return -1;
					}
				} else if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
					if (p_ucount->merge(p_tmp_uvalue, i_ucount::UNION) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(p_tmp_uvalue, i_ucount::UNION)");
						p_tmp_ucount->uninit();
						p_tmp_uvalue->uninit();
						p_tmp_ucount->release();
						p_tmp_uvalue->release();
						return -1;
					}
				} else {
					ERROR_LOG("ERROR: it should never come here");
					p_tmp_ucount->uninit();
					p_tmp_uvalue->uninit();
					p_tmp_ucount->release();
					p_tmp_uvalue->release();
					return -1;
				}
			} else {
				ERROR_LOG("ERROR: uexpr syntax: %s:%d", m_token, m_token_type);
				p_tmp_ucount->uninit();
				p_tmp_uvalue->uninit();
				p_tmp_ucount->release();
				p_tmp_uvalue->release();
				unlink(ucount_path_name);
				unlink(uvalue_path_name);
				return -1;
			}
		} else if (strcmp(token, "-") == 0) {
			if (eval_token_type == UCOUNT_REPORT || eval_token_type == UCOUNT_RESULT) {
				if (tmp_eval_token_type == UCOUNT_REPORT || tmp_eval_token_type == UCOUNT_RESULT) {
					if (p_ucount->merge(p_tmp_ucount, i_ucount::EXCEPT) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(p_tmp_ucount, i_ucount::EXCEPT)");
						p_tmp_ucount->uninit();
						p_tmp_uvalue->uninit();
						p_tmp_ucount->release();
						p_tmp_uvalue->release();
						return -1;
					}
				} else if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
					if (p_ucount->merge(p_tmp_uvalue, i_ucount::EXCEPT) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(p_tmp_uvalue, i_ucount::EXCEPT)");
						p_tmp_ucount->uninit();
						p_tmp_uvalue->uninit();
						p_tmp_ucount->release();
						p_tmp_uvalue->release();
						return -1;
					}
				} else {
					ERROR_LOG("ERROR: it should never come here");
					p_tmp_ucount->uninit();
					p_tmp_uvalue->uninit();
					p_tmp_ucount->release();
					p_tmp_uvalue->release();
					return -1;
				}
			} else {
				ERROR_LOG("ERROR: uexpr syntax: %s:%d", m_token, m_token_type);
				p_tmp_ucount->uninit();
				p_tmp_uvalue->uninit();
				p_tmp_ucount->release();
				p_tmp_uvalue->release();
				unlink(ucount_path_name);
				unlink(uvalue_path_name);
				return -1;
			}
		} else if (strcmp(token, "&+") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_add) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&#") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_intadd) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&-") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_sub) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&*") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_mul) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&/") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_div) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&A") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_max) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&I") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_min) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&L") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_left) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "&R") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::INTERSECT, c_uexpr::uvalue_right) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::INTERSECT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|+") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_add) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|#") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_intadd) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|-") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_sub) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|*") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_mul) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|/") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_div) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|A") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_max) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|I") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_min) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|L") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_left) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "|R") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::UNION, c_uexpr::uvalue_right) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::UNION);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-+") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_add) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-#") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_intadd) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "--") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_sub) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-*") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_mul) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-/") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_div) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-A") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_max) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-I") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_min) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-L") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_left) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else if (strcmp(token, "-R") == 0) {
			p_uvalue->flush();
			p_tmp_uvalue->flush();

			if (tmp_eval_token_type == UVALUE_REPORT || tmp_eval_token_type == UVALUE_RESULT) {
				if (p_uvalue->merge(p_tmp_uvalue, i_uvalue::EXCEPT, c_uexpr::uvalue_right) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge: %s", p_uvalue->get_last_errstr());
					return -1;
				}
			} else {
				p_uvalue->merge(p_tmp_ucount, i_uvalue::EXCEPT);
				p_uvalue->flush();
			}
		} else {
			ERROR_LOG("ERROR: uexpr syntax: %s:%d", m_token, m_token_type);
			p_tmp_ucount->uninit();
			p_tmp_uvalue->uninit();
			p_tmp_ucount->release();
			p_tmp_uvalue->release();
			unlink(ucount_path_name);
			unlink(uvalue_path_name);
			return -1;
		}

		p_tmp_ucount->uninit();
		p_tmp_uvalue->uninit();
		p_tmp_ucount->release();
		p_tmp_uvalue->release();
		unlink(ucount_path_name);
		unlink(uvalue_path_name);

		strcpy(token, m_token);
		token_type = m_token_type;
	}

	return 0;
}

int c_uexpr::eval_par(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type)
{
	if (*m_token == '(') {
		if (get_token() != 0) {
			ERROR_LOG("ERROR: get_token() != 0");
			return -1;
		}
		if (eval_set(p_ucount, p_uvalue, p_eval_token_type) != 0) {
			ERROR_LOG("ERROR: eval_set");
			return -1;
		}
		if (*m_token != ')') {
			ERROR_LOG("ERROR: Unbalanced Parentheses");
			return -1;
		}
		if (get_token() != 0) {
			ERROR_LOG("ERROR: get_token() != 0");
			return -1;
		}
	} else {
		if (atom(p_ucount, p_uvalue, p_eval_token_type) != 0) {
			ERROR_LOG("ERROR: atom(p_ucount, p_uvalue) != 0");
			return -1;
		}
	}

	return 0;
}

int c_uexpr::atom(i_ucount *p_ucount, i_uvalue *p_uvalue, token_type_t *p_eval_token_type)
{
	*p_eval_token_type = m_token_type;
    char cmd[1024] = {0};

	switch (m_token_type) {
		case UCOUNT_REPORT: {
			int flag = 0;
			const char *p_tmp_token = NULL;
			if (m_token[0] == '@') {
				p_tmp_token = m_token + 1;
				flag = 1;
			} else {
				p_tmp_token = m_token;
				flag = 0;
			}

			if (strchr(p_tmp_token, ',') == NULL) {
				flag=0;
				char ulist_path_name[PATH_MAX] = {0};

				int rv = sscanf(p_tmp_token, "[%[^]]",  ulist_path_name);
				if (rv != 1) {
					ERROR_LOG("ERROR: sscanf != 1: p_tmp_token: %s", p_tmp_token);
					return -1;
				}

				if (set_ufile_from_list(p_ucount, ulist_path_name) != 0) {
					ERROR_LOG("ERROR: set_ufile_from_list(%p, %s).", p_ucount, ulist_path_name);
					return -1;
				}
			}
			else {
				char ufile_path_name[PATH_MAX] = {0};
				uint32_t report_id = 0;
				uint32_t day = 0;

				int rv = sscanf(p_tmp_token, "[%u,%d]", &report_id, &day);
				if (rv != 2) {
					ERROR_LOG("ERROR: sscanf != 2: p_tmp_token: %s", p_tmp_token);
					return -1;
				}

				//同步唯一数文件
				if (rsync_ufile(report_id, day, m_token_type) != 0) {
					if (!flag) {
						ERROR_LOG("ERROR: rsync_ufile: report_id: %u day: %u", report_id, day);
						return -1;
					}
				}

				if (get_ufile_path_name(report_id, day, ufile_path_name, sizeof(ufile_path_name), m_token_type) != 0) {
					ERROR_LOG("ERROR: get_ufile_path_name");
					return -1;
				}
		
				if (access(ufile_path_name, R_OK) != 0) {      // 文件不存在
					if (!flag) {
						ERROR_LOG("ERROR: ufile_path_name: %s is not accessable: %s",
									ufile_path_name, strerror(errno));
						return -1;
					}
				} else {
					if (p_ucount->merge(ufile_path_name, i_ucount::UNION) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(%s, i_ucount::UNION) != 0", ufile_path_name);
						return -1;
					}
				}

			}


			break;
		}
		case UCOUNT_RESULT: {
			int flag = 0;
			const char *p_tmp_token = NULL;
			if (m_token[0] == '@') {
				p_tmp_token = m_token + 1;
				flag = 1;
			} else {
				p_tmp_token = m_token;
				flag = 0;
			}

			char ufile_path_name[PATH_MAX] = {0};

			if (strchr(p_tmp_token, ',') == NULL) {
				flag=0;
				char ulist_path_name[PATH_MAX] = {0};

				int rv = sscanf(p_tmp_token, "{%[^}]", ulist_path_name);
				if (rv != 1) {
					ERROR_LOG("ERROR: sscanf != 1: p_tmp_token: %s", p_tmp_token);
					return -1;
				}

				if (set_ufile_from_list(p_ucount, ulist_path_name) != 0) {
					ERROR_LOG("ERROR: set_ufile_from_list(%p, %s).", p_ucount, ulist_path_name);
					return -1;
				}
			}
			else {
				uint32_t result_id = 0;
				uint32_t day = 0;

				int rv = sscanf(p_tmp_token, "{%u,%d}", &result_id, &day);
				if (rv != 2) {
					ERROR_LOG("ERROR: sscanf != 2: p_tmp_token: %s", p_tmp_token);
					return -1;
				}

				if (get_ufile_path_name(result_id, day, ufile_path_name, sizeof(ufile_path_name), m_token_type) != 0) {
					ERROR_LOG("ERROR: get_ufile_path_name");
					return -1;
				}

				if (access(ufile_path_name, R_OK) != 0) {      // 文件不存在
                    snprintf(cmd, sizeof(cmd), "tar -zxSpf %s/ucount_result_%d_%d.tar.gz -C %s/", m_bak_ucount_result_data_dir, result_id, day, m_ucount_result_data_dir);
                    DEBUG_LOG("tar_cmd: %s", cmd);
                    if((rv = system(cmd)) != 0) {
                        ERROR_LOG("tar_cmd(%s) : %s", cmd, strerror(errno));
                    }
                }
                    
				if (access(ufile_path_name, R_OK) != 0) {      // 文件不存在
					if (!flag) {
						ERROR_LOG("ERROR: ufile_path_name: %s is not accessable: %s",
									ufile_path_name, strerror(errno));
						return -1;
                    }
				} else {
					if (p_ucount->merge(ufile_path_name, i_ucount::UNION) != 0) {
						ERROR_LOG("ERROR: p_ucount->merge(%s, i_ucount::UNION) != 0", ufile_path_name);
						return -1;
					}
				}
			}

			break;
		}
		case UVALUE_REPORT: {
			int flag = 0;
			const char *p_tmp_token = NULL;
			if (m_token[0] == '@') {
				p_tmp_token = m_token + 1;
				flag = 1;
			} else {
				p_tmp_token = m_token;
				flag = 0;
			}

			uint32_t report_id = 0;
			uint32_t day = 0;

			int rv = sscanf(p_tmp_token, "[[%u,%d]]", &report_id, &day);
			if (rv != 2) {
				ERROR_LOG("ERROR: sscanf != 2: p_tmp_token: %s", p_tmp_token);
				return -1;
			}

            //同步文件
			if (rsync_ufile(report_id, day, m_token_type) != 0) {
				if (!flag) {
					ERROR_LOG("ERROR: rsync_ufile: report_id: %u day: %u", report_id, day);
					return -1;
				}
			}

			char ufile_path_name[PATH_MAX] = {0};
			if (get_ufile_path_name(report_id, day, ufile_path_name, sizeof(ufile_path_name), m_token_type) != 0) {
				ERROR_LOG("ERROR: get_ufile_path_name");
				return -1;
			}

			if (access(ufile_path_name, R_OK) != 0) {      // 文件不存在
				if (!flag) {
					ERROR_LOG("ERROR: ufile_path_name: %s is not accessable: %s",
								ufile_path_name, strerror(errno));
					return -1;
				}
			} else {
				if (p_uvalue->merge(ufile_path_name, i_uvalue::UNION, uvalue_equ) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge(%s, i_uvalue::UNION, uvalue_equ) != 0",
								ufile_path_name);
					return -1;
				}
			}

			break;
		}
		case UVALUE_RESULT: {
			int flag = 0;
			const char *p_tmp_token = NULL;
			if (m_token[0] == '@') {
				p_tmp_token = m_token + 1;
				flag = 1;
			} else {
				p_tmp_token = m_token;
				flag = 0;
			}

			uint32_t result_id = 0;
			uint32_t day = 0;

			int rv = sscanf(p_tmp_token, "{{%u,%d}}", &result_id, &day);
			if (rv != 2) {
				ERROR_LOG("ERROR: sscanf != 2: p_tmp_token: %s", p_tmp_token);
				return -1;
			}

			char ufile_path_name[PATH_MAX] = {0};
			if (get_ufile_path_name(result_id, day, ufile_path_name, sizeof(ufile_path_name), m_token_type) != 0) {
				ERROR_LOG("ERROR: get_ufile_path_name");
				return -1;
			}

            DEBUG_LOG("ufile_path_name: %s", ufile_path_name);

            if (access(ufile_path_name, R_OK) != 0) {      // 文件不存在
                snprintf(cmd, sizeof(cmd), "tar -zxSpf %s/uvalue_result_%d_%d.tar.gz -C %s/", m_bak_uvalue_result_data_dir, result_id, day, m_uvalue_result_data_dir);
                DEBUG_LOG("tar_cmd: %s", cmd);
                if((rv = system(cmd)) != 0) {
                    ERROR_LOG("tar_cmd(%s) : %s", cmd, strerror(errno));
                }
            }
                    
			if (access(ufile_path_name, R_OK) != 0) {      // 文件不存在
				if (!flag) {
					ERROR_LOG("ERROR: ufile_path_name: %s is not accessable: %s",
								ufile_path_name, strerror(errno));
					return -1;
                }
			} else {
				if (p_uvalue->merge(ufile_path_name, i_uvalue::UNION, uvalue_equ) != 0) {
					ERROR_LOG("ERROR: p_uvalue->merge(%s, i_uvalue::UNION, uvalue_equ) != 0",
								ufile_path_name);
					return -1;
				}
			}

			break;
		}
		default: {
			ERROR_LOG("ERROR: m_token_type unknown.");
			memset(m_last_errstr, 0, sizeof(m_last_errstr));
			strncpy(m_last_errstr, "ERROR: Syntax Error.", sizeof(m_last_errstr) - 1);
			return -1;
		}
	}

	if (get_token() != 0) {
		ERROR_LOG("ERROR: get_token().");
		return -1;
	}

	return 0;
}

int c_uexpr::uninit()
{
	if (!m_inited) {
		return -1;
	}

	m_inited = 0;

	return 0;
}

int c_uexpr::release()
{
	delete this;

	return 0;
}

int c_uexpr::get_token()
{
	char *p_tmp = NULL;
	m_token_type = NONE;
	p_tmp = m_token;

	*p_tmp = 0;

	while (isspace(*m_p_uexpr)) {
		++m_p_uexpr;
	}

	if (*m_p_uexpr == 0) {
		m_token_type = NONE;
		return 0;
	}

	if (strncmp(m_p_uexpr, "=", strlen("=")) == 0 ||
		strncmp(m_p_uexpr, "(", strlen("(")) == 0 ||
		strncmp(m_p_uexpr, ")", strlen(")")) == 0) {
		m_token_type = DELIMITER;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "&+", strlen("&+")) == 0 ||
			   strncmp(m_p_uexpr, "&#", strlen("&-")) == 0 ||
			   strncmp(m_p_uexpr, "&-", strlen("&-")) == 0 ||
			   strncmp(m_p_uexpr, "&*", strlen("&*")) == 0 ||
			   strncmp(m_p_uexpr, "&/", strlen("&/")) == 0 ||
			   strncmp(m_p_uexpr, "&A", strlen("&/")) == 0 ||
			   strncmp(m_p_uexpr, "&I", strlen("&/")) == 0 ||
			   strncmp(m_p_uexpr, "&L", strlen("&L")) == 0 ||
			   strncmp(m_p_uexpr, "&R", strlen("&R")) == 0 ||
			   strncmp(m_p_uexpr, "|+", strlen("|+")) == 0 ||
			   strncmp(m_p_uexpr, "|#", strlen("|+")) == 0 ||
			   strncmp(m_p_uexpr, "|-", strlen("|-")) == 0 ||
			   strncmp(m_p_uexpr, "|*", strlen("|*")) == 0 ||
			   strncmp(m_p_uexpr, "|/", strlen("|/")) == 0 ||
			   strncmp(m_p_uexpr, "|A", strlen("|A")) == 0 ||
			   strncmp(m_p_uexpr, "|I", strlen("|I")) == 0 ||
			   strncmp(m_p_uexpr, "|L", strlen("|L")) == 0 ||
			   strncmp(m_p_uexpr, "|R", strlen("|R")) == 0 ||
			   strncmp(m_p_uexpr, "-+", strlen("-+")) == 0 ||
			   strncmp(m_p_uexpr, "-#", strlen("-+")) == 0 ||
			   strncmp(m_p_uexpr, "--", strlen("--")) == 0 ||
			   strncmp(m_p_uexpr, "-*", strlen("-*")) == 0 ||
			   strncmp(m_p_uexpr, "-/", strlen("-/")) == 0 ||
			   strncmp(m_p_uexpr, "-L", strlen("-L")) == 0 ||
			   strncmp(m_p_uexpr, "-R", strlen("-R")) == 0) {
		m_token_type = DELIMITER;
		*p_tmp++ = *m_p_uexpr++;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "&", strlen("&")) == 0 ||
			   strncmp(m_p_uexpr, "|", strlen("|")) == 0 ||
			   strncmp(m_p_uexpr, "-", strlen("-")) == 0) {
		m_token_type = DELIMITER;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "[[", strlen("[[")) == 0) {
		m_token_type = UVALUE_REPORT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != ']') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "@[[", strlen("@[[")) == 0) {
		m_token_type = UVALUE_REPORT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != ']') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "{{", strlen("{{")) == 0) {
		m_token_type = UVALUE_RESULT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != '}') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "@{{", strlen("@{{")) == 0) {
		m_token_type = UVALUE_RESULT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != '}') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "[", strlen("[")) == 0) {
		m_token_type = UCOUNT_REPORT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != ']') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "@[", strlen("@[")) == 0) {
		m_token_type = UCOUNT_REPORT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != ']') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "{", strlen("{")) == 0) {
		m_token_type = UCOUNT_RESULT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != '}') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, "@{", strlen("@{")) == 0) {
		m_token_type = UCOUNT_RESULT;
		while (*m_p_uexpr != 0 && *m_p_uexpr != '}') {
			*p_tmp++ = *m_p_uexpr++;
		}
		*p_tmp++ = *m_p_uexpr++;
	} else if (strncmp(m_p_uexpr, ":", strlen(":")) == 0) {
		*p_tmp++ = *m_p_uexpr++;
		while (*m_p_uexpr != 0 && *m_p_uexpr != ':') {
			*p_tmp++ = *m_p_uexpr++;
		}
		if (*m_p_uexpr == ':') {
			m_token_type = SO_NAME;
		} else {
			m_token_type = SO_PARAM;
		}
	} else {
		memset(m_last_errstr, 0, sizeof(m_last_errstr));
		strncpy(m_last_errstr, "ERROR: Syntax error.", sizeof(m_last_errstr) - 1);
		return -1;
	}

	*p_tmp = 0;

	return 0;
}

int c_uexpr::put_token()
{
	for (char *ch = m_token; *ch; ch++) {
		m_p_uexpr--;
	}

	return 0;
}

i_ucount * c_uexpr::create_and_init_tmp_ucount(char *p_ucount_path_name, int len)
{
	i_ucount *p_ucount = NULL;
	int ucount_fd = -1;

	if (snprintf(p_ucount_path_name, len, "%s/ucount_tmp_XXXXXX", m_tmp_dir) == len) {
		ERROR_LOG("ERROR: p_ucount_path_name: %s is truncated", p_ucount_path_name);
		return NULL;
	}

	// 调用mkstemp以产生随机的临时文件名。
	ucount_fd = mkstemp(p_ucount_path_name);

	if (ucount_fd == -1) {
		ERROR_LOG("ERROR: ucount_fd == -1 p_ucount_path_name: %s", p_ucount_path_name);
		return NULL;
	}

	close(ucount_fd);
	ucount_fd = -1;
	unlink(p_ucount_path_name);

	if (create_ucount_instance(&p_ucount) != 0) {
		ERROR_LOG("ERROR: create_ucount_instance");
		return NULL;
	}

	if (p_ucount->init(p_ucount_path_name, i_ucount::CREATE | i_ucount::EXCL, 0600) != 0) {
		ERROR_LOG("ERROR: p_ucount->init(%s, i_ucount::CREATE | i_ucount::EXCL, 0)", p_ucount_path_name);
		p_ucount->release();
		return NULL;
	}

	return p_ucount;
}

i_ucount * c_uexpr::create_and_init_tmp_ucount(const char *p_ucount_path_name)
{
	i_ucount *p_ucount = NULL;

	if (create_ucount_instance(&p_ucount) != 0) {
		ERROR_LOG("ERROR: create_ucount_instance");
		return NULL;
	}

	unlink(p_ucount_path_name);
	if (p_ucount->init(p_ucount_path_name, i_ucount::CREATE | i_ucount::EXCL, 0600) != 0) {
		ERROR_LOG("ERROR: p_ucount->init(%s, i_ucount::CREATE | i_ucount::EXCL, 0)", p_ucount_path_name);
		p_ucount->release();
		return NULL;
	}

	return p_ucount;
}

i_uvalue * c_uexpr::create_and_init_tmp_uvalue(char *p_uvalue_path_name, int len)
{
	i_uvalue *p_uvalue = NULL;
	int uvalue_fd = -1;

	if (snprintf(p_uvalue_path_name, len, "%s/uvalue_tmp_XXXXXX", m_tmp_dir) == len) {
		ERROR_LOG("ERROR: p_uvalue_path_name: %s is truncated", p_uvalue_path_name);
		return NULL;
	}

	uvalue_fd = mkstemp(p_uvalue_path_name);

	if (uvalue_fd == -1) {
		ERROR_LOG("ERROR: uvalue_fd == -1");
		return NULL;
	}

	close(uvalue_fd);
	uvalue_fd = -1;
	unlink(p_uvalue_path_name);

	if (create_uvalue_instance(&p_uvalue) != 0) {
		ERROR_LOG("ERROR: create_uvalue_instance");
		return NULL;
	}

	if (p_uvalue->init(p_uvalue_path_name, i_uvalue::CREATE | i_uvalue::EXCL | i_uvalue::BTREE, 0600, NULL) != 0) {
		ERROR_LOG("ERROR: p_uvalue->init(%s, i_uvalue::CREATE | i_uvalue::EXCL | i_uvalue::BTREE, 0600, NULL)", p_uvalue_path_name);
		p_uvalue->release();
		return NULL;
	}

	return p_uvalue;
}

i_uvalue * c_uexpr::create_and_init_tmp_uvalue(const char *p_uvalue_path_name)
{
	i_uvalue *p_uvalue = NULL;

	if (create_uvalue_instance(&p_uvalue) != 0) {
		ERROR_LOG("ERROR: create_uvalue_instance");
		return NULL;
	}

	unlink(p_uvalue_path_name);
	if (p_uvalue->init(p_uvalue_path_name, i_uvalue::CREATE | i_uvalue::EXCL | i_uvalue::BTREE, 0600, NULL) != 0) {
		ERROR_LOG("ERROR: p_uvalue->init(%s, i_uvalue::CREATE | i_uvalue::EXCL | i_uvalue::BTREE, 0600, NULL)", p_uvalue_path_name);
		p_uvalue->release();
		return NULL;
	}

	return p_uvalue;
}

int c_uexpr::uvalue_equ(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1;
	*p_result_value_size = value_size1;
	return 0;
}

int c_uexpr::uvalue_add(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1 + *(uint32_t *)p_value_data2;
	*p_result_value_size = value_size1;
	return 0;
}

int c_uexpr::uvalue_intadd(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(int32_t *)p_result_value = *(int32_t *)p_value_data1 + *(int32_t *)p_value_data2;
	*p_result_value_size = value_size1;
	return 0;
}

int c_uexpr::uvalue_sub(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1 - *(uint32_t *)p_value_data2;
	*p_result_value_size = value_size1;
	return 0;
}

int c_uexpr::uvalue_mul(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1 * *(uint32_t *)p_value_data2;
	*p_result_value_size = value_size1;
	return 0;
}

int c_uexpr::uvalue_div(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	if (*(uint32_t *)p_value_data2 == 0) {
		return -1;
	}

	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1 / *(uint32_t *)p_value_data2;
	*p_result_value_size = value_size1;

	return 0;
}

int c_uexpr::uvalue_max(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = std::max(*(uint32_t *)p_value_data1, *(uint32_t *)p_value_data2);
	*p_result_value_size = value_size1;
	return 0;
}

int c_uexpr::uvalue_min(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = std::min(*(uint32_t *)p_value_data1, *(uint32_t *)p_value_data2);
	*p_result_value_size = value_size1;

	return 0;
}

int c_uexpr::uvalue_left(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1;
	*p_result_value_size = value_size1;

	return 0;
}

int c_uexpr::uvalue_right(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data2;
	*p_result_value_size = value_size1;

	return 0;
}


int c_uexpr::get_ufile_path_name(int r_id, int day_count, char *p_buffer, int buffer_size, token_type_t token_type)
{
	int rv = -1;

	switch (token_type) {
		case UCOUNT_REPORT:
			rv = snprintf(p_buffer, buffer_size, "%s/map%d_%d", m_ucount_report_data_dir, r_id, day_count);
			break;
		case UCOUNT_RESULT:
			rv = snprintf(p_buffer, buffer_size, "%s/ucount_result_%d_%d", m_ucount_result_data_dir, r_id, day_count);
			break;
		case UVALUE_REPORT:
			rv = snprintf(p_buffer, buffer_size, "%s/uvalue_%d_%d", m_uvalue_report_data_dir, r_id, day_count);
			break;
		case UVALUE_RESULT:
			rv = snprintf(p_buffer, buffer_size, "%s/uvalue_result_%d_%d", m_uvalue_result_data_dir, r_id, day_count);
			break;
		default:
			ERROR_LOG("ERROR: get_ufile_path_name: r_id: %d day_count: %d token_type: %u", r_id, day_count, token_type);
			return -1;
	}

	if (rv == buffer_size) {
		ERROR_LOG("ERROR: p_buffer: %s is truncated", p_buffer);
		return -1;
	}

	return 0;
}

int c_uexpr::rsync_ufile(int r_id, int day_count, token_type_t token_type)
{
	char rsync_cmd[4096] = {0};
	char local_ufile_path_name[PATH_MAX] = {0};
	int offset = 0;
    int rv;

	get_ufile_path_name(r_id, day_count, local_ufile_path_name, sizeof(local_ufile_path_name) - 1, token_type);

    if(m_rsync == 0) {
        if(access(local_ufile_path_name, F_OK) == 0) {//不强制同步文件
            return 0;
        } else {
            //去bak目录中找，有的话解压
            switch (token_type) {
                case UCOUNT_REPORT:
                    snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "tar -zxSpf %s/map%d_%d.tar.gz -C %s/ > /dev/null 2>&1",
                            m_bak_ucount_report_data_dir, r_id, day_count, m_ucount_report_data_dir);
                    break;
                case UVALUE_REPORT:
                    snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "tar -zxSpf %s/uvalue_%d_%d.tar.gz -C %s/ > /dev/null 2>&1",
                            m_bak_uvalue_report_data_dir, r_id, day_count, m_uvalue_report_data_dir);
                    break;
                default:
                    ERROR_LOG("ERROR: rsync_ufile: token_type is not report");
                    return -1;
            }

            DEBUG_LOG("tar_cmd: %s", rsync_cmd);
            rv = system(rsync_cmd);
            if(rv != 0) {
                ERROR_LOG("ERROR: system(%s): %s", rsync_cmd, strerror(rv));
            } else {
                return 0;
            }
        }
    }


    //去ucount、uvalue服务器上同步文件
	switch (token_type) {
		case UCOUNT_REPORT:
			offset = r_id % m_vec_rsync_ucount_addr.size();
			snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "rsync -cS %s/map%d_%d %s > /dev/null 2>&1",
						m_vec_rsync_ucount_addr[offset].c_str(), r_id, day_count, local_ufile_path_name);
			break;
		case UVALUE_REPORT:
			offset = r_id % m_vec_rsync_uvalue_addr.size();
			snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "rsync -cS %s/uvalue_%d_%d %s > /dev/null 2>&1",
						m_vec_rsync_uvalue_addr[offset].c_str(), r_id, day_count, local_ufile_path_name);
			break;
		default:
			ERROR_LOG("ERROR: rsync_ufile: token_type is not report");
			return -1;
	}

	DEBUG_LOG("rsync_cmd: %s", rsync_cmd);
    if((rv = system(rsync_cmd) != 0)) {
        //没有同步到，去bak目录同步压缩文件
        switch (token_type) {
            case UCOUNT_REPORT:
                offset = r_id % m_vec_rsync_bak_ucount_addr.size();
                snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "rsync -cS %s/map%d_%d.tar.gz %s.tar.gz > /dev/null 2>&1",
                        m_vec_rsync_bak_ucount_addr[offset].c_str(), r_id, day_count, local_ufile_path_name);
                break;
            case UVALUE_REPORT:
                offset = r_id % m_vec_rsync_bak_uvalue_addr.size();
                snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "rsync -cS %s/uvalue_%d_%d.tar.gz %s.tar.gz > /dev/null 2>&1",
                        m_vec_rsync_bak_uvalue_addr[offset].c_str(), r_id, day_count, local_ufile_path_name);
                break;
            default:
                ERROR_LOG("ERROR: rsync_ufile: token_type is not report");
                return -1;
        }

        DEBUG_LOG("rsync_cmd: %s", rsync_cmd);
        if ((rv = system(rsync_cmd)) == 0) {
            switch (token_type) {
                case UCOUNT_REPORT:
                    offset = r_id % m_vec_rsync_bak_ucount_addr.size();
                    snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "tar -zxSpf %s.tar.gz -C %s/ > /dev/null 2>&1",
                            local_ufile_path_name, m_ucount_report_data_dir);
                    break;
                case UVALUE_REPORT:
                    offset = r_id % m_vec_rsync_bak_uvalue_addr.size();
                    snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "tar -zxSpf %s.tar.gz -C %s > /dev/null 2>&1",
                            local_ufile_path_name, m_uvalue_report_data_dir);
                    break;
                default:
                    ERROR_LOG("ERROR: rsync_ufile: token_type is not report");
                    return -1;
            }
            DEBUG_LOG("tar_cmd: %s", rsync_cmd);
            system(rsync_cmd);
            //snprintf(rsync_cmd, sizeof(rsync_cmd) - 1, "rm %s.tar.gz -f", local_ufile_path_name);
            //DEBUG_LOG("rm_cmd: %s", rsync_cmd);
            //system(rsync_cmd);
            return 0;
        } else {
            ERROR_LOG("ERROR: system(%s): %s", rsync_cmd, strerror(rv));
            return -1;
        }
	}

	return 0;
}

int c_uexpr::set_ufile_from_list(i_ucount *p_ucount, const char *ulist_path_name)
{
	if (p_ucount == NULL || ulist_path_name == NULL) {
		ERROR_LOG("ERROR: null argument.");
		return -1;
	}

	char buf[32] = {0};
	FILE *handle = NULL;
	if ((handle = fopen(ulist_path_name, "r")) == NULL) {
		ERROR_LOG("ERROR: fopen(%s, \"r\"): %s", ulist_path_name, strerror(errno));
		return -1;
	}

	while (fgets(buf, sizeof(buf) - 1, handle) != NULL) {
		uint32_t uid = (uint32_t)atoi(buf);
		if (uid <= 0) {
			continue;
		}
		if (p_ucount->set(uid, i_ucount::SET) != 0) {
			ERROR_LOG("p_ucount->set(%d, i_ucount::SET).", uid);
			//return -1;
		}
	}

	if (fclose(handle) == EOF) {
		ERROR_LOG("ERROR: fclose(): %s", strerror(errno));
		return -1;
	}

	return 0;
}

static int write_ucount_list(uint32_t ucount, uint32_t isset, void *p_user_data)
{
	if (p_user_data == NULL) {
		return -1;
	}

	fprintf((FILE *)p_user_data, "%d\n", ucount);

	return 0;
}

int c_uexpr::get_list_from_ufile(i_ucount *p_ucount, const char *ulist_path_name)
{
	if (p_ucount == NULL || ulist_path_name == NULL) {
		ERROR_LOG("ERROR: null argument.");
		return -1;
	}

	FILE *handle = NULL;
	if ((handle = fopen(ulist_path_name, "w+")) == NULL) {
		ERROR_LOG("ERROR: fopen(%s, \"r\"): %s", ulist_path_name, strerror(errno));
		return -1;
	}

	void *user_buffer = handle;
	if (p_ucount->traverse(write_ucount_list, user_buffer, 1) != 0) {
		ERROR_LOG("ERROR: traverse().");
	}

	if (fclose(handle) == EOF) {
		ERROR_LOG("ERROR: fclose(): %s", strerror(errno));
		return -1;
	}

	return 0;
}

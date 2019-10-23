
#include "log.h"
#include "i_uexpr_so.h"

int init(i_config *p_config, const char *p_so_param, void **pp_user_data)
{
	DEBUG_LOG("cb_init: p_so_param: %s", p_so_param);
	return 0;
}

int traverse(const void *key, uint32_t key_size, const void *value, uint32_t value_size, 
			    void *p_user_data)
{
	DEBUG_LOG("traverse: key: %u value: %u", *(uint32_t *)key, *(uint32_t *)value);
	return 0;
}

uexpr_so_result_t * get_result(void *p_user_data)
{
	DEBUG_LOG("get_result");
	return 0;
}

int uninit(void *p_user_data)
{
	DEBUG_LOG("uninit");
	return 0;
}


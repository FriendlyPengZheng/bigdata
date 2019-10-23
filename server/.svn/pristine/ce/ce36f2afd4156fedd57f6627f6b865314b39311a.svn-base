
#include <stdio.h>

#include "log.h"
#include "i_ucount.h"

int uvalue_add(const void *p_value_data1, uint32_t value_size1,
				        const void *p_value_data2, uint32_t value_size2,
						void *p_result_value, uint32_t *p_result_value_size)
{
	*(uint32_t *)p_result_value = *(uint32_t *)p_value_data1 + *(uint32_t *)p_value_data2;
	*p_result_value_size = value_size1;
	return 0;
}

int cb_traverse(uint32_t ucount, uint32_t isset, void *p_user_data)
{
	fprintf(stdout, "%u\n", ucount);

	return 0;
}

int main()
{
//	i_uvalue *p_uvalue = NULL;
//
//	if (create_uvalue_instance(&p_uvalue) != 0) {
//		ERROR_LOG("ERROR: create_uexpr_instance");
//		return -1;
//	}
//
//	p_uvalue->init("./uvalue-report-data/uvalue_report_1_0", i_uvalue::CREATE | i_uvalue::BTREE, 0600, NULL);
//	p_uvalue->merge("./uvalue-report-data/uvalue_report_2_0", i_uvalue::INTERSECT, uvalue_add);
//	p_uvalue->uninit();
//	p_uvalue->release();
	
	i_ucount *p_ucount = NULL;

	create_ucount_instance(&p_ucount);

	p_ucount->init("/home/hansel/workspace/ucount-read/data/result-data/map_result_8711_0", 0, 0);
	p_ucount->traverse(cb_traverse, NULL, 1);
	p_ucount->uninit();
	p_ucount->release();

	return 0;
}


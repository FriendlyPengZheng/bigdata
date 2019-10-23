/**
 * @file proto_ustring_test.cpp
 * @author richard <richard@taomee.com>
 * @date 2011-01-13
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>

#include "log.h"
#include "i_proto_so.h"

int (*p_proto_init)(i_timer *p_timer, i_config *p_config);
int (*p_get_proto_id)(uint32_t *p_proto_id, int *proto_count);
int (*p_proto_process)(ss_message_header_t ss_msg_hdr, void *p_data);
int (*p_proto_uninit)();

int main(int argc, char **argv)
{
	void *p_handle = NULL;
	char *p_error = NULL;
	i_timer *p_timer = NULL;
	i_config *p_config = NULL;
	uint32_t proto_id[64] = {0};
	int proto_count = 0;
	ss_message_header_t ss_msg_hdr = {0};
	
	char ustring_count[] = {"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"};
	printf("%lu\n", strlen(ustring_count));

	struct ustring_value_t {
		uint32_t value;
		char string[0];
	};

	char buffer[1024] = {0};
	ustring_value_t *p_ustring_value = (ustring_value_t *)buffer;
	p_ustring_value->value = 12;
	strcpy(p_ustring_value->string, "Hello World!");

	char config_file_list[][PATH_MAX] = {"../../../../conf/stat_server.ini"};
	int config_file_count = sizeof(config_file_list) / sizeof(*config_file_list);

	srand(time(NULL));

	p_handle = dlopen("proto_ustring.so", RTLD_LAZY);
	if (p_handle == NULL) {
		fprintf(stderr, "ERROR: dlopen: %s\n", dlerror());
		goto ERROR;
	}

	dlerror();    /* Clear any existing error */

	*(void **)(&p_proto_init) = dlsym(p_handle, "proto_init");
	p_error = dlerror();
	if (p_error != NULL)  {
		fprintf(stderr, "ERROR: dlsym: %s\n", p_error);
		goto ERROR;
	}
	*(void **)(&p_get_proto_id) = dlsym(p_handle, "get_proto_id");
	p_error = dlerror();
	if (p_error != NULL)  {
		fprintf(stderr, "ERROR: dlsym: %s\n", p_error);
		goto ERROR;
	}
	*(void **)(&p_proto_process) = dlsym(p_handle, "proto_process");
	p_error = dlerror();
	if (p_error != NULL)  {
		fprintf(stderr, "ERROR: dlsym: %s\n", p_error);
		goto ERROR;
	}
	*(void **)(&p_proto_uninit) = dlsym(p_handle, "proto_uninit");
	p_error = dlerror();
	if (p_error != NULL)  {
		fprintf(stderr, "ERROR: dlsym: %s\n", p_error);
		goto ERROR;
	}

	create_timer_instance(&p_timer);
	p_timer->init();

	create_config_instance(&p_config);
	p_config->init(config_file_list, config_file_count);

	if (p_proto_init(p_timer, p_config) != 0) {
		ERROR_LOG("ERROR: proto_init");
		goto ERROR;
	}

	if (p_get_proto_id(proto_id, &proto_count) != 0) {
		ERROR_LOG("ERROR: get_proto_id");
		goto ERROR;
	}

	if (proto_count == 0) {
		ERROR_LOG("ERROR: proto_count == 0");
		goto ERROR;
	}

	for (int i = 0; i != proto_count; ++i) {
		fprintf(stdout, "proto_id[%d]: %u\n", i, proto_id[i]);
	}

/*
	for (int i = 0; i != 1000; ++i) {
		ss_msg_hdr.len = sizeof(ss_msg_hdr) + strlen(ustring_count);
		ss_msg_hdr.report_id = i;
		ss_msg_hdr.timestamp = time(NULL);
		ss_msg_hdr.cli_addr = 0;
		ss_msg_hdr.proto_id = 13;

		if (p_proto_process(ss_msg_hdr, &ustring_count) != 0) {
			fprintf(stderr, "ERROR: p_proto_process: report_id: %d ustring_count: %s i: %d\n", ss_msg_hdr.report_id, ustring_count, i);
			goto ERROR;
		}
	}
*/
	
	for (int i = 0; i != 10; ++i) {
		ss_msg_hdr.len = sizeof(ss_msg_hdr) + sizeof(ustring_count);
		ss_msg_hdr.report_id = i;
		ss_msg_hdr.timestamp = time(NULL);
		ss_msg_hdr.cli_addr = 0;
		ss_msg_hdr.proto_id = 26;

		if (p_proto_process(ss_msg_hdr, p_ustring_value) != 0) {
			fprintf(stderr, "ERROR: p_proto_process: report_id: %d ustring_count: %s i: %d\n", ss_msg_hdr.report_id, ustring_count, i);
			goto ERROR;
		}
	}

	if (p_proto_uninit() != 0) {
		ERROR_LOG("ERROR: proto_init");
		goto ERROR;
	}

	p_timer->uninit();
	p_timer->release();

	p_config->uninit();
	p_config->release();

	dlclose(p_handle);
	p_handle = NULL;

	return 0;

ERROR:
	if (p_handle != NULL) {
		dlclose(p_handle);
		p_handle = NULL;
	}

	return -1;
}


#ifndef ASYNC_SERVER_CONFIG_H
#define ASYNC_SERVER_CONFIG_H

bool load_config_file(const char *file_name);
extern "C" int config_get_intval(const char *key, int defult);
extern "C" const char *config_get_strval(const char *key, const char *defult);

bool load_msgid_file(const char *file_name);
extern "C" uint32_t config_get_msgid(const char *key, int val);

#endif

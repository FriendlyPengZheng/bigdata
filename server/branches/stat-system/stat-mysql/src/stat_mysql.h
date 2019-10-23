#ifndef STAT_MYSQL_H
#define STAT_MYSQL_H

int stat_mysql_init(i_timer *p_timer, i_config *p_config);
int stat_mysql_process(const server_db_request_t *req);
int get_proto_id(uint32_t *p_proto_id, int *proto_count);
int stat_mysql_uninit();

#endif

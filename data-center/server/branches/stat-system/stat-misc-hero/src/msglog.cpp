#include <assert.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/fcntl.h>
#include <sys/mman.h>
#include <sys/signal.h>
#include <sys/stat.h>
#include <errno.h>
#include "misc_utils.h"
#include "log.h"
#include "msglog.h"
//#include "c_mysql_connect_auto_ptr.h"


extern std::string local_ip;
enum {
	buf_size	= 4096
};

static char sql[1024];
int shootao_log(shootao_data_t *p_shootao_data, uint32_t timestamp, c_mysql_connect_auto_ptr* mysql)
{
	//判断传入数据是否为空
	if(p_shootao_data == NULL)
	{
		return -1;
	}
	//判断game_id是否为数字
	if(!is_numeric(p_shootao_data->game_id))
	{
		ERROR_LOG("game is not numeric:%s", p_shootao_data->game_id);
		return -1;
	}
	//判断step是否为数字
	if(!is_numeric(p_shootao_data->step))
	{
		ERROR_LOG("step is not numeric:%s", p_shootao_data->step);
		return -1;
	}
	//判断flag是否为数字
	if(!is_numeric(p_shootao_data->flag))
	{
		ERROR_LOG("flag is not numeric:%s", p_shootao_data->flag);
		return -1;
	}

	//数据入库
	int gid = atoi(p_shootao_data->game_id);
	int step = atoi(p_shootao_data->step);
	int flag = atoi(p_shootao_data->flag);
	sprintf(sql, "insert into t_report_info set game_id=%d,step=%d,flag=%d,ip='%s'", gid, step, flag, p_shootao_data->ip);
	mysql->do_sql(sql);
	return 0;
}

int msglog(const char* logfile, uint32_t type, uint32_t timestamp, const void* data, int len)
{
	char buf[buf_size];
    message_header_t *h;
    int fd, s;

	s = sizeof(message_header_t)+len;

	assert((len >= 0) && (s >= (int)sizeof(message_header_t)) && (s <= buf_size));

	h = (message_header_t*)buf;

	memset(h, 0, sizeof(*h));

    h->len = s;
    h->hlen = sizeof(message_header_t);
    h->type = type;
    h->timestamp = timestamp;

    if(len>0) memcpy((char *)(h+1), data, len);

    signal(SIGXFSZ, SIG_IGN);
    fd = open(logfile, O_WRONLY|O_APPEND, 0666);
    if(fd<0) 
    {
        fd = open(logfile, O_WRONLY|O_APPEND|O_CREAT, 0666);
        int ret=fchmod(fd,0777);
        if ((ret!=0)||(fd<0))
        {
            return -1;
        }
    }

    write(fd, (char *)h, s);
    close(fd);

    signal(SIGXFSZ, SIG_DFL);
    return 0;
}

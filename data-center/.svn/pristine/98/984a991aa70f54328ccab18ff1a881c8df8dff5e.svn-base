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


extern std::string local_ip;
enum {
	buf_size	= 4096
};

int tongji_log(tongji_data_t *p_tongji_data, uint32_t timestamp)
{
	if(p_tongji_data == NULL)
	{
		return -1;
	}
	//判断game_id是否为数字
	if(!is_numeric(p_tongji_data->game_id))
	{
		ERROR_LOG("gameid is not numeric:%s", p_tongji_data->game_id);
		return -1;
	}


	char filename[1024] = {0};
	//filename:10000_game_custom_1404357780
	sprintf(filename, "/opt/taomee/stat/data/inbox/%s_game_custom_%u", p_tongji_data->game_id, timestamp/20*20);
	
	//因功夫派转码方式的问题，先临时将功夫派的数据写到别的目录下
//	if(atoi(p_tongji_data->game_id)  == 6)
//	{
//		sprintf(filename, "/opt/taomee/stat/data/gf/%s_game_custom_%u", p_tongji_data->game_id, timestamp/60*60);
//
//	}
	//以只写的方式打开文件，写入的数据以附加的形式放到文件后面，若文件不存在则创建，设置最高权限
	int fd = open(filename, O_WRONLY|O_APPEND|O_CREAT, 0777);
	if(fd < 0)
	{
		ERROR_LOG("open file %s failed[%s].", filename, strerror(errno));
		return -1;
	}

	char msg_content[2048] = {0};
	if(strlen(p_tongji_data->uid) == 0)
	{
		sprintf(p_tongji_data->uid, "0");
	}

	int msg_len = 0;
	if(strlen(p_tongji_data->item) == 0)
	{//先将IP写死
        msg_len = sprintf(msg_content, "_hip_=%s\t_stid_=%s\t_sstid_=%s\t_gid_=%s\t_zid_=%s\t_sid_=%s\t_pid_=%s\t_ts_=%u\t_acid_=%s\t_plid_=%s\n", local_ip.c_str(), p_tongji_data->stid, p_tongji_data->sstid, p_tongji_data->game_id, p_tongji_data->zid, p_tongji_data->sid, p_tongji_data->pid, timestamp, p_tongji_data->uid, p_tongji_data->plid);
	}
	else
	{
        msg_len = sprintf(msg_content, "_hip_=%s\t_stid_=%s\t_sstid_=%s\t_gid_=%s\t_zid_=%s\t_sid_=%s\t_pid_=%s\t_ts_=%u\t_acid_=%s\t_plid_=%s\titem=%s\t_op_=item:item\n", local_ip.c_str(), p_tongji_data->stid, p_tongji_data->sstid, p_tongji_data->game_id, p_tongji_data->zid, p_tongji_data->sid, p_tongji_data->pid, timestamp, p_tongji_data->uid, p_tongji_data->plid, p_tongji_data->item);
	}

	write(fd, msg_content, msg_len);
	close(fd);
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

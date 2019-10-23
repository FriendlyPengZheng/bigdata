#include <sys/time.h>
#include "c_mysql_operator.h"
#include "log.h"
#include "global.h"
#include <stdlib.h>
#include <time.h>
#include <stdio.h>

#include "utils.h"

//保存数据库消息
typedef struct {
    char     db_host[16];
    char     db_user[64];
    char     db_pwd[64];
    uint32_t db_port;
    c_mysql_connect_auto_ptr* p_mc;
} db_info_t;
static db_info_t db_info[100];

//数据库名前缀 db_stat_config.t_db_info.db_name字段中 id之前的字符串 (db_stat_result_  db_stat_report_) 
const char * db_prename[2];

c_mysql_operator::c_mysql_operator()
{
    inited = false;
    for(int i=0; i<2; i++) {
        db[i].init();
        db_range_min[i] = 0;
        db_range_max[i] = 0;
    }
}

c_mysql_operator::~c_mysql_operator()
{
    if(inited) {
        uninit();
    }
}

uint32_t c_mysql_operator::init()
{
    //读配置库信息
    if(db_stat_config.init(config_get_strval("db_config_host", "192.168.71.68"),
            config_get_strval("db_config_user", "srvmgr"),
            config_get_strval("db_config_passwd", "srvmgr@pwd"),
            config_get_strval("db_config_db", "db_stat_config"),
            config_get_intval("db_config_port", 3306), CLIENT_INTERACTIVE) != 0) {
        ERROR_LOG("db_stat_config.init() failed");
        return 3;
    }

    //db_config_name  配置文件中 有哪些db信息是需要从db_stat_config.t_db_info中读取的 
    const char * db_config_name[] = {"db_result_name", "db_report_name"};
    //db_default_name  默认的db名前缀
    const char * db_default_name[] = {"db_stat_result_", "db_stat_report_"};
    //db_name   输出错误提示用
    const char * db_name[] = {"db_result", "db_report"};

    char sql_buf[4096];
    MYSQL_ROW row;
    uint32_t db_info_index;

    for(uint32_t i=0; i<sizeof(db)/sizeof(db[0]); i++) {
        db_info_index = 0;
        //读取db名前缀
        db_prename[i] = config_get_strval(db_config_name[i], db_default_name[i]);

        //根据db名前缀 得到每个db的id号
        //并算出min_id与max_id 就可以推导出从rp/rs_id 计算得到db_id的线性公式
        //这段代码也可以不用 推导公式可以直接写死
        snprintf(sql_buf, sizeof(sql_buf), "select min(cast(replace(db_name,'%s','') as signed integer)) as min,max(cast(replace(db_name,'%s','') as signed integer)) as max,count(*) from t_db_info where db_name like '%s%%'", db_prename[i], db_prename[i], db_prename[i]);
        if(db_stat_config.do_sql(sql_buf) != 0) {
            ERROR_LOG("get %s range failed", db_name[i]);
            return 4;
        } else {
            if((row = db_stat_config.get_next_row()) == NULL) {
                ERROR_LOG("get %s range return NULL", db_name[i]);
                return 5;
            } else {
                db_range_min[i] = atol(row[0]);
                db_range_max[i] = atol(row[1]);

                if(db_range_max[i] - db_range_min[i] + 1 != atol(row[2])) {
                    ERROR_LOG("%s : %u-%u+1 != %lu", db_name[i], db_range_max[i], db_range_min[i], atol(row[2]));
                    return 6;
                }
            }
        }

        //读取每个db的信息并保存
        snprintf(sql_buf, sizeof(sql_buf), "select db_host, db_user, db_pswd, db_name, db_port from t_db_info where db_name like '%s%%'", db_prename[i]);
        if(db_stat_config.do_sql(sql_buf) != 0) {
            ERROR_LOG("%s info return failed", db_name[i]);
            return 7;
        }

        if(db_stat_config.get_selected_cnt() != (db_range_max[i] - db_range_min[i] + 1)) {
            ERROR_LOG("%s select[%u] less than %u", db_name[i], db_stat_config.get_selected_cnt(), db_range_max[i] - db_range_min[i] + 1);
            return 8;
        }

        uint32_t cnt = 0;
        uint32_t j=0;
        while((row = db_stat_config.get_next_row()) != NULL) {
            for(j=0; j<db_info_index; j++) {
                if(strcmp(db_info[j].db_host, row[0]) == 0 &&
                        strcmp(db_info[j].db_user, row[1]) == 0 &&
                        strcmp(db_info[j].db_pwd, row[2]) == 0 &&
                        db_info[j].db_port == atol(row[4]))
                    break;
            }
            if(j == db_info_index) {
                if(db[i].insert(db_range_min[i]+cnt, row[0], row[1], row[2], row[3], atol(row[4]), CLIENT_INTERACTIVE) != 0) {
                    ERROR_LOG("insert %s%u failed", db_prename[i], db_range_min[i]+cnt);
                    return 9;
                }
                strcpy(db_info[j].db_host, row[0]);
                strcpy(db_info[j].db_user, row[1]);
                strcpy(db_info[j].db_pwd, row[2]);
                db_info[j].db_port = atol(row[4]);
                db_info[j].p_mc = db[i].get(db_range_min[i]+cnt);
                db_info_index++;
            } else {
                db[i].insert(db_range_min[i]+cnt, db_info[j].p_mc);
            }
            cnt++;
        }

        if(cnt != (db_range_max[i] - db_range_min[i] + 1)) {
            ERROR_LOG("cnt[%u] != %u", cnt, (db_range_max[i] - db_range_min[i] + 1));
            return 10;
        }
    }

    //snprintf(sql_buf, sizeof(sql_buf), "%s\0", "select id from t_result where operator=9");
    //if(db_stat_config.do_sql(sql_buf) != 0) {
    //取history类型的result_id 只有history类型的数据在结果库中的time字段是存的YYYYMMDDHHII，其它都是YYMMDDHHII
    if(db_stat_config.do_sql("select id from t_result where operator=9") != 0) {
        ERROR_LOG("get result operator failed");
        return 11;
    }
    while((row = db_stat_config.get_next_row()) != NULL) {
        rs_operator.insert(std::pair<uint32_t, uint8_t>(atol(row[0]), 1));
    }

    inited = true;
    return 0;
}

uint32_t c_mysql_operator::uninit()
{
    db_stat_config.uninit();
    for(uint32_t i=0; i<sizeof(db)/sizeof(db[0]); i++) {
        db[i].uninit();
    }
    rs_operator.clear();
    inited = false;
    return 0;
}

#define FULL_TIME   (0)
#define SHORT_TIME  (1)

const uint8_t time_type[] = {YYMMDDHHII, YYMMDD};
const char * op_fun[] = {"max", "min", "sum", "avg"};

//timeval _tBegin, _tEnd, _tDiff;
//
//bool _SubTimeval(timeval &result, timeval &begin, timeval &end)
//// 计算gettimeofday函数获得的end减begin的时间差，并将结果保存在result中。
//{
//    if ( begin.tv_sec>end.tv_sec ) return false;
//
//    if ( (begin.tv_sec == end.tv_sec) && (begin.tv_usec > end.tv_usec) )   
//        return   false;
//
//    result.tv_sec = ( end.tv_sec - begin.tv_sec );   
//    result.tv_usec = ( end.tv_usec - begin.tv_usec );   
//
//    if (result.tv_usec<0) {
//        result.tv_sec--;
//        result.tv_usec+=1000000;}  
//
//        return true;
//}

/**
 *     @fn  get
 *  @brief  取数据接口
 *
 *  @param  op_type  操作类型：sum，max，min，avg，ucount，dlast(一段时间内的最后一条记录)，last(end之前的最后一条数据)
 *  @param  type     id类型：rs/rp
 *  @param  id       id
 *  @param  start    起始时间戳
 *  @param  end      结束时间戳
 *  @param  gap      间隔，隔多久取一个数据，即sum,max,min,avg等的时间跨度，ucount和last忽略此值
 *  @param  *time    返回值
 *  @param  *value   返回值
 * @return  0-success, else-failed
 */
int32_t c_mysql_operator::get(uint8_t op_type, uint8_t type, uint32_t id, uint32_t start, uint32_t end, uint32_t gap, uint32_t* time, double* value)
{
    //check parameter
    if(type != RS && type != RP) {
        ERROR_LOG("undefined type[%u]", type);
        return -1;
    }
    if(time == NULL || value == NULL || end < start || ((op_type != DLAST && op_type != LAST) && gap == 0)) {
        ERROR_LOG("error parameter : time[%p], value[%p], end[%u], start[%u], op_type[%u], gap[%u]", time, value, end, start, op_type, gap);
        return -2;
    }

    //get time type
    uint8_t t_time;
    if(type == RP) {
        t_time = FULL_TIME;
    } else {
        t_time = rs_operator.find(id)!=rs_operator.end()?FULL_TIME:SHORT_TIME;
    }

    //get result/report_db connect pointer
    uint32_t db_cnt;
    c_mysql_connect_auto_ptr* p_db;

    if(op_type != UCOUNT) {
        db_cnt = db_range_max[type] - db_range_min[type] + 1;
        p_db = db[type].get((id / 100) % db_cnt + db_range_min[type]);
        if(p_db == NULL) {
            ERROR_LOG("get %s db connect pointer failed", type==RS?"result":"report");
            return -3;
        }
    } else {
        p_db = &db_stat_config;
    }

    char sql_buf[4096*5] = {0};
    uint32_t ret = 0;
    MYSQL_ROW row;
    //gettimeofday(&_tBegin, 0);
    uint32_t i;

    switch(op_type) {
    case MAX:
    case MIN:
    case SUM:
    case AVG:
		for(i=start; i<=end; ) {
			sql_buf[0] = 0;
			for(int j=0; j<30 && i<=end; j++) {
				sprintf(sql_buf + strlen(sql_buf), "(select min(time),%s(value) from %s%u.t_%s_%u where time>=%u and time<%u and id=%u) union all ", op_fun[op_type], db_prename[type], (id/100)%db_cnt+db_range_min[type], type==RS?"result":"report", id%100, get_string_time(i, time_type[t_time]), get_string_time(i+gap, time_type[t_time]), id);
				i += gap;
			}
			sql_buf[strlen(sql_buf)-10] = 0;
			if(p_db->do_sql(sql_buf) == 0) {
				while((row = p_db->get_next_row()) != NULL) {
					if(row[0] != NULL && row[1] != NULL) {
						time[ret] = get_time_of_string(atol(row[0]));
						value[ret] = atof(row[1]);
#ifdef DEBUG
						DEBUG_LOG("%u %u %u %f", i, ret, time[ret], value[ret]);
#endif
						ret++;
					}
				}
			}
		}
		break;
    case DLAST:
        for(i=start; i<=end; ) {
            sql_buf[0] = 0;
            for(int j=0; j<30 && i<=end; j++) {
                sprintf(sql_buf + strlen(sql_buf), "(select time, value from %s%u.t_%s_%u where time>=%u and time<%u and id=%u order by time desc limit 1) union all ", db_prename[type], (id/100)%db_cnt+db_range_min[type], type==RS?"result":"report", id%100, get_string_time(i, time_type[t_time]), get_string_time(i+gap, time_type[t_time]), id);
                i += gap;
            }
            sql_buf[strlen(sql_buf)-10] = 0;
            DEBUG_LOG("%s", sql_buf);
            if(p_db->do_sql(sql_buf) == 0) {
                while((row = p_db->get_next_row()) != NULL) {
                    if(row[0] != NULL && row[1] != NULL) {
                        time[ret] = get_time_of_string(atol(row[0]));
                        value[ret] = atof(row[1]);
//#ifdef DEBUG
                        DEBUG_LOG("%u %u %u %f", i, ret, time[ret], value[ret]);
//#endif
                        ret++;
                    }
                }
            }
        }
        break;
    case UCOUNT:
        gap = 86400;
        for(i=start; i<=end; i+=gap) {
            snprintf(sql_buf, sizeof(sql_buf), "select day, count from t_ucount where report_id=%u and day=%u", id, i/86400);
            if(p_db->do_sql(sql_buf) == 0) {
                row = p_db->get_next_row();
                if(row != NULL && row[0] != NULL && row[1] != NULL) {
                    time[ret] = atol(row[0])*86400;
                    value[ret] = atof(row[1]);
                    ret++;
                }
            }
        }
        break;
    case LAST:
        snprintf(sql_buf, sizeof(sql_buf), "select time, value from %s%u.t_%s_%u where time>=%u and time<%u and id=%u order by time desc limit 1", db_prename[type], (id/100)%db_cnt+db_range_min[type], type==RS?"result":"report", id%100, get_string_time(start, time_type[t_time]), get_string_time(end, time_type[t_time]), id);
        if(p_db->do_sql(sql_buf) == 0) {
            row = p_db->get_next_row();
            if(row != NULL && row[0] != NULL && row[1] != NULL) {
                time[ret] = get_time_of_string(atol(row[0]));
                value[ret] = atof(row[1]);
                ret++;
            }
            else {
                DEBUG_LOG("row == NULL ");
            }
        }
        else {
            DEBUG_LOG("do sql != 0");
        }
        break;
    default:
        ERROR_LOG("undefined op_type[%u]", op_type);
        return -4;
    }
    
    //gettimeofday(&_tEnd, 0);
    //_SubTimeval(_tDiff, _tBegin, _tEnd);
    //DEBUG_LOG("process mysql %lu.%06lu", _tDiff.tv_sec, _tDiff.tv_usec); 

    return ret;
}

/**
 *     @fn  set
 *  @brief  写结果库
 *
 *  @param  id    rs_id
 *  @param  cnt   time:value对个数
 *  @param  data  time:value存放的内存首地址
 * @return  0-success, else-failed
 */
uint32_t c_mysql_operator::set(uint32_t id, uint32_t cnt, const char * data)
{
    const time_value_t * set_tv_t = (const time_value_t *)data;
    uint8_t t_time = rs_operator.find(id)!=rs_operator.end()?FULL_TIME:SHORT_TIME;

    uint32_t db_cnt = db_range_max[RS] - db_range_min[RS] + 1;
    c_mysql_connect_auto_ptr* p_db = db[RS].get((id / 100) % db_cnt + db_range_min[RS]);
    if(p_db == NULL) {
        ERROR_LOG("get result db connect pointer failed");
        return -3;
    }

    char sql_buf[4096*5];
    for(uint32_t i=0; i<cnt; i++) {
        sql_buf[0] = 0;
        sprintf(sql_buf + strlen(sql_buf), "insert into %s%u.t_result_%u set id=%u, time=%u, value=%f ON DUPLICATE KEY UPDATE value=%f", db_prename[RS], (id/100) % db_cnt+db_range_min[RS], id%100, id, get_string_time(set_tv_t[i].time, time_type[t_time]), set_tv_t[i].value, set_tv_t[i].value);
        if(p_db->do_sql(sql_buf) != 0) {
            ERROR_LOG("error do sql");
            return 1;
        }
    }

    return 0;
}


#include <stdlib.h>
#include "mysql_operator.h"
#include "c_mysql_connect_auto_ptr.h"
#include "log.h"
#include "proto.h"
#include <string.h>

static char sql[1024];

uint32_t getGPZS(c_mysql_connect_auto_ptr* mysql, uint32_t g, int32_t p,int32_t z, int32_t s)
{
    //返回gpzs_id , 为0的话是错误
    sprintf(sql, "select gpzs_id from t_gpzs_info where game_id=%u and platform_id=%d and zone_id=%d and server_id=%d", g, p, z, s);
    uint32_t ret = mysql->do_sql(sql);
    if(ret != 0) {
        return 0;
    }

    MYSQL_ROW row = mysql->get_next_row();
    if(row == NULL) {
        return 0;
    }

    ret = atoi(row[0]);
    return ret;
}

uint32_t insertGPZS(c_mysql_connect_auto_ptr* mysql, uint32_t g, int32_t p,int32_t z, int32_t s)
{
    sprintf(sql, "insert into t_gpzs_info set game_id=%u,platform_id=%d,zone_id=%d,server_id=%d,gpzs_name=\"%s\"", g, p, z, s, getGPZSName(p, z, s));
    mysql->do_sql(sql);
    return getGPZS(mysql, g, p, z, s);
}

//uint32_t insertTree(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid, const char* sstid)
//{
//    uint32_t ret;
//    //1.查找此节点是否已经存在
//    if((ret = getSstidNode(mysql, g, stid, sstid)) != 0) {
//        return ret;
//    }
//    //2.查找stid节点是否存在,不存在则插入
//    if((ret = getStidNode(mysql, g, stid)) == 0) {
//        //有错误，应该能获取到stid的节点id
//        ERROR_LOG("can not get stid[%s.%s] node for game[%u]", stid, sstid, g);
//        return 0;
//    }
//    //3.插入sstid节点
//    return insertSstidNode(mysql, g, stid, sstid, ret);
//}
//
//uint32_t insertSstidNode(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid, const char* sstid, uint32_t parent)
//{
//    uint32_t ret;
//    sprintf(sql, "insert into t_web_tree set node_name=\"%s\",game_id=%u,parent_id=%u,is_leaf=1,is_basic=%u,stid=\"%s\",sstid=\"%s\"", sstid, g, parent, sstid[0]=='_'?1:0, stid, sstid);
//    if((ret = mysql->do_sql(sql)) != 0) {
//        return 0;
//    }
//    return getSstidNode(mysql, g, stid, sstid);
//}
//
//uint32_t getSstidNode(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid, const char* sstid)
//{
//    uint32_t ret;
//    MYSQL_ROW row;
//    sprintf(sql, "select node_id from t_web_tree where game_id=%u and stid=\"%s\" and sstid=\"%s\"", g, stid, sstid);
//    if((ret = mysql->do_sql(sql)) != 0 
//            || (row = mysql->get_next_row()) == NULL) {
//        return 0;
//    } else {
//        return atoi(row[0]);
//    }
//}
//
//uint32_t getStidNode(c_mysql_connect_auto_ptr* mysql, uint32_t g, const char* stid)
//{
//    uint32_t ret;
//    MYSQL_ROW row;
//    sprintf(sql, "select node_id from t_web_tree where game_id=%u and stid=\"%s\" and sstid=''", g, stid);
//    if((ret = mysql->do_sql(sql)) != 0) {
//        return 0;
//    }
//    if((row = mysql->get_next_row()) == NULL) {
//        //stid节点不存在，则插入
//        sprintf(sql, "insert into t_web_tree set node_name=\"%s\", game_id=%u, is_leaf=0, is_basic=%d, stid=\"%s\"", stid, g, stid[0]=='_'?1:0, stid);
//        if((ret = mysql->do_sql(sql)) != 0) {
//            return 0;
//        }
//        sprintf(sql, "select node_id from t_web_tree where game_id=%u and stid=\"%s\" and sstid=''", g, stid);
//        if((ret = mysql->do_sql(sql)) != 0) {
//            return 0;
//        }
//        if((row = mysql->get_next_row()) == NULL) {
//            return 0;
//        } else {
//            return atoi(row[0]);
//        }
//    }
//    return atoi(row[0]);
//}

//getNodeId 获取node_id
//调用此函数，说明在t_report_info中没有，需要新加一个叶子节点
uint32_t getNodeId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field)
{
    if(strcmp(stid, "msgid_") == 0) {
        return 575921;// xml上传的数据，不新建节点
    }
    uint32_t ret, node_id;
    MYSQL_ROW row;
    sprintf(sql, "select node_id from t_report_info where game_id=%u and stid=\"%s\" and node_id != 0 order by node_id desc limit 1", g, stid);
    if((ret = mysql->do_sql(sql)) != 0) {
        return 0;
    }
    if((row = mysql->get_next_row()) == NULL) {
        //stid是新的，新建树节点
        if((node_id = createNodeId(mysql, stid, g, 0, 0, stid[0]=='_'?1:0)) == 0) {
            return 0;
        }
        //建叶子节点
        return createNodeId(mysql, sstid, g, node_id, 1, stid[0]=='_'?1:0);
    }
    //stid不是新的
    sprintf(sql, "select node_id from t_report_info where game_id=%u and stid=\"%s\" and sstid=\"%s\" and node_id != 0 order by node_id desc limit 1", g, stid, sstid);
    if((ret = mysql->do_sql(sql)) != 0) {
        return 0;
    }
    if((row = mysql->get_next_row()) == NULL) {
        //sstid是新的
        //stid op_type op_field
        sprintf(sql, "select node_id from t_report_info where game_id=%u and stid=\"%s\" and sstid!=\"%s\" and op_fields=\"%s\" and op_type=%u and node_id != 0 group by node_id", g, stid, sstid, field, op + 1);
        if((ret = mysql->do_sql(sql)) != 0) {
            return 0;
        }
        //所有完成任务人数的放在一个节点下面
        if((row = mysql->get_next_row()) != NULL) {
            if(mysql->get_selected_cnt() != 1) {
                sprintf(sql, "select parent_id from t_web_tree where node_id = (select max(node_id) from t_report_info where game_id=%u and stid=\"%s\" and op_fields=\"%s\" and op_type=%u)", g, stid, field, op + 1);
                if((ret = mysql->do_sql(sql)) != 0 || (row = mysql->get_next_row()) == NULL) {//不应该走到这个分支，到这里说明有孤立节点
                    sprintf(sql, "select parent_id from t_web_tree where node_id in (select node_id from t_report_info where game_id=%u and stid=\"%s\" and op_fields=\"%s\" and op_type=%u) limit 1", g, stid, field, op + 1);
                    if((ret = mysql->do_sql(sql)) != 0 || (row = mysql->get_next_row()) == NULL) {
                        return 0;
                    }
                }
                return createNodeId(mysql, sstid, g, atoi(row[0]), 1, stid[0]=='_'?1:0);
            } else {
                //stid下面只有一个节点
                int node_id = atoi(row[0]);
                sprintf(sql, "select count(distinct(sstid)) from t_report_info where game_id=%u and stid=\"%s\" and node_id=%u", g, stid, node_id);
                if((ret = mysql->do_sql(sql)) != 0 || (row = mysql->get_next_row()) == NULL) {
                    return 0;
                }
                if(atoi(row[0]) != 1) {
                    //这个节点下有多个sstid 所有sstid都合并在这个节点下面
                    return node_id;
                } else {
                    //默认为每个sstid新建一个节点
                    sprintf(sql, "select parent_id from t_web_tree where node_id = %u", node_id);
                    if(mysql->do_sql(sql) != 0 || (row = mysql->get_next_row()) == NULL) {
                        return 0;
                    }
                    return createNodeId(mysql, sstid, g, atoi(row[0]), 1, stid[0]=='_'?1:0);
                }
            }
        }
        //stid不是新的，一定可以找到
        sprintf(sql, "select parent_id from t_web_tree where node_id = (select max(node_id) from t_report_info where game_id=%u and stid=\"%s\" and sstid!=\"%s\")", g, stid, sstid);
        if((ret = mysql->do_sql(sql)) != 0 || (row = mysql->get_next_row()) == NULL) {
            return 0;
        }
        return createNodeId(mysql, sstid, g, atoi(row[0]), 1, stid[0]=='_'?1:0);
        //ERROR_LOG("can not be here game=%u stid=%s sstid=%s op_fields=%s op_type=%u", g, stid, sstid, field, op);
        //return 0;
    } else {
        //sstid不是新的
        //stid op_field
        sprintf(sql, "select node_id from t_report_info where game_id=%u and stid=\"%s\" and sstid=\"%s\" and op_fields=\"%s\" and node_id != 0 order by node_id desc limit 1", g, stid, sstid, field);
        if((ret = mysql->do_sql(sql)) != 0) {
            return 0;
        }
        if((row = mysql->get_next_row()) != NULL) {
            return atoi(row[0]);
        }
        //stid op_type
        sprintf(sql, "select node_id from t_report_info where game_id=%u and stid=\"%s\" and sstid=\"%s\" and op_type='%d' and node_id != 0 order by node_id desc limit 1", g, stid, sstid, op + 1);
        if((ret = mysql->do_sql(sql)) != 0) {
            return 0;
        }
        if((row = mysql->get_next_row()) != NULL) {
            return atoi(row[0]);
        }
        //stid 
        sprintf(sql, "select node_id from t_report_info where game_id=%u and stid=\"%s\" and sstid=\"%s\" and node_id != 0 order by node_id desc limit 1", g, stid, sstid);
        if((ret = mysql->do_sql(sql)) != 0) {
            return 0;
        }
        if((row = mysql->get_next_row()) != NULL) {
            return atoi(row[0]);
        }
        ERROR_LOG("can not be here game=%u stid=%s sstid=%s op_field=%s op_type=%u", g, stid, sstid, field, op);
        return 0;
    }
    ERROR_LOG("can not be here game=%u stid=%s sstid=%s op_field=%s op_type=%u", g, stid, sstid, field, op);
    return 0;
}

uint32_t createNodeId(c_mysql_connect_auto_ptr* mysql, const char* node_name, uint32_t g, uint32_t parent, uint8_t leaf, uint8_t basic)
{
    uint32_t ret;
    MYSQL_ROW row;
    sprintf(sql, "insert into t_web_tree set node_name=\"%s\",game_id=%u,parent_id=%u,is_leaf=%u,is_basic=%u",
                node_name, g, parent, leaf, basic);
    if((ret = mysql->do_sql(sql)) != 0) {
        return 0;
    }
    if((ret = mysql->do_sql("select LAST_INSERT_ID()")) != 0) {
        return 0;
    }
    if((row = mysql->get_next_row()) == NULL) {
        return 0;
    }
    return atoi(row[0]);
}

uint32_t insertReport(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field)
{
    uint32_t ret;
    if((ret = getReportId(mysql, g, op, stid, sstid, field)) != 0) {
        return ret;
    }
    if((ret = getNodeId(mysql, g, op, stid, sstid, field)) == 0) {
        ERROR_LOG("get node id error : game=%u op=%u stid=%s sstid=%s field=%s", g, op, stid, sstid, field);
        return 0;
    }
    if((ret = insertReportId(mysql, g, op, stid, sstid, field, ret)) != 0) {
        return 0;
    }
    if((ret = mysql->do_sql("select LAST_INSERT_ID()")) != 0) {
        return 0;
    }
    MYSQL_ROW row = mysql->get_next_row();
    if(row == NULL) {
        return 0;
    } else {
        return atoi(row[0]);
    }
}

uint32_t getReportId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field)
{
    sprintf(sql, "select report_id from t_report_info where game_id=%u and stid=\"%s\" and sstid=\"%s\" and op_fields=\"%s\" and op_type=%u", g, stid, sstid, field, op + 1);
    if(mysql->do_sql(sql) != 0) {
        return 0;
    }
    MYSQL_ROW row = mysql->get_next_row();
    if(row != NULL) {
        return atoi(row[0]);
    } else {
        return 0;
    }
}

uint32_t insertReportId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field, uint32_t node_id)
{
    if(op > MAX_OP) {
        ERROR_LOG("unexcepted op_type[%u]", op);
        return 0;
    }
    sprintf(sql, "insert into t_report_info set report_name=\"%s\",game_id=%u,stid=\"%s\",sstid=\"%s\",op_fields=\"%s\",op_type=%u,is_multi=%u,node_id=%u", getReportName(op, stid, sstid, field), g, stid, sstid, field, op+1, getMulti(op, field), node_id);
    return mysql->do_sql(sql);
}

uint8_t getMulti(uint8_t op, const char* field)
{
    if(op > SET)    return 1;
    if(op == UCOUNT || op == COUNT) {
        if(strlen(field) != 0) {
            return 1;
        } else {
            return 0;
        }
    }
    if(strchr(field, ',') != 0) {
        return 1;
    } else {
        return 0;
    }
}

const char* getReportName(uint8_t op, const char* stid, const char* sstid, const char* field)
{
    static char buf[1024];
    if(field[0] != 0) {
        sprintf(buf, "%s%s%s", sstid, field, getOpName(op));
    } else {
        sprintf(buf, "%s%s", sstid, getOpName(op));
    }
    return buf;
}

uint32_t insertData(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint8_t op, const char* stid, const char* sstid, const char* field, const char* key)
{
    uint32_t ret, r_id;
    if((ret = getReportId(mysql, g, op, stid, sstid, field)) == 0) {
        return 0;
    }
    r_id = ret;
    uint32_t order = 0;
    uint32_t tmp_order = 0;
    char tmp_name[256];
    char tmp_char;
    const char* range = getRange(op, field, key);
    if(sscanf(range, "%d:%s", &tmp_order, tmp_name) == 2) {
        range = tmp_name;
        order = tmp_order;
    } else if(sscanf(range, "%d", &order) == 1 ||
            sscanf(range, "%c%d", &tmp_char, &tmp_order) == 2 ||
            sscanf(range, "%c%c%d", &tmp_char, &tmp_char, &tmp_order) == 3) {
        order = tmp_order;
    }
    sprintf(sql, "select data_id from t_data_info where r_id=%u and type='report' and range=\"%s\"", r_id, range);

    if((ret = mysql->do_sql(sql)) != 0) {
        return 0;
    }
    MYSQL_ROW row = mysql->get_next_row();
    if(row == NULL) {
        //没有找到data_id，需要新插入一个
        if(strlen(range) == 0) {
            sprintf(sql, "insert into t_data_info set data_name=CONCAT(\"%s\", \"%s\"), r_id=%d,type='report',range=\"%s\",sthash=%u", range, getOpName(op), r_id, range, getHash(stid));
        } else {
            sprintf(sql, "insert into t_data_info set data_name=CONCAT(\"%s\", \"%s\"), r_id=%d,type='report',range=\"%s\",sthash=%u,display_order=%u", range, getOpName(op), r_id, range, getHash(stid), order);
        }
        if((ret = mysql->do_sql(sql)) != 0) {
            return 0;
        }
        if((ret = mysql->do_sql("select LAST_INSERT_ID()")) != 0) {
            return 0;
        }
        MYSQL_ROW row = mysql->get_next_row();
        if(row == NULL) {
            return 0;
        } else {
            DEBUG_LOG("[%s] id = %s", sql, row[0]);
            sprintf(sql, "update t_data_info set modify_time = add_time where data_id = %s", row[0]);
            mysql->do_sql(sql);
            return atoi(row[0]);
        }
    } else {
        return atoi(row[0]);
    }
}

const char* getRange(uint8_t op, const char* field, const char*key)
{
    static char buf[1] = { 0 };
    if(op == UCOUNT || op == COUNT) {
        if(strlen(field) == 0) {
            return buf;
        } else {
            return key;
        }
    }
    if(op <= SET) {
        if(strchr(field, ',') != 0) {
            return key;
        } else {
            return buf;
        }
    }
    if(op == IP_DISTR) {
        return key;
    }
    //等级分布
    //TODO:item类型的等级分布
    return key;
}

uint32_t getHash(const char* p)
{
    uint32_t h = 0;
    while (*p) {
        h = h * 11 + (*p << 4) + (*p >> 4);
        p++;
    }
    return h;
}

const char* getOpName(uint8_t op)
{
    switch(op) {
        case UCOUNT:
            return "人数";
        case COUNT:
            return "人次";
        case SUM:
            return "求和";
        case MAX:
            return "最大";
        case SET:
            return "";
        case DISTR_SUM:
        case DISTR_MAX:
        case DISTR_SET:
            //return "分布";
            return "";
        case IP_DISTR:
            return "地区分布";
        default :
            return "未知类型";
    }
}

const char* getGPZSName(int32_t p, int32_t z, int32_t s)
{
    static char name[1024];
    name[0] = 0;
    if(z == -1 && s == -1) {
        if(p == -1) {
            sprintf(name, "全游戏");
        } else {
            sprintf(name, "%d平台", p);
        }
    }

    if(z != -1) {
        sprintf(name, "%d区", z);
    }
    if(s == -1) {
        //sprintf(&name[strlen(name)], "全服");
    } else {
        sprintf(&name[strlen(name)], "%d服", s);
    }
    return name;
}

uint32_t getTask(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t, const char* range)
{
    uint32_t r;
    uint32_t order = 0;
    uint32_t tmp_order = 0;
    char tmp_name[256];
    char tmp_char;
    if(sscanf(range, "%d:%s", &tmp_order, tmp_name) == 2) {
        range = tmp_name;
        order = tmp_order;
    } else if(sscanf(range, "%d", &order) == 1 ||
            sscanf(range, "%c%d", &tmp_char, &tmp_order) == 2 ||
            sscanf(range, "%c%c%d", &tmp_char, &tmp_char, &tmp_order) == 3) {
        order = tmp_order;
    }
    if((r = getCommonResultId(mysql, g, t)) != 0) {
        return getData(mysql, r, range);
    }
    return 0;
}

uint32_t getData(c_mysql_connect_auto_ptr* mysql, uint32_t r, const char* range)
{
    sprintf(sql, "select data_id from t_data_info where r_id=%u and type='result' and range=\"%s\"", r, range);
    uint32_t ret = mysql->do_sql(sql);
    if(ret != 0) {
        return 0;
    }

    MYSQL_ROW row = mysql->get_next_row();
    if(row == NULL) {
        return 0;
    }

    ret = atoi(row[0]);
    return ret;
}

uint32_t insertTask(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t, const char* range)
{
    static char sthash[256];
    snprintf(sthash, 256, "%u;%u", g, t);
    uint32_t r_id, r;
    char tmp_name[256];
    char tmp_char;
    int32_t order = 0;
    int32_t tmp_order = 0;
    //解析区间和显示顺序
    if(sscanf(range, "%d:%s", &tmp_order, tmp_name) == 2) {
        range = tmp_name;
        order = tmp_order;
    } else if(sscanf(range, "%d", &order) == 1 ||
            sscanf(range, "%c%d", &tmp_char, &tmp_order) == 2 ||
            sscanf(range, "%c%c%d", &tmp_char, &tmp_char, &tmp_order) == 3) {
        order = tmp_order;
    }
    if((r_id = getCommonResultId(mysql, g, t)) == 0) {
        if((r_id = insertCommonResultId(mysql, g, t)) == 0) {
            return 0;
        }
    } else {
        if((r = getData(mysql, r_id, range)) != 0) {
            return r;
        }
    }
    //得到了result_id,并且data_id不存在
    sprintf(sql, "insert into t_data_info set data_name=CONCAT((select task_name from t_common_task where task_id=%u), \"%s\"), r_id=%u, type='result', range=\"%s\", display_order = %d, sthash=%u",
            t, range, r_id, range, order, getHash(sthash));
    r = mysql->do_sql(sql);
    if(r != 0) {
        return 0;
    }
    if((r = mysql->do_sql("select LAST_INSERT_ID()")) != 0) {
        return 0;
    }
    MYSQL_ROW row;
    if((row = mysql->get_next_row()) == NULL) {
        return 0;
    }
    return atoi(row[0]);
}

uint32_t getCommonResultId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t)
{
    sprintf(sql, "select result_id from t_common_result where game_id=%u and task_id=%u", g, t);
    uint32_t ret = mysql->do_sql(sql);
    if(ret != 0) {
        return 0;
    }

    MYSQL_ROW row = mysql->get_next_row();
    if(row == NULL) {
        return 0;
    }

    ret = atoi(row[0]);
    return ret;
}

uint32_t insertCommonResultId(c_mysql_connect_auto_ptr* mysql, uint32_t g, uint32_t t)
{
    //TODO 检查task_id的合法性
    uint32_t ret;
    sprintf(sql, "insert into t_common_result set result_name=(select task_name from t_common_task where task_id = %u), game_id=%u, task_id=%u",
            t, g, t);
    if((ret = mysql->do_sql(sql)) != 0) {
        return 0;
    }
    if((ret = mysql->do_sql("select LAST_INSERT_ID()")) != 0) {
        return 0;
    }
    MYSQL_ROW row;
    if((row = mysql->get_next_row()) == NULL) {
        return 0;
    }
    return atoi(row[0]);
}

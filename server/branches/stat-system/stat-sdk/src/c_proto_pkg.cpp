#include  "c_proto_pkg.h"
#include  "log.h"

#include <stdlib.h>
#include <string.h>

/**
 *     @fn  c_proto_pkg
 *  @brief  构造函数
 *
 *  @param  无
 * @return  无
 */
c_proto_pkg::c_proto_pkg()
{
    p_recv_get_info = (recv_get_info_t*)buf_recv;
    p_ret_get_info = (ret_get_info_t*)buf_ret;

    p_recv_ucount_info = (recv_ucount_info_t*)buf_recv;
    p_ret_ucount_info = (ret_ucount_info_t*)buf_ret;

    p_recv_set_info = (recv_set_info_t*)buf_recv;
    p_recv_uexpr_info = (recv_uexpr_info_t*)buf_recv;

    p_recv_id_info = NULL;
    p_ret_id_info = NULL;//p_ret_get_info->ret_id_info;
}

/**
 *     @fn  ~c_proto_pkg
 *  @brief  析构函数
 *
 *  @param  无
 * @return  无
 */

c_proto_pkg::~c_proto_pkg()
{

}

/**
 *     @fn  recv_pkg
 *  @brief  接受请求包
 *
 *  @param  请求包的缓冲区指针
 * @return  success-0,failed-1
 */
uint32_t c_proto_pkg::recv_pkg(const char * buf)
{
    if(buf == NULL)
    {
        ERROR_LOG("buf == NULL");
        return 1;
    }
    pkg_len = ((recv_get_info_t*)buf)->pkg_len;
    if(pkg_len > sizeof(this->buf_recv))
    {
        ERROR_LOG("pkg_len[%u] > %lu", pkg_len, sizeof(this->buf_recv));
        return 2;
    }
    memcpy(this->buf_recv, buf, pkg_len);
    p_recv_id_info = p_recv_get_info->recv_id_info;
    done_len = sizeof(recv_get_info_t);
    done_cnt = 0;
#ifdef DEBUG
    DEBUG_LOG("pkg_len = %u", p_recv_get_info->pkg_len);
    DEBUG_LOG("cmd_id  = 0x%08X", p_recv_get_info->cmd_id);
    DEBUG_LOG("id_cnt  = %u", p_recv_get_info->id_cnt);
    for(uint32_t i=0; i<p_recv_get_info->id_cnt; i++)
    {
        DEBUG_LOG("  id[%u]    = %u", i, p_recv_id_info[i].id);
        DEBUG_LOG("  type[%u]  = %s", i, p_recv_id_info[i].type==0?"rs":p_recv_id_info[i].type==1?"rp":"unknown");
        DEBUG_LOG("  start[%u] = %u", i, p_recv_id_info[i].start_time);
        DEBUG_LOG("  end[%u]   = %u", i, p_recv_id_info[i].end_time);
        DEBUG_LOG("  gap[%u]   = %u", i, p_recv_id_info[i].gap_time);
    }

    //DEBUG_LOG("set_id = %u", p_recv_set_info->id);
    //DEBUG_LOG("set_time = %u", p_recv_set_info->time);
    //DEBUG_LOG("set_value = %f", p_recv_set_info->value);
#endif
    start_pack();
    return 0;
}

/**
 *     @fn  get_next_id_info
 *  @brief  获得下一个rs/rp_id的信息
 *
 *  @param  无
 * @return  success-下一个rs/rp_id的信息的首地址，failed-NULL
 */
const recv_id_info_t * c_proto_pkg::get_next_id_info()
{
    if(done_cnt >= p_recv_get_info->id_cnt)
    {
        //ERROR_LOG("done_cnt[%u] >= p_recv_get_info->id_cnt[%d]", done_cnt, p_recv_get_info->id_cnt);
        p_recv_id_info = NULL;
        return NULL;
    }
    if(done_len + sizeof(recv_id_info_t) > pkg_len)
    {
        ERROR_LOG("done_len[%u] + sizeof(recv_id_info_t)[%lu] > pkg_len[%u]", done_len, sizeof(recv_id_info_t), pkg_len);
        p_recv_id_info = NULL;
        return NULL;
    }
#ifdef DEBUG
    DEBUG_LOG("ret = %p", p_recv_id_info);
    DEBUG_LOG("id[%u]    = %u", done_cnt, p_recv_id_info->id);
    DEBUG_LOG("type[%u]  = %s", done_cnt, p_recv_id_info->type==0?"rs":p_recv_id_info->type==1?"rp":"unknown");
    DEBUG_LOG("start[%u] = %u", done_cnt, p_recv_id_info->start_time);
    DEBUG_LOG("end[%u]   = %u", done_cnt, p_recv_id_info->end_time);
    DEBUG_LOG("gap[%u]   = %u", done_cnt, p_recv_id_info->gap_time);
#endif
    done_cnt++;
    done_len += sizeof(recv_id_info_t);
    return p_recv_id_info++;
}

/**
 *     @fn  start_pack
 *  @brief  开始打包返回包
 *
 *  @param  无
 * @return  success-0,failed-1
 */
uint32_t c_proto_pkg::start_pack()
{
    memset(buf_ret, 0, sizeof(buf_ret));
    p_ret_get_info = (ret_get_info_t *)buf_ret;
    p_ret_id_info = p_ret_get_info->ret_id_info;
    p_ret_get_info->pkg_len = sizeof(ret_get_info_t);
    p_ret_get_info->id_cnt = 0;
    
    p_ret_ucount_info = (ret_ucount_info_t*)buf_ret;
    mimi_id = p_ret_ucount_info->mimi_id;
    p_ret_ucount_info->mimi_cnts = 0;

    p_ret_uexpr_info = (ret_uexpr_info_t*)buf_ret;

    return 0;
}

/**
 *     @fn  pop_id
 *  @brief  将一个rs/rp_id信息打包
 *
 *  @param  uint32_t id:rs/rp_id, uint8_t:type,rs==1,rp==2
 * @return  success-0,failed-1
 */
uint32_t c_proto_pkg::pop_id(uint32_t id, uint8_t type)
{
    if(p_ret_get_info->pkg_len + sizeof(ret_id_info_t) > sizeof(buf_ret))
    {
        ERROR_LOG("p_ret_get_info->pkg_len[%u] + sizeof(ret_id_info_t)[%lu] > sizeof(buf_ret)[%lu]", p_ret_get_info->pkg_len, sizeof(ret_id_info_t), sizeof(buf_ret));
        return 1;
    }
    p_ret_get_info->id_cnt++;
    p_ret_id_info = (ret_id_info_t *)(buf_ret + p_ret_get_info->pkg_len);
    p_ret_id_info->id = id;
    p_ret_id_info->type = type;
    p_ret_id_info->tv_cnt = 0;
    p_time_value = p_ret_id_info->time_value;
    p_ret_get_info->pkg_len += sizeof(ret_id_info_t);

    return 0;
}

/**
 *     @fn  pop_tv
 *  @brief  将一个(time,vaule)对打包
 *
 *  @param  uint32_t time:时间，double value:值
 * @return  success-0,failed-1
 */
uint32_t c_proto_pkg::pop_tv(uint32_t time, double value)
{
    if(p_ret_get_info->pkg_len + sizeof(time_value_t) > sizeof(buf_ret))
    {
        ERROR_LOG("p_ret_get_info->pkg_len[%u] + sizeof(time_value_t)[%lu]> sizeof(buf_ret)[%lu]", p_ret_get_info->pkg_len, sizeof(time_value_t), sizeof(buf_ret));
        return 1;
    }
    p_ret_id_info->tv_cnt++;
    p_time_value->time = time;
    p_time_value->value = value;
    p_time_value++;
    p_ret_get_info->pkg_len += sizeof(time_value_t);
    return 0;
}

/**
 *     @fn  pop_mimiid
 *  @brief  打包米米号列表
 *
 *  @param  uint32_t mimiid:米米号
 * @return  success-0,failed-1
 */
uint32_t c_proto_pkg::pop_mimiid(uint32_t mimiid)
{
    if(p_ret_ucount_info->pkg_len + sizeof(mimiid) > sizeof(buf_ret))
    {
        ERROR_LOG("p_ret_ucount_info->pkg_len[%u] + sizeof(mimiid)[%lu] > sizeof(buf_ret)[%lu]", p_ret_ucount_info->pkg_len, sizeof(mimiid), sizeof(buf_ret));
        return 1;
    }
    *mimi_id = mimiid;
    mimi_id++;
    p_ret_ucount_info->mimi_cnts++;
    p_ret_ucount_info->pkg_len += sizeof(mimiid);
    return 0;
}

/**
 *     @fn  ret_pkg
 *  @brief  获取返回包
 *
 *  @param  无
 * @return  success-返回包首地址,failed-NULL
 */
const char * c_proto_pkg::ret_pkg()
{
//#ifdef DEBUG
//    DEBUG_LOG("pkg_len = %u", p_ret_get_info->pkg_len);
//    DEBUG_LOG("cmd_id = 0x%08X", p_ret_get_info->cmd_id);
//    DEBUG_LOG("result = %u", p_ret_get_info->result);
//    DEBUG_LOG("id_cnt = %u", p_ret_get_info->id_cnt);
//    ret_id_info_t * p_tmp = p_ret_get_info->ret_id_info;
//    for(uint32_t i=0; i<p_ret_get_info->id_cnt; i++)
//    {
//        DEBUG_LOG("      id[%u] = %u", i, p_tmp[i].id);
//        DEBUG_LOG("    type[%u] = %s", i, p_tmp[i].type==0?"rs":p_tmp[i].type==1?"rp":"unknown");
//        DEBUG_LOG("  tv_cnt[%u] = %u", i, p_tmp[i].tv_cnt);
//        for(uint32_t j=0; j<p_tmp[i].tv_cnt; j++)
//        {
//            DEBUG_LOG("     time[%u] = %u", j, p_tmp[i].time_value[j].time);
//            DEBUG_LOG("    vaule[%u] = %f", j, p_tmp[i].time_value[j].value);   
//        }
//    }
//#endif
    return buf_ret; 
}

/**
 *     @fn  set_uexpr_error
 *  @brief  设置uexpr错误提示
 *
 *  @param  const char * string: 错误提示
 * @return  void
 */

void c_proto_pkg::set_uexpr_error(const char * string)
{
    strcpy(p_ret_uexpr_info->error, string);
    p_ret_uexpr_info->pkg_len = sizeof(ret_uexpr_info_t) + strlen(string) + 1;
}

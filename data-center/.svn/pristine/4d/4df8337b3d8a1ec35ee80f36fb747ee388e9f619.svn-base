/**
 * =====================================================================================
 *       @file  sdk_server.cpp
 *      @brief  
 *
 *     Created  2015-10-27 17:57:45
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "sdk_server.hpp"
#include "string_utils.hpp"

#include <arpa/inet.h>

#define RET_ERR(fd, ret, cmd, value) do { \
        ret.mutable_ret()->set_ret(value); \
        int length = ret.ByteSize(); \
        char ret_buf[length+sizeof(data_t)]; \
        ret.SerializePartialToArray(ret_buf+sizeof(data_t), length); \
        data_t* d = (data_t*)ret_buf; \
        d->total_len = length+sizeof(data_t); \
        d->cmd = cmd; \
        net_send_cli(fd, ret_buf, length+sizeof(data_t)); \
        ERROR_LOG("cmd 0x%04x ret 0x%04x", cmd, value); \
        return ; \
} while(0)

#define RET_OK(fd, ret, cmd) do { \
        ret.mutable_ret()->set_ret(0); \
        int length = ret.ByteSize(); \
        char ret_buf[length+sizeof(data_t)]; \
        ret.SerializePartialToArray(ret_buf+sizeof(data_t), length); \
        data_t* d = (data_t*)ret_buf; \
        d->total_len = length+sizeof(data_t); \
        d->cmd = cmd; \
        net_send_cli(fd, ret_buf, length+sizeof(data_t)); \
        return ; \
} while(0)

#define GET_STR(recv, value) (recv.has_##value() ? recv.value() : "")
#define GET_INT(recv, value) (recv.has_##value() ? recv.value() : 0)
#define GET_BOL(recv, value) (recv.has_##value() ? recv.value() : false)
#define GET_STRUCT(recv, value) (recv.has_##value() ? str_##value[recv.value()] : "")

#define NO_GAME          (0x0001)
#define NO_USER          (0x0002)
#define NO_GAME_GAMEID   (0x0003)
#define NO_USER_UID      (0x0004)
#define NO_OLTIME        (0x0005)
#define NO_OLCNT         (0x0006)
#define NO_LEVEL         (0x0007)
#define NO_PAY_AMOUNT    (0x0008)
#define NO_PAY_UNIT      (0x0009)
#define NO_CURRENCY_TYPE (0x000a)
#define NO_PAY_REASON    (0x000b)
#define NO_OUTCOME       (0x000c)
#define NO_PAY_CHANNEL   (0x000d)
#define NO_AMT           (0x000e)
#define NO_PAY_AMT       (0x000f)
#define NO_REASON        (0x0010)
#define NO_TASKTYPE      (0x0011)
#define NO_TASKNAME      (0x0012)
#define NO_SPIRIT        (0x0013)
#define NO_TRANSSTEP     (0x0014)

#define NO_STID          (0x0101)
#define NO_SSTID         (0x0102)
#define OP_ERR           (0x0103)
#define NO_CUSTOM_KEY    (0x0104)
#define NO_CUSTOM_VALUE  (0x0105)

static char str_zone[][10] = {
    "", "电信", "网通", "总在线"
};

SdkServer::SdkServer() {
    init();
};

SdkServer::~SdkServer() {
    uninit();
};

int SdkServer::init() {
    getStatLogger(2,-1,-1);
    getStatLogger(5,-1,-1);
    getStatLogger(6,-1,-1);
    getStatLogger(10,-1,-1);
    getStatLogger(16,-1,-1);
    return 0;
}

int SdkServer::uninit() {
    for(std::map<string, StatLogger*>::iterator it = m_stat_logger_map.begin();
            it != m_stat_logger_map.end();
            it++) {
        if(it->second != NULL) {
            delete it->second;
            it->second = NULL;
        }
    }
    return 0;
}

int SdkServer::get_server_pkg_len(const char *buf, uint32_t len) {
    return 0;
}

void SdkServer::client_connected(int fd, uint32_t ip) {

}

void SdkServer::client_disconnected(int fd) {

}

void SdkServer::server_disconnected(int fd) {

}

void SdkServer::timer_event() {

}

void SdkServer::process_client_pkg(int fd, const char*buf, uint32_t len) {
    const data_t *data = (const data_t*)buf;
    uint32_t body_len = len - sizeof(data_t);
    switch(data->cmd) {
        case 0xf001:
            do_0xf001(fd, data->proto_body, body_len);
            break;
        case 0xf002:
            do_0xf002(fd, data->proto_body, body_len);
            break;
        case 0xf003:
            do_0xf003(fd, data->proto_body, body_len);
            break;
        case 0xf004:
            do_0xf004(fd, data->proto_body, body_len);
            break;
        case 0xf005:
            do_0xf005(fd, data->proto_body, body_len);
            break;
        case 0xf006:
            do_0xf006(fd, data->proto_body, body_len);
            break;
        case 0xf007:
            do_0xf007(fd, data->proto_body, body_len);
            break;
        case 0xf008:
            do_0xf008(fd, data->proto_body, body_len);
            break;
        case 0xf009:
            do_0xf009(fd, data->proto_body, body_len);
            break;
        case 0xf00a:
            do_0xf00a(fd, data->proto_body, body_len);
            break;
        case 0xf00b:
            do_0xf00b(fd, data->proto_body, body_len);
            break;
        case 0xf00c:
            do_0xf00c(fd, data->proto_body, body_len);
            break;
        case 0xf00d:
            do_0xf00d(fd, data->proto_body, body_len);
            break;
        case 0xf00e:
            do_0xf00e(fd, data->proto_body, body_len);
            break;
        case 0xf00f:
            do_0xf00f(fd, data->proto_body, body_len);
            break;
        case 0xf010:
            do_0xf010(fd, data->proto_body, body_len);
            break;
        case 0xf100:
            do_0xf100(fd, data->proto_body, body_len);
            break;
        default:
            ERROR_LOG("unknown cmd 0x%x", data->cmd);
    }
}

void SdkServer::process_server_pkg(int fd, const char*buf, uint32_t len) {

}

StatLogger* SdkServer::getStatLogger(uint32_t gameid, int zoneid, int serverid) {
    std::stringstream keystream;
    keystream << gameid << "_" << zoneid << "_" << serverid;
    string key = keystream.str();
    std::map<string, StatLogger*>::iterator it = m_stat_logger_map.find(key);
    if(it != m_stat_logger_map.end()) {
        return it->second;
    } else {
        StatLogger *new_logger = new StatLogger(gameid, zoneid, serverid);
        m_stat_logger_map.insert(std::pair<string, StatLogger*>(key, new_logger));
        return new_logger;
    }
}

void SdkServer::do_0xf001(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf001;
    statlogger::cs_0xF001_veri_pass_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF001_veri_pass_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    uint32_t client_ip = 0;
    if(recv.has_client_ip()) {
        client_ip = ntohl(inet_addr(recv.client_ip().c_str()));
    }

    statlogger->verify_passwd(
            acct_id,
            client_ip,
            GET_STR(recv, ads_id),
            GET_STR(recv, browse),
            GET_STR(recv, device),
            GET_STR(recv, os),
            GET_STR(recv, resolution),
            GET_STR(recv, network),
            GET_STR(recv, isp)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf002(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf002;
    statlogger::cs_0xF002_reg_role_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF002_reg_role_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    string player_id;
    if(uid.has_pid()) {
        StatCommon::digittostr(uid.pid(), player_id);
    }

    uint32_t client_ip = 0;
    if(recv.has_client_ip()) {
        client_ip = ntohl(inet_addr(recv.client_ip().c_str()));
    }

    statlogger->reg_role(
            acct_id,
            player_id,
            GET_STR(recv, race),
            client_ip,
            GET_STR(recv, ads_id),
            GET_STR(recv, browse),
            GET_STR(recv, device),
            GET_STR(recv, os),
            GET_STR(recv, resolution),
            GET_STR(recv, network),
            GET_STR(recv, isp)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf003(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf003;
    statlogger::cs_0xF003_login_online_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF003_login_online_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    string player_id;
    if(uid.has_pid()) {
        StatCommon::digittostr(uid.pid(), player_id);
    }

    uint32_t client_ip = 0;
    if(recv.has_client_ip()) {
        client_ip = ntohl(inet_addr(recv.client_ip().c_str()));
    }

    statlogger->login_online(
            acct_id,
            player_id,
            GET_STR(recv, race),
            GET_BOL(recv, isvip),
            GET_INT(recv, level),
            client_ip,
            GET_STR(recv, ads_id),
            GET_STRUCT(recv, zone),
            GET_STR(recv, browse),
            GET_STR(recv, device),
            GET_STR(recv, os),
            GET_STR(recv, resolution),
            GET_STR(recv, network),
            GET_STR(recv, isp)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf004(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf004;
    statlogger::cs_0xF004_logout_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF004_logout_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_oltime()) {
        RET_ERR(fd, ret, cmd, NO_OLTIME);
    }

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    //logout(string acct_id, bool isvip, int lv, int oltime)
    statlogger->logout(
            acct_id,
            GET_BOL(recv, isvip),
            GET_INT(recv, level),
            recv.oltime()
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf005(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf005;
    statlogger::cs_0xF005_online_count_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF005_online_count_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    statloggercommon::game_info_t game = recv.game();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!recv.has_olcnt()) {
        RET_ERR(fd, ret, cmd, NO_OLCNT);
    }

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->online_count(
            recv.olcnt(),
            GET_STRUCT(recv, zone)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf006(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf006;
    statlogger::cs_0xF006_level_up_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF006_level_up_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_level()) {
        RET_ERR(fd, ret, cmd, NO_LEVEL);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->level_up(
            acct_id,
            GET_STR(recv, race),
            recv.level()
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf007(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf007;
    statlogger::cs_0xF007_pay_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF007_pay_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_pay_amount()) {
        RET_ERR(fd, ret, cmd, NO_PAY_AMOUNT);
    }

    if(!recv.has_pay_unit()) {
        RET_ERR(fd, ret, cmd, NO_PAY_UNIT);
    }

    if(!recv.has_currency_type()) {
        RET_ERR(fd, ret, cmd, NO_CURRENCY_TYPE);
    }

    if(!recv.has_pay_reason()) {
        RET_ERR(fd, ret, cmd, NO_PAY_REASON);
    }

    if(!recv.has_outcome()) {
        RET_ERR(fd, ret, cmd, NO_OUTCOME);
    }

    if(!recv.has_pay_channel()) {
        RET_ERR(fd, ret, cmd, NO_PAY_CHANNEL);
    }

    int unit = 1;
    switch(recv.pay_unit()) {
        case statlogger::YUAN:
            unit = 1;
            break;
        case statlogger::JIAO:
            unit = 10;
            break;
        case statlogger::FEN:
            unit = 100;
            break;
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    string outcome;
    StatCommon::digittostr(recv.outcome(), outcome);

    string pay_channel;
    StatCommon::digittostr(recv.pay_channel(), pay_channel);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->pay(
            acct_id,
            GET_BOL(recv, isvip),
            recv.pay_amount() * unit,
            (StatLogger::CurrencyType)recv.currency_type(),
            (StatLogger::PayReason)recv.pay_reason(),
            outcome,
            recv.outcnt(),
            pay_channel
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf008(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf008;
    statlogger::cs_0xF008_free_golds_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF008_free_golds_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_amt()) {
        RET_ERR(fd, ret, cmd, NO_AMT);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->obtain_golds(
            acct_id,
            recv.amt()
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf009(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf009;
    statlogger::cs_0xF009_buy_item_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF009_buy_item_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_pay_amt()) {
        RET_ERR(fd, ret, cmd, NO_PAY_AMT);
    }

    if(!recv.has_outcome()) {
        RET_ERR(fd, ret, cmd, NO_OUTCOME);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->buy_item(
            acct_id,
            GET_BOL(recv, isvip),
            GET_INT(recv, lv),
            recv.pay_amt(),
            recv.outcome(),
            GET_INT(recv, outcnt)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf00a(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf00a;
    statlogger::cs_0xF00A_buy_other_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF00A_buy_other_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_pay_amt()) {
        RET_ERR(fd, ret, cmd, NO_PAY_AMT);
    }

    if(!recv.has_reason()) {
        RET_ERR(fd, ret, cmd, NO_REASON);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->use_golds(
            acct_id,
            GET_BOL(recv, isvip),
            recv.reason(),
            recv.pay_amt()*100,
            GET_INT(recv, lv)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf00b(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf00b;
    statlogger::cs_0xF00B_accept_task_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF00B_accept_task_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_type()) {
        RET_ERR(fd, ret, cmd, NO_TASKTYPE);
    }

    if(!recv.has_name()) {
        RET_ERR(fd, ret, cmd, NO_TASKNAME);
    }
    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->accept_task(
            StatLogger::TaskType(recv.type()),
            acct_id,
            recv.name(),
            GET_INT(recv, lv)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf00c(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf00c;
    statlogger::cs_0xF00C_finish_task_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF00C_finish_task_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_type()) {
        RET_ERR(fd, ret, cmd, NO_TASKTYPE);
    }

    if(!recv.has_name()) {
        RET_ERR(fd, ret, cmd, NO_TASKNAME);
    }
    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->finish_task(
            StatLogger::TaskType(recv.type()),
            acct_id,
            recv.name(),
            GET_INT(recv, lv)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf00d(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf00d;
    statlogger::cs_0xF00D_abort_task_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF00D_abort_task_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_type()) {
        RET_ERR(fd, ret, cmd, NO_TASKTYPE);
    }

    if(!recv.has_name()) {
        RET_ERR(fd, ret, cmd, NO_TASKNAME);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->abort_task(
            StatLogger::TaskType(recv.type()),
            acct_id,
            recv.name(),
            GET_INT(recv, lv)
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf00e(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf00e;
    statlogger::cs_0xF00E_obtain_spirit_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF00E_obtain_spirit_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_spirit()) {
        RET_ERR(fd, ret, cmd, NO_SPIRIT);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->obtain_spirit(
            acct_id,
            GET_BOL(recv, isvip),
            GET_INT(recv, lv),
            recv.spirit()
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf00f(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf00f;
    statlogger::cs_0xF00F_lose_spirit_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF00F_lose_spirit_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_spirit()) {
        RET_ERR(fd, ret, cmd, NO_SPIRIT);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->lose_spirit(
            acct_id,
            GET_BOL(recv, isvip),
            GET_INT(recv, lv),
            recv.spirit()
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf010(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf010;
    statlogger::cs_0xF010_new_trans_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF010_new_trans_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_step()) {
        RET_ERR(fd, ret, cmd, NO_TRANSSTEP);
    }

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    statlogger->new_trans(
            StatLogger::NewTransStep(recv.step()),
            acct_id
            );

    RET_OK(fd, ret, cmd);
}

void SdkServer::do_0xf100(int fd, const char*buf, uint32_t len) {
    static uint32_t cmd = 0xf100;
    statlogger::cs_0xF100_custom_t recv;
    recv.ParseFromArray(buf, len);

    statlogger::sc_0xF100_custom_t ret;

    if(!recv.has_game()) {
        RET_ERR(fd, ret, cmd, NO_GAME);
    }

    if(!recv.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER);
    }

    statloggercommon::game_info_t game = recv.game();
    statloggercommon::user_id_t uid = recv.uid();

    if(!game.has_gameid()) {
        RET_ERR(fd, ret, cmd, NO_GAME_GAMEID);
    }

    if(!uid.has_uid()) {
        RET_ERR(fd, ret, cmd, NO_USER_UID);
    }

    if(!recv.has_stid()) {
        RET_ERR(fd, ret, cmd, NO_STID);
    }

    if(!recv.has_sstid()) {
        RET_ERR(fd, ret, cmd, NO_SSTID);
    }

    StatLogger* statlogger = getStatLogger(game.gameid(),
            game.has_zoneid() ? game.zoneid() : -1,
            game.has_serverid() ? game.serverid() : -1);

    string acct_id;
    StatCommon::digittostr(uid.uid(), acct_id);

    string player_id;
    if(uid.has_pid()) {
        StatCommon::digittostr(uid.pid(), player_id);
    }

    if(!recv.has_op()) {
        if(recv.has_item()) {
            StatInfo info;
            info.add_info("item", recv.item());
            info.add_op(StatInfo::op_item, "item");
            statlogger->log(
                    recv.stid(),
                    recv.sstid(),
                    acct_id,
                    player_id,
                    info
                    );

            RET_OK(fd, ret, cmd);
        } else {
            statlogger->log(
                    recv.stid(),
                    recv.sstid(),
                    acct_id,
                    player_id
                    );

            RET_OK(fd, ret, cmd);

        }
    } else {
        int op = recv.op();

        if(!recv.has_value()) {
            RET_ERR(fd, ret, cmd, NO_CUSTOM_VALUE);
        }

        StatInfo info;
        info.add_info(GET_STR(recv, key), recv.value());

        if(recv.has_item()) {
            info.add_info("item", recv.item());
            if(op == statlogger::SUM) {
                op = StatInfo::op_item_sum;
            } else if(op == statlogger::MAX) {
                op = StatInfo::op_item_max;
            } else if(op == statlogger::SET) {
                op = StatInfo::op_item_set;
            } else {
                RET_ERR(fd, ret, cmd, OP_ERR);
            }

            info.add_op((StatInfo::OpCode)op, "item", GET_STR(recv, key));
            statlogger->log(
                recv.stid(),
                recv.sstid(),
                acct_id,
                player_id,
                info
                );
        } else {
            if(op == statlogger::SUM) {
                op = StatInfo::op_sum;
            } else if(op == statlogger::MAX) {
                op = StatInfo::op_max;
            } else if(op == statlogger::SET) {
                op = StatInfo::op_set;
            } else {
                RET_ERR(fd, ret, cmd, OP_ERR);
            }

            info.add_op((StatInfo::OpCode)op, GET_STR(recv, key));
            statlogger->log(
                recv.stid(),
                recv.sstid(),
                acct_id,
                player_id,
                info
                );
        }
    }

    RET_OK(fd, ret, cmd);
}

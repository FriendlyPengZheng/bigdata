#include <cassert>
#include <cstdlib>
#include <map>
#include <stdexcept>
#include <sstream>
#include <utility>

#include <log.h>
#include <async_server.h>

#include "config.h"
#include "c_mysql_connect_auto_ptr.h"
#include "utils.h"
#include "proto2.h"

using namespace std;

struct RouteInfo {
	uint32_t tbl_start_num;
	uint32_t tbl_end_num;
	c_mysql_connect_auto_ptr* mysql;
};

ProtoHandler::ProtoHandler()
{
	m_buf.reserve(1024 * 100);
	m_auxbuf.reserve(100);
	for (int i = 0; i != sc_dbmgr_num; ++i) {
		m_dbmgr[i].init();
	}

	// map dbname to dbtype
	std::map<std::string, int>	dbtype;
	dbtype["db_stat_default_data"]    = 1;
	dbtype["db_stat_item_data"]       = 2;
	dbtype["db_stat_arithmetic_data"] = 3;
	dbtype["db_stat_distr_data"]      = 4;

	// connect to config_db
	c_mysql_connect_auto_ptr db_stat_config;
    if(db_stat_config.init(config_get_strval("db_config_host", ""),
            				config_get_strval("db_config_user", ""),
				            config_get_strval("db_config_passwd", ""),
				            config_get_strval("db_config_db", ""),
				            config_get_intval("db_config_port", 3306),
				            CLIENT_INTERACTIVE) != 0) {
		throw runtime_error(string("failed to connect to config db '")
								+ config_get_strval("db_config_host", "") + ":"
								+ config_get_strval("db_config_db", "") + "'");
    }

	// read route info from db and connect to mysql instances
	string sql = "SELECT * FROM t_sdk_route_info ORDER BY db_name;";
	if(db_stat_config.do_sql(sql.c_str()) != 0) {
		throw runtime_error(string("failed to read route info from table '") + "t_sdk_route_info'");
	}
	multimap<string, RouteInfo> mysql_map;
	MYSQL_ROW row = db_stat_config.get_next_row();
	while (row != 0) {
		RouteInfo route_info;
		route_info.tbl_start_num = atoi(row[1]);
		route_info.tbl_end_num = atoi(row[2]);
		route_info.mysql = new c_mysql_connect_auto_ptr();
		if (route_info.mysql->init(row[3], row[5], row[6], row[0],
									atoi(row[4]), CLIENT_INTERACTIVE) != 0) {
			throw runtime_error(string("failed to connect to db '") + row[0] + "'");
		}
		mysql_map.insert(make_pair(row[0], route_info));
		row = db_stat_config.get_next_row();
	}

	// init route table
	for (multimap<string, RouteInfo>::iterator it = mysql_map.begin();
			it != mysql_map.end(); ++it) {
		c_mysql_connect_mgr& dbmgr  = m_dbmgr[dbtype[it->first]];
		const RouteInfo& route_info = it->second;
		for (uint32_t i = route_info.tbl_start_num; i <= route_info.tbl_end_num; ++i) {
			dbmgr.insert(i, route_info.mysql);
		}
	}
}

void ProtoHandler::process(int fd, const void* data)
{
	const ProtoHeader* h = reinterpret_cast<const ProtoHeader*>(data);
	const ReqInfoArray* info_arr = reinterpret_cast<const ReqInfoArray*>(h->body);
	uint32_t expected_len = info_arr->id_cnt * sizeof(ReqInfo) + sizeof(ReqInfoArray) + sizeof(ProtoHeader);
	if ((h->len != expected_len) || (h->result != 0)) {
		EMERG_LOG("invalid packet: cmd=0x%X result=%u len=%u expected_len=%u",
					h->cmd, h->result, h->len, expected_len);
		net_close_cli(fd);
		return;
	}

	m_buf.clear();
	m_buf.append(reinterpret_cast<const char*>(h), sizeof(ProtoHeader)); // len will be replaced at the end
	RspInfoArray rsparr = { info_arr->id_cnt };
	m_buf.append(reinterpret_cast<const char*>(&rsparr), sizeof(RspInfoArray));
	for (uint32_t i = 0; i != info_arr->id_cnt; ++i) {
		const ReqInfo& info = info_arr->info[i];
		if (!chk_req_type(info.type) || !chk_id_type(info.idtype)) {
			EMERG_LOG("invalid reqtype or idtype: cmd=0x%X reqtype=%u idtype=%u",
						h->cmd, info.type, info.idtype);
			net_close_cli(fd);
			return;
		}

		m_buf.append(reinterpret_cast<const char*>(&info.zone_id), sizeof(info.zone_id));
		m_buf.append(reinterpret_cast<const char*>(&info.svr_id), sizeof(info.svr_id));
		m_buf.append(reinterpret_cast<const char*>(&info.type), sizeof(info.type));
		m_buf.append(reinterpret_cast<const char*>(&info.id), sizeof(info.id));
		m_buf.append(reinterpret_cast<const char*>(&info.idtype), sizeof(info.idtype));
		m_buf.append(reinterpret_cast<const char*>(info.column), sizeof(info.column));
		m_buf.append(reinterpret_cast<const char*>(&info.aux_key), sizeof(info.aux_key));

		uint32_t row_cnt = 0;
		string   sql     = get_sql(*h, info);
		c_mysql_connect_auto_ptr* mysql = m_dbmgr[info.idtype].get(info.id % 100);
		if (mysql->do_sql(sql.c_str()) == 0) {
			row_cnt = mysql->get_selected_cnt();
		}
		m_buf.append(reinterpret_cast<const char*>(&row_cnt), sizeof(row_cnt));

		MYSQL_ROW row  = mysql->get_next_row();
		while(row != 0) {
			TimeValue tv(get_time_of_string(atoi(row[0])));
			strncpy(tv.value, row[1], sizeof(tv.value));
			m_buf.append(reinterpret_cast<const char*>(&tv), sizeof(tv));

			row  = mysql->get_next_row();
        }
	}
	//repack packet len
	uint32_t len = m_buf.size();
	m_auxbuf.clear();
	m_auxbuf.append(reinterpret_cast<char*>(&len), sizeof(len));
	m_buf.replace(0, m_auxbuf.size(), m_auxbuf);

	net_send_cli(fd, m_buf.data(), len);
}

//-----------------------------------------------
// Private Methods
//
string ProtoHandler::get_sql(const ProtoHeader& h, const ReqInfo& info) const
{
	static string idname[] = { "", "", "event_id", "task_id" };
	static string auxkey[] = { "", "", "item_id", "", "range_low" };

	ostringstream oss;
	oss << "SELECT time, " << info.column << " FROM "
		<< get_tbl_name(info.id, info.idtype, info.type) << " WHERE "
		<< idname[info.type] << " = " << info.id << " AND "
		<< "server_id = " << info.svr_id << " AND time >= "
		<< get_string_time(info.start_time, YYYYMMDD) << " AND time <= "
		<< get_string_time(info.end_time, YYYYMMDD) << " AND zone_id = "
		<< info.zone_id;
	if (auxkey[info.idtype].size()) {
		oss << " AND " << auxkey[info.idtype] << " = " << info.aux_key;
	}
	oss << " ORDER BY time ASC;";

	return oss.str();
}

string ProtoHandler::get_tbl_name(uint32_t id, uint32_t idtype, uint32_t reqtype) const
{
	static string db_type[]  = { "", "default", "item", "arithmetic", "distr" };
	static string tbl_type[] = { "", "", "source", "task" };

	ostringstream oss;
	oss << "t_" << db_type[idtype] << '_' << tbl_type[reqtype] << '_' << (id % 100);

	return oss.str();
}


/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-server服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <string_utils.hpp>
#include "statlog_custom_sender.hpp"

//bool StatLogCustomSender::parse_filename(const string& fn, string& filetype, time_t& ts) const
//{
	//vector<string> fn_parts;

    //StatCommon::split(fn, '_', fn_parts);
	//if (fn_parts.size() == 4)
    //{
        //filetype = fn_parts[2];
        //if(!filetype.empty() && is_valid_filetype(filetype))
        //{
			//return true;
		//}
	//}

	//return false;
//}
StatLogCustomSender::StatLogCustomSender() : m_sendtodb_auto(1), m_rename_trigger(400)
{
    m_traffic_control = true;
    m_items_type = StatLogItemsParser::SLI_CUSTOM;
}

int StatLogCustomSender::init()
{
    StatLogSender::init();

    m_sendtodb_auto = StatCommon::stat_config_get("custom-sendtodb-auto", 0); 
    m_sendtodb_auto = (m_sendtodb_auto == 0) ? 0 : 1;
    DEBUG_LOG("init custom sendtodb_auto as %d", m_sendtodb_auto);

    m_rename_trigger = StatCommon::stat_config_get("custom-rename-trigger", 400);
    m_rename_trigger = std::max(m_rename_trigger, 400); 
    m_rename_trigger = std::min(m_rename_trigger, 1500); 
    DEBUG_LOG("init custom rename_trigger as %d", m_rename_trigger);

    return 0;
}

bool StatLogCustomSender::sanity_check_file(const StatLogFile& slf) const
{
    if(slf.get_file_type() == StatLogFile::SLFT_CUSTOM)
        return true;
    else
        return false;
}

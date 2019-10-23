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
#include "statlog_basic_sender.hpp"

//bool StatLogBasicSender::parse_filename(const string& fn, string& filetype, time_t& ts) const
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

bool StatLogBasicSender::sanity_check_file(const StatLogFile& slf) const
{
    if(slf.get_file_type() == StatLogFile::SLFT_BASIC)
        return true;
    else
        return false;
}

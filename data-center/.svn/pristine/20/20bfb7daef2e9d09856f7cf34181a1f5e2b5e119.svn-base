/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  bennyjiao<bennyjiao@taomee.com>
 *   @date    2013-05-08
 * =====================================================================================
 */

#ifndef  STAT_ALARMER_VERIFIER_HPP
#define  STAT_ALARMER_VERIFIER_HPP

#include <stat_config.hpp>
#include "stat_common.hpp"

using std::string;

class StatAlarmerVerifier
{
	private:
		string m_passwd;
	public:
		StatAlarmerVerifier()
		{
			StatCommon::stat_config_get("app_static_passwd", m_passwd);
		}
		~StatAlarmerVerifier()
		{}
		bool verify(const string& taomee_id, const string& passwd)
		{
			return m_passwd == passwd;
		}
};

#endif  /*STAT_ALARMER_VERIFIER_HPP*/

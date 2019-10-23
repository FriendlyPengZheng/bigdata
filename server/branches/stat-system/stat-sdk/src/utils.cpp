#include <cassert>
#include <ctime>

#include <log.h>

#include "utils.h"

//将时间戳转换成可读形式的时间  1354245856 => 121130 / 1211301124
uint32_t get_string_time(uint32_t timestamp, uint8_t type)
{
    time_t time = (time_t)timestamp;
    struct tm * timeinfo = localtime(&time);
    uint32_t ret = 0;

	switch (type) {
	case YYMMDD:
		ret = timeinfo->tm_mday + (timeinfo->tm_mon * 100 + 100) + ((timeinfo->tm_year+1900)%100) * 10000;
		break;
	case YYYYMMDD:
		ret = timeinfo->tm_mday + (timeinfo->tm_mon * 100 + 100) + ((timeinfo->tm_year+1900)%100) * 10000;
		ret += 20000000;
		break;
	case YYMMDDHHII:
		ret = timeinfo->tm_min + timeinfo->tm_hour * 100 + timeinfo->tm_mday * 10000 + (timeinfo->tm_mon * 1000000 + 1000000) + ((timeinfo->tm_year+1900)%100) * 100000000;
		break;
	default:
		assert("invalid type!");
	}

    return ret;
}

//将可读形式的时间转换成时间戳  1211301124 => 1354245856
uint32_t get_time_of_string(uint32_t time)
{
    struct tm tm;
    time_t r;
	if (time > 20000000 && time < 30000000) { // YYYYMMDD
		time -= 20000000;
	}
    if(10101 <= time && time <= 991231) { //YYMMDD
        time *= 10000;
    }
    if(101010000 <= time && time <= 9912312359) { //YYMMDDHHII
        tm.tm_min = time % 100;
        time /= 100;
        if(tm.tm_min >= 60)
            return 0;
        tm.tm_hour = time % 100;   
        time /= 100;
        if(tm.tm_hour >= 24)
            return 0;
        tm.tm_mday = time % 100;
        time /= 100;
        if(0 == tm.tm_mday || tm.tm_mday >= 32)
            return 0;
        tm.tm_mon = time % 100 - 1;
        time /= 100;
        if(tm.tm_mon >= 12)
            return 0;
        tm.tm_year = time + 100;
        tm.tm_sec = 0;                   
        tm.tm_isdst = 0;                 
        r = mktime(&tm);                 
        return r > 0 ? (uint32_t)r : 0;
    } else {
        return 0;
    }
}


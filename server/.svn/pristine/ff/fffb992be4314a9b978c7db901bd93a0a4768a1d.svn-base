#ifndef STAT_CALC_CONFIG_H
#define STAT_CALC_CONFIG_H

#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <map>
#include <string>
#include "log.h"

using std::map;
using std::string;

class StatCalcConfig
{
    public:
        StatCalcConfig(const char* file_name = "../conf/stat-calc.conf");

        void stat_config_get(const char* key, string& value)
        {
            if(key == NULL)
                return;

            value.clear();

            const char* c = config_get_strval(key, not_found);
            if(c == not_found)
                return;

            value = c;
        }

        inline void stat_config_get(const string& key, string& value)
        {   
            stat_config_get(key.c_str(), value);
        }   

        inline int stat_config_get(const char* key, int def)
        {
            if(key == NULL)
                return def;

            return config_get_intval(key, def);
        }

        inline int stat_config_get(const string& key, int def)
        {   
            return stat_config_get(key.c_str(), def);
        }   


    private:

        static const char* not_found;
        bool parse_config_file(const char * file_name, bool overwrite_flag);

        bool load_config_file(const char *file_name);
        bool reload_config_file(const char * file_name);

        int config_get_intval(const char *key, int val);
        const char *config_get_strval(const char *key, const char *val);

    private:

        map<string, string> m_calc_config_map;
};

#endif

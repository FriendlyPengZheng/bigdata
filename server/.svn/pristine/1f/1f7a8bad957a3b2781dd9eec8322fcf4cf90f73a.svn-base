#include "stat_calc_config.hpp"

const char* StatCalcConfig::not_found = "Oops! -_- not found";

StatCalcConfig::StatCalcConfig(const char* file_name)
{
    load_config_file(file_name);
}

bool StatCalcConfig::load_config_file(const char *file_name)
{
    m_calc_config_map.clear();

    return parse_config_file(file_name, false);
}

bool StatCalcConfig::reload_config_file(const char * file_name)
{
    return parse_config_file(file_name, true);
}

int StatCalcConfig::config_get_intval(const char *key, int val)
{
    std::map<std::string, std::string>::iterator it; 
    it = m_calc_config_map.find(key);
    if (it == m_calc_config_map.end())
        return val;

    return atoi((*it).second.c_str());
}

const char* StatCalcConfig::config_get_strval(const char *key, const char *val)
{
    std::map<std::string, std::string>::iterator it;
    it = m_calc_config_map.find(key);
    if (it == m_calc_config_map.end())
        return val;

    return (*it).second.c_str();
}

bool StatCalcConfig::parse_config_file(const char * file_name, bool overwrite_flag)
{
    int fd = open(file_name, O_RDONLY);
    if (fd == -1)
    {
        return false;
    }

    int len = lseek(fd, 0L, SEEK_END);
    lseek(fd, 0L, SEEK_SET);
    char * data = (char *)malloc(len + 1);
    if (data == NULL)
    {
        close(fd);
        return false;
    }

    bool ret = true;
    do
    {
        if (read(fd, data, len) == -1)
        {
            ret = false;
            break;
        }

        data[len] = 0;
        char * start = data;
        char * end;
        while (data + len > start)
        {
            end = strchr(start, '\n');
            if (end)
            {
                *end = 0;
            }

            if (*start != '#')
            {
                char * key;
                char * val;
                key = strtok(start, "= \t");
                val = strtok(NULL, "= \t");
                if (key != NULL && val != NULL)
                {
                    if (strncmp("include", key, 8))
                    {
                        std::string str_key = key;
                        std::string str_val = val;
                        if (m_calc_config_map.find(str_key) == m_calc_config_map.end())
                        {
                            m_calc_config_map[str_key] = str_val;
                        }
                        else
                        {
                            if (overwrite_flag)
                            {
                                m_calc_config_map[str_key] = str_val;
                            }
                            else
                            {
                                BOOT_LOG(-1, "config %s same key: %s", file_name, key);
                            }
                        }

                    }
                    else
                    {
                        ret = parse_config_file(val, overwrite_flag);
                        if (!ret)
                        {
                            break;
                        }
                    }
                }
            }


            if (end)
            {
                start = end + 1;
            }
            else
            {
                break;
            }
        }

    }
    while (0);

    free(data);
    close(fd);

    return ret;
}

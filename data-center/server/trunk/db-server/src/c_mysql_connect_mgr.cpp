#include "c_mysql_connect_mgr.h"
#include "log.h"
#include <vector>

using namespace std;

c_mysql_connect_mgr::c_mysql_connect_mgr()
{
    inited = false;
}

c_mysql_connect_mgr::~c_mysql_connect_mgr()
{
    if(inited) {
        uninit();
    }
}

uint32_t c_mysql_connect_mgr::init()
{
    mysql_connect.clear();
    inited = true;
    return 0;
}

uint32_t c_mysql_connect_mgr::uninit()
{
    std::map<uint32_t ,c_mysql_connect_auto_ptr*>::iterator it;
    std::vector<c_mysql_connect_auto_ptr*> v_deleted;
    uint32_t i=0;
    for(it=mysql_connect.begin(); it!=mysql_connect.end(); it++) {
        if(it->second != NULL) {
            for(i=0; i<v_deleted.size(); i++) {
                if(it->second == v_deleted[i])
                    break;
            }
            if(i == v_deleted.size()) {
                it->second->uninit();
                delete it->second;
                v_deleted.push_back(it->second);
            }
        } else {
            ERROR_LOG("%u link to NULL pointer", it->first);
        }
    }
    mysql_connect.clear();
    inited = false;
    return 0;
}

uint32_t c_mysql_connect_mgr::insert(uint32_t key, const char *host, const char *user, const char *passwd, const char *db, unsigned int port, unsigned int client_flag)
{
    c_mysql_connect_auto_ptr* mc = new c_mysql_connect_auto_ptr();
    if(mc == NULL) {
        ERROR_LOG("key[%u] host[%s] user[%s] passwd[XXXXXX] db[%s] port[%u] client_flag[%u]", key, host, user, db, port, client_flag);
        ERROR_LOG("new c_mysql_connect_auto_ptr() return NULL");
        return 1;
    }
    uint32_t ret;
    if((ret = mc->init(host, user, passwd, db, port, client_flag)) == 0) {
        mysql_connect.insert(std::pair<uint32_t, c_mysql_connect_auto_ptr*>(key, mc));
    } else {
        ERROR_LOG("key[%u] host[%s] user[%s] passwd[XXXXXX] db[%s] port[%u] client_flag[%u]", key, host, user, db, port, client_flag);
        ERROR_LOG("mc->init() return %u", ret);
        return 2;
    }
    return 0;
}

uint32_t c_mysql_connect_mgr::insert(uint32_t key, c_mysql_connect_auto_ptr* mc)
{
    if(mc == NULL) {
        ERROR_LOG("c_mysql_connect_auto_ptr* p_mc = %p", mc);
        return 1;
    }
    mysql_connect.insert(std::pair<uint32_t, c_mysql_connect_auto_ptr*>(key, mc));
    return 0;
}

c_mysql_connect_auto_ptr* c_mysql_connect_mgr::get(uint32_t key)
{
    std::map<uint32_t, c_mysql_connect_auto_ptr*>::iterator it;
    c_mysql_connect_auto_ptr* ret = NULL;
    if((it = mysql_connect.find(key)) != mysql_connect.end()) {
        ret = it->second;
    }
    return ret;
}

void c_mysql_connect_mgr::print()
{
	for (map<uint32_t, c_mysql_connect_auto_ptr*>::iterator it = mysql_connect.begin();
			it != mysql_connect.end(); ++it) {
		DEBUG_LOG("%u: %p", it->first, it->second);
	}
}

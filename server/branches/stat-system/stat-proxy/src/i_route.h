
#ifndef I_ROUTE_H_20091112
#define I_ROUTE_H_20091112

typedef struct {
    unsigned int msgid;
    unsigned int server_key;
} rule_item_t;

struct i_route
{
public:
    virtual int init() = 0;
    virtual int add_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key,int is_atomic) = 0;
    virtual int update_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key) = 0;
    virtual int set_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid,unsigned int server_key) = 0;
    virtual int remove_rule(unsigned short channel_id, unsigned int start_msgid,unsigned int end_msgid) = 0;
    virtual int enum_rules(unsigned short channel_id, rule_item_t* p_recv_buffer,int buffer_len) = 0;
    virtual int get_rule(unsigned short channel_id, unsigned int msgid,unsigned int* p_server_key) = 0;
    virtual int get_rules_count(unsigned short channel_id) = 0;
    virtual int get_last_error() = 0;
    virtual int uninit() = 0;
    virtual int release() = 0;
};

int create_route_instance(i_route** pp_instance);

#endif//I_ROUTE_H_20091112

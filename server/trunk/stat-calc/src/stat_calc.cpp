#include "stat_calc.hpp"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stat_job.hpp>
#include <stat_job_status.hpp>
#include <stat_process_status.hpp>
#include <c_mysql_connect_auto_ptr.h>
#include <unistd.h>
#include <sys/wait.h>

#include "tcp_client.hpp"
#include "../../stat-common/stat_config.hpp"
#include "../../stat-common/string_utils.hpp"

#include "stat_common.hpp"
#include "stat_proto_defines.hpp"

#include <sstream>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

using std::ostringstream;

#define RUN (1)
#define PRINT (2)

StatCalc::StatCalc(const char* file_name) : m_stat_calc_config(file_name) {
    init();
}

StatCalc::~StatCalc() {
    if(process != NULL) delete process;
}

void StatCalc::init() {
    // init alarm server
    init_alarm_ip_port();

    // init local ip
    std::ostringstream oss;
    oss.str("");

    oss << "local-ip" ; 
    string ip_key = oss.str();
    string local_str_ip;
    m_stat_calc_config.stat_config_get(ip_key, local_str_ip);
    if(local_str_ip.empty())
    {   
        ERROR_LOG("can not get %s from conf.", ip_key.c_str());
        local_str_ip = "0.0.0.0";
    }   

    struct in_addr addr;
    inet_pton(AF_INET, local_str_ip.c_str(), &addr);
    m_local_ip = addr.s_addr;

    // init mysql
    m_stat_calc_config.stat_config_get("db-host", m_db_host);
    if(m_db_host.empty()) {
        ERROR_LOG("can not get db-host from conf.");
        exit(1);
    }

    string db_port;
    m_stat_calc_config.stat_config_get("db-port", db_port);
    if(db_port.empty()) {
        ERROR_LOG("can not get db-port from conf.");
        exit(1);
    }

    m_stat_calc_config.stat_config_get("db-user", m_db_user);
    if(m_db_user.empty()) {
        ERROR_LOG("can not get db-user from conf.");
        exit(1);
    }

    m_stat_calc_config.stat_config_get("db-passwd", m_db_passwd);
    if(m_db_passwd.empty()) {
        ERROR_LOG("can not get db-passwd from conf.");
        exit(1);
    }

    m_stat_calc_config.stat_config_get("db-name", m_db_name);
    if(m_db_name.empty()) {
        ERROR_LOG("can not get db-name from conf.");
        exit(1);
    }

    m_db_port = atoi(db_port.c_str());

    if(this->mysql.init(m_db_host.c_str(), m_db_user.c_str(), m_db_passwd.c_str(), m_db_name.c_str(), m_db_port, CLIENT_INTERACTIVE) != 0) {
        ERROR_LOG("can not connect to %s:%s", m_db_host.c_str(), db_port.c_str());
        exit(2);
    }

    m_mr_gen.set_mysql(m_db_host.c_str(), m_db_user.c_str(), m_db_passwd.c_str(), m_db_name.c_str(), m_db_port);
    m_shell_gen.set_mysql(m_db_host.c_str(), m_db_user.c_str(), m_db_passwd.c_str(), m_db_name.c_str(), m_db_port);
    m_php_gen.set_mysql(m_db_host.c_str(), m_db_user.c_str(), m_db_passwd.c_str(), m_db_name.c_str(), m_db_port);

    // init log path
    m_stat_calc_config.stat_config_get("log-path", m_job_log_path);
    if(m_job_log_path.empty()) {
        m_job_log_path = "../log";
        mkdir(m_job_log_path.c_str(), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH);
    }

    string log_path = "../log";
    m_stat_calc_config.stat_config_get("log_dir", log_path);
    if (-1 == log_init(log_path.c_str(),
                    (log_lvl_t)m_stat_calc_config.stat_config_get("log_level", 8),
                       m_stat_calc_config.stat_config_get("log_size", 33554432),
                       m_stat_calc_config.stat_config_get("log_maxfiles", 100),
                       "statcalc_")) {
        ERROR_LOG("log init");
        exit(2);
    }

    m_stat_calc_config.stat_config_get("pwd", m_pwd);
    if(m_pwd.empty()) {
        m_pwd = "/opt/taomee/hadoop/bigdata-code/stat-calc";
    }

    m_max_try_again_count = m_stat_calc_config.stat_config_get("max_try_again_count", 1);

    process = NULL;
}

void StatCalc::print_usage() {
    printf("Usage:  stat-calc [OPTION...] [DATE]\n");
    printf("\n");
    printf("Examples:\n");
    printf("  stat-calc -r -p 1 -d 20141001  #run process 1 on day 2014-10-10\n");
    printf("  stat-calc -s -j 1              #get script for job 1\n");
    printf("\n");
    printf("  Main operation mode:\n\n");
    printf("    -r  run all job in one process\n");
    printf("    -s  get job script\n");
    printf("\n");
    printf("  Select process or job:\n\n");
    printf("    -p  process id to run(with option -r)\n");
    printf("    -j  job id to print(with option -s) or start(with option -r)\n");
    printf("\n");
    printf("  Opthe options:\n\n");
    printf("    -d  date with format YYYYMMDD\n");
    printf("    -g  game id split by ','(1,2,5,6)\n");
}

int
StatCalc::run(int args, char* argv[]) {
    //执行参数
    int model = -1;
    int processid = -1;
    int date = -1;
    int jobid = -1;
    const char* gids = NULL;
    int* a_gid = NULL;
    int n_gid = 0;
    for(int i=1; i<args; i++) {
        if(strcmp(argv[i], "-r") == 0) {
            model = RUN;
        } else if(strcmp(argv[i], "-s") == 0) {
            model = PRINT;
        } else if(strcmp(argv[i], "-d") == 0) {
            date = atoi(argv[++i]);
            strncpy(this->date, argv[i], sizeof(this->date));
        } else if(strcmp(argv[i], "-p") == 0) {
            processid = atoi(argv[++i]);
        } else if(strcmp(argv[i], "-j") == 0) {
            jobid = atoi(argv[++i]);
        } else if(strcmp(argv[i], "-g") == 0) {
            gids = argv[++i];
        } else if(strcmp(argv[i], "-h") == 0) {
            print_usage();
            exit(0);
        } else {
            printf("Unsupported option %s.\nTry `%s -h` for more information.\n", argv[i], argv[0]);
            exit(3);
        }
    }

    //检查参数合法性
    if(model != RUN && model != PRINT) {
        printf("run model invalid!\nTry `%s -h` for more information.\n", argv[0]);
        exit(3);
    }

    if(model == RUN && processid <= 0) {
        printf("no process id!\nTry `%s -h` for more information.\n", argv[0]);
        exit(3);
    }

    if(model == PRINT && jobid <= 0) {
        printf("no job id!\nTry `%s -h` for more information.\n", argv[0]);
        exit(3);
    }

    if(date <= 20000000 || date >= 29991231) {
        printf("date format not valid!\nTry `%s -h` for more information.\n", argv[0]);
        exit(3);
    }

    m_job_log_path += "/";
    m_job_log_path += this->date;
    mkdir(m_job_log_path.c_str(), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH);

    if(gids != NULL) {
        string s_gids(gids);
        vector<string> elems;
        elems.clear();

        StatCommon::split(s_gids, ',', elems);
        if(elems.size() <= 0) {
            printf("gameid format not valid!\nTry `%s -h` for more information.\n", argv[0]);
            exit(3);
        }

        n_gid = elems.size();
        a_gid = new int[n_gid];

        for(int i=0; i<n_gid; i++) {
            int g = atoi(elems[i].c_str());
            if(g <= 0) {
                printf("gameid format not valid!\nTry `%s -h` for more information.\n", argv[0]);
                delete[] a_gid;
                exit(3);
            } else {
                a_gid[i] = g;
            }
        }
    }

    if(model == RUN) {
        runProcess(processid, jobid, date, a_gid, n_gid);
    } else if(model == PRINT) {
        runPrint(jobid, a_gid, n_gid);
    } else {

    }

    if(a_gid != NULL)   delete[] a_gid;

    return 0;
}

void
StatCalc::runProcess(int processid, int start, int date, int* gids, int ngid) {
    //StatMrGenerate p;
    //int gid[] = { 1,2,3 };
	//p.process_job(2, "../", "test.sh", gid, sizeof(gid)/sizeof(gid[0]));
    //return 0;
	//新建一个工作流对象process
    if(process != NULL) delete process;
    process = new StatProcess(processid);
    //从配置读取并发数
    int paralle_limit = m_stat_calc_config.stat_config_get("paralle-limit", 5);
    paralle_limit = paralle_limit >= 10 ? 10 : paralle_limit;
    paralle_limit = paralle_limit <= 1 ? 1 : paralle_limit;
    process->setParallelLimit(paralle_limit);

    char sql[1024];
    sprintf(sql, "select t_job_process.job_id,type,after_job from t_job_process inner join t_job_classification on t_job_process.job_id=t_job_classification.job_id where process_id = %d;", processid);
    this->mysql.do_sql(sql);
    MYSQL_ROW row;
	//根据父节点和子节点对job对象里的元素进行添加(jobid 状态 父节点 子节点)
    while((row = this->mysql.get_next_row()) != NULL) {
        uint32_t id = atoi(row[0]);
        uint32_t type = atoi(row[1]);
        char filename[1024];
        char jobname[256];

        process->addJob(id, type, std::string(row[2]));
        DEBUG_LOG("%s => %s\n", row[0], row[2]);

        StatJobGenerate *gen = NULL;
        //printf("jobid = %d  type = %d\n", id, type);
        switch(type) {
            case JOB_START:
            case JOB_DBUPLOAD:
            case JOB_MYSQLUPLOAD:
            case JOB_END:
                gen = NULL; break;
            case JOB_MR:
                gen = &m_mr_gen;   break;
            case JOB_SHELL:
                gen = &m_shell_gen; break;
            case JOB_PHP:
                gen = &m_php_gen;    break;
            default:
                gen = NULL; break;
        }

        if(gen != NULL) {
            getJobName(id, jobname);
            job_name_map.insert(std::pair<uint32_t, string>(id, string(jobname)));
            sprintf(filename, "%d_%s.sh", id, jobname);
            gen->process_job(id, string("../script"), string(filename), gids, ngid);
        }
    }

    if(process->getJobCnt() == 0) {
        DEBUG_LOG("process %d is empty.\n", processid);
        return;
    }

	//起始节点是从0开始的，0不用执行
    if(start > 0 && !process->setStart(start)) {
        process->setStart(start);//设置起始节点
        ERROR_LOG("job %d not in process %d.\n", start, processid);
        return;
    }

    pid_t pid;
    int statloc;
    bool doagain = false;
    while(true) {
		//遍历所有的job，获得一个可以运行的job
        int job_id = process->getRunnableJob();
        //printf("get runnable job id = %d\n", job_id);
        if(job_id == -1) {
            uint32_t e = process->getErrorCode();
            if(e == StatProcess::reach_parallel_limit
                    || e == StatProcess::waiting_for_current_job) {
                sleep(1);
            } else {
                break;
            }
        } else {
			//获取job正常,生成对应job并执行
            doJobById(job_id);
        }
        pid = waitpid(-1, &statloc, WNOHANG);
        doagain = false;
        if(pid != 0) {
            std::map<pid_t, uint32_t>::iterator it = pid_set.find(pid);
            if(it == pid_set.end()) {
                ERROR_LOG("can not be here.pid %u not found in pid_set", pid);
                break;
            }
            job_id = it->second;
            if(WIFEXITED(statloc)) {
                if(WEXITSTATUS(statloc) == 0) {
                    jobExitOK(pid);
                    doagain = false;
                } else {
                    DEBUG_LOG("%d[%d] return %d\n", pid, job_id, WEXITSTATUS(statloc));
                    doagain = true;
                }
            } else if(WIFSIGNALED(statloc)) {
                    DEBUG_LOG("%d[%d] killed by signal %d\n", pid, job_id, WEXITSTATUS(statloc));
                    doagain = true;
			}

            if(doagain) {
                it = job_try_again_count.find(job_id);
                uint32_t count = 0;
                if(it == job_try_again_count.end() || (count=it->second) < m_max_try_again_count) {
                    job_try_again_count.erase(job_id);
                    job_try_again_count.insert(std::pair<uint32_t, uint32_t>(job_id, ++count));
                    DEBUG_LOG("%d do job %d again\n", count, job_id);
                    doJobById(job_id);
                } else {
                    ERROR_LOG("%d return %d\n", pid, WEXITSTATUS(statloc));
                    jobExitErr(pid, statloc);
                }
            }
        }
    }

    process->print();
}

void
StatCalc::runPrint(int jobid, int* gids, int ngid) {
    char sql[1024];
    sprintf(sql, "select t_job_process.job_id,type,after_job from t_job_process inner join t_job_classification on t_job_process.job_id=t_job_classification.job_id where t_job_process.job_id = %d;", jobid);
    this->mysql.do_sql(sql);
    MYSQL_ROW row;
	//根据父节点和子节点对job对象里的元素进行添加(jobid 状态 父节点 子节点)
    if((row = this->mysql.get_next_row()) != NULL) {
        uint32_t id = atoi(row[0]);
        uint32_t type = atoi(row[1]);
        char filename[1024];
        char jobname[256];

        process = new StatProcess(1);
        process->addJob(id, type, std::string(row[2]));
        DEBUG_LOG("%s => %s\n", row[0], row[2]);

        StatJobGenerate *gen = NULL;
        //printf("jobid = %d  type = %d\n", id, type);
        switch(type) {
            case JOB_START:
            case JOB_DBUPLOAD:
            case JOB_MYSQLUPLOAD:
            case JOB_END:
                gen = NULL; break;
            case JOB_MR:
                gen = &m_mr_gen;   break;
            case JOB_SHELL:
                gen = &m_shell_gen; break;
            case JOB_PHP:
                gen = &m_php_gen;    break;
            default:
                gen = NULL; break;
        }

        if(gen != NULL) {
            getJobName(id, jobname);
            job_name_map.insert(std::pair<uint32_t, string>(id, string(jobname)));
            sprintf(filename, "%d_%s.sh", id, jobname);
            gen->process_job(id, string("../script"), string(filename), gids, ngid);
        }
    }

}

//这里调用生成脚本的接口(kendy)
int
StatCalc::doJobById(uint32_t id) {
    pid_t pid;
    //const char* jobname;
    if((pid = fork()) < 0) {
        ERROR_LOG("fork error\n");
        return -1;
    } else if(pid == 0) {
        DEBUG_LOG("do job %d\n", id);
        sleep(1);
        //init stdout and stderr to log file
        std::map<uint32_t, string>::iterator it = job_name_map.find(id);
        static char buf[64];
        if(it != job_name_map.end()) {
            sprintf(buf, "%d_%s", id, it->second.c_str());
        } else {
            sprintf(buf, "%d_%s", id, date);
        }
        dup2logfile(buf);
        uint32_t type;
        type = process->getJobType(id);
        int e;
        char p[1024];
        char filename[1024];
        switch(type) {
            case JOB_START:
            case JOB_DBUPLOAD:
            case JOB_MYSQLUPLOAD:
            case JOB_END:
                exit(0);
            case JOB_MR:
            case JOB_SHELL:
                sprintf(filename, "%d_%s.sh", id, it->second.c_str());
                sprintf(p, "%s/script/%d_%s.sh", m_pwd.c_str(), id, it->second.c_str());
                e = execlp("sh", p, p, date, (char*)0);//do mr/shell script
                break;
            case JOB_PHP:
                sprintf(filename, "%d_%s.sh", id, it->second.c_str());
                sprintf(p, "%s/script/%d_%s.sh", m_pwd.c_str(), id, it->second.c_str());
                e = execlp("php", p, p, date, (char*)0);//do php script
                break;
            default:
                exit(0);
        }
        //TODO:do job
        //获取job类型，并调用相应的脚本生成类(kendy)，并执行
		//查询任务分类表，获取job类型
        //ScriptCreate sc(id);
        //sc.create(path, filename, id, ...);
        //int e = execlp("php", "/home/ping/ccode/e.php", "e.php", "1", "2", (char*)0);//do php script
        //int e = execlp("sh", "/home/ping/ccode/echoall", "echoall", "a", "b", (char*)0);//do shell scrpit
        //int e = execlp("sh", "/opt/taomee/hadoop/hadoop/bin/hadoop", "/opt/taomee/hadoop/hadoop/bin/hadoop", "fs", "-ls", "/", (char*)0);//do hadoop command
        //printf("%d_%s sleep and exit\n", id, it->second.c_str());
		exit(0);
    } else {
        process->jobStart(id);
        pid_set.insert(std::pair<pid_t, uint32_t>(pid, id));
        //job start and parent return ok.
        return 0;
    }
}

void
StatCalc::jobExitOK(pid_t pid) {
    std::map<pid_t, uint32_t>::iterator it = pid_set.find(pid);
    if(it != pid_set.end()) {
        pid_set.erase(it);
        process->jobExit(it->second, 0);
		DEBUG_LOG("job %d exit ok\n", it->second);
    }
}

void
StatCalc::jobExitErr(pid_t pid, int status) {
    std::map<pid_t, uint32_t>::iterator it = pid_set.find(pid);
    if(it != pid_set.end()) {
        pid_set.erase(it);
        process->jobExit(it->second, status);
        printf("job %d exit error\n", it->second);
        //TODO:发送告警
        //调用发送告警类(tomli)，将job名称，错误码发送给stat-center
        std::map<uint32_t, string>::iterator it2 = job_name_map.find(it->second);
        if(it2 != job_name_map.end()) {
            alarm(status, it2->second);
        } else {
            static char buf[64];
            sprintf(buf, "%d_%s", it->second, date);
            string a(buf);
            alarm(status, a);
        }
    }
}

void StatCalc::init_alarm_ip_port()
{
    int proxy_count = m_stat_calc_config.stat_config_get("stat-center-count", 1);

    for(int id = 0; id < proxy_count; ++id)
    {
        ostringstream oss;
        oss.str("");

        oss << "stat-center" << "-host" ;
        string ip_key = oss.str();
        string ipp;
        m_stat_calc_config.stat_config_get(ip_key, ipp);
        if(ipp.empty())
        {
            ERROR_LOG("can not get %s from conf.", ip_key.c_str());
            return;
        }

        vector<string> elems;
        elems.clear();

        StatCommon::split(ipp, ':', elems);
        if(elems.size() != 2)
        {
            ERROR_LOG("bad format of ip:port, %s", ipp.c_str());
            return;
        }

        m_alarm_ip = elems[0];
        m_alarm_port = elems[1];
    }
}

void StatCalc::alarm(int status, string& job_name) const
{
    char pkg_buff[100];
    int8_t offset = 0;

    uint32_t pkg_len = sizeof(struct StatCalcError) + job_name.length();   // TOFIXED
    memcpy(pkg_buff+offset, &pkg_len, sizeof(pkg_len));
    offset += sizeof(pkg_len);

    uint32_t proto_id = 0xA018;
    memcpy(pkg_buff+offset, &proto_id, sizeof(uint32_t));
    offset += sizeof(uint32_t);

    uint8_t module_type = 12;
    memcpy(pkg_buff+offset, &module_type, 1);
    offset += 1;

    memcpy(pkg_buff+offset, &m_local_ip, sizeof(m_local_ip));
    offset += sizeof(m_local_ip);

    int8_t status_temp = (int8_t)status;
    memcpy(pkg_buff+offset, &status_temp, sizeof(int8_t));
    offset += sizeof(int8_t);

    uint8_t job_name_len = job_name.length();
    memcpy(pkg_buff+offset, &job_name_len, sizeof(uint8_t));
    offset += sizeof(uint8_t);

    memcpy(pkg_buff+offset, job_name.c_str(), job_name.length());

    do {
        TcpClient tc;
        int fd = tc.connect(m_alarm_ip, m_alarm_port);
        if (fd < 0)
            break;
        tc.set_timeout(20);

        int ret = tc.send(pkg_buff, pkg_len);
        if (ret < (int)pkg_len)
            break;

        char buff[100] = {0};
        ret = tc.recv(buff, sizeof(StatAlarmRet));
        if (ret < (int)sizeof(StatAlarmRet))
            break;
    } while(false);
}

void
StatCalc::getJobName(uint32_t id, char* buf) {
    buf[0] = 0;
    
    if(buf == NULL)  return;

    StatJobGenerate* gen;
    uint32_t type;
    type = process->getJobType(id);
    switch(type) {
        case JOB_START:
        case JOB_DBUPLOAD:
        case JOB_MYSQLUPLOAD:
        case JOB_END:
            gen = NULL; break;
        case JOB_MR:
            gen = &m_mr_gen;   break;
        case JOB_SHELL:
            gen = &m_shell_gen; break;
        case JOB_PHP:
            gen = &m_php_gen;    break;
        default:
            gen = NULL; break;
    }
    if(gen == NULL)  return;

    const char* jobname = gen->get_jobname(id);
    if(jobname == NULL) return;

    string jn(jobname);

    vector<string> elems;
    StatCommon::split(jobname, '$', elems);
    if(elems.size() >=2) {
        jn = elems[0];
    }

    elems.clear();
    StatCommon::split(jobname, '.', elems);
    if(elems.size() >=2) {
        jn = elems[0];
    }

    //replace '"'
    for(uint32_t i=0; i<jn.length(); i++) {
        if(jn[i] == '"' || jn[i] == ' ' || jn[i] == '_') {
            jn.replace(i, 1, "");
        }
    }
    sprintf(buf, "%s_%s", jn.c_str(), date);
}

void
StatCalc::dup2logfile(const char* jobname) {
    char pathname[1024];
    sprintf(pathname, "%s/%s.log", m_job_log_path.c_str(), jobname);
    FILE *std_fp;
    if((std_fp = fopen(pathname,"w+")) == NULL){
        exit(6);
    }
    if(dup2(fileno(std_fp),STDOUT_FILENO) == -1){
        fclose(std_fp);
        exit(5);
    }
    if(dup2(fileno(std_fp),STDERR_FILENO) == -1){
        fclose(std_fp);
        exit(5);
    }
    fclose(std_fp);
}

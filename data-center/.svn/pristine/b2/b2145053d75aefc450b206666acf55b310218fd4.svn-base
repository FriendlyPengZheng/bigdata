#include "stat_process.hpp"
#include "stat_process_status.hpp"
#include "stat_job_status.hpp"
#include <vector>
#include <stdlib.h>
#include <stdint.h>

StatProcess::StatProcess(uint32_t pid) {
    process_id = pid;
    status = PROCESS_WAIT;
    parallel_limit = 4294967295;
    error = 0;
}

StatProcess::~StatProcess() {
    for(std::map<uint32_t, StatJob*>::iterator it = job_set.begin(); it != job_set.end(); it++) {
        delete it->second;
    }
}

//根据父节点和子节点对job对象里的元素进行添加(jobid 状态 父节点 子节点)
uint32_t
StatProcess::addJob(uint32_t jobid, uint32_t jobtype, const string& children) {
    if(added_job_set.find(jobid) == added_job_set.end() || added_job_set.empty()) {
		//将jobid插入added_job_set结构
        added_job_set.insert(jobid);
		//通过jobid获取这个job的对象(jobid 状态 父节点 子节点)
        StatJob* new_job = getJobById(jobid);
		//以','为分隔符将children分隔,并存放在vector结构children_str中
        std::vector<std::string> children_str;
        StatCommon::split(children, ',', children_str);
        for(std::vector<std::string>::iterator i = children_str.begin(); i != children_str.end(); i++) {
            int32_t id = atoi(i->c_str());
			//若不是结束节点
            if(id != -1) {
                new_job->addChildren(id);//添加子节点
                StatJob* child = getJobById(id);
                child->addParent(jobid);//给子节点添加父节点
            }
        }
        getJobById(jobid)->setType(jobtype);
        return 0;
    } else {
        status = PROCESS_INVALID;
        error = not_dag;
        return not_dag;
    }
}

StatJob*
StatProcess::getJobById(uint32_t jobid) {
    std::map<uint32_t, StatJob*>::iterator it = job_set.find(jobid);
    if(it == job_set.end()) {
        StatJob* new_job = new StatJob(jobid);
        job_set.insert(std::pair<uint32_t, StatJob*>(jobid, new_job));
        return new_job;
    } else {
        return it->second;
    }
}

void
StatProcess::print() {
    for(std::map<uint32_t, StatJob*>::iterator it = job_set.begin(); it != job_set.end(); it++) {
        it->second->print();
    }
}

/**
 *  @fn  getRunnableJob
 *  @brief  遍历所有的job，获得一个可以运行的job
 *
 *  @param  
 * @return  -1:失败；>=0:job id
 */

int
StatProcess::getRunnableJob() {
    //当前process已经成功退出
    if(status == PROCESS_EXIT_OK) {
        error = exit_ok;
        return -1;
    }
    //当前process不处于可运行状态
    if(status != PROCESS_WAIT && status != PROCESS_RUNNING) {
        error = error_status;
        return -1;
    }
    //当前运行的job数量已达到配置上限
    if(running_job_set.size() >= parallel_limit) {
        error = reach_parallel_limit;
        return -1;
    }
    //遍历所有job，取得一个可运行状态，即返回
    for(std::map<uint32_t, StatJob*>::iterator it = job_set.begin(); it != job_set.end(); it++) {
        StatJob* job = it->second;
        if(jobRunnable(job)) {
            if(status == PROCESS_WAIT)  status = PROCESS_RUNNING;
            error = ok;
            return job->getJobId();
        }
    }
    //没有可运行的job，但是有正在运行的job，说明需要等运行的job结束以后，才有依赖的后续job可运行
    if(running_job_set.size() != 0) {
        error = waiting_for_current_job;
    } else {
        //没有可运行job，也没有正在运行的job，说明该process可以结束了
        error = no_runnable_job;
        if(exited_err_job_set.size() != 0) {
            status = PROCESS_EXIT_ERROR;
        } else {
            status = PROCESS_EXIT_OK;
        }
    }
    return -1;
}

/**
 *     @fn  jobRunnable
 *  @brief  判断给定的job是否可以运行。一个可以运行的job必须同时满足2个条件：1.处于wait状态；2.没有依赖的父节点，或者所有父节点都已正常结束
 *
 *  @param  需要判断的StatJob对象指针
 * @return  job是否可以运行
 */

bool
StatProcess::jobRunnable(StatJob* job) {
    if(job->getStatus() != JOB_WAIT)    return false;
    const std::set<uint32_t> parent = job->getParent();
    if(parent.empty())   return true;
    for(std::set<uint32_t>::iterator it = parent.begin(); it != parent.end(); it++) {
        if(exited_ok_job_set.find(*it) == exited_ok_job_set.end())   return false;
    }
    return true;
}

const char*
StatProcess::getErrorStr() {
    switch(error) {
        case ok:
            return "no error";
        case not_dag:
            return "not DAG";
        case error_status:
            return "wrong status";
        case exit_ok:
            return "process ok";
        case no_runnable_job:
            return "no runnable jobs";
        case reach_parallel_limit:
            return "reach parallel limit";
        case waiting_for_current_job:
            return "waiting for current job[s]";
        default:
            return "unknown error code";
    }
}

void
StatProcess::jobStart(uint32_t jobid) {
    std::map<uint32_t, StatJob*>::iterator it = job_set.find(jobid);
    if(it != job_set.end()) {
        running_job_set.insert(jobid);
        it->second->setStatus(JOB_RUNNING);
    }
}

void
StatProcess::jobExit(uint32_t jobid, int32_t status) {
    std::map<uint32_t, StatJob*>::iterator it = job_set.find(jobid);
    if(it != job_set.end()) {
        running_job_set.erase(jobid);
        if(status == 0) {
            it->second->setStatus(JOB_EXIT_OK);
            exited_ok_job_set.insert(jobid);
        } else {
            putIntoExitErrSet(it->second);
            it->second->setStatus(JOB_EXIT_ERROR);
            exited_err_job_set.insert(jobid);
        }
    }
}

bool
StatProcess::setStart(uint32_t start) {
    std::map<uint32_t, StatJob*>::iterator it = job_set.find(start);
    if(it == job_set.end()) {
        return false;
    }
    std::set<uint32_t> to_run_job_set;
    putIntoToRunSet(start, to_run_job_set);
    for(it = job_set.begin(); it != job_set.end(); it++) {
        if(to_run_job_set.find(it->first) == to_run_job_set.end()) {
            jobExit(it->first, 0);
        }
    }
    return true;
}

void
StatProcess::putIntoToRunSet(uint32_t id, std::set<uint32_t>& to_run_set) {
    std::map<uint32_t, StatJob*>::iterator it;
    if((it = job_set.find(id)) != job_set.end()) {
        to_run_set.insert(id);
        const std::set<uint32_t> children = it->second->getChildren();
        for(std::set<uint32_t>::iterator child = children.begin(); child != children.end(); child++) {
            putIntoToRunSet(*child, to_run_set);
        }
    }
}

void
StatProcess::putIntoExitErrSet(StatJob* job) {
    if(job != NULL) {
        job->setStatus(JOB_PARENT_ERROR);
        exited_err_job_set.insert(job->getJobId());
        const std::set<uint32_t> children = job->getChildren();
        for(std::set<uint32_t>::iterator child = children.begin(); child != children.end(); child++) {
            std::map<uint32_t, StatJob*>::iterator it = job_set.find(*child);
            if(it != job_set.end()) {
                putIntoExitErrSet(it->second);
            }
        }
    }
}

/**
 * =====================================================================================
 *       @file  stat_job.cpp
 *      @brief  
 *
 *     Created  2014-09-25 16:35:51
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "stat_job.hpp"
#include <stdio.h>

StatJob::StatJob(uint32_t job_id) {
    this->job_id = job_id;
    this->status = JOB_WAIT;
}

void
StatJob::setStatus(StatJobStatus status) {
    this->status = status;
}

void
StatJob::addParent(uint32_t pid) {
    parent.insert(pid);
}

void
StatJob::addChildren(uint32_t cid) {
    children.insert(cid);
}

void
StatJob::print() {
    printf("job[%d]", this->job_id);
    printf("\n--------------------------------------------\n");
    printf("status\t\t:\t");
    switch(this->status) {
        case JOB_WAIT:
            printf("wait for running");
            break;
        case JOB_RUNNING:
            printf("is running");
            break;
        case JOB_EXIT_OK:
            printf("run and exit ok");
            break;
        case JOB_EXIT_ERROR:
            printf("run and exit error");
            break;
        case JOB_PARENT_ERROR:
            printf("parent exit error");
            break;
        default:
            printf("unknown status[%d]", this->status);
            break;
    }
    printf("\n--------------------------------------------\n");
    printf("parent job id\t:\t");
    if(this->parent.empty()) {
        printf("null");
    } else {
        for(std::set<uint32_t>::iterator it = this->parent.begin(); it != this->parent.end(); it++) {
            printf("%d\t", *it);
        }
    }
    printf("\n--------------------------------------------\n");
    printf("children job id\t:\t");
    if(this->children.empty()) {
        printf("null");
    } else {
        for(std::set<uint32_t>::iterator it = this->children.begin(); it != this->children.end(); it++) {
            printf("%d\t", *it);
        }
    }
    printf("\n--------------------------------------------\n");
    printf("job type\t:\t");
    switch(type) {
        case JOB_START:
            printf("Start");    break;
        case JOB_MR:
            printf("MapReducer");   break;
        case JOB_SHELL:
            printf("shell");    break;
        case JOB_PHP:
            printf("php");  break;
        case JOB_DBUPLOAD:
            printf("DB_UPLOAD");    break;
        case JOB_MYSQLUPLOAD:
            printf("MYSQL_UPLOAD"); break;
        case JOB_END:
            printf("End");  break;
        default:
            printf("Unknown");  break;
    }
    printf("\n--------------------------------------------\n");
}


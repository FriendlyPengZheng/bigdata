/**
 * =====================================================================================
 *       @file  stat_process.hpp
 *      @brief  
 *
 *     Created  2014-09-25 17:26:20
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  STAT_PROCESS_HPP
#define  STAT_PROCESS_HPP

#include <stdint.h>
#include <map>
#include <set>
#include "stat_job.hpp"
#include "stat_job_status.hpp"
#include "stat_process_status.hpp"
#include "string_utils.hpp"
//工作流类
class StatProcess {
    public:
        enum ReturnCode {
            ok,
            not_dag,
            error_status,
            exit_ok,
            no_runnable_job,
            reach_parallel_limit,
            waiting_for_current_job,
        };

        StatProcess(uint32_t);
        ~StatProcess();

        StatProcessStatus getStatus() {
            return status;
        }

        void setParallelLimit(uint32_t l) {
            parallel_limit = l;
        }

        uint32_t getParallelLimit() {
            return parallel_limit;
        }

        uint32_t addJob(uint32_t, uint32_t, const string&);

        void jobStart(uint32_t);

        void jobExit(uint32_t, int32_t);

        void print();

        int getRunnableJob();

        const char* getErrorStr();

        uint32_t getErrorCode() {
            return error;
        }

        bool setStart(uint32_t);

        uint32_t getJobType(uint32_t id) {
            return getJobById(id)->getType();
        }

        uint32_t getJobCnt() {
            return job_set.size();
        }

    private:
        uint32_t process_id;
        uint32_t parallel_limit;
        uint32_t error;
        StatProcessStatus status;
        std::map<uint32_t, StatJob*> job_set;
        std::set<uint32_t> exited_ok_job_set;
        std::set<uint32_t> exited_err_job_set;
        std::set<uint32_t> running_job_set;
        std::set<uint32_t> added_job_set;

        StatJob* getJobById(uint32_t);
        bool jobRunnable(StatJob* job);
        void putIntoToRunSet(uint32_t, std::set<uint32_t>&);
        void putIntoExitErrSet(StatJob* job);
};

#endif  /*STAT_PROCESS_HPP*/

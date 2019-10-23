/**
 * =====================================================================================
 *       @file  stat_job.hpp
 *      @brief  
 *
 *     Created  2014-09-25 16:19:39
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  STAT_JOB_HPP
#define  STAT_JOB_HPP

#include <stdint.h>
#include <set>
#include "stat_job_status.hpp"

class StatJob {
    public:
        StatJob(uint32_t job_id);
        uint32_t getJobId() {
            return job_id;
        }
        void setStatus(StatJobStatus);
        StatJobStatus getStatus() {
            return status;
        }
        const std::set<uint32_t> getParent() {
            return (const std::set<uint32_t>)parent;
        }
        const std::set<uint32_t> getChildren() {
            return (const std::set<uint32_t>)children;
        }
        void addParent(uint32_t);
        void addChildren(uint32_t);
        bool hasParent() {
            return !parent.empty();
        }
        bool hasChildren() {
            return !children.empty();
        }
        void print();

        uint32_t getType() {
            return type;
        }

        void setType(uint32_t t) {
            type = t;
        }

    private:
        uint32_t job_id;
        uint32_t type;
        StatJobStatus status;
        std::set<uint32_t> parent;
        std::set<uint32_t> children;
};

#endif  /*STAT_JOB_HPP*/

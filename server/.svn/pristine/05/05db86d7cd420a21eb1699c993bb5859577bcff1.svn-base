/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <signal.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <cerrno>
#include <cstring>

#include "stat_common.hpp"
#include "string_utils.hpp"
#include "os_utils.hpp"

static void dummy_func(int)
{
}

namespace StatCommon
{
    // 包含操作系统版本信息的文件
    const char* const debian_release = "/etc/debian_version";
    const char* const centos_release = "/etc/redhat-release";

    int get_os_info(string& os_name, string& os_version)
    {
        const char* release_file = NULL;

        if(::access(debian_release, R_OK) == 0)
        {
            os_name = "debian";
            release_file = debian_release;
        }
        else if(::access(centos_release, R_OK) == 0)
        {
            os_name = "centos";
            release_file = centos_release;
        }
        else
            return -1;

        int fd = ::open(release_file, O_RDONLY);
        if(fd < 0)
        {
            return -1;
        }

        char version[64] = {0};
        int n = ::read(fd, version, sizeof(version) / sizeof(char));
        if(n < 0)
        {
            ::close(fd);
            return -1;
        }

        ::close(fd);

        string version_string(version, n);
        trim(version_string," ");
        trim(version_string,"\n");

        if(os_name == "debian")
            os_version = version_string;
        else
        {
            std::vector<string> elems;
            split(version_string, ' ', elems);
            if(elems.size() < 3)
                return -1;

            os_version = elems[2];
        }

        return 0;
    }

    int run_cmd_sync_with_timeout(const string& cmd_full_path, const vector<string>& args, unsigned timeout)
    {
        if(cmd_full_path.empty() || timeout < 1)
            return -1;

        std::vector<string> elems;
        split(cmd_full_path, '/', elems);

        // cmd_full_path非空, elems.size至少是1, 安全.
        string cmd_name = elems[elems.size() - 1];

        int argc = args.size();
        char** argv = new (std::nothrow) char* [argc + 2]; // argv[0] must be cmd_name and argv must be NULL-terminated
        if(argv == NULL)
            return -1;

        // argv 只在子进程中使用，在这里强行转换
        argv[0] = (char*)cmd_name.c_str();
        for(int i = 1; i <= argc; ++i)
        {
            argv[i] = (char*)args[i - 1].c_str();
        }
        argv[argc + 1] = NULL;

        sighandler_t orig_sigchild = signal(SIGCHLD, dummy_func);

        sigset_t mask;
        sigset_t orig_mask;

        sigemptyset (&mask);
        sigaddset (&mask, SIGCHLD);
        // block SIGCHILD first to avoid race condition.
        if (sigprocmask(SIG_BLOCK, &mask, &orig_mask) < 0) 
        {
            return -1;
        }
        
        int ret = 0;
        struct timespec ts;
        ts.tv_sec = timeout;
        ts.tv_nsec = 0;

        pid_t pid = fork();

        if(pid == 0)
        {
            // close all opened files except stdin stdout stderr
            struct rlimit rl;
            if(getrlimit(RLIMIT_NOFILE, &rl) < 0)
                rl.rlim_max = 1024;
            if(rl.rlim_max == RLIM_INFINITY)
                rl.rlim_max = 1024;

            for(unsigned i = 3; i < rl.rlim_max; ++i)
                close(i);

            execv(cmd_full_path.c_str(), argv);

            _exit(254);
        }
        else if(pid > 0)
        {
            // wait for SIGCHLD, if timeout expired, kill child.
            // if child's output is bigger than PIPE_BUF, child will be blocked. 
            // So parent will kill it when timeout expired.
            do
            {
                if (sigtimedwait(&mask, NULL, &ts) < 0)
                {
                    if (errno == EINTR)
                    {
                        /* Interrupted by a signal other than SIGCHLD. */
                        continue;
                    }
                    else if (errno == EAGAIN)
                    {
                        DEBUG_LOG("cmd: %s timeout, killing it.", cmd_full_path.c_str());
                        kill(pid, SIGKILL);
                        break;
                    }
                    else
                    {
                        ERROR_LOG("cmd: %s, sigtimedwait failed.", cmd_full_path.c_str());
                        ret = -1;
                        break;
                    }
                }

                break;
            }
            while (1);

            int status, werr;
            while((werr = waitpid(pid, &status, 0)) < 0 && errno == EINTR);
            if(werr < 0)
            {
                ERROR_LOG("cmd: %s, waitpid failed.", cmd_full_path.c_str());
                ret = -1;
            }

            if(WIFEXITED(status))
            {
                ret = WEXITSTATUS(status);
                if(ret == 254) // 254为exec失败
                    ret = -1;
            }
            else
                ret = -1;
        } // parent process
        else
            ret = -1; // fork failed

        // restore orignal signal handler and mask
        signal(SIGCHLD, orig_sigchild);
        sigprocmask(SIG_SETMASK, &orig_mask, NULL);

        return ret;
    }
}

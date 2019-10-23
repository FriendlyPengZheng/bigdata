/**
 * =====================================================================================
 *       @file  tcp_writer.cpp
 *      @brief  
 *
 *     Created  2014-07-14 22:05:56
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "tcp_writer.hpp"
#include "global.h"
#include "log.h"
#include <errno.h>

int TcpWriter::writev(int fd, const struct iovec *iov, int iovcnt)
{
    if(fd < 0 || iov == NULL || iovcnt == 0)
        return TC_ERROR;
    ssize_t sent_bytes = ::writev(fd, iov, iovcnt);
    if(sent_bytes <= 0)
    {
        if(errno == EAGAIN || errno == EWOULDBLOCK)
        {
            ERROR_LOG("timeout while sending data.");
            return TC_TIMEOUT;
        }

        ERROR_LOG("writev failed: %s.", strerror(errno));
        return TC_ERROR;
    }

    return sent_bytes;
}

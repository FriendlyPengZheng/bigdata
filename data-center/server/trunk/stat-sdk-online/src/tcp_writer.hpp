#ifndef  TCP_WRITER_H
#define  TCP_WRITER_H

#include <sys/uio.h>

class TcpWriter {
    public :
        static int writev(int fd,const struct iovec *iov, int iovcnt);
};

#endif  /*TCP_WRITER_H*/

#ifndef STAT_LOG_FILE_DISK_WRITER_HPP
#define STAT_LOG_FILE_DISK_WRITER_HPP

#include "statlog_file_writer.hpp"
#include "statlog_file_disk.hpp"

class StatLogFileDiskWriter : public StatLogFileWriter
{
public:
    StatLogFileDiskWriter(const string& inbox_path, const string& outbox_path, 
            const string& fwtype, size_t max_fsize, uint16_t max_files = 30) : 
        StatLogFileWriter(inbox_path, outbox_path, fwtype, max_fsize, max_files)
    {}
    virtual ~StatLogFileDiskWriter()
    {}

private:
    virtual StatLogFile* create_statlog_file(const string& fpath, size_t max_fsize)
    {
        return (new (std::nothrow) StatLogFileDisk(fpath, max_fsize));
    }

    StatLogFileDiskWriter(const StatLogFileDiskWriter&);
    StatLogFileDiskWriter& operator = (const StatLogFileDiskWriter&);
};

#endif

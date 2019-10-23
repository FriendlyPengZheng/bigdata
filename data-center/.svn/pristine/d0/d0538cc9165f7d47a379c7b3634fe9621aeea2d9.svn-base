#ifndef STAT_LOG_FILE_MMAP_WRITER_HPP
#define STAT_LOG_FILE_MMAP_WRITER_HPP

#include "statlog_file_writer.hpp"
#include "statlog_file_mmap.hpp"

class StatLogFileMmapWriter : public StatLogFileWriter
{
public:
    StatLogFileMmapWriter(const string& inbox_path, const string& outbox_path, 
            const string& fwtype, size_t max_fsize, uint16_t max_files = 30) : 
        StatLogFileWriter(inbox_path, outbox_path, fwtype, max_fsize, max_files)
    {}
    virtual ~StatLogFileMmapWriter()
    {}

private:
    virtual StatLogFile* create_statlog_file(const string& fpath, size_t max_fsize)
    {
        return (new (std::nothrow) StatLogFileMmap(fpath, max_fsize));
    }

    StatLogFileMmapWriter(const StatLogFileMmapWriter&);
    StatLogFileMmapWriter& operator = (const StatLogFileMmapWriter&);
};

#endif

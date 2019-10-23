#ifndef STAT_LOG_CUR_WRITER_PROC_HPP
#define STAT_LOG_CUR_WRITER_PROC_HPP

#include "statlog_writer_proc.hpp"
#include "statlog_file_mmap_writer.hpp"

class StatLogCurWriterProc : public StatLogWriterProc
{
public:
    StatLogCurWriterProc()
    {}
    virtual ~StatLogCurWriterProc()
    {}
private:
    virtual StatLogFileWriter* create_file_writer(const string& inbox_path, const string& outbox_path, 
            const string& fwtype, size_t max_fsize, uint16_t max_files = 30)
    {
        return (new (std::nothrow) StatLogFileMmapWriter(inbox_path, outbox_path, fwtype, max_fsize, max_files));
    }

    StatLogCurWriterProc(const StatLogCurWriterProc&);
    StatLogCurWriterProc& operator = (const StatLogCurWriterProc&);
};

#endif

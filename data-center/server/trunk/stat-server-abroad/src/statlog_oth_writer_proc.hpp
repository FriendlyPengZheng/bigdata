#ifndef STAT_LOG_OTH_WRITER_PROC_HPP
#define STAT_LOG_OTH_WRITER_PROC_HPP

#include "statlog_writer_proc.hpp"
#include "statlog_file_disk_writer.hpp"

class StatLogOthWriterProc : public StatLogWriterProc
{
public:
    StatLogOthWriterProc()
    {}
    virtual ~StatLogOthWriterProc()
    {}
private:
    virtual StatLogFileWriter* create_file_writer(const string& inbox_path, const string& outbox_path, 
            const string& fwtype, size_t max_fsize, uint16_t max_files = 30)
    {
        return (new (std::nothrow) StatLogFileDiskWriter(inbox_path, outbox_path, fwtype, max_fsize, max_files));
    }

    StatLogOthWriterProc(const StatLogOthWriterProc&);
    StatLogOthWriterProc& operator = (const StatLogOthWriterProc&);
};

#endif

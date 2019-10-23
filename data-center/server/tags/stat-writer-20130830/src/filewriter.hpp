#ifndef WRITER_FILEWRITER_HPP_
#define WRITER_FILEWRITER_HPP_

#include <cerrno>
#include <cstring>
#include <ctime>
#include <iomanip>
#include <map>
#include <sstream>
#include <string>
#include <utility>

#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <iter_serv/log.h>

#include "./libant/inet/inet_utils.hpp"

// 按小时把数据写入文件
class FileWriter {
public:
	/**
	 * @brief 构造FileWriter对象
	 * @param data_dir 数据文件根目录
	 * @param filename_prefix 数据文件名前缀
	 * @param maxfd 缓存打开的maxfd个文件描述符
	 */
	FileWriter(const char* data_dir, const char* filename_prefix, int maxfd = 10)
			: m_maxfd(maxfd), m_data_dir(data_dir), m_filename_prefix(filename_prefix)
		{ }
	~FileWriter();


	template <typename DataType>
	int write(const DataType& data);

	void set_filename_prefix(const std::string& prefix)
		{ m_filename_prefix = prefix; }

	void set_sub_dir(const std::string& sub_dir);

private:
	typedef std::map<std::string, int> FdHolder;
	
private:
	FdHolder			m_fds;
	FdHolder::size_type	m_maxfd;
	std::string			m_data_dir;
	std::string			m_sub_dir;
	std::string			m_filename_prefix;
};

template <typename DataType>
int FileWriter::write(const DataType& data)
{
	std::ostringstream oss;
	oss << data.timestamp / 3600 << ">|<" << m_sub_dir << "<|>" << m_filename_prefix;
	std::string key = oss.str();
	FdHolder::iterator it = m_fds.find(key);
	if (it == m_fds.end()) {
		tm t;
		time_t ts = data.timestamp;
		if (localtime_r(&ts, &t) == 0) {
			EMERG_LOG("localtime_r: %s", strerror(errno));
			return -1;
		}

		// 按天建目录
		oss.str("");
		oss << m_data_dir << '/' << m_sub_dir << '/' << t.tm_year + 1900
			<< std::setw(2) << std::setfill('0') << t.tm_mon + 1
			<< std::setw(2) << std::setfill('0') << t.tm_mday;
		mkdir(oss.str().c_str(), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH);

		// 按小时写文件
		oss << '/' << m_filename_prefix << '-' << get_local_ipaddr()
			<< '-' << std::setw(2) << std::setfill('0') << t.tm_hour;
		// TODO: 如果出现效率问题，可以改用O_NONBLOCK或O_NDELAY
		int fd = open(oss.str().c_str(), O_WRONLY | O_APPEND | O_CREAT,
						S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH);
		if (fd == -1) {
			EMERG_LOG("open: %s", strerror(errno));
			return -1;
		}

		// 只缓存m_maxfd个文件描述符
		if (m_fds.size() >= m_maxfd) {
			FdHolder::size_type cnt = m_fds.size() - m_maxfd + 1;
			for (FdHolder::size_type i = 0; i != cnt; ++i) {
				FdHolder::iterator it = m_fds.begin();
				close(it->second);
				m_fds.erase(it);
			}
		}

		std::pair<FdHolder::iterator, bool> ret = m_fds.insert(std::make_pair(key, fd));
		if (ret.second == false) {
			EMERG_LOG("insert failure: %s", oss.str().c_str());
			return -1;
		}
		it = ret.first;
	}

	oss.str("");
	oss << data;
	return ::write(it->second, oss.str().c_str(), oss.str().size());
}

#endif // WRITER_FILEWRITER_HPP_


#include <unistd.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <stdexcept>

#include "./filelister.hpp"

using namespace std;

FileLister::FileLister(const std::string& path)
	: m_dirpath(path)
{
    m_dir = opendir(m_dirpath.c_str());
	if (m_dir == 0) {
		throw runtime_error(string("opendir failed: ") + path + ", " + strerror(errno));
	}
}

FileLister::~FileLister()
{
	closedir(m_dir);
}

void FileLister::start()
{
	rewinddir(m_dir);
}

string FileLister::next()
{
	dirent* entry = readdir(m_dir);
	while (entry) {
		if (entry->d_type == DT_REG) {
			return entry->d_name;
		}
		entry = readdir(m_dir);
	}

	return "";
}

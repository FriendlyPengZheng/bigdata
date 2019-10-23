#include <unordered_set>

#include "filewriter.hpp"

using namespace std;

FileWriter::~FileWriter()
{
	for (FdHolder::iterator it = m_fds.begin(); it != m_fds.end(); ++it) {
		close(it->second);
	}
}

void FileWriter::set_sub_dir(const string& sub_dir)
{
	// static variable is shared by all callers, and that's what we need
	static unordered_set<string> dirset;

	m_sub_dir = sub_dir;
	if (dirset.find(m_sub_dir) == dirset.end()) {
		ostringstream oss;
		oss << m_data_dir << '/' << m_sub_dir;
		int r = mkdir(oss.str().c_str(), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH);
		if ((r == 0) || (errno == EEXIST)) {
			dirset.insert(m_sub_dir);
		} else {
			EMERG_LOG("mkdir failed: dir=%s err=%s", oss.str().c_str(), strerror(errno));
		}
	}
}


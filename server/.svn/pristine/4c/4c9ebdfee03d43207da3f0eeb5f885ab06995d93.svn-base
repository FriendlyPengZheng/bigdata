#include <cassert>
#include <cerrno>

#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>

#include "../string/string_utils.hpp"
#include "./fs_utils.hpp"

using namespace std;

bool makedir(const string& path)
{
	assert(path.size());

	string dir;
	vector<string> subdirs = split(path, '/');
	if (subdirs[0].empty()) {
		subdirs.erase(subdirs.begin());
	} else if (subdirs[0] == ".") {
		dir = ".";
		subdirs.erase(subdirs.begin());
	} else if (subdirs[0] == "..") {
		dir = "..";
		subdirs.erase(subdirs.begin());
	} else {
		dir = ".";
	}

	for (vector<string>::size_type i = 0; i != subdirs.size(); ++i) {
		dir += "/" + subdirs[i];
		int ret = mkdir(dir.c_str(), S_IRWXU | S_IRGRP | S_IXGRP | S_IROTH | S_IXOTH);
		if ((ret == -1) && (errno != EEXIST)) {
			return false;
		}
	}
	
	return true;
}

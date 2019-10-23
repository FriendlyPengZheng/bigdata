package com.taomee.tms.mgr.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileOperateTools {
	
	//判断文件夹是否存在
	public static Boolean isDirectory(String path) {
		File dir = new File(path);
		if (dir.exists()) {
			if (dir.isDirectory()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static List<File> getFiles(String path) {
		File dir = new File(path);
		File[] files = null;
		if (dir.exists() && dir.isDirectory()) {
			files =  dir.listFiles();
		}
		
		List<File> result = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					result.add(files[i]);
				}
			}
		}
		
		return result;
	}
}

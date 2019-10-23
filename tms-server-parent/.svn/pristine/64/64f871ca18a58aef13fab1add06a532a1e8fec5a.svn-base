package com.taomee.tms.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadTxtFile {
	public static ArrayList<String> readFile(String fileName) {
		File file = new File(fileName);

		//System.out.println("filename: " + file);
		if (file.exists()) {
			if (file.isFile()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(
							file));
					ArrayList<String> readList = new ArrayList<String>();
					readList.clear();
					String lineTxt = "";

					while ((lineTxt = br.readLine()) != null) {
						//System.out.println(lineTxt);
						//br.skip(1);
						readList.add(lineTxt);
					}
					br.close();
					return readList;
				} catch (Exception e) {
					System.err.println("文件读取错误!");
					e.printStackTrace();
				}
			} 
		}else {
			System.out.println("文件不存在!");
		}
		return null;

	}

	public static void main(String args[]) {
		ArrayList<String> str = readFile("D://hadoop//test//serverIdToGameId");
		for (String aa : str) {
			System.out.println(aa);
		}
	}
}

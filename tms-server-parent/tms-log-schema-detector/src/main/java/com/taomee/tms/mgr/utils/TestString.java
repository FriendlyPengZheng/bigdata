package com.taomee.tms.mgr.utils;

public class TestString {

	public static void main(String[] args) {
		String path = "/tms";
		// System.out.println(path.substring(0,path.lastIndexOf("/")));
		// System.out.println();
		StringBuilder path2 = new StringBuilder("/");
		for (String s : path.split("/")) {

			if (s.length() != 0) {

				path2.append(s);
				System.out.println(path2);
				path2.append("/");

			}

		}

	}

}

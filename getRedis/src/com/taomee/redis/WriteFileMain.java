package com.taomee.redis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class WriteFileMain {
	public static void main(String[] args) {
		String day = null;
		if(args.length >= 1){
			day = args[0];
		}else {
			new Throwable("args[] is empty,please enter a date parameter(eg:20190715)").printStackTrace();
			return;
		}
		/*Date date = new Date();
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd");
		String day = sFormat.format(date);*/
		
		File file = new File("follower_"+day);
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter bw = null;
		OutputStream os = null;
		try {
            os = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
			outputStreamWriter = new OutputStreamWriter(os);
			
			bw = new BufferedWriter(outputStreamWriter);
			GetListData getListData = new GetListData();
			List<String> strList = getListData.getData();
			for (String str : strList) {
				bw.write(str+"\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
	        e.printStackTrace();
	    }finally{
	    	try {
				bw.close();
				
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    }
		
	}
}

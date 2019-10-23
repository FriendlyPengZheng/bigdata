package taomee_io_reader_writer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 字符流
 * @author looper
 * @date 2016年8月2日
 */
public class CharFlow {
	
	
	public static void main(String[] args) {
		
		//Boolean flag=ByteFlow.fileCopy("d:/soft/nexus-3.0.0-03-win64.zip", "d:/soft/2.zip");
		Boolean flag=CharFlow.fileCopy("D:/soft/1.jpg", "D:/soft/2.jpg");
		System.out.println(flag);
	}
	/**
	 * 字节流 ：文件的copy
	 * @param src
	 * @param dst
	 * @return
	 */
	public static Boolean fileCopy(String src,String dst)
	{
		FileReader fis=null;
		FileWriter fos=null;
		BufferedReader bis=null;
		BufferedWriter bos=null;
		try {
			File src_file=new File(src);
			File dst_file=new File(dst);
			fis=new FileReader(src_file);
			fos=new FileWriter(dst_file);
			bis=new BufferedReader(fis);
			bos=new BufferedWriter(fos);
			char[] b=new char[1024];
			int len;
			while((len=bis.read(b))!= -1)
			{
				bos.write(b, 0, len);
				bos.flush();
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("catch");
		}finally
		{
			if(bos!=null)
			{
				try {
					bos.close();
					//System.out.println("1");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(bis!=null)
			{
				try {
					bis.close();
					//System.out.println("2");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fos!=null)
			{
				try {
					fos.close();
					//System.out.println("3");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fis!=null)
			{
				try {
					fis.close();
					//System.out.println("4");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	

}

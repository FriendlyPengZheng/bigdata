package file;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Java文件操作
 * @author looper
 * @date 2016年8月2日
 */
public class JavaFile {
	
	@Test
	public void test1()
	{
		try {
			File file=new File("d:/soft");
			//file.
			/*Boolean b=file.createNewFile();
			System.out.println(b);*/
			System.out.println(file.canExecute());
			System.out.println(file.canRead());
			System.out.println(file.canWrite());
			//System.out.println(file.getTotalSpace());
			//重命名的时候，需要file文件存在，dst不存在
			//file.renameTo(new File("hello.txt"));
			System.out.println(file.isFile());
			System.out.println(file.isDirectory());
			System.out.println(file.length());
			//System.out.println(file.delete());
			//返回的是string
			/*String []files=file.list();
			for(String s:files)
			{
				System.out.println(s);
			}*/
			//返回的是File
			File[] files=file.listFiles();
			for(File f:files)
			{
				System.out.println(f.getPath());
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

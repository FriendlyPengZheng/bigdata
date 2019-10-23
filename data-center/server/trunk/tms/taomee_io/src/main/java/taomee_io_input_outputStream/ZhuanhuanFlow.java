package taomee_io_input_outputStream;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class ZhuanhuanFlow {
	
public static void main(String[] args) {
	new ZhuanhuanFlow().copy("hello.txt", "Ta.txt");
}
	public Boolean copy(String src, String dst) {
		try {
			File file = new File(src);
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			InputStreamReader isr = new InputStreamReader(bis, "utf-8");
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(new File(dst)));
			char[] b = new char[1024];
			int len;
			while ((len = isr.read(b)) != -1) {
				bw.write(b, 0, len);
			}
			bw.close();
			isr.close();
			bis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		return false;
	}

}

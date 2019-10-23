package taomee_nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java NIO
 * 
 * @author looper
 * @date 2016年8月5日
 */
public class NIO_Examples {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(NIO_Examples.class);

	public static void main(String[] args) {
		Boolean flag=new NIO_Examples().filecopy("d:/soft/1.jpg", "d:/soft/3.jpg");
		LOGGER.info("NIO copy文件:"+flag);
	}

	public Boolean filecopy(String src, String dst) {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		FileChannel fcin = null;
		FileChannel fcout = null;
		ByteBuffer buffer = null;
		try {
			fin = new FileInputStream(new File(src));
			fout = new FileOutputStream(new File(dst));
			fcin = fin.getChannel();
			fcout = fout.getChannel();
			buffer = ByteBuffer.allocate(1024);
			LOGGER.info("buffer limit:"+buffer.limit());
			/*while (true) {
				buffer.clear();
				int r = fcin.read(buffer);
				if (r == -1) {
					break;
				}
				buffer.flip();
				fcout.write(buffer);
				return true;
			}*/
			int len;
			while((len=fcin.read(buffer))!=-1)
			{
				LOGGER.debug("buffer position:"+buffer.position());
				//通道切换
				buffer.flip();
				//数据写入
				fcout.write(buffer);
				//清空之前缓冲区的数据
				buffer.clear();
			}
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fcout != null) {
				try {
					fcout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fcin != null) {
				try {
					fcin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}

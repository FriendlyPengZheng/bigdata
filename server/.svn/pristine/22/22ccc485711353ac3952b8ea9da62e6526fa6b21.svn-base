package com.taomee.bigdata.upload;

import java.net.URI;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import com.taomee.bigdata.util.TaomeeThread;

class HDFSWriter
{
    private static final int MAX_SIZE = 1048576;//1M
    private String filename = null;
    private FileSystem fs = null;
    private FSDataOutputStream out = null;
    private StringBuilder string = new StringBuilder(MAX_SIZE);
    private int writeLength = 0;

    public FileSystem getFileSystem() { return fs; }

    public HDFSWriter(String f) {
        filename = f;
        try {
            fs = FileSystem.get(URI.create(f), new Configuration());
            Path p = new Path(f);
            if(!fs.exists(p)) {
                out = fs.create(p);
            } else {
                out = fs.append(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if(out != null) {
            try {
                write();
                out.close();
                //fs.close(); 不能在此关闭fs，否则所有打开的文件都不能写入
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        out = null;
    }

    public void write(String str) throws IOException {
        if(out == null) return;
        string.append(str);
        string.append('\n');
        if(string.length() >= MAX_SIZE) {
            write();
        }
    }

    private void write() throws IOException {
        if(string.length() == 0) {
            return ;
        }
        byte[] bytes = string.toString().getBytes("UTF-8");
        string.delete(0, string.length());
        out.write(bytes);
        writeLength += bytes.length;
        if(writeLength >= (MAX_SIZE * 100)) {
            out.close();
            try {
                Thread.sleep(100);
            } catch (java.lang.InterruptedException e) { }
            out = fs.append(new Path(filename));
            writeLength = 0;
            //System.out.println("close and open " + filename + " again.");
        }
    }

    public void finalize() {
        close();
    }
}

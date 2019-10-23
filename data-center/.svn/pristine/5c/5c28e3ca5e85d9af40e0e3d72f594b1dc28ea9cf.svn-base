package com.taomee.bigdata.upload;

import java.net.URI;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

class LocalWriter
{
    private static final int MAX_SIZE = 1024*1024*10;
    private String filename = null;
    private FileOutputStream os = null;
    private StringBuilder string = new StringBuilder(MAX_SIZE);

    public LocalWriter(String f) {
        filename = f;
        try {
            os = new FileOutputStream(filename, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if(os != null) {
            try {
                write();
                os.close();
                os = null;
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void write(String str) throws IOException {
        if(os == null) return;
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
        try {
            os.write(bytes);
            //System.err.print(string);
            string.delete(0, string.length());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        bytes = null;
    }

    public void finalize() {
        close();
    }
}

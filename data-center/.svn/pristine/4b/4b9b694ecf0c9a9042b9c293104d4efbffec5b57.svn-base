package com.taomee.bigdata.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import java.util.Iterator;

class LocalFileBuffer
{
    private TreeMap<String, File> fileList = new TreeMap<String, File>();
    private Iterator<String> it = null;
    private File currentFile = null;
    private InputStreamReader reader = null;
    private BufferedReader br = null;
    private String backupPath = null;
    private String oriBackupPath = null;
    private Integer game, day;

    public LocalFileBuffer(String path, String b) {
        getFileList(path);
        it = fileList.keySet().iterator();
        oriBackupPath = b;
        //if(it.hasNext())    open(it.next());
    }

    private void getFileList(String path) {
        File f = new File(path);
        if(!f.exists()) return;
        if(f.isDirectory()) {
            File fs[] = f.listFiles();
            for(int i=0; i<fs.length; i++) {
                if(fs[i].getName().endsWith("swp")) continue;
                if(fs[i].isDirectory()) {
                    getFileList(fs[i].getPath());
                } else {
                    fileList.put(fs[i].lastModified()+fs[i].getName(), fs[i]);
                }
            }
        } else {
            fileList.put(f.lastModified()+f.getName(), f);
        }
    }

    private void open(File f) {
        currentFile = f;
        try {
            reader = new InputStreamReader(new FileInputStream(currentFile), "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        br = new BufferedReader(reader);
        if(oriBackupPath == null) {
            backupPath = null;
        } else {
            String items[] = currentFile.getName().split("_");
            if(items.length >= 4 && items[3].length() == 8) {
                backupPath = String.format("%s/%s/%s", oriBackupPath, items[3], items[0]);
                File b = new File(backupPath);
                b.mkdirs();
            } else {
                backupPath = oriBackupPath;
            }
        }
        System.out.println("open " + currentFile.getAbsolutePath());
    }

    private void close() {
        try {
            if(br != null)  br.close();
            if(reader != null)  reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void move() {
        if(backupPath != null && currentFile != null) {
            currentFile.renameTo(new File(backupPath, currentFile.getName()));
            System.out.println(currentFile.getName() + " rename to " + backupPath);
        }
    }

    public boolean next() {
        close();
        if(it.hasNext()) {
            open(fileList.get(it.next()));
            return true;
        } else {
            currentFile = null;
            return false;
        }
    }

    public String readLine() {
        if(currentFile == null) return null;
        String str = null;
        try {
            str = br.readLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        //if(str.trim().length() == 0) {
        //    System.err.println("file=[" + currentFile.toString() + "]");
        //}
        return str == null ? null : str.trim();
    }
}

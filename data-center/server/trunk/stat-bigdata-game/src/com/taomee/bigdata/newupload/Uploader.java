package com.taomee.bigdata.newupload;

import java.util.Enumeration;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.StringBuffer;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import com.taomee.bigdata.util.TaomeeThread;
import com.taomee.bigdata.lib.Logger;
import com.taomee.bigdata.lib.TcpClient;
import com.taomee.bigdata.lib.ErrorCode;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import java.net.URI;

public class Uploader extends TaomeeThread {
    private String remotePath = null;
    private String localPath  = null;
    private String backupPath = null;
    private String ip = getIP();
    private Logger log = Logger.getInstance();
    private String statserver = null;
    private TcpClient server = null;
    private UploaderProto proto = new UploaderProto();

    private UploaderStatLogger statLogger = new UploaderStatLogger();
    private HashMap<String, String> keyValueMap = new HashMap<String, String>();
    private HashMap<String, FileSystem> fileSystemMap = new HashMap<String, FileSystem>();
    private HashSet<String> createdFileSet = new HashSet<String>();
    private HashMap<String, FSDataOutputStream> outputMap = new HashMap<String, FSDataOutputStream>();

    private static String getIP() {
        String tmp = "UnknownHost";
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while(allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if(ip != null && ip instanceof Inet4Address) {
                        String str =  ip.getHostAddress();
                        if(str.startsWith("192."))   return str;
                        else if(str.startsWith("10."))  tmp = str;
                    } 
                }
            }
        } catch (java.net.SocketException e) { }
        return tmp;
    }

    public void setStatCenter(String s) {
        statserver = s;
        String ip = s.split(":")[0];
        String port = s.split(":")[1];
        if(server == null) server = new TcpClient();
        if(!server.connect(ip, port)) {
            server = null;
        }
    }

    public String getStatCenter() {
        return statserver;
    }

    public TcpClient getStatCenterConnection() {
        return server;
    }

    public void setRemotePath(String r) {
        remotePath = r;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setLocalPath(String r) {
        localPath = r;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setBackupPath(String r) {
        backupPath = r;
    }

    public String getBackupPath() {
        return backupPath;
    }

    private static void printUsage() {
        System.out.println("Uploader " +
                "-statCenter <ip:port>" +
                "-remotePath <remote path> " +
                "-localPath <local path> " +
                "[-backupPath <backup path>]" +
                "[-sleepTime <sleep minute>]");
    }

    public static void main(String[] args) {
        Uploader up = new Uploader();
        up.setSleepTime(60);
        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-remotePath"))  {
                up.setRemotePath(args[++i]);
            } else if(args[i].equals("-localPath")) {
                up.setLocalPath(args[++i]);
            } else if(args[i].equals("-backupPath")) {
                up.setBackupPath(args[++i]);
            } else if(args[i].equals("-statCenter")) {
                up.setStatCenter(args[++i]);
            } else if(args[i].equals("-sleepTime")) {
                up.setSleepTime(Long.valueOf(args[++i]) * 60);
            }
        }

        if(up.getRemotePath() == null || up.getLocalPath() == null ||
                up.getStatCenter() == null || up.getStatCenterConnection() == null) {
            printUsage();
            return ;
        }

        up.run(args);
    }

    protected void process(String[] args) {
        //for(int i=0; i<2; i++) {
        //    reportToCenter("2", "basic", 100, i);
        //}
        //stop();
        //if(!isRunning())  return;
        Configuration conf = new Configuration();
        HashSet<File> fileList = getFileList(localPath);
        if(fileList == null) {
            log.DEBUG_LOG(localPath + " is empty");
            if(backupPath == null) {
                stop();
            }
            return ;
        }
        Iterator<File> it = fileList.iterator();
        String line;
        String game = "0";
        String timestamp = "0";
        String stid;
        String hip;
        //每个文件要写入的内容，写缓存起来，等解析文件正确后，再一次性写入
        HashMap<String, StringBuffer> fileBuffer = new HashMap<String, StringBuffer>();
        DateTransfer date = new DateTransfer();

        int fileCnt = 0;
        while(isRunning() && it.hasNext()) {
            int ret = ErrorCode.OK;
            //每次读取一个文件进行处理：
            File file = it.next();
            if(!file.exists())  continue;
            log.DEBUG_LOG("upload " + file.getName() + " ... " + System.currentTimeMillis());
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                fileBuffer.clear();

                //1. 读取一行，解析_gid_, _ts_, _stid_ => 确定要写入到hdfs中的哪个文件
                while((line = br.readLine()) != null) {
                    if(line.length() == 0 || !analysis(line))  continue;

                    //2. 解析_gid_, _ts_, _stid_ => 确定要写入到hdfs中的哪个文件
                    game = getValue("_gid_");       //游戏id
                    timestamp = getValue("_ts_");   //落统计数据的时间
                    stid = getValue("_stid_");      //统计项id
                    hip = getValue("_hip_");        //落统计项的服务器ip
                    if(game == null ||
                            timestamp == null ||
                            stid == null ||
                            hip == null)   continue;

                    //3. 确定要写入到hdfs中的哪个文件
                    String hdfsFileName = getRemoteFile(date.getDayString(Integer.valueOf(timestamp)),
                            date.getHourString(Integer.valueOf(timestamp)),
                            game,
                            stid);
                    StringBuffer buffer = fileBuffer.get(hdfsFileName);

                    //4.将要写入的内容缓存到hashMap中
                    if(buffer == null)  buffer = new StringBuffer(1024);
                    buffer.append(line);
                    buffer.append('\n');
                    fileBuffer.put(hdfsFileName, buffer);

                    //5.统计
                    statLogger.addStat(game, stid, timestamp, hip);
                }
            } catch (java.io.FileNotFoundException e) {
                log.EXCEPTION_LOG(e); 
                ret = ErrorCode.LOCAL_FILE_NOT_FOUND;
                reportToCenter(game, null, 0, ret);
                continue;
            } catch (IOException e) {
                log.EXCEPTION_LOG(e);
                ret = ErrorCode.LOCAL_IOEXP;
                reportToCenter(game, null, 0, ret);
                continue;
            } finally {
                try {
                    if(br != null) br.close();
                } catch(IOException e) {
                    log.EXCEPTION_LOG(e);
                    ret = ErrorCode.LOCAL_CLOSE_EXP;
                    reportToCenter(game, null, 0, ret);
                }
            }

            log.DEBUG_LOG("upload readok " + file.getName() + " ... " + System.currentTimeMillis());
            //6.写入hdfs
            Iterator<String> itt = fileBuffer.keySet().iterator();
            while(itt.hasNext()) {
                String filename = itt.next();
                FileSystem fs = null;
                FSDataOutputStream out = null;
                try {
                    fs = getFileSystem(filename, conf);
                    //log.DEBUG_LOG("upload get fs " + file.getName() + " ... " + System.currentTimeMillis());

                    out = getFSOutput(filename, fs);
                    //log.DEBUG_LOG("upload get out " + file.getName() + " ... " + System.currentTimeMillis());
                    if(out == null) {
                        try {
                            Thread.sleep(1000*60*15);
                            stop();
                            reportToCenter(game, filename, fileBuffer.get(filename).length(), ErrorCode.HDFS_REMOTE_EXP);
                            log.ERROR_LOG("upload get out " + file.getName() + " failed " + System.currentTimeMillis());
                            statLogger.writeStat();
                            closeOutput();
                            return;
                        } catch (Exception e) {
                            log.EXCEPTION_LOG(e);
                            return;
                        }
                    }

                    BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(fileBuffer.get(filename).toString().getBytes("UTF-8")));
                    //log.DEBUG_LOG("upload get is " + file.getName() + " ... " + System.currentTimeMillis());

                    IOUtils.copyBytes(is, out, 4096*1024, false);   //自定义数据会比较大，给4M缓存
                    out.flush();
                    is.close();

                } catch (org.apache.hadoop.ipc.RemoteException re) {
                    log.EXCEPTION_LOG(re);
                    IOException e = re.unwrapRemoteException();
                    ret = ErrorCode.HDFS_REMOTE_EXP;
                } catch (java.io.UnsupportedEncodingException e) {
                    log.EXCEPTION_LOG(e);
                    ret = ErrorCode.HDFS_NOT_UTF8;
                } catch (IOException e) {
                    log.EXCEPTION_LOG(e);
                    ret = ErrorCode.HDFS_IOEXP;
                }
                reportToCenter(game, filename, fileBuffer.get(filename).length(), ret);
            }
            log.DEBUG_LOG("upload writehdfs " + file.getName() + " ... " + System.currentTimeMillis());
            if(backupPath != null && ret == ErrorCode.OK) {
                String b = backupPath + "/" + date.getDayString(Integer.valueOf(timestamp)) + "/" + game;
                (new File(b)).mkdirs();
                file.renameTo(new File(b, file.getName()));
            }

            if(++fileCnt == 200) {
                ret = closeOutput();
                fileCnt = 0;
                //log.DEBUG_LOG("upload closehdfs " + file.getName() + " ... " + System.currentTimeMillis());
            }
        }
        statLogger.writeStat();
        int ret = closeOutput();
        log.DEBUG_LOG("upload writestat " + System.currentTimeMillis());

        //不对文件进行备份移动的话，上传完就退出，避免重复上传
        if(backupPath == null) {
            stop();
        } else {
            log.DEBUG_LOG("sleep ...");
        }
    }

    protected void exit() {
        log.DEBUG_LOG("exit");
    }

    private HashSet<File> getFileList(String path) {
        File f = new File(path);
        if(!f.exists()) return null;
        HashSet<File> fileList = new HashSet<File>();
        if(f.isDirectory()) {
            File fs[] = f.listFiles();
            for(int i=0; i<fs.length; i++) {
                //跳过临时文件，或者不存在的文件
                if(fs[i].getName().endsWith("swp") || !fs[i].exists()) continue;
                //递归调用，支持多级路径
                if(fs[i].isDirectory()) {
                    HashSet<File> cfs = getFileList(fs[i].getPath());
                    Iterator<File> it = cfs.iterator();
                    while(it.hasNext()) {
                        fileList.add(it.next());
                    }
                } else {
                    fileList.add(fs[i]);
                }
            }
        } else {
            fileList.add(f);
        }
        if(fileList.size() == 0)    return null;
        return fileList;
    }

    private boolean analysis(String line) {
        keyValueMap.clear();
        String items[] = line.split("\t");
        for(int i=0; i<items.length; i++) {
            String kv[] = items[i].split("=");
            if(kv.length != 2)  return false;
            keyValueMap.put(kv[0], kv[1]);
        }
        return true;
    }

    private String getValue(String key) {
        return keyValueMap.get(key);
    }

    private String getRemoteFile(String day, String hour, String game, String stid) {
        // return /bigdata/input/20140901/2/192.168.1.177_15_basic
        String type = stid.startsWith("_") ? "basic" : "custom";
        return String.format("%s/%s/%s/%s_%s_%s", remotePath, day, game, ip, hour, type);
    }

    private FileSystem getFileSystem(String filename, Configuration conf) throws IOException {
        FileSystem fs = fileSystemMap.get(filename);
        if(fs == null) {
            fs = FileSystem.get(URI.create(filename), conf);
            fileSystemMap.put(filename, fs);
        }
        return fs;
    }

    private FSDataOutputStream getFSOutput(String filename, FileSystem fs) {
        FSDataOutputStream out;
        try {
            if(createdFileSet.contains(filename)) {
                out = outputMap.get(filename);
                if(out == null) {
                    out = fs.append(new Path(filename));
                    outputMap.put(filename, out);
                }
                createdFileSet.add(filename);
                return out;
            }
            if (!fs.exists(new Path(filename))) {
                out = outputMap.get(filename);
                if(out == null) {
                    out = fs.create(new Path(filename));
                    outputMap.put(filename, out);
                }
            } else {
                out = outputMap.get(filename);
                if(out == null) {
                    out = fs.append(new Path(filename));
                    outputMap.put(filename, out);
                }
            }
            createdFileSet.add(filename);
            return out;
        } catch (Exception e) {
            log.EXCEPTION_LOG(e);
            return null;
        }
    }

    private int closeOutput() {
        int ret = ErrorCode.OK;
        Iterator<String> it = outputMap.keySet().iterator();
        while(it.hasNext()) {
            String filename = it.next();
            FSDataOutputStream out = outputMap.get(filename);
            if(out == null) {
                ret = ErrorCode.CLOSE_NULL;   //should never be here
                statLogger.addErrorStat(ret, filename);
                continue;
            }
            try {
                out.close();
            } catch (IOException e) {
                ret = ErrorCode.CLOSE_EXP;
            }
        }
        outputMap.clear();
        return ret;
    }

    private void reportToCenter(String game, String filename, int length, int ret) {
        proto.setIp(this.ip);
        proto.setGameId(Integer.valueOf(game));
        proto.setBasicCnt(0);
        proto.setCustomCnt(0);
        if(filename != null) {
            if(filename.endsWith("basic")) {
                proto.setBasicCnt(length);
            } else {
                proto.setCustomCnt(length);
            }
        }
        proto.setEFlag(ret);

        server.send(proto.getPackage());
        server.recv(9);
    }

}

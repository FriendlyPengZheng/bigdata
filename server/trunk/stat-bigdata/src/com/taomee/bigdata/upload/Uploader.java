package com.taomee.bigdata.upload;

import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.InetAddress;
import com.taomee.bigdata.lib.TcpClient;
import com.taomee.bigdata.lib.ErrorCode;

public class Uploader
{
    private String statserver = null;
    private TcpClient server = null;
    private UploaderProto proto = new UploaderProto();
    private String ip = getIP();
    private HashSet<String> warnings = new HashSet<String>();
    private DateTransfer date = new DateTransfer();

    private static void printUsage() {
        System.out.println("Uploader " +
                "-statCenter <ip:port> " +
                "-localPath <local path> " +
                "[-backupPath <backup path>]");
    }

    private static int getTimestamp(String str) {
        if(str.contains("_ts_")) {
            int i=-1;
            try {
                i = Integer.valueOf(str.substring(str.lastIndexOf("_ts_=")+5).split("\t")[0]);
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.err.println("str=[" + str + "]");
            } catch (java.lang.NumberFormatException e) {
                System.err.println("str=[" + str + "]");
            } catch (Exception e) {
                System.err.println("str=[" + str + "]");
                System.err.println(e.getMessage());
            }
            return i;
        } else {
            return -1;
        }
    }

    private static String getGame(String str) {
        if(str.contains("_gid_")) {
            String ret = null;
            try {
                ret = str.substring(str.lastIndexOf("_gid_=")+6).split("\t")[0];
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.err.println("str=[" + str + "]");
            } catch (Exception e) {
                System.err.println("str=[" + str + "]");
                System.err.println(e.getMessage());
            }
            return ret;
        } else {
            return null;
        }
    }

    public static String getIP() {
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

    private static LocalWriter getRemoteFile(HashMap<String, LocalWriter> map, String day, String hour, String game, String name) throws IOException {
        String key;
        if(Integer.valueOf(game) == 10000) {
            key = String.format("%s:%s:%s", day, game, name);
        } else {
            key = String.format("%s:%s:%s:%s", day, hour, game, name);
        }
        LocalWriter ret = map.get(key);
        if(ret == null) {
            System.out.println(String.format("data/%s/%s/%s", day, game, name));
            (new File(String.format("data/%s/%s", day, game))).mkdirs();
            ret = new LocalWriter(String.format("data/%s/%s/%s", day, game, name));
            map.put(key, ret);
        }
        return ret;
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

    public int run(String args[]) {
        //UploaderStatLogger statLogger = new UploaderStatLogger();
        String filename = null;
        String game = null;
        Iterator<String> it = null;
        HashMap<String, LocalWriter> files = new HashMap<String, LocalWriter>();
        String localPath = null;    //本地路径 该路径下所有文件都会被上传
        String backupPath = null;   //备份路径 上传后的文件会被移动到此路径下

        for(int i=0; i<args.length; i++) {
            if(args[i].equals("-localPath")) {
                localPath = args[++i];
            } else if(args[i].equals("-backupPath")) {
                backupPath = args[++i];
            } else if(args[i].equals("-statCenter")) {
                setStatCenter(args[++i]);
            }
        }

        if(localPath == null || server == null) {
            printUsage();
            return -1;
        }

        LocalFileBuffer local = new LocalFileBuffer(localPath, backupPath);
        String str = null;
        LocalWriter writer = null;
        int fileCnt = 0;
        boolean done;
        while(local.next() && fileCnt++ < 1000) {
            int lineCnt = 0;
            done = true;
            while((str = local.readLine()) != null) {
                lineCnt++;
                if(lineCnt%1000==0) System.out.println(lineCnt);
                try {
                    if(str.length() == 0)   continue;
                    int d = getTimestamp(str);
                    //if(d == -1) return -1;
                    if(d == -1 || d <= 946656000 ) continue; //2000-01-01 00:00:00
                    game = getGame(str);
                    if(game == null) continue;
                    boolean isBasic = (str.contains("_stid_=_")) ? true : false;
                    try {
                        filename = getFileName(ip, isBasic, d, Integer.valueOf(game));
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    writer = getRemoteFile(files,
                            date.getDayString(d),
                            date.getHourString(d),
                            game,
                            filename);
                    writer.write(str);
                    //statLogger.addStat(game, (isBasic ? "_basic_" : "custom"), String.valueOf(d), ip);
                //} catch (org.apache.hadoop.ipc.RemoteException re) {
                    //re.printStackTrace();
                    ////IOException e = re.unwrapRemoteException();
                    ////System.out.println(e.getClass().getName());
                    //reportToCenter(game, filename, 0, ErrorCode.HDFS_REMOTE_EXP);
                    //done = false;
                    //continue;
                //} catch (IOException e) {
                    //done = false;
                    //continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    reportToCenter(game, filename, 0, ErrorCode.HDFS_IOEXP);
                    done = false;
                    continue;
                }
            }
            if(done) {
                local.move();
            }
            if(fileCnt%10==0) {
                it = files.keySet().iterator();
                while(it.hasNext()) {
                    files.get(it.next()).close();
                }
                files.clear();
            }
        }
        //statLogger.writeStat();
        it = files.keySet().iterator();
        while(it.hasNext()) {
            files.get(it.next()).close();
        }
        //it = files.keySet().iterator();
        //if(it.hasNext()) {
        //    FileSystem fs = files.get(it.next()).getFileSystem();//关闭文件系统，只需关闭一次
        //    try {
        //        fs.close();
        //    } catch (IOException e1) {
        //        e1.printStackTrace();
        //        reportToCenter(game, filename, 0, ErrorCode.HDFS_IOEXP);
        //    }
        //}
        return 0;
    }

    private String getFileName(String ip, boolean isBasic, int d, int game) {
        if(game == 10000) {
            return ip + (isBasic ? "_basic" : "_custom");
        } else {
            return ip + "_" + date.getHourString(d) + (isBasic ? "_basic" : "_custom");
        }
    }

    private void reportToCenter(String game, String filename, int length, int ret) {
        int gameid = Integer.valueOf(game);
        String type = null;
        if(filename != null) {
            if(filename.endsWith("basic")) {
                proto.setBasicCnt(length);
                type = "basic";
            } else {
                proto.setCustomCnt(length);
                type = "custom";
            }
        }
        proto.setIp(this.ip);
        proto.setGameId(gameid);
        proto.setBasicCnt(0);
        proto.setCustomCnt(0);
        proto.setEFlag(ret);

        String key = String.format("%d_%s", gameid, type);
        if(!warnings.contains(key)) {
            server.send(proto.getPackage());
            server.recv(9);
            warnings.add(key);
        }
    }

    public static void main(String args[]) throws Exception {
        System.exit((new Uploader()).run(args));
    }
}

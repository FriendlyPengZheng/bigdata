package com.taomee.bigdata.newupload;

import com.taomee.bigdata.monitor.util.StatLogger;
import com.taomee.bigdata.lib.Logger;
import com.taomee.bigdata.lib.ErrorCode;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileWriter;

public class UploaderStatLogger extends StatLogger {
    private Logger log = Logger.getInstance();
    private int addCnt = 0;
    //记录每分钟上传条数
    private HashMap<Integer, Integer> minuteCntMap = new HashMap<Integer, Integer>();
    //记录每个游戏，每个ip，每分钟上传条数
    private HashMap<String, HashMap<Integer, Integer>> gameIpTypeCntMap = new HashMap<String, HashMap<Integer, Integer>>();
    public UploaderStatLogger() {
        super(10000);
    }

    public UploaderStatLogger(int game) {
        super(game);
    }

    public void addStat(String game, String stid, String time, String hip) {
        Integer cTime = (int)(System.currentTimeMillis()/1000);
        cTime -= (cTime%60);
        Integer cnt = minuteCntMap.get(cTime);
        if(cnt == null) cnt = 0;
        cnt++;
        minuteCntMap.put(cTime, cnt);
        String key = String.format("%s-%s-%s", game, hip, stid.startsWith("_") ? "基础项" : "自定义项");
        HashMap<Integer, Integer> gameMinuteCntMap = gameIpTypeCntMap.get(key);
        if(gameMinuteCntMap == null)    gameMinuteCntMap = new HashMap<Integer, Integer>();
        Integer t = Integer.valueOf(time);
        t -= (t%60);
        cnt = gameMinuteCntMap.get(t);
        if(cnt == null) cnt = 0;
        cnt++;
        gameMinuteCntMap.put(t, cnt);
        gameIpTypeCntMap.put(key, gameMinuteCntMap);
        if(++addCnt == 10000) {
            writeStat();
            addCnt = 0;
        }
    }

    public void writeStat() {
        String filename = getFileName();
        try {
            FileWriter file = null;
            for(int i=0; i<10; i++) {
                try {
                    filename = getFileName();
                    file = new FileWriter(filename, true);
                    break;
                } catch(IOException e) {
                    log.EXCEPTION_LOG(e);
                    try {
                        Thread.sleep(1000);
                    } catch (java.lang.InterruptedException e1) { }
                }
            }
            if(file == null)    return;
            Iterator<Integer> mit = minuteCntMap.keySet().iterator();
            while(mit.hasNext()) {
                Integer m = mit.next();
                Integer c = minuteCntMap.get(m);
                String s = String.format("_hip_=%s\t_stid_=_monitor_\t_sstid_=_uploader_\t_gid_=%d\t_zid_=%d\t_sid_=%d\t_pid_=%d\t_ts_=%d\t_acid_=-1\t_plid_=-1\tcnt=%d\t_op_=item_sum:_hip_,cnt\n", ip, game, zone, server, platform, m, c);
                file.write(s);
            }
            Iterator<String> kit = gameIpTypeCntMap.keySet().iterator();
            while(kit.hasNext()) {
                String k = kit.next();
                HashMap<Integer, Integer> gameMinuteCntMap = gameIpTypeCntMap.get(k);
                mit = gameMinuteCntMap.keySet().iterator();
                while(mit.hasNext()) {
                    Integer m = mit.next();
                    Integer c = gameMinuteCntMap.get(m);
                    String s = String.format("_hip_=%s\t_stid_=_monitor_\t_sstid_=_uploader_\t_gid_=%d\t_zid_=%d\t_sid_=%d\t_pid_=%d\t_ts_=%d\t_acid_=-1\t_plid_=-1\tGame-Ip-Type=%s\tcnt=%d\t_op_=item_sum:Game-Ip-Type,cnt\n", ip, game, zone, server, platform, m, k, c);
                    file.write(s);
                }
            }
            file.close();
        } catch (IOException e) {
            log.EXCEPTION_LOG(e);
        }
        minuteCntMap.clear();
        gameIpTypeCntMap.clear();
    }

    public void addErrorStat(int errorCode, String filepath) {
        String filename = getFileName();
        try {
            FileWriter file = new FileWriter(filename, true);
            String s = String.format("_hip_=%s\t_stid_=_monitor_\t_sstid_=_uploader_\t_gid_=%d\t_zid_=%d\t_sid_=%d\t_pid_=%d\t_ts_=%d\t_acid_=-1\t_plid_=-1\tresaon=%s\tfile=%s\t_op_=item:reason\n", ip, game, zone, server, platform, ErrorCode.getErrorString(errorCode), filepath);
            file.write(s);
            file.close();
        } catch (IOException e) {
            log.EXCEPTION_LOG(e);
        }
    }

}

package com.taomee.bigdata.monitor.util;

import com.taomee.bigdata.upload.Uploader;
import java.util.HashSet;
import java.util.Iterator;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class StatLogger {
    protected int game;
    protected int platform;
    protected int zone;
    protected int server;
    protected final String ip = Uploader.getIP();
    protected static final String path = "/opt/taomee/stat/data/inbox";

    public StatLogger(int game) {
        this(game, -1, -1, -1);
    }

    public StatLogger(int game, int platform, int zone, int server) {
        this.game = game;
        this.platform = platform;
        this.zone = zone;
        this.server = server;
    }

    private void setPermission(String file) {
        File f = new File(file);
        try {
            if(!f.exists()) f.createNewFile();
        } catch (IOException e) {

        }
        f.setExecutable(true, false);
        f.setReadable(true, false);
        f.setWritable(true, false);
    }

    protected String getFileName() {
        long time = System.currentTimeMillis()/1000;
        return getFileName(time);
    }

    protected String getFileName(long time) {
        String filename = String.format("%s/%d_game_basic_%d", path, game, time);
        setPermission(filename);
        return filename;
    }

    public void addStat(HashSet<Logger> loggers, long time) {
        Iterator<Logger> it = loggers.iterator();
        String filename = getFileName(time);
        try {
            FileWriter file = new FileWriter(filename, true);
            while(it.hasNext()) {
                Logger logger = it.next();
                String stid = logger.getStid();
                String sstid = logger.getSstid();
                BigDecimal value = BigDecimal.valueOf(logger.getValue()).stripTrailingZeros();
                if(stid != null && sstid.length() != 0 &&
                        sstid != null && sstid.length() != 0) {
                    String s = String.format("_hip_=%s\t_stid_=%s\t_sstid_=%s\t_gid_=%d\t_zid_=%d\t_sid_=%d\t_pid_=%d\t_ts_=%d\t_acid_=-1\t_plid_=-1\tvalue=%s\t_op_=item_set:_hip_,value\n",
                            ip, stid, sstid, game, zone, server, platform, time, value.toPlainString());
                    file.write(s);
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

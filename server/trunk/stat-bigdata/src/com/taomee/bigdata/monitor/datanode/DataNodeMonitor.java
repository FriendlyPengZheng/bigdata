package com.taomee.bigdata.monitor.datanode;

import com.taomee.common.json.*;
import com.taomee.bigdata.upload.Uploader;
import com.taomee.bigdata.monitor.util.*;
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.LinkedHashMap;
import java.util.HashSet;

public class DataNodeMonitor extends Monitor
{
    private final String ip = Uploader.getIP();
    private static final int port = 50075;
    private DataNodeHeartbeat reporter = new DataNodeHeartbeat();
    private DataNodeRegister register = new DataNodeRegister();

    private long remaining;
    private long capacity;
    private long dfsused;
    private float dfsusedPercent;
    private String storageInfo;

    public DataNodeMonitor() {
        //register.setPort(port); //short类型溢出 
    }

    public Protocol getProtocol() {
        return reporter;
    }

    public Register getRegister() {
        return register;
    }

    public byte[] report(String[] args) {
        try {
            long time = System.currentTimeMillis()/1000;
            logger.clear();
            String ret = HttpClient.readContentFromGet("http://"+ip+":"+port+"/jmx?qry=Hadoop:service=DataNode,name=FSDatasetState*");
            JSONArray array = JSONDecode.decode(ret);
            remaining = Long.valueOf(array.get("beans").get(0).get("Remaining").get());
            capacity  = Long.valueOf(array.get("beans").get(0).get("Capacity").get());
            dfsused   = Long.valueOf(array.get("beans").get(0).get("DfsUsed").get());
            storageInfo = array.get("beans").get(0).get("StorageInfo").get();
            dfsusedPercent = (float) ((dfsused + 0.0f) / capacity * 100.0);
            reporter.setRemaining(remaining);
            reporter.setCapacity(capacity);
            reporter.setDfsused(dfsused);
            reporter.setDfsusedPercent(dfsusedPercent);
            reporter.setStorageInfo(storageInfo);
            logger.add(new Logger("_datanode_", "remaining(G)", remaining/1024.0/1024.0/1024.0));
            logger.add(new Logger("_datanode_", "capacity(G)", capacity/1024.0/1024.0/1024.0));
            logger.add(new Logger("_datanode_", "dfsused(G)", dfsused/1024.0/1024.0/1024.0));
            logger.add(new Logger("_datanode_", "dfsusedPercent(%)", dfsusedPercent));
            stat_logger.addStat(logger, time);
        } catch (IOException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
        return reporter.pack();
    }
}

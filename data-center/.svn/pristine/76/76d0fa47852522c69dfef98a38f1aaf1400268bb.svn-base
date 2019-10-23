package com.taomee.bigdata.monitor.namenode;

import com.taomee.bigdata.monitor.util.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.hdfs.server.common.UpgradeStatusReport;
import org.apache.hadoop.hdfs.protocol.FSConstants.DatanodeReportType;
import org.apache.hadoop.hdfs.DistributedFileSystem.DiskStatus;
import org.apache.hadoop.hdfs.protocol.FSConstants.UpgradeAction;
import org.apache.hadoop.hdfs.protocol.FSConstants;
import org.apache.hadoop.conf.Configuration;

public class NameNodeMonitor extends Monitor
{
    private NameNodeHeartbeat reporter = new NameNodeHeartbeat();
    private NameNodeRegister register = new NameNodeRegister();

    private Configuration conf;
    public NameNodeMonitor() {
        conf = new Configuration();
    }

    public Protocol getProtocol() {
        return reporter;
    }

    public Register getRegister() {
        return register;
    }

    public byte[]report(String[] args) {
        DistributedFileSystem dfs;
        try {
            dfs = (DistributedFileSystem) (FileSystem.get(conf));
        } catch (IOException e) {
            return null;
        }
        try {
            long time = System.currentTimeMillis()/1000;
            logger.clear();
            DiskStatus ds = dfs.getDiskStatus();
            long capacity = ds.getCapacity();
            long used = ds.getDfsUsed();
            long remaining = ds.getRemaining();
            long presentCapacity = used + remaining;
            boolean mode = dfs.setSafeMode(FSConstants.SafeModeAction.SAFEMODE_GET);
            if (mode) {
                reporter.setSafeMode(1);
                logger.add(new Logger("_namenode_", "safemode", 1));
            } else {
                reporter.setSafeMode(0);
                logger.add(new Logger("_namenode_", "safemode", 0));
            }
            //if (status != null) {
            //    reporter.put("Upgrade status report", status.getStatusText(false));
            //} else {
            //    reporter.remove("Upgrade status report");
            //}
            reporter.setConfiguredCapacity(capacity);
            logger.add(new Logger("_namenode_", "configured_capacity(G)", capacity/1024.0/1024.0/1024.0));
            reporter.setPresentCapacity(presentCapacity);
            logger.add(new Logger("_namenode_", "persent_capacity(G)", presentCapacity/1024.0/1024.0/1024.0));
            reporter.setDfsRemaining(remaining);
            logger.add(new Logger("_namenode_", "remaining(G)", remaining/1024.0/1024.0/1024.0));
            reporter.setDfsused(used);
            logger.add(new Logger("_namenode_", "used(G)", used/1024.0/1024.0/1024.0));
            float usedPercent = (float)(((1.0 * used) / presentCapacity) * 100);
            reporter.setDfsusedPercent(usedPercent);
            logger.add(new Logger("_namenode_", "used_percent(%)", usedPercent));
            reporter.setUnderReplicatedBlocks(dfs.getUnderReplicatedBlocksCount());
            logger.add(new Logger("_namenode_", "under_replicated_blocks", dfs.getUnderReplicatedBlocksCount()));
            reporter.setMissingBlocks(dfs.getMissingBlocksCount());
            logger.add(new Logger("_namenode_", "missing_blocks", dfs.getMissingBlocksCount()));

            DatanodeInfo[] live = dfs.getClient().datanodeReport(
                    DatanodeReportType.LIVE);
            DatanodeInfo[] dead = dfs.getClient().datanodeReport(
                    DatanodeReportType.DEAD);
            reporter.setTotalDatanodes(live.length + dead.length);
            logger.add(new Logger("_namenode_", "total_datanodes", live.length + dead.length));
            reporter.setLiveNodes(live.length);
            logger.add(new Logger("_namenode_", "live_nodes", live.length));
            reporter.setDeadNodes(dead.length);
            logger.add(new Logger("_namenode_", "dead_nodes", dead.length));
            stat_logger.addStat(logger, time);

            //for (DatanodeInfo dn : live) {
            //    System.out.println(dn.getDatanodeReport());
            //    System.out.println();
            //}
            for (DatanodeInfo dn : dead) {
                System.err.println("[" + time + "]" + dn.getDatanodeReport());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return null;
        } finally {
            try {
                dfs.close();
            } catch (IOException e) { }
        }
        return reporter.pack();
    }
}

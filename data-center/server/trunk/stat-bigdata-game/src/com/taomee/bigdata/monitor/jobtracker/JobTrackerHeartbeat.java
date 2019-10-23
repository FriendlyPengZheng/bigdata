package com.taomee.bigdata.monitor.jobtracker;

import com.taomee.bigdata.monitor.util.Protocol;
import java.nio.ByteBuffer;

public class JobTrackerHeartbeat extends Protocol
{
    private int activeTaskTrackers;
    private int blackListedTaskTrackers;
    private int runningMapTasks;
    private int maxMapTasks;
    private int runningReduceTasks;
    private int maxReduceTasks;
    private int failed;
    private int killed;
    private int prep;
    private int running;
    private String failedJobId;  //最近执行

    public void setActiveTaskTrackers(int activeTaskTrackers) {
        this.activeTaskTrackers = activeTaskTrackers;
    }
    public void setBlackListedTaskTrackers(int blackListedTaskTrackers) {
        this.blackListedTaskTrackers = blackListedTaskTrackers;
    }
    public void setRunningMapTasks(int runningMapTasks) {
        this.runningMapTasks = runningMapTasks;
    }
    public void setMaxMapTasks(int maxMapTasks) {
        this.maxMapTasks = maxMapTasks;
    }
    public void setRunningReduceTasks(int runningReduceTasks) {
        this.runningReduceTasks = runningReduceTasks;
    }
    public void setMaxReduceTasks(int maxReduceTasks) {
        this.maxReduceTasks = maxReduceTasks;
    }
    public void setFailed(int failed) {
        this.failed = failed;
    }
    public void setKilled(int killed) {
        this.killed = killed;
    }
    public void setPrep(int prep) {
        this.prep = prep;
    }
    public void setRunning(int running) {
        this.running = running;
    }
    public void setFailedJobId(String failedJobId) {
        this.failedJobId = failedJobId;
    }

    public JobTrackerHeartbeat() {
        proto_id = Protocol.CMD_JOBTRACK;
        module = Protocol.MODULE_JOBTRACK;
    }

    public byte[] pack() {
        byte[] sFailedJobID = toLH(failedJobId);
        len = getHeaderLength() + 40 + sFailedJobID.length;
        byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.put(packHeader());
        byteBuffer.put(toLH(activeTaskTrackers));
        byteBuffer.put(toLH(blackListedTaskTrackers));
        byteBuffer.put(toLH(runningMapTasks));
        byteBuffer.put(toLH(maxMapTasks));
        byteBuffer.put(toLH(runningReduceTasks));
        byteBuffer.put(toLH(maxReduceTasks));
        byteBuffer.put(toLH(failed));
        byteBuffer.put(toLH(killed));
        byteBuffer.put(toLH(prep));
        byteBuffer.put(toLH(running));
        byteBuffer.put(sFailedJobID);
        return byteBuffer.array();
    }
}

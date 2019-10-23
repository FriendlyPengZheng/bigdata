package com.taomee.bigdata.monitor.namenode;

import com.taomee.bigdata.monitor.util.Protocol;
import java.nio.ByteBuffer;

public class NameNodeHeartbeat extends Protocol
{
    private int safeMode;
    private long configuredCapacity;
    private long presentCapacity;
    private long dfsRemaining;
    private long dfsused;
    private float dfsusedPrecent;
    private long underReplicatedBlocks;
    private long missingBlocks;
    private int totalDatanodes;
    private int liveNodes;
    private int deadNodes;

    public void setSafeMode(int safeMode) {
        this.safeMode = safeMode;
    }
    public void setConfiguredCapacity(long configuredCapacity) {
        this.configuredCapacity = configuredCapacity;
    }
    public void setPresentCapacity(long presentCapacity) {
        this.presentCapacity = presentCapacity;
    }
    public void setDfsRemaining(long dfsRemaining) {
        this.dfsRemaining = dfsRemaining;
    }
    public void setDfsused(long dfsused) {
        this.dfsused = dfsused;
    }
    public void setDfsusedPercent(float dfsusedPrecent) {
        this.dfsusedPrecent = dfsusedPrecent;
    }
    public void setUnderReplicatedBlocks(long underReplicatedBlocks) {
        this.underReplicatedBlocks = underReplicatedBlocks;
    }
    public void setMissingBlocks(long missingBlocks) {
        this.missingBlocks = missingBlocks;
    }
    public void setTotalDatanodes(int totalDatanodes) {
        this.totalDatanodes = totalDatanodes;
    }
    public void setLiveNodes(int liveNodes) {
        this.liveNodes = liveNodes;
    }
    public void setDeadNodes(int deadNodes) {
        this.deadNodes = deadNodes;
    }

    public NameNodeHeartbeat() {
        proto_id = Protocol.CMD_NAMENODE;
        module = Protocol.MODULE_NAMENODE;
        len = getHeaderLength() + 68;
        byteBuffer = ByteBuffer.allocate(len);
    }

    public byte[] pack() {
        byteBuffer.clear();
        byteBuffer.put(packHeader());
        byteBuffer.put(toLH(safeMode));
        byteBuffer.put(toLH(configuredCapacity));
        byteBuffer.put(toLH(presentCapacity));
        byteBuffer.put(toLH(dfsRemaining));
        byteBuffer.put(toLH(dfsused));
        byteBuffer.put(toLH(dfsusedPrecent));
        byteBuffer.put(toLH(underReplicatedBlocks));
        byteBuffer.put(toLH(missingBlocks));
        byteBuffer.put(toLH(totalDatanodes));
        byteBuffer.put(toLH(liveNodes));
        byteBuffer.put(toLH(deadNodes));
        return byteBuffer.array();
    }
}

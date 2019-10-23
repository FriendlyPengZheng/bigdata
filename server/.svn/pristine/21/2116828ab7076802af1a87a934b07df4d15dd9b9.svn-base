package com.taomee.bigdata.monitor.datanode;

import com.taomee.bigdata.monitor.util.Protocol;
import java.nio.ByteBuffer;

public class DataNodeHeartbeat extends Protocol
{
    private long remaining;
    private long capacity;
    private long dfsused;
    private float dfsusedPercent;
    private String storageInfo;

    public DataNodeHeartbeat() {
        proto_id = Protocol.CMD_DATENODE;
        module = Protocol.MODULE_DATANODE;
    }

    public void setRemaining(long r) {
        remaining = r;
    }

    public void setCapacity(long c) {
        capacity = c;
    }

    public void setDfsused(long d) {
        dfsused = d;
    }

    public void setDfsusedPercent(float f) {
        dfsusedPercent = f;
    }

    public void setStorageInfo(String s) {
        storageInfo = s;
    }

    public byte[] pack() {
        byte[] bStorageInfo = toLH(storageInfo);
        len = getHeaderLength() + 28 + bStorageInfo.length;
        byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.put(packHeader());
        byteBuffer.put(toLH(capacity));
        byteBuffer.put(toLH(remaining));
        byteBuffer.put(toLH(dfsused));
        byteBuffer.put(toLH(dfsusedPercent));
        byteBuffer.put(bStorageInfo);
        return byteBuffer.array();
    }
}

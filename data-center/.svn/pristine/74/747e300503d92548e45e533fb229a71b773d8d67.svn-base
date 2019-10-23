package com.taomee.bigdata.monitor.util;

import java.nio.ByteBuffer;

public class HeartbeatRet {
    protected int len;
    protected int proto_id;
    protected byte ret;

    public int getLength() {
        return 4+4+1;
    }

    public void unpack(byte[] b) {
        if(b == null || b.length != getLength()) {
            return ;
        }
        ByteBuffer buffer = ByteBuffer.allocate(b.length);
        buffer.put(b);
        buffer.rewind();
        len = buffer.getInt();
        proto_id = buffer.getInt();
        ret = buffer.get();
    }

    public int getLen() {
        return len;
    }

    public int getProtoid() {
        return proto_id;
    }
    
    public byte getRet() {
        return ret;
    }
}

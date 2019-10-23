package com.taomee.bigdata.monitor.util;

import java.nio.ByteBuffer;

public class Register extends Protocol
{
    private short port = 0;

    public Register() {
        proto_id = Protocol.CMD_REGISTER;
        byteBuffer = ByteBuffer.allocate(getHeaderLength() + 2);
    }

    public byte[] pack() {
        byteBuffer.clear();
        len = getHeaderLength() + 2;
        byteBuffer.put(packHeader());
        byteBuffer.put(toLH(port));
        return byteBuffer.array();
    }

    public void setPort(short p) {
        port = p;
    }

    public void setModule(byte m) {
        module = m;
    }
}

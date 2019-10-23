package com.taomee.bigdata.upload;

import com.taomee.bigdata.lib.TcpClient;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UploaderProto {
    private int len = 33;
    private int protoId = 0xa014;
    private byte moduleType = 11;
    private byte ip[];
    private int gameId;
    private int time;
    private int basicCnt;
    private int customCnt;
    private int eFlag;

    public void setIp(String ip) {
        try {
            InetAddress iadd = InetAddress.getByName(ip);
            this.ip = iadd.getAddress();
        } catch (java.net.UnknownHostException e) {
            try {
                this.ip = InetAddress.getByName("127.0.0.1").getAddress();
            } catch (java.net.UnknownHostException e1) { }
        }
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void setBasicCnt(int basicCnt) {
        this.basicCnt = basicCnt;
    }

    public void setCustomCnt(int customCnt) {
        this.customCnt = customCnt;
    }

    public void setEFlag(int eFlag) {
        this.eFlag = eFlag;
    }

    public byte[] getPackage() {
        time = (int)(System.currentTimeMillis()/1000);
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(TcpClient.toLH(len));
        buffer.put(TcpClient.toLH(protoId));
        buffer.put(moduleType);
        buffer.put(ip);
        buffer.put(TcpClient.toLH(gameId));
        buffer.put(TcpClient.toLH(time));
        buffer.put(TcpClient.toLH(basicCnt));
        buffer.put(TcpClient.toLH(customCnt));
        buffer.put(TcpClient.toLH(eFlag));
        return buffer.array();
    }
}

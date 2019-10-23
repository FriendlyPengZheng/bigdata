package com.taomee.bigdata.monitor.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.net.SocketAddress;

class SocketClient //阻塞的socket客户端
{
    Socket socket = null;
    String address = null;
    int port;
    int timeout = 0;

    private SocketClient() { }
    
    public static SocketClient connect(String ip, int port) {
        return connect(ip, port, 3);
    }

    public static SocketClient connect(String ip, int port, int timeout) {
        SocketClient client = new SocketClient();
        try {
            client.socket = new Socket(ip, port);
            client.socket.setSoTimeout(timeout * 1000);
            client.socket.setKeepAlive(true);
            client.socket.setReuseAddress(true);
            client.address = ip;
            client.port = port;
            client.timeout = timeout;
            return client;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) { }
            socket = null;
        }
    }

    public int send(byte[] b) {
        if(b == null || b.length == 0) {
            return 0;
        }
        if(socket == null) {
            return -1;
        }
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write(b, 0, b.length);
            output.flush();
            return b.length;
        } catch (SocketException e) {
            try {
                //socket.close();
                socket = new Socket(address, port);
                socket.setSoTimeout(timeout * 1000);
                socket.setKeepAlive(true);
                socket.setReuseAddress(true);
            } catch (IOException ne) {
                ne.printStackTrace();
                return 0;
            }
            return send(b);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public byte[] recv(int length) {
        if(length == 0) {
            return new byte[0];
        }
        if(socket == null) {
            return null;
        }
        try {
            byte[] b = new byte[length];
            DataInputStream input = new DataInputStream(socket.getInputStream());
            int recv_length = 0;
            while(recv_length < length) {
                int l = input.read(b, recv_length, length - recv_length);
                if(l == -1) break;
                recv_length += l;
            }
            return b;
        } catch (SocketException e) {
            try {
                //socket.close();
                socket = new Socket(address, port);
                socket.setSoTimeout(timeout * 1000);
                socket.setKeepAlive(true);
                socket.setReuseAddress(true);
            } catch (IOException ne) {
                ne.printStackTrace();
                return null;
            }
            return recv(length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

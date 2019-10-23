package com.taomee.bigdata.monitor.util;

import java.lang.Thread;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.HashSet;

import com.taomee.bigdata.monitor.namenode.*;
import com.taomee.bigdata.monitor.datanode.*;
import com.taomee.bigdata.monitor.jobtracker.*;
import com.taomee.bigdata.monitor.tasktracker.*;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MonitorRunner extends Configured implements Tool
{
    private static final MonitorRunner runner = new MonitorRunner();
    private HashSet<Monitor> monitors = new HashSet<Monitor>();
    private SocketClient socket;
    private String ip = null;
    private int port = 0;
    private int timeout = 5;
    private int gap = 60000;

    private MonitorRunner() { super(); }

    private static final void shutdownCallback() {
        runner.close();
    }

    private void close() {
        Iterator<Monitor> it = monitors.iterator();
        while(it.hasNext()) {
            Monitor monitor = it.next();
            monitor.close();
        }
        socket.close();
    }

    public static MonitorRunner get() {
        return runner;
    }

    public static void sleep(int t) {
        try {
            Thread.sleep(t);
        } catch (java.lang.InterruptedException e) { }
    }

    public int run(String[] args) {
        for(int i=0; i<args.length; i++) {
            if(args[i].compareToIgnoreCase("namenode") == 0) {
                monitors.add(new NameNodeMonitor());
            } else if(args[i].compareToIgnoreCase("jobtracker") == 0) {
                monitors.add(new JobTrackerMonitor());
            } else if(args[i].compareToIgnoreCase("datanode") == 0) {
                monitors.add(new DataNodeMonitor());
            } else if(args[i].compareToIgnoreCase("tasktracker") == 0) {
                monitors.add(new TaskTrackerMonitor());
            } else if(args[i].compareToIgnoreCase("-ip") == 0) {
                ip = args[++i];
            } else if(args[i].compareToIgnoreCase("-port") == 0) {
                port = Integer.valueOf(args[++i]);
            } else if(args[i].compareToIgnoreCase("-timeout") == 0) {
                timeout = Integer.valueOf(args[++i]);
            } else if(args[i].compareToIgnoreCase("-gap") == 0) {
                gap = Integer.valueOf(args[++i]) * 1000;
            }
        }
        if(ip == null || port == 0) {
            System.err.println("ip or port not configured");
            return 1;
        }
        socket = SocketClient.connect(ip, port, timeout);
        if(socket == null) {
            System.err.println("connect to " + ip + ":" + port + " error");
            return 2;
        }
        Iterator<Monitor> it = monitors.iterator();
        Byte ret;
        while(it.hasNext()) {
            Monitor monitor = it.next();
            ret = register(monitor);
            //TODO : 处理center回包
        }
        while(true) {
            it = monitors.iterator();
            while(it.hasNext()) {
                Monitor monitor = it.next();
                ret = heartbeat(monitor, args);
                if(ret != null && ret != 0) {
                    System.err.println("heartbeat ret " + ret);
                    register(monitor); //
                }
            }
            sleep(gap);
        }
    }

    private Byte register(Monitor monitor) {
        System.out.println("register " + monitor.getClass().getName());
        Register register = monitor.getRegister();
        RegisterRet register_ret = monitor.getRegisterRet();
        socket.send(register.pack()); //注册模块
        register_ret.unpack(socket.recv(register_ret.getLength()));
        return register_ret.getRet();
    }

    private Byte heartbeat(Monitor monitor, String[] args) {
        byte[] reporters;
        if((reporters = monitor.report(args)) == null) {
            return null;
        }
        socket.send(reporters);
        HeartbeatRet heartbeat_ret = monitor.getHeartbeatRet();
        heartbeat_ret.unpack(socket.recv(heartbeat_ret.getLength()));
        return heartbeat_ret.getRet();
    }

    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdownCallback();
            }
        });
        ToolRunner.run(MonitorRunner.get(), args);
    }
}

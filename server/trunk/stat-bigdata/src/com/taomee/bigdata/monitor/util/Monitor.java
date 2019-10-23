package com.taomee.bigdata.monitor.util;

import java.util.HashSet;

public abstract class Monitor
{
    protected RegisterRet register_ret = new RegisterRet();
    protected HeartbeatRet heartbeat_ret = new HeartbeatRet();
    protected HashSet<Logger> logger = new HashSet<Logger>();
    protected StatLogger stat_logger = new StatLogger(10000, -1, -1, -1);

    public abstract byte[] report(String[] args);
    public abstract Protocol getProtocol();
    public abstract Register getRegister();

    public RegisterRet getRegisterRet() {
        return register_ret;
    }

    public HeartbeatRet getHeartbeatRet() {
        return heartbeat_ret;
    }

    public void close() {
        getProtocol().clear();
    }

}

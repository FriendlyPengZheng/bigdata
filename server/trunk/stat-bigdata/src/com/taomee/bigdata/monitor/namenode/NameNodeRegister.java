package com.taomee.bigdata.monitor.namenode;

import com.taomee.bigdata.monitor.util.Register;
import com.taomee.bigdata.monitor.util.Protocol;

class NameNodeRegister extends Register
{
    public NameNodeRegister() {
        super();
        setModule(Protocol.MODULE_NAMENODE);
    }
}

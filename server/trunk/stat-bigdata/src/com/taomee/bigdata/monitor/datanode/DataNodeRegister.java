package com.taomee.bigdata.monitor.datanode;

import com.taomee.bigdata.monitor.util.Register;
import com.taomee.bigdata.monitor.util.Protocol;

class DataNodeRegister extends Register
{
    public DataNodeRegister() {
        super();
        setModule(Protocol.MODULE_DATANODE);
    }
}

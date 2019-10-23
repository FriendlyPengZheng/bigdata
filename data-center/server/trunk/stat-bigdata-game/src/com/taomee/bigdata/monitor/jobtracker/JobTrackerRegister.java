package com.taomee.bigdata.monitor.jobtracker;

import com.taomee.bigdata.monitor.util.Register;
import com.taomee.bigdata.monitor.util.Protocol;

class JobTrackerRegister extends Register
{
    public JobTrackerRegister() {
        super();
        setModule(Protocol.MODULE_JOBTRACK);
    }
}

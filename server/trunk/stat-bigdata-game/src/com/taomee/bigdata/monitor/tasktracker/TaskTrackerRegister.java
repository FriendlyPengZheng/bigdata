package com.taomee.bigdata.monitor.tasktracker;

import com.taomee.bigdata.monitor.util.Register;
import com.taomee.bigdata.monitor.util.Protocol;

class TaskTrackerRegister extends Register
{
    public TaskTrackerRegister() {
        super();
        setModule(Protocol.MODULE_TASKTRACK);
    }
}

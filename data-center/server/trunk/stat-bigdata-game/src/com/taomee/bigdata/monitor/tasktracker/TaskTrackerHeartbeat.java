package com.taomee.bigdata.monitor.tasktracker;

import com.taomee.bigdata.monitor.util.Protocol;
import java.nio.ByteBuffer;

public class TaskTrackerHeartbeat extends Protocol
{
    int mapsRunning;
    int reduceRunning;
    int mapTaskSlots;
    int reduceTaskSlots;
    int taskCompleted;

    public TaskTrackerHeartbeat() {
        proto_id = Protocol.CMD_TASKTRACK;
        module = Protocol.MODULE_TASKTRACK;
        len = getHeaderLength() + 20;
        byteBuffer = ByteBuffer.allocate(len);
    }

    public void setMapsRunning(int mapsRunning) {
        this.mapsRunning = mapsRunning;
    }
    public void setReduceRunning(int reduceRunning) {
        this.reduceRunning = reduceRunning;
    }
    public void setMapTaskSlots(int mapTaskSlots) {
        this.mapTaskSlots = mapTaskSlots;
    }
    public void setReduceTaskSlots(int reduceTaskSlots) {
        this.reduceTaskSlots = reduceTaskSlots;
    }
    public void setTaskCompleted(int taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    public byte[] pack() {
        byteBuffer.clear();
        byteBuffer.put(packHeader());
        byteBuffer.put(toLH(mapsRunning));
        byteBuffer.put(toLH(reduceRunning));
        byteBuffer.put(toLH(mapTaskSlots));
        byteBuffer.put(toLH(reduceTaskSlots));
        byteBuffer.put(toLH(taskCompleted));
        return byteBuffer.array();
    }
}

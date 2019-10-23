package com.taomee.bigdata.monitor.tasktracker;

import com.taomee.common.json.*;
import com.taomee.bigdata.upload.Uploader;
import com.taomee.bigdata.monitor.util.*;
import java.io.IOException;
import java.util.LinkedHashMap;

public class TaskTrackerMonitor extends Monitor
{
    private TaskTrackerHeartbeat reporter = new TaskTrackerHeartbeat();
    private TaskTrackerRegister register = new TaskTrackerRegister();

    private static final int port = 50060;
    private final String ip = Uploader.getIP();
    private static final String[] infos = new String[] {
        "mapsRunning", "reducesRunning", "mapTaskSlots", "reduceTaskSlots", "taskCompleted"
    };

    public Protocol getProtocol() {
        return reporter;
    }

    public Register getRegister() {
        return register;
    }

    public byte[] report(String[] args) {
        try {
            long time = System.currentTimeMillis()/1000;
            logger.clear();
            String ret = HttpClient.readContentFromGet("http://"+ip+":"+port+"/jmx?qry=Hadoop:service=TaskTracker,name=TaskTrackerMetrics");
            JSONArray array = JSONDecode.decode(ret);
            int maps_running = array.get("beans").get(0).get("maps_running").getInt();
            int reduces_running = array.get("beans").get(0).get("reduces_running").getInt();
            int mapTaskSlots = array.get("beans").get(0).get("mapTaskSlots").getInt();
            int reduceTaskSlots = array.get("beans").get(0).get("reduceTaskSlots").getInt();
            int tasks_completed = array.get("beans").get(0).get("tasks_completed").getInt();
            reporter.setMapsRunning(maps_running);
            reporter.setReduceRunning(reduces_running);
            reporter.setMapTaskSlots(mapTaskSlots);
            reporter.setReduceTaskSlots(reduceTaskSlots);
            reporter.setTaskCompleted(tasks_completed);
            logger.add(new Logger("_tasktrakcer_", "maps_running", maps_running));
            logger.add(new Logger("_tasktrakcer_", "reduces_running", reduces_running));
            logger.add(new Logger("_tasktrakcer_", "map_task_slots", mapTaskSlots));
            logger.add(new Logger("_tasktrakcer_", "reduce_task_slots", reduceTaskSlots));
            logger.add(new Logger("_tasktrakcer_", "tasks_completed", tasks_completed));
            stat_logger.addStat(logger, time);
        } catch (IOException e) {
            return null;
        }
        return reporter.pack();
    }
}

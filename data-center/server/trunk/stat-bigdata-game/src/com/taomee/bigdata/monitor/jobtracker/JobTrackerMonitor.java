package com.taomee.bigdata.monitor.jobtracker;

import com.taomee.bigdata.monitor.util.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.JobID;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Iterator;

public class JobTrackerMonitor extends Monitor
{
    private JobTrackerHeartbeat reporter = new JobTrackerHeartbeat();
    private JobTrackerRegister register = new JobTrackerRegister();
    private HashSet<String> runningJob = new HashSet<String>();
    private HashSet<String> failedJob  = new HashSet<String>();
    private int running = 0;
    private int failed = 0;
    private int killed = 0;
    private int prep = 0;

    public Protocol getProtocol() {
        return reporter;
    }

    public Register getRegister() {
        return register;
    }

    public byte[] report(String[] args) {
        JobConf conf = new JobConf(true);
        JobClient client;
        JobStatus[] jobStatus;
        ClusterStatus clusterStatus;
        try {
            client = new JobClient(conf);
            jobStatus = client.getAllJobs();
            clusterStatus = client.getClusterStatus();
        } catch (IOException e) {
            return null;
        }
        long time = System.currentTimeMillis()/1000;
        logger.clear();
        reporter.setActiveTaskTrackers(clusterStatus.getTaskTrackers());
        logger.add(new Logger("_jobtracker_", "active_task_trackers", clusterStatus.getTaskTrackers()));
        reporter.setBlackListedTaskTrackers(clusterStatus.getBlacklistedTrackers());
        logger.add(new Logger("_jobtracker_", "black_listed_task_trackers", clusterStatus.getBlacklistedTrackers()));
        reporter.setRunningMapTasks(clusterStatus.getMapTasks());
        logger.add(new Logger("_jobtracker_", "running_map_trackers", clusterStatus.getMapTasks()));
        reporter.setMaxMapTasks(clusterStatus.getMaxMapTasks());
        logger.add(new Logger("_jobtracker_", "max_map_trackers", clusterStatus.getMaxMapTasks()));
        reporter.setRunningReduceTasks(clusterStatus.getReduceTasks());
        logger.add(new Logger("_jobtracker_", "running_reduce_trackers", clusterStatus.getReduceTasks()));
        reporter.setMaxReduceTasks(clusterStatus.getMaxReduceTasks());
        logger.add(new Logger("_jobtracker_", "max_reduce_trackers", clusterStatus.getMaxReduceTasks()));
        LinkedHashMap<String, Integer>cnt = new LinkedHashMap<String, Integer>();
        failed = 0;
        killed = 0;
        prep = 0;
        running = 0;
        StringBuffer failedBuffer = new StringBuffer();
        StringBuffer failedIdBuffer = new StringBuffer();
        for(int i=0; i<jobStatus.length; i++) {
            int status = jobStatus[i].getRunState();
            String id = jobStatus[i].getJobID().toString();
            switch(status) {
                case JobStatus.FAILED:
                    failed ++;
                    if(runningJob.contains(id) || !failedJob.contains(id)) {
                        runningJob.remove(id);
                        failedJob.add(id);
                        failedIdBuffer.append(id+",");
                        try {
                            String jobName = client.getJob(jobStatus[i].getJobID()).getJobName();
                            jobName = jobName.split("=")[0];
                            if(jobName.split(":").length >= 2) jobName = jobName.split(":")[1];
                            failedBuffer.append(jobName+",");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case JobStatus.KILLED:
                    killed ++;
                    break;
                case JobStatus.PREP:
                    prep ++;
                    break;
                case JobStatus.RUNNING:
                    runningJob.add(id);
                    break;
                case JobStatus.SUCCEEDED:
                    runningJob.remove(id);
                    break;
            }
        }
        reporter.setFailed(failed);
        logger.add(new Logger("_jobtracker_", "failed", failed));
        reporter.setKilled(killed);
        logger.add(new Logger("_jobtracker_", "killed", killed));
        reporter.setPrep(prep);
        logger.add(new Logger("_jobtracker_", "prep", prep));
        reporter.setRunning(running);
        logger.add(new Logger("_jobtracker_", "running", running));
        reporter.setFailedJobId(failedBuffer.toString());
        if(failedBuffer.length() != 0) {
            System.err.println("[" + time + "]failed jobs name:" + failedBuffer.toString());
            System.err.println("[" + time + "]failed jobs id  :" + failedIdBuffer.toString());
        }
        stat_logger.addStat(logger, time);
        return reporter.pack();
    }
}

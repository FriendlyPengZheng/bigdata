<?php
class backend_DataFactory
{   
    const TYPE_STAT_CLIENT = 0;
    const TYPE_STAT_SERVER = 1;
    const TYPE_DB_SERVER = 2;
    const TYPE_CONFIG_SERVER = 3;
    const TYPE_STAT_REDIS = 4;
    const TYPE_STAT_NAMENODE = 5;
    const TYPE_STAT_JOBTRACKER = 6;
    const TYPE_STAT_DATANODE = 7;
    const TYPE_STAT_STATTASKTRACKER = 8;


    /**
     * @brief getDataClass 
     * 获取特定moduleType的类名称
     * @param {interval} $moduleType
     *
     * @return {string}
     */
    public static function getDataClass($moduleType)
    {
        $prefix = 'backend_proto_';
        switch ($moduleType) {
            case self::TYPE_STAT_CLIENT:
            case self::TYPE_STAT_SERVER:
                $name = 'StatClientData';
                break;

            case self::TYPE_DB_SERVER:
            case self::TYPE_CONFIG_SERVER:
                $name = 'DbConfigServer';
                break;

            case self::TYPE_STAT_NAMENODE:
                $name = 'StatNameNode';
                break;

            case self::TYPE_STAT_JOBTRACKER:
                $name = 'StatJobTracker';
                break;

            case self::TYPE_STAT_DATANODE:
                $name = 'StatDataNode';
                break;

            case self::TYPE_STAT_STATTASKTRACKER:
            default:
                $name = 'StatTaskTracker';
                break;
        }

        return $prefix . $name;
    }

    /**
     * @brief getDataHead 
     * 获取特定moduleType的thead
     * @param {interval} $moduleType
     *
     * @return {array}
     */
    public static function getDataHead($moduleType)
    {
        switch ($moduleType) {
            case self::TYPE_STAT_CLIENT:
            case self::TYPE_STAT_SERVER:
                return array(
                    'ip' => 'IP',
                    'port' => TM::t('tongji', '端口'),
                    'lastHeartBeatTime' => TM::t('tongji', '最后心跳时间'),
                    'workplaceSize' => TM::t('tongji', '工作目录可用空间'),
                    'inboxFileCount' => TM::t('tongji','Inbox目录文件数'),
                    'inboxFileSize' => TM::t('tongji', 'Inbox目录大小'),
                    'outboxFileCount' => TM::t('tongji', 'Outbox目录文件数'),
                    'outboxFileSize' => TM::t('tongji', 'Outbox目录大小'),
                    'sentFileCount' => TM::t('tongji', 'Sent目录文件数'),
                    'sentFileSize' => TM::t('tongji', 'Sent目录大小')
                );

                break;

            case self::TYPE_DB_SERVER:
            case self::TYPE_CONFIG_SERVER:
                return array(
                    'ip' => 'IP',
                    'port' => TM::t('tongji', '端口'),
                    'lastHeartBeatTime' => TM::t('tongji','最后心跳时间')
                );
                break;

            case self::TYPE_STAT_NAMENODE:
                return array(
                    'ip' => 'IP',
                    'port' => TM::t('tongji', '端口'),
                    'lastHeartBeatTime' => TM::t('tongji', '最后心跳时间'),
                    'configuredSize' => TM::t('tongji', '最大磁盘空间'),
                    'presentSize' => TM::t('tongji', '可用磁盘空间'),
                    'dfsRemainingSize' => TM::t('tongji', '剩余磁盘空间'),
                    'dfsUsedSize' => TM::t('tongji', '已用磁盘空间'),
                    'dfsUsedPercent' => TM::t('tongji', '已用磁盘百分比'),
                    'maxDfsUsedPercent' => TM::t('tongji', '最大已用磁盘百分比'),
                    'underReplicatedBlocks' => TM::t('tongji', '要拷贝文件块数'),
                    'missingBlocks' => TM::t('tongji', '丢失文件块个数'),
                    'totalDatanodes' => TM::t('tongji', 'Datanode数'),
                    'liveNodes' => TM::t('tongji', '存活Datanode数'),
                    'deadNodes' => TM::t('tongji', '挂掉Datanode数')
                ); 
                break;

            case self::TYPE_STAT_JOBTRACKER:
                return array(
                    'ip' => 'IP',
                    'port' => TM::t('tongji', '端口'),
                    'lastHeartBeatTime' => TM::t('tongji', '最后心跳时间'),
                    'activeTaskTrackers' => TM::t('tongji', '活跃Jobtracker'),
                    'blackListedTaskTrackers' => TM::t('tongji', 'Tasktracker黑名单数'),
                    'runningMapTasks' => TM::t('tongji', '在运行Map数'),
                    'maxMapTasks' => TM::t('tongji', '最大运行Map数'),
                    'runningReduceTasks' =>TM::t('tongji', '在运行Reduce数'),
                    'maxReduceTasks' =>TM::t('tongji', '最大运行Reduce数'),
                    'failed' => TM::t('tongji', '失败任务数'),
                    'killed' => TM::t('tongji', '强制终止任务数'),
                    'prep' => TM::t('tongji', '等待执行任务数'),
                    'running' => TM::t('tongji', '在执行任务数'),
                    'storageInfo' =>TM::t('tongji', '最近执行失败的任务ID')
                );
                break;

            case self::TYPE_STAT_DATANODE:
                return array(
                    'ip' => 'IP',
                    'port' => TM::t('tongji', '端口'),
                    'lastHeartBeatTime' => TM::t('tongji', '最后心跳时间'),
                    'presentSize' => TM::t('tongji', '可用磁盘空间'),
                    'dfsRemainingSize' => TM::t('tongji', '剩余磁盘空间'),
                    'dfsUsedSize' => TM::t('tongji', '已用磁盘空间'),
                    'dfsUsedPercent' => TM::t('tongji', '已用磁盘百分比'),
                    'storageInfo' => TM::t('tongji','挂载目录')
                );
                break;

            case self::TYPE_STAT_STATTASKTRACKER:
            default:
                return array(
                    'ip' => 'IP',
                    'port' => TM::t('tongji', '端口'),
                    'lastHeartBeatTime' => TM::t('tongji', '最后心跳时间'),
                    'mapsRunning' => TM::t('tongji', '在运行Map数'),
                    'reduceRunning' => TM::t('tongji', '在运行Reduce数'),
                    'mapTaskSlots' => TM::t('tongji', '可运行Map数'),
                    'reduceTaskSlots' => TM::t('tongji', '可运行Reduce数'),
                    'TaskCompleted' => TM::t('tongji', '完成的任务数')
                );
                break;
        }
    }
}

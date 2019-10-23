<?php
class TimingCommand extends TMDaemonCommand
{
    /**
     * @var bool 当没有任务时，调用onNoTask回调(true)还是直接退出(false)
     */
    public $resident = true;

    protected function onNoTask()
    {
        sleep(30);
    }

    protected function getTask()
    {
        $model = new TimingTask();
        return $model->getOneTask();
    }

    protected function beforeWork($task)
    {
        $model = new TimingTask();
        $model->task_id = $task['task_id'];
        $model->status = 2; // 执行中
        $model->start = false;
        $model->next_execute_time = $model->getNextExecuteTime($task);
        $model->last_execute_time = time();
        $model->update(array('last_execute_time', 'status', 'next_execute_time', 'task_id'));
    }

    protected function work($task)
    {
        $command = $task['command'];
        $pid = sprintf('%s_%s', $task['task_id'], time());
        $console = TM::app()->getBasePath() . DIRECTORY_SEPARATOR . 'console' . DIRECTORY_SEPARATOR . 'index.php';
        $pidFile = TM::app()->getRuntimePath() . DIRECTORY_SEPARATOR . 'pid';
        TMFileHelper::mkdir($pidFile);
        $pidFile .= DIRECTORY_SEPARATOR . "task-$pid.pid";
        $returnVar = pclose(popen("/usr/bin/php $console $command --vpid=$pid --pidFile=$pidFile", 'r'));
        return $returnVar !== false;
    }

    public function afterSucceed($task)
    {
        $this->endWork($task);
    }

    protected function endWork($task)
    {
        $model = new TimingTask();
        $model->task_id = $task['task_id'];
        $model->status = 1; // 初始化
        $model->start = false;
        $model->update(array('status', 'task_id'));
    }

    public function afterFail($task)
    {
        $this->endWork($task);
        $this->log($task['task_id']. '执行失败！', 'error');
    }

    protected function clean()
    {
        TM::app()->db->setActive(false);
    }
}

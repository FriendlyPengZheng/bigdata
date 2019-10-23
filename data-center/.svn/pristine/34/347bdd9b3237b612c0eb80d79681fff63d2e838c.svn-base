<?php
class TimingTask extends TMFormModel
{
    public $interval;

    public $start = true;

    public function tableName()
    {
        return 't_timing_task';
    }

    public function rules()
    {
        return array(
            array('timing_type', 'enum', 'range' => array(1, 2)), // 1：初始化 2：执行中
            array('timing_options', 'checkTimingOptions'),
            array('command', 'string', 'min' => 1, 'max' => 1024)
        );
    }

    public function checkTimingOptions($value)
    {
        $interval = 0;
        $datum = 0;
        if (!isset($value['interval'])) return false;
        $this->interval = $value['interval'];
        TMValidator::createValidator('number', $this, 'interval')->validate($this, 'interval');
        if ($this->timing_type == 1) {
            $interval = $value['interval'] * 60;
            $datum = time();
        } else {
            if (!isset($value['time'])) return false;
            $value['time'] = strtotime($value['time']);
            if (!$value['time']) return false;
            $datum = $this->_getWeeklyDatum($value['interval'], $value['time']);
            $value['time'] = date('H:i', $value['time']);
            $interval = 7 * 24 * 60 * 60;
        }
        $this->timing_interval = $interval;
        $this->next_execute_time = $datum + $interval;
        $this->timing_options = json_encode($value);
        return true;
    }

    private function _getWeeklyDatum($iWeeklyIndex, $time)
    {
        if ($iWeeklyIndex == date('N', time()) && $time < time()) return $time;
        $aWeekly = [1 => 'Sunday', 2 => 'Monday', 3 => 'Tuesday', 4 => 'Wednesday', 5 => 'Thursday', 6 => 'Friday', 7 => 'Saturday'];
        return strtotime(date('Y-m-d ', strtotime("last {$aWeekly[$iWeeklyIndex]}")) . date('H:i:s', $time));
    }

    public function getOneTask()
    {
        return $this->getDb()->createCommand()
            ->select($this->attributes())->from($this->tableName())
            ->where('status = 1 AND next_execute_time <=' . time())
            ->limit(1)
            ->queryRow();
    }

    public function start()
    {
        $console = TM::app()->getBasePath() . DIRECTORY_SEPARATOR . 'console' . DIRECTORY_SEPARATOR . 'index.php';
        $pidFile = TM::app()->getRuntimePath() . DIRECTORY_SEPARATOR . 'pid';
        TMFileHelper::mkdir($pidFile);
        $pidFile .= DIRECTORY_SEPARATOR . 'timing.pid';
        pclose(popen("/usr/bin/php $console timing start --pidFile=$pidFile", 'r'));
    }

    public function afterInsert()
    {
        $this->start();
        return true;
    }

    public function afterUpdate()
    {
        if ($this->start) {
            $this->start();
        }
        return true;
    }

    public function afterDelete()
    {
        $this->start();
       return true;
    }

    public function getNextExecuteTime($task)
    {
        return $task['next_execute_time'] + $task['timing_interval'];
    }
}

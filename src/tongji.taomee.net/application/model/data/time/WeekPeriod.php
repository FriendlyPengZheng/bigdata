<?php
class data_time_WeekPeriod extends data_time_Period
{
    /**
     * Init this period.
     */
    public function init()
    {
        parent::init();
        $this->setTitle('周')->setInterval(604800);
    }

    /**
     * Format the point's time.
     * @param integer $iTime
     * @return string
     */
    protected function format($iTime)
    {
        return date('Y-m-d', $iTime) . '~' . date('Y-m-d', $iTime + $this->getInterval() - 86400);
    }

    /**
     * Get the default start timestamp.
     * @return mixed
     */
    protected function getDefaultFrom()
    {
        return '-4weeks';
    }

    /**
     * Amend this period.
     */
    protected function amend()
    {
        $iFrom = data_time_Time::amend($this->getFrom() + 86400, 'Y-m-d 00:00:00', 'last monday');
        $iTo = data_time_Time::amend($this->getTo() + 86400, 'Y-m-d 00:00:00', 'last monday');
        $this->setFrom($iFrom)->setTo($iTo);
    }

    /**
     * @brief getYoyFrom
     * 获取同比开始事件
     * @return {timestamp}
     */
    protected function getYoyFrom()
    {
        return $this->getFrom() - 2419200;
    }

    /**
     * @brief getYoyTo
     * 获取同比结束事件
     * @return {timestamp}
     */
    protected function getYoyTo()
    {
        return $this->getTo() - 2419200;
    }

    /**
     * @brief getQoqFrom
     * 获取环比开始事件
     * @return {timestamp}
     */
    protected function getQoqFrom()
    {
        return $this->getFrom() - 604800;
    }

    /**
     * @brief getQoqTo
     * 获取环比结束事件
     * @return {timestamp}
     */
    protected function getQoqTo()
    {
        return $this->getTo() - 604800;
    }
}

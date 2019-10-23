<?php
class data_time_MinutePeriod extends data_time_Period
{
    /**
     * Init this period.
     */
    public function init()
    {
        parent::init();
        $this->setTitle('分')->setInterval(
            60 * ceil(($this->getTo() - $this->getFrom()) / 86400)
        );
    }

    /**
     * Get table type.
     * @return string
     */
    public function getTableType()
    {
        return 'minute';
    }

    /**
     * Format the point's time.
     * @param integer $iTime
     * @return string
     */
    protected function format($iTime)
    {
        return date('Y-m-d H:i', $iTime);
    }

    /**
     * Get the default start timestamp.
     * @return mixed
     */
    protected function getDefaultFrom()
    {
        return 'now';
    }

    /**
     * Amend this period.
     */
    protected function amend()
    {
        // Edit by violet, only show the time after 5 o'clock
        $iFrom = data_time_Time::amend($this->getFrom(), 'Y-m-d 00:00:00');
        // Amend the end time to the last minute of the day.
        $iTo = data_time_Time::amend($this->getTo(), 'Y-m-d 00:00:00') + 86340;
        $this->setFrom($iFrom)->setTo($iTo);
    }

    /**
     * @brief getYoyFrom
     * 获取同比开始事件
     * @return {timestamp}
     */
    protected function getYoyFrom()
    {
        return $this->getFrom() - 86400;
    }

    /**
     * @brief getYoyTo
     * 获取同比结束事件
     * @return {timestamp}
     */
    protected function getYoyTo()
    {
        return $this->getTo() - 86400;
    }

    /**
     * @brief getQoqFrom
     * 获取环比开始事件
     * @return {timestamp}
     */
    protected function getQoqFrom()
    {
        return $this->getFrom() - 60;
    }

    /**
     * @brief getQoqTo
     * 获取环比结束事件
     * @return {timestamp}
     */
    protected function getQoqTo()
    {
        return $this->getTo() - 60;
    }
}

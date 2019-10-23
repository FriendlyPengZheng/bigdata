<?php
class data_time_MonthPeriod extends data_time_Period
{
    /**
     * Init this period.
     */
    public function init()
    {
        parent::init();
        $this->setTitle('月')->setInterval('+1month');
    }

    /**
     * Format the point's time.
     * @param integer $iTime
     * @return string
     */
    protected function format($iTime)
    {
        return date('Y-m', $iTime);
    }

    /**
     * Get the default start timestamp.
     * @return mixed
     */
    protected function getDefaultFrom()
    {
        return '-1year';
    }

    /**
     * Amend this period.
     */
    protected function amend()
    {
        $iFrom = data_time_Time::amend($this->getFrom(), 'Y-m-01 00:00:00');
        $iTo = data_time_Time::amend($this->getTo(), 'Y-m-01 00:00:00');
        $this->setFrom($iFrom)->setTo($iTo);
    }

    /**
     * @brief getYoyFrom
     * 获取同比开始事件
     * @return {timestamp}
     */
    protected function getYoyFrom()
    {
        return strtotime('-1 year', $this->getFrom());
    }

    /**
     * @brief getYoyTo
     * 获取同比结束事件
     * @return {timestamp}
     */
    protected function getYoyTo()
    {
        return strtotime('-1 year', $this->getTo());
    }

    /**
     * @brief getQoqFrom
     * 获取环比开始事件
     * @return {timestamp}
     */
    protected function getQoqFrom()
    {
        return strtotime('-1 month', $this->getFrom());
    }

    /**
     * @brief getQoqTo
     * 获取环比结束事件
     * @return {timestamp}
     */
    protected function getQoqTo()
    {
        return strtotime('-1 month', $this->getTo());
    }
}

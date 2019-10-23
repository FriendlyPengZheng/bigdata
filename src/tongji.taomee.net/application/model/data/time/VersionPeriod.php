<?php
class data_time_VersionPeriod extends data_time_WeekPeriod
{
    /**
     * Init this period.
     */
    public function init()
    {
        parent::init();
        $this->setTitle('版本周');
    }

    /**
     * Amend this period.
     */
    protected function amend()
    {
        $iFrom = data_time_Time::amend($this->getFrom() + 86400, 'Y-m-d 00:00:00', 'last friday');
        $iTo = data_time_Time::amend($this->getTo() + 86400, 'Y-m-d 00:00:00', 'last friday');
        $this->setFrom($iFrom)->setTo($iTo);
    }
}

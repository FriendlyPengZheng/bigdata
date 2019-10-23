<?php
abstract class data_time_Period extends TMComponent
{
    /**
     * @var integer the start timestamp.
     */
    private $_from = null;

    /**
     * @var integer the end timestamp.
     */
    private $_to = null;

    /**
     * @var mixed the period interval in seconds or string.
     */
    private $_interval = null;

    /**
     * @var string the period title.
     */
    private $_title = '';

    /**
     * @var array Time points and default values during the period.
     */
    private $_points = null;

    /**
     * Constructor.
     * @param mixed $from
     * @param mixed $to
     */
    public function __construct($from, $to)
    {
        $this->_from = $from;
        $this->_to = $to;
        $this->init();
    }

    /**
     * Init this period.
     */
    public function init()
    {
        $now = time();

        $this->_from = data_time_Time::filter($this->_from, $this->getDefaultFrom(), $now);
        $this->_to = data_time_Time::filter($this->_to, $this->getDefaultTo(), $now);
        if ($this->_from > $this->_to) {
            $this->_from = $this->_to - $this->_from;
            $this->_to = $this->_to - $this->_from;
            $this->_from = $this->_to + $this->_from;
        }

        $this->amend();
    }

    /**
     * Get table type.
     * @return string
     */
    public function getTableType()
    {
        return 'day';
    }

    /**
     * Get time points and default values during the period.
     * @param boolean $defaultNull
     * @param mixed $defaultValue
     * @return array
     */
    public function getPoints($defaultNull = false, $defaultValue = 0)
    {
        if (isset($this->_points)) {
            return $this->_points;
        }
        $this->_points = array('key' => array(), 'value' => array());
        $iTime = $this->_from;
        $iThreshold = time() - 180;
        while ($iTime <= $this->_to) {
            $this->_points['key'][] = $this->format($iTime);
            $this->_points['value'][$iTime] = ($defaultNull || $iTime > $iThreshold) ? null : $defaultValue;
            if (is_numeric($this->_interval)) {
                $iTime += $this->_interval;
            } else {
                $iTime = strtotime($this->_interval, $iTime);
            }
        }
        return $this->_points;
    }

    /**
     * Set time points for populating.
     * @param array $points
     * @return data_time_Period
     */
    public function setPoints($points)
    {
        $this->_points = $points;
        return $this;
    }

    /**
     * Format the point's time.
     * @param integer $iTime
     * @return string
     */
    abstract protected function format($iTime);

    /**
     * Get the default start timestamp.
     * @return mixed
     */
    abstract protected function getDefaultFrom();

    /**
     * Get the default end timestamp.
     * @return mixed
     */
    protected function getDefaultTo()
    {
        return 'now';
    }

    /**
     * Amend this period.
     */
    protected function amend()
    {
        // Override this method if necessary.
    }

    /**
     * Get the title.
     * @return string
     */
    public function getTitle()
    {
        return $this->_title;
    }

    /**
     * Set the title.
     * @param string $title
     * @return data_time_Period
     */
    public function setTitle($title)
    {
        $this->_title = TM::t('tongji', $title);
        return $this;
    }

    /**
     * Get the start timestamp.
     * @return integer
     */
    public function getFrom()
    {
        return $this->_from;
    }

    /**
     * Set the start timestamp.
     * @param integer $from
     * @return data_time_Period
     */
    public function setFrom($from)
    {
        $this->_from = $from;
        return $this;
    }

    /**
     * Get the end timestamp.
     * @return integer
     */
    public function getTo()
    {
        return $this->_to;
    }

    /**
     * Set the end timestamp.
     * @param integer $to
     * @return data_time_Period
     */
    public function setTo($to)
    {
        $this->_to = $to;
        return $this;
    }

    /**
     * Get the interval in seconds or string.
     * @return integer|string
     */
    public function getInterval()
    {
        return $this->_interval;
    }

    /**
     * Set the interval in seconds or string.
     * @param integer|string $interval
     * @return data_time_Period
     */
    public function setInterval($interval)
    {
        $this->_interval = $interval;
        return $this;
    }

    /**
     * 获取同比
     * @return {Period}
     */
    public function getYoy()
    {
        $period = clone $this;
        $period->setFrom($this->getYoyFrom())->setTo($this->getYoyTo());
        return $period;
    }

    /**
     * 获取同比开始事件
     * @return {timestamp}
     */
    abstract protected function getYoyFrom();

    /**
     * 获取同比结束事件
     * @return {timestamp}
     */
    abstract protected function getYoyTo();

    /**
     * 获取环比
     * @return {Period}
     */
    public function getQoq()
    {
        $period = clone $this;
        $period->setFrom($this->getQoqFrom())->setTo($this->getQoqTo());
        return $period;
    }

    /**
     * 获取环比开始事件
     * @return {timestamp}
     */
    abstract protected function getQoqFrom();

    /**
     * 获取环比结束事件
     * @return {timestamp}
     */
    abstract protected function getQoqTo();
}

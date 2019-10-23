<?php
class DateMarker extends TMComponent
{
    /**
     * @var string prefix
     */
    public $prefix = '';

    /**
     * 记下日期
     * @param array $aDate
     * @return DateMarker
     */
    public function markDate($aDate)
    {
        if ($aDate['from'] === $aDate['to']) return $this;
        foreach (array('from', 'to') as $type) {
            if (!isset($aDate[$type]) || !$aDate[$type]) continue;
            if (is_array($aDate[$type])) {
                ksort($aDate[$type]);
                if (count($aDate[$type]) > 1) continue;
                $aDate[$type] = array_shift($aDate[$type]);
            }
            $suffix = preg_match('/^[0-9]{4}-[0-9]{2}$/', $aDate[$type]) ? 'm' : '';
            if (strtotime($aDate[$type]) !== false) {
                TM::app()->session->add("{$this->prefix}_date_marker_{$suffix}{$type}", $aDate[$type]);
            }
        }
        return $this;
    }

    /**
     * 从标记中恢复日期
     * @param string $suffix 用于区分日期类型
     * @return array
     */
    public function getDateMark($suffix = '')
    {
        $aDate = array_fill_keys(array('from', 'to'), null);
        foreach (array('from', 'to') as $type) {
            $date = TM::app()->session->get("{$this->prefix}_date_marker_{$suffix}{$type}");
            if (strtotime($date) !== false) {
                $aDate[$type] = $date;
            }
        }
        return $aDate;
    }

    /**
     * 设置前缀
     * @param string $prefix
     * @return DateMarker
     */
    public function setPrefix($prefix = '')
    {
        $this->prefix = $prefix;
        return $this;
    }
}

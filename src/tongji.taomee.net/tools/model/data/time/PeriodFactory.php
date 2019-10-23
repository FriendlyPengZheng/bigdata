<?php
class data_time_PeriodFactory extends TMComponent
{
    const TYPE_MINUTE = 4;
    const TYPE_DAY = 1;
    const TYPE_WEEK = 2;
    const TYPE_MONTH = 3;
    const TYPE_HOUR = 5;

    /**
     * @var array periods cache.
     */
    private static $_periods = array();

    /**
     * Create period.
     * @param string|integer $from
     * @param string|integer $to
     * @param integer $type
     * @return data_time_Period
     */
    public static function createPeriod($from, $to, $type = self::TYPE_DAY)
    {
        $key = implode(':', array($from, $to, $type));
        if (isset(self::$_periods[$key])) {
            return self::$_periods[$key];
        }
        switch ($type) {
            case self::TYPE_MINUTE:
                self::$_periods[$key] = new data_time_MinutePeriod($from, $to);
                break;

            case self::TYPE_HOUR:
                self::$_periods[$key] = new data_time_HourPeriod($from, $to);
                break;

            case self::TYPE_WEEK:
                self::$_periods[$key] = new data_time_WeekPeriod($from, $to);
                break;

            case self::TYPE_MONTH:
                self::$_periods[$key] = new data_time_MonthPeriod($from, $to);
                break;

            case self::TYPE_DAY:
            default:
                self::$_periods[$key] = new data_time_DayPeriod($from, $to);
                break;
        }
        return self::$_periods[$key];
    }

    /**
     * 获取时间配置的名称
     */
    public static function getPeriodConfigure()
    {
        return array(
            self::TYPE_DAY => TM::t('tongji', '日'),
            self::TYPE_WEEK => TM::t('tongji', '周'),
            self::TYPE_MONTH => TM::t('tongji', '月'),
            self::TYPE_MINUTE => TM::t('tongji', '分钟'),
            self::TYPE_HOUR => TM::t('tongji', '小时')
        );
    }
}

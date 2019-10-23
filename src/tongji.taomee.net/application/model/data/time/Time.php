<?php
class data_time_Time extends TMComponent
{
    /**
     * @var array Weeks' Chinese name.
     */
    private static $_weeks = array('周日', '周一', '周二', '周三', '周四', '周五', '周六');

    /**
     * Filter time in seconds or string.
     * @param mixed $time Time in seconds or string.
     * @param mixed $default The default time in seconds or string.
     * @param integer $max The max time in seconds.
     * @return integer Filtered time in seconds.
     */
    public static function filter($time, $default, $max = null)
    {
        $time = self::unixtime($time);
        if (false === $time) {
            $time = self::unixtime($default);
        }
        if (isset($max) && $time > $max) {
            $time = $max;
        }
        return $time;
    }

    /**
     * Convert time to timestamp, false if time is invalid.
     * @param mixed $time
     * @return mixed
     */
    public static function unixtime($time)
    {
        if (!is_numeric($time)) {
             // deal with date like '2014-02', if today is '2014-01-31', strtotime('2013-02') is '2013-03-03'
            if (preg_match('/^[0-9]{4}-[0-9]{2}$/', $time)) {
                $time .= '-01';
            }
            $time = strtotime($time);
        }
        return $time;
    }

    /**
     * Amend time by the given format and offset.
     * @param integer $time
     * @param string $format eg.'Y-m-01 00:00:00'
     * @param string $extra eg.'-1day'
     * @return integer
     */
    public static function amend($time, $format, $extra = null)
    {
        $time = strtotime(date($format, $time));
        if (isset($extra)) {
            $time = strtotime($extra, $time);
        }
        return $time;
    }

    /**
     * Return the date's week.
     * @param string $date
     * @return mixed
     */
    public static function date2week($date)
    {
        if (($time = strtotime($date)) !== false) {
            return self::$_weeks[date('w', $time)];
        }
        return false;
    }

    /**
     * Align the given dates, take the first from and to as ruler.
     * @param array $dates
     * @param string $format
     * @return false|array
     */
    public static function align($dates, $format = 'Y-m-d')
    {
        $dates['from'] = (array)$dates['from'];
        $dates['to'] = (array)$dates['to'];
        ksort($dates['from']);
        ksort($dates['to']);
        $dates['from'] = array_values($dates['from']);
        $dates['to'] = array_values($dates['to']);

        $cntFrom = count($dates['from']);
        $cntTo = count($dates['to']);
        if (!$cntFrom || !$cntTo) return false;
        if ($cntFrom === $cntTo) return $dates;
        $num = max($cntFrom, $cntTo);

        if (($from = strtotime($dates['from'][0])) === false) return false;
        if (($to = strtotime($dates['to'][0])) === false) return false;

        $interval = $from - $to;
        while (--$num > -1) {
            if (!isset($dates['from'][$num])) {
                if (($time = strtotime($dates['to'][$num])) === false) {
                    unset($dates['to'][$num]);
                } else {
                    $dates['from'][$num] = date($format, $time + $interval);
                }
            }
            if (!isset($dates['to'][$num])) {
                if (($time = strtotime($dates['from'][$num])) === false) {
                    unset($dates['from'][$num]);
                } else {
                    $dates['to'][$num] = date($format, $time - $interval);
                }
            }
        }
        return $dates;
    }
}

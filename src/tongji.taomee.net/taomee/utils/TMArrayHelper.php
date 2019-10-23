<?php
class TMArrayHelper
{
    /**
     * Return the values from a single column in the input array.
     * @param array $input
     * @param mixed $columnKey
     * @param mixed $indexKey
     * @return array
     * @see http://www.php.net/manual/en/function.array-column.php
     */
    public static function column(array $input, $columnKey, $indexKey = null)
    {
        if (function_exists('array_column')) {
            return array_column($input, $columnKey, $indexKey);
        }
        $output = array();
        foreach ($input as $value) {
            if (!is_array($value)) {
                continue;
            }

            $val = null;
            if (!isset($columnKey)) {
                $val = $value;
            } elseif (isset($value[(string)$columnKey])) {
                $val = $value[(string)$columnKey];
            } else {
                continue;
            }

            if (!isset($indexKey, $value[(string)$indexKey])) {
                $output[] = $val;
            } else {
                $output[(string)$value[(string)$indexKey]] = $val;
            }
        }
        return $output;
    }

    /**
     * Get the value of the special key from a series of arrays.
     * @param mixed $key
     * @param array $arr...
     * @param mixed default value
     * @return mixed
     */
    public static function assoc()
    {
        $num = func_num_args();
        if ($num < 3) {
            return;
        }

        $args = func_get_args();
        $key = array_shift($args);
        $default = array_pop($args);
        foreach ($args as $arg) {
            if (is_array($arg) && isset($arg[$key])) {
                return $arg[$key];
            }
        }
        return $default;
    }

    /**
     * Merge two or more arrays recursively.
     * If the input arrays have the same keys, either string keys or numeric keys,
     * the later value for that key will overwrite the previous one.
     * @param array $arr...
     * @return array
     */
    public static function recursiveMerge($arr)
    {
        $num = func_num_args();
        if ($num < 2) {
            return $arr;
        }

        $args = func_get_args();
        array_shift($args);
        foreach ($args as $arg) {
            foreach ($arg as $key => $value) {
                if (array_key_exists($key, $arr) && is_array($arr[$key]) && is_array($value)) {
                    $arr[$key] = self::recursiveMerge($arr[$key], $value);
                } else {
                    $arr[$key] = $value;
                }
            }
        }
        return $arr;
    }
}

<?php
/**
* @file HeaderBag.php
* @brief HTTP头处理
* @author violet violet@taomee.com
* @version 1.0
* @date 2017-08-30
*/

namespace taomee\web\request;

class HeaderBag implements \IteratorAggregate, \Countable
{
    protected $headers;

    public function __construct(array $headers = array())
    {
        foreach ($headers as $key => $value) {
            $this->set($key, $value);
        }
    }

    /**
     * @brief all 
     * 返回所有参数列表
     *
     * @return {array}
     */
    public function all()
    {
        return $this->headers;
    }

    /**
     * @brief get 
     * 返回指定key的值
     *
     * @param {string} $key
     * @param {mixed} $default 默认值
     * @param {boolean} $first
     *
     * @return {mixed}
     */
    public function get($key, $default = null, $first = true)
    {
        $key = str_replace('_', '-', strtolower($key));

        if (!array_key_exists($key, $this->headers)) {
            if (null === $default) { 
                return $first ? null : array();
            }

            return $first ? $default : array($default);
        }

        if ($first) {
            return count($this->headers[$key]) ? $this->headers[$key][0] : $default;
        }

        return $this->headers[$key];
    }

    /**
     * @brief set 
     * 设置指定key的值
     *
     * @param {string} $key
     * @param {array} $values
     * @param {boolean} $replace
     */
    public function set($key, $values, $replace = true)
    {
        $key = str_replace('_', '-', strtolower($key));

        $values = array_values((array) $values);

        if (true === $replace || !isset($this->headers[$key])) {
            $this->headers[$key] = $values;
        } else {
            $this->headers[$key] = array_merge($this->headers[$key], $values);
        }
    }

    /**
     * @brief has 
     * 查询指定的key是否存在
     *
     * @param {string} $key
     *
     * @return {boolean}
     */
    public function has($key)
    {
        return array_key_exists(str_replace('_', '-', strtolower($key)), $this->headers);
    }

    /**
     * @brief remove 
     * 移除指定key
     *
     * @param {string} $key
     */
    public function remove($key)
    {
        $key = str_replace('_', '-', strtolower($key));
        unset($this->headers[$key]);
    }

    /**
     * @brief getIterator 
     * 实现\IteratorAggregate接口
     *
     * @return {\ArrayIterator}
     */
    public function getIterator()
    {   
        return new \ArrayIterator($this->headers);
    }

    /**
     * @brief count 
     * 实现\Countable接口
     *
     * @return {integer}
     */
    public function count()
    {   
        return count($this->headers);
    }
}

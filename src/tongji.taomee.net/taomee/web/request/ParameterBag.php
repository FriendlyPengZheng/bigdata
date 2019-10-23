<?php
/**
* @file TMParameterBag.php
* @brief 参数打包
* @author violet violet@taomee.com
* @version 1.0
* @date 2017-08-29
*/

namespace taomee\web\request;

class ParameterBag implements \IteratorAggregate, \Countable
{
    protected $parameters;

    public function __construct($parameters)
    {
        $this->parameters = $parameters;
    }

    /**
     * @brief all 
     * 返回所有参数列表
     *
     * @return {array}
     */
    public function all()
    {
        return $this->parameters;
    }

    /**
     * @brief get 
     * 返回指定key的值
     *
     * @param {string} $key
     * @param {mixed} $default 默认值
     *
     * @return {mixed}
     */
    public function get($key, $default = null)
    {
        return array_key_exists($key, $this->parameters) ? $this->parameters[$key] : $default;
    }

    /**
     * @brief set 
     * 设置指定key的值
     *
     * @param {string} $key
     * @param {mixed} $value
     */
    public function set($key, $value)
    {
        $this->parameters[$key] = $value;
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
        return array_key_exists($key, $this->parameters);
    }

    /**
     * @brief remove 
     * 移除指定key
     *
     * @param {string} $key
     */
    public function remove($key)
    {
        unset($this->parameters[$key]);
    }

    /**
     * @brief getIterator 
     * 实现\IteratorAggregate接口
     *
     * @return {\ArrayIterator}
     */
    public function getIterator()
    {   
        return new \ArrayIterator($this->parameters);
    }

    /**
     * @brief count 
     * 实现\Countable接口
     *
     * @return {integer}
     */
    public function count()
    {   
        return count($this->parameters);
    }
}

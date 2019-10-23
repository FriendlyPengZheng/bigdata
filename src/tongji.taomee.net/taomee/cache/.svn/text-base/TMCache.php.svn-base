<?php
abstract class TMCache extends TMComponent
{
    /**
     * @var boolean whether the cache is open.
     */
    public $open = true;

    /**
     * @var string prefix of every cache key.
     */
    public $prefix = '';

    /**
     * Init the component.
     */
    public function init()
    {
    }

    /**
     * Retrieve an item.
     * @param string $key
     * @return mixed
     */
    abstract public function get($key);

    /**
     * Retrieve multiple items.
     * @param array $keys
     * @return array
     */
    abstract public function getMulti($keys);

    /**
     * Store an item.
     * @param string $key
     * @param mixed $value
     * @param integer $expire
     * @return boolean
     */
    abstract public function set($key, $value, $expire = 0);

    /**
     * Store multiple items.
     * @param array $items key/value
     * @param integer $expire
     * @return boolean
     */
    abstract public function setMulti($items, $expire = 0);

    /**
     * Delete an item.
     * @param string $key
     * @return boolean
     */
    abstract public function delete($key);

    /**
     * Delete multiple items.
     * @param string $keys
     * @return boolean
     */
    abstract public function deleteMulti($keys);

    /**
     * Invalidate all items in the cache.
     * @return boolean
     */
    abstract public function flush();

    /**
     * Set a new expiration on an item.
     * @param string $key
     * @param integer $expire
     * @return boolean
     */
    abstract public function touch($key, $expire = 0);
}

class TMCacheException extends TMException
{
}

<?php
class TMMemcache extends TMCache
{
    /**
     * @var array memcache servers.
     */
    public $servers = array(array('127.0.0.1', 11211));

    /**
     * @var {integer} Value in seconds which will be used for connecting to the daemon.
     */
    public $timeout = 60;

    /**
     * @var {boolean} Controls the use of a persistent connection.
     */
    public $persistent = true;

    /**
     * @var string serializer for cache data.
     * 'igbinary' or default 'json'
     */
    public $serializer = 'json';

    /**
     * @var callable handler for serialize.
     */
    private $_mSerializeHandler = 'json_encode';

    /**
     * @var callable handler for unserialize.
     */
    private $_mUnserializeHandler = 'json_decode';

    /**
     * @var Memcached instance of Memcached.
     */
    private $_oMemcache = null;

    /**
     * Init the component.
     */
    public function init()
    {
        parent::init();

        if (!extension_loaded('memcache')) {
            throw new TMCacheException(TM::t('taomee', 'TMMemcache requires PHP memcache extension to be loaded.'));
        }
        $this->_oMemcache = new Memcache();
        if (!is_array($this->servers) || empty($this->servers)) {
            throw new TMCacheException(TM::t('taomee', 'Servers of TMMemcached must be a non-empty array.'));
        }
        foreach ($this->servers as $key => $server) {
            if (!is_array($server) || empty($server) || !isset($server[0], $server[1])) {
                throw new TMCacheException(TM::t('taomee', 'The {key}th server of TMMemcached must have host and port.', array('{key}' => $key)));
            }
            $this->_oMemcache->addServer($server[0], $server[1], $this->persistent, 50, $this->timeout);
        }

        $this->_initSerializer();
    }

    /**
     * Init the serializer.
     */
    private function _initSerializer()
    {
        if ($this->serializer === 'igbinary' && extension_loaded('igbinary')) {
            $this->_mSerializeHandler = 'igbinary_serialize';
            $this->_mUnserializeHandler = 'igbinary_unserialize';
        } else {
            $this->serializer = 'json';
        }
    }

    /**
     * Retrieve an item.
     * @param string $key
     * @return mixed
     */
    public function get($key)
    {
        if ($this->open) {
            $aParams = array($this->_oMemcache->get($this->prefix . $key));
            if ($this->_mUnserializeHandler === 'json_decode') {
                $aParams[] = true;
            }
            return call_user_func_array($this->_mUnserializeHandler, $aParams);
        }
        return false;
    }

    /**
     * Retrieve multiple items.
     * @param array $keys
     * @return array
     */
    public function getMulti($keys)
    {
        $aValues = $aKeys = array();
        foreach ($keys as $key) {
            $aKeys[] = $this->prefix . $key;
            $aValues[$key] = false;
        }
        if ($this->open) {
            $aTemp = $this->_oMemcache->get($aKeys);
            foreach ($keys as $i => $key) {
                if (isset($aTemp[$aKeys[$i]])) {
                    $aParams = array($aTemp[$aKeys[$i]]);
                    if ($this->_mUnserializeHandler === 'json_decode') {
                        $aParams[] = true;
                    }
                    $aValues[$key] = call_user_func_array($this->_mUnserializeHandler, $aParams);
                }
            }
        }
        return $aValues;
    }

    /**
     * Store an item.
     * @param string $key
     * @param mixed $value
     * @param integer $expire
     * @return boolean
     */
    public function set($key, $value, $expire = 0)
    {
        $value = call_user_func($this->_mSerializeHandler, $value);
        if ($expire > 0) {
            $expire += time();
        }
        return $this->_oMemcache->set($this->prefix . $key, $value, 0, $expire);
    }

    /**
     * Store multiple items.
     * @param array $items key/value
     * @param integer $expire
     * @return boolean
     */
    public function setMulti($items, $expire = 0)
    {
        if ($expire > 0) {
            $expire += time();
        }
        $aItems = array();
        foreach ($items as $key => $value) {
            $this->_oMemcache->set($this->prefix . $key, call_user_func($this->_mSerializeHandler, $value), 0, $expire);
        }
        return true;
    }

    /**
     * Delete an item.
     * @param string $key
     * @return boolean
     */
    public function delete($key)
    {
        return $this->_oMemcache->delete($this->prefix . $key);
    }

    /**
     * Delete multiple items.
     * @param string $keys
     * @return boolean
     */
    public function deleteMulti($keys)
    {
        foreach ($keys as &$key) {
            $key = $this->prefix . $key;
            $this->_oMemcache->delete($key);
        }
        return true;
    }

    /**
     * Invalidate all items in the cache.
     * @return boolean
     */
    public function flush()
    {
        return $this->_oMemcache->flush();
    }

    /**
     * Set a new expiration on an item.
     * @param string $key
     * @param integer $expire
     * @return boolean
     */
    public function touch($key, $expire = 0)
    {
        $value = $this->_oMemcache->get($this->prefix . $key);
        return $this->set($key, $value, $expire);
    }
}

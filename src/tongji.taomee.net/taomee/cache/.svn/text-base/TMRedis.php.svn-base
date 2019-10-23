<?php
/**
 * @file      TMRedis.php
 * @bref      TMRedis
 * @author    liujing <liujing@taomee.com>
 * @date      2017-5-25 下午2:54:56
 * @version   1.0.0
 */
 class TMRedis extends TMCache
 {
     /**
      * @var string redis server.
      */
     public $host = '127.0.0.1';
      
     /**
     * @var integar redis port.
     */
     public $port = 6379;
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
      * @var Redis
      */
     private $_oRedis = null;
     
     /**
      * Init the component.
      */
     public function init()
     {
         parent::init();
         if (!extension_loaded('redis')) {
             throw new TMCacheException(TM::t('taomee', 'TMRedis requires PHP redis extension to be loaded.'));
         }
         $this->_oRedis = new Redis();
         if (!isset($this->host) || !isset($this->port)) {
             throw new TMCacheException(TM::t('taomee', 'Server of TMRedis must be set.'));
         }
         $this->_oRedis->connect($this->host, $this->port);
     
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
             $aParams = array($this->_oRedis->get($this->prefix . $key));
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
             $aTemp = $this->_oRedis->mget($aKeys);
             foreach ($keys as $i => $key) {
                 if (isset($aTemp[$i])) {
                     $aParams = array($aTemp[$i]);
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
         return $this->_oRedis->set($this->prefix . $key, $value, $expire);
     }
     
     /**
      * Store multiple items.
      * @param array $items key/value
      * @param integer $expire
      * @return boolean
      */
     public function setMulti($items, $expire = 0)
     {
         $aItems = array();
         foreach ($items as $key => $value) {
             $value = call_user_func($this->_mSerializeHandler, $value);
             $this->_oRedis->set($this->prefix . $key, $value, $expire);
         }
     }
     
     /**
      * Delete an item.
      * @param string $key
      * @return boolean
      */
     public function delete($key)
     {
         return $this->_oRedis->del($this->prefix . $key);
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
         }
         return $this->_oRedis->del($keys);
     }
     
     /**
      * Invalidate all items in the cache.
      * @return boolean
      */
     public function flush()
     {
         return $this->_oRedis->flushall();
     }
     
     /**
      * Set a new expiration on an item.
      * @param string $key
      * @param integer $expire
      * @return boolean
      */
     public function touch($key, $expire = 0)
     {
         if ($expire > 0) {
             $expire += time();
         }
         return $this->_oRedis->expire($this->prefix . $key, $expire);
     }
 }
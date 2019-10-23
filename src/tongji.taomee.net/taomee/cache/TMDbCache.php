<?php
/**
* @file TMDbCache.php
* @brief 数据库缓存
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-02-13
*/
class TMDbCache extends TMCache
{
    /**
     * @var {mixed} connect db object or array
     */
    public $connectionId;

    /**
     * @var {string} cache table name
     */
    public $cacheTableName = 't_auto_cached_table';

    /**
     * @var {boolean} create table auto.
     */
    public $autoCreateCacheTable;

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
     * @var {object} object for database.
     */
    private $_db;

    /**
     * @var {boolean} whether clear the gc or not.
     */
    private $_gced;

    /**
     * @var {integer} gc probability.
     */
    private $_gcProbability = 100;

    /**
     * Init the component.
     */
    public function init()
    {
        parent::init();
        $this->_initSerializer();
        $this->getDbConnection();
        if ($this->autoCreateCacheTable) {
            $sql = "DELETE FROM {$this->cacheTableName} WHERE expire>0 AND expire<" . time();
            try {
                $this->getDbConnection()->createCommand($sql)->execute();
            } catch (Exception $e) {
                $this->createCacheTable($this->getDbConnection(), $this->cacheTableName);
            }
        }
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
     * @brief getDbConnection 
     * 获取数据库链接
     *
     * @return {TMDbConnection}
     */
    public function getDbConnection()
    {
        if ($this->_db !== null) {
            return $this->_db;
        } else if (($connectionId = $this->connectionId) !== null) {
            if (($this->_db = TM::app()->getComponent($connectionId)) instanceof TMDbConnection) {
                return $this->_db;
            }
            throw new TMCacheException(TM::t('taomee', 'The connectionId of TMDbCache is invalid.'));
        } else {
            $dbFile = TM::app()->getRuntimePath() . DIRECTORY_SEPARATOR . 'cache.db';
            return $this->_db = new TMDbConnection('sqlite:' . $dbFile);
        }
    }

    /**
     * @brief createCacheTable 
     * 创建缓存数据库
     *
     * @param {TMDbConnection} $db
     * @param {string} $tableName
     */
    protected function createCacheTable($db, $tableName)
    {
        $driver = $db->getDriverName();
        if ($driver == 'mysql') {
            $blob = 'LONGBLOB';
        } else {
            $blob = 'BLOB';
        }
        $sql = <<<EOD
CREATE TABLE $tableName (
    id CHAR(256) PRIMARY KEY,
    expire INTEGER,
    value $blob
);
EOD;
        $db->createCommand($sql)->execute();
    }

    /**
     * Retrieve an item.
     * @param string $key
     * @return mixed
     */
    public function get($key)
    {
        $time = time();
        $id = $this->prefix . $key;

        $sql = "SELECT value FROM {$this->cacheTableName} WHERE id='$id' AND (expire=0 OR expire>$time)";

        $aParams = array($this->getDbConnection()->createCommand($sql)->queryScalar());
        if ($this->_mUnserializeHandler === 'json_decode') {
            $aParams[] = true;
        }
        return call_user_func_array($this->_mUnserializeHandler, $aParams);
    }

    /**
     * Retrieve multiple items.
     * @param array $keys
     * @return array
     */
    public function getMulti($keys)
    {
        if (empty($keys)) {
            return array();
        }

        $aValues = $aKeys = array();
        foreach ($keys as $key) {
            $aKeys[] = $this->prefix . $key;
            $aValues[$key] = false;
        }

        $ids = implode("','", $aKeys);
        $time = time();
        $sql = "SELECT id, value FROM {$this->cacheTableName} WHERE id IN ('$ids') AND (expire=0 OR expire>$time)";

        $rows = $this->getDbConnection()->createCommand($sql)->queryAll();

        foreach ($rows as $row) {
            $aParams = array($row['value']);
            if ($this->_mUnserializeHandler === 'json_decode') {
                $aParams[] = true;
            }
            $aValues[$row['id']] = call_user_func_array($this->_mUnserializeHandler, $aParams);
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
        $this->delete($key);
        return $this->add($key, $value, $expire);
    }

    /**
     * @brief add 
     * add an item in database
     *
     * @param {string} $key
     * @param {mixed} $value
     * @param {integer} $expire
     *
     * @return {boolean}
     */
    protected function add($key, $value, $expire)
    {
        if (!$this->_gced && mt_rand(0,1000000) < $this->_gcProbability) {
            $this->gc();
            $this->_gced = true;
        }
        $id = $this->prefix . $key;
        if ($expire > 0) {
            $expire += time();
        } else {
            $expire = 0;
        }
        $sql = "INSERT INTO {$this->cacheTableName} (id, value, expire) VALUES('$id', :value, '$expire')";
        try {
            $command = $this->getDbConnection()->createCommand($sql);
            $command->bindValue(':value', call_user_func($this->_mSerializeHandler, $value), PDO::PARAM_LOB);
            $command->execute();
            return true;
        } catch (Exception $e) {
            return false;
        }
    }

    /**
     * @brief gc 
     * remove all items that expired
     */
    protected function gc()
    {
        $this->getDbConnection()->createCommand("DELETE FROM {$this->cacheTableName} WHERE expire>0 AND expire<" . time())->execute();
    }

    /**
     * Store multiple items.
     * @param array $items key/value
     * @param integer $expire
     * @return boolean
     */
    public function setMulti($items, $expire = 0)
    {
        foreach ($items as $key => $value) {
            $this->delete($key);
            $this->add($key, $value, $expire);
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
        $id = $this->prefix . $key;
        $sql = "DELETE FROM {$this->cacheTableName} WHERE id = '$id'";
        $this->getDbConnection()->createCommand($sql)->execute();
        return true;
    }

    /**
     * Delete multiple items.
     * @param string $keys
     * @return boolean
     */
    public function deleteMulti($keys)
    {
        foreach ($keys as $key) {
            $this->delete($key);
        }
        return true;
    }

    /**
     * Invalidate all items in the cache.
     * @return boolean
     */
    public function flush()
    {
        return $this->getDbConnection()->createCommand("DELETE FROM {$this->cacheTableName}")->execute();
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
        $id = $this->prefix . $key;
        $sql = "UPDATE {$this->cacheTableName} SET expire = $expire WHERE id = '$id'";
        return $this->getDbConnection()->createCommand($sql)->execute();
    }
}

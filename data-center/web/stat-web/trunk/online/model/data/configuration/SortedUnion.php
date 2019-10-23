<?php
abstract class data_configuration_SortedUnion extends TMModel
{
    /**
     * @var array Sorted configurations, take something unique as keys.
     */
    private $_sortedConf;

    /**
     * @var array Union sqls.
     */
    private $_unionSql;

    /**
     * @var array Parameters for union sqls.
     */
    private $_unionParam;

    /**
     * @var array Cache for reusing.
     */
    private $_cache;

    /**
     * Initialize some properties.
     */
    public function __construct()
    {
        $this->_sortedConf = $this->_unionSql = $this->_unionParam = array();
        $this->_cache = null;
    }

    /**
     * Return sorted configurations.
     * @return array
     */
    public function getSortedConf()
    {
        return $this->_sortedConf;
    }

    /**
     * Add one configuration to the sorted configurations.
     * @param string $key
     * @param mixed  $conf
     * @return data_configuration_SortedUnion
     */
    public function addSortedConf($key, $conf)
    {
        $conf['_uuid'] = $key;
        $this->_sortedConf[] = $conf;

        return $this;
    }

    /**
     * Add one union with sql and parameters.
     * @param string      $sql
     * @param ...
     * @return data_configuration_SortedUnion
     */
    public function addUnion($sql)
    {
        $this->_unionSql[] = $sql;

        $num = func_num_args();
        if ($num > 1) {
            $args = func_get_args();
            if (is_array($args[1])) {
                $this->_unionParam = array_merge($this->_unionParam, $args[1]);
            } else {
                foreach ($args as $k => $v) {
                    if ($k) {
                        $this->_unionParam[] = $v;
                    }
                }
            }
        }

        return $this;
    }

    /**
     * Get cache.
     * @return array
     */
    public function getCache()
    {
        return $this->_cache;
    }

    /**
     * Set cache.
     * @param array $cache
     * @return data_configuration_SortedUnion
     */
    public function setCache($cache)
    {
        $this->_cache = $cache;

        return $this;
    }

    /**
     * Union the result.
     * @return array
     */
    public function union()
    {
        if (!$this->_unionSql) {
            return array();
        }

        return $this->getDb()->createCommand('(' . implode(') UNION ALL (', $this->_unionSql) . ')')
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->queryAll($this->_unionParam);
    }
}

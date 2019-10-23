<?php
class tool_DataDb extends TMFormModel
{
    /**
     * @var string The memcache key of the data database info.
     */
    public $cacheKey = 'data_db_info';

    /**
     * @var string The prefix of data database component.
     */
    public $componentPrefix = 'dataDb';

    /**
     * @var array Map hash to database configuration.
     */
    private static $_dataDbMap = array();

    /**
     * @var array Database configuration.
     */
    private static $_dataDbConf = array();

    /**
     * Return the table name.
     * @return string
     */
    public function tableName()
    {
        return 't_db_info';
    }

    /**
     * Attributes.
     * @return array
     */
    public function attributes()
    {
        return array('db_id', 'db_user', 'db_pwd', 'db_host', 'db_port');
    }

    /**
     * Get data database component.
     * @param  integer        $iIdx
     * @return TMDbConnection
     */
    public function getDataDb($iIdx)
    {
        $componentId = $this->componentPrefix . $iIdx;
        if (TM::app()->hasComponent($componentId)) {
            return TM::app()->$componentId;
        }

        if (!isset(self::$_dataDbConf[$iIdx])) {
            throw new TMException(TM::t('tongji', '非法的序号值{idx}！', array('{idx}' => $iIdx)));
        }
        TM::app()->setComponent($componentId, array_merge(
            self::$_dataDbConf[$iIdx],
            array('class' => 'system.db.TMDbConnection', 'charset' => 'utf8')
        ));
        return TM::app()->$componentId;
    }

    /**
     * Get data database index.
     * @param  integer $iHash
     * @return integer
     */
    public function getIndex($iHash)
    {
        $iHash = (int)($iHash/100);
        if (!self::$_dataDbMap && !self::$_dataDbConf) {
            list(self::$_dataDbMap, self::$_dataDbConf) = $this->getDataDbInfo();
        }
        if (!isset(self::$_dataDbMap[$iHash])) {
            throw new TMException(TM::t('tongji', '非法的哈希值{hash}！', array('{hash}' => $iHash)));
        }
        return self::$_dataDbMap[$iHash];
    }

    /**
     * Get data database infomation.
     * @return array
     */
    public function getDataDbInfo()
    {
        $aInfo = TM::app()->getCache()->get($this->cacheKey);
        if ($aInfo) return $aInfo;

        $aDb = $this->findAll();
        $idx = 0;
        $aMap = $aConf = $aIdx = array();
        foreach ($aDb as $db) {
            $uk = sprintf('%s:%s:%s:%s', $db['db_host'], $db['db_port'], $db['db_user'], $db['db_pwd']);
            if (!isset($aIdx[$uk])) {
                $aConf[$idx] = array(
                    'connectionString' => sprintf('mysql:host=%s;port=%s', $db['db_host'], $db['db_port']),
                    'username'         => $db['db_user'],
                    'password'         => $db['db_pwd']
                );
                $aIdx[$uk] = $idx++;
            }
            $aMap[$db['db_id']] = $aIdx[$uk];
        }

        $aInfo = array($aMap, $aConf);
        TM::app()->getCache()->set($this->cacheKey, $aInfo);
        return $aInfo;
    }
}

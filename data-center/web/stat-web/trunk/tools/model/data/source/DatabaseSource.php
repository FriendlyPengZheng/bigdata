<?php
class data_source_DatabaseSource extends data_source_Source
{
    /**
     * @var tool_DataDb The database helper.
     */
    private $_dbHelper = null;

    /**
     * @var data_source_SQLHelper Helper for building sql.
     */
    private $_sqlHelper = null;

    /**
     * @var array TMDbCommand
     */
    private $_commands = null;

    /**
     * Create a data_source_DatabaseSource instance.
     * @param data_source_SQLHelper $sqlHelper
     */
    public function __construct(data_source_SQLHelper $sqlHelper = null)
    {
        $this->_dbHelper = new tool_DataDb();

        if (isset($sqlHelper)) {
            $this->_sqlHelper = $sqlHelper;
        } else {
            $this->_sqlHelper = new data_source_TimeSeriesSQLHelper();
        }
    }

    /**
     * Get data.
     * @param  array $aUserParam
     * @return array
     */
    public function get($aUserParam)
    {
        $aData = array();
        $aQuery = $this->getQuery(isset($aUserParam['calc_type']) ? $aUserParam['calc_type'] : 'avg');
        foreach ($aQuery as $idx => $group) {
            foreach ($group as $query) {
                if (!$query['union']) continue;
                $aData += $this->getCommand($idx)
                    ->setText('(' . implode(') UNION (', $query['union']) . ') ORDER BY _key')
                    ->queryAll($query['param']);
            }
        }
        return $aData;
    }

    /**
     * Get things for querying, each has an union array and a parameter array.
     * @param string $calcType
     * @return array
     */
    protected function getQuery($calcType = 'avg')
    {
        $aQuery = $aPos = array();
        $configurationSet = $this->getConfigurationSet();
        if ($configurationSet->isEmpty()) return $aQuery;

        $oPeriodSet = $this->getPeriodSet();
        $sTableType = $oPeriodSet->getTableType();
        $this->_sqlHelper->setTableType($sTableType)->setCalcType($calcType);
        foreach ($configurationSet as $configuration) {
            if ($configuration instanceof data_configuration_UndefinedConfiguration) continue;
            $iHash = $configuration->getHash();
            $iIdx = $this->_dbHelper->getIndex($iHash);
            if (!isset($aQuery[$iIdx])) {
                $aPos[$iIdx] = array('row' => 0, 'col' => 0);
                $aQuery[$iIdx][0] = array('union' => array(), 'param' => array());
            }
            $sTable = $this->getTable($iHash, $sTableType);
            foreach ($oPeriodSet as $period) {
                $aQuery[$iIdx][$aPos[$iIdx]['row']]['union'][$aPos[$iIdx]['col']] =
                    'SELECT CONCAT(data_id, ":", gpzs_id), ' .
                    $this->_sqlHelper->getSelect() .
                    ' FROM ' . $sTable .
                    ' WHERE time >= ? AND time <= ? AND data_id = ? AND gpzs_id = ? ' .
                    $this->_sqlHelper->getExtra($period);
                array_push($aQuery[$iIdx][$aPos[$iIdx]['row']]['param'],
                    $period->getFrom(), $period->getTo(),
                    $configuration->getDataId(), $configuration->getGpzsId());
                if (++$aPos[$iIdx]['col'] > 1000) {
                    $aPos[$iIdx]['col'] = 0;
                    $aQuery[$iIdx][++$aPos[$iIdx]['row']] = array('union' => array(), 'param' => array());
                }
            }
        }
        return $aQuery;
    }

    /**
     * Get database query command.
     * @param  integer $iIdx
     * @return TMDbCommand
     */
    protected function getCommand($iIdx)
    {
        if (isset($this->_commands[$iIdx])) return $this->_commands[$iIdx]->reset();

        return $this->_commands[$iIdx] =
            $this->_dbHelper->getDataDb($iIdx)
                ->createCommand()
                ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP);
    }

    /**
     * Get data table.
     * @param integer $iHash
     * @param string $sTableType
     * @return string
     */
    protected function getTable($iHash, $sTableType)
    {
        return 'db_td_data_' . (int)($iHash/100) . '.t_db_data_' . $sTableType . '_' . ($iHash%100);
    }
}

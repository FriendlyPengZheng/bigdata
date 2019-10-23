<?php
class common_DataInfo extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_data_info';
    }

    public function attributes()
    {
        return array(
            'data_id', 'data_name', '(sthash mod 10000) as sthash', 'range',
            'IF(range_name = "", range, range_name) as range_name', 'add_time'
        );
    }

    /**
     * Get data range info by rid, type.
     * @param  array $info
     * @return array
     */
    public function getRangeByRid($info, $tags = array())
    {
        TMValidator::ensure(is_array($tags), TM::t('tongji', 'tags筛选条件不是数组,格式不对'));
        $aParams = array();
        $command = $this->getDb()->createCommand()
            ->select(implode(',', $this->attributes()) . ',data_id AS id,data_name AS name')
            ->from($this->tableName())
            ->where('hide = 0 AND r_id = ? AND type = ?');
        if (!empty($tags)) {
                 $command->andWhere($tags[0] . $tags[1] . '?');
                 $aParams[] = $tags[2];
        }
        return $command->order('display_order ASC')
            ->queryAll(array_merge(array($info['r_id'], $info['type']), $aParams));
    }

    /**
     * 设置名称
     * @param  array   $aUserParam
     * @return integer
     */
    public function setName($aUserParam)
    {
        $this->data_id = $aUserParam['id'];
        $this->data_name = $aUserParam['name'];
        return $this->update(array('data_name'));
    }

    /**
     * 设置range名称
     * @param  array   $aUserParam
     * @return integer
     */
    public function setRangeName($aUserParam)
    {
        $aUserParam['id'] = (array)$aUserParam['id'];
        if (!$aUserParam['id']) return;
        $aCondition = $aParam = array();
        foreach ($aUserParam['id'] as $key => $id) {
            $aCondition[] = ':data_id' . $key;
            $aParam[':data_id' . $key] = $id;
        }
        return $this->updateByCondition(
            array('range_name' => $aUserParam['name']),
            'data_id IN (' . implode(',', $aCondition) . ')',
            $aParam
        );
    }

    /**
     * 根据report/result更新datainfo
     * @param array $aFields
     * @param array $aRid
     * @return boolean
     */
    public function updateByRid($aFields, $aRid)
    {
        $sCondition = 'r_id = :r_id AND type = :type';
        $aParam = array(':r_id' => $aRid['r_id'], ':type' => $aRid['type']);
        if (isset($aRid['range'])) {
            $sCondition .= ' AND range = :range';
            $aParam[':range'] = $aRid['range'];
        }
        return $this->updateByCondition($aFields, $sCondition, $aParam);
    }

    /**
     * 更新数据名称，数据名称包含range
     * @param  string  $sPrefix
     * @param  string  $sSuffix
     * @param  array   $aRid
     * @return boolean
     */
    public function setNameWithRangeByRid($sPrefix, $sSuffix, $aRid)
    {
        return $this->getDb()->createCommand(
            'UPDATE ' . $this->tableName() . ' ' .
            'SET data_name = CONCAT(?, range, ?) ' .
            'WHERE r_id = ? AND type = ?'
        )->execute(array($sPrefix, $sSuffix, $aRid['r_id'], $aRid['type']));
    }

    /**
     * Update data info by the given condition.
     * @param  array   $aFields
     * @param  string  $sCondition
     * @param  array   $aParam
     * @return boolean
     */
    public function updateByCondition($aFields, $sCondition, $aParam = array())
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(), $aFields, $sCondition, $aParam
        );
    }

    /**
     * Get range intersection of the given stat items
     *
     * @param  array $aUserParam
     * @return array
     */
    public function getCommonRange($aUserParam)
    {
        if (!is_array($aUserParam['stat_info']) || !$aUserParam['stat_info']) {
            return array();
        }
        $commonRange = null;
        foreach ($aUserParam['stat_info'] as $info) {
            $info['type'] = TMArrayHelper::assoc('type', $info, $aUserParam['type']);
            $stat = common_Stat::getStatByUk(TMArrayHelper::assoc('game_id', $info, $aUserParam['game_id']), array($info));
            if (!$stat || $stat[0]['type'] === 'undefined') {
                return array();
            }
            $range = TMArrayHelper::column($this->getRangeByRid($stat[0]), null, 'range');
            $commonRange = $commonRange === null ? $range : array_intersect_key($commonRange, $range);
            if (!$commonRange) {
                return array();
            }
        }
        return array_values($commonRange);
    }

    public function getDataListByRids($aRIds)
    {
        $aINParam = $aINPos = array();
        foreach ($aRIds as $r) {
            array_push($aINParam, $r['r_id'], $r['type']);
            array_push($aINPos, '(r_id = ? AND type = ?)');
        }
        return $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('CONCAT_WS(":", r_id, type),' . implode(',', $this->attributes()))
            ->from($this->tableName())
            ->where('hide = 0')
            ->andWhere(implode(' OR ', $aINPos))
            ->order('display_order ASC')
            ->queryAll($aINParam);
    }

    public $aUniqueKey = array();
    public $aSqlStatement = array();
    public $aSqlParameter = array();
    public $select;
    private $_currentIndex = 0;

    public function getDataList($aConfigureInfo)
    {
        $this->select =  implode(',', $this->attributes());
        foreach ($aConfigureInfo as $configure) {
            if (isset($configure['data_id'])) {
                $this->_dataId($configure);
            } elseif (isset($configure['range'])) {
                $this->_rIdTypeRange($configure);
            } else {
                $this->_rIdType($configure);
            }
        }
        if (empty($this->aSqlStatement)) return array();

        $configuration = array();
        while ($this->_currentIndex >= 0) {
            $configuration += $this->_getConfiguration($this->_currentIndex);
            $this->_currentIndex--;
        }
        return $configuration;
    }

    private function _dataId($configure)
    {
        $this->_addStatement('SELECT data_id AS id,' . $this->select .
            ' FROM ' . $this->tableName() .
            ' WHERE hide = 0 AND data_id = ?');
        $this->_addParameter($configure['data_id']);
        $this->aUniqueKey[$configure['data_id']] = $configure;
    }

    private function _rIdType($configure)
    {
        $this->_addStatement('SELECT CONCAT_WS(":", r_id, type) AS id,' . $this->select .
            ' FROM ' . $this->tableName() .
            ' WHERE hide = 0 AND r_id = ? AND type = ?' .
            ' ORDER BY display_order ASC');
        $this->_addParameter($configure['r_id']);
        $this->_addParameter($configure['type']);
        $this->aUniqueKey[$configure['r_id'] . ':' . $configure['type']] = $configure;
    }

    private function _rIdTypeRange($configure)
    {
        $this->_addStatement('SELECT CONCAT_WS(":", r_id, type, range) AS id,' . $this->select .
            ' FROM ' . $this->tableName() .
            ' WHERE hide = 0 AND r_id = ? AND type = ? AND range = ?');
        $this->_addParameter($configure['r_id']);
        $this->_addParameter($configure['type']);
        $this->_addParameter($configure['range']);
        $this->aUniqueKey[$configure['r_id'] . ':' . $configure['type'] . ':' . $configure['range']] = $configure;
    }

    private function _addStatement($statement)
    {
        if (!isset($this->aSqlStatement[0])) {

        }
        else if (count($this->aSqlStatement[$this->_currentIndex]) > 100) {
            $this->aSqlStatement[++$this->_currentIndex] = array();
            $this->aSqlParameter[$this->_currentIndex] = array();
        }
        $this->aSqlStatement[$this->_currentIndex][] = $statement;
    }

    private function _addParameter($parameter)
    {
        $this->aSqlParameter[$this->_currentIndex][] = $parameter;
    }

    private function _getConfiguration($iIdIndex)
    {
        if (isset($this->aSqlStatement[$iIdIndex])) {
            return $this->getDb()->createCommand()
                ->setText('(' . implode(') UNION ALL (', $this->aSqlStatement[$iIdIndex]) . ')')
                ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
                ->queryAll($this->aSqlParameter[$iIdIndex]);
        }
        return false;
    }
}

<?php
class module_Set extends TMFormModel
{
    public function tableName()
    {
        return 't_web_set';
    }

    public function findById()
    {
        return $this->findAll(array(
            'condition' => array('set_id' => $this->set_id)
        ));
    }

    public function findByGameId()
    {
        return $this->findAll(array(
            'select' => implode(',', $this->attributes()) .
                ',set_id AS r_id,("set") AS type,set_name AS r_name,("1") AS is_multi',
            'condition' => array('game_id' => $this->game_id)
        ));
    }

    public function parseSetData($component, $aDataIndex, $iGameId)
    {
        if (!is_array($aDataIndex)) {
            $aDataIndex = explode(':', $aDataIndex);
        }

        $dataList = array();
        if ($component['process_type'] === 'expres') {
            $dataList = $this->parseExprs($component, $aDataIndex, $iGameId);
        } else {
            $dataList = $this->parseDistr($component, $aDataIndex, $iGameId);
        }

        return $this->saveSetData($component, $aDataIndex, $iGameId, $dataList);
    }

    /**
     * 解析表达式
     * @param  array   $component
     * @param  array   $aDataIndex
     * @param  integer $iGameId
     * @return array
     */
    protected function parseExprs($component, $aDataIndex, $iGameId)
    {
        $dataInfo = $this->parseStr($component['data_info']);
        $builder = new data_configuration_ConfigurationBuilder();
        $configurationSet = $builder->build(1, $dataInfo, 0, $iGameId)->values();
        $dataList = array();
        foreach ($aDataIndex as $exprIdx) {
            if (!isset($component['process_list'][$exprIdx])) continue;
            $exprInfo = $component['process_list'][$exprIdx];
            $exprData = array_values(array_filter(explode(',', trim($exprInfo['data'])), 'strlen'));
            $operandCount = count($exprData);
            if (!$operandCount || !$exprInfo['expre']) continue; // 至少一个操作数，且表达式不为空
            $dataStr = '';
            foreach ($exprData as $idx => $dataIdx) {
                if (!isset($configurationSet[$dataIdx]) ||
                        $configurationSet[$dataIdx] instanceof data_configuration_UndefinedConfiguration ||
                        $configurationSet[$dataIdx] instanceof data_configuration_ConfigurationSet) {
                    continue 2;
                }
                if ($operandCount === 1) { // 只有一个操作数，则放入data_id，置data_expr为空
                    $exprInfo['data_id'] = $configurationSet[$dataIdx]->getDataId();
                    $exprInfo['data_expr'] = '';
                    $exprInfo['factor'] = $configurationSet[$dataIdx]->getFactor(); // 不考虑{1}*100的情况
                    $dataList[] = $exprInfo;
                    continue 2;
                }
                if ((float)$configurationSet[$dataIdx]->getFactor() !== 1.0) {
                    $exprInfo['expre'] = str_replace('{' . $idx . '}',
                        '({' . $idx . '}*' . (float)$configurationSet[$dataIdx]->getFactor() . ')', $exprInfo['expre']);
                }
                $dataStr .= $configurationSet[$dataIdx]->getDataId() . ';';
            }
            $exprInfo['data_id'] = 0;
            $exprInfo['data_expr'] = rtrim($dataStr, ';') . '|' . $exprInfo['expre'];
            $exprInfo['factor'] = 1;
            $dataList[] = $exprInfo;
        }
        return $dataList;
    }

    /**
     * 解析分布
     * @param  array   $component
     * @param  array   $aDataIndex
     * @param  integer $iGameId
     * @return array
     */
    protected function parseDistr($component, $aDataIndex, $iGameId)
    {
        $dataInfo = $this->parseStr($component['data_info']);
        $filteredDataInfo = array();
        foreach ($aDataIndex as $distrIdx) {
            if (!isset($component['process_list'][$distrIdx])) continue;
            $distrInfo = $component['process_list'][$distrIdx];
            $distrData = array_values(array_filter(explode(',', trim($distrInfo['data'])), 'strlen'));
            foreach ($distrData as $idx => $dataIdx) {
                if (!isset($dataInfo[$dataIdx]) ||
                        (isset($dataInfo[$dataIdx]['percentage']) &&
                        $dataInfo[$dataIdx]['percentage'] == data_configuration_Distr::PERCENTAGE_DATALIZED)) {
                    continue;
                }
                $temp = data_configuration_DistrFactoryManager
                    ::createDistrFactory($distrInfo['distr_by'])
                    ->filterDataInfo($dataInfo[$dataIdx]);
                $temp['distr_by']   = $distrInfo['distr_by'];
                $temp['distr_name'] = $distrInfo['distr_name'];
                $temp['dimen_name'] = $distrInfo['dimen_name'];
                $filteredDataInfo[] = $temp;
            }
        }
        $builder = new data_configuration_ConfigurationBuilder();
        return $this->extractConfiguration($builder->build(1, $filteredDataInfo, 0, $iGameId));
    }

    /**
     * 提取配置信息
     * @param  data_configuration_Configurable $configuration
     * @return array
     */
    protected function extractConfiguration(data_configuration_Configurable $configuration)
    {
        if ($configuration instanceof data_configuration_UndefinedConfiguration) {
            return array();
        }

        if ($configuration instanceof data_configuration_ConfigurationSet) {
            $dataList = array();
            foreach ($configuration as $conf) {
                $dataList = array_merge($dataList, $this->extractConfiguration($conf));
            }
            return $dataList;
        }

        if ($configuration instanceof data_configuration_Configuration) {
            $temp = array(
                'data_name' => $configuration->getDataName(),
                'data_id'   => $configuration->getDataId(),
                'data_expr' => '',
                'factor'    => $configuration->getFactor(),
                'precision' => $configuration->getPrecision(),
                'unit'      => $configuration->getUnit()
            );
            $extra = $configuration->getExtra();
            if (data_configuration_DistrFactoryManager::isRangeDistr($extra['distr_by'])) {
                $temp['data_name'] = $extra['distr_name'] . '【' . $extra['dimen_name'] . '】' . $extra['range_name'];
            } elseif (data_configuration_DistrFactoryManager::isSstidDistr($extra['distr_by'])) {
                $temp['data_name'] = $extra['distr_name'] . '【' . $extra['dimen_name'] . '】' . $extra['sstid_name'];
            }
            return array($temp);
        }
    }

    /**
     * 解析Query String成数组
     * @param array $arr
     * @return array
     */
    protected function parseStr($arr)
    {
        $temp = array();
        foreach ($arr as $str) {
            parse_str($str, $temp[]);
        }
        return $temp;
    }

    /**
     * 保存集合数据，存在则更新
     * @param  array   $component
     * @param  array   $aDataIndex
     * @param  integer $iGameId
     * @param  array   $dataList
     * @return integer 集合ID
     */
    protected function saveSetData($component, $aDataIndex, $iGameId, $dataList)
    {
        $transaction = $this->getDb()->beginTransaction();
        try {
            $iSetId = $this->saveSet($component, $iGameId, $aDataIndex);
            $setData = new module_SetData();
            $setData->deleteAllByAttributes(array('set_id' => $iSetId));
            foreach ($dataList as $info) {
                $info['game_id'] = $iGameId;
                $info['set_id']  = $iSetId;
                $setData = new module_SetData();
                $setData->attributes = $info;
                $setData->replace();
            }
            $transaction->commit();
            return $iSetId;
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 保存，若冲突则更新
     * @param  array   $component
     * @param  integer $iGameId
     * @param  array   $aDataIndex
     * @return integer 集合ID
     */
    protected function saveSet($component, $iGameId, $aDataIndex)
    {
        $authId = TM::app()->navigator->getAuthIdByNaviKey(array_filter(explode('-', $component['module_key'])), $iGameId);
        $dataIndex = implode(':', $aDataIndex);
        $this->getDb()->createCommand(
            'INSERT INTO ' . $this->tableName() . ' (set_name,game_id,component_id,data_index,auth_id) ' .
            'VALUES (?,?,?,?,?) ' .
            'ON DUPLICATE KEY UPDATE set_id=LAST_INSERT_ID(set_id),set_name=?,data_index=?,auth_id=?'
        )->execute(array(
            $component['title'],
            $iGameId,
            $component['component_id'],
            $dataIndex,
            $authId,
            $component['title'],
            $dataIndex,
            $authId
        ));
        return $this->getDb()->getLastInsertID();
    }
}

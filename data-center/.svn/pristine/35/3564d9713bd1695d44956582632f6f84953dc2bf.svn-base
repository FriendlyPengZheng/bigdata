<?php
class gamecustom_report_Item extends gamecustom_report_Base
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_item_config_info';
    }

    /**
     * Get configuration for report or result.
     * @param array $aInfo
     * @return array
     */
    public function getConfiguration($aInfo)
    {
        if ($aInfo['is_setted']) {
            $aConfig = $this->getConfig($aInfo['r_id'], $aInfo['type']);
            return $this->buildConfig($aConfig);
        } else {
            $aTemp['pre_name'] = $aTemp['suf_name'] = '';
            if (isset($this->aOpTypeNames[$aInfo['op_type']])) {
                $aTemp['suf_name'] = $this->aOpTypeNames[$aInfo['op_type']];
            }
            return $aTemp;
        }
    }

    /**
     * Get config from database.
     * @param integer $iRid
     * @param string $sType
     * @return array
     */
    protected function getConfig($iRid, $sType)
    {
        return $this->getDb()->createCommand()
            ->select('data_name')
            ->from($this->tableName())
            ->where('r_id = ? AND type = ?')
            ->queryRow(array($iRid, $sType));
    }

    /**
     * Build config for display.
     * @param array $aConfig
     * @return array
     */
    protected function buildConfig($aConfig)
    {
        $aTemp = array();
        if ($aConfig) {
            $temp = explode('[$item]', $aConfig['data_name']);
            if (isset($temp[1])) {
                $aTemp['pre_name'] = $temp[0];
                $aTemp['suf_name'] = $temp[1];
            }
            $aTemp['data_name'] = $aConfig['data_name'];
        }
        return $aTemp;
    }

    /**
     * Set configuration for report/result.
     * @param array $aInfo
     * @param array $aUserParam
     * @return boolean
     */
    public function setConfiguration($aInfo, $aUserParam)
    {
        $transaction = $this->getDb()->beginTransaction();
        try {
            // 标识report/result为设置过区间分布
            if (!$aInfo['is_setted']) {
                $model = new common_Report();
                $model->updateStatItem($aInfo['r_id'], array('is_setted' => 1));
            }
            // 修改config
            $aRid = array('r_id' => $aInfo['r_id'], 'type' => $aInfo['type']);
            $this->replaceConfig(array_merge(
                array('data_name' => $aUserParam['pre_name'] . '[$item]' . $aUserParam['suf_name']),
                $aRid
            ));
            // 修改已存在的数据名称
            $model = new common_DataInfo();
            $model->setNameWithRangeByRid($aUserParam['pre_name'], $aUserParam['suf_name'], $aRid);
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 更新配置
     * @param array $aFields
     * @return boolean
     */
    protected function replaceConfig($aFields)
    {
        $sSql = ' INSERT INTO ' . $this->tableName()
              . ' (r_id, type, data_name)'
              . ' VALUES(:r_id, :type, :data_name)'
              . ' ON DUPLICATE KEY UPDATE data_name = :data_name';
        return $this->getDb()->createCommand()->setText($sSql)->execute(array(
            ':r_id' => $aFields['r_id'],
            ':type' => $aFields['type'],
            ':data_name' => $aFields['data_name']
        ));
    }
}

<?php
class gamecustom_report_Distr extends gamecustom_report_Base
{
    /**
     * @var array names for op_type
     */
    protected $aOpTypeNames = array(
        'distr_sum' => '求和',
        'distr_max' => '最大值',
        'distr_set' => '人次'
    );

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_distr_range_info';
    }

    /**
     * Get configuration for report or result.
     * @param array $aInfo
     * @return array
     */
    public function getConfiguration($aInfo)
    {
        if ($aInfo['is_setted']) {
            $aRange = $this->getRange($aInfo['r_id'], $aInfo['type']);
            return $this->buildRange($aRange);
        } else {
            $aTemp['pre_name'] = $aTemp['suf_name'] = '';
            if (isset($this->aOpTypeNames[$aInfo['op_type']])) {
                $aTemp['suf_name'] = $this->aOpTypeNames[$aInfo['op_type']];
            }
            return $aTemp;
        }
    }

    /**
     * Get range list from database.
     * @param integer $iRid
     * @param string $sType
     * @return array
     */
    protected function getRange($iRid, $sType)
    {
        return $this->getDb()->createCommand()
            ->select('data_name, lower_bound, upper_bound')
            ->from($this->tableName())
            ->where('r_id = ? AND type = ?')
            ->order('lower_bound ASC')
            ->queryAll(array($iRid, $sType));
    }

    /**
     * Build the range array for display.
     * @param array $aRange
     * @return array
     */
    protected function buildRange($aRange)
    {
        $aTemp = array();
        foreach ($aRange as $range) {
            if ($range['lower_bound'] == -1) {
                $aTemp['low_flag'] = strpos($range['data_name'], '[$low]') !== false;
                $aTemp['high_flag'] = strpos($range['data_name'], '[$high]') !== false;
                $seperator = ($aTemp['low_flag'] XOR $aTemp['high_flag']) ?
                    ($aTemp['low_flag'] ? '[$low]' : '[$high]') : '[$low]~[$high]';
                $temp = explode($seperator, $range['data_name']);
                if (isset($temp[1])) {
                    $aTemp['pre_name'] = $temp[0];
                    $aTemp['suf_name'] = $temp[1];
                }
                $aTemp['data_name'] = $range['data_name'];
                continue;
            }
            $aTemp['range'][] = array(
                'low'  => $range['lower_bound'],
                'high' => $range['upper_bound'] ? $range['upper_bound'] : '+∞'
            );
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
        $this->checkRange($aUserParam);
        $this->parseRange($aUserParam);
        $transaction = $this->getDb()->beginTransaction();
        try {
            // 标识report/result为设置过区间分布
            if (!$aInfo['is_setted']) {
                $model = new common_Report();
                $model->updateStatItem($aInfo['r_id'], array('is_setted' => 1));
            }
            // 删除当前区间分布
            $this->deleteRange($aInfo['r_id'], $aInfo['type']);
            // 隐藏当前区间的数据
            $model = new common_DataInfo();
            $aRid = array('r_id' => $aInfo['r_id'], 'type' => $aInfo['type']);
            $model->updateByRid(array('hide' => 1), $aRid);
            foreach ($aUserParam['distr_range'] as $key => $range) {
                // 插入行新的区间分布
                $this->insertRange(array_merge($aInfo, $range));
                // 对于非配置项，如果数据中存在区间，则显示且更新数据名称
                if ($key != -1) {
                    $model->updateByRid(array('data_name' => $range['data_name'], 'hide' => 0), array_merge($range, $aRid));
                }
            }
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Delete range list from database.
     * @param integer $iRid
     * @param string $sType
     * @return boolean
     */
    protected function deleteRange($iRid, $sType)
    {
        return $this->getDb()->createCommand()->delete(
            $this->tableName(),
            'r_id = ? AND type = ?',
            array($iRid, $sType)
        );
    }

    /**
     * Insert a range.
     * @param array $aRange
     * @return boolean
     */
    protected function insertRange($aRange)
    {
        return $this->getDb()->createCommand()->insert(
            $this->tableName(), array(
                'r_id' => $aRange['r_id'],
                'type' => $aRange['type'],
                'data_name' => $aRange['data_name'],
                'lower_bound' => $aRange['lower_bound'],
                'upper_bound' => $aRange['upper_bound'],
                'range' => $aRange['range']
            )
        );
    }

    /**
     * Check range.
     * @param array $aUserParam
     */
    protected function checkRange(&$aUserParam)
    {
        $aUserParam['low_flag'] = $aUserParam['low_flag'] ? 1 : 0;
        $aUserParam['high_flag'] = $aUserParam['high_flag'] ? 1 : 0;
        TMValidator::ensure($aUserParam['low_flag'] || $aUserParam['high_flag'],
            TM::t('tongji', '区间开始与结束至少需要一个！'));

        $iCount = count($aUserParam['range_low']);
        TMValidator::ensure($iCount, TM::t('tongji', '请指定区间！'));
        TMValidator::ensure(is_array($aUserParam['range_low']) && is_array($aUserParam['range_high'])
            && $iCount === count($aUserParam['range_high']),
            TM::t('tongji', '区间开始与结束不一致！'));
        $aUserParam['range_low'] = array_values($aUserParam['range_low']);
        $aUserParam['range_high'] = array_values($aUserParam['range_high']);

        $aUserParam['range'] = array();
        foreach ($aUserParam['range_low'] as $key => $low) {
            $low  = (int)$low;
            $high = (int)$aUserParam['range_high'][$key];
            if ($high) {
                TMValidator::ensure($low <= $high,
                    TM::t('tongji', '区间结束{high}不能比开始{low}小！', array('{high}' => $high, '{low}' => $low)));
            }
            $aUserParam['range'][$low] = $high;
        }
    }

    /**
     * Parse the range array for update database.
     * @param array $aUserParam
     */
    protected function parseRange(&$aUserParam)
    {
        $iCount = count($aUserParam['range']);
        TMValidator::ensure($iCount > 1, TM::t('tongji', '区间至少为2个！'));
        ksort($aUserParam['range']);
        $seperator = ($aUserParam['low_flag'] XOR $aUserParam['high_flag']) ?
            ($aUserParam['low_flag'] ? '[$low]' : '[$high]') : '[$low]~[$high]';
        $iTemp = $iIdx = 0;
        $aUserParam['distr_range'] = array();
        foreach ($aUserParam['range'] as $low => $high) {
            TMValidator::ensure($iTemp == $low,
                TM::t('tongji', '区间开始{low}应当是{temp}', array('{low}' => $low, '{temp}' => $iTemp)));
            if ($iIdx === $iCount - 1 && $high === 0) {
                $iTemp = $low;
                break;
            }
            $aUserParam['distr_range'][] = array(
                'data_name' => $aUserParam['pre_name'] .
                    str_replace(array('[$low]', '[$high]'), array($low, $high), $seperator) . $aUserParam['suf_name'],
                'range' => $iIdx === 0 ? ('<' . $high) : sprintf('[%s,%s)', $low, $high),
                'lower_bound' => $low,
                'upper_bound' => $high
            );
            $iTemp = $high;
            $iIdx++;
        }
        // 附加一个到正无穷大的区间
        $aUserParam['distr_range'][] = array(
            'data_name' => $aUserParam['pre_name'] . '>=' . $iTemp . $aUserParam['suf_name'],
            'range' => '>=' . $iTemp,
            'lower_bound' => $iTemp,
            'upper_bound' => 0
        );
        unset($aUserParam['range']);

        $aUserParam['distr_range'][-1] = array(
            'data_name' => $aUserParam['pre_name'] . $seperator . $aUserParam['suf_name'],
            'range' => '',
            'lower_bound' => -1,
            'upper_bound' => -1
        );
    }
}

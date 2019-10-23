<?php
class gamecustom_diy_Base
{
    const CATEGORY_BASIC = 1;
    const CATEGORY_DISTR = 2;
    const CATEGORY_ITEM = 3;

    /**
     * 实例化自定义加工项配置
     * @param integer $iType
     * @return gamecustom_diy_Base
     */
    public static function instance($iType = self::CATEGORY_BASIC)
    {
        switch ($iType) {
            case self::CATEGORY_DISTR:
                return new gamecustom_diy_Distr();
                break;
            case self::CATEGORY_ITEM:
                return new gamecustom_diy_Item();
                break;
            default:
                return new self();
                break;
        }
    }

    /**
     * Build data name, return empty string to use the diy name
     *
     * @param  array  $aUserParam
     * @return string
     */
    public function buildDataName($aUserParam)
    {
        return '';
    }

    /**
     * Parse operands into diy data format
     *
     * @param  string $dataName
     * @param  string $dataRule
     * @param  array  $aOperandInfo
     * @return array
     */
    public function parseDiyData($dataName, $dataRule, $aOperandInfo)
    {
        list($commonRange, $operandRange, $dataIdMap) = $this->parseAsDataId($aOperandInfo);
        $diyData = array(array(
            'range' => '',
            'data_expr' =>
                $this->formatDataExpr(str_replace(array_keys($dataIdMap), array_values($dataIdMap), $dataRule)),
            'data_name' => $dataName
        ));
        return $diyData;
    }

    /**
     * Get configuration for diy
     *
     * @param  array $aInfo
     * @return array
     */
    public function getConfiguration($aInfo)
    {
        return array('data_rule_name' => $this->buildDataRuleName($aInfo));
    }

    /**
     * Build data rule name
     *
     * @param  array  $aInfo
     * @return string
     */
    protected function buildDataRuleName($aInfo)
    {
        $model = new gamecustom_Diy();
        $model->attributes = $aInfo;
        $aOperandInfo = $model->checkDataRule();

        $aSearch = $aReplace = array();
        foreach ($aOperandInfo as $operand => $operandInfo) {
            $aSearch[] = $operand;
            $aReplace[] = '[' . $operandInfo['r_name'] . ']';
        }
        return str_replace($aSearch, $aReplace, $model->data_rule);
    }

    /**
     * Parse operands to data_id representation
     *
     * @param  array $aOperandInfo
     * @return array (common ranges, operand ranges, data_id map)
     */
    protected function parseAsDataId($aOperandInfo)
    {
        $dataInfoModel = new common_DataInfo();
        $dataIdMap = $operandRange = array();
        $commonRange = null;
        foreach ($aOperandInfo as $operand => $info) {
            switch ($info['type']) {
                case 'set':
                    $dataIdMap[$operand] = $this->buildSetDataExpr($info);
                    break;

                case 'report':
                    $dataInfo = $dataInfoModel->getRangeByRid($info);
                    TMValidator::ensure($dataInfo,
                        TM::t('tongji', '操作数{operand}没有数据！'), array('{operand}' => $operand));
                    $info['r_type'] = (int)$info['r_type'];
                    if ($info['r_type'] === self::CATEGORY_BASIC) {
                        $dataIdMap[$operand] = 'd_' . $dataInfo[0]['data_id'];
                    } else {
                        $dataIdMap[$operand] = null;
                        foreach ($dataInfo as $data) {
                            $operandRange[$operand][$data['range']] = 'd_' . $data['data_id'];
                        }
                        $commonRange = $commonRange === null ?
                            $operandRange[$operand] : array_intersect_key($commonRange, $operandRange[$operand]);
                    }
                    break;

                default:
                    TMValidator::ensure(false, TM::t('tongji', '操作数{operand}类型未知！'), array('{operand}' => $operand));
                    break;
            }
        }

        return array((array)$commonRange, $operandRange, $dataIdMap);
    }

    /**
     * Build info to expr
     */
    protected function buildSetDataExpr($setDataInfo)
    {
        $setDataInfo['data_id'] = (int)$setDataInfo['data_id'];
        if ($setDataInfo['data_id'] !== 0) {
            $setDataInfo['factor'] = (float)$setDataInfo['factor'];
            $dataId = 'd_' . $setDataInfo['data_id'];
            if ($setDataInfo['factor'] !== 1.0) {
                $dataId = "($dataId*{$setDataInfo['factor']})";
            }
            return $dataId;
        }

        // 1337;1343|({0}*0.01)*{1}
        $info = explode('|', $setDataInfo['data_expr']);
        TMValidator::ensure(isset($info[1]),
            TM::t('tongji', '表达式{expr}不正确！', array('{expr}' => $setDataInfo['data_expr'])));
        $dataIds = array_filter(explode(';', $info[0]));
        $pairs = array();
        foreach ($dataIds as $idx => $dataId) {
            $pairs['{' . $idx . '}'] = 'd_' . $dataId;
        }
        return '(' . str_replace(array_keys($pairs), array_values($pairs), $info[1]) . ')';
    }

    /**
     * Format d_12*d_34/100+d_56 to 12;34;56|{0}*{1}/100+{2}
     */
    protected function formatDataExpr($dataExpr)
    {
        TMValidator::ensure(preg_match_all('/d_(\d+)/', $dataExpr, $matches, PREG_SET_ORDER) !== false,
            TM::t('tongji', '{expr}不包含数据！', array('{expr}' => $dataExpr)));
        $dataIds = array();
        $idx = 0;
        foreach ($matches as $match) {
            if (!isset($dataIds[$match[1]])) {
                $dataIds[$match[1]] = $idx;
                ++$idx;
            }
            $dataExpr = str_replace($match[0], '{' . $dataIds[$match[1]] . '}', $dataExpr);
        }

        return implode(';', array_keys($dataIds)) . '|' . $dataExpr;
    }
}

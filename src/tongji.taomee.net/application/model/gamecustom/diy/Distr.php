<?php
class gamecustom_diy_Distr extends gamecustom_diy_Base
{
    /**
     * Build data name, return empty string to use the diy name
     *
     * @param  array  $aUserParam
     * @return string
     */
    public function buildDataName($aUserParam)
    {
        $aUserParam['pre_name'] = trim($aUserParam['pre_name']);
        $aUserParam['suf_name'] = trim($aUserParam['suf_name']);
        TMValidator::ensure(
            $aUserParam['low_flag'] || $aUserParam['high_flag'], TM::t('tongji', '数据名称中至少包含上限或者下限！'));

        return $aUserParam['pre_name'] .
            (($aUserParam['low_flag'] XOR $aUserParam['high_flag']) ?
                ($aUserParam['low_flag'] ? '[$low]' : '[$high]') : '[$low]~[$high]') .
            $aUserParam['suf_name'];
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
        TMValidator::ensure(!empty($commonRange), TM::t('tongji', '操作数不存在公共数据！'));
        $diyData = array();
        foreach ($commonRange as $range => $useless) {
            foreach ($dataIdMap as $operand => &$dataId) {
                if (isset($operandRange[$operand], $operandRange[$operand][$range])) {
                    $dataId = $operandRange[$operand][$range];
                }
            }
            $pairs = array();
            if (preg_match('/^\[(\d+),(\d+)\)$/', $range, $matches)) {
                $pairs = array('[$low]' => $matches[1], '[$high]' => $matches[2]);
            } elseif (preg_match('/^<(\d+)$/', $range, $matches)) {
                $pairs = array('[$low]' => 0, '[$high]' => $matches[1]);
            } else {
                $pairs = array_fill_keys(array('[$low]~[$high]', '[$low]', '[$high]'), $range);
            }
            $diyData[] = array(
                'range' => $range,
                'data_expr' =>
                    $this->formatDataExpr(str_replace(array_keys($dataIdMap), array_values($dataIdMap), $dataRule)),
                'data_name' => str_replace(array_keys($pairs), array_values($pairs), $dataName)
            );
        }
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
        $aExtra = array();
        $aExtra['low_flag'] = strpos($aInfo['data_name'], '[$low]') !== false;
        $aExtra['high_flag'] = strpos($aInfo['data_name'], '[$high]') !== false;
        $seperator = ($aExtra['low_flag'] XOR $aExtra['high_flag']) ?
            ($aExtra['low_flag'] ? '[$low]' : '[$high]') : '[$low]~[$high]';
        list($aExtra['pre_name'], $aExtra['suf_name']) = explode($seperator, $aInfo['data_name']);
        $aExtra['data_rule_name'] = $this->buildDataRuleName($aInfo);

        return $aExtra;
    }
}

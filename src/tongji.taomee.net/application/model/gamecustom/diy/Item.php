<?php
class gamecustom_diy_Item extends gamecustom_diy_Base
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

        return $aUserParam['pre_name'] . '[$item]' . $aUserParam['suf_name'];
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
            $diyData[] = array(
                'range' => $range,
                'data_expr' =>
                    $this->formatDataExpr(str_replace(array_keys($dataIdMap), array_values($dataIdMap), $dataRule)),
                'data_name' => str_replace('[$item]', $range, $dataName)
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
        list($aExtra['pre_name'], $aExtra['suf_name']) = explode('[$item]', $aInfo['data_name']);
        $aExtra['data_rule_name'] = $this->buildDataRuleName($aInfo);

        return $aExtra;
    }
}

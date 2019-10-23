<?php
class data_configuration_SstidDistrFactory extends data_configuration_DistrFactory
{
    public $game_id;
    public $stid;
    public $op_type;
    public $op_fields;
    public $range;

    /* 分布KEY名称 */
    protected $distrKey = 'name';

    /**
     * 检测生成器配置
     * @return boolean
     */
    protected function isValid()
    {
        if (!$this->issetProperties(array('game_id', 'stid', 'op_type', 'op_fields', 'range'))) {
            return false;
        }
        // 设置分布名称
        if (!isset($this->distr_name)) {
            $this->distr_name = $this->stid;
        }
        return true;
    }

    /**
     * 获取data_id列表
     * @return null|array
     */
    protected function getDataList()
    {
        // 已经过滤掉stid=sstid的情况
        $aReport = $this->getReportModel()->getSstid(array(
            'game_id' => $this->game_id,
            'stid' => $this->stid,
            'op_type' => $this->op_type,
            'op_fields' => $this->op_fields
        ));
        if (!$aReport) return;
        // 记录顺序，report可能order
        $aFormat = array();
        foreach ($aReport as $report) {
            $report['range'] = $this->range;
            $aFormat["{$report['r_id']}:{$report['type']}:{$report['range']}"] = $report;
        }
        $aDataList = $this->getDataInfoModel()->getDataList($aFormat);
        if (!$aDataList) return;
        // 排除掉没有data_id的
        foreach ($aFormat as $key => &$report) {
            if (!isset($aDataList[$key])) {
                unset($aFormat[$key]);
                continue;
            }
            $report = array_merge($report, $aDataList[$key][0]);
        }
        return array_values($aFormat);
    }

    /**
     * 过滤data信息
     * @param  array $dataInfo
     * @return array
     */
    public function filterDataInfo($dataInfo)
    {
        unset($dataInfo['sstid']);
        return $dataInfo;
    }
}

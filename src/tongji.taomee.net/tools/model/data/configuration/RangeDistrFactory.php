<?php
class data_configuration_RangeDistrFactory extends data_configuration_DistrFactory
{
    public $game_id;
    public $gpzs_id;
    public $type;
    /* REPORT */
    public $stid;
    public $sstid;
    public $op_type;
    public $op_fields;
    /* RESULT */
    public $task_id;

    /* 分布KEY名称 */
    protected $distrKey = 'range_name';

    /**
     * 检测生成器配置
     * @return boolean
     */
    protected function isValid()
    {
        if (!$this->issetProperties(array('game_id', 'type'))) {
            return false;
        }
        switch ($this->type) {
            case common_Stat::TYPE_REPORT:
                if (!$this->issetProperties(array('stid', 'sstid', 'op_type', 'op_fields'))) {
                    return false;
                }
                break;
            case common_Stat::TYPE_RESULT:
                if (!$this->issetProperties(array('task_id'))) {
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * 获取data_id列表
     * @return null|array
     */
    protected function getDataList()
    {
        $aReport = common_Stat::getStatByUk($this->game_id, array(array(
            'type' => $this->type,
            /* REPORT */
            'stid' => $this->stid,
            'sstid' => $this->sstid,
            'op_type' => $this->op_type,
            'op_fields' => $this->op_fields,
            /* RESULT */
            'task_id' => $this->task_id
        )));
        if (!$aReport) return;
        $aReport = array_pop($aReport);
        // 设置分布名称
        if (!isset($this->distr_name)) {
            $this->distr_name = $aReport['r_name'];
        }
        return $this->getDataInfoModel()->getRangeByRid($aReport);
    }

    /**
     * 过滤data信息
     * @param  array $dataInfo
     * @return array
     */
    public function filterDataInfo($dataInfo)
    {
        unset($dataInfo['range']);
        return $dataInfo;
    }
}

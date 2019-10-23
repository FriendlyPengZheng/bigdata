<?php
class data_configuration_GpzsDistrFactory extends data_configuration_DistrFactory
{
    public $game_id;
    public $type;
    public $range;
    /* REPORT */
    public $stid;
    public $sstid;
    public $op_type;
    public $op_fields;
    /* RESULT */
    public $task_id;

    /* 分布KEY名称 */
    protected $distrKey = 'gpzs_name';

    /* 区分平台和区服 */
    private $_distrBy;

    /**
     * 构造方法
     * @param integer $distrBy
     */
    public function __construct($distrBy)
    {
        $this->_distrBy = $distrBy;
    }

    /**
     * 生成分布，适用于gpzs_id分布
     * @param array $aUserParam
     * @return null|array
     */
    public function createDistr($aUserParam)
    {
        if (!$this->isValid()) return;
        $aDataList = $this->getDataList();
        if (!$aDataList) return;
        $aGpzsList = $this->getGpzsList($aUserParam);
        if (!$aGpzsList) return;
        $dataInfo = array_pop($aDataList);
        $configurationSet = new data_configuration_ConfigurationSet();
        if ($this->extend) $extend = array();
        foreach ($aGpzsList as $gpzsInfo) {
            $configuration = new data_configuration_Configuration(
                $dataInfo['data_id'], $dataInfo['sthash'], $gpzsInfo['gpzs_id']);
            $configuration->setDataName($gpzsInfo[$this->distrKey])->setFactor($this->factor)->setUnit($this->unit);
            $configurationSet->add($configuration);
            if ($this->extend) $extend[] = $gpzsInfo['add_time'];
        }
        $distr = $this->newDistrInstance($configurationSet, $this->distr_name ? $this->distr_name : $dataInfo['data_name']);
        if ($this->extend) $distr->setExtend(array(array('name' => TM::t('tongji', '接入时间'), 'data' => $extend)));
        return $distr;
    }

    /**
     * 检测生成器配置
     * @return boolean
     */
    protected function isValid()
    {
        if (!$this->issetProperties(array('game_id', 'type', 'range'))) {
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
        $aReport = common_Stat::getStatByUk(
            $this->game_id, array(array(
                'type' => $this->type,
                /* REPORT */
                'stid' => $this->stid,
                'sstid' => $this->sstid,
                'op_type' => $this->op_type,
                'op_fields' => $this->op_fields,
                /* RESULT */
                'task_id' => $this->task_id
            ))
        );
        if (!$aReport) return;
        foreach ($aReport as &$report) {
            $report['range'] = $this->range;
        }
        $aDataList = $this->getDataInfoModel()->getDataList($aReport);
        if (!$aDataList) return;
        return array_pop($aDataList);
    }

    /**
     * 获取gpzs_id列表
     * @param array $aUserParam
     * @return null|array
     */
    protected function getGpzsList($aUserParam)
    {
        $model = new common_GpzsInfo();
        if (data_configuration_DistrFactoryManager::isPlatformDistr($this->_distrBy)) {
            $model->attributes = $aUserParam;
            $model->server_id = -1;
            $model->zone_id = -1;
            if ($model->platform_id === '-1') $model->platform_id = null;
            $gpzsList = $model->getList();
            foreach ($gpzsList as $key => $gpzs) {
                // 排除全平台
                if ($gpzs['platform_id'] === '-1') {
                    unset($gpzsList[$key]);
                    break;
                }
            }
            return $gpzsList;
        }
        if (data_configuration_DistrFactoryManager::isZoneServerDistr($this->_distrBy)) {
            $model->attributes = $aUserParam;
            if ($model->server_id === '-1' && $model->zone_id === '-1') $model->server_id = $model->zone_id = null;
            $gpzsList = $model->getList();
            foreach ($gpzsList as $key => $gpzs) {
                // 排除全区全服
                if ($gpzs['server_id'] === '-1' && $gpzs['zone_id'] === '-1') {
                    unset($gpzsList[$key]);
                    break;
                }
            }
            return $gpzsList;
        }
    }
}

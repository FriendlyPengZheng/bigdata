<?php
abstract class data_configuration_DistrFactory extends TMComponent
{
    /* Common parameters for distribution */
    public $distr_name;
    public $dimen_name;
    public $distr_type = data_configuration_Distr::TYPE_SUM;
    public $factor = 1;
    public $unit = '';
    public $sort_type = data_configuration_Distr::SORT_DFL;
    public $precision = 2;
    public $percentage = data_configuration_Distr::PERCENTAGE_NORMAL;
    public $export = 1;
    public $extend = 0;

    /* Key for distribution */
    protected $distrKey = 'data_name';

    /* Models */
    private $_reportModel;
    private $_dataInfoModel;

    /**
     * 生成分布，适用于data_id的分布，gpzs_id分布需重写
     * @param array $aUserParam
     * @return null|array
     */
    public function createDistr($aUserParam)
    {
        if (!$this->isValid()) return;
        $aDataList = $this->getDataList();
        if (!$aDataList) return;
        $configurationSet = new data_configuration_ConfigurationSet();
        foreach ($aDataList as $dataInfo) {
            $configuration = new data_configuration_Configuration(
                $dataInfo['data_id'], $dataInfo['sthash'], isset($this->gpzs_id) ? $this->gpzs_id : $aUserParam['gpzs_id']);
            $configuration->setDataName($dataInfo[$this->distrKey])->setFactor($this->factor)->setUnit($this->unit);
            $configurationSet->add($configuration);
        }
        return $this->newDistrInstance($configurationSet);
    }

    /**
     * Return ReportModel
     */
    public function getReportModel()
    {
        if (!isset($this->_reportModel)) {
            $this->_reportModel = new common_Report();
        }
        return $this->_reportModel;
    }

    /**
     * Return DataInfoModel
     */
    public function getDataInfoModel()
    {
        if (!isset($this->_dataInfoModel)) {
            $this->_dataInfoModel = new common_DataInfo();
        }
        return $this->_dataInfoModel;
    }

    /**
     * 设置属性
     * @param array $properties
     */
    public function setProperties($properties)
    {
        foreach ($properties as $property => $value) {
            if (property_exists($this, $property)) {
                $this->$property = $value;
            }
        }
        return $this;
    }

    /**
     * 检测属性是否设置
     * @param array $properties
     * @return boolean
     */
    public function issetProperties($properties)
    {
        foreach ($properties as $property) {
            if (!isset($this->$property)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 过滤data信息
     * @param  array $dataInfo
     * @return array
     */
    public function filterDataInfo($dataInfo)
    {
        return $dataInfo;
    }

    /**
     * 生成data_configuration_Distr实例
     * @param data_configuration_ConfigurationSet $configurationSet
     * @param string $distrName
     * @return data_configuration_Distr
     */
    protected function newDistrInstance($configurationSet, $distrName = null)
    {
        return data_configuration_Distr::newInstance(
                $configurationSet, $distrName ? $distrName : $this->distr_name)
            ->setDistrType($this->distr_type)
            ->setDimenName($this->dimen_name)
            ->setSortType($this->sort_type)
            ->setPrecision($this->precision)
            ->setPercentageType($this->percentage)
            ->setExport($this->export)
            ->setUnit($this->unit);
    }

    /**
     * 检测生成器配置
     * @return boolean
     */
    abstract protected function isValid();
}

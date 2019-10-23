<?php
class data_calculator_Distributor extends TMComponent
{
    /**
     * @var data_configuration_Distr 分布配置
     */
    private $_distrConfiguration;

    /**
     * @var array 分布时间数据，已填充
     */
    private $_sourceData;

    /**
     * @var array 分布KEY
     */
    private $_key;

    /**
     * @var array 分布DATA
     */
    private $_data;

    /**
     * @var array 用于排序的尺子
     */
    private $_ruler;

    /**
     * 设置分布配置
     * @param data_configuration_Distr $distrConfiguration
     * @return data_calculator_Distributor
     */
    public function setDistrConfiguration($distrConfiguration)
    {
        $this->_distrConfiguration = $distrConfiguration;
        return $this;
    }

    /**
     * 设置分布时间数据
     * @param array $sourceData
     * @return data_calculator_Distributor
     */
    public function setSourceData($sourceData)
    {
        $this->_sourceData = $sourceData;
        return $this;
    }

    /**
     * 获取源数据
     * @return array
     */
    public function getSourceData()
    {
        return $this->_sourceData;
    }

    /**
     * Set ruler for sorting.
     * @param array $ruler
     * @return data_calculator_Distributor
     */
    public function setRuler($ruler = null)
    {
        $this->_ruler = $ruler;
        return $this;
    }

    /**
     * 计算分布
     * @return null|array
     */
    public function distribute()
    {
        if (!$this->_distrConfiguration || !$this->_sourceData) return;

        $this->_key = $this->_data = array();
        $this->_data['name'] = $this->_distrConfiguration->getDistrName();
        $this->_data['data'] = array();
        $percentageType = $this->_distrConfiguration->getPercentageType();
        if ($percentageType !== data_configuration_Distr::PERCENTAGE_DISABLED) {
            $this->_data['percentage'] = array();
            $fSum = 0;
        }
        foreach ($this->_sourceData['data'] as $data) {
            $this->_key[] = $data['name'];
            $fValue = $this->_distrValue($data['data']);
            if (isset($fSum)) $fSum += $fValue;
            $this->_data['data'][] = isset($fValue) ? ($fValue . $this->_distrConfiguration->getUnit()) : $fValue;
        }
        if (isset($this->_data['percentage'])) {
            if ($fSum) {
                foreach ($this->_data['data'] as $fValue) {
                    $this->_data['percentage'][] = round($fValue/$fSum*100, 2) . '%';
                }
            } else {
                $this->_data['percentage'] = array_fill(0, count($this->_data['data']), '0%');
            }
            if ($percentageType === data_configuration_Distr::PERCENTAGE_DATALIZED) {
                $this->_data['data'] = $this->_data['percentage'];
                unset($this->_data['percentage']);
            }
        }
        $this->_sort();
        return array('key' => $this->_key, 'data' => array($this->_data));
    }

    /**
     * 计算分布值
     * @param array $values 一组值
     * @return null|float
     */
    private function _distrValue($values)
    {
        $count = count(array_filter($values, 'strlen'));
        if (!$count) return;

        switch ($this->_distrConfiguration->getDistrType()) {
            case data_configuration_Distr::TYPE_AVG:
                $count = count($values);
                if (!$count) return;
                return round(array_sum($values)/$count, $this->_distrConfiguration->getPrecision());
                break;

            case data_configuration_Distr::TYPE_ABSAVG:
                return round(array_sum($values)/$count, $this->_distrConfiguration->getPrecision());
                break;

            case data_configuration_Distr::TYPE_MAX:
                return max($values);
                break;

            case data_configuration_Distr::TYPE_SUM:
            default:
                return array_sum($values);
                break;
        }
    }

    /**
     * 对求分布完成的数据排序，同时对时间序列排序
     */
    private function _sort()
    {
        if (!isset($this->_ruler)) {
            $this->_ruler = $this->_data['data'];
        }
        $ruler = $this->_ruler;

        switch ($this->_distrConfiguration->getSortType()) {
            case data_configuration_Distr::SORT_ASC:
                if (isset($this->_data['percentage'])) {
                    array_multisort($ruler, SORT_ASC, SORT_NUMERIC,
                        $this->_data['data'],
                        $this->_data['percentage'],
                        $this->_key,
                        $this->_sourceData['data']
                    );
                } else {
                    array_multisort($ruler, SORT_ASC, SORT_NUMERIC,
                        $this->_data['data'],
                        $this->_key,
                        $this->_sourceData['data']
                    );
                }
                break;

            case data_configuration_Distr::SORT_DESC:
                if (isset($this->_data['percentage'])) {
                    array_multisort($ruler, SORT_DESC, SORT_NUMERIC,
                        $this->_data['data'],
                        $this->_data['percentage'],
                        $this->_key,
                        $this->_sourceData['data']
                    );
                } else {
                    array_multisort($ruler, SORT_DESC, SORT_NUMERIC,
                        $this->_data['data'],
                        $this->_key,
                        $this->_sourceData['data']
                    );
                }
                break;

            case data_configuration_Distr::SORT_DFL:
            default:
                break;
        }
    }
}

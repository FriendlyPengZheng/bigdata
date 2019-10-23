<?php
class data_populator_Populator extends TMComponent
{
    /**
     * @var data_configuration_ConfigurationSet
     */
    private $_configurationSet = null;

    /**
     * @var data_time_Period
     */
    private $_period = null;

    /**
     * @var array
     */
    private $_sourceData = array();

    /**
     * Populate data.
     * @param array $aUserParam
     * @return null|array
     */
    public function populate($aUserParam)
    {
        $aPoints = $this->_period->getPoints(isset($aUserParam['fill_null']) && $aUserParam['fill_null']);
        $aKeys = array_keys($aPoints['value']);
        if (!$aKeys) return;
        $iStart  = $this->_period->getFrom();
        $iEnd = $this->_period->getTo();
        $aKeys[] = -1; // The end.
        $aData = array();
        $iThreshold = time() - 180;
        foreach ($this->_configurationSet as $key => $configuration) {
            $aEach = array('name' => $configuration->getDataName(), 'data' => $aPoints['value']);
            if (isset($this->_sourceData[$key])) {
                $sUnit = $configuration->getUnit();
                $fFactor = $configuration->getFactor();
                $iPrecision = $configuration->getPrecision();
                $i = 0;
                foreach ($this->_sourceData[$key] as $value) {
                    if ($value['_key'] < $iStart) continue;
                    if ($value['_key'] > $iEnd) break;
                    while ($value['_key'] >= $aKeys[$i+1] && $aKeys[$i+1] !== -1) $i++;
                    if ($value['_key'] > $iThreshold) continue;
                    $aEach['data'][$aKeys[$i]] = round($value['_value'] * $fFactor, $iPrecision) . $sUnit;
                }
            }
            $aEach['data'] = array_values($aEach['data']);
            $aData[] = $aEach;
        }
        return array(
            'key' => $aPoints['key'],
            'data' => $aData,
            'pointStart' => $this->_period->getFrom(),
            'pointInterval' => $this->_period->getInterval()
        );
    }

    /**
     * Get configurations for getting data.
     * @return data_configuration_ConfigurationSet
     */
    public function getConfigurationSet()
    {
        return $this->_configurationSet;
    }

    /**
     * Set configurations for getting data.
     * @param data_configuration_ConfigurationSet $configurationSet
     * @return data_populator_Populator
     */
    public function setConfigurationSet(data_configuration_ConfigurationSet $configurationSet)
    {
        $this->_configurationSet = $configurationSet;
        return $this;
    }

    /**
     * Get the data period.
     * @return data_time_Period
     */
    public function getPeriod()
    {
        return $this->_period;
    }

    /**
     * Set the data period.
     * @param data_time_Period $period
     * @return data_populator_Populator
     */
    public function setPeriod(data_time_Period $period)
    {
        $this->_period = $period;
        return $this;
    }

    /**
     * Get source data to be populated.
     * @return array
     */
    public function getSourceData()
    {
        return $this->_sourceData;
    }

    /**
     * Set source data to be populated.
     * @param array $sourceData
     * @return data_populator_Populator
     */
    public function setSourceData($sourceData)
    {
        $this->_sourceData = $sourceData;
        return $this;
    }
}

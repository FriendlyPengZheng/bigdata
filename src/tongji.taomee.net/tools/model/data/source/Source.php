<?php
abstract class data_source_Source extends TMComponent
{
    /**
     * @var data_configuration_ConfigurationSet
     */
    private $_configurationSet = null;

    /**
     * @var data_time_PeriodSet
     */
    private $_periodSet = null;

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
     * @return data_source_Source
     */
    public function setConfigurationSet(data_configuration_ConfigurationSet $configurationSet)
    {
        $this->_configurationSet = $configurationSet;
        return $this;
    }

    /**
     * Get the data periodSet.
     * @return data_time_PeriodSet
     */
    public function getPeriodSet()
    {
        return $this->_periodSet;
    }

    /**
     * Set the data periodSet.
     * @param data_time_PeriodSet $period
     * @return data_source_Source
     */
    public function setPeriodSet(data_time_PeriodSet $periodSet)
    {
        $this->_periodSet = $periodSet;
        return $this;
    }

    /**
     * Get data.
     * @param array $aUserParam
     * @return array
     */
    abstract public function get($aUserParam);
}

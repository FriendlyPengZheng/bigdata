<?php
class data_configuration_ConfigurationBuilder extends TMComponent
{
    /**
     * @var data_configuration_StatInfo
     */
    private $_statModel;

    /**
     * @var data_configuration_DataInfo
     */
    private $_dataModel;

    /**
     * Build the configurations into one data_configuration_ConfigurationSet.
     * @param boolean $isStat
     * @param array   $confs
     * @param integer $defaultGameId
     * @param integer $defaultGpzsId
     * @return data_configuration_ConfigurationSet
     */
    public function build($isStat, $confs, $defaultGpzsId = null, $defaultGameId = null)
    {
        if ($isStat) {
            return $this->buildStatConf($confs, $defaultGpzsId, $defaultGameId);
        }
        return $this->buildDataConf($confs, $defaultGpzsId);
    }

    /**
     * Build the configurations into one data_configuration_ConfigurationSet.
     * @param array   $confs
     * @param integer $defaultGpzsId
     * @param integer $defaultGameId
     * @return data_configuration_ConfigurationSet
     */
    protected function buildStatConf($confs, $defaultGpzsId = null, $defaultGameId = null)
    {
        $this->_statModel = new data_configuration_StatInfo();
        $statInfo = $this->_statModel->getInfo($confs, $defaultGameId);
        $statSortedConf = $this->_statModel->getSortedConf();
        $dataConf = $this->getDataConf($statSortedConf, $statInfo);

        $this->_dataModel = new data_configuration_DataInfo();
        $dataInfo = $this->_dataModel->getInfo($dataConf);
        $dataSortedConf = $this->_dataModel->getSortedConf();

        $set = new data_configuration_ConfigurationSet();
        foreach ($statSortedConf as $stat) {
            $gpzsId = TMArrayHelper::assoc('gpzs_id', $stat, $defaultGpzsId);
            if (!isset($statInfo[$stat['_uuid']])) {
                $set->add($this->setConfigurationInfo(
                    new data_configuration_UndefinedConfiguration(),
                    $stat
                ));
                continue;
            }
            $subset = new data_configuration_ConfigurationSet();
            foreach ($statInfo[$stat['_uuid']] as $useless) {
                $data = array_shift($dataSortedConf);
                if (!isset($dataInfo[$data['_uuid']])) {
                    $subset->add($this->setConfigurationInfo(
                        new data_configuration_UndefinedConfiguration(),
                        array_merge($useless, $stat)
                    ));
                    continue;
                }
                foreach ($dataInfo[$data['_uuid']] as $subdata) {
                    $subset->add($this->setConfigurationInfo(
                        new data_configuration_Configuration($subdata['data_id'], $subdata['sthash'], $gpzsId),
                        array_merge($subdata, $useless, $stat)
                    ));
                }
            }
            if (count($subset) > 1) {
                $set->add($subset);
                continue;
            }
            $set->add($subset->current());
        }

        return $set;
    }

    /**
     * Build the configurations into one data_configuration_ConfigurationSet.
     * @param array   $confs
     * @param integer $defaultGpzsId
     * @return data_configuration_ConfigurationSet
     */
    protected function buildDataConf($confs, $defaultGpzsId = null)
    {
        $this->_dataModel = new data_configuration_DataInfo();
        $dataInfo = $this->_dataModel->getInfo($confs);
        $dataSortedConf = $this->_dataModel->getSortedConf();

        $set = new data_configuration_ConfigurationSet();
        foreach ($dataSortedConf as $data) {
            $gpzsId = TMArrayHelper::assoc('gpzs_id', $data, $defaultGpzsId);
            if (!isset($dataInfo[$data['_uuid']])) {
                $set->add($this->setConfigurationInfo(
                    new data_configuration_UndefinedConfiguration(),
                    $data
                ));
                continue;
            }
            $subset = new data_configuration_ConfigurationSet();
            foreach ($dataInfo[$data['_uuid']] as $subdata) {
                $subset->add($this->setConfigurationInfo(
                    new data_configuration_Configuration($subdata['data_id'], $subdata['sthash'], $gpzsId),
                    array_merge($subdata, $data)
                ));
            }
            if (count($subset) > 1) {
                $set->add($subset);
                continue;
            }
            $set->add($subset->current());
        }

        return $set;
    }

    /**
     * Set configuration info.
     * @param array $info
     * @return data_configuration_UndefinedConfiguration
     */
    protected function setConfigurationInfo($conf, $info)
    {
        return $conf->setDataName(TMArrayHelper::assoc('data_name', $info, $conf->getDataName()))
            ->setFactor(TMArrayHelper::assoc('factor', $info, $conf->getFactor()))
            ->setPrecision(TMArrayHelper::assoc('precision', $info, $conf->getPrecision()))
            ->setUnit(TMArrayHelper::assoc('unit', $info, $conf->getUnit()))
            ->setExtra($info);
    }

    /**
     * Get data configurations from stat info.
     * @param array $statSortedConf
     * @param array $statInfo
     * @return array
     */
    protected function getDataConf($statSortedConf, $statInfo)
    {
        $dataConf = array();
        foreach ($statSortedConf as $stat) {
            if (!isset($statInfo[$stat['_uuid']])) {
                continue;
            }
            foreach ($statInfo[$stat['_uuid']] as $data) {
                if (isset($stat['range'])) {
                    $data['range'] = $stat['range'];
                }
                $dataConf[] = $data;
            }
        }
        return $dataConf;
    }

    /**
     * Get stat model.
     * @return data_configuration_StatInfo
     */
    public function getStatModel()
    {
        return $this->_statModel;
    }

    /**
     * Get data model.
     * @return data_configuration_DataInfo
     */
    public function getDataModel()
    {
        return $this->_dataModel;
    }
}

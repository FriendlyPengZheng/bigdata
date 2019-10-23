<?php
class gameanalysis_Data extends TMModel
{
    public function getDb()
    {
        return TM::app()->rDb;
    }

    public function getPeriod($aUserParam)
    {
        $aUserParam['from'] = (array)$aUserParam['from'];
        $aUserParam['to'] = (array)$aUserParam['to'];
        if (count($aUserParam['from']) !== count($aUserParam['to'])) {
            return;
        }

        foreach ($aUserParam['from'] as $idx => $from) {
            return data_time_PeriodFactory::createPeriod($from, $aUserParam['to'][$idx]);
        }
    }

    public function populate($aSource, $aDataInfo, $aConf, $period)
    {
        $aPoints = $period->getPoints();
        $aData = array();
        foreach ($aDataInfo as $dataId) {
            $aData[$dataId] = array('key' => $aPoints['key'], 'data' => array());
            foreach ($aConf as $conf) {
                $aEach = array('name' => $conf['name'], 'data' => $aPoints['value']);
                if (isset($aSource[$dataId])) {
                    foreach ($aSource[$dataId] as $value) {
                        if (!isset($aEach['data'][$value['_key']])) continue;
                        $aEach['data'][$value['_key']] = $value[$conf['field']];
                    }
                }
                $aEach['data'] = array_values($aEach['data']);
                $aData[$dataId]['data'][] = $aEach;
            }
        }
        return $aData;
    }
}

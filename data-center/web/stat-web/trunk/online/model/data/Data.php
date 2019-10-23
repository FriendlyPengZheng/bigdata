<?php
class data_Data extends TMModel
{
    public function getData($dataInfo, $periodSet)
    {
        if (!$dataInfo) return;

        try {
            return $this->getDataFromProto($dataInfo, $periodSet);
        } catch (Exception $e) {
            TM::app()->getLog()->log(
                $e->getMessage() . "\n" . "Stack trace:\n" . $e->getTraceAsString() . "\n",
                TMLog::TYPE_ERROR
            );
            return $this->getDataFromDatabase($dataInfo, $periodSet);
        }
    }

    public function getStatData($rpInfo, $periodSet)
    {
        if (!$rpInfo) return;

        $rules = array();
        foreach ($rpInfo as $info) {
            $rules[] = 'rp_' . $info['data_id'];
        }
        $aData = array();
        foreach ($periodSet as $period) {
            $source = TM::app()->statHttp->get($rules, $period->getFrom(), $period->getTo());
            $aData[] = $this->populate($source, $period, $rpInfo);
        }
        return $aData;
    }

    public function getDataFromProto($dataInfo, $periodSet)
    {
        TM::import('application.components.StringReader');
        $socket = TM::app()->socket->connect();
        $head = pack('LLL', 0x0001, 1, 0);
        $aData = array();
        foreach ($periodSet as $period) {
            $buffer = $head . pack('LLL', $period->getFrom(), $period->getTo(), count($dataInfo));
            foreach ($dataInfo as $info) {
                $buffer .= pack('LLL', $info['data_id'], $info['sthash'], $info['gpzs_id']);
            }
            $socket->write(pack('L', strlen($buffer) + 4) . $buffer);

            $aHead = unpack('Llength/Lcommand/Lversion/Lcode', $socket->read(16));
            if ($aHead['code']) {
                throw new TMException(TM::t('online', '数据接口错误！返回{info}', array('{info}' => print_r($aHead, true))));
            }
            $reader = new StringReader($socket->read($aHead['length'] - 16));
            $aInfo = unpack('Lbegin/Lend/Lcount', $reader->read(12));
            $source = array();
            while ($aInfo['count']-- > 0) {
                $aEach = unpack('Ldata_id/Lsthash/Lgpzs_id/Scount', $reader->read(14));
                $aEach['value'] = array();
                while ($aEach['count']-- > 0) {
                    $aEach['value'][] = unpack('Ltime/dvalue', $reader->read(12));
                }
                $source[] = $aEach;
            }
            $aData[] = $this->populate($source, $period, $dataInfo);
        }
        return $aData;
    }

    protected function populate($source, $period, $dataInfo)
    {
        $aPoints = $period->getPoints();
        $aKeys = array_keys($aPoints['value']);
        $aKeys[] = -1;
        $data = array();
        $idx = 0;
        $iThreshold = time() - 180;
        foreach ($dataInfo as $dataKey => $info) {
            $each = array('name' => '', 'data' => $aPoints['value']);
            if (isset($source[$idx]) && $source[$idx]) {
                $factor = TMArrayHelper::assoc('factor', $info, 1);
                $precision = TMArrayHelper::assoc('precision', $info, 0);
                $i = 0;
                foreach ($source[$idx]['value'] as $value) {
                    while ($value['time'] >= $aKeys[$i+1] && $aKeys[$i+1] !== -1) $i++;
                    if (!isset($value['value'])) continue;
                    if ($value['time'] > $iThreshold) continue;
                    $each['data'][$aKeys[$i]] = round($factor*$value['value'], $precision);
                }
            }
            $each['data'] = array_values($each['data']);
            $data[$dataKey] = $each;
            $idx++;
        }
        return array(
            'key' => $aPoints['key'],
            'data' => $data,
            'pointStart' => $period->getFrom(),
            'pointInterval' => $period->getInterval()
        );
    }

    public function getDataFromDatabase($dataInfo, $periodSet)
    {
        $configurationSet = new data_configuration_ConfigurationSet();
        foreach ($dataInfo as $info) {
            $configurationSet->add(
                new data_configuration_Configuration($info['data_id'], $info['sthash'], $info['gpzs_id'])
            );
        }
        $source = new data_source_DatabaseSource();
        $sourceData = $source->setConfigurationSet($configurationSet)->setPeriodSet($periodSet)
            ->get(array('calc_type' => 'avg'));

        $populator = new data_populator_Populator();
        $aData = array();
        $dataKeys = array_keys($dataInfo);
        foreach ($periodSet as $period) {
            $data = $populator->setConfigurationSet($configurationSet)
                ->setPeriod($period)->setSourceData($sourceData)->populate(array('fill_null' => 0));
            $data['data'] = array_combine($dataKeys, $data['data']);
            $aData[] = $data;
        }
        return $aData;
    }
}

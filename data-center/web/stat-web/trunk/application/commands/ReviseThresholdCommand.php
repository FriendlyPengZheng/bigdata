<?php
class ReviseThresholdCommand extends TMConsoleCommand
{
    public function actions()
    {
        return array(
            'index' => true,
            'reviseDb' => true
        );
    }

    public function reviseDb()
    {
        TM::import('application.model.common.*');
        $oModel = new common_EmailData();
        $aThreshold = TM::app()->getDb()->createCommand()
                ->from($oModel->tableName())
                ->where('threshold != \'\' ')
                ->queryAll();
        foreach ($aThreshold as $thresholds) {
            $threshold = json_decode($thresholds['threshold']);
            $threshold = $this->_object2Array($threshold);
            $temp['qoq'] = array();
            foreach ($threshold['qoq'] as $date => $setting) {
                if ((int)$setting['max'] > 0 && (int)$setting['max'] <= 100) {
                    $setting['max'] = '100%';
                }
                $temp['qoq'][$date] = array('min' => $setting['min'], 'max' => $setting['max']);
            }
            $oModel->attributes = $thresholds;
            $oModel->threshold = json_encode($temp);
            $oModel->update();
        }
        echo 'OK';
        exit;
    }

    public function _object2Array($param)
    {
        if(is_object($param)) {
            $param = (array)$param;
        }
        if(is_array($param)) {
            foreach($param as $key => $value) {
                $param[$key] = $this->_object2Array($value);
             }
        }
        return $param;
    }
}

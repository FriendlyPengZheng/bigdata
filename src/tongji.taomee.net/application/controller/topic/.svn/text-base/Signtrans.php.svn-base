<?php
class Signtrans extends common_Base
{
    /**
     * Actions
     */
    public function actions()
    {
        return array(
            'multiple' => array(),
            'single'   => array(),
            'realtime' => array(),
            'getTransGame' => array(),
            'getSummary'   => array(
                'game_id' => 0,
                'step' => 's2/s1',
                'average' => 0,
                'rate_type' => topic_SignTrans::RATE_TOTAL
            ),
            'getTransRateSummary' => array(    //abandoned
                'game_id' => 0,
                'rate_type' => topic_SignTrans::RATE_TOTAL
            ),
            'getTransRateByGame' => array(
                'rate_type' => topic_SignTrans::RATE_TOTAL,
                'average' => 0
            )
        );
    }

    /**
     * 接收公共参数
     * @return array
     */
    public function commonParameters()
    {
        $aDate = $this->getDateMark();
        return array_merge(parent::commonParameters(), array(
            'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
            'to' => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d'),
            'period' => data_time_PeriodFactory::TYPE_HOUR,
            'game_id' => null,
            'export' => 0,
            'file_name' => null
        ));
    }

    public function multiple($aUserParameters)
    {
        $this->display('topic/multiple.html');
    }

    public function single($aUserParameters)
    {
        $this->display('topic/single.html');
    }

    public function realtime($aUserParameters)
    {
        $this->display('topic/realtime.html');
    }

    public function getTransGame($aUserParameters)
    {
        $model = new topic_SignTrans();
        $this->ajax(0, $model->getGameList());
    }

    /**
     * 多游戏单步骤转化率
     */
    public function getSummary($aUserParameters)
    {
        $model = new topic_SignTrans();
        $model->attributes = $aUserParameters;
        if ($model->period == data_time_PeriodFactory::TYPE_DAY) $this->markDate($aUserParameters);
        if ($aUserParameters['export']) {
            $this->initExporter($aUserParameters);
            $this->oExporter->add($this->sFilename);
            if ($model->period == data_time_PeriodFactory::TYPE_HOUR) {
                $aUserParameters['dimen'] = TM::t('tongji', '步骤');
                if ($aData = $model->getTransRateByGame($aUserParameters)) {
                    foreach ($aData as $data) {
                        $this->oExporter->putWithTitle(substr($data['key'][0], 0, 10));
                        array_shift($data['data']);
                        array_pop($data['data']);
                        if ($model->game_id == 1) {
                            end($data['data']);
                            $data['data'][key($data['data'])]['name'] = '创建角色';
                        }
                        $this->exportTransRate(array($data), $aUserParameters);
                    }
                }
            } else {
                $aUserParameters['dimen'] = TM::t('tongji', '游戏');
                foreach ($model->getTransStep() as $key => $step) {
                    if ($key === 's1') continue;
                    $this->oExporter->putWithTitle($step);
                    $model->step = "$key/s1";
                    $this->exportTransRate($model->getSummary($aUserParameters), $aUserParameters);
                }
            }
            $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
        }
        $aData = $model->getSummary($aUserParameters);
        if ($aUserParameters['average'] && $aData) $aData = $this->getAvgTransRate($aData, $aUserParameters);
        if (count($aData) > 1) $aData = $this->formatNameByPeriodType($aData, $model->period, $aUserParameters);
        $this->ajax(0, $aData);
    }

    /**
     * 多游戏各步骤平均转化率
     */
    public function getTransRateSummary($aUserParameters)
    {
        if ($aUserParameters['export']) $this->getSummary($aUserParameters);
        $model = new topic_SignTrans();
        $model->attributes = $aUserParameters;
        if ($model->period == data_time_PeriodFactory::TYPE_DAY) $this->markDate($aUserParameters);
        $aAvgData = array('key' => null, 'data' => array());
        foreach ($model->getGameList() as $key => $game) {
            if ($key == 1) continue;
            $model->game_id = $key;
            $aData = $model->getTransRateByGame($aUserParameters);
            unset($aData[0]['data'][0]);
            $aUserParameters['name'] = TM::t('tongji', $game);
            $aData = $this->getAvgTransRate($aData, $aUserParameters);
            $aAvgData['key'] = $aData[0]['key'];
            $aAvgData['data'][] = $aData[0]['data'][0];
        }
        $this->ajax(0, array($aAvgData));
    }

    /**
     * 单游戏平均转化率及各步骤转化率
     */
    public function getTransRateByGame($aUserParameters)
    {
        if (is_array($aUserParameters['from'])) ksort($aUserParameters['from']);
        if (is_array($aUserParameters['to'])) ksort($aUserParameters['to']);
        $this->markDate($aUserParameters);
        $model = new topic_SignTrans();
        $model->attributes = $aUserParameters;
        $aData = $model->getTransRateByGame($aUserParameters);
        if ($aUserParameters['export']) {
            $aUserParameters['dimen'] = TM::t('tongji', '步骤');
            $this->initExporter($aUserParameters);
            $this->oExporter->add($this->sFilename);
            $this->exportTransRate($aData, $aUserParameters);
            $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
        }
        if ($aUserParameters['average'] && $aData) $aData = $this->getAvgTransRate($aData, $aUserParameters);
        $this->ajax(0, $aData);
    }

    /**
     * 平均转化率
     * @param array $aData
     * @param array $aUserParameters
     */
    protected function getAvgTransRate($aData, $aUserParameters)
    {
        $multi = count($aData) > 1;
        $aUserParameters['from'] = (array)$aUserParameters['from'];
        $aUserParameters['to'] = (array)$aUserParameters['to'];
        $name = TM::t('tongji', isset($aUserParameters['name']) ? $aUserParameters['name'] : '平均转化率');
        foreach ($aData as $key => &$data) {
            $temp = array('key' => array(), 'data' => array(array(
                'name' => $multi ? "[{$aUserParameters['from'][$key]}~{$aUserParameters['to'][$key]}]" . $name : $name,
                'data' => array()
            )));
            foreach ($data['data'] as $value) {
                $temp['key'][] = $value['name'];
                $temp['data'][0]['data'][] = $value['avg'];
            }
            $data = $temp;
        }
        return $aData;
    }

    /**
     * 导出单游戏转化率
     * @param $aData
     */
    protected function exportTransRate($aData, $aUserParameters)
    {
        if (!$aData) return;
        foreach ($aData as $data) {
            $title = array_merge(array($aUserParameters['dimen'], TM::t('tongji', '平均转化率')),
                array_reverse($data['key']));
            $this->oExporter->put($title, CsvExporter::ENCODE_PREV, 2);
            foreach ($data['data'] as $value) {
                $line = array_merge(array($value['name'], $value['avg']), array_reverse($value['data']));
                $this->oExporter->put($line, CsvExporter::ENCODE_PREV);
            }
            $this->oExporter->put(); // 空行
        }
    }

    /**
     * 根据区间类型格式化名称
     * @param array $aData
     * @param integer $periodType
     * @param array $aUserParameters
     * @return array
     */
    protected function formatNameByPeriodType($aData, $periodType, $aUserParameters)
    {
        $aUserParameters['from'] = (array)$aUserParameters['from'];
        $aUserParameters['to'] = (array)$aUserParameters['to'];
        foreach ($aData as $i => $data) {
            $name = $periodType == data_time_PeriodFactory::TYPE_DAY ?
                "[{$aUserParameters['from'][$i]}~{$aUserParameters['to'][$i]}]" : "[{$aUserParameters['from'][$i]}]";
            foreach ($data['data'] as $key => $value) {
                $aData[$i]['data'][$key]['name'] = $name . $aData[$i]['data'][$key]['name'];
            }
        }
        return $aData;
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters) 
    { 
        // 检查是否有超级管理员权限        
        if (TM::app()->getUser()->isAdmin()) {
            $this->assign('admin_auth', true);
		}
    }
}

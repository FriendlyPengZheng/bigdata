<?php
abstract class gameanalysis_Overview extends gameanalysis_Analysis
{
    public function actions()
    {
        return array_merge(parent::actions(), array(
            'data'  => array('gpzs_id' => null),
            'isTaskComplete' => array()
        ));
    }

    public function commonParameters()
    {
        return array_merge(parent::commonParameters(), array(
            'contrast' => 0,
            'contrast_from' => date('Y-m-d', strtotime('-60 day'))
        ));
    }

    protected function assignCommon($aUserParameters)
    {
        $aUserParameters['platform_id'] = -1;
        $model = new common_GpzsInfo();
        $model->attributes = $aUserParameters;
        $aPlatform = $model->getPlatform();
        if ($aPlatform) {
            $this->assign('gpzs_id', $aPlatform[0]['gpzs_id']);
            $aUserParameters['gpzs_id'] = $aPlatform[0]['gpzs_id'];
            $this->assign('datalist', $this->getSummary($aUserParameters));
        }
        // ignore
        $this->assignIgnore($aUserParameters);
        // game type
        $this->assign('param', array('game_type' => $this->sGameType), true);
    }

    /**
     * 单个游戏概览数据
     * @param array $aUserParameters
     */
    public function data($aUserParameters)
    {
        $this->ajax(0, $this->getSummary($aUserParameters));
    }

    /**
     * 概览求和数据
     * @param array $aUserParameters
     * @return array
     */
    protected function getSummary($aUserParameters)
    {
        if ($aUserParameters['contrast']) {
            $dates = data_time_Time::align(array('from' => $aUserParameters['from'], 'to' => $aUserParameters['to']));
            if ($dates === false) return;
            $aUserParameters = array_merge($aUserParameters, $dates);
            $aUserParameters['yoy'] = $aUserParameters['qoq'] = 0;
        }
        $aUserParameters = array_merge($aUserParameters, $this->getConfiguration());
        $distrData = data_Data::model()->getDistribution($aUserParameters);
        if (!$distrData) return;
        $data = array();
        foreach ($distrData[0]['key'] as $idx => $name) {
            $data[$idx]['name'] = $name;
            $data[$idx]['data'][] = $distrData[0]['data'][0]['data'][$idx];
            if (isset($distrData[0]['data'][1])) {
                $data[$idx]['data'][] = $distrData[0]['data'][1]['data'][$idx];
                $data[$idx]['data'][] = (float)$distrData[0]['data'][1]['contrast_rate'][$idx];
            }
        }
        return $data;
    }

    /**
     * 默认显示页面
     * @param array $aUserParameters
     */
    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/overview.html');
    }

    /**
     * 获取数据配置
     * @return array
     */
    protected function getConfiguration()
    {
        return array(
            'period' => 1,
            'yoy' => 0,
            'qoq' => 0,
            'export' => 0,
            'data_info' => array(array(
                'distr_by' => 5,
                'distr_type' => 1,
                'percentage' => 3,
                'data_info' => array(array(
                        'type'      => 2,
                        'task_id'   => 14,
                        'range'     => '',
                        'data_name' => TM::t('tongji', '新增用户数')
                    ), array(
                        'type'      => 1,
                        'stid'      => '_newpl_',
                        'sstid'     => '_newpl_',
                        'op_fields' => '',
                        'op_type'   => 'ucount',
                        'range'     => '',
                        'data_name' => TM::t('tongji', '新增角色数')
                    ), array(
                        'type'      => 1,
                        'stid'      => '_acpay_',
                        'sstid'     => '_acpay_',
                        'op_fields' => '_amt_',
                        'op_type'   => 'sum',
                        'range'     => '',
                        'factor'    => 0.01,
                        'data_name' => TM::t('tongji', '收入（元）')
                    )
                )
            ))
        );
    }

    /**
     * 数据加工是否完成
     */
    public function isTaskComplete($aUserParameters)
    {
        // 目前所有游戏一起计算
        $aUserParameters['game_id'] = -1;
        $this->ajax(0, (new tool_TaskCompleteLog())->isDataProcessingComplete($aUserParameters));
    }
}

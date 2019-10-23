<?php
class Cdntraffic extends common_Base
{
    public function actions()
    {
        return array(
            'rttraffic'  => array(),
            'daytraffic' => array(),
            'multigame'  => array(),
            'getCdnGame' => array(),
            'getDayTrafficPerMan' => array(
                'gpzs_id'   => null,
                'fill_null' => 1,    /* 填充null or 0 */
                'qoq'       => null, /* 环比 */
                'yoy'       => null, /* 同比 */
                /* 统计项列表 */
                'data_info' => array(),
                'by_item'   => 1,
                'average'   => 0,
                'rate2data' => 0
            )
        );
    }

    public function commonParameters()
    {
        $aDate = $this->getDateMark();
        return array_merge(parent::commonParameters(), array(
            'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
            'to' => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d'),
            'period' => data_time_PeriodFactory::TYPE_DAY,
            'game_id' => null,
            'export' => 0,
            'file_name' => null,
            'contrast' => 0,
            'contrast_from' => date('Y-m-d', strtotime('-60 day'))
        ));
    }

    public function rttraffic($aUserParameters)
    {
        $this->display('topic/rttraffic.html');
    }

    public function daytraffic($aUserParameters)
    {
        $this->display('topic/daytraffic.html');
    }

    public function multigame($aUserParameters)
    {
        $this->display('topic/multiple.html');
    }

    /**
     * CDN游戏列表
     */
    public function getCdnGame($aUserParameters)
    {
        $oDataInfo = new common_DataInfo();
        $aDataInfo = $oDataInfo->getDataList(common_Stat::getStatByUk(10000, array(array(
            'type' => common_Stat::TYPE_REPORT,
            'stid' => '_mintraffic_',
            'sstid' => '_cdnmax_',
            'op_type' => 'set',
            'op_fields' => 'cdnchannel,cdnvalue'
        ))));
        if (!$aDataInfo) $this->ajax(0);
        $aDataInfo = array_pop($aDataInfo);
        $aCdnGame = array();
        $aGameConfiguration = $this->getGameConfiguration();
        foreach ($aDataInfo as $dataInfo) {
            $aCdnGame[$dataInfo['range']] = isset($aGameConfiguration[$dataInfo['range']])
                ? $aGameConfiguration[$dataInfo['range']]['game_name'] : $dataInfo['range'];
        }
        $this->ajax(0, $aCdnGame);
    }

    /**
     * 人均消耗
     */
    public function getDayTrafficPerMan($aUserParameters)
    {
        if ($aUserParameters['contrast'] &&
                $this->dealContrastDate($aUserParameters) === false) {
            $this->ajax(0);
        }
        // 获取活跃数据
        $aGameConfiguration = $this->getGameConfiguration();
        $aActiveData = $aExpressions = array();
        foreach ($aUserParameters['data_info'] as $i => $dataInfo) {
            if (!isset($dataInfo['range'], $aGameConfiguration[$dataInfo['range']])) {
                unset($aUserParameters['data_info'][$i]);
                continue;
            }
            $aGameInfo = $aGameConfiguration[$dataInfo['range']];
            $aExpressions[] = array('data_name' => $aGameInfo['game_name']);
            $aParameters = $aUserParameters;
            $aParameters['game_id'] = $aGameInfo['game_id'];
            $aParameters['gpzs_id'] = $aGameInfo['gpzs_id'];
            $aParameters = array_merge($aParameters, $this->getActiverConfiguration());
            $model = new data_Data();
            // gpzs不需要权限控制
            $activeData = $model->getTimeSeries($aParameters, false);
            foreach ($activeData as $key => $data) {
                if (!isset($aActiveData[$key], $aActiveData[$key]['key'])) $aActiveData[$key]['key'] = $data['key'];
                if (!isset($aActiveData[$key], $aActiveData[$key]['data'])) $aActiveData[$key]['data'] = array();
                $aActiveData[$key]['data'] = array_merge($aActiveData[$key]['data'], $data['data']);
            }
        }
        // 获取流量数据
        $model = new data_Data();
        $aTrafficData = $model->getTimeSeries($aUserParameters);
        // 表达式
        $iCount = count($aExpressions);
        foreach ($aExpressions as $key => &$expression) {
            $expression['expr'] = sprintf('{%d}/{%d}*1024000', $iCount + $key, $key);
            if ($iCount === 1) $expression['name'] = TM::t('tongji', '消耗带宽量');
        }
        // 计算
        $calc = new data_calculator_Calculator();
        $aData = array();
        foreach ($aTrafficData as $key => $data) {
            $aData[] = $calc->setExpressions($aExpressions)->setOperandsData(array(
                'key'  => $data['key'],
                'data' => array_merge($aActiveData[$key]['data'], $data['data'])
            ))->calculate();
        }
        if ($aUserParameters['contrast']) {
            $aData = $model->contrast($aUserParameters, $aData);
        }
        if ($aUserParameters['export']) $this->export($aUserParameters, $aData);
        $this->ajax(0, $aData);
    }

    /**
     * 处理对比时间
     */
    protected function dealContrastDate(&$aUserParameters)
    {
        $aUserParameters['from'] = (array)$aUserParameters['from'];
        $aUserParameters['to']   = (array)$aUserParameters['to'];
        if (!$aUserParameters['from'] ||
                count($aUserParameters['from']) < count($aUserParameters['to'])) {
            return false;
        }
        $iInterval = strtotime($aUserParameters['to'][0]) - strtotime($aUserParameters['from'][0]);
        foreach ($aUserParameters['from'] as $key => $from) {
            if (!isset($aUserParameters['to'][$key])) {
                $aUserParameters['to'][$key] = date('Y-m-d', strtotime($from) + $iInterval);
            }
        }
        // 对比时，去掉同比环比
        $aUserParameters['yoy'] = $aUserParameters['qoq'] = 0;
        return true;
    }

    /**
     * 导出
     */
    protected function export($aUserParameters, $aData)
    {
        $this->initExporter($aUserParameters);
        $this->oExporter->add($this->sFilename);
        foreach ($aData as $data) {
            $this->oExporter->putWithTitle(TM::t('tongji', '日期'), $data['key']);
            foreach ($data['data'] as $item) {
                $this->oExporter->putWithTitle($item['name'], $item['data']);
                if ($aUserParameters['yoy']) $this->oExporter->putWithTitle(TM::t('tongji', '同比'), $item['yoy']);
                if ($aUserParameters['qoq']) $this->oExporter->putWithTitle(TM::t('tongji', '环比'), $item['qoq']);
            }
            $this->oExporter->put();
        }
        $this->ajax(0, ['code' => 0, 'url' => $this->oExporter->getFilePath()]);
    }

    /**
     * 游戏域名到游戏信息的映射
     */
    protected function getGameConfiguration()
    {
        return array(
            'gf.61.com'    => array('game_id' => 6,  'gpzs_id' => 230, 'game_name' => '功夫派'),
            'hua.61.com'   => array('game_id' => 5,  'gpzs_id' => 177, 'game_name' => '小花仙'),
            'jl.61.com'    => array('game_id' => 16, 'gpzs_id' => 3, 'game_name' => '热血精灵派'),
            'mole.61.com'  => array('game_id' => 1,  'gpzs_id' => 129, 'game_name' => '摩尔庄园'),
            'seer.61.com'  => array('game_id' => 2,  'gpzs_id' => 105, 'game_name' => '赛尔号'),
            'seer2.61.com' => array('game_id' => 10, 'gpzs_id' => 15, 'game_name' => '约瑟传说')
        );
    }

    /**
     * 活跃玩家配置
     */
    protected function getActiverConfiguration()
    {
        return array('data_info' => array(array(
            'type'      => common_Stat::TYPE_REPORT,
            'stid'      => '_lgac_',
            'sstid'     => '_lgac_',
            'op_type'   => 'ucount',
            'op_fields' => '',
            'range'     => ''
        )));
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

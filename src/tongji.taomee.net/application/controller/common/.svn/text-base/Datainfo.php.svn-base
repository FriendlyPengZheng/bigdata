<?php
class Datainfo extends TMController
{
    public function actions()
    {
        return [
            'getList' => [
                'game_id'    => null,
                'type'       => common_Stat::TYPE_REPORT,
                'range'      => null,
                /* report参数 */
                'stid'       => null,
                'sstid'      => null,
                'op_fields'  => null,
                'op_type'    => null,
                /* result参数 */
                'task_id'    => null
            ],
            'getRangeList' => [
                'game_id'    => null,
                'type'       => common_Stat::TYPE_REPORT,
                'range'      => null,
                /* report参数 */
                'stid'       => null,
                'sstid'      => null,
                'op_fields'  => null,
                'op_type'    => null,
                /* result参数 */
                'task_id'    => null
            ],
            'getCommonRange' => [
                'game_id' => [],
                'type' => common_Stat::TYPE_REPORT,
                'stat_info' => [] // stid+sstid+op_fields+op_type or task_id
            ],
            'setName' => ['id' => null, 'name' => null],
            'setRangeName' => ['id' => null, 'name' => null],
            'getListByRid' => ['type' => null, 'r_id' => null]
        ];
    }

    /**
     * 获取数据信息
     * @param array $aUserParameters
     */
    public function getList($aUserParameters)
    {
        $statInfo = common_Stat::getStatByUk($aUserParameters['game_id'], array($aUserParameters));
        if (!$statInfo) $this->ajax(0, array());

        $model = new common_DataInfo();
        $aList = array();
        foreach ($statInfo as $stat) {
            if ($stat['type'] === 'undefined') continue;
            $range = $model->getRangeByRid($stat);
            if (!$range) continue;
            foreach ($range as $info) {
                $aList[] = array(
                    'id'   => $info['data_id'],
                    'name' => $info['data_name']
                );
            }
        }
        $this->ajax(0, $aList);
    }

    /**
     * 获取共享range的data_id
     * @param array $aUserParameters
     */
    public function getRangeList($aUserParameters)
    {
        $statConf = $this->getRangeSharedStat($aUserParameters);
        $statInfo = common_Stat::getStatByUk($aUserParameters['game_id'], $statConf);
        if (!$statInfo) $this->ajax(0, array());

        $model = new common_DataInfo();
        $aList = array();
        foreach ($statInfo as $stat) {
            if ($stat['type'] === 'undefined') continue;
            $range = $model->getRangeByRid($stat);
            if (!$range) continue;
            foreach ($range as $info) {
                if (isset($aList[$info['range']])) {
                    $aList[$info['range']]['id'] .= ',' . $info['data_id'];
                    continue;
                }
                $aList[$info['range']]['id'] = $info['data_id'];
                $aList[$info['range']]['name'] = $info['range_name'];
            }
        }
        $this->ajax(0, array_values($aList));
    }

    /**
     * 获取report/result的range交集
     */
    public function getCommonRange($aUserParameters)
    {
        $this->ajax(0, (new common_DataInfo())->getCommonRange($aUserParameters));
    }

    /**
     * 设置名称
     * @param array $aUserParameters
     */
    public function setName($aUserParameters)
    {
        $model = new common_DataInfo();
        $this->ajax(0, $model->setName($aUserParameters));
    }

    /**
     * 设置range名称
     * @param array $aUserParameters
     */
    public function setRangeName($aUserParameters)
    {
        $aUserParameters['id'] = array_filter(explode(',', $aUserParameters['id']));
        $model = new common_DataInfo();
        $this->ajax(0, $model->setRangeName($aUserParameters));
    }

    /**
     * 共用range的report/result配置
     * @param array $statInfo
     * @return array
     */
    protected function getRangeSharedStat($statInfo)
    {
        $aGroups = [
            0 => [
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_buyitem_', 'op_type' => 'sum', 'op_fields' => '_paychannel_,_amt_'],
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_buyitem_', 'op_type' => 'count', 'op_fields' => '_paychannel_'],
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_buyitem_', 'op_type' => 'ucount', 'op_fields' => '_paychannel_']
            ],
            1 => [
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_acpay_', 'op_type' => 'sum', 'op_fields' => '_paychannel_,_amt_'],
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_acpay_', 'op_type' => 'count', 'op_fields' => '_paychannel_'],
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_acpay_', 'op_type' => 'ucount', 'op_fields' => '_paychannel_']
            ],
            2 => [
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_vipmonth_', 'op_type' => 'sum', 'op_fields' => '_paychannel_,_amt_'],
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_vipmonth_', 'op_type' => 'count', 'op_fields' => '_paychannel_'],
                ['type' => 1, 'stid' => '_acpay_', 'sstid' => '_vipmonth_', 'op_type' => 'ucount', 'op_fields' => '_paychannel_']
            ],
            3 => [
                ['type' => 1, 'stid' => '_buyvip_', 'sstid' => '_buyvip_', 'op_type' => 'count', 'op_fields' => '_amt_'],
                ['type' => 1, 'stid' => '_buyvip_', 'sstid' => '_buyvip_', 'op_type' => 'ucount', 'op_fields' => '_amt_'],
                ['type' => 1, 'stid' => '_buyvip_', 'sstid' => '_buyvip_', 'op_type' => 'sum', 'op_fields' => '_amt_,_payamt_']
            ],
            4 => [
                ['type' => 1, 'stid' => '_ccacct_', 'sstid' => '_ccacct_', 'op_type' => 'count', 'op_fields' => '_cac_'],
                ['type' => 1, 'stid' => '_ccacct_', 'sstid' => '_ccacct_', 'op_type' => 'ucount', 'op_fields' => '_cac_']
            ],
            5 => [
                ['type' => 1, 'stid' => '_unsub_', 'sstid' => '_unsub_', 'op_type' => 'count', 'op_fields' => '_uc_'],
                ['type' => 1, 'stid' => '_unsub_', 'sstid' => '_unsub_', 'op_type' => 'ucount', 'op_fields' => '_uc_']
            ],
            6 => [
                ['type' => 2, 'task_id' => 146],
                ['type' => 2, 'task_id' => 147]
            ],
            7 => [
                ['type' => 2, 'task_id' => 148],
                ['type' => 2, 'task_id' => 149]
            ],
            8 => [
                ['type' => 2, 'task_id' => 150],
                ['type' => 2, 'task_id' => 151]
            ],
            9 => [
                ['type' => 2, 'task_id' => 152],
                ['type' => 2, 'task_id' => 153]
            ],
            10 => [
                ['type' => 2, 'task_id' => 344],
                ['type' => 2, 'task_id' => 345]
            ],
            11 => [
                ['type' => 1, 'stid' => '_loginacct_', 'sstid' => '_loginacct_', 'op_type' => 'ucount', 'op_fields' => '_actype_'],
                ['type' => 1, 'stid' => '_regacct_', 'sstid' => '_regacct_', 'op_type' => 'ucount', 'op_fields' => '_actype_']
            ],
            12 => [
                ['type' => 2, 'task_id' => 354],
                ['type' => 2, 'task_id' => 355]
            ],
            13 => [
                ['type' => 2, 'task_id' => 352],
                ['type' => 2, 'task_id' => 353]
            ],
            14 => [
                ['type' => 1, 'stid' => '_loginacct_', 'sstid' => '_loginacct_', 'op_type' => 'ucount', 'op_fields' => '_acgid_'],
                ['type' => 1, 'stid' => '_regacct_', 'sstid' => '_regacct_', 'op_type' => 'ucount', 'op_fields' => '_acgid_']
            ]
        ];
        $aMap = [
            '1:_acpay_:_buyitem_:sum:_paychannel_,_amt_' => 0,
            '1:_acpay_:_buyitem_:count:_paychannel_' => 0,
            '1:_acpay_:_buyitem_:ucount:_paychannel_' => 0,
            '1:_acpay_:_acpay_:sum:_paychannel_,_amt_' => 1,
            '1:_acpay_:_acpay_:count:_paychannel_' => 1,
            '1:_acpay_:_acpay_:ucount:_paychannel_' => 1,
            '1:_acpay_:_vipmonth_:sum:_paychannel_,_amt_' => 2,
            '1:_acpay_:_vipmonth_:count:_paychannel_' => 2,
            '1:_acpay_:_vipmonth_:ucount:_paychannel_' => 2,
            '1:_buyvip_:_buyvip_:count:_amt_' => 3,
            '1:_buyvip_:_buyvip_:ucount:_amt_' => 3,
            '1:_buyvip_:_buyvip_:sum:_amt_,_payamt_' => 3,
            '1:_ccacct_:_ccacct_:count:_cac_' => 4,
            '1:_ccacct_:_ccacct_:ucount:_cac_' => 4,
            '1:_unsub_:_unsub_:count:_uc_' => 5,
            '1:_unsub_:_unsub_:ucount:_uc_' => 5,
            '2:146' => 6,
            '2:147' => 6,
            '2:148' => 7,
            '2:149' => 7,
            '2:150' => 8,
            '2:151' => 8,
            '2:152' => 9,
            '2:153' => 9,
            '2:344' => 10,
            '2:345' => 10,
            '1:_loginacct_:_loginacct_:ucount:_actype_' => 11,
            '1:_regacct_:_regacct_:ucount:_actype_' => 11,
            '2:354' => 12,
            '2:355' => 12,
            '2:352' => 13,
            '2:353' => 13,
            '1:_loginacct_:_loginacct_:ucount:_acgid_' => 14,
            '1:_regacct_:_regacct_:ucount:_acgid_' => 14
        ];

        $sKey = $statInfo['type'] . ':' . common_Stat::instance($statInfo['type'])->getUk($statInfo);
        if (isset($aMap[$sKey])) {
            return $aGroups[$aMap[$sKey]];
        }

        return [$statInfo];
    }

    /**
     * Get data list by report_id/result_id.
     *
     * @param  array $aUserParameters
     * @return null
     */
    public function getListByRid($aUserParameters)
    {
        $this->ajax(0, (new common_DataInfo())->getRangeByRid($aUserParameters));
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
        // 检查游戏权限
        if (isset($aParameters['game_id'])) {
            (new common_Game())->checkGameAuth($aParameters['game_id']);
        }
    }
}

<?php
class Selfhelp extends common_Base
{
    const OP_SETDIFF = 'setdiff';
    const OP_INTERSECT = 'intersect';
    const OP_UNION = 'union';

    /**
     * @var string The user's query info
     */
    protected $userQueryInfo = array();

    public function actions()
    {
        /* $aDate = $this->getDateMark(); */
        return [
            'index' => array(
                'from' => date('Y-m-d', strtotime('-7 day')),
                'to' => date('Y-m-d')
            ),
            'add' => [
                'file_name' => null,
                'filter_info' => [1],
                'game_id' => null,
                'operation' => self::OP_INTERSECT,
                'operands' => []
            ]
        ];
    }

    public function index($aUserParameters)
    {
        $this->assign('param', ['title' => TM::t('tongji', '自助查询')], true);
        $this->display('tool/selfhelp.html');
    }

    public function add($aUserParameters)
    {
        $params = $this->parseParams($aUserParameters);
		$file = tool_File::createFile(tool_File::SRC_SELFHELP);
        $params = array_merge($params, array('userqueryinfo' => $this->userQueryInfo));
        $file->setFileName($aUserParameters['file_name'])->setParams($params);
        $file->initFileKey($file->getParams());
        $userFile = new tool_UserFile();
        $this->ajax(0, $userFile->addFile($file, [
                'user_id' => TM::app()->getUser()->getUserId(),
                'user_name' => TM::app()->getUser()->getUserName()
                ]
        ));
    }


    protected function parseParams($aUserParameters)    /* add stid sstid op_fields range */
    {
        $params = [];
        $params['operation'] = $this->filterOperation($aUserParameters['operation']);
        $this->InsertQueryInfo($aUserParameters, $params);

        $reportModel = new common_Report();
        $dataModel = new common_DataInfo();
        $original = TM::app()->session->get('record_data');
        $gameArray = array();
        foreach ($aUserParameters['operands'] as $idx => $operand) {
            $op = [];
            TMValidator::ensure(
                isset($operand['type'], $operand['r_id'], $operand['periods']) &&
                $operand['type'] === 'report' &&
                $operand['periods'] &&
                is_array($operand['periods']),
                TM::t('tongji', '事件{idx}不合法！', ['{idx}' => $idx])
            );

            $this->parseGpzs($operand);

            $op['gpzs_id'] = $operand['gpzs_id'];
            $op['game_id'] = $operand['game_id'];

            $report = $reportModel->getStatItemList([
                'game_id' => $operand['game_id'],
                'report_id' => $operand['r_id'],
                'status' => 0 // show
            ], [
                'is_basic' => 0,
                'hide' => 0 // show
            ], null);

            $gameInfo = (new common_Game())->getInfoByGameId($operand['game_id']);
            $gameArray = array_merge($gameArray, (array)$gameInfo['game_name']);
            TMValidator::ensure($report, TM::t('tongji', '事件{idx}不合法！', ['{idx}' => $idx]));
            $data = TMArrayHelper::column($dataModel->getRangeByRid($operand), null, 'data_id');

            TMValidator::ensure($data, TM::t('tongji', '事件{idx}不合法！', ['{idx}' => $idx]));
            if (isset($operand['data_id'])) {
                TMValidator::ensure(
                    isset($data[$operand['data_id']]),
                    TM::t('tongji', '事件{idx}不合法！', ['{idx}' => $idx])
                );
                $op['data_id'] = (int)$operand['data_id'];
            } else {
                $op['data_id'] = (int)key($data);
            }
            $op['periods'] = $operand['periods'];
            $params['operands'][] = $op;

            foreach($operand['periods'] as $k => $v) {
                 $rangeArray[$k] = implode($v, '~');
            }
            $info = isset($operand['data_id']) ? $data[$operand['data_id']]['data_name'] : '无';
            $dataArray = array_values($data);
            list($dataId, $dataName) = isset($operand['data_id']) ? array($operand['data_id'], $info)
                                       : array($dataArray[0]['data_id'], $report[0]['report_name']);
            $topBar = TM::app()->session->get('topBar');
            $source = $topBar['name'];
            if($source !== '专题分析') {
            // if(isset($original[$gameInfo['game_name']], $original[$gameInfo['game_name']][1],
            //          $original[$gameInfo['game_name']][1][$dataId])) {
                $gameInfo['game_name'] = $gameInfo['game_name'] . '【' .  (new common_Game())->getGameTypeName()[$gameInfo['game_type']] . '】';
                $source = $original[$gameInfo['game_name']][1][$dataId]['top_bar'];
            }
            $this->userQueryInfo['eventInfo'][$idx] = array(array(
                                                      '事件' => (string)($idx+1),
                                                      '所属游戏' => $gameInfo['game_name'],
                                                      '所在模块' => (string)$source,
                                                      '平台' => $this->userQueryInfo['temp']['平台'],
                                                      '区服' => $this->userQueryInfo['temp']['区服'],
                                                      '日期范围' => implode($rangeArray, ','),
                                                      '第一级' => $report[0]['stid'],
                                                      '第二级' => $report[0]['sstid'],
                                                      '第三级' => $report[0]['report_name'],
                                                      '第四级' => $info));

            /*删除购物车在session中的记录*/
            $this->cleanBasket(array('game_name' => $gameInfo['game_name'],
                                     'item' => array('item_name' => $dataName, 'data_id' => $dataId)));
        }
        $this->userQueryInfo['topInfo']['游戏'] = implode(array_unique($gameArray), ',');
        return $params;
    }

    protected function parseGpzs(&$operand)
    {
        $gpzs = (new common_GpzsInfo())->findAll([
            'condition' => [
                'gpzs_id' => $operand['gpzs_id'],
                'status' => 0
            ]
        ]);

        TMValidator::ensure($gpzs, TM::t('tongji', '渠道（平台）区服不存在！'));

        $platformInfo = (new common_GpzsInfo())->findAll([
            'condition' => [
                'game_id' => $gpzs[0]['game_id'],
                'platform_id' => $gpzs[0]['platform_id'],
                'zone_id' => -1,
                'server_id' => -1
            ]
        ]);

        $extraInfo = '';
        if($gpzs[0]['zone_id'] === '-1') {
            $extraInfo .= ' 全区';
        } if($gpzs[0]['server_id'] === '-1') {
            $extraInfo .= ' 全服';
        } if($gpzs[0]['gpzs_name'] === '全平台') {
            $gpzs[0]['gpzs_name'] = '';
            $extraInfo = '全区全服';
        }

        $this->userQueryInfo['temp']['区服'] = $gpzs[0]['gpzs_name'] . $extraInfo;
        $this->userQueryInfo['temp']['平台'] = $platformInfo[0]['gpzs_name'];

        $operands['game_id'] = (int)$gpzs[0]['game_id'];
        (new common_Game())->checkGameAuth($operand['game_id']);
        $operands['gpzs_id'] = (int)$gpzs[0]['gpzs_id'];
    }

    protected function filterOperation($operation)
    {
        TMValidator::ensure(
            in_array(
                $operation,
                [self::OP_SETDIFF, self::OP_INTERSECT, self::OP_UNION]
            ),
            TM::t('tongji', '未知运算！')
        );
        $this->userQueryInfo['topInfo']['事件操作方式'] = $this->operationFactory($operation);
        $this->userQueryInfo['topInfo']['提交查询时间'] = (string)date('Y-m-d H:i');
        return $operation;
    }

    /**
     * @brief InsertQueryInfo
     * 插入用户查询信息
     * @param {array} $aUserParameters
     * @param {array} $params
     *
     * @return
     */
    protected function InsertQueryInfo($aUserParameters, &$params)
    {
        $params['game_id'] = (int)$aUserParameters['game_id'];
        (new common_Game())->checkGameAuth($params['game_id']);

        TMValidator::ensure(is_array($aUserParameters['filter_info']), TM::t('tongji', '查询条件不是数组'));
        $this->getQueryFilter($aUserParameters['filter_info']);
        $params['filter_info'] = $aUserParameters['filter_info'];
        $params['file_name'] = $aUserParameters['file_name'];

        $gameInfo = (new common_Game())->getInfoByGameId($params['game_id']);
        $this->userQueryInfo['topInfo']['选定查询的游戏'] = $gameInfo['game_name'];
        TMValidator::ensure(
            is_array($aUserParameters['operands']) && $aUserParameters['operands'],
            TM::t('tongji', '事件不能为空！')
        );
    }

    /**
     * @brief cleanBasket
     * clean the information in the basket
     * @param {array} $param
     *
     * @return
     */
    private function cleanBasket($param)
    {
        $original = TM::app()->session->get('record_data');
        if(isset($original)) {
            if(isset($original[$param['game_name']][1][$param['item']['data_id']])) {
                unset($original[$param['game_name']][1][$param['item']['data_id']]);
                if(empty($original[$param['game_name']][1])) {
                    unset($original[$param['game_name']]);
                    $update = $original;
                } else {
                    $record = $original[$param['game_name']];
                    $update = array_merge($original, array($param['game_name'] => $record));
                }
                TM::app()->session->add('record_data', $update);
            }
        }
    }

    private function operationFactory($operation)
    {
        switch ($operation) {
            case self::OP_SETDIFF:
                $name = '差集运算';
                break;
            case self::OP_INTERSECT:
                $name = '交集运算';
                break;
            case self::OP_UNION: $name = '并集运算'; 
        }
        return $name;
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
        parent::beforeRunAction($aParameters);
        // 检查游戏管理权限
        if (isset($aParameters['game_id'])) {
            if (!(new common_Game())->hasGameManageAuth($aParameters['game_id'])) {
                TM::app()->getUser()->setAuthorized(false)->forbidden();
            }
        }
    }

    private function getQueryFilter($filter)
    {
        if(count($filter) === 1 && (int)$filter[0] === 1) {
            $extraInfo = '仅米米号';
        }
        else if(count($filter) === 17) {
            $extraInfo = '所有指标';
        }
        else {
            $extraInfo = '指定指标';
        }
        $this->userQueryInfo['topInfo']['选定查询的方式'] = $extraInfo; //implode($filterInfo, ',');
    }

	/**
	 * 获取自助查询白名单内的权限列表
	 */
	public function getGamePermitList($aUserParameters)
	{
		$gameList = (new common_Game())->getGamePermitList(array(2,5,6,10,16));
		$this->ajax(0,array_values($gameList));
	}

    private function getFilterArray()
    {
        return array(
            '1' =>  '米米号',
            '2' =>  '首次登录时间',
            '3' =>  '最后登录时间',
            '4' =>  '等级',
            '5' =>  '当前是否为VIP',
            '6' =>  '当月付费额',
            '7' =>  '当月付费次数',
            '8' =>  '首次按条付费时间',
            '9' =>  '最后按条付费时间',
            '10' =>  '累计按条付费总额',
            '11' =>  '累计按条付费次数',
            '12' =>  '首次包月时间',
            '13' =>  '最后包月时间',
            '14' =>  '累计包月总额',
            '15' =>  '累计包月次数',
            '16' =>  '游戏币消耗量',
            '17' =>  '游戏币存量'
            );
	}
}

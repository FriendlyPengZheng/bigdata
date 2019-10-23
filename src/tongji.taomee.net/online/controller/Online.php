<?php
class Online extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(
                'time'    => date('Y-m-d'),
                'game_id' => -1, // 所有游戏
                'zs_id'   => null,
                'ctrl'    => 0
            ),
            'getZoneServer' => array(
                'game_id' => null
            ),
            'getData' => array(
                'zs_id' => null,
                'time'  => null
            )
        );
    }

    public function index($aUserParam)
    {
        $game = $this->_getGameOptions();
        $this->assign('game', $game);

        $dataList = $this->_getDataListOptions();
        if ($aUserParam['game_id'] && isset($dataList[$aUserParam['game_id']])) {
            $dataList = $dataList[$aUserParam['game_id']];
        } else {
            $dataList = reset($dataList);
        }
        $this->assign('zone_server', $dataList);

        if (!isset($aUserParam['zs_id'])) {
            $first = reset($dataList);
            $this->assign('param', array('zs_id' => $first['id']), true);
        }

        $this->display('user/online.html');
    }

    /**
        * @brief autoIndex 自动展示首页模板
        *
        * @return 
     */
    public function autoIndex()
    {
        $this->display('user/autoonline.html');
    }
    
    /**
        * @brief getGameInfo 异步返回要自动展示的游戏数据
        *
        * @return 
     */
    public function getGameInfo()
    {
        $game = $this->_getGameInfo();
        $this->ajax(0,$game);
    }

    /**
        * @brief _getGameInfo 私有方法获取游戏数据
        *
        * @return 返回重组后的数组
     */
    private function _getGameInfo()
    {
        $arr = TM::app()->session->get('game_info');
        if (isset($arr)) return $arr;

        $authGame = TM::app()->getUser()->getGameByAuth();
        $limitArr = array(2, 5, 6, 10, 15, 16, 25);
        $arr = array();
        if(!empty($authGame)) {
            foreach ($authGame as $gameId => $gameInfo) {
                if(!empty($gameInfo['data_list'])) {
                    foreach ($gameInfo['data_list'] as $dataId => $dataInfo){
                        if(in_array($gameId,$limitArr)) {
                            $arr[]=array('gameName' => $gameInfo['game_name'],'game_id' => $gameId,'zs_id' => $dataId,'dataName' => $dataInfo['data_name']);
                        }
                     }
                }
            }
        }
        TM::app()->session->add('game_info', $arr);
        return $arr;
    }

    /**
        * @brief getZoneServer 根据前台传递的game_id 获取该游戏的区。
        *
        * @param $aUserParam
        *
        * @return 
     */
    public function getZoneServer($aUserParam)
    {
        $dataList = $this->_getDataListOptions();
        if ($aUserParam['game_id'] && isset($dataList[$aUserParam['game_id']])) {
            $dataList = $dataList[$aUserParam['game_id']];
        } else {
            $dataList = reset($dataList);
        }
        $this->ajax(0, $dataList);
    }

    /**
        * @brief getData 根据参数统计游戏在线数据
        *
        * @param $aUserParam
        *
        * @return 
     */
    public function getData($aUserParam)
    {
        $aUserParam['zs_id'] = array_filter(explode(',', $aUserParam['zs_id']));
        $aUserParam['time'] = array_filter(explode(',', $aUserParam['time']));
        if (!$aUserParam['zs_id'] || !$aUserParam['time']) $this->ajax(0);

        list($dataList, $rpInfo, $dataInfo) = $this->_parseDataKey($aUserParam['zs_id']);
        $periodSet = new data_time_PeriodSet();
        foreach ($aUserParam['time'] as $date) {
            $periodSet->add(data_time_PeriodFactory::createPeriod($date, $date, data_time_PeriodFactory::TYPE_MINUTE));
        }
        $model = new data_Data();
        $rpData = $model->getStatData($rpInfo, $periodSet);
        $tjData = $model->getData($dataInfo, $periodSet);
        if (!$rpData && !$tjData) $this->ajax(0);

        $this->ajax(0, $this->_combineData($dataList, $periodSet, $rpData, $tjData));
    }

    private function _getGameOptions()
    {
        $game = TM::app()->session->get('game_options');
        if (isset($game)) return $game;

        $options = $this->_getOptions();
        return $options[0];
    }

    private function _getDataListOptions()
    {
        $dataList = TM::app()->session->get('data_list_options');
        if (isset($dataList)) return $dataList;

        $options = $this->_getOptions();
        return $options[1];
    }

    /**
        * @brief _getOptions 根据不同用户权限读取的游戏数组，然后重组该游戏数据
        *
        * @return 
     */
    private function _getOptions()
    {
        $game = $summary = $dataList = array();
        $authGame = TM::app()->getUser()->getGameByAuth();
        foreach ($authGame as $gameId => $gameInfo) {
            if (isset($gameInfo['summary']) && $gameInfo['summary']) {
                foreach ($gameInfo['summary'] as $dataKey) {
                    if (!isset($gameInfo['data_list'][$dataKey])) continue;
                    $summary[] = $dataKey;
                }
            }
            $game[] = array('id' => $gameId, 'name' => $gameInfo['game_name']);

            $dataList[$gameId] = array();
            foreach ($gameInfo['data_list'] as $dataKey => $dataInfo) {
                $dataList[$gameId][] = array('id' => $dataKey, 'name' => $dataInfo['data_name']);
            }
            if (count($gameInfo['data_list']) > 1) {
                array_unshift($dataList[$gameId], array(
                    'id'   => implode(',', array_keys($gameInfo['data_list'])),
                    'name' => TM::t('online', '所有区服')
                ));
            }
        }

        if ($summary) {
            array_unshift($game, array(
                'id'   => -1,
                'name' => TM::t('online', '通常')
            ));
            $dataList[-1] = array(array(
                'id'   => implode(',', $summary),
                'name' => TM::t('online', '通常')
            ));
        }

        TM::app()->session->add('game_options', $game);
        TM::app()->session->add('data_list_options', $dataList);
        return array($game, $dataList);
    }

    private function _parseDataKey($aDataKey)
    {
        $authGame = TM::app()->getUser()->getGameByAuth();
        $dataList = $rpInfo = $dataInfo = array();
        foreach ($aDataKey as $dataKey) {
            $gameId = $dataKey >> 8;
            if (!isset($authGame[$gameId]['data_list'][$dataKey])) continue;
            $info = $authGame[$gameId]['data_list'][$dataKey];
            if (is_array($info['data_id'])) {
                $rp = $data = array();
                foreach ($info['data_id'] as $key) {
                    if (!isset($authGame[$gameId]['data_list'][$key])) continue 2;
                    $sub = $authGame[$gameId]['data_list'][$key];
                    if (isset($sub['gpzs_id'])) {
                        $data[$key] = $sub;
                    } else {
                        $rp[$key] = $sub;
                    }
                }
                $dataInfo += $data;
                $rpInfo += $rp;
            } else {
                if (isset($info['gpzs_id'])) {
                    $dataInfo[$dataKey] = $info;
                } else {
                    $rpInfo[$dataKey] = $info;
                }
            }
            $info['data_name'] = ($dataKey & 0xFF) ? $info['data_name'] : $authGame[$gameId]['game_name'];
            $dataList[$dataKey] = $info;
        }
        return array($dataList, $rpInfo, $dataInfo);
    }

    private function _combineData($dataList, $periodSet, $rpData, $tjData)
    {
        $aData = array();
        $sum = function($v1, $v2) { if (isset($v1) && isset($v2)) return $v1 + $v2; };
        foreach ($periodSet as $idx => $period) {
            $data = $source = array();
            if ($rpData) {
                $source += $rpData[$idx]['data'];
                $data = $rpData[$idx];
            }
            if ($tjData) {
                $source += $tjData[$idx]['data'];
                $data = $tjData[$idx];
            }
            $data['data'] = array();
            $date = date('Y-m-d', $period->getFrom());
            $week = data_time_Time::date2week($date);
            foreach ($dataList as $dataKey => $info) {
                $each = array('name' => $info['data_name'] . ' ' . $date . ' ' . $week, 'data' => array());
                if (is_array($info['data_id'])) {
                    foreach ($info['data_id'] as $key) {
                        if (!isset($source[$key])) continue 2;
                        if (!$each['data']) {
                            $each['data'] = $source[$key]['data'];
                            continue;
                        }
                        $each['data'] = array_map($sum, $each['data'], $source[$key]['data']);
                    }
                } elseif (isset($source[$dataKey])) {
                    $each['data'] = $source[$dataKey]['data'];
                }
                $data['data'][] = $each;
            }
            $aData[] = $data;
        }
        return $aData;
    }
}

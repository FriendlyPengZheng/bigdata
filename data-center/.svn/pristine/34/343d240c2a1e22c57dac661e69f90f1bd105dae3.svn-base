<?php
class Basket extends TMController
{
    public function actions()
    {
        return array(
            'addBasketInfo' => array(
                'game_name' => null,
                'game_id' => null,
                'items' => array()
            ),
            'deleteBasketInfo' => array(
                'game_name' => null,
                'items' => array()
            ),
            'reviseInfo' => array(
                'filter_info' => null,
                'operands' => null,
            )
        );
    }

    /**
     * @brief addBasketInfo
     * insert record into the basket
     * @param $aUserParameters
     *
     * @return {boolean}
     */
    public function addBasketInfo($aUserParameters)
    {
        if(!(isset($aUserParameters['game_name']) && isset($aUserParameters['game_id']))) {
            $this->ajax(1, false);
        }
        if(is_array($data = $aUserParameters['items'])) {
            $reportItem = $this->setKeyByValue($data);
            $reportData = array(0 => $aUserParameters['game_id'], 1 => $reportItem);
            if($original = TM::app()->session->get('record_data')) {
                $gameArray = isset($original[$aUserParameters['game_name']]) ? $original[$aUserParameters['game_name']] : null;
                /* $record = array_merge((array)$gameArray, (array)$reportData); */
                $record = array(0 => $aUserParameters['game_id'], 1 => (array)$gameArray[1] + (array)$reportData[1]);
                $update = array_merge($original, array($aUserParameters['game_name'] => $record));
                TM::app()->session->add('record_data', $update);
            }
            else {
                $record = array($aUserParameters['game_name'] => $reportData);
                TM::app()->session->add('record_data', $record);
            }
            $this->ajax(0, true);
        }
        $this->ajax(1, false);
    }

    /**
     * @brief deleteBasketInfo
     * delete record from the basket
     * @param $aUserParameters
     *
     * @return {boolean}
     */
    public function deleteBasketInfo($aUserParameters)
    {
        if($aUserParameters['game_name'] === null) {
            $this->ajax(1, false);
        }
        $isExisted = false;
        if(is_array($data = $aUserParameters['items'])) {
            $data = $this->setKeyByValue($data);
            $original = TM::app()->session->get('record_data');
            foreach($data as $k => $v) {
                if(isset($original[$aUserParameters['game_name']][1][$k])) {
                    $isExisted = true;
                    unset($original[$aUserParameters['game_name']][1][$k]);
                    if(empty($original[$aUserParameters['game_name']][1])) {
                            unset($original[$aUserParameters['game_name']]);
                            $update = $original;
                    } else {
                        $record = $original[$aUserParameters['game_name']];
                        $update = array_merge($original, array($aUserParameters['game_name'] => $record));
                    }
                    TM::app()->session->add('record_data', $update);
                }
            }
            TMValidator::ensure($isExisted, TM::t('tongji', '要在购物车中删除的记录根本不存在, 没有放在购物车中.'));
            $this->ajax(0, true);
        }
        $this->ajax(1, false);
    }

    /**
     * @brief getBasketInfo
     * get all the records from the basket
     * @return {array}
     */
    public function getBasketInfo()
    {
        $basketInfo = [];
        $filter = TM::app()->session->get('filter_info');
        if(!isset($filter)) {
            $filter = [1];
        }
        $recordData = TM::app()->session->get('record_data');
        $dftTime = date('Y-m-d', strtotime('-7 day')) . '~' . date('Y-m-d');
        if(isset($recordData)) {
            foreach($recordData as $k => $v) {
                $data = array_merge(array('game_name' => $k), array('game_id' => $v[0]), array('filter_info' => $filter), array('dft_time' => $dftTime), array('items' => array_values($v[1])));
                $basketInfo[] = $data;
            }
            $this->ajax(0, $basketInfo);
        }
        $this->ajax(0, array());
    }

    /**
     * @brief reviseInfo
     * revise the item in the basket
     * @param $aUserParameters
     *
     * @return {boolean}
     */
    public function reviseInfo($aUserParameters)
    {
        if(isset($aUserParameters['filter_info'])) {
            TM::app()->session->add('filter_info', $aUserParameters['filter_info']);
        }
        if(isset($aUserParameters['operands'])) {
            $isExisted = false;
            if(is_array($data = $aUserParameters['operands'])) {
                $data = $this->setKeyByValue($data);
                $original = TM::app()->session->get('record_data');
                foreach($data as $k => $v) {
                    TMValidator::ensure($v['game_name'], TM::t('tongji', '需要修订的具体游戏不能知道'));
                    if(isset($original[$v['game_name']][1][$k])) {
                        $isExisted = true;
                        $original[$v['game_name']][1][$k] = $v;
                        TM::app()->session->add('record_data', $original);
                    }
                }
                TMValidator::ensure($isExisted, TM::t('tongji', '要在购物车中删除的记录根本不存在, 没有放在购物车中.'));
            }
        }
        $this->ajax(0, true);
    }

    /**
     * @brief addKeyByValue
     * 将每条记录的键值设置为data_id值
     * @param {array} $aParams
     *
     * @return {array}
     */
    private function setKeyByValue($aParams)
    {
        $dataModel = new common_DataInfo();
        $topBar = TM::app()->session->get('topBar');
        $reportData = [];
        foreach($aParams as $k => $value) {
            if(!isset($value['periods'])) {
                $value['periods'] = array(0 => array('from' => date('Y-m-d', strtotime('-7 day')),
                                          'to' => date('Y-m-d')));
            }
            if(isset($value['data_id']) && $value['data_id'] === '') {
                unset($value['data_id']);
            }
            $value['top_bar'] = $topBar['name'];
            TMValidator::ensure($value['item_name'], TM::t('tongji', 'item_name参数错误')); 
            if(!isset($value['data_id'])) {
                $valueExtra = array_merge($value, array('type' => 'report'));
                $dataInfo = TMArrayHelper::column($dataModel->getRangeByRid($valueExtra), null, 'data_id');
                TMValidator::ensure($dataInfo, TM::t('tongji', 'dataInfo数据库中不存在,参数错误')); 
                $reportData[key($dataInfo)] = $value;
            }
            else {
                $reportData[$value['data_id']]  = $value;
            }
        }
        return $reportData;
    }
}

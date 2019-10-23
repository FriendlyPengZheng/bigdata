<?php
class Content extends gamecustom_Custom
{
    const WEEKLY = 'week';
    const MONTHLY = 'month';
    const ALL = 'all';
    const MODIFY_WEEKLY = 'modify_week';

    public function actions()
    {
        return array(
            'getContentList' => array(
                'game_id' => null,
                'node_id' => null,
                'from'    => null,
                'to'      => null,
                'tags'    => self::ALL,
                'searchValue' => null
            ),
            'getDataList' => array(
                'r_id' => null,
                'type' => common_Stat::TYPE_REPORT,
                'tags' => self::ALL,
                'searchValue' => null
            ),
            'getTimePoints' => array(
                'from'    => null,
                'to'      => null
            )
        );
    }

    public function getContentList($aUserParameters)
    {
        $aPoints = data_time_PeriodFactory::createPeriod(
            $aUserParameters['from'], $aUserParameters['to'], data_time_PeriodFactory::TYPE_DAY)->getPoints();
        if ($aUserParameters['searchValue']) {
            $searchValue = $aUserParameters['searchValue'];
        }
        $model = new common_DataInfo();
        $aUserParameters['time'] = $this->getTime($aUserParameters['tags']);
        $content = parent::getContentList($aUserParameters);
        foreach ($content as $isMulti => &$value) {
            foreach ($value as $key => &$info) {
                if (empty($info)) {
                    continue;
                }
                // 搜索
                if (isset($searchValue) && false === strstr($info['r_name'], $searchValue)) {
                    unset($value[$key]);
                    continue;
                }
                $items = count($model->getRangeByRid($info));
                if ($info['type'] === common_Stat::type2string(common_Stat::TYPE_REPORT) && $isMulti) {
                    $info['r_length'] = $items;
                } else {
                    $info['r_length'] = -1;
                }
                $info = $this->setExtraListParam($info, (int)$aUserParameters['game_id']);
            }
            $content[$isMulti] = array_values($value);
        }
        $this->ajax(0, array(
            'date' => $aPoints['key'],
            'data' => $content
        ));
    }

    public function getDataList($aUserParameters)
    {
        // diy data
        if ($aUserParameters['type'] === 'diy') {
            $model = new gamecustom_DiyData();
            $model->diy_id = $aUserParameters['r_id'];
            $this->ajax(0, $model->findByDiyId());
        }

        // report or result
        if (is_numeric($aUserParameters['type'])) {
            $aUserParameters['type'] = common_Stat::type2string($aUserParameters['type']);
        }
        if ($aUserParameters['searchValue']) {
            $searchValue = $aUserParameters['searchValue'];
        }
        $model = new common_DataInfo();
        $tags = $this->getTags($aUserParameters['tags']);
        $dataInfo = $model->getRangeByRid($aUserParameters, $tags);
        $reportModel = new common_Report();
        $statItem = $reportModel->getStatItemById((int)$aUserParameters['r_id']);
        foreach($dataInfo as $key => &$info) {
            if (isset($searchValue) && false === strstr($info['data_name'], $searchValue)) {
                unset($dataInfo[$key]);
                continue;
            }
            $info['ifselfhelp'] = $statItem['op_type'];
            $info['r_id'] = $aUserParameters['r_id'];
            $info = $this->setExtraListParam($info, (int)$statItem['game_id']);
        }
        $this->ajax(0, $dataInfo);
    }

    public function getTimePoints($aUserParameters)
    {
        $aPoints = data_time_PeriodFactory::createPeriod(
            $aUserParameters['from'], $aUserParameters['to'], data_time_PeriodFactory::TYPE_DAY)->getPoints();
        $this->ajax(0, $aPoints['key']);
    }

    /**
     * @brief getTags
     *
     * @param $tagType
     *
     * @return
     */
    public function getTags($tagType) 
    {
        $timeCode = array(
            self::WEEKLY =>  array('add_time', '>=', date('Y-m-d H:i:s', strtotime('-7 day'))),
            self::MONTHLY => array('add_time', '>=', date('Y-m-d H:i:s', strtotime('-1 month'))),
            self::MODIFY_WEEKLY => array('modify_time', '>=', date('Y-m-d H:i:s', strtotime('-7 day'))),
            self::ALL => array('add_time', '>', -1)
        );
        return TMArrayHelper::assoc($tagType, $timeCode, self::ALL);
    }

    /**
     * @brief getTime
     *
     * @param $timeType
     *
     * @return
     */
    public function getTime($timeType) 
    {
        $timeCode = array(
            self::WEEKLY =>  date('Y-m-d H:i:s', strtotime('-7 day')),
            self::MONTHLY => date('Y-m-d H:i:s', strtotime('-1 month')),
            self::ALL => -1
        );
        return TMArrayHelper::assoc($timeType, $timeCode, self::ALL);
    }
}

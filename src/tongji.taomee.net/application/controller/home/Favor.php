<?php
class Favor extends home_Home
{
    public function actions()
    {
        $aDate = $this->getDateMark();
        return array(
            'index' => array(
                'period' => 1,
                'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
                'to'   => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d'),
                'platform_id' => null,
                'gpzs_id' => null,
                'favor_id' => null
            ),
            'getList' => array(),
            'export' => array(
                'favor_id' => null,
                'gpzs_id' => null,
                'period' => 1,
                'from' => null,
                'to' => null,
                'file_name' => null
            ),
            'add' => array(
                'favor_name' => null,
                'favor_type' => null,
                'game_id'    => null,
                'layout'     => null
            ),
            'set' => array(
                'favor_id'   => null,
                'favor_name' => null,
                'layout'     => null
            ),
            'delete' => array('favor_id' => null),
            'setDefault' => array('favor_id' => null),
            'cancelDefault' => array('favor_id' => null),
            'share' => array(
                'favor_id' => null,
                'users' => null
            )
        );
    }


    /**
     * Assign home aside.
     * @param array $aUserParameters
     * @return array current favor
     */
    protected function assignAside($aUserParameters)
    {
        $model = new home_Favor();
        $iUserId = TM::app()->getUser()->getUserId();
        $aList = $model->getListByUserId($iUserId);
        if (!$aList) {
            $aDefaultFavor = $model->addDefaultFavor($iUserId);
            $aList = array($aDefaultFavor);
        }

        $route = TM::app()->getCompleteRoute();
        $oUrlManager = TM::app()->getUrlManager();
        $iCurrent = null;
        $shared = true;
        foreach ($aList as $key => &$favor) {
            $favor['url'] = $oUrlManager->rebuildUrl($route, array('favor_id' => $favor['favor_id']));
            if ($favor['favor_id'] === $aUserParameters['favor_id'] || $favor['is_default'] && !isset($iCurrent)) {
                $shared = false;
                $iCurrent = $key;
            }
        }
        $key ++;
        if (isset($iCurrent)) {
            $shared = false;
        }
        unset($favor);

        $model = new home_SharedFavor();
        $favors = $model->getListByUserId($iUserId);
        foreach ($favors as $favor) {
            $aList[$key] = $favor;
            $aList[$key]['url'] = $oUrlManager->rebuildUrl($route, array('favor_id' => $favor['favor_id']));
            if ($favor['favor_id'] === $aUserParameters['favor_id'] || $favor['is_default'] && !isset($iCurrent)) {
                $shared = true;
                $iCurrent = $key;
            }
            $key ++;
        }

        if (!isset($iCurrent)) {
            $shared = false;
        }


        $aList[(int)$iCurrent]['current'] = 1;
        $this->assign('home_aside', $aList);
        $this->assign('shared', $shared ? 1 : 0);
        return $aList[(int)$iCurrent];
    }

    /**
     * Assign show_date if the interval is larger than max seconds.
     * @param array $aUserParameters
     * @param integer $iMaxSecs
     */
    protected function assignShowDate($aUserParameters, $iMaxSecs)
    {
        $aExtra = array(
            'show' => 0,
            'show_from' => $aUserParameters['from'],
            'show_to' => $aUserParameters['to']
        );
        $iBegin = strtotime($aUserParameters['from']);
        $iEnd = strtotime($aUserParameters['to']);
        if ($iBegin && $iEnd && $iEnd - $iBegin > $iMaxSecs) {
            $aExtra['show'] = 1;
            $aExtra['show_from'] = date('Y-m-d', $iEnd - $iMaxSecs);
        }
        $this->assign('param', $aExtra, true);
    }

    public function index($aUserParameters)
    {
        $this->assignShowDate($aUserParameters, 63072000); // 730 days
        $aInfo = $this->assignAside($aUserParameters);
        if ($aInfo['favor_type'] == home_Favor::TYPE_SINGLE_GAME) {
            $aAuthGameList = (new common_Game())->getIdGroupedGameByAuth();
            if (isset($aAuthGameList[$aInfo['game_id']])) {
                $aInfo['game_name'] = $aAuthGameList[$aInfo['game_id']]['game_name'];
                $this->assignGpzs(array_merge($aInfo, array('platform_id' => $aUserParameters['platform_id'])));
            }
        }
        $this->assign('favor_info', $aInfo);
        $this->display('home/favor.html');
    }

    /**
     * Assign platform and zone-server info.
     * @param array $aUserParameters
     */
    protected function assignGpzs($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->attributes = $aUserParameters;
        $aPlatform = $model->getPlatform();
        $this->assign('platform', $aPlatform);
        if (!isset($aUserParameters['platform_id']) && ($key = key($aPlatform)) !== false) {
            $aUserParameters['platform_id'] = $aPlatform[$key]['platform_id'];
            $model->platform_id = $aPlatform[$key]['platform_id'];
        }
        $this->assign('zone_server', $model->getZoneServer());
    }

    /**
     * Get favor list.
     */
    public function getList()
    {
        $model = new home_Favor();
        $this->ajax(0, $model->getListByUserId(TM::app()->getUser()->getUserId()));
    }

    /**
     * Export favor data
     */
    public function export($aUserParameters)
    {
        // For data_Data getTimeSeries
        $aUserParameters['period'] = data_time_PeriodFactory::TYPE_DAY;
        $aUserParameters['game_id'] = 0;
        $aUserParameters['by_item'] = 0;
        $aUserParameters['yoy'] = 0;
        $aUserParameters['qoq'] = 0;
        $aUserParameters['contrast'] = 0;
        $aUserParameters['average'] = 0;
        $aUserParameters['rate2data'] = 0;
        $aUserParameters['by_data_expr'] = 1;

        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Favor();
        $aFavor = $model->getFavorById($aUserParameters['favor_id']);
        TMValidator::ensure($aFavor, TM::t('tongji', '我的收藏不存在'));
        $this->initExporter($aUserParameters)->setTempDirname();
        $model = new home_Collect();
        $aCollects = $model->getListByFavorId($aUserParameters['favor_id']);
        TMValidator::ensure($aCollects, TM::t('tongji', '收藏中还没有小部件，添加一个试试吧！'));
        $model = new home_Metadata();
        $iRows = 0;
        foreach ($aCollects as $idx => &$collect) {
            $collect['data_info'] = $model->getListByCollectId($collect['collect_id']);
            if (!$collect['data_info']) {
                unset($aCollects[$idx]);
                continue;
            }
            $iRows += count($collect['data_info']);
        }
        TMValidator::ensure($iRows, TM::t('tongji', '收藏中没有数据！'));
        $aTimePoints = data_time_PeriodFactory::createPeriod(
            $aUserParameters['from'], $aUserParameters['to'], $aUserParameters['period'])->getPoints();
        $aUserParameters['collects'] = $aCollects;
        $this->gotoExportFile(
            [
                'rows' => $iRows,
                'cols' => count($aTimePoints['key'])
            ],
            $aUserParameters,
            ['home', 'favor', $aUserParameters['favor_id']]
        );
        /* $this->ajax(0, $this->packCollects($aUserParameters)); */
        $this->ajax(0, ['code' => 0, 'url' => $this->packCollects($aUserParameters)]);
    }

    /**
     * Pack collects' data in one favor.
     * @param  array  $aUserParameters
     * @return string
     */
    protected function packCollects($aUserParameters)
    {
        $aCollects = $aUserParameters['collects'];
        unset($aUserParameters['collects']);
        foreach ($aCollects as $collect) {
            $aUserParameters['data_info'] = $collect['data_info'];
            $aData = data_Data::model()->getTimeSeries($aUserParameters);
            $this->writeTimeSeries($aData, $collect['collect_name'], true);
        }
        return $this->oExporter->pack($this->sFilename);
    }

    public function add($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Favor();
        $this->ajax(0, $model->add($aUserParameters));
    }

    public function set($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Favor();
        $this->ajax(0, $model->set($aUserParameters));
    }

    /**
     * 删除我的收藏
     */
    public function delete($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Favor();
        $favor = $model->getFavorById($aUserParameters['favor_id']);
        if ($favor['user_id'] == $aUserParameters['user_id']) {
            $model->deleteFavor($aUserParameters);
            $model = new home_SharedFavor();
            $model->deleteAllByAttributes(['favor_id' => $aUserParameters['favor_id']]);
        } else {
            $model = new home_SharedFavor();
            $model->favor_id = $aUserParameters['favor_id'];
            $model->user_id = $aUserParameters['user_id'];
            $model->delete();
        }
        $this->ajax(0);
    }

    /**
     * 设置为默认收藏
     */
    public function setDefault($aUserParameters)
    {
        $model = new home_Favor();
        $modelShared = new home_SharedFavor();
        $transaction = $model->getDb()->beginTransaction();
        $favor = $model->getFavorById($aUserParameters['favor_id']);
        TMValidator::ensure($favor, TM::t('tongji', '收藏不存在！'));
        $userId = TM::app()->getUser()->getUserId();
        try {
            $modelShared->updateFavorByUserId($userId, ['is_default' => 0]);
            $model->updateFavorByUserId($userId, ['is_default' => 0]);
            if ($favor['user_id'] == $userId) {
                $model->setDefault($aUserParameters);
            } else {
                $aUserParameters['user_id'] = $userId;
                $modelShared->setDefault($aUserParameters);
            }
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
        $this->ajax(0);
    }

    /**
     * 取消默认收藏
     */
    public function cancelDefault($aUserParameters)
    {
        $model = new home_Favor();
        $modelShared = new home_SharedFavor();

        $favor = $model->getFavorById($aUserParameters['favor_id']);
        TMValidator::ensure($favor, TM::t('tongji', '收藏不存在！'));
        $userId = TM::app()->getUser()->getUserId();
        if ($favor['user_id'] == $userId) {
            $model->cancelDefault($aUserParameters);
        } else {
            $aUserParameters['user_id'] = $userId;
            $modelShared->cancelDefault($aUserParameters);
        }
        $this->ajax(0);
    }

    public function share($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->user->getUserId();

        $model = new home_Favor();
        $favor = $model->checkFavorExists($aUserParameters, false);

        $model = new home_SharedFavor();
        $model->favor_id = $favor['favor_id'];

        $users = explode(',', $aUserParameters['users']);
        foreach ($users as $userId) {
            $userId = (int)$userId;
            if (!$userId) continue;
            $model->user_id = $userId;
            $model->delete();
            $model->insert();
        }
        $this->ajax(0);
    }
}

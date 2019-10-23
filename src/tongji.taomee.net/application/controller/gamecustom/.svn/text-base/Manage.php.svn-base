<?php
class Manage extends gamecustom_Custom
{
    public function actions()
    {
        return array(
            'index' => array('view_r' => '', 'game_id' => $this->getDefaultGameId()),
            'getStatItem' => array('r_id' => null, 'type' => 'report'),
            'setStatItem' => array(
                'r_id' => null,
                'pre_name' => null,
                'suf_name' => null,
                'low_flag' => null,
                'high_flag' => null,
                'range_low' => array(),
                'range_high' => array()
            ),
            'saveDiy' => array(
                'diy_id' => null, // null for insert, other for update
                'diy_name' => '',
                'diy_type' => null,
                'data_rule' => '',
                'pre_name' => null,
                'suf_name' => null,
                'low_flag' => null,
                'high_flag' => null,
                'node_id' => null,
                'game_id' => null
            ),
            'removeStatItem' => array('r_list' => null),
            'getStatItemByNodeId' => array('game_id' => null, 'node_id' => null),
            'setStatItemName' => array('type' => 'report', 'id' => null, 'name' => null),
            'adjustStatOrder' => array('type' => null, 'id' => null, 'parent_id' => null, 'after_id' => null),
            'adjustDataList' => array('type' => null, 'r_id' => null, 'id' => array())
        );
    }

    public function index($aUserParameters)
    {
        $this->display('gamecustom/manage.html');
    }

    /**
     * 获取统计项信息
     */
    public function getStatItem($aUserParameters)
    {
        $aUserParameters['r_id'] = (int)$aUserParameters['r_id'];
        // diy
        if ($aUserParameters['type'] === 'diy') {
            $model = new gamecustom_Diy();
            $model->diy_id = $aUserParameters['r_id'];
            $aInfo = $model->findById();
            if ($aInfo) {
                $aInfo = $aInfo[0];
                (new common_Game())->checkGameManageAuth($aInfo['game_id']);
                $oHandler = gamecustom_diy_Base::instance($aInfo['r_type']);
                $aInfo = array_merge($aInfo, $oHandler->getConfiguration($aInfo));
            }
            $this->ajax(0, $aInfo);
        }

        // report
        $model = new common_Report();
        $aInfo = $model->getStatItemById($aUserParameters['r_id']);
        if ($aInfo) {
            (new common_Game())->checkGameManageAuth($aInfo['game_id']);
            $aInfo = $model->setCategory($aInfo);
            $oHandler = gamecustom_report_Base::instance($aInfo['r_type']);
            $aInfo = array_merge($aInfo, $oHandler->getConfiguration($aInfo));
        }
        $this->ajax(0, $aInfo);
    }

    /**
     * 设置统计项信息
     */
    public function setStatItem($aUserParameters)
    {
        $aUserParameters['r_id'] = (int)$aUserParameters['r_id'];
        $model = new common_Report();
        $aInfo = $model->getStatItemById($aUserParameters['r_id']);
        TMValidator::ensure($aInfo, TM::t('tongji', '统计项不存在！'));
        (new common_Game())->checkGameManageAuth($aInfo['game_id']);
        $aInfo = $model->setCategory($aInfo);
        $oHandler = gamecustom_report_Base::instance($aInfo['r_type']);
        $this->ajax(0, $oHandler->setConfiguration($aInfo, $aUserParameters));
    }

    /**
     * 保存自定义加工项
     */
    public function saveDiy($aUserParameters)
    {
        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            $model = new gamecustom_Diy();
            $model->attributes = $aUserParameters;
            $diyInfo = $model->saveDiy($aUserParameters);
            $transaction->commit();
            $this->ajax(0, $diyInfo);
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 游戏自定义获取结点下统计项
     */
    public function getStatItemByNodeId($aUserParameters)
    {
        $aUserParameters['game_id'] = (int)$aUserParameters['game_id'];
        $aUserParameters['node_id'] = (int)$aUserParameters['node_id'];
        if ($aUserParameters['node_id'] < 0) {
            $aUserParameters['node_id'] = 0;
        }

        // report
        $model = new common_Report();
        $aReportList = $model->getStatItemList([
            'node_id' => $aUserParameters['node_id'],
            'game_id' => $aUserParameters['game_id'],
            'status' => 0
        ], [], 'r.display_order, CONVERT(r.report_name USING gb2312)');
        foreach ($aReportList as &$value) {
            $value = $model->setCategory($value);
        }

        // diy
        $model = new gamecustom_Diy();
        $aDiyList = $model->getStatItemList([
            'node_id' => $aUserParameters['node_id'],
            'game_id' => $aUserParameters['game_id'],
            'status' => 0
        ], [], 'r.display_order, CONVERT(r.diy_name USING gb2312)');

        $this->ajax(0, array_merge($aReportList, $aDiyList));
    }

    /**
     * 统计项重命名，包括游戏自定义、游戏自定义加工项
     */
    public function setStatItemName($aUserParameters)
    {
        if ($aUserParameters['type'] === 'diy') {
            $model = new gamecustom_Diy();
        } else {
            $model = new common_Report();
        }
        $this->ajax(0, $model->setName($aUserParameters));
    }

    /**
     * 删除统计项
     */
    public function removeStatItem($aUserParameters)
    {
        $aUserParameters['r_list'] = (array)$aUserParameters['r_list'];
        $diyModel = new gamecustom_Diy();

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            foreach ($aUserParameters['r_list'] as $info) {
                if (!isset($info['type'], $info['r_id'])) continue;
                if ($info['type'] === 'diy') {
                    $diyModel->diy_id = $info['r_id'];
                    $aInfo = $diyModel->findById();
                    if ($aInfo) {
                        $aInfo = $aInfo[0];
                        (new common_Game())->checkGameManageAuth($aInfo['game_id']);
                        $diyModel->removeDiy();
                    }
                } else { // report
                    // TODO
                }
            }
            $transaction->commit();
            $this->ajax(0);
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 调整统计项顺序
     */
    public function adjustStatOrder($aUserParameters)
    {
        $statModel = $this->createStatModel($aUserParameters['type']);

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            $statModel->adjustDisplayOrder($aUserParameters);
            $transaction->commit();
            $this->ajax(0);
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 调整分布及item数据
     */
    public function adjustDataList($aUserParameters)
    {
        $statModel = $this->createStatModel($aUserParameters['type']);

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            $statModel->adjustDataList($aUserParameters);
            $transaction->commit();
            $this->ajax(0);
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 根据类型创建Model
     *
     * @param  string  $type
     * @return TMModel
     */
    protected function createStatModel($type)
    {
        switch ($type) {
            case 'report':
                return new common_Report();
                break;

            case 'diy':
                return new gamecustom_Diy();
                break;

            default:
                TMValidator::ensure(false, TM::t('tongji', '统计项类型不合法！'));
                break;
        }
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
}

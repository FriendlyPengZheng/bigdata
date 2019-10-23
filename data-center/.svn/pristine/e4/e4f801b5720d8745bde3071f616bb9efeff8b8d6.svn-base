<?php
class Stat extends common_Base
{
    public function actions()
    {
        return array(
            'getIndicators' => array('game_id' => null, 'type' => array('report', 'set')),
            'getSettings'   => array('type' => 'report', 'r_id' => null)
        );
    }

    /**
     * 获取指标，包括游戏分析、游戏自定义、游戏自定义加工项，可通过传入type筛选
     */
    public function getIndicators($aUserParameters)
    {
        $aUserParameters['game_id'] = (int)$aUserParameters['game_id'];
        $aUserParameters['type'] = (array)$aUserParameters['type'];
        $aAuth = TM::app()->getUser()->getAuthority();
        $aIndicators = array();

        if (in_array('set', $aUserParameters['type'])) {
            $model = new module_Set();
            $model->game_id = $aUserParameters['game_id'];
            $aSet = $model->findByGameId();
            foreach ($aSet as $idx => &$set) {
                if ($set['auth_id'] && !isset($aAuth[$set['auth_id']])) {
                    unset($aSet[$idx]);
                } else {
                    $set['set_name'] = $set['r_name'] = TM::t('tongji', $set['set_name']);
                    unset($set['auth_id']);
                }
            }
            $aIndicators = array_merge($aIndicators, $aSet);
        }

        $customAuthId = TM::app()->navigator->getAuthIdByNaviKey(array('gamecustom'));
        if (!$customAuthId || isset($aAuth[$customAuthId])) {
            if (in_array('report', $aUserParameters['type'])) {
                $model = new common_Report();
                $aIndicators = array_merge($aIndicators, $model->getStatItemList(
                    array('game_id' => $aUserParameters['game_id'], 'status' => 0),
                    array('is_basic' => 0, 'hide' => 0)
                ));
            }
            if (in_array('diy', $aUserParameters['type'])) {
                $model = new gamecustom_Diy();
                $model->game_id = $aUserParameters['game_id'];
                $aIndicators = array_merge($aIndicators, $model->findByGameId());
            }
        }

        $this->ajax(0, $aIndicators);
    }

    /**
     * 获取指标下的数据
     */
    public function getSettings($aUserParameters)
    {
        switch ($aUserParameters['type']) {
            case 'set':
                $model = new module_SetData();
                $model->set_id = $aUserParameters['r_id'];
                $this->ajax(0, $model->formatList($model->getList()));
                break;

            case 'diy':
                $model = new gamecustom_DiyData();
                $model->diy_id = $aUserParameters['r_id'];
                $this->ajax(0, $model->findByDiyId());
                break;

            default:
                $model = new common_DataInfo();
                $this->ajax(0, $model->getRangeByRid($aUserParameters));
                break;
        }
    }
}

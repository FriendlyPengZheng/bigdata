<?php
class Game extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(),
            'getGameType' => array(),
            'getStatus' => array(),
            'getFuncMask' => array(),
            'save' => array(
                'game_id' => null,
                'game_name' => null,
                'game_type' => null,
                'auth_id' => null,
                'manage_auth_id' => null,
                'status' => null,
                'ignore' => null,
                'func_slot' => null,
                'online_auth_id' => null,
                'game_email' => null
            ),
            'setStatus' => array(
                'game_id' => null,
                'status' => null
            ),
            'getGameList' => array()
        );
    }

    /**
     * 游戏管理页
     */
    public function index($aUserParameters)
    {
        $model = new common_Game();
        $this->assign('games', $model->getFormattedList());
        $this->display('conf/games.html');
    }

    /**
     * 类型
     */
    public function getGameType($aUserParameters)
    {
        $model = new common_Game();
        $this->ajax(0, $model->getGameType());
    }

    /**
     * 状态
     */
    public function getStatus($aUserParameters)
    {
        $model = new common_Game();
        $this->ajax(0, $model->getStatus());
    }

    /**
     * 功能掩码
     */
    public function getFuncMask($aUserParameters)
    {
        $model = new common_Game();
        $this->ajax(0, $model->getFuncMask());
    }

    /**
     * 保存游戏配置
     */
    public function save($aUserParameters)
    {
        $aUserParameters['game_id'] = (int)$aUserParameters['game_id'];
        $model = new common_Game();
        $aGame = $model->findAll(array(
            'condition' => array(
                'game_id' => $aUserParameters['game_id']
            )
        ));

        $model->attributes = $aUserParameters;
        if ($aGame) {
            if($isUpdated = $model->update()) {
                TM::app()->getLog()->log('success!' , TMLog::TYPE_INFO);
            }
            else {
                TM::app()->getLog()->log('failed!' , TMLog::TYPE_INFO);
            }
            $this->ajax(0, $isUpdated);
        }
        $this->ajax(0, $model->insert());
    }

    /**
     * 设置状态
     */
    public function setStatus($aUserParameters)
    {
        $model = new common_Game();
        $model->attributes = $aUserParameters;
        $this->ajax(0, $model->update(array('status')));
    }

    /**
     * 获取用户所有的权限列表
     */
    public function getGameList($aUserParameters)
    {
        $this->ajax(0, array_values((new common_Game())->getIdGroupedGameByAuth()));
    }
}

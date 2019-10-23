<?php
class Signtransext extends common_Base
{
    public function actions()
    {
        $signtransGame = $this->_getSigntransGame();
        $defaultGameId = $signtransGame ? $signtransGame[0]['game_id'] : null;
        return array(
            'multiple' => array(),
            'single'   => array('game_id' => $defaultGameId),
            'realtime' => array('game_id' => $defaultGameId),
            'getSigntransGame' => array()
        );
    }

    public function commonParameters()
    {
        $aDate = $this->getDateMark();
        return array_merge(parent::commonParameters(), array(
            'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
            'to' => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d')
        ));
    }

    public function multiple($aUserParameters)
    {
        $this->display('topic/multiple.html');
    }

    public function single($aUserParameters)
    {
        $this->display('topic/single.html');
    }

    public function realtime($aUserParameters)
    {
        $this->display('topic/realtime.html');
    }

    /**
     * 获取注册转化游戏action
     */
    public function getSigntransGame($aUserParameters)
    {
        $this->ajax(0, $this->_getSigntransGame());
    }

    /**
     * 获取注册转化游戏
     *
     * @return array
     */
    private function _getSigntransGame()
    {
        static $signtransGame;

        if (!isset($signtransGame)) {
            $signtransGame = (new common_Game())->findByFuncMaskWithGpzs(common_Game::SIGNTRANS_MASK);
        }

        return $signtransGame;
    }

    protected function beforeRunAction()
    {
        // 检查是否有超级管理员权限        
        if (TM::app()->getUser()->isAdmin()) {
            $this->assign('admin_auth', true);
		}
    }
}

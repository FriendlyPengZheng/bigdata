<?php
abstract class common_Overall extends common_Base
{
    public function actions()
    {
        $aDate = $this->getDateMark();
        return array(
            'index' => array(
                'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
                'to' => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d')
            )
        );
    }

    public function index($aUserParameters)
    {
        $iGameId = $this->getGameId();
        $aAuthGame = (new common_Game())->getIdGroupedGameByAuth();
        if (!isset($aAuthGame[$iGameId])) {
            TM::app()->getUser()->setAuthorized(false)->forbidden();
        }

        $this->assign('param', [
            'title' => $this->getPageTitle(),
            'game_id' => $aAuthGame[$iGameId]['game_id'],
            'gpzs_id' => $aAuthGame[$iGameId]['gpzs_id']
        ], true);

        $this->display('public/period.html');
    }

    abstract protected function getPageTitle();
    abstract protected function getGameId();
}

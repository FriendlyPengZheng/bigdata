<?php
abstract class common_Partial extends common_Base
{
    public function actions()
    {
        $aDate = $this->getDateMark();
        $gameList = $this->findGameList();
        $defaultGameId = $gameList ? $gameList[0]['game_id'] : null;
        return array(
            'index' => array(
                'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
                'to' => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d'),
                'game_id' => $defaultGameId
            ),
            'getGameList' => array()
        );
    }

    public function index($aUserParameters)
    {
        $this->assign('param', [
            'title' => $this->getPageTitle(),
            'gamelist_r' => $this->getGameListRoute()
        ], true);

        $this->display('public/period_game.html');
    }

    public function getGameList($aUserParameters)
    {
        $this->ajax(0, $this->findGameList());
    }

    protected function findGameList()
    {
        static $gameList;

        if (!isset($gameList)) {
            $games = (new common_Game())->findByFuncMaskWithGpzs($this->getGameFuncMask());
            $authIds = TM::app()->getUser()->getAuthority();
            foreach ($games as $key => $game) {
                if (!isset($authIds[$game['auth_id']])) {
                    unset($games[$key]);
                }
            }
            $gameList = array_values($games);
        }

        return $gameList;
    }

    protected function getGameListRoute()
    {
        $reflector = new ReflectionClass(get_class($this));
        $filename = $reflector->getFileName();
        $path = TM::app()->getControllerPath();
        return strstr(substr($filename, strlen($path) + 1), '.php', true) . DS . 'getGameList';
    }

    abstract protected function getPageTitle();
    abstract protected function getGameFuncMask();
}

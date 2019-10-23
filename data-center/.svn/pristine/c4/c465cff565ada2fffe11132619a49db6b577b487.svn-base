<?php
abstract class common_Page extends common_Base
{
    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aUserParameters)
    {
        parent::beforeRunAction($aUserParameters);
        // 检查游戏权限
        if (isset($aUserParameters['game_id']) && $aUserParameters['game_id']) {
            // 记录平台（渠道）、区服、gpzs_id参数
            $aGpzsInfo = TM::app()->session->get('gpzs_info', array());
            foreach (array('platform_id', 'zone_id', 'server_id') as $key) {
                if (isset($aUserParameters[$key])) {
                    $aGpzsInfo[$key] = $aUserParameters[$key];
                }
            }
            TM::app()->session->add('game_id', $aUserParameters['game_id']);
            TM::app()->session->add('gpzs_info', $aGpzsInfo);

            // 检查当前链接是否正确
            if (!TM::app()->getHttp()->isAjaxRequest()) {
                $navigator = TM::app()->navigator->getNavigator();
                $games = $navigator['game'];
                $gameInfo = $games[$aUserParameters['game_id']];
                $r = TM::app()->getCurrentRoute();
                if (false !== strpos($gameInfo['url'], $gameInfo['game_type']) && false === strpos($r, $gameInfo['game_type'])) {
                    TM::app()->getHttp()->redirect($gameInfo['url']);
                }
            }
        }
    }

    /**
     * @brief getAuthGame 
     * 获取有权限的游戏列表
     *
     * @return {array}
     */
    abstract protected function getAuthGame();

    /**
     * 获取默认游戏ID
     * @return array
     */
    protected function getDefaultGameId()
    {
        $aAuthGame = $this->getAuthGame();
        $iGameId = TM::app()->session->get('game_id');
        if (!$iGameId || !isset($aAuthGame[$iGameId])) {
            $iGameId = key($aAuthGame);
        }
        return $iGameId;
    }
}

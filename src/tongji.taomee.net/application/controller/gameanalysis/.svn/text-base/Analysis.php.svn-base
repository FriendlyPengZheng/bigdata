<?php
abstract class gameanalysis_Analysis extends common_Page
{
    /**
     * @var string 游戏类型
     */
    protected $sGameType = 'mobilegame';

    public function actions()
    {
        return array_merge(parent::actions(), array(
                               'index' => array(),
                               'month' => array(),
                               'userleft' => array()));
    }

    /**
     * 接收公共参数
     * @return array
     */
    public function commonParameters()
    {
        $aDate = $this->getDateMark();
        $aGpzsInfo = TM::app()->session->get('gpzs_info', array());
        return array_merge(parent::commonParameters(), array(
            'from'        => isset($aDate['from']) ? $aDate['from'] : date('Y-m-d', strtotime('-30 day')),
            'to'          => isset($aDate['to']) ? $aDate['to'] : date('Y-m-d'),
            'period'      => data_time_PeriodFactory::TYPE_DAY,
            'game_id'     => $this->getDefaultGameId(),
            'platform_id' => isset($aGpzsInfo['platform_id']) ? $aGpzsInfo['platform_id'] : null,
            'zone_id'     => isset($aGpzsInfo['zone_id']) ? $aGpzsInfo['zone_id'] : null,
            'server_id'   => isset($aGpzsInfo['server_id']) ? $aGpzsInfo['server_id'] : null,
            'export'      => 0,
            'file_name'   => null
        ));
    }

    /**
     * 设置通用显示项
     * @param array $aUserParameters
     */
    protected function assignCommon($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->attributes = $aUserParameters;
        $model->platform_id = null;
        $aPlatform = $model->getPlatform();
        $this->assign('platform', $aPlatform);
        // ignore
        $this->assignIgnore($aUserParameters);
        // access token
        // $this->assignToken();
    }

    /**
     * 设置忽略项
     * @param array $aUserParameters
     */
    protected function assignIgnore($aUserParameters)
    {
        $aGameList = (new common_Game())->getIdGroupedGameByAuth(common_Game::GAMEANALYSIS_MASK);
        $aIgnore = array();
        if (isset($aUserParameters['game_id']) && isset($aGameList[$aUserParameters['game_id']])) {
            $aIgnore = array_filter(explode('_', $aGameList[$aUserParameters['game_id']]['ignore']));
        }
        $this->assign('ignore', $aIgnore);

        // 检查是否有超级管理员权限        
        if (TM::app()->getUser()->isAdmin()) {
            $this->assign('admin_auth', true);
		}
    }

    /**
     * 设置数据令牌
     */
    protected function assignToken()
    {
        $oToken = TM::createComponent(array(
            'class' => 'application.components.Token',
            'namespace' => TM::app()->getUser()->getUserId() . '@' . TM::app()->getCompleteRoute()
        ));
        $this->assign('token', $oToken->generate());
    }

    /**
     * 默认公共页面（日）
     * @param array $aUserParameters
     */
    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/period_gpzs.html');
    }

    /**
     * 默认公共页面（月）
     * @param array $aUserParameters
     */
    public function month($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/month_gp.html');
    }

    /**
     * 留存率页面（日周月）
     * @param array $aUserParameters
     */
    public function userleft($aUserParameters) 
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/userleft.html');
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
        parent::beforeRunAction($aParameters);
        // 调整游戏类型
        if (isset($aParameters['game_id'])) {
            $aAuthGame = (new common_Game())->getIdGroupedGameByAuth(common_Game::GAMEANALYSIS_MASK);
            // 检查要查看的游戏是否有权限
            if (!isset($aAuthGame[$aParameters['game_id']])) {
                TM::app()->getUser()->setAuthorized(false)->forbidden();
            }
            $this->sGameType = $aAuthGame[$aParameters['game_id']]['game_type'];
        }
    }

    /**
     * @see parent
     */
    protected function getAuthGame()
    {
        return (new common_Game())->getIdGroupedGameByAuth(common_Game::GAMEANALYSIS_MASK);
    }
}

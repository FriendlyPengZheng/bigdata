<?php
abstract class gameanalysis_Gpzsanalysis extends gameanalysis_Analysis
{
    public function actions()
    {
        return array_merge(parent::actions(), array(
            'manage'  => array(),
            'setName' => array(
                'id' => null,
                'name' => null
            ),
            'setHide' => array(
                'id' => null,
                'hide' => 0 // 0:正常 1：下架
            )
        ));
    }

    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/gpzs.html');
    }

    public function manage($aUserParameters)
    {
        $this->display('gameanalysis/without_tools.html');
    }

    public function setName($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->gpzs_id = $aUserParameters['id'];
        $model->gpzs_name = $aUserParameters['name'];
        $this->ajax(0, $model->update(array('gpzs_name')));
    }

    public function setHide($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->gpzs_id = $aUserParameters['id'];
        $model->status = $aUserParameters['hide'];
        $this->ajax(0, $model->update(array('status')));
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
            $aAuthGame = (new common_Game())->getIdGroupedGameByAuth();
            // 权限已经确认过了，这里不再判断，可以挪到Analysis
            $this->sGameType = $aAuthGame[$aParameters['game_id']]['game_type'];
        }
    }
}

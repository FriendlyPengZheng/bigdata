<?php
class Gpzs extends TMController
{
    public function actions()
    {
        return array(
            'getPlatform' => array(
                'game_id' => null,
                'status'  => 0
            ),
            'getZoneServer' => array(
                'game_id' => null,
                'platform_id' => -1,
                'status' => 0
            ),
            'getZoneServerAll' => array(
                'game_id' => null,
                'status' => 0
            )
        );
    }

    public function getPlatform($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->attributes = $aUserParameters;
        $this->ajax(0, $model->getPlatform());
    }

    public function getZoneServer($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->attributes = $aUserParameters;
        $this->ajax(0, $model->getZoneServer());
    }

    public function getZoneServerAll($aUserParameters)
    {
        $model = new common_GpzsInfo();
        $model->game_id = $aUserParameters['game_id'];
        $platforms = $model->getPlatform();
        $list = array();
        foreach ($platforms as $platform) {
            $model->platform_id = $platform['platform_id'];
            $zoneServers = $model->getZoneServer();
            foreach ($zoneServers as $idx => &$zoneServer) {
                if ($zoneServer['zone_id'] === '-1' && $zoneServer['server_id'] === '-1') {
                    unset($zoneServers[$idx]);
                    continue;
                }
                $zoneServer['platform_name'] = $platform['gpzs_name'];
            }
            $list = array_merge($list, $zoneServers);
        }
        $this->ajax(0, $list);
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
        // 检查游戏权限
        if (isset($aParameters['game_id'])) {
            (new common_Game())->checkGameAuth($aParameters['game_id']);
        }
    }
}

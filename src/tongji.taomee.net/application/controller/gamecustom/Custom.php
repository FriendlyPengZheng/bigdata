<?php
abstract class gamecustom_Custom extends common_Page
{
    /**
     * Get custom content list
     *
     * @return array
     */
    protected function getContentList($aUserParameters)
    {
        !isset($aUserParameters['time']) && $aUserParameters['time'] = -1;
        $reportModel = new common_Report();
        $reportContent = $reportModel->getContentList($aUserParameters);
        $diyModel = new gamecustom_Diy();
        $diyContent = $diyModel->getContentList($aUserParameters);

        $content = array();
        foreach ([0, 1] as $isMulti) {
            $content[$isMulti] = array_merge(
                TMArrayHelper::assoc($isMulti, $reportContent, array()),
                TMArrayHelper::assoc($isMulti, $diyContent, array())
            );
        }
        $temp = array();
        if (isset($aUserParameters['time'])) {
            foreach ($content[0] as $val) {
                if($val['add_time'] > $aUserParameters['time']) {
                    $temp[] = $val;
                }
            }
        } 
        $content[0] = $temp;
        return $content;
    }

    /**
     * @brief setExtraListaParam
     * 由于增加了购物车,需要增加额外信息(ifselfhelp,iffavor,inselfhelp,infavor)
     * @param {array} $aParam
     * @param {integer} $gameId
     *
     * @return array
     */
    protected function setExtraListParam($aParam, $gameId)
    {
        $gameInfo = (new common_Game())->getInfoByGameId($gameId);
        $cacheInfo = TM::app()->session->get('record_data');
        if(isset($aParam)) {
            $aParam['ifselfhelp'] = in_array($aParam['ifselfhelp'], array('ucount'))? 1 : 0;
            $aParam['iffavor'] = 1;
            $aParam['inselfhelp'] = 0;
            $aParam['infavor'] = 0;
        }
        $gameName = '';
        if(isset($gameInfo)) {
            $gameName = $gameInfo['game_name'] . '【' .  $this->getGameType()[$gameInfo['game_type']] . '】';
        }
        if(isset($cacheInfo) && isset($cacheInfo[$gameName])) {
            $data = $cacheInfo[$gameName];
            foreach($data[1] as $k => $v) {
                if((int)$v['r_id'] === (int)$aParam['r_id']) {
                    if(isset($aParam['data_id']) && (int)$k === (int)$aParam['data_id']) {
                        $aParam['ifselfhelp'] && $aParam['inselfhelp'] = 1;
                        $aParam['iffavor'] && $aParam['infavor'] = 1;
                    }
                    else if(!isset($aParam['data_id'])) {
                        $aParam['ifselfhelp'] && $aParam['inselfhelp'] = 1;
                        $aParam['iffavor'] && $aParam['infavor'] = 1;
                    }
                }
            }
        }
        return $aParam;
    }

    protected function getGameType()
    {
        return (new common_Game())->getGameTypeName();
    }

    /**
     * @see parent
     */
    protected function getAuthGame()
    {
        return (new common_Game())->getIdGroupedGameByAuth();
    }
}

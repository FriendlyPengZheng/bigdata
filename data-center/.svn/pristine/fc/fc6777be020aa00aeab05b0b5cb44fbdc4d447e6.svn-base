<?php
class Backend extends TMController
{
    /**
     *公共参数
     *@return array
     */
    public function actions()
    {
        return array(
            'index' => array('game_id' => null),
            'getTableList' => array('aside_key' => null),
            'deleteMonitor' => array('delete_type' => null,'ip' => array()),
            'banWarnForIp' => array('ban_type' => null,'fbd_flag' => null,'minutes' => null,'ip' => array()),
            'banWarnForType' => array('ban_type' => null,'fbd_flag' => null,'minutes' => null)
        );
    }


    /**
     * 显示后台监控页面
     */
    public function index($aUserParameters)
    {   
        $moduleType = (int)TM::app()->getActionExtra();
        $this->assign('thead',backend_DataFactory::getDataHead($moduleType));
        if (TM::app()->getUser()->isAdmin()) {
            $this->assign('manage_auth', true);
		}
        $this->display('gameanalysis/backend.html');
    }
    

    /**
     * @brief getTableList 
     * 获取特定监控类型的列表
     *
     * @return {json Array}
     */
    public function getTableList($aUserParameters)
    {   
        $oBackendData = new backend_Data();
        $aReciveData = $oBackendData->getTableList($aUserParameters['aside_key']);
        $this->ajax(0, $aReciveData);
    }

    /**
     * @brief deleteMonitor 
     * 删除监控
     *
     * @return {json Array}
     */
    public function deleteMonitor($aUserParameters)
    {
        $oBackendData = new backend_Data();
        !$aUserParameters['ip'] && $this->ajax(-1);
        !is_array($aUserParameters['ip']) && $aUserParameters['ip'] = array($aUserParameters['ip']);
        $aIp = array_filter(array_unique($aUserParameters['ip'])); 
        $oBackendData->deleteMonitor($aUserParameters['delete_type'],$aIp[0]);
        $this->ajax(0);
    }


    /**
     * @brief banWarnForIp 
     * 批量/单个禁止告警,开启告警
     *
     * @return {json Array}
     */
    public function  banWarnForIp($aUserParameters)
    {   
        $oBackendData = new backend_Data();
        $iFbdFlag = null;
        if($aUserParameters['fbd_flag']){
            $iFbdFlag = 128;
        }
        else{
            $iFbdFlag = 0;
        }
        !$aUserParameters['ip'] && $this->ajax(-1);
        !is_array($aUserParameters['ip']) && $aUserParameters['ip'] = array($aUserParameters['ip']);
        $aIp = array_filter(array_unique($aUserParameters['ip']));
        foreach($aIp as $ip){
            $oBackendData->forbiddenAlarm($aUserParameters['ban_type'],$iFbdFlag,$ip,$aUserParameters['minutes']);
        }
        $this->ajax(0);
    }


    /**
     * @brief banWarnForType 
     * 类型禁止告警,开启告警
     *
     * @return {json Array}
     */
    public function banWarnForType($aUserParameters)
    {   
        $oBackendData = new backend_Data();
        $iFbdFlag = null;
        if($aUserParameters['fbd_flag']){
            $iFbdFlag = 192;
        }
        else{
            $iFbdFlag = 64;
        }
        $oBackendData->forbiddenAlarm($aUserParameters['ban_type'],$iFbdFlag,null,$aUserParameters['minutes']);
        $this->ajax(0);
    }

}
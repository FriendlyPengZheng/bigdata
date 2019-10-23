<?php
class Kpi extends common_Base
{
    /**
     * Actions
     */
    public function actions()
    {
        return array(
            'index'  => array()
        );
    }

    /**
     * 接收公共参数
     * @return array
     */
    public function commonParameters()
    {
        $aDate = $this->getDateMark('m');
        return array_merge(parent::commonParameters(), array(
            'from' => isset($aDate['from']) ? $aDate['from'] : date('Y-m', strtotime('-1 year')),
            'to' => isset($aDate['to']) ? $aDate['to'] : date('Y-m')
        ));
    }

    public function index($aUserParameters)
    {
        $this->display('topic/kpi.html');
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters) 
    { 
        // 检查是否有超级管理员权限        
        if (TM::app()->getUser()->isAdmin()) {
            $this->assign('admin_auth', true);
		}
    }
}

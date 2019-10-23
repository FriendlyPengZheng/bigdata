<?php
abstract class gameanalysis_Realtime extends gameanalysis_Analysis
{
    /**
     * 公共参数
     * @return array
     */
    public function commonParameters()
    {
        return array_merge(parent::commonParameters(), array(
            'from' => date('Y-m-d'),
            'to' => date('Y-m-d')
        ));
    }

    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/realtime.html');
    }
}

<?php
abstract class gameanalysis_Board extends gameanalysis_Analysis
{
    public function commonParameters()
    {
        return array_merge(parent::commonParameters(), array(
            'from' => date('Y-m-d', strtotime('-1day')),
            'to' => date('Y-m-d', strtotime('-1day'))
        ));
    }

    public function index($aUserParameters)
    {
        $this->assignCommon($aUserParameters);
        $this->display('gameanalysis/board.html');
    }
}

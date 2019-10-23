<?php
class gameanalysis_WhaleUserInfo extends TMFormModel
{
    public function tableName()
    {
        return 'v_whale_user';
    }

    public function getList($aUserParam)
    {
        return $this->findAll(array(
            'condition' => array(
                'game_id'     => $aUserParam['game_id'],
                'platform_id' => $aUserParam['platform_id'],
                'account_id'  => $aUserParam['account_id']
            )
        ));
    }
}

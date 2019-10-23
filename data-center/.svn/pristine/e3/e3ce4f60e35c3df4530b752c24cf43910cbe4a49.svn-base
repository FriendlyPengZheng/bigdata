<?php
class home_Home extends common_Base
{
    /**
     * Get metadatas for collect.
     *
     * @param  array &$aUserParameters
     * @return array
     */
    protected function getMetadata($aUserParameters)
    {
        $aUserParameters['user_id'] = TM::app()->getUser()->getUserId();
        $model = new home_Collect();
        $aCollect = $model->checkCollectExists($aUserParameters);
        //if ($aCollect['user_id'] != $aUserParameters['user_id']) {
            //$model = new home_SharedCollect();
            //$aCollect = $model->findByCollectId($aUserParameters['collect_id'], $aUserParameters['user_id']);
            //TMValidator::ensure($aCollect, TM::t('tongji', '小部件不存在！'));
        //}
        unset($aCollect);
        $model = new home_Metadata();
        return $model->getListByCollectId($aUserParameters['collect_id']);
    }
}

<?php
class home_SharedFavorCollect extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_share_favor_collect';
    }

    /**
     * Check if the collect exists.
     * @param array $aUserParam
     * @return array the collect info if it exists
     */
    public function checkCollectExists($aUserParam)
    {
        return $this->getDb()->createCommand()
            ->select('collect_id, favor_id, user_id')
            ->from($this->tableName())
            ->where('collect_id = ?')
            ->andWhere('user_id = ?')
            ->queryRow(array($aUserParam['collect_id'], $aUserParam['user_id']));
    }

    /**
     * Get collect list by favor_id.
     *
     * @param  integer $iFavorId
     * @param  integer $iUserId
     * @param  boolean $bCountMetadata
     * @return array
     */
    public function getListByFavorId($iFavorId, $iUserId = null, $bCountMetadata = false)
    {
        $sCondition = 'c.favor_id = ?';
        $aParam = array($iFavorId);
        if (isset($iUserId)) {
            $sCondition .= ' AND c.user_id <> ?';
            $aParam[] = $iUserId;
        }

        $oCommand = $this->getDb()->createCommand()
            ->select('c.collect_id,c.collect_name,c.favor_id,c.draw_type,c.user_id,a.calc_option,a.calculateRow_option')
            ->from('t_web_collect c')
            ->leftJoin($this->tableName() . ' a', 'a.collect_id = c.collect_id')
            ->where($sCondition);

        if ($bCountMetadata) {
            $oCommand->select($oCommand->getSelect() . ',COUNT(1) AS metadata_cnt')
                ->leftJoin((new home_Metadata())->tableName() . ' m', 'c.collect_id=m.collect_id')
                ->group('c.collect_id');
        }

        return $oCommand->order('c.collect_id')->queryAll($aParam);
    }
}

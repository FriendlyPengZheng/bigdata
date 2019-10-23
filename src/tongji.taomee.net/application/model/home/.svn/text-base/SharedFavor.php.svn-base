<?php
class home_SharedFavor extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_share_favor';
    }

    /**
     * Set the favor as the default one.
     * @param array $aUserParam
     * @return boolean
     */
    public function setDefault($aUserParam)
    {
        $this->favor_id = $aUserParam['favor_id'];
        $this->user_id = $aUserParam['user_id'];
        $this->is_default = 1;
        return $this->update(array('is_default'));
    }

    /**
     * Cancel the favor as the default one.
     * @param array $aUserParam
     * @return boolean
     */
    public function cancelDefault($aUserParam)
    {
        $this->favor_id = $aUserParam['favor_id'];
        $this->user_id = $aUserParam['user_id'];
        $this->is_default = 0;
        return $this->update(array('is_default'));
    }

    /**
     * Get favor list by user_id.
     * @param integer $iUserId
     * @param integer $iFavorType
     * @return array
     */
    public function getListByUserId($iUserId, $iFavorType = null)
    {
        $sCondition = 'a.user_id = ?';
        $aParam = array($iUserId);
        if (isset($iFavorType)) {
            $sCondition .= ' AND c.favor_type = ?';
            $aParam[] = $iFavorType;
        }
        return $this->getDb()->createCommand()
            ->select('c.favor_id, c.favor_name, c.favor_type, c.layout, c.game_id, a.is_default')
            ->from($this->tableName() . ' a')
            ->join('t_web_favor c', 'a.favor_id = c.favor_id')
            ->where($sCondition)
            ->order('c.favor_id')
            ->queryAll($aParam);
    }

    /**
     * Update favor info by user_id.
     * @param integer $iUserId
     * @param array $aFields
     * @return boolean
     */
    public function updateFavorByUserId($iUserId, $aFields)
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            $aFields,
            'user_id = :user_id',
            array(':user_id' => $iUserId)
        );
    }
}

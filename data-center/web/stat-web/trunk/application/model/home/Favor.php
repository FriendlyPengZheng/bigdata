<?php
class home_Favor extends TMFormModel
{
    const TYPE_SINGLE_GAME = 1;
    const TYPE_MIXED_GAME = 2;

    const LAYOUT_100 = 1;
    const LAYOUT_50 = 2;

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_favor';
    }

    /**
     * @brief rules
     * 获取参数检测的配置
     *
     * @return {array}
     */
    public function rules()
    {
        return array(
            array('favor_name', 'string', 'min' => 1, 'max' => 32),
            array('favor_type,layout', 'number', 'min' => 1, 'max' => 2, 'integerOnly' => true)
        );
    }

    /**
     * Add a favor.
     * @param array $aUserParam
     * @return integer
     */
    public function add($aUserParam)
    {
        $this->attributes = $aUserParam;
        $this->validate(array('favor_name', 'favor_type', 'layout'));
        if ($aUserParam['favor_type'] == self::TYPE_SINGLE_GAME) {
            (new common_Game())->checkGameAuth($aUserParam['game_id']);
        } else {
            $this->game_id = 0;
        }
        $this->insert();
        return $this->favor_id;
    }

    /**
     * Update the favor info.
     * @param array $aUserParam
     * @return boolean
     */
    public function set($aUserParam)
    {
        $this->attributes = $aUserParam;
        $this->checkFavorExists($aUserParam);
        return $this->update(array('favor_name', 'layout'));
    }

    /**
     * Delete the favor, also its collects and metadatas.
     * @param array $aUserParam
     * @return boolean
     */
    public function deleteFavor($aUserParam)
    {
        $this->checkFavorExists($aUserParam);
        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->favor_id = $aUserParam['favor_id'];
            $this->delete();
            $oCollect  = new home_Collect();
            $shared    = new home_SharedCollect();
            $oMetadata = new home_Metadata();
            $aCollect = $oCollect->getListByFavorId($aUserParam['favor_id']);
            foreach ($aCollect as $collect) {
                $oCollect->collect_id = $collect['collect_id'];
                $oCollect->delete();
                $oMetadata->deleteMetadataByCollectId($collect['collect_id']);
                $shared->deleteAllByAttributes(array(
                    'collect_id' => $collect['collect_id']
                ));
            }

            $shared->deleteAllByAttributes(array(
                'favor_id' => $aUserParam['favor_id']
            ));
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Set the favor as the default one.
     * @param array $aUserParam
     * @return boolean
     */
    public function setDefault($aUserParam)
    {
        $this->favor_id = $aUserParam['favor_id'];
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
        $sCondition = 'user_id = ?';
        $aParam = array($iUserId);
        if (isset($iFavorType)) {
            $sCondition .= ' AND favor_type = ?';
            $aParam[] = $iFavorType;
        }
        return $this->getDb()->createCommand()
            ->select('favor_id, favor_name, favor_type, layout, game_id, is_default')
            ->from($this->tableName())
            ->where($sCondition)
            ->order('favor_id')
            ->queryAll($aParam);
    }

    /**
     * Add a default favor.
     * @param integer $iUserId
     * @return array the added favor info.
     */
    public function addDefaultFavor($iUserId)
    {
        $aDefaultFavor = $this->defaultFavor();
        $aDefaultFavor['user_id'] = $iUserId;
        $this->attributes = $aDefaultFavor;
        $this->insert();
        $aDefaultFavor['favor_id'] = $this->favor_id;
        return $aDefaultFavor;
    }

    /**
     * @brief defaultFavor
     *
     * @return
     */
    protected function defaultFavor()
    {
        $aDefaultFavor = array(
            'favor_name' => TM::t('tongji', '我的收藏'),
            'layout' => self::LAYOUT_100,
            'is_default' => 0
        );
        $aAuthGameList = (new common_Game())->getIdGroupedGameByAuth();
        if ($aAuthGameList) {
            // use the first authorized game as default
            $aDefaultFavor['favor_type'] = self::TYPE_SINGLE_GAME;
            $aDefaultFavor['game_id'] = key($aAuthGameList);
        } else {
            $aDefaultFavor['favor_type'] = self::TYPE_MIXED_GAME;
        }
        return $aDefaultFavor;
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

    /**
     * Check if the favor exists.
     * @param array $aUserParam
     * @return array the favor info if it exists
     */
    public function checkFavorExists($aUserParam, $checkedUser=true)
    {
        $aFavor = $this->getFavorById($aUserParam['favor_id']);
        if ($checkedUser) {
            TMValidator::ensure($aFavor && $aFavor['user_id'] == $aUserParam['user_id'], TM::t('tongji', '收藏不存在！'));
        }
        return $aFavor;
    }

    /**
     * Get favor info by favor_id.
     * @param integer $iFavorId
     * @return array
     */
    public function getFavorById($iFavorId)
    {
        return $this->getDb()->createCommand()
            ->select('favor_id, favor_name, favor_type, layout, game_id, user_id')
            ->from($this->tableName())
            ->where('favor_id = ?')
            ->queryRow(array($iFavorId));
    }

    /**
     * @brief fetchSharedFavor 
     * 获取指定用户的默认分享收藏
     *
     * @param {integer} $userId
     * @param {integer} $favorId
     *
     * @return {array}
     */
    public function fetchSharedFavor($userId, $favorId)
    {
        $module = $this->getFavorById($favorId);

        $defaultSharedName = '分享收藏';

        $favor = $this->getDb()->createCommand()
            ->select('favor_id, favor_name, favor_type, layout, game_id, user_id')
            ->from($this->tableName())
            ->where('favor_name = ?')
            ->andWhere('favor_type = ?')
            ->andWhere('user_id = ?')
            ->queryRow(array($defaultSharedName, $module['favor_type'], $userId));
        if ($favor) return $favor;

        unset($module['favor_id']);
        $module['user_id'] = $userId;
        $module['favor_name'] = $defaultSharedName;
        $module['favor_id'] = $this->add($module);
        return $module;
    }
}

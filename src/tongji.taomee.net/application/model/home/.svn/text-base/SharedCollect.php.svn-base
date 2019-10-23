<?php
class home_SharedCollect extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_shared_collect';
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
            array('collect_name', 'string', 'min' => 1, 'max' => 255),
            array('draw_type', 'number', 'min' => 1, 'max' => 5, 'integerOnly' => true)
        );
    }

    /**
     * Get collect list by favor_id.
     *
     * @param  integer $iFavorId
     * @return array
     */
    public function getListByFavorId($iFavorId)
    {
        return $this->getDb()->createCommand()
            ->select('c.collect_id,c.collect_name,d.favor_id,c.draw_type,c.user_id,c.calc_option, COUNT(1) AS metadata_cnt, d.calc_option, d.calculateRow_option')
            ->from($this->tableName() . ' d')
            ->join('t_web_collect AS c', 'd.collect_id = c.collect_id')
            ->leftJoin((new home_Metadata())->tableName() . ' m', 'c.collect_id=m.collect_id')
            ->where('d.favor_id = ?')
            ->group('c.collect_id')
            ->order('c.collect_id')
            ->queryAll(array($iFavorId));
    }

    /**
     * Move this collect to other favor.
     * @param array $aUserParam
     * @return boolean
     */
    public function move($aUserParam)
    {
        $aCollect = $this->findByCollectId($aUserParam['collect_id'], $aUserParam['user_id']);
        if ($aUserParam['favor_id'] == $aCollect['favor_id']) {
            return;
        }
        $oFavor = new home_Favor();
        $aCurrent = $oFavor->checkFavorExists(array_merge($aUserParam, array('favor_id' => $aCollect['favor_id'])));
        $aForward = $oFavor->checkFavorExists($aUserParam);
        TMValidator::ensure(
            $aCurrent['favor_type'] === $aForward['favor_type'],
            TM::t('tongji', '移向收藏与小部件所在收藏类型不一致！')
        );
        if ($aCurrent['favor_type'] == home_Favor::TYPE_SINGLE_GAME) {
            TMValidator::ensure(
                $aCurrent['game_id'] === $aForward['game_id'],
                TM::t('tongji', '移向收藏与小部件所在收藏游戏类型不一致！')
            );
        }
        return $this->getDb()->createCommand()
            ->update($this->tableName(), array(
                'favor_id' => $aUserParam['favor_id']
            ), array(
                'collect_id' => $aCollect['collect_id'],
                'favor_id' => $aCollect['favor_id']
            ));
    }

    /**
     * @brief findByCollectId 
     * 通过collectId获取信息
     *
     * @param {integer} $collectId
     *
     * @return {null|array}
     */
    public function findByCollectId($collectId, $userId)
    {
        return $this->getDb()->createCommand()
            ->select('*')
            ->from($this->tableName())
            ->where('collect_id = ?')
            ->andWhere('user_id = ?')
            ->queryRow(array($collectId, $userId));
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
     * @param  integer $iFavorId
     * @return array
     */
    public function getListByUserId($userId)
    {
        return $this->getDb()->createCommand()
            ->select('c.favor_id,c.favor_name,c.favor_type,c.layout,c.game_id, c.game_id, c.is_default')
            ->from($this->tableName() . ' d')
            ->join('t_web_favor AS c', 'd.favor_id = c.favor_id')
            ->where('d.user_id = ?')
            ->andWhere('d.collect_id = 0')
            ->queryAll(array($userId));
    }
}

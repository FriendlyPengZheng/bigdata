<?php
class gameanalysis_Gametask extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_gametask_info';
    }

    /**
     * 取游戏任务列表
     * @param array $aUserParam
     * @return array
     */
    public function getList($aUserParam)
    {
        return $this->getDb()->createCommand()
            ->select('sstid AS id,gametask_name AS name,hide')
            ->from($this->tableName())
            ->where('type = ? AND game_id = ?')
            ->queryAll(array($aUserParam['type'], $aUserParam['game_id']));
    }

    /**
     * 变更名称
     * @param array $aUserParam
     * @return array
     */
    public function setName($aUserParam)
    {
        $aInfo = $this->exists($aUserParam);
        $aInfo['gametask_name'] = $aUserParam['name'];
        return $this->replace($aInfo);
    }

    /**
     * 设置隐藏或显示
     * @param array $aUserParam
     * @return array
     */
    public function setHide($aUserParam)
    {
        $aInfo = $this->exists($aUserParam);
        $aInfo['hide'] = $aUserParam['hide'];
        return $this->replace($aInfo);
    }

    /**
     * Replace the gametask.
     *
     * @param  array   $aFields Gametask info
     * @return integer          Effect rows
     */
    public function replace($aFields)
    {
        return $this->getDb()->createCommand(
                'REPLACE INTO ' . $this->tableName() .
                ' (`type`, game_id, sstid, gametask_name, `order`, hide) VALUES (?, ?, ?, ?, ?, ?)')
            ->execute(array(
                $aFields['type'], $aFields['game_id'],
                $aFields['sstid'], $aFields['gametask_name'], $aFields['order'], $aFields['hide']
            ));
    }

    /**
     * 游戏任务是否存在
     * @param array $aUserParam
     * @return array the exist gametask info
     * @throw TMValidatorException if not exist
     */
    protected function exists($aUserParam)
    {
        $aInfo = $this->findAll(array(
            'condition' => array(
                'type' => $aUserParam['type'],
                'game_id' => $aUserParam['game_id'],
                'sstid' => $aUserParam['id']
            )
        ));
        TMValidator::ensure($aInfo, TM::t('tongji', '游戏任务不存在！'));
        return $aInfo[0];
    }

    /**
     * 隐藏或者显示某一类型的所有游戏任务
     *
     * @param  array $aUserParam
     * @return int
     */
    public function setHideAll($aUserParam)
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            array('hide' => (int)$aUserParam['hide'] % 2),
            'type=:type AND game_id=:game_id',
            array(':type' => $aUserParam['type'], ':game_id' => $aUserParam['game_id'])
        );
    }
}

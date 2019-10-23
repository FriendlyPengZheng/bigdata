<?php
class TreeClientInfo extends TMFormModel
{
    /**
     * @brief tableName 
     * 返回表名
     *
     * @return {string}
     */
    public function tableName()
    {
        return 't_client_stat_info';
    }

    /**
     * @brief getGarbledInfo 
     * 获取乱码的信息
     *
     * @return {array}
     */
    public function getGarbledInfo($garbled)
    {
        return $this->getDb()->createCommand()->select('a.stid, a.sstid, b.game_name, a.hip')
            ->from($this->tableName() . ' AS a')
            ->join('t_game_info AS b', 'a.game = b.game_id')
            ->where('stid = ?')
            ->orWhere('sstid = ?')
            ->queryAll(array($garbled, $garbled));
    }
}

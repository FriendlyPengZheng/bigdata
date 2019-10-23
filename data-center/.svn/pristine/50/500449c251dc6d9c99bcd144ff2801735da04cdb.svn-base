<?php
class gamecustom_DiyData extends TMFormModel
{
    public function tableName()
    {
        return 't_web_diy_data';
    }

    public function saveData()
    {
        $this->getDb()->createCommand(
            'INSERT INTO ' . $this->tableName() . ' (diy_id,range,data_expr,data_name,game_id,display_order) ' .
            'VALUES (?,?,?,?,?,?) ' .
            'ON DUPLICATE KEY UPDATE diy_data_id=LAST_INSERT_ID(diy_data_id),data_expr=?,data_name=?,' .
                'game_id=?,display_order=?'
        )->execute(array(
            $this->diy_id,
            $this->range,
            $this->data_expr,
            $this->data_name,
            $this->game_id,
            $this->display_order,
            $this->data_expr,
            $this->data_name,
            $this->game_id,
            $this->display_order
        ));
        return $this->getDb()->getLastInsertID();
    }

    public function deleteByDiyId($exclude = array())
    {
        $sSql = 'DELETE FROM ' . $this->tableName() . ' WHERE diy_id = ?';
        $aParam = array($this->diy_id);
        if ($exclude) {
            $sSql .= ' AND diy_data_id NOT IN (';
            foreach ($exclude as $diyDataId) {
                $sSql .= '?,';
                $aParam[] = $diyDataId;
            }
            $sSql = rtrim($sSql, ',') . ')';
        }
        return $this->getDb()->createCommand($sSql)->execute($aParam);
    }

    public function findByDiyId()
    {
        return $this->getDb()->createCommand()
            ->select('CONCAT_WS(":","0",`data_expr`) AS `id`,"0" AS `data_id`,`diy_data_id`,`diy_id`,`range`,' .
                '`data_expr`,`precision`,`unit`,`data_name`,`game_id`,`display_order`,`hide`')
            ->from($this->tableName())
            ->where('diy_id=?')
            ->order('display_order ASC')
            ->queryAll(array($this->diy_id));
    }
}

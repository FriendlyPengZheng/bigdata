<?php
class gameanalysis_Item extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_item_info';
    }

    /**
     * 取道具列表
     * @param array $aUserParam
     * @return array
     */
    public function getList($aUserParam)
    {
        if ($aUserParam['game_id'] == 2) {
            $oDbConnection = $this->getDb();
            $oTransaction = $oDbConnection->beginTransaction();
        
            $tmp_tb_create = 'CREATE TEMPORARY TABLE cat1 (item_id int(11), category_name varchar(255), primary key(item_id, category_name)) DEFAULT CHARSET=utf8';
            $tmp_tb_init = 'INSERT INTO cat1 SELECT rel.item_id, cat.category_name FROM t_web_item_category_rel rel INNER JOIN ( SELECT cat1.category_id,CONCAT_WS("-",cat2.category_name,cat1.category_name ) AS category_name FROM t_web_item_category cat1 LEFT JOIN t_web_item_category cat2 ON cat1.parent_id=cat2.category_id WHERE cat1.sstid = ? AND cat1.game_id = ? AND cat1.is_leaf = ?) cat ON rel.category_id = cat.category_id';
            $tmp_db_query = 'SELECT item.item_id AS id, item.item_name AS name, item.hide, cat1.category_name FROM t_item_info item LEFT JOIN cat1 ON item.item_id = cat1.item_id WHERE item.sstid = ? and item.game_id = ?';
        
            $oDbConnection->createCommand($tmp_tb_create)->execute();
            $oDbConnection->createCommand($tmp_tb_init)->execute(array($aUserParam['sstid'],
                                                                       $aUserParam['game_id'],
                                                                       gameanalysis_ItemCategory::TYPE_LEAF));
            $aItems = $oDbConnection->createCommand($tmp_db_query)->queryAll(array($aUserParam['sstid'],
                                                                                   $aUserParam['game_id']));
            $oTransaction->commit();
        } else {
            $aItems = $this->getDb()->createCommand(
                'SELECT item.item_id AS id,item.item_name AS name,item.hide,cat1.category_name ' .
                'FROM t_item_info item LEFT JOIN (' .
                'SELECT rel.item_id,cat.category_name ' .
                'FROM t_web_item_category_rel rel INNER JOIN (' .
                'SELECT cat1.category_id,CONCAT_WS("-",cat2.category_name,cat1.category_name) AS category_name ' .
                'FROM t_web_item_category cat1 ' .
                'LEFT JOIN t_web_item_category cat2 ON cat1.parent_id=cat2.category_id ' .
                'WHERE cat1.sstid = ? AND cat1.game_id = ? AND cat1.is_leaf=?' .
                ') cat ON rel.category_id = cat.category_id ' .
                ') cat1 ON item.item_id = cat1.item_id ' .
                'WHERE item.sstid = ? and item.game_id = ?')
                ->queryAll(array(
                               $aUserParam['sstid'], $aUserParam['game_id'],
                               gameanalysis_ItemCategory::TYPE_LEAF,
                               $aUserParam['sstid'], $aUserParam['game_id']
                                 ));
        }

        $aMerge = array();
        foreach ($aItems as $item) {
            if (isset($aMerge[$item['id']])) {
                $aMerge[$item['id']]['category_name'] .= ';' . $item['category_name'];
            } else {
                $aMerge[$item['id']] = $item;
            }
        }
        $values = array_values($aMerge);
        if ($aUserParam['pagination']) {
            return array_slice($values, (int)$aUserParam['start'], (int)$aUserParam['end']);
        } else {
            return $values;
        }
    }

    /**
     * 取道具列表数量
     * @param array $aUserParam
     * @return array
     */
    public function getListTotal($aUserParam)
    {
        $aUserParam['pagination'] = false;
        return count($this->getList($aUserParam));
    }

    /**
     * 变更名称
     * @param array $aUserParam
     * @return array
     */
    public function setName($aUserParam)
    {
        $aInfo = $this->exists($aUserParam);
        $aInfo['item_name'] = $aUserParam['name'];
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
     * Replace the item.
     *
     * @param  array   $aFields Item info
     * @return integer          Effect rows
     */
    public function replace($aFields)
    {
        return $this->getDb()->createCommand(
                'REPLACE INTO ' . $this->tableName() .
                ' (sstid, game_id, item_id, item_name, hide) VALUES (?, ?, ?, ?, ?)')
            ->execute(array(
                $aFields['sstid'], $aFields['game_id'],
                $aFields['item_id'], $aFields['item_name'], $aFields['hide']));
    }

    /**
     * 道具是否存在
     * @param array $aUserParam
     * @return array the exist item info
     * @throw TMValidatorException if not exist
     */
    protected function exists($aUserParam)
    {
        $aInfo = $this->findAll(array(
            'condition' => array(
                'sstid' => $aUserParam['sstid'],
                'game_id' => $aUserParam['game_id'],
                'item_id' => $aUserParam['id']
            )
        ));
        TMValidator::ensure($aInfo, TM::t('tongji', '道具不存在！'));
        return $aInfo[0];
    }
}

<?php
class gameanalysis_ItemCategorySale extends gameanalysis_Data
{
    public function getConfiguration()
    {
        return array(
            array('name' => TM::t('tongji', '销售金额'), 'field' => '_salemoney'),
            array('name' => TM::t('tongji', '销售数量'), 'field' => '_salenum'),
            array('name' => TM::t('tongji', '购买人次'), 'field' => '_buycount')
        );
    }

    public function getSaleListTotal($aUserParam, data_time_Period $period)
    {
		$this->_createTemporaryTable($aUserParam['parent_id']);
		$table = 't_current_item_sale_data ';
		if ($period->getFrom() < strtotime(date('Y-01-01 00:00:00'))) {
			$table = 't_item_sale_data ';
		}
        $sSql = 'SELECT COUNT(DISTINCT(rel.category_id)) ' .
                'FROM tmp_category_table rel ' .
                'INNER JOIN (' .
                    'SELECT DISTINCT(item_id), game_id, sstid ' . 
                    'FROM ' . $table .
                    'WHERE vip = -1 AND gpzs_id = ? AND sstid = ? AND time >= ? AND time <= ? ' .
                ') data ' .
                'ON rel.item_id = data.item_id AND rel.game_id = data.game_id AND rel.sstid = data.sstid ' .
                'WHERE rel.hide = 0';
        return $this->getDb()->createCommand($sSql)->queryScalar(array(
            $aUserParam['gpzs_id'], $aUserParam['sstid'], $period->getFrom(), $period->getTo()
        ));
    }

	private function _createTemporaryTable($parentId)
	{
        $sql = 'DROP TEMPORARY TABLE IF EXISTS tmp_category_table';
		$connection = $this->getDb();
		$connection->createCommand($sql)->execute();
		$sql = 'CREATE TEMPORARY TABLE tmp_category_table (SELECT rel.category_id, cat.category_name, cat.is_leaf, rel.item_id, item.hide, rel.game_id, rel.sstid ' . 
	           'FROM t_web_item_category_rel rel ' .
               'INNER JOIN (' .
                    'SELECT category_id,category_name,is_leaf ' .
                    'FROM t_web_item_category ' .
                    'WHERE parent_id = ?' .
               ') cat ' .
			   'ON rel.category_id = cat.category_id ' . 
			   'INNER JOIN t_item_info item ' .
			   'ON rel.item_id = item.item_id AND rel.game_id = item.game_id AND rel.sstid = item.sstid)';

		$connection->createCommand($sql)->execute([$parentId]);
		$connection->createCommand('ALTER TABLE tmp_category_table ADD INDEX (item_id, game_id, sstid)')->execute();
	}

    public function getSaleList($aUserParam, data_time_Period $period, $pagination=true)
    {
        $aUserParam['factor'] = (float)$aUserParam['factor'];
        $factor = '';
        if ($aUserParam['factor'] !== 1.0) {
            $factor = '*(' . $aUserParam['factor'] . ')';
        }

		$this->_createTemporaryTable($aUserParam['parent_id']);
		$table = 't_current_item_sale_data ';
		if ($period->getFrom() < strtotime(date('Y-01-01 00:00:00'))) {
			$table = 't_item_sale_data ';
		}
        $sSql = 'SELECT rel.category_id,rel.category_name,rel.is_leaf,SUM(data.buycount) AS _buycount,' .
                    'SUM(data.salenum) AS _salenum,SUM(data.salemoney)'. $factor . ' AS _salemoney ' .
                'FROM tmp_category_table rel ' .
                'INNER JOIN (' .
                    'SELECT game_id,item_id,sstid,SUM(buycount) AS buycount,SUM(salenum) AS salenum,' .
                        'SUM(salemoney) AS salemoney ' .
                    'FROM ' . $table .
                    'WHERE vip = -1 AND gpzs_id = ? AND sstid = ? AND time >= ? AND time <= ? ' .
                    'GROUP BY item_id' .
                ') data ' .
                'ON rel.item_id = data.item_id AND rel.game_id = data.game_id AND rel.sstid = data.sstid ' .
                'WHERE rel.hide = 0 GROUP BY rel.category_id ';
        if (isset($aUserParam['top'])) {
            $sSql .= 'ORDER BY _salemoney DESC LIMIT ' . $aUserParam['top'];
        } elseif ($pagination) {
            $sSql .= 'LIMIT ' . $aUserParam['start'] . ', ' . $aUserParam['end'];
        }

        return $this->getDb()->createCommand($sSql)->queryAll(array(
            $aUserParam['gpzs_id'], $aUserParam['sstid'], $period->getFrom(), $period->getTo()
        ));
    }

    public function getSaleDetailData($aUserParam, $aCategory, $period)
    {
        $aSaleDetail = $this->getSaleDetail($aUserParam, $aCategory, $period);
        if (!$aSaleDetail) {
            return;
        }

        $aConf = $this->getConfiguration();
        return $this->populate($aSaleDetail, $aCategory, $aConf, $period);
    }

    protected function getSaleDetail($aUserParam, $aCategory, $period)
    {
        if (!$aCategory) return;

        $aUserParam['factor'] = (float)$aUserParam['factor'];
        $factor = '';
        if ($aUserParam['factor'] !== 1.0) {
            $factor = '*(' . $aUserParam['factor'] . ')';
        }

        $sSql = 'SELECT cat.category_id,data.time AS _key,cat.category_name,SUM(data.buycount) AS _buycount,' .
                    'SUM(data.salenum) AS _salenum,SUM(data.salemoney)'. $factor . ' AS _salemoney ' .
                'FROM t_web_item_category_rel rel ' .
                'INNER JOIN (' .
                    'SELECT category_id,category_name ' .
                    'FROM t_web_item_category ' .
                    'WHERE category_id IN (';
        $aParam = array();
        foreach ($aCategory as $categoryId) {
            $sSql .= '?,';
            $aParam[] = $categoryId;
        }
        $sSql = rtrim($sSql, ',') . ')' .
                ') cat ' .
                'ON rel.category_id = cat.category_id ' .
                'INNER JOIN t_item_info item ' .
                'ON rel.item_id = item.item_id AND rel.game_id = item.game_id AND rel.sstid = item.sstid ' .
                'INNER JOIN (' .
                    'SELECT game_id,time,item_id,sstid,buycount,salenum,salemoney ' .
                    'FROM t_item_sale_data ' .
                    'WHERE vip = -1 AND gpzs_id = ? AND sstid = ? AND time >= ? and time <= ?' .
                ') data ' .
                'ON rel.item_id = data.item_id AND rel.game_id = data.game_id AND rel.sstid = data.sstid ' .
                'WHERE item.hide = 0 GROUP BY rel.category_id,data.time';
        array_push($aParam, $aUserParam['gpzs_id'], $aUserParam['sstid'], $period->getFrom(), $period->getTo());

        return $this->getDb()->createCommand($sSql)->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)->queryAll($aParam);
    }
}

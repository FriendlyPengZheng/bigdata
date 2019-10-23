<?php
class gameanalysis_ItemSale extends gameanalysis_Data
{
    /**
     * Masks for configuration.
     */
    const MASK_MONEY = 2;

    /**
     * @var array Item ids.
     */
    private $_itemIds = array();

    public function tableName()
    {
        return 'v_item_sale_data';
    }

    public function addItemId($itemId)
    {
        $this->_itemIds[] = $itemId;
    }

    public function getItemIds()
    {
        return $this->_itemIds;
    }

    public function getConfiguration($mask = null)
    {
        $aConf = array(
            array('name' => TM::t('tongji', '销售金额'), 'field' => '_salemoney', 'slot' => 3),
            array('name' => TM::t('tongji', '销售数量'), 'field' => '_salenum', 'slot' => 3),
            array('name' => TM::t('tongji', '购买人数'), 'field' => '_buyucount', 'slot' => 1)
        );
        if ($mask === null) return $aConf;

        foreach ($aConf as $idx => $conf) {
            if (($conf['slot'] & $mask) === 0) {
                unset($aConf[$idx]);
            }
        }
        return array_values($aConf);
    }

    public function getSaleData($aUserParam, data_time_Period $period)
    {
        $aSaleList = $this->getSaleList($aUserParam, $period, false);
        if (!$aSaleList) {
            return;
        }

        $aData = array('key' => array(), 'data' => array());
        $aConf = $this->getConfiguration(self::MASK_MONEY);
        foreach ($aConf as $idx => $conf) {
            $aData['data'][$idx] = array('name' => $conf['name'], 'data' => array());
        }
        foreach ($aSaleList as $sale) {
            $this->addItemId($sale['item_id']);
            $aData['key'][] = $sale['item_name'];
            foreach ($aConf as $idx => $conf) {
                $aData['data'][$idx]['data'][] = $sale[$conf['field']];
            }
        }
        return $aData;
    }

    public function getSaleList($aUserParam, data_time_Period $period, $pagination=true)
    {
        $aUserParam['factor'] = (float)$aUserParam['factor'];
        $factor = '';
        if ($aUserParam['factor'] !== 1.0) {
            $factor = '*(' . $aUserParam['factor'] . ')';
        }

        $oCommand = $this->getDb()->createCommand()
            ->select('item_id,item_name,SUM(buycount) AS _buycount,SUM(salenum) AS _salenum,SUM(salemoney)' .
                $factor . ' AS _salemoney')
            ->from($this->tableName())
            ->where('sstid = ? AND gpzs_id = ? AND time >= ? AND time <= ? AND vip = -1')
            ->group('item_id');
        if (isset($aUserParam['top'])) {
            $oCommand->order('_salemoney DESC')->limit((int)$aUserParam['top']);
        } elseif ($pagination) {
            $oCommand->offset((int)$aUserParam['start'])->limit((int)$aUserParam['end']);
        }
        if (isset($aUserParam['category_id'])) {
            $rel = new gameanalysis_ItemCategoryRel();
            $rel->game_id = $aUserParam['game_id'];
            $rel->sstid = $aUserParam['sstid'];
            $rel->category_id = $aUserParam['category_id'];
            $aItems = $rel->getCategoryItem();
            if (empty($aItems)) return;
            $oCommand->andWhere(array('IN', 'item_id', TMArrayHelper::column($aItems, 'item_id')));
        }

        return $oCommand->queryAll(array($aUserParam['sstid'], $aUserParam['gpzs_id'], $period->getFrom(), $period->getTo()));
    }

    public function getSaleListTotal($aUserParam, data_time_Period $period)
    {
        $oCommand = $this->getDb()->createCommand()
            ->select('count(distinct(item_id))')
            ->from('t_item_sale_data')
            ->where('sstid = ? AND gpzs_id = ? AND time >= ? AND time <= ? AND vip = -1');
        if (isset($aUserParam['category_id'])) {
            $rel = new gameanalysis_ItemCategoryRel();
            $rel->game_id = $aUserParam['game_id'];
            $rel->sstid = $aUserParam['sstid'];
            $rel->category_id = $aUserParam['category_id'];
            $aItems = $rel->getCategoryItem();
            if (empty($aItems)) return;
            $oCommand->andWhere(array('IN', 'item_id', TMArrayHelper::column($aItems, 'item_id')));
        }

        return $oCommand->queryScalar(array($aUserParam['sstid'], $aUserParam['gpzs_id'], $period->getFrom(), $period->getTo()));
    }

    public function getSaleDetailData($aUserParam, $aItem, $period, $mask = null)
    {
        $aSaleDetail = $this->getSaleDetail($aUserParam, $aItem, $period);
        if (!$aSaleDetail) {
            return;
        }

        $aConf = $this->getConfiguration($mask);
        return $this->populate($aSaleDetail, $aItem, $aConf, $period);
    }

    protected function getSaleDetail($aUserParam, $aItem, $period)
    {
        if (!$aItem) return;

        $aUserParam['factor'] = (float)$aUserParam['factor'];
        $factor = '';
        if ($aUserParam['factor'] !== 1.0) {
            $factor = '*(' . $aUserParam['factor'] . ')';
        }

        $sql = 'SELECT item_id,time AS _key,item_name,buyucount AS _buyucount,salenum AS _salenum,salemoney' .
                    $factor . ' AS _salemoney ' .
               'FROM ' . $this->tableName() . ' ' .
               'WHERE vip = -1 AND sstid = ? AND gpzs_id = ? AND time >= ? AND time <= ? AND item_id IN (';
        $param = array($aUserParam['sstid'], $aUserParam['gpzs_id'], $period->getFrom(), $period->getTo());
        foreach ($aItem as $itemId) {
            $sql .= '?,';
            $param[] = $itemId;
        }
        return $this->getDb()->createCommand(rtrim($sql, ',') . ')')
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->queryAll($param);
    }
}

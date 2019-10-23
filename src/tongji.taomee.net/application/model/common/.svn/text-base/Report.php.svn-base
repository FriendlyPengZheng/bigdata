<?php
class common_Report extends common_Stat
{
    /**
     * @var array 分布类型op_type
     */
    protected $aDistrOpTypes = array(
        'distr_sum' => true,
        'distr_max' => true,
        'distr_set' => true
    );

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_report_info';
    }

    public function attributes()
    {
        return array('report_id as r_id', '("report") AS type', 'sstid', 'sstid_name', 'report_name AS r_name', 'is_multi');
    }

    /**
     * 通过stid, op_type, op_fields取sstid，用于展示
     * @param array $aUserParam
     * @return array
     */
    public function getSstid($aUserParam)
    {
        return $this->getDb()->createCommand()
            ->select(implode(',', $this->attributes()) . ', IF(sstid_name = "", sstid, sstid_name) as name')
            ->from($this->tableName())
            ->where('game_id = ? AND stid = ? AND op_type = ? AND op_fields = ? AND stid != sstid')
            ->queryAll(array(
                $aUserParam['game_id'],
                $aUserParam['stid'],
                $aUserParam['op_type'],
                $aUserParam['op_fields']
            ));
    }

    /**
     * 通过stid, op_type, op_fields取sstid，用于修改
     * @param array $aUserParam
     * @return array
     */
    public function getCommonSstid($aUserParam)
    {
        $statInfo = $this->getSstidSharedStat($aUserParam);
        $aCondition = $aTemp = array();
        $aParam = array($aUserParam['game_id']);
        foreach ($statInfo as $stat) {
            $aCondition[] = '(stid = ? AND op_type = ? AND op_fields = ?)';
            array_push($aParam, $stat['stid'], $stat['op_type'], $stat['op_fields']);
        }
        $statInfo = $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('sstid, report_id as id, IF(sstid_name = "", sstid, sstid_name) as name')
            ->from($this->tableName())
            ->where('game_id = ? AND stid != sstid AND (' . implode(' OR ', $aCondition) . ')')
            ->queryAll($aParam);
        foreach ($statInfo as $sstid => $stat) {
            $id = $name = null;
            foreach ($stat as $r) {
                $id = isset($id) ? ($id . ',' . $r['id']) : $r['id'];
                $name = isset($name) ? $name : $r['name'];
            }
            $aTemp[] = array('id' => $id, 'name' => $name);
        }
        return $aTemp;
    }

    /**
     * 共用sstid的report配置
     * @param  array $statInfo
     * @return array
     */
    protected function getSstidSharedStat($statInfo)
    {
        $aGroups = array(
            0 => array(
                array('stid' => '_acpay_', 'op_type' => 'sum', 'op_fields' => '_amt_'),
                array('stid' => '_acpay_', 'op_type' => 'count', 'op_fields' => '')
            )
        );
        $aMap = array(
            '_acpay_:sum:_amt_' => 0,
            '_acpay_:count:' => 0
        );

        $sKey = $statInfo['stid'] . ':' . $statInfo['op_type'] . ':' . $statInfo['op_fields'];
        if (isset($aMap[$sKey])) {
            return $aGroups[$aMap[$sKey]];
        }
        return array($statInfo);
    }

    /**
     * 根据node_id获取内容列表，只获取显示的项(status=0)
     *
     * @param  array     $aUserParam
     * @param  bool|null $mIsMulti
     * @return array
     */
    public function getContentList($aUserParam, $mIsMulti = null)
    {
        $oCommand = $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('is_multi,report_id AS r_id, op_type As ifselfhelp , report_name AS r_name,("report") AS type,display_order, add_time')
            ->from($this->tableName())
            ->where('game_id=? AND node_id=? AND status=0')
            ->order('display_order,CONVERT(report_name USING gb2312)');
        $aParam = [$aUserParam['game_id'], $aUserParam['node_id']];

        if (isset($mIsMulti)) {
            $oCommand->andWhere('is_multi=?');
            $aParam[] = $mIsMulti;
        }
        return $oCommand->queryAll($aParam);
    }

    /**
     * 设置名称
     * @param  array   $aUserParam
     * @return integer
     */
    public function setName($aUserParam)
    {
        $this->report_id = $aUserParam['id'];
        $this->report_name = $aUserParam['name'];
        return $this->update(array('report_name'));
    }

    /**
     * 设置名称
     * @param  array   $aUserParam
     * @return integer
     */
    public function setSstidName($aUserParam)
    {
        $aUserParam['id'] = (array)$aUserParam['id'];
        if (!$aUserParam['id']) return;
        $aCondition = $aParam = array();
        foreach ($aUserParam['id'] as $key => $id) {
            $aCondition[] = ':report_id' . $key;
            $aParam[':report_id' . $key] = $id;
        }
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            array('sstid_name' => $aUserParam['name']),
            'report_id IN (' . implode(',', $aCondition) . ')',
            $aParam
        );
    }

    /**
     * 通过节点ID更新统计项信息
     * @param  integer $iNodeId
     * @param  array   $aFields
     * @return integer
     */
    public function updateStatItemByNodeId($iNodeId, $aFields)
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            $aFields,
            'node_id = :last_node_id',
            array(':last_node_id' => $iNodeId)
        );
    }

    /**
     * 更新统计项信息
     * @param  integer $iRid
     * @param  array   $aFields
     * @return integer
     */
    public function updateStatItem($iRid, $aFields)
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            $aFields,
            'report_id = :r_id',
            array(':r_id' => $iRid)
        );
    }

    /**
     * 通过r_id取统计项信息
     * @param  integer $iRid
     * @return array
     */
    public function getStatItemById($iRid)
    {
        return $this->getDb()->createCommand()
            ->select('report_id as r_id,report_name as r_name,("report") as type,game_id,node_id,op_type,is_multi,is_setted,stid,sstid,op_fields')
            ->from($this->tableName())
            ->where('report_id = ?')
            ->queryRow(array($iRid));
    }

    /**
     * 取统计项
     *
     * @param  array       $aStatItemConditions
     * @param  array       $aTreeConditions
     * @param  string|null $sOrder
     * @param  string|null $sGroup
     * @param  string|null $sSelect
     * @return array
     */
    public function getStatItemList(
        $aStatItemConditions,
        $aTreeConditions = [],
        $sOrder = 'r.is_multi,r.report_id',
        $sGroup = null,
        $sSelect = <<<'EOT'
r.report_id,r.report_name,r.stid,r.sstid,r.op_type,r.op_fields,r.is_multi,r.is_setted,
r.report_id AS r_id,("report") AS type,r.report_name AS r_name
EOT
    ) {
        $oCommand = $this->getDb()->createCommand()
            ->select($sSelect)
            ->from($this->tableName() . ' r')
            ->order($sOrder)
            ->limit(1000)
            ->group($sGroup);
        $aConditions = ['r' => $aStatItemConditions];
        if ($aTreeConditions) {
            $aConditions['t'] = $aTreeConditions;
            $oCommand->join('t_web_tree t', 'r.node_id = t.node_id');
        }
        $aCondition = ['AND'];
        $aParam = [];
        foreach ($aConditions as $alias => $conditions) {
            foreach ($conditions as $key => $value) {
                $aCondition[] = $alias . '.' . $key . ' = ?';
                $aParam[] = $value;
            }
        }
        return $oCommand->where($aCondition)->queryAll($aParam);
    }

    /**
     * 设置统计项种类
     * @param  array $aStatItem
     * @return array
     */
    public function setCategory($aStatItem)
    {
        if (!$aStatItem) return $aStatItem;
        $aStatItem['r_type'] = gamecustom_report_Base::CATEGORY_BASIC;
        if ($aStatItem['is_multi']) {
            $aStatItem['r_type'] = gamecustom_report_Base::CATEGORY_ITEM;
            if (isset($this->aDistrOpTypes[$aStatItem['op_type']])) {
                $aStatItem['r_type'] = gamecustom_report_Base::CATEGORY_DISTR;
                $aStatItem['is_empty'] = !$aStatItem['is_setted'];
            }
        }
        return $aStatItem;
    }

    public function findByUk($iGameId, $aUniqueKey)
    {
        $aINParam = $aINPos = array();
        foreach ($aUniqueKey as $r) {
            array_push($aINParam, $r['stid'], $r['sstid'], $r['op_type'], $r['op_fields']);
            array_push($aINPos, '(stid = ? AND sstid = ? AND op_type = ? AND op_fields = ?)');
        }
        $aINParam[] = $iGameId;
        return $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('CONCAT_WS(":", stid, sstid, op_type, op_fields),' . implode(',', $this->attributes()))
            ->from($this->tableName())
            ->where(implode(' OR ', $aINPos))
            ->andWhere('game_id = ?')
            ->queryAll($aINParam);
    }

    public function getUk($aInfo)
    {
        return $aInfo['stid'] . ':' . $aInfo['sstid'] . ':' . $aInfo['op_type'] . ':' . $aInfo['op_fields'];
    }

    public function undefined($suffix)
    {
        $undefined = array_fill_keys(array('r_id', 'r_name', 'sstid', 'sstid_name'), 'undefined' . $suffix);
        $undefined['type'] = 'undefined';
        $undefined['is_multi'] = 0;
        return $undefined;
    }

    /**
     * 调整统计项顺序，在parent_id下将id置于after_id之前
     *
     * @param  array     $aUserParam
     * @param  bool|null $mIsBasic
     * @return null
     */
    public function adjustDisplayOrder($aUserParam, $mIsBasic = 0)
    {
        $aInfo = $this->ensureExistsAndAuthed((int)$aUserParam['id'], $mIsBasic);

        $iAfterId = (int)$aUserParam['after_id'];
        if ($iAfterId !== 0) {
            $aAfter = $this->ensureExistsAndAuthed($iAfterId, $mIsBasic);
            TMValidator::ensure(
                (int)$aAfter['node_id'] === (int)$aInfo['node_id'],
                TM::t('tongji', '排序统计项所在节点必须一致！')
            );
        }

        $aChildren = $this->getContentList($aInfo, $aInfo['is_multi']);
        $aChildren = array_pop($aChildren);
        TM::createComponent([
            'class'    => 'application.components.PositionSorter',
            'list'     => $aChildren,
            'key'      => 'r_id',
            'callback' => function($key, $order) {
                $this->updateStatItem($key, ['display_order' => $order]);
            }
        ])->before($aInfo['r_id'], $iAfterId);
    }

    /**
     * 调整统计项数据
     *
     * @param  array     $aUserParam
     * @param  bool|null $mIsBasic
     * @return bool
     */
    public function adjustDataList($aUserParam, $mIsBasic = 0)
    {
        $aInfo = $this->ensureExistsAndAuthed((int)$aUserParam['r_id'], $mIsBasic);
        $dataModel = new common_DataInfo();

        $aDataList = TMArrayHelper::column($dataModel->getRangeByRid($aInfo), null, 'data_id');
        $aMirror = $this->findByUk(
            $aInfo['game_id'],
            [array_merge($aInfo, $aInfo['op_type'] === 'count' ? ['op_type' => 'ucount'] : ['op_type' => 'count'])]
        );
        if ($aMirror) $aMirror = TMArrayHelper::column($dataModel->getRangeByRid($aMirror[key($aMirror)][0]), null, 'range');

        $aNewList = (array)$aUserParam['id'];
        $iOrder = 1;
        foreach ($aNewList as $dataId) {
            if (!isset($aDataList[$dataId])) continue;
            if (isset($aMirror[$aDataList[$dataId]['range']])) {
                $dataModel->data_id = $aMirror[$aDataList[$dataId]['range']]['data_id'];
                $dataModel->display_order = $iOrder;
                $dataModel->update(['display_order']);
            }
            $dataModel->data_id = $dataId;
            $dataModel->display_order = $iOrder++;
            $dataModel->update(['display_order']);
            unset($aDataList[$dataId]);
        }

        foreach($aDataList as $dataId => $dataInfo) {
            if (isset($aMirror[$dataInfo['range']])) {
                $dataModel->data_id = $aMirror[$dataInfo['range']]['data_id'];
                $dataModel->hide = 1;
                $dataModel->update(['hide']);
            }
            $dataModel->data_id = $dataId;
            $dataModel->hide = 1;
            $dataModel->update(['hide']);
        }
        return true;
    }

    /**
     * 保证统计项存在并且当前用户有权限修改之
     *
     * @param  int      $iStatId
     * @param  int|null $mIsBasic
     * @return array
     */
    protected function ensureExistsAndAuthed($iStatId, $mIsBasic = null)
    {
        $aInfo = $this->getStatItemById($iStatId);
        TMValidator::ensure($aInfo, TM::t('tongji', '统计项{id}不存在！', ['{id}' => $iStatId]));
        (new common_Game())->checkGameManageAuth($aInfo['game_id']);

        if (isset($mIsBasic)) {
            TMValidator::ensure(
                (new gamecustom_Tree())->getNodeById($aInfo['node_id'], $mIsBasic),
                TM::t('tongji', '统计项{id}不合法！', ['{id}' => $iStatId])
            );
        }

        return $aInfo;
    }
}

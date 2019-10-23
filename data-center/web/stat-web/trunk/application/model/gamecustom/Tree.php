<?php
class gamecustom_Tree extends TMFormModel
{
    const NODE_UNKNOWN = 2;
    const NODE_LEAF = 1;
    const NODE_NONLEAF = 0;

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_tree';
    }

    /**
     * 获取指定节点下的树
     * 只返回其下一级节点
     *
     * @param  array $aUserParam
     * @return array
     */
    public function getTree($aUserParam)
    {
        $nodekey = 'cached_tree_node_' . $aUserParam['game_id'] . $aUserParam['parent_id'];
        $cache = TM::app()->cache;
        if (($nodes = $cache->get($nodekey))) {
            return $nodes;
        }   
        $key = 'cached_tree_' . $aUserParam['game_id'];
        $aEntireTree = $cache->get($key);
        if (!$aEntireTree) {
            $aEntireTree = $this->tree($aUserParam);
            $cache->set($key, $aEntireTree, 3600);
        }   
        if (isset($aEntireTree[$aUserParam['parent_id']])) {
            $cache->set($nodekey, $aEntireTree[$aUserParam['parent_id']], 3600);
            return $aEntireTree[$aUserParam['parent_id']];
        } 
    }

    /**
     * 从数据库获取树结构（只包含两级）
     *
     * @param  array $aUserParam
     * @return array
     */
    protected function tree($aUserParam)
    {
        return $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('parent_id, node_id, node_name, is_leaf')
            ->from($this->tableName())
            ->where('game_id = ? AND is_basic = 0 AND hide = 0')
            ->order('display_order,node_id DESC')
            ->queryAll(array($aUserParam['game_id']));
    }

    /**
     * 设置node名称
     * @param array $aUserParam
     * @return array
     */
    public function setName($aUserParam)
    {
        $aUserParam['node_id'] = (int)$aUserParam['id'];
        $this->ensureNodeExistsAndAuthed($aUserParam['node_id']);
        return $this->updateNode(
            $aUserParam['node_id'],
            array('node_name' => $aUserParam['name'])
        );
    }

    /**
     * 确保节点存在且拥有游戏权限
     * @param integer $iNodeId
     * @return array
     */
    protected function ensureNodeExistsAndAuthed($iNodeId)
    {
        $aInfo = $this->getNodeById($iNodeId);
        TMValidator::ensure($aInfo, TM::t('tongji', '节点{id}不存在！', array('{id}' => $iNodeId)));
        (new common_Game())->checkGameManageAuth($aInfo['game_id']);
        return $aInfo;
    }

    /**
     * 获取节点信息
     *
     * @param  int $iNodeId
     * @param  int $iIsBasic
     * @return mixed
     */
    public function getNodeById($iNodeId, $iIsBasic = 0)
    {
        return $this->getDb()->createCommand()
            ->select('node_id, node_name, game_id, parent_id, is_leaf, hide, display_order')
            ->from($this->tableName())
            ->where('node_id = ? AND is_basic = ?')
            ->queryRow(array($iNodeId, $iIsBasic));
    }

    /**
     * 更新节点
     * @param integer $iNodeId
     * @param array $aFields
     * @return integer
     */
    public function updateNode($iNodeId, $aFields)
    {
        TM::app()->getUser()->info(TM::t('tongji', '更新节点'), array('node_id' => $iNodeId, 'fields' => $aFields));
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            $aFields,
            'node_id = :node_id',
            array(':node_id' => $iNodeId)
        );
    }

    /**
     * 新增节点
     * @param array $aUserParam
     * @return array
     */
    public function addNode($aUserParam)
    {
        $aUserParam['parent_id'] = (int)$aUserParam['parent_id'];
        // 游戏权限在接收参数时已经检查
        // 第一级节点父节点ID为0
        $aUserParam['is_leaf'] = self::NODE_UNKNOWN; // 默认未知类型
        if ($aUserParam['parent_id']) {
            $aInfo = $this->getNodeById($aUserParam['parent_id']);
            TMValidator::ensure($aInfo && $aUserParam['game_id'] == $aInfo['game_id'], TM::t('tongji', '父节点不存在！'));
            TMValidator::ensure($aInfo['is_leaf'] != self::NODE_LEAF, TM::t('tongji', '不能在叶子节点下新增节点！'));
            if ($aInfo['parent_id']) {
                $aUserParam['is_leaf'] = self::NODE_LEAF; // 只能叶子，确保三层结构
            }
        }
        $aChildren = $this->getNodeByPid($aUserParam['parent_id'], $aUserParam['name'], $aUserParam['game_id']);
        TMValidator::ensure(!$aChildren, TM::t('tongji', '父节点下已经存在相同名称的节点！'));
        $aFields = array(
            'node_name' => $aUserParam['name'],
            'game_id' => $aUserParam['game_id'],
            'parent_id' => $aUserParam['parent_id'],
            'is_leaf' => $aUserParam['is_leaf'],
            'is_basic' => 0
        );
        $transaction = $this->getDb()->beginTransaction();
        try {
            if ($aUserParam['parent_id']) {
                $this->updateNode($aUserParam['parent_id'], array('is_leaf' => self::NODE_NONLEAF));
            }
            $this->getDb()->createCommand()->insert($this->tableName(), $aFields);
            $aFields['node_id'] = $this->getDb()->getLastInsertID();
            $transaction->commit();
            TM::app()->getUser()->info(TM::t('tongji', '新增节点'), array('fields' => $aFields));
            return $aFields;
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 通过父节点ID查找子节点，子节点名称可选，父节点为0时需指定游戏
     * @param integer $iParentId
     * @param string $sNodeName
     * @param integer $iGameId
     * @return array
     */
    protected function getNodeByPid($iParentId, $sNodeName = null, $iGameId = null)
    {
        $sCondition = 'parent_id = ? AND is_basic = 0';
        $aParam = array($iParentId);
        if ($iParentId == 0) {
            $sCondition .= ' AND game_id = ?';
            $aParam[] = (int)$iGameId;
        }
        if (isset($sNodeName)) {
            $sCondition .= ' AND node_name = ?';
            $aParam[] = $sNodeName;
        }
        return $this->getDb()->createCommand()
            ->select('node_id, node_name, game_id, parent_id, is_leaf, hide,display_order')
            ->from($this->tableName())
            ->order('display_order, is_leaf, node_id DESC')
            ->where($sCondition)
            ->queryAll($aParam);
    }

    /**
     * 删除节点
     *
     * @param array $aNodeIds
     */
    public function delNode($aNodeIds)
    {
        $transaction = $this->getDb()->beginTransaction();
        try {
            foreach ($aNodeIds as $nodeId) {
                $aInfo = $this->ensureNodeExistsAndAuthed((int)$nodeId);
                // 检查是否有兄弟节点
                $this->unknownParentNode($aInfo);
                $aNodes = array($aInfo);
                // 非叶子，找到其子对应的统计项重置后删除
                if ($aInfo['is_leaf'] == self::NODE_NONLEAF) {
                    $aNodes = array_merge($aNodes, $this->getNodeByPid($aInfo['node_id']));
                }
                $model = new common_Report();
                $diyModel = new gamecustom_Diy();
                foreach ($aNodes as $node) {
                    $model->updateStatItemByNodeId($node['node_id'], array('node_id' => 0));
                    $diyModel->updateStatItemByNodeId($node['node_id'], array('node_id' => 0));
                    $this->deleteNode($node['node_id']);
                }
            }
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 当父节点下没有其他子节点时，设置父节点为未知
     * @param array $aInfo
     */
    protected function unknownParentNode($aInfo)
    {
        if ($aInfo['parent_id']) {
            $aSibling = $this->getNodeByPid($aInfo['parent_id']);
            $bHasSibling = false;
            foreach ($aSibling as $sibling) {
                if ($sibling['node_id'] != $aInfo['node_id']) {
                    $bHasSibling = true;
                }
            }
            if (!$bHasSibling) {
                $this->updateNode($aInfo['parent_id'], array('is_leaf' => self::NODE_UNKNOWN));
            }
        }
    }

    /**
     * 删除节点
     * @param integer $iNodeId
     * @return integer
     */
    protected function deleteNode($iNodeId)
    {
        TM::app()->getUser()->info(TM::t('tongji', '删除节点'), array('node_id' => $iNodeId));
        return $this->getDb()->createCommand()->delete(
            $this->tableName(),
            'node_id = :node_id',
            array(':node_id' => $iNodeId)
        );
    }

    /**
     * 移动节点，父节点不能为叶子节点，树深度不能超过3层
     * @param array $aUserParam
     */
    public function moveNode($aUserParam)
    {
        $aUserParam['node_id'] = (int)$aUserParam['id'];
        $aInfo = $this->ensureNodeExistsAndAuthed($aUserParam['node_id']);
        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->unknownParentNode($aInfo);
            $aUserParam['parent_id'] = (int)$aUserParam['parent_id'];
            if ($aUserParam['parent_id']) {
                $aParentInfo = $this->ensureNodeExistsAndAuthed($aUserParam['parent_id']);
                TMValidator::ensure($aInfo['game_id'] == $aParentInfo['game_id'], TM::t('tongji', '父子节点属于不同游戏！'));
                TMValidator::ensure($aParentInfo['is_leaf'] != self::NODE_LEAF,
                    TM::t('tongji', '父节点是叶子节点，不能包含其他节点！'));
                if ($aParentInfo['is_leaf'] == self::NODE_UNKNOWN) {
                    $this->updateNode($aParentInfo['node_id'], array('is_leaf' => self::NODE_NONLEAF));
                }
                $aCheck = array();
                if ($aParentInfo['parent_id']) {
                    $aCheck[] = $aInfo;
                } else {
                    $aCheck = $this->getNodeByPid($aInfo['node_id']);
                }
                foreach ($aCheck as $check) {
                    TMValidator::ensure($check['is_leaf'] != self::NODE_NONLEAF, TM::t('tongji', '树深度不能超过{1}层！', ['{1}' => 3]));
                    if ($check['is_leaf'] == self::NODE_UNKNOWN) {
                        $this->updateNode($check['node_id'], array('is_leaf' => self::NODE_LEAF));
                    }
                }
            }
            $this->updateNode($aInfo['node_id'], array('parent_id' => $aUserParam['parent_id']));
            $aInfo['parent_id'] = $aUserParam['parent_id'];
            $this->adjustDisplayOrder((int)$aUserParam['after_id'], $aInfo);
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 将$aNodeInfo排序到$iAfterId的前面

     * @param  integer $iAfterId
     * @param  array   $aNodeInfo
     * @return null
     */
    public function adjustDisplayOrder($iAfterId, $aNodeInfo)
    {
        $aNodeInfo['node_id'] = (int)$aNodeInfo['node_id'];
        $aNodeInfo['parent_id'] = (int)$aNodeInfo['parent_id'];

        if ($iAfterId !== 0) {
            $aAfterInfo = $this->ensureNodeExistsAndAuthed($iAfterId);
            TMValidator::ensure(
                (int)$aAfterInfo['parent_id'] === $aNodeInfo['parent_id'],
                TM::t('tongji', '排序节点的父节点必须一致！')
            );
        } else {
            $iAfterId = 0;
        }

        TM::createComponent([
            'class'    => 'application.components.PositionSorter',
            'list'     => $this->getNodeByPid($aNodeInfo['parent_id'], null, $aNodeInfo['game_id']),
            'key'      => 'node_id',
            'callback' => function($key, $order) {
                $this->updateNode($key, ['display_order' => $order]);
            }
        ])->before($aNodeInfo['node_id'], $iAfterId);
    }

    /**
     * 合并节点，对非叶子节点有效
     * @param array $aUserParam
     */
    public function mergeNode($aUserParam)
    {
        $aUserParam['node_id'] = (int)$aUserParam['id'];
        $aInfo = $this->ensureNodeExistsAndAuthed($aUserParam['node_id']);
        TMValidator::ensure($aInfo['is_leaf'] == self::NODE_NONLEAF, TM::t('tongji', '非叶子节点才能合并！'));
        $transaction = $this->getDb()->beginTransaction();
        try {
            $this->updateNode($aInfo['node_id'], array('is_leaf' => self::NODE_LEAF));
            $this->merge($aInfo['node_id'], $aInfo, new common_Report(), new gamecustom_Diy());
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 递归合并节点
     *
     * @param integer        $iNodeId   合并到的节点
     * @param array          $aNode     节点信息
     * @param common_Report  $oReport   用于操作统计项
     * @param gamecustom_Diy $oDiyModel 用于操作自定义加工项
     */
    protected function merge($iNodeId, $aNode, $oReport = null, $oDiyModel = null)
    {
        if ($aNode['is_leaf'] != self::NODE_NONLEAF) return;
        $aNodes = $this->getNodeByPid($aNode['node_id']);
        if (!isset($oReport)) $oReport = new common_Report();
        if (!isset($oDiyModel)) $oDiyModel = new gamecustom_Diy();
        foreach ($aNodes as $node) {
            if ($node['is_leaf'] == self::NODE_NONLEAF) {
                $this->merge($iNodeId, $node, $oReport, $oDiyModel);
            } elseif ($node['is_leaf'] == self::NODE_LEAF) {
                $oReport->updateStatItemByNodeId($node['node_id'], array('node_id' => $iNodeId));
                $oDiyModel->updateStatItemByNodeId($node['node_id'], array('node_id' => $iNodeId));
            }
            $this->deleteNode($node['node_id']);
        }
    }

    /**
     * 绑定统计项到树节点
     * @param array $aUserParam
     */
    public function moveStatItem($aUserParam)
    {
        $aUserParam['r_id'] = (int)$aUserParam['id'];
        $model = $aUserParam['type'] === 'diy' ? new gamecustom_Diy() : new common_Report();
        $aStatItem = $model->getStatItemById($aUserParam['r_id']);
        TMValidator::ensure($aStatItem, TM::t('tongji', '统计项不存在！'));

        $aUserParam['node_id'] = (int)$aUserParam['parent_id'];
        if ($aUserParam['node_id'] < 0) {
            $aUserParam['node_id'] = 0;
        }
        $bUpdateNode = false;
        if ($aUserParam['node_id']) {
            $aInfo = $this->ensureNodeExistsAndAuthed($aUserParam['node_id']);
            TMValidator::ensure($aInfo['is_leaf'] != self::NODE_NONLEAF, TM::t('tongji', '非叶子节点不能绑定统计项！'));
            TMValidator::ensure($aInfo['game_id'] == $aStatItem['game_id'], TM::t('tongji', '统计项与节点游戏不一致！'));
            if ($aInfo['is_leaf'] == self::NODE_UNKNOWN) {
                $bUpdateNode = true;
            }
        } else {
            (new common_Game())->checkGameAuth($aStatItem['game_id']);
        }
        $transaction = $this->getDb()->beginTransaction();
        try {
            if ($bUpdateNode) {
                $this->updateNode($aUserParam['node_id'], array('is_leaf' => self::NODE_LEAF));
            }
            $model->updateStatItem($aUserParam['r_id'], array('node_id' => $aUserParam['node_id']));
            return $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * 取树，用于配置管理
     * @param array $aUserParam
     * @return array
     */
    public function getManageTree($aUserParam)
    {
        $aEntireTree = $this->tree($aUserParam);
        if (!isset($aEntireTree[$aUserParam['parent_id']])) return;
        if ($aUserParam['parent_id']) return $aEntireTree[$aUserParam['parent_id']];
        return array_merge(
            array(array(
                'node_id' => -1,
                'node_name' => '回收站',
                'is_leaf' => 1
            )),
            $aEntireTree[$aUserParam['parent_id']]
        );
    }

    /**
     * 搜索结点名称
     *
     * @param  array $aUserParam
     * @return array
     */
    public function search($aUserParam)
    {
        if (!$aUserParam['keyword']) return array();

        $aSearched = $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('parent_id, node_id')
            ->from($this->tableName())
            ->where('game_id = ? AND is_basic = 0 AND hide = 0 AND node_name LIKE ?')
            ->queryAll(array($aUserParam['game_id'], "%{$aUserParam['keyword']}%"));
        $aSearched = array_filter(array_keys($aSearched));
        if ($aSearched) {
            return array_merge($aSearched, $this->getParentId($aSearched));
        }

        return array();
    }

    /**
     * 取出节点
     *
     * @param  array $aNodeId
     * @return array
     */
    protected function getParentId($aNodeId)
    {
        $aParentId = $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('parent_id, node_id')
            ->from($this->tableName())
            ->where(array('IN', 'node_id', $aNodeId))
            ->andWhere('is_basic = 0 AND hide = 0')
            ->queryAll();
        $aParentId = array_filter(array_keys($aParentId));
        if ($aParentId) {
            return array_merge($aParentId, $this->getParentId($aParentId));
        }

        return array();
    }
}

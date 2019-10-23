<?php
class gamecustom_Diy extends TMFormModel
{
    /**
     * @var array Operand models cache
     */
    protected $operandModels = array();

    /**
     * Return table name of the model
     *
     * @return string
     */
    public function tableName()
    {
        return 't_web_diy';
    }

    /**
     * Return rules of the model's attributes
     *
     * @return array
     */
    public function rules()
    {
        return array(
            array('diy_name', 'string', 'min' => 1, 'max' => 255),
            array('node_id', 'exist',
                'className' => 'gamecustom_Tree',
                'condition' => array(
                    'game_id' => $this->game_id,
                    'is_basic' => 0
                ),
                'exclude' => array(
                    'is_leaf' => gamecustom_Tree::NODE_NONLEAF
                )
            )
        );
    }

    /**
     * Save diy, diy_id for update or empty diy_id for insert
     *
     * @param  array $aUserParam
     * @return array
     */
    public function saveDiy($aUserParam)
    {
        $this->validate($this->attributeNames());
        if ($this->diy_id) {
            $aDiyInfo = $this->findById();
            TMValidator::ensure($aDiyInfo, TM::t('tongji', '自定义加工项不存在！'));
        }

        $this->checkNodeUnknown();
        $aOperandInfo = $this->checkDataRule();

        $confModel = gamecustom_diy_Base::instance($this->diy_type);
        if (($this->data_name = $confModel->buildDataName($aUserParam)) === '') {
            $this->data_name = $this->diy_name; // Use diy name as default
        }

        $aDiyData = $confModel->parseDiyData($this->data_name, $this->data_rule, $aOperandInfo);
        $this->diy_id ? $this->update() : $this->insert();

        $this->saveDiyData($aDiyData);
        $this->saveDiyRel($aOperandInfo);
        return array(
            'type' => 'diy',
            'diy_id' => $this->diy_id,
            'diy_type' => $this->diy_type,
            'diy_name' => $this->diy_name
        );
    }

    /**
     * Find diy info by diy_id
     *
     * @return array
     */
    public function findById()
    {
        return $this->findAll(array(
            'select' => implode(',', $this->attributeNames()) .
                ',("diy") AS type,diy_id AS r_id,diy_name AS r_name,diy_type AS r_type',
            'condition' => array('diy_id' => $this->diy_id)
        ));
    }

    /**
     * Find diy info by game_id
     *
     * @return array
     */
    public function findByGameId()
    {
        return $this->findAll(array(
            'select' => implode(',', $this->attributes()) .
                ',("diy") AS type,diy_id AS r_id,diy_name AS r_name,diy_type!=1 AS is_multi',
            'condition' => array('game_id' => $this->game_id)
        ));
    }

    /**
     * Remove diy
     *
     * @return bool
     */
    public function removeDiy()
    {
        $diyDataModel = new gamecustom_DiyData();
        $diyDataModel->deleteAllByAttributes(array('diy_id' => $this->diy_id));

        $diyRelModel = new gamecustom_DiyRel();
        $diyRelModel->deleteAllByAttributes(array('diy_id' => $this->diy_id));

        // FIXME Check if its node is NODE_UNKNOWN now
        return $this->delete();
    }

    /**
     * 通过节点ID更新自定义加工项信息
     *
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
     * 通过diy_id取自定义加工项信息
     *
     * @param  integer $iDiyId
     * @return array
     */
    public function getStatItemById($iDiyId)
    {
        return $this->getDb()->createCommand()
            ->select('diy_id AS r_id,diy_type AS r_type,diy_name AS r_name,("diy") AS type,game_id,node_id')
            ->from($this->tableName())
            ->where('diy_id = ?')
            ->queryRow(array($iDiyId));
    }

    /**
     * 更新自定义加工项信息
     *
     * @param  integer $iDiyId
     * @param  array   $aFields
     * @return integer
     */
    public function updateStatItem($iDiyId, $aFields)
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            $aFields,
            'diy_id = :r_id',
            array(':r_id' => $iDiyId)
        );
    }

    /**
     * 取游戏自定义加工项
     *
     * @param  array       $aStatItemConditions
     * @param  array       $aTreeConditions
     * @param  string|null $order
     * @return array
     */
    public function getStatItemList($aStatItemConditions, $aTreeConditions = [], $sOrder = null)
    {
        $aCondition = ['AND'];
        $aParam = [];
        $aConditions = ['r' => $aStatItemConditions];
        $oCommand = $this->getDb()->createCommand()
            ->select('r.diy_id AS r_id, r.diy_type AS r_type, ("diy") AS type, r.diy_name AS r_name')
            ->from($this->tableName() . ' r');
        if (isset($sOrder)) {
            $oCommand->order($sOrder);
        } else {
            $oCommand->order('r.diy_type, r.diy_id');
        }
        if ($aTreeConditions) {
            $aConditions['t'] = $aTreeConditions;
            $oCommand->join('t_web_tree t', 'r.node_id = t.node_id');
        }
        foreach ($aConditions as $alias => $conditions) {
            foreach ($conditions as $key => $value) {
                $aCondition[] = $alias . '.' . $key . ' = ?';
                $aParam[] = $value;
            }
        }
        return $oCommand->where($aCondition)->queryAll($aParam);
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
            ->select('diy_type!=1,diy_id AS r_id,diy_name AS r_name,("diy") AS type,display_order, ctime AS add_time')
            ->from($this->tableName())
            ->where('game_id=? AND node_id=? AND status=0')
            ->order('display_order, CONVERT(diy_name USING gb2312) ASC');
        $aParam = [$aUserParam['game_id'], $aUserParam['node_id']];

        if (isset($mIsMulti)) {
            if ($mIsMulti) {
                $oCommand->andWhere('diy_type IN (?,?)');
                array_push($aParam, 2, 3); // distr and item
            } else {
                $oCommand->andWhere('diy_type=?');
                $aParam[] = 1; // basic
            }
        }

        return $oCommand->queryAll($aParam);
    }

    /**
     * 设置游戏自定义加工项名称
     *
     * @param  array $aUserParam id and name
     * @return bool|null
     */
    public function setName($aUserParam)
    {
        $this->diy_id = $aUserParam['id'];
        $diyInfo = $this->findById();
        TMValidator::ensure($diyInfo, TM::t('tongji', '自定义加工项不存在！'));
        $diyInfo = $diyInfo[0];
        $this->diy_name = $aUserParam['name'];
        $this->data_name = $diyInfo['data_name'];

        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            if ((int)$diyInfo['diy_type'] === gamecustom_diy_Base::CATEGORY_BASIC) {
                $diyDataModel = new gamecustom_DiyData();
                $diyDataModel->diy_id = $this->diy_id;
                foreach ($diyDataModel->findByDiyId() as $diyData) {
                    $diyDataModel->diy_data_id = $diyData['diy_data_id'];
                    $diyDataModel->data_name = $this->diy_name;
                    $diyDataModel->update(array('data_name'));
                }
                $this->data_name = $this->diy_name;
            }
            $this->update(array('diy_name', 'data_name'));
            $transaction->commit();
            return true;
        } catch (Exception $e) {
            $transaction->rollback();
            throw $e;
        }
    }

    /**
     * Check data_rule, return its operands
     *
     * @return array
     */
    public function checkDataRule()
    {
        $this->data_rule = $this->filterDataRule($this->data_rule);
        TMValidator::ensure($this->data_rule, TM::t('tongji', '数据表达式不能为空！'));

        $aOperands = $this->parseDataRule($this->data_rule);
        TMValidator::ensure($aOperands, TM::t('tongji', '数据表达式至少要有一个操作数！'));
        $iBitmap = 0; // 1-basic 2-distr 3-item
        $aOperandInfo = array();
        foreach ($aOperands as $operand) {
            $operandInfo = $this->checkOperand($operand, $this->game_id);
            $iBitmap |= 1 << ($operandInfo['r_type'] - 1);
            $aOperandInfo[$operand] = $operandInfo;
        }
        TMValidator::ensure((int)$this->diy_type === $this->parseDiyType($iBitmap),
            TM::t('tongji', '加工项类型设置不正确！'));

        return $aOperandInfo;
    }

    /**
     * Filter data_rule
     *
     * @param  string $dataRule
     * @return string
     */
    protected function filterDataRule($dataRule)
    {
        $dataRule = htmlentities($dataRule, null, 'UTF-8');
        return str_replace(array('&nbsp;', ' '), '', $dataRule);
    }

    /**
     * Parse data_rule to get operands
     *
     * @param  string $dataRule
     * @return array
     */
    protected function parseDataRule($dataRule)
    {
        $pattern = '/^([\+\-\*\/\(\)]|\d*\.\d+|\d+\.\d*|\d+|' .
            '\[(?:set|report)_\d+(?:_[^\]]+?)?\])/';
        try {
            TM::import('system.notation.*');
            $scanner = new TMNotationScanner($dataRule, $pattern);
            $parser = new TMNotationParser($scanner);
            $parser->parse(null, true);
            return array_unique($scanner->getIdents());
        } catch (Exception $e) {
            TMValidator::ensure(false, TM::t('tongji', '数据表达式语法不正确，{err}', array('{err}' => $e->getMessage())));
        }
    }

    /**
     * Check operand
     *
     * @param  string $operand
     * @param  int    $gameId
     * @return array
     */
    protected function checkOperand($operand, $gameId)
    {
        $parts = explode('_', trim($operand, '[]'), 2);
        $method = 'check' . ucfirst($parts[0]) . 'Operand';
        try {
            if (method_exists($this, $method)) {
                // checkSetOperand or checkReportOperand
                return $this->$method($parts[1], $gameId);
            }
            TMValidator::ensure(false, TM::t('tongji', '操作数{operand}不合法！'));
        } catch (TMValidatorException $e) {
            TMValidator::ensure(false, str_replace('{operand}', $operand, $e->getMessage()));
        }
    }

    /**
     * Check set operand
     *
     * @param  string $key
     * @param  int    $gameId
     * @return array
     */
    protected function checkSetOperand($key, $gameId)
    {
        $parts = explode('_', $key, 2);
        TMValidator::ensure(isset($parts[1]), TM::t('tongji', '{operand}格式不正确！'));
        $setId = $parts[0];

        $parts = explode(':', $parts[1], 2);
        TMValidator::ensure(isset($parts[1]), TM::t('tongji', '{operand}格式不正确！'));
        $dataId = $parts[0];
        $dataExpr = $parts[1];

        $setData = $this->getOperandModel('set')->findAll(array('condition' => array(
            'set_id' => $setId,
            'game_id' => $gameId,
            'data_id' => $dataId,
            'data_expr' => $dataExpr
        )));
        TMValidator::ensure($setData, TM::t('tongji', '{operand}不存在！'));
        $setData = $setData[0];
        $setData['type'] = 'set';
        $setData['r_id'] = $setData['set_id'];
        $setData['r_name'] = $setData['data_name'];
        $setData['r_type'] = 1;
        $setData['r_data_id'] = $dataId . ':' . $dataExpr;
        return $setData;
    }

    /**
     * Check report operand
     *
     * @param  string $key
     * @param  int    $gameId
     * @return array
     */
    protected function checkReportOperand($reportId, $gameId)
    {
        $reportModel = $this->getOperandModel('report');
        $report = $reportModel->getStatItemById($reportId);
        TMValidator::ensure($report, TM::t('tongji', '{operand}不存在！'));
        TMValidator::ensure((int)$report['game_id'] === (int)$gameId, TM::t('tongji', '{operand}游戏不一致！'));
        $report = $reportModel->setCategory($report);
        return $report;
    }

    /**
     * Get operand model
     *
     * @param  string $type
     * @return object
     */
    protected function getOperandModel($type)
    {
        if (isset($this->operandModels[$type])) {
            return $this->operandModels[$type];
        }

        switch ($type) {
            case 'set':
                $class = 'module_SetData';
                break;

            case 'report':
                $class = 'common_Report';
                break;

            default:
                TMValidator::ensure(false, TM::t('tongji', '未知类型{type}！', array('{type}' => $type)));
                break;
        }

        return $this->operandModels[$type] = new $class();
    }

    /**
     * Parse diy_type
     *
     * @param  int $iBitmap
     * @return int
     */
    protected function parseDiyType($iBitmap)
    {
        // 1-basic 2-distr 3-item
        TMValidator::ensure(!($iBitmap & 4) || !($iBitmap & 2), TM::t('tongji', '操作数不能同时为item和distr！'));
        $diyType = null;
        foreach (array(3, 2, 1) as $rType) {
            if ($iBitmap & (1 << ($rType - 1))) {
                $diyType = $rType;
                break;
            }
        }
        TMValidator::ensure($diyType !== null, TM::t('tongji', '自定义加工项类型不正确！'));

        return $diyType;
    }

    /**
     * Check if the node is type unknown, update it to type leaf
     *
     * @return null
     */
    protected function checkNodeUnknown()
    {
        // 更新结点的is_leaf为NODE_LEAF
        $tree = new gamecustom_Tree();
        $node = $tree->getNodeById($this->node_id);
        if ((int)$node['is_leaf'] === gamecustom_Tree::NODE_UNKNOWN) {
            $tree->node_id = $this->node_id;
            $tree->is_leaf = gamecustom_Tree::NODE_LEAF;
            $tree->update(array('is_leaf'));
        }
    }

    /**
     * Save diy data
     *
     * @param  array $diyData
     * @return null
     */
    protected function saveDiyData($diyData)
    {
        $diyDataIds = array();
        $displayOrder = 0;
        foreach ($diyData as $data) {
            $diyDataModel = new gamecustom_DiyData();
            $diyDataModel->attributes = $data;
            $diyDataModel->display_order = $displayOrder++;
            $diyDataModel->diy_id = $this->diy_id;
            $diyDataModel->game_id = $this->game_id;
            $diyDataIds[] = $diyDataModel->saveData();
        }

        $diyDataModel = new gamecustom_DiyData();
        $diyDataModel->diy_id = $this->diy_id;
        $diyDataModel->deleteByDiyId($diyDataIds);
    }

    /**
     * Save diy relations
     *
     * @param  array $aOperandInfo
     * @return null
     */
    protected function saveDiyRel($aOperandInfo)
    {
        $diyRelModel = new gamecustom_DiyRel();
        $diyRelModel->deleteAllByAttributes(array('diy_id' => $this->diy_id));
        foreach ($aOperandInfo as $operand => $info) {
            $diyRelModel = new gamecustom_DiyRel();
            $diyRelModel->attributes = $info;
            $diyRelModel->diy_id = $this->diy_id;
            $diyRelModel->insert();
        }
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

        $aChildren = $this->getContentList($aInfo, (int)$aInfo['r_type'] !== 1);
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
        $dataModel = new gamecustom_DiyData();
        $dataModel->diy_id = $aInfo['r_id'];
        $aDataList = TMArrayHelper::column($dataModel->findByDiyId(), null, 'diy_data_id');

        $aNewList = (array)$aUserParam['id'];
        $iOrder = 1;
        foreach ($aNewList as $dataId) {
            if (!isset($aDataList[$dataId])) continue;
            $dataModel->diy_data_id = $dataId;
            $dataModel->display_order = $iOrder++;
            $dataModel->update(['display_order']);
            unset($aDataList[$dataId]);
        }

        foreach($aDataList as $dataId => $dataInfo) {
            $dataModel->diy_data_id = $dataId;
            $dataModel->delete();
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

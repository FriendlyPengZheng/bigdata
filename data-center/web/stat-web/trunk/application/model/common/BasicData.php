<?php
class common_BasicData extends TMFormModel
{
    public function tableName()
    {
        return 't_web_basic_data';
    }

    public function rules()
    {
        return array(
            array('data_name', 'string', 'max' => 255, 'min' => 1),
            array('type', 'enum', 'range' => array(1, 2)),
            array('stid,sstid,op_fields,range', 'string', 'allowEmpty' => true, 'max' => 64),
            array('op_type', 'enum', 'allowEmpty' => true,
                'range' => array('ucount', 'count', 'sum', 'max', 'set', 'distr_sum', 'distr_max', 'distr_set', 'ip_distr')),
            array('period', 'enum', 'range' => array(1, 2, 3, 4, 5, 6)),
            array('unit', 'string', 'allowEmpty' => true, 'max' => 10),
            array('type', 'checkExists'),
            array('comment_id', 'checkCommentId')
        );
    }

    public function getPeriodGroupedList()
    {
        return $this->getDb()->createCommand()
            ->setFetchMode(PDO::FETCH_ASSOC|PDO::FETCH_GROUP)
            ->select('period,' . implode(',', $this->attributes())) /* 以period分组 */
            ->from($this->tableName())
            ->queryAll();
    }

    public function getListWithComment()
    {
        $list = $this->getDb()->createCommand(
            'SELECT d.basic_id, d.data_name, d.type, d.stid, d.sstid, d.op_fields, d.op_type, ' .
                'd.task_id, d.range, d.period, d.factor, d.precision, d.unit, d.comment_id, c.comment ' .
            'FROM ' . $this->tableName() . ' d ' .
            'LEFT JOIN ' . (new common_Comment())->tableName() . ' c ' .
            'ON d.comment_id = c.comment_id'
        )->queryAll();
        return $this->formatList($list);
    }

    public function checkCommentId($value, $attribute, $params)
    {
        if ($value) {
            if (!$this->comment) {
                $this->comment_id = 0;
                return true;
            }
            $oComment = new common_Comment();
            $aComment = $oComment->findAll(array('condition' => array('comment_id' => $value)));
            if (!$aComment) return false;
            $oComment->comment_id = $value;
            $oComment->comment = $this->comment;
            $oComment->keyword = $this->data_name;
            return $oComment->update();
        }
        if (!$this->comment) return true;

        $oComment = new common_Comment();
        $oComment->comment = $this->comment;
        $oComment->keyword = $this->data_name;
        return $oComment->insert() && $this->comment_id = $oComment->comment_id;
    }

    public function checkExists($value, $attribute, $params)
    {
        $condition = array();
        if ($value == common_Stat::TYPE_REPORT) {
            $condition['stid']      = $this->stid;
            $condition['sstid']     = $this->sstid;
            $condition['op_fields'] = $this->op_fields;
            $condition['op_type']   = $this->op_type;
        } else {
            $condition['task_id'] = $this->task_id;
        }
        $condition['type']   = $value;
        $condition['range']  = $this->range;
        $condition['period'] = $this->period;

        $basic = $this->findAll(array('condition' => $condition));
        TMValidator::ensure(
            !$basic || ($this->basic_id && $basic[0]['basic_id'] == $this->basic_id),
            TM::t('tongji', '已存在相同的元数据！')
        );
        return true;
    }

    public function updateByCommentId($iCommentId, $aFields)
    {
        return $this->getDb()->createCommand()->update(
            $this->tableName(),
            $aFields,
            'comment_id=:id',
            array(':id' => $iCommentId)
        );
    }

    public function findComments($dataInfo)
    {
        $union = $param = array();
        $common = 'SELECT c.* FROM ' . $this->tableName() . ' b INNER JOIN ' . (new common_Comment())->tableName() .
            ' c ON b.comment_id = c.comment_id WHERE b.type = ? ';
        foreach ($dataInfo as $infoUrl) {
            $info = array();
            parse_str($infoUrl, $info);
            if (!$info || !isset($info['type'])) continue;
            $sql = $common;
            $param[] = $info['type'];
            if ($info['type'] == common_Stat::TYPE_REPORT) {
                if (!isset($info['stid'], $info['sstid'], $info['op_fields'], $info['op_type'])) continue;
                $sql .= 'AND b.stid = ? AND b.sstid = ? AND b.op_fields = ? AND b.op_type = ? ';
                array_push($param, $info['stid'], $info['sstid'], $info['op_fields'], $info['op_type']);
            } else {
                if (!isset($info['task_id'])) continue;
                $sql .= 'AND b.task_id = ? ';
                $param[] = $info['task_id'];
            }
            $sql .= 'AND b.range = ? AND b.period = ?';
            $param[] = TMArrayHelper::assoc('range', $info, '');
            $param[] = TMArrayHelper::assoc('period', $info, data_time_PeriodFactory::TYPE_DAY);
            $union[] = $sql;
        }
        if (!$union) return;

        return $this->getDb()->createCommand('(' . implode(') UNION ALL (', $union) . ')')->queryAll($param);
    }

    protected function formatList($list)
    {
        $periods = data_time_PeriodFactory::getPeriodConfigure();
        foreach ($list as &$data) {
            $data['period_name'] = TMArrayHelper::assoc($data['period'], $periods, '');
            $data['type_name'] = common_Stat::type2string($data['type']);
        }
        return $list;
    }
}

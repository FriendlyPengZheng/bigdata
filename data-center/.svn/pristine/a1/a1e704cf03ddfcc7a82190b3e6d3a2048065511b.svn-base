<?php
class common_EmailData extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_email_data';
    }

    /**
     * 获取参数检测的配置
     *
     * @return array
     */
    public function rules()
    {
        return array(
            array('email_content_id, data_expr, data_name, gpzs_id', 'required'),
            array('data_expr', 'checkDataExpr'),
            array('data_name', 'string', 'min' => 1, 'max' => 30),
            array('data_date_type', 'default', 'value' => 'DAY'),
            array('data_date_type', 'enum', 'range' => array('MINUTE', 'DAY', 'MONTH')),
            array('offset', 'default', 'value' => 0),
            array('offset', 'number'),
            array('unit', 'string', 'allowEmpty' => true, 'max' => 30),
            array('in_table', 'enum', 'range' => array(0, 1), 'defaultValue' => 1),
            array('in_graph', 'enum', 'range' => array(0, 1), 'defaultValue' => 1)
        );
    }

    /**
     * 检测data_expr字段
     * 形如：145002;145076|{0}/{1}/100
     *
     * @param  string  $dataExpr
     * @return boolean
     */
    public function checkDataExpr($dataExpr)
    {
        $aDataExpr = explode('|', $dataExpr);
        if (count($aDataExpr) !== 2) return false;
        if (!preg_match('/^\d+(;\d+)*$/', $aDataExpr[0])) return false;
        return true;
    }

    /**
     * 获取邮件数据
     *
     * @param  integer $iEmailId
     * @return array
     */
    public function findByEmailId($iEmailId)
    {
        return $this->getDb()->createCommand()
            ->select('b.content_title,a.email_data_id,a.data_date_type,a.data_expr,a.data_name,a.gpzs_id,' .
                'a.offset,a.unit,a.in_table,a.in_graph')
            ->from($this->tableName() . ' a')
            ->join('t_web_email_content b', 'a.email_content_id=b.email_content_id')
            ->where('email_id = ?')
            ->order('order,data_order DESC,email_data_id ASC')
            ->QUERYALL(array((int)$iEmailId));
    }

    /**
     * 删除前需要检测其email_content_id是否包含多个数据，如果没有的话，删除对应的email_content
     *
     * @return boolean
     */
    public function beforeDelete()
    {
        $iEmailContentId = $this->getDb()->createCommand()
            ->select($this->attributes())
            ->from($this->tableName())
            ->where('email_data_id = ?')
            ->queryScalar(array((int)$this->email_data_id));
        if ($iEmailContentId) {
            $iEmailDataCount = $this->getDb()->createCommand()
                ->select('COUNT(1)')
                ->from($this->tableName())
                ->where('email_content_id = ?')
                ->queryScalar(array($iEmailContentId));
            if ($iEmailDataCount == 1) {
                return (new common_EmailContent())->deleteAllByAttributes(array(
                    'email_content_id' => array($iEmailContentId)
                ));
            }
        }
        return true;
    }
}

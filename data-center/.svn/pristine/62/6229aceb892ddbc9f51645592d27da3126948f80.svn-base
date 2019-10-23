<?php
class common_EmailTemplateData extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_email_template_data';
    }

    /**
     * 获取参数检测的配置
     *
     * @return array
     */
    public function rules()
    {
        return array(
            array('data_expr, data_name', 'required'),
            array('data_date_type', 'default', 'value' => 'DAY'),
            array('data_date_type', 'enum', 'range' => array('MINUTE', 'DAY', 'MONTH')),
            array('data_expr', 'checkDataExpr'),
            array('data_name', 'string', 'min' => 1, 'max' => 30),
            array('offset', 'default', 'value' => 0),
            array('offset', 'number'),
            array('unit', 'string', 'allowEmpty' => true, 'max' => 30),
            array('in_table', 'enum', 'range' => array(0, 1), 'defaultValue' => 1),
            array('in_graph', 'enum', 'range' => array(0, 1), 'defaultValue' => 1)
        );
    }

    /**
     * 检测data_expr字段
     *
     * @param  string  $dataExpr
     * @return boolean
     */
    public function checkDataExpr($dataExpr)
    {
        echo $dataExpr;
    }
}

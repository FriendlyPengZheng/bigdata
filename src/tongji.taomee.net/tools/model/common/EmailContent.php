<?php
class common_EmailContent extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_email_content';
    }

    /**
     * 获取参数检测的配置
     * @return array
     */
    public function rules()
    {
        return array(
            array('email_id', 'required'),
            array('content_type', 'enum',
                'range' => array('MIXED', 'TABLE', 'MULTI_GAME_TABLE', 'EXCEL'), 'defaultValue' => 'MIXED')
        );
    }

    public function beforeDelete()
    {
        $model = new common_EmailData();
        return $model->deleteAllByAttributes(array(
            'email_content_id' => $this->email_content_id
        ));
    }
}

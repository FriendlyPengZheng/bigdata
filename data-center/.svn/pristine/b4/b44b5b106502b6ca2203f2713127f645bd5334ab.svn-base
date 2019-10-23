<?php
class common_EmailTemplate extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_email_template';
    }

    /**
     * 获取参数检测的配置
     * @return array
     */
    public function rules()
    {
        return array(
            array('email_template_id', 'exist'),
            array('email_template_content_id', 'number'),
            array('template_type', 'enum')
        );
    }
}

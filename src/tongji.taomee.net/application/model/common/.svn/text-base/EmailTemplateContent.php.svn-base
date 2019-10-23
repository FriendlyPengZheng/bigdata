<?php
class common_EmailTemplateContent extends TMFormModel
{
    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_email_template_content';
    }

    /**
     * 获取参数检测的配置
     * @return array
     */
    public function rules()
    {
        return array(
            array('email_template_content_id', 'exist'),
            array('order', 'number'),
            array('content_type', 'enum')
        );
    }
}

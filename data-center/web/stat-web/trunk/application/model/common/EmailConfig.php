<?php
class common_EmailConfig extends TMFormModel
{
    /**
     * 邮件状态
     */
    const NORMAL = 0;
    const AUTO_SEND = 1;

    const FREQUENCY_DAILY = 'DAILY';
    const FREQUENCY_MONTHLY = 'MONTHLY';

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_web_email_config';
    }

    /**
     * 获取参数检测的配置
     *
     * @return array
     */
    public function rules()
    {
        return array(
            array('receviers', 'checkMultiEmail',
                'pattern' => '/[a-z0-9_\-\+]+@[a-z0-9\-]+\.([a-z]{2,3})(?:\.[a-z]{2})?/i'),
            array('test_receiver', 'checkMultiEmail',
                'pattern' => '/[a-z0-9_\-\+]+@[a-z0-9\-]+\.([a-z]{2,3})(?:\.[a-z]{2})?/i', 'allowEmpty' => true),
            array('cc', 'checkMultiEmail',
                'pattern' => '/[a-z0-9_\-\+]+@[a-z0-9\-]+\.([a-z]{2,3})(?:\.[a-z]{2})?/i', 'allowEmpty' => true),
            array('subject', 'string', 'min' => 1, 'max' => 150),
			array('remarks', 'string', 'max' => 1000),
			array('weixin_recev', 'string', 'allowEmpty' => true),
			array('weixin_media_id', 'string', 'allowEmpty' => true),
            array('frequency_type', 'enum',
                'range' => array(self::FREQUENCY_DAILY,self::FREQUENCY_MONTHLY), 'allowEmpty' => true, 'defaultValue' => 'DAILY'),
            array('frequency', 'default', 'value' => 0),
            array('frequency', 'number'),
            array('status', 'enum', 'range' => array(self::AUTO_SEND, self::NORMAL))
        );
    }

    /**
     * 检测多个email字符串
     *
     * @param  string  $emails
     * @param  string  $attribute
     * @param  array   $params
     * @return boolean
     */
    public function checkMultiEmail($emails, $attribute, $params)
    {
        if (!isset($params['allowEmpty'])) $params['allowEmpty'] = false;
        if ($params['allowEmpty'] && empty($emails)) return true;
        if (!preg_match_all($params['pattern'], $emails, $matches)) {
            return false;
        }
        $this->$attribute = implode(';', $matches[0]);
        return true;
    }

    public function beforeDelete()
    {
        $model = new common_EmailContent();
        $aContents = $model->findAll(array(
            'condition' => array(
                'email_id' => (int)$this->email_id
            )
        ));
        $contentId = array();
        foreach ($aContents as $content) {
            $contentId[] = $content['email_content_id'];
        }
        $model->setPrimaryKey($contentId);
        return $model->delete();
    }

    /**
     * 更新最后一次发送邮件信息
     *
     * @param  integer $iEmailId
     * @param  string  $sUser
     * @return boolean
     */
    public function updateLastSend($iEmailId, $sUser)
    {
        $this->email_id = $iEmailId;
        $this->last_send_time = time();
        $this->last_send_user = $sUser;
        return $this->update(array('last_send_time', 'last_send_user'));
	}
	/**
	 * 更新微信mediaId
	 * @param integer
	 * @param string
	 * @return boolean
	 */
	public function updateMediaId($iEmailId, $mediaid)
	{
		$this->email_id = $iEmailId;
		$this->weixin_media_id = $mediaid;
		return $this->update(array('weixin_media_id'));
	}
}

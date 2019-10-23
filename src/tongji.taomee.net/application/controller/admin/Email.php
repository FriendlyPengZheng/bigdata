<?php
class Email extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(
                'email_id' => null
            ),
            'manage' => array(
                'view_r' => null,
                'email_id' => null
            ),
            'apply' => array(
                'receviers' => null,
                'test_receiver' => null,
                'cc' => null,
                'subject' => null,
				'remarks' => null,
				'weixin_recev' => null,
				'weixin_media_id' => null,
                'gpzs_id' => null,
                'game_id' => null,
                'frequency_type' => common_EmailConfig::FREQUENCY_DAILY
            ),
            'updateEmailConfig' => array(
                'receviers' => null,
                'test_receiver' => null,
                'cc' => null,
                'subject' => null,
                'remarks' => null,
                'dependencies' => null,
				'email_id' => null,
				'weixin_recev' => null,
				'weixin_media_id' => null
            ),
            'getEmailConfig' => array(
                'email_id' => null
            ),
            'delete' => array(
                'email_id' => null
            ),
            'setStatus' => array('email_id' => null, 'status' => null),
            'addTemplate' => array(
                'data_date_type' => null,
                'data_expr' => null,
                'data_name' => null,
                'offset' => null,
                'unit' => null,
                'in_table' => null,
                'in_graph' => null
            ),
            // 发送邮件
			'send' => array('email_id' => null, 'is_test' => 0),
			'sendWeixin'=>array('email_id' => null, 'is_test' => 0),
            // 重命名邮件数据
            'renameData' => array(
                'email_data_id' => null,
                'data_name' => null
            ),
            // 删除邮件数据
            'deleteData' => array('email_data_id' => null),
            // 设置阈值
            'setThreshold' => array('email_data_id' => null, 'threshold' => null)
        );
    }

    /**
     * 显示页面
     */
    public function index($aUserParameters)
    {
        $aEmailList  = (new common_EmailConfig())->findAll([
            'order' => 'order'
        ]);
        if (!$aEmailList) {
            $this->assign('email', array());
            $this->assign('param', array(
                'email_id' => 0,
                'receviers' => '',
                'test_receiver' => '',
                'last_send_time' => '-',
                'frequency_type' => '-'
            ));
            $this->assign('datalist', array());
            $this->display('kernel/daydata.html');
        }

        $iCurrentKey = 0;
        foreach ($aEmailList as $key => &$email) {
            $email['current'] = 0;
            $email['url'] = TM::app()->getUrlManager()->rebuildUrl(
                'admin/email/index/01', array('email_id' => $email['email_id'])
            );
            $email['last_send_time'] = $email['last_send_time'] ? date('Y-m-d H:i:s', $email['last_send_time']) : '-';
            if ((int)$aUserParameters['email_id'] === (int)$email['email_id']) $iCurrentKey = $key;
        }
        $aEmailList[$iCurrentKey]['current'] = 1;
        $this->assign('email', $aEmailList);
        $this->assign('param', $aEmailList[$iCurrentKey], true);
        $aUserParameters['email_id'] = $aEmailList[$iCurrentKey]['email_id'];
        $this->assign('datalist', $this->_getDataList($aUserParameters));
        $this->display('kernel/daydata.html');
    }

    /**
     * 获取一封Email下所有数据列表
     */
    private function _getDataList(&$aUserParameters)
    {
        return (new common_EmailData())->findByEmailId($aUserParameters['email_id']);
    }

    /**
     * 显示管理页面
     */
    public function manage()
    {
        $this->assign('emaillist', (new common_EmailConfig())->findAll([
            'order' => 'order'
        ]));
        $this->display('kernel/manage.html');
    }

    /**
     * 根据result_id/report_id列表获取data_id列表
     */
    private function _getDataIdsByRids($aItems)
    {
        $dataInfo  = new common_DataInfo();
        $aDataInfo = $dataInfo->getDataListByRids($aItems);
        $aOrdered  = array();
        foreach ($aItems as $rid) {
            $key = $rid['r_id'] . ':' . $rid['type'];
            isset($aDataInfo[$key]) && ($aOrdered[] = $aDataInfo[$key]);
        }
        return $aOrdered;
    }

    /**
     * 根据stid,sstid,op_type,op_fields,range/task_id,range获取data_id列表
     */
    private function _getDataIds($iGameId, $sTemplateExpr)
    {
        $aExprs = explode('|', $sTemplateExpr);
        isset($aExprs[2]) && $aExprs[1] = $aExprs[1] . '|' . $aExprs[2];
        $aUniqueKeys = explode(';', $aExprs[0]);
        $aRInfo   = array();
        $aRRanges = array();
        $i = 0;
        foreach ($aUniqueKeys as $unique) {
            $u = explode(':', $unique);
            if ($u[0] == 1) {
                if (isset($u[5])) $aRRanges[$i] = array_pop($u);
                $aRInfo[$i] = array_combine(array('type', 'stid', 'sstid', 'op_type', 'op_fields'), $u);
            } else {
                if (isset($u[2])) $aRRanges[$i] = array_pop($u);
                $aRInfo[$i] = array_combine(array('type', 'task_id'), $u);
            }
            $i ++;
        }
        $aItems = common_Stat::getStatByUk($iGameId, $aRInfo);
        if (!$aItems || count($aItems) !== count($aRInfo)) return null;

        $aDataInfo = $this->_getDataIdsByRids($aItems);
        if (!$aDataInfo || count($aDataInfo) !== count($aRInfo)) return null;

        $bMulti  = false;
        $aHandle = array();
        foreach ($aDataInfo as $index => $data) {
            if (count($data) > 1 && !isset($aRRanges[$index])) {
                if ($bMulti) { return null; }
                $bMulti = true;
            }
            foreach ($data as $i => $d) {
                if (isset($aRRanges[$index]) && $d['range'] != $aRRanges[$index]) continue;
                if (!$bMulti) $i = 0;
                if ($i > 0) {
                    $aHandle[$i] = $aHandle[0];
                }
                $aHandle[$i][$index] = $d['data_id'];
            }
        }
        foreach ($aHandle as &$h) {
            $h = implode(';', $h) . '|' . $aExprs[1];
        }
        return $aHandle;
    }

    /**
     * 添加Email配置
     */
    private function _addEmail($aUserParameters)
    {
        $model = new common_EmailConfig();
        $model->attributes = $aUserParameters;
        $model->insert();
        $emailId = $model->getPrimaryKey();
        $model->order = $emailId;
        $model->update(array('order'));
        return $emailId;
    }

    /**
     * 修改Email配置
     */
    public function updateEmailConfig($aUserParameters)
    {
        $model = new common_EmailConfig();
        $model->attributes = $aUserParameters;
        $this->ajax(0, $model->update(array('receviers', 'test_receiver', 'cc', 'subject', 'remarks', 'dependencies','weixin_recev', 'weixin_media_id')));
    }

    /**
     * 获取Email配置
     */
    public function getEmailConfig($aUserParameters)
    {
        $this->ajax(0, $this->_getEmailById($aUserParameters['email_id']));
    }

    /**
     * 根据模板创建Email包括其数据
     */
    public function apply($aUserParameters)
    {
        $iEmailId = $this->_addEmail($aUserParameters);
        $modelType = new common_EmailTemplate();
        $model = new common_EmailTemplateContent();
        $template = new common_EmailTemplateData();
        $emailContent = new common_EmailContent();
        $emailData = new common_EmailData();
        $emailContent->email_id = $iEmailId;
        $emailData->gpzs_id = $aUserParameters['gpzs_id'];
        $templateContent = $modelType->findAll(array(
                            'condition' => array(
                                'template_type' => $aUserParameters['frequency_type']
                            )
                        ));
        $aContent = $model->findAll(array(
                    'condition' => array(
                        'email_template_id' => $templateContent[0]['email_template_id']
                        )
                    ));
        foreach ($aContent as $content) {
            $allData = $template->findAll(array(
                        'condition' => array(
                            'email_template_content_id' => $content['email_template_content_id']
                            )
                        ));
            if (!$allData) continue;
            $emailContent->attributes = $content;
            $emailContent->insert();
            $emailData->email_content_id = $emailContent->getPrimaryKey();
            foreach ($allData as $data) {
                if (!($dataIds = $this->_getDataIds($aUserParameters['game_id'], $data['data_expr']))) continue;
                foreach ($dataIds as $expr) {
                    $data['data_expr'] = $expr;
                    $emailData->attributes = $data;
                    $emailData->insert();
                    $emailData->email_data_id = null;
                }
            }
            $emailContent->email_content_id = null;
        }
        $this->ajax(0, array('email_id' => $iEmailId));
    }

    /**
     * 删除Email配置及其数据配置
     */
    public function delete($aUserParameters)
    {
        $model = new common_EmailConfig();
        $model->setPrimaryKey($aUserParameters['email_id']);
        $this->ajax(0, $model->delete());
    }

    /**
     * Set email status, auto send or not
     */
    public function setStatus($aUserParameters)
    {
        $model = new common_EmailConfig();
        $model->email_id = (int)$aUserParameters['email_id'];
        $model->status = (int)$aUserParameters['status'];
        $model->update(array('status'));
        $this->ajax(0, $model->status);
    }

    /**
     * Send the email.
     *
     * @param  array $aUserParameters
     * @return array
     */
    public function send($aUserParameters)
    {
        $aEmail = $this->_getEmailById($aUserParameters['email_id']);
        $aTo = array_filter(explode(';', $aUserParameters['is_test'] ? $aEmail['test_receiver'] : $aEmail['receviers']));
        TMValidator::ensure($aTo, TM::t('tongji', '邮件收件人不能为空！'));
        $mail = TM::app()->mailer->createMessage()->setTo($aTo);
        if (!$aUserParameters['is_test']) {
            if ($aCc = array_filter(explode(';', $aEmail['cc']))) $mail->setCc($aCc);
        }
        $aContent = $this->_getContentByEmailId($aUserParameters['email_id']);
        $aMessage = $this->_getMessage($aContent);
        $aParam = $this->_manageParams($aEmail, $aMessage);
        isset($aParam['fullFilePath']) && ($mail = $mail->attachFromPath($aParam['fullFilePath']));
        $failedRecipients = array();
        $mail->setBody(
                TM::app()->template->clearAllAssign()->assign(array(
                        'mail' => $mail,
                        'avatarPath' => TM::app()->getBasePath() . DS .
                        'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                        'subject' => $aParam['subject'],
                        'date' => $aParam['date'],
                        'content' => $aMessage['content'],
                        'remarks' => explode("\n", $aParam['eRemarks_top']),
                        'remarkss' => explode("\n",$aParam['eRemarks_bot']),
                        ))->fetch($aParam['template']), 'text/html'
                )->setSubject($aParam['subject'])->send($failedRecipients);
        $this->_clearGraph($aMessage);
        if ($failedRecipients) $this->ajax(1, '没能成功发送给' . implode(',', $failedRecipients));

        $model = new common_EmailConfig();
        $model->updateLastSend($aUserParameters['email_id'], TM::app()->getUser()->getUserName());
        $this->ajax(0, array(
            'receviers'      => $aEmail['receviers'],
            'test_receiver'  => $aEmail['test_receiver'],
            'last_send_time' => date('Y-m-d H:i:s', $model->last_send_time),
            'last_send_user' => $model->last_send_user
        ));
    }

    /**
     * @brief _manageParams
     *
     * @param {array} $aEmail
     * @param {array} $aMessage
     *
     * @return
     */
    private function _manageParams($aEmail, $aMessage)
    {
        $aParam = array();
        $eRemarks = explode("|", $aEmail['remarks']);
        $aParam['eRemarks_top'] = $eRemarks[0];
        $aParam['eRemarks_bot'] = $eRemarks[1];
        $aParam['date'] = $eRemarks[2];
        if (!empty($aMessage['attachment'])) {
            $aParam['dependencies'] = json_decode($aEmail['dependencies']);
            $aFileInfo = $this->_getAttachment($aMessage['attachment'], $aParam);
            !empty($aFileInfo) && $aParam['fullFilePath'] = $aFileInfo['dirname'] . DIRECTORY_SEPARATOR . $aFileInfo['basename'];
        }
        switch($aEmail['frequency_type']) {
            case EmailCode::MONTHLY:
                $aParam['date']    = date('Ym', strtotime($aParam['date']));
                $aParam['subject'] = $aEmail['subject'] . DS . $aParam['date'];
                $aParam['template'] = 'kernel/monthly-report.html';
                break;
            case EmailCode::DAILY:
                $aParam['date']    = date('Y-m-d', strtotime($aParam['date']));
                $aParam['subject'] = $aEmail['subject'] . '日报' . DS . $aParam['date'];
                $aParam['template'] = 'kernel/daily-report.html';
                break;
            default:
        }
        return $aParam;
    }

    /**
     * Get email info by email_id, required or not.
     *
     * @param  integer $iEmailId
     * @param  boolean $required
     * @return array
     */
    private function _getEmailById($emailId, $required = true)
    {
        $aEmail = (new common_EmailConfig())->findAll(array(
            'condition' => array('email_id' => (int)$emailId)
        ));
        if ($required) {
            TMValidator::ensure($aEmail, TM::t('tongji', '邮件配置不存在！'));
        }
        return $aEmail[0];
    }

    /**
     * Get email content by email_id, required or not.
     *
     * @param  integer $iEmailId
     * @param  boolean $required
     * @return array
     */
    private function _getContentByEmailId($emailId, $required = true)
    {
        $aContent = (new common_EmailContent())->findAll(array(
            'condition' => array('email_id' => (int)$emailId),
            'order' => 'order'
        ));
        if ($required) {
            TMValidator::ensure($aContent, TM::t('tongji', '邮件内容不存在！'));
		}
        return $aContent;
    }

    /**
     * Get message.
     *
     * @param  array   $aContent
     * @param  boolean $required
     * @return array
     */
    private function _getMessage($aContent, $required = true)
    {
        $emailModel = new common_EmailData();
        $dataModel = new data_Data();
        $aMessage = array(
            'content' => array(),
            'attachment' => array()
        );
        $param = array('condition' => array());
        foreach ($aContent as $key => $content) {
            $isAttachment = false;
			$param['condition']['email_content_id'] = $content['email_content_id'];
			$param['condition']['remove_flag'] = 0;
            $param['order'] = 'data_order DESC,email_data_id';
			$list = $emailModel->findAll($param);
            if (!$list) continue;
            $message = null;
            switch ($content['content_type']) {
                case 'TABLE':
                    $message = $this->_getTableContent($dataModel, $list);
                    break;

                case 'MULTI_GAME_TABLE':
                    $message = $this->_getTableContent($dataModel, $list, $content['content_title']);
                    break;

                case 'EXCEL':
                    $isAttachment = true;
                    $message = $this->_getExcelContent($dataModel, $list, $content['content_title']);
                    break;

                default:
                    $message = $this->_getMixedContent($dataModel, $list);
                    break;
            }
            if ($message) {
                if ($isAttachment) {
                    $aMessage['attachment'][] = $message;
                } else {
                    $aMessage['content'][] = $message;
                }
            }
        }
        if ($required) {
            TMValidator::ensure($aMessage, '邮件内容不存在！');
		}
        return $aMessage;
	}

	/************************************************************************/
	/* 发送微信 */
	public function sendWeixin($aUserParameters)
	{
		$aEmail = $this->_getEmailById($aUserParameters['email_id']);
		TMValidator::ensure($aEmail['weixin_recev'],TM::t('tongji','微信收件人不能为空！'));
		$aContent = $this->_getContentByEmailId($aUserParameters['email_id']);
		$wMessage = $this->_getWeixinMessage($aContent);
		$wContent = TM::app()->template->clearAllAssign()->assign(array('content' => $wMessage))->fetch('kernel/weixin-report.html');
		$weixin_model = new common_WeixinData();
		if(strstr($aEmail['subject'],'各游戏汇总') == false){
			$s_res = $weixin_model->sendWeixin($aEmail,$wContent,$wMessage[0]['digest'],$aUserParameters['is_test']);
		}else{
			$senddigest = null;
			foreach($wMessage as $wm){
				$senddigest .= $wm['digest'].' ';
			}
			$s_res = $weixin_model->sendWeixin($aEmail,$wContent,$senddigest,$aUserParameters['is_test']);
		}
		if($s_res['errmsg'] == "ok"){
			$this->ajax(0,array());
		}else{
			$this->ajax(1,$s_res['errmsg']);
		}
	}
    private function _getWeixinMessage($aContent,$required = true)
	{
		$emailModel = new common_EmailData();
		$dataModel = new data_Data();
		$aMessage = array();
		$param = array('condition' => array());
		foreach ($aContent as $content){
			$param['condition']['email_content_id'] = $content['email_content_id'];
			$param['condition']['remove_flag'] = 0;
			$list = $emailModel->findAll(array_merge(array("order" => "data_order DESC,email_data_id"),$param));
			if(!$list)continue;
			$message = null;
			switch($content['content_type']){
				case 'TABLE':
					$message = $this->_getTableContente($dataModel,$list);
					break;
				case 'MULTI_GAME_TABLE':
					$message = $this->_getTableContente($dataModel,$list,$content['content_title']);
					break;
				default:
					$message = $this->_getMixedContent($dataModel,$list);
					break;
			}
			if($message)$aMessage[] = $message;
		}
		if($required){
			TMValidator::ensure($aMessage,'邮件内容不存在！');
		}
		return $aMessage;
	}
	private function _getDescription($data,$title = null)
	{ 
		$digest = $digesta = $digestb = $digestc = null;
		foreach($data as $d){
			if(strstr($title,'收入额')){
                if (strstr($d[0]['data'][0]['name'], '收入总额')) $digest = '总收入 '.round($d[0]['data'][0]['data'][0]);
			}elseif(strstr($title,'新增用户数')){
                if (strstr($d[0]['data'][0]['name'], '总新增')) $digest .= ' 总新增 '.round($d[0]['data'][0]['data'][0]);
			}else{
				$dataname = substr($d[0]['data'][0]['name'], strpos($d[0]['data'][0]['name'], ']') + 1);
				if(strstr($dataname,"新增用户数")){
					$digestb = '新增 '.$d[0]['data'][0]['data'][0];
				}elseif(strstr($dataname,"收入")){
					$digesta = '收入 '.round($d[0]['data'][0]['data'][0]);
				}elseif(strstr($dataname,"活跃用户")){
					$digestc = '活跃 '.$d[0]['data'][0]['data'][0];
				}
			}
		}
		if($title == ''){
			$digest = $digesta.' '.$digestb.' '. $digestc;
		}
		return $digest;
	}
    private function _getTableContente($dataModel, $list, $title = null)
    {
        $aData = array();
        foreach ($list as $info) {
            $aParam = array_merge(
                $this->_getCommonParams($info),
                $this->_getTableTime($info['data_date_type'], $info['offset'])
            );
            $aParam['contrast'] = 1;
            if ($data = $dataModel->getTimeSeries($aParam)) {
                $aData[] = $data;
            }
		}
        if ($aData) return array('content' => $this->_tablee($aData, $title),'digest' => $this->_getDescription($aData,$title));
        return $aData;
    }

    private function _tablee($data, $title = null)
    {
		$aData = array();
		foreach ($data as $d) {
			$dname = substr($d[0]['data'][0]['name'], strpos($d[0]['data'][0]['name'], ']') + 1);
			if(strstr($dname,'Arppu') || strstr($title,'Arppu')){
				$aData[] = array(
					$this->_modname($dname),
					$d[0]['data'][0]['data'][0], 
					$d[1]['data'][0]['data'][0], $this->_paintw($d[1]['data'][0]['contrast_rate'][0]),
					$d[2]['data'][0]['data'][0], $this->_paintw($d[2]['data'][0]['contrast_rate'][0]),
					$d[3]['data'][0]['data'][0], $this->_paintw($d[3]['data'][0]['contrast_rate'][0])
				);
			}else{
            	$aData[] = array(
                	$this->_modname($dname),
                	$this->_modval($d[0]['data'][0]['data'][0]),
                	$this->_modval($d[1]['data'][0]['data'][0]), $this->_paintw($d[1]['data'][0]['contrast_rate'][0]),
                	$this->_modval($d[2]['data'][0]['data'][0]), $this->_paintw($d[2]['data'][0]['contrast_rate'][0]),
                	$this->_modval($d[3]['data'][0]['data'][0]), $this->_paintw($d[3]['data'][0]['contrast_rate'][0])
				);
			}
		}
		if($title != ''){
			$tpos = strpos($title,'（');
			if($tpos){
				$title = substr($title, 0, $tpos);
			}
		}
        return TM::app()->template->clearAllAssign()
            ->assign('title', $title)
            ->assign('data', $aData)
            ->fetch('kernel/weixin-report-table.html');
	}
	private function _modname($dataname)
	{
		$dpos = strpos($dataname,'（');
		if($dpos){
			$dataname = substr($dataname, 0, $dpos);
		}
		if(strstr($dataname,"新增用户次日留存率")){
			$dataname = "新增次日留存";
		}
		$dataname = str_replace('数','',$dataname);
		if(strstr($dataname,'Arppu')){
			$dataname = 'Arppu';
		}
		$dataname = mb_substr($dataname,0,4,'UTF-8').'<br>'.mb_substr($dataname,4,8,'UTF-8');		
		return trim($dataname);
	}
	private function _modval($datanum)
	{
		if(!strstr($datanum,'%')){
			return round($datanum);
		}
		return $datanum;
	}
    private function _paintw($percentage, $desc = true)
    {
        if ($percentage == 0) {
            return $desc ? '持平' : '0%';
        }
        if ($percentage > 0) {
            $desc = $desc ? '+' : '';
        } else {
            $desc = $desc ? '-' : '-';
        }
        $percentage = abs($percentage);
		if ($percentage > 5) {
			if($desc == "+"){
				return '<font color=red>+'.$percentage.'%</font>';
			}elseif($desc == "-"){
                return '<font color=gree>-'.$percentage.'%</font>';
			}
        } else {
            return $desc . $percentage . '%';
        }
    }

	/************************************************************************/
    /**
     * Get content for type TABLE.
     *
     * @param  data_Data $dataModel
     * @param  array     $list
     * @param  string    $title
     * @param  string    $date
     * @return array
     */
    private function _getTableContent($dataModel, $list, $title = null)
    {
        $aData = array();
        $isDay = true;
        foreach ($list as $info) {
            $aParam = $this->_getCommonParams($info);
            switch ($info['data_date_type']) {
                case 'MONTH':
                    $aParam['contrast'] = 0;
                    $aParam['yoy'] = 1;
                    $aParam['qoq'] = 1;
                    $aParam = array_merge($aParam,
                        $this->_getTableTime($info['data_date_type'], $info['offset'], 'last month')
                    );
                    $isDay = false;
                    break;
                case 'DAY':
                    $aParam['contrast'] = 1;
                    $aParam = array_merge($aParam,
                        $this->_getTableTime($info['data_date_type'], $info['offset'])
                    );
                    break;
                default:
            }
            if ($data = $dataModel->getTimeSeries($aParam)) {
                $aData[] = $data;
            }
        }
        if ($aData && $isDay) return array('content' => $this->_table($aData, $title));
        if ($aData && !$isDay) return array('table' => $this->_monthTable($aData, $title));
        return $aData;
    }

    /**
     * @brief _getExcelContent
     *
     * @param {data_Data} $dataModel
     * @param {array}     $list
     * @param {string}    $title
     *
     * @return
     */
    private function _getExcelContent(data_Data $dataModel, $list, $title)
    {
        $aData = array();
        $message = array();
        $aTimeRange = null;
        foreach ($list as $info) {
            $temp = explode('|', $info['data_expr']);
            $tags = explode(':', $temp[2]);
            $aParam = array_merge(
                $this->_getCommonParams($info),
                $this->_getTableTime($info['data_date_type'], $info['offset'], 'last month')
            );
            $aParam['contrast'] = 0;
            $aParam = array_merge($aParam, common_ExcelFactory::getParams($tags[0]));
            if ($data = $dataModel->getTimeSeries($aParam)) {
                $aTimeRange = $data[0]['key'];
                $data = $data[0]['data'];
                $info = array('info' => array('data' => array_reverse($data[0]['data']), 'printType' =>$tags[1]));
                switch ((int)$tags[0]) {
                    case common_ExcelFactory::TYPE_DATA_QOQ:
                        $qoq = $data[0]['name'] . '增长率';
                        $qoqData = array('data' => array_reverse($data[0]['qoq']),
                                         'printType' => common_ExcelFactory::TYPE_SECOND_BLUE_TITLE
                                   );
                        $aData = array_merge($aData, array($data[0]['name'] => $info,
                                             $qoq => array('info' => $qoqData))
                                   );
                        break;
                    case common_ExcelFactory::TYPE_DATA_DIFF:
                        array_shift($aTimeRange);
                        $aParam['exprs'] = null;
                        $info['info']['data'] = $this->_getQoqByLast($dataModel, $aParam);
                    default:
                        $aData = array_merge($aData, array($data[0]['name'] => $info));
                }
            }
        }
        if ($aData) {
            $title = array_filter(explode('|', $title));
            $message[$title[0]]['content'] = $aData;
            $message[$title[0]]['title'] = array($title[1] => count($aData));
            $message[$title[0]]['time'] = array_reverse($aTimeRange);
        }
        return $message;
    }

    /**
     * @brief _getQoqByLast
     *
     * @param {data_Data} $dataModel
     * @param {array} $aParam
     *
     * @return
     */
    private function _getQoqByLast(data_Data $dataModel, $aParam)
    {
        $data = $dataModel->getTimeSeries($aParam);
        $data = $data[0]['data'];
        $oContrast = new data_calculator_Contrast();
        array_shift($data[0]['data']);
        array_pop($data[1]['data']);
        $oContrast->setBaseData($data[0]['data']);
        return array_reverse($oContrast->contrast($data[1]['data'], 0));
    }

    /**
     * @param  data_Data $dataModel
     * @param  array     $list
     * @return array
     */
    private function _getMixedContent($dataModel, $list)
    {
        $aMessage = $aGraphData = $aTableData = array();
        $period = 'DAY';
        foreach ($list as $info) {
            $common = $this->_getCommonParams($info);
            if ($info['in_graph']) {
                $aParam = array_merge($common, $this->_getGraphTime($info['data_date_type'], $info['offset']));
                $aParam['contrast'] = 0;
                if ($data = $dataModel->getTimeSeries($aParam)) {
                    $period = $info['data_date_type'];
                    $aGraphData = array_merge($aGraphData, $data);
                }
            }
            if ($info['in_table']) {
                $aParam = array_merge($common, $this->_getTableTime($info['data_date_type'], $info['offset']));
                $aParam['contrast'] = 1;
                if ($data = $dataModel->getTimeSeries($aParam)) {
                    $aTableData[] = $data;
                }
            }
        }
        if ($aGraphData) $aMessage['graph'] = $this->_plot($aGraphData, $period);
        if ($aTableData) $aMessage['table'] = $this->_paragraph($aTableData);
        return $aMessage;
    }

    /**
     * Get common parameters against the email data info.
     *
     * @param  array $info
     * @return array
     */
    private function _getCommonParams($info)
    {
        $temp = explode('|', $info['data_expr']);
        $common = array(
            'exprs' => array(array(
                'expr' => $temp[1],
                'data_name' => $info['data_name'],
                'unit' => $info['unit'],
                'precision' => 2
            )),
            'gpzs_id' => $info['gpzs_id'],
            'game_id' => null,
            'by_item' => 0,
            'yoy' => 0,
            'qoq' => 0,
            'average' => 0,
            'rate2data' => 0
        );
        $dataIds = array_filter(explode(';', $temp[0]));
        foreach ($dataIds as $dataId) {
            $tmpDataId = explode('_', $dataId);
            if (count($tmpDataId) == 2) {
                $common['data_info'][] = array('data_id' => $tmpDataId[0], 'gpzs_id' => $tmpDataId[1], 'precision' => 6);
            } else {
                $common['data_info'][] = array('data_id' => $dataId, 'precision' => 6);
            }
        }
        return $common;
    }

    /**
     * Get email data time for graph based on a day, default yesterday.
     *
     * @param  enum(MINUTE, DAY) $period
     * @param  integer           $offset
     * @param  string            $base
     * @return array
     */
    private function _getGraphTime($period = 'DAY', $offset = 0, $base = 'yesterday')
    {
        $iTime = strtotime($base) - $offset * 86400;
        $aParam = array('from' => array(), 'to' => array());
        switch ($period) {
            case 'MINUTE':
                $aParam['period'] = 4;
                foreach (array(0, 86400, 604800) as $secs) {
                    $aParam['from'][] = $aParam['to'][] = date('Y-m-d', $iTime - $secs);
                }
                break;

            case 'DAY':
                $aParam['period'] = 1;
                $aParam['from'][] = date('Y-m-d', $iTime);
                $aParam['to'][] = date('Y-m-d', $iTime - 2419200);
                break;

            default:
                throw new TMException(TM::t('tongji', '时间类型不正确！'));
                break;
        }
        return $aParam;
    }

    /**
     * Get email data time for table based on a day, default yesterday.
     *
     * @param  enum(DAY) $period
     * @param  integer   $offset
     * @param  string    $base
     * @return array
     */
    private function _getTableTime($period = 'DAY', $offset = 0, $base = 'yesterday')
    {
        $aParam = array('from' => array(), 'to' => array());
        switch ($period) {
            case 'DAY':
                $iTime = strtotime($base) - $offset * 86400;
                $aParam['period'] = 1;
                foreach (array(0, 86400, 604800, 2419200) as $secs) {
                    $aParam['from'][] = $aParam['to'][] = date('Y-m-d', $iTime - $secs);
                }
                break;

            case 'MONTH':
                $aParam['period'] = 3;
                $base = $this->_monthCheckTime($base);
                $iTime = strtotime($base) - (int)$offset * 2419200;
                $aParam['from'][] = date('Y-m-d', $iTime);
                $aParam['to'][] = date('Y-m-d', strtotime($base));
                $iLastyear = strtotime($base) - 13 * 2419200;
                $aParam['from'][] = $aParam['to'][] = date('Y-m-d', $iLastyear);
                break;

            default:
                throw new TMException(TM::t('tongji', '时间类型不正确！'));
                break;
        }
        return $aParam;
    }

    /**
     * @brief _monthCheckTime
     *
     * @param {string} $date
     *
     * @return {string}
     */
    private function _monthCheckTime($date)
    {
        $tempDate = date('Y-m-d', strtotime($date));
        switch ($date) {
            case 'last month':
                $date = explode('-', $tempDate);
                if ((int)date('d') > 28) {
                    if ((int)$date[1] == 1) {
                        $tmpMonth = 12;
                        $tmpYear = (int)$date[0] - 1;
                    } else {
                        $tmpMonth = (int)$date[1] - 1;
                        $tmpYear = (int)$date[0];
                    }
                    $tempDate = "$tmpYear-$tmpMonth-1";
                }
            default:
        }
        return $tempDate;
    }

    /**
     * Get a percentage.
     *
     * @param  float   $base
     * @param  float   $value
     * @param  integer $precision
     * @return float
     */
    private function _percentage($base, $value, $precision = 2)
    {
        $base = (float)$base;
        if ($base) {
            $value = (float)$value;
            return round(($value-$base)/$base*100, $precision);
        }
        return 0;
    }

    /**
     * Plot a graph.
     *
     * @param  array             $aGraphData
     * @param  enum(MINUTE, DAY) $period
     * @return string
     */
    private function _plot($aGraphData, $period = 'DAY')
    {
        $oPlot = TM::app()->plot->createCanvas()->setXData($aGraphData[0]['key']);
        foreach ($aGraphData as $data) {
            $prefix = $period === 'MINUTE' ? substr($data['key'][0], 0, strpos($data['key'][0], ' ')) : '';
            $oPlot->addYData($prefix . $data['data'][0]['name'], $data['data'][0]['data']);
        }
        switch ($period) {
            case 'MINUTE':
                $oPlot->setXFormat('%H时', true, '%Y-%m-%d %H:%M')->setXTicsAdd(7200);
                break;

            case 'DAY':
                $oPlot->setXFormat('%m-%d', true, '%Y-%m-%d')->setXTicsAdd(259200);
                break;

            default:
                throw new TMException(TM::t('tongji', '时间类型不正确！'));
                break;
        }
        return $oPlot->plot();
    }

    /**
     * Html the table data.
     *
     * @param  array  $aTableData
     * @return string
     */
    private function _paragraph($aTableData)
    {
        $temp = '<p>【<span class="fb">%s</span>】<span class="fb">%s</span>；' .
                '%s前一天（<span class="fb">%s</span>）<span class="fb">%s</span>；' .
                '%s上周同期（<span class="fb">%s</span>）<span class="fb">%s</span>；' .
                '%s上月同期（<span class="fb">%s</span>）<span class="fb">%s</span></p>';
        $html = '';
        foreach ($aTableData as $data) {
            $html .= sprintf($temp, substr($data[0]['data'][0]['name'], strpos($data[0]['data'][0]['name'], ']') + 1),
                $data[0]['data'][0]['data'][0],
                (float)$data[1]['data'][0]['data'][0] ? '较' : '',
                $data[1]['data'][0]['data'][0],
                (float)$data[1]['data'][0]['data'][0] ? $this->_paint($data[1]['data'][0]['contrast_rate'][0]) : '',
                (float)$data[2]['data'][0]['data'][0] ? '较' : '',
                $data[2]['data'][0]['data'][0],
                (float)$data[2]['data'][0]['data'][0] ? $this->_paint($data[2]['data'][0]['contrast_rate'][0]) : '',
                (float)$data[3]['data'][0]['data'][0] ? '较' : '',
                $data[3]['data'][0]['data'][0],
                (float)$data[3]['data'][0]['data'][0] ? $this->_paint($data[3]['data'][0]['contrast_rate'][0]) : ''
            );
        }
        return $html;
    }

    /**
     * Paint the percentage.
     * @param  float   $percentage
     * @param  boolean $desc
     * @param  integer $base
     * @return string
     */
    private function _paint($percentage, $desc = true, $base = 5)
    {
        if ($percentage == 0) {
            return $desc ? '持平' : '0%';
        }
        $class = '';
        if ($percentage > 0) {
            $desc = $desc ? '上升' : '';
            $class = 'increase';
        } else {
            $desc = $desc ? '下降' : '-';
            $class = 'decrease';
        }
        $percentage = abs($percentage);
        if ($percentage > $base) {
            return '<span class="' . $class . '">' . ($desc ? $desc : '') . $percentage . '%</span>';
        } else {
            return ($desc ? $desc : '' ) . $percentage . '%';
        }
    }

    /**
     * Stringfy table.
     *
     * @param  array  $data
     * @param  string $title
     * @return string
     */
    private function _table($data, $title = null)
    {
        $aData = array();
        foreach ($data as $d) {
            $aData[] = array(
                substr($d[0]['data'][0]['name'], strpos($d[0]['data'][0]['name'], ']') + 1),
                $d[0]['data'][0]['data'][0],
                $d[1]['data'][0]['data'][0], $this->_paint($d[1]['data'][0]['contrast_rate'][0]),
                $d[2]['data'][0]['data'][0], $this->_paint($d[2]['data'][0]['contrast_rate'][0]),
                $d[3]['data'][0]['data'][0], $this->_paint($d[3]['data'][0]['contrast_rate'][0])
            );
        }
        return TM::app()->template->clearAllAssign()
            ->assign('title', $title)
            ->assign('data', $aData)
            ->fetch('kernel/daily-report-table.html');
    }

    /**
     * @brief _monthTable 
     *
     * @param array  $data
     * @param string $title
     *
     * @return string
     */
    private function _monthTable($data, $title = null)
    {
        $aData = array();
        foreach ($data as $d) {
            $aData[] = array(
                $d[0]['data'][0]['name'],
                $d[0]['data'][0]['data'][1],
                $d[0]['data'][0]['data'][0],
                $this->_paint((float)rtrim($d[0]['data'][0]['qoq'][1], '%'), false, 30),
                $d[1]['data'][0]['data'][0],
                $this->_paint((float)rtrim($d[0]['data'][0]['yoy'][1], '%'), false, 30)
            );
        }
        return TM::app()->template->clearAllAssign()
            ->assign('title', $title)
            ->assign('time', date('Y-m', strtotime('last month')))
            ->assign('data', $aData)
            ->fetch('kernel/monthly-report-table.html');
    }

    /**
     * Unlink the temp graph file.
     *
     * @param array $aMessage
     */
    private function _clearGraph($aMessage)
    {
        foreach ($aMessage as $val) {
            if (isset($val['graph'])) {
                @unlink($val['graph']);
            }
        }
    }

    /**
     * Rename email data.
     *
     * @param  array $aUserParameters
     * @return ajax
     */
    public function renameData($aUserParameters)
    {
        $emailData = new common_EmailData();
        $emailData->email_data_id = $aUserParameters['email_data_id'];
        $emailData->data_name = $aUserParameters['data_name'];
        $this->ajax(0, $emailData->update(array('data_name')));
    }

    /**
     * Delete email data.
     *
     * @param  array $aUserParameters
     * @return ajax
     */
    public function deleteData($aUserParameters)
    {
        $emailData = new common_EmailData();
        $emailData->email_data_id = (int)$aUserParameters['email_data_id'];
        $this->ajax(0, $emailData->delete());
    }

    /**
     * Set data threshold.
     *
     * @param  array $aUserParameters
     * @return ajax
     */
    public function setThreshold($aUserParameters)
    {
        $emailData = new common_EmailData();
        $emailData->email_data_id = (int)$aUserParameters['email_data_id'];
        if (!is_array($aUserParameters['threshold'])) {
            $emailData->threshold = '';
            $emailData->update(array('threshold'));
            $this->ajax(0, $emailData->threshold);
        }

        $threshold = array();
        $allowKey = array('qoq' => true, 'null_not_check' => true);
        $allowDay = array_fill_keys(array('Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'), true);
        foreach ($aUserParameters['threshold'] as $key => $info) {
            if (!isset($allowKey[$key])) continue;
            switch ($key) {
                case 'qoq':
                    foreach ($info as $k => $v) {
                        if (!isset($allowDay[$k])) continue;
                        $v['min'] = trim($v['min']);
                        $v['max'] = trim($v['max']);
                        if ($v['min'] === '' || $v['max'] === '') continue;
                        $threshold[$key][$k] = $v;
                    }
                    break;
              case 'null_not_check':
                    $threshold[$key] = $info;
                    break;

            }
        }
        $emailData->threshold = json_encode($threshold);
        $emailData->update(array('threshold'));
        $this->ajax(0, $emailData->threshold);
    }

    /**
     * @brief _getAttachment
     *
     * @param {array} $aContent
     * @param {string} $params
     * @param {string} $type
     *
     * @return {string}
     */
    private  function _getAttachment($aContent, $params, $type = 'EXCEL')
    {
        $time = null;
        switch ($type) {
            case 'EXCEL':
                $aMonthContent = array();
                foreach ($aContent as $content) {
                    foreach ($content as $title => $record) {
                        $aMonthContent[$title][] = $record;
                        $time = $record['time'];
                    }
                }
                return $this->_createExcel(array('date_range' => $time,
                                                 'content' => $aMonthContent,
                                                 'subject' => $params['eRemarks_top'],
                                                 'dependency' => $params['dependencies']
                                                 )
                       );
            default:
                return array();
        }
    }

    /**
     * @brief _createExcel
     *
     * @param {array} $aUserParameters : date_range {array}
     *                                   content {array}
     *                                   subject {string}
     *                                   dependency {object}
     * @return
     */
    private function _createExcel($aUserParameters)
    {
        $dependency = $this->_object2Array($aUserParameters['dependency']);
        $oMonthExcel = new common_Excel();
        $oMonthExcel->init();
        $oMonthExcel->load($dependency['callback'][0]['filename'], $dependency['callback'][1]['title'], TM::app()->getBasePath() . DS . 'config' . DS . 'excel' . DS . 'month');
        $oMonthExcel->createSheet($aUserParameters['subject']);
        $start = 'A1';
        $dataStart = 'B2';
        foreach ($aUserParameters['content'] as $rowTitle => $aContent) {
                $oMonthExcel->createRowContent($start, array($aUserParameters['subject'] . $rowTitle => 2), common_ExcelFactory::TYPE_ROW_CONTENT);
                $dateStart = $this->_add($start, 2, true);
                $oMonthExcel->createRowContent($dateStart, $aUserParameters['date_range'], common_ExcelFactory::TYPE_SECOND_BLUE_TITLE);
                $start = $this->_add($start);
            foreach ($aContent as $tableData) {
                $oMonthExcel->createColumContent($start, $tableData['title'], common_ExcelFactory::TYPE_FIRST_TITLE);
                $start = $this->_add($start, current($tableData['title']));
                foreach ($tableData['content'] as $name => $value) {
                    list ($dataType, $colContent) = $this->_getExcelType($value['info']);
                    $oMonthExcel->createColumContent($dataStart, (array)$name, $colContent);
                    $oMonthExcel->createRowData($value['info']['data'], $this->_add($dataStart, 1, true), $dataType);
                    $dataStart = $this->_add($dataStart);
                }
            }
            $lineTo = $this->_add($dataStart, count($aUserParameters['date_range']), true);
            $oMonthExcel->drawLine($dataStart, $lineTo, common_Excel::TOPLINE);
            $dataStart = $this->_add($dataStart, 3);
            $start = $this->_add($start, 2);
        }

        $oMonthExcel->setCellsSize(38, 2);
        $oMonthExcel->setCellsSize(20, 13, 'C');
        $filename = 'B01-' . $aUserParameters['subject'] . '-MonthReport(last 13 month)-' . date('Ym', strtotime('-1 month'));
        return $oMonthExcel->save($filename);
    }

    /**
     * @brief _object2Array
     *
     * @param {array|object} $param
     *
     * @return {array}
     */
    private function _object2Array($param)
    {
        if (is_object($param)) {
            $param = (array)$param;
        }
        if (is_array($param)) {
            foreach($param as $key => $value) {
                $param[$key] = $this->_object2Array($value);
            }
        }
        return $param;
    }

    /**
     * @brief _getExcelType
     *
     * @param {array} $value
     *
     * @return {array}
     */
    private function _getExcelType($value)
    {
        switch ((int)$value['printType']) {
            case common_ExcelFactory::TYPE_SPECIAL:
                return array(common_ExcelFactory::TYPE_DATA_CONTENT, common_ExcelFactory::TYPE_SECOND_BLUE_TITLE);
            case common_ExcelFactory::TYPE_COMMON:
            default:
                return array(common_ExcelFactory::TYPE_ROW_DATA, common_ExcelFactory::TYPE_SECOND_TITLE);
        }
    }

    /**
     * @brief _add
     * 行列增加函数
     * @param {string} $str    单元格,形如{A1, B23..}
     * @param {integer} $num   增加间隔
     * @param {boolean} $isCol 列增加
     *
     * @return {string}
     */
    private function _add($str, $num = 1, $isCol = false)
    {
        if(!$isCol) {
            return substr($str, 0, 1) . ((int)substr($str, 1) + $num) ;
        }
        else {
            return chr(ord(substr($str, 0 ,1)) + $num) . substr($str, 1) ;
        }
    }
}

class EmailCode
{
    const DAILY = 'DAILY';
    const WEEKLY = 'WEEKLY';
    const MONTHLY = 'MONTHLY';
    const QUARTELY = 'QUARTERLY';

    static public function getEmailCodeDesc($typeCode)
    {
        $Code = array(
            'DAILY' => '日报',
            'WEEKLY' => '周报',
            'MONTHLY' => '月报',
            'QUARTERLY' => '季报'
        );
        return TMArrayHelper::assoc($typeCode, $Code, '日报');
    }
}

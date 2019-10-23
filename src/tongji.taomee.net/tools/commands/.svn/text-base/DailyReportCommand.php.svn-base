<?php
class DailyReportCommand extends TMConsoleCommand
{
    /**
     * @var string Virtual process id
     */
    public $vpid;

    /**
     * @var string The file saving the process id
     */
    public $pidFile;

    /**
     * @var array Emails sent
     */
    private $_doneEmail = array();

    /**
     * @var array Emails error
     */
    private $_errEmail = array();

    /**
     * @var array Error data
     */
    private $_error = array();

    /**
     * Return available actions
     *
     * @return array
     */
    public function actions()
    {
        return array(
            'send' => true,
            'threshold' => true
        );
    }

    /**
     * Send emails with AUTO_SEND status. When $test is true, just sent to test recipients.
     *
     * @param  bool $test
     * @return null
     */
    public function send($test = 1)
    {
        $this->log('Start sending daily reports...');
        $this->log('Get daily reports info...');
        $aEmail = $this->_getDependencySortedEmails();
        $this->log('Get daily reports info...[ok]');
        $this->log('Send daily reports...');
        foreach ($aEmail as $email) {
			try {
				// conduct check
                if (isset($email['dependencies']['email']) && $email['dependencies']['email']) {
                    foreach ($email['dependencies']['email'] as $id) {
                        TMValidator::ensure(isset($this->_doneEmail[$id]), TM::t('tongji', '依赖邮件【{id}】{subject}！', [
                            '{id}' => $id,
                            '{subject}' => isset($this->_errEmail[$id]) ? $this->_errEmail[$id]['subject'] : TM::t('tongji', '未知')
                        ]));
                    }
                }
                if (isset($email['dependencies']['callback']) && $email['dependencies']['callback']) {
                    foreach ($email['dependencies']['callback'] as $callback) {
                        if (!isset($callback['func']) || !method_exists($this, $callback['func'])) continue;
                        if (isset($callback['params'])) {
                            $this->{$callback['func']}($callback['params']);
                        } else {
                            $this->{$callback['func']}();
                        }
                    }
                }
                // send email
                $this->_error = array();
                $this->_sendEmail($email, (bool)$test);
                if ($this->_error) {
                    $this->_errEmail[$email['email_id']] = array(
                        'subject' => $email['subject'], 'err_desc' => TM::t('tongji', '数据异常：'), 'err_data' => $this->_error);
                } else {
                    $this->_doneEmail[$email['email_id']] = array('subject' => $email['subject']);
				    $this->sendWeixin($email, (bool)$test);
                }
				$this->log(sprintf('Sending %s ...[ok] ', $email['subject']));
            } catch (Exception $e) {
                // failed
                $this->_errEmail[$email['email_id']] = array('subject' => $email['subject'], 'err_desc' => $e->getMessage());
            }
		}
        unset($aEmail);
        $this->log('Send daily reports...[ok]');
        $this->log('Send result...');
        $this->_sendResult();
        $this->log('Send result...[ok]');
    }

    /**
     * Return emails with the order by which there are no dependency problems.
     *
     * @return array
     */
    private function _getDependencySortedEmails()
    {
        $aEmail = (new common_EmailConfig())->findAll([
            'condition' => ['status' => common_EmailConfig::AUTO_SEND],
            'order' => 'order'
        ]);

        $temp = $nodepend = $target = $depend = $depended = array();
        foreach ($aEmail as $info) {
            $info['dependencies'] = json_decode($info['dependencies'], true);
            $temp[$info['email_id']] = $info;
            if (!$info['dependencies'] || !isset($info['dependencies']['email']) || !$info['dependencies']['email']) {
                array_push($nodepend, $info['email_id']);
                continue;
            }
            foreach ($info['dependencies']['email'] as $dependId) {
                $depend[$info['email_id']][$dependId] = $depended[$dependId][$info['email_id']] = true;
            }
        }
        while ($nodepend) {
            $dependId = array_shift($nodepend);
            array_push($target, $temp[$dependId]);
            if (!isset($depended[$dependId]) || !$depended[$dependId]) continue;
            foreach ($depended[$dependId] as $id => $useless) {
                if (!isset($depend[$id][$dependId])) continue;
                unset($depend[$id][$dependId]);
                if (!$depend[$id]) {
                    array_push($nodepend, $id);
                }
            }
        }
        return $target;
    }

    /**
     * Send email
     *
     * @param  array $aEmail
     * @param  bool  $isTest
     * @return null
     */
    private function _sendEmail($aEmail, $isTest = true)
    {
        $aTo = array_filter(explode(';', $isTest ? $aEmail['test_receiver'] : $aEmail['receviers']));
        TMValidator::ensure($aTo, TM::t('tongji', '邮件收件人不能为空！'));
        $aContent = $this->_getContentByEmailId($aEmail['email_id']);
        $aMessage = $this->_getMessage($aContent);
        if ($this->_error) return;

        $mail = TM::app()->mailer->createMessage()->setSubject($aEmail['subject'] . '日报' . DS . date('Y-m-d', strtotime('yesterday')))->setTo($aTo);
        if (!$isTest) {
            if ($aCc = array_filter(explode(';', $aEmail['cc']))) $mail->setCc($aCc);
        }
		$failedRecipients = array();
		$eRemarks = explode("|",$aEmail['remarks']);
		$eRemarks_top = $eRemarks[0];
		$eRemarks_bot = $eRemarks[1];
        $mail->setBody(
            TM::app()->template->clearAllAssign()->assign(array(
                'mail' => $mail,
                'avatarPath' => TM::app()->getBasePath() . DS .
                    'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                'subject' => $aEmail['subject'],
                'date' => date('Y-m-d', strtotime('yesterday')),
                'content' => $aMessage,
				'remarks' => explode("\n",$eRemarks_top),
				'remarkss' => explode("\n",$eRemarks_bot)
            ))->fetch('daily-report.html'), 'text/html'
        )->send($failedRecipients);
        $this->_clearGraph($aMessage);
        TMValidator::ensure(!$failedRecipients, TM::t('tongji', '没能成功发送给{r}！', ['{r}' => implode(',', $failedRecipients)]));

        (new common_EmailConfig())->updateLastSend($aEmail['email_id'], 'system');
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
        $aMessage = array();
        $param = array('condition' => array());
        foreach ($aContent as $content) {
            $param['condition']['email_content_id'] = $content['email_content_id'];
			$param['condition']['remove_flag'] = 0;
			$list = $emailModel->findAll(array_merge(array("order" => "data_order DESC, email_data_id ASC"),$param));
            if (!$list) continue;
            $this->_check($dataModel, $list, $content['content_title']);
            if ($this->_error) {
                $required = false;
                continue;
            }
            $message = null;
            switch ($content['content_type']) {
                case 'TABLE':
                    $message = $this->_getTableContent($dataModel, $list);
                    break;

                case 'MULTI_GAME_TABLE':
                    $message = $this->_getTableContent($dataModel, $list, $content['content_title']);
                    break;

                default:
                    $message = $this->_getMixedContent($dataModel, $list);
                    break;
            }
            if ($message) $aMessage[] = $message;
        }
        if ($required) {
            TMValidator::ensure($aMessage, '邮件内容不存在！');
        }
        return $aMessage;
    }

    /**
     * Get content for type TABLE.
     *
     * @param  data_Data $dataModel
     * @param  array     $list
     * @param  string    $title
     * @return array
     */
    private function _getTableContent($dataModel, $list, $title = null)
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
        if ($aData) return array('content' => $this->_table($aData, $title));
        return $aData;
    }

    /**
     * Get content for type MIXED.
     *
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
        $iTime = strtotime($base) - $offset * 86400;
        $aParam = array('from' => array(), 'to' => array());
        switch ($period) {
            case 'DAY':
                $aParam['period'] = 1;
                foreach (array(0, 86400, 604800, 2419200) as $secs) {
                    $aParam['from'][] = $aParam['to'][] = date('Y-m-d', $iTime - $secs);
                }
                break;

            default:
                throw new TMException(TM::t('tongji', '时间类型不正确！'));
                break;
        }
        return $aParam;
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
     * @return string
     */
    private function _paint($percentage, $desc = true)
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
        if ($percentage > 5) {
            return '<span class="' . $class . '">' . $desc . $percentage . '%</span>';
        } else {
            return $desc . $percentage . '%';
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
            ->fetch('daily-report-table.html');
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
     * Check data.
     *
     * @param  data_Data $dataModel Model for getting data
     * @param  array     $list      Data configuration
     * @param  string    $title     Prefix of data name
     * @return null
     * @throw  Exception
     */
    private function _check($dataModel, $list, $title)
    {
        foreach ($list as $info) {
            if ($info['data_date_type'] !== 'DAY') continue; // check day data only

            // Check null value
            $aParam = $this->_getCommonParams($info);
            $aParam['from'] = $aParam['to'] = date('Y-m-d', strtotime('yesterday') - (int)$info['offset'] * 86400);
            $aParam['period'] = $aParam['fill_null'] = $aParam['contrast'] = 1;
            $data = $dataModel->getTimeSeries($aParam);

            $info['threshold'] = json_decode($info['threshold'], true);
            if (!(isset($info['threshold']['null_not_check']) && $info['threshold']['null_not_check'])
                    && !isset($data[0]['data'][0]['data'][0])) {
                // no value
                $this->_error[] = array(
                    'name' => $title . $info['data_name'],
                    'desc' => '没有值'
                );
                return;
            }

            if (isset($info['threshold']['qoq']) && $info['threshold']['qoq']) {
                $this->_checkQoq($dataModel, $info, $title);
            }
        }
    }

    /**
     * Check qoq
     *
     * @param  data_Data $dataModel
     * @param  array     $info
     * @param  string    $title
     * @return null
     */
    private function _checkQoq($dataModel, $info, $title)
    {
        $aParam = array_merge(
            $this->_getCommonParams($info),
            $this->_getQoqTime($info['offset'])
        );
        $w = date('D', strtotime($aParam['from'][0]));
        if (!isset($info['threshold']['qoq'][$w])) return;
        $aParam['contrast'] = $aParam['fill_null'] = 1;
        $data = $dataModel->getTimeSeries($aParam);
        if (!isset($data[0]['data'][0]['data'][0], $data[1]['data'][0]['data'][0], $data[1]['data'][0]['contrast_rate'][0])) {
            // no value
            $this->_error[] = array(
                'name' => $title . $info['data_name'],
                'desc' => '没有值'
            );
            return;
        }
        if ((float)$data[1]['data'][0]['contrast_rate'][0] > (float)$info['threshold']['qoq'][$w]['max'] ||
                (float)$data[1]['data'][0]['contrast_rate'][0] < (float)$info['threshold']['qoq'][$w]['min']) {
            // error value
            $this->_error[] = array(
                'name' => $title . $info['data_name'],
                'desc' => sprintf('今日: %s，昨日: %s，环比: %s，范围: %s ~ %s',
                    $data[0]['data'][0]['data'][0],
                    $data[1]['data'][0]['data'][0],
                    $data[1]['data'][0]['contrast_rate'][0],
                    $info['threshold']['qoq'][$w]['min'],
                    $info['threshold']['qoq'][$w]['max']
                )
            );
        }
    }

    /**
     * Get qoq time, just for day
     *
     * @param  int    $offset
     * @param  string $base
     * @return array
     */
    private function _getQoqTime($offset = 0, $base = 'yesterday')
    {
        $iTime = strtotime($base) - $offset * 86400;
        $aParam = array('period' => 1, 'from' => array(), 'to' => array());
        foreach (array(0, 86400) as $secs) {
            $aParam['from'][] = $aParam['to'][] = date('Y-m-d', $iTime - $secs);
        }
        return $aParam;
    }

    /**
     * Send result of daily report sending
     *
     * @return null
     */
    private function _sendResult()
    {
        $subject = TM::t('tongji', '日报发送报告');
        $mail = TM::app()->mailer->createMessage()->setSubject($subject);
        $mail->setTo($mail->cc);
        $failedRecipients = array();
        $doneCount = count($this->_doneEmail);
        $errCount = count($this->_errEmail);
        $totalCount = $doneCount + $errCount;
        $mail->setBody(
            TM::app()->template->clearAllAssign()->assign(array(
                'mail' => $mail,
                'avatarPath' => TM::app()->getBasePath() . DS .
                    'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                'subject' => $subject,
                'date' => date('Y-m-d', strtotime('yesterday')),
                'totalCount' => $totalCount,
                'doneCount' => $doneCount,
                'errCount' => $errCount,
                'doneEmail' => $this->_doneEmail,
                'errEmail' => $this->_errEmail
            ))->fetch('daily-report-result.html'), 'text/html'
        )->send($failedRecipients);

        if ($failedRecipients) {
            $this->log(TM::t('tongji', '没能成功发送给{r}！', ['{r}' => implode(',', $failedRecipients)]), 'error');
        }
    }

    /**
     * Check whether data processing is complete
     *
     * @param  array $params
     * @return null
     * @throw  TMValidatorException
     */
    private function _complete($params)
    {
        TMValidator::ensure(isset($params['game_id']), TM::t('tongji', '检查数据加工情况时参数不正确，缺少game_id！'));
        TMValidator::ensure((new tool_TaskCompleteLog())->isDataProcessingComplete($params), TM::t('tongji', '数据加工未完成！'));
    }

    /**
     * Check whether data is consistent with old stat system
     *
     * @param  array $params
     * @return null
     * @throw  TMValidatorException
     */
    private function _consistent($params)
    {
        TMValidator::ensure(isset($params['game_id']), TM::t('tongji', '检查数据一致性时参数不正确，缺少game_id！'));
        $oModel = new tool_TaskCompleteLog();
        $aLog = $oModel->findAll(array(
            'condition' => array(
                'game_id' => $params['game_id'],
                'task_type' => tool_TaskCompleteLog::DAILY_REPORT_CHECK,
                'time' => data_time_Time::amend(strtotime('yesterday'), 'Y-m-d 00:00:00')
            )
        ));
        TMValidator::ensure($aLog, TM::t('tongji', '数据检查未完成！'));
        TMValidator::ensure(
            (int)$aLog[0]['result'] === tool_TaskCompleteLog::SUCCESS,
            $aLog[0]['remark'] === ''
                ? TM::t('tongji', '数据不一致！')
                : TM::t('tongji', '数据不一致，{desc}！', ['{desc}' => $aLog[0]['remark']])
        );
    }

    /**
     * Set email data threshold
     *
     * @param  string $src
     * @return null
     */
    public function threshold($src)
    {
        $reader = new XmlReader();
        TMValidator::ensure($reader->open($src), TM::t('tongji', '文件不能正常打开！'));
        $threshold = array();
        while ($reader->read()) {
            if ($reader->nodeType !== XMLReader::ELEMENT || $reader->name !== 'Threshold') continue;
            $id = $reader->getAttribute('id');
            $type = $reader->getAttribute('type');
            $threshold[$id]['qoq']['Mon'][$type] = ($reader->getAttribute('d1') * 100) . '%';
            $threshold[$id]['qoq']['Tue'][$type] = ($reader->getAttribute('d2') * 100) . '%';
            $threshold[$id]['qoq']['Wed'][$type] = ($reader->getAttribute('d3') * 100) . '%';
            $threshold[$id]['qoq']['Thu'][$type] = ($reader->getAttribute('d4') * 100) . '%';
            $threshold[$id]['qoq']['Fri'][$type] = ($reader->getAttribute('d5') * 100) . '%';
            $threshold[$id]['qoq']['Sat'][$type] = ($reader->getAttribute('d6') * 100) . '%';
            $threshold[$id]['qoq']['Sun'][$type] = ($reader->getAttribute('d7') * 100) . '%';
        }

        $data = new common_EmailData();
        foreach ($threshold as $id => $t) {
            $data->email_data_id = $id;
            $data->threshold = json_encode($t);
            $data->update(array('threshold'));
        }

        echo "Done!\n";
    }

    /************************************************************************/
    /* 发送微信 */
    private function sendWeixin($aEmail,$is_test = true)
	{
		if($aEmail['weixin_recev'] == ''){
			return;
		}
        //TMValidator::ensure($aEmail['weixin_recev'],TM::t('tongji','微信收件人不能为空！'));
        $aContent = $this->_getContentByEmailId($aEmail['email_id']);
        $wMessage = $this->_getWeixinMessage($aContent);
        $wContent = TM::app()->template->clearAllAssign()->assign(array('content' => $wMessage))->fetch('weixin-report.html');
        $weixin_model = new common_WeixinData();
        if(strstr($aEmail['subject'],'各游戏汇总') == false){
            $s_res = $weixin_model->sendWeixin($aEmail,$wContent,$wMessage[0]['digest'],$is_test);
        }else{
            $senddigest = null;
            foreach($wMessage as $wm){
                $senddigest .= $wm['digest'].' ';
            }
            $s_res = $weixin_model->sendWeixin($aEmail,$wContent,$senddigest);
        }
    }
    private function _getWeixinMessage($aContent,$required = false)
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
        $sum = null;
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
            ->fetch('weixin-report-table.html');
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


}

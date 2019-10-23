<?php
class DataCheckCommand extends TMConsoleCommand
{
    /**
     * @var string 虚拟进程ID，用于标识当前进程
     */
    public $vpid;

    /**
     * @var string 进程ID文件
     */
    public $pidFile;

    /**
     * @var array Email要显示的内容
     */
    protected $content = array();

    /**
     * @var array 游戏ID跟名称对应关系
     */
    protected $gameList = array();

    public function actions()
    {
        return array(
            'start' => true
        );
    }

    /**
     * 开始自动检测数据
     */
    public function start($args)
    {
        $arguments = array(
            array(
                'data_id' => 270257,
                'data_name' => TM::t('tongji', '新增用户数'),
                'game_id' => 2,
                'gpzs_id' => 105,
                'condition' => array(
                    array('min' => -30, 'max' => -20),
                    array('min' => -82, 'max' => -60),
                    array('min' => -10, 'max' => 10),
                    array('min' => -5, 'max' => 10),
                    array('min' => -5, 'max' => 15),
                    array('min' => 90, 'max' => 160),
                    array('min' => 110, 'max' => 180)
                )
            ),
            array(
                'data_id' => 270857,
                'data_name' => TM::t('tongji', '活跃用户数'),
                'game_id' => 2,
                'gpzs_id' => 105,
                'condition' => array(
                    array('min' => -15, 'max' => -5),
                    array('min' => -70, 'max' => -50),
                    array('min' => -5, 'max' => 5),
                    array('min' => -5, 'max' => 5),
                    array('min' => -2, 'max' => 8),
                    array('min' => 50, 'max' => 100),
                    array('min' => 50, 'max' => 80)
                )
            ),
            array(
                'data_id' => 256929,
                'data_name' => TM::t('tongji', '按条收入'),
                'game_id' => 2,
                'gpzs_id' => 105,
                'condition' => array(
                    array('min' => -35, 'max' => -20),
                    array('min' => -70, 'max' => -50),
                    array('min' => -5, 'max' => 10),
                    array('min' => -5, 'max' => 10),
                    array('min' => -5, 'max' => 20),
                    array('min' => 90, 'max' => 180),
                    array('min' => 30, 'max' => 70)
                )
            ),
            array(
                'data_id' => 258367,
                'data_name' => TM::t('tongji', '按条付费用户数'),
                'game_id' => 2,
                'gpzs_id' => 105,
                'condition' => array(
                    array('min' => -30, 'max' => -15),
                    array('min' => -70, 'max' => -50),
                    array('min' => -5, 'max' => 10),
                    array('min' => -5, 'max' => 10),
                    array('min' => -5, 'max' => 10),
                    array('min' => 70, 'max' => 160),
                    array('min' => 30, 'max' => 70)
                )
            ),
            //array(
                //'data_id' => 258367,
                //'data_name' => TM::t('tongji', '新增次日留存'),
                //'game_id' => 2,
                //'gpzs_id' => 105,
                //'condition' => array(
                    //array('min' => -30, 'max' => -15),
                    //array('min' => -70, 'max' => -50),
                    //array('min' => -5, 'max' => 10),
                    //array('min' => -5, 'max' => 10),
                    //array('min' => -5, 'max' => 10),
                    //array('min' => 70, 'max' => 160),
                    //array('min' => 30, 'max' => 70)
                //)
            //),
            array(
                'data_id' => 301913,
                'data_name' => TM::t('tongji', '新增用户数'),
                'game_id' => 1,
                'gpzs_id' => 129,
                'condition' => array(
                    array('min' => -23, 'max' => -15),
                    array('min' => -82, 'max' => -70),
                    array('min' => -5, 'max' => 8),
                    array('min' => -10, 'max' => 10),
                    array('min' => -10, 'max' => 10),
                    array('min' => 90, 'max' => 160),
                    array('min' => 110, 'max' => 180)
                )
            ),
            array(
                'data_id' => 302928,
                'data_name' => TM::t('tongji', '活跃用户数'),
                'game_id' => 1,
                'gpzs_id' => 129,
                'condition' => array(
                    array('min' => -14, 'max' => -8),
                    array('min' => -70, 'max' => -50),
                    array('min' => -5, 'max' => 5),
                    array('min' => -5, 'max' => 5),
                    array('min' => -5, 'max' => 5),
                    array('min' => 50, 'max' => 80),
                    array('min' => 50, 'max' => 80)
                )
            )
        );
        try {
            $this->checkQoq($arguments);
            if (!empty($this->content)) {
                $this->sendFailMail();
            } else {
                $this->sendSuccess();
            }
        } catch (Exception $e) {
            $this->log('检测出错：' . $e->getMessage(), 'error');
        }
    }

    /**
     * @brief sendFailMail
     * 有错误，发送邮件给相关人员
     */
    protected function sendFailMail()
    {
        $subject = TM::t('tongji', '数据检测');
        $mail = TM::app()->mailer->createMessage()->setSubject($subject);
        $mail->setBody(
            TM::app()->template->clearAllAssign()->assign(array(
                'mail' => $mail,
                'avatarPath' => TM::app()->getBasePath() . DS .
                    'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                'subject' => $subject,
                'time' => date('Y-m-d H:i:s'),
                'content' => $this->getContent()
            ))->fetch('data.html'), 'text/html'
        )->send();
    }

    /**
     * @brief sendSuccess
     * 全部正确，发送邮件给相关人员
     */
    protected function sendSuccess()
    {
        $subject = TM::t('tongji', '数据检测');
        $mail = TM::app()->mailer->createMessage()->setSubject($subject);
        $mail->setBody(
            TM::app()->template->clearAllAssign()->assign(array(
                'mail' => $mail,
                'avatarPath' => TM::app()->getBasePath() . DS .
                    'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                'subject' => $subject,
                'time' => date('Y-m-d H:i:s')
            ))->fetch('data-success.html'), 'text/html'
        )->send();
    }

    /**
     * @brief addContent
     * 添加发送邮件内容
     *
     * @param $content
     */
    protected function addContent($content)
    {
        $this->content[] = $content;
    }

    /**
     * @brief getContent
     * 获取邮件内容
     *
     * @param {array}
     */
    protected function getContent()
    {
        return $this->content;
    }

    /**
     * @brief checkQoq
     * 检测传入数据配置的环比
     *
     * @param {array} $configuration
     */
    protected function checkQoq($configuration)
    {
        $arguments = array(
            'gpzs_id' => null,
            'gpzs_name' => true,
            'game_id' => null,
            'by_item' => false,
            'from' => date('Y-m-d', strtotime('yesterday')),
            'to' => date('Y-m-d', strtotime('yesterday')),
            'period' => data_time_PeriodFactory::TYPE_DAY,
            'yoy' => 0,
            'qoq' => 1,
            'export' => 0,
            'contrast' => 0,
            'average' => 0,
            'rate2data' => 0,
            'data_info' => $configuration
        );
        $data = (new data_Data())->getTimeSeries($arguments);
        $content = array();
        $data = $data[0]['data'];
        foreach ($arguments['data_info'] as $index => $configure) {
            $metadata = $data[$index];
            $range = $this->getCheckRange($configure['condition']);
            if ($this->checkValue($metadata['qoq'][0], $range)) {
                $content[] = array(
                    $this->findGameName($configure['game_id']),
                    $metadata['name'],
                    $configure['gpzs_id'],
                    $metadata['data'][0],
                    $metadata['qoq'][0] . '%',
                    '('.$range['min'].'%,'.$range['max'].'%)'
                );
            }
        }
        if (empty($content)) return;
        $this->addContent(array(
            'category' => TM::t('tongji', '环比'),
            'thead' => array(
                TM::t('tongji', '游戏名称'),
                TM::t('tongji', '指标名称'),
                'Gpzs ID',
                TM::t('tongji', '当前值'),
                TM::t('tongji', '环比值'),
                TM::t('tongji', '范围')
            ),
            'content' => $content
        ));
    }

    /**
     * @brief getCheckRange
     * 获取检测的范围
     *
     * @param {array} $condition
     *
     * @return {array}
     */
    protected function getCheckRange($condition)
    {
        return $condition[date('w', strtotime('yesterday'))];
    }

    /**
     * @brief checkValue
     *
     * @param {float} $value
     * @param {array} $range
     *
     * @return boolean
     */
    protected function checkValue($value, $range)
    {
        return $value > $range['max'] || $value < $range['min'];
    }

    /**
     * @brief findGameName
     * 获取游戏名称
     *
     * @param {interval} $gameId
     *
     * @return {string}
     */
    protected function findGameName($gameId)
    {
        if (!$this->gameList) {
            $gameList = (new common_Game())->findAll();
            $this->gameList = TMArrayHelper::column($gameList, 'game_name', 'game_id');
        }
        return isset($this->gameList[$gameId]) ? $this->gameList[$gameId] : $gameId;
    }
}

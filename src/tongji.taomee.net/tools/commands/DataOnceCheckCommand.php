<?php
class DataOnceCheckCommand extends TMConsoleCommand
{
    public $vpid;

    public $pidFile;

    /**
     * @brief array
     * Email要显示的内容
     */
    protected $content = array();

    public function actions()
    {
        return array(
            'start' => true
        );
    }

    /**
     * @brief start
     * 开始自动检测数据
     */
    public function start($args)
    {
        // 注：去掉
        // %(0x25)
        // ,(0x2c)
        // .(0x2e) can't be removed
        // /(0x2f)
        // :(0x3a)
        // ;(0x3b)
        // =(0x3d)
        // ?(0x3f) can't be removed
        // |(0x7c) can't be removed
        TMEncode::$commonCharacterRange = array(
            array(0x20, 0x24),
            array(0x26, 0x2b),
            array(0x2d, 0x2e),
            array(0x30, 0x39),
            array(0x3c, 0x3c),
            array(0x3e, 0x7e)
        );
        try {
            $arguments = array(
                'gpzs_id' => null,
                'gpzs_name' => true,
                'game_id' => null,
                'by_item' => false,
                'from' => '2014-01-01',
                'to' => date('Y-m-d', strtotime('-1 day')),
                'period' => data_time_PeriodFactory::TYPE_DAY,
                'yoy' => 0,
                'qoq' => 0,
                'export' => 0,
                'contrast' => 0,
                'average' => 0,
                'rate2data' => 0,
                'data_info' => array()
            );
            $gameList = (new common_Game())->findAll();
            $garbledList = array();
            foreach ($gameList as $game) {
                $arguments['game_id'] = $game['game_id'];
                if (($garbled = $this->checkGameData($arguments))) {
                    $garbledList[] = array(
                        'game_name' => $game['game_name'],
                        'count' => count($garbled),
                        'info' => $garbled
                    );
                }
            }
            if ($garbledList) {
                $this->addContent(array(
                    'thead' => array(
                        TM::t('tongji', '游戏'),
                        TM::t('tongji', '数据ID'),
                        TM::t('tongji', '数据名称'),
                        TM::t('tongji', '显示状态'),
                        TM::t('tongji', 'stid'),
                        TM::t('tongji', 'sstid'),
                        TM::t('tongji', 'node_id')
                    ),
                    'garbled' => $garbledList
                ));
            }
            if ($this->getContent()) {
                $this->sendFailMail();
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
        $subject = TM::t('tongji', '乱码检测');
        $mail = TM::app()->mailer->createMessage()->setSubject($subject);
        $mail->to = $mail->cc;
        $mail->setBody(
            TM::app()->template->clearAllAssign()->assign(array(
                'mail' => $mail,
                'avatarPath' => TM::app()->getBasePath() . DS .
                    'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                'subject' => $subject,
                'time' => date('Y-m-d H:i:s'),
                'content' => $this->getContent()
            ))->fetch('garbled.html'), 'text/html'
        )->send();
        $this->content = array();
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
     * @brief checkGameData
     * 以防数据过多，按游戏检测
     *
     * @param {array} $arguments
     *
     * @return {string}
     */
    protected function checkGameData($arguments)
    {
        $excludeReports = array(
            115298, 115823
        );
        return (new garbled_DataOnceCheck())->setExcludeReport($excludeReports)->check($arguments)->getGarbled();
    }
}

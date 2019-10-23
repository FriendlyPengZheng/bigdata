<?php
class EncodeCheckCommand extends TMConsoleCommand
{
    public $vpid; 

    public $pidFile;

    protected $content = array();

    public function actions()
    {
        return array(
            'start' => true
        );
    }

    /**
     * @brief start 
     * 开始自动检测乱码
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
        // 一些可以包含的字符
        // 中文-（破折号）
        // 中文.（中间点）
        TMEncode::$includeChineseUtf8Character = array(
            '\xe2\x80\x94',
            '\c2\b7'
        );

        foreach ((new common_Game())->findAll() as $game) {
            $this->checkTree($game['game_id']);
            $content = $this->getContent();
            if (empty($content)) continue;
            $this->clearContent();
            $subject = TM::t('tongji', '乱码检测') . '-' . TM::t('tongji', $game['game_name']);
            $mail = TM::app()->mailer->createMessage()->setSubject($subject);
            if (empty($game['game_email'])) {
                $mail->setTo($mail->cc);
            } else {
                $mail->setTo($game['game_email']);
            }

            $mail->setBody(
                TM::app()->template->clearAllAssign()->assign(array(
                    'mail' => $mail,
                    'avatarPath' => TM::app()->getBasePath() . DS .
                        'webroot' . DS . 'static' . DS . 'common' . DS . 'images' . DS . 'tongji.png',
                    'subject' => $subject,
                    'time' => date('Y-m-d H:i:s'),
                    'content' => $content
                ))->fetch('garbled.html'), 'text/html'
            )->send();

            $this->log(sprintf('Send game %s tree-encode-checking email.', $game['game_name']));
        }
    }

    /**
     * @brief addContent 
     * 添加乱码内容
     *
     * @param {array} $content
     */
    protected function addContent($content)
    {
        $this->content[] = $content;
    }

    /**
     * @brief getContent 
     * 获取乱码内容
     *
     * @return {array}
     */
    protected function getContent()
    {
        return $this->content;
    }

    /**
     * @brief clearContent 
     * 清除上次检测的乱码内容
     */
    protected function clearContent()
    {
        $this->content = array();
    }

    /**
     * @brief getDefaultTreeInfo 
     * 默认树节点信息
     *
     * @return {array}
     */
    protected function getDefaultTreeInfo()
    {
        return array(
            array(
                'stid' => '__unknown__',
                'sstid' => '__unknown__',
                'game_name' => '__unknown__'//,
                //'hip' => '__unknown__'
            )
        );
    }

    /**
     * @brief checkTree 
     * 检测树节点的乱码
     */
    protected function checkTree($gameId)
    {
        $this->log(sprintf('Start %s check tree encode.', $gameId));
        $garbled = (new garbled_TreeEncodeCheck())->check($gameId)->getGarbled();
        $this->log(sprintf('End %s check tree encode.', $gameId));
        if (empty($garbled)) return;

        $report = new common_ReportInfo();
        foreach ($garbled as $index => &$collect) {
            $info = $report->getGarbledInfo($collect['garbled'], $gameId);
            if (!$info) $info = $this->getDefaultTreeInfo();
            $collect['count'] = count($info);
            $collect['info'] = $info;
        }
        $this->addContent(array(
            'thead' => array(
                TM::t('tongji', '树节点'),
                TM::t('tongji', '树节点ID'),
                TM::t('tongji', '乱码'),
                'stid', 'sstid'
            ),
            'garbled' => $garbled
        ));
    }
}

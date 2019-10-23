<?php
class Item extends TMController
{
    public function actions()
    {
        return array(
            'upload' => array('game_id' => null)
        );
    }

    /**
     * @brief uploadXML 
     *
     * @param $aUserParameters
     *
     * @return 
     */
    public function upload($aUserParameters)
    {
        TMValidator::ensure($aUserParameters['game_id'], TM::t('tongji', '上传道具名称必须指定游戏！'));
        $file = $this->uploadXML($aUserParameters);
        set_error_handler(array($this,'handleError'));
        $reader = new XmlReader();
        TMValidator::ensure($reader->open($file), TM::t('tongji', '文件不能正常打开！'));
        $oModel = new gamecustom_CustomMsgidInfo();
        $itemAttrs = array('ID', 'Type', 'First', 'Second');
        $selectAttrs = array('Third', 'Fourth');
        $aParams = array();
        $count = 0;
        $transaction = TM::app()->getDb()->beginTransaction();
        try {
            while ($reader->read()) {
                $count++;
                $itemIsFailed = false;
                if ($reader->nodeType !== XMLReader::ELEMENT || strtolower($reader->name) !== 'stat') {
                        continue;
                }
                $third = $fourth = null;
                foreach ($itemAttrs as $attr) {
                    if (($aParams[strtolower($attr)] = $reader->getAttribute($attr)) === null ||
                        empty($aParams[strtolower($attr)])) {
                        $itemIsFailed = true;
                        break;
                    }
                }
                $itemIsFailed && TMValidator::ensure(false, TM::t('tongji', '第{id}行缺少ID, Type, First, Second 中的一种', array('{id}' => $count)));
                $third = $reader->getAttribute('Third');
                $fourth = $reader->getAttribute('Fourth');

                $oModel->replace(array(
                    'game_id' => $aUserParameters['game_id'],
                    'type' => $aParams['type'],
                    'id' => $aParams['id'],
                    'first' => $aParams['first'],
                    'second' => $aParams['second'],
                    'third' => isset($third) ? $third : '',
                    'fourth' => isset($fourth) ? $fourth : ''
                ));
            }
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollback();
            $warnInfo = $oModel->getErrors();
            if (!empty($warnInfo)) {
                $this->ajax(0, array('warn_info' => $warnInfo));
            }
            throw $e;
        }
        restore_error_handler();
        $this->ajax(0, array('warn_info' => null));
    }

    /**
     * Upload item file.
     *
     * @param  array  $aUserParameters
     * @return string The item path
     */
    protected function uploadXML($aUserParameters)
    {
        $path = $this->getXMLPath();
        TMUploadFile::register();
        $file = new TMUploadFile('files', new TMUploadFileSystem($path, true));
        $file->setName($aUserParameters['game_id'] . time());
        $file->addValidators(array(
            new TMUploadMimetypeValidator(array('application/xml', 'text/plain')),
            new TMUploadExtensionValidator('xml')))
        ->upload();

        return $path . $file->getNameWithExtension();
    }

    /**
     * Get path for item files, create it if not exists.
     *
     * @return string      The item path.
     * @throws TMException If fails to get the path.
     */
    protected function getXMLPath()
    {
        $path = rtrim(TM::app()->getRuntimePath(), DS) . DS . 'xml' . DS;
        if (TMFileHelper::mkdir($path) === false) {
            throw new TMException(TM::t('tongji', '道具目录{dir}不可写！', array('{dir}' => $path)));
        }
        return $path;
    }

    /**
     * @brief handleError
     * 通用错误处理函数
     */
    public function handleError($iErrorCode, $message)
    {
        if ($iErrorCode) {
            restore_error_handler();
            throw new TMValidatorException(TM::t('taomee', 'xml格式不正确或者不是UTF-8格式'));
        }
    }
}


<?php
abstract class common_Base extends TMController
{
    /**
     * @var DateMarker
     */
    private $_dateMarker = null;
/**
     * @var CsvExporter
     */
    protected $oExporter = null;

    /**
     * @var string
     */
    protected $sFilename = null;

    /**
     * Init exporter.
     * @param array $aUserParameters
     * @return CsvExporter
     */
    protected function initExporter($aUserParameters)
    {
        if (!isset($this->oExporter)) {
            $sUserName = TM::app()->getUser()->getUserName();
            $this->oExporter = TM::createComponent(array(
                'class' => 'application.components.CsvExporter',
                'defaultDirname' => 'files' . DS . $sUserName
            ));
            $this->sFilename = $sUserName;
            if (isset($aUserParameters['file_name'])) {
                if ($sFilename = TMFileHelper::sanitizeFilename($aUserParameters['file_name'])) {
                    $this->sFilename = $sFilename;
                }
            }
        }
        return $this->oExporter;
    }

    /**
     * Write data to file.
     * @param  array   $aData
     * @param  string  $sFilename
     * @param  boolean $bConvertEncoding whether to convert the filename's encoding
     * @return string  the path of the new file
     */
    protected function writeTimeSeries($aData, $sFilename = null, $bConvertEncoding = false)
    {
        if (isset($sFilename)) $this->oExporter->add($sFilename, $bConvertEncoding);
        if (!$aData) return $this->oExporter->getFilePath();

        foreach ($aData as $data) {
            $this->oExporter->putWithTitle(TM::t('tongji', '日期'), $data['key']);
            foreach ($data['data'] as $sub) {
                $this->oExporter->putWithTitle($sub['name'], $sub['data']);
            }
            $this->oExporter->put(); // Blank line
        }

        return $this->oExporter->getFilePath();
    }

    /**
     * Goto exporting file.
     *
     * @param  array $props
     * @param  array $params
     * @param  array $prefix
     * @return null
     */
    protected function gotoExportFile($props, $params, $prefix)
    {
        $file = tool_File::createFile(tool_File::SRC_EXPORT, $props);
        if (!$file->isLarge()) return;

        $params['columns'] = TMArrayHelper::assoc('cols', $props, 90/* three months */);
        $file->setFileName($this->sFilename)
             ->setParams($params)
             ->initFileKey($params, $prefix);

        $userFile = new tool_UserFile();
        $this->ajax(0, $userFile->addFile(
            $file,
            [
                'user_id' => TM::app()->getUser()->getUserId(),
                'user_name' => TM::app()->getUser()->getUserName()
            ]
        ));
    }

    /**
     * Get the date marker.
     * @return DateMarker
     */
    private function _getDateMarker()
    {
        if (!isset($this->_dateMarker)) {
            $this->_dateMarker = TM::createComponent(array('class' => 'application.components.DateMarker'));
        }
        return $this->_dateMarker;
    }

    /**
     * Get the marked date.
     * @param string $suffix
     * @return array
     */
    protected function getDateMark($suffix = '')
    {
        return $this->_getDateMarker()->getDateMark($suffix);
    }

    /**
     * Mark the date.
     * @param array $aDate
     */
    protected function markDate($aDate)
    {
        $this->_getDateMarker()->markDate($aDate);
    }

    /**
     * 检查数据令牌
     * @param array $aUserParameters
     */
    protected function checkToken($aUserParameters)
    {
        $oToken = TM::createComponent(array(
            'class' => 'application.components.Token'
        ));
        if (!$oToken->valid($aUserParameters['token'])) {
            TM::app()->getUser()->setAuthorized(false)->forbidden();
        }
    }

    /**
     * This method will be called before action runs.
     * @param array $aParameters
     */
    protected function beforeRunAction($aParameters)
    {
        // 检查游戏权限
        if (isset($aParameters['game_id']) && $aParameters['game_id']) {
            10000 != $aParameters['game_id'] && (new common_Game())->checkGameAuth($aParameters['game_id']);
        }
        if ((!isset($aParameters['not_mark_date']) || !$aParameters['not_mark_date']) &&
                isset($aParameters['period']) &&
                $aParameters['period'] != data_time_PeriodFactory::TYPE_MINUTE &&
                $aParameters['period'] != data_time_PeriodFactory::TYPE_HOUR) {
            $this->markDate($aParameters);
        }
    }
}

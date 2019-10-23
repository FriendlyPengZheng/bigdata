<?php
class TMLog extends TMComponent
{
    const TYPE_WARNING = 'warn';
    const TYPE_ERROR = 'error';
    const TYPE_INFO = 'info';

    private $_sLogPath;

    private $_sLogFilePrefix = 'log_';

    private $_fileSize = 5242880; // 5M

    public function init()
    {
        if ($this->getLogPath() === null) {
            $sRuntimePath = TM::app()->getRuntimePath();
            $sLogPath = $sRuntimePath . DS . 'log';
            if (!is_dir($sLogPath)) {
                mkdir($sLogPath, 0777);
            }
            $this->setLogPath($sLogPath);
        }
    }

    /**
     * @brief getLogPath
     * 获取日志目录
     *
     * @return {string}
     */
    public function getLogPath()
    {
        return $this->_sLogPath;
    }

    /**
     * @brief setLogPath
     * 设置日志目录
     *
     * @param {string} $sPath
     * @throw TMException
     */
    public function setLogPath($sPath)
    {
        $this->_sLogPath = realpath($sPath);
        if (false === $this->_sLogPath || !is_dir($this->_sLogPath) || !is_writable($this->_sLogPath)) {
            throw new TMLogException(TM::t('taomee', "日志目录{$this->_sLogPath}必须存在且可写。"));
        }
        if (!is_dir($this->_sLogPath . DS . 'error')) {
            @mkdir($this->_sLogPath . DS . 'error', 0777);
        }
        if (!is_dir($this->_sLogPath . DS . 'info')) {
            @mkdir($this->_sLogPath . DS . 'info', 0777);
        }
        if (!is_dir($this->_sLogPath . DS . 'warn')) {
            @mkdir($this->_sLogPath . DS . 'warn', 0777);
        }
    }

    /**
     * @brief mkdir 
     * 创建日志目录
     */
    protected function mkdir($type)
    {
        if (!is_dir($this->_sLogPath . DS . $type)) {
            TMFileHelper::mkdir($this->_sLogPath . DS . $type);
        }
    }

    /**
     * @brief log
     * 记录日志
     *
     * @param {string} $sLogMsg
     * @param {string} $sLogType
     * @param {string} $sLogFilePrefix
     */
    public function log($sLogMsg, $sLogType='warn', $sLogFilePrefix='')
    {
        $this->mkdir($sLogType);
        $sLogFilePrefix = $sLogFilePrefix ? $sLogFilePrefix : $this->_sLogFilePrefix;
        $sLogFile = $this->getLogPath() . DS . $sLogType . DS . $sLogFilePrefix . date('Y-m-d') . '.log';
        if (file_exists($sLogFile) && filesize($sLogFile) >= $this->_fileSize) {
            @rename($sLogFile, $sLogFile.time());
        }
        $sCurrent = date('Y-m-d H:i:s');
        error_log("[$sCurrent]\n$sLogMsg\n", 3, $sLogFile);
    }
}

class TMLogException extends TMException
{
}

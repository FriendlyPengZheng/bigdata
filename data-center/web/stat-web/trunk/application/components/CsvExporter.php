<?php
/*
 * CSV exporter.
 */
class CsvExporter extends TMComponent
{
    const ENCODE_NONE = 1;
    const ENCODE_PREV = 2;
    const ENCODE_ALL  = 3;

    /**
     * @var string 默认文件夹
     */
    public $defaultDirname = null;

    /**
     * @var string 临时文件夹
     */
    private $_sTempDirname = null;

    /**
     * @var string 临时路径
     */
    private $_sTempPath = null;

    /**
     * @var string 当前文件全路径
     */
    private $_sFilePath = '';

    /**
     * @var resource 当前文件句柄
     */
    private $_rFileHandle = null;

    /**
     * @var array 文件数组
     */
    private $_aFiles = array();

    /**
     * constructor
     */
    public function __construct()
    {
    }

    /**
     * 初始化组件
     */
    public function init()
    {
        if (!isset($this->defaultDirname)) {
            throw new CsvExporterException(TM::t('taomee', '须指定默认目录！'));
        }
    }

    /**
     * 添加文件
     * @param string $sBasename
     * @param boolean $bConvertEncoding
     * @param string $sDirname
     * @throw CsvExporterException
     */
    public function add($sBasename, $bConvertEncoding = false, $sDirname = null)
    {
        $aPathInfo = $this->_pathinfo($sDirname, $sBasename);
        if ($bConvertEncoding) {
            $this->_setFileName($aPathInfo, iconv('UTF-8', 'GBK//IGNORE', $aPathInfo['filename']));
        }
        $sFileName = $aPathInfo['filename'];
        $iCount    = 1;
        $sFilePath = $this->_getFileFullPath($aPathInfo);
        while (isset($this->_aFiles[$sFilePath])) {
            $this->_setFileName($aPathInfo, $sFileName . '(' . $iCount . ')');
            $sFilePath = $this->_getFileFullPath($aPathInfo);
            $iCount++;
        }
        $this->_aFiles[$sFilePath] = $aPathInfo;
        $this->_sFilePath          = $sFilePath;
        if (file_exists($this->_sFilePath)) {
            unlink($this->_sFilePath);
        }
        $this->close();
        $this->_rFileHandle = fopen($this->_sFilePath, 'w');
        if (false === $this->_rFileHandle) {
            throw new CsvExporterException(TM::t('taomee', '文件{filepath}不可写！', array('{filepath}' => $this->_sFilePath)));
        }
    }

    /**
     * 分解文件路径信息，过滤掉文件名非法字符
     * @param string $sDirname
     * @param string $sBasename
     * @param string $sExtension
     * @return array
     */
    private function _pathinfo($sDirname, $sBasename, $sExtension = '.csv')
    {
        if (!isset($sDirname)) {
            $sDirname = $this->defaultDirname;
        }
        $aPathInfo = array(
            'dirname'  => rtrim($sDirname, DIRECTORY_SEPARATOR),
            'basename' => TMFileHelper::sanitizeFilename($sBasename)
        );
        if ($this->_sTempDirname) {
            $aPathInfo['dirname'] .= DIRECTORY_SEPARATOR . $this->_sTempDirname;
            $this->_sTempPath = $aPathInfo['dirname'];
        }
        if (!TMFileHelper::mkdir($aPathInfo['dirname'])) {
            throw new CsvExporterException(TM::t('taomee', '路径{dirname}不可创建！', array('{dirname}' => $aPathInfo['dirname'])));
        }
        if ($sExtension === substr($aPathInfo['basename'], -4)) {
            $aPathInfo['filename']  = substr($aPathInfo['basename'], 0, strlen($aPathInfo['basename']) - 4);
        } else {
            $aPathInfo['filename']  = $aPathInfo['basename'];
        }
        $aPathInfo['extension'] = $sExtension;
        return $aPathInfo;
    }

    /**
     * 设置文件名
     * @param array $aPathInfo
     * @param array $sFileName
     */
    private function _setFileName(&$aPathInfo, $sFileName)
    {
        $aPathInfo['filename'] = $sFileName;
        $aPathInfo['basename'] = $aPathInfo['filename'] . $aPathInfo['extension'];
    }

    /**
     * 拼装文件全路径
     * @param array $aPathInfo
     * @return string
     */
    private function _getFileFullPath($aPathInfo)
    {
        return $aPathInfo['dirname'] . DIRECTORY_SEPARATOR . $aPathInfo['filename'] . $aPathInfo['extension'];
    }

    /**
     * 列写入
     * @param array $aTitle
     * @param array $aContent
     */
    public function putColumns($aTitle, $aContent)
    {
        $this->put($aTitle, self::ENCODE_ALL);
        foreach ($aContent[0] as $idx => $data) {
            $aRow = array();
            foreach ($aContent as &$value) {
                $aRow[] = $value[$idx];
            }
            $this->put($aRow);
        }
    }

    /**
     * 写一行
     * @param array $aRow
     * @param integer $iEncodeType
     * @param integer $iOffset
     */
    public function put($aRow = array(), $iEncodeType = self::ENCODE_NONE, $iOffset = 1)
    {
        if (!is_array($aRow)) return;

        $bEncoding = true;
        switch ($iEncodeType) {
            case self::ENCODE_PREV:
                $iOffset = min((int)$iOffset, count($aRow));
                break;
            case self::ENCODE_ALL:
                $iOffset = count($aRow);
                break;
            default:
                $bEncoding = false;
                break;
        }
        if ($bEncoding) {
            for ($i = 0; $i < $iOffset; $i++) {
                $aRow[$i] = iconv('UTF-8', 'GBK//IGNORE', $aRow[$i]);
            }
        }
        if (fputcsv($this->_rFileHandle, $aRow) === false) {
            throw new CsvExporterException(TM::t('taomee', '写入错误！'));
        }
    }

    /**
     * 带title写一行
     * @param string $sTitle
     * @param array $aRow
     * @param boolean $bReverse
     */
    public function putWithTitle($sTitle, $aRow = array(), $bReverse = true)
    {
        $sTitle = iconv('UTF-8', 'GBK//IGNORE', $sTitle);
        if ($bReverse) {
            $aRow[] = $sTitle;
            $aRow = array_reverse($aRow);
        } else {
            array_unshift($aRow, $sTitle);
        }
        $this->put($aRow);
    }

    /**
     * 获取当前文件全路径
     * @return string
     */
    public function getFilePath()
    {
        return $this->_sFilePath;
    }

    /**
     * 设置临时文件夹，默认当前时间戳
     * @param string $sTempDirname
     * @return CsvExporter
     */
    public function setTempDirname($sTempDirname = null)
    {
        if (isset($sTempDirname)) {
            $this->_sTempDirname = trim(TMFileHelper::sanitizeFilename($sTempDirname), DIRECTORY_SEPARATOR);
        } else {
            $this->_sTempDirname = time();
        }
        return $this;
    }

    /**
     * 获取临时文件夹名称
     * @return string
     */
    public function getTempDirname()
    {
        return $this->_sTempDirname;
    }

    /**
     * 压缩打包
     * @param string $sBasename
     * @param string $sFormat
     * @param string $sDirname
     * @return boolean
     */
    public function pack($sBasename, $sFormat = 'tgz', $sDirname = null)
    {
        // 跳出临时文件夹
        $this->_sTempDirname = null;
        if (empty($this->_aFiles)) {
            throw new CsvExporterException(TM::t('taomee', '没有文件生成！'));
        }
        $sExtension = '.' . $sFormat;
        $aPathInfo = $this->_pathinfo($sDirname, $sBasename, $sExtension);
        $sFilePath = $this->_getFileFullPath($aPathInfo);
        if ($aPathInfo['extension'] === '.tgz') {
            $this->_tar($sFilePath);
        } elseif ($aPathInfo['extension'] === '.zip') {
            $this->_zip($sFilePath);
        } else {
            throw new CsvExporterException(TM::t('taomee', '不支持压缩文件类型{format}！', array('{format}' => $sFormat)));
        }
        if ($this->_sTempPath) {
            TMFileHelper::rmdir($this->_sTempPath);
        }
        return $sFilePath;
    }

    /**
     * Tar格式打包
     * @param string $sFilePath
     */
    private function _tar($sFilePath)
    {
        if (file_exists($sFilePath)) {
            unlink($sFilePath);
        }
        $sCommand = '/bin/tar czf "' . $sFilePath . '"';
        foreach ($this->_aFiles as $file) {
            $sCommand .= ' -C "' . realpath($file['dirname']) . '" "' . $file['basename'] . '"';
        }
        system($sCommand, $iStatus);
        if ($iStatus) {
            throw new CsvExporterException(TM::t('taomee', '无法压缩文件，错误码{code}！', array('{code}' => $iStatus)));
        }
    }

    /**
     * Zip格式打包
     * @param string $sFilePath
     */
    private function _zip($sFilePath)
    {
        $oZip = new ZipArchive();
        if (!$oZip->open($sFilePath, ZIPARCHIVE::OVERWRITE)) {
            throw new CsvExporterException(TM::t('taomee', '无法打开文件{file}！', array('{file}' => $sFilePath)));
        }
        foreach ($this->_aFiles as $fullpath => $file) {
            if (!$oZip->addFile($fullpath, $file['basename'])) {
                throw new CsvExporterException(TM::t('taomee', '无法压缩文件{file}！', array('{file}' => $fullpath)));
            }
        }
        $oZip->close();
    }

    /**
     * 关闭文件描述符
     */
    public function close()
    {
        if ($this->_rFileHandle) {
            fclose($this->_rFileHandle);
        }
    }
}

/*
 * Exception class for CSV exporter.
 */
class CsvExporterException extends TMException
{
}

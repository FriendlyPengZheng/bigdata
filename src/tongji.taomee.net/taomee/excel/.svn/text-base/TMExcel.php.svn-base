<?php
/**
 * TMExcel class
 */
class TMExcel extends TMComponent
{
    /*
     * phpExcel class
     */
    public $objectExcel = null;

    /**
     * @var string 默认文件夹
     */
    public $defaultDirname = null;

    /**
     * @var string 创建者
     */
    public $creator = null;

    /**
     * @var string 最近一次修改者
     */
    public $lastModifiedBy = null;

    /**
     *  格式config
     */
    private $_styleConfig = array();

    /*
     * now the working sheet
     */
    private $_activeSheet = null;

    /*
     * the sheet array
     */
    private $_aSheet = [];

    /*
     * Writer
     */
    private $_oWrite = null;

    /*
     * Reader
     */
    private $_oReader = null;

    /**
     * @var string the path which the classes relative to.
     */
    private static $_relativePath = 'system.excel.';

    /*
     * @var array the classes that this component needs.
     */
    private static $_dependentClasses = array(
        'required' => array(
            'TMExcel'
        ),
        'optional' => array(
             'PHPExcel'
        )
    );

    /**
     * @brief init
     *
     * @return
     */
    public function init()
    {
        parent::init();
        foreach (self::$_dependentClasses as $key => $classes) {
            $autoload =  ($key === 'required') ? true : false;
            foreach($classes as $class) {
                TM::import(self::$_relativePath . $class, $autoload);
            }
        }
        $this->objectExcel = new PHPExcel();
        if (!isset($this->defaultDirname)) {
            throw new TMExcelException(TM::t('taomee', 'Must specify a default directory!'));
        }
        $this->_aSheet[] = $this->getActiveSheet();
    }

    public function __construct()
    {
    }

    /**
     * @brief checkCell 
     * 检查单元格格式
     * @param {string} $cell 单元格{A1, B34...}
     *
     */
    protected function checkCell($cell)
    {
        TMValidator::ensure(preg_match('/^[A-Z]+\d+$/', $cell), TM::t('taomee', '{0} cell is not the correct format', array('{0}' => $cell)));
    }

    /**
     * @brief save
     * 生成,保存Excel文件
     * @param {string}  $sBasename 文件名(可带后缀,默认系统带(.xlsx))
     * @param {boolean} $bConvertEncoding 文件名需要转码不
     * @param {string}  $sDirname 文件目录
     *
     * @return {array}
     */
    public function save($sBasename, $sDirname = null)
    {
        $aPathInfo = $this->_pathinfo($sDirname, $sBasename);
        $this->_setFileName($aPathInfo, $aPathInfo['filename']);
        $this->_oWrite = PHPExcel_IOFactory::createWriter($this->objectExcel, 'Excel2007');
        $tempFile = $aPathInfo['dirname'] . DIRECTORY_SEPARATOR . $aPathInfo['basename'];
        $this->_oWrite->save($tempFile);
        return $aPathInfo;
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
     * @brief _pathinfo
     * 对路径进行处理
     * @param {string} $sDirname
     * @param {string} $sBasename
     * @param {string} $sExtension
     *
     * @return {array}
     */
    private function _pathinfo($sDirname, $sBasename, $sExtension = '.xlsx')
    {
        if (!isset($sDirname)) {
            $sDirname = $this->defaultDirname;
        }
        $aPathInfo = array(
            'dirname' => rtrim($sDirname, DIRECTORY_SEPARATOR),
            'basename' => TMFileHelper::sanitizeFilename($sBasename)
        );
        if (!TMFileHelper::mkdir($aPathInfo['dirname'])) {
            throw new TMExcelException(TM::t('taomee', 'path{dirname}can not be created！', array('{dirname}' => $aPathInfo['dirname'])));
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
     * @brief _setFileName 
     * 设置文件名与带后缀的文件名
     * @param {array}  &$aPathInfo
     * @param {string} $sFileName
     *
     */
    private function _setFileName(&$aPathInfo, $sFileName)
    {
        $aPathInfo['filename'] = $sFileName;
        $aPathInfo['basename'] = $aPathInfo['filename'] . $aPathInfo['extension'];
    }

    /**
     * @brief load
     * 加载一个已经存在的excel文件
     * @param {string} $sBasename
     * @param {string} $title
     * @param {string} $sDirname
     *
     * @return {TMExcel} 
     */
    public function load($sBasename, $title = null, $sDirname = null)
    {
        $aPathInfo = $this->_pathinfo($sDirname, $sBasename);
        $this->_setFileName($aPathInfo, $aPathInfo['filename']);
        $this->_oReader = PHPExcel_IOFactory::createReader('Excel2007');
        $tempFile = $aPathInfo['dirname'] . DIRECTORY_SEPARATOR . $aPathInfo['basename'];
         if(!file_exists($tempFile)) {
            TMValidator::ensure(false, TM::t('taomee', 'Loading the Excel file does not exist.'));
         }
        $this->objectExcel = $this->_oReader->load($tempFile);
        if(isset($this->creator)) {
            $this->getProperties()->setCreator($this->creator);
        }
        if(isset($this->lastModifiedBy)) {
            $this->getProperties()->setLastModifiedBy($this->lastModifiedBy);
        }
        $this->_aSheet = [];
        $this->_aSheet[] = $this->getActiveSheet();
        if(isset($title)) {
            $this->getActiveSheet()->setTitle($title);
        }
        return $this;
    }

    /**
     * @brief setFileTitle 
     *
     * @param {string} $title
     *
     * @return {TMExcel}
     */
    public function setFileTitle($title = null)
    {
        if(isset($title)) {
            $this->getProperties()->setTitle($title);
        }
        return $this;
    }

    /**
     * @brief getProperties
     * 获得当前excel的基本信息
     * @return {PHPExcel_DocumentProperties}
     *
     */
    public function getProperties()
    {
        return $this->objectExcel->getProperties();
    }

    /**
     * @brief getActiveSheet
     * 获得当前操作的工作区
     * @return {PHPExcel_Worksheet}
     *
     */
    public function getActiveSheet()
    {
        return $this->objectExcel->getActiveSheet();
    }

    /**
     * @brief createSheet
     * 创建一个新工作区
     * @param {string} $title 工作sheet名字
     *
     */
    public function createSheet($title = null)
    {
        if(count($this->_aSheet) === 0) {
            $this->_aSheet[] = $this->getActiveSheet();
        }
        $this->objectExcel->createSheet();
        $this->_aSheet[] = $this->getActiveSheet();
        $this->objectExcel->setActiveSheetIndex(count($this->_aSheet) - 1);
        if(isset($title)) {
            $this->getActiveSheet()->setTitle($title);
        }
    }

    /**
     * @brief setActiveSheetTitle
     * 设置当前工作sheet名称
     * @param {string} $title
     *
     * @return {TMExcel}
     */
    public function setActiveSheetTitle($title = null)
    {
        if(isset($title)) {
            $this->getActiveSheet()->setTitle($title);
        }
        return $this;
    }

    /**
     * @brief addStyleConfig
     * 设置单元格配置信息
     * @param {array} $aStyle
     * @param {TMExcelCellStyle} $oStyle
     *
     * @return {TMExcel}
     */
    public function addStyleConfig($name, TMExcelCellStyle $oStyle)
    {
        if(!isset($this->_styleConfig[$name])) {
            $this->_styleConfig[$name] = $oStyle->getStyle();
        }
        return $this;
    }

    /**
     * @brief getStyleConfig
     * @param {string} $name
     * 获得配置信息
     *
     * @return {array} | {PHPExcel_Style}
     */
    public function getStyleConfig($name = null)
    {
        if(isset($name) && isset($this->_styleConfig[$name])) {
            return $this->_styleConfig[$name];
        }
        return $this->_styleConfig;
    }

    /**
     * @brief setFormatCells
     * 从已有的格式中获取style应用于选定的单元格
     * @param {string} $start 单元格起始 类型{A1, B34...}
     * @param {string} $end   单元格结束 类型{A1, B34...}
     * @param {string} $styleType 样式名称
     * @param {boolean} $combineCells 是否有合并单元格操作
     *
     */
    public function setFormatCells($start, $end, $styleType = null, $combineCells = false)
    {
        if(empty($start) || empty($end)) return;
        $objStyle = isset($this->_styleConfig[$styleType]) ? $this->_styleConfig[$styleType] : null;
        $this->checkCell($start) && $this->checkCell($end);
        $range = implode(':', array($start, $end));
        if($combineCells) {
            $this->getActiveSheet()->mergeCells($range);
        }
        if($objStyle) {
            $this->getActiveSheet()->duplicateStyle($objStyle, $range);
        }
    }

    /**
     * @brief putColumsContent
     * 写一列
     * @param {array} $aData
     * @param {string} $start 单元格起始 类型{A1, B34...}
     *
     */
    public function putColumns($aData = array(), $start)
    {
        if (!is_array($aData) || empty($start)) return;
        foreach($aData as $title) {
            $this->putRows((array)$title, $start);
            preg_match('/^[A-Z]+/', $start, $col);
            $start = substr($start, 0, strlen($col[0])) . ((int)substr($start, strlen($col[0])) + 1);
        }
    }

    /**
     * 写一行
     * @param {array}   $aRow
     * @param {string}  $start
     *
     */
    public function putRows($aRow = array(), $start)
    {
        if (!is_array($aRow) || empty($start)) return;
        $this->checkCell($start);
        $this->getActiveSheet()->fromArray($aRow, NULL, $start);
    }

    /**
     * @brief setColumnCellSize
     * 设置一列单元格大小
     * @param {string} $start 单元格起始 类型{A, B...}
     * @param {integer} $size 单元格大小 
     *
     */
    public function setColumnCellSize($start, $size = null)
    {
        TMValidator::ensure(preg_match('/^[A-Z]+/', $start), TM::t('taomee', '{0} cell is not the correct format', array('{0}' => $start)));
        if(!isset($size)) {
            $size = 10;
        }
        $this->getActiveSheet()->getColumnDimension($start)->setWidth($size);
    }

}

/**
 * TMExcelException class.
 */
class TMExcelException extends TMException
{
}


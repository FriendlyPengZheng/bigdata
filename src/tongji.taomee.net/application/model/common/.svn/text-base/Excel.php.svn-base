<?php
class common_Excel
{
    const LEFTLINE  = 'left';
    const RIGHTLINE  = 'right';
    const TOPLINE  = 'top';
    const BOTTOMLINE  = 'bottom';

    public function __construct()
    {
        TM::app()->MonthExcel = TM::app()->MonthExcel;
        TM::app()->MonthExcel->init();
    }

    public function init()
    {
         TM::app()->MonthExcel->setFileTitle('Tongji');
    }

    /**
     * @brief createColumContent 
     * 产生一列的表头
     * @param {string} $start 起始单元格
     * @param {array}  $aContent 表头信息
     * @param {integer} $configType 配置类型
     *
     * @return
     */
    public function createColumContent($start, $aContent, $configType = null)
    {
         $aContent = $this->_setContentConfig($start, $aContent, $configType);
         TM::app()->MonthExcel->putColumns($aContent, $start);
    }

    /**
     * @brief createRowContent 
     * 产生一行的表头
     * @param {string}  $start 起始单元格
     * @param {array}   $aContent 表头信息
     * @param {integer} $configType 配置类型
     *
     * @return 
     */
    public function createRowContent($start, $aContent, $configType = null)
    {
         $aContent = $this->_setContentConfig($start, $aContent, $configType, true);
         TM::app()->MonthExcel->putRows($aContent, $start);
    }

    /**
     * @brief _setContentConfig 
     * 为表头设置配置
     * @param {string} $start 起始单元格
     * @param {array}  $aContent 表头信息
     * @param {integer} $configType 配置类型
     * @param {boolean} $isCol 是一列表头不
     *
     * @return {array}
     */
    private function _setContentConfig($start, $aContent, $configType, $isCol = false)
    {
         if(!isset($aContent) || !is_array($aContent)) {
               TMValidator::ensure(false, TM::t('tongji', '{0}写入Excel中的数据不合法', array('{0}' => $aContent)));
         }
         $activeSheet = TM::app()->MonthExcel->getActiveSheet();
         $oStyle = common_ExcelFactory::getExcelConfig($configType, $start, $activeSheet);
         if(isset($configType)) {
             TM::app()->MonthExcel->addStyleConfig($start, $oStyle);
         }

         if(isset($aContent[0])) {
            $end = $this->_add($start, count($aContent) - 1, $isCol);
            TM::app()->MonthExcel->setFormatCells($start, $end, $start);
            return $aContent;
         }
         else {
            $tmpStart = $start;
            foreach($aContent as $content => $num) {
                $end = $this->_add($tmpStart, $num - 1, $isCol);
                TM::app()->MonthExcel->setFormatCells($tmpStart, $end, $start, true);
                $this->_putCommmonContent($tmpStart, (array)$content, $isCol);
                $tmpStart = $this->_add($end, 1, $isCol);
            }
            return array();
         }
    }

    /**
     * @brief _putCommmonContent 
     * 公共用于写表头数据,不含格式
     * @param {string}  $start 单元格,形如{A1, B23..}
     * @param {array}   $aContent 需要写入的内容
     * @param {boolean} $isCol 列增加
     *
     * @return 
     */
    private function _putCommmonContent($start, $aContent, $isCol) 
    {
        if($isCol) {
            TM::app()->MonthExcel->putRows($aContent, $start);
        }
        else {
            TM::app()->MonthExcel->putColumns($aContent, $start);
        }
    }

    /**
     * @brief load 
     * 加载已经存在的excel文件
     * @param {string} $sBasename
     * @param {string} $title
     * @param {string} $sDirname
     *
     * @return
     */
    public function load($sBasename, $title = null, $sDirname = null)
    {
        if(!isset($sDirname)) {
            $dirname = TM::app()->MonthExcel->defaultDirname;
            $sDirname = $dirname . DIRECTORY_SEPARATOR . 'source';
        }
        TM::app()->MonthExcel->load($sBasename, $title, $sDirname);
        $this->init();
    }

    /**
     * @brief createSheet 
     * 创建新的工作区(sheet)
     * @param $title
     *
     * @return 
     */
    public function createSheet($title = null)
    {
        TM::app()->MonthExcel->createSheet($title);
    }

    /**
     * @brief save 
     * 保存Excel文件
     * @param {string} $sBasename
     * @param {boolean} $bConvertEncoding
     *
     * @return 
     */
    public function save($filename)
    {
         $dirname = TM::app()->MonthExcel->defaultDirname;
         $dirname = $dirname . DIRECTORY_SEPARATOR . date('Y_m');
         $aPathInfo = TM::app()->MonthExcel->save($filename, $dirname);
         $fileFullPath = $aPathInfo['dirname'] . DIRECTORY_SEPARATOR . $aPathInfo['basename'];
         if(!file_exists($fileFullPath)) {
            TMValidator::ensure(false, TM::t('tongji', 'Excel文件不存在'));
         }
         return $aPathInfo;
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

    /**
     * @brief createRowData
     * 记录一行数据(带有配置(格式的))
     * @param {array} $aData 写入数据
     * @param {string} $start 起始单元格
     * @param {integer} $configType 配置类型
     *
     * @return
     */
    public function createRowData($aData, $start, $configType = null)
    {
         if(!isset($aData) || !is_array($aData)) {
               TMValidator::ensure(false, TM::t('tongji', '{0}写入Excel中的数据不合法', array('{0}' => $aContent)));
         }
         if(isset($configType)) {
            $end = $this->_add($start, count($aData) - 1, true);
            $activeSheet = TM::app()->MonthExcel->getActiveSheet();
            $oStyle = common_ExcelFactory::getExcelConfig($configType, $start, $activeSheet);
            TM::app()->MonthExcel->addStyleConfig($start, $oStyle);
            TM::app()->MonthExcel->setFormatCells($start, $end, $start);
         }
         TM::app()->MonthExcel->putRows($aData, $start);
    }


    /**
     * @brief setCellsSize 
     *
     * @param {integer} $size
     * @param {integer} $colLen 单元格,形如{A, B..}
     * @param {string}  $start
     * @return
     */
    public function setCellsSize($size = 20, $colLen = null, $start = 'A')
    {
        if(!$colLen) return;
        for($i = 0; $i < $colLen; $i++) {
            TM::app()->MonthExcel->setColumnCellSize($start, $size);
            $start = $this->_add($start, 1, true);
        }
    }

    /**
     * @brief drawLine 
     *
     * @param {string} $start 单元格,形如{A1, B23..}
     * @param {string} $end 单元格,形如{A1, B23..}
     * @param {0x} $position 十六进制
     *
     * @return 
     */
    public function drawLine($start, $end, $position)
    {
        $activeSheet = TM::app()->MonthExcel->getActiveSheet();
        $oStyle = TM::createComponent(array(
                    'class' => 'system.excel.TMExcelCellStyle',
                    'oSheet' => $activeSheet));
        $oStyle->init($start);
        $lineType = 'set' . ucfirst($position) . 'BorderStyle';
        $position = 'setBorders' . ucfirst($position) . 'Color';
        $oStyle->$position('FF000000')->$lineType('thin');
        TM::app()->MonthExcel->addStyleConfig($start, $oStyle);
        TM::app()->MonthExcel->setFormatCells($start, $end, $start);
    }
}


<?php
class common_ExcelFactory
{
    const TYPE_DATA_QOQ = 1;
    const TYPE_ONLY_DATA = 2;
    const TYPE_DATA_DIFF = 3;

    const TYPE_SPECIAL = 1;
    const TYPE_COMMON = 2;

    const TYPE_DATA_CONTENT = 1;
    const TYPE_FIRST_TITLE = 2;
    const TYPE_SECOND_BLUE_TITLE = 3;
    const TYPE_SECOND_TITLE = 4;
    const TYPE_ROW_CONTENT = 5;
    const TYPE_ROW_DATA = 6;

    public static function getParams($type)
    {
        $aParam = array();
        switch((int)$type) {
            case self::TYPE_DATA_QOQ:
                 $aParam = array('qoq' => 1, 'yoy' => 0);
                 break;
            case self::TYPE_ONLY_DATA:
            default:
                 $aParam = array('qoq' => 0, 'yoy' => 0);
        }
        return $aParam;
    }

    public static function getExcelConfig($type, $name, $obSheet)
    {
        $oStyle = TM::createComponent(array(
                    'class' => 'system.excel.TMExcelCellStyle',
                    'oSheet' => $obSheet));
        $oStyle->init($name);
        switch((int)$type) {
            case self::TYPE_DATA_CONTENT:
                $oStyle->setFontSize(10)->setFontName('Microsoft YaHei')->setFontColor(StyleCode::COLOR_BLACK)
                       ->setFillType('solid')->setFillColor(StyleCode::COLOR_SHADOW_BLUE)
                       ->setAlignmentHorizontal('right')->setAlignmentVertical('center')
                       ->setBordersTopColor(StyleCode::COLOR_BLACK)->setTopBorderStyle('thin');
                 break;
            case self::TYPE_FIRST_TITLE:
                 $oStyle->setFontSize(11)->setFontName('Microsoft YaHei')->setFontColor(StyleCode::COLOR_BLACK_THICK)
                        ->setFillType('solid')->setFillColor(StyleCode::COLOR_YELLOW)
                        ->setBordersTopColor(StyleCode::COLOR_BLACK)->setTopBorderStyle('thin')
                        ->setBordersRightColor(StyleCode::COLOR_BLACK)->setRightBorderStyle('thin')
                        ->setBordersBottomColor(StyleCode::COLOR_BLACK)->setBottomBorderStyle('thin')
                        ->setAlignmentHorizontal('center')->setAlignmentVertical('center');
                 break;
            case self::TYPE_SECOND_BLUE_TITLE:
                 $oStyle->setFontSize(10)->setFontName('Microsoft YaHei')->setFontColor(StyleCode::COLOR_WHITE)
                        ->setFillType('solid')->setFillColor(StyleCode::COLOR_BLUE)
                        ->setBordersTopColor(StyleCode::COLOR_BLACK)->setTopBorderStyle('thin')
                        ->setAlignmentHorizontal('center')->setAlignmentVertical('center');
                 break;
            case self::TYPE_SECOND_TITLE:
                 $oStyle->setFontSize(10)->setFontName('Microsoft YaHei')->setFontColor(StyleCode::COLOR_BLACK)
                        ->setAlignmentHorizontal('center')->setAlignmentVertical('center');
                 break;
            case self::TYPE_ROW_CONTENT:
                 $oStyle->setFontSize(14)->setFontName('Microsoft YaHei')->setFontColor(StyleCode::COLOR_WHITE)
                        ->setAlignmentHorizontal('center')->setAlignmentVertical('center')
                        ->setBordersTopColor(StyleCode::COLOR_BLACK)->setTopBorderStyle('thin')
                        ->setFillType('solid')->setFillColor(StyleCode::COLOR_BLUE);
                 break;
            case self::TYPE_ROW_DATA:
                 $oStyle->setFontSize(10)->setFontName('Microsoft YaHei')->setFontColor(StyleCode::COLOR_BLACK)
                        ->setAlignmentHorizontal('right')->setAlignmentVertical('center');
        }
        return $oStyle;
    }
}

class StyleCode
{
    const  COLOR_BLACK = 'FF000000';
    const  COLOR_BLACK_THICK = 'FF404040';
    const  COLOR_WHITE = 'FFFFFFFF';
    const  COLOR_BLUE = 'FF538DD5';
    const  COLOR_SHADOW_BLUE = 'FFC5D9F1';
    const  COLOR_YELLOW = 'FFFFFCA8';
    const  COLOR_GRAY = 'FFC0C0C0';
}


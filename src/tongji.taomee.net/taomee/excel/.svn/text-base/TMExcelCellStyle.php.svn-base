<?php
class TMExcelCellStyle extends TMComponent
{
    /**
     * style's name
     */
    public  $styleName = null;

    /**
     * object sheet class
     */
    public  $oSheet = null;

    /**
     * object Style class
     */
    private $_oStyle = null;

    public function __construct()
    {
    }

    public function init($name = null)
    {
        TMValidator::ensure(isset($this->oSheet), TM::t('taomee', 'Sheet object is not set'));
        if(isset($name)) {
            $this->styleName = $name;
        }
        TMValidator::ensure(isset($this->styleName), TM::t('taomee', 'Excel Style is not set its name!'));
        $this->_oStyle = $this->oSheet->getStyle($this->styleName);
    }

    /**
     * 获得style的类
     * @return {PHPExcel_Style}
     */
    public function getStyle()
    {
        if(isset($this->_oStyle)) {
            return $this->_oStyle;
        }
        TMValidator::ensure(false, TM::t('taomee', 'Excel StyleObject is not exisited'));
    }

    /**
     * 设置字体大小
     * @param $size
     *
     * @return {TMExcelCellStyle}
     */
    public function setFontSize($size)
    {
        $this->_oStyle->getFont()->setSize($size);
        return $this;
    }

    /**
     * 设置字体类型(如微软雅黑)
     * @param $name
     *
     * @return {TMExcelCellStyle}
     */
    public function setFontName($name)
    {
        $this->_oStyle->getFont()->setName($name);
        return $this;
    }

    /**
     * 设置字体颜色(ARGB)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setFontColor($color)
    {
        $this->_oStyle->getFont()->getColor()->setARGB($color);
        return $this;
    }

    /**
     * 设置单元格填充类型
     * @param $type
     *
     * @return {TMExcelCellStyle}
     */
    public function setFillType($type)
    {
        $this->_oStyle->getFill()->setFillType($type);
        return $this;
    }

    /**
     * 设置单元格填充颜色(ARGB)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setFillColor($color)
    {
        $this->_oStyle->getFill()->getStartColor()->setARGB($color);
        return $this;
    }

    /**
     * 设置上边框颜色(ARGB)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setBordersTopColor($color)
    {
        $this->_oStyle->getBorders()->getTop()->getColor()->setARGB($color);
        return $this;
    }

    /**
     * 设置下边框颜色(ARGB)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setBordersBottomColor($color)
    {
        $this->_oStyle->getBorders()->getBottom()->getColor()->setARGB($color);
        return $this;
    }

    /**
     * 设置左边框颜色(ARGB)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setBordersLeftColor($color)
    {
        $this->_oStyle->getBorders()->getLeft()->getColor()->setARGB($color);
        return $this;
    }

    /**
     * 设置右边框颜色(ARGB)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setBordersRightColor($color)
    {
        $this->_oStyle->getBorders()->getRight()->getColor()->setARGB($color);
        return $this;
    }

    /**
     * 设置单元格中文字在横向的位置(如居中)
     * @param $position
     *
     * @return {TMExcelCellStyle}
     */
    public function setAlignmentVertical($position)
    {
        $this->_oStyle->getAlignment()->setVertical($position);
        return $this;
    }

    /**
     * 设置单元格中文字在纵向的位置(如居中)
     * @param $position
     *
     * @return {TMExcelCellStyle}
     */
    public function setAlignmentHorizontal($position)
    {
        $this->_oStyle->getAlignment()->setHorizontal($position);
        return $this;
    }

    /**
     * 设置上边框类型(thick, thin)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setTopBorderStyle($type)
    {
        $this->_oStyle->getBorders()->getTop()->setBorderStyle($type);
        return $this;
    }

    /**
     * 设置下边框类型(thick, thin)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setBottomBorderStyle($type)
    {
        $this->_oStyle->getBorders()->getBottom()->setBorderStyle($type);
        return $this;
    }

    /**
     * 设置左边框类型(thick, thin)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setLeftBorderStyle($type)
    {
        $this->_oStyle->getBorders()->getLeft()->setBorderStyle($type);
        return $this;
    }

    /**
     * 设置右边框类型(thick, thin)
     * @param $color
     *
     * @return {TMExcelCellStyle}
     */
    public function setRightBorderStyle($type)
    {
        $this->_oStyle->getBorders()->getRight()->setBorderStyle($type);
        return $this;
    }
}

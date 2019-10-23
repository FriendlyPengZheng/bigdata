<?php
class data_calculator_Contrast extends TMComponent
{
    /**
     * @var array basic data for contrast.
     */
    private $_baseData = array();

    public function setBaseData($aBaseData)
    {
        $this->_baseData = $aBaseData;
        return $this;
    }

    public function getBaseData()
    {
        return $this->_baseData;
    }

    public function contrast($aContrastData)
    {
        if (!$this->_baseData || !$aContrastData) return;
        $count = min(count($this->_baseData), count($aContrastData));
        $aData = array();
        while ($count --) {
            $contrast = $aContrastData[$count];
            $aData[] = ((float)$contrast ? round(($this->_baseData[$count] / $contrast - 1) * 100, 2) : 0) . '%';
        }
        return array_reverse($aData);
    }
}

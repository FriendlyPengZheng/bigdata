<?php
class data_calculator_Average extends TMComponent
{
    protected $precision = 2;

    protected function filter($value)
    {
        return $value !== '' && $value !== null && $value !== '-';
    }

    public function setPrecision($precision)
    {
        $this->precision = (int)$precision;

        return $this;
    }

    public function getPrecision()
    {
        return $this->precision;
    }

    public function average($data)
    {
        $tmpData = array_filter($data, array($this, 'filter'));
        if (empty($tmpData)) return;

        return round(array_sum($tmpData)/count($tmpData), $this->getPrecision()) . ltrim($tmpData[key($tmpData)], '.0...9');
    }
}

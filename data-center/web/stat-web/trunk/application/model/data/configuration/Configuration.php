<?php
class data_configuration_Configuration extends TMComponent implements data_configuration_Configurable
{
    /**
     * @var string The name of the data configuration.
     */
    protected $dataName = null;

    /**
     * @var string The name of the gpzs configuration.
     */
    protected $gpzsName = null;

    /**
     * @var integer The data id.
     */
    protected $dataId = null;

    /**
     * @var integer The hash for find the database and table.
     */
    protected $hash = null;

    /**
     * @var integer The gpzs id.
     */
    protected $gpzsId = null;

    /**
     * @var float All values will be multiplied by the factor.
     */
    protected $factor = 1;

    /**
     * @var string The unit will be appended to all values.
     */
    protected $unit = null;

    /**
     * @var integer The number of decimal digits to round to.
     */
    protected $precision = 2;

    /**
     * @var array Extra infomation.
     */
    protected $extra = array();

    /**
     * @var boolean Whether authorize while set gpzs.
     */
    protected $authorize = true;

    /**
     * Create one data configuration by given parameters.
     */
    public function __construct($dataId, $hash, $gpzsId)
    {
        $this->dataId = $dataId;
        $this->hash = $hash;
        $this->gpzsId = $gpzsId;
    }

    /**
     * Get data id.
     * @return integer
     */
    public function getDataId()
    {
        return $this->dataId;
    }

    /**
     * Get the hash for find the database and table.
     * @return integer
     */
    public function getHash()
    {
        return $this->hash;
    }

    /**
     * Get gpzs id.
     * @return integer
     */
    public function getGpzsId()
    {
        return $this->authorize ? common_GpzsInfo::filter($this->gpzsId) : $this->gpzsId;
    }

    /**
     * Get data name.
     * @return string
     */
    public function getDataName()
    {
        return $this->dataName;
    }

    /**
     * Set data name.
     * @param string $dataName
     * @return data_configuration_Configuration
     */
    public function setDataName($dataName)
    {
        $this->dataName = $dataName;
        return $this;
    }

    /**
     * Get gpzs name.
     * @return string
     */
    public function getGpzsName()
    {
        return $this->gpzsName;
    }

    /**
     * Set gpzs name.
     * @param string $gpzsName
     * @return data_configuration_Configuration
     */
    public function setGpzsName($gpzsName)
    {
        $this->gpzsName = $gpzsName;
        return $this;
    }

    /**
     * Get the factor by which all values will be multiplied.
     * @return float
     */
    public function getFactor()
    {
        return $this->factor;
    }

    /**
     * Set the factor by which all values will be multiplied.
     * @param float $factor
     * @return data_configuration_Configuration
     */
    public function setFactor($factor)
    {
        $this->factor = $factor;
        return $this;
    }

    /**
     * Get the unit that will be appended to all values.
     * @return string
     */
    public function getUnit()
    {
        return $this->unit;
    }

    /**
     * Set the unit that will be appended to all values.
     * @param string $unit
     * @return data_configuration_Configuration
     */
    public function setUnit($unit)
    {
        $this->unit = $unit;
        return $this;
    }

    /**
     * Get the values' precision.
     * @return integer
     */
    public function getPrecision()
    {
        return $this->precision;
    }

    /**
     * Set the values' precision.
     * @param integer $precision
     * @return data_configuration_Configuration
     */
    public function setPrecision($precision)
    {
        $this->precision = $precision;
        return $this;
    }

    /**
     * Get key that represents this configuration uniquely.
     * @return string
     */
    public function getKey()
    {
        return $this->dataId . ':' . $this->gpzsId;
    }

    /**
     * Set extra infomation.
     * @param array $extra
     * @return data_configuration_Configuration
     */
    public function setExtra($extra)
    {
        $this->extra = $extra;
        return $this;
    }

    /**
     * Get extra infomation.
     * @return array
     */
    public function getExtra()
    {
        return $this->extra;
    }

    /**
     * Set authorize to be false
     * @return {data_configuration_Configuration}
     */
    public function closeAuthorize()
    {
        $this->authorize = false;
        return $this;
    }

    /**
     * Set authorize to be true
     * @return {data_configuration_Configuration}
     */
    public function openAuthorize()
    {
        $this->authorize = true;
        return $this;
    }
}

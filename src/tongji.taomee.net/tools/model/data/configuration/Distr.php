<?php
class data_configuration_Distr extends TMComponent
{
    /* How to distribute? */
    const TYPE_SUM = 1;
    const TYPE_AVG = 2; /* sum(DATA) / count(DATA) */
    const TYPE_ABSAVG = 3; /* sum(DATA) / count(not_null(DATA)) */
    const TYPE_MAX = 4;

    /* How to sort? */
    const SORT_DFL = 1;
    const SORT_ASC = 2;
    const SORT_DESC = 3;

    /* How to deal with percentage? */
    const PERCENTAGE_DATALIZED = 1;
    const PERCENTAGE_NORMAL = 2;
    const PERCENTAGE_DISABLED = 3;

    /**
     * @var data_configuration_ConfigurationSet
     */
    private $_configurationSet;

    /**
     * @var array Expressions.
     */
    private $_expressions;

    /**
     * @var string The name of the distribution.
     */
    private $_distrName;

    /**
     * @var string The dimension name of the distribution.
     */
    private $_dimenName;

    /**
     * @var integer How to distribute the given data.
     */
    private $_distrType = self::TYPE_SUM;

    /**
     * @var integer The number of decimal digits to round to.
     */
    private $_precision = 2;

    /**
     * @var integer How to sort.
     */
    private $_sortType = self::SORT_DFL;

    /**
     * @var integer Percentage type.
     */
    private $_percentageType = self::PERCENTAGE_NORMAL;

    /**
     * @var boolean Whether to be exported.
     */
    private $_export = true;

    /**
     * @var string Unit.
     */
    private $_unit = '';

    /**
     * @var array Extend value.
     */
    private $_extend = array();

    /**
     * Create a Distribution.
     * @param data_configuration_ConfigurationSet $configurationSet
     * @param string $distrName
     */
    public function __construct(data_configuration_ConfigurationSet $configurationSet, $distrName)
    {
        $this->_configurationSet = $configurationSet;
        $this->_distrName = $distrName;
    }

    /**
     * Create a new data_configuration_Distr instance.
     * @param data_configuration_ConfigurationSet $configurationSet
     * @param string $distrName
     * @return data_configuration_Distr
     */
    public static function newInstance(data_configuration_ConfigurationSet $configurationSet, $distrName)
    {
        return new self($configurationSet, $distrName);
    }

    /**
     * Get ConfigurationSet for distributing.
     * @return data_configuration_ConfigurationSet
     */
    public function getConfigurationSet()
    {
        return $this->_configurationSet;
    }

    /**
     * Get name of the distribution.
     * @return string
     */
    public function getDistrName()
    {
        return $this->_distrName;
    }

    /**
     * Get dimension name of the distribution.
     * @return string
     */
    public function getDimenName()
    {
        return $this->_dimenName;
    }

    /**
     * Set dimension name of the distribution.
     * @param string $dimenName
     * @return data_configuration_Distr
     */
    public function setDimenName($dimenName)
    {
        $this->_dimenName = $dimenName;
        return $this;
    }

    /**
     * Get type of distribution.
     * @return integer
     */
    public function getDistrType()
    {
        return $this->_distrType;
    }

    /**
     * Set distributing type.
     * @param integer $distrType
     * @return data_configuration_Distr
     */
    public function setDistrType($distrType)
    {
        $this->_distrType = $distrType;
        return $this;
    }

    /**
     * Get the precision, for TYPE_AVG and TYPE_ABSAVG.
     * @return integer
     */
    public function getPrecision()
    {
        return $this->_precision;
    }

    /**
     * Set precision, default 2.
     * @param integer $precision
     * @return data_configuration_Distr
     */
    public function setPrecision($precision)
    {
        $this->_precision = $precision;
        return $this;
    }

    /**
     * Get percentage type.
     * @return integer
     */
    public function getPercentageType()
    {
        return $this->_percentageType;
    }

    /**
     * Set percentage type.
     * @param integer $percentageType
     * @return data_configuration_Distr
     */
    public function setPercentageType($percentageType)
    {
        $this->_percentageType = (int)$percentageType;
        return $this;
    }

    /**
     * Whether to be exported.
     * @return boolean
     */
    public function isExport()
    {
        return $this->_export;
    }

    /**
     * Set whether to be exported or not.
     * @param boolean $export
     * @return data_configuration_Distr
     */
    public function setExport($export)
    {
        $this->_export = $export;
        return $this;
    }

    /**
     * Get Unit.
     * @return string
     */
    public function getUnit()
    {
        return $this->_unit;
    }

    /**
     * Get unit.
     * @param string $unit
     * @return data_configuration_Distr
     */
    public function setUnit($unit)
    {
        $this->_unit = $unit;
        return $this;
    }

    /**
     * Get sort of distribution.
     * @return integer
     */
    public function getSortType()
    {
        return $this->_sortType;
    }

    /**
     * Set distributing order.
     * @param integer $sortType
     * @return data_configuration_Distr
     */
    public function setSortType($sortType)
    {
        $this->_sortType = $sortType;
        return $this;
    }

    /**
     * Get expressions.
     * @return array
     */
    public function getExpressions()
    {
        return $this->_expressions;
    }

    /**
     * Set expressions.
     * @param array $expressions
     * @return data_configuration_Distr
     */
    public function setExpressions($expressions)
    {
        $this->_expressions = $expressions;
        return $this;
    }

    /**
     * Set extend value.
     * @param mixed $value
     * @return data_configuration_Distr
     */
    public function setExtend($value)
    {
        $this->_extend = $value;
        return $this;
    }

    /**
     * Get extend value.
     * @return mixed
     */
    public function getExtend()
    {
        return $this->_extend;
    }
}

<?php
abstract class data_source_SQLHelper extends TMComponent
{
    /**
     * @var string Type of table.
     */
    protected $tableType = null;

    /**
     * @var string Type of calculating.
     */
    protected $calcType = null;

    /**
     * @var string SELECT part.
     */
    protected $select = null;

    /**
     * @var string extra part, such as GROUP BY, ORDER.
     */
    protected $extra = null;

    /**
     * Set table type, 'day' or 'minute'.
     * @param string $tableType
     * @return data_source_SQLHelper
     */
    public function setTableType($tableType = 'day')
    {
        $this->tableType = $this->filterTableType($tableType);
        return $this;
    }

    /**
     * Set calculating type, 'avg' or 'sum'.
     * @param string $calcType
     * @return data_source_SQLHelper
     */
    public function setCalcType($calcType = 'avg')
    {
        $this->calcType = $this->filterCalcType($calcType);
        return $this;
    }

    /**
     * Filter the table type, default day.
     * @param string $tableType
     * @return string
     */
    public function filterTableType($tableType)
    {
        if ($tableType === 'minute') {
            return $tableType;
        }
        return 'day';
    }

    /**
     * Filter the calculating type, default avg.
     * @param string $calcType
     * @return string
     */
    public function filterCalcType($calcType)
    {
        if ($calcType === 'sum') {
            return $calcType;
        }
        return 'avg';
    }

    /**
     * Get SELECT part of the sql.
     * @return string
     */
    abstract public function getSelect();

    /**
     * Get extra part of the sql.
     * @param data_time_Period $period
     * @return string
     */
    abstract public function getExtra($period);
}

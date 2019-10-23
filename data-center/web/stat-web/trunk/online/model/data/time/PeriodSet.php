<?php
class data_time_PeriodSet extends TMComponent implements Iterator, ArrayAccess, Countable
{
    /**
     * @var data_time_Period[]
     */
    private $_periods = array();

    /**
     * @var data_time_Period Current one.
     */
    private $_period = null;

    /**
     * @var string the type of a table, can be day, minute
     */
    private $_tableType = null;

    /**
     * Add one period to the set.
     * @param data_time_Period $period
     * @return data_time_PeriodSet
     */
    public function add(data_time_Period $period)
    {
        if (isset($this->_tableType) && $this->_tableType !== $period->getTableType()) {
            throw new TMException(TM::t('tongji', '时间段集合对应表须一致！'));
        }
        $this->_tableType = $period->getTableType();
        $this->_periods[] = $period;
    }

    /**
     * Whether this set contains periods.
     * @return boolean
     */
    public function isEmpty()
    {
        return empty($this->_periods);
    }

    /* Methods of Iterator */

    public function current()
    {
        return current($this->_periods);
    }

    public function key()
    {
        return key($this->_periods);
    }

    public function next()
    {
        $this->_period = next($this->_periods);
    }

    public function rewind()
    {
        $this->_period = reset($this->_periods);
    }

    public function valid()
    {
        return $this->_period !== false;
    }

    /* Methods of ArrayAccess */

    public function offsetExists($offset)
    {
        return isset($this->_periods[$offset]);
    }

    public function offsetGet($offset)
    {
        return $this->offsetExists($offset) ? $this->_periods[$offset] : null;
    }

    public function offsetSet($offset, $value)
    {
        $this->add($value);
    }

    public function offsetUnset($offset)
    {
        if ($this->offsetExists($offset)) {
            unset($this->_periods[$offset]);
        }
    }

    /* Methods of Countable */

    public function count()
    {
        return count($this->_periods);
    }

    /* Methods of it owns only */

    public function getTableType()
    {
        return $this->_tableType;
    }
}

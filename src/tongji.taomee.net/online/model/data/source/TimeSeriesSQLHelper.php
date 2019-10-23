<?php
class data_source_TimeSeriesSQLHelper extends data_source_SQLHelper
{
    /**
     * Get SELECT part of the sql.
     * @return string
     */
    public function getSelect()
    {
        if (isset($this->select)) {
            return $this->select;
        }
        if ($this->tableType === 'minute') {
            $this->select = "MIN(time) AS _key, {$this->calcType}(value) AS _value";
            $this->extra = null;
        } else {
            $this->select = "time AS _key, value AS _value";
            $this->extra = '';
        }
        return $this->select;
    }

    /**
     * Get extra part of the sql.
     * @param data_time_Period $period
     * @return string
     */
    public function getExtra($period)
    {
        if (!isset($this->select)) {
            throw new TMException(TM::t('tongji', 'You must call getSelect() first!'));
        }
        if (isset($this->extra)) {
            return $this->extra;
        }
        if ($this->tableType === 'minute') {
            $this->extra = "GROUP BY FLOOR((time-{$period->getFrom()})/{$period->getInterval()})";
        } else {
            $this->extra = '';
        }
        return $this->extra;
    }
}

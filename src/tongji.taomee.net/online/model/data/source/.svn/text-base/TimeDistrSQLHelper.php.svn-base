<?php
class data_source_TimeDistrSQLHelper extends data_source_SQLHelper
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
        return $this->select = "CAST((time+28800)/60 AS SIGNED) MOD 1440 AS _key, {$this->calcType}(value) AS _value";
    }

    /**
     * Get extra part of the sql.
     * @param data_time_Period $period
     * @return string
     */
    public function getExtra($period)
    {
        if (isset($this->extra)) {
            return $this->extra;
        }
        return $this->extra = 'GROUP BY _key';
    }
}

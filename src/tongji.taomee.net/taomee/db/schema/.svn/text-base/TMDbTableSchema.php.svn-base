<?php
/**
 * TMDbTableSchema class file.
 */

/**
 * TMDbTableSchema is the base class for retrieving table information.
 *
 */
class TMDbTableSchema extends TMComponent
{
    /**
     * @var string The name of this table.
     */
    public $name;

    /**
     * @var string|array The primary key of this table.
     */
    public $primaryKey;

    /**
     * @var Array The column list of this table.
     */
    public $columns = array();

    public $sequenceName;

    public function getColumn($name)
    {
        return isset($this->columns[$name]) ? $this->columns[$name] : null;
    }

    public function getColumnNames()
    {
        return array_keys($this->columns);
    }
}

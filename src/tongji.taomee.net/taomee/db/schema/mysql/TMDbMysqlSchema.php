<?php
/**
 * TMDbMysqlSchema class file.
 */

/**
 * TMDbMysqlSchema is the class for retrieving metadata information from a MySQL database (version 4.1.x and 5.x).
 */
class TMDbMysqlSchema extends TMDbSchema
{
    /**
     * Creates a command builder for the database.
     * @return TMDbMysqlCommandBuilder command builder instance
     */
    protected function createCommandBuilder()
    {
        return new TMDbMysqlCommandBuilder($this);
    }

    /**
     * Quotes a database name for use in a query.
     * @param string $name database name
     * @return string the properly quoted database name
     */
    public function quoteSimpleDbName($name)
    {
        return '`' . $name . '`';
    }

    /**
     * Quotes a table name for use in a query.
     * A simple table name does not schema prefix.
     * @param string $name table name
     * @return string the properly quoted table name
     */
    public function quoteSimpleTableName($name)
    {
        return '`' . $name . '`';
    }

    /**
     * Quotes a column name for use in a query.
     * A simple column name does not contain prefix.
     * @param string $name column name
     * @return string the properly quoted column name
     */
    public function quoteSimpleColumnName($name)
    {
        return '`' . $name . '`';
    }

    /**
     * Loads the metadata for the specified table.
     * @param string $name table name
     * @return TMDbMysqlTableSchema driver dependent table metadata. Null if the table does not exist
     */
    protected function loadTable($name)
    {
        $table = new TMDbMysqlTableSchema();
        $table->name = $name;
        if ($this->findColumns($table)) {
            return $table;
        } else {
            return null;
        }
    }

    /** 
     * Collects the table column metadata.
     * @param CMysqlTableSchema $table the table metadata
     * @return boolean whether the table exists in the database
     */
    protected function findColumns($table)
    {   
        $sql = 'SHOW FULL COLUMNS FROM ' . $table->name;
        try {
            $columns = $this->getDbConnection()->createCommand($sql)->queryAll();
        } catch (Exception $e) {   
            return false;
        }
        foreach ($columns as $column) {
            $c = $this->createColumn($column);
            $table->columns[$c->name] = $c;
            if ($c->isPrimaryKey) {
                if ($table->primaryKey === null) {
                    $table->primaryKey = $c->name;
                } elseif (is_string($table->primaryKey)) {
                    $table->primaryKey = array($table->primaryKey, $c->name);
                } else {
                    $table->primaryKey[] = $c->name;
                }
                if ($c->autoIncrement) {
                    $table->sequenceName = '';
                }
            }
        }
        return true;
    }

    /**
     * Creates a table column.
     * @param array $column column metadata
     * @return TMDbMysqlColumnSchema normalized column metadata
     */
    protected function createColumn($column)
    {
        $c = new TMDbMysqlColumnSchema();
        $c->name = $column['Field'];
        $c->rawName = $this->quoteColumnName($c->name);
        $c->allowNull = $column['Null'] === 'YES';
        $c->isPrimaryKey = strpos($column['Key'], 'PRI') !== false;
        $c->initColumn($column['Type'], $column['Default']);
        $c->autoIncrement = strpos(strtolower($column['Extra']), 'auto_increment') !== false;
        return $c;
    }
}

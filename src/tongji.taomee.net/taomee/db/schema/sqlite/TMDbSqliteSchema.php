<?php
/**
 * TMDbMysqlSchema class file.
 */

/**
 * TMDbMysqlSchema is the class for retrieving metadata information from a MySQL database (version 4.1.x and 5.x).
 */
class TMDbSqliteSchema extends TMDbSchema
{
    /**
     * Creates a command builder for the database.
     * @return TMDbSqliteCommandBuilder command builder instance
     */
    protected function createCommandBuilder()
    {
        return new TMDbSqliteCommandBuilder($this);
    }

    /**
     * Loads the metadata for the specified table.
     * @param string $name table name
     * @return TMDbMysqlTableSchema driver dependent table metadata. Null if the table does not exist
     */
    protected function loadTable($name)
    {
        $table = new TMDbTableSchema();
        $table->name = $name;
        if ($this->findColumns($table)) {
            return $table;
        } else {
            return null;
        }
    }

    /** 
     * Collects the table column metadata.
     * @param TMDbTableSchema $table the table metadata
     * @return boolean whether the table exists in the database
     */
    protected function findColumns($table)
    {   
        $sql = 'PRAGMA table_info(' . $table->name . ')';
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
            }
        }
        if (is_string($table->primaryKey) && !strncasecmp($table->columns[$table->primaryKey]->dbType, 'int', 3)) {
            $table->sequenceName = '';
            $table->columns[$table->primaryKey]->autoIncrement = true;
        }
        return true;
    }

    /**
     * Creates a table column.
     * @param array $column column metadata
     * @return TMDbSqliteColumnSchema normalized column metadata
     */
    protected function createColumn($column)
    {
        $c = new TMDbSqliteColumnSchema();
        $c->name = $column['name'];
        $c->rawName = $this->quoteColumnName($c->name);
        $c->allowNull = $column['notnull'];
        $c->isPrimaryKey = $column['pk'] != 0;
        $c->initColumn(strtolower($column['type']), $column['dflt_value']);
        return $c;
    }
}

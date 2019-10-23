<?php
/**
 * TMDbMysqlCommandBuilder class file.
 */

/**
 * TMDbMysqlCommandBuilder provides mysql-special methods to create query commands.
 */
class TMDbMysqlCommandBuilder extends TMDbCommandBuilder
{
    /**
     * Build the SQL to apply USE.
     * @param string $dbName the database to use.
     * @return string SQL with USE.
     */
    public function applyUse($dbName)
    {
        return 'USE ' . $this->getSchema()->quoteSimpleDbName($dbName);
    }
}

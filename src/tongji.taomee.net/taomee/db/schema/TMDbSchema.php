<?php
/**
 * TMDbSchema class file.
 */

/**
 * TMDbSchema is the base class for retrieving metadata information.
 *
 * @property TMDbConnection $dbConnection Database connection. The connection is active.
 * @property TMDbCommandBuilder $commandBuilder The SQL command builder for this connection.
 */
abstract class TMDbSchema extends TMComponent
{
    /**
     * @var TMDbConnection Database connection. The connection is active.
     */
    private $_connection;

    /**
     * @var TMDbCommandBuilder The SQL command builder for this connection.
     */
    private $_builder;

    /**
     * @var Array The table list of this connection.
     */
    private $_tables;

    /**
     * Constructor.
     * @param TMDbConnection $conn database connection.
     */
    public function __construct($conn)
    {
        $this->_connection = $conn;
    }

    /**
     * @return TMDbConnection database connection. The connection is active.
     */
    public function getDbConnection()
    {
        return $this->_connection;
    }

    /**
     * @return TMDbCommandBuilder the SQL command builder for this connection.
     */
    public function getCommandBuilder()
    {
        if ($this->_builder !== null) {
            return $this->_builder;
        } else {
            return $this->_builder = $this->createCommandBuilder();
        }
    }

    /**
     * Creates a command builder for the database.
     * This method may be overridden by child classes to create a DBMS-specific command builder.
     * @return TMDbCommandBuilder command builder instance
     */
    protected function createCommandBuilder()
    {
        return new TMDbCommandBuilder($this);
    }

    /**
     * Quotes a table name for use in a query.
     * If the table name contains schema prefix, the prefix will also be properly quoted.
     * @param string $name table name
     * @return string the properly quoted table name
     * @see quoteSimpleTableName
     */
    public function quoteTableName($name)
    {
        if (strpos($name, '.') === false)
            return $this->quoteSimpleTableName($name);
        $parts = explode('.', $name);
        foreach ($parts as $i => $part)
            $parts[$i] = $this->quoteSimpleTableName($part);
        return implode('.', $parts);

    }

    /**
     * Quotes a simple table name for use in a query.
     * A simple table name does not schema prefix.
     * @param string $name table name
     * @return string the properly quoted table name
     */
    public function quoteSimpleTableName($name)
    {
        return "'" . $name . "'";
    }

    /**
     * Quotes a column name for use in a query.
     * If the column name contains prefix, the prefix will also be properly quoted.
     * @param string $name column name
     * @return string the properly quoted column name
     * @see quoteSimpleColumnName
     */
    public function quoteColumnName($name)
    {
        if (($pos = strrpos($name, '.')) !== false) {
            $prefix = $this->quoteTableName(substr($name, 0, $pos)) . '.';
            $name = substr($name, $pos + 1);
        } else
            $prefix = '';
        return $prefix . ($name === '*' ? $name : $this->quoteSimpleColumnName($name));
    }

    /**
     * Quotes a simple column name for use in a query.
     * A simple column name does not contain prefix.
     * @param string $name column name
     * @return string the properly quoted column name
     */
    public function quoteSimpleColumnName($name)
    {
        return '"' . $name . '"';
    }

    /**
     * Loads the metadata for the specified table.
     * @param string $name table name
     * @return TMTableSchema driver dependent table metadata. Null if the table does not exist
     */
    public function getTable($name)
    {
        if (isset($this->_tables[$name])) {
            return $this->_tables[$name];
        }
        return $this->_tables[$name] = $this->loadTable($name); 
    }

    /**
     * Loads the metadata for the specified table.
     * @param string $name table name
     * @return TMTableSchema driver dependent table metadata. Null if the table does not exist
     */
    abstract protected function loadTable($name);
}

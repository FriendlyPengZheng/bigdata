<?php
/**
 * TMDbConnection represents a connection to a database.
 */
class TMDbConnection extends TMComponent
{
    /**
     * @var string the path which the classes relative to.
     */
    private static $_relativePath = 'system.db.';

    /**
     * @var array the classes that this component needs.
     */
    private static $_dependentClasses = array(
        'required' => array(
            'TMDbConnection'
        ),
        'optional' => array(
            'TMDbException',
            'TMDbCommand',
            'TMDbTransaction',
            'TMDbDataReader',
            'schema.TMDbCommandBuilder',
            'schema.TMDbSchema',
            'schema.TMDbTableSchema',
            'schema.TMDbColumnSchema',
            'schema.mysql.TMDbMysqlCommandBuilder',
            'schema.mysql.TMDbMysqlSchema',
            'schema.mysql.TMDbMysqlTableSchema',
            'schema.mysql.TMDbMysqlColumnSchema',
            'schema.sqlite.TMDbMysqlCommandBuilder',
            'schema.sqlite.TMDbMysqlSchema',
            'schema.sqlite.TMDbMysqlColumnSchema'
        )
    );

    /**
     * @var string The Data Source Name, or DSN, contains the information required to connect to the database.
     */
    public $connectionString;

    /**
     * @var string the username for establishing DB connection. Defaults to empty string.
     */
    public $username = '';

    /**
     * @var string the password for establishing DB connection. Defaults to empty string.
     */
    public $password = '';

    /**
     * @var boolean whether the database connection should be automatically established.
     * This property is only effective when we use TMDbConnection as components.
     */
    public $autoConnect = true;

    /**
     * @var string the charset used for database connection. The property is only used
     * for MySQL and PostgreSQL databases. Defaults to null, meaning using default charset
     * as specified by the database.
     */
    public $charset;

    /**
     * @var boolean whether to turn on prepare emulation. Defaults to false, meaning PDO
     * will use the native prepare support if available. For some databases (such as MySQL),
     * this may need to be set true so that PDO can emulate the prepare support to bypass
     * the buggy native prepare support. Note, this property is only effective for PHP 5.1.3 or above.
     * The default value is null, which will not change the ATTR_EMULATE_PREPARES value of PDO.
     */
    public $emulatePrepare;

    /**
     * @var string the default prefix for table names. Defaults to null, meaning no table prefix.
     * By setting this property, any token like '{{tableName}}' in {@link TMDbCommand::text} will
     * be replaced by 'prefixTableName', where 'prefix' refers to this property value.
     */
    public $tablePrefix;

    /**
     * @var array list of SQL statements that should be executed right after the DB connection is established.
     */
    public $initSQLs;

    /**
     * @var array mapping between PDO driver and schema class name.
     * A schema class can be specified using path alias.
     */
    public $driverMap = array(
        // 'pgsql'   => 'TMDbPgsqlSchema',  // PostgreSQL
        // 'mysqli'  => 'TMDbMysqlSchema',  // MySQL
        'sqlite'  => 'TMDbSqliteSchema', // sqlite 3
        // 'sqlite2' => 'TMDbSqliteSchema', // sqlite 2
        // 'mssql'   => 'TMDbMssqlSchema',  // Mssql driver on windows hosts
        // 'dblib'   => 'TMDbMssqlSchema',  // dblib drivers on linux (and maybe others os) hosts
        // 'sqlsrv'  => 'TMDbMssqlSchema',  // Mssql
        // 'oci'     => 'TMDbOciSchema',    // Oracle driver
        'mysql'   => 'TMDbMysqlSchema'   // MySQL
    );

    /**
     * @var string Custom PDO wrapper class.
     */
    public $pdoClass = 'PDO';

    /**
     * @var string Custom model class.
     */
    public $modelClass;

    /**
     * @var string Custom Logger class.
     */
    public $logger;

    /**
     * @var array Attributes (name=>value) that are previously explicitly set for the DB connection.
     */
    private $_attributes = array();

    /**
     * @var boolean Whether the DB connection is established.
     */
    private $_active = false;

    /**
     * @var PDO The PDO instance, null if the connection is not established yet.
     */
    private $_pdo;

    /**
     * @var TMDbTransaction The currently active transaction. Null if no active transaction.
     */
    private $_transaction;

    /**
     * @var TMDbSchema The database schema for the current connection.
     */
    private $_schema;

    /**
     * Note, the DB connection is not established when this connection
     * instance is created. Set {@link setActive active} property to true
     * to establish the connection.
     *
     * @param string $dsn      The Data Source Name, or DSN, contains the information required to connect to the database.
     * @param string $username The user name for the DSN string.
     * @param string $password The password for the DSN string.
     */
    public function __construct($dsn = '', $username = '', $password = '')
    {
        $this->connectionString = $dsn;
        $this->username = $username;
        $this->password = $password;
    }

    /**
     * Close the connection when serializing.
     *
     * @return array List of properties to be serialized.
     */
    public function __sleep()
    {
        $this->close();
        return array_keys(get_object_vars($this));
    }

    /**
     * Init this component.
     *
     * @return null
     */
    public function init()
    {
        parent::init();
        foreach (self::$_dependentClasses as $key => $classes) {
            $autoload = ($key === 'required') ? true : false;
            foreach ($classes as $class) {
                TM::import(self::$_relativePath . $class, $autoload);
            }
        }
        if ($this->autoConnect) {
            $this->setActive(true);
        }
    }

    /**
     * Returns a list of available PDO drivers.
     *
     * @return array List of available PDO drivers.
     */
    public static function getAvailableDrivers()
    {
        return PDO::getAvailableDrivers();
    }

    /**
     * Returns whether the DB connection is established.
     *
     * @return bool Whether the DB connection is established.
     */
    public function getActive()
    {
        return $this->_active;
    }

    /**
     * Open or close the DB connection.
     *
     * @param  bool          $value Whether to open or close DB connection
     * @return null
     * @throws TMDbException        If connection fails
     */
    public function setActive($value)
    {
        if ($value != $this->_active) {
            if ($value) {
                $this->open();
            } else {
                $this->close();
            }
        }
    }

    /**
     * Opens DB connection if it is currently not
     *
     * @return null
     * @throws TMDbException If connection fails
     */
    protected function open()
    {
        if ($this->_pdo !== null) {
            return;
        }

        if (empty($this->connectionString)) {
            throw new TMDbException('TMDbConnection.connectionString cannot be empty.');
        }

        try {
            $this->_pdo = $this->createPdoInstance();
            $this->initConnection($this->_pdo);
            $this->_active = true;
        } catch(PDOException $e) {
            throw new TMDbException(
                'TMDbConnection failed to open the DB connection: ' . $e->getMessage(),
                $e->getCode(),
                $e->errorInfo
            );
        }
    }

    /**
     * Closes the currently active DB connection.
     * It does nothing if the connection is already closed.
     *
     * @return null
     */
    protected function close()
    {
        $this->_pdo = null;
        $this->_active = false;
        $this->_schema = null;
    }

    /**
     * Creates the PDO instance.
     * When some functionalities are missing in the pdo driver, we may use
     * an adapter class to provide them.
     *
     * @return PDO           PDO instance.
     * @throws TMDbException When failed to open DB connection
     */
    protected function createPdoInstance()
    {
        $pdoClass = $this->pdoClass;

        // Useless for mysql driver
        // $driver = $this->getDriverName();
        // if ($driver === 'mssql' || $driver === 'dblib') {
        //     $pdoClass = 'TMMssqlPdoAdapter';
        // } elseif ($driver === 'sqlsrv') {
        //     $pdoClass = 'TMMssqlSqlsrvPdoAdapter';
        // }

        if (!class_exists($pdoClass)) {
            throw new TMDbException(sprintf(
                'TMDbConnection is unable to find PDO class "%s". Make sure PDO is installed correctly.',
                $pdoClass
            ));
        }

        try {
            return new $pdoClass($this->connectionString, $this->username, $this->password, $this->_attributes);
        } catch (Exception $e) {
            throw new TMDbException(
                'TMDbConnection failed to open the DB connection: ' . $e->getMessage(),
                $e->getCode(),
                ($e instanceof PDOException) ? $e->errorInfo : array()
            );
        }
    }

    /**
     * Initializes the open db connection.
     * This method is invoked right after the db connection is established.
     * The default implementation is to set the charset for MySQL and PostgreSQL database connections.
     *
     * @param  PDO  $pdo The PDO instance
     * @return null
     */
    protected function initConnection($pdo)
    {
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        if ($this->emulatePrepare !== null && constant('PDO::ATTR_EMULATE_PREPARES')) {
            $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, $this->emulatePrepare);
        }

        if ($this->charset !== null) {
            $driver = strtolower($pdo->getAttribute(PDO::ATTR_DRIVER_NAME));
            if (in_array($driver, array('pgsql', 'mysql', 'mysqli'))) {
                $pdo->exec('SET NAMES ' . $pdo->quote($this->charset));
            }
        }

        if ($this->initSQLs !== null) {
            foreach($this->initSQLs as $sql) {
                $pdo->exec($sql);
            }
        }
    }

    /**
     * Returns the PDO instance, null if the connection is not established yet
     *
     * @return PDO The PDO instance.
     */
    public function getPdoInstance()
    {
        return $this->_pdo;
    }

    /**
     * Creates a command for execution.
     *
     * @param  mixed       $query The DB query to be executed.
     *                            This can be either a string representing a SQL statement,
     *                            or an array representing different fragments of a SQL statement. Please refer to {@link TMDbCommand::__construct}
     *                            for more details about how to pass an array as the query. If this parameter is not given,
     *                            you will have to call query builder methods of {@link TMDbCommand} to build the DB query.
     * @return TMDbCommand        The command for execution.
     */
    public function createCommand($query = null)
    {
        $this->setActive(true);
        return new TMDbCommand($this, $query);
    }

    /**
     * Returns the currently active transaction. Null if no active transaction.
     *
     * @return TMDbTransaction The currently active transaction.
     */
    public function getCurrentTransaction()
    {
        if ($this->_transaction !== null) {
            if ($this->_transaction->getActive()) {
                return $this->_transaction;
            }
        }
    }

    /**
     * Starts a transaction.
     *
     * @return TMDbTransaction The transaction.
     */
    public function beginTransaction()
    {
        $this->setActive(true);
        $this->_pdo->beginTransaction();
        return $this->_transaction = new TMDbTransaction($this);
    }

    /**
     * Returns the database schema for the current connection
     *
     * @return TMDbSchema
     * @throws TMDbException If TMDbConnection does not support reading schema for specified database driver
     */
    public function getSchema()
    {
        if ($this->_schema !== null) {
            return $this->_schema;
        }

        $driver = $this->getDriverName();
        if (isset($this->driverMap[$driver])) {
            return $this->_schema = new $this->driverMap[$driver]($this);
        }

        throw new TMDbException(sprintf('TMDbConnection does not support reading schema for %s database.', $driver));
    }

    /**
     * Returns the SQL command builder for the current DB connection.
     *
     * @return TMDbCommandBuilder
     */
    public function getCommandBuilder()
    {
        return $this->getSchema()->getCommandBuilder();
    }

    /**
     * Returns the ID of the last inserted row or sequence value.
     * It must be called before commit if you are using mysql transaction.
     *
     * @param  string $sequenceName Name of the sequence object (required by some DBMS)
     * @return int                  The id of the last inserted row or sequence value.
     */
    public function getLastInsertID($sequenceName = '')
    {
        $this->setActive(true);
        return $this->_pdo->lastInsertId($sequenceName);
    }

    /**
     * Quotes a string value for use in a query.
     *
     * @param  mixed $str Value to be quoted
     * @return mixed      Quoted value
     */
    public function quoteValue($str)
    {
        if (is_int($str) || is_float($str)) {
            return $str;
        }

        $this->setActive(true);
        if (($value = $this->_pdo->quote($str)) !== false) {
            return $value;
        } else {
            // The driver doesn't support quote, \000 is NUL and \032 is SUB(which means Ctrl+Z)
            return "'" . addcslashes(str_replace("'", "''", $str), "\000\n\r\\\032") . "'";
        }
    }

    /**
     * Quotes a table name for use in a query.
     * If the table name contains schema prefix, the prefix will also be properly quoted.
     *
     * @param  string $name Table name
     * @return string       Quoted table name
     */
    public function quoteTableName($name)
    {
        return $this->getSchema()->quoteTableName($name);
    }

    /**
     * Quotes a column name for use in a query.
     * If the column name contains prefix, the prefix will also be properly quoted.
     *
     * @param  string $name Column name
     * @return string       Quoted column name
     */
    public function quoteColumnName($name)
    {
        return $this->getSchema()->quoteColumnName($name);
    }

    /**
     * Determines the PDO type for the specified PHP type.
     *
     * @param  string $type The PHP type (obtained by gettype() call).
     * @return int
     */
    public function getPdoType($type)
    {
        static $map = array(
            'boolean'  => PDO::PARAM_BOOL,
            'integer'  => PDO::PARAM_INT,
            'string'   => PDO::PARAM_STR,
            'resource' => PDO::PARAM_LOB,
            'NULL'     => PDO::PARAM_NULL
        );
        return isset($map[$type]) ? $map[$type] : PDO::PARAM_STR;
    }

    /**
     * Returns the case of the column names
     * Options:
     * PDO::CASE_LOWER   Force column names to lower case.
     * PDO::CASE_NATURAL Leave column names as returned by the database driver.
     * PDO::CASE_UPPER   Force column names to upper case.
     *
     * @return int
     */
    public function getColumnCase()
    {
        return $this->getAttribute(PDO::ATTR_CASE);
    }

    /**
     * Sets the case of the column names.
     * Options:
     * PDO::CASE_LOWER   Force column names to lower case.
     * PDO::CASE_NATURAL Leave column names as returned by the database driver.
     * PDO::CASE_UPPER   Force column names to upper case.
     *
     * @param  int  $value The case of the column names
     * @return null
     */
    public function setColumnCase($value)
    {
        $this->setAttribute(PDO::ATTR_CASE, $value);
    }

    /**
     * Returns how the null and empty strings are converted.
     * Options:
     * PDO::NULL_NATURAL      No conversion.
     * PDO::NULL_EMPTY_STRING Empty string is converted to NULL.
     * PDO::NULL_TO_STRING    NULL is converted to an empty string.
     *
     * @return int
     */
    public function getNullConversion()
    {
        return $this->getAttribute(PDO::ATTR_ORACLE_NULLS);
    }

    /**
     * Sets how the null and empty strings are converted.
     * Options:
     * PDO::NULL_NATURAL      No conversion.
     * PDO::NULL_EMPTY_STRING Empty string is converted to NULL.
     * PDO::NULL_TO_STRING    NULL is converted to an empty string.
     *
     * @param  int  $value How the null and empty strings are converted
     * @return null
     */
    public function setNullConversion($value)
    {
        $this->setAttribute(PDO::ATTR_ORACLE_NULLS, $value);
    }

    /**
     * Returns whether creating or updating a DB record will be automatically committed.
     * Some DBMS (such as sqlite) may not support this feature.
     *
     * @return int
     */
    public function getAutoCommit()
    {
        return $this->getAttribute(PDO::ATTR_AUTOCOMMIT);
    }

    /**
     * Sets whether creating or updating a DB record will be automatically committed.
     * Some DBMS (such as sqlite) may not support this feature.
     *
     * @param  bool $value Whether creating or updating a DB record will be automatically committed.
     * @return null
     */
    public function setAutoCommit($value)
    {
        $this->setAttribute(PDO::ATTR_AUTOCOMMIT, $value);
    }

    /**
     * Returns whether the connection is persistent or not.
     * Some DBMS (such as sqlite) may not support this feature.
     *
     * @return bool
     */
    public function getPersistent()
    {
        return $this->getAttribute(PDO::ATTR_PERSISTENT);
    }

    /**
     * Sets whether the connection is persistent or not.
     * Some DBMS (such as sqlite) may not support this feature.
     *
     * @param  bool $value Whether the connection is persistent or not
     * @return null
     */
    public function setPersistent($value)
    {
        $this->setAttribute(PDO::ATTR_PERSISTENT, $value);
    }

    /**
     * Returns the name of the DB driver
     *
     * @return string|null Such as "mysql"
     */
    public function getDriverName()
    {
        if (($pos = strpos($this->connectionString, ':')) !== false) {
            return strtolower(substr($this->connectionString, 0, $pos));
        }
    }

    /**
     * Obtains a specific DB connection attribute information.
     *
     * @param int $name The attribute to be queried
     */
    public function getAttribute($name)
    {
        $this->setActive(true);
        return $this->_pdo->getAttribute($name);
    }

    /**
     * Sets an attribute on the database connection.
     *
     * @param int   $name  The attribute to be set
     * @param mixed $value The attribute value
     */
    public function setAttribute($name, $value)
    {
        if($this->_pdo instanceof PDO) {
            $this->_pdo->setAttribute($name, $value);
        } else {
            $this->_attributes[$name] = $value;
        }
    }

    /**
     * Returns the attributes that are previously explicitly set for the DB connection.
     *
     * @return array
     */
    public function getAttributes()
    {
        return $this->_attributes;
    }

    /**
     * Sets a set of attributes on the database connection.
     *
     * @param array $values Attributes (name=>value) to be set.
     */
    public function setAttributes($values)
    {
        foreach($values as $name => $value) {
            $this->_attributes[$name] = $value;
        }
    }

    /**
     * Switch current database.
     *
     * @param  string         $dbName The database to switch to.
     * @return TMDbConnection         For chaining call.
     * @throw  PDOException           If the database doesn't exist.
     */
    public function switchDb($dbName)
    {
        $this->setActive(true);
        $this->_pdo->exec($this->getCommandBuilder()->applyUse($dbName));
        return $this;
    }
}

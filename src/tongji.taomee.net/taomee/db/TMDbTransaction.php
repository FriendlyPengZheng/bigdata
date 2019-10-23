<?php
/**
 * TMDbTransaction class file
 */

/**
 * TMDbTransaction represents a DB transaction.
 *
 * It is usually created by calling {@link TMDbConnection::beginTransaction}.
 *
 * The following code is a common scenario of using transactions:
 * <pre>
 * $transaction=$connection->beginTransaction();
 * try
 * {
 *    $connection->createCommand($sql1)->execute();
 *    $connection->createCommand($sql2)->execute();
 *    //.... other SQL executions
 *    $transaction->commit();
 * }
 * catch(Exception $e)
 * {
 *    $transaction->rollback();
 * }
 * </pre>
 *
 * @property TMDbConnection $connection The DB connection for this transaction.
 * @property boolean $active Whether this transaction is active.
 */
class TMDbTransaction extends TMComponent
{
    /**
     * @var TMDbConnection The DB connection for this transaction.
     */
    private $_connection = null;

    /**
     * @var boolean Whether this transaction is active.
     */
    private $_active;

    /**
     * Constructor.
     * @param TMDbConnection $connection the connection associated with this transaction
     * @see TMDbConnection::beginTransaction
     */
    public function __construct(TMDbConnection $connection)
    {
        $this->_connection = $connection;
        $this->_active = true;
    }

    /**
     * Commits a transaction.
     * @throws TMDbException if the transaction or the DB connection is not active.
     */
    public function commit()
    {
        if ($this->_active && $this->_connection->getActive()) {
            $this->_connection->getPdoInstance()->commit();
            $this->_active = false;
        } else {
            throw new TMDbException('TMDbTransaction is inactive and cannot perform commit or roll back operations.');
        }
    }

    /**
     * Rolls back a transaction.
     * @throws TMDbException if the transaction or the DB connection is not active.
     */
    public function rollback()
    {
        if ($this->_active && $this->_connection->getActive()) {
            $this->_connection->getPdoInstance()->rollBack();
            $this->_active = false;
        } else {
            throw new TMDbException('TMDbTransaction is inactive and cannot perform commit or roll back operations.');
        }
    }

    /**
     * @return TMDbConnection the DB connection for this transaction
     */
    public function getConnection()
    {
        return $this->_connection;
    }

    /**
     * @return boolean whether this transaction is active
     */
    public function getActive()
    {
        return $this->_active;
    }

    /**
     * @param boolean $value whether this transaction is active
     */
    protected function setActive($value)
    {
        $this->_active = $value;
    }
}

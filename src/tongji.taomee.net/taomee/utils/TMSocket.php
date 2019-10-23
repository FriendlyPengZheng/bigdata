<?php
/*
 * TMSocket class.
 */
class TMSocket extends TMComponent
{
    /**
     * Socket errno.
     */
    const EAGAIN = 11;

    /**
     * @var string The IP address.
     */
    public $address;

    /**
     * @var integer The port.
     */
    public $port;

    /**
     * @var integer The time-out in seconds, default 10.
     */
    public $timeout = 10;

    /**
     * @var integer The property specifies the protocol family to be used by the socket.
     */
    public $domain = AF_INET;

    /**
     * @var integer The property selects the type of communication to be used by the socket.
     */
    public $communicationType = SOCK_STREAM;

    /**
     * @var integer
     * The property sets the specific protocol within the specified domain to be used
     * when communicating on the returned socket.
     */
    public $protocol = SOL_TCP;

    /**
     * @var resource The socket resource.
     */
    private $_socket;

    /**
     * Init TMSocket component.
     */
    public function init()
    {
        if (!isset($this->address)) {
            throw new TMSocketException(TM::t('taomee', '须指定Socket地址！'));
        }
        if (!isset($this->port)) {
            throw new TMSocketException(TM::t('taomee', '须指定Socket端口！'));
        }
    }

    /**
     * Write the current socket.
     * @param  string  $buffer
     * @param  integer $length
     * @return integer
     */
    public function write($buffer, $length = null)
    {
        if (!isset($length)) $length = strlen($buffer);

        $sent = 0;
        while ($sent < $length) {
            // always blocking
            $len = socket_write($this->_socket, substr($buffer, $sent), $length);
            if ($len === false) {
                $errno = socket_last_error();
                if ($errno === self::EAGAIN) {
                    throw new TMSocketTimeoutException(
                        TM::t('taomee', 'socket写入超时：{err}', array('{err}' => socket_strerror($errno)))
                    );
                } else {
                    throw new TMSocketException(
                        TM::t('taomee', 'socket写入错误：{err}', array('{err}' => socket_strerror($errno)))
                    );
                }
            }
            $sent += $len;
        }

        return $length;
    }

    /**
     * Read the current socket.
     * @param  integer $length
     * @param  integer $type
     * @return string
     */
    public function read($length, $type = PHP_BINARY_READ)
    {
        $str = '';
        $read = 0;
        while ($read < $length) {
            // always blocking
            $bytes = socket_read($this->_socket, $length, $type);
            if ($bytes === false) {
                $errno = socket_last_error();
                if ($errno === self::EAGAIN) {
                    throw new TMSocketTimeoutException(
                        TM::t('taomee', 'socket读取超时：{err}', array('{err}' => socket_strerror($errno)))
                    );
                } else {
                    throw new TMSocketException(
                        TM::t('taomee', 'socket读取错误：{err}', array('{err}' => socket_strerror($errno)))
                    );
                }
            }
            if ($bytes === '') {
                throw new TMSocketException(
                    TM::t('taomee', 'socket读取错误：{err}', array('{err}' => 'The peer has performed an orderly shutdown.'))
                );
            }
            $read += strlen($bytes);
            $str .= $bytes;
        }

        return $str;
    }

    /**
     * Close the current socket.
     */
    public function close()
    {
        if (is_resource($this->_socket)) {
            socket_close($this->_socket);
        }
        $this->_socket = null;
    }

    /**
     * Connect.
     */
    public function connect()
    {
        $this->_socket = socket_create($this->domain, $this->communicationType, $this->protocol);
        if ($this->_socket === false) {
            throw new TMSocketException(
                TM::t('taomee', '创建socket错误：{err}', array('{err}' => socket_strerror(socket_last_error())))
            );
        }

        $flag = socket_set_option($this->_socket, SOL_SOCKET, SO_RCVTIMEO, array('sec' => $this->timeout, 'usec' => 0));
        if ($flag === false) {
            throw new TMSocketException(
                TM::t('taomee', '设置接收超时时间错误：{err}', array('{err}' => socket_strerror(socket_last_error())))
            );
        }
        $flag = socket_set_option($this->_socket, SOL_SOCKET, SO_SNDTIMEO, array('sec' => $this->timeout, 'usec' => 0));
        if ($flag === false) {
            throw new TMSocketException(
                TM::t('taomee', '设置发送超时时间错误：{err}', array('{err}' => socket_strerror(socket_last_error())))
            );
        }

        $flag = @socket_connect($this->_socket, $this->address, $this->port);
        if ($flag === false) {
            throw new TMSocketException(
                TM::t('taomee', 'socket连接错误：{err}', array('{err}' => socket_strerror(socket_last_error())))
            );
        }

        return $this;
    }

    /**
     * Destructor.
     */
    public function __destruct()
    {
        $this->close();
    }
}

/*
 * TMSocketException class.
 */
class TMSocketException extends TMException
{
}

/*
 * TMSocketTimeoutException class.
 */
class TMSocketTimeoutException extends TMSocketException
{
}

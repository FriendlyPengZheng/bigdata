<?php
/**
 * Implements writing primitives for binary streams
 */
class TMProtoCodecBinaryWriter
{
    /**
     * @var resource
     */
    protected $fd;

    /**
     * Stream wrapper
     */
    public function __construct()
    {
        $this->fd = fopen('php://memory', 'wb');
    }

    public function __destruct()
    {
        is_resource($this->fd) && fclose($this->fd);
    }

    /**
     * Get the current bytes in the stream
     *
     * @return string
     */
    public function getBytes()
    {
        fseek($this->fd, 0, SEEK_SET);
        return stream_get_contents($this->fd);
    }

    /**
     * Store the given bytes in the stream
     *
     * @param  string           $bytes
     * @param  int              $length
     * @throws TMProtoException
     */
    public function write($bytes, $length = null)
    {
        if ($length === null) {
            $length = strlen($bytes);
        }

        $written = fwrite($this->fd, $bytes, $length);
        if ($written === false) {
            throw new TMProtoException(TM::t('taomee', '写入错误！'));
        }
        if ($written !== $length) {
            throw new TMProtoException(TM::t('taomee', '写入不足{length}！', array('{length}' => $length)));
        }
    }

    /**
     * Store a string

     * @param string $value
     * @param int    $length
     */
    public function string($value, $length = null)
    {
        if (null === $length) {
            $format = 'a*';
        } else {
            $format = 'a' . $length;
        }
        $this->write(pack($format, $value));
    }

    /**
     * Store an int8
     *
     * @param int $value
     */
    public function int8($value)
    {
        $this->write(pack('c', $value), 1);
    }

    /**
     * Store an unsigned int8
     *
     * @param int $value
     */
    public function uint8($value)
    {
        $this->write(pack('C', $value), 1);
    }

    /**
     * Store an int16
     *
     * @param int $value
     */
    public function int16($value)
    {
        $bytes = pack('s', $value);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        $this->write($bytes, 2);
    }

    /**
     * Store an unsigned int16
     *
     * @param int $value
     */
    public function uint16($value)
    {
        $bytes = pack('S', $value);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        $this->write($bytes, 2);
    }

    /**
     * Store an int32
     *
     * @param int $value
     */
    public function int32($value)
    {
        $bytes = pack('l', $value);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        $this->write($bytes, 4);
    }

    /**
     * Store an unsigned int32
     *
     * @param int $value
     */
    public function uint32($value)
    {
        $bytes = pack('L', $value);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        $this->write($bytes, 4);
    }

    /**
     * Store an int64
     *
     * @param int $value
     */
    public function int64($value)
    {
        $hi = $value >> 32;
        $lo = $value & 0xFFFFFFFF;
        if (TMProto::getTransportEndian() === TMProto::ENDIAN_BIG) {
            $bytes = pack('N', $hi) . pack('N', $lo);
        } else {
            $bytes = pack('V', $lo) . pack('V', $hi);
        }

        $this->write($bytes, 8);
    }

    /**
     * Store an unsigned int64
     *
     * @param int $value
     */
    public function uint64($value)
    {
        $this->int64($value);
    }

    /**
     * Store a 32bit float
     *
     * @param float $value
     */
    public function float($value)
    {
        $bytes = pack('f', $value);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        $this->write($bytes, 4);
    }

    /**
     * Store a 64bit double
     *
     * @param double $value
     */
    public function double($value)
    {
        $bytes = pack('d', $value);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        $this->write($bytes, 8);
    }
}

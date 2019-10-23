<?php
/**
 * Implements reading primitives for binary streams
 */
class TMProtoCodecBinaryReader
{
    /**
     * @var resource
     */
    protected $fd;

    /**
     * Create a new reader from a file descriptor or a string of bytes
     *
     * @param resource|string $fdOrString
     */
    public function __construct($fdOrString = null)
    {
        if (null !== $fdOrString) {
            $this->init($fdOrString);
        }
    }

    public function __destruct()
    {
        is_resource($this->fd) && fclose($this->fd);
    }

    /**
     * Create a new reader from a file descriptor or a string of bytes
     *
     * @param resource|string $fdOrString
     */
    public function init($fdOrString)
    {
        if (is_resource($fdOrString)) {
            $this->fd = $fdOrString;
        } else {
            /**
             * Could this be faster by using a custom String wrapper?
             * Yes, it is. But need more memory.
             */
            $this->fd = fopen('data://text/plain,' . urlencode($fdOrString), 'rb');
        }
    }


    /**
     * Obtain a number of bytes from the string
     *
     * @param  int              $length If null, reads to the end.
     * @return string
     * @throws TMProtoException
     */
    public function read($length = null)
    {
        if (null === $length) {
            $bytes = '';
            while (!feof($this->fd)) {
                $bytes .= fread($this->fd, 4096);
            }
            return $bytes;
        }

        // Protect against 0 byte reads when an EOF
        if ($length < 1) return '';

        $bytes = fread($this->fd, $length);
        if (false === $bytes) {
            throw new TMProtoException(TM::t('taomee', '读取错误！'));
        }

        $read = strlen($bytes);
        if ($read < $length) {
            throw new TMProtoException(
                TM::t('taomee', '读取{read}，需要{length}！', array('{read}' => $read, '{length}' => $length)));
        }

        return $bytes;
    }

    /**
     * Check if we have reached the end of the stream
     *
     * @return bool
     */
    public function eof()
    {
        return feof($this->fd);
    }

    /**
     * Obtain the current position in the stream
     *
     * @return int
     */
    public function pos()
    {
        return ftell($this->fd);
    }

    /**
     * Decode a string

     * @param int $length
     */
    public function string($length = null)
    {
        if (null === $length) {
            $format = 'a*';
        } else {
            $format = 'a' . $length;
        }
        list(, $result) = unpack($format, $this->read($length));

        return $result;
    }

    /**
     * Decode an int8
     *
     * @return int
     */
    public function int8()
    {
        list(, $result) = unpack('c', $this->read(1));

        return $result;
    }

    /**
     * Decode an unsigned int8
     *
     * @return int
     */
    public function uint8()
    {
        list(, $result) = unpack('C', $this->read(1));

        return $result;
    }

    /**
     * Decode an int16
     *
     * @return int
     */
    public function int16()
    {
        $bytes = $this->read(2);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        list(, $result) = unpack('s', $bytes);
        return $result;
    }

    /**
     * Decode an unsigned int16
     *
     * @return int
     */
    public function uint16()
    {
        $bytes = $this->read(2);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        list(, $result) = unpack('S', $bytes);
        return $result;
    }

    /**
     * Decode an int32
     *
     * @return int
     */
    public function int32()
    {
        $bytes = $this->read(4);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        list(, $result) = unpack('l', $bytes);
        return $result;
    }

    /**
     * Decode an unsigned int32
     *
     * @return int
     */
    public function uint32()
    {
        $bytes = $this->read(4);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        list(, $result) = unpack('L', $bytes);
        return $result;
    }

    /**
     * Decode an int64
     *
     * @return int
     */
    public function int64()
    {
        $bytes = $this->read(8);
        if (TMProto::getTransportEndian() === TMProto::ENDIAN_BIG) {
            list(, $hi, $lo) = unpack('N*', $bytes);
        } else {
            list(, $lo, $hi) = unpack('V*', $bytes);
        }

        return $hi << 32 | $lo;
    }

    /**
     * Decode an unsigned int64
     *
     * @return int
     */
    public function uint64()
    {
        return $this->int64();
    }

    /**
     * Decode a 32bit float
     *
     * @return float
     */
    public function float()
    {
        $bytes = $this->read(4);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        list(, $result) = unpack('f', $bytes);
        return $result;
    }

    /**
     * Decode a 64bit double
     *
     * @return float
     */
    public function double()
    {
        $bytes = $this->read(8);
        if (TMProto::getMachineEndian() !== TMProto::getTransportEndian()) {
            $bytes = strrev($bytes);
        }

        list(, $result) = unpack('d', $bytes);
        return $result;
    }
}

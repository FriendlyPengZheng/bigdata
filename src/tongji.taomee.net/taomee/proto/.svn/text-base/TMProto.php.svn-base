<?php
class TMProto
{
    const RULE_REQUIRED = 1;
    const RULE_REPEATED = 2;

    const TYPE_STRING  = 1;
    const TYPE_INT8    = 2;
    const TYPE_UINT8   = 3;
    const TYPE_INT16   = 4;
    const TYPE_UINT16  = 5;
    const TYPE_INT32   = 6;
    const TYPE_UINT32  = 7;
    const TYPE_INT64   = 8;
    const TYPE_UINT64  = 9;
    const TYPE_FLOAT   = 10;
    const TYPE_DOUBLE  = 11;
    const TYPE_MESSAGE = 12;

    const ENDIAN_LITTLE = 1;
    const ENDIAN_BIG    = 2;

    /**
     * @var int
     */
    protected static $transportEndian = self::ENDIAN_LITTLE;

    /**
     * @var array
     */
    protected static $codecs = array();

    /**
     * Set transport endian
     *
     * @param  int $endian
     * @return
     */
    public static function setTransportEndian($endian)
    {
        if ($endian !== self::ENDIAN_LITTLE && $endian !== self::ENDIAN_BIG) {
            throw new TMProtoException(TM::t('taomee', '未知端！'));
        }

        self::$transportEndian = $endian;
    }

    /**
     * Get transport endian
     *
     * @return int
     */
    public static function getTransportEndian()
    {
        return self::$transportEndian;
    }

    /**
     * Get the current architecture's endian
     *
     * @return int
     */
    public static function getMachineEndian()
    {
        static $endian;

        if (null === $endian) {
            list(, $result) = unpack('L', pack('V', 1));
            $endian = $result === 1 ? self::ENDIAN_LITTLE : self::ENDIAN_BIG;
        }

        return $endian;
    }

    /**
     * Obtain an instance of the descriptor's registry
     *
     * @return TMProtoRegistry
     */
    public static function getRegistry()
    {
        static $registry = null;

        if (null === $registry) {
            $registry = new TMProtoRegistry();
        }

        return $registry;
    }


    public static function getCodec($codec = null)
    {
        if ($codec instanceof TMProtoCodecInterface) {
            return $codec;
        }

        // Bootstrap the library's default codec if none is available
        if (!isset(self::$codecs['default'])) {
            $default = new TMProtoCodecBinary();
            self::registerCodec('default', $default);
            self::registerCodec('binary', $default);
            self::registerCodec('array', new TMProtoCodecArray());
        }

        if (is_string($codec)) {
            $codec = strtolower($codec);
            if (!isset(self::$codecs[$codec])) {
                throw new TMProtoException(TM::t('taomee', '解码器{codec}不存在！', array('{codec}' => $codec)));
            }
            return self::$codecs[$codec];
        }

        return self::getCodec('default');
    }

    public static function registerCodec($name, TMProtoCodecInterface $codec)
    {
        $name = strtolower($name);
        self::$codecs[$name] = $codec;
    }

    public static function setDefaultCodec($codec)
    {
        if (is_string($codec)) {
            $codec = self::getCodec($codec);
        }

        if ($codec instanceof TMProtoCodecInterface) {
            self::registerCodec('default', $codec);
        } else {
            throw new TMProtoException(TM::t('taomee', '解码器必须实现{if}！', array('{if}' => 'TMProtoCodecInterface')));
        }
    }

    public static function unregisterCodec($name)
    {
        $name = strtolower($name);
        if (isset(self::$codecs[$name])) {
            unset(self::$codecs[$name]);
            return true;
        }
        return false;
    }

    /**
     * Encodes a message using the default codec
     *
     * @param  TMProtoMessage $message
     * @return string
     */
    public static function encode(TMProtoMessage $message)
    {
        $codec = self::getCodec();
        return $codec->encode($message);
    }

    /**
     * @param  string|TMProtoMessage $message
     * @param  string                $data
     * @return TMProtoMessage
     */
    public static function decode($message, $data)
    {
        if (is_string($message)) {
            $message = new $message();
        }

        $codec = self::getCodec();
        return $codec->decode($message, $data);
    }

    /**
     * Register include pathes.
     *
     * @return null
     */
    public static function register()
    {
        TM::import('system.proto.*');
        TM::import('system.proto.codec.*');
        TM::import('system.proto.compiler.*');
    }
}

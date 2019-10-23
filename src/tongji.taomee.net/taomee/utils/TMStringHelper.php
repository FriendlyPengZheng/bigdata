<?php
class TMStringHelper
{
    const HEX2BIN_WS = "\t\n\r";

    /**
     * @brief hex2bin 
     * Decodes a hexadecimally encoded binary string
     * Compatible for PHP < 5.4
     *
     * @param {string} $data
     *
     * @return {string}
     */
    static public function hex2bin($data)
    {
        if (function_exists('hex2bin')) {
            return hex2bin($data);
        } else {
            self::HEX2BIN_WS;
            $pos = 0;
            $result = '';
            while ($pos < strlen($data)) {
                if (false !== strpos(self::HEX2BIN_WS, $data{$pos})) {
                    $pos ++;
                } else {
                    $code = hexdec(substr($data, $pos, 2));
                    $pos += 2;
                    $result .= chr($code);
                }
            }
            return $result;
        }
    }

    /**                                                       
     * 深度遍历进行编码                                       
     */                                                       
    static public function deephtmlspecialchars($string)      
    {                                                         
        if (is_string($string)) {                             
            return htmlspecialchars($string);                 
        } else if (is_array($string)) {                       
            foreach ($string as &$str) {                      
                $str = self::deephtmlspecialchars($str);      
            }                                                 
            return $string;                                   
        } else {                                              
            return $string;                                   
        }                                                     
    }                                                         

    /**
     * @brief sha256 
     * SHA-256加密
     *
     * @param {string} $string
     *
     * @return {string}
     */
    static public function sha256($string)
    {
        return bin2hex(hash('sha256', $string, true));
    }

    /**
     * @brief random 
     * 生成随机字符串
     *
     * @param {integer} $length 字符串长度，默认为16
     *
     * @return {string}
     */
    static public function random($length = 16)
    {
        $string = '';

        while (($len = static::length($string)) < $length) {
            $size = $length - $len;

            $bytes = static::randomBytes($size);

            $string .= static::substr(str_replace(['/', '+', '='], '', base64_encode($bytes)), 0, $size);
        }

        return $string;
    }

    /**
     * Return the length of the given string.
     *
     * @param  string  $value
     * @return int
     */
    static public function length($value)
    {
        return mb_strlen($value);
    }

    /**
     * @brief randomBytes 
     * 生成随机二进制字节
     *
     * @param {integer} $length
     *
     * @return {string}
     */
    static public function randomBytes($length)
    {
        if (function_exists('random_bytes')) {
            return random_bytes($length);
        }
        if (function_exists('mcrypt_create_iv')) {
            return mcrypt_create_iv($length, MCRYPT_DEV_URANDOM);
        } 
        if (function_exists('openssl_random_pseudo_bytes')) {
            return openssl_random_pseudo_bytes($length);
        }
    }

    /**
     * Returns the portion of string specified by the start and length parameters.
     *
     * @param  string  $string
     * @param  int  $start
     * @param  int|null  $length
     * @return string
     */
    static public function substr($string, $start, $length = null)
    {
        return mb_substr($string, $start, $length, 'UTF-8');
    }
}

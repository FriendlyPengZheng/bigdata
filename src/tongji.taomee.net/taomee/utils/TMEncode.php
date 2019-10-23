<?php
class TMEncode
{
    /**
     * @brief array 
     * 常用的字符hex段
     * 20 2f => 特殊字符
     * 30 39 => 0 - 9 
     * 3a 40 => 特殊字符
     * 41 5a => A - Z
     * 5b 60 => 特殊字符
     * 61 7a => a - z
     * 7b 7e => 特殊字符
     */
    public static $commonCharacterRange = array(
        array(0x20, 0x7e)
    );

    /**
     * UTF8中文对应的hex段
     * 80 --- bf
     * e2 ba 80   e2 bd bf => 中文  U+2E80 ... U+2EFF: CJK Radicals Supplement
     * e2 bc 80   e2 bf 95 => 中文  U+2F00 ... U+2FDF: Kangxi Radicals
     * e3 80 80   e3 80 9f => 中文  全角符号
     * e2 80 90   e2 80 a7 => 中文  全角符号
     * e2 80 b0   e2 81 9e => 中文  半角符号
     * e4 b8 80   e4 bf bf => 中文  U+4E00 ... U+9FFF: CJK Unified Ideographs
     * e5 80 80   e5 bf bf => 中文  U+4E00 ... U+9FFF: CJK Unified Ideographs
     * e6 80 80   e6 bf bf => 中文  U+4E00 ... U+9FFF: CJK Unified Ideographs
     * e7 80 80   e7 bf bf => 中文  U+4E00 ... U+9FFF: CJK Unified Ideographs
     * e8 80 80   e8 bf bf => 中文  U+4E00 ... U+9FFF: CJK Unified Ideographs
     * e9 80 80   e9 bf 8b => 中文  U+4E00 ... U+9FFF: CJK Unified Ideographs
     * 
     * ef a4 80   ef a7 bf => 中文  U+F900 ... U+FAFF: CJK Compatibility Ideographs
     * ef b8 b0   ef bc af => 中文  U+FE30 ... U+FE4F: CJK Compatibility Forms
     * ef b9 90   ef bd 8f => 特殊字符  U+FE50 ... U+FE6F: Small Form Variants
     * ef bc 81   ef bd 9e => 特殊字符  U+FF00 ... U+FFEF: Halfwidth and Fullwidth Forms
     */
    public static $chineseCharacterRange = array(
		array(0xe2ba80, 0xe2bdbf),
		array(0xe2bc80, 0xe2bf95),
		array(0xe4b880, 0xe9bf8b),
		array(0xefa480, 0xefa7bf),
		array(0xefb8b0, 0xefbcaf),
		array(0xefb990, 0xefbd8f),
		array(0xe38080, 0xe3809f),
		array(0xe28080, 0xe280a7),
		array(0xe280b0, 0xe2819e),
		array(0xefb880, 0xefbd9e) 
    );

    /**
     * @brief array 
     * 其他可以包含的字符
     */
    public static $specialIncludeCharacter = array(
        '0xc2b7' // 中文中间的点
    );

    /**
     * @brief array 
     * 可以包含的简体中文字符
     */
    public static $includeChineseUtf8Character = array(
    );

    /**
     * @brief string
     * 上一次报错检测的十六进制字符
     */
    private static $_lastCharacters;

    /**
     * @brief isUTF8String 
     * 检测是否属于配置范围内的字符串
     *
     * @param {string} $string
     */
    public static function isUTF8String($string)
    {
        $hex = '0x';
        $bytes = 0;
        $length = strlen($string);
        $currentString = $lastString = '';
        for ($i = 0; $i < $length; $i++){
            $bytes ++;
            $currentString = $string[$i];
            $curhex = dechex(ord($currentString));
            $hex .= $curhex;
            if ($bytes == 1 && self::_isCommonCharacter($hex)) {
                $hex = '0x';
                $bytes = 0;
                continue;
            } else if ($bytes == 3 && self::_isChineseCharacter($hex)) {
                $hex = '0x';
                $bytes = 0;
            } else if (self::_isSpecialCharacter($hex)) {
                $hex = '0x';
                $bytes = 0;
                continue;
            } else if ($bytes > 3) {
                self::_throwException($lastString, $hex);
            }
            if ($bytes !== 1 && !self::_specialCheckForUTF8ChineseCharacter($curhex)) {
                self::_throwException($lastString, $hex);
            }
            $lastString = $currentString;
        }
        if ($bytes === 1 && !self::_isCommonCharacter($hex)) {
            self::_throwException($currentString, $hex);
        }
        return $string;
    }

    /**
     * @brief isSimpleChinese 
     * 检测是否属于简体中文
     *
     * @param {string} $string
     */
    public static function isSimpleChinese($string)
    {
        if (!extension_loaded('mbstring')) {
            throw new TMException(TM::t('taomee', '扩展{extension}没有加载', array('{extension}' => 'mbstring')));
        }
        foreach (self::$includeChineseUtf8Character as $character) {
            $string = mb_ereg_replace($character, '', $string);
        }
        if ($string !== iconv('gb2312', 'UTF-8//IGNORE', iconv('UTF-8//IGNORE', 'gb2312', $string))) {
            throw new TMEncodeException($string);
        }
    }

    /**
     * @brief _specialCheckForUTF8ChineseCharacter 
     *
     * @param {0x} $hex
     *
     * @return {boolean}
     */
    private static function _specialCheckForUTF8ChineseCharacter($hex)
    {
        $hex = "0x$hex";
        if ($hex >= 0x80 && $hex <= 0xbf) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @brief _isCommonCharacter
     * check if the character hex is in setted commonCharacterRange
     *
     * @param {0x} $hex
     *
     * @return {boolean}
     */
    private static function _isCommonCharacter($hex)
    {
        $return = false;
        foreach (self::$commonCharacterRange as $range) {
            if ($hex >= $range[0] && $hex <= $range[1]) {
                $return = true;
                break;
            }
        }
        return $return;
    }

    /**
     * @brief _isChineseCharacter 
     * check if the character hex is in setted chineseCharacterRange
     *
     * @param {0x} $hex
     *
     * @return {boolean}
     */
    private static function _isChineseCharacter($hex)
    {
        $return = false;
        foreach (self::$chineseCharacterRange as $range) {
            if ($hex >= $range[0] && $hex <= $range[1]) {
                $return = true;
                break;
            }
        }
        return $return;
    }

    /**
     * @brief _isSpecialCharacter 
     * check if the character hex is in setted specialIncludeCharacter
     *
     * @param {0x} $hex
     *
     * @return {boolean}
     */
    private static function _isSpecialCharacter($hex)
    {
        return in_array($hex, self::$specialIncludeCharacter);
    }

    /**
     * @brief _throwException 
     * throw the exception
     *
     * @param {string} $text
     * @param {string} $encoding
     */
    private static function _throwException($text, $encoding)
    {
        self::$_lastCharacters = $encoding;
        throw new TMEncodeException($text);
    }

    /**
     * @brief getLastThrowEncoding 
     * 获取上次报错的编码
     *
     * @return 
     */
    public static function getLastThrowEncoding()
    {
        return self::$_lastCharacters;
    }
}

class TMEncodeException extends TMException
{
}

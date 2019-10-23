<?php
class TMFileHelper
{
    /**
     * Remove Invisible Characters.
     * This prevents sandwiching null characters between ascii characters, like Java\0script.
     * @param string $str
     * @param boolean $urlEncoded
     * @return string
     */
    public static function removeInvisibleCharacters($str, $urlEncoded = true)
    {
        $nonDisplayables = array();

        // every control character except newline (dec 10)
        // carriage return (dec 13), and horizontal tab (dec 09)

        if ($urlEncoded) {
            $nonDisplayables[] = '/%0[0-8bcef]/';    // url encoded 00-08, 11, 12, 14, 15
            $nonDisplayables[] = '/%1[0-9a-f]/';     // url encoded 16-31
        }

        $nonDisplayables[] = '/[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]+/S';    // 00-08, 11, 12, 14-31, 127

        do {
            $str = preg_replace($nonDisplayables, '', $str, -1, $count);
        } while ($count);

        return $str;
    }

    /**
     * 过滤文件名
     * @param string $str
     * @param boolean $relativePath
     * @return string
     */
    public static function sanitizeFilename($str, $relativePath = false)
    {
        $bad = array(
            "../",
            "<!--",
            "-->",
            "<",
            ">",
            "'",
            '"',
            '&',
            '$',
            '#',
            '{',
            '}',
            '[',
            ']',
            '=',
            ';',
            '?',
            "%20",
            "%22",
            "%3c",        // <
            "%253c",      // <
            "%3e",        // >
            "%0e",        // >
            "%28",        // (
            "%29",        // )
            "%2528",      // (
            "%26",        // &
            "%24",        // $
            "%3f",        // ?
            "%3b",        // ;
            "%3d"         // =
        );

        if (!$relativePath) {
            $bad[] = './';
            $bad[] = '/';
        }

        $str = self::removeInvisibleCharacters($str, false);
        return stripslashes(str_replace($bad, '', $str));
    }

    /**
     * Shared environment safe version of mkdir. Supports recursive creation.
     * For avoidance of umask side-effects chmod is used.
     * @param string $dst path to be created
     * @param integer $mode access bitmask
     * @param boolean $recursive whether to create directory structure recursive if parent dirs do not exist
     * @return boolean result of mkdir
     * @see mkdir
     */
    public static function mkdir($dst, $mode = 0777, $recursive = true)
    {
        if (is_dir($dst)) {
            if ($mode === (fileperms($dst) & 0777)) {
                return true;
            }
            @chmod($dst, $mode);
            return true;
        }

        $prevDir = dirname($dst);
        if ($recursive && !is_dir($prevDir)) {
            self::mkdir($prevDir, $mode, true);
        }
        if (is_writable($prevDir)) {
            $res = @mkdir($dst, $mode);
            @chmod($dst, $mode);
            return $res;
        } else {
            return false;
        }
    }

    /**
     * Removes a directory recursively.
     * @param string $directory to be deleted recursively.
     */
    public static function rmdir($directory)
    {
        if (($handle = @opendir($directory)) !== false) {
            while (($item = readdir($handle)) !== false) {
                if ($item === '.' || $item === '..') {
                    continue;
                }
                $item = rtrim($directory, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR . $item;
                if (is_dir($item)) {
                    self::rmdir($item);
                } else {
                    unlink($item);
                }
            }
            closedir($handle);
            if (is_dir($directory)) {
                rmdir($directory);
            }
        }
    }

    /**
     * Send a file to browser.
     *
     * @param  string $filePath
     * @param  string $fileName
     * @param  bool   $useXSendFile
     * @param  string $server
     * @return null
     */
    public static function sendFile($filePath, $fileName = null, $useXSendFile = false, $server = 'apache')
    {
        if (!isset($fileName)) $fileName = basename($filePath);

        header("Content-type: application/octet-stream");

        // deal with chinese name
        $ua = TM::app()->getHttp()->getUserAgent();
        $encodedName = rawurlencode($fileName);
        if (preg_match('/MSIE/', $ua)) {
            header('Content-Disposition: attachment; filename="' . $encodedName . '"');
        } elseif (preg_match('/Firefox/', $ua)) {
            header('Content-Disposition: attachment; filename*="utf8\'\'' . $fileName . '"');
        } else {
            header('Content-Disposition: attachment; filename="' . $fileName . '"');
        }

        // use x-send-file
        if ($useXSendFile) {
            switch ($server) {
                case 'apache':
                    header("X-Sendfile: $filePath");
                    exit(0);
                    break;

                case 'nginx':
                    header("X-Accel-Redirect: $filePath");
                    exit(0);
                    break;

                case 'lighttpd':
                    header("X-LIGHTTPD-send-file: $filePath");
                    exit(0);
                    break;
            }
        }

        // direct output
        readfile($filePath);
        exit(0);
    }

    /**
     * Send a file to browser.
     *
     * @param  string $fileName
     * @param  string $content
     * @param  bool   $useXSendFile
     * @param  string $server
     * @return null
     */
    public static function sendDataFile($fileName, $content)
    {
        header('Content-type: application/octet-stream');

        // deal with chinese name
        $ua = TM::app()->getHttp()->getUserAgent();
        $encodedName = rawurlencode($fileName);
        if (preg_match('/MSIE/', $ua)) {
            header('Content-Disposition: attachment; filename="' . $encodedName . '"');
        } elseif (preg_match('/Firefox/', $ua)) {
            header('Content-Disposition: attachment; filename*="utf8\'\'' . $fileName . '"');
        } else {
            header('Content-Disposition: attachment; filename="' . $fileName . '"');
        }
        echo $content;
        exit(0);
    }

    /**
     * @brief writeFile 
     * 向指定目录写文件，返回文件名称
     *
     * @param {string} $path 文件路径
     * @param {string} $message 文件内容 
     * @param {string} $suffix 文件后缀
     * @param {integer} $retryLimit 重试次数 
     *
     * @return {string}
     */
    public static function writeFile($path, $message, $suffix='', $retryLimit=3)
    {
        $fileName = $path . DS . self::getRandomString(16);
        for ($i = 0; $i < $retryLimit; ++$i) {
            $fp = @fopen($fileName . $suffix, 'x');
            if (false !== $fp) {
                if (false === fwrite($fp, $message)) {
                    return false;
                }
                if (false === fclose($fp)) {
                    return false;
                } else {
                    return $fileName . $suffix;
                }
            } else {
                $fileName .= self::getRandomString(1);
            }
        }
        return false;
    }

    /**
     * @brief getRandomString 
     * 获取随机字符串
     *
     * @param {integer} $count
     *
     * @return {string}
     */
    public static function getRandomString($count)
    {
        // This string MUST stay FS safe, avoid special chars
        $base = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.';
        $ret = '';
        $strlen = strlen($base);
        for ($i = 0; $i < $count; ++$i) {
            $ret .= $base[((int) rand(0, $strlen - 1))];
        }
        return $ret;
    }
}

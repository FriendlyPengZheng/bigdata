<?php
/**
* @file TMZipArchive.php
* @brief 对ZipArchive的扩展，支持gbk编码正常解压
* @author violet violet@taomee.com
* @version 1.0
* @date 2016-06-08
*/
class TMZipArchive extends ZipArchive
{
    /**
     * @brief extractTo 
     * 支持非UTF8格式的转换
     * @see parent
     */
    public function extractTo($destination, $entries=null)
    {
        $result = true;

        // Prepare dirs
        $destination = str_replace(array('/', '\\'), DIRECTORY_SEPARATOR, $destination);
        if (substr($destination, mb_strlen(DIRECTORY_SEPARATOR, 'UTF-8') * -1) != DIRECTORY_SEPARATOR) {
            $destination .= DIRECTORY_SEPARATOR;
        }

        // Prepare entries
        if (null !== $entries) {
            $entries = is_array($entries) ? $entries : array($entries);
        }

        // Extract files
        for ($i = 0; $i < $this->numFiles; $i++) {
            $filename = $this->getNameIndex($i);
            $encode = mb_detect_encoding($filename, 'UTF-8, ISO-8859-*, GB18030, BIG-5');
            if (strtoupper(trim($encode)) !== 'UTF-8') {
                $filename = iconv($encode, 'UTF-8//IGNORE', $filename);
            }
            if (null !== $entries && !in_array($filename, $entries)) continue;

            // Directory
            if (substr($filename, -1) == DIRECTORY_SEPARATOR) {
                // New dir
                if (!is_dir($destination . $filename)) {
                    if (@mkdir($destination . $filename, 0755, true) === false) {
                        $result = false;
                        break;
                    }
                }
            } else {
                if (dirname($filename) != '.') {
                    if (!is_dir($destination . dirname($filename))) {
                        // New dir (for file)
                        if (@mkdir($destination . dirname($filename), 0755, true) === false) {
                            $result = false;
                            break;
                        }
                    }
                }
                // New file
                if (@file_put_contents($destination . $filename, $this->getFromIndex($i)) === false) {
                    $result = false;
                    break;
                }
            }
        }

        return $result;
    }
}

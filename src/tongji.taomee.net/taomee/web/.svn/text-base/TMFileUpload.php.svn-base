<?php
class TMFileUpload
{
    /**
     * @string the name of upload file 
     */
    private $_name;

    /**
     * @string the temporary name of upload file 
     */
    private $_tempName;

    /**
     * @string the type of upload file 
     */
    private $_type;

    /**
     * @string the size of upload file 
     */
    private $_size;

    /**
     * @string the error of upload file 
     */
    private $_error;

    /**
     * @brief getInstance 
     * 根据上传的字段名获取实例
     *
     * @param {string} $name
     *
     * @return {TMFileUpload|null}
     */
    public static function getInstance($name)
    {
        if (!isset($_FILES) || !is_array($_FILES) || !isset($_FILES[$name])) {
            return;
        }
        $info = $_FILES[$name];
        return new TMFileUpload($info['name'], $info['tmp_name'], $info['type'], $info['size'], $info['error']);
    }

    /**
     * @param {string} $name
     * @param {string} $tempName
     * @param {string} $type
     * @param {float} $size
     * @param {interval} $error
     *
     * @return {TMFileUpload}
     */
    public function __construct($name, $tempName, $type, $size, $error)
    {
        $this->_name = $name;
        $this->_tempName = $tempName;
        $this->_type = $type;
        $this->_size = $size;
        $this->_error = $error;
    }

    /**
     * @brief getName 
     * 获取上传文件名
     *
     * @return {string}
     */
    public function getName()
    {
        return $this->_name;
    }

    /**
     * @brief getTempName 
     * 获取上传临时文件名
     *
     * @return {string}
     */
    public function getTempName()
    {
        return $this->_tempName;
    }

    /**
     * @brief getType 
     * 获取上传文件的类型
     *
     * @return {string}
     */
    public function getType()
    {
        return $this->type;
    }

    /**
     * @brief getSize 
     * 获取上传文件的大小
     *
     * @return {float}
     */
    public function getSize()
    {
        return $this->size;
    }

    /**
     * @brief getError 
     * 获取上传文件错误码
     *
     * @return {interval}
     */
    public function getError()
    {
        return $this->error;
    }

    /**
     * @brief getExtensionName 
     * 获取上传文件扩展名
     *
     * @return {string}
     */
    public function getExtensionName()
    {
        if (false !== ($pos = strrpos($this->_name, '.'))) {
            return (string)substr($this->_name, $pos + 1);
        } else {
            return '';
        }
    }

    /**
     * @brief saveAs 
     * 保存上传文件
     *
     * @param {string} $file
     * @param {boolean} $deleteTempFile
     *
     * @return {boolean}
     */
    public function saveAs($file, $deleteTempFile=true)
    {
        if (UPLOAD_ERR_OK === $this->_error) {
            if ($deleteTempFile) {
                return move_uploaded_file($this->_tempName, $file);
            } elseif (is_uploaded_file($this->_tempName)){
                return copy($this->_tempName, $file);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

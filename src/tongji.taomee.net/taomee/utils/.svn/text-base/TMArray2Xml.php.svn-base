<?php
class TMArray2Xml
{
    /**
     * @var {string} XML文件的根标签 
     */
    public $root = 'root';

    /**
     * @var {string} XML版本号
     */
    public $version = '1.0';

    /**
     * @var {string} XML字符集
     */
    public $encoding = 'utf-8';

    /**
     * {XmlWriter}
     */
    private $_xml;

    /**
     * @brief __construct 
     *
     * @param {string} $root XML文件的根标
     * @param {string} $version XML版本号
     * @param {string} $encoding XML字符集
     */
    public function __construct($root=null, $version=null, $encoding=null)
    {
        if (null !== $root) $this->root = $root;
        if (null !== $version) $this->version = $version;
        if (null !== $encoding) $this->encoding = $encoding;
        $this->_xml = new XmlWriter();
    }

    /**
     * @brief transform 
     * 将数组转换为简单XML字符串（不支持属性设置）
     *
     * @param {array} $data
     * @param {boolean} $isArray
     *
     * @return {string}
     */
    public function transform($data, $isArray=false)
    {
        if (!$isArray) {
            $this->_xml->openMemory();  
            $this->_xml->startDocument($this->version, $this->encoding);  
            $this->_xml->startElement($this->root);
        }
        foreach ($data as $key => $value) {
            if (is_array($value)) {  
                $this->_xml->startElement($key);  
                $this->transform($value, true);  
                $this->_xml->endElement();  
                continue;  
            }  
            $this->_xml->writeElement($key, $value);  
        }
        if (!$isArray) {
            $this->_xml->endElement();  
            return $this->_xml->outputMemory(true);
        }
    }
}

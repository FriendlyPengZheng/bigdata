<?php
/**
* @file TMConfigure.php
* @brief 动态读取设置的配置
* @author violet violet@taomee.com
* @version 1.0 
* @date 2015-08-30
*/
class TMConfigure extends TMComponent
{
    /**
     * @var {array} 解析后的属性值
     */
    private $_attributesValues = array();

    public function __get($attributeName)
    {
        return $this->get($attributeName);
    }

    /**
     * @brief get 
     * 获取配置
     *
     * @param {string} $attributeName 设置的属性
     * @param {string} $key 属性额外参数
     *
     * @return {mixed}
     */
    public function get($attributeName, $key='')
    {
        $attributeKey = "$attributeName$key";
        if (array_key_exists($attributeKey, $this->_attributesValues)) {
            return $this->_attributesValues[$attributeKey];
        }

        $values = parent::__get($attributeName);
        // 属性值需要重新解析
        if (is_string($values) && $values[0] == '@') {
            $values = substr($values, 1);
            $value  = TM::getPathWithAlias($values);
            // 包含_，表示文件中KEY值
            if (strstr($values, '_')) {
                $value = $this->parseFileKey($value);
                return $this->_attributesValues[$attributeKey] = $value;
            // 包含@，需要额外的参数组成文件名称
            } else if (strstr($value, '@')) {
                $file = str_replace('@', $key, $value);
                $file = $file . '.php';
                if (!file_exists($file) || !is_readable($file)) {
                    throw new TMException(TM::t('taomee', '文件{file}不存在或不可读！',
                        array('{file}' => $file)));
                }
                $value = require($file);
                return $this->_attributesValues[$attributeKey] = $value;
            // 读出文件中所有值
            } else {
                $file = $value . '.php';
                if (!file_exists($file) || !is_readable($file)) {
                    throw new TMException(TM::t('taomee', '文件{file}不存在或不可读！',
                        array('{file}' => $file)));
                }
                return $this->_attributesValues[$attributeKey] = require($file);
            }
        }
        return $values;
    }

    /**
     * @brief parseFileKey 
     * 解析文件中KEY
     */
    protected function parseFileKey($values)
    {
        $pos  = strrpos($values, '_');
        $file = substr($values, 0, $pos) . '.php';
        $key  = substr($values, $pos + 1);
        if (!file_exists($file) || !is_readable($file)) {
            throw new TMException(TM::t('taomee', '文件{file}不存在或不可读！',
                array('{file}' => $file)));
        }
        $values = require($file);
        if ($key) {
            if (!is_array($values) || !array_key_exists($key, $values)) {
                throw new TMException(TM::t('taomee', '文件{file}不存在键值{key}！',
                    array('{file}' => $file, '{key}' => $key)));
            }
            $values = $values[$key];
        }
        return $values;
    }
}

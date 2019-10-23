<?php
/**
* @file TMEmailValidator.php
* @brief 邮件地址检测
* @author violet violet@taomee.com
* @version 1.0
* @date 2015-10-12
*/
class TMEmailValidator extends TMValidator
{
    /**
     * @var string
     */
    public $pattern = '/^[a-zA-Z0-9!#$%&\'*+\\/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&\'*+\\/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?$/';

    /**
     * @var string
     */
    public $fullPattern = '/^[^@]*<[a-zA-Z0-9!#$%&\'*+\\/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&\'*+\\/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?>$/';

    /**
     * @var {boolean} 是否允许姓名。(e.g. "Violet Deng<violet@taomee.com>")
     */
    public $allowName = false;

    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = true;

    /**
     * @var {boolean} 是否检测MX记录 
     */
    public $checkMX = false;

    /**
     * @var {boolean} 是否检测MX端口
     */
    public $checkPort = false;

    /**
     * Validates the attribute of the object.
     * @param TMModel $object the object being validated
     * @param string $attribute the attribute being validated
     */
    protected function validateAttribute($object, $attribute)
    {
        $this->validateValue($object, $attribute, $object->$attribute);
    }

    /**
     * Validates the given value
     * @param {object} $object 需要验证的类
     * @param {string} $attribute the attribute being validated
     * @param {mixed} $value the value being validated
     */
    public function validateValue(TMValidatorInterface $object, $attribute, $value)
    {
        if ($this->allowEmpty && $this->isEmpty($value)) {
            return;
        }

        $valid = is_string($value) && strlen($value) <= 254 && 
            (preg_match($this->pattern, $value) || $this->allowName && preg_match($this->fullPattern, $value));

        if ($valid) {
            $domain = rtrim(substr($value, strpos($value,'@') + 1), '>');
        }

        if ($valid && $this->checkMX && function_exists('checkdnsrr')) {
            $valid = checkdnsrr($domain, 'MX');
        }

        if ($valid && $this->checkPort && function_exists('fsockopen') && function_exists('dns_get_record')) {
            $valid = $this->checkMxPorts($domain);
        }

        if (!$valid) {
            $this->throwError($object, $attribute, '{attribute}配置错误！');
        }
    }

    /**
     * @brief checkMxPorts 
     * 检测端口
     *
     * @param {string} $domain
     *
     * @return {boolean}
     */
    protected function checkMxPorts($domain)
    {
        $records = dns_get_record($domain, DNS_MX);
        if (false === $records || empty($records)) {
            return false;
        }
        usort($records, array($this, 'mxSort'));

        foreach($records as $record) {
            $handle = @fsockopen($record['target'], 25);
            if (false !== $handle) {
                fclose($handle);
                return true;
            }
        }
        return false;

    }

    /**
     * @brief mxSort 
     * 排序回调
     *
     * @param {array} $a
     * @param {array} $b
     *
     * @return {integer}
     */
    protected function mxSort($a, $b)
    {
        if ($a['pri'] == $b['pri']) {
            return 0;
        }
        return ($a['pri'] < $b['pri']) ? -1 : 1;
    }
}

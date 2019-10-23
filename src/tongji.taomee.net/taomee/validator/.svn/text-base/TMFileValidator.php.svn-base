<?php
class TMFileValidator extends TMValidator
{
    /**
     * @var integer maximum file size. Defaults to null, meaning no maximum limit.
     */
    public $maxSize;

    /**
     * @var integer minimum file size. Defaults to null, meaning no minimum limit.
     */
    public $minSize;

    /**
     * @var string|array allow file type list. Defaults to null, meaning no file types limit.
     */
    public $types;

    /**
     * @var boolean whether the attribute value can be null or empty. Defaults to true,
     * meaning that if the attribute is empty, it is considered valid.
     */
    public $allowEmpty = false;

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
        if (!$value instanceOf TMFileUpload) {
            $message = '{attribute}必须为TMFileUpload类型！';
            $this->throwError($object, $attribute, $message);
        }
    }

    /**
     * @brief validateFile 
     * 检测文件
     *
     * @param {object} $object
     * @param {string} $attribute
     * @param {TMFileUpload} $file
     */
    protected function validateFile($object, $attribute, $file)
    {
        // 没有上传文件
        if (null === $file || ($error = $file->getError()) === UPLOAD_ERR_NO_FILE) {
            if (!$this->allowEmpty) {
                $message = '{attribute}不能为空！';
                $this->throwError($object, $attribute, $message);
            }
        }
        // 只有部分文件上传
        else if (UPLOAD_ERR_PARTIAL === $error) {
        }
        // 文件最大大小
        else if (UPLOAD_ERR_INI_SIZE === $error || UPLOAD_ERR_FORM_SIZE === $error || null !== $this->maxSize && $file->getSize() > $this->maxSize) {
            $message = '{attribute}文件太大！';
            $this->throwError($object, $attribute, $message);
        }
        // 临时文件夹找不到
        else if (UPLOAD_ERR_NO_TMP_DIR === $error) {
            $message = '上传临时文件夹丢失！';
            $this->throwError($object, $attribute, $message);
        }
        // 硬盘不可写
        else if (UPLOAD_ERR_CANT_WRITE === $error) {
            $message = '硬盘不可写！';
            $this->throwError($object, $attribute, $message);
        }
        // 硬盘不可写
        else if (UPLOAD_ERR_EXTENSION === $error) {
            $message = 'PHP扩展阻止文件上传！';
            $this->throwError($object, $attribute, $message);
        }

        if (null !== $this->minSize && $file->getSize() < $this->minSize) {
            $message = '{attribute}文件太小！';
            $this->throwError($object, $attribute, $message);
        }
        if (null !== $this->types) {
            $types = $this->types;
            if (is_string($types)) {
                $types = preg_split('/[\s,]+/', strtolower($types), -1, PREG_SPLIT_NO_EMPTY);
            }
            if (!in_array($file->getExtensionName(), $types)) {
                $message = '上传文件类型不正确！';
                $this->throwError($object, $attribute, $message);
            }
        }
    }
}

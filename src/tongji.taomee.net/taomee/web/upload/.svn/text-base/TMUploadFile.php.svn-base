<?php
class TMUploadFile implements ArrayAccess, Countable
{
    /**
     * Upload error code messages
     * @var array
     */
    protected static $errorCodeMessages = array();

    /**
     * Storage delegate
     * @var TMUploadStorageInterface
     */
    protected $storage;

    /**
     * File information
     * @var array[TMUploadFileInfoInterface]
     */
    protected $objects = array();

    /**
     * Validators
     * @var array[TMUploadValidatorInterface]
     */
    protected $validators = array();

    /**
     * Before validation callback
     * @var callable
     */
    protected $beforeValidationCallback;

    /**
     * After validation callback
     * @var callable
     */
    protected $afterValidationCallback;

    /**
     * Before upload callback
     * @var callable
     */
    protected $beforeUploadCallback;

    /**
     * After upload callback
     * @var callable
     */
    protected $afterUploadCallback;

    /**
     * Constructor
     *
     * @param  string                    $key     The $_FILES[] key
     * @param  TMUploadStorageInterface  $storage The upload delegate instance
     * @throws TMUploadException                  If file uploads are disabled in the php.ini file
     * @throws TMValidatorException               If $_FILES[] does not contain key
     */
    public function __construct($key, TMUploadStorageInterface $storage)
    {
        // Check if file uploads are allowed
        if (ini_get('file_uploads') == false) {
            throw new TMUploadException(TM::t('taomee', '系统禁止上传文件！'));
        }

        // Check if key exists
        if (isset($_FILES[$key]) === false) {
            throw new TMUploadValidatorException(TM::t('taomee', '找不到{key}标识的上传文件!', array('{key}' => $key)));
        }

        // Initialize upload error code messages.
        $this->initErrorCodeMessages();

        // Collect file info
        if (is_array($_FILES[$key]['tmp_name']) === true) {
            foreach ($_FILES[$key]['tmp_name'] as $index => $tmpName) {
                if ($_FILES[$key]['error'][$index] !== UPLOAD_ERR_OK) {
                    throw new TMUploadValidatorException(
                        $_FILES[$key]['name'][$index] . ' ' . self::$errorCodeMessages[$_FILES[$key]['error'][$index]]);
                }

                $this->objects[] = new TMUploadFileInfo(
                    $_FILES[$key]['tmp_name'][$index],
                    $_FILES[$key]['name'][$index]
                );
            }
        } else {
            if ($_FILES[$key]['error'] !== UPLOAD_ERR_OK) {
                throw new TMUploadValidatorException(
                    $_FILES[$key]['name'] . ' ' . self::$errorCodeMessages[$_FILES[$key]['error']]);
            }

            $this->objects[] = new TMUploadFileInfo(
                $_FILES[$key]['tmp_name'],
                $_FILES[$key]['name']
            );
        }

        $this->storage = $storage;
    }

    /**
     * Initialize upload error code messages.
     *
     * @return null
     */
    protected function initErrorCodeMessages()
    {
        if (empty(self::$errorCodeMessages)) {
            self::$errorCodeMessages = array(
                1 => TM::t('taomee', '上传文件大小超过系统限定值{filesize}！', array(
                    '{filesize}' => ini_get('upload_max_filesize'))),
                2 => TM::t('taomee', '上传文件大小超过表单的MAX_FILE_SIZE！'),
                3 => TM::t('taomee', '文件只有部分被上传！'),
                4 => TM::t('taomee', '没有文件被上传！'),
                6 => TM::t('taomee', '上传临时目录缺失！'),
                7 => TM::t('taomee', '上传文件写入失败！'),
                8 => TM::t('taomee', '文件上传被PHP扩展阻止！')
            );
        }
    }

    /********************************************************************************
     * Callbacks
     *******************************************************************************/

    /**
     * Set `beforeValidation` callable
     *
     * @param  callable          $callable Should accept one `TMUploadFileInfoInterface` argument
     * @return TMUploadFile                Self
     * @throws TMUploadException           If argument is not a callable object
     */
    public function beforeValidate($callable)
    {
        if (is_callable($callable) === false) {
            throw new TMUploadException(TM::t('taomee', '验证前回调不可调用！'));
        }
        $this->beforeValidation = $callable;

        return $this;
    }

    /**
     * Set `afterValidation` callable
     *
     * @param  callable          $callable Should accept one `TMUploadFileInfoInterface` argument
     * @return TMUploadFile                Self
     * @throws TMUploadException           If argument is not a callable object
     */
    public function afterValidate($callable)
    {
        if (is_callable($callable) === false) {
            throw new TMUploadException(TM::t('taomee', '验证后回调不可调用！'));
        }
        $this->afterValidation = $callable;

        return $this;
    }

    /**
     * Set `beforeUpload` callable
     *
     * @param  callable          $callable Should accept one `TMUploadFileInfoInterface` argument
     * @return TMUploadFile                Self
     * @throws TMUploadException           If argument is not a callable object
     */
    public function beforeUpload($callable)
    {
        if (is_callable($callable) === false) {
            throw new TMUploadException(TM::t('taomee', '上传前回调不可调用！'));
        }
        $this->beforeUpload = $callable;

        return $this;
    }

    /**
     * Set `afterUpload` callable
     *
     * @param  callable          $callable Should accept one `TMUploadFileInfoInterface` argument
     * @return TMUploadFile                Self
     * @throws TMUploadException           If argument is not a callable object
     */
    public function afterUpload($callable)
    {
        if (is_callable($callable) === false) {
            throw new TMUploadException(TM::t('taomee', '上传后回调不可调用！'));
        }
        $this->afterUpload = $callable;

        return $this;
    }

    /**
     * Apply callable
     *
     * @param  string                    $callbackName
     * @param  TMUploadFileInfoInterface $file
     * @return TMUploadFile                            Self
     */
    protected function applyCallback($callbackName, TMUploadFileInfoInterface $file)
    {
        if (in_array($callbackName, array('beforeValidation', 'afterValidation', 'beforeUpload', 'afterUpload')) === true) {
            if (isset($this->$callbackName) === true) {
                call_user_func_array($this->$callbackName, array($file));
            }
        }
    }

    /********************************************************************************
     * Validation and Error Handling
     *******************************************************************************/

    /**
     * Add file validators
     *
     * @param  array[TMUploadValidatorInterface] $validators
     * @return TMUploadFile                                  Self
     */
    public function addValidators($validators)
    {
        foreach ($validators as $validator) {
            $this->addValidator($validator);
        }

        return $this;
    }

    /**
     * Add file validator
     *
     * @param  TMUploadValidatorInterface $validator
     * @return TMUploadFile                          Self
     */
    public function addValidator(TMUploadValidatorInterface $validator)
    {
        $this->validators[] = $validator;

        return $this;
    }

    /**
     * Get file validators
     *
     * @return array[TMUploadValidatorInterface]
     */
    public function getValidators()
    {
        return $this->validators;
    }

    /**
     * Validate this collection.
     *
     * @return bool
     * @throws TMUploadValidatorException If validation fails
     * @throws TMUploadException          If validation callbacks not callable
     */
    public function validate()
    {
        foreach ($this->objects as $fileInfo) {
            // Before validation callback
            $this->applyCallback('beforeValidation', $fileInfo);

            // Check is uploaded file
            if ($fileInfo->isUploadedFile() === false) {
                throw new TMUploadValidatorException(
                    $fileInfo->getNameWithExtension() . ' ' . TM::t('taomee', '不是上传文件！'));
            }

            // Apply user validations
            foreach ($this->validators as $validator) {
                try {
                    $validator->validate($fileInfo);
                } catch (TMUploadValidatorException $e) {
                    throw new TMUploadValidatorException(
                        $fileInfo->getNameWithExtension() . ' ' . $e->getMessage());
                }
            }

            // After validation callback
            $this->applyCallback('afterValidation', $fileInfo);
        }
    }

    /********************************************************************************
     * Helper Methods
     *******************************************************************************/

    public function __call($name, $arguments)
    {
        $count = count($this->objects);
        $result = null;

        if ($count) {
            if ($count > 1) {
                $result = array();
                foreach ($this->objects as $object) {
                    $result[] = call_user_func_array(array($object, $name), $arguments);
                }
            } else {
                $result = call_user_func_array(array($this->objects[0], $name), $arguments);
            }
        }

        return $result;
    }

    /********************************************************************************
    * Upload
    *******************************************************************************/

    /**
     * Upload file (delegated to storage object)
     *
     * @return bool
     * @throws TMValidatorException If validation fails
     * @throws TMUploadException    If upload fails
     */
    public function upload()
    {
        $this->validate();

        foreach ($this->objects as $fileInfo) {
            $this->applyCallback('beforeUpload', $fileInfo);
            $this->storage->upload($fileInfo);
            $this->applyCallback('afterUpload', $fileInfo);
        }

        return true;
    }

    /********************************************************************************
     * Array Access Interface
     *******************************************************************************/

    public function offsetExists($offset)
    {
        return isset($this->objects[$offset]);
    }

    public function offsetGet($offset)
    {
        return isset($this->objects[$offset]) ? $this->objects[$offset] : null;
    }

    public function offsetSet($offset, $value)
    {
        $this->objects[$offset] = $value;
    }

    public function offsetUnset($offset)
    {
        unset($this->objects[$offset]);
    }

    /********************************************************************************
     * Countable Interface
     *******************************************************************************/

    public function count()
    {
        return count($this->objects);
    }

    /********************************************************************************
     * Helpers
     *******************************************************************************/

    /**
     * Convert human readable file size (e.g. "10K" or "3M") into bytes
     *
     * @param  string $input
     * @return int
     */
    public static function humanReadableToBytes($input)
    {
        $number = (int)$input;
        $units = array(
            'b' => 1,
            'k' => 1024,
            'm' => 1048576,
            'g' => 1073741824
        );
        $unit = strtolower(substr($input, -1));
        if (isset($units[$unit])) {
            $number = $number * $units[$unit];
        }

        return $number;
    }

    /**
     * Register include pathes.
     *
     * @return null
     */
    public static function register()
    {
        TM::import('system.web.upload.*');
        TM::import('system.web.upload.storage.*');
        TM::import('system.web.upload.validator.*');
    }
}

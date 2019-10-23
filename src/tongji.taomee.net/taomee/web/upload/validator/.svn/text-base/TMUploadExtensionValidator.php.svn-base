<?php
class TMUploadExtensionValidator implements TMUploadValidatorInterface
{
    /**
     * Array of acceptable file extensions without leading dots
     * @var array
     */
    protected $allowedExtensions;

    /**
     * Constructor
     *
     * @param string|array $allowedExtensions Allowed file extensions
     * @example new TMUploadExtensionValidator(array('png','jpg','gif'))
     * @example new TMUploadExtensionValidator('png')
     */
    public function __construct($allowedExtensions)
    {
        if (is_string($allowedExtensions) === true) {
            $allowedExtensions = array($allowedExtensions);
        }

        $this->allowedExtensions = array_map('strtolower', $allowedExtensions);
    }

    /**
     * Validate
     *
     * @param  TMUploadFileInfoInterface $fileInfo
     * @throws TMUploadValidatorException          If validation fails
     */
    public function validate(TMUploadFileInfoInterface $fileInfo)
    {
        $fileExtension = strtolower($fileInfo->getExtension());

        if (in_array($fileExtension, $this->allowedExtensions) === false) {
            throw new TMUploadValidatorException(TM::t('taomee',
                '文件扩展名不非法，必须是{ext}中其一', array('{ext}' => implode(', ', $this->allowedExtensions))));
        }
    }
}

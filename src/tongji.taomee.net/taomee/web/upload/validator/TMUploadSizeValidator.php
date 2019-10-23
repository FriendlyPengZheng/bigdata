<?php
class TMUploadSizeValidator implements TMUploadValidatorInterface
{
    /**
     * Minimum acceptable file size (bytes)
     * @var int
     */
    protected $minSize;

    /**
     * Maximum acceptable file size (bytes)
     * @var int
     */
    protected $maxSize;

    /**
     * Constructor
     *
     * @param int $maxSize Maximum acceptable file size in bytes (inclusive)
     * @param int $minSize Minimum acceptable file size in bytes (inclusive)
     */
    public function __construct($maxSize, $minSize = 0)
    {
        if (is_string($maxSize)) {
            $maxSize = TMUploadFile::humanReadableToBytes($maxSize);
        }
        $this->maxSize = $maxSize;

        if (is_string($minSize)) {
            $minSize = TMUploadFile::humanReadableToBytes($minSize);
        }
        $this->minSize = $minSize;
    }

    /**
     * Validate
     *
     * @param  TMUploadFileInfoInterface  $fileInfo
     * @throws TMUploadValidatorException           If validation fails
     */
    public function validate(TMUploadFileInfoInterface $fileInfo)
    {
        $fileSize = $fileInfo->getSize();

        if ($fileSize < $this->minSize) {
            throw new TMUploadValidatorException(TM::t('taomee',
                '上传文件必须不小于{size}！', array('{size}' => $this->minSize)));
        }

        if ($fileSize > $this->maxSize) {
            throw new TMUploadValidatorException(TM::t('taomee',
                '上传文件必须不大于{size}！', array('{size}' => $this->maxSize)));
        }
    }
}

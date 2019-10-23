<?php
class TMUploadMimetypeValidator implements TMUploadValidatorInterface
{
    /**
     * Valid media types
     * @var array
     */
    protected $mimetypes;

    /**
     * Constructor
     *
     * @param string|array $mimetypes
     */
    public function __construct($mimetypes)
    {
        if (is_string($mimetypes) === true) {
            $mimetypes = array($mimetypes);
        }
        $this->mimetypes = $mimetypes;
    }

    /**
     * Validate
     *
     * @param  TMUploadFileInfoInterface  $fileInfo
     * @throws TMUploadValidatorException           If validation fails
     */
    public function validate(TMUploadFileInfoInterface $fileInfo)
    {
        if (in_array($fileInfo->getMimetype(), $this->mimetypes) === false) {
            throw new TMUploadValidatorException(TM::t('taomee',
                'MIME不合法，必须是{mime}中其一！', array('{mime}' => implode(', ', $this->mimetypes))));
        }
    }
}

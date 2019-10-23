<?php
class TMUploadFileInfo extends SplFileInfo implements TMUploadFileInfoInterface
{
    /**
     * File name (without extension)
     * @var string
     */
    protected $name;

    /**
     * File extension (without dot prefix)
     * @var string
     */
    protected $extension;

    /**
     * File mimetype
     * @var string
     */
    protected $mimetype;

    /**
     * Constructor
     *
     * @param string $filePathname Absolute path to uploaded file on disk
     * @param string $newName      Desired file name (with extension) of uploaded file
     */
    public function __construct($filePathname, $newName = null)
    {
        // urlencode for Chinese
        $desiredName = urlencode(is_null($newName) ? $filePathname : $newName);
        $this->name = TMFileHelper::sanitizeFilename(urldecode(pathinfo($desiredName, PATHINFO_FILENAME)));
        $this->extension = strtolower(TMFileHelper::sanitizeFilename(urldecode(pathinfo($desiredName, PATHINFO_EXTENSION))));

        parent::__construct($filePathname);
    }

    /**
     * Get file name (without extension)
     *
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * Set file name (without extension)
     *
     * @param  string           $name
     * @return TMUploadFileInfo Self
     */
    public function setName($name)
    {
        $this->name = $name;

        return $this;
    }

    /**
     * Get file extension (without dot prefix)
     *
     * @return string
     */
    public function getExtension()
    {
        return $this->extension;
    }

    /**
     * Set file extension (without dot prefix)
     *
     * @param  string           $extension
     * @return TMUploadFileInfo Self
     */
    public function setExtension($extension)
    {
        $this->extension = $extension;

        return $this;
    }

    /**
     * Get file name with extension
     *
     * @return string
     */
    public function getNameWithExtension()
    {
        return $this->extension === '' ? $this->name : sprintf('%s.%s', $this->name, $this->extension);
    }

    /**
     * Get mimetype
     *
     * @return string
     */
    public function getMimetype()
    {
        if (isset($this->mimetype) === false) {
            if (extension_loaded('fileinfo') === true) {
                $finfo = new finfo(FILEINFO_MIME);
                $mimetype = $finfo->file($this->getPathname());
                $mimetypeParts = preg_split('/\s*[;,]\s*/', $mimetype);
                $this->mimetype = strtolower($mimetypeParts[0]);
                unset($finfo);
            } else {
                $this->mimetype = mime_content_type($this->getPathname());
            }
        }

        return $this->mimetype;
    }

    /**
     * Get md5
     *
     * @return string
     */
    public function getMd5()
    {
        return md5_file($this->getPathname());
    }

    /**
     * Get image dimensions
     *
     * @return array formatted array of dimensions
     */
    public function getDimensions()
    {
        list($width, $height) = getimagesize($this->getPathname());

        return array(
            'width' => $width,
            'height' => $height
        );
    }

    /**
     * Is this file uploaded with a POST request?
     *
     * This is a separate method so that it can be stubbed in unit tests to avoid
     * the hard dependency on the `is_uploaded_file` function.
     *
     * @return bool
     */
    public function isUploadedFile()
    {
        return is_uploaded_file($this->getPathname());
    }
}

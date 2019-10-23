<?php
class TMUploadFileSystem implements TMUploadStorageInterface
{
    /**
     * Path to upload destination directory (with trailing slash)
     * @var string
     */
    protected $directory;

    /**
     * Overwrite existing files?
     * @var bool
     */
    protected $overwrite;

    /**
     * Constructor
     *
     * @param  string                    $directory Relative or absolute path to upload directory
     * @param  bool                      $overwrite Should this overwrite existing files?
     * @throws TMUploadException                    If directory does not exist or not writable
     */
    public function __construct($directory, $overwrite = false)
    {
        if (!is_dir($directory)) {
            throw new TMUploadException(TM::t('taomee', '上传目录{dir}不存在！', array('dir' => $directory)));
        }
        if (!is_writable($directory)) {
            throw new TMUploadException(TM::t('taomee', '上传目录{dir}不可写！', array('dir' => $directory)));
        }
        $this->directory = rtrim($directory, '/') . DIRECTORY_SEPARATOR;
        $this->overwrite = (bool)$overwrite;
    }

    /**
     * Upload
     *
     * @param  TMUploadFileInfoInterface  $file The file object to upload
     * @throws TMUploadValidatorException       If overwrite is false and file already exists
     * @throws TMUploadException                If error moving file to destination
     */
    public function upload(TMUploadFileInfoInterface $fileInfo)
    {
        $destinationFile = $this->directory . $fileInfo->getNameWithExtension();
        if ($this->overwrite === false && file_exists($destinationFile) === true) {
            throw new TMUploadValidatorException(TM::t('taomee', '上传文件已存在！'));
        }

        if ($this->moveUploadedFile($fileInfo->getPathname(), $destinationFile) === false) {
            throw new TMUploadException(TM::t('taomee', '上传文件不能被移动到指定位置！'));
        }
    }

    /**
     * Move uploaded file
     *
     * @param  string $source      The source file
     * @param  string $destination The destination file
     * @return bool
     */
    protected function moveUploadedFile($source, $destination)
    {
        return move_uploaded_file($source, $destination);
    }
}

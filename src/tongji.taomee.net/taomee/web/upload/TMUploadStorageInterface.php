<?php
interface TMUploadStorageInterface
{
    /**
     * Upload file
     *
     * This method is responsible for uploading an `TMUploadFileInfoInterface` instance
     * to its intended destination. If upload fails, an exception should be thrown.
     *
     * @param  TMUploadFileInfoInterface $fileInfo
     * @throws TMException                         If upload fails
     */
    public function upload(TMUploadFileInfoInterface $fileInfo);
}

<?php
class tool_file_Selfhelp extends tool_File
{
    public $fileSource = self::SRC_SELFHELP;
    public $extensions = ['csv','tgz','zip','gz'];

    public function initFileKey($params, $prefix = [])
    {
        $this->fileKey = md5($params);

        return $this;
	}
	public function getFilePath()
	{
	    return TM::app()->getRuntimePath() . DIRECTORY_SEPARATOR . 'files';
	}


    public function notify($fileId)
    {
        $result = TMCurlHelper::fetch('http://192.168.71.57/lock-db/custom.php',
                                      array('f' => $fileId));
        /* $result = TMCurlHelper::fetch('http://192.168.71.57/lock-db/custom.php', ''); */
    }

    public function getFile($fileInfo)
    {
		$path = $this->getFilePath() . DIRECTORY_SEPARATOR . 'selfhelp';

        foreach ($this->extensions as $extension) {
            $file = $path . DIRECTORY_SEPARATOR . $fileInfo['file_id'] . '.' . $extension;
            if (file_exists($file)) {
                return $file;
            }
        }
        TMValidator::ensure(false, TM::t('tongji', '文件异常，请联系管理员！'));
    }
}

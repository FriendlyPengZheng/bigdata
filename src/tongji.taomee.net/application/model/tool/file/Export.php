<?php
class tool_file_Export extends tool_File
{
    public $rows;

    public $cols;

    public $pageCellLimit = 10000;

    public $extensions = ['csv', 'tgz', 'zip', 'gz'];

    protected $fileSource = self::SRC_EXPORT;

    public function isLarge()
    {
        return (int)$this->rows * (int)$this->cols > (int)$this->pageCellLimit;
    }

    public function findOne($status = self::ST_INIT)
    {
        $aInfo = $this->getDb()->createCommand()
            ->select('file_id,file_key,params')
            ->from($this->tableName())
            ->where('file_source=? AND status=?')
            ->queryRow([$this->fileSource, $status]);

        if ($aInfo) $aInfo['params'] = json_decode($aInfo['params'], true);

        return $aInfo;
    }

    public function getFilePath()
    {
        return TM::app()->getRuntimePath() . DIRECTORY_SEPARATOR . 'files';
    }

    public function initFileKey($params, $prefix = [])
    {
        $gpzs = (array)$params['gpzs_id'];
        $prefix[] = count($gpzs) === 1 ? $gpzs[0] : 0;

        $from = (array)$params['from'];
        $to = (array)$params['to'];
        $prefix[] = reset($from);
        $prefix[] = reset($to);

        $this->fileKey = implode('.', $prefix);

        return $this;
    }

    public function notify($fileId)
    {
        $console = TM::app()->getBasePath() . DIRECTORY_SEPARATOR . 'console' . DIRECTORY_SEPARATOR . 'index.php';
        $pidFile = TM::app()->getRuntimePath() . DIRECTORY_SEPARATOR . 'pid';
        TMFileHelper::mkdir($pidFile);
        $pidFile .= DIRECTORY_SEPARATOR . 'export.pid';
        pclose(popen("/usr/bin/php $console export start --pidFile=$pidFile", 'r'));
    }

    public function getFile($fileInfo)
    {
        $path = $this->getFilePath();
        foreach ($this->extensions as $extension) {
            $file = $path . DIRECTORY_SEPARATOR . $fileInfo['file_key'] . '.' . $extension;
            if (file_exists($file)) return $file;
        }

        TMValidator::ensure(false, TM::t('tongji', '文件异常，请联系管理员！'));
    }
}

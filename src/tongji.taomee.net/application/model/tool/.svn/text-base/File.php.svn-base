<?php
abstract class tool_File extends TMFormModel
{
    /**
     * Status contants
     */
    const ST_INIT = 0;
    const ST_GEN = 1;
    const ST_DONE = 2;
    const ST_ERR = 3;
    const ST_REMOVE = 4;

    /**
     * File source contants
     */
    const SRC_SELFHELP = 1;
    const SRC_EXPORT = 2;

    /**
     * @var string Name of the file
     */
    protected $fileName;

    /**
     * @var string The file's unique key
     */
    protected $fileKey;

    /**
     * @var string The file source
     */
    protected $fileSource;

    /**
     * @var string
     */
    protected $params;

    public static function createFile($fileSrc, $props = [])
    {
        switch ($fileSrc) {
            case self::SRC_SELFHELP:
                $file = new tool_file_Selfhelp();
                break;

            case self::SRC_EXPORT:
            default:
                $file = new tool_file_Export();
                break;
        }

        if ($props && is_array($props)) {
            foreach ($props as $k => $v) {
                if (property_exists($file, $k)) {
                    $file->$k = $v;
                }
            }
        }
        return $file;
    }

    public function tableName()
    {
        return 't_web_file';
    }

    public function getFileKey()
    {
        return $this->fileKey;
    }

    public function isLarge()
    {
        return true;
    }

    public function findByFileKey($fileKey)
    {
        return $this->getDb()->createCommand()
            ->select('file_id,file_name,file_key,status,message')
            ->from($this->tableName())
            ->where('file_source=? AND file_key=?')
            ->queryRow([$this->fileSource, $fileKey]);
    }

    public function findByFileId($fileId)
    {
        return $this->getDb()->createCommand()
            ->select('file_id,file_name,file_key,params,status,message')
            ->from($this->tableName())
            ->where('file_source=? AND file_id=?')
            ->queryRow([$this->fileSource, $fileId]);
    }

    public function setFileName($fileName)
    {
        $this->fileName = $fileName;

        return $this;
    }

    public function setParams($params)
    {
        $this->params = json_encode($params);
        return $this;
    }

    public function getParams()
    {
        return $this->params;
    }

    public function beforeInsert()
    {
        if (isset($this->fileName)) {
            $this->file_name = $this->fileName;
        }

        if (isset($this->fileKey)) {
            $this->file_key = $this->fileKey;
        }

        if (isset($this->fileSource)) {
            $this->file_source = $this->fileSource;
        }

        return true;
    }

    abstract public function initFileKey($params, $prefix = []);
    abstract public function notify($fileId);
    abstract public function getFile($fileInfo);
}

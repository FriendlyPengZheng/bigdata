<?php
class tool_UserFile extends TMFormModel
{
    const EXPORT = 0;
    const SUCCESS = 1;
    const EXISTS = 2;

    protected $route = 'tool/file/export';

    public function tableName()
    {
        return 't_web_user_file';
    }

    public function addFile(tool_File $file, $userInfo)    // append a new file record
    {
        if (($fileKey = $file->getFileKey()) === null) {
            throw new TMException(TM::t('tongji', '文件KEY未被初始化！'));
        }

        $fileInfo = $file->findByFileKey($fileKey);
        if ($fileInfo) {
            return $this->dispatch($file, $fileInfo, $userInfo);
        } else {
            $file->file_id = null;
            $file->insert();
            $this->addMap($file->file_id, $userInfo);
            $file->notify($this->file_id);
            return ['code' => static::SUCCESS];
        }
    }

    protected function mapExists($fileId, $userId)
    {
        $map = $this->findAll([
            'condition' => [
                'file_id' => $fileId,
                'user_id' => $userId
            ]
        ]);

        if ($map) return $map[0];

        return false;
    }

    protected function addMap($fileId, $userInfo)
    {
        $this->file_id = $fileId;
        $this->user_id = $userInfo['user_id'];
        $this->user_name = $userInfo['user_name'];
        $this->add_time = date('Y-m-d H:i:s');
        $this->insert();
    }

    protected function dispatch($file, $fileInfo, $userInfo)
    {
        $map = $this->mapExists($fileInfo['file_id'], $userInfo['user_id']);

        switch ($fileInfo['status']) {
            case tool_File::ST_INIT:
            case tool_File::ST_GEN:
                $code = static::SUCCESS;
                if ($map === false) {
                    $this->addMap($fileInfo['file_id'], $userInfo);
                } else {
                    $code = static::EXISTS;
                }
                return ['code' => $code];
                break;

            case tool_File::ST_DONE:
                if ($map === false) $this->addMap($fileInfo['file_id'], $userInfo);
                return $this->prepareFile($file, $fileInfo);
                break;

            case tool_File::ST_ERR:
            case tool_File::ST_REMOVE:
            default:
                if ($map === false) $this->addMap($fileInfo['file_id'], $userInfo);
                TMValidator::ensure(
                    false,
                    $fileInfo['message']
                        ? $fileInfo['message']
                        : TM::t('tongji', '生成文件时出现错误,或者文件已经被删除')
                );
                break;
        }
    }

    public function prepareFile($file, $fileInfo)
    {
        return [
            'code' => static::EXPORT,
            'url' => TM::app()->getUrlManager()->rebuildUrl(
                $this->route,
                [
                    'ajax' => 1,
                    'mark' => $this->markFile($file->getFile($fileInfo), $fileInfo['file_name'])
                ]
            )
        ];
    }

    protected function markFile($filePath, $fileName)
    {
        $mark = md5(uniqid(mt_rand(), true));

        TM::app()->session->add(
            $mark,
            [
                'path' => $filePath,
                'name' => $fileName
            ]
        );

        return $mark;
    }

    public function sendFile($mark)
    {
        $file = TM::app()->session->get($mark);
        if ($file && isset($file['path'], $file['name'])) {
            TMFileHelper::sendFile(
                $file['path'],
                $file['name'] . '.' . pathinfo($file['path'], PATHINFO_EXTENSION),
                true
            );
        } else {
            echo 'File not found!';
            exit(0);
        }
    }

    public function findUserFile($userId, $fileIds = [])
    {
        $command = $this->getDb()->createCommand()
            ->select('u.add_time,f.file_id,f.file_name,f.file_key,f.file_source,f.progress,f.status,f.message')
            ->from($this->tableName() . ' u')
            ->join((new tool_file_Export())->tableName() . ' f', 'u.file_id=f.file_id')
            ->where('u.user_id=?')
            ->order('u.add_time DESC');

        if ($fileIds) {
            $command->andWhere(['IN', 'f.file_id', $fileIds]);
        }
        $fileList = $command->queryAll([$userId]);
        $queue = $this->queryQueue();
        foreach($fileList as $k => &$file) {
            if((int)$file['status'] !== tool_File::ST_REMOVE) {
                $file = array_merge($file, array('queue_num' => (string)count($queue)));
                continue;
            }
            unset($fileList[$k]);
        }
        return $fileList;
    }

    public function deleteUserFile($fileId)
    {
        return $this->getDb()->createCommand()->update(
                (new tool_file_Selfhelp())->tableName(),
                array('status' => tool_File::ST_REMOVE),
                'file_id = :file_id',
                array(':file_id' => $fileId)
        ) !== 0;
    }

    public function queryQueue()
    {
        return $this->getDb()->createCommand()
            ->select('file_id,file_name,file_source,progress,status,message')
            ->from((new tool_file_Export())->tableName())
            ->where('status=1 AND file_source=1')
            ->queryAll();
    }
}

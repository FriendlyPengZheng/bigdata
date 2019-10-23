<?php
class File extends TMController
{
    public function actions()
    {
        return [
            'index' => [],
            'get' => ['file_source' => null, 'file_id' => null],
            'export' => ['mark' => null],
            'del' => ['file_id' => null],
            'refresh' => ['file_id' => null]
        ];
    }

    public function index($aUserParameters)
    {
        $this->assign(
            'file_list',
            (new tool_UserFile())->findUserFile(TM::app()->getUser()->getUserId())
        );

        $this->display('tool/file.html');
    }

    public function get($aUserParameters)
    {
        $userFile = new tool_UserFile();
        $map = $userFile->findAll([
            'condition' => [
                'file_id' => $aUserParameters['file_id'],
                'user_id' => TM::app()->getUser()->getUserId()
            ]
        ]);
        TMValidator::ensure($map, TM::t('tongji', '文件不存在！'));

        $file = tool_File::createFile($aUserParameters['file_source']);
        $fileInfo = $file->findAll([
            'condition' => [
                'file_id' => $aUserParameters['file_id']
            ]
        ]);
        TMValidator::ensure($fileInfo, TM::t('tongji', '文件不存在！'));

        $fileInfo = $fileInfo[0];
        if ($fileInfo['status'] == tool_File::ST_ERR) {
            TMValidator::ensure(false, $fileInfo['message']);
        } elseif ($fileInfo['status'] != tool_File::ST_DONE) {
            TMValidator::ensure(false, TM::t('tongji', '文件正在处理中，请稍候！'));
        } elseif ($fileInfo['status'] == tool_File::ST_REMOVE) {
            TMValidator::ensure(false, TM::t('tongji', '文件已经被删除,无法获取该文件'));
        }

        $this->ajax(0, $userFile->prepareFile($file, $fileInfo));
    }

    public function export($aUserParameters)
    {
        (new tool_UserFile())->sendFile($aUserParameters['mark']);
    }

    public function del($aUserParameters)
    {
        $this->ajax(
            0, (new tool_UserFile())->deleteUserFile($aUserParameters['file_id'])
            // (new tool_UserFile())->deleteAllByAttributes([
            //     'user_id' => TM::app()->getUser()->getUserId(),
            //     'file_id' => $aUserParameters['file_id']
            // ])
        );
    }

    public function refresh($aUserParameters)
    {
        $fileInfo = (new tool_UserFile())->findUserFile(
                TM::app()->getUser()->getUserId(),
                $aUserParameters['file_id']);
        $this->ajax(0, $fileInfo);
    }
}

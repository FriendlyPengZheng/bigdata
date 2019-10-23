<?php
class garbled_EncodeLog extends TMFormModel
{
    public function tableName()
    {
        return 't_web_encode_log';
    }

    public function beforeInsert()
    {
        $this->time = time();
        return true;
    }
}

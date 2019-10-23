<?php
class common_Comment extends TMFormModel
{
    public function tableName()
    {
        return 't_web_comment';
    }

    public function rules()
    {
        return array(
            array('keyword', 'string', 'max' => 64, 'min' => 1),
            array('keyword', 'unique', 'exclude' => array('comment_id' => $this->comment_id)),
            array('comment', 'string', 'min' => 1)
        );
    }

    public function findByKeyword()
    {
        return $this->getDb()->createCommand()
            ->select($this->attributes())
            ->from($this->tableName())
            ->where('keyword LIKE ?')
            ->queryAll(array('%' . $this->keyword . '%'));
    }

    public function beforeDelete()
    {
        (new module_ComponentComment())->deleteAllByAttributes(array('comment_id' => $this->comment_id));
        (new common_BasicData())->updateByCommentId($this->comment_id, array('comment_id' => 0));
        return true;
    }
}

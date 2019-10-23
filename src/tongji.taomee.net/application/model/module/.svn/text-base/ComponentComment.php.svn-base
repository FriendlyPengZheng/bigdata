<?php
class module_ComponentComment extends TMFormModel
{
    public function tableName()
    {
        return 't_web_component_comment';
    }

    public function rules()
    {
        return array(
            array('comment_id', 'checkCommentId')
        );
    }

    public function getCommentsByComponentId()
    {
        return $this->getDb()->createCommand(
            'SELECT c.*,cc.display_order ' .
            'FROM ' . $this->tableName() . ' cc ' .
            'INNER JOIN ' . (new common_Comment())->tableName() . ' c ' .
            'ON cc.comment_id = c.comment_id ' .
            'WHERE cc.component_id = ?'
        )->queryAll(array($this->component_id));
    }

    public function checkCommentId($value, $attribute, $params)
    {
        $oComment = new common_Comment();
        $aComment = $oComment->findAll(array('condition' => array('comment_id' => $value)));
        if (!$aComment) return false;
        return true;
    }
}

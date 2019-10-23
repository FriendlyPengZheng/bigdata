<?php
class Comment extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(),
            'getComments' => array(
                'fetch_type' => 3, // 1-关键词精确匹配 2-关键词模糊匹配 3-所有
                'keyword'    => null
            ),
            'getComponentComments' => array(
                'component_id' => null
            ),
            'save' => array(
                'comment_id' => null,
                'keyword' => null,
                'comment' => null
            ),
            'delete' => array(
                'comment_id' => null
            )
        );
    }

    public function index($aUserParameters)
    {
        $model = new common_Comment();
        $this->assign('comments', $model->findAll());
        $this->display('conf/comment.html');
    }

    public function getComments($aUserParameters)
    {
        $model = new common_Comment();
        switch ($aUserParameters['fetch_type']) {
            case '1': // 关键词精确匹配
                $this->ajax(0, $model->findAll(array('condition' => array('keyword' => $aUserParameters['keyword']))));
                break;

            case '2': // 关键词模糊匹配
                $model->keyword = $aUserParameters['keyword'];
                $this->ajax(0, $model->findByKeyword());
                break;

            case '3': // 所有
            default:
                $this->ajax(0, $model->findAll());
                break;
        }
    }

    public function getComponentComments($aUserParameters)
    {
        $model = new module_ComponentComment();
        $model->component_id = $aUserParameters['component_id'];
        $comments = $model->getCommentsByComponentId();
        foreach ($comments as &$comment) {
            $comment['keyword'] = TM::t('tongji', $comment['keyword']);
            $comment['comment'] = TM::t('tongji', $comment['comment']);
        }
        $this->ajax(0, $comments);
    }

    public function save($aUserParameters)
    {
        $aUserParameters['comment_id'] = (int)$aUserParameters['comment_id'];
        $model = new common_Comment();
        $model->attributes = $aUserParameters;
        if ($model->comment_id) {
            $model->update();
        } else {
            $model->comment_id = null;
            $model->insert();
        }
        $this->ajax(0, array('comment_id' => $model->comment_id));
    }

    public function delete($aUserParameters)
    {
        $model = new common_Comment();
        $model->comment_id = (int)$aUserParameters['comment_id'];
        $this->ajax(0, $model->delete());
    }
}

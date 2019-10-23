<?php
class garbled_TreeEncodeCheck extends TMModel
{
    protected $garbledItems = array();

    /**
     * @brief getGarbled 
     * 获取本次检测处的乱码
     *
     * @return {array}
     */
    public function getGarbled()
    {
        return $this->garbledItems;
    }

    /**
     * @brief check 
     * 检测未检测的树节点名称，将乱码标志hide改为self::HIDE_AUTO_HIDE，并记入log
     *
     * @param {integer} $gameId
     *
     * @return {TreeEncodeCheck}
     */
    public function check($gameId)
    {
        $oTree = new common_Tree();
        $oTree->status = common_Tree::STATUS_CHECKED;

        $aTreeNodes = $oTree->findAll(array(
            'condition' => array(
                'game_id' => $gameId,
                'status' => common_Tree::STATUS_NOT_CHECKED,
                'hide' => common_Tree::HIDE_NOT_HIDE
            )
        ));

        $log = new garbled_EncodeLog();
        $log->table_name = $oTree->tableName();

        foreach ($aTreeNodes as $node) {
            $oTree->node_id = $node['node_id'];
            try {
                TMEncode::isUTF8String($node['node_name']);
                TMEncode::isSimpleChinese($node['node_name']);
                $oTree->update(array('status'));
            } catch (TMEncodeException $e) {
                // logger in database
                $log->encode_log_id = '';
                $log->to_hide_id = $node['node_id'];
                $log->garbled = $node['node_name'];
                $log->insert();

                // update hide, and status in common_Tree
                $oTree->hide = common_Tree::HIDE_AUTO_HIDE;
                $oTree->update(array('hide', 'status'));

                // store the garbled in memory
                $this->garbledItems[] = array(
                    'garbled' => $node['node_name'],
                    'id' => $node['node_id'],
                    'error' => $e->getMessage()
                );
            }
        }
        return $this;
    }
}

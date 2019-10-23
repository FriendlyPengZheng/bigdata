<?php
class common_Tree extends TMFormModel
{
    /**
     * 标志状态，已检测过
     */
    const STATUS_CHECKED = 1;

    /**
     * 标志状态，未检测
     */
    const STATUS_NOT_CHECKED = 0;

    /**
     * 隐藏标志，未隐藏
     */
    const HIDE_NOT_HIDE = 0;

    /**
     * 隐藏标志，已隐藏
     */
    const HIDE_TO_HIDE = 1;

    /**
     * 隐藏标志，自动隐藏
     */
    const HIDE_AUTO_HIDE = 2;

    /**
     * 基础数据标志，是基础数据
     */
    const BASIC_IS_BASIC = 1;

    /**
     * 基础数据标志，不是基础数据
     */
    const BASIC_NOT_BASIC = 0;

    /**
     * 叶子节点标志，是叶子节点
     */
    const LEAF_IS_LEAF = 1;

    /**
     * 叶子节点标志，不是叶子节点
     */
    const LEAF_NOT_LEAF = 0;

    /**
     * @brief tableName 
     * 返回表名
     *
     * @return {string}
     */
    public function tableName()
    {
        return 't_web_tree';
    }
}

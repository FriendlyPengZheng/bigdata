<?php
class common_ReportInfo extends TMFormModel
{
    /**
     * 标志状态，已检测过
     */
    const FLAG_CHECKED = 1;

    /**
     * 标志状态，未检测
     */
    const FLAG_NOT_CHECKED = 0;

    /**
     * 隐藏标志，未隐藏
     */
    const STATUS_NOT_HIDE = 0;

    /**
     * 隐藏标志，已隐藏
     */
    const STATUS_TO_HIDE = 1;

    /**
     * 隐藏标志，自动隐藏
     */
    const STATUS_AUTO_HIDE = 2;

    /**
     * @brief tableName 
     * 返回表名
     *
     * @return {string}
     */
    public function tableName()
    {
        return 't_report_info';
    }

    /**
     * @brief getGarbledInfo 
     * 获取乱码的信息
     *
     * @return {array}
     */
    public function getGarbledInfo($garbled, $gameId)
    {
        return $this->getDb()->createCommand()->select('stid, sstid')
            ->from($this->tableName())
            ->where('game_id = ?')
            ->andWhere('stid = ? OR sstid = ?')
            ->queryAll(array($gameId, $garbled, $garbled));
    }
}

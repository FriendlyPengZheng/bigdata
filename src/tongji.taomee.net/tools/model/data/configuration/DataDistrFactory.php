<?php
class data_configuration_DataDistrFactory extends data_configuration_DistrFactory
{
    /* Data分布参数 */
    public $game_id;
    public $gpzs_id;
    public $data_info;
    public $by_item = 1;     // 可不设置，默认值1
    public $exprs = array(); // 可不设置，默认空数组

    /**
     * 生成分布
     * @param array $aUserParam
     * @return null|array
     */
    public function createDistr($aUserParam)
    {
        // 参数检查
        if (!$this->isValid()) {
            return;
        }

        $builder = new data_configuration_ConfigurationBuilder();
        $configurationSet = $builder->build($this->by_item, $this->data_info, $this->gpzs_id, $this->game_id);
        $distr = $this->newDistrInstance($configurationSet);
        if ($this->exprs) {
            $distr->setExpressions($this->exprs);
        }
        return $distr;
    }

    /**
     * 检测生成器配置
     * @return boolean
     */
    protected function isValid()
    {
        if (!$this->issetProperties(array('game_id', 'gpzs_id', 'by_item', 'data_info', 'exprs'))) {
            return false;
        }
        return true;
    }
}

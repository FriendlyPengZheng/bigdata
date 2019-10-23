<?php
class gamecustom_CustomMsgidInfo extends TMFormModel
{
    public $errors = [];

    /*
    * compute type
    */
    const COULD_REVISE = 'unused';
    const NOT_REVISE = 'used';

    /**
     * Table name for this model.
     * @return string
     */
    public function tableName()
    {
        return 't_custom_msgid_info';
    }

    /**
     * 获取参数检测的配置
     *
     * @return array
     */
    public function rules()
    {
        return array(
            array('id, game_id, first, second', 'required'),
            array('id', 'number', 'integerOnly' => true),
            array('status', 'default', 'value' => 'unused'),
            array('status', 'enum', 'range' => array('unused', 'used')),
            array('type', 'enum', 'range' => array(1, 2, 3), 'defaultValue' => 1)
        );
    }

    /**
     * Replace the item.
     *
     * @param  array   $aFields Item info
     * @return integer          Effect rows
     */
    public function replace($aFields)
    {
        $this->check($aFields);
        $this->attributes = $aFields;
        $info = $this->exists($aFields);
        $addTime = strtotime($info['add_time']);
        if (isset($info) && strtotime('-5 day') <= $addTime) { 
            if ($info['status'] !== 'used') {
                $info['id'] = $aFields['id'];
                $this->errors[] = $info;
                TM::app()->getLog()->log(sprintf("id=%s\tgameId=%s\tfirst=%s\tsecond=%s\tthird=%s",
                    $info['id'], $info['game_id'], $info['first'], $info['second'], $info['third']
                    ), TMLog::TYPE_INFO);
                throw new Exception('the info is repeat');
            }
        } else if(!isset($info)) {
            $this->insert();
        }
    }

    public function getErrors()
    {
        return $this->errors;
    }

    /**
     * @brief check
     * @param {array} $aParam
     * @return
     */
    protected function check($aParam)
    {
        switch ($aParam['type']) {
            case 1 :
                TMValidator::ensure($aParam['fourth'] === null || empty($aParam['fourth']),
                                    TM::t('tongji', 'id为{0}条有错, type为{1}, 不能有fourth',
                                    array('{0}' => $aParam['id'], '{1}' => $aParam['type']))
                            );
                break;
            case 2:
            case 3:
                TMValidator::ensure($aParam['third'] !== null && !empty($aParam['third']),
                                    TM::t('tongji', 'id为{0}条有错, type为{1}, 一定要有third',
                                    array('{0}' => $aParam['id'], '{1}' => $aParam['type']))
                           );
                break;
            default:
        }
    }

    /**
     * 数据是否存在
     * @param array $aUserParam
     * @return array the exist item info
     * @throw TMValidatorException if not exist
     */
    protected function exists($aUserParam)
    {
        $aInfo1 = $this->findAll(array(
            'condition' => array(
                'id' => $aUserParam['id']
            )
        ));
        $aInfo2 = $this->findAll(array(
            'condition' => array(
                //'id' => $aUserParam['id'],
                'game_id' => $aUserParam['game_id'],
                'first' => $aUserParam['first'],
                'second' => $aUserParam['second'],
                'third' => $aUserParam['third']
            )
        ));
        if (isset($aInfo1[0])) return $aInfo1[0];
        if (isset($aInfo2[0])) return $aInfo2[0];
        return null;
    }

    /**
     * @brief checkArrayIsEqual
     * 比较数组是否相同($aBase 中的选项是否在$aContrast存在)
     * @param $aBase
     * @param $aContrast
     *
     * @return
     */
    protected function checkArrayIsEqual($aBase, $aContrast)
    {
        foreach ($aBase as $k => $v) {
            if ($v !== $aContrast[$k]) return false;
        }
        return true;
    }
}

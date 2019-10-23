<?php
class module_SetData extends TMFormModel
{
    public function tableName()
    {
        return 't_web_set_data';
    }

    public function getList()
    {
        return $this->getDb()->createCommand()
            ->select('CONCAT_WS(":",`data_id`,`data_expr`) AS `id`,`data_name` AS `name`,' .
                '`data_name`,`data_id`,`data_expr`,`factor`,`precision`,`unit`')
            ->from($this->tableName())
            ->where('set_id=?')
            ->order('data_name')
            ->queryAll(array($this->set_id));
    }

    public function formatList($list)
    {
        foreach ($list as &$info) {
            $info['name'] = $info['data_name'] = TM::t('tongji', $info['data_name']);
            $info['unit'] = TM::t('tongji', $info['unit']);
        }

        return $list;
    }

    public function replace()
    {
        $fields = array();
        foreach ($this->attributeNames() as $attr) {
            if (isset($this->$attr)) {
                $fields[$attr] = $this->$attr;
            }
        }
        if (!$fields) return 0;
        return $this->getDb()->createCommand()->replace(
            $this->tableName(),
            $fields
        );
    }
}

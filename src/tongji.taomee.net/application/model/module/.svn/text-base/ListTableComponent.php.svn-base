<?php
class module_ListTableComponent extends module_Component
{
    /**
     * Rules for attributes of this model.
     * @return array rules, one rule one element.
     */
    public function rules()
    {
        $rules = parent::rules();
        return array_merge($rules, array(
            array('urlPage,urlExtend,renameUrl,urlPagination', 'string', 'allowEmpty' => true, 'max' => 128),
            array('thead_type', 'checkAttr', 'value' => $this->thead_title),
            array('isAjax,enablePagination', 'enum', 'range' => array(0, 1))
        ));
    }

    /**
     * Return all names of attributes.
     * @return array
     */
    public function attributeNames()
    {
        $names = parent::attributeNames();
        return array_merge($names, array_keys($this->parameters()));
    }

    /**
     * Return the special parameters for the current component.
     * @return array
     */
    protected function parameters()
    {
        return array(
            'urlExtend'     => '',
            'urlPage'       => '',
            'urlPagination' => '',
            'renameUrl'     => '',
            'isAjax'        => 1,
            'enablePagination' => 0,
            'thead_type'    => array(),
            'thead_title'   => array(),
            'appendColumns' => array()
        );
    }

    /**
     * Doing something before insert a record.
     * @return boolean If false, the inserting action will be stopped.
     */
    public function beforeInsert()
    {
        $this->encodeProperties();
        return true;
    }

    /**
     * Doing something before update a record by primary key.
     * @return boolean If false, the updating action will be stopped.
     */
    public function beforeUpdate()
    {
        $this->encodeProperties();
        return true;
    }

    /**
     * Build a component to the form of configuration.
     * @param array $component
     * @return array
     */
    public function build($component)
    {
        $component = parent::build($component);
        $component['url'] = array(
            'page'   => $component['urlPage'],
            'extend' => $component['urlExtend'],
            'paginationUrl' => $component['urlPagination']
        );
        $this->getBuilder()->addExprsWrapData(array(array(
            'common' => $component['urlExtend'],
            'data'   => ''
        )));
        unset($component['urlPage'], $component['urlExtend'], $component['urlPagination']);
        $component['thead']  = $this->buildThead($component);
        $component['isAjax'] = (bool)$component['isAjax'];
        $component['pagination'] = (bool)$component['enablePagination'];
        $component['appendColumns'] = $this->buildAppendColumns($component);
        return $component;
    }

    /**
     * @brief buildAppendColumns
     * @param  array $component
     * @return array
     */
    protected function buildAppendColumns($component)
    {
        $appendColumns = array();
        foreach ($component['appendColumns'] as $key => $val) {
            if ($val['isFn']) {
                $appendColumns[] = $val['fn'];
                continue;
            }
            $appendColumns[] = array(
                'type' => $val['type'],
                'key' => $val['key'],
                'isID' => TMArrayHelper::assoc('isID', $val, 0)
            );
        }
        return $appendColumns;
    }

    /**
     * Build thead.
     * @param array $component
     * @return array
     */
    protected function buildThead($component)
    {
        $thead = array();
        if ($component['thead_type']) {
            foreach ($component['thead_type'] as $key => $type) {
                $thead[] = array(
                    'type'  => $type,
                    'title' => TM::t('tongji', $component['thead_title'][$key])
                );
            }
        }
        unset($component['thead_type'], $component['thead_title']);
        return $thead;
    }
}

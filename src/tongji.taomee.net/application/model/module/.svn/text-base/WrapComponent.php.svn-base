<?php
class module_WrapComponent extends module_Component
{
    /**
     * Init the property ignore
     */
    public function init()
    {
        $this->componentIgnore = new module_ComponentWithIgnore();
        $this->componentIgnore->setComponent($this);
        $this->componentTitle = new module_ComponentWithTitle();
        $this->componentTitle->setComponent($this);
    }

    /**
     * Rules for attributes of this model.
     * @return array rules, one rule one element.
     */
    public function rules()
    {
        $rules = parent::rules();
        return array_merge($rules, array(
            array('title', 'string', 'max' => 128, 'min' => 1),
            array('ignore,headEnabled,bottomEnabled,isSelControl', 'enum', 'range' => array(0, 1)),
            array('ignoreId', 'number', 'integerOnly' => true, 'allowEmpty' => true, 'max' => PHP_INT_MAX, 'min' => 0),
            array('width', 'number', 'integerOnly' => true, 'max' => 100, 'min' => 1),
            array('remove,edit,download,favor,comment,heatmap,renameUrl,nameListUrl,ignoreTools', 'string', 'allowEmpty' => true, 'max' => 128),
            array('attr_key', 'checkAttr', 'value' => $this->attr_value),
            array('process_type', 'enum', 'range' => array('expres', 'distr')),
            array('process_list', 'checkProcessList', 'process_type' => $this->process_type, 'data_info' => $this->data_info)
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
            'title'         => '',
            'ignore'        => 0,
            'ignoreId'      => '',
            'headEnabled'   => 0,
            'headTime'      => array(),
            'condition'     => array(),
            'bottomEnabled' => 0,
            'width'         => 50,
            'remove'        => '',
            'nameListUrl'   => '',
            'renameUrl'     => '',
            'edit'          => '',
            'download'      => '',
            'ignoreTools'   => '',
            'favor'         => '',
            'comment'       => '',
            'heatmap'       => '',
            'attr_key'      => array(),
            'attr_value'    => array(),
            'isSelControl'  => 0,
            'selControl'    => array(),
            /* bound metadatas */
            'process_type'  => 'expres',
            'process_list'  => array(),
            'data_info'     => array()
        );
    }

    /**
     * Check the process list, which contains the metadatas for every child of the wrap component.
     * @param array $value
     * @param string Name of the attribute, that is 'process_list'
     * @param string $params Contains key 'process_type'
     * @return boolean
     */
    public function checkProcessList($value, $attribute, $params)
    {
        if (!is_array($value) || !is_array($params['data_info'])) {
            return false;
        }
        $dataCnt = count($params['data_info']);
        foreach ($value as $each) {
            if (!isset($each['data'])) {
                return false;
            }
            foreach (explode(',', $each['data']) as $idx) {
                if ($idx > $dataCnt) {
                    return false;
                }
            }
            if ($params['process_type'] === 'expres') {
                if (!isset($each['data_name'], $each['expre'], $each['precision'], $each['unit'])) {
                    return false;
                }
            } elseif (!isset($each['dimen_name'], $each['distr_name'], $each['distr_type'], $each['sort_type'], $each['distr_by'])) {
                return false;
            }
        }
        return true;
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
        $component['title'] = TM::t('tongji', $component['title']);

        $this->getBuilder()->pushBasicData(array(
            'process_type' => $component['process_type'],
            'process_list' => $component['process_list'],
            'data_info' => $component['data_info']
        ));
        unset($component['process_type'], $component['process_list'], $component['data_info']);

        if(isset($component['renameUrl']) && $component['nameListUrl']){
            $component['rename'] = array(
                'renameUrl' => $component['renameUrl'],
                'nameListUrl' => $component['nameListUrl']);
            unset($component['renameUrl'], $component['nameListUrl']);
        }
        $component['width'] /= 100;
        $component['ignore'] = (bool)$component['ignore'];
        $component['headEnabled'] = (bool)$component['headEnabled'];
        $component['bottomEnabled'] = (bool)$component['bottomEnabled'];

        return $this->buildHeadTime($this->buildSelControl($this->buildAttr($component)));
    }

    /**
     * buildHeadTime
     * @param array $component
     * @return array
     */
    protected function buildHeadTime($component)
    {
        if (isset($component['headTime'])) {
            if ($component['headTime']) {
                $temp = array();
                $conf = data_time_PeriodFactory::getPeriodConfigure();
                foreach ($component['headTime'] as $period) {
                    if (isset($conf[$period])) {
                        $temp[] = array('title' => $conf[$period], 'dataId' => $period);
                    }
                }
                $component['headTime'] = $temp;
            } else {
                unset($component['headTime']);
            }
        }

        return $component;
    }

    /**
     * buildSelControl
     * @param array $component
     * @return array
     */
    protected function buildSelControl($component)
    {
        if (!isset($component['isSelControl'], $component['selControl']) ||
                !$component['isSelControl'] ||
                !$component['selControl']) {
            $component['isSelControl'] = false;
            unset($component['selControl']);
            return $component;
        }
        $component['isSelControl'] = true;
        $component['selControl']['isMatch'] = (bool)$component['selControl']['isMatch'];
        $component['selControl']['isMultiple'] = (bool)TMArrayHelper::assoc('isMultiple', $component['selControl'], false);

        foreach ($component['selControl']['selConfig'] as &$config) {
            $config['titlePre'] = TM::t('tongji', $config['titlePre']);
            $config['titleSuf'] = TM::t('tongji', $config['titleSuf']);
            $config['data'] = $this->buildSelControlData($config);
            $config['url']['page'] = $config['urlPage'];
            $config['url']['extend'] = $config['urlExtend'];
            $config['isAjax'] = (bool)$config['isAjax'];
            unset($config['control_id'], $config['control_name'], $config['urlPage'], $config['urlExtend']);
        }
        $component['selControl']['config'] = $component['selControl']['selConfig'];
        unset($component['selControl']['selConfig']);

        return $component;
    }

    /**
     * buildSelControlData
     * @param array $component
     * @return array
     */
    protected function buildSelControlData($component)
    {
        $data = array();
        if ($component['control_id']) {
            foreach ($component['control_id'] as $key => $value) {
                $data[] = array(
                    'id' => $value,
                    'name' => TM::t('tongji', $component['control_name'][$key])
                );
            }
        }
        return $data;
    }
}

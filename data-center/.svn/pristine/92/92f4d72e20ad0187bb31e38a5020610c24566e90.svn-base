<?php
class module_Component extends TMFormModel
{
    /**
     * @var module_ComponentIgnore
     */
    public $componentIgnore;

    /**
     * @var module_ComponentTitle
     */
    public $componentTitle;

    /**
     * @var module_ModuleBuilder
     */
    private $_builder;

    /**
     * Init the property ignore
     */
    public function init()
    {
        $this->componentIgnore = new module_ComponentWithoutIgnore();
        $this->componentTitle  = new module_ComponentWithoutTitle();
        $this->componentIgnore->setComponent($this);
        $this->componentTitle->setComponent($this);
    }

    /**
     * Get table for this model.
     * @return string the name of table.
     */
    public function tableName()
    {
        return 't_web_component';
    }

    /**
     * Rules for attributes of this model.
     * @return array rules, one rule one element.
     */
    public function rules()
    {
        return array(
            array('component_type', 'enum', 'range' => array('wrap', 'tabs', 'tab', 'data', 'listtable')),
            array('parent_id', 'checkExists', 'allow' => 0)
        );
    }

    /**
     * Return all names of attributes.
     * @return array
     */
    public function attributeNames()
    {
        return array('component_id', 'component_type', 'properties', 'parent_id', 'module_key', 'ignore_id', 'component_desc');
    }

    /**
     * Return all attributes of this model.
     * @return string
     */
    public function attributes()
    {
        return 'component_id,component_type,properties,parent_id,module_key,ignore_id,component_desc';
    }

    /**
     * Find components by module key, sometimes with parent id.
     * @param string $moduleKey
     * @param integer $parentId
     * @param boolean $decodeProperties
     * @return array
     */
    public function findByModuleKey($moduleKey, $parentId = null, $decodeProperties = true)
    {
        $params = array(
            'condition' => array(
                'module_key' => $moduleKey,
                'hidden' => 0
            ),
            'order' => 'display_order,component_id'
        );
        if (isset($parentId)) {
            $params['condition']['parent_id'] = $parentId;
        }
        if (!$decodeProperties) {
            return $this->findAll($params);
        }
        return $this->decodeProperties($this->findAll($params));
    }

    /**
     * Find component by component id.
     * @param integer $componentId
     * @param boolean $decodeProperties
     * @return array
     */
    public function findById($componentId, $decodeProperties = true)
    {
        $params = array(
            'condition' => array(
                'component_id' => $componentId,
                'hidden' => 0
            ),
            'order' => 'display_order'
        );
        if (!$decodeProperties) {
            return $this->findAll($params);
        }
        return $this->decodeProperties($this->findAll($params));
    }

    /**
     * Get the special parameters for the current component.
     * @return array
     */
    public function getParameters()
    {
        return $this->parameters();
    }

    /**
     * Check array attributes.
     * @param array $value Value of attributes.
     * @param string $attribute Name of attributes.
     * @param array $params Extra params.
     * @return boolean
     */
    public function checkAttr($value, $attribute, $params)
    {
        if (count($value) !== count($params['value'])) {
            return false;
        }
        foreach ($value as $key) {
            if ($key === '') {
                return false;
            }
        }
        return true;
    }

    /**
     * Check parent_id attribute.
     * @param array $value Value of attribute.
     * @param string $attribute Name of attribute.
     * @param array $params Extra params.
     * @return boolean
     */
    public function checkExists($value, $attribute, $params)
    {
        if (isset($params['allow']) && $value == $params['allow']) {
            return true;
        }
        return !!$this->findById($value);
    }

    /**
     * Build a component to the form of configuration.
     * @param array $component
     * @return array
     */
    public function build($component)
    {
        $component['id'] = $component['component_id'];
        $component['type'] = $component['component_type'];

        unset($component['component_id'], $component['component_type'], $component['parent_id'], $component['module_key'], $component['component_desc']);
        return $component;
    }

    /**
     * Decode properties of the components found.
     * @param array $components
     * @return array
     */
    protected function decodeProperties($components)
    {
        if ($components) {
            foreach ($components as &$component) {
                $properties = json_decode($component['properties'], true);
                if ($properties && is_array($properties)) {
                    $component = array_merge($component, $properties);
                }
                unset($component['properties']);
            }
        }
        return $components;
    }

    /**
     * Encode properties of the current component.
     */
    protected function encodeProperties()
    {
        $properties = $this->parameters();
        foreach ($properties as $key => &$value) {
            if (isset($this->$key)) {
                $value = $this->$key;
            }
        }
        $this->setAttribute('properties', json_encode($properties));
    }

    /**
     * Return the special parameters for the current component.
     * @return array
     */
    protected function parameters()
    {
        return array();
    }

    /**
     * Build array attributes.
     * @param array $component
     * @param string $keyName
     * @return array
     */
    protected function buildAttr($component, $keyName = 'attr')
    {
        if (isset($component['attr_key'])) {
            $component[$keyName] = array();
            foreach ($component['attr_key'] as $i => $key) {
                $component[$keyName][$key] = $component['attr_value'][$i];
            }
            unset($component['attr_key'], $component['attr_value']);
        }
        return $component;
    }

    /**
     * Delete all children of the component.
     * @return boolean
     */
    public function beforeDelete()
    {
        $params = array(
            'condition' => array(
                'parent_id' => $this->component_id,
            )
        );
        if ($components = $this->findAll($params)) {
            foreach ($components as $component) {
                $model = new module_Component();
                $model->setPrimaryKey($component['component_id']);
                if (!$model->delete()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Copy this component to the module whose module_key is $moduleKey.
     * @param string $moduleKey
     * @param integer $parentId
     * @return boolean
     */
    public function copy($moduleKey, $parentId = 0)
    {
        $model = clone $this;
        $model->component_id = null;
        $model->module_key = $moduleKey;
        $model->parent_id = $parentId;
        if ($model->insert()) {
            $componentId = $model->component_id;
            if ($children = $this->findByModuleKey($this->module_key, $this->component_id, false)) {
                foreach ($children as $child) {
                    $model = new module_Component();
                    $model->attributes = $child;
                    if (!$model->copy($moduleKey, $componentId)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Get builder
     * @return module_ModuleBuilder
     */
    public function getBuilder()
    {
        return $this->_builder;
    }

    /**
     * Set builder
     * @param module_ModuleBuilder $builder
     * @return module_Component
     */
    public function setBuilder(module_ModuleBuilder $builder)
    {
        $this->_builder = $builder;
        return $this;
    }

    /**
     * update ignore
     */
    public function afterInsert()
    {
        $this->componentIgnore->ignore();
        $this->componentTitle->title();
    }

    /**
     * update ignore
     */
    public function afterUpdate()
    {
        $this->componentIgnore->ignore();
        $this->componentTitle->title();
    }

    /**
     * find all component can be ignored
     *
     * @return array
     */
    public function findIgnored()
    {
        return $this->getDb()->createCommand()
            ->select('component_title,component_type,ignore_id')
            ->from($this->tableName())
            ->where('ignore_id <> 0')
            ->queryAll();
    }
}

<?php
class module_TabComponent extends module_Component
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
            array('attr_key', 'checkAttr', 'value' => $this->attr_value)
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
            'title'      => '',
            'tabsSkin'   => '',
            'ignore'     => 0,
            'ignoreId'   => '',
            'attr_key'   => array(),
            'attr_value' => array()
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
        $component['title'] = TM::t('tongji', $component['title']);
        $component['ignore'] = (bool)TMArrayHelper::assoc('ignore', $component, false);
        return $this->buildAttr($component);
    }
}

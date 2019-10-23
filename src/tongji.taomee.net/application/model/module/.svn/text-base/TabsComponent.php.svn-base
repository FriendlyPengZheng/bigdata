<?php
class module_TabsComponent extends module_Component
{
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
            'tabsSkin' => ''
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
}

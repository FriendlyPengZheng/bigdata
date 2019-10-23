<?php
class module_ComponentIgnore implements module_IComponentIgnore
{
    protected $component;

    public function setComponent(module_Component $component)
    {
        $this->component = $component;
        return $this;
    }

    public function ignore()
    {
        return;
    }
}

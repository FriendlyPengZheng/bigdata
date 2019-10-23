<?php
class module_ComponentWithTitle extends module_ComponentTitle
{
    public function title()
    {
        if ($this->component->component_title) {
            return;
        }
        if ($this->component->title) {
            $this->component->component_title = $this->component->title;
            return $this->component->update(array('component_title'));
        }
    }
}

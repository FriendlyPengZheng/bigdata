<?php
class module_ComponentWithIgnore extends module_ComponentIgnore
{
    public function ignore()
    {
        if ($this->component->ignore_id) {
            return;
        }
        if ($this->component->ignoreId) {
            $this->component->ignore_id = $this->component->ignoreId;
        } else {
            $this->component->ignoreId = $this->component->ignore_id = 20000 + $this->component->getPrimaryKey();
        }
        return $this->component->update(array('ignore_id', 'properties'));
    }
}

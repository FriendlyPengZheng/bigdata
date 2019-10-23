<?php
class TMNotationContext
{
    protected $fnt = array();

    protected $cst = array('PI' => M_PI, 'π' => M_PI);

    public function fn($name, $args)
    {
        if (!isset($this->fnt[$name])) {
            throw new TMNotationRuntimeError(TM::t('taomee', '未定义函数{func}！', array('{func}' => $name)));
        }

        return (float)call_user_func_array($this->fnt[$name], $args);
    }

    public function cs($name)
    {
        if (!isset($this->cst[$name])) {
            throw new TMNotationRuntimeError(TM::t('taomee', '未定义常量{cst}！', array('{cst}' => $name)));
        }

        return $this->cst[$name];
    }

    public function def($name, $value = null)
    {
        // simple wrapper
        if ($value === null) $value = $name;

        if (is_callable($value)) {
            $this->fnt[$name] = $value;
        } elseif (is_numeric($value)) {
            $this->cst[$name] = (float)$value;
        } else {
            throw new TMNotationException(TM::t('taomee', '定义值须是函数或者常量！'));
        }
    }
}

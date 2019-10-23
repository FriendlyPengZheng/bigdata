<?php
class TMNotationToken
{
    const T_NUMBER      = 1;  // Number (int/float/double)
    const T_IDENT       = 2;  // Constant
    const T_FUNCTION    = 4;  // Function
    const T_POPEN       = 8;  // (
    const T_PCLOSE      = 16; // )
    const T_COMMA       = 32; // ,
    const T_OPERATOR    = 64; // Operator
    const T_PLUS        = 65; // +
    const T_MINUS       = 66; // -
    const T_TIMES       = 67; // *
    const T_DIV         = 68; // /
    const T_MOD         = 69; // %
    const T_POW         = 70; // ^
    const T_UNARY_PLUS  = 71; // + (sign bit)
    const T_UNARY_MINUS = 72; // - (sign bit)
    const T_NOT         = 73; // !

    protected $type;

    protected $value;

    protected $argc = 0;

    public function __construct($type, $value)
    {
        $this->type  = $type;
        $this->value = $value;
    }

    public function getType()
    {
        return $this->type;
    }

    public function getValue()
    {
        return $this->value;
    }

    public function getArgc()
    {
        return $this->argc;
    }

    public function setType($type)
    {
        $this->type = $type;

        return $this;
    }

    public function setValue($value)
    {
        $this->value = $value;

        return $this;
    }

    public function setArgc($argc)
    {
        $this->argc = $argc;

        return $this;
    }
}

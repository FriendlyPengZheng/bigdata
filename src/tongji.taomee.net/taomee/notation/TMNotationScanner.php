<?php
class TMNotationScanner
{
    protected $pattern = '/^([!,\+\-\*\/\^%\(\)]|\d*\.\d+|\d+\.\d*|\d+|[a-z_A-Zπ]+[a-z_A-Z0-9]*|[ \t]+)/';

    protected $tokens = array(0);

    protected $lookup = array(
        '+' => TMNotationToken::T_PLUS,
        '-' => TMNotationToken::T_MINUS,
        '/' => TMNotationToken::T_DIV,
        '%' => TMNotationToken::T_MOD,
        '^' => TMNotationToken::T_POW,
        '*' => TMNotationToken::T_TIMES,
        '(' => TMNotationToken::T_POPEN,
        ')' => TMNotationToken::T_PCLOSE,
        '!' => TMNotationToken::T_NOT,
        ',' => TMNotationToken::T_COMMA
    );

    public function __construct($input, $pattern = null)
    {
        if (isset($pattern)) {
            $this->pattern = $pattern;
        }

        $prev = new TMNotationToken(TMNotationToken::T_OPERATOR, 'noop');

        while (trim($input) !== '') {
            if (!preg_match($this->pattern, $input, $match)) {
                // syntax error
                throw new TMNotationSyntaxError(
                    TM::t('taomee', '语法错误，{near}！', array('{near}' => substr($input, 0, 10))));
            }

            if (empty($match[1]) && $match[1] !== '0') {
                // Avoid endless loop
                throw new TMNotationSyntaxError(
                    TM::t('taomee', '存在无限循环，{near}！', array('{near}' => substr($input, 0, 10))));
            }

            // current value of deduct input
            $input = substr($input, strlen($match[1]));

            if (($value = trim($match[1])) === '') {
                // ignore blank
                continue;
            }

            if (is_numeric($value)) {
                if ($prev->getType() === TMNotationToken::T_PCLOSE) {
                    $this->tokens[] = new TMNotationToken(TMNotationToken::T_TIMES, '*');
                }

                $this->tokens[] = $prev = new TMNotationToken(TMNotationToken::T_NUMBER, (float)$value);
                continue;
            }

            switch ($type = isset($this->lookup[$value]) ? $this->lookup[$value] : TMNotationToken::T_IDENT) {
                case TMNotationToken::T_PLUS:
                    if ($prev->getType() & TMNotationToken::T_OPERATOR || $prev->getType() == TMNotationToken::T_POPEN) {
                        $type = TMNotationToken::T_UNARY_PLUS;
                    }
                    break;

                case TMNotationToken::T_MINUS:
                    if ($prev->getType() & TMNotationToken::T_OPERATOR || $prev->getType() == TMNotationToken::T_POPEN) {
                        $type = TMNotationToken::T_UNARY_MINUS;
                    }
                    break;

                case TMNotationToken::T_POPEN:
                    switch ($prev->getType()) {
                        case TMNotationToken::T_IDENT:
                            $prev->setType(TMNotationToken::T_FUNCTION);
                            break;

                        case TMNotationToken::T_NUMBER:
                        case TMNotationToken::T_PCLOSE:
                            // allowed 2(2) -> 2 * 2 | (2)(2) -> 2 * 2
                            $this->tokens[] = new TMNotationToken(TMNotationToken::T_TIMES, '*');
                            break;
                    }
                    break;
            }

            $this->tokens[] = $prev = new TMNotationToken($type, $value);
        }
    }

    public function curr()
    {
        return current($this->tokens);
    }

    public function next()
    {
        return next($this->tokens);
    }

    public function prev()
    {
        return prev($this->tokens);
    }

    public function peek()
    {
        $v = next($this->tokens);
        prev($this->tokens);

        return $v;
    }

    public function dump()
    {
        print_r($this->tokens);
    }

    public function getIdents()
    {
        $idents = array();
        foreach ($this->tokens as $i => $t) {
            if ($i && $t->getType() === TMNotationToken::T_IDENT) {
                $idents[] = $t->getValue();
            }
        }

        return $idents;
    }
}

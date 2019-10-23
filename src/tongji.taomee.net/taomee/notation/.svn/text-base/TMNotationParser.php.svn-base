<?php
class TMNotationParser
{
    const ST_1 = 1; // waiting for operand or sign bit
    const ST_2 = 2; // waiting for operator

    protected $scanner;

    protected $state = self::ST_1;

    protected $queue;

    protected $stack;

    public function __construct(TMNotationScanner $scanner)
    {
        $this->scanner = $scanner;

        // initialize
        $this->queue = array();
        $this->stack = array();

        // queue produce
        while (($t = $this->scanner->next()) !== false) {
            $this->handle($t);
        }

        // When there are no more tokens to read:
        // While there are still operator tokens in the stack:
        while ($t = array_pop($this->stack)) {
            if ($t->getType() === TMNotationToken::T_POPEN || $t->getType() === TMNotationToken::T_PCLOSE) {
                throw new TMNotationParseError(TM::t('taomee', '括号嵌套不正确！'));
            }

            $this->queue[] = $t;
        }
    }

    public function parse(TMNotationContext $ctx = null, $syntaxOnly = false)
    {
        if ($ctx === null) {
            $ctx = new TMNotationContext();
        }

        $this->stack = array();
        $len = 0;

        // While there are input tokens left
        // Read the next token from input.
        while ($t = array_shift($this->queue)) {
            switch ($t->getType()) {
                case TMNotationToken::T_NUMBER:
                case TMNotationToken::T_IDENT:
                    // a constant value determined
                    if ($t->getType() === TMNotationToken::T_IDENT) {
                        // If syntax only, assume all contstants to be 1
                        $t = new TMNotationToken(TMNotationToken::T_NUMBER, $syntaxOnly ? 1 : $ctx->cs($t->getValue()));
                    }

                    // If the token is a value or identifier
                    // Push it onto the stack.
                    $this->stack[] = $t;
                    ++$len;
                    break;

                case TMNotationToken::T_PLUS:
                case TMNotationToken::T_MINUS:
                case TMNotationToken::T_UNARY_PLUS:
                case TMNotationToken::T_UNARY_MINUS:
                case TMNotationToken::T_TIMES:
                case TMNotationToken::T_DIV:
                case TMNotationToken::T_MOD:
                case TMNotationToken::T_POW:
                case TMNotationToken::T_NOT:
                    // It is known a priori that the operator takes n arguments.
                    $na = $this->argc($t);

                    // If there are fewer than n values on the stack
                    if ($len < $na) {
                        throw new TMNotationRuntimeError(TM::t('taomee', '操作符{op}参数不够，需要{need}，只有{has}！',
                            array('{op}' => $t->getValue(), '{need}' => $na, '{has}' => $len)));
                    }

                    $rhs = array_pop($this->stack);
                    $lhs = null;

                    if ($na > 1) {
                        $lhs = array_pop($this->stack);
                    }

                    $len -= $na - 1;

                    // Push the returned results, if any, back onto the stack.
                    // If syntax only, assume all results to be 1
                    $this->stack[] = new TMNotationToken(TMNotationToken::T_NUMBER,
                        $syntaxOnly ? 1 : $this->op($t->getType(), $lhs, $rhs));
                    break;

                case TMNotationToken::T_FUNCTION:
                    // function
                    $argc = $t->getArgc();
                    $argv = array();

                    $len -= $argc - 1;

                    for (; $argc > 0; --$argc) {
                        array_unshift($argv, array_pop($this->stack)->getValue());
                    }

                    // Push the returned results, if any, back onto the stack.
                    // If syntax only, assume all results to be 1
                    $this->stack[] = new TMNotationToken(TMNotationToken::T_NUMBER,
                        $syntaxOnly ? 1 : $ctx->fn($t->getValue(), $argv));
                    break;

                default:
                    throw new TMNotationRuntimeError(
                        TM::t('taomee', '无效的标识{token}！', array('{token}' => $t->getValue())));
                    break;
            }
        }

        // If there is only one value in the stack
        // That value is the result of the calculation.
        if (count($this->stack) === 1) {
            return array_pop($this->stack)->getValue();
        }

        // If there are more values in the stack
        // (Error) The user input has too many values.
        throw new TMNotationRuntimeError(TM::t('taomee', '操作数过多！'));
    }

    protected function op($op, $lhs, $rhs)
    {
        if ($lhs !== null) {
            $lhs = $lhs->getValue();
            $rhs = $rhs->getValue();

            switch ($op) {
                case TMNotationToken::T_PLUS:
                    return $lhs + $rhs;
                    break;

                case TMNotationToken::T_MINUS:
                    return $lhs - $rhs;
                    break;

                case TMNotationToken::T_TIMES:
                    return $lhs * $rhs;
                    break;

                case TMNotationToken::T_DIV:
                    if ($rhs === 0.) {
                        throw new TMNotationRuntimeError(TM::t('taomee', '除数为0！'));
                    }
                    return $lhs / $rhs;
                    break;

                case TMNotationToken::T_MOD:
                    if ($rhs === 0.) {
                        throw new TMNotationRuntimeError(TM::t('taomee', '对0取模！'));
                    }
                    return (float)$lhs % $rhs;
                    break;

                case TMNotationToken::T_POW:
                    return (float)pow($lhs, $rhs);
                    break;
            }

            return 0;
        }

        switch ($op) {
            case TMNotationToken::T_NOT:
                return (float)!$rhs->getValue();
                break;

            case TMNotationToken::T_UNARY_MINUS:
                return -$rhs->getValue();
                break;

            case TMNotationToken::T_UNARY_PLUS:
                return +$rhs->getValue();
        }
    }

    protected function argc(TMNotationToken $t)
    {
        switch ($t->getType()) {
            case TMNotationToken::T_PLUS:
            case TMNotationToken::T_MINUS:
            case TMNotationToken::T_TIMES:
            case TMNotationToken::T_DIV:
            case TMNotationToken::T_MOD:
            case TMNotationToken::T_POW:
                return 2;
                break;
        }

        return 1;
    }

    public function dump($str = false)
    {
        if ($str === false) {
            print_r($this->queue);
            return;
        }

        $res = array();
        foreach ($this->queue as $t) {
            $val = $t->getValue();

            switch ($t->getType()) {
                case TMNotationToken::T_UNARY_MINUS:
                case TMNotationToken::T_UNARY_PLUS:
                    $val = 'unary' . $val;
                    break;
            }

            $res[] = $val;
        }

        echo implode(' ', $res);
    }

    protected function fargs($fn)
    {
        $this->handle($this->scanner->next()); // (

        $argc = 0;
        $next = $this->scanner->peek();

        if ($next && $next->getType() !== TMNotationToken::T_PCLOSE) {
            $argc = 1;

            while ($t = $this->scanner->next()) {
                $this->handle($t);

                if ($t->getType() === TMNotationToken::T_PCLOSE) {
                    break;
                }

                if ($t->getType() === TMNotationToken::T_COMMA) {
                    ++$argc;
                }
            }
        }

        $fn->setArgc($argc);
    }

    protected function handle(TMNotationToken $t)
    {
        switch ($t->getType()) {
            case TMNotationToken::T_NUMBER:
            case TMNotationToken::T_IDENT:
                // If the token is a number (identifier), then add it to the output queue.
                $this->queue[] = $t;
                $this->state = self::ST_2;
                break;

            case TMNotationToken::T_FUNCTION:
                // If the token is a function token, then push it onto the stack.
                $this->stack[] = $t;
                $this->fargs($t);
                break;


            case TMNotationToken::T_COMMA:
                // If the token is a function argument separator (e.g., a comma):
                $pe = false;

                while ($t = end($this->stack)) {
                    if ($t->getType() === TMNotationToken::T_POPEN) {
                        $pe = true;
                        break;
                    }

                    // Until the token at the top of the stack is a left parenthesis,
                    // pop operators off the stack onto the output queue.
                    $this->queue[] = array_pop($this->stack);
                }

                // If no left parentheses are encountered, either the separator was misplaced
                // or parentheses were mismatched.
                if ($pe !== true) {
                    throw new TMNotationParseError(
                        TM::t('taomee', '缺少标志{1}或者{2}！', array('{1}' => '(', '{2}' => ',')));
                }
                break;

            // If the token is an operator, op1, then:
            case TMNotationToken::T_PLUS:
            case TMNotationToken::T_MINUS:
            case TMNotationToken::T_UNARY_PLUS:
            case TMNotationToken::T_UNARY_MINUS:
            case TMNotationToken::T_TIMES:
            case TMNotationToken::T_DIV:
            case TMNotationToken::T_MOD:
            case TMNotationToken::T_POW:
            case TMNotationToken::T_NOT:
                while (!empty($this->stack)) {
                    $s = end($this->stack);

                    // While there is an operator token, o2, at the top of the stack
                    // op1 is left-associative and its precedence is less than or equal to that of op2,
                    // or op1 has precedence less than that of op2,
                    // Let + and ^ be right associative.
                    // Correct transformation from 1^2+3 is 12^3+
                    // The differing operator priority decides pop / push
                    // If 2 operators have equal priority then associativity decides.
                    switch ($s->getType()) {
                        default:
                            break 2;

                        case TMNotationToken::T_PLUS:
                        case TMNotationToken::T_MINUS:
                        case TMNotationToken::T_UNARY_PLUS:
                        case TMNotationToken::T_UNARY_MINUS:
                        case TMNotationToken::T_TIMES:
                        case TMNotationToken::T_DIV:
                        case TMNotationToken::T_MOD:
                        case TMNotationToken::T_POW:
                        case TMNotationToken::T_NOT:
                            $p1 = $this->preced($t);
                            $p2 = $this->preced($s);

                            if (!(($this->assoc($t) === 1 && ($p1 <= $p2)) || ($p1 < $p2))) {
                                break 2;
                            }

                            // Pop o2 off the stack, onto the output queue;
                            $this->queue[] = array_pop($this->stack);
                    }
                }

                // push op1 onto the stack.
                $this->stack[] = $t;
                $this->state = self::ST_1;
                break;

            // If the token is a left parenthesis, then push it onto the stack.
            case TMNotationToken::T_POPEN:
                $this->stack[] = $t;
                $this->state = self::ST_1;
                break;

            // If the token is a right parenthesis:
            case TMNotationToken::T_PCLOSE:
                $pe = false;

                // Until the token at the top of the stack is a left parenthesis,
                // pop operators off the stack onto the output queue
                while ($t = array_pop($this->stack)) {
                    if ($t->getType() === TMNotationToken::T_POPEN) {
                        // Pop the left parenthesis from the stack, but not onto the output queue.
                        $pe = true;
                        break;
                    }

                    $this->queue[] = $t;
                }

                // If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
                if ($pe !== true) {
                    throw new TMNotationParseError(TM::t('taomee', '不期望标识{token}！', array('{token}' => ')')));
                }

                // If the token at the top of the stack is a function token, pop it onto the output queue.
                if (($t = end($this->stack)) && $t->getType() === TMNotationToken::T_FUNCTION) {
                    $this->queue[] = array_pop($this->stack);
                }

                $this->state = self::ST_2;
                break;

            default:
                throw new TMNotationParseError(TM::t('taomee', '未知标识{token}！', array('{token}' => $t->getValue())));
                break;
        }
    }

    protected function assoc(TMNotationToken $t)
    {
        switch ($t->getType()) {
            case TMNotationToken::T_TIMES:
            case TMNotationToken::T_DIV:
            case TMNotationToken::T_MOD:
            case TMNotationToken::T_PLUS:
            case TMNotationToken::T_MINUS:
                return 1; // ltr
                break;

            case TMNotationToken::T_NOT:
            case TMNotationToken::T_UNARY_PLUS:
            case TMNotationToken::T_UNARY_MINUS:
            case TMNotationToken::T_POW:
                return 2; // rtl
                break;
        }

        return 0;
    }

    protected function preced(TMNotationToken $t)
    {
        switch ($t->getType()) {
            case TMNotationToken::T_NOT:
            case TMNotationToken::T_UNARY_PLUS:
            case TMNotationToken::T_UNARY_MINUS:
                return 4;
                break;

            case TMNotationToken::T_POW:
                return 3;
                break;

            case TMNotationToken::T_TIMES:
            case TMNotationToken::T_DIV:
            case TMNotationToken::T_MOD:
                return 2;
                break;

            case TMNotationToken::T_PLUS:
            case TMNotationToken::T_MINUS:
                return 1;
                break;
        }

        return 0;
    }
}

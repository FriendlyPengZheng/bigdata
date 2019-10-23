<?php
/**
 * TMTemplateLexer
 */
class TMTemplateLexer
{
    public $data;
    public $counter;
    public $token;
    public $value;
    public $line;

    public $compiler;
    public $template;

    public $ldel;
    public $ldelLength;
    public $rdel;
    public $rdelLength;

    public $tokenNames = array (
        'IDENTITY'        => '===',
        'NONEIDENTITY'    => '!==',
        'EQUALS'          => '==',
        'NOTEQUALS'       => '!=',
        'GREATEREQUAL'    => '>=',
        'LESSEQUAL'       => '<=',
        'GREATERTHAN'     => '>',
        'LESSTHAN'        => '<',
        'MOD'             => '%',
        'NOT'             => '!',
        'LAND'            => '&&',
        'LOR'             => '||',
        'LXOR'            => 'xor',
        'OPENP'           => '(',
        'CLOSEP'          => ')',
        'OPENB'           => '[',
        'CLOSEB'          => ']',
        'PTR'             => '->',
        'APTR'            => '=>',
        'EQUAL'           => '=',
        'NUMBER'          => 'number',
        'UNIMATH'         => '+" , "-',
        'MATH'            => '*" , "/" , "%',
        'INCDEC'          => '++" , "--',
        'SPACE'           => ' ',
        'DOLLAR'          => '$',
        'SEMICOLON'       => ';',
        'COLON'           => ':',
        'DOUBLECOLON'     => '::',
        'AT'              => '@',
        'QUOTE'           => '"',
        'BACKTICK'        => '`',
        'VERT'            => '|',
        'DOT'             => '.',
        'COMMA'           => '","',
        'ANDSYM'          => '"&"',
        'QMARK'           => '"?"',
        'ID'              => 'identifier',
        'TEXT'            => 'text',
        'FAKEPHPSTARTTAG' => 'Fake PHP start tag',
        'PHPSTARTTAG'     => 'PHP start tag',
        'PHPENDTAG'       => 'PHP end tag',
        'LITERALSTART'    => 'Literal start',
        'LITERALEND'      => 'Literal end',
        'LDELSLASH'       => 'closing tag',
        'COMMENT'         => 'comment',
        'AS'              => 'as',
        'TO'              => 'to'
    );

    // required by LexerGenerator.
    public $mbstring_overload;


    function __construct($data, $compiler)
    {
        $this->data = $data;
        $this->counter = 0;
        $this->line = 1;
        $this->compiler = $compiler;
        $this->template = $compiler->template;

        $this->ldel = preg_quote($this->template->leftDelimiter, '/');
        $this->ldelLength = strlen($this->template->leftDelimiter);
        $this->rdel = preg_quote($this->template->rightDelimiter, '/');
        $this->rdelLength = strlen($this->template->rightDelimiter);
        $this->tokenNames['LDEL'] = $this->template->leftDelimiter;
        $this->tokenNames['RDEL'] = $this->template->rightDelimiter;

        $this->mbstring_overload = ini_get('mbstring.func_overload') & 2;
     }


    private $_yy_state = 1;
    private $_yy_stack = array();

    public function yylex()
    {
        return $this->{'yylex' . $this->_yy_state}();
    }

    public function yypushstate($state)
    {
        array_push($this->_yy_stack, $this->_yy_state);
        $this->_yy_state = $state;
    }

    public function yypopstate()
    {
        $this->_yy_state = array_pop($this->_yy_stack);
    }

    public function yybegin($state)
    {
        $this->_yy_state = $state;
    }




    public function yylex1()
    {
        $tokenMap = array (
              1 => 0,
              2 => 1,
              4 => 0,
              5 => 0,
              6 => 0,
              7 => 1,
              9 => 0,
              10 => 0,
              11 => 0,
              12 => 0,
              13 => 0,
              14 => 0,
              15 => 0,
              16 => 0,
              17 => 0,
              18 => 0,
            );
        if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
            return false; // end of input
        }
        $yy_global_pattern = "/\G(\\{\\})|\G(".$this->ldel."\\s*\\*([\S\s]*?)\\*\\s*".$this->rdel.")|\G(".$this->ldel."\\s*strip\\s*".$this->rdel.")|\G(".$this->ldel."\\s*\/strip\\s*".$this->rdel.")|\G(".$this->ldel."\\s*literal\\s*".$this->rdel.")|\G(".$this->ldel."\\s*(if|elseif|else if|while)\\s+)|\G(".$this->ldel."\\s*for\\s+)|\G(".$this->ldel."\\s*foreach(?![^\s]))|\G(".$this->ldel."\\s*\/)|\G(".$this->ldel."\\s*)|\G(<\\?(?:php\\w+|=|[a-zA-Z]+)?)|\G(\\?>)|\G(\\s*".$this->rdel.")|\G(<%)|\G(%>)|\G([\S\s])/iS";

        do {
            if ($this->mbstring_overload ? preg_match($yy_global_pattern, mb_substr($this->data, $this->counter,2000000000,'latin1'), $yymatches) : preg_match($yy_global_pattern,$this->data, $yymatches, null, $this->counter)) {
                $yysubmatches = $yymatches;
                $yymatches = array_filter($yymatches, 'strlen'); // remove empty sub-patterns
                if (!count($yymatches)) {
                    throw new Exception('Error: lexing failed because a rule matched' .
                        ' an empty string.  Input "' . substr($this->data,
                        $this->counter, 5) . '... state TEXT');
                }
                next($yymatches); // skip global match
                $this->token = key($yymatches); // token number
                if ($tokenMap[$this->token]) {
                    // extract sub-patterns for passing to lex function
                    $yysubmatches = array_slice($yysubmatches, $this->token + 1,
                        $tokenMap[$this->token]);
                } else {
                    $yysubmatches = array();
                }
                $this->value = current($yymatches); // token value
                $r = $this->{'yy_r1_' . $this->token}($yysubmatches);
                if ($r === null) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    // accept this token
                    return true;
                } elseif ($r === true) {
                    // we have changed state
                    // process this token in the new state
                    return $this->yylex();
                } elseif ($r === false) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
                        return false; // end of input
                    }
                    // skip this token
                    continue;
                }            } else {
                throw new Exception('Unexpected input at line' . $this->line .
                    ': ' . $this->data[$this->counter]);
            }
            break;
        } while (true);

    } // end function


    const TEXT = 1;
    function yy_r1_1($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TEXT;
    }
    function yy_r1_2($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_COMMENT;
    }
    function yy_r1_4($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_STRIPON;
    }
    }
    function yy_r1_5($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_STRIPOFF;
    }
    }
    function yy_r1_6($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LITERALSTART;
        $this->yypushstate(self::LITERAL);
    }
    }
    function yy_r1_7($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELIF;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r1_9($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELFOR;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r1_10($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELFOREACH;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r1_11($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELSLASH;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r1_12($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDEL;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r1_13($yy_subpatterns)
    {

    if (in_array($this->value, Array('<?', '<?=', '<?php'))) {
        $this->token = TMTemplateParser::TP_PHPSTARTTAG;
    } elseif ($this->value == '<?xml') {
        $this->token = TMTemplateParser::TP_XMLTAG;
    } else {
      $this->token = TMTemplateParser::TP_FAKEPHPSTARTTAG;
      $this->value = substr($this->value, 0, 2);
    }
    }
    function yy_r1_14($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_PHPENDTAG;
    }
    function yy_r1_15($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TEXT;
    }
    function yy_r1_16($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_ASPSTARTTAG;
    }
    function yy_r1_17($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_ASPENDTAG;
    }
    function yy_r1_18($yy_subpatterns)
    {

    if ($this->mbstring_overload) {
        $to = mb_strlen($this->data, 'latin1');
    } else {
        $to = strlen($this->data);
    }
    preg_match("/{$this->ldel}|<\?|\?>|<%|%>/", $this->data, $match, PREG_OFFSET_CAPTURE, $this->counter);
    if (isset($match[0][1])) {
        $to = $match[0][1];
    }
    if ($this->mbstring_overload) {
        $this->value = mb_substr($this->data, $this->counter, $to - $this->counter, 'latin1');
    } else {
        $this->value = substr($this->data, $this->counter, $to - $this->counter);
    }
    $this->token = TMTemplateParser::TP_TEXT;
    }


    public function yylex2()
    {
        $tokenMap = array (
              1 => 0,
              2 => 0,
              3 => 1,
              5 => 0,
              6 => 0,
              7 => 0,
              8 => 0,
              9 => 0,
              10 => 0,
              11 => 0,
              12 => 0,
              13 => 0,
              14 => 0,
              15 => 0,
              16 => 0,
              17 => 0,
              18 => 0,
              19 => 0,
              20 => 0,
              21 => 0,
              22 => 0,
              23 => 0,
              24 => 3,
              28 => 0,
              29 => 0,
              30 => 0,
              31 => 0,
              32 => 0,
              33 => 0,
              34 => 0,
              35 => 0,
              36 => 1,
              38 => 1,
              40 => 0,
              41 => 0,
              42 => 0,
              43 => 0,
              44 => 0,
              45 => 0,
              46 => 0,
              47 => 0,
              48 => 0,
              49 => 0,
              50 => 0,
              51 => 0,
              52 => 0,
              53 => 0,
              54 => 0,
              55 => 1,
              57 => 0,
              58 => 0,
              59 => 0,
              60 => 0,
              61 => 0,
            );
        if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
            return false; // end of input
        }
        $yy_global_pattern = "/\G(\")|\G('[^'\\\\]*(?:\\\\.[^'\\\\]*)*')|\G([$]template\\.block\\.(child|parent))|\G(\\$)|\G(\\s*".$this->rdel.")|\G(\\s+as\\s+)|\G(\\s+to\\s+)|\G(\\s+step\\s+)|\G(\\s+instanceof\\s+)|\G(\\s*===\\s*)|\G(\\s*!==\\s*)|\G(\\s*==\\s*)|\G(\\s*!=\\s*|\\s*<>\\s*)|\G(\\s*>=\\s*)|\G(\\s*<=\\s*)|\G(\\s*>\\s*)|\G(\\s*<\\s*)|\G(\\s+mod\\s+)|\G(!\\s*|not\\s+)|\G(\\s*&&\\s*|\\s*and\\s+)|\G(\\s*\\|\\|\\s*|\\s*or\\s+)|\G(\\s*xor\\s+)|\G(\\((int(eger)?|bool(ean)?|float|double|real|string|binary|array|object)\\)\\s*)|\G(\\s*\\(\\s*)|\G(\\s*\\))|\G(\\[\\s*)|\G(\\s*\\])|\G(\\s*->\\s*)|\G(\\s*=>\\s*)|\G(\\s*=\\s*)|\G(\\+\\+|--)|\G(\\s*(\\+|-)\\s*)|\G(\\s*(\\*|\/|%)\\s*)|\G(@)|\G(\\s+[0-9]*[a-zA-Z_][a-zA-Z0-9_\-:]*\\s*=\\s*)|\G([0-9]*[a-zA-Z_]\\w*)|\G(\\d+)|\G(`)|\G(\\|)|\G(\\.)|\G(\\s*,\\s*)|\G(\\s*;)|\G(::)|\G(\\s*:\\s*)|\G(\\s*&\\s*)|\G(\\s*\\?\\s*)|\G(0[xX][0-9a-fA-F]+)|\G(\\s+)|\G(".$this->ldel."\\s*(if|elseif|else if|while)\\s+)|\G(".$this->ldel."\\s*for\\s+)|\G(".$this->ldel."\\s*foreach(?![^\s]))|\G(".$this->ldel."\\s*\/)|\G(".$this->ldel."\\s*)|\G([\S\s])/iS";

        do {
            if ($this->mbstring_overload ? preg_match($yy_global_pattern, mb_substr($this->data, $this->counter,2000000000,'latin1'), $yymatches) : preg_match($yy_global_pattern,$this->data, $yymatches, null, $this->counter)) {
                $yysubmatches = $yymatches;
                $yymatches = array_filter($yymatches, 'strlen'); // remove empty sub-patterns
                if (!count($yymatches)) {
                    throw new Exception('Error: lexing failed because a rule matched' .
                        ' an empty string.  Input "' . substr($this->data,
                        $this->counter, 5) . '... state TEMPLATE');
                }
                next($yymatches); // skip global match
                $this->token = key($yymatches); // token number
                if ($tokenMap[$this->token]) {
                    // extract sub-patterns for passing to lex function
                    $yysubmatches = array_slice($yysubmatches, $this->token + 1,
                        $tokenMap[$this->token]);
                } else {
                    $yysubmatches = array();
                }
                $this->value = current($yymatches); // token value
                $r = $this->{'yy_r2_' . $this->token}($yysubmatches);
                if ($r === null) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    // accept this token
                    return true;
                } elseif ($r === true) {
                    // we have changed state
                    // process this token in the new state
                    return $this->yylex();
                } elseif ($r === false) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
                        return false; // end of input
                    }
                    // skip this token
                    continue;
                }            } else {
                throw new Exception('Unexpected input at line' . $this->line .
                    ': ' . $this->data[$this->counter]);
            }
            break;
        } while (true);

    } // end function


    const TEMPLATE = 2;
    function yy_r2_1($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_QUOTE;
    $this->yypushstate(self::DOUBLEQUOTEDSTRING);
    }
    function yy_r2_2($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_SINGLEQUOTESTRING;
    }
    function yy_r2_3($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TEMPLATEBLOCKCHILDPARENT;
    }
    function yy_r2_5($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_DOLLAR;
    }
    function yy_r2_6($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_RDEL;
    $this->yypopstate();
    }
    function yy_r2_7($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_AS;
    }
    function yy_r2_8($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TO;
    }
    function yy_r2_9($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_STEP;
    }
    function yy_r2_10($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_INSTANCEOF;
    }
    function yy_r2_11($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_IDENTITY;
    }
    function yy_r2_12($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_NONEIDENTITY;
    }
    function yy_r2_13($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_EQUALS;
    }
    function yy_r2_14($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_NOTEQUALS;
    }
    function yy_r2_15($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_GREATEREQUAL;
    }
    function yy_r2_16($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_LESSEQUAL;
    }
    function yy_r2_17($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_GREATERTHAN;
    }
    function yy_r2_18($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_LESSTHAN;
    }
    function yy_r2_19($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_MOD;
    }
    function yy_r2_20($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_NOT;
    }
    function yy_r2_21($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_LAND;
    }
    function yy_r2_22($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_LOR;
    }
    function yy_r2_23($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_LXOR;
    }
    function yy_r2_24($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TYPECAST;
    }
    function yy_r2_28($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_OPENP;
    }
    function yy_r2_29($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_CLOSEP;
    }
    function yy_r2_30($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_OPENB;
    }
    function yy_r2_31($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_CLOSEB;
    }
    function yy_r2_32($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_PTR;
    }
    function yy_r2_33($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_APTR;
    }
    function yy_r2_34($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_EQUAL;
    }
    function yy_r2_35($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_INCDEC;
    }
    function yy_r2_36($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_UNIMATH;
    }
    function yy_r2_38($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_MATH;
    }
    function yy_r2_40($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_AT;
    }
    function yy_r2_41($yy_subpatterns)
    {

    // resolve conflicts with shorttag and right delimiter starting with '='
    if (substr($this->data, $this->counter + strlen($this->value) - 1, $this->rdelLength) == $this->template->rightDelimiter) {
        preg_match("/\s+/", $this->value, $match);
        $this->value = $match[0];
        $this->token = TMTemplateParser::TP_SPACE;
    } else {
        $this->token = TMTemplateParser::TP_ATTR;
    }
    }
    function yy_r2_42($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_ID;
    }
    function yy_r2_43($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_INTEGER;
    }
    function yy_r2_44($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_BACKTICK;
    $this->yypopstate();
    }
    function yy_r2_45($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_VERT;
    }
    function yy_r2_46($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_DOT;
    }
    function yy_r2_47($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_COMMA;
    }
    function yy_r2_48($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_SEMICOLON;
    }
    function yy_r2_49($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_DOUBLECOLON;
    }
    function yy_r2_50($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_COLON;
    }
    function yy_r2_51($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_ANDSYM;
    }
    function yy_r2_52($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_QMARK;
    }
    function yy_r2_53($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_HEX;
    }
    function yy_r2_54($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_SPACE;
    }
    function yy_r2_55($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELIF;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r2_57($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELFOR;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r2_58($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELFOREACH;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r2_59($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELSLASH;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r2_60($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDEL;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r2_61($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TEXT;
    }



    public function yylex3()
    {
        $tokenMap = array (
              1 => 0,
              2 => 0,
              3 => 0,
              4 => 0,
              5 => 0,
              6 => 0,
              7 => 0,
            );
        if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
            return false; // end of input
        }
        $yy_global_pattern = "/\G(".$this->ldel."\\s*literal\\s*".$this->rdel.")|\G(".$this->ldel."\\s*\/literal\\s*".$this->rdel.")|\G(<\\?(?:php\\w+|=|[a-zA-Z]+)?)|\G(\\?>)|\G(<%)|\G(%>)|\G([\S\s])/iS";

        do {
            if ($this->mbstring_overload ? preg_match($yy_global_pattern, mb_substr($this->data, $this->counter,2000000000,'latin1'), $yymatches) : preg_match($yy_global_pattern,$this->data, $yymatches, null, $this->counter)) {
                $yysubmatches = $yymatches;
                $yymatches = array_filter($yymatches, 'strlen'); // remove empty sub-patterns
                if (!count($yymatches)) {
                    throw new Exception('Error: lexing failed because a rule matched' .
                        ' an empty string.  Input "' . substr($this->data,
                        $this->counter, 5) . '... state LITERAL');
                }
                next($yymatches); // skip global match
                $this->token = key($yymatches); // token number
                if ($tokenMap[$this->token]) {
                    // extract sub-patterns for passing to lex function
                    $yysubmatches = array_slice($yysubmatches, $this->token + 1,
                        $tokenMap[$this->token]);
                } else {
                    $yysubmatches = array();
                }
                $this->value = current($yymatches); // token value
                $r = $this->{'yy_r3_' . $this->token}($yysubmatches);
                if ($r === null) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    // accept this token
                    return true;
                } elseif ($r === true) {
                    // we have changed state
                    // process this token in the new state
                    return $this->yylex();
                } elseif ($r === false) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
                        return false; // end of input
                    }
                    // skip this token
                    continue;
                }            } else {
                throw new Exception('Unexpected input at line' . $this->line .
                    ': ' . $this->data[$this->counter]);
            }
            break;
        } while (true);

    } // end function


    const LITERAL = 3;
    function yy_r3_1($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LITERALSTART;
        $this->yypushstate(self::LITERAL);
    }
    }
    function yy_r3_2($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LITERALEND;
        $this->yypopstate();
    }
    }
    function yy_r3_3($yy_subpatterns)
    {

    if (in_array($this->value, Array('<?', '<?=', '<?php'))) {
        $this->token = TMTemplateParser::TP_PHPSTARTTAG;
    } else {
        $this->token = TMTemplateParser::TP_FAKEPHPSTARTTAG;
        $this->value = substr($this->value, 0, 2);
    }
    }
    function yy_r3_4($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_PHPENDTAG;
    }
    function yy_r3_5($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_ASPSTARTTAG;
    }
    function yy_r3_6($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_ASPENDTAG;
    }
    function yy_r3_7($yy_subpatterns)
    {

    if ($this->mbstring_overload) {
        $to = mb_strlen($this->data, 'latin1');
    } else {
        $to = strlen($this->data);
    }
    preg_match("/{$this->ldel}\/?literal{$this->rdel}|<\?|<%|\?>|%>/", $this->data, $match, PREG_OFFSET_CAPTURE, $this->counter);
    if (isset($match[0][1])) {
        $to = $match[0][1];
    } else {
        $this->compiler->triggerTemplateError("Missing or misspelled literal closing tag");
    }
    if ($this->mbstring_overload) {
        $this->value = mb_substr($this->data, $this->counter, $to - $this->counter, 'latin1');
    } else {
        $this->value = substr($this->data, $this->counter, $to - $this->counter);
    }
    $this->token = TMTemplateParser::TP_LITERAL;
    }



    public function yylex4()
    {
        $tokenMap = array (
              1 => 1,
              3 => 0,
              4 => 0,
              5 => 0,
              6 => 0,
              7 => 0,
              8 => 0,
              9 => 0,
              10 => 0,
              11 => 3,
              15 => 0,
            );
        if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
            return false; // end of input
        }
        $yy_global_pattern = "/\G(".$this->ldel."\\s*(if|elseif|else if|while)\\s+)|\G(".$this->ldel."\\s*for\\s+)|\G(".$this->ldel."\\s*foreach(?![^\s]))|\G(".$this->ldel."\\s*\/)|\G(".$this->ldel."\\s*)|\G(\")|\G(`\\$)|\G(\\$[0-9]*[a-zA-Z_]\\w*)|\G(\\$)|\G(([^\"\\\\]*?)((?:\\\\.[^\"\\\\]*?)*?)(?=(".$this->ldel."|\\$|`\\$|\")))|\G([\S\s])/iS";

        do {
            if ($this->mbstring_overload ? preg_match($yy_global_pattern, mb_substr($this->data, $this->counter,2000000000,'latin1'), $yymatches) : preg_match($yy_global_pattern,$this->data, $yymatches, null, $this->counter)) {
                $yysubmatches = $yymatches;
                $yymatches = array_filter($yymatches, 'strlen'); // remove empty sub-patterns
                if (!count($yymatches)) {
                    throw new Exception('Error: lexing failed because a rule matched' .
                        ' an empty string.  Input "' . substr($this->data,
                        $this->counter, 5) . '... state DOUBLEQUOTEDSTRING');
                }
                next($yymatches); // skip global match
                $this->token = key($yymatches); // token number
                if ($tokenMap[$this->token]) {
                    // extract sub-patterns for passing to lex function
                    $yysubmatches = array_slice($yysubmatches, $this->token + 1,
                        $tokenMap[$this->token]);
                } else {
                    $yysubmatches = array();
                }
                $this->value = current($yymatches); // token value
                $r = $this->{'yy_r4_' . $this->token}($yysubmatches);
                if ($r === null) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    // accept this token
                    return true;
                } elseif ($r === true) {
                    // we have changed state
                    // process this token in the new state
                    return $this->yylex();
                } elseif ($r === false) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
                        return false; // end of input
                    }
                    // skip this token
                    continue;
                }            } else {
                throw new Exception('Unexpected input at line' . $this->line .
                    ': ' . $this->data[$this->counter]);
            }
            break;
        } while (true);

    } // end function


    const DOUBLEQUOTEDSTRING = 4;
    function yy_r4_1($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELIF;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r4_3($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELFOR;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r4_4($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELFOREACH;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r4_5($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDELSLASH;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r4_6($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_TEXT;
    } else {
        $this->token = TMTemplateParser::TP_LDEL;
        $this->yypushstate(self::TEMPLATE);
    }
    }
    function yy_r4_7($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_QUOTE;
    $this->yypopstate();
    }
    function yy_r4_8($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_BACKTICK;
    $this->value = substr($this->value, 0, -1);
    $this->yypushstate(self::TEMPLATE);
    }
    function yy_r4_9($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_DOLLARID;
    }
    function yy_r4_10($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TEXT;
    }
    function yy_r4_11($yy_subpatterns)
    {

    $this->token = TMTemplateParser::TP_TEXT;
    }
    function yy_r4_15($yy_subpatterns)
    {

    if ($this->mbstring_overload) {
        $to = mb_strlen($this->data, 'latin1');
    } else {
        $to = strlen($this->data);
    }
    if ($this->mbstring_overload) {
        $this->value = mb_substr($this->data, $this->counter, $to - $this->counter, 'latin1');
    } else {
        $this->value = substr($this->data, $this->counter, $to - $this->counter);
    }
    $this->token = TMTemplateParser::TP_TEXT;
    }



    public function yylex5()
    {
        $tokenMap = array (
              1 => 0,
              2 => 0,
              3 => 0,
              4 => 0,
            );
        if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
            return false; // end of input
        }
        $yy_global_pattern = "/\G(".$this->ldel."\\s*strip\\s*".$this->rdel.")|\G(".$this->ldel."\\s*\/strip\\s*".$this->rdel.")|\G(".$this->ldel."\\s*block)|\G([\S\s])/iS";

        do {
            if ($this->mbstring_overload ? preg_match($yy_global_pattern, mb_substr($this->data, $this->counter,2000000000,'latin1'), $yymatches) : preg_match($yy_global_pattern,$this->data, $yymatches, null, $this->counter)) {
                $yysubmatches = $yymatches;
                $yymatches = array_filter($yymatches, 'strlen'); // remove empty sub-patterns
                if (!count($yymatches)) {
                    throw new Exception('Error: lexing failed because a rule matched' .
                        ' an empty string.  Input "' . substr($this->data,
                        $this->counter, 5) . '... state CHILDBODY');
                }
                next($yymatches); // skip global match
                $this->token = key($yymatches); // token number
                if ($tokenMap[$this->token]) {
                    // extract sub-patterns for passing to lex function
                    $yysubmatches = array_slice($yysubmatches, $this->token + 1,
                        $tokenMap[$this->token]);
                } else {
                    $yysubmatches = array();
                }
                $this->value = current($yymatches); // token value
                $r = $this->{'yy_r5_' . $this->token}($yysubmatches);
                if ($r === null) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    // accept this token
                    return true;
                } elseif ($r === true) {
                    // we have changed state
                    // process this token in the new state
                    return $this->yylex();
                } elseif ($r === false) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
                        return false; // end of input
                    }
                    // skip this token
                    continue;
                }            } else {
                throw new Exception('Unexpected input at line' . $this->line .
                    ': ' . $this->data[$this->counter]);
            }
            break;
        } while (true);

    } // end function


    const CHILDBODY = 5;
    function yy_r5_1($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        return false;
    } else {
        $this->token = TMTemplateParser::TP_STRIPON;
    }
    }
    function yy_r5_2($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        return false;
    } else {
        $this->token = TMTemplateParser::TP_STRIPOFF;
    }
    }
    function yy_r5_3($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        return false;
    } else {
        $this->yypopstate();
        return true;
    }
    }
    function yy_r5_4($yy_subpatterns)
    {

    if ($this->mbstring_overload) {
        $to = mb_strlen($this->data, 'latin1');
    } else {
        $to = strlen($this->data);
    }
    preg_match("/".$this->ldel."\s*((\/)?strip\s*".$this->rdel."|block\s+)/", $this->data, $match, PREG_OFFSET_CAPTURE, $this->counter);
    if (isset($match[0][1])) {
        $to = $match[0][1];
    }
    if ($this->mbstring_overload) {
        $this->value = mb_substr($this->data, $this->counter, $to - $this->counter, 'latin1');
    } else {
        $this->value = substr($this->data, $this->counter, $to - $this->counter);
    }
    return false;
    }



    public function yylex6()
    {
        $tokenMap = array (
              1 => 0,
              2 => 0,
              3 => 1,
              5 => 0,
            );
        if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
            return false; // end of input
        }
        $yy_global_pattern = "/\G(".$this->ldel."\\s*block)|\G(".$this->ldel."\\s*\/block)|\G(".$this->ldel."\\s*[$]template\\.block\\.(child|parent))|\G([\S\s])/iS";

        do {
            if ($this->mbstring_overload ? preg_match($yy_global_pattern, mb_substr($this->data, $this->counter,2000000000,'latin1'), $yymatches) : preg_match($yy_global_pattern,$this->data, $yymatches, null, $this->counter)) {
                $yysubmatches = $yymatches;
                $yymatches = array_filter($yymatches, 'strlen'); // remove empty sub-patterns
                if (!count($yymatches)) {
                    throw new Exception('Error: lexing failed because a rule matched' .
                        ' an empty string.  Input "' . substr($this->data,
                        $this->counter, 5) . '... state CHILDBLOCK');
                }
                next($yymatches); // skip global match
                $this->token = key($yymatches); // token number
                if ($tokenMap[$this->token]) {
                    // extract sub-patterns for passing to lex function
                    $yysubmatches = array_slice($yysubmatches, $this->token + 1,
                        $tokenMap[$this->token]);
                } else {
                    $yysubmatches = array();
                }
                $this->value = current($yymatches); // token value
                $r = $this->{'yy_r6_' . $this->token}($yysubmatches);
                if ($r === null) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    // accept this token
                    return true;
                } elseif ($r === true) {
                    // we have changed state
                    // process this token in the new state
                    return $this->yylex();
                } elseif ($r === false) {
                    $this->counter += ($this->mbstring_overload ? mb_strlen($this->value,'latin1'): strlen($this->value));
                    $this->line += substr_count($this->value, "\n");
                    if ($this->counter >= ($this->mbstring_overload ? mb_strlen($this->data,'latin1'): strlen($this->data))) {
                        return false; // end of input
                    }
                    // skip this token
                    continue;
                }            } else {
                throw new Exception('Unexpected input at line' . $this->line .
                    ': ' . $this->data[$this->counter]);
            }
            break;
        } while (true);

    } // end function


    const CHILDBLOCK = 6;
    function yy_r6_1($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_BLOCKSOURCE;
    } else {
        $this->yypopstate();
        return true;
    }
    }
    function yy_r6_2($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_BLOCKSOURCE;
    } else {
        $this->yypopstate();
        return true;
    }
    }
    function yy_r6_3($yy_subpatterns)
    {

    if ($this->template->autoLiteral && (strpos(" \n\t\r", substr($this->value, $this->ldelLength, 1)) !== false)) {
        $this->token = TMTemplateparser::TP_BLOCKSOURCE;
    } else {
        $this->yypopstate();
        return true;
    }
    }
    function yy_r6_5($yy_subpatterns)
    {

    if ($this->mbstring_overload) {
        $to = mb_strlen($this->data, 'latin1');
    } else {
        $to = strlen($this->data);
    }
    preg_match("/".$this->ldel."\s*((\/)?block(\s|".$this->rdel.")|[\$]template\.block\.(child|parent)\s*".$this->rdel.")/",
        $this->data, $match, PREG_OFFSET_CAPTURE, $this->counter);
    if (isset($match[0][1])) {
        $to = $match[0][1];
    }
    if ($this->mbstring_overload) {
        $this->value = mb_substr($this->data, $this->counter, $to - $this->counter, 'latin1');
    } else {
        $this->value = substr($this->data, $this->counter, $to - $this->counter);
    }
    $this->token = TMTemplateParser::TP_BLOCKSOURCE;
    }

}
?>

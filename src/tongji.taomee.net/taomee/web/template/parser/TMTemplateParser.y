%stack_size 500
%name TP_
%declare_class {class TMTemplateParser}
%include_class
{
    public $lex;
    public $compiler;
    public $template;
    public $tpl;

    public $isXml;
    public $aspTags;
    public $currentBuffer;
    public $rootBuffer;

    public $retValue = 0;

    public static $prefixNumber = 0;
    public $strip = false;

    function __construct($lex, $compiler)
    {
        $this->lex = $lex;
        $this->compiler = $compiler;
        $this->template = $this->compiler->template;
        $this->tpl = $this->compiler->tpl;

        $this->compiler->hasVariableString = false;
        $this->compiler->prefixCode = array();
        $this->blockNestingLevel = 0;
        $this->isXml = false;
        $this->currentBuffer = $this->rootBuffer = new TMTemplateBuffer($this);
    }

    public static function escapeStartTag($tagText)
    {
        $tag = preg_replace('/\A<\?(.*)\z/', '<<?php ?>?\1', $tagText, -1 , $count); // Escape tag
        return $tag;
    }

    public static function escapeEndTag($tagText)
    {
        return '?<?php ?>>';
    }

    public function compileVariable($variable)
    {
        if (strpos($variable,'(') == 0) {
            // not a variable variable
            $var = trim($variable, '\'');
            $this->compiler->tagNoCache = $this->compiler->tagNoCache | $this->tpl->getVariable($var, null, true, false)->noCache;
            $this->tpl->properties['variables'][$var] = $this->compiler->tagNoCache | $this->compiler->noCache;
        }
        return '$_tpl->tplVars[' . $variable . ']->value';
    }
}

%token_prefix TP_

%parse_accept
{
    $this->retValue = $this->_retvalue;
}

%syntax_error
{
    $this->yymajor = $yymajor;
    $this->compiler->triggerTemplateError();
}

%stack_overflow
{
    $this->compiler->triggerTemplateError("Stack overflow in template parser");
}

%left VERT.
%left COLON.

start(res) ::= template. {
    res = $this->rootBuffer->toPHP();
}

// single template element
template ::= template_element(e). {
    if (e != null) {
        $this->currentBuffer->appendSubTree(e);
    }
}

// loop of elements
template ::= template template_element(e). {
    if (e != null) {
        $this->currentBuffer->appendSubTree(e);
    }
}

// empty template
template ::= .

// template elements

// Template tag
template_element(res) ::= template_tag(tt) RDEL. {
    if ($this->compiler->hasCode) {
        $tmp = '';
        foreach ($this->compiler->prefixCode as $code) {
            $tmp .= $code;
        }
        $this->compiler->prefixCode = array();
        res = new TMTemplateTag($this, $this->compiler->processNoCacheCode($tmp.tt, true));
    } else {
        res = null;
    }
    $this->compiler->hasVariableString = false;
    $this->blockNestingLevel = count($this->compiler->tagStack);
}

// comments
template_element(res) ::= COMMENT(c). {
    res = null;
}

// Literal
template_element(res) ::= literal(l). {
    res = new TMTemplateText($this, l);
}

// '<?php' tag
template_element(res) ::= PHPSTARTTAG(tt). {
    res = new TMTemplateText($this, self::escapeStartTag(tt));
}

// '?>' tag
template_element(res) ::= PHPENDTAG. {
    if ($this->isXml) {
        $this->compiler->tagNoCache = true;
        $this->isXml = false;
        $tmp = $this->tpl->hasNoCacheCode;
        res = new TMTemplateText($this, $this->compiler->processNoCacheCode("<?php echo '?>';?>\n", $this->compiler, true));
        $this->tpl->hasNoCacheCode = $tmp;
    } else {
        res = new TMTemplateText($this, '?<?php ?>>');
    }
}

// '<%' tag
template_element(res) ::= ASPSTARTTAG. {
    res = new TMTemplateText($this, '<<?php ?>%');
}

// '%>' tag
template_element(res) ::= ASPENDTAG. {
    res = new TMTemplateText($this, '%<?php ?>>');
}

template_element(res) ::= FAKEPHPSTARTTAG(o). {
    if ($this->strip) {
        res = new TMTemplateText($this, preg_replace('![\t ]*[\r\n]+[\t ]*!', '', self::escapeStartTag(o)));
    } else {
        res = new TMTemplateText($this, self::escapeStartTag(o));
    }
}

// XML tag
template_element(res) ::= XMLTAG. {
    $this->compiler->tagNoCache = true;
    $this->isXml = true;
    $tmp = $this->tpl->hasNoCacheCode;
    res = new TMTemplateText($this, $this->compiler->processNoCacheCode("<?php echo '<?xml';?>", $this->compiler, true));
    $this->tpl->hasNoCacheCode = $tmp;
}

// template text
template_element(res) ::= TEXT(o). {
    if ($this->strip) {
        res = new TMTemplateText($this, preg_replace('![\t ]*[\r\n]+[\t ]*!', '', o));
    } else {
        res = new TMTemplateText($this, o);
    }
}

// strip on
template_element ::= STRIPON(d). {
    $this->strip = true;
}

// strip off
template_element ::= STRIPOFF(d). {
    $this->strip = false;
}

// process source of inheritance child block
template_element ::= BLOCKSOURCE(s). {
    if ($this->strip) {
        TMTemplateCompileBlock::blockSource($this->compiler, preg_replace('![\t ]*[\r\n]+[\t ]*!', '', s));
    } else {
        TMTemplateCompileBlock::blockSource($this->compiler, s);
    }
}

// Litteral
literal(res) ::= LITERALSTART LITERALEND. {
    res = '';
}

literal(res) ::= LITERALSTART literal_elements(l) LITERALEND. {
    res = l;
}

literal_elements(res) ::= literal_elements(l1) literal_element(l2). {
    res = l1 . l2;
}

literal_elements(res) ::= . {
    res = '';
}

literal_element(res) ::= literal(l). {
    res = l;
}

literal_element(res) ::= LITERAL(l). {
    res = l;
}

literal_element(res) ::= PHPSTARTTAG(st). {
    res = self::escapeStartTag(st);
}

literal_element(res) ::= FAKEPHPSTARTTAG(st). {
    res = self::escapeStartTag(st);
}

literal_element(res) ::= PHPENDTAG(et). {
    res = self::escapeEndTag(et);
}

literal_element(res) ::= ASPSTARTTAG(st). {
    res = '<<?php ?>%';
}

literal_element(res) ::= ASPENDTAG(et). {
    res = '%<?php ?>>';
}

// output tags start here

// output with optional attributes
template_tag(res) ::= LDEL value(e). {
    res = $this->compiler->compileTag('PrintExpression', array(), array('value'=>e));
}

template_tag(res) ::= LDEL value(e) modifierlist(l) attributes(a). {
    res = $this->compiler->compileTag('PrintExpression', a, array('value' => e, 'modifierlist' => l));
}

template_tag(res) ::= LDEL value(e) attributes(a). {
    res = $this->compiler->compileTag('PrintExpression', a, array('value' => e));
}

template_tag(res)   ::= LDEL expr(e) modifierlist(l) attributes(a). {
    res = $this->compiler->compileTag('PrintExpression', a, array('value' => e,'modifierlist' => l));
}

template_tag(res)   ::= LDEL expr(e) attributes(a). {
    res = $this->compiler->compileTag('PrintExpression', a, array('value' => e));
}

// Template tags start here

// assign new style, can be removed.
template_tag(res) ::= LDEL DOLLAR ID(i) EQUAL value(e). {
    res = $this->compiler->compileTag('assign', array(array('value' => e), array('var' => "'" . i . "'")));
}

template_tag(res) ::= LDEL DOLLAR ID(i) EQUAL expr(e). {
    res = $this->compiler->compileTag('assign', array(array('value' => e), array('var' => "'" . i . "'")));
}

template_tag(res) ::= LDEL DOLLAR ID(i) EQUAL expr(e) attributes(a). {
    res = $this->compiler->compileTag('assign', array_merge(array(array('value' => e), array('var' => "'" . i . "'")), a));
}

template_tag(res) ::= LDEL varindexed(vi) EQUAL expr(e) attributes(a). {
    res = $this->compiler->compileTag('assign',
        array_merge(array(array('value' => e), array('var' => vi['var'])), a),
        array('template_internal_index' => vi['template_internal_index']));
}

template_tag(res) ::= LDEL ID(i) attributes(a). {
    res = $this->compiler->compileTag(i, a);
}

template_tag(res) ::= LDEL ID(i). {
    res = $this->compiler->compileTag(i, array());
}

// registered object tag
template_tag(res) ::= LDEL ID(i) PTR ID(m) attributes(a). {
    res = $this->compiler->compileTag(i, a, array('object_method' => m));
}

// tag with modifier and attributes
template_tag(res) ::= LDEL ID(i) modifierlist(l) attributes(a). {
    res = '<?php ob_start();?>' . $this->compiler->compileTag(i, a) . '<?php echo ';
    res .= $this->compiler->compileTag('Modifier', array(), array('modifierlist' => l, 'value' => 'ob_get_clean()')) . '?>';
}

// registered object tag with modifiers
template_tag(res) ::= LDEL ID(i) PTR ID(me) modifierlist(l) attributes(a). {
    res = '<?php ob_start();?>' . $this->compiler->compileTag(i, a, array('object_method' => me)) . '<?php echo ';
    res .= $this->compiler->compileTag('Modifier', array(), array('modifierlist' => l, 'value' => 'ob_get_clean()')) . '?>';
}

// {if}, {elseif} and {while} tag
template_tag(res) ::= LDELIF(i) expr(ie). {
    $tag = trim(substr(i, $this->lex->ldelLength));
    res = $this->compiler->compileTag(($tag == 'else if') ? 'elseif' : $tag, array(), array('if condition' => ie));
}

template_tag(res) ::= LDELIF(i) expr(ie) attributes(a). {
    $tag = trim(substr(i, $this->lex->ldelLength));
    res = $this->compiler->compileTag(($tag == 'else if') ? 'elseif' : $tag, a, array('if condition' => ie));
}

template_tag(res) ::= LDELIF(i) statement(ie). {
    $tag = trim(substr(i, $this->lex->ldelLength));
    res = $this->compiler->compileTag(($tag == 'else if') ? 'elseif' : $tag, array(), array('if condition' => ie));
}

template_tag(res) ::= LDELIF(i) statement(ie) attributes(a). {
    $tag = trim(substr(i, $this->lex->ldelLength));
    res = $this->compiler->compileTag(($tag == 'else if') ? 'elseif' : $tag, a, array('if condition' => ie));
}

// {for} tag
template_tag(res) ::= LDELFOR statements(st) SEMICOLON optspace expr(ie) SEMICOLON optspace DOLLAR varvar(v2) foraction(e2) attributes(a). {
    res = $this->compiler->compileTag('for',
        array_merge(a, array(array('start' => st), array('ifexp' => ie), array('var' => v2), array('step' => e2))), 1);
}

foraction(res) ::= EQUAL expr(e). {
    res = '=' . e;
}

foraction(res) ::= INCDEC(e). {
    res = e;
}

template_tag(res) ::= LDELFOR statement(st) TO expr(v) attributes(a). {
    res = $this->compiler->compileTag('for', array_merge(a, array(array('start' => st), array('to' => v))), 0);
}

template_tag(res) ::= LDELFOR statement(st) TO expr(v) STEP expr(v2) attributes(a). {
    res = $this->compiler->compileTag('for', array_merge(a, array(array('start' => st), array('to' => v), array('step' => v2))), 0);
}

// {foreach} tag
template_tag(res) ::= LDELFOREACH attributes(a). {
    res = $this->compiler->compileTag('foreach', a);
}

// {foreach $array as $var} tag
template_tag(res) ::= LDELFOREACH SPACE value(v1) AS DOLLAR varvar(v0) attributes(a). {
    res = $this->compiler->compileTag('foreach', array_merge(a, array(array('from' => v1), array('item' => v0))));
}

template_tag(res) ::= LDELFOREACH SPACE value(v1) AS DOLLAR varvar(v2) APTR DOLLAR varvar(v0) attributes(a). {
    res = $this->compiler->compileTag('foreach', array_merge(a, array(array('from' => v1), array('item' => v0), array('key' => v2))));
}

template_tag(res) ::= LDELFOREACH SPACE expr(e) AS DOLLAR varvar(v0) attributes(a). {
    res = $this->compiler->compileTag('foreach', array_merge(a, array(array('from' => e), array('item' => v0))));
}

template_tag(res) ::= LDELFOREACH SPACE expr(e) AS DOLLAR varvar(v1) APTR DOLLAR varvar(v0) attributes(a). {
    res = $this->compiler->compileTag('foreach',array_merge(a,array(array('from'=>e),array('item'=>v0),array('key'=>v1))));
}

// {setfilter}
template_tag(res) ::= LDELSETFILTER ID(m) modparameters(p). {
    res = $this->compiler->compileTag('setfilter', array(), array('modifier_list' => array(array_merge(array(m), p))));
}

template_tag(res) ::= LDELSETFILTER ID(m) modparameters(p) modifierlist(l). {
    res = $this->compiler->compileTag('setfilter', array(), array('modifier_list' => array_merge(array(array_merge(array(m), p)), l)));
}

// {$template.block.child} or {$template.block.parent}
template_tag(res) ::= LDEL TEMPLATEBLOCKCHILDPARENT(i). {
    if (i[strrpos(i, '.') + 1] == 'c') {
        // {$template.block.child}
        res = TMTemplateCompileBlock::compileChildBlock($this->compiler);
    } else {
        // {$template.block.parent}
        res = TMTemplateCompileBlock::compileParentBlock($this->compiler);
    }
}

// end of block tag  {/....}
template_tag(res) ::= LDELSLASH ID(i). {
    res = $this->compiler->compileTag(i . 'close', array());
}

template_tag(res) ::= LDELSLASH ID(i) modifierlist(l). {
    res = $this->compiler->compileTag(i . 'close', array(), array('modifier_list' => l));
}

// end of block object tag  {/....}
template_tag(res) ::= LDELSLASH ID(i) PTR ID(m). {
    res = $this->compiler->compileTag(i . 'close', array(), array('object_method' => m));
}

template_tag(res)   ::= LDELSLASH ID(i) PTR ID(m) modifierlist(l). {
    res = $this->compiler->compileTag(i . 'close', array(), array('object_method' => m, 'modifier_list' => l));
}

//Attributes of Template tags

// list of attributes
attributes(res) ::= attributes(a1) attribute(a2). {
    res = a1;
    res[] = a2;
}

// single attribute
attributes(res) ::= attribute(a). {
    res = array(a);
}

// no attributes
attributes(res) ::= . {
    res = array();
}

// attribute
attribute(res) ::= SPACE ID(v) EQUAL ID(id). {
    if (preg_match('~^true$~i', id)) {
        res = array(v => 'true');
    } elseif (preg_match('~^false$~i', id)) {
        res = array(v => 'false');
    } elseif (preg_match('~^null$~i', id)) {
        res = array(v => 'null');
    } else {
        res = array(v => "'" . id . "'");
    }
}

attribute(res) ::= ATTR(v) expr(e). {
    res = array(trim(v, " =\n\r\t") => e);
}

attribute(res) ::= ATTR(v) value(e). {
    res = array(trim(v, " =\n\r\t") => e);
}

attribute(res) ::= SPACE ID(v). {
    res = "'" . v . "'";
}

attribute(res) ::= SPACE expr(e). {
    res = e;
}

attribute(res) ::= SPACE value(v). {
    res = v;
}

attribute(res) ::= SPACE INTEGER(i) EQUAL expr(e). {
    res = array(i => e);
}

// statement
statements(res) ::= statement(s). {
    res = array(s);
}

statements(res) ::= statements(s1) COMMA statement(s). {
    s1[] = s;
    res = s1;
}

statement(res) ::= DOLLAR varvar(v) EQUAL expr(e). {
    res = array('var' => v, 'value' => e);
}

statement(res) ::= varindexed(vi) EQUAL expr(e). {
    res = array('var' => vi, 'value' => e);
}

statement(res) ::= OPENP statement(st) CLOSEP. {
    res = st;
}

// expressions

// single value
expr(res) ::= value(v). {
    res = v;
}

// ternary
expr(res) ::= ternary(v). {
    res = v;
}

// arithmetic expression
expr(res) ::= expr(e) MATH(m) value(v). {
    res = e . trim(m) . v;
}

expr(res) ::= expr(e) UNIMATH(m) value(v). {
    res = e . trim(m) . v;
}

// bit operation
expr(res) ::= expr(e) ANDSYM(m) value(v). {
    res = e . trim(m) . v;
}

// array
expr(res) ::= array(a). {
    res = a;
}

// modifier
expr(res) ::= expr(e) modifierlist(l). {
    res = $this->compiler->compileTag('Modifier', array(), array('value' => e, 'modifierlist' => l));
}

// if expression

// simple expression
expr(res) ::= expr(e1) ifcond(c) expr(e2). {
    res = e1 . c . e2;
}

expr(res) ::= expr(e1) ISIN array(a).  {
    res = 'in_array(' . e1 . ',' . a . ')';
}

expr(res) ::= expr(e1) ISIN value(v).  {
    res = 'in_array(' . e1 . ',(array)' . v . ')';
}

expr(res) ::= expr(e1) lop(o) expr(e2).  {
    res = e1 . o . e2;
}

expr(res) ::= expr(e1) ISDIVBY expr(e2). {
    res = '!(' . e1 . ' % ' . e2 . ')';
}

expr(res) ::= expr(e1) ISNOTDIVBY expr(e2).  {
    res = '(' . e1 . ' % ' . e2 . ')';
}

expr(res) ::= expr(e1) ISEVEN. {
    res = '!(1 & ' . e1 . ')';
}

expr(res) ::= expr(e1) ISNOTEVEN. {
    res = '(1 & ' . e1 . ')';
}

expr(res) ::= expr(e1) ISEVENBY expr(e2). {
    res = '!(1 & ' . e1 . ' / ' . e2 . ')';
}

expr(res) ::= expr(e1) ISNOTEVENBY expr(e2). {
    res = '(1 & ' . e1 . ' / ' . e2 . ')';
}

expr(res) ::= expr(e1) ISODD.  {
    res = '(1 & ' . e1 . ')';
}

expr(res) ::= expr(e1) ISNOTODD. {
    res = '!(1 & ' . e1 . ')';
}

expr(res) ::= expr(e1) ISODDBY expr(e2). {
    res = '(1 & ' . e1 . ' / ' . e2 . ')';
}

expr(res) ::= expr(e1) ISNOTODDBY expr(e2).  {
    res = '!(1 & ' . e1 . ' / ' . e2 . ')';
}

expr(res) ::= value(v1) INSTANCEOF(i) ID(id). {
    res = v1 . i . id;
}

expr(res) ::= value(v1) INSTANCEOF(i) value(v2). {
    self::$prefixNumber++;
    $this->compiler->prefixCode[] = '<?php $_tmp' . self::$prefixNumber . '=' . v2 . ';?>';
    res = v1 . i . '$_tmp' . self::$prefixNumber;
}

// ternary
ternary(res) ::= OPENP expr(v) CLOSEP QMARK DOLLAR ID(e1) COLON expr(e2). {
    res = v . ' ? ' . $this->compileVariable("'" . e1 . "'") . ' : ' . e2;
}

ternary(res) ::= OPENP expr(v) CLOSEP QMARK expr(e1) COLON expr(e2). {
    res = v . ' ? ' . e1 . ' : ' . e2;
}

// value
value(res) ::= variable(v). {
    res = v;
}

// +/- value
value(res) ::= UNIMATH(m) value(v). {
    res = m . v;
}

// logical negation
value(res) ::= NOT value(v). {
    res = '!' . v;
}

value(res) ::= TYPECAST(t) value(v). {
    res = t . v;
}

value(res) ::= variable(v) INCDEC(o). {
    res = v . o;
}

// numeric
value(res) ::= HEX(n). {
    res = n;
}

value(res) ::= INTEGER(n). {
    res = n;
}

value(res) ::= INTEGER(n1) DOT INTEGER(n2). {
    res = n1 . '.' . n2;
}

value(res) ::= INTEGER(n1) DOT. {
    res = n1 . '.';
}

value(res) ::= DOT INTEGER(n1). {
    res = '.' . n1;
}

// ID, true, false, null
value(res) ::= ID(id). {
    if (preg_match('~^true$~i', id)) {
        res = 'true';
    } elseif (preg_match('~^false$~i', id)) {
        res = 'false';
    } elseif (preg_match('~^null$~i', id)) {
        res = 'null';
    } else {
        res = "'" . id . "'";
    }
}

// function call
value(res) ::= function(f). {
    res = f;
}

// expression
value(res) ::= OPENP expr(e) CLOSEP. {
    res = "(" . e . ")";
}

// singele quoted string
value(res) ::= SINGLEQUOTESTRING(t). {
    res = t;
}

// double quoted string
value(res) ::= doublequoted_with_quotes(s). {
    res = s;
}

// static class access
value(res) ::= ID(c) DOUBLECOLON static_class_access(r). {
    res = c . '::' . r;
}

value(res) ::= varindexed(vi) DOUBLECOLON static_class_access(r). {
    if (vi['var'] == '\'template\'') {
        res = $this->compiler->compileTag('SpecialVariable', array(), vi['template_internal_index']) . '::' . r;
    } else {
        res = $this->compileVariable(vi['var']) . vi['template_internal_index'] . '::' . r;
    }
}

// Template tag
value(res) ::= template_tag(st) RDEL. {
    self::$prefixNumber++;
    $this->compiler->prefixCode[] = '<?php ob_start();?>' . st . '<?php $_tmp' . self::$prefixNumber . '=ob_get_clean();?>';
    res = '$_tmp' . self::$prefixNumber;
}

value(res) ::= value(v) modifierlist(l). {
    res = $this->compiler->compileTag('Modifier', array(), array('value' => v, 'modifierlist' => l));
}


// variables
// Template variable (optional array)
variable(res) ::= varindexed(vi). {
    if (vi['var'] == '\'template\'') {
        $templateVar = $this->compiler->compileTag('SpecialVariable', array(), vi['template_internal_index']);
        res = $templateVar;
    } else {
        // used for array reset, next, prev, end, current
        $this->lastVariable = vi['var'];
        $this->lastIndex = vi['template_internal_index'];
        res = $this->compileVariable(vi['var']) . vi['template_internal_index'];
    }
}

// variable with property
variable(res) ::= DOLLAR varvar(v) AT ID(p). {
    res = '$_tpl->tplVars[' . v . ']->' . p;
}

// object
variable(res) ::= object(o). {
    res = o;
}

varindexed(res) ::= DOLLAR varvar(v) arrayindex(a). {
    res = array('var'=>v, 'template_internal_index' => a);
}

// array index

// multiple array index
arrayindex(res) ::= arrayindex(a1) indexdef(a2). {
    res = a1 . a2;
}

// no array index
arrayindex ::= . {
    return;
}

// single index definition
indexdef(res) ::= DOT DOLLAR varvar(v).  {
    res = '[' . $this->compileVariable(v) . ']';
}

indexdef(res) ::= DOT DOLLAR varvar(v) AT ID(p). {
    res = '[' . $this->compileVariable(v) . '->' . p . ']';
}

indexdef(res) ::= DOT ID(i). {
    res = "['" . i . "']";
}

indexdef(res) ::= DOT INTEGER(n). {
    res = "[" . n . "]";
}

indexdef(res) ::= DOT LDEL expr(e) RDEL. {
    res = "[" . e . "]";
}

// section tag index
indexdef(res) ::= OPENB ID(i)CLOSEB. {
    res = '[' . $this->compiler->compileTag('SpecialVariable', array(), '[\'section\'][\'' . i . '\'][\'index\']') . ']';
}

indexdef(res) ::= OPENB ID(i) DOT ID(i2) CLOSEB. {
    res = '[' . $this->compiler->compileTag('SpecialVariable', array(), '[\'section\'][\'' . i . '\'][\'' . i2 . '\']') . ']';
}

// PHP style index
indexdef(res) ::= OPENB expr(e) CLOSEB. {
    res = "[" . e . "]";
}

// assign append array
indexdef(res)  ::= OPENB CLOSEB. {
    res = '[]';
}

// variable variable names

// singel identifier element
varvar(res) ::= varvarele(v). {
    res = v;
}

// sequence of identifier elements
varvar(res) ::= varvar(v1) varvarele(v2). {
    res = v1 . '.' . v2;
}

// fix sections of element
varvarele(res) ::= ID(s). {
    res = '\'' . s . '\'';
}

// variable sections of element
varvarele(res) ::= LDEL expr(e) RDEL. {
    res = '(' . e . ')';
}

// objects
object(res) ::= varindexed(vi) objectchain(oc). {
    if (vi['var'] == '\'template\'') {
        res =  $this->compiler->compileTag('SpecialVariable', array(), vi['template_internal_index']) . oc;
    } else {
        res = $this->compileVariable(vi['var']) . vi['template_internal_index'] . oc;
    }
}

// single element
objectchain(res) ::= objectelement(oe). {
    res = oe;
}

// chain of elements
objectchain(res) ::= objectchain(oc) objectelement(oe). {
    res = oc . oe;
}

// variable
objectelement(res) ::= PTR ID(i) arrayindex(a). {
    res = '->' . i . a;
}

objectelement(res) ::= PTR DOLLAR varvar(v) arrayindex(a). {
    res = '->{' . $this->compileVariable(v) . a . '}';
}

objectelement(res) ::= PTR LDEL expr(e) RDEL arrayindex(a). {
    res = '->{' . e . a . '}';
}

objectelement(res) ::= PTR ID(ii) LDEL expr(e) RDEL arrayindex(a). {
    res = '->{\'' . ii . '\'.' . e . a . '}';
}

// method
objectelement(res) ::= PTR method(f).  {
    res = '->' . f;
}

// function
function(res) ::= ID(f) OPENP params(p) CLOSEP. {
    if (strcasecmp(f,'isset') === 0 || strcasecmp(f,'empty') === 0 || strcasecmp(f,'array') === 0 || is_callable(f)) {
        $funcName = strtolower(f);
        if ($funcName == 'isset') {
            if (count(p) == 0) {
                $this->compiler->triggerTemplateError('Illegal number of paramer in "isset()"');
            }
            res = f . "(" . str_replace("')->value", "', null, true, false)->value", implode(',', p)) . ")";
        } elseif (in_array($funcName, array('empty', 'reset', 'current', 'end', 'prev', 'next'))) {
            if (count(p) != 1) {
                $this->compiler->triggerTemplateError('Illegal number of paramer in "empty()"');
            }
            if ($funcName == 'empty') {
                res = $funcName . '(' . str_replace("')->value", "', null, true, false)->value", p[0]) . ')';
            } else {
                res = $funcName . '(' . p[0] . ')';
            }
        } else {
            res = f . "(". implode(',', p) . ")";
        }
    } else {
        $this->compiler->triggerTemplateError("Unknown function \"" . f . "\"");
    }
}

// method
method(res) ::= ID(f) OPENP params(p) CLOSEP. {
    res = f . "(" . implode(',', p) . ")";
}

method(res) ::= DOLLAR ID(f) OPENP params(p) CLOSEP. {
    self::$prefixNumber++;
    $this->compiler->prefixCode[] = '<?php $_tmp' . self::$prefixNumber . '=' . $this->compileVariable("'" . f . "'") . ';?>';
    res = '$_tmp' . self::$prefixNumber . '(' . implode(',', p) . ')';
}

// function/method parameter

// multiple parameters
params(res) ::= params(p) COMMA expr(e). {
    res = array_merge(p, array(e));
}

// single parameter
params(res) ::= expr(e). {
    res = array(e);
}

// no parameter
params(res) ::= . {
    res = array();
}

// modifier

modifierlist(res) ::= modifierlist(l) modifier(m) modparameters(p). {
    res = array_merge(l, array(array_merge(m, p)));
}

modifierlist(res) ::= modifier(m) modparameters(p). {
    res = array(array_merge(m, p));
}

modifier(res) ::= VERT AT ID(m). {
    res = array(m);
}

modifier(res) ::= VERT ID(m). {
    res = array(m);
}

// modifier parameter

// multiple parameter
modparameters(res) ::= modparameters(mps) modparameter(mp). {
    res = array_merge(mps, mp);
}

// no parameter
modparameters(res) ::= . {
    res = array();
}

// parameter expression
modparameter(res) ::= COLON value(mp). {
    res = array(mp);
}

modparameter(res) ::= COLON array(mp). {
    res = array(mp);
}

// static class methode call
static_class_access(res) ::= method(m). {
    res = m;
}

// static class methode call with object chainig
static_class_access(res) ::= method(m) objectchain(oc). {
    res = m . oc;
}

// static class constant
static_class_access(res) ::= ID(v). {
    res = v;
}

// static class variables
static_class_access(res) ::=  DOLLAR ID(v) arrayindex(a). {
    res = '$' . v . a;
}

// static class variables with object chain
static_class_access(res) ::= DOLLAR ID(v) arrayindex(a) objectchain(oc). {
    res = '$' . v . a . oc;
}

// if conditions and operators
ifcond(res) ::= EQUALS. {
    res = '==';
}

ifcond(res) ::= NOTEQUALS. {
    res = '!=';
}

ifcond(res) ::= GREATERTHAN. {
    res = '>';
}

ifcond(res) ::= LESSTHAN. {
    res = '<';
}

ifcond(res) ::= GREATEREQUAL. {
    res = '>=';
}

ifcond(res) ::= LESSEQUAL. {
    res = '<=';
}

ifcond(res) ::= IDENTITY. {
    res = '===';
}

ifcond(res) ::= NONEIDENTITY. {
    res = '!==';
}

ifcond(res) ::= MOD. {
    res = '%';
}

lop(res) ::= LAND. {
    res = '&&';
}

lop(res) ::= LOR. {
    res = '||';
}

lop(res) ::= LXOR. {
    res = ' XOR ';
}

// array element assignment

array(res) ::= OPENB arrayelements(a) CLOSEB. {
    res = 'array(' . a . ')';
}

arrayelements(res) ::= arrayelement(a). {
    res = a;
}

arrayelements(res) ::= arrayelements(a1) COMMA arrayelement(a). {
    res = a1 . ',' . a;
}

arrayelements ::= . {
    return;
}

arrayelement(res) ::= value(e1) APTR expr(e2). {
    res = e1 . '=>' . e2;
}

arrayelement(res) ::= ID(i) APTR expr(e2). {
    res = '\'' . i . '\'=>' . e2;
}

arrayelement(res) ::= expr(e). {
    res = e;
}


// double qouted strings

doublequoted_with_quotes(res) ::= QUOTE QUOTE. {
    res = "''";
}

doublequoted_with_quotes(res) ::= QUOTE doublequoted(s) QUOTE. {
    res = s->toPHP();
}

doublequoted(res) ::= doublequoted(o1) doublequotedcontent(o2). {
    o1->appendSubTree(o2);
    res = o1;
}

doublequoted(res) ::= doublequotedcontent(o). {
    res = new TMTemplateDoubleQuoted($this, o);
}

doublequotedcontent(res) ::= BACKTICK variable(v) BACKTICK. {
    res = new TMTemplateCode($this, '(string)' . v);
}

doublequotedcontent(res) ::= BACKTICK expr(e) BACKTICK. {
    res = new TMTemplateCode($this, '(string)' . e);
}

doublequotedcontent(res) ::= DOLLARID(i). {
    res = new TMTemplateCode($this, '(string)$_tpl->tplVars[\'' . substr(i, 1) . '\']->value');
}

doublequotedcontent(res) ::= LDEL variable(v) RDEL. {
    res = new TMTemplateCode($this, '(string)' . v);
}

doublequotedcontent(res) ::= LDEL expr(e) RDEL. {
    res = new TMTemplateCode($this, '(string)(' . e . ')');
}

doublequotedcontent(res) ::= template_tag(st) RDEL. {
    res = new TMTemplateTag($this, st);
}

doublequotedcontent(res) ::= TEXT(o). {
    res = new TMTemplateDoubleQuotedContent($this, o);
}

// optional space

optspace(res) ::= SPACE(s). {
    res = s;
}

optspace(res) ::= . {
    res = '';
}

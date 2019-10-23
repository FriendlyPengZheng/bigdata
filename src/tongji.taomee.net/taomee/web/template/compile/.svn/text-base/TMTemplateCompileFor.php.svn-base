<?php
/**
 * Template Compile For
 * Compiles the {for} {forelse} {/for} tags
 */

/**
 * Template Compile For Class
 */
class TMTemplateCompileFor extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {for} tag
     *
     * - {for $var in $array}
     * For looping over arrays or iterators
     *
     * - {for $x=0; $x<$y; $x++}
     * For general loops
     *
     * The parser is gereration different sets of attribute by which this compiler can
     * determin which syntax is used.
     *
     * @param  array  $args      array with attributes from parser
     * @param  object $compiler  compiler object
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        if ($parameter == 0) {
            $this->requiredAttributes = array('start', 'to');
            $this->optionalAttributes = array('max', 'step');
        } else {
            $this->requiredAttributes = array('start', 'ifexp', 'var', 'step');
            $this->optionalAttributes = array();
        }
        $attr = $this->getAttributes($compiler, $args);
        $output = '<?php';
        if ($parameter == 1) {
            foreach ($attr['start'] as $statement) {
                $output .= " \$_tpl->tplVars[$statement[var]] = new TMTemplateVariable;"
                         . " \$_tpl->tplVars[$statement[var]]->value = $statement[value];\n";
            }
            $output .= " if ($attr[ifexp]) { for (\$foo=true; $attr[ifexp];"
                     . " \$_tpl->tplVars[$attr[var]]->value$attr[step]) {\n";
        } else {
            $statement = $attr['start'];
            $output .= " \$_tpl->tplVars[$statement[var]] = new TMTemplateVariable;";
            if (isset($attr['step'])) {
                $output .= " \$_tpl->tplVars[$statement[var]]->step = $attr[step];";
            } else {
                $output .= " \$_tpl->tplVars[$statement[var]]->step = 1;";
            }
            if (isset($attr['max'])) {
                $output .= " \$_tpl->tplVars[$statement[var]]->total = (int)min("
                         . "ceil((\$_tpl->tplVars[$statement[var]]->step > 0 ?"
                         . " $attr[to] + 1 - ($statement[value]) : $statement[value] - ($attr[to]) + 1)"
                         . " / abs(\$_tpl->tplVars[$statement[var]]->step)), $attr[max]);\n";
            } else {
                $output .= " \$_tpl->tplVars[$statement[var]]->total ="
                         . " (int)ceil((\$_tpl->tplVars[$statement[var]]->step > 0 ?"
                         . " $attr[to] + 1 - ($statement[value]) : $statement[value] - ($attr[to]) + 1)"
                         . " / abs(\$_tpl->tplVars[$statement[var]]->step));\n";
            }
            $output .= "if (\$_tpl->tplVars[$statement[var]]->total > 0) {\n"
                     . "for (\$_tpl->tplVars[$statement[var]]->value = $statement[value],"
                     . " \$_tpl->tplVars[$statement[var]]->iteration = 1;"
                     . " \$_tpl->tplVars[$statement[var]]->iteration <= \$_tpl->tplVars[$statement[var]]->total;"
                     . " \$_tpl->tplVars[$statement[var]]->value += \$_tpl->tplVars[$statement[var]]->step,"
                     . " \$_tpl->tplVars[$statement[var]]->iteration++) {\n"
                     . "\$_tpl->tplVars[$statement[var]]->first = \$_tpl->tplVars[$statement[var]]->iteration == 1;"
                     . "\$_tpl->tplVars[$statement[var]]->last ="
                     . " \$_tpl->tplVars[$statement[var]]->iteration == \$_tpl->tplVars[$statement[var]]->total;";
        }
        $output .= '?>';
        $this->openTag($compiler, 'for', array('for', $compiler->noCache));
        $compiler->noCache = $compiler->noCache | $compiler->tagNoCache;
        return $output;
    }
}

/**
 * Template Compile Forelse Class
 */
class TMTemplateCompileForelse extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {forelse} tag
     *
     * @param  array  $args      array with attributes from parser
     * @param  object $compiler  compiler object
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $attr = $this->getAttributes($compiler, $args);
        list($openTag, $noCache) = $this->closeTag($compiler, array('for'));
        $this->openTag($compiler, 'forelse', array('forelse', $noCache));
        return '<?php }} else { ?>';
    }
}

/**
 * Template Compile Forclose Class
 */
class TMTemplateCompileForclose extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {/for} tag
     *
     * @param  array  $args      array with attributes from parser
     * @param  object $compiler  compiler object
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $attr = $this->getAttributes($compiler, $args);
        if ($compiler->noCache) {
            $compiler->tagNoCache = true;
        }
        list($openTag, $compiler->noCache) = $this->closeTag($compiler, array('for', 'forelse'));
        if ($openTag == 'forelse') {
            return '<?php } ?>';
        } else {
            return '<?php }} ?>';
        }
    }
}

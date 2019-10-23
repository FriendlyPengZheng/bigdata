<?php
/**
 * Template Compile Foreach
 * Compiles the {foreach} {foreachelse} {/foreach} tags
 */

/**
 * Template Compile Foreach Class
 */
class TMTemplateCompileForeach extends TMTemplateCompileBase
{
    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $requiredAttributes = array('from', 'item');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $optionalAttributes = array('name', 'key');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $shortTagOrder = array('from', 'item', 'key', 'name');

    /**
     * Compiles code for the {foreach} tag
     *
     * @param  array  $args      array with attributes from parser
     * @param  object $compiler  compiler object
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $tpl = $compiler->tpl;
        $attr = $this->getAttributes($compiler, $args);
        $from = $attr['from'];
        $item = $attr['item'];
        // as前后的变量不能一样
        if (!strncmp("\$_tpl->tplVars[$item]", $from, strlen($item) + 16)) {
            $compiler->triggerTemplateError(
                "Item variable {$item} may not be the same variable as 'from'",
                $compiler->lex->line);
        }
        if (isset($attr['key'])) {
            $key = $attr['key'];
        } else {
            $key = null;
        }
        $this->openTag($compiler, 'foreach', array('foreach', $compiler->noCache, $item, $key));
        $compiler->noCache = $compiler->noCache | $compiler->tagNoCache;
        if (isset($attr['name'])) {
            $name = $attr['name'];
            $hasName = true;
            $templateVarName = '$template.foreach.' . trim($name, '\'"') . '.';
        } else {
            $name = null;
            $hasName = false;
        }
        $itemVarName = '$' . trim($item, '\'"') . '@';
        if ($hasName) {
            $usesTemplateFirst = strpos($tpl->source->content, $templateVarName . 'first') !== false;
            $usesTemplateLast = strpos($tpl->source->content, $templateVarName . 'last') !== false;
            $usesTemplateIndex = strpos($tpl->source->content, $templateVarName . 'index') !== false;
            $usesTemplateIteration = strpos($tpl->source->content, $templateVarName . 'iteration') !== false;
            $usesTemplateShow = strpos($tpl->source->content, $templateVarName . 'show') !== false;
            $usesTemplateTotal = strpos($tpl->source->content, $templateVarName . 'total') !== false;
        } else {
            $usesTemplateFirst = false;
            $usesTemplateLast = false;
            $usesTemplateTotal = false;
            $usesTemplateShow = false;
        }
        $usesPropFirst = $usesTemplateFirst || strpos($tpl->source->content, $itemVarName . 'first') !== false;
        $usesPropLast = $usesTemplateLast || strpos($tpl->source->content, $itemVarName . 'last') !== false;
        $usesPropIndex = $usesPropFirst || strpos($tpl->source->content, $itemVarName . 'index') !== false;
        $usesPropIteration = $usesPropLast || strpos($tpl->source->content, $itemVarName . 'iteration') !== false;
        $usesPropShow = strpos($tpl->source->content, $itemVarName . 'show') !== false;
        $usesPropTotal = $usesTemplateTotal || $usesTemplateShow || $usesPropShow || $usesPropLast
                            || strpos($tpl->source->content, $itemVarName . 'total') !== false;
        $output = "<?php \$_tpl->tplVars[$item] = new TMTemplateVariable; \$_tpl->tplVars[$item]->_loop = false;\n";
        if ($key != null) {
            $output .= "\$_tpl->tplVars[$key] = new TMTemplateVariable;\n";
        }
        $output .= "\$_from = $from; if (!is_array(\$_from) && !is_object(\$_from)) { settype(\$_from, 'array'); }\n";
        if ($usesPropTotal) {
            $output .= "\$_tpl->tplVars[$item]->total = \$_tpl->_count(\$_from);\n";
        }
        if ($usesPropIteration) {
            $output .= "\$_tpl->tplVars[$item]->iteration = 0;\n";
        }
        if ($usesPropIndex) {
            $output .= "\$_tpl->tplVars[$item]->index = -1;\n";
        }
        if ($usesPropShow) {
            $output .= "\$_tpl->tplVars[$item]->show = (\$_tpl->tplVars[$item]->total > 0);\n";
        }
        if ($hasName) {
            if ($usesTemplateTotal) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['total']"
                         . " = \$_tpl->tplVars[$item]->total;\n";
            }
            if ($usesTemplateIteration) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['iteration'] = 0;\n";
            }
            if ($usesTemplateIndex) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['index'] = -1;\n";
            }
            if ($usesTemplateShow) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['show']"
                         . " = (\$_tpl->tplVars[$item]->total > 0);\n";
            }
        }
        $output .= "foreach (\$_from as \$_tpl->tplVars[$item]->key => \$_tpl->tplVars[$item]->value) {\n"
                 . "\$_tpl->tplVars[$item]->_loop = true;\n";
        if ($key != null) {
            $output .= "\$_tpl->tplVars[$key]->value = \$_tpl->tplVars[$item]->key;\n";
        }
        if ($usesPropIteration) {
            $output .= "\$_tpl->tplVars[$item]->iteration++;\n";
        }
        if ($usesPropIndex) {
            $output .= "\$_tpl->tplVars[$item]->index++;\n";
        }
        if ($usesPropFirst) {
            $output .= "\$_tpl->tplVars[$item]->first = \$_tpl->tplVars[$item]->index === 0;\n";
        }
        if ($usesPropLast) {
            $output .= "\$_tpl->tplVars[$item]->last"
                     . " = \$_tpl->tplVars[$item]->iteration === \$_tpl->tplVars[$item]->total;\n";
        }
        if ($hasName) {
            if ($usesTemplateFirst) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['first']"
                         . " = \$_tpl->tplVars[$item]->first;\n";
            }
            if ($usesTemplateIteration) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['iteration']++;\n";
            }
            if ($usesTemplateIndex) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['index']++;\n";
            }
            if ($usesTemplateLast) {
                $output .= "\$_tpl->tplVars['template']->value['foreach'][$name]['last']"
                         . " = \$_tpl->tplVars[$item]->last;\n";
            }
        }
        $output .= "?>";
        return $output;
    }
}

/**
 * Template Compile Foreachelse Class
 */
class TMTemplateCompileForeachelse extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {foreachelse} tag
     *
     * @param  array  $args      array with attributes from parser
     * @param  object $compiler  compiler object
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $attr = $this->getAttributes($compiler, $args);
        list($openTag, $noCache, $item, $key) = $this->closeTag($compiler, array('foreach'));
        $this->openTag($compiler, 'foreachelse', array('foreachelse', $noCache, $item, $key));
        return "<?php }\nif (!\$_tpl->tplVars[$item]->_loop) {\n?>";
    }
}

/**
 * Template Compile Foreachclose Class
 */
class TMTemplateCompileForeachclose extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {/foreach} tag
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
        list($openTag, $compiler->noCache, $item, $key) = $this->closeTag($compiler, array('foreach', 'foreachelse'));
        return "<?php } ?>";
    }
}

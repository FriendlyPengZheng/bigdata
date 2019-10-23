<?php
/**
 * Template Compile If
 * Compiles the {if} {else} {elseif} {/if} tags
 */

/**
 * Template Compile If Class
 */
class TMTemplateCompileIf extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {if} tag
     * @param array  $args      array with attributes from parser
     * @param object $compiler  compiler object
     * @param array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $attr = $this->getAttributes($compiler, $args);
        $this->openTag($compiler, 'if', array(1, $compiler->noCache));
        $compiler->noCache = $compiler->noCache | $compiler->tagNoCache;
        if (!array_key_exists("if condition", $parameter)) {
            $compiler->triggerTemplateError("Missing if condition", $compiler->lex->line);
        }
        // {if $a = $b + $c} something {/if}
        // {if $a[1] = $b + $c} something {/if}
        if (is_array($parameter['if condition'])) {
            if ($compiler->noCache) {
                $noCache = ',true';
                if (is_array($parameter['if condition']['var'])) {
                    $compiler->tpl->tplVars[trim($parameter['if condition']['var']['var'], "'")]
                                        = new TMTemplateVariable(null, true);
                } else {
                    $compiler->tpl->tplVars[trim($parameter['if condition']['var'], "'")]
                                        = new TMTemplateVariable(null, true);
                }
            } else {
                $noCache = '';
            }
            if (is_array($parameter['if condition']['var'])) {
                $output = "<?php if (!isset(\$_tpl->tplVars["
                        . $parameter['if condition']['var']['var']
                        . "]) || !is_array(\$_tpl->tplVars["
                        . $parameter['if condition']['var']['var']
                        . "]->value)) \$_tpl->createLocalArrayVariable("
                        . $parameter['if condition']['var']['var']
                        . "$noCache);\n"
                        . "if (\$_tpl->tplVars["
                        . $parameter['if condition']['var']['var']
                        . "]->value"
                        . $parameter['if condition']['var']['template_internal_index']
                        . " = "
                        . $parameter['if condition']['value']
                        . ") {?>";
            } else {
                $output = "<?php if (!isset(\$_tpl->tplVars["
                        . $parameter['if condition']['var']
                        . "])) \$_tpl->tplVars["
                        . $parameter['if condition']['var']
                        . "] = new TMTemplateVariable(null{$noCache});"
                        . "if (\$_tpl->tplVars["
                        . $parameter['if condition']['var']
                        . "]->value = "
                        . $parameter['if condition']['value']
                        . ") {?>";
            }
            return $output;
        } else {
            return "<?php if ({$parameter['if condition']}) {?>";
        }
    }
}

/**
 * Template Compile Else Class
 */
class TMTemplateCompileElse extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {else} tag
     * @param array  $args      array with attributes from parser
     * @param object $compiler  compiler object
     * @param array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        list($nesting, $compiler->tagNoCache) = $this->closeTag($compiler, array('if', 'elseif'));
        $this->openTag($compiler, 'else', array($nesting, $compiler->tagNoCache));
        return "<?php } else { ?>";
    }
}

/**
 * Template Compile ElseIf Class
 */
class TMTemplateCompileElseif extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {elseif} tag
     * @param array  $args      array with attributes from parser
     * @param object $compiler  compiler object
     * @param array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $attr = $this->getAttributes($compiler, $args);
        list($nesting, $compiler->tagNoCache) = $this->closeTag($compiler, array('if', 'elseif'));
        if (!array_key_exists("if condition", $parameter)) {
            $compiler->triggerTemplateError("Missing elseif condition", $compiler->lex->line);
        }
        if (is_array($parameter['if condition'])) {
            $conditionByAssign = true;
            if ($compiler->noCache) {
                $noCache = ',true';
                if (is_array($parameter['if condition']['var'])) {
                    $compiler->tpl->tplVars[trim($parameter['if condition']['var']['var'], "'")]
                                        = new TMTemplateVariable(null, true);
                } else {
                    $compiler->tpl->tplVars[trim($parameter['if condition']['var'], "'")]
                                        = new TMTemplateVariable(null, true);
                }
            } else {
                $noCache = '';
            }
        } else {
            $conditionByAssign = false;
        }

        if (empty($compiler->prefixCode)) {
            if ($conditionByAssign) {
                $this->openTag($compiler, 'elseif', array($nesting + 1, $compiler->tagNoCache));
                if (is_array($parameter['if condition']['var'])) {
                    $output = "<?php } else { if (!isset(\$_tpl->tplVars["
                            . $parameter['if condition']['var']['var']
                            . "]) || !is_array(\$_tpl->tplVars["
                            . $parameter['if condition']['var']['var']
                            . "]->value)) \$_tpl->createLocalArrayVariable("
                            . $parameter['if condition']['var']['var']
                            . "$noCache);\n"
                            . "if (\$_tpl->tplVars["
                            . $parameter['if condition']['var']['var']
                            . "]->value"
                            . $parameter['if condition']['var']['template_internal_index']
                            . " = "
                            . $parameter['if condition']['value']
                            . ") {?>";
                } else {
                    $output = "<?php } else { if (!isset(\$_tpl->tplVars["
                            . $parameter['if condition']['var']
                            . "])) \$_tpl->tplVars["
                            . $parameter['if condition']['var']
                            . "] = new TMTemplateVariable(null{$noCache});"
                            . "if (\$_tpl->tplVars["
                            . $parameter['if condition']['var']
                            . "]->value = "
                            . $parameter['if condition']['value']
                            . ") {?>";
                }
                return $output;
            } else {
                $this->openTag($compiler, 'elseif', array($nesting, $compiler->tagNoCache));
                return "<?php } elseif ({$parameter['if condition']}) {?>";
            }
        } else {
            $tmp = '';
            foreach ($compiler->prefixCode as $code) {
                $tmp .= $code;
            }
            $compiler->prefixCode = array();
            $this->openTag($compiler, 'elseif', array($nesting + 1, $compiler->tagNoCache));
            if ($conditionByAssign) {
                if (is_array($parameter['if condition']['var'])) {
                    $output = "<?php } else {?>{$tmp}<?php if (!isset(\$_tpl->tplVars["
                            . $parameter['if condition']['var']['var']
                            . "]) || !is_array(\$_tpl->tplVars["
                            . $parameter['if condition']['var']['var']
                            . "]->value)) \$_tpl->createLocalArrayVariable("
                            . $parameter['if condition']['var']['var']
                            . "$noCache);\n"
                            . "if (\$_tpl->tplVars["
                            . $parameter['if condition']['var']['var']
                            . "]->value"
                            . $parameter['if condition']['var']['template_internal_index']
                            . " = "
                            . $parameter['if condition']['value']
                            . ") {?>";
                } else {
                    $output = "<?php } else {?>{$tmp}<?php if (!isset(\$_tpl->tplVars["
                            . $parameter['if condition']['var']
                            . "])) \$_tpl->tplVars["
                            . $parameter['if condition']['var']
                            . "] = new TMTemplateVariable(null{$noCache});"
                            . "if (\$_tpl->tplVars["
                            . $parameter['if condition']['var']
                            . "]->value = "
                            . $parameter['if condition']['value']
                            . ") {?>";
                }
                return $output;
            } else {
                return "<?php } else {?>{$tmp}<?php if ({$parameter['if condition']}) {?>";
            }
        }
    }
}

/**
 * Template Compile Ifclose Class
 */
class TMTemplateCompileIfclose extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {/if} tag
     * @param array  $args      array with attributes from parser
     * @param object $compiler  compiler object
     * @param array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        if ($compiler->noCache) {
            $compiler->tagNoCache = true;
        }
        list($nesting, $compiler->noCache) = $this->closeTag($compiler, array('if', 'else', 'elseif'));
        $tmp = '';
        for ($i=0; $i<$nesting; $i++) {
            $tmp .= '}';
        }
        return "<?php {$tmp}?>";
    }
}

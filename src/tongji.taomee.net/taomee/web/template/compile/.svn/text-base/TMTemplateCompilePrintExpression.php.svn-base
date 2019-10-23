<?php
/**
 * Compile Print Expression
 * Compiles any tag which will output an expression or variable
 */
class TMTemplateCompilePrintExpression extends TMTemplateCompileBase
{
    /**
     * @var array Attribute definition: Overwrites base class.
     * @see TMTemplateCompileBase
     */
    public $optionalAttributes = array('assign');

    /**
     * @var array Attribute definition: Overwrites base class.
     * @see TMTemplateCompileBase
     */
    public $optionFlags = array('nocache', 'nofilter');

    /**
    * Compiles code for gererting output from any expression
    * @param array $args array with attributes from parser
    * @param object $compiler compiler object
    * @param array $parameter array with compilation parameter
    * @return string compiled code
    */
    public function compile($args, $compiler, $parameter)
    {
        // 检查并获取属性
        $attr = $this->getAttributes($compiler, $args);
        // 是否缓存
        if ($attr['nocache'] === true) {
            $compiler->tagNoCache = true;
        }
        if (isset($attr['assign'])) {
            $output = "<?php \$_tpl->assign({$attr['assign']}, {$parameter['value']}); ?>";
        } else {
            // 显示
            $output = $parameter['value'];
            if (!$attr['nofilter']) {
                // 自动转义HTML
                if ($compiler->tpl->template->escapeHtml) {
                    $output = "htmlspecialchars({$output}, ENT_QUOTES, '"
                            . addslashes(TMTemplate::$charset) . "')";
                }
            }
            $compiler->hasOutput = true;
            $output = "<?php echo {$output}; ?>";
        }
        return $output;
    }
}

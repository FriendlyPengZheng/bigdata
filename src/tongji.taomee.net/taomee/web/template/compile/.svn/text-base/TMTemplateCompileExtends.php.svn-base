<?php
/**
 * Template Compile Extends
 * Compiles the {extends} tag
 */
class TMTemplateCompileExtends extends TMTemplateCompileBase
{
    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $requiredAttributes = array('file');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $shortTagOrder = array('file');

    /**
     * Compiles code for the {extends} tag
     * @param  array  $args     array with attributes from parser
     * @param  object $compiler compiler object
     * @return string compiled code
     */
    public function compile($args, $compiler)
    {
        $attr = $this->getAttributes($compiler, $args);
        if ($attr['nocache'] === true) {
            $compiler->triggerTemplateError('Nocache option not allowed', $compiler->lex->line);
        }
        if (strpos($attr['file'], '$_tmp') !== false) {
            $compiler->triggerTemplateError('Illegal value for file attribute', $compiler->lex->line);
        }
        // 不允许变量文件名
        if ($compiler->hasVariableString
            || !(substr_count($attr['file'], '"') == 2 || substr_count($attr['file'], "'") == 2)
            || substr_count($attr['file'], '(') != 0
            || substr_count($attr['file'], '$_tpl->') != 0) {
            $compiler->triggerTemplateError('Variable template file name not allowed',
                                $compiler->lex->line);
        }
        $name = trim($attr['file'], "\"'");
        $tpl = new TMInternalTemplate($name, $compiler->template, $compiler->tpl);
        $uid = $tpl->source->uid;
        if (isset($compiler->extendsUid[$uid])) {
            $compiler->triggerTemplateError("Illegal recursive call of \"{$tpl->source->filepath}\"",
                                $this->lex->line - 1);
        }
        $compiler->extendsUid[$uid] = true;
        // 保存继承资源
        array_unshift($compiler->sources, $tpl->source);
        unset($tpl);
        $compiler->inheritanceChild = true;
        $compiler->lex->yypushstate(TMTemplateLexer::CHILDBODY);
        return '';
    }
}

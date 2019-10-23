<?php
/**
 * Template Compile Nocache
 * Compiles the {nocache} {/nocache} tags.
 */

/**
 * Template Compile Nocache Class
 */
class TMTemplateCompileNocache extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {nocache} tag
     * This tag does not generate compiled output. It only sets a compiler flag.
     * @param  array  $args     array with attributes from parser
     * @param  object $compiler compiler object
     * @return bool
     */
    public function compile($args, $compiler)
    {
        $attr = $this->getAttributes($compiler, $args);
        if ($attr['nocache'] === true) {
            $compiler->triggerTemplateError('nocache option not allowed', $compiler->lex->line);
        }
        if ($compiler->tpl->caching) {
            $this->openTag($compiler, 'nocache', $compiler->noCache);
            $compiler->noCache = true;
        }
        $compiler->hasCode = false;

        return true;
    }
}

/**
 * Template Compile Nocacheclose Class
 */
class TMTemplateCompileNocacheclose extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {/nocache} tag
     * This tag does not generate compiled output. It only sets a compiler flag.
     * @param  array  $args     array with attributes from parser
     * @param  object $compiler compiler object
     * @return bool
     */
    public function compile($args, $compiler)
    {
        $attr = $this->getAttributes($compiler, $args);
        if ($compiler->tpl->caching) {
            $compiler->noCache = $this->closeTag($compiler, 'nocache');
        }
        $compiler->hasCode = false;

        return true;
    }
}

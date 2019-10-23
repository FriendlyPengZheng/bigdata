<?php
/**
 * Base implementation for resource that don't use the compiler
 */
abstract class TMTemplateResourceUncompiled extends TMTemplateResource
{
    /**
     * Render and output the template (without using the compiler)
     *
     * @param  TMTemplateSource    $source   source object
     * @param  TMInternalTemplate  $template template object
     * @throws TMTemplateException on failure
     */
    abstract public function renderUncompiled(TMTemplateSource $source, TMInternalTemplate $template);

    /**
     * populate compiled object with compiled filepath
     *
     * @param TMTemplateCompiled $compiled compiled object
     * @param TMInternalTemplate $template template object (is ignored)
     */
    public function populateCompiledFilepath(TMTemplateCompiled $compiled, TMInternalTemplate $template)
    {
        $compiled->filepath = false;
        $compiled->timestamp = false;
        $compiled->exists = false;
    }
}

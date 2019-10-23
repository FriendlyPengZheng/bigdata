<?php
/**
 * Base implementation for resource that don't compile cache
 */
abstract class TMTemplateResourceRecompiled extends TMTemplateResource
{
    /**
     * populate Compiled Object with compiled filepath
     *
     * @param  TMTemplateCompiled $compiled compiled object
     * @param  TMInternalTemplate $template template object
     * @return void
     */
    public function populateCompiledFilepath(TMTemplateCompiled $compiled, TMInternalTemplate $template)
    {
        $compiled->filepath = false;
        $compiled->timestamp = false;
        $compiled->exists = false;
    }
}

<?php
/**
 * Template Resource File
 *
 * Implements the file system as resource for Template
 */
class TMTemplateResourceFile extends TMTemplateResource
{
    /**
     * Populate Source Object with meta data from Resource
     *
     * @param TMTemplateSource   $source    source object
     * @param TMInternalTemplate $template  template object
     */
    public function populate(TMTemplateSource $source, TMInternalTemplate $template = null)
    {
        $source->filepath = $this->buildFilepath($source, $template);
        if ($source->filepath !== false) {
            $source->uid = sha1($source->filepath);
            if ($source->template->compileCheck && !isset($source->timestamp)) {
                $source->timestamp = @filemtime($source->filepath);
                $source->exists = !!$source->timestamp;
            }
        }
    }

    /**
     * Populate Source Object with timestamp and exists from Resource
     *
     * @param TMTemplateSource $source source object
     */
    public function populateTimestamp(TMTemplateSource $source)
    {
        $source->timestamp = @filemtime($source->filepath);
        $source->exists = !!$source->timestamp;
    }

    /**
     * Load template's source from file into current template object
     *
     * @param  TMTemplateSource    $source source object
     * @return string              template source
     * @throws TMTemplateException if source cannot be loaded
     */
    public function getContent(TMTemplateSource $source)
    {
        if ($source->timestamp) {
            return file_get_contents($source->filepath);
        }
        throw new TMTemplateException("Unable to read template {$source->type} '{$source->name}'");
    }

    /**
     * Determine basename for compiled filename
     *
     * @param  TMTemplateSource $source source object
     * @return string           resource's basename
     */
    public function getBasename(TMTemplateSource $source)
    {
        $file = $source->name;
        if (($pos = strpos($file, ']')) !== false) {
            $file = substr($file, $pos + 1);
        }
        return basename($file);
    }
}

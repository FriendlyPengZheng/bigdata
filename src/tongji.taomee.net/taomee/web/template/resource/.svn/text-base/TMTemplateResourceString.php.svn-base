<?php
/**
 * Template Resource String
 */
class TMTemplateResourceString extends TMTemplateResource
{
    /**
     * populate Source Object with meta data from Resource
     *
     * @param  TMTemplateSource   $source    source object
     * @param  TMInternalTemplate $_template template object
     * @return void
     */
    public function populate(TMTemplateSource $source, TMInternalTemplate $_template = null)
    {
        $source->uid = $source->filepath = sha1($source->name);
        $source->timestamp = 0;
        $source->exists = true;
    }

    /**
     * Load template's source from resource name into current template object
     *
     * @uses decode() to decode base64 and urlencoded templateResources
     * @param  TMTemplateSource $source source object
     * @return string                 template source
     */
    public function getContent(TMTemplateSource $source)
    {
        return $this->decode($source->name);
    }

    /**
     * Decode base64 and urlencode
     *
     * @param  string $string template resource to decode
     * @return string decoded template resource
     */
    protected function decode($string)
    {
        if (($pos = strpos($string, ':')) !== false) {
            if (!strncmp($string, 'base64', 6)) {
                return base64_decode(substr($string, 7));
            } elseif (!strncmp($string, 'urlencode', 9)) {
                return urldecode(substr($string, 10));
            }
        }
        return $string;
    }

    /**
     * Modify resource name according to resource handlers specifications
     *
     * @param  TMTemplate $template     TMTemplate instance
     * @param  string     $resourceName resource name to make unique
     * @return string     unique resource name
     */
    protected function buildUniqueResourceName(TMTemplate $template, $resourceName)
    {
        return get_class($this) . '#' . $this->decode($resourceName);
    }

    /**
     * Determine basename for compiled filename
     * Always returns an empty string.
     * @param  TMTemplateSource $source source object
     * @return string r       esource's basename
     */
    protected function getBasename(TMTemplateSource $source)
    {
        return '';
    }
}

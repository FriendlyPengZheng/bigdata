<?php
/**
 * Template Resource Data Object
 * Cache Data Container for Template Files
 */
class TMTemplateCached
{
    /**
    * @var string Template Cache Id (TMInternalTemplate::$cacheId)
    */
    public $cacheId = null;

    /**
    * @var string Template Compile Id (TMInternalTemplate::$compileId)
    */
    public $compileId = null;

    /**
    * @var TMTemplateSource Source Object
    */
    public $source = null;

    /**
    * @var string Source Filepath
    */
    public $filepath = false;

    /**
    * @var string Source Content
    */
    public $content = null;

    /**
    * @var integer Source Timestamp
    */
    public $timestamp = false;

    /**
    * @var boolean Source Existence
    */
    public $exists = false;

    /**
    * @var boolean Cache Is Valid
    */
    public $valid = false;

    /**
    * @var boolean Cache was processed
    */
    public $processed = false;

    /**
    * @var TMTemplateCacheResource CacheResource Handler
    */
    public $handler = null;

    /**
     * create Cached Object container
     *
     * @param TMInternalTemplate $tpl template object
     */
    public function __construct(TMInternalTemplate $tpl)
    {
        // 属性赋值
        $this->compileId = $tpl->compileId;
        $this->cacheId = $tpl->cacheId;
        $this->source = $tpl->source;
        $tpl->cached = $this;
        $template = $tpl->template;
        // 加载缓存资源处理器
        $this->handler = $handler = TMTemplateCacheResource::load($template);
        // check if cache is valid
        if (!($tpl->caching == TMTemplate::CACHING_LIFETIME_CURRENT
                || $tpl->caching == TMTemplate::CACHING_LIFETIME_SAVED)
            || $tpl->source->recompiled) {
            $handler->populate($this, $tpl);
            return;
        }
        $handler->populate($this, $tpl);
        if ($this->timestamp === false || $template->forceCompile || $template->forceCache)
            $this->valid = false;
        else
            $this->valid = true;
        if ($this->valid && $tpl->caching == TMTemplate::CACHING_LIFETIME_CURRENT
            && $tpl->cacheLifetime >= 0 && time() > ($this->timestamp + $tpl->cacheLifetime)) {
            $this->valid = false;
        }
        if ($this->valid) {
            if ($handler->process($tpl, $this) === false)
                $this->valid = false;
            else
                $this->processed = true;
        } else
            return;
        // 每个资源设置不同的缓存时间
        if ($this->valid && $tpl->caching === TMTemplate::CACHING_LIFETIME_SAVED
            && $tpl->properties['cache_lifetime'] >= 0
            && time() > ($tpl->cached->timestamp + $tpl->properties['cache_lifetime']))
            $this->valid = false;
    }

    /**
     * Write this cache object to handler
     *
     * @param TMInternalTemplate $tpl     template object
     * @param string           $content content to cache
     * @return boolean success
     */
    public function write(TMInternalTemplate $tpl, $content)
    {
        if (!$tpl->source->recompiled) {
            if ($this->handler->writeCachedContent($tpl, $content)) {
                $this->timestamp = time();
                $this->exists = true;
                $this->valid = true;
                return true;
            }
        }
        return false;
    }
}

/**
 * Cache Handler API
 */
abstract class TMTemplateCacheResource
{
    /**
     * @var array cache for TMTemplateCacheResource instances
     */
    public static $resources = array();

    /**
     * @var array resource types provided by the core
     */
    protected static $coreResourceTypes = array(
        'file' => true
    );

    /**
     * Populate Cached Object with meta data from Resource
     *
     * @param TMTemplateCached   $cached cached object
     * @param TMInternalTemplate $tpl    template object
     * @return void
     */
    abstract public function populate(TMTemplateCached $cached, TMInternalTemplate $tpl);

    /**
     * Populate Cached Object with timestamp and exists from Resource
     *
     * @param TMTemplateCached $cached Cached object
     * @return void
     */
    abstract public function populateTimestamp(TMTemplateCached $cached);

    /**
     * Read the cached template and process header
     *
     * @param TMInternalTemplate $tpl template object
     * @param TMTemplateCached   $cached cached object
     * @return booelan true or false if the cached content does not exist
     */
    abstract public function process(TMInternalTemplate $tpl, TMTemplateCached $cached = null);

    /**
     * Write the rendered template output to cache
     *
     * @param TMInternalTemplate $tpl template object
     * @param string             $content  content to cache
     * @return boolean success
     */
    abstract public function writeCachedContent(TMInternalTemplate $tpl, $content);

    /**
     * Return cached content
     *
     * @param TMInternalTemplate $tpl template object
     * @param string $content content of cache
     */
    public function getCachedContent(TMInternalTemplate $tpl)
    {
        if ($tpl->cached->handler->process($tpl)) {
            ob_start();
            $tpl->properties['unifunc']($tpl);
            return ob_get_clean();
        }
        return null;
    }

    /**
     * Empty cache
     *
     * @param TMTemplate $tpl    TMTemplate object
     * @param integer  $expTime  expiration time (number of seconds, not timestamp)
     * @return integer number of cache files deleted
     */
    abstract public function clearAll(TMTemplate $tpl, $expTime = null);

    /**
     * Empty cache for a specific template
     *
     * @param TMTemplate $template     TMTemplate object
     * @param string   $resourceName template name
     * @param string   $cacheId      cache id
     * @param string   $compileId    compile id
     * @param integer  $expTime      expiration time (number of seconds, not timestamp)
     * @return integer number of cache files deleted
     */
    abstract public function clear(TMTemplate $template, $resourceName, $cacheId, $compileId, $expTime);

    /**
     * Load Cache Resource Handler
     * @param  TMTemplate $template TMTemplate object
     * @param  string   $type     name of the cache resource
     * @return TMTemplateCacheResource
     */
    public static function load(TMTemplate $template, $type = null)
    {
        if (!isset($type))
            $type = $template->cachingType;
        // try template's runtime cache
        if (isset($template->cacheResourceHandlers[$type]))
            return $template->cacheResourceHandlers[$type];
        // try core resource cache
        if (isset(self::$coreResourceTypes[$type])) {
            if (!isset(self::$resources[$type])) {
                $cacheResourceClass = 'TMTemplateCacheResource' . ucfirst($type);
                if (class_exists($cacheResourceClass)) {
                    self::$resources[$type] = new $cacheResourceClass();
                } else {
                    throw new TMTemplateException("Cache resource handler '{$cacheResourceClass}' does not exist.");
                }
            }
            return $template->cacheResourceHandlers[$type] = self::$resources[$type];
        }
        throw new TMTemplateException("Unable to load cache resource '{$type}'");
    }

    /**
     * Invalid Loaded Cache Files
     *
     * @param TMTemplate $template TMTemplate object
     */
    public static function invalidLoadedCache(TMTemplate $template)
    {
        foreach ($template->templateObjects as $tpl) {
            if (isset($tpl->cached)) {
                $tpl->cached->valid = false;
                $tpl->cached->processed = false;
            }
        }
    }
}

<?php
/**
 * This class does contain all necessary methods for the HTML cache on file system
 */
class TMTemplateCacheResourceFile extends TMTemplateCacheResource
{
    /**
     * Populate Cached Object with meta data from Resource
     * @param TMTemplateCached   $cached cached object
     * @param TMInternalTemplate $tpl    template object
     * @return void
     */
    public function populate(TMTemplateCached $cached, TMInternalTemplate $tpl)
    {
        $sourceFilepath = str_replace(':', '.', $tpl->source->filepath);
        $cacheId = isset($tpl->cacheId) ? preg_replace('![^\w\|]+!', '_', $tpl->cacheId) : null;
        $compileId = isset($tpl->compileId) ? preg_replace('![^\w\|]+!', '_', $tpl->compileId) : null;
        $filepath = $tpl->source->uid;
        if (isset($cacheId))
            $cacheId = str_replace('|', '^', $cacheId) . '^';
        else
            $cacheId = '';
        if (isset($compileId))
            $compileId = $compileId . '^';
        else
            $compileId = '';
        $cacheDir = $tpl->template->getCacheDir();
        $cached->filepath = $cacheDir . $cacheId . $compileId . $filepath
                          . '.' . basename($sourceFilepath) . '.php';
        $cached->timestamp = @filemtime($cached->filepath);
        $cached->exists = !!$cached->timestamp;
    }

    /**
     * Populate Cached Object with timestamp and exists from Resource
     *
     * @param TMTemplateCached $cached cached object
     * @return void
     */
    public function populateTimestamp(TMTemplateCached $cached)
    {
        $cached->timestamp = @filemtime($cached->filepath);
        $cached->exists = !!$cached->timestamp;
    }

    /**
     * Read the cached template and process its header
     *
     * @param TMInternalTemplate $tpl    template object
     * @param TMTemplateCached   $cached cached object
     * @return booelan true or false if the cached content does not exist
     */
    public function process(TMInternalTemplate $tpl, TMTemplateCached $cached = null)
    {
        $_tpl = $tpl;
        return @include $tpl->cached->filepath;
    }

    /**
     * Write the rendered template output to cache
     *
     * @param TMInternalTemplate $tpl     template object
     * @param string           $content content to cache
     * @return boolean success
     */
    public function writeCachedContent(TMInternalTemplate $tpl, $content)
    {
        if (TMTemplateUtility::writeFile($tpl->cached->filepath, $content, $tpl->template) === true) {
            $tpl->cached->timestamp = @filemtime($tpl->cached->filepath);
            $tpl->cached->exists = !!$tpl->cached->timestamp;
            if ($tpl->cached->exists)
                return true;
        }
        return false;
    }

    /**
     * Empty cache
     *
     * @param TMInternalTemplate $tpl     template object
     * @param integer          $expTime expiration time (number of seconds, not timestamp)
     * @return integer number of cache files deleted
     */
    public function clearAll(TMTemplate $template, $expTime = null)
    {
        return $this->clear($template, null, null, null, $expTime);
    }

    /**
     * Empty cache for a specific template
     *
     * @param TMTemplate  $template     template object
     * @param string    $resourceName template name
     * @param string    $cacheId      cache id
     * @param string    $compileId    compile id
     * @param integer   $expTime      expiration time (number of seconds, not timestamp)
     * @return integer number of cache files deleted
     */
    public function clear(TMTemplate $template, $resourceName, $cacheId, $compileId, $expTime)
    {
        $cacheId = isset($cacheId) ? preg_replace('![^\w\|]+!', '_', $cacheId) : null;
        $compileId = isset($compileId) ? preg_replace('![^\w\|]+!', '_', $compileId) : null;
        $dir = $template->getCacheDir();
        $dirLength = strlen($dir);
        if (isset($cacheId)) {
            $cacheIdParts = explode('|', $cacheId);
            $cacheIdPartsCount = count($cacheIdParts);
        }
        if (isset($resourceName)) {
            $saveStat = $template->caching;
            $template->caching = true;
            $tpl = new TMInternalTemplate($resourceName, $template);
            $template->caching = $saveStat;

            // remove from template cache
            // have the template registered before unset()
            $tpl->source;
            $tplId = $template->joinedTemplateDir . '#' . $resourceName . $tpl->cacheId . $tpl->compileId;
            if (isset($tplId[150]))
                $tplId = sha1($tplId);
            unset($template->templateObjects[$tplId]);

            if ($tpl->source->exists)
                $resourceNameParts = basename(str_replace('^', '/', $tpl->cached->filepath));
            else
                return 0;
        }
        $count = 0;
        $time = time();
        if (file_exists($dir)) {
            $cacheDirs = new RecursiveDirectoryIterator($dir);
            $cache = new RecursiveIteratorIterator($cacheDirs, RecursiveIteratorIterator::CHILD_FIRST);
            foreach ($cache as $file) {
                // 过滤掉隐藏文件
                if (substr(basename($file->getPathname()), 0, 1) == '.'
                    || strpos($file, '.svn') !== false)
                    continue;
                if ($file->isDir()) {
                    if (!$cache->isDot())
                        @rmdir($file->getPathname());
                } else {
                    $parts = explode('^', str_replace('\\', '/', substr((string)$file, $dirLength)));
                    $partsCount = count($parts);
                    // check name
                    if (isset($resourceName)) {
                        if ($parts[$partsCount - 1] != $resourceNameParts)
                            continue;
                    }
                    // check compile id
                    if (isset($compileId)
                        && (!isset($parts[$partsCount - 2])
                            || $parts[$partsCount - 2] != $compileId))
                        continue;
                    // check cache id
                    if (isset($cacheId)) {
                        // count of cache id parts
                        $partsCount = isset($compileId) ? $partsCount - 2 : $partsCount - 1;
                        if ($partsCount < $cacheIdPartsCount)
                            continue;
                        for ($i = 0; $i < $cacheIdPartsCount; $i++) {
                            if ($parts[$i] != $cacheIdParts[$i]) continue 2;
                        }
                    }
                    // 过期
                    if (isset($expTime)) {
                        if ($expTime < 0) {
                            preg_match('#\'cache_lifetime\' =>\s*(\d*)#', file_get_contents($file), $match);
                            if ($time < (@filemtime($file) + $match[1]))
                                continue;
                        } else {
                            if ($time - @filemtime($file) < $expTime)
                                continue;
                        }
                    }
                    $count += @unlink((string)$file) ? 1 : 0;
                }
            }
        }
        return $count;
    }
}

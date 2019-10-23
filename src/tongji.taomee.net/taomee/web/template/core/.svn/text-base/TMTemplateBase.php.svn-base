<?php
/*
 * This file contains the basic shared methods for template handling
 */

/*
 * Class with shared template methods
 */
abstract class TMTemplateBase extends TMTemplateData
{

    /**
     * Fetches a rendered template
     * @param  string $template     the resource handle of the template file or template object
     * @param  mixed  $cacheId      cache id to be used with this template
     * @param  mixed  $compileId    compile id to be used with this template
     * @param  object $parent       next higher level of Template variables
     * @param  bool   $display      true: display, false: fetch
     * @param  bool   $mergeTplVars if true parent template variables merged in to local scope
     * @return string rendered template output
     */
    public function fetch($template = null, $cacheId = null, $compileId = null, $parent = null, $display = false, $mergeTplVars = true)
    {
        // if no template provided, use caller as template
        if ($template === null && $this instanceof TMInternalTemplate) $template = $this;

        if (!empty($cacheId) && is_object($cacheId)) {
            $parent = $cacheId;
            $cacheId = null;
        }
        if ($parent === null && ($this instanceof TMTemplate || is_string($template))) $parent = $this;

        // create template object if necessary
        $tpl = ($template instanceof TMInternalTemplate) ? $template : $this->template->createTemplate($template, $cacheId, $compileId, $parent);

        // if called by Template object make sure we use current caching status
        if ($this instanceof TMTemplate) $tpl->caching = $this->caching;

        // merge all variable scopes into template
        if ($mergeTplVars) $saveTplVars = $this->_mergeTemplateVars($tpl);

        // dummy local template variable
        if (!isset($tpl->tplVars['template'])) $tpl->tplVars['template'] = new TMTemplateVariable();

        // must reset merge template data, include tag uses it.
        $tpl->template->mergedTemplatesFunc = array();

        // 模板源每次都需要重新编译（如eval）
        if ($tpl->source->recompiled) $tpl->caching = false;

        // 检查模板源是否存在
        $this->_checkSourceExistence($tpl);

        // 不缓存或者缓存不合法，重新渲染或者重新编译渲染
        if (!($tpl->caching == TMTemplate::CACHING_LIFETIME_CURRENT || $tpl->caching == TMTemplate::CACHING_LIFETIME_SAVED) || !$tpl->cached->valid) {
            // 源需要编译（若是PHP代码，则不需要编译）
            if (!$tpl->source->uncompiled) {
                // 赋值模板数据
                $_tpl = $tpl;
                if ($tpl->source->recompiled) {
                    $code = $tpl->compiler->compileTemplate($tpl);
                    try {
                        ob_start();
                        eval("?>" . $code);
                        unset($code);
                    } catch (Exception $e) {
                        ob_get_clean();
                        throw $e;
                    }
                } else {
                    // 编译文件不存在或者需要强制编译，则从源文件编译
                    if (!$tpl->compiled->exists || ($tpl->template->forceCompile && !$tpl->compiled->isCompiled)) {
                        $tpl->compileTemplateSource();
                        $code = file_get_contents($tpl->compiled->filepath);
                        eval("?>" . $code);
                        unset($code);
                        $tpl->compiled->loaded = true;
                        $tpl->compiled->isCompiled = true;
                    }
                    // 没有加载则加载，若必须编译，再从源文件编译
                    if (!$tpl->compiled->loaded) {
                        include($tpl->compiled->filepath);
                        if ($tpl->mustCompile) {
                            $tpl->compileTemplateSource();
                            $code = file_get_contents($tpl->compiled->filepath);
                            eval("?>" . $code);
                            unset($code);
                            $tpl->compiled->isCompiled = true;
                        }
                        $tpl->compiled->loaded = true;
                    } else {
                        // 解析属性
                        $tpl->decodeProperties($tpl->compiled->properties, false);
                    }
                    try {
                        ob_start();
                        if (empty($tpl->properties['unifunc']) || !is_callable($tpl->properties['unifunc'])) {
                            throw new TMTemplateException("Invalid compiled template for '{$tpl->templateResource}'");
                        }
                        $tpl->properties['unifunc']($tpl);
                    } catch (Exception $e) {
                        ob_get_clean();
                        throw $e;
                    }
                }
            } else {
                // 不需要重新编译，渲染
                try {
                    ob_start();
                    $tpl->source->renderUncompiled($tpl);
                } catch (Exception $e) {
                    ob_get_clean();
                    throw $e;
                }
            }
            $_output = ob_get_clean();
            if (!$tpl->source->recompiled && empty($tpl->properties['file_dependency'][$tpl->source->uid])) {
                $tpl->properties['file_dependency'][$tpl->source->uid] = array(
                    $tpl->source->filepath,
                    $tpl->source->timestamp,
                    $tpl->source->type
                );
            }
            // 合并父级的文件依赖
            if ($tpl->parent instanceof TMInternalTemplate) {
                $tpl->parent->properties['file_dependency'] = array_merge(
                    $tpl->parent->properties['file_dependency'],
                    $tpl->properties['file_dependency']
                );
            }
            // 不需要重新编译但需要缓存
            if (!$tpl->source->recompiled && ($tpl->caching == TMTemplate::CACHING_LIFETIME_SAVED
                || $tpl->caching == TMTemplate::CACHING_LIFETIME_CURRENT)) {
                $tpl->properties['has_no_cache_code'] = false;
                // get text between non cached items
                $cacheSplit = preg_split("!/\*%%TemplateNoCache:{$tpl->properties['no_cache_hash']}"
                    . "%%\*\/(.+?)/\*/%%TemplateNoCache:{$tpl->properties['no_cache_hash']}%%\*/!s", $_output);
                // get non cached items
                preg_match_all("!/\*%%TemplateNoCache:{$tpl->properties['no_cache_hash']}"
                    . "%%\*\/(.+?)/\*/%%TemplateNoCache:{$tpl->properties['no_cache_hash']}%%\*/!s", $_output,
                    $cacheParts);
                $output = '';
                // loop over items, stitch back together
                foreach ($cacheSplit as $currIdx => $currSplit) {
                    // escape PHP tags in template content
                    $output .= preg_replace('/(<%|%>|<\?php|<\?|\?>)/', "<?php echo '\$1'; ?>\n", $currSplit);
                    if (isset($cacheParts[0][$currIdx])) {
                        $tpl->properties['has_no_cache_code'] = true;
                        // remove nocache tags from cache output
                        $output .= preg_replace("!/\*/?%%TemplateNoCache:{$tpl->properties['no_cache_hash']}%%\*/!",
                                                '', $cacheParts[0][$currIdx]);
                    }
                }
                $_tpl = $tpl;
                try {
                    ob_start();
                    eval("?>" . $output);
                    $_output = ob_get_clean();
                } catch (Exception $e) {
                    ob_get_clean();
                    throw $e;
                }
                // write cache file content
                $tpl->writeCachedContent($output);
            } else {
                // 如果父子哈希都不为空，用父替换掉子
                if (!empty($tpl->properties['no_cache_hash'])
                    && !empty($tpl->parent->properties['no_cache_hash'])) {
                    $_output = str_replace("{$tpl->properties['no_cache_hash']}",
                                           $tpl->parent->properties['no_cache_hash'], $_output);
                    $tpl->parent->hasNoCacheCode = $tpl->parent->hasNoCacheCode || $tpl->hasNoCacheCode;
                }
            }
        } else {
            try {
                ob_start();
                $tpl->properties['unifunc']($tpl);
                $_output = ob_get_clean();
            } catch (Exception $e) {
                ob_get_clean();
                throw $e;
            }
        }
        // display or fetch
        if ($display) {
            if ($this->caching && $this->cacheModifiedCheck) {
                $isCached = $tpl->isCached() && !$tpl->hasNoCacheCode;
                $lastModifiedDate = @substr($_SERVER['HTTP_IF_MODIFIED_SINCE'], 0,
                                            strpos($_SERVER['HTTP_IF_MODIFIED_SINCE'], 'GMT') + 3);
                if ($isCached && $tpl->cached->timestamp <= strtotime($lastModifiedDate)) {
                    header('Status: 304 Not Modified');
                } else {
                    header('Last-Modified: ' . gmdate('D, d M Y H:i:s', $tpl->cached->timestamp) . ' GMT');
                    echo $_output;
                }
            } else {
                echo $_output;
            }
            if ($mergeTplVars) {
                $tpl->tplVars = $saveTplVars;
            }
            return;
        } else {
            if ($mergeTplVars) {
                $tpl->tplVars = $saveTplVars;
            }
            return $_output;
        }
    }

    /**
     * Merge template vars.
     *
     * @param TMInternalTemplate $tpl the internal template whose vars need to be merged.
     * @return array last vars
     */
    private function _mergeTemplateVars(TMInternalTemplate &$tpl)
    {
        // save local variables
        $saveTplVars = $tpl->tplVars;
        $ptrArray = array($tpl);
        $ptr = $tpl;
        while (isset($ptr->parent)) {
            $ptrArray[] = $ptr = $ptr->parent;
        }
        $ptrArray = array_reverse($ptrArray);
        $parentPtr = reset($ptrArray);
        $tplVars = $parentPtr->tplVars;
        while ($parentPtr = next($ptrArray)) {
            if (!empty($parentPtr->tplVars)) {
                $tplVars = array_merge($tplVars, $parentPtr->tplVars);
            }
        }
        if (!empty(TMTemplate::$globalTplVars)) {
            $tplVars = array_merge(TMTemplate::$globalTplVars, $tplVars);
        }
        $tpl->tplVars = $tplVars;
        return $saveTplVars;
    }

    /**
     * Check if the source of the template exists.
     *
     * @param TMInternalTemplate $tpl the internal template whose vars need to be merged.
     * @throw TMTemplateException
     */
    private function _checkSourceExistence(TMInternalTemplate &$tpl)
    {
        // checks if template exists
        if (!$tpl->source->exists) {
            if ($tpl->parent instanceof TMInternalTemplate)
                $parentResource = " in '{$tpl->parent->templateResource}'";
            else
                $parentResource = '';
            throw new TMTemplateException("Unable to load template {$tpl->source->type}"
                . " '{$tpl->source->name}'{$parentResource}");
        }
    }

    /**
     * displays a Template
     *
     * @param string $template   the resource handle of the template file or template object
     * @param mixed  $cacheId    cache id to be used with this template
     * @param mixed  $compileId  compile id to be used with this template
     * @param object $parent     next higher level of Template variables
     */
    public function display($template = null, $cacheId = null, $compileId = null, $parent = null)
    {
        $this->fetch($template, $cacheId, $compileId, $parent, true);
    }

    /**
     * test if cache is valid
     *
     * @param  mixed         $tpl       the resource handle of the template file or template object
     * @param  mixed         $cacheId   cache id to be used with this template
     * @param  mixed         $compileId compile id to be used with this template
     * @param  object        $parent    next higher level of Template variables
     * @return boolean       cache status
     */
    public function isCached($tpl = null, $cacheId = null, $compileId = null, $parent = null)
    {
        if ($tpl === null && $this instanceof TMInternalTemplate)
            return $this->cached->valid;
        if (!($tpl instanceof TMInternalTemplate)) {
            if ($parent === null)
                $parent = $this;
            $tpl = $this->template->createTemplate($tpl, $cacheId, $compileId, $parent);
        }
        return $tpl->cached->valid;
    }
}

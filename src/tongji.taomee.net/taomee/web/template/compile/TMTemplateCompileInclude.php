<?php
/**
 * Template Compile Include
 * Compiles the {include} tag
 */
class TMTemplateCompileInclude extends TMTemplateCompileBase
{
    /**
     * caching mode to create no cache code but no cache file
     */
    const CACHING_NO_CACHE_CODE = 9999;

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
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $optionFlags = array('nocache', 'inline', 'caching');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $optionalAttributes = array('_any');

    /**
     * Compiles code for the {include} tag
     * @param  array  $args      array with attributes from parser
     * @param  object $compiler  compiler object
     * @param  array  $parameter array with compilation parameter
     * @return string compiled code
     */
    public function compile($args, $compiler, $parameter)
    {
        $attr = $this->getAttributes($compiler, $args);
        $includeFile = $attr['file'];
        // 包含文件会赋值给$template变量而不是显示
        if (isset($attr['assign'])) {
            $assign = $attr['assign'];
        }
        $parentScope = TMTemplate::SCOPE_LOCAL;
        if (isset($attr['scope'])) {
            $attr['scope'] = trim($attr['scope'], "'\"");
            if ($attr['scope'] == 'parent') {
                $parentScope = TMTemplate::SCOPE_PARENT;
            } elseif ($attr['scope'] == 'root') {
                $parentScope = TMTemplate::SCOPE_ROOT;
            } elseif ($attr['scope'] == 'global') {
                $parentScope = TMTemplate::SCOPE_GLOBAL;
            }
        }
        $caching = TMTemplate::CACHING_OFF;
        // 标志被包含文件是否合并到包含文件中
        $mergeCompiledIncludes = ($compiler->template->mergeCompiledIncludes || $attr['inline'] === true)
                                 && !$compiler->tpl->source->recompiled;
        if ($compiler->tpl->caching
            && ((!$compiler->inheritance && !$compiler->noCache && !$compiler->tagNoCache)
            || ($compiler->inheritance && ($compiler->noCache || $compiler->tagNoCache)))) {
            $caching = self::CACHING_NO_CACHE_CODE;
        }
        /*
         * if the {include} tag provides individual parameter for caching
         * it will not be included into the common cache file and treated like
         * a no cache section
         */
        if (isset($attr['cache_lifetime'])) {
            $cacheLifetime = $attr['cache_lifetime'];
            $compiler->tagNoCache = true;
            $caching = TMTemplate::CACHING_LIFETIME_CURRENT;
        } else {
            $cacheLifetime = 'null';
        }
        if (isset($attr['cache_id'])) {
            $cacheId = $attr['cache_id'];
            $compiler->tagNoCache = true;
            $caching = TMTemplate::CACHING_LIFETIME_CURRENT;
        } else {
            $cacheId = '$_tpl->cacheId';
        }
        if (isset($attr['compile_id'])) {
            $compileId = $attr['compile_id'];
        } else {
            $compileId = '$_tpl->compileId';
        }
        if ($attr['caching'] === true) {
            $caching = TMTemplate::CACHING_LIFETIME_CURRENT;
        }
        if ($attr['nocache'] === true) {
            $compiler->tagNoCache = true;
            if ($mergeCompiledIncludes || $compiler->inheritance) {
                $caching = self::CACHING_NO_CACHE_CODE;
            } else {
                $caching = TMTemplate::CACHING_OFF;
            }
        }
        $hasCompiledTemplate = false;
        if ($mergeCompiledIncludes || $compiler->inheritance) {
            // 不定模板名称
            if ($compiler->hasVariableString
                || !(substr_count($includeFile, '"') == 2 || substr_count($includeFile, "'") == 2)
                || substr_count($includeFile, '(') != 0
                || substr_count($includeFile, '$_tpl->') != 0) {
                $mergeCompiledIncludes = false;
                if ($compiler->inheritance) {
                    $compiler->triggerTemplateError('Variable template file names not allow within {block} tags.');
                }
            }
            // 不定编译ID
            if (isset($attr['compile_id'])) {
                if (!(substr_count($attr['compile_id'], '"') == 2 || substr_count($attr['compile_id'], "'") == 2)
                    || substr_count($attr['compile_id'], '(') != 0
                    || substr_count($attr['compile_id'], '$_tpl->') != 0) {
                    $mergeCompiledIncludes = false;
                    if ($compiler->inheritance) {
                        $compiler->triggerTemplateError('Variable compileId not allow within {block} tags.');
                    }
                }
            }
            if ($compiler->tpl->caching
                && ($compiler->tagNoCache || $compiler->noCache)
                && $caching != self::CACHING_NO_CACHE_CODE) {
                $mergeCompiledIncludes = false;
                if ($compiler->inheritance) {
                    $compiler->triggerTemplateError('Invalid caching mode of subtemplate within {block} tags.');
                }
            }
        }
        if ($mergeCompiledIncludes || $compiler->inheritance) {
            // 分配不同的编译ID
            $uid = sha1($compileId);
            $tplName = null;
            $noCache = false;
            eval("\$tplName = $includeFile;");
            if (!isset($compiler->template->mergedTemplatesFunc[$tplName][$uid]) || $compiler->inheritance) {
                $tpl = new TMInternalTemplate($tplName, $compiler->template, $compiler->tpl,
                                $compiler->tpl->cacheId, $compiler->tpl->compileId);
                $compiler->template->mergedTemplatesFunc[$tplName][$uid]['func']
                        = $tpl->properties['unifunc']
                        = 'content_' . str_replace('.', '_', uniqid('', true));
                // 对于内联包含使用当前编译ID
                $compiler->template->mergedTemplatesFunc[$tplName][$uid]['no_cache_hash']
                        = $tpl->properties['no_cache_hash']
                        = $compiler->tpl->properties['no_cache_hash'];
                if ($compiler->tpl->caching && $caching == self::CACHING_NO_CACHE_CODE) {
                    $noCache = true;
                }
                if ($compiler->inheritance) {
                    $tpl->compiler->inheritance = true;
                }
                $tpl->mustCompile = true;
                if (!($tpl->source->uncompiled) && $tpl->source->exists) {
                    $compiledCode = $tpl->compiler->compileTemplate($tpl, $noCache);
                    unset($tpl->compiler);
                    $tpl->properties['file_dependency'][$tpl->source->uid] = array(
                        $tpl->source->filepath,
                        $tpl->source->timestamp,
                        $tpl->source->type
                    );
                    $compiler->tpl->properties['file_dependency'] = array_merge(
                        $compiler->tpl->properties['file_dependency'],
                        $tpl->properties['file_dependency']
                    );
                    // 去掉头
                    $compiledCode = preg_replace("/(<\?php \/\*%%TemplateHeaderCode:{$tpl->properties['no_cache_hash']}%%\*\/(.+?)\/\*\/%%TemplateHeaderCode%%\*\/\?>\n)/s", '', $compiledCode);
                    if ($tpl->hasNoCacheCode) {
                        $compiledCode = str_replace("{$tpl->properties['no_cache_hash']}",
                            $compiler->tpl->properties['no_cache_hash'], $compiledCode);
                        $compiler->tpl->hasNoCacheCode = true;
                    }
                    $compiler->mergedTemplates[$tpl->properties['unifunc']] = $compiledCode;
                    $hasCompiledTemplate = true;
                    unset ($tpl);
                }
            } else {
                $hasCompiledTemplate = true;
            }
        }
        // 删除include标签的标准属性
        unset($attr['file'], $attr['assign'],
              $attr['cache_id'], $attr['compile_id'], $attr['cache_lifetime'],
              $attr['nocache'], $attr['caching'], $attr['scope'], $attr['inline']);
        // 保留要赋值给$template变量的标签
        if (!empty($attr)) {
            if ($parentScope == TMTemplate::SCOPE_LOCAL) {
                foreach ($attr as $key => $value) {
                    $pairs[] = "'$key'=>$value";
                }
                $vars = 'array(' . join(',', $pairs) . ')';
            } else {
                $compiler->triggerTemplateError(
                                    'Variable passing not allowed in parent/global scope',
                                    $compiler->lex->line);
            }
        } else {
            $vars = 'array()';
        }
        if ($hasCompiledTemplate) {
            $compiler->suppressNoCacheProcessing = true;
            $hash = $compiler->template->mergedTemplatesFunc[$tplName][$uid]['no_cache_hash'];
            $output = "<?php /* Call merged included template \"" . $tplName . "\" */\n"
                    . "\$_tplStack[] = \$_tpl;\n"
                    . "\$_tpl = \$_tpl->setupInlineSubTemplate("
                    . "$includeFile, $cacheId, $compileId, $caching, $cacheLifetime, $vars, $parentScope, '$hash');\n";
            if (isset($assign)) {
                $output .= 'ob_start();';
            }
            $output .= $compiler->template->mergedTemplatesFunc[$tplName][$uid]['func'] . "(\$_tpl);\n"
                     . "\$_tpl = array_pop(\$_tplStack);";
            if (isset($assign)) {
                $output .= "\$_tpl->tplVars[$assign] = new TMTemplateVariable(ob_get_clean());";
            }
            $output .= "\n/* End of included template \"" . $tplName . "\" */?>";
            return $output;
        }

        if (isset($assign)) {
            $output = "<?php \$_tpl->tplVars[$assign] = new TMTemplateVariable(\$_tpl->getSubTemplate("
                    . "$includeFile, $cacheId, $compileId, $caching, $cacheLifetime, $vars, $parentScope));?>\n";
        } else {
            $output = "<?php echo \$_tpl->getSubTemplate("
                    . "$includeFile, $cacheId, $compileId, $caching, $cacheLifetime, $vars, $parentScope);?>\n";
        }
        return $output;
    }
}

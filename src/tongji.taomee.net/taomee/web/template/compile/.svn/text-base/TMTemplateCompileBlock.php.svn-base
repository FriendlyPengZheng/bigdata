<?php
/**
 * Template Compile Block
 * Compiles the {block}{/block} tags
 */

/**
 * Template Compile Block Class
 */
class TMTemplateCompileBlock extends TMTemplateCompileBase
{

    const parent = '____TEMPLATE_BLOCK_PARENT____';

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $requiredAttributes = array('name');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $shortTagOrder = array('name');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $optionFlags = array('hide', 'append', 'prepend', 'nocache');

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $optionalAttributes = array('internal_file', 'internal_uid', 'internal_line');

    /**
     * @var array nested child block names
     */
    public static $nestedBlockNames = array();

    /**
     * @var array child block source buffer
     */
    public static $blockData = array();

    /**
     * Compiles code for the {block} tag
     * @param  array   $args     array with attributes from parser
     * @param  object  $compiler compiler object
     * @return boolean true
     */
    public function compile($args, $compiler)
    {
        $attr = $this->getAttributes($compiler, $args);
        $_name = trim($attr['name'], "\"'");
        if ($compiler->inheritanceChild) {
            array_unshift(self::$nestedBlockNames, $_name);
            $this->tpl->blockData[$_name]['source'] = '';
            // build {block} for child block
            self::$blockData[$_name]['source'] =
                "{$compiler->template->leftDelimiter}childblock name={$attr['name']}"
                . " file='{$compiler->tpl->source->filepath}'"
                . " uid='{$compiler->tpl->source->uid}' line={$compiler->lex->line}";
            if ($attr['nocache']) {
                self::$blockData[$_name]['source'] .= ' nocache';
            }
            self::$blockData[$_name]['source'] .= $compiler->template->rightDelimiter;
            $save = array($attr, $compiler->inheritance);
            $this->openTag($compiler, 'block', $save);
            $compiler->inheritance = true;
            $compiler->lex->yypushstate(TMTemplateLexer::CHILDBLOCK);
            $compiler->hasCode = false;
            return;
        }
        if ($attr['nocache'] == true) {
            $compiler->tagNoCache = true;
        }
        $save = array($attr, $compiler->inheritance, $compiler->parser->currentBuffer, $compiler->noCache);
        $this->openTag($compiler, 'block', $save);
        $compiler->inheritance = true;
        $compiler->noCache = $compiler->noCache | $compiler->tagNoCache;
        $compiler->parser->currentBuffer = new TMTemplateBuffer($compiler->parser);
        $compiler->hasCode = false;
        return true;
    }

    /**
     * Compile saved child block source
     *
     * @param object  $compiler compiler object
     * @param string  $_name    optional name of child block
     * @return string compiled code of child block
     */
    public static function compileChildBlock($compiler, $_name = null)
    {
        if ($compiler->inheritanceChild) {
            $name1 = self::$nestedBlockNames[0];
            if (isset($compiler->tpl->blockData[$name1])) {
                self::$blockData[$name1]['source'] .= $compiler->tpl->blockData[$name1]['source'];
                self::$blockData[$name1]['child'] = true;
            }
            $compiler->lex->yypushstate(TMTemplateLexer::CHILDBLOCK);
            $compiler->hasCode = false;
            return;
        }
        // if called by {$template.block.child} we must search the name of enclosing {block}
        if ($_name == null) {
            $stackCount = count($compiler->tagStack);
            while (--$stackCount >= 0) {
                if ($compiler->tagStack[$stackCount][0] == 'block') {
                    $_name = trim($compiler->tagStack[$stackCount][1][0]['name'], "\"'");
                    break;
                }
            }
        }
        if ($_name == null) {
            $compiler->triggerTemplateError(
                    'Tag {$template.block.child} used outside {block} tags.',
                    $compiler->lex->line);
        }
        // undefined child
        if (!isset($compiler->tpl->blockData[$_name]['source'])) {
            return '';
        }
        // flag that child is already compile by {$template.block.child}
        $compiler->tpl->blockData[$_name]['compiled'] = true;
        $tpl = new TMInternalTemplate('string:' . $compiler->tpl->blockData[$_name]['source'],
                    $compiler->template, $compiler->tpl,
                    $compiler->tpl->cacheId, $compiler->tpl->compileId,
                    $compiler->tpl->caching, $compiler->tpl->cacheLifetime);
        $tpl->properties['no_cache_hash'] = $compiler->tpl->properties['no_cache_hash'];
        $tpl->allowRelativePath = true;
        $tpl->compiler->inheritance = true;
        $tpl->compiler->suppressTemplatePropertyHeader = true;
        $tpl->compiler->suppressMergedTemplates = true;
        $noCache = $compiler->noCache || $compiler->tagNoCache;
        if (strpos($compiler->tpl->blockData[$_name]['source'], self::parent) !== false) {
            $_output = str_replace(self::parent, $compiler->parser->currentBuffer->toPHP(),
                     $tpl->compiler->compileTemplate($tpl, $noCache));
        } elseif ($compiler->tpl->blockData[$_name]['mode'] == 'prepend') {
            $_output = $tpl->compiler->compileTemplate($tpl, $noCache)
                     . $compiler->parser->currentBuffer->toPHP();
        } elseif ($compiler->tpl->blockData[$_name]['mode'] == 'append') {
            $_output = $compiler->parser->currentBuffer->toPHP()
                     . $tpl->compiler->compileTemplate($tpl, $noCache);
        } elseif (!empty($compiler->tpl->blockData[$_name])) {
            $_output = $tpl->compiler->compileTemplate($tpl, $noCache);
        }
        $compiler->tpl->properties['file_dependency'] = array_merge(
                $compiler->tpl->properties['file_dependency'],
                $tpl->properties['file_dependency']);
        $compiler->mergedTemplates = array_merge(
                $compiler->mergedTemplates,
                $tpl->compiler->mergedTemplates);
        if ($tpl->hasNoCacheCode) {
            $compiler->tpl->hasNoCacheCode = true;
        }
        unset($tpl);
        $compiler->hasCode = true;
        return $_output;
    }

    /**
     * Compile $template.block.parent
     * @param object   $compiler compiler object
     * @param string   $_name    optional name of parent block
     * @return string  compiled code of parent block
     */
    public static function compileParentBlock($compiler, $_name = null)
    {
        if ($_name == null) {
            $stackCount = count($compiler->tagStack);
            while (--$stackCount >= 0) {
                if ($compiler->tagStack[$stackCount][0] == 'block') {
                    $_name = trim($compiler->tagStack[$stackCount][1][0]['name'], "\"'");
                    break;
                }
            }
        }
        if ($_name == null) {
            $compiler->triggerTemplateError(
                    'Tag {$template.block.parent} used outside {block} tags.',
                    $compiler->lex->line);
        }
        if (empty(self::$nestedBlockNames)) {
            $compiler->triggerTemplateError(
                    'Illegal {$template.block.parent} in parent template.',
                    $compiler->lex->line);
        }
        self::$blockData[self::$nestedBlockNames[0]]['source'] .= self::parent;
        $compiler->lex->yypushstate(TMTemplateLexer::CHILDBLOCK);
        $compiler->hasCode = false;
        return;
    }

    /**
     * Process block source
     * @param string $source source text
     * @return ''
     */
    public static function blockSource($compiler, $source)
    {
        self::$blockData[self::$nestedBlockNames[0]]['source'] .= $source;
    }

}

/**
 * Template Compile Blockclose Class
 */
class TMTemplateCompileBlockclose extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {/block} tag
     * @param array   $args     array with attributes from parser
     * @param object  $compiler compiler object
     * @return string compiled code
     */
    public function compile($args, $compiler)
    {
        $compiler->hasCode = true;
        $attr = $this->getAttributes($compiler, $args);
        // array($attr, $compiler->inheritance, $compiler->parser->currentBuffer, $compiler->noCache);
        $savedData = $this->closeTag($compiler, array('block'));
        $_name = trim($savedData[0]['name'], "\"'");
        $compiler->inheritance = $savedData[1];
        // 编译继承子级模板
        if ($compiler->inheritanceChild) {
            $name1 = TMTemplateCompileBlock::$nestedBlockNames[0];
            TMTemplateCompileBlock::$blockData[$name1]['source']
                .= "{$compiler->template->leftDelimiter}/childblock{$compiler->template->rightDelimiter}";
            $level = count(TMTemplateCompileBlock::$nestedBlockNames);
            array_shift(TMTemplateCompileBlock::$nestedBlockNames);
            if (!empty(TMTemplateCompileBlock::$nestedBlockNames)) {
                $name2 = TMTemplateCompileBlock::$nestedBlockNames[0];
                if (isset($compiler->tpl->blockData[$name1]) || !$savedData[0]['hide']) {
                    if (isset(TMTemplateCompileBlock::$blockData[$name1]['child'])
                        || !isset($compiler->tpl->blockData[$name1])) {
                        TMTemplateCompileBlock::$blockData[$name2]['source']
                            .= TMTemplateCompileBlock::$blockData[$name1]['source'];
                    } else {
                        if ($compiler->tpl->blockData[$name1]['mode'] == 'append') {
                            TMTemplateCompileBlock::$blockData[$name2]['source']
                                .= TMTemplateCompileBlock::$blockData[$name1]['source']
                                        . $compiler->tpl->blockData[$name1]['source'];
                        } elseif ($compiler->tpl->blockData[$name1]['mode'] == 'prepend') {
                            TMTemplateCompileBlock::$blockData[$name2]['source']
                                .= $compiler->tpl->blockData[$name1]['source']
                                        . TMTemplateCompileBlock::$blockData[$name1]['source'];
                        } else {
                            TMTemplateCompileBlock::$blockData[$name2]['source']
                                .= $compiler->tpl->blockData[$name1]['source'];
                        }
                    }
                }
                unset(TMTemplateCompileBlock::$blockData[$name1]);
                $compiler->lex->yypushstate(TMTemplateLexer::CHILDBLOCK);
            } else {
                if (isset($compiler->tpl->blockData[$name1]) || !$savedData[0]['hide']) {
                    if (isset($compiler->tpl->blockData[$name1])
                        && !isset(TMTemplateCompileBlock::$blockData[$name1]['child'])) {
                        if (strpos($compiler->tpl->blockData[$name1]['source'],
                            TMTemplateCompileBlock::parent) !== false) {
                            $compiler->tpl->blockData[$name1]['source'] = str_replace(
                                TMTemplateCompileBlock::parent,
                                TMTemplateCompileBlock::$blockData[$name1]['source'],
                                $compiler->tpl->blockData[$name1]['source']);
                        } elseif ($compiler->tpl->blockData[$name1]['mode'] == 'prepend') {
                            $compiler->tpl->blockData[$name1]['source']
                                .= TMTemplateCompileBlock::$blockData[$name1]['source'];
                        } elseif ($compiler->tpl->blockData[$name1]['mode'] == 'append') {
                            $compiler->tpl->blockData[$name1]['source']
                                = TMTemplateCompileBlock::$blockData[$name1]['source']
                                    . $compiler->tpl->blockData[$name1]['source'];
                        }
                    } else {
                        $compiler->tpl->blockData[$name1]['source']
                            = TMTemplateCompileBlock::$blockData[$name1]['source'];
                    }
                    $compiler->tpl->blockData[$name1]['mode'] = 'replace';
                    if ($savedData[0]['append']) {
                        $compiler->tpl->blockData[$name1]['mode'] = 'append';
                    }
                    if ($savedData[0]['prepend']) {
                        $compiler->tpl->blockData[$name1]['mode'] = 'prepend';
                    }
                }
                unset(TMTemplateCompileBlock::$blockData[$name1]);
                $compiler->lex->yypushstate(TMTemplateLexer::CHILDBODY);
            }
            $compiler->hasCode = false;
            return;
        }
        if (isset($compiler->tpl->blockData[$_name])
            && !isset($compiler->tpl->blockData[$_name]['compiled'])) {
            $_output = TMTemplateCompileBlock::compileChildBlock($compiler, $_name);
        } else {
            if ($savedData[0]['hide'] && !isset($compiler->tpl->blockData[$_name]['source'])) {
                $_output = '';
            } else {
                $_output = $compiler->parser->currentBuffer->toPHP();
            }
            unset($compiler->tpl->blockData[$_name]['compiled']);
        }
        $compiler->parser->currentBuffer = $savedData[2];
        if ($compiler->noCache) {
            $compiler->tagNoCache = true;
        }
        $compiler->noCache = $savedData[3];
        $compiler->suppressNoCacheProcessing = true;

        return $_output;
    }
}

/**
 * Template Compile Child Block Class
 */
class TMTemplateCompileChildblock extends TMTemplateCompileBase
{

    /**
     * Attribute definition: Overwrites base class.
     * @var array
     * @see TMTemplateCompileBase
     */
    public $requiredAttributes = array('name', 'file', 'uid', 'line');

    /**
     * Compiles code for the {childblock} tag
     * @param array  $args     array with attributes from parser
     * @param object $compiler compiler object
     * @return boolean true
     */
    public function compile($args, $compiler)
    {
        $attr = $this->getAttributes($compiler, $args);
        if ($attr['nocache'] == true) {
            $compiler->tagNoCache = true;
        }
        $save = array($attr, $compiler->noCache);
        $this->openTag($compiler, 'childblock', $save);
        $compiler->noCache = $compiler->noCache | $compiler->tagNoCache;
        $compiler->hasCode = false;

        return true;
    }
}

/**
 * Template Compile Child Block Close Class
 */
class TMTemplateCompileChildblockclose extends TMTemplateCompileBase
{
    /**
     * Compiles code for the {/childblock} tag
     * @param array  $args     array with attributes from parser
     * @param object $compiler compiler object
     * @return boolean true
     */
    public function compile($args, $compiler)
    {
        $attr = $this->getAttributes($compiler, $args);
        $savedData = $this->closeTag($compiler, array('childblock'));
        $compiler->noCache = $savedData[1];
        $compiler->hasCode = false;

        return true;
    }
}

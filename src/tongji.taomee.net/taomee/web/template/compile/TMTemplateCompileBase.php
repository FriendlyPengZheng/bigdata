<?php
/**
 * TMTemplateCompileBase
 * This class does extend all compilers
 */
abstract class TMTemplateCompileBase
{
    /**
     * @var array Array of names of required attribute required by tag
     */
    public $requiredAttributes = array();

    /**
     * Array of names of optional attribute required by tag
     * use array('_any') if there is no restriction of attributes names
     * @var array
     */
    public $optionalAttributes = array();

    /**
     * @var array Short tag attribute order defined by its names
     * 简写选项的顺序
     */
    public $shortTagOrder = array();

    /**
     * @var array Array of names of valid option flags
     * 标志选项
     */
    public $optionFlags = array('nocache');

    /**
     * This function checks if the attributes passed are valid
     * The attributes passed for the tag to compile are checked against the list of required and
     * optional attributes. Required attributes must be present. Optional attributes are check against
     * the corresponding list. The keyword '_any' specifies that any attribute will be accepted
     * as valid
     * @param object $compiler   compiler object
     * @param array  $attributes attributes applied to the tag
     * @return array of mapped attributes for further processing
     */
    public function getAttributes($compiler, $attributes)
    {
        $indexedAttr = array();
        foreach ($attributes as $key => $mixed) {
            if (!is_array($mixed)) {
                $trimedMixed = trim($mixed, '\'"');
                // 优先考虑标志选项，然后简写选项
                if (in_array($trimedMixed, $this->optionFlags)) {
                    $indexedAttr[$trimedMixed] = true;
                } elseif (isset($this->shortTagOrder[$key])) {
                    $indexedAttr[$this->shortTagOrder[$key]] = $mixed;
                } else {
                    $compiler->triggerTemplateError('Too many shorthand attributes',
                                    $compiler->lex->line);
                }
            } else {
                $kv = each($mixed);
                // 标志选项取值包括boolean、字符串true或false、数字1或0
                if (in_array($kv['key'], $this->optionFlags)) {
                    if (is_bool($kv['value'])) {
                        $indexedAttr[$kv['key']] = $kv['value'];
                    } elseif (is_string($kv['value'])
                        && in_array(trim($kv['value'], '\'"'), array('true', 'false'))) {
                        if (trim($kv['value']) == 'true') {
                            $indexedAttr[$kv['key']] = true;
                        } else {
                            $indexedAttr[$kv['key']] = false;
                        }
                    } elseif (is_numeric($kv['value']) && in_array($kv['value'], array(0, 1))) {
                        if ($kv['value'] == 1) {
                            $indexedAttr[$kv['key']] = true;
                        } else {
                            $indexedAttr[$kv['key']] = false;
                        }
                    } else {
                        $compiler->triggerTemplateError("Illegal value of option flag \"{$kv['key']}\"",
                                        $compiler->lex->line);
                    }
                } else {
                    reset($mixed);
                    $indexedAttr[key($mixed)] = $mixed[key($mixed)];
                }
            }
        }
        // 检查所有必须属性
        foreach ($this->requiredAttributes as $attr) {
            if (!array_key_exists($attr, $indexedAttr)) {
                $compiler->triggerTemplateError("Missing \"" . $attr . "\" attribute", $compiler->lex->line);
            }
        }
        // 检查不允许的属性
        if ($this->optionalAttributes != array('_any')) {
            $tmpArray = array_merge($this->requiredAttributes, $this->optionalAttributes, $this->optionFlags);
            foreach ($indexedAttr as $key => $dummy) {
                if (!in_array($key, $tmpArray) && $key !== 0) {
                    $compiler->triggerTemplateError("Unexpected \"" . $key . "\" attribute", $compiler->lex->line);
                }
            }
        }
        // 没有设置的属性默认为false
        foreach ($this->optionFlags as $flag) {
            if (!isset($indexedAttr[$flag])) {
                $indexedAttr[$flag] = false;
            }
        }
        return $indexedAttr;
    }

    /**
     * Push opening tag name on stack
     * Optionally additional data can be saved on stack
     * @param object $compiler compiler object
     * @param string $openTag  the opening tag's name
     * @param mixed  $data     optional data saved
     */
    public function openTag($compiler, $openTag, $data = null)
    {
        array_push($compiler->tagStack, array($openTag, $data));
    }

    /**
     * Pop closing tag
     * Raise an error if this stack-top doesn't match with expected opening tags
     * @param object $compiler    compiler object
     * @param mixed  $expectedTag the expected opening tag names
     * @return mixed any type the opening tag's name or saved data
     */
    public function closeTag($compiler, $expectedTag)
    {
        if (count($compiler->tagStack) > 0) {
            list($openTag, $data) = array_pop($compiler->tagStack);
            if (in_array($openTag, (array)$expectedTag)) {
                if (is_null($data)) {
                    return $openTag;
                } else {
                    return $data;
                }
            }
            $compiler->triggerTemplateError("Unclosed {$compiler->template->leftDelimiter}"
                            . $openTag . "{$compiler->template->rightDelimiter} tag");
            return;
        }
        $compiler->triggerTemplateError("Unexpected closing tag",
                        $compiler->lex->line);
        return;
    }
}

<?php
class TMHTML
{
    public static function chosen($options, $name, $htmlOptions=array())
    {
        $html = '';
        $value = null;
        if (isset($htmlOptions['value'])) {
            $value = $htmlOptions['value'];
            unset($htmlOptions['value']);
        }
        if (!isset($htmlOptions['id'])) $htmlOptions['id'] = $name;
        foreach ($options as $option) {
            $content = 'option';
            if (isset($option['content'])) {
                $content = $option['content'];
                unset($option['content']);
            } 
            if (isset($option['value'])) {
                $content = $content ? $content : $option['value'];
                if ($option['value'] == $value) $option['selected'] = 1;
            }
            $html .= self::tag('option', $option, $content, true);
        }
        echo self::tag('select', array_merge(
            array(
                'name' => $name
            ),
            $htmlOptions
        ), $html, true);
    }

    public static function tag($tagName, $htmlOptions, $contents=false, $closeTag=true)
    {
        $html = '<' . $tagName . self::htmlOptions($htmlOptions);
        if ($contents === false) {
            return $closeTag ? $html . '></' . $tagName . '>' : $html . '/>';
        } else {
            return $closeTag ? $html . '>' . $contents . '</' . $tagName . '>' : $html . '>' . $contents;
        }
    }

    public static function htmlOptions($htmlOptions)
    {
        $html = ' ';
        foreach ($htmlOptions as $key => $value) {
            $html .= ($key . '="' . $value . '" ');
        }
        return $html;
    }

    protected static function inputField($type, $name, $value, $htmlOptions)
    {
        $htmlOptions['type'] = $type;
        $htmlOptions['value'] = $value;
        $htmlOptions['name'] = $name;
        isset($htmlOptions['id']) ? '' : $htmlOptions['id'] = $name;
        return self::tag('input', $htmlOptions);
    }

    public static function textField($name, $value='', $htmlOptions=array())
    {
        echo self::inputField('text', $name, $value, $htmlOptions);
    }

    public static function textarea($name, $contents='', $htmlOptions=array())
    {
        $htmlOptions['name'] = $name;
        echo self::tag('textarea', $htmlOptions, $contents);
    }

    public static function hiddenField($name, $value='', $htmlOptions=array())
    {
        echo self::inputField('hidden', $name, $value, $htmlOptions);
    }

    public static function submitField($name, $value='', $htmlOptions=array())
    {
        echo self::inputField('submit', $name, $value, $htmlOptions);
    }

    public static function buttonField($name, $value='', $htmlOptions=array())
    {
        echo self::inputField('button', $name, $value, $htmlOptions);
    }

    public static function checkboxField($name, $checked=false, $htmlOptions=array())
    {
        if ($checked) $htmlOptions['checked'] = $checked;
        else unset($htmlOptions['checked']);
        $value = isset($htmlOptions['value']) ? $htmlOptions['checked'] : 1;
        echo self::inputField('checkbox', $name, $value, $htmlOptions);
    }

    public static function label($model, $attribute, $htmlOptions=array())
    {
        if (!isset($htmlOptions['for'])) $htmlOptions['for'] = $attribute;
        echo self::tag('label', $htmlOptions, $model->getAttributeLabel($attribute));
    }

    /**
     * 生成URL
     * @param {string} $route 路径
     * @param {array}  $params 参数数组
     */
    public static function url($route, $params = array())
    {
        echo TM::app()->getUrlManager()->rebuildUrl($route, $params);
    }

    /**
     * @brief csrfToken 
     * 生成csrf token内容
     */
    public static function csrfToken()
    {
        echo TM::app()->session->get('__token');
    }

    /**
     * @brief csrfField 
     * 生成csrf表单隐藏字段
     */
    public static function csrfField()
    {
        self::hiddenField('_token', TM::app()->session->get('__token'));
    }
}

<?php
class TMTranslator extends TMComponent
{
    /**
     * @var array|string Paths of language packages.
     */
    public $langPath;

    /**
     * @var boolean whether to record the mis-translated words.
     */
    public $record = false;

    /**
     * @var string Record path.
     */
    public $recordPath;

    /**
     * @var string The application locale, used as the default locale.
     */
    private $_locale = null;

    /**
     * @var array All languages.
     */
    private $_language = array();

    /**
     * Filter the language paths.
     */
    public function init()
    {
        $this->langPath = (array)$this->langPath;
        foreach ($this->langPath as $key => &$path) {
            if (is_dir($path)) {
                $path = rtrim($path, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR;
            } else {
                unset($this->langPath[$key]);
            }
        }
        $this->_locale = TM::app()->getLocale();
        $this->_language = array();
    }

    /**
     * Set locale
     *
     * @param  string       $locale
     * @return TMTranslator
     */
    public function setLocale($locale)
    {
        $this->_locale = $locale;

        return $this;
    }

    /**
     * Get locale
     *
     * @return string
     */
    public function getLocale()
    {
        return $this->_locale;
    }

    /**
     * Translate the given message.
     *
     * @param  string $category
     * @param  string $message
     * @param  string $locale
     * @return string
     */
    public function translate($category, $message, $locale = null)
    {
        $locale = isset($locale) ? $locale : $this->_locale;
        if (!$locale || $locale === 'zh_CN' || !$category || !$message || !$this->langPath) {
            return $message;
        }

        if (!isset($this->_language[$locale][$category])) {
            $this->_language[$locale][$category] = array();
            foreach ($this->langPath as $path) {
                $file = $path . $locale . DIRECTORY_SEPARATOR . $category . '.php';
                if (is_readable($file)) {
                    if (($language = include($file)) && is_array($language)) {
                        $this->_language[$locale][$category] += $language;
                    }
                }
            }
        }
        if (isset($this->_language[$locale][$category][$message])) {
            return $this->_language[$locale][$category][$message];
        }

        if ($this->record && is_writeable($this->recordPath)) {
            file_put_contents(
                rtrim($this->recordPath, DIRECTORY_SEPARATOR) . DIRECTORY_SEPARATOR . $category . '.txt',
                $message . PHP_EOL,
                FILE_APPEND
            );
        }

        return $message;
    }
}

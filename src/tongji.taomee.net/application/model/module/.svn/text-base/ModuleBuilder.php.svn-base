<?php
class module_ModuleBuilder extends TMComponent
{
    private $_moduleKey = null;

    private $_buildPath = null;

    private $_component = null;

    private $_basicData = array();

    private $_wrapData = array();

    public function __construct($moduleKey = null, $buildPath = null)
    {
        $this->_moduleKey = $moduleKey;
        $this->_buildPath = $buildPath;
        $this->_component = new module_Component();
    }

    public function setModuleKey($moduleKey)
    {
        $this->_moduleKey = $moduleKey;
        return $this;
    }

    public function getModuleKey()
    {
        return $this->_moduleKey;
    }

    public function setBuildPath($buildPath)
    {
        $this->_buildPath = $buildPath;
        return $this;
    }

    public function getBuildPath()
    {
        return $this->_buildPath;
    }

    public function endBasicData()
    {
        return end($this->_basicData);
    }

    public function pushBasicData($basicData)
    {
        array_push($this->_basicData, $basicData);
    }

    public function popBasicData()
    {
        return array_pop($this->_basicData);
    }

    public function addExprsWrapData($data)
    {
        $this->_wrapData['key'] = 'exprs';
        return $this->_addWrapData($data);
    }

    public function addDistrWrapData($data)
    {
        $this->_wrapData['key'] = 'data_info';
        return $this->_addWrapData($data);
    }

    private function _addWrapData($data)
    {
        if (!isset($this->_wrapData['period'])) {
            $this->_wrapData['common'] = '';
            $this->_wrapData['data'] = $this->_wrapData['period'] = $this->_wrapData['group'] = array();
        }
        $aGroup = array();
        $iNextStartIdx = count($this->_wrapData['data']);
        foreach ($data as $key => $info) {
            $info['common'] = trim($info['common']);
            $info['data'] = trim($info['data']);
            if ($info['common'] !== '') {
                $queryString = parse_url($info['common'], PHP_URL_QUERY);
                $queryParam = array();
                parse_str($queryString, $queryParam);
                foreach ($queryParam as $param => $value) {
                    if (strpos($param, 'group_') === 0) { // put param like 'group_name' to $aGroup
                        $aGroup[substr($param, 6)] = $value;
                        unset($queryParam[$param]);
                    }
                }
                $this->_wrapData['common'] = parse_url($info['common'], PHP_URL_PATH) . '?' . http_build_query($queryParam);
                if ($info['data'] !== '') {
                    foreach (explode(',', $info['data']) as $dataIdx) {
                        $this->_wrapData['data'][] = $dataIdx;
                        $this->_wrapData['period'][] = array('period' => $key + 1);
                        if (!isset($aGroup['start'])) $aGroup['start'] = $iNextStartIdx;
                    }
                }
            }
        }
        $this->_wrapData['group'][] = $aGroup;
        return $this;
    }

    public function flushWrapData()
    {
        if (!$this->_wrapData) {
            return array();
        }
        $wrapData = $this->_wrapData;
        $this->_wrapData = array();
        if ($wrapData['group']) {
            $wrapData['common'] = ltrim($wrapData['common'] . '&' . http_build_query(array(
                'group' => $wrapData['group'])), '&');
        }
        if ($wrapData['period']) {
            $wrapData['common'] = ltrim($wrapData['common'] . '&' . http_build_query(array(
                $wrapData['key'] => $wrapData['period'])), '&');
        }
        $wrapData['data'] = implode(',', $wrapData['data']);
        return array($wrapData);
    }

    public function build()
    {
        $this->_check();
        $components = $this->_component->findByModuleKey($this->_moduleKey, 0);
        if (!$components) {
            throw new TMException(TM::t('tongji', '模块不包含组件！'));
        }
        $files = array();
        $tempLocale = TM::app()->translator->getLocale();
        $tempBuildPath = $this->_buildPath;
        foreach (TM::app()->getSupportedLocales() as $locale) {
            $this->_buildPath = $tempBuildPath . DIRECTORY_SEPARATOR . $locale;
            TM::app()->translator->setLocale($locale);
            $files[] = $this->_writeJson($this->_buildComponents($components));
        }
        TM::app()->translator->setLocale($tempLocale);
        return $files;
    }

    private function _check()
    {
        if (!isset($this->_moduleKey)) {
            throw new TMException(TM::t('tongji', '请指定模块KEY！'));
        }
        if (!isset($this->_buildPath)) {
            throw new TMException(TM::t('tongji', '请指定生成路径！'));
        }
        $this->_buildPath = rtrim($this->_buildPath, DIRECTORY_SEPARATOR);
        TMFileHelper::mkdir($this->_buildPath);
    }

    private function _buildComponents($components)
    {
        foreach ($components as &$component) {
            $component = module_ComponentFactory::createComponent($component['component_type'])
                ->setBuilder($this)->build($component);
            if ($children = $this->_getChildren($component)) {
                $component['child'] = $this->_buildComponents($children);
            }
            if (module_ComponentFactory::isWrapComponent($component['type'])) {
                $component['urlExtend'] = module_DataComponent::buildExtendUrl($this->flushWrapData(), $this, $component);
                $component['urlExtend'] = array_shift($component['urlExtend']);
                $component['urlExtend'] .= '&' . (string)$this->_getExtend($component);
                $component['data_index'] = $this->_getDataIndex($this->popBasicData());
            }
        }
        return $components;
    }

    private function _getChildren($component)
    {
        return $this->_component->findByModuleKey($this->_moduleKey, $component['id']);
    }

    private function _writeJson($components)
    {
        if (($json = json_encode($components)) === false) {
            throw new TMException(TM::t('tongji', 'JSON编码错误！'));
        }
        $moduleKey = TMFileHelper::sanitizeFilename($this->_moduleKey);
        $keyParts = array_filter(explode('-', str_replace(array(" ", "\t", "\n", "\r", "\0", "\x0B"), '', $moduleKey)));
        $file = $this->_buildPath;
        if (isset($keyParts[1])) {
            $file .= DIRECTORY_SEPARATOR . $keyParts[0];
            TMFileHelper::mkdir($file);
        }
        $file .= DIRECTORY_SEPARATOR . $moduleKey . '.json';
        if (file_put_contents($file, $json) === false) {
            throw new TMException(TM::t('tongji', '写入文件{file}错误！', array('{file}' => $file)));
        }
        return $file;
    }

    private function _getDataIndex($basicData)
    {
        $dataIndex = array();
        foreach ($basicData['process_list'] as $idx => $info) {
            if (isset($info['period']) && $info['period'] == 1) {
                $dataIndex[] = $idx;
            }
        }
        return $dataIndex;
    }

    private function _getExtend($component)
    {
        if (isset($component['isSelControl']) && $component['isSelControl'] &&
                isset($component['selControl']['config'][0]['isAjax']) && $component['selControl']['config'][0]['isAjax'] &&
                isset($component['selControl']['config'][0]['urlKey']) && $component['selControl']['config'][0]['urlKey'] &&
                isset($component['selControl']['config'][0]['key']) &&
                isset($component['selControl']['config'][0]['url']['extend']) && $component['selControl']['config'][0]['url']['extend']
                ) {
            parse_str(parse_url($component['selControl']['config'][0]['url']['extend'], PHP_URL_QUERY), $extend);
            $extend['urlKey'] = $component['selControl']['config'][0]['urlKey'];
            $extend['key'] = $component['selControl']['config'][0]['key'];
            $extend['isMultiple'] = TMArrayHelper::assoc('isMultiple', $component['selControl'], 0);
            return http_build_query(array('extend' => $extend));
        }
    }
}

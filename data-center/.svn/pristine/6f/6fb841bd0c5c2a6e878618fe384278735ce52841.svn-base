<?php
class module_DataComponent extends module_Component
{
    /**
     * @var array Parameters that will be kept when building.
     */
    protected static $keepParameters = array('isTimeDimensionInherit', 'isSelControl');

    /**
     * Rules for attributes of this model.
     * @return array rules, one rule one element.
     */
    public function rules()
    {
        $rules = parent::rules();
        return array_merge($rules, array(
            array('urlExtend', 'checkUrlExtend'),
            array('urlPage,prepareData,theadFn', 'string', 'allowEmpty' => true, 'max' => 128),
            array('urlTimeDimension', 'enum', 'range' => array(1, 2, 3, 4, 5, 6)),
            array('thead_type', 'checkAttr', 'value' => $this->thead_title),
            array('isTimeDimensionInherit,show_table,checkbox,hide,hugeTable,theadAvg,show_graph,chartStock,chartPage,lineAreaColumn,lineColumn,isSetYAxisMin,isSelControl',
                'enum', 'range' => array(0, 1)),
            array('timeDimension', 'enum', 'range' => array('min', 'day', 'onlymin')),
            array('columnStack', 'enum', 'range' => array('percent'), 'allowEmpty' => true),
            array('chartConfig', 'checkChartConfig')
        ));
    }

    /**
     * Return all names of attributes.
     * @return array
     */
    public function attributeNames()
    {
        $names = parent::attributeNames();
        return array_merge($names, array_keys($this->parameters()));
    }

    /**
     * Return the special parameters for the current component.
     * @return array
     */
    protected function parameters()
    {
        return array(
            'urlExtend'        => array(),
            'urlPage'          => '',
            'urlTimeDimension' => 1,
            'isTimeDimensionInherit' => 1,
            'show_table'       => 1,
            'argument'         => array(),
            'checkbox'         => 0,
            'hide'             => 1,
            'prepareData'      => '',
            'thead_type'       => array(),
            'thead_title'      => array(),
            'hugeTable'        => 0,
            'theadFn'          => '',
            'minHeight'        => null,
            'theadAvg'         => 0,
            'show_graph'       => 1,
            'chartStock'       => 0,
            'chartPage'        => 0,
            'timeDimension'    => 'day',
            'columnStack'      => '',
            'lineAreaColumn'   => 0,
            'lineColumn'       => 0,
            'isSetYAxisMin'    => 0,
            'keyUnit'          => '',
            'chartConfig'      => array(),
            'isSelControl'     => 0,
            'urlMatch'         => array()
        );
    }

    /**
     * Doing something before insert a record.
     * @return boolean If false, the inserting action will be stopped.
     */
    public function beforeInsert()
    {
        $this->encodeProperties();
        return true;
    }

    /**
     * Doing something before update a record by primary key.
     * @return boolean If false, the updating action will be stopped.
     */
    public function beforeUpdate()
    {
        $this->encodeProperties();
        return true;
    }

    /**
     * Build a component to the form of configuration.
     * @param array $component
     * @return array
     */
    public function build($component)
    {
        $component = parent::build($component);
        $component['isTimeDimensionInherit'] = (bool)$component['isTimeDimensionInherit'];
        return $this->clean($this->buildChild($this->buildUrl($component)));
    }

    /**
     * Build urlExtend.
     * @param array $component
     * @return array
     */
    protected function buildUrl($component)
    {
        if (isset($component['isSelControl'], $component['urlMatch']) && $component['isSelControl']) {
            $component['isSelControl'] = true;
            $component['urlExtend'] = array();
            foreach ($component['urlMatch'] as $urlMatch) {
                $component['urlExtend'][$urlMatch['id']] = $urlMatch;
            }
            unset($component['urlMatch']);
        } else {
            $component['isSelControl'] = false;
        }

        $component['url']['extend'] = self::buildExtendUrl($component['urlExtend'], $this->getBuilder(), $component);
        if (!$component['isSelControl']) {
            // the first element of period type is default an empty string.
            array_unshift($component['url']['extend'], '');
        }

        $component['url']['page'] = $component['urlPage'];
        $component['url']['timeDimension'] = $component['urlTimeDimension'];
        return $component;
    }

    /**
     * Build extendUrl.
     * @param array                $aExtend
     * @param module_ModuleBuilder $builder
     * @param array                $component
     * @return array
     */
    public static function buildExtendUrl($aExtend, $builder, $component)
    {
        $aBasic = $builder->endBasicData();
        if ($aBasic['process_type'] === 'expres') {
            if (module_ComponentFactory::isDataComponent($component['type'])) {
                $builder->addExprsWrapData($aExtend);
            }
            return self::buildExpresExtendUrl($aExtend, $builder, $component);
        } else {
            if (module_ComponentFactory::isDataComponent($component['type'])) {
                $builder->addDistrWrapData($aExtend);
            }
            return self::buildDistrExtendUrl($aExtend, $builder, $component);
        }
    }

    /**
     * Build extendUrl for exprs.
     * @param array                $aExtend
     * @param module_ModuleBuilder $builder
     * @param array                $component
     * @return array
     */
    public static function buildExpresExtendUrl($aExtend, $builder, $component)
    {
        $aBasic = $builder->popBasicData();
        $aExtendUrl = array();
        foreach ($aExtend as $key => $info) {
            $info['common'] = trim($info['common']);
            $info['data'] = trim($info['data']);
            if ($info['data'] === '') {
                $aExtendUrl[$key] = $info['common'];
                continue;
            }
            $dataIdx = $dataInfo = $exprs = array();
            $counter = 0;
            foreach (explode(',', $info['data']) as $idxOfList) {
                $idxOfList = (int)$idxOfList;
                TMValidator::ensure(isset($aBasic['process_list'][$idxOfList]),
                    TM::t('tongji', '组件{id}存在不合法的合成数据！', array('{id}' => $component['id'])));
                if (module_ComponentFactory::isDataComponent($component['type']) && !$component['isSelControl']) {
                    $aBasic['process_list'][$idxOfList]['period'] = $key + 1;
                }
                $tempExpr = $aBasic['process_list'][$idxOfList];
                foreach (explode(',', $tempExpr['data']) as $idx => $idxOfBasic) {
                    if ($idxOfBasic === '') continue;
                    $idxOfBasic = (int)$idxOfBasic;
                    TMValidator::ensure(isset($aBasic['data_info'][$idxOfBasic]),
                        TM::t('tongji', '组件{id}存在不合法的元数据！', array('{id}' => $component['id'])));
                    if (!isset($dataIdx[$idxOfBasic])) {
                        parse_str($aBasic['data_info'][$idxOfBasic], $tempInfo);
                        if (isset($tempInfo['data_name'])) $tempInfo['data_name'] = TM::t('tongji', $tempInfo['data_name']);
                        if (isset($tempInfo['unit'])) $tempInfo['unit'] = TM::t('tongji', $tempInfo['unit']);
                        $dataInfo[] = $tempInfo;
                        $dataIdx[$idxOfBasic] = $counter++;
                    }
                    // Use a \t to avoid replacing the replaced operands.
                    if ($idx !=  $dataIdx[$idxOfBasic]) {
                        $tempExpr['expre'] = str_replace(
                            '{' . $idx . '}', "{\t" . $dataIdx[$idxOfBasic] . '}', $tempExpr['expre']);
                    }
                }
                $tempExpr['expr'] = str_replace("\t", '', $tempExpr['expre']);
                if (isset($tempExpr['data_name'])) $tempExpr['data_name'] = TM::t('tongji', $tempExpr['data_name']);
                unset($tempExpr['expre'], $tempExpr['data']);
                $exprs[] = $tempExpr;
            }
            $aExtendUrl[$key] = ltrim($info['common'] . '&' .
                http_build_query(array('data_info' => $dataInfo, 'exprs' => $exprs), null, '&', PHP_QUERY_RFC3986), '&');
        }
        $builder->pushBasicData($aBasic);
        return $aExtendUrl;
    }

    /**
     * Build extendUrl for distr.
     * @param array                $aExtend
     * @param module_ModuleBuilder $builder
     * @param array                $component
     * @return array
     */
    public static function buildDistrExtendUrl($aExtend, $builder, $component)
    {
        $aBasic = $builder->popBasicData();
        $aExtendUrl = array();
        foreach ($aExtend as $key => $info) {
            $info['common'] = trim($info['common']);
            $info['data'] = trim($info['data']);
            if ($info['data'] === '') {
                $aExtendUrl[$key] = $info['common'];
                continue;
            }
            $distrInfo = array();
            foreach (explode(',', $info['data']) as $idxOfList) {
                $idxOfList = (int)$idxOfList;
                TMValidator::ensure(isset($aBasic['process_list'][$idxOfList]),
                    TM::t('tongji', '组件{id}存在不合法的合成数据！', array('{id}' => $component['id'])));
                if (module_ComponentFactory::isDataComponent($component['type']) && !$component['isSelControl']) {
                    $aBasic['process_list'][$idxOfList]['period'] = $key + 1;
                }
                $tempDistr = $aBasic['process_list'][$idxOfList];
                $dataInfo = array();
                foreach (explode(',', $tempDistr['data']) as $idxOfBasic) {
                    if ($idxOfBasic === '') continue;
                    $idxOfBasic = (int)$idxOfBasic;
                    TMValidator::ensure(isset($aBasic['data_info'][$idxOfBasic]),
                        TM::t('tongji', '组件{id}存在不合法的元数据！', array('{id}' => $component['id'])));
                    parse_str($aBasic['data_info'][$idxOfBasic], $tempInfo);
                    if (isset($tempInfo['data_name'])) $tempInfo['data_name'] = TM::t('tongji', $tempInfo['data_name']);
                    if (isset($tempInfo['unit'])) $tempInfo['unit'] = TM::t('tongji', $tempInfo['unit']);
                    $dataInfo[] = $tempInfo;
                }
                if (data_configuration_DistrFactoryManager::isDataDistr($tempDistr['distr_by'])) {
                    $tempDistr['data_info'] = $dataInfo;
                } elseif ($dataInfo) {
                    $tempDistr = array_merge($tempDistr, array_pop($dataInfo));
                }
                if (isset($tempDistr['distr_name'])) $tempDistr['distr_name'] = TM::t('tongji', $tempDistr['distr_name']);
                if (isset($tempDistr['dimen_name'])) $tempDistr['dimen_name'] = TM::t('tongji', $tempDistr['dimen_name']);
                $distrInfo[] = $tempDistr;
            }
            $aExtendUrl[$key] = ltrim($info['common'] . '&' .
                http_build_query(array('data_info' => $distrInfo), null, '&', PHP_QUERY_RFC3986), '&');
        }
        $builder->pushBasicData($aBasic);
        return $aExtendUrl;
    }

    /**
     * Build child.
     * @param array $component
     * @return array
     */
    protected function buildChild($component)
    {
        $argument = TMArrayHelper::assoc('argument', $component, array());
        if ($component['show_graph']) {
            if ($component['chartConfig']) {
                foreach ($component['chartConfig'] as &$info) {
                    $info['name'] = TM::t('tongji', $info['name']);
                    $info['unit'] = TM::t('tongji', $info['unit']);
                }
            }
            $component['child'][] = array(
                'type'           => 'graph',
                'chartStock'     => (bool)$component['chartStock'],
                'page'      => (bool)$component['chartPage'],
                'timeDimension'  => $component['timeDimension'],
                'columnStack'    => $component['columnStack'],
                'lineAreaColumn' => (bool)$component['lineAreaColumn'],
                'lineColumn'     => (bool)$component['lineColumn'],
                'average'        => in_array('average', $argument),
                'isSetYAxisMin'  => isset($component['isSetYAxisMin']) ? (bool)$component['isSetYAxisMin'] : false,
                'keyUnit'        => TM::t('tongji', $component['keyUnit']),
                'chartConfig'    => $component['chartConfig']
            );
        }
        if ($component['show_table']) {
            $component['child'][] = array(
                'type'        => $component['hugeTable'] ? 'hugeTable': 'table',
                'qoq'         => in_array('qoq', $argument),
                'yoy'         => in_array('yoy', $argument),
                'average'     => in_array('average', $argument),
                'sum'         => in_array('sum', $argument),
                'percentage'  => in_array('percentage', $argument),
                'checkbox'    => (bool)$component['checkbox'],
                'hide'        => (bool)$component['hide'],
                'prepareData' => $component['prepareData'],
                'theadAvg'    => (bool)$component['theadAvg'],
                'minHeight'   => $component['minHeight'],
                'thead'       => $component['theadFn'] ? $component['theadFn'] : $this->buildThead($component),
                'theadConfig' => $this->buildThead($component)
            );
        }
        return $component;
    }

    /**
     * Build thead.
     * @param array $component
     * @return array
     */
    protected function buildThead($component)
    {
        $thead = array();
        if ($component['thead_type']) {
            foreach ($component['thead_type'] as $key => $type) {
                $thead[] = array(
                    'type' => $type,
                    'title' => TM::t('tongji', $component['thead_title'][$key])
                );
            }
        }
        return $thead;
    }

    /**
     * Clean unnecessary elements.
     * @param array $component
     * @return array
     */
    protected function clean($component)
    {
        foreach (array_diff_key($this->parameters(), array_fill_keys(self::$keepParameters, 0))
                as $key => $value) {
            unset($component[$key]);
        }
        return $component;
    }

    /**
     * Check urlExtend.
     * @param array $value Value of urlExtend.
     * @param string $attribute 'urlExtend'
     * @param array $params Extra params.
     * @return boolean
     */
    public function checkUrlExtend($value, $attribute, $params)
    {
        if (!is_array($value) || count($value) !== 6) { // day, week, month, minute, hour, version
            return false;
        }
        foreach ($value as $each) {
            if (!isset($each['common'], $each['data'])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check chart configurations.
     * @param array $value
     * @param string $attribute 'chartConfig'
     * @param array $params
     * @return boolean
     */
    public function checkChartConfig($value, $attribute, $params)
    {
        return true;
    }
}

<?php
class Manage extends TMController
{
    public function actions()
    {
        return array(
            'index' => array('view_r' => null),
            'displayPage' => array(
                'navi_key' => null,
                'module_name' => null,
                'page_url' => null
            ),
            'getModules' => array(),
            'getComponents' => array(
                'module_key' => '',
                'parent_id' => 0
            ),
            'getComponentById' => array('component_id' => null),
            'getComments' => array(
                'component_id' => null,
                'fetch_type' => null // 1-按组件ID查找，2-按元数据查找
            ),
            'addComponent' => array_merge(
                array(
                    'component_type' => 'wrap',
                    'parent_id' => 0,
                    'module_key' => '',
                    'properties' => '',
                    'component_desc' => ''
                ),
                $this->_getComponentParameters()
            ),
            'updateComponent' => array_merge(
                array(
                    'component_id' => null,
                    'parent_id' => 0,
                    'module_key' => '',
                    'properties' => '',
                    'component_desc' => ''
                ),
                $this->_getComponentParameters()
            ),
            'deleteComponent' => array('component_id' => null),
            'copyComponent' => array('component_id' => null, 'module_key' => ''),
            'build' => array('module_key' => null),
            'parse' => array('component_id' => null, 'data_index' => array(), 'game_id' => null),
            'saveComments' => array('component_id' => null, 'comment_id' => null),
            'ignore' => array('ignoreId' => null, 'gameId' => null)
        );
    }

    public function index($aUserParameters)
    {
        $navigator = TM::app()->navigator->getNavigatorForManage();
        $this->assign($navigator);
        $this->display('conf/pages.html');
    }

    public function displayPage($aUserParameters)
    {
        $this->display('conf/module.html');
    }

    public function getModules($aUserParameters)
    {
        $navigator = TM::app()->navigator->getNavigatorForManage();
        $modules = array();
        foreach ($navigator['navigator'] as $value) {
            $modules[] = $this->_formatModule($value);
        }
        $this->ajax(0, $modules);
    }

    private function _formatModule($module)
    {
        $formatted = array('name' => $module['name'], 'key' => $module['key']);
        if (isset($module['children']) && $module['children']) {
            foreach ($module['children'] as $child) {
                $formatted['children'][] = $this->_formatModule($child);
            }
        }
        return $formatted;
    }

    public function getComponents($aUserParameters)
    {
        $model = new module_Component();
        $this->ajax(0, $model->findByModuleKey($aUserParameters['module_key'], $aUserParameters['parent_id']));
    }

    public function getComponentById($aUserParameters)
    {
        $model = new module_Component();
        $components = $model->findById($aUserParameters['component_id']);
        if ($components) {
            $this->ajax(0, $components[0]);
        }
        $this->ajax(0);
    }

    public function getComments($aUserParameters)
    {
        switch ($aUserParameters['fetch_type']) {
            case '2': // 按元数据查找
                $components = (new module_Component())->findById($aUserParameters['component_id']);
                if (!$components ||
                        !module_ComponentFactory::isWrapComponent($components[0]['component_type']) ||
                        !isset($components[0]['data_info']) || !$components[0]['data_info'])
                    $this->ajax(0);
                $this->ajax(0, (new common_BasicData())->findComments($components[0]['data_info']));
                break;

            case '1': // 按组件ID查找
            default:
                $model = new module_ComponentComment();
                $model->component_id = $aUserParameters['component_id'];
                $this->ajax(0, $model->getCommentsByComponentId());
                break;
        }
    }

    public function addComponent($aUserParameters)
    {
        $model = module_ComponentFactory::createComponent($aUserParameters['component_type']);
        $model->attributes = $aUserParameters;
        $model->insert(array_keys($aUserParameters));
        $this->ajax(0, array('component_id' => $model->component_id));
    }

    public function updateComponent($aUserParameters)
    {
        $model = new module_Component();
        $components = $model->findById($aUserParameters['component_id']);
        TMValidator::ensure($components, TM::t('tongji', '组件不存在！'));

        $aUserParameters['component_type'] = $components[0]['component_type'];
        $model = module_ComponentFactory::createComponent($aUserParameters['component_type']);
        $model->attributes = $aUserParameters;
        $this->ajax(0, $model->update());
    }

    public function deleteComponent($aUserParameters)
    {
        $model = new module_Component();
        $model->setPrimaryKey($aUserParameters['component_id']);
        $this->ajax(0, $model->delete());
    }

    public function copyComponent($aUserParameters)
    {
        TMValidator::ensure($aUserParameters['module_key'], TM::t('tongji', '须指定目标模块！'));
        $aUserParameters['module_key'] = (array)$aUserParameters['module_key'];
        foreach ($aUserParameters['module_key'] as $moduleKey) {
            if (!$moduleKey) {
                continue;
            }
            $model = new module_Component();
            $components = $model->findById($aUserParameters['component_id'], false);
            if (!$components) {
                continue;
            }
            $model->attributes = $components[0];
            $model->copy($moduleKey);
        }
        $this->ajax(0);
    }

    public function build($aUserParameters)
    {
        $builder = new module_ModuleBuilder($aUserParameters['module_key'], $this->_getBuildPath());
        $this->ajax(0, $builder->build());
    }

    public function parse($aUserParameters)
    {
        (new common_Game())->checkGameAuth($aUserParameters['game_id']);

        $componentModel = new module_Component();
        $components = $componentModel->findById($aUserParameters['component_id']);
        if (!$components || !$aUserParameters['data_index'] || !$aUserParameters['game_id'] ||
                !module_ComponentFactory::isWrapComponent($components[0]['component_type'])) {
            $this->ajax(0);
        }

        $setModel = new module_Set();
        $setDataModel = new module_SetData();
        // $set = $setModel->findAll(array('condition' => array(
        //     'game_id'      => $aUserParameters['game_id'],
        //     'component_id' => $aUserParameters['component_id']
        // )));
        // if ($set) {
        //     $setDataModel->set_id = $set[0]['set_id'];
        // } else {
        $setDataModel->set_id = $setModel->parseSetData(
            $components[0],
            $aUserParameters['data_index'],
            $aUserParameters['game_id']
        );
        // }

        $this->ajax(0, array(
            'type'      => 'set',
            'id'        => $setDataModel->set_id,
            'data_list' => $setDataModel->formatList($setDataModel->getList())
        ));
    }

    public function saveComments($aUserParameters)
    {
        $components = (new module_Component())->findById($aUserParameters['component_id']);
        TMValidator::ensure($components &&
            module_ComponentFactory::isWrapComponent($components[0]['component_type']),
            TM::t('tongji', 'Wrap组件才能配置注释！'));

        $model = new module_ComponentComment();
        $model->deleteAllByAttributes(array('component_id' => $aUserParameters['component_id']));

        if (!$aUserParameters['comment_id']) $this->ajax(0);
        $aUserParameters['comment_id'] = array_unique((array)$aUserParameters['comment_id']);
        $model->component_id = $aUserParameters['component_id'];
        foreach ($aUserParameters['comment_id'] as $commentId) {
            if (!$commentId) continue;
            $model->comment_id = $commentId;
            $model->insert();
        }
        $this->ajax(0);
    }

    public function getIgnoredComponents($aUserParameters)
    {
        $model = new module_Component();
        $this->ajax(0, $model->findIgnored());
    }

    /**
     * Return special parameters for one component.
     * @return array
     */
    private function _getComponentParameters()
    {
        $type = TM::app()->getHttp()->getParameter('component_type', 'wrap');
        return module_ComponentFactory::createComponent($type)->getParameters();
    }

    /**
     * Return the build path.
     * @return string
     */
    private function _getBuildPath()
    {
        return 'json';
    }

    /**
     * 隐藏模块
     */
    public function ignore($userParams)
    {
        $model = new common_Game();
        $model->addIgnore($userParams['ignoreId'], $userParams['gameId']);
        $this->ajax(0);
    }
}

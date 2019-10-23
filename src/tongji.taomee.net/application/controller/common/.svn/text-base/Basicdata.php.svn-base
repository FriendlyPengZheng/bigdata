<?php
class Basicdata extends TMController
{
    public function actions()
    {
        return array(
            'index' => array(),
            'getListGroupByPeriod' => array(),
            'save' => array(
                'basic_id' => null,
                'period' => null,
                'data_name' => null,
                'type' => null,
                'stid' => null,
                'sstid' => null,
                'op_type' => null,
                'op_fields' => null,
                'task_id' => null,
                'range' => null,
                'factor' => null,
                'precision' => null,
                'unit' => null,
                'comment_id' => null,
                'comment' => null
            ),
            'delete' => array('basic_id' => null)
        );
    }

    public function index($aUserParameters)
    {
        $model = new common_BasicData();
        $this->assign('metadatas', $model->getListWithComment());
        $this->display('conf/metadatas.html');
    }

    public function getListGroupByPeriod($aUserParameters)
    {
        $model = new common_BasicData();
        $aList = $model->getPeriodGroupedList();
        foreach ($aList as $period => $list) {
            foreach ($list as $key => $basicData) {
                if ($basicData['type'] == common_Stat::TYPE_REPORT) {
                    unset($basicData['task_id']);
                } else {
                    unset($basicData['stid'], $basicData['sstid'], $basicData['op_fields'], $basicData['op_type']);
                }
                unset($basicData['basic_id'], $basicData['comment_id']);
                $aList[$period][$key] = array(
                    'data_name' => $basicData['data_name'],
                    'url' => urldecode(http_build_query($basicData))
                );
            }
        }
        $this->ajax(0, $aList);
    }

    public function save($aUserParameters)
    {
        $aUserParameters['basic_id'] = (int)$aUserParameters['basic_id'];
        $model = new common_BasicData();
        $model->attributes = $aUserParameters;
        $model->comment = $aUserParameters['comment'];
        if ($model->basic_id) {
            $model->update();
        } else {
            $model->basic_id = null;
            $model->insert();
        }
        $this->ajax(0, array('basic_id' => $model->basic_id));
    }

    public function delete($aUserParameters)
    {
        $model = new common_BasicData();
        $model->basic_id = (int)$aUserParameters['basic_id'];
        $this->ajax(0, $model->delete());
    }
}
